package com.xia.flybanner.listener;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author weixia
 * @date 2019/4/16.
 */
public interface OnPageChangeListener {

    void onScrollStateChanged(RecyclerView recyclerView, int newState);

    void onScrolled(RecyclerView recyclerView, int dx, int dy);

    void onPageSelected(int index, boolean isLastPage);
}
