package com.xia.flybanner.listener

import androidx.recyclerview.widget.RecyclerView

/**
 * @author weixia
 * @date 2019/4/16.
 */
interface OnPageChangeListener {

    fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int)

    fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)

    fun onPageSelected(index: Int, isLastPage: Boolean)
}
