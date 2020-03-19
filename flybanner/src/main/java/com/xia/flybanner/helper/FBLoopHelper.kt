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
    private var firstItemPos = 0
    private var lastPosition = 0

    private val pagerSnapHelper by lazy { PagerSnapHelper() }
    private var loopViewPager: FBLoopViewPager? = null
    private var pageAdapter: FBPageAdapter<*>? = null
    private var scrollListener: RecyclerView.OnScrollListener? = null
    private var pageChangeListener: OnPageChangeListener? = null

    fun attachToRecyclerView(loopViewPager: FBLoopViewPager, pageAdapter: FBPageAdapter<*>) {
        this.loopViewPager = loopViewPager
        this.pageAdapter = pageAdapter
        this.lastPosition = -1

        scrollListener?.let {
            loopViewPager.removeOnScrollListener(it)
        }
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                pageChangeListener?.onScrollStateChanged(recyclerView, newState)

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
                if (lastPosition != position) {
                    lastPosition = position
                    setCurrentItem(currentItem)

                    pageChangeListener?.apply {
                        val isLastPage = position == count - 1
                        this.onPageSelected(position, isLastPage)
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                pageChangeListener?.onScrolled(recyclerView, dx, dy)
            }
        }
        loopViewPager.addOnScrollListener(scrollListener!!)

        initWidth(loopViewPager)
        pagerSnapHelper.attachToRecyclerView(loopViewPager)
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
                setCurrentItem(firstItemPos)
            }
        })
    }

    fun setCurrentItem(item: Int) {
        setCurrentItem(item, false)
    }

    fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        if (smoothScroll) {
            loopViewPager?.smoothScrollToPosition(item)
        } else {
            scrollToPosition(item)
        }
    }

    private fun scrollToPosition(pos: Int) {
        val layoutManager = loopViewPager?.layoutManager as? LinearLayoutManager
        layoutManager?.scrollToPositionWithOffset(pos, 0)
    }

    fun setFirstItemPos(firstItemPos: Int) {
        this.firstItemPos = firstItemPos
    }

    fun getFirstItemPos(): Int {
        return firstItemPos
    }

    fun getCurrentItem(): Int {
        val layoutManager = loopViewPager?.layoutManager ?: return 0
        val view = pagerSnapHelper.findSnapView(layoutManager) ?: return 0
        return layoutManager.getPosition(view)
    }

    fun getRealCurrentItem(): Int {
        val count = getRealItemCount()
        return if (count != 0) {
            getCurrentItem() % count
        } else 0
    }

    fun getRealItemCount(): Int {
        return pageAdapter?.getRealItemCount() ?: 0
    }

    fun setOnPageChangeListener(onPageChangeListener: OnPageChangeListener?) {
        this.pageChangeListener = onPageChangeListener
    }
}
