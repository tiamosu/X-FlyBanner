package com.xia.flybanner.constant;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * @author weixia
 * @date 2019/4/23.
 */
public final class PageType {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_GUIDE = 1;

    @IntDef({TYPE_NORMAL, TYPE_GUIDE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }
}
