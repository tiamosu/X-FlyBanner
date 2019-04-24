package com.xia.flybanner.constant

import androidx.annotation.IntDef

/**
 * @author weixia
 * @date 2019/4/17.
 */
object PageIndicatorOrientation {
    const val HORIZONTAL = 0
    const val VERTICAL = 1

    @IntDef(HORIZONTAL, VERTICAL)
    @Retention(AnnotationRetention.SOURCE)
    annotation class IndicatorOrientation
}
