package com.example.musicplayer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import com.example.musicplayer.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var mService: MusicPlayerService
    private var mBound: Boolean = false


    private lateinit var currentMusic: MutableStateFlow<MusicModel>
    private lateinit var isPlaying: MutableStateFlow<Boolean>
    private lateinit var durationValue: MutableStateFlow<Int>


    private var durationHandler: Job? = null
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {

            val binder = service as MusicPlayerService.MusicPlayerBinder
            mService = binder.getService()
            binder.setMusicList(AppConstant.musicPlayList)
            binder.setMusicIndex(AppConstant.musicIndex.value!!)
            currentMusic = binder.currentMusic()
            isPlaying = binder.isPlayer()
            durationValue = binder.getTotalDuration()

            lifecycleScope.launch {
                currentMusic.collectLatest {
                    binding.musicName.text = it.fileName
                    binding.artistName.text = it.artistName
                    binding.sb.max = it.duration.toInt()
                    binding.sb.progress = 0
                }
            }

            lifecycleScope.launch {
                isPlaying.collectLatest {
                    binding.btnPlay.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this@MainActivity,
                            if (it) R.drawable.ic_pause else R.drawable.ic_play
                        )
                    )
                }
            }

            lifecycleScope.launch {
                durationValue.collectLatest {
                    Log.e("TAG", "onServiceConnected: Duration $it")
                    startDuration(it)
                }
            }
            if (intent.data != null) {
                mService.singleOne(intent.data!!)
            } else
                mService.initializeMediaPlayer()

            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    fun startDuration(totalDuration: Int) {
        binding.sb.max = totalDuration
        durationHandler?.cancel(
        )
        var duration = 0
        durationHandler = CoroutineScope(Dispatchers.IO).launch {
            do {
                duration = mService.getCurrentDuration()
                binding.sb.progress = duration
                delay(1000)
            } while (duration < totalDuration)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val tempIntent = intent
        Log.e("TAG", "onCreate: ${tempIntent.data}")
        Log.e("TAG", "onCreate: ${tempIntent.action}")


        Intent(this, MusicPlayerService::class.java).also { intent ->
            startService(intent)
            bindService(intent, connection, BIND_AUTO_CREATE)
        }

        binding.btnNext.setOnClickListener {
            mService.nextSong()
        }

        binding.btnPrevious.setOnClickListener {
            mService.previousSong()
        }

        binding.btnPlay.setOnClickListener {
            mService.playAndPause()
        }

        binding.sb.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mService.seekTo(seekBar?.progress ?: 0)
            }

        })
    }
//    fun initializeMusicValues() {
//        AppConstant.musicIndex.observe(this){
//            binding.musicName.text = AppConstant.musicPlayList[it].fileName
//            binding.artistName.text = AppConstant.musicPlayList[it].artistName
//            binding.sb.progress = 0
//            binding.sb.max = AppConstant.musicPlayList[it].duration.toInt()
//            totalDuration = AppConstant.musicPlayList[it].duration
//        }
//        val musicPiece = AppConstant.musicPlayList[AppConstant.musicIndex.value!!]
//        binding.musicName.text = musicPiece.fileName
//        binding.artistName.text = musicPiece.artistName
//        binding.sb.max = musicPiece.duration.toInt()
//        binding.sb.progress = 0
//
//        if (mBound) {
//            if (progressRunnable != null) {
//                progressRunnable!!.cancel()
//            }
//            mService.cleanAndRelease()
//            mService.initializedMediaPlayer()
//            startLookForDuration()
//        } else {
//            Intent(this, MusicService::class.java).also { intent ->
//                intent.action = "start_new"
//                startService(intent)
//                bindService(intent, connection, BIND_AUTO_CREATE)
//            }
//        }
//        totalDuration = musicPiece.duration
//    }
//
//    private fun initializeAllListener() {
//        binding.btnPlay.setOnClickListener {
//            val playOrPause = mService.pauseAndPlay()
//        }
//
//        binding.btnNext.setOnClickListener {
//            Log.e("TAG", "initializeAllListener: ${AppConstant.musicPlayList.size}", )
//            if (AppConstant.musicIndex.value == AppConstant.musicPlayList.size - 1) {
//                AppConstant.musicIndex.value = 0
//            } else if (AppConstant.musicIndex.value!! < AppConstant.musicPlayList.size) {
//                AppConstant.musicIndex.value = AppConstant.musicIndex.value?.plus(1)
//            }
//
//            initializeMusicValues()
//        }
//        binding.btnPrevious.setOnClickListener {
//            if (AppConstant.musicIndex.value == 0) {
//                AppConstant.musicIndex.value = AppConstant.musicPlayList.size - 1
//            } else {
//                AppConstant.musicIndex.value = AppConstant.musicIndex.value?.minus(1)
//            }
//            initializeMusicValues()
//        }
//
//        binding.sb.setOnSeekBarChangeListener(
//            object : SeekBar.OnSeekBarChangeListener {
//                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
//
//                }
//
//                override fun onStartTrackingTouch(p0: SeekBar?) {
//                }
//
//                override fun onStopTrackingTouch(p0: SeekBar?) {
//                    mService.setDuration(p0!!.progress)
//                }
//
//            },
//        )
//    }
//
//    fun startLookForDuration() {
//        mService.isPlaying.observe(this) {
//            if (it) {
//                binding.btnPlay.setImageDrawable(
//                    AppCompatResources.getDrawable(
//                        this,
//                        R.drawable.ic_pause
//                    )
//                )
//            } else {
//                binding.btnPlay.setImageDrawable(
//                    AppCompatResources.getDrawable(
//                        this,
//                        R.drawable.ic_play
//                    )
//                )
//            }
//        }
//
//
//            var duration:Int =0
//        progressRunnable?.cancel()
//        progressRunnable = CoroutineScope(Dispatchers.IO).launch {
//            do {
//                if(mBound){
//                    duration= mService.getDuration()
//                    binding.sb.progress = duration
//                }
//                delay(1000)
//            } while (duration < totalDuration)
//        }
//
//    }
//
//    override fun onDestroy() {
//        progressRunnable?.cancel()
//        super.onDestroy()
//    }


    override fun onDestroy() {
        durationHandler?.cancel()
        super.onDestroy()
    }
}
