package com.example.videoplayer

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.videoplayer.databinding.ActivityVideoPlayerBinding

class VideoPlayer : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayerBinding
    private var mediaPlayer: MediaPlayer? = null
    var mediaController: MediaController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val landscape = intent.getBooleanExtra("landscape",false)
        if(landscape)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.videoSurfaceContainer.setOnClickListener {

        }

        binding.videoSurface.holder.addCallback(
            object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    Log.e("TAG", "surfaceCreated: ", )
                    mediaController?.mediaPlayer?.setDisplay(holder)
                    mediaController?.mediaPlayer?.prepare()
                    mediaController?.setAnchorView(binding.videoSurfaceContainer)
                }
                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {

                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {

                }
            },
        )


        mediaController = MediaController(this)
        mediaController?.initiateMediaPlayer()

        Log.e("TAG", "onCreate: End Of Body")
    }

    override fun onDestroy() {
        mediaController?.mediaPlayer?.release()
        mediaController?.seekHandler?.cancel()
        super.onDestroy()
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

}
