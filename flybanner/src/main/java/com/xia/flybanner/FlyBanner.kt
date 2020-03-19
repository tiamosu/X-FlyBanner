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

    //banner 翻页方向，默认为横向
    private var pageOrientation = PageOrientation.HORIZONTAL

    //banner 是否无限循环模式
    private var isLoopMode = true

    //banner 是否自动播放
    private var isAutoPlay = true

    //banner 自动翻页间隔时间
    private var autoTurningTime = 3000L

    //banner 圆角设置
    private var pageRadius = -1
    private var pageTopLeftRadius = 0
    private var pageTopRightRadius = 0
    private var pageBottomLeftRadius = 0
    private var pageBottomRightRadius = 0

    //是否显示指示器，默认显示
    private var showIndicator = true

    //设置指示器方向，默认为横向
    private var indicatorOrientation = PageIndicatorOrientation.HORIZONTAL

    //设置指示器的位置，（默认为右下角）
    private var indicatorAlign = PageIndicatorAlign.ALIGN_RIGHT_BOTTOM

    //设置指示器偏移
    private var indicatorMargin = -1
    private var indicatorLeftMargin = 30
    private var indicatorTopMargin = 30
    private var indicatorRightMargin = 30
    private var indicatorBottomMargin = 30

    //设置指示器间距
    private var indicatorSpacing = 5

    //指示器视图集
    private val pointViews = ArrayList<ImageView>()

    //数据集
    private var datas: List<T> = ArrayList()

    //视图构造
    private var holderCreator: FBViewHolderCreator? = null

    //数据总数
    private var dataSize = 0

    //正在翻页
    private var turning = false

    //是否能够手动翻页
    private var canTurn = false

    //banner 是否使用卡片式缩放视图
    private var isScaleCardView = false

    /**
     * 次要方块的露出距离的权重
     * 此权重为相对RecyclerView而言，并且分别针对左右两侧。
     * 即：当 [secondaryExposedWeight] = 0.1F,那么主Item的宽度为RecyclerView.width * 0.8F
     */
    private var secondaryExposedWeight = 0f

    /**
     * 次item缩放量
     * 表示当Item位于次Item位置时，显示尺寸相对于完整尺寸的量
     */
    private var scaleGap = 0f

    private val loopHelper by lazy { FBLoopHelper() }
    private val adSwitchTask by lazy { AdSwitchTask(this) }
    private val pageChangeListener by lazy { FBPageChangeListener() }
    private var pageAdapter: FBPageAdapter<*>? = null
    private lateinit var loopViewPager: FBLoopViewPager
    private lateinit var indicatorView: LinearLayout

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlyBanner)
        pageOrientation = typedArray.getInt(R.styleable.FlyBanner_fb_pageOrientation, PageOrientation.HORIZONTAL)
        isLoopMode = typedArray.getBoolean(R.styleable.FlyBanner_fb_pageLoopMode, true)
        isAutoPlay = typedArray.getBoolean(R.styleable.FlyBanner_fb_pageAutoPlay, true)
        autoTurningTime = typedArray.getInteger(R.styleable.FlyBanner_fb_pageAutoTurningTime, 3000).toLong()
        pageRadius = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_pageRadius, -1)
        pageTopLeftRadius = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_pageRadiusTopLeft, 0)
        pageTopRightRadius = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_pageRadiusTopRight, 0)
        pageBottomLeftRadius = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_pageRadiusBottomLeft, 0)
        pageBottomRightRadius = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_pageRadiusBottomRight, 0)

        showIndicator = typedArray.getBoolean(R.styleable.FlyBanner_fb_indicatorShow, true)
        indicatorOrientation = typedArray.getInt(R.styleable.FlyBanner_fb_indicatorOrientation, PageIndicatorOrientation.HORIZONTAL)
        indicatorAlign = typedArray.getInt(R.styleable.FlyBanner_fb_indicatorAlign, PageIndicatorAlign.ALIGN_RIGHT_BOTTOM)
        indicatorMargin = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_indicatorMargin, -1)
        indicatorLeftMargin = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_indicatorMarginLeft, SizeUtils.dp2px(15f))
        indicatorRightMargin = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_indicatorMarginRight, SizeUtils.dp2px(15f))
        indicatorTopMargin = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_indicatorMarginTop, SizeUtils.dp2px(15f))
        indicatorBottomMargin = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_indicatorMarginBottom, SizeUtils.dp2px(15f))
        indicatorSpacing = typedArray.getDimensionPixelOffset(R.styleable.FlyBanner_fb_indicatorSpacing, SizeUtils.dp2px(3f))
        typedArray.recycle()

        init(context)
    }

    private fun init(context: Context) {
        if (pageRadius != -1) {
            pageBottomRightRadius = pageRadius
            pageBottomLeftRadius = pageBottomRightRadius
            pageTopRightRadius = pageBottomLeftRadius
            pageTopLeftRadius = pageTopRightRadius
        }
        if (indicatorMargin != -1) {
            indicatorBottomMargin = indicatorMargin
            indicatorTopMargin = indicatorBottomMargin
            indicatorRightMargin = indicatorTopMargin
            indicatorLeftMargin = indicatorRightMargin
        }

        val view = LayoutInflater.from(context)
                .inflate(R.layout.fb_include_viewpager, this, true)
        loopViewPager = view.findViewById(R.id.fb_loop_vp)
        indicatorView = view.findViewById(R.id.fb_indicator_ll)
    }

    /**
     * 设置视图数据初始化，必须第一步调用
     */
    fun setPages(holderCreator: FBViewHolderCreator, datas: List<T>): PageBuilder {
        this.datas = datas
        this.dataSize = datas.size
        this.holderCreator = holderCreator
        return PageBuilder(this)
    }

    inner class PageBuilder(private val mFlyBanner: FlyBanner<*>) {

        fun setPageLoopMode(isLoopMode: Boolean): PageBuilder {
            this.mFlyBanner.isLoopMode = isLoopMode
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
            this.mFlyBanner.pageOrientation = orientation
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
            this.mFlyBanner.pageTopLeftRadius = SizeUtils.dp2px(topLeftRadius.toFloat())
            this.mFlyBanner.pageTopRightRadius = SizeUtils.dp2px(topRightRadius.toFloat())
            this.mFlyBanner.pageBottomLeftRadius = SizeUtils.dp2px(bottomLeftRadius.toFloat())
            this.mFlyBanner.pageBottomRightRadius = SizeUtils.dp2px(bottomRightRadius.toFloat())
            return this
        }

        /**
         * 设置缩放卡片式视图
         */
        fun setScaleCardView(isScaleCardView: Boolean,
                             secondaryExposedWeight: Float?,
                             scaleGap: Float?): PageBuilder {
            this.mFlyBanner.isScaleCardView = isScaleCardView
            if (secondaryExposedWeight != null) {
                this.mFlyBanner.secondaryExposedWeight = secondaryExposedWeight
            }
            if (scaleGap != null) {
                this.mFlyBanner.scaleGap = scaleGap
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
            val orientation = if (pageOrientation == PageOrientation.HORIZONTAL)
                RecyclerView.HORIZONTAL
            else
                RecyclerView.VERTICAL
            val layoutManager: RecyclerView.LayoutManager
            if (isScaleCardView) {
                layoutManager = FBScaleLayoutManager(context, orientation)
                layoutManager.secondaryExposedWeight = secondaryExposedWeight
                layoutManager.scaleGap = scaleGap
            } else {
                layoutManager = LinearLayoutManager(context, orientation, false)
            }
            loopViewPager.layoutManager = layoutManager
        }

        private fun setPageRadius() {
            val cornerRadius = RecyclerViewCornerRadius(loopViewPager)
            if (pageTopLeftRadius >= 0) {
                cornerRadius.topLeftRadius = pageTopLeftRadius
            }
            if (pageTopRightRadius >= 0) {
                cornerRadius.topRightRadius = pageTopRightRadius
            }
            if (pageBottomLeftRadius >= 0) {
                cornerRadius.bottomLeftRadius = pageBottomLeftRadius
            }
            if (pageBottomRightRadius >= 0) {
                cornerRadius.bottomRightRadius = pageBottomRightRadius
            }
            loopViewPager.addItemDecoration(cornerRadius)
        }

        private fun setPageAdapter() {
            pageAdapter = FBPageAdapter(holderCreator!!, datas, isLoopMode)
            loopViewPager.adapter = pageAdapter
            loopHelper.setFirstItemPos(if (isLoopMode) dataSize else 0)
            loopHelper.attachToRecyclerView(loopViewPager, pageAdapter!!)
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
            this.mFlyBanner.indicatorAlign = align
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
            this.mFlyBanner.indicatorOrientation = orientation
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
            this.mFlyBanner.indicatorLeftMargin = SizeUtils.dp2px(leftMargin.toFloat())
            this.mFlyBanner.indicatorTopMargin = SizeUtils.dp2px(topMargin.toFloat())
            this.mFlyBanner.indicatorRightMargin = SizeUtils.dp2px(rightMargin.toFloat())
            this.mFlyBanner.indicatorBottomMargin = SizeUtils.dp2px(bottomMargin.toFloat())
            return this
        }

        /**
         * 设置指示器间距
         */
        fun setIndicatorSpacing(indicatorSpacing: Int): IndicatorBuilder {
            this.mFlyBanner.indicatorSpacing = SizeUtils.dp2px(indicatorSpacing.toFloat())
            return this
        }

        /**
         * 设置指示器是否显示
         */
        fun setIndicatorVisible(indicatorVisible: Boolean): IndicatorBuilder {
            this.mFlyBanner.showIndicator = indicatorVisible
            return this
        }

        /**
         * 指示器配置
         */
        fun indicatorBuild(): FlyBuilder {
            val visible = if (showIndicator) View.VISIBLE else View.GONE
            indicatorView.visibility = visible

            setPageIndicator()
            return FlyBuilder(mFlyBanner)
        }

        private fun setPageIndicator() {
            pointViews.clear()
            indicatorView.removeAllViews()
            if (datas.isEmpty() || mIndicatorId.size < 2
                    || indicatorView.visibility != View.VISIBLE) {
                return
            }
            setPageIndicatorOrientation()
            setPageIndicatorAlign()
            setPageIndicatorMargin()

            for (count in 0 until dataSize) {
                // 翻页指示的点
                val pointView = ImageView(context)
                if (indicatorOrientation == PageIndicatorOrientation.HORIZONTAL) {
                    pointView.setPadding(indicatorSpacing, 0, indicatorSpacing, 0)
                } else {
                    pointView.setPadding(0, indicatorSpacing, 0, indicatorSpacing)
                }
                if (loopHelper.getFirstItemPos() % dataSize == count) {
                    pointView.setImageResource(mIndicatorId[1])
                } else {
                    pointView.setImageResource(mIndicatorId[0])
                }
                pointViews.add(pointView)
                indicatorView.addView(pointView)
            }

            pageChangeListener.setPageIndicator(pointViews, mIndicatorId)
            loopHelper.setOnPageChangeListener(pageChangeListener)
        }

        private fun setPageIndicatorOrientation() {
            if (indicatorOrientation == PageIndicatorOrientation.HORIZONTAL) {
                indicatorView.orientation = LinearLayout.HORIZONTAL
            } else {
                indicatorView.orientation = LinearLayout.VERTICAL
            }
        }

        private fun setPageIndicatorAlign() {
            val layoutParams = indicatorView.layoutParams as LayoutParams
            val verbs = intArrayOf(
                    ALIGN_PARENT_TOP, CENTER_VERTICAL, ALIGN_PARENT_BOTTOM,
                    CENTER_HORIZONTAL, CENTER_IN_PARENT, ALIGN_PARENT_RIGHT)
            for (verb in verbs) {
                layoutParams.addRule(verb, 0)
            }

            when (indicatorAlign) {
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
            indicatorView.layoutParams = layoutParams
        }

        private fun setPageIndicatorMargin() {
            val layoutParams = indicatorView.layoutParams as MarginLayoutParams
            if (indicatorLeftMargin >= 0) {
                layoutParams.leftMargin = indicatorLeftMargin
            }
            if (indicatorTopMargin >= 0) {
                layoutParams.topMargin = indicatorTopMargin
            }
            if (indicatorRightMargin >= 0) {
                layoutParams.rightMargin = indicatorRightMargin
            }
            if (indicatorBottomMargin >= 0) {
                layoutParams.bottomMargin = indicatorBottomMargin
            }
            indicatorView.layoutParams = layoutParams
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
    fun setAutoPlay(isAutoPlay: Boolean): FlyBanner<*> {
        this.isAutoPlay = isAutoPlay
        if (!isAutoPlay) {
            stopTurning()
            return this
        }
        if (turning) {
            return this
        }
        startTurning()
        return this
    }

    /**
     * 设置翻页监听器
     */
    fun setOnPageChangeListener(onPageChangeListener: OnPageChangeListener?): FlyBanner<*> {
        pageChangeListener.setOnPageChangeListener(onPageChangeListener)
        return this
    }

    /**
     * 设置 item 点击事件监听
     */
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?): FlyBanner<*> {
        pageAdapter?.setOnItemClickListener(onItemClickListener)
        return this
    }

    /**
     * 设置当前页对应的 position
     */
    fun setCurrentItem(position: Int): FlyBanner<*> {
        stopTurning()
        val page = if (isLoopMode) dataSize + position else position
        loopHelper.setCurrentItem(page, true)
        startTurning()
        return this
    }

    /**
     * 获取当前页对应的 position
     */
    fun getCurrentItem(): Int {
        return loopHelper.getRealCurrentItem()
    }

    /**
     * 是否开启了自动翻页
     */
    fun isAutoPlay(): Boolean {
        return isAutoPlay
    }

    /***
     * 开始翻页
     * @param autoTurningTime 自动翻页时间
     */
    @JvmOverloads
    fun startTurning(autoTurningTime: Long = this.autoTurningTime) {
        this.autoTurningTime = autoTurningTime

        stopTurning()
        if (autoTurningTime < 0 || !isAutoPlay || dataSize <= 1) {
            return
        }
        //设置可以翻页并开启翻页
        canTurn = true
        turning = true
        postDelayed(adSwitchTask, autoTurningTime)
    }

    /**
     * 停止翻页
     */
    fun stopTurning() {
        if (turning) {
            turning = false
            removeCallbacks(adSwitchTask)
        }
    }

    //触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!canTurn) {
            return super.dispatchTouchEvent(ev)
        }
        val action = ev.action
        if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL
                || action == MotionEvent.ACTION_OUTSIDE) {
            startTurning(autoTurningTime)
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
            if (banner == null || !banner.turning) {
                return
            }
            val currentItem = banner.loopHelper.getCurrentItem()
            var page = currentItem + 1
            if (!banner.isLoopMode && page == banner.dataSize) {
                banner.stopTurning()
                return
            }
            if (banner.isLoopMode && banner.isScaleCardView) {
                if (page == 3 * banner.dataSize && mIsScrollRight) {
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
            banner.loopHelper.setCurrentItem(page, true)
            banner.postDelayed(banner.adSwitchTask, banner.autoTurningTime)
        }
    }
}
