package com.daqsoft.provider.uiTemplate.operation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.databinding.*
import com.daqsoft.provider.uiTemplate.operation.OperationTemplateFactory.Companion.STYLE_SUB_THREE_ONE
import com.daqsoft.provider.uiTemplate.operation.holder.*

/**
 * @Description 运营专区适配器
 * @ClassName   OperationTemplateAdapter
 * @Author      luoyi
 * @Time        2020/10/12 9:50
 */
class OperationTemplateAdapter(val helper: LayoutHelper) :
    DelegateAdapter.Adapter<RecyclerView.ViewHolder>() {

    private val factory: OperationTemplateFactory by lazy {
        OperationTemplateFactory()
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        commonTemlate?.let {
            when (holder) {
                is OperataionImgTwoViewHolder -> {
                    holder.bindDataToUI(it)
                }
                is OperataionImgThreeSubOneViewHolder -> {
                    holder.bindDataToUI(it)
                }
                is OperataionImgThreeSubTwoViewHolder -> {
                    holder.bindDataToUI(it)
                }
                is OperataionImgFourViewHolder -> {
                    holder.bindDataToUI(it)
                }
                is OperataionImgFivenViewHolder -> {
                    holder.bindDataToUI(it)
                }
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        commonTemlate?.let {
            return factory.getItemType(it)
        }

        return super.getItemViewType(position)

    }

}