package com.xia.banner.option.img

import android.annotation.SuppressLint
import com.xia.banner.R
import com.xia.flybanner.FlyBanner
import com.xia.flybanner.constant.PageIndicatorAlign
import com.xia.flybanner.constant.PageIndicatorOrientation
import com.xia.flybanner.constant.PageOrientation
import com.xia.flybanner.listener.OnItemClickListener
import com.xia.flybanner.listener.OnPageChangeListener

/**
 * @author weixia
 * @date 2019/4/16.
 */
@SuppressLint("WrongConstant")
object BannerCreator {

    @JvmStatic
    fun setDefault(flyBanner: FlyBanner<Any>,
                   datas: List<Any>,
                   isHorizontal: Boolean,
                   isLoopMode: Boolean,
                   isScaleCardView: Boolean,
                   onItemClickListener: OnItemClickListener?,
                   onPageChangeListener: OnPageChangeListener?) {

        val dataSize = datas.size
        val indicatorAlign = if (isHorizontal) PageIndicatorAlign.ALIGN_RIGHT_BOTTOM else PageIndicatorAlign.ALIGN_RIGHT_CENTER
        val indicatorOrientation = if (isHorizontal) PageIndicatorOrientation.HORIZONTAL else PageIndicatorOrientation.VERTICAL
        val orientation = if (isHorizontal) PageOrientation.HORIZONTAL else PageOrientation.VERTICAL

        flyBanner
                //设置 banner 视图数据初始化
                .setPages(HolderCreator(), datas)
                //设置 banner 是否无限循环播放
                .setPageLoopMode(isLoopMode)
                //设置 banner 翻页方向
                .setPageOrientation(orientation)
                //设置 viewPager 圆角
                .setPageRadius(20)
                //配置卡片式缩放视图
                .setScaleCardView(isScaleCardView, 0.1f, 0.85f)
                //banner 配置生成
                .pageBuild()
                //设置指示器样式
                .setIndicatorId(intArrayOf(R.drawable.indicator_gray_radius, R.drawable.indicator_white_radius))
                //设置指示器位置，默认为右下角
                .setIndicatorAlign(indicatorAlign)
                //设置指示器方向，默认为横向
                .setIndicatorOrientation(indicatorOrientation)
                //设置指示器偏移
                .setIndicatorMargin(15)
                //设置指示器间距
                .setIndicatorSpacing(3)
                //设置指示器是否显示
                .setIndicatorVisible(dataSize > 1)
                //指示器生成
                .indicatorBuild()
                //设置自动轮播时间
                .start(3000)
                //设置是否进行自动轮播
                .setAutoPlay(dataSize > 1 && isLoopMode)
                //设置点击事件监听
                .setOnItemClickListener(onItemClickListener)
                //设置页面切换事件监听
                .setOnPageChangeListener(onPageChangeListener)
    }
}
