package com.xia.flybanner.helper;

import android.view.View;
import android.view.ViewGroup;

import com.xia.flybanner.utils.ScreenUtil;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author weixia
 * @date 2019/4/16.
 */
public class FBPageAdapterHelper {
    private static int sPagePadding = 0;
    private static int sShowLeftCardWidth = 0;

    public void onCreateViewHolder(ViewGroup parent, View itemView) {
        final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        lp.width = parent.getWidth() - ScreenUtil.dip2px(itemView.getContext(), 2 * (sPagePadding + sShowLeftCardWidth));
        itemView.setLayoutParams(lp);
    }

    public void onBindViewHolder(View itemView, final int position, int itemCount) {
        final int padding = ScreenUtil.dip2px(itemView.getContext(), sPagePadding);
        itemView.setPadding(padding, 0, padding, 0);
        final int leftMarin = position == 0 ? padding + ScreenUtil.dip2px(itemView.getContext(), sShowLeftCardWidth) : 0;
        final int rightMarin = position == itemCount - 1 ? padding + ScreenUtil.dip2px(itemView.getContext(), sShowLeftCardWidth) : 0;
        setViewMargin(itemView, leftMarin, 0, rightMarin, 0);
    }

    private void setViewMargin(View view, int left, int top, int right, int bottom) {
        final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (lp.leftMargin != left || lp.topMargin != top || lp.rightMargin != right || lp.bottomMargin != bottom) {
            lp.setMargins(left, top, right, bottom);
            view.setLayoutParams(lp);
        }
    }

    public void setPagePadding(int pagePadding) {
        sPagePadding = pagePadding;
    }

    public void setShowLeftCardWidth(int showLeftCardWidth) {
        sShowLeftCardWidth = showLeftCardWidth;
    }
}
