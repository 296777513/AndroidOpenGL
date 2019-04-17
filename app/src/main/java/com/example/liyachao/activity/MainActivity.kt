package com.example.liyachao.activity

import android.Manifest
import android.app.Activity
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.liyachao.R
import com.example.liyachao.permission.PermissionUtils
import com.example.liyachao.utils.FileUtil
import com.knight.alphavideoplayer.giftvideo.VideoController
import com.knight.alphavideoplayer.utils.Constants
import com.knight.glview.CameraMediaControl
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author liyachao 296777513
 * @version 1.0
 * @date 2017/3/1
 */
class MainActivity : Activity(), View.OnClickListener {

    lateinit var control: CameraMediaControl
    lateinit var videoController: VideoController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionUtils.checkPermissions1({
            setContentView(R.layout.activity_main)
            /**
             *   @playerType 播放器类型
             *   PLAYER_IJK
             *   PLAYER_EXO
             *   PLAYER_MEDIA
             *   @viewType view展示类型
             *   VIEW_GLSURFACE
             *   VIEW_GLTEXUTRE
             *   @isLoop 是否循环播放
             *   @mRoot父布局
             */
            videoController = VideoController(mRoot, isLoop = false, playerType = VideoController.PLAYER_MEDIA, viewType = VideoController.VIEW_GLSURFACE)
            videoController.prepareVideo(FileUtil.initPath() + "Alarms/boat8.mp4")
//            videoController.start()
            FileUtil.initPath()
            mSwitchCamera.setOnClickListener(this)
            mPlayMp4.setOnClickListener(this)
            mPlayNewMp4.setOnClickListener(this)
            mChangeMp4.setOnClickListener(this)
//            control = mCameraGLSurfaceView.mediaControl
        }, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        Log.i("liyachao111", "thread: ${Thread.currentThread().priority}");
    }

    override fun onClick(v: View) {
        when (v) {

            mSwitchCamera -> control.switchCamera()

            mPlayMp4 -> {
                Constants.isNewPlayer = false
                initPlayer()
                videoController.start()
            }

            mPlayNewMp4 -> {
                Constants.isNewPlayer = true
                initPlayer()
                videoController.start()
            }
            mChangeMp4 -> index = (index + 1) % 4
        }
    }

    val newMp4 = arrayOf("boat1.mp4", "rocket5.mp4", "fastboat1.mp4", "airplane1.mp4")
    val oldMp4 = arrayOf("boat.mp4", "rocket.mp4", "fastboat.mp4", "airplane.mp4")
    var index = 0
    private fun initPlayer() {
        mRoot.removeView(videoController.alphaView.mView)
        videoController = VideoController(mRoot, isLoop = false, playerType = VideoController.PLAYER_EXO, viewType = VideoController.VIEW_GLSURFACE)
        videoController.prepareVideo(FileUtil.initPath() + "Alarms/${if (Constants.isNewPlayer) newMp4[index] else oldMp4[index]}")
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
