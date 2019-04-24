package com.xia.flybanner.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author weixia
 * @date 2019/4/16.
 */
abstract class FBHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    init {
        apply {
            initView(itemView)
        }
    }

    protected abstract fun initView(itemView: View)

    abstract fun updateUI(data: T)
}
