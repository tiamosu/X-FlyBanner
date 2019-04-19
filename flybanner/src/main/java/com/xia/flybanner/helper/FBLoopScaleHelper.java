package com.xia.flybanner.helper;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

import com.xia.flybanner.adapter.FBPageAdapter;
import com.xia.flybanner.constant.FBConfig;
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
public class FBLoopScaleHelper {
    private int mFirstItemPos;

    private final PagerSnapHelper mPagerSnapHelper = new PagerSnapHelper();
    private FBLoopViewPager mLoopViewPager;
    private FBPageAdapter mPageAdapter;
    private OnPageChangeListener mOnPageChangeListener;

    public void attachToRecyclerView(@NonNull final FBLoopViewPager loopViewPager,
                                     @NonNull final FBPageAdapter adapter) {
        this.mLoopViewPager = loopViewPager;
        this.mPageAdapter = adapter;

        loopViewPager.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastPosition = -1;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onScrollStateChanged(recyclerView, newState);
                }

                //这里变换位置实现循环
                final int count = adapter.getRealItemCount();
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
                if (lastPosition != position) {
                    lastPosition = position;
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
        });

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
        if (smoothScroll && mLoopViewPager != null) {
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
            linearLayoutManager.scrollToPositionWithOffset(pos, (FBConfig.PAGE_PADDING + FBConfig.SHOW_LEFT_CARD_WIDTH));
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
