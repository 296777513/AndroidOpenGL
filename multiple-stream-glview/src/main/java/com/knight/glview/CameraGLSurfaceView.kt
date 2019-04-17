package com.knight.glview

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix
import android.util.AttributeSet
import com.knight.glview.gles.NormalDrawer
import com.knight.glview.gles.CameraDrawer
import com.knight.glview.gles.IDrawer
import com.knight.glview.util.GlUtil
import com.knight.glview.util.LOG
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * @author liyachao 296777513
 * @version 1.0
 * @date 2017/3/1
 */
class CameraGLSurfaceView(private val mContext: Context, attrs: AttributeSet) : GLSurfaceView(mContext, attrs), Renderer {
    private val control: CameraTouchControl
    val mediaControl: CameraMediaControl
    private val mDirectDrawers = ArrayList<IDrawer>()
    private val mSurfaceTextures = ArrayList<SurfaceTexture>()

    init {
        setEGLContextClientVersion(2)
        setRenderer(this)
        control = CameraTouchControl(this, ::changeThumbnailPosition)
        mediaControl = CameraMediaControl(mContext)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        val textureID = GlUtil.createTextureID(2)
        for (i in textureID.indices) {
            val surfaceTexture = SurfaceTexture(textureID[i])
            mSurfaceTextures.add(surfaceTexture)
            if (i == 0) {
                mDirectDrawers.add(CameraDrawer(textureID[i], mContext, surfaceTexture))
            } else {
                mDirectDrawers.add(NormalDrawer(textureID[i], mContext, surfaceTexture))
            }
        }
        mediaControl.prepare()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val rate = height.toFloat() / width.toFloat()
        LOG.logI("最终设置 GLSurfaceView width: $width  height: $height  rate: $rate")
        val dm = context.resources.displayMetrics
        LOG.logI("最终设置 屏幕 width: ${dm.widthPixels}  height: ${dm.heightPixels}  rate: ${dm.heightPixels.toFloat()/dm.widthPixels.toFloat()}")

        mediaControl.bindSurface(mSurfaceTextures, rate)
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        for (i in mDirectDrawers.indices) {
            val directDrawer = mDirectDrawers[i]
            if (i == 0) {
                Matrix.setIdentityM(directDrawer.mMVP, 0)
            } else {
                control.calculateMatrix(directDrawer.mMVP)
            }
            directDrawer.draw()
        }

    }

    private fun changeThumbnailPosition() {
        val directDrawer = mDirectDrawers.removeAt(mDirectDrawers.size - 1)
        mDirectDrawers.add(0, directDrawer)
    }

    override fun onPause() {
        super.onPause()
        mediaControl.onPause()
    }


}
