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
import com.xia.flybanner.constant.PageType;
import com.xia.flybanner.helper.FBLoopHelper;
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
    //banner 翻页类型，默认为普通循环翻页
    private int mPageType;
    //banner 翻页方向，默认为横向
    private int mPageOrientation;
    //banner 是否自动翻页
    private boolean mCanLoop;
    //banner 自动翻页间隔时间
    private long mAutoTurningTime;
    //banner 圆角设置
    private int mPageRadius;
    private int mPageTopLeftRadius, mPageTopRightRadius, mPageBottomLeftRadius, mPageBottomRightRadius;

    //是否显示指示器，默认显示
    private boolean mShowIndicator;
    //设置指示器方向，默认为横向
    private int mIndicatorOrientation;
    //设置指示器的位置，（默认为右下角）
    private int mIndicatorAlign;
    //设置指示器偏移
    private int mIndicatorMargin;
    private int mIndicatorLeftMargin, mIndicatorTopMargin, mIndicatorRightMargin, mIndicatorBottomMargin;
    //设置指示器间距
    private int mIndicatorSpacing;

    //指示器视图集
    private final ArrayList<ImageView> mPointViews = new ArrayList<>();
    //数据集
    private List<T> mDatas = new ArrayList<>();
    //视图构造
    private FBViewHolderCreator mHolderCreator;
    //数据总数
    private int mDataSize;
    //正在翻页
    private boolean mTurning;
    //是否能够手动翻页
    private boolean mCanTurn;
    //普通版 banner 翻页类型
    private boolean mIsNormalMode;

    private final FBLoopHelper mLoopHelper = new FBLoopHelper();
    private final AdSwitchTask mAdSwitchTask = new AdSwitchTask(this);
    private final FBPageChangeListener mPageChangeListener = new FBPageChangeListener();
    private FBPageAdapter mPageAdapter;
    private FBLoopViewPager mLoopViewPager;
    private LinearLayout mIndicatorView;

    public FlyBanner(Context context) {
        this(context, null);
    }

    public FlyBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlyBanner);
        mPageType = typedArray.getInt(R.styleable.FlyBanner_fb_pageType, PageType.TYPE_NORMAL);
        mPageOrientation = typedArray.getInt(R.styleable.FlyBanner_fb_pageOrientation, PageOrientation.HORIZONTAL);
        mCanLoop = typedArray.getBoolean(R.styleable.FlyBanner_fb_pageCanLoop, true);
        mAutoTurningTime = typedArray.getInteger(R.styleable.FlyBanner_fb_pageAutoTurningTime, 3000);
        mPageRadius = typedArray.getInt(R.styleable.FlyBanner_fb_pageRadius, -1);
        mPageTopLeftRadius = typedArray.getInt(R.styleable.FlyBanner_fb_pageRadiusTopLeft, 0);
        mPageTopRightRadius = typedArray.getInt(R.styleable.FlyBanner_fb_pageRadiusTopRight, 0);
        mPageBottomLeftRadius = typedArray.getInt(R.styleable.FlyBanner_fb_pageRadiusBottomLeft, 0);
        mPageBottomRightRadius = typedArray.getInt(R.styleable.FlyBanner_fb_pageRadiusBottomRight, 0);

        mShowIndicator = typedArray.getBoolean(R.styleable.FlyBanner_fb_indicatorShow, true);
        mIndicatorOrientation = typedArray.getInt(R.styleable.FlyBanner_fb_indicatorOrientation, PageIndicatorOrientation.HORIZONTAL);
        mIndicatorAlign = typedArray.getInt(R.styleable.FlyBanner_fb_indicatorAlign, PageIndicatorAlign.ALIGN_RIGHT_BOTTOM);
        mIndicatorMargin = typedArray.getInt(R.styleable.FlyBanner_fb_indicatorMargin, -1);
        mIndicatorLeftMargin = typedArray.getInt(R.styleable.FlyBanner_fb_indicatorMarginLeft, 0);
        mIndicatorRightMargin = typedArray.getInt(R.styleable.FlyBanner_fb_indicatorMarginRight, 0);
        mIndicatorTopMargin = typedArray.getInt(R.styleable.FlyBanner_fb_indicatorMarginTop, 0);
        mIndicatorBottomMargin = typedArray.getInt(R.styleable.FlyBanner_fb_indicatorMarginBottom, 0);
        mIndicatorSpacing = typedArray.getInt(R.styleable.FlyBanner_fb_indicatorSpacing, 5);
        typedArray.recycle();

        init(context);
    }

    private void init(Context context) {
        mIsNormalMode = mPageType == PageType.TYPE_NORMAL;
        if (mPageRadius != -1) {
            mPageTopLeftRadius
                    = mPageTopRightRadius
                    = mPageBottomLeftRadius
                    = mPageBottomRightRadius
                    = mPageRadius;
        }
        if (mIndicatorMargin != -1) {
            mIndicatorLeftMargin
                    = mIndicatorRightMargin
                    = mIndicatorTopMargin
                    = mIndicatorBottomMargin
                    = mIndicatorMargin;
        }

        final View view = LayoutInflater.from(context)
                .inflate(R.layout.fb_include_viewpager, this, true);
        mLoopViewPager = view.findViewById(R.id.fb_loop_vp);
        mIndicatorView = view.findViewById(R.id.fb_indicator_ll);
    }

    /**
     * 设置视图数据初始化，必须第一步调用
     */
    public PageBuilder setPages(@NonNull final FBViewHolderCreator holderCreator,
                                @NonNull final List<T> datas) {
        this.mDatas = datas;
        this.mDataSize = datas.size();
        this.mHolderCreator = holderCreator;
        return new PageBuilder(this);
    }

    public class PageBuilder {
        private FlyBanner mFlyBanner;

        public PageBuilder(FlyBanner flyBanner) {
            this.mFlyBanner = flyBanner;
        }

        public PageBuilder setType(final @PageType.Type int type) {
            this.mFlyBanner.mPageType = type;
            this.mFlyBanner.mIsNormalMode = type == PageType.TYPE_NORMAL;
            return this;
        }

        /**
         * 配置 banner 翻页方向，（默认为横向）
         * <p>
         * {@link PageOrientation.HORIZONTAL}: 横向
         * {@link PageOrientation.VERTICAL}: 竖向
         */
        public PageBuilder setOrientation(final @PageOrientation.Orientation int orientation) {
            this.mFlyBanner.mPageOrientation = orientation;
            return this;
        }

        /**
         * 设置 viewPager 圆角
         */
        public PageBuilder setRadius(final int radius) {
            setRadius(radius, radius, radius, radius);
            return this;
        }

        /**
         * 设置 viewPager 圆角
         */
        public PageBuilder setRadius(int topLeftRadius, int topRightRadius,
                                     int bottomLeftRadius, int bottomRightRadius) {
            this.mFlyBanner.mPageTopLeftRadius = topLeftRadius;
            this.mFlyBanner.mPageTopRightRadius = topRightRadius;
            this.mFlyBanner.mPageBottomLeftRadius = bottomLeftRadius;
            this.mFlyBanner.mPageBottomRightRadius = bottomRightRadius;
            return this;
        }

        public IndicatorBuilder pageBuild() {
            setPageOrientation();
            setPageRadius();
            setPageAdapter();
            return new IndicatorBuilder(mFlyBanner);
        }

        private void setPageOrientation() {
            final int orientation = mPageOrientation == PageOrientation.HORIZONTAL
                    ? RecyclerView.HORIZONTAL : RecyclerView.VERTICAL;
            final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                    getContext(), orientation, false);
            mLoopViewPager.setLayoutManager(layoutManager);
        }

        private void setPageRadius() {
            final RecyclerViewCornerRadius cornerRadius = new RecyclerViewCornerRadius(mLoopViewPager);
            if (mPageTopLeftRadius >= 0) {
                cornerRadius.mTopLeftRadius = mPageTopLeftRadius;
            }
            if (mPageTopRightRadius >= 0) {
                cornerRadius.mTopRightRadius = mPageTopRightRadius;
            }
            if (mPageBottomLeftRadius >= 0) {
                cornerRadius.mBottomLeftRadius = mPageBottomLeftRadius;
            }
            if (mPageBottomRightRadius >= 0) {
                cornerRadius.mBottomRightRadius = mPageBottomRightRadius;
            }
            mLoopViewPager.addItemDecoration(cornerRadius);
        }

        @SuppressWarnings("unchecked")
        private void setPageAdapter() {
            mPageAdapter = new FBPageAdapter(mHolderCreator, mDatas, mIsNormalMode);
            mLoopViewPager.setAdapter(mPageAdapter);
            mLoopHelper.setFirstItemPos(mIsNormalMode ? mDataSize : 0);
            mLoopHelper.attachToRecyclerView(mLoopViewPager, mPageAdapter);
        }
    }

    public class IndicatorBuilder {
        private FlyBanner mFlyBanner;
        private int[] mIndicatorId = new int[]{
                R.drawable.indicator_gray_radius, R.drawable.indicator_white_radius
        };

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
            this.mFlyBanner.mIndicatorAlign = align;
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
            this.mFlyBanner.mIndicatorOrientation = orientation;
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
            this.mFlyBanner.mIndicatorLeftMargin = leftMargin;
            this.mFlyBanner.mIndicatorTopMargin = topMargin;
            this.mFlyBanner.mIndicatorRightMargin = rightMargin;
            this.mFlyBanner.mIndicatorBottomMargin = bottomMargin;
            return this;
        }

        /**
         * 设置指示器间距
         */
        public IndicatorBuilder setIndicatorSpacing(final int indicatorSpacing) {
            this.mFlyBanner.mIndicatorSpacing = indicatorSpacing;
            return this;
        }

        /**
         * 设置指示器是否显示
         */
        public IndicatorBuilder setIndicatorVisible(final boolean indicatorVisible) {
            this.mFlyBanner.mShowIndicator = indicatorVisible;
            return this;
        }

        /**
         * 指示器配置
         */
        public FlyBuilder indicatorBuild() {
            final int visible = mShowIndicator ? VISIBLE : GONE;
            mIndicatorView.setVisibility(visible);

            setPageIndicator();
            return new FlyBuilder(mFlyBanner);
        }

        private void setPageIndicator() {
            mPointViews.clear();
            mIndicatorView.removeAllViews();
            if (mDatas.isEmpty() || mIndicatorId.length < 2
                    || mIndicatorView.getVisibility() != VISIBLE) {
                return;
            }
            setPageIndicatorOrientation();
            setPageIndicatorAlign();
            setPageIndicatorMargin();

            for (int count = 0; count < mDataSize; count++) {
                // 翻页指示的点
                final ImageView pointView = new ImageView(getContext());
                if (mIndicatorOrientation == PageIndicatorOrientation.HORIZONTAL) {
                    pointView.setPadding(mIndicatorSpacing, 0, mIndicatorSpacing, 0);
                } else {
                    pointView.setPadding(0, mIndicatorSpacing, 0, mIndicatorSpacing);
                }
                if (mLoopHelper.getFirstItemPos() % mDataSize == count) {
                    pointView.setImageResource(mIndicatorId[1]);
                } else {
                    pointView.setImageResource(mIndicatorId[0]);
                }
                mPointViews.add(pointView);
                mIndicatorView.addView(pointView);
            }

            mPageChangeListener.setPageIndicator(mPointViews, mIndicatorId);
            mLoopHelper.setOnPageChangeListener(mPageChangeListener);
        }

        private void setPageIndicatorOrientation() {
            if (mIndicatorOrientation == PageIndicatorOrientation.HORIZONTAL) {
                mIndicatorView.setOrientation(LinearLayout.HORIZONTAL);
            } else {
                mIndicatorView.setOrientation(LinearLayout.VERTICAL);
            }
        }

        private void setPageIndicatorAlign() {
            final LayoutParams layoutParams = (LayoutParams) mIndicatorView.getLayoutParams();
            final int[] verbs = {
                    RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.CENTER_VERTICAL,
                    RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.CENTER_HORIZONTAL,
                    RelativeLayout.CENTER_IN_PARENT, RelativeLayout.ALIGN_PARENT_RIGHT,
            };
            for (int verb : verbs) {
                layoutParams.addRule(verb, 0);
            }

            switch (mIndicatorAlign) {
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

        private void setPageIndicatorMargin() {
            final ViewGroup.MarginLayoutParams layoutParams
                    = (MarginLayoutParams) mIndicatorView.getLayoutParams();
            if (mIndicatorLeftMargin >= 0) {
                layoutParams.leftMargin = mIndicatorLeftMargin;
            }
            if (mIndicatorTopMargin >= 0) {
                layoutParams.topMargin = mIndicatorTopMargin;
            }
            if (mIndicatorRightMargin >= 0) {
                layoutParams.rightMargin = mIndicatorRightMargin;
            }
            if (mIndicatorBottomMargin >= 0) {
                layoutParams.bottomMargin = mIndicatorBottomMargin;
            }
            mIndicatorView.setLayoutParams(layoutParams);
        }
    }

    public class FlyBuilder {
        private FlyBanner mFlyBanner;

        public FlyBuilder(FlyBanner flyBanner) {
            this.mFlyBanner = flyBanner;
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
        mPageChangeListener.setOnPageChangeListener(onPageChangeListener);
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
        final int page = mIsNormalMode ? mDataSize + position : position;
        mLoopHelper.setCurrentItem(page, true);
        startTurning();
        return this;
    }

    /**
     * 获取当前页对应的 position
     */
    public int getCurrentItem() {
        return mLoopHelper.getRealCurrentItem();
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
                final int page = banner.mLoopHelper.getCurrentItem() + 1;
                if (!banner.mIsNormalMode && page == banner.mDataSize) {
                    banner.stopTurning();
                    return;
                }
                banner.mLoopHelper.setCurrentItem(page, true);
                banner.postDelayed(banner.mAdSwitchTask, banner.mAutoTurningTime);
            }
        }
    }
}
