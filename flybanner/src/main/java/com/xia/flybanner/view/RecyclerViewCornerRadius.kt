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
    private var rectF: RectF? = null
    private var path: Path? = null

    @JvmField
    var topLeftRadius = 0

    @JvmField
    var topRightRadius = 0

    @JvmField
    var bottomLeftRadius = 0

    @JvmField
    var bottomRightRadius = 0

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
                rectF = RectF(0f, 0f, recyclerView.measuredWidth.toFloat(), recyclerView.measuredHeight.toFloat())
                path = Path()
                path!!.reset()
                path!!.addRoundRect(rectF!!, floatArrayOf(
                        topLeftRadius.toFloat(), topLeftRadius.toFloat(),
                        topRightRadius.toFloat(), topRightRadius.toFloat(),
                        bottomLeftRadius.toFloat(), bottomLeftRadius.toFloat(),
                        bottomRightRadius.toFloat(), bottomRightRadius.toFloat()),
                        Path.Direction.CCW)
            }
        })
    }

    fun setCornerRadius(radius: Int) {
        this.topLeftRadius = radius
        this.topRightRadius = radius
        this.bottomLeftRadius = radius
        this.bottomRightRadius = radius
    }

    fun setCornerRadius(topLeftRadius: Int, topRightRadius: Int, bottomLeftRadius: Int, bottomRightRadius: Int) {
        this.topLeftRadius = topLeftRadius
        this.topRightRadius = topRightRadius
        this.bottomLeftRadius = bottomLeftRadius
        this.bottomRightRadius = bottomRightRadius
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        rectF?.let {
            c.clipRect(it)
        }
        path?.let {
            c.clipPath(it)
        }
    }
}
