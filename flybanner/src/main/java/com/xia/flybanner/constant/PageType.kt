package com.xia.flybanner.constant

import androidx.annotation.IntDef

/**
 * @author weixia
 * @date 2019/4/23.
 */
object PageType {
    const val TYPE_NORMAL = 0
    const val TYPE_GUIDE = 1

    @IntDef(TYPE_NORMAL, TYPE_GUIDE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type
}
