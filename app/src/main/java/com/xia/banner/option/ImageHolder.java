package com.xia.banner.option;

import android.content.Context;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.xia.banner.R;
import com.xia.flybanner.holder.FBHolder;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * @author weixia
 * @date 2019/4/16.
 */
public class ImageHolder extends FBHolder<Integer> {
    private AppCompatImageView mAppCompatImageView;

    ImageHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void initView(View itemView) {
        mAppCompatImageView = itemView.findViewById(R.id.item_banner_iv);
    }

    @Override
    public void updateUI(Integer data) {
        if (mAppCompatImageView != null) {
            final Context context = mAppCompatImageView.getContext();
            Glide.with(context)
                    .load(data)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(mAppCompatImageView);
        }
    }
}
