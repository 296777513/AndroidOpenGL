package com.knight.glview

import android.content.Context
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.knight.glview.util.MatrixUtil

class CameraTouchControl(val root: View, changeThumbnailPosition: () -> Unit) {

    val context = root.context
    val dm = context.resources.displayMetrics
    // 屏幕的宽度
    private val mScreenWidth: Float = dm.widthPixels.toFloat()
    // 屏幕的高度
    private val mScreenHeight: Float = dm.heightPixels.toFloat()

    // 小视频的高度
    private val mThumbnailHeight: Float = mScreenHeight / 4
    // 小视频的宽度
    private val mThumbnailWidth: Float = mScreenWidth / 4

    //距离屏幕的最小距离
    private val mMargin: Int = dip2px(context, 2f)

    // 记录小视频的坐标
    private val mThumbnailRect: RectF = RectF(mMargin.toFloat(),
            mScreenHeight - mMargin, mMargin + mThumbnailWidth, mScreenHeight - mMargin.toFloat() - mThumbnailHeight)

    //最小的滑动距离
    private val mTouchSlop: Int = ViewConfiguration.get(context).scaledPagingTouchSlop

    // 标识符，判断手指按下的范围是否在小视频的坐标内
    private var mTouchThumbnail = false

    // 标识符，判断手指是移动小视频而不是点击小视频
    private var isMoveThumbnail = false
    // 按下时手指的x坐标值
    private var mDownX = 0f
    // 按下时手指的y坐标值
    private var mDownY = 0f

    private var mLastYLength = 0f
    private var mLastXLength = 0f

    init {
        root.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mDownX = event.x
                    mDownY = event.y
                    if (mDownX > mThumbnailRect.left && mDownX < mThumbnailRect.right
                            && mDownY > mThumbnailRect.bottom && mDownY < mThumbnailRect.top) {
                        mTouchThumbnail = true
                        mLastYLength = 0f
                        mLastXLength = 0f
                        return@setOnTouchListener true
                    } else {
                        mTouchThumbnail = false
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val moveX = event.x
                    val moveY = event.y
                    if (mTouchThumbnail) {
                        val lengthX = Math.abs(mDownX - moveX)
                        val lengthY = Math.abs(mDownY - moveY)
                        val length = Math.sqrt(Math.pow(lengthX.toDouble(), 2.0) + Math.pow(lengthY.toDouble(), 2.0)).toFloat()
                        if (length > mTouchSlop) {
                            moveView(mThumbnailRect, mDownY - moveY, moveX - mDownX)
                            isMoveThumbnail = true
                        } else {
                            isMoveThumbnail = false
                        }
                        return@setOnTouchListener true
                    }
                }
                MotionEvent.ACTION_UP -> if (mTouchThumbnail) {
                    mLastYLength = 0f
                    mLastXLength = 0f
                    //抬起手指时，如果不是移动小视频，那么就是点击小视频
                    if (!isMoveThumbnail) {
                        changeThumbnailPosition.invoke()
                    }
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }


    /**
     * 移动小视频
     *
     * @param rectF   小视频的坐标
     * @param lengthY 在Y轴移动的距离
     * @param lengthX 在X轴移动的距离
     */
    fun moveView(rectF: RectF, lengthY: Float, lengthX: Float) {
        rectF.top = rectF.top - (lengthY - mLastYLength)
        rectF.bottom = rectF.bottom - (lengthY - mLastYLength)
        rectF.left = rectF.left + (lengthX - mLastXLength)
        rectF.right = rectF.right + (lengthX - mLastXLength)

        if (rectF.top > mScreenHeight - mMargin) {
            rectF.top = mScreenHeight - mMargin
            rectF.bottom = rectF.top - mThumbnailHeight
        }

        if (rectF.bottom < mMargin) {
            rectF.bottom = mMargin * 1f
            rectF.top = rectF.bottom + mThumbnailHeight
        }

        if (rectF.right > mScreenWidth - mMargin) {
            rectF.right = mScreenWidth - mMargin
            rectF.left = rectF.right - mThumbnailWidth
        }

        if (rectF.left < mMargin) {
            rectF.left = mMargin.toFloat()
            rectF.right = rectF.left + mThumbnailWidth
        }

        mLastYLength = lengthY
        mLastXLength = lengthX
    }

    fun calculateMatrix(mMvp: FloatArray) {
        MatrixUtil.calculateMatrix(mMvp, mThumbnailRect, mScreenWidth, mScreenHeight)

    }

    fun dip2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

}