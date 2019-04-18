package com.xia.banner.option.text;

import android.view.View;

import com.xia.banner.R;
import com.xia.flybanner.holder.FBHolder;
import com.xia.flybanner.holder.FBViewHolderCreator;

/**
 * @author weixia
 * @date 2019/4/16.
 */
public class NoticeHolderCreator implements FBViewHolderCreator {

    @Override
    public FBHolder createHolder(View itemView) {
        return new NoticeHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_notice_text;
    }
}
