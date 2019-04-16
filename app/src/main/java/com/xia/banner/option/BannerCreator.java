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
        flyBanner.setPages(new HolderCreator(), datas)
                .useIndicator()
                .setCanLoop(bannerSize > 1)
                .startTurning(5000)
                .setOnItemClickListener(onItemClickListener)
                .setOnPageChangeListener(onPageChangeListener);
    }
}
