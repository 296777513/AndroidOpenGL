package com.knight.glview.gles

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.Matrix
import com.knight.glview.CameraCapture
import com.knight.glview.R
import com.knight.glview.gles.IDrawer
import com.knight.glview.util.GlUtil
import com.knight.glview.util.TextResourceReader
import java.lang.RuntimeException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class CameraDrawer(override val mTexture: Int,
                   override val context: Context,
                   override val mSurface: SurfaceTexture) : IDrawer {

    private var mVertexBuffer: FloatBuffer
    private var mDrawListBuffer: ShortBuffer
    private var mUVTexVertexBuffer: FloatBuffer
    private var mProgram: Int = 0
    private var mPositionHandle = 0
    private var mTextureCoordinatorHandle = 0
    private var mMVPMatrixHandle = 0

    private var mVertices = floatArrayOf(
            -1f, 1f,    // top left
            -1f, -1f,  // bottom left
            1f, -1f, // bottom right
            1f, 1f // top right
    )

    private val mTextHeightRatio = 0.1f


    private val UV_TEX_VERTEX = floatArrayOf(
            0f, 1f - mTextHeightRatio,  // bottom right
            1f, 1f - mTextHeightRatio,  // bottom left
            1f, 0f + mTextHeightRatio,  // top left
            0f, 0f + mTextHeightRatio  // top right
    )

    private val DRAW_ORDER = shortArrayOf(0, 2, 1, 0, 3, 2)

    override val mMVP = FloatArray(16)

    init {
        val vertexShader = TextResourceReader.readTextFileFromResource(context, R.raw.video_vertex_shader)
        val fragmentShader = TextResourceReader.readTextFileFromResource(context, R.raw.video_normal_fragment_shader)

        mProgram = GlUtil.createProgram(vertexShader, fragmentShader)
        if (mProgram == 0) {
            throw  RuntimeException("Unable to create GLES program")
        }

        mProgram = GlUtil.createProgram(vertexShader, fragmentShader) // create vertex's shader and fragment's shader, add to shader for build
        if (mProgram == 0) {
            throw  RuntimeException("Unable to create GLES program")
        }
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        GlUtil.checkLocation(mPositionHandle, "vPosition")

        mTextureCoordinatorHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate")
        GlUtil.checkLocation(mTextureCoordinatorHandle, "inputTextureCoordinate")

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        GlUtil.checkLocation(mMVPMatrixHandle, "uMVPMatrix")

        mDrawListBuffer = ByteBuffer.allocateDirect(DRAW_ORDER.size * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(DRAW_ORDER)
        mVertexBuffer = ByteBuffer.allocateDirect(mVertices.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(mVertices)
        mUVTexVertexBuffer = ByteBuffer.allocateDirect(UV_TEX_VERTEX.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(UV_TEX_VERTEX)

        mUVTexVertexBuffer.position(0)
        mDrawListBuffer.position(0)
        mVertexBuffer.position(0)
        Matrix.setIdentityM(mMVP, 0)
    }


    override fun draw() {
        if (CameraCapture.get().cameraPosition == 1) {
            mMVP[5] = Math.abs(mMVP[5])
        } else {
            mMVP[5] = -Math.abs(mMVP[5])
        }
        mSurface.updateTexImage()
        GLES20.glUseProgram(mProgram)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture)

        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0,
                mVertexBuffer)

        GLES20.glEnableVertexAttribArray(mTextureCoordinatorHandle)
        GLES20.glVertexAttribPointer(mTextureCoordinatorHandle, 2, GLES20.GL_FLOAT, false, 0,
                mUVTexVertexBuffer)

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVP, 0)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, DRAW_ORDER.size,
                GLES20.GL_UNSIGNED_SHORT, mDrawListBuffer)

        GLES20.glDisableVertexAttribArray(mPositionHandle)
        GLES20.glDisableVertexAttribArray(mTextureCoordinatorHandle)
        GLES20.glDisableVertexAttribArray(mMVPMatrixHandle)
    }


}