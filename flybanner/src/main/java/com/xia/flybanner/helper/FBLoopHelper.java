package com.xia.flybanner.helper;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

import com.xia.flybanner.adapter.FBPageAdapter;
import com.xia.flybanner.listener.OnPageChangeListener;
import com.xia.flybanner.view.FBLoopViewPager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author weixia
 * @date 2019/4/16.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class FBLoopHelper {
    private int mFirstItemPos;
    private int mLastPosition;

    private final PagerSnapHelper mPagerSnapHelper = new PagerSnapHelper();
    private FBLoopViewPager mLoopViewPager;
    private FBPageAdapter mPageAdapter;
    private RecyclerView.OnScrollListener mOnScrollListener;
    private OnPageChangeListener mOnPageChangeListener;

    public void attachToRecyclerView(final FBLoopViewPager loopViewPager,
                                     final FBPageAdapter pageAdapter) {
        this.mLoopViewPager = loopViewPager;
        this.mPageAdapter = pageAdapter;
        this.mLastPosition = -1;
        if (loopViewPager == null) {
            return;
        }
        if (mOnScrollListener != null) {
            loopViewPager.removeOnScrollListener(mOnScrollListener);
        }
        mOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onScrollStateChanged(recyclerView, newState);
                }

                //这里变换位置实现循环
                final int count = mPageAdapter.getRealItemCount();
                if (count <= 0 && newState != RecyclerView.SCROLL_STATE_IDLE) {
                    return;
                }
                int currentItem = getCurrentItem();
                if (currentItem < count) {
                    currentItem = count + currentItem;
                } else if (currentItem >= 2 * count) {
                    currentItem = currentItem - count;
                }
                final int position = currentItem % count;
                if (mLastPosition != position) {
                    mLastPosition = position;
                    setCurrentItem(currentItem);

                    if (mOnPageChangeListener != null) {
                        final boolean isLastPage = position == count - 1;
                        mOnPageChangeListener.onPageSelected(position, isLastPage);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onScrolled(recyclerView, dx, dy);
                }
            }
        };
        loopViewPager.addOnScrollListener(mOnScrollListener);

        initWidth();
        mPagerSnapHelper.attachToRecyclerView(loopViewPager);
    }

    /**
     * 初始化卡片宽度
     */
    private void initWidth() {
        final ViewTreeObserver vto = mLoopViewPager.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    final ViewTreeObserver currentVto = mLoopViewPager.getViewTreeObserver();
                    if (currentVto.isAlive()) {
                        currentVto.removeOnGlobalLayoutListener(this);
                    }
                    setCurrentItem(mFirstItemPos);
                }
            });
        }
    }

    public void setCurrentItem(int item) {
        setCurrentItem(item, false);
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        if (mLoopViewPager == null) {
            return;
        }
        if (smoothScroll) {
            mLoopViewPager.smoothScrollToPosition(item);
        } else {
            scrollToPosition(item);
        }
    }

    private void scrollToPosition(int pos) {
        final RecyclerView.LayoutManager layoutManager;
        if (mLoopViewPager != null
                && (layoutManager = mLoopViewPager.getLayoutManager()) instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            linearLayoutManager.scrollToPositionWithOffset(pos, 0);
        }
    }

    public void setFirstItemPos(int firstItemPos) {
        this.mFirstItemPos = firstItemPos;
    }

    public int getFirstItemPos() {
        return mFirstItemPos;
    }

    public int getCurrentItem() {
        final RecyclerView.LayoutManager layoutManager;
        final View view;
        if (mLoopViewPager != null && (layoutManager = mLoopViewPager.getLayoutManager()) != null
                && (view = mPagerSnapHelper.findSnapView(layoutManager)) != null) {
            return layoutManager.getPosition(view);
        }
        return 0;
    }

    public int getRealCurrentItem() {
        final int count = getRealItemCount();
        if (count != 0) {
            return getCurrentItem() % count;
        }
        return 0;
    }

    public int getRealItemCount() {
        if (mPageAdapter != null) {
            return mPageAdapter.getRealItemCount();
        }
        return 0;
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.mOnPageChangeListener = onPageChangeListener;
    }
}