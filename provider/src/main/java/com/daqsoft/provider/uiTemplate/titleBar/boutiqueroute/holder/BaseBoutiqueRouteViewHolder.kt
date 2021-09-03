package com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.holder

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.LineTagBean
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean

abstract class BaseBoutiqueRouteViewHolder<T : ViewDataBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bindDataToUI(commonTemplate: CommonTemlate, dataLineType: MutableList<LineTagBean>, dataLine: MutableList<HomeContentBean>)
}