package com.xia.flybanner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xia.flybanner.holder.FBHolder
import com.xia.flybanner.holder.FBViewHolderCreator
import com.xia.flybanner.listener.OnItemClickListener

/**
 * @author weixia
 * @date 2019/4/16.
 */
class FBPageAdapter<T>(private val mCreator: FBViewHolderCreator,
                       private val mDatas: List<T>,
                       private val mIsNormalMode: Boolean) : RecyclerView.Adapter<FBHolder<T>>() {

    private val mDataSize: Int = mDatas.size
    private var mOnItemClickListener: OnItemClickListener? = null

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FBHolder<T> {
        val layoutId = mCreator.getLayoutId()
        val itemView = LayoutInflater.from(parent.context)
                .inflate(layoutId, parent, false)
        return mCreator.createHolder(itemView) as FBHolder<T>
    }

    override fun onBindViewHolder(holder: FBHolder<T>, position: Int) {
        val realPosition = position % mDataSize
        holder.updateUI(mDatas[realPosition])

        mOnItemClickListener?.let {
            holder.itemView.setOnClickListener(OnPageClickListener(realPosition))
        }
    }

    override fun getItemCount(): Int {
        //根据模式决定长度
        return if (mDataSize == 0 || mDataSize == 1 || !mIsNormalMode) {
            mDataSize
        } else 3 * mDataSize
    }

    fun getRealItemCount(): Int {
        return mDataSize
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.mOnItemClickListener = onItemClickListener
    }

    private inner class OnPageClickListener internal constructor(private val mPosition: Int) : View.OnClickListener {

        override fun onClick(v: View) {
            mOnItemClickListener?.onItemClick(mPosition)
        }
    }
}
