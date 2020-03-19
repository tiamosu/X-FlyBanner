package com.xia.banner

import android.app.Application

import com.bumptech.glide.Glide

/**
 * @author weixia
 * @date 2019/4/16.
 */
@Suppress("unused")
class MyApp : Application() {

    override fun onLowMemory() {
        Glide.get(this).clearMemory()
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory()
        }
        Glide.get(this).trimMemory(level)
        super.onTrimMemory(level)
    }
}
