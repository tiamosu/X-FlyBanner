package com.xia.flybanner.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xia.flybanner.holder.FBHolder;
import com.xia.flybanner.holder.FBViewHolderCreator;
import com.xia.flybanner.listener.OnItemClickListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author weixia
 * @date 2019/4/16.
 */
@SuppressWarnings("unused")
public class FBPageAdapter<T> extends RecyclerView.Adapter<FBHolder> {
    private final FBViewHolderCreator mCreator;
    private final List<T> mDatas;
    private final int mDataSize;
    private final boolean mIsGuidePage;
    private OnItemClickListener mOnItemClickListener;

    public FBPageAdapter(final FBViewHolderCreator creator,
                         final List<T> datas, final boolean isGuidePage) {
        this.mCreator = creator;
        this.mDatas = datas;
        this.mDataSize = mDatas.size();
        this.mIsGuidePage = isGuidePage;
    }

    @NonNull
    @Override
    public FBHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final int layoutId = mCreator.getLayoutId();
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return mCreator.createHolder(itemView);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull FBHolder holder, int position) {
        final int realPosition = position % mDataSize;
        holder.updateUI(mDatas.get(realPosition));

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new OnPageClickListener(realPosition));
        }
    }

    @Override
    public int getItemCount() {
        //根据模式决定长度
        if (mDataSize == 0 || mDataSize == 1 || mIsGuidePage) {
            return mDataSize;
        }
        return 3 * mDataSize;
    }

    public int getRealItemCount() {
        return mDataSize;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    private class OnPageClickListener implements View.OnClickListener {
        private int mPosition;

        OnPageClickListener(int position) {
            this.mPosition = position;
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mPosition);
            }
        }
    }
}
