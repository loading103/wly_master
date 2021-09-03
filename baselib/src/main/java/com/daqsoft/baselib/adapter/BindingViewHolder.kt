package com.daqsoft.baselib.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView的ViewHolder
 * @author 周俊蒙
 * @date 2019/5/26
 */
open class BindingViewHolder<T : ViewDataBinding>(binding: T) :
        RecyclerView.ViewHolder(binding.root) {
    val mBinding = binding
}