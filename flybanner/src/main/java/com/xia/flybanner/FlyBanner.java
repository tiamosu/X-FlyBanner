package com.xia.flybanner;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xia.flybanner.adapter.FBPageAdapter;
import com.xia.flybanner.helper.FBLoopScaleHelper;
import com.xia.flybanner.holder.FBViewHolderCreator;
import com.xia.flybanner.listener.FBPageChangeListener;
import com.xia.flybanner.listener.OnItemClickListener;
import com.xia.flybanner.listener.OnPageChangeListener;
import com.xia.flybanner.view.FBLoopViewPager;
import com.xia.flybanner.view.RecyclerViewCornerRadius;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author weixia
 * @date 2019/4/16.
 */
@SuppressWarnings("all")
public class FlyBanner<T> extends RelativeLayout {
    private final ArrayList<ImageView> mPointViews = new ArrayList<>();
    private List<T> mDatas = new ArrayList<>();
    private int mDataSize;
    private int[] mPageIndicatorId;
    private long mAutoTurningTime;
    private boolean mCanLoop;
    private boolean mTurning;
    private boolean mCanTurn;

    private FBPageAdapter mPageAdapter;
    private FBLoopViewPager mLoopViewPager;
    private LinearLayout mLoPageTurningPoint;
    private final FBLoopScaleHelper mLoopScaleHelper = new FBLoopScaleHelper();
    private FBPageChangeListener mPageChangeListener;
    private AdSwitchTask mAdSwitchTask;

    public enum PageIndicatorAlign {
        ALIGN_PARENT_LEFT, ALIGN_PARENT_RIGHT, CENTER_HORIZONTAL
    }

    public FlyBanner(Context context) {
        this(context, null);
    }

    public FlyBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlyBanner);
        mCanLoop = typedArray.getBoolean(R.styleable.FlyBanner_fb_canLoop, true);
        mAutoTurningTime = typedArray.getInteger(R.styleable.FlyBanner_fb_autoTurningTime, -1);
        typedArray.recycle();

        init(context);
    }

    private void init(Context context) {
        final View view = LayoutInflater.from(context)
                .inflate(R.layout.fb_include_viewpager, this, true);
        mLoopViewPager = view.findViewById(R.id.fb_loopViewPager);
        mLoPageTurningPoint = view.findViewById(R.id.fb_loPageTurningPoint);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                context, LinearLayoutManager.HORIZONTAL, false);
        mLoopViewPager.setLayoutManager(linearLayoutManager);
        mAdSwitchTask = new AdSwitchTask(this);
    }

    /**
     * 设置视图数据初始化，必须第一步调用
     */
    public IndicatorBuilder setPages(@NonNull FBViewHolderCreator holderCreator,
                                     @NonNull List<T> datas) {
        this.mDatas = datas;
        this.mDataSize = datas.size();
        mPageAdapter = new FBPageAdapter(holderCreator, datas, mCanLoop);
        mLoopViewPager.setAdapter(mPageAdapter);

        mLoopScaleHelper.setFirstItemPos(mCanLoop ? mDataSize : 0);
        mLoopScaleHelper.attachToRecyclerView(mLoopViewPager);
        return new IndicatorBuilder(this);
    }

    public class IndicatorBuilder {
        private FlyBanner mFlyBanner;

        IndicatorBuilder(FlyBanner flyBanner) {
            mFlyBanner = flyBanner;
        }

        /**
         * 默认使用指示器样式及位置（右下角）
         */
        public CommonBuilder useIndicator() {
            useIndicator(null, null);
            return new CommonBuilder(mFlyBanner);
        }

        /**
         * 设置指示器样式及默认位置（右下角）
         */
        public CommonBuilder useIndicator(@NonNull @IdRes int[] indicatorId) {
            useIndicator(indicatorId, null);
            return new CommonBuilder(mFlyBanner);
        }

        /**
         * 默认使用指示器样式及自定义位置
         */
        public CommonBuilder useIndicator(@NonNull PageIndicatorAlign align) {
            useIndicator(null, align);
            return new CommonBuilder(mFlyBanner);
        }

        /**
         * 设置指示器样式及位置
         */
        public CommonBuilder useIndicator(@Nullable @IdRes int[] indicatorId,
                                          @Nullable PageIndicatorAlign align) {
            indicatorId = indicatorId != null ? indicatorId
                    : new int[]{R.drawable.indicator_gray_radius, R.drawable.indicator_white_radius};
            this.mFlyBanner.mPageIndicatorId = indicatorId;

            setPageIndicator(indicatorId);
            setPageIndicatorAlign(align);
            return new CommonBuilder(mFlyBanner);
        }
    }

    public class CommonBuilder {
        private FlyBanner mFlyBanner;

        public CommonBuilder(FlyBanner flyBanner) {
            mFlyBanner = flyBanner;
        }

        /**
         * 设置指示器偏移
         *
         * @param leftMargin
         * @param topMargin
         * @param rightMargin
         * @param bottomMargin
         * @return
         */
        public CommonBuilder setIndicatorMargin(@Nullable Integer leftMargin, @Nullable Integer topMargin,
                                                @Nullable Integer rightMargin, @Nullable Integer bottomMargin) {
            final ViewGroup.MarginLayoutParams layoutParams
                    = (MarginLayoutParams) mLoPageTurningPoint.getLayoutParams();
            if (leftMargin != null && leftMargin >= 0) {
                layoutParams.leftMargin = leftMargin;
            }
            if (topMargin != null && topMargin >= 0) {
                layoutParams.topMargin = topMargin;
            }
            if (rightMargin != null && rightMargin >= 0) {
                layoutParams.rightMargin = rightMargin;
            }
            if (bottomMargin != null && bottomMargin >= 0) {
                layoutParams.bottomMargin = bottomMargin;
            }
            mLoPageTurningPoint.setLayoutParams(layoutParams);
            return this;
        }

        /**
         * 设置翻页效果
         */
        public CommonBuilder setLayoutManager(@NonNull RecyclerView.LayoutManager layoutManager) {
            mLoopViewPager.setLayoutManager(layoutManager);
            return this;
        }

        /**
         * 设置 viewPager 圆角
         */
        public CommonBuilder setRadius(@NonNull Integer radius) {
            setRadius(radius, radius, radius, radius);
            return this;
        }

        /**
         * 设置 viewPager 圆角
         */
        public CommonBuilder setRadius(@Nullable Integer topLeftRadius, @Nullable Integer topRightRadius,
                                       @Nullable Integer bottomLeftRadius, @Nullable Integer bottomRightRadius) {
            topLeftRadius = (topLeftRadius == null || topLeftRadius < 0) ? 0 : topLeftRadius;
            topRightRadius = (topRightRadius == null || topRightRadius < 0) ? 0 : topRightRadius;
            bottomLeftRadius = (bottomLeftRadius == null || bottomLeftRadius < 0) ? 0 : bottomLeftRadius;
            bottomRightRadius = (bottomRightRadius == null || bottomRightRadius < 0) ? 0 : bottomRightRadius;

            final RecyclerViewCornerRadius cornerRadius = new RecyclerViewCornerRadius(mLoopViewPager);
            cornerRadius.setCornerRadius(topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius);
            mLoopViewPager.addItemDecoration(cornerRadius);
            return this;
        }

        /**
         * 开始执行，最后一步调用
         */
        public FlyBanner start(long autoTurningTime) {
            startTurning(autoTurningTime);
            return mFlyBanner;
        }
    }

    private void setPageIndicator(@Nullable @IdRes int[] indicatorId) {
        mPointViews.clear();
        mLoPageTurningPoint.removeAllViews();
        if (mDatas.isEmpty() || indicatorId.length < 2) {
            return;
        }

        for (int count = 0; count < mDataSize; count++) {
            // 翻页指示的点
            final ImageView pointView = new ImageView(getContext());
            pointView.setPadding(5, 0, 5, 0);
            if (mLoopScaleHelper.getFirstItemPos() % mDataSize == count) {
                pointView.setImageResource(indicatorId[1]);
            } else {
                pointView.setImageResource(indicatorId[0]);
            }
            mPointViews.add(pointView);
            mLoPageTurningPoint.addView(pointView);
        }

        mPageChangeListener = new FBPageChangeListener(mPointViews, indicatorId);
        mLoopScaleHelper.setOnPageChangeListener(mPageChangeListener);
    }

    /**
     * 指示器的方向
     *
     * @param align 三个方向：
     *              居左 （RelativeLayout.ALIGN_PARENT_LEFT），
     *              居中 （RelativeLayout.CENTER_HORIZONTAL），
     *              居右 （RelativeLayout.ALIGN_PARENT_RIGHT）
     */
    private void setPageIndicatorAlign(@Nullable PageIndicatorAlign align) {
        if (mDatas.isEmpty() || mPageIndicatorId == null || mPageIndicatorId.length < 2) {
            return;
        }
        align = align != null ? align : PageIndicatorAlign.ALIGN_PARENT_RIGHT;

        final RelativeLayout.LayoutParams layoutParams
                = (RelativeLayout.LayoutParams) mLoPageTurningPoint.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
                align == PageIndicatorAlign.ALIGN_PARENT_LEFT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
                align == PageIndicatorAlign.ALIGN_PARENT_RIGHT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
                align == PageIndicatorAlign.CENTER_HORIZONTAL ? RelativeLayout.TRUE : 0);
        mLoPageTurningPoint.setLayoutParams(layoutParams);
    }

    /**
     * 设置是否自动轮播
     */
    public FlyBanner setCanLoop(boolean canLoop) {
        this.mCanLoop = canLoop;
        if (!canLoop) {
            stopTurning();
        }
        if (mPageAdapter != null) {
            mPageAdapter.setCanLoop(canLoop);
        }
        notifyDataSetChanged();
        return this;
    }

    /**
     * 通知数据变化
     */
    public FlyBanner notifyDataSetChanged() {
        final RecyclerView.Adapter adapter;
        if ((adapter = mLoopViewPager.getAdapter()) != null) {
            adapter.notifyDataSetChanged();
        }
        if (mPageIndicatorId != null) {
            setPageIndicator(mPageIndicatorId);
        }
        mLoopScaleHelper.setCurrentItem(mCanLoop ? mDataSize : 0);
        return this;
    }

    /**
     * 设置底部指示器是否可见
     */
    public FlyBanner setPointViewVisible(boolean visible) {
        mLoPageTurningPoint.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 设置翻页监听器
     */
    public FlyBanner setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        //如果有默认的监听器（即是使用了默认的翻页指示器）则把用户设置的依附到默认的上面，否则就直接设置
        if (mPageChangeListener != null) {
            mPageChangeListener.setOnPageChangeListener(onPageChangeListener);
        } else {
            mLoopScaleHelper.setOnPageChangeListener(onPageChangeListener);
        }
        return this;
    }

    /**
     * 设置 item 点击事件监听
     */
    public FlyBanner setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        if (mPageAdapter != null) {
            mPageAdapter.setOnItemClickListener(onItemClickListener);
        }
        return this;
    }

    /**
     * 设置当前页对应的 position
     */
    public FlyBanner setCurrentItem(int position, boolean smoothScroll) {
        mLoopScaleHelper.setCurrentItem(mCanLoop ? mDataSize + position : position, smoothScroll);
        return this;
    }

    /**
     * 获取 viewPager
     */
    public FBLoopViewPager getLoopViewPager() {
        return mLoopViewPager;
    }

    /**
     * 获取当前页对应的 position
     */
    public int getCurrentItem() {
        return mLoopScaleHelper.getRealCurrentItem();
    }

    /**
     * 是否开启了自动轮播
     */
    public boolean isCanLoop() {
        return mCanLoop;
    }

    /***
     * 开始翻页
     * @param autoTurningTime 自动翻页时间
     */
    public void startTurning(long autoTurningTime) {
        this.mAutoTurningTime = autoTurningTime;
        if (autoTurningTime < 0 || !mCanLoop || mDataSize <= 1) {
            return;
        }
        //如果是正在翻页的话先停掉
        if (mTurning) {
            stopTurning();
        }
        //设置可以翻页并开启翻页
        mCanTurn = true;
        mTurning = true;
        postDelayed(mAdSwitchTask, autoTurningTime);
    }

    public void startTurning() {
        startTurning(mAutoTurningTime);
    }

    public void stopTurning() {
        mCanTurn = false;
        mTurning = false;
        removeCallbacks(mAdSwitchTask);
    }

    //触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mCanTurn) {
            return super.dispatchTouchEvent(ev);
        }
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL
                || action == MotionEvent.ACTION_OUTSIDE) {
            // 开始翻页
            startTurning(mAutoTurningTime);
        } else if (action == MotionEvent.ACTION_DOWN) {
            // 停止翻页
            stopTurning();
        }
        return super.dispatchTouchEvent(ev);
    }

    private static class AdSwitchTask implements Runnable {

        private final WeakReference<FlyBanner> mReference;

        AdSwitchTask(FlyBanner convenientBanner) {
            this.mReference = new WeakReference<>(convenientBanner);
        }

        @Override
        public void run() {
            final FlyBanner banner = mReference.get();
            if (banner != null && banner.mTurning) {
                final int page = banner.mLoopScaleHelper.getCurrentItem() + 1;
                banner.mLoopScaleHelper.setCurrentItem(page, true);
                banner.postDelayed(banner.mAdSwitchTask, banner.mAutoTurningTime);
            }
        }
    }
}
