package com.xia.banner.option;

import com.xia.flybanner.FlyBanner;
import com.xia.flybanner.listener.OnItemClickListener;
import com.xia.flybanner.listener.OnPageChangeListener;

import java.util.ArrayList;

/**
 * @author weixia
 * @date 2019/4/16.
 */
public final class BannerCreator {

    public static void setDefault(final FlyBanner<Integer> flyBanner,
                                  final ArrayList<Integer> datas,
                                  final OnItemClickListener onItemClickListener,
                                  final OnPageChangeListener onPageChangeListener) {

        flyBanner
                //设置视图数据初始化
                .setPages(new HolderCreator(), datas)
                //设置指示器可见性，默认为可见状态
//                .setIndicatorVisible(datas.size() > 1)
                //设置指示器样式
//                .setIndicatorId(new int[]{R.drawable.indicator_gray_radius, R.drawable.indicator_white_radius})
                //设置指示器位置，默认为右下角
//                .setIndicatorAlign(PageIndicatorAlign.ALIGN_RIGHT_BOTTOM)
                //设置指示器方向，默认为横向
//                .setIndicatorOrientation(PageIndicatorOrientation.HORIZONTAL)
                //设置指示器偏移
//                .setIndicatorMargin(30)
                //指示器配置
                .useIndicator()
                //设置翻页效果
//                .setLayoutManager(new LinearLayoutManager(flyBanner.getContext()))
                //设置 viewPager 圆角
                .setRadius(50)
                //设置自动轮播时间
                .start(5000)
                //设置是否进行自动轮播
//                .setCanLoop(datas.size() > 1)
                //初始页显示
//                .setCurrentItem(2, false)
                //设置点击事件监听
                .setOnItemClickListener(onItemClickListener)
                //设置页面切换事件监听
                .setOnPageChangeListener(onPageChangeListener);
    }
}
