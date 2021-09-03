package com.daqsoft.provider.uiTemplate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.daqsoft.baselib.adapter.BindingViewHolder

/**
 * @Description
 * @ClassName   BaseDelegateAdapter
 * @Author      luoyi
 * @Time        2020/10/10 13:55
 */
abstract class BaseDelegateAdapter<T : ViewDataBinding>(
    private val helper: LayoutHelper,
    private val resourceId: Int
) :
    DelegateAdapter.Adapter<BindingViewHolder<*>>() {
    var binding: T? = null
    override fun onCreateLayoutHelper(): LayoutHelper {
        return helper
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<*> {
        return BindingViewHolder<T>(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                resourceId,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BindingViewHolder<*>, position: Int) {
        binding = (holder as BindingViewHolder<T>).mBinding
        bindDataToView(binding!!, position)
        binding?.executePendingBindings()
    }


    abstract fun bindDataToView(mBinding: T, position: Int)

}