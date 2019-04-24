package com.xia.banner.option.text

import android.view.View

import com.xia.banner.R
import com.xia.flybanner.holder.FBHolder
import com.xia.flybanner.holder.FBViewHolderCreator

/**
 * @author weixia
 * @date 2019/4/16.
 */
class NoticeHolderCreator : FBViewHolderCreator {

    override fun createHolder(itemView: View): FBHolder<*> {
        return NoticeHolder(itemView)
    }

    override fun getLayoutId(): Int {
        return R.layout.item_notice_text
    }
}
