package com.daqsoft.slowLiveModule.rv.dsl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.DiffResult.NO_POSITION
import androidx.recyclerview.widget.RecyclerView


internal class RvDslAdapter : RecyclerView.Adapter<RvDslAdapter.RvDslViewHolder>() {
    internal val data: MutableList<RvItem> = mutableListOf()

    fun submitList(list: List<RvItem>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].xml()
    }

    override fun onCreateViewHolder(parent: ViewGroup, resId: Int): RvDslViewHolder {
        return RvDslViewHolder(inflate(parent, resId))
    }

    override fun onBindViewHolder(holder: RvDslViewHolder, position: Int) {
        data[position].render(position, holder.itemView, this)
    }

    override fun onBindViewHolder(holder: RvDslViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onViewAttachedToWindow(holder: RvDslViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.adapterPosition != NO_POSITION) {
            data[holder.adapterPosition].onItemAttachWindow(holder.adapterPosition, holder.itemView)
        }
    }

    override fun onViewDetachedFromWindow(holder: RvDslViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder.adapterPosition != NO_POSITION) {
            data[holder.adapterPosition].onItemDetachWindow(holder.adapterPosition, holder.itemView)
        }
    }

    override fun onViewRecycled(holder: RvDslViewHolder) {
        super.onViewRecycled(holder)
        if (holder.adapterPosition != NO_POSITION) {
            data[holder.adapterPosition].onItemRecycled(holder.adapterPosition, holder.itemView)
        }
    }

    private fun inflate(parent: ViewGroup, resId: Int): View {
        return LayoutInflater.from(parent.context).inflate(resId, parent, false)
    }

    internal class RvDslViewHolder(view: View) : RecyclerView.ViewHolder(view)
}