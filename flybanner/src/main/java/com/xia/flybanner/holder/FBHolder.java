package com.xia.flybanner.holder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author weixia
 * @date 2019/4/16.
 */
@SuppressWarnings("WeakerAccess")
public abstract class FBHolder<T> extends RecyclerView.ViewHolder {

    public FBHolder(View itemView) {
        super(itemView);
        initView(itemView);
    }

    protected abstract void initView(View itemView);

    public abstract void updateUI(T data);
}
