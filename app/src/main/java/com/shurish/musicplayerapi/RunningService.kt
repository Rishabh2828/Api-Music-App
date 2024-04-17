package com.shurish.musicplayerapi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RunningService : Service() {
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "running_channel",
                "Rishabh Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {

            Actions.START.toString() -> stopSelf()
            Actions.STOP.toString() -> stopSelf()
            Actions.PLAY.toString() -> {
                val musicUri = intent.getStringExtra("musicUri")
                val imageUrl = intent.getStringExtra("imageUrl")
                val imageName = intent.getStringExtra("imageName")
                if (imageName != null) {
                    playMusic(musicUri, imageUrl, imageName)
                }
            }
            Actions.PAUSE.toString() -> pauseMusic()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService(imageUrl: String, imageName: String) {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = getBitmapFromUrl(imageUrl)
            bitmap?.let {
                val notification = NotificationCompat.Builder(this@RunningService, "running_channel")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Music is active")
                    .setContentText(imageName)
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(it)
                    .build()
                startForeground(1, notification)
            }
        }
    }

    private suspend fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        val request = Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .submit()
        return try {
            request.get() // This call suspends until the image is loaded
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun playMusic(musicUri: String?, image : String?, imageName : String) {
        musicUri?.let {
            mediaPlayer.apply {
                reset()
                setDataSource(applicationContext, it.toUri())
                prepareAsync()
                setOnPreparedListener { mp ->
                    image?.let {
                        startForegroundService(image, imageName)
                    }

                    mp.start()



                }
            }
        }
    }

    private fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    enum class Actions {
        START, STOP, PLAY, PAUSE
    }
}
