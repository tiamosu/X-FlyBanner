package com.xia.banner.option.img;

import android.view.View;

import com.xia.banner.R;
import com.xia.flybanner.holder.FBHolder;
import com.xia.flybanner.holder.FBViewHolderCreator;

/**
 * @author weixia
 * @date 2019/4/16.
 */
public class HolderCreator implements FBViewHolderCreator {

    @Override
    public FBHolder createHolder(View itemView) {
        return new ImageHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_banner_img;
//        return R.layout.item_banner_img_scale;
    }
}
