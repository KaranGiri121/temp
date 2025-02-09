package com.example.musicplayer

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.ContentUris
import android.content.Intent
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MusicPlayerService : Service() {

    private var binder = MusicPlayerBinder()
    private var musicList: MutableList<MusicModel> = mutableListOf()
    private var musicPiece: MutableStateFlow<MusicModel> = MutableStateFlow(MusicModel())
    private var isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private var durationHandler: MutableStateFlow<Int> = MutableStateFlow(0)


    private var musicIndex: Int = 0
    private var mediaPlayer: MediaPlayer? = null
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class MusicPlayerBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
        fun setMusicList(newMusic: List<MusicModel>) {
            Log.e("TAG", "setMusicList: ${newMusic.size}")
            musicList = newMusic.toMutableList()
        }

        fun setMusicIndex(newIndex: Int) {
            musicIndex = newIndex
        }

        fun isPlayer(): MutableStateFlow<Boolean> = isPlaying

        fun currentMusic(): MutableStateFlow<MusicModel> = musicPiece

        fun getTotalDuration(): MutableStateFlow<Int> = durationHandler
    }


    fun getCurrentDuration(): Int = mediaPlayer?.currentPosition ?: 0

    override fun onCreate() {
        Log.e("TAG", "onCreate: ")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("TAG", "onStartCommand: ")
        val action = intent?.action

        action?.let {

            val state = AppConstant.MediaState.fromState(action)
            Log.e("TAG", "onStartCommand: $state")
            when (state) {
                AppConstant.MediaState.START_NEW -> {
                    singleOne(intent?.data!!)
                }

                AppConstant.MediaState.NEXT -> {
                    nextSong()
                }

                AppConstant.MediaState.PREV -> {
                    previousSong()
                }

                AppConstant.MediaState.PLAY -> {
                    playAndPause()
                }

                AppConstant.MediaState.PAUSE -> {
                    playAndPause()
                }

                null -> {
                }
            }
        }
        return START_STICKY
    }

     fun singleOne(path: Uri) {
        Log.e("TAG", "initializeMediaPlayer: Start")
        mediaPlayer?.stop()
        mediaPlayer?.release()

        mediaPlayer = MediaPlayer.create(
            this, path
        )

        Log.e("TAG", "initializeMediaPlayer: Start To Play")
        mediaPlayer?.start()
        isPlaying.update {
            true
        }

        durationHandler.update {
            mediaPlayer?.duration ?: 0
        }

        sendNotification(musicPiece.value)
    }

    fun initializeMediaPlayer() {
        Log.e("TAG", "initializeMediaPlayer: Start")
        val newMusicPiece = musicList[musicIndex]
        musicPiece.update {
            newMusicPiece
        }
        mediaPlayer?.stop()
        mediaPlayer?.release()

        mediaPlayer = MediaPlayer.create(
            this, ContentUris.withAppendedId(AppConstant.musicUri, musicPiece.value.id)
        )

        Log.e("TAG", "initializeMediaPlayer: Start To Play")
        mediaPlayer?.start()
        isPlaying.update {
            true
        }

        durationHandler.update {
            mediaPlayer?.duration ?: 0
        }

        sendNotification(musicPiece.value)
    }

    fun playAndPause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        } else {
            mediaPlayer?.start()
        }

        isPlaying.update {
            mediaPlayer?.isPlaying ?: false
        }
    }

    fun seekTo(pos: Int) {
        mediaPlayer?.seekTo(pos)
    }

    fun nextSong() {
        if (musicIndex == musicList.size - 1) {
            //Todo Loop Functionality
            musicIndex = 0
        } else {
            musicIndex = musicIndex.plus(1)
        }

        val newMusicPiece = musicList[musicIndex]

        musicPiece.update {
            newMusicPiece
        }
        initializeMediaPlayer()
    }

    fun previousSong() {
        if (musicIndex != 0) {
            musicIndex = musicIndex.minus(1)
        }

        val newMusicPiece = musicList[musicIndex]
        musicPiece.update {
            newMusicPiece
        }

        initializeMediaPlayer()
    }

    fun sendNotification(musicPiece: MusicModel) {
        val mediaSession = MediaSessionCompat(this, "MusicPlayer")
        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession.sessionToken)
            .setShowActionsInCompactView(0, 1, 2, 3, 4)

        val pauseAction = NotificationCompat.Action.Builder(
            R.drawable.ic_pause, "Pause",
            PendingIntent.getService(this, 121, Intent(this, MusicPlayerService::class.java).apply {
                action = "Pause"
            }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        ).build()

        val playAction = NotificationCompat.Action.Builder(
            R.drawable.ic_play, "Play",
            PendingIntent.getService(this, 121, Intent(this, MusicPlayerService::class.java).apply {
                action = "Play"
            }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        ).build()

        val nextAction = NotificationCompat.Action.Builder(
            R.drawable.ic_next, "Next",
            PendingIntent.getService(this, 121, Intent(this, MusicPlayerService::class.java).apply {
                action = "Next"
            }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        ).build()

        val previous = NotificationCompat.Action.Builder(
            R.drawable.ic_previous, "Previous",
            PendingIntent.getService(this, 121, Intent(this, MusicPlayerService::class.java).apply {
                action = "Previous"
            }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        ).build()

        val notification = NotificationCompat.Builder(this, "music").setStyle(mediaStyle)
            .setContentTitle("Music Player")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(Notification.PRIORITY_MAX).setWhen(0)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)


        notification.addAction(previous)
        if (isPlaying.value)
            notification.addAction(pauseAction)
        else
            notification.addAction(playAction)

        notification.addAction(nextAction)


        if (isPlaying.value) {
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                    .putString(MediaMetadata.METADATA_KEY_TITLE, musicPiece.fileName)
                    .putString(MediaMetadata.METADATA_KEY_ARTIST, musicPiece.artistName).putString(
                        MediaMetadata.METADATA_KEY_ALBUM_ART_URI,
                        ContentUris.withAppendedId(AppConstant.musicUri, 1000000055).toString()
                    ).putLong(
                        MediaMetadata.METADATA_KEY_DURATION, musicPiece.duration
                    ).build()
            )
        }
        val state =
            if (isPlaying.value) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED

        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder().setState(
                state,
                mediaPlayer?.currentPosition!!.toLong(),
                1F
            ).setActions(
                PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_SEEK_TO or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            ).build()
        )

        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onSeekTo(pos: Long) {
                mediaPlayer?.seekTo(pos.toInt())
            }
        })
        startForeground(121, notification.build())
    }
}
