package com.daqsoft.provider.uiTemplate.titleBar.culturetourism.holder

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.HomeBranchBean

abstract class BaseCultureTourismViewHolder<T : ViewDataBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bindDataToUI(commonTemplate: CommonTemlate, dataLineType: MutableList<HomeBranchBean>)
}