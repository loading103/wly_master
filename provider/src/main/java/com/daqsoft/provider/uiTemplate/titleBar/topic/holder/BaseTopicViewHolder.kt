package com.daqsoft.provider.uiTemplate.titleBar.topic.holder

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.HomeTopicBean

abstract class BaseTopicViewHolder<T : ViewDataBinding>(val binding: T) :
    RecyclerView.ViewHolder(binding.root) {
    abstract fun bindDataToUI(commonTemplate: CommonTemlate, dataLineType: MutableList<HomeTopicBean>)
}