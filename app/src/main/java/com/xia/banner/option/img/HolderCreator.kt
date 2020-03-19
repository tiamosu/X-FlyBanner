package com.xia.banner.option.img

import android.view.View

import com.xia.banner.R
import com.xia.flybanner.holder.FBHolder
import com.xia.flybanner.holder.FBViewHolderCreator

/**
 * @author weixia
 * @date 2019/4/16.
 */
class HolderCreator : FBViewHolderCreator {

    override fun createHolder(itemView: View): FBHolder<*> {
        return ImageHolder(itemView)
    }

    override fun getLayoutId(): Int {
        return R.layout.item_banner_img
    }
}
