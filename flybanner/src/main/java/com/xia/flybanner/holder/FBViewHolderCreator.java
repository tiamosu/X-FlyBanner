package com.xia.flybanner.holder;

import android.view.View;

/**
 * @author weixia
 * @date 2019/4/16.
 */
public interface FBViewHolderCreator {

    FBHolder createHolder(View itemView);

    int getLayoutId();
}
