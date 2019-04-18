package com.xia.flybanner.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xia.flybanner.helper.FBPageAdapterHelper;
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
    private final FBPageAdapterHelper mHelper = new FBPageAdapterHelper();
    private final FBViewHolderCreator mCreator;
    private final List<T> mDatas;
    private final int mDataSize;
    private boolean mCanLoop;
    private OnItemClickListener mOnItemClickListener;

    public FBPageAdapter(FBViewHolderCreator creator, List<T> datas, boolean canLoop) {
        this.mCreator = creator;
        this.mDatas = datas;
        this.mCanLoop = canLoop;
        this.mDataSize = mDatas.size();
    }

    @NonNull
    @Override
    public FBHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final int layoutId = mCreator.getLayoutId();
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        mHelper.onCreateViewHolder(parent, itemView);
        return mCreator.createHolder(itemView);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull FBHolder holder, int position) {
        mHelper.onBindViewHolder(holder.itemView, position, getItemCount());
        final int realPosition = position % mDataSize;
        holder.updateUI(mDatas.get(realPosition));

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new OnPageClickListener(realPosition));
        }
    }

    @Override
    public int getItemCount() {
        //根据模式决定长度
        if (mDataSize == 0) {
            return 0;
        }
        return 3 * mDataSize;
    }

    public void setCanLoop(boolean canLoop) {
        this.mCanLoop = canLoop;
    }

    public int getRealItemCount() {
        return mDataSize;
    }

    public boolean isCanLoop() {
        return mCanLoop;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    private class OnPageClickListener implements View.OnClickListener {
        private int position;

        OnPageClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(position);
            }
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
