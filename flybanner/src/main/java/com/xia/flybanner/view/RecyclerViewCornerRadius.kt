package com.xia.flybanner.view

import android.annotation.TargetApi
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.os.Build
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView

/**
 * @author weixia
 * @date 2019/4/16.
 */
class RecyclerViewCornerRadius(private val recyclerView: RecyclerView) : RecyclerView.ItemDecoration() {
    private var mRectF: RectF? = null
    private var mPath: Path? = null

    @JvmField
    var mTopLeftRadius = 0
    @JvmField
    var mTopRightRadius = 0
    @JvmField
    var mBottomLeftRadius = 0
    @JvmField
    var mBottomRightRadius = 0

    init {
        init()
    }

    private fun init() {
        val vto = recyclerView.viewTreeObserver
        if (!vto.isAlive) {
            return
        }
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onGlobalLayout() {
                val currentVto = recyclerView.viewTreeObserver
                if (currentVto.isAlive) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        @Suppress("DEPRECATION")
                        currentVto.removeGlobalOnLayoutListener(this)
                    } else {
                        currentVto.removeOnGlobalLayoutListener(this)
                    }
                }
                mRectF = RectF(0f, 0f, recyclerView.measuredWidth.toFloat(), recyclerView.measuredHeight.toFloat())
                mPath = Path()
                mPath!!.reset()
                mPath!!.addRoundRect(mRectF, floatArrayOf(
                        mTopLeftRadius.toFloat(), mTopLeftRadius.toFloat(),
                        mTopRightRadius.toFloat(), mTopRightRadius.toFloat(),
                        mBottomLeftRadius.toFloat(), mBottomLeftRadius.toFloat(),
                        mBottomRightRadius.toFloat(), mBottomRightRadius.toFloat()),
                        Path.Direction.CCW)
            }
        })
    }

    fun setCornerRadius(radius: Int) {
        this.mTopLeftRadius = radius
        this.mTopRightRadius = radius
        this.mBottomLeftRadius = radius
        this.mBottomRightRadius = radius
    }

    fun setCornerRadius(topLeftRadius: Int, topRightRadius: Int, bottomLeftRadius: Int, bottomRightRadius: Int) {
        this.mTopLeftRadius = topLeftRadius
        this.mTopRightRadius = topRightRadius
        this.mBottomLeftRadius = bottomLeftRadius
        this.mBottomRightRadius = bottomRightRadius
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        mRectF?.let {
            c.clipRect(it)
        }
        mPath?.let {
            c.clipPath(it)
        }
    }
}
