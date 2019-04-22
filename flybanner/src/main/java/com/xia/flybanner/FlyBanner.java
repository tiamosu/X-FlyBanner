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
import com.xia.flybanner.constant.PageOrientation;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author weixia
 * @date 2019/4/16.
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "JavadocReference"})
public class FlyBanner<T> extends RelativeLayout {
    private final ArrayList<ImageView> mPointViews = new ArrayList<>();
    private List<T> mDatas = new ArrayList<>();
    private FBViewHolderCreator mHolderCreator;
    private int mDataSize;
    private long mAutoTurningTime;
    private boolean mCanLoop;
    private boolean mTurning;
    private boolean mCanTurn;
    private boolean mIsGuidePage;

    private final FBLoopScaleHelper mLoopScaleHelper = new FBLoopScaleHelper();
    private final AdSwitchTask mAdSwitchTask = new AdSwitchTask(this);
    private FBPageAdapter mPageAdapter;
    private FBLoopViewPager mLoopViewPager;
    private LinearLayout mIndicatorView;
    private FBPageChangeListener mPageChangeListener;

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
    }

    /**
     * 设置视图数据初始化，必须第一步调用
     */
    public FlyBuilder setPages(@NonNull final FBViewHolderCreator holderCreator,
                               @NonNull final List<T> datas) {
        this.mDatas = datas;
        this.mDataSize = datas.size();
        this.mHolderCreator = holderCreator;
        return new FlyBuilder(this);
    }

    public class FlyBuilder {
        private FlyBanner mFlyBanner;
        private int mOrientation = PageOrientation.HORIZONTAL;
        private boolean mIsPageMode;
        private int mSecondaryExposed;
        private float mSecondaryExposedWeight;
        private float mScaleGap;

        public FlyBuilder(FlyBanner flyBanner) {
            this.mFlyBanner = flyBanner;
        }

        /**
         * 配置 banner 翻页方向，（默认为横向）
         * <p>
         * {@link PageOrientation.HORIZONTAL}: 横向
         * {@link PageOrientation.VERTICAL}: 竖向
         */
        public FlyBuilder setOrientation(final @PageOrientation.Orientation int orientation) {
            this.mOrientation = orientation;
            return this;
        }

        /**
         * 配置 banner 是否为引导页面
         */
        public FlyBuilder setGuidePage(final boolean isGuidePage) {
            this.mFlyBanner.mIsGuidePage = isGuidePage;
            return this;
        }

        /**
         * 是否是 ViewPager 模式，如果是，那么将一次只能翻动1页
         */
        public FlyBuilder setPageMode(final boolean isPageMode) {
            this.mIsPageMode = isPageMode;
            return this;
        }

        /**
         * 次要方块的露出距离，此处单位为px
         * <p>
         * {@link PageOrientation.HORIZONTAL}时，表示左右侧Item的露出距离
         * {@link PageOrientation.VERTICAL}时，表示上下方Item的露出距离
         */
        public FlyBuilder setSecondaryExposed(final int secondaryExposed) {
            this.mSecondaryExposed = secondaryExposed;
            return this;
        }

        /**
         * 次要方块的露出距离的权重
         * 注意，只有当{@link #mSecondaryExposed = 0}时，才会使用此属性
         * 此权重为相对{@link RecyclerView}而言，并且分别针对左右两侧。
         * 即：当{@link #mSecondaryExposedWeight = 0.1F}时，
         * 那么主 item 的宽度为 RecyclerView.width * 0.8F
         */
        public FlyBuilder secondaryExposedWeight(final float secondaryExposedWeight) {
            this.mSecondaryExposedWeight = secondaryExposedWeight;
            return this;
        }

        /**
         * 次 item 缩放量
         * 表示当 item 位于次 item 位置时，显示尺寸相对于完整尺寸的量
         */
        public FlyBuilder scaleGap(final float scaleGap) {
            this.mScaleGap = scaleGap;
            return this;
        }

        public IndicatorBuilder pageBuild() {
            setPageLayoutManager();
            setPageOrientation(mOrientation);
            setPageAdapter();
            return new IndicatorBuilder(mFlyBanner);
        }

        private void setPageLayoutManager() {

        }

        private void setPageOrientation(final int orientation) {
            final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                    getContext(), (orientation == PageOrientation.HORIZONTAL
                    ? RecyclerView.HORIZONTAL : RecyclerView.VERTICAL), false);
            mLoopViewPager.setLayoutManager(layoutManager);
        }

        @SuppressWarnings("unchecked")
        private void setPageAdapter() {
            mPageAdapter = new FBPageAdapter(mHolderCreator, mDatas, mIsGuidePage);
            mLoopViewPager.setAdapter(mPageAdapter);
            mLoopScaleHelper.setFirstItemPos(mIsGuidePage ? 0 : mDataSize);
            mLoopScaleHelper.attachToRecyclerView(mLoopViewPager, mPageAdapter);
        }
    }

    public class IndicatorBuilder {
        private FlyBanner mFlyBanner;
        private int[] mIndicatorId = new int[]{
                R.drawable.indicator_gray_radius, R.drawable.indicator_white_radius
        };
        private int mIndicatorAlign = PageIndicatorAlign.ALIGN_RIGHT_BOTTOM;
        private int mIndicatorOrientation = PageIndicatorOrientation.HORIZONTAL;
        private int mLeftMargin, mTopMargin, mRightMargin, mBottomMargin;

        public IndicatorBuilder(FlyBanner flyBanner) {
            this.mFlyBanner = flyBanner;
        }

        /**
         * 设置指示器样式
         */
        public IndicatorBuilder setIndicatorId(final @IdRes int[] indicatorId) {
            this.mIndicatorId = indicatorId;
            return this;
        }

        /**
         * 指示器的位置，（默认为右下角）
         * <p>
         * {@link PageIndicatorAlign.ALIGN_LEFT_TOP}: 左上角
         * {@link PageIndicatorAlign.ALIGN_LEFT_CENTER}: 左竖向中间
         * {@link PageIndicatorAlign.ALIGN_LEFT_BOTTOM}: 左下角
         * {@link PageIndicatorAlign.ALIGN_TOP_CENTER}: 上横向中间
         * {@link PageIndicatorAlign.ALIGN_IN_CENTER}: 中间
         * {@link PageIndicatorAlign.ALIGN_BOTTOM_CENTER}: 下横向中间
         * {@link PageIndicatorAlign.ALIGN_RIGHT_TOP}: 右上角
         * {@link PageIndicatorAlign.ALIGN_RIGHT_CENTER}: 右竖向中间
         * {@link PageIndicatorAlign.ALIGN_RIGHT_BOTTOM}: 右下角
         */
        public IndicatorBuilder setIndicatorAlign(final @PageIndicatorAlign.IndicatorAlign int align) {
            this.mIndicatorAlign = align;
            return this;
        }

        /**
         * 设置指示器方向，（默认为横向）
         * <p>
         * {@link PageIndicatorOrientation.HORIZONTAL}: 横向，
         * {@link PageIndicatorOrientation.VERTICAL}: 竖向
         */
        public IndicatorBuilder setIndicatorOrientation(
                final @PageIndicatorOrientation.IndicatorOrientation int orientation) {
            this.mIndicatorOrientation = orientation;
            return this;
        }

        /**
         * 设置指示器偏移
         */
        public IndicatorBuilder setIndicatorMargin(final int margin) {
            setIndicatorMargin(margin, margin, margin, margin);
            return this;
        }

        /**
         * 设置指示器偏移
         */
        public IndicatorBuilder setIndicatorMargin(final int leftMargin, final int topMargin,
                                                   final int rightMargin, final int bottomMargin) {
            this.mLeftMargin = leftMargin;
            this.mTopMargin = topMargin;
            this.mRightMargin = rightMargin;
            this.mBottomMargin = bottomMargin;
            return this;
        }

        /**
         * 指示器配置
         */
        public CommonBuilder indicatorBuild(final boolean isVisible) {
            final int visible = isVisible ? VISIBLE : GONE;
            mIndicatorView.setVisibility(visible);

            setPageIndicator(mIndicatorId, mIndicatorAlign, mIndicatorOrientation,
                    mLeftMargin, mTopMargin, mRightMargin, mBottomMargin);
            return new CommonBuilder(mFlyBanner);
        }

        private void setPageIndicator(final int[] indicatorId, final int indicatorAlign,
                                      final int indicatorOrientation, final int leftMargin,
                                      final int topMargin, final int rightMargin, final int bottomMargin) {
            mPointViews.clear();
            mIndicatorView.removeAllViews();
            if (mDatas.isEmpty() || indicatorId.length < 2 || mIndicatorView.getVisibility() != VISIBLE) {
                return;
            }
            setPageIndicatorOrientation(indicatorOrientation);
            setPageIndicatorAlign(indicatorAlign);
            setPageIndicatorMargin(leftMargin, topMargin, rightMargin, bottomMargin);

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
        }

        private void setPageIndicatorOrientation(final int indicatorOrientation) {
            if (indicatorOrientation == PageIndicatorOrientation.HORIZONTAL) {
                mIndicatorView.setOrientation(LinearLayout.HORIZONTAL);
            } else {
                mIndicatorView.setOrientation(LinearLayout.VERTICAL);
            }
        }

        private void setPageIndicatorAlign(final int indicatorAlign) {
            final LayoutParams layoutParams = (LayoutParams) mIndicatorView.getLayoutParams();
            final int[] verbs = {
                    RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.CENTER_VERTICAL,
                    RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.CENTER_HORIZONTAL,
                    RelativeLayout.CENTER_IN_PARENT, RelativeLayout.ALIGN_PARENT_RIGHT,
            };
            for (int verb : verbs) {
                layoutParams.addRule(verb, 0);
            }

            switch (indicatorAlign) {
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

        private void setPageIndicatorMargin(final int leftMargin, final int topMargin,
                                            final int rightMargin, final int bottomMargin) {
            final ViewGroup.MarginLayoutParams layoutParams
                    = (MarginLayoutParams) mIndicatorView.getLayoutParams();
            if (leftMargin >= 0) {
                layoutParams.leftMargin = leftMargin;
            }
            if (topMargin >= 0) {
                layoutParams.topMargin = topMargin;
            }
            if (rightMargin >= 0) {
                layoutParams.rightMargin = rightMargin;
            }
            if (bottomMargin >= 0) {
                layoutParams.bottomMargin = bottomMargin;
            }
            mIndicatorView.setLayoutParams(layoutParams);
        }
    }

    public class CommonBuilder {
        private FlyBanner mFlyBanner;

        public CommonBuilder(FlyBanner flyBanner) {
            this.mFlyBanner = flyBanner;
        }

        /**
         * 设置 viewPager 圆角
         */
        public CommonBuilder setRadius(final int radius) {
            setRadius(radius, radius, radius, radius);
            return this;
        }

        /**
         * 设置 viewPager 圆角
         */
        public CommonBuilder setRadius(int topLeftRadius, int topRightRadius,
                                       int bottomLeftRadius, int bottomRightRadius) {
            final RecyclerViewCornerRadius cornerRadius = new RecyclerViewCornerRadius(mLoopViewPager);
            if (topLeftRadius >= 0) {
                cornerRadius.mTopLeftRadius = topLeftRadius;
            }
            if (topRightRadius >= 0) {
                cornerRadius.mTopRightRadius = topRightRadius;
            }
            if (bottomLeftRadius >= 0) {
                cornerRadius.mBottomLeftRadius = bottomLeftRadius;
            }
            if (bottomRightRadius >= 0) {
                cornerRadius.mBottomRightRadius = bottomRightRadius;
            }
            mLoopViewPager.addItemDecoration(cornerRadius);
            return this;
        }

        /**
         * 开始执行，最后一步调用
         */
        public FlyBanner start(final long autoTurningTime) {
            startTurning(autoTurningTime);
            return mFlyBanner;
        }
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
        final int page = mIsGuidePage ? position : mDataSize + position;
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

        stopTurning();
        if (autoTurningTime < 0 || !mCanLoop || mDataSize <= 1) {
            return;
        }
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
