package com.xia.banner.option.text;

import android.annotation.SuppressLint;

import com.xia.flybanner.FlyBanner;
import com.xia.flybanner.listener.OnItemClickListener;
import com.xia.flybanner.listener.OnPageChangeListener;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * @author weixia
 * @date 2019/4/16.
 */
@SuppressWarnings("unchecked")
@SuppressLint("WrongConstant")
public final class NoticeCreator {

    public static void setDefault(final FlyBanner flyBanner,
                                  final List datas,
                                  final boolean isHorizontal,
                                  final OnItemClickListener onItemClickListener,
                                  final OnPageChangeListener onPageChangeListener) {

        final LinearLayoutManager layoutManager = new LinearLayoutManager(
                flyBanner.getContext(), (isHorizontal ? LinearLayoutManager.HORIZONTAL
                : LinearLayoutManager.VERTICAL), false
        );
        flyBanner
                //设置视图数据初始化
                .setPages(new NoticeHolderCreator(), datas)
                //指示器配置使用
                .useIndicator(false)
                //设置翻页效果
                .setLayoutManager(layoutManager)
                //设置自动轮播时间
                .start(3000)
                //设置是否进行自动轮播
                .setCanLoop(datas.size() > 1)
                //设置点击事件监听
                .setOnItemClickListener(onItemClickListener)
                //设置页面切换事件监听
                .setOnPageChangeListener(onPageChangeListener);
    }
}
