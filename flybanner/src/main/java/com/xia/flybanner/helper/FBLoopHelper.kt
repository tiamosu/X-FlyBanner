package com.xia.flybanner.helper

import android.os.Build
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.xia.flybanner.adapter.FBPageAdapter
import com.xia.flybanner.listener.OnPageChangeListener
import com.xia.flybanner.view.FBLoopViewPager

/**
 * @author weixia
 * @date 2019/4/16.
 */
class FBLoopHelper {
    private var mFirstItemPos: Int = 0
    private var mLastPosition: Int = 0

    private val mPagerSnapHelper = PagerSnapHelper()
    private var mLoopViewPager: FBLoopViewPager? = null
    private var mPageAdapter: FBPageAdapter<*>? = null
    private var mOnScrollListener: RecyclerView.OnScrollListener? = null
    private var mOnPageChangeListener: OnPageChangeListener? = null

    fun attachToRecyclerView(loopViewPager: FBLoopViewPager,
                             pageAdapter: FBPageAdapter<*>) {
        this.mLoopViewPager = loopViewPager
        this.mPageAdapter = pageAdapter
        this.mLastPosition = -1

        mOnScrollListener?.let {
            loopViewPager.removeOnScrollListener(it)
        }
        mOnScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                mOnPageChangeListener?.onScrollStateChanged(recyclerView, newState)

                //这里变换位置实现循环
                val count = pageAdapter.getRealItemCount()
                if (count <= 0 && newState != RecyclerView.SCROLL_STATE_IDLE) {
                    return
                }
                var currentItem = getCurrentItem()
                if (currentItem < count) {
                    currentItem += count
                } else if (currentItem >= 2 * count) {
                    currentItem -= count
                }
                val position = currentItem % count
                if (mLastPosition != position) {
                    mLastPosition = position
                    setCurrentItem(currentItem)

                    mOnPageChangeListener?.apply {
                        val isLastPage = position == count - 1
                        this.onPageSelected(position, isLastPage)
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                mOnPageChangeListener?.onScrolled(recyclerView, dx, dy)
            }
        }
        loopViewPager.addOnScrollListener(mOnScrollListener!!)

        initWidth(loopViewPager)
        mPagerSnapHelper.attachToRecyclerView(loopViewPager)
    }

    /**
     * 初始化卡片宽度
     */
    private fun initWidth(loopViewPager: FBLoopViewPager) {
        val vto = loopViewPager.viewTreeObserver
        if (!vto.isAlive) {
            return
        }
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val currentVto = loopViewPager.viewTreeObserver
                if (currentVto.isAlive) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        @Suppress("DEPRECATION")
                        currentVto.removeGlobalOnLayoutListener(this)
                    } else {
                        currentVto.removeOnGlobalLayoutListener(this)
                    }
                }
                setCurrentItem(mFirstItemPos)
            }
        })
    }

    fun setCurrentItem(item: Int) {
        setCurrentItem(item, false)
    }

    fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        if (smoothScroll) {
            mLoopViewPager?.smoothScrollToPosition(item)
        } else {
            scrollToPosition(item)
        }
    }

    private fun scrollToPosition(pos: Int) {
        val layoutManager = mLoopViewPager?.layoutManager as? LinearLayoutManager
        layoutManager?.scrollToPositionWithOffset(pos, 0)
    }

    fun setFirstItemPos(firstItemPos: Int) {
        this.mFirstItemPos = firstItemPos
    }

    fun getFirstItemPos(): Int {
        return mFirstItemPos
    }

    fun getCurrentItem(): Int {
        val layoutManager = mLoopViewPager?.layoutManager ?: return 0
        val view = mPagerSnapHelper.findSnapView(layoutManager) ?: return 0
        return layoutManager.getPosition(view)
    }

    fun getRealCurrentItem(): Int {
        val count = getRealItemCount()
        return if (count != 0) {
            getCurrentItem() % count
        } else 0
    }

    fun getRealItemCount(): Int {
        return mPageAdapter?.getRealItemCount() ?: 0
    }

    fun setOnPageChangeListener(onPageChangeListener: OnPageChangeListener?) {
        this.mOnPageChangeListener = onPageChangeListener
    }
}
