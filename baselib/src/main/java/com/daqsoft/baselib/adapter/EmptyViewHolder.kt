package com.daqsoft.baselib.adapter

import androidx.databinding.ViewDataBinding

/**
 * 空布局ViewHolder
 *
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-6-10
 * @since JDK 1.8.0_191
 */
class EmptyViewHolder(binding: ViewDataBinding) : BindingViewHolder<ViewDataBinding>(binding) {
    val emptyViewHolderBinding = binding
}