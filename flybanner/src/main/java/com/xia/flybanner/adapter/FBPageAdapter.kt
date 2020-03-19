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
class FBPageAdapter<T>(private val holderCreator: FBViewHolderCreator,
                       private val datas: List<T>,
                       private val isNormalMode: Boolean) : RecyclerView.Adapter<FBHolder<T>>() {

    private val dataSize: Int = datas.size
    private var itemClickListener: OnItemClickListener? = null

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FBHolder<T> {
        val layoutId = holderCreator.getLayoutId()
        val itemView = LayoutInflater.from(parent.context)
                .inflate(layoutId, parent, false)
        return holderCreator.createHolder(itemView) as FBHolder<T>
    }

    override fun onBindViewHolder(holder: FBHolder<T>, position: Int) {
        val realPosition = position % dataSize
        holder.updateUI(datas[realPosition])

        itemClickListener?.let {
            holder.itemView.setOnClickListener(OnPageClickListener(realPosition))
        }
    }

    override fun getItemCount(): Int {
        //根据模式决定长度
        return if (dataSize == 0 || dataSize == 1 || !isNormalMode) {
            dataSize
        } else 3 * dataSize
    }

    fun getRealItemCount(): Int {
        return dataSize
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.itemClickListener = onItemClickListener
    }

    private inner class OnPageClickListener internal constructor(private val mPosition: Int) : View.OnClickListener {

        override fun onClick(v: View) {
            itemClickListener?.onItemClick(mPosition)
        }
    }
}
