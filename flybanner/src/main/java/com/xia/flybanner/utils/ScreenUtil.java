package com.xia.flybanner.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

/**
 * @author weixia
 * @date 2019/4/16.
 */
public final class ScreenUtil {

    public static int getScreenWidth(Context context) {
        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Point p = new Point();
        if (wm != null) {
            wm.getDefaultDisplay().getSize(p);
        }
        return p.x;
    }

    public static int getScreenHeight(Context context) {
        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Point p = new Point();
        if (wm != null) {
            wm.getDefaultDisplay().getSize(p);
        }
        return p.y;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
