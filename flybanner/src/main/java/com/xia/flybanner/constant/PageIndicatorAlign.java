package com.xia.flybanner.constant;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * @author weixia
 * @date 2019/4/17.
 */
public final class PageIndicatorAlign {
    public static final int ALIGN_PARENT_LEFT = 9;
    public static final int ALIGN_PARENT_TOP = 10;
    public static final int ALIGN_PARENT_RIGHT = 11;
    public static final int ALIGN_PARENT_BOTTOM = 12;
    public static final int CENTER_IN_PARENT = 13;
    public static final int CENTER_HORIZONTAL = 14;
    public static final int CENTER_VERTICAL = 15;

    @IntDef({ALIGN_PARENT_LEFT, ALIGN_PARENT_TOP, ALIGN_PARENT_RIGHT,
            ALIGN_PARENT_BOTTOM, CENTER_IN_PARENT, CENTER_HORIZONTAL,
            CENTER_VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IndicatorAlign {
    }
}
