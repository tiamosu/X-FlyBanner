package com.xia.banner.option.text

import android.annotation.SuppressLint

import com.xia.flybanner.FlyBanner
import com.xia.flybanner.constant.PageOrientation
import com.xia.flybanner.listener.OnItemClickListener
import com.xia.flybanner.listener.OnPageChangeListener

/**
 * @author weixia
 * @date 2019/4/16.
 */
@SuppressLint("WrongConstant")
object NoticeCreator {

    @JvmStatic
    fun setDefault(flyBanner: FlyBanner<Any>,
                   datas: List<Any>,
                   isHorizontal: Boolean,
                   onItemClickListener: OnItemClickListener?,
                   onPageChangeListener: OnPageChangeListener?) {

        val orientation = if (isHorizontal) PageOrientation.HORIZONTAL else PageOrientation.VERTICAL

        flyBanner
                //设置视图数据初始化
                .setPages(NoticeHolderCreator(), datas)
                //设置 banner 翻页方向
                .setPageOrientation(orientation)
                //banner 配置生成
                .pageBuild()
                //设置指示器是否显示
                .setIndicatorVisible(false)
                //指示器生成
                .indicatorBuild()
                //设置自动轮播时间
                .start(3000)
                //设置是否进行自动轮播
                .setCanLoop(datas.size > 1)
                //设置点击事件监听
                .setOnItemClickListener(onItemClickListener)
                //设置页面切换事件监听
                .setOnPageChangeListener(onPageChangeListener)
    }
}
