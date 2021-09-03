package com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.LineTagBean
import com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.adapter.LineTypeAdapter
import com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.holder.BoutiqueRouteViewHolderFour
import com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.holder.BoutiqueRouteViewHolderOne
import com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.holder.BoutiqueRouteViewHolderThree
import com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.holder.BoutiqueRouteViewHolderTwo
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean

/**
 * 精品线路
 */
class BoutiqueRouteAdapter(val helper: LayoutHelper) : DelegateAdapter.Adapter<RecyclerView.ViewHolder>() {


    private val factory: BoutiqueRouteFactory by lazy { BoutiqueRouteFactory() }

    var commonTemplate: CommonTemlate? = null

    // 精品路线列表
    var dataLineType: MutableList<LineTagBean> = mutableListOf()

    // 精品路线内容
    var dataLine: MutableList<HomeContentBean> = mutableListOf()

    override fun onCreateLayoutHelper(): LayoutHelper {
        return helper
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return factory.create(parent, viewType)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (commonTemplate != null && !dataLineType.isNullOrEmpty() && !dataLine.isNullOrEmpty()) {
            when (holder) {
                is BoutiqueRouteViewHolderOne -> {
                    holder.bindDataToUI(commonTemplate!!, dataLineType, dataLine)
                    holder.lineTypeAdapter.setChangeType(object : LineTypeAdapter.ChangeType {
                        override fun changeType(type: String) {
                            changeTypeInterface?.changeType(type)
                        }
                    })
                }
                is BoutiqueRouteViewHolderTwo -> {
                    holder.bindDataToUI(commonTemplate!!, dataLineType, dataLine)
                    holder.lineTypeAdapter.setChangeType(object : LineTypeAdapter.ChangeType {
                        override fun changeType(type: String) {
                            changeTypeInterface?.changeType(type)
                        }
                    })
                }
                is BoutiqueRouteViewHolderThree -> {
                    holder.bindDataToUI(commonTemplate!!, dataLineType, dataLine)
                    holder.lineTypeAdapter.setChangeType(object : LineTypeAdapter.ChangeType {
                        override fun changeType(type: String) {
                            changeTypeInterface?.changeType(type)
                        }
                    })
                }
                is BoutiqueRouteViewHolderFour->{
                    holder.bindDataToUI(commonTemplate!!, dataLineType, dataLine)
                    holder.lineTypeAdapter.setChangeType(object : LineTypeAdapter.ChangeType {
                        override fun changeType(type: String) {
                            changeTypeInterface?.changeType(type)
                        }
                    })
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

    private var changeTypeInterface: ChangeType? = null

    fun setChangeType(changeTypeInterface: ChangeType) {
        this.changeTypeInterface = changeTypeInterface
    }

    interface ChangeType {
        fun changeType(type: String)
    }
}