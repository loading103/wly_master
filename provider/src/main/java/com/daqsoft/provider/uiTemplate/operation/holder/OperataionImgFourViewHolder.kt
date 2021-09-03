package com.daqsoft.provider.uiTemplate.operation.holder

import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.databinding.ItemOperationStyleImgFourBinding
import com.daqsoft.provider.databinding.ItemOperationStyleImgThreeSubOneBinding

/**
 * @Description
 * @ClassName   OperataionImgFourViewHolder
 * @Author      luoyi
 * @Time        2020/10/12 14:05
 */
class OperataionImgFourViewHolder(
    binding: ItemOperationStyleImgFourBinding
) :
    BaseOperationViewHolder<ItemOperationStyleImgFourBinding>(binding) {

    override fun bindDataToUI(template: CommonTemlate) {
        binding.template = template
    }

}