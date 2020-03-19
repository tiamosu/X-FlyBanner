package com.xia.banner.option.img

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.xia.banner.R
import com.xia.flybanner.holder.FBHolder

/**
 * @author weixia
 * @date 2019/4/16.
 */
class ImageHolder internal constructor(itemView: View) : FBHolder<Any>(itemView) {
    private var imageView: AppCompatImageView? = null

    override fun initView(itemView: View) {
        imageView = itemView.findViewById(R.id.item_banner_iv)
    }

    override fun updateUI(data: Any) {
        imageView?.apply {
            Glide.with(context)
                    .load(data)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(this)
        }
    }
}
