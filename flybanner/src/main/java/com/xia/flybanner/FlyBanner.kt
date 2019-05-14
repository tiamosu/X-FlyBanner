package com.xia.flybanner

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.IdRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xia.flybanner.adapter.FBPageAdapter
import com.xia.flybanner.constant.PageIndicatorAlign
import com.xia.flybanner.constant.PageIndicatorOrientation
import com.xia.flybanner.constant.PageOrientation
import com.xia.flybanner.constant.PageType
import com.xia.flybanner.helper.FBLoopHelper
import com.xia.flybanner.holder.FBViewHolderCreator
import com.xia.flybanner.listener.FBPageChangeListener
import com.xia.flybanner.listener.OnItemClickListener
import com.xia.flybanner.listener.OnPageChangeListener
import com.xia.flybanner.utils.SizeUtils
import com.xia.flybanner.view.FBLoopViewPager
import com.xia.flybanner.view.FBScaleLayoutManager
import com.xia.flybanner.view.RecyclerViewCornerRadius
import java.lang.ref.WeakReference
import java.util.*

/**
 * @author weixia
 * @date 2019/4/16.
 */
@Suppress("unused")
class FlyBanner<T> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : RelativeLayout(context, attrs) {

    //banner 翻页类型，默认为普通循环翻页
    private var mPageType: Int = PageType.TYPE_NORMAL
    //banner 翻页方向，默认为横向
    private var mPageOrientation: Int = PageOrientation.HORIZONTAL
    //banner 是否自动翻页
    private var mCanLoop: Boolean = true
    //banner 自动翻页间隔时间
    private var mAutoTurningTime: Long = 3000L
    //banner 圆角设置
    private var mPageRadius: Int = -1
    private var mPageTopLeftRadius: Int = 0
    private var mPageTopRightRadius: Int = 0
    private var mPageBottomLeftRadius: Int = 0
    private var mPageBottomRightRadius: Int = 0

    //是否显示指示器，默认显示
    private var mShowIndicator: Boolean = true
    //设置指示器方向，默认为横向
    private var mIndicatorOrientation: Int = PageIndicatorOrientation.HORIZONTAL
    //设置指示器的位置，（默认为右下角）
    private var mIndicatorAlign: Int = PageIndicatorAlign.ALIGN_RIGHT_BOTTOM
    //设置指示器偏移
    private var mIndicatorMargin: Int = -1
    private var mIndicatorLeftMargin: Int = 30
    private var mIndicatorTopMargin: Int = 30
    private var mIndicatorRightMargin: Int = 30
    private var mIndicatorBottomMargin: Int = 30
    //设置指示器间距
    private var mIndicatorSpacing: Int = 5

    //指示器视图集
    private val mPointViews = ArrayList<ImageView>()
    //数据集
    private var mDatas: List<T> = ArrayList()
    //视图构造
    private var mHolderCreator: FBViewHolderCreator? = null
    //数据总数
    private var mDataSize: Int = 0
    //正在翻页
    private var mTurning: Boolean = false
    //是否能够手动翻页
    private var mCanTurn: Boolean = false
    //是否为普通翻页类型
    private var mIsNormalMode: Boolean = false

    //banner 是否使用卡片式缩放视图
    private var mIsScaleCardView: Boolean = false
    /**
     * 次要方块的露出距离的权重
     * 此权重为相对RecyclerView而言，并且分别针对左右两侧。
     * 即：当 [mSecondaryExposedWeight] = 0.1F,那么主Item的宽度为RecyclerView.width * 0.8F
     */
    private var mSecondaryExposedWeight: Float = 0f
    /**
     * 次item缩放量
     * 表示当Item位于次Item位置时，显示尺寸相对于完整尺寸的量
     */
    private var mScaleGap: Float = 0f

    private val mLoopHelper = FBLoopHelper()
    private val mAdSwitchTask = AdSwitchTask(this)
    private val mPageChangeListener = FBPageChangeListener()
    private var mPageAdapter: FBPageAdapter<*>? = null
    private lateinit var mLoopViewPager: FBLoopViewPager
    private lateinit var mIndicatorView: LinearLayout

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlyBanner)
        mPageType = typedArray.getInt(R.styleable.FlyBanner_fb_pageType, PageType.TYPE_NORMAL)
        mPageOrientation = typedArray.getInt(R.styleable.FlyBanner_fb_pageOrientation, PageOrientation.HORIZONTAL)
        mCanLoop = typedArray.getBoolean(R.styleable.FlyBanner_fb_pageCanLoop, true)
        mAutoTurningTime = typedArray.getInteger(R.styleable.FlyBanner_fb_pageAutoTurningTime, 3000).toLong()
        mPageRadius = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_pageRadius, -1)
        mPageTopLeftRadius = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_pageRadiusTopLeft, 0)
        mPageTopRightRadius = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_pageRadiusTopRight, 0)
        mPageBottomLeftRadius = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_pageRadiusBottomLeft, 0)
        mPageBottomRightRadius = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_pageRadiusBottomRight, 0)

        mShowIndicator = typedArray.getBoolean(R.styleable.FlyBanner_fb_indicatorShow, true)
        mIndicatorOrientation = typedArray.getInt(R.styleable.FlyBanner_fb_indicatorOrientation, PageIndicatorOrientation.HORIZONTAL)
        mIndicatorAlign = typedArray.getInt(R.styleable.FlyBanner_fb_indicatorAlign, PageIndicatorAlign.ALIGN_RIGHT_BOTTOM)
        mIndicatorMargin = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_indicatorMargin, -1)
        mIndicatorLeftMargin = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_indicatorMarginLeft, SizeUtils.dp2px(15f))
        mIndicatorRightMargin = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_indicatorMarginRight, SizeUtils.dp2px(15f))
        mIndicatorTopMargin = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_indicatorMarginTop, SizeUtils.dp2px(15f))
        mIndicatorBottomMargin = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_indicatorMarginBottom, SizeUtils.dp2px(15f))
        mIndicatorSpacing = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_indicatorSpacing, SizeUtils.dp2px(3f))
        typedArray.recycle()

        init(context)
    }

    private fun init(context: Context) {
        mIsNormalMode = mPageType == PageType.TYPE_NORMAL
        if (mPageRadius != -1) {
            mPageBottomRightRadius = mPageRadius
            mPageBottomLeftRadius = mPageBottomRightRadius
            mPageTopRightRadius = mPageBottomLeftRadius
            mPageTopLeftRadius = mPageTopRightRadius
        }
        if (mIndicatorMargin != -1) {
            mIndicatorBottomMargin = mIndicatorMargin
            mIndicatorTopMargin = mIndicatorBottomMargin
            mIndicatorRightMargin = mIndicatorTopMargin
            mIndicatorLeftMargin = mIndicatorRightMargin
        }

        val view = LayoutInflater.from(context)
                .inflate(R.layout.fb_include_viewpager, this, true)
        mLoopViewPager = view.findViewById(R.id.fb_loop_vp)
        mIndicatorView = view.findViewById(R.id.fb_indicator_ll)
    }

    /**
     * 设置视图数据初始化，必须第一步调用
     */
    fun setPages(holderCreator: FBViewHolderCreator, datas: List<T>): PageBuilder {
        this.mDatas = datas
        this.mDataSize = datas.size
        this.mHolderCreator = holderCreator
        return PageBuilder(this)
    }

    inner class PageBuilder(private val mFlyBanner: FlyBanner<*>) {

        fun setPageType(@PageType.Type type: Int): PageBuilder {
            this.mFlyBanner.mPageType = type
            this.mFlyBanner.mIsNormalMode = type == PageType.TYPE_NORMAL
            return this
        }

        /**
         * 配置 banner 翻页方向，（默认为横向）
         *
         *
         * [PageOrientation.HORIZONTAL]: 横向
         * [PageOrientation.VERTICAL]: 竖向
         */
        fun setPageOrientation(@PageOrientation.Orientation orientation: Int): PageBuilder {
            this.mFlyBanner.mPageOrientation = orientation
            return this
        }

        /**
         * 设置 viewPager 圆角
         */
        fun setPageRadius(radius: Int): PageBuilder {
            setRadius(radius, radius, radius, radius)
            return this
        }

        /**
         * 设置 viewPager 圆角
         */
        fun setRadius(topLeftRadius: Int, topRightRadius: Int,
                      bottomLeftRadius: Int, bottomRightRadius: Int): PageBuilder {
            this.mFlyBanner.mPageTopLeftRadius = SizeUtils.dp2px(topLeftRadius.toFloat())
            this.mFlyBanner.mPageTopRightRadius = SizeUtils.dp2px(topRightRadius.toFloat())
            this.mFlyBanner.mPageBottomLeftRadius = SizeUtils.dp2px(bottomLeftRadius.toFloat())
            this.mFlyBanner.mPageBottomRightRadius = SizeUtils.dp2px(bottomRightRadius.toFloat())
            return this
        }

        /**
         * 设置缩放卡片式视图
         */
        fun setScaleCardView(isScaleCardView: Boolean,
                             secondaryExposedWeight: Float?,
                             scaleGap: Float?): PageBuilder {
            this.mFlyBanner.mIsScaleCardView = isScaleCardView
            if (secondaryExposedWeight != null) {
                this.mFlyBanner.mSecondaryExposedWeight = secondaryExposedWeight
            }
            if (scaleGap != null) {
                this.mFlyBanner.mScaleGap = scaleGap
            }
            return this
        }

        fun pageBuild(): IndicatorBuilder {
            setPageOrientation()
            setPageRadius()
            setPageAdapter()
            return IndicatorBuilder(mFlyBanner)
        }

        private fun setPageOrientation() {
            val orientation = if (mPageOrientation == PageOrientation.HORIZONTAL)
                RecyclerView.HORIZONTAL
            else
                RecyclerView.VERTICAL
            val layoutManager: RecyclerView.LayoutManager
            if (mIsScaleCardView) {
                layoutManager = FBScaleLayoutManager(context, orientation)
                layoutManager.mSecondaryExposedWeight = mSecondaryExposedWeight
                layoutManager.mScaleGap = mScaleGap
            } else {
                layoutManager = LinearLayoutManager(context, orientation, false)
            }
            mLoopViewPager.layoutManager = layoutManager
        }

        private fun setPageRadius() {
            val cornerRadius = RecyclerViewCornerRadius(mLoopViewPager)
            if (mPageTopLeftRadius >= 0) {
                cornerRadius.mTopLeftRadius = mPageTopLeftRadius
            }
            if (mPageTopRightRadius >= 0) {
                cornerRadius.mTopRightRadius = mPageTopRightRadius
            }
            if (mPageBottomLeftRadius >= 0) {
                cornerRadius.mBottomLeftRadius = mPageBottomLeftRadius
            }
            if (mPageBottomRightRadius >= 0) {
                cornerRadius.mBottomRightRadius = mPageBottomRightRadius
            }
            mLoopViewPager.addItemDecoration(cornerRadius)
        }

        private fun setPageAdapter() {
            mPageAdapter = FBPageAdapter(mHolderCreator!!, mDatas, mIsNormalMode)
            mLoopViewPager.adapter = mPageAdapter
            mLoopHelper.setFirstItemPos(if (mIsNormalMode) mDataSize else 0)
            mLoopHelper.attachToRecyclerView(mLoopViewPager, mPageAdapter!!)
        }
    }

    inner class IndicatorBuilder(private val mFlyBanner: FlyBanner<*>) {
        private var mIndicatorId = intArrayOf(R.drawable.indicator_gray_radius, R.drawable.indicator_white_radius)

        /**
         * 设置指示器样式
         */
        fun setIndicatorId(@IdRes indicatorId: IntArray): IndicatorBuilder {
            this.mIndicatorId = indicatorId
            return this
        }

        /**
         * 指示器的位置，（默认为右下角）
         *
         *
         * [PageIndicatorAlign.ALIGN_LEFT_TOP]: 左上角
         * [PageIndicatorAlign.ALIGN_LEFT_CENTER]: 左竖向中间
         * [PageIndicatorAlign.ALIGN_LEFT_BOTTOM]: 左下角
         * [PageIndicatorAlign.ALIGN_TOP_CENTER]: 上横向中间
         * [PageIndicatorAlign.ALIGN_IN_CENTER]: 中间
         * [PageIndicatorAlign.ALIGN_BOTTOM_CENTER]: 下横向中间
         * [PageIndicatorAlign.ALIGN_RIGHT_TOP]: 右上角
         * [PageIndicatorAlign.ALIGN_RIGHT_CENTER]: 右竖向中间
         * [PageIndicatorAlign.ALIGN_RIGHT_BOTTOM]: 右下角
         */
        fun setIndicatorAlign(@PageIndicatorAlign.IndicatorAlign align: Int): IndicatorBuilder {
            this.mFlyBanner.mIndicatorAlign = align
            return this
        }

        /**
         * 设置指示器方向，（默认为横向）
         *
         *
         * [PageIndicatorOrientation.HORIZONTAL]: 横向，
         * [PageIndicatorOrientation.VERTICAL]: 竖向
         */
        fun setIndicatorOrientation(
                @PageIndicatorOrientation.IndicatorOrientation orientation: Int): IndicatorBuilder {
            this.mFlyBanner.mIndicatorOrientation = orientation
            return this
        }

        /**
         * 设置指示器偏移
         */
        fun setIndicatorMargin(margin: Int): IndicatorBuilder {
            setIndicatorMargin(margin, margin, margin, margin)
            return this
        }

        /**
         * 设置指示器偏移
         */
        fun setIndicatorMargin(leftMargin: Int, topMargin: Int,
                               rightMargin: Int, bottomMargin: Int): IndicatorBuilder {
            this.mFlyBanner.mIndicatorLeftMargin = SizeUtils.dp2px(leftMargin.toFloat())
            this.mFlyBanner.mIndicatorTopMargin = SizeUtils.dp2px(topMargin.toFloat())
            this.mFlyBanner.mIndicatorRightMargin = SizeUtils.dp2px(rightMargin.toFloat())
            this.mFlyBanner.mIndicatorBottomMargin = SizeUtils.dp2px(bottomMargin.toFloat())
            return this
        }

        /**
         * 设置指示器间距
         */
        fun setIndicatorSpacing(indicatorSpacing: Int): IndicatorBuilder {
            this.mFlyBanner.mIndicatorSpacing = SizeUtils.dp2px(indicatorSpacing.toFloat())
            return this
        }

        /**
         * 设置指示器是否显示
         */
        fun setIndicatorVisible(indicatorVisible: Boolean): IndicatorBuilder {
            this.mFlyBanner.mShowIndicator = indicatorVisible
            return this
        }

        /**
         * 指示器配置
         */
        fun indicatorBuild(): FlyBuilder {
            val visible = if (mShowIndicator) View.VISIBLE else View.GONE
            mIndicatorView.visibility = visible

            setPageIndicator()
            return FlyBuilder(mFlyBanner)
        }

        private fun setPageIndicator() {
            mPointViews.clear()
            mIndicatorView.removeAllViews()
            if (mDatas.isEmpty() || mIndicatorId.size < 2
                    || mIndicatorView.visibility != View.VISIBLE) {
                return
            }
            setPageIndicatorOrientation()
            setPageIndicatorAlign()
            setPageIndicatorMargin()

            for (count in 0 until mDataSize) {
                // 翻页指示的点
                val pointView = ImageView(context)
                if (mIndicatorOrientation == PageIndicatorOrientation.HORIZONTAL) {
                    pointView.setPadding(mIndicatorSpacing, 0, mIndicatorSpacing, 0)
                } else {
                    pointView.setPadding(0, mIndicatorSpacing, 0, mIndicatorSpacing)
                }
                if (mLoopHelper.getFirstItemPos() % mDataSize == count) {
                    pointView.setImageResource(mIndicatorId[1])
                } else {
                    pointView.setImageResource(mIndicatorId[0])
                }
                mPointViews.add(pointView)
                mIndicatorView.addView(pointView)
            }

            mPageChangeListener.setPageIndicator(mPointViews, mIndicatorId)
            mLoopHelper.setOnPageChangeListener(mPageChangeListener)
        }

        private fun setPageIndicatorOrientation() {
            if (mIndicatorOrientation == PageIndicatorOrientation.HORIZONTAL) {
                mIndicatorView.orientation = LinearLayout.HORIZONTAL
            } else {
                mIndicatorView.orientation = LinearLayout.VERTICAL
            }
        }

        private fun setPageIndicatorAlign() {
            val layoutParams = mIndicatorView.layoutParams as LayoutParams
            val verbs = intArrayOf(
                    ALIGN_PARENT_TOP, CENTER_VERTICAL, ALIGN_PARENT_BOTTOM,
                    CENTER_HORIZONTAL, CENTER_IN_PARENT, ALIGN_PARENT_RIGHT)
            for (verb in verbs) {
                layoutParams.addRule(verb, 0)
            }

            when (mIndicatorAlign) {
                PageIndicatorAlign.ALIGN_LEFT_TOP -> layoutParams.addRule(ALIGN_PARENT_TOP, TRUE)
                PageIndicatorAlign.ALIGN_LEFT_CENTER -> layoutParams.addRule(CENTER_VERTICAL, TRUE)
                PageIndicatorAlign.ALIGN_LEFT_BOTTOM -> layoutParams.addRule(ALIGN_PARENT_BOTTOM, TRUE)
                PageIndicatorAlign.ALIGN_TOP_CENTER -> {
                    layoutParams.addRule(ALIGN_PARENT_TOP, TRUE)
                    layoutParams.addRule(CENTER_HORIZONTAL, TRUE)
                }
                PageIndicatorAlign.ALIGN_IN_CENTER -> layoutParams.addRule(CENTER_IN_PARENT, TRUE)
                PageIndicatorAlign.ALIGN_BOTTOM_CENTER -> {
                    layoutParams.addRule(ALIGN_PARENT_BOTTOM, TRUE)
                    layoutParams.addRule(CENTER_HORIZONTAL, TRUE)
                }
                PageIndicatorAlign.ALIGN_RIGHT_TOP -> {
                    layoutParams.addRule(ALIGN_PARENT_RIGHT, TRUE)
                    layoutParams.addRule(ALIGN_PARENT_TOP, TRUE)
                }
                PageIndicatorAlign.ALIGN_RIGHT_CENTER -> {
                    layoutParams.addRule(ALIGN_PARENT_RIGHT, TRUE)
                    layoutParams.addRule(CENTER_VERTICAL, TRUE)
                }
                PageIndicatorAlign.ALIGN_RIGHT_BOTTOM -> {
                    layoutParams.addRule(ALIGN_PARENT_RIGHT, TRUE)
                    layoutParams.addRule(ALIGN_PARENT_BOTTOM, TRUE)
                }
                else -> {
                }
            }
            mIndicatorView.layoutParams = layoutParams
        }

        private fun setPageIndicatorMargin() {
            val layoutParams = mIndicatorView.layoutParams as MarginLayoutParams
            if (mIndicatorLeftMargin >= 0) {
                layoutParams.leftMargin = mIndicatorLeftMargin
            }
            if (mIndicatorTopMargin >= 0) {
                layoutParams.topMargin = mIndicatorTopMargin
            }
            if (mIndicatorRightMargin >= 0) {
                layoutParams.rightMargin = mIndicatorRightMargin
            }
            if (mIndicatorBottomMargin >= 0) {
                layoutParams.bottomMargin = mIndicatorBottomMargin
            }
            mIndicatorView.layoutParams = layoutParams
        }
    }

    inner class FlyBuilder(private val mFlyBanner: FlyBanner<*>) {

        /**
         * 开始执行，最后一步调用
         */
        fun start(autoTurningTime: Long): FlyBanner<*> {
            startTurning(autoTurningTime)
            return mFlyBanner
        }
    }

    /**
     * 设置是否自动翻页
     */
    fun setCanLoop(canLoop: Boolean): FlyBanner<*> {
        this.mCanLoop = canLoop
        if (!canLoop) {
            stopTurning()
            return this
        }
        if (mTurning) {
            return this
        }
        startTurning()
        return this
    }

    /**
     * 设置翻页监听器
     */
    fun setOnPageChangeListener(onPageChangeListener: OnPageChangeListener?): FlyBanner<*> {
        mPageChangeListener.setOnPageChangeListener(onPageChangeListener)
        return this
    }

    /**
     * 设置 item 点击事件监听
     */
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?): FlyBanner<*> {
        mPageAdapter?.setOnItemClickListener(onItemClickListener)
        return this
    }

    /**
     * 设置当前页对应的 position
     */
    fun setCurrentItem(position: Int): FlyBanner<*> {
        stopTurning()
        val page = if (mIsNormalMode) mDataSize + position else position
        mLoopHelper.setCurrentItem(page, true)
        startTurning()
        return this
    }

    /**
     * 获取当前页对应的 position
     */
    fun getCurrentItem(): Int {
        return mLoopHelper.getRealCurrentItem()
    }

    /**
     * 是否开启了自动翻页
     */
    fun isCanLoop(): Boolean {
        return mCanLoop
    }

    /***
     * 开始翻页
     * @param autoTurningTime 自动翻页时间
     */
    @JvmOverloads
    fun startTurning(autoTurningTime: Long = mAutoTurningTime) {
        this.mAutoTurningTime = autoTurningTime

        stopTurning()
        if (autoTurningTime < 0 || !mCanLoop || mDataSize <= 1) {
            return
        }
        //设置可以翻页并开启翻页
        mCanTurn = true
        mTurning = true
        postDelayed(mAdSwitchTask, autoTurningTime)
    }

    /**
     * 停止翻页
     */
    fun stopTurning() {
        if (mTurning) {
            mTurning = false
            removeCallbacks(mAdSwitchTask)
        }
    }

    //触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!mCanTurn) {
            return super.dispatchTouchEvent(ev)
        }
        val action = ev.action
        if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL
                || action == MotionEvent.ACTION_OUTSIDE) {
            startTurning(mAutoTurningTime)
        } else if (action == MotionEvent.ACTION_DOWN) {
            stopTurning()
        }
        return super.dispatchTouchEvent(ev)
    }

    private class AdSwitchTask internal constructor(convenientBanner: FlyBanner<*>) : Runnable {

        private val mReference: WeakReference<FlyBanner<*>> = WeakReference(convenientBanner)
        //是否从左向右翻页
        private var mIsScrollRight = true

        override fun run() {
            val banner = mReference.get()
            if (banner == null || !banner.mTurning) {
                return
            }
            val currentItem = banner.mLoopHelper.getCurrentItem()
            var page = currentItem + 1
            if (!banner.mIsNormalMode && page == banner.mDataSize) {
                banner.stopTurning()
                return
            }
            if (banner.mIsNormalMode && banner.mIsScaleCardView) {
                if (page == 3 * banner.mDataSize && mIsScrollRight) {
                    mIsScrollRight = false
                }
                if (!mIsScrollRight) {
                    page = currentItem - 1
                    if (page == -1) {
                        page = currentItem + 1
                        mIsScrollRight = true
                    }
                }
            }
            banner.mLoopHelper.setCurrentItem(page, true)
            banner.postDelayed(banner.mAdSwitchTask, banner.mAutoTurningTime)
        }
    }
}
