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
@SuppressWarnings("unused")
public class FBLoopScaleHelper {
    private int mPagePadding = 0; // 卡片的padding, 卡片间的距离等于2倍的mPagePadding
    private int mShowLeftCardWidth = 0;   // 左边卡片显示大小
    private int mFirstItemPos;

    private final PagerSnapHelper mPagerSnapHelper = new PagerSnapHelper();
    private FBLoopViewPager mLoopViewPager;
    private OnPageChangeListener onPageChangeListener;

    public void attachToRecyclerView(final FBLoopViewPager loopViewPager) {
        this.mLoopViewPager = loopViewPager;
        if (loopViewPager == null) {
            return;
        }

        initWidth();
        mPagerSnapHelper.attachToRecyclerView(loopViewPager);

        loopViewPager.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                //这里变换位置实现循环
                final RecyclerView.Adapter adapter;
                if (!((adapter = loopViewPager.getAdapter()) instanceof FBPageAdapter)) {
                    return;
                }
                final FBPageAdapter pagerAdapter = (FBPageAdapter) adapter;
                final int count = pagerAdapter.getRealItemCount();
                int position = getCurrentItem();
                if (pagerAdapter.isCanLoop()) {
                    if (position < count) {
                        position = count + position;
                        setCurrentItem(position);
                    } else if (position >= 2 * count) {
                        position = position - count;
                        setCurrentItem(position);
                    }
                }
                if (onPageChangeListener != null) {
                    onPageChangeListener.onScrollStateChanged(recyclerView, newState);
                    //停止滚动
                    if (count != 0 && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        onPageChangeListener.onPageSelected(position % count);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (onPageChangeListener != null) {
                    onPageChangeListener.onScrolled(recyclerView, dx, dy);
                }
                onScrolledChangedCallback();
            }
        });
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
                    scrollToPosition(mFirstItemPos);
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
            linearLayoutManager.scrollToPositionWithOffset(pos, (mPagePadding + mShowLeftCardWidth));
            mLoopViewPager.post(new Runnable() {
                @Override
                public void run() {
                    onScrolledChangedCallback();
                }
            });
        }
    }

    public void setFirstItemPos(int firstItemPos) {
        this.mFirstItemPos = firstItemPos;
    }

    /**
     * RecyclerView位移事件监听, view大小随位移事件变化
     */
    private void onScrolledChangedCallback() {

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
        final RecyclerView.Adapter adapter;
        if (mLoopViewPager != null
                && (adapter = mLoopViewPager.getAdapter()) instanceof FBPageAdapter) {
            final FBPageAdapter pageAdapter = (FBPageAdapter) adapter;
            final int count = pageAdapter.getRealItemCount();
            return getCurrentItem() % count;
        }
        return 0;
    }

    public void setPagePadding(int pagePadding) {
        mPagePadding = pagePadding;
    }

    public void setShowLeftCardWidth(int showLeftCardWidth) {
        mShowLeftCardWidth = showLeftCardWidth;
    }

    public int getFirstItemPos() {
        return mFirstItemPos;
    }

    public int getRealItemCount() {
        final RecyclerView.Adapter adapter;
        if (mLoopViewPager != null
                && (adapter = mLoopViewPager.getAdapter()) instanceof FBPageAdapter) {
            final FBPageAdapter pageAdapter = (FBPageAdapter) adapter;
            return pageAdapter.getRealItemCount();
        }
        return 0;
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }
}
