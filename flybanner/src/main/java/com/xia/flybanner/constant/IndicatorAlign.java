package com.xia.flybanner.constant;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * @author weixia
 * @date 2019/4/17.
 */
public final class IndicatorAlign {
    public static final int ALIGN_LEFT_TOP = 10;
    public static final int ALIGN_LEFT_CENTER = 11;
    public static final int ALIGN_LEFT_BOTTOM = 12;
    public static final int ALIGN_TOP_CENTER = 13;
    public static final int ALIGN_IN_CENTER = 14;
    public static final int ALIGN_BOTTOM_CENTER = 15;
    public static final int ALIGN_RIGHT_TOP = 16;
    public static final int ALIGN_RIGHT_CENTER = 17;
    public static final int ALIGN_RIGHT_BOTTOM = 18;

    @IntDef({ALIGN_LEFT_TOP, ALIGN_LEFT_CENTER, ALIGN_LEFT_BOTTOM,
            ALIGN_TOP_CENTER, ALIGN_IN_CENTER, ALIGN_BOTTOM_CENTER,
            ALIGN_RIGHT_TOP, ALIGN_RIGHT_CENTER, ALIGN_RIGHT_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Align {
    }
}
