package com.daqsoft.provider.uiTemplate.titleBar.found

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.amap.api.maps.model.LatLng
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.FoundAroundBean
import com.daqsoft.provider.uiTemplate.titleBar.found.holder.FoundTemplateFourHolder
import com.daqsoft.provider.uiTemplate.titleBar.found.holder.FoundTemplateOneHolder
import com.daqsoft.provider.uiTemplate.titleBar.found.holder.FoundTemplateThreeHolder
import com.daqsoft.provider.uiTemplate.titleBar.found.holder.FoundTemplateTwoHolder
import com.daqsoft.provider.utils.StringUtils

/**
 * @Description
 * @ClassName   FoundTemplateAdapter
 * @Author      luoyi
 * @Time        2020/10/15 16:03
 */
class FoundTemplateAdapter(val helper: LayoutHelper) :
    DelegateAdapter.Adapter<RecyclerView.ViewHolder>() {

    val factory: FoundTemplateFactory by lazy {
        FoundTemplateFactory()
    }
    var currentPostion: LatLng? = null
    var commonTemlate: CommonTemlate? = null

    /**
     * 发现列表
     */
    var founds: MutableList<FoundAroundBean> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return factory.create(parent, viewType)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        commonTemlate?.let {
            if (!commonTemlate!!.moduleCode.isNullOrEmpty())
                return factory.getItemViewType(commonTemlate!!.moduleCode!!)
        }
        return super.getItemViewType(position)
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        return helper
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (!founds.isNullOrEmpty() && commonTemlate != null) {
            when (holder) {
                is FoundTemplateOneHolder -> {
                    if (currentPostion != null)
                        holder.bindDataToUI(commonTemlate!!)
                    holder.setFoundsToUI(founds, currentPostion!!)
                }
                is FoundTemplateTwoHolder -> {
                    if (currentPostion != null) {
                        holder.bindDataToUI(commonTemlate!!)
                        holder.setFoundsToUI(founds, currentPostion!!)
                    }
                }
                is FoundTemplateThreeHolder -> {
                    if (currentPostion != null) {
                        holder.bindDataToUI(commonTemlate!!)
                        holder.setFoundsToUI(founds, currentPostion!!)
                    }
                }
                is FoundTemplateFourHolder -> {
                    if (currentPostion != null) {
                        holder.bindDataToUI(commonTemlate!!)
                        holder.setFoundsToUI(founds, currentPostion!!)
                    }
                }
            }
        }
    }
}