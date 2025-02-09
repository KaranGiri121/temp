package com.example.videoplayer

import android.content.ContentUris
import android.content.Context
import android.media.MediaPlayer
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.content.res.AppCompatResources
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MediaController(context: Context) : FrameLayout(context), CustomMediaPlayer {

    private final val TAG = "TAG"
    var mediaPlayer: MediaPlayer? = null
    private var anchorView: ViewGroup? = null
    private val isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(true)
    var seekHandler: Job? = null
    var seekBar: SeekBar? = null

    fun initiateMediaPlayer() {
        mediaPlayer = MediaPlayer()
        Log.e("TAG", "onCreate: MediaPlayer Created")
        mediaPlayer?.reset()
        mediaPlayer?.setOnPreparedListener {

            it.start()
            seekBar?.max = it.duration
            seekBar?.progress = 0
            seekHandler()
        }

        mediaPlayer?.setDataSource(
            context,
            ContentUris.withAppendedId(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                AppConstant.videoList[AppConstant.videoIndex].id
            )
        )
        Log.e(TAG, "initiateMediaPlayer: End Of Function")
    }

    fun changeVideo() {
        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(
            context,
            ContentUris.withAppendedId(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                AppConstant.videoList[AppConstant.videoIndex].id
            )
        )
        mediaPlayer?.prepare()
    }

    fun setAnchorView(
        view: ViewGroup
    ) {
        anchorView = view

        val frameParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            Gravity.BOTTOM
        )

        removeAllViews()
        val controllerView = getControllerView()

        addView(controllerView, frameParams)

        anchorView?.addView(this, frameParams)
    }


    fun getControllerView(): View {
        val layoutInflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val controllerView = layoutInflater.inflate(R.layout.media_controller_layout, null)
        initializeButtons(controllerView)
//        checkDuration()
        return controllerView!!
    }

    fun initializeButtons(view: View) {
        val playButton = view.findViewById<ImageView>(R.id.play)
        val previousButton = view.findViewById<ImageView>(R.id.previous)
        val nextButton = view.findViewById<ImageView>(R.id.next)
        seekBar = view.findViewById<SeekBar>(R.id.sb)

        CoroutineScope(Dispatchers.IO).launch {
            isPlaying.collectLatest {
                if (it) {
                    playButton.setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.ic_pause
                        )
                    )
                } else {
                    playButton.setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.ic_play
                        )
                    )
                }
            }
        }

        playButton.setOnClickListener {
            if (isPlaying()) {
                pause()
            } else {
                start()
            }
        }

        previousButton.setOnClickListener {
            prevItem()
        }

        nextButton.setOnClickListener {
            nextItem()
        }

        seekBar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                p0?.let {
                    seekTo(it.progress)
                }
            }

        })
    }

    fun seekHandler() {
        seekHandler?.cancel()
        seekHandler = CoroutineScope(Dispatchers.IO).launch {
            var duration = 0

            do {
                duration = mediaPlayer?.currentPosition!!
                seekBar?.progress = duration
                delay(1000)
            } while (duration < mediaPlayer?.duration!!)
        }
    }

    override fun start() {
        isPlaying.update {
            true
        }
        mediaPlayer?.start()
    }

    override fun pause() {
        isPlaying.update {
            false
        }
        mediaPlayer?.pause()
    }

    override fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun seekTo(pos: Int) {
        mediaPlayer?.seekTo(pos)
    }

    override fun isPlaying(): Boolean {
        return isPlaying.value
    }

    override fun getBufferPercentage(): Int {
        return 0
    }

    override fun canPause(): Boolean {
        return isPlaying.value
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun isFullScreen(): Boolean {
        return true
    }

    override fun toggleFullScreen() {

    }

    override fun nextItem() {
        AppConstant.videoIndex += 1
        changeVideo()
    }

    override fun prevItem() {
        if (AppConstant.videoIndex != 0) {
            AppConstant.videoIndex -= 1
        }
        changeVideo()
    }

}
