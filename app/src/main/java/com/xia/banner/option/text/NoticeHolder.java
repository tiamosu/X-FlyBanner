package com.xia.banner.option.text;

import android.view.View;

import com.xia.banner.R;
import com.xia.flybanner.holder.FBHolder;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author weixia
 * @date 2019/4/16.
 */
public class NoticeHolder extends FBHolder {
    private AppCompatTextView mAppCompatTextView;

    NoticeHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void initView(View itemView) {
        mAppCompatTextView = itemView.findViewById(R.id.item_notice_tv);
    }

    @Override
    public void updateUI(Object data) {
        if (mAppCompatTextView != null) {
            mAppCompatTextView.setText(String.valueOf(data));
        }
    }
}
