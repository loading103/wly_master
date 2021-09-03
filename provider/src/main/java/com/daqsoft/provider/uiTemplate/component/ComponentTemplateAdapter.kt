package com.daqsoft.provider.uiTemplate.component

import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.CommponentDetail
import com.daqsoft.provider.uiTemplate.component.holder.ComponentTemplateCustomHolder
import com.daqsoft.provider.uiTemplate.component.holder.ComponentTemplateImageHolder
import com.daqsoft.provider.uiTemplate.component.holder.ComponentTemplateMenuHolder
import java.lang.ref.SoftReference

/**
 * @Description 功能组件模板
 * @ClassName   ComponentTemplateAdapter
 * @Author      luoyi
 * @Time        2020/10/16 15:27
 */
class ComponentTemplateAdapter(val helper: LayoutHelper) :
    DelegateAdapter.Adapter<RecyclerView.ViewHolder>() {
    var fragmentManger: SoftReference<FragmentManager>? = null
    private val factory: ComponentTemplateFactory by lazy {
        ComponentTemplateFactory()
    }

    var commonTemlate: CommonTemlate? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return factory.create(parent, viewType)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        return helper
    }

    override fun getItemViewType(position: Int): Int {
        commonTemlate?.let {
            return factory.getItemType(it)
        }
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        commonTemlate?.let {
            when (holder) {
                is ComponentTemplateImageHolder -> {
                    if (!it.componentDetail.isNullOrEmpty()) {
                        holder.bindCommonent(it.componentDetail!!, it)
                    }
                    if(fragmentManger!=null){
                        holder.fragmentManger = fragmentManger
                    }

                }
                is ComponentTemplateCustomHolder -> {
                    if (!it.componentDetail.isNullOrEmpty()) {
                        holder.bindCommonent(it.componentDetail!!, it)
                    }
                    if(fragmentManger!=null){
                        holder.fragmentManger = fragmentManger
                    }
                }
                is ComponentTemplateMenuHolder -> {
                    if (!it.componentDetail.isNullOrEmpty()) {
                        holder.bindCommonent(it.componentDetail!!, it)
                    }
                    if(fragmentManger!=null){
                        holder.fragmentManger = fragmentManger
                    }
                }
            }
        }

    }
}