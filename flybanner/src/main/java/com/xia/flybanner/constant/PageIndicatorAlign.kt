package com.xia.flybanner.constant

import androidx.annotation.IntDef

/**
 * @author weixia
 * @date 2019/4/17.
 */
object PageIndicatorAlign {
    const val ALIGN_LEFT_TOP = 10
    const val ALIGN_LEFT_CENTER = 11
    const val ALIGN_LEFT_BOTTOM = 12
    const val ALIGN_TOP_CENTER = 13
    const val ALIGN_IN_CENTER = 14
    const val ALIGN_BOTTOM_CENTER = 15
    const val ALIGN_RIGHT_TOP = 16
    const val ALIGN_RIGHT_CENTER = 17
    const val ALIGN_RIGHT_BOTTOM = 18

    @IntDef(ALIGN_LEFT_TOP, ALIGN_LEFT_CENTER, ALIGN_LEFT_BOTTOM,
            ALIGN_TOP_CENTER, ALIGN_IN_CENTER, ALIGN_BOTTOM_CENTER,
            ALIGN_RIGHT_TOP, ALIGN_RIGHT_CENTER, ALIGN_RIGHT_BOTTOM)
    @Retention(AnnotationRetention.SOURCE)
    annotation class IndicatorAlign
}
