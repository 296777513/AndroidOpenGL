package com.knight.glview.util

import android.graphics.RectF
import android.opengl.Matrix

object MatrixUtil {
    @JvmStatic
    fun calculateMatrix(mMVP: FloatArray, rectF: RectF, screenWidth: Float, screenHeight: Float) {
        Matrix.setIdentityM(mMVP, 0)
        val scaleX = 1f / 4f
        val scaleY = 1f / 4f
        val ratioX = (rectF.left - .5f * (1 - scaleX) * screenWidth) / rectF.width()
        val ratioY = (rectF.top - .5f * (1 + scaleY) * screenHeight) / rectF.height()
        Matrix.scaleM(mMVP, 0, scaleX, scaleY, 0f)
        Matrix.translateM(mMVP, 0, ratioX * 2, ratioY * 2, 0f)
    }
}