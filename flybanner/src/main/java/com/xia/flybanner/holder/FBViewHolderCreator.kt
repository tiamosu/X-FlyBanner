package com.xia.flybanner.holder

import android.view.View

/**
 * @author weixia
 * @date 2019/4/16.
 */
interface FBViewHolderCreator {

    fun getLayoutId(): Int

    fun createHolder(itemView: View): FBHolder<*>
}
