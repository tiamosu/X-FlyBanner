package com.xia.banner.option;

import android.content.Context;

import com.xia.banner.R;
import com.xia.flybanner.FlyBanner;
import com.xia.flybanner.constant.PageIndicatorAlign;
import com.xia.flybanner.listener.OnItemClickListener;
import com.xia.flybanner.listener.OnPageChangeListener;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;

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
        final Context context = flyBanner.getContext();
        final int[] indicatorId = new int[]{R.drawable.indicator_gray_radius, R.drawable.indicator_white_radius};
        flyBanner
                //设置视图数据初始化
                .setPages(new HolderCreator(), datas)
                //设置指示器显隐
                .setIndicatorVisible(bannerSize > 1)
                //设置指示器样式
                .setIndicatorId(indicatorId)
                //设置指示器位置
                .setIndicatorAlign(PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                //设置指示器偏移
                .setIndicatorMargin(null, null, 100, null)
                //指示器配置
                .useIndicator()
                //设置翻页效果
                .setLayoutManager(new LinearLayoutManager(context))
                //设置 viewPager 圆角
                .setRadius(50)
                //设置自动轮播时间
                .start(5000)
                //设置是否进行自动轮播
                .setCanLoop(bannerSize > 1)
                //初始页显示
//                .setCurrentItem(2, false)
                //设置点击事件监听
                .setOnItemClickListener(onItemClickListener)
                //设置页面切换事件监听
                .setOnPageChangeListener(onPageChangeListener);
    }
}
