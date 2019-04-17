package com.xia.banner;

import android.app.Application;

import com.bumptech.glide.Glide;

/**
 * @author weixia
 * @date 2019/4/16.
 */
public class MyApp extends Application {

    @Override
    public void onLowMemory() {
        Glide.get(this).clearMemory();
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);
        super.onTrimMemory(level);
    }
}
