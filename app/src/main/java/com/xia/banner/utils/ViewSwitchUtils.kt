package com.xia.banner.utils

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.widget.ImageView

/**
 * 图片背景切换动画帮助类
 *
 * @author weixia
 * @date 2019/4/19.
 */
object ViewSwitchUtils {

    @JvmStatic
    fun startSwitchBackgroundAnim(view: ImageView, bitmap: Bitmap) {
        val oldDrawable = view.drawable
        val oldBitmapDrawable: Drawable
        var oldTransitionDrawable: TransitionDrawable? = null
        when (oldDrawable) {
            is TransitionDrawable -> {
                oldTransitionDrawable = oldDrawable
                oldBitmapDrawable = oldTransitionDrawable.findDrawableByLayerId(oldTransitionDrawable.getId(1))
            }
            is BitmapDrawable -> oldBitmapDrawable = oldDrawable
            else -> oldBitmapDrawable = ColorDrawable(-0x3d3d3e)
        }

        val resources = view.resources
        val newBitmapDrawable = BitmapDrawable(resources, bitmap)
        if (oldTransitionDrawable == null) {
            val drawables = arrayOf(oldBitmapDrawable, newBitmapDrawable)
            oldTransitionDrawable = TransitionDrawable(drawables)
            oldTransitionDrawable.setId(0, 0)
            oldTransitionDrawable.setId(1, 1)
            oldTransitionDrawable.isCrossFadeEnabled = true
            view.setImageDrawable(oldTransitionDrawable)
        } else {
            oldTransitionDrawable.setDrawableByLayerId(oldTransitionDrawable.getId(0), oldBitmapDrawable)
            oldTransitionDrawable.setDrawableByLayerId(oldTransitionDrawable.getId(1), newBitmapDrawable)
        }
        oldTransitionDrawable.startTransition(1000)
    }
}
