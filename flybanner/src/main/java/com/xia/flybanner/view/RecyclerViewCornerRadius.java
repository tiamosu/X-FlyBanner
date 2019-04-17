package com.xia.flybanner.view;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author weixia
 * @date 2019/4/16.
 */
public class RecyclerViewCornerRadius extends RecyclerView.ItemDecoration {
    private RectF mRectF;
    private Path mPath;

    private int mTopLeftRadius = 0;
    private int mTopRightRadius = 0;
    private int mBottomLeftRadius = 0;
    private int mBottomRightRadius = 0;

    public RecyclerViewCornerRadius(final RecyclerView recyclerView) {
        final ViewTreeObserver vto = recyclerView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    final ViewTreeObserver currentVto = recyclerView.getViewTreeObserver();
                    if (currentVto.isAlive()) {
                        currentVto.removeOnGlobalLayoutListener(this);
                    }
                    mRectF = new RectF(0, 0, recyclerView.getMeasuredWidth(), recyclerView.getMeasuredHeight());
                    mPath = new Path();
                    mPath.reset();
                    mPath.addRoundRect(mRectF, new float[]{
                            mTopLeftRadius, mTopLeftRadius,
                            mTopRightRadius, mTopRightRadius,
                            mBottomLeftRadius, mBottomLeftRadius,
                            mBottomRightRadius, mBottomRightRadius
                    }, Path.Direction.CCW);
                }
            });
        }
    }

    public void setCornerRadius(int radius) {
        this.mTopLeftRadius = radius;
        this.mTopRightRadius = radius;
        this.mBottomLeftRadius = radius;
        this.mBottomRightRadius = radius;
    }

    public void setCornerRadius(int topLeftRadius, int topRightRadius, int bottomLeftRadius, int bottomRightRadius) {
        this.mTopLeftRadius = topLeftRadius;
        this.mTopRightRadius = topRightRadius;
        this.mBottomLeftRadius = bottomLeftRadius;
        this.mBottomRightRadius = bottomRightRadius;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        c.clipRect(mRectF);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            c.clipPath(mPath);
        } else {
            c.clipPath(mPath, Region.Op.REPLACE);
        }
    }
}
