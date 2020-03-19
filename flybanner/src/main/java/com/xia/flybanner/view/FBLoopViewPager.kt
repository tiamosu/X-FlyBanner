package com.xia.flybanner.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

/**
 * @author weixia
 * @date 2019/4/16.
 */
class FBLoopViewPager : RecyclerView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        val velocityXTemp = solveVelocity(velocityX)
        val velocityYTemp = solveVelocity(velocityY)
        return super.fling(velocityXTemp, velocityYTemp)
    }

    private fun solveVelocity(velocity: Int): Int {
        return if (velocity > 0) {
            velocity.coerceAtMost(FLING_MAX_VELOCITY)
        } else {
            velocity.coerceAtLeast(-FLING_MAX_VELOCITY)
        }
    }

    companion object {
        private const val FLING_SCALE_DOWN_FACTOR = 0.5f // 减速因子
        private const val FLING_MAX_VELOCITY = 3000 // 最大顺时滑动速度
    }
}
