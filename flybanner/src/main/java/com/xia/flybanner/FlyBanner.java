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
import com.xia.flybanner.constant.PageIndicatorAlign;
import com.xia.flybanner.constant.PageIndicatorOrientation;
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
    private long mAutoTurningTime;
    private boolean mCanLoop;
    private boolean mTurning;
    private boolean mCanTurn;

    private FBPageAdapter mPageAdapter;
    private FBLoopViewPager mLoopViewPager;
    private LinearLayout mIndicatorView;
    private final FBLoopScaleHelper mLoopScaleHelper = new FBLoopScaleHelper();
    private FBPageChangeListener mPageChangeListener;
    private AdSwitchTask mAdSwitchTask;

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
        mLoopViewPager = view.findViewById(R.id.fb_loop_vp);
        mIndicatorView = view.findViewById(R.id.fb_indicator_ll);

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
        private int[] mIndicatorId;
        private Integer mIndicatorAlign;

        IndicatorBuilder(FlyBanner flyBanner) {
            mFlyBanner = flyBanner;
        }

        /**
         * 设置指示器是否可见，默认为可见
         */
        public IndicatorBuilder setIndicatorVisible(boolean visible) {
            mIndicatorView.setVisibility(visible ? View.VISIBLE : View.GONE);
            return this;
        }

        /**
         * 设置指示器样式
         */
        public IndicatorBuilder setIndicatorId(@IdRes int[] indicatorId) {
            mIndicatorId = indicatorId;
            return this;
        }

        /**
         * 设置指示器位置，默认为右下角
         */
        public IndicatorBuilder setIndicatorAlign(@PageIndicatorAlign.IndicatorAlign int align) {
            mIndicatorAlign = align;
            return this;
        }

        /**
         * 设置指示器方向：横向（HORIZONTAL）、竖向（VERTICAL），默认为横向
         */
        public IndicatorBuilder setIndicatorOrientation(@PageIndicatorOrientation.OrientationMode int orientation) {
            if (orientation == PageIndicatorOrientation.VERTICAL) {
                mIndicatorView.setOrientation(LinearLayout.VERTICAL);
            } else {
                mIndicatorView.setOrientation(LinearLayout.HORIZONTAL);
            }
            return this;
        }

        /**
         * 设置指示器偏移
         */
        public IndicatorBuilder setIndicatorMargin(@Nullable Integer margin) {
            setIndicatorMargin(margin, margin, margin, margin);
            return this;
        }

        /**
         * 设置指示器偏移
         */
        public IndicatorBuilder setIndicatorMargin(@Nullable Integer leftMargin, @Nullable Integer topMargin,
                                                   @Nullable Integer rightMargin, @Nullable Integer bottomMargin) {

            if (mIndicatorView.getVisibility() == VISIBLE) {
                final ViewGroup.MarginLayoutParams layoutParams
                        = (MarginLayoutParams) mIndicatorView.getLayoutParams();
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
                mIndicatorView.setLayoutParams(layoutParams);
            }
            return this;
        }

        /**
         * 指示器配置
         */
        public CommonBuilder useIndicator() {
            if (mIndicatorView.getVisibility() == VISIBLE) {
                setPageIndicator(mIndicatorId, mIndicatorAlign);
            }
            return new CommonBuilder(mFlyBanner);
        }
    }

    public class CommonBuilder {
        private FlyBanner mFlyBanner;

        public CommonBuilder(FlyBanner flyBanner) {
            mFlyBanner = flyBanner;
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

    private void setPageIndicator(@Nullable @IdRes int[] indicatorId,
                                  @Nullable @PageIndicatorAlign.IndicatorAlign Integer align) {
        mPointViews.clear();
        mIndicatorView.removeAllViews();

        if (indicatorId == null) {
            indicatorId = new int[]{R.drawable.indicator_gray_radius, R.drawable.indicator_white_radius};
        }
        if (mDatas.isEmpty() || indicatorId.length < 2) {
            return;
        }

        for (int count = 0; count < mDataSize; count++) {
            // 翻页指示的点
            final ImageView pointView = new ImageView(getContext());
            if (mIndicatorView.getOrientation() == LinearLayout.HORIZONTAL) {
                pointView.setPadding(5, 0, 5, 0);
            } else {
                pointView.setPadding(0, 5, 0, 5);
            }
            if (mLoopScaleHelper.getFirstItemPos() % mDataSize == count) {
                pointView.setImageResource(indicatorId[1]);
            } else {
                pointView.setImageResource(indicatorId[0]);
            }
            mPointViews.add(pointView);
            mIndicatorView.addView(pointView);
        }

        mPageChangeListener = new FBPageChangeListener(mPointViews, indicatorId);
        mLoopScaleHelper.setOnPageChangeListener(mPageChangeListener);

        setPageIndicatorAlign(align);
    }

    /**
     * 指示器的位置
     */
    private void setPageIndicatorAlign(@Nullable @PageIndicatorAlign.IndicatorAlign Integer align) {
        final ViewGroup.LayoutParams params;
        if (!((params = mIndicatorView.getLayoutParams()) instanceof RelativeLayout.LayoutParams)) {
            return;
        }
        final LayoutParams layoutParams = (LayoutParams) params;
        if (align == null) {
            align = PageIndicatorAlign.ALIGN_RIGHT_BOTTOM;
        }
        switch (align) {
            case PageIndicatorAlign.ALIGN_LEFT_TOP:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                break;
            case PageIndicatorAlign.ALIGN_LEFT_CENTER:
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                break;
            case PageIndicatorAlign.ALIGN_LEFT_BOTTOM:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                break;
            case PageIndicatorAlign.ALIGN_TOP_CENTER:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                break;
            case PageIndicatorAlign.ALIGN_IN_CENTER:
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                break;
            case PageIndicatorAlign.ALIGN_BOTTOM_CENTER:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                break;
            case PageIndicatorAlign.ALIGN_RIGHT_TOP:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                break;
            case PageIndicatorAlign.ALIGN_RIGHT_CENTER:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                break;
            case PageIndicatorAlign.ALIGN_RIGHT_BOTTOM:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                break;
            default:
                break;
        }
        mIndicatorView.setLayoutParams(layoutParams);
    }

    /**
     * 设置是否自动翻页
     */
    public FlyBanner setCanLoop(boolean canLoop) {
        this.mCanLoop = canLoop;
        if (!canLoop) {
            stopTurning();
            return this;
        }
        if (mTurning) {
            return this;
        }
        startTurning();
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
    public FlyBanner setCurrentItem(int position) {
        stopTurning();
        final int page = mCanLoop ? mDataSize + position : position;
        mLoopScaleHelper.setCurrentItem(page, true);
        startTurning();
        return this;
    }

    /**
     * 获取 viewPager
     */
    public FBLoopViewPager getLoopViewPager() {
        return mLoopViewPager;
    }

    /**
     * 获取 viewPager 布局管理
     */
    public RecyclerView.LayoutManager getLayoutManager() {
        return mLoopViewPager.getLayoutManager();
    }

    /**
     * 获取当前页对应的 position
     */
    public int getCurrentItem() {
        return mLoopScaleHelper.getRealCurrentItem();
    }

    /**
     * 是否开启了自动翻页
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
        stopTurning();
        //设置可以翻页并开启翻页
        mCanTurn = true;
        mTurning = true;
        postDelayed(mAdSwitchTask, autoTurningTime);
    }

    public void startTurning() {
        startTurning(mAutoTurningTime);
    }

    /**
     * 停止翻页
     */
    public void stopTurning() {
        if (mTurning) {
            mTurning = false;
            removeCallbacks(mAdSwitchTask);
        }
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
            startTurning(mAutoTurningTime);
        } else if (action == MotionEvent.ACTION_DOWN) {
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
