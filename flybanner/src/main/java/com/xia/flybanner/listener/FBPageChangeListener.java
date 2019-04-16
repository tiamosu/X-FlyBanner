package com.xia.flybanner.listener;

import android.widget.ImageView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author weixia
 * @date 2019/4/16.
 */
public class FBPageChangeListener implements OnPageChangeListener {
    private final ArrayList<ImageView> mPointViews;
    private final int[] mPageIndicatorId;
    private OnPageChangeListener mOnPageChangeListener;

    public FBPageChangeListener(ArrayList<ImageView> pointViews, int[] pageIndicatorId) {
        this.mPointViews = pointViews;
        this.mPageIndicatorId = pageIndicatorId;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onScrollStateChanged(recyclerView, newState);
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onScrolled(recyclerView, dx, dy);
        }
    }

    @Override
    public void onPageSelected(int index) {
        final int size = mPointViews.size();
        for (int i = 0; i < size; i++) {
            mPointViews.get(index).setImageResource(mPageIndicatorId[1]);
            if (index != i) {
                mPointViews.get(i).setImageResource(mPageIndicatorId[0]);
            }
        }
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(index);
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.mOnPageChangeListener = onPageChangeListener;
    }
}
