package com.xia.banner.option;

import android.view.View;

import com.xia.banner.R;
import com.xia.fly.imageloader.ImageConfigImpl;
import com.xia.fly.ui.imageloader.ImageLoader;
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
        ImageLoader.loadImage(
                ImageConfigImpl.load(data)
                        .crossFade()
                        .into(mAppCompatImageView)
                        .build()
        );
    }
}
