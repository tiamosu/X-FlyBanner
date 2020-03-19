package com.xia.flybanner.listener

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * @author weixia
 * @date 2019/4/16.
 */
class FBPageChangeListener : OnPageChangeListener {
    private var pointViews: ArrayList<ImageView>? = null
    private var pageIndicatorId: IntArray? = null
    private var pageChangeListener: OnPageChangeListener? = null

    fun setPageIndicator(pointViews: ArrayList<ImageView>, pageIndicatorId: IntArray) {
        this.pointViews = pointViews
        this.pageIndicatorId = pageIndicatorId
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        pageChangeListener?.onScrollStateChanged(recyclerView, newState)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        pageChangeListener?.onScrolled(recyclerView, dx, dy)
    }

    override fun onPageSelected(index: Int, isLastPage: Boolean) {
        pointViews?.apply {
            val size = this.size
            for (i in 0 until size) {
                this[index].setImageResource(pageIndicatorId?.get(1) ?: 0)
                if (index != i) {
                    this[i].setImageResource(pageIndicatorId?.get(0) ?: 0)
                }
            }
        }
        pageChangeListener?.onPageSelected(index, isLastPage)
    }

    fun setOnPageChangeListener(onPageChangeListener: OnPageChangeListener?) {
        this.pageChangeListener = onPageChangeListener
    }
}
