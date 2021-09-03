package com.daqsoft.provider.uiTemplate.operation.holder

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.provider.bean.CommonTemlate

/**
 * @Description
 * @ClassName   BaseOperationViewHolder
 * @Author      luoyi
 * @Time        2020/10/12 14:07
 */
abstract class BaseOperationViewHolder<T : ViewDataBinding>(
    val binding: T
) :
    RecyclerView.ViewHolder(binding.root) {


    abstract fun bindDataToUI(template: CommonTemlate)

}