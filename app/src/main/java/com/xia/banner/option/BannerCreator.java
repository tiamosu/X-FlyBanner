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

    public static void setDefault(FlyBanner<Integer> flyBanner,
                                  ArrayList<Integer> datas,
                                  OnItemClickListener onItemClickListener,
                                  OnPageChangeListener onPageChangeListener) {
        final int bannerSize = datas.size();
        flyBanner
                //设置试图布局及数据
                .setPages(new HolderCreator(), datas)
                //设置页面指示器
                .useIndicator()
                //设置自动轮播时间
                .startTurning(5000)
                //设置是否进行自动轮播
                .setCanLoop(bannerSize > 1)
                //设置指示器显隐
                .setPointViewVisible(bannerSize > 1)
                //设置点击事件监听
                .setOnItemClickListener(onItemClickListener)
                //设置页面切换事件监听
                .setOnPageChangeListener(onPageChangeListener);
    }
}
