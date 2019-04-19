package com.xia.banner.option.img;

import android.annotation.SuppressLint;

import com.xia.banner.R;
import com.xia.flybanner.FlyBanner;
import com.xia.flybanner.constant.PageIndicatorAlign;
import com.xia.flybanner.constant.PageIndicatorOrientation;
import com.xia.flybanner.constant.PageOrientation;
import com.xia.flybanner.listener.OnItemClickListener;
import com.xia.flybanner.listener.OnPageChangeListener;

import java.util.List;

/**
 * @author weixia
 * @date 2019/4/16.
 */
@SuppressWarnings("unchecked")
@SuppressLint("WrongConstant")
public final class BannerCreator {

    public static void setDefault(final FlyBanner flyBanner,
                                  final List datas,
                                  final boolean isHorizontal,
                                  final OnItemClickListener onItemClickListener,
                                  final OnPageChangeListener onPageChangeListener) {

        final int dataSize = datas.size();
        final int indicatorAlign = isHorizontal ? PageIndicatorAlign.ALIGN_RIGHT_BOTTOM : PageIndicatorAlign.ALIGN_RIGHT_CENTER;
        final int indicatorOrientation = isHorizontal ? PageIndicatorOrientation.HORIZONTAL : PageIndicatorOrientation.VERTICAL;
        final int orientation = isHorizontal ? PageOrientation.HORIZONTAL : PageOrientation.VERTICAL;

        flyBanner
                //设置 banner 视图数据初始化
                .setPages(new HolderCreator(), datas)
                //设置 banner 翻页方向
                .setOrientation(orientation)
                //指示器配置使用
                .useIndicator(dataSize > 1)
                //设置指示器样式
                .setIndicatorId(new int[]{R.drawable.indicator_gray_radius, R.drawable.indicator_white_radius})
                //设置指示器位置，默认为右下角
                .setIndicatorAlign(indicatorAlign)
                //设置指示器方向，默认为横向
                .setIndicatorOrientation(indicatorOrientation)
                //设置指示器偏移
                .setIndicatorMargin(30)
                //指示器生成
                .indicatorBuild()
                //设置 viewPager 圆角
                .setRadius(50)
                //设置自动轮播时间
                .start(3000)
                //设置是否进行自动轮播
                .setCanLoop(dataSize > 1)
                //设置点击事件监听
                .setOnItemClickListener(onItemClickListener)
                //设置页面切换事件监听
                .setOnPageChangeListener(onPageChangeListener);
    }
}
