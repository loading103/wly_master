package com.daqsoft.provider.uiTemplate.operation.holder

import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.databinding.ItemOperationStyleImgFiveNBinding
import com.daqsoft.provider.databinding.ItemOperationStyleImgTwoBinding

/**
 * @Description
 * @ClassName   OperataionImgTwoViewHolder
 * @Author      luoyi
 * @Time        2020/10/12 14:04
 */
class OperataionImgTwoViewHolder(
    binding: ItemOperationStyleImgTwoBinding
) :
    BaseOperationViewHolder<ItemOperationStyleImgTwoBinding>(binding) {


    override fun bindDataToUI(template: CommonTemlate) {
        binding.template = template
    }

}