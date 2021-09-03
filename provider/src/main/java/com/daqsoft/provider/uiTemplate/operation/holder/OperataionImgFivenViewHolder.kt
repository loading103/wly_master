package com.daqsoft.provider.uiTemplate.operation.holder

import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.HomeMenu
import com.daqsoft.provider.bean.OperationTemplate
import com.daqsoft.provider.databinding.ItemModuleTemplateBinding
import com.daqsoft.provider.databinding.ItemOperationStyleImgFiveNBinding
import com.daqsoft.provider.utils.MenuJumpUtils
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   OperataionImgFivenViewHolder
 * @Author      luoyi
 * @Time        2020/10/12 14:05
 */
class OperataionImgFivenViewHolder(binding: ItemOperationStyleImgFiveNBinding) :
    BaseOperationViewHolder<ItemOperationStyleImgFiveNBinding>(binding) {

    override fun bindDataToUI(template: CommonTemlate) {
        binding.rvOperationTemplates.run {
            layoutManager =
                LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
        adapter.clear()
        if (!template.operateDetailDtoList.isNullOrEmpty()) {
            adapter.add(template.operateDetailDtoList!!)
        }
    }

    /**
     * 模块适配器
     */
    private val adapter = object :
        RecyclerViewAdapter<ItemModuleTemplateBinding, OperationTemplate>(
            R.layout.item_module_template
        ) {
        override fun setVariable(
            mBinding: ItemModuleTemplateBinding,
            position: Int,
            item: OperationTemplate
        ) {
            mBinding.url = item.imgUrl
            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    MenuJumpUtils.adJump(item)
                }
        }
    }

}