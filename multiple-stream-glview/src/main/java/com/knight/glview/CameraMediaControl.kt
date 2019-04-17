package com.knight.glview

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface
import com.knight.glview.CameraCapture
import com.knight.glview.R
import java.io.IOException

class CameraMediaControl(val context: Context) : LifecycleObserver {

    val mediaPlayer: MediaPlayer = MediaPlayer()
    val videoUrl = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.video1)

    fun prepare() {
        CameraCapture.get().openBackCamera()
        try {
            mediaPlayer.setDataSource(context, videoUrl)
            mediaPlayer.prepare()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun bindSurface(surfaces: List<SurfaceTexture>, rate: Float) {
        CameraCapture.get().setRatio(rate)
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUrl)

        if (!CameraCapture.get().isPreviewing) {
            CameraCapture.get().doStartPreview(surfaces[0])
        }
        mediaPlayer.setSurface(Surface(surfaces[1]))
        mediaPlayer.start()
        mediaPlayer.setVolume(0f, 0f)
        mediaPlayer.isLooping = true
    }

    fun switchCamera() {
        CameraCapture.get().switchCamera(1)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        mediaPlayer.pause()
        CameraCapture.get().doStopCamera()
    }
}