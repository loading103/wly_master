package com.daqsoft.provider.uiTemplate.titleBar.culturetourism

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.HomeBranchBean
import com.daqsoft.provider.uiTemplate.titleBar.culturetourism.holder.CultureTourismViewHolderOne
import com.daqsoft.provider.uiTemplate.titleBar.culturetourism.holder.CultureTourismViewHolderThree
import com.daqsoft.provider.uiTemplate.titleBar.culturetourism.holder.CultureTourismViewHolderTwo

/**
 * 文旅品牌
 */
class CultureTourismAdapter(val helper: LayoutHelper) : DelegateAdapter.Adapter<RecyclerView.ViewHolder>() {

    private val factory: CultureTourismFactory by lazy { CultureTourismFactory() }

    var commonTemplate: CommonTemlate? = null

    // 文旅品牌
    var cultureTourism: MutableList<HomeBranchBean> = mutableListOf()

    override fun onCreateLayoutHelper(): LayoutHelper {
        return helper
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return factory.create(parent, viewType)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, p0osition: Int) {
        if (commonTemplate != null && !cultureTourism.isNullOrEmpty()) {
            when (holder) {
                is CultureTourismViewHolderOne -> {
                    holder.bindDataToUI(commonTemplate!!, cultureTourism)
                }
                is CultureTourismViewHolderTwo -> {
                    holder.bindDataToUI(commonTemplate!!, cultureTourism)
                }
                is CultureTourismViewHolderThree -> {
                    holder.bindDataToUI(commonTemplate!!, cultureTourism)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        commonTemplate?.let {
            return factory.getItemViewType(it.moduleCode ?: "1")
        }
        return super.getItemViewType(position)
    }
}