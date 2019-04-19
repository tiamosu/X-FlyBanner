package com.xia.flybanner.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.xia.flybanner.constant.FBConfig;
import com.xia.flybanner.utils.ScreenUtil;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author weixia
 * @date 2019/4/16.
 */
public class FBPageAdapterHelper {

    public void onCreateViewHolder(final ViewGroup parent, final View itemView) {
        final ViewTreeObserver vto = parent.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    final ViewTreeObserver currentVto = parent.getViewTreeObserver();
                    if (currentVto.isAlive()) {
                        currentVto.removeOnGlobalLayoutListener(this);
                    }
                    final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                    final Context context = itemView.getContext();
                    final int pagePadding = ScreenUtil.dip2px(context,
                            2 * (FBConfig.PAGE_PADDING + FBConfig.SHOW_LEFT_CARD_WIDTH));
                    layoutParams.width = parent.getMeasuredWidth() - pagePadding;
                    itemView.setLayoutParams(layoutParams);
                }
            });
        }
    }

    public void onBindViewHolder(final View itemView, final int position, final int itemCount) {
        final Context context = itemView.getContext();
        final int padding = ScreenUtil.dip2px(context, FBConfig.PAGE_PADDING);
        itemView.setPadding(padding, 0, padding, 0);

        final int margin = padding + ScreenUtil.dip2px(context, FBConfig.SHOW_LEFT_CARD_WIDTH);
        final int leftMarin = position == 0 ? margin : 0;
        final int rightMarin = position == itemCount - 1 ? margin : 0;
        setViewMargin(itemView, leftMarin, 0, rightMarin, 0);
    }

    @SuppressWarnings("SameParameterValue")
    private void setViewMargin(final View view, final int leftMargin, final int topMargin,
                               final int rightMargin, final int bottomMargin) {
        final ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (layoutParams.leftMargin != leftMargin) {
            layoutParams.leftMargin = leftMargin;
        }
        if (layoutParams.topMargin != topMargin) {
            layoutParams.topMargin = topMargin;
        }
        if (layoutParams.rightMargin != rightMargin) {
            layoutParams.rightMargin = rightMargin;
        }
        if (layoutParams.bottomMargin != bottomMargin) {
            layoutParams.bottomMargin = bottomMargin;
        }
        view.setLayoutParams(layoutParams);
    }
}
