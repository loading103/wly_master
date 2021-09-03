package com.daqsoft.provider.uiTemplate.operation.holder

import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.databinding.ItemOperationStyleImgThreeSubOneBinding
import com.daqsoft.provider.databinding.ItemOperationStyleImgThreeSubTwoBinding

/**
 * @Description
 * @ClassName   OperataionImgThreeSubOneViewHolder
 * @Author      luoyi
 * @Time        2020/10/12 14:04
 */
class OperataionImgThreeSubOneViewHolder(
    binding: ItemOperationStyleImgThreeSubOneBinding
) :
    BaseOperationViewHolder<ItemOperationStyleImgThreeSubOneBinding>(binding) {

    override fun bindDataToUI(
        template: CommonTemlate
    ) {
        binding.template = template
    }

}