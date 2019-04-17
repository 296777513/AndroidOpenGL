package com.knight.glview.gles

import android.content.Context
import android.graphics.SurfaceTexture

interface IDrawer {
    val mTexture: Int
    val context: Context
    val mSurface: SurfaceTexture
    val mMVP: FloatArray

    fun draw()
}