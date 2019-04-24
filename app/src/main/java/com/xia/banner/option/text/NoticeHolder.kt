package com.xia.banner.option.text

import android.view.View

import com.xia.banner.R
import com.xia.flybanner.holder.FBHolder

import androidx.appcompat.widget.AppCompatTextView

/**
 * @author weixia
 * @date 2019/4/16.
 */
class NoticeHolder internal constructor(itemView: View) : FBHolder<Any>(itemView) {
    private var mAppCompatTextView: AppCompatTextView? = null

    override fun initView(itemView: View) {
        mAppCompatTextView = itemView.findViewById(R.id.item_notice_tv)
    }

    override fun updateUI(data: Any) {
        if (mAppCompatTextView != null) {
            mAppCompatTextView!!.text = data.toString()
        }
    }
}
