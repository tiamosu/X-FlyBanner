package com.xia.flybanner.listener

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * @author weixia
 * @date 2019/4/16.
 */
class FBPageChangeListener : OnPageChangeListener {
    private var mPointViews: ArrayList<ImageView>? = null
    private var mPageIndicatorId: IntArray? = null
    private var mOnPageChangeListener: OnPageChangeListener? = null

    fun setPageIndicator(pointViews: ArrayList<ImageView>, pageIndicatorId: IntArray) {
        this.mPointViews = pointViews
        this.mPageIndicatorId = pageIndicatorId
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        mOnPageChangeListener?.onScrollStateChanged(recyclerView, newState)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        mOnPageChangeListener?.onScrolled(recyclerView, dx, dy)
    }

    override fun onPageSelected(index: Int, isLastPage: Boolean) {
        mPointViews?.apply {
            val size = this.size
            for (i in 0 until size) {
                this[index].setImageResource(mPageIndicatorId?.get(1) ?: 0)
                if (index != i) {
                    this[i].setImageResource(mPageIndicatorId?.get(0) ?: 0)
                }
            }
        }
        mOnPageChangeListener?.onPageSelected(index, isLastPage)
    }

    fun setOnPageChangeListener(onPageChangeListener: OnPageChangeListener?) {
        this.mOnPageChangeListener = onPageChangeListener
    }
}
