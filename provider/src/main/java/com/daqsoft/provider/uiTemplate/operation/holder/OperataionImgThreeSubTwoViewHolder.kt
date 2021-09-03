package com.daqsoft.provider.uiTemplate.operation.holder

import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.databinding.ItemOperationStyleImgThreeSubTwoBinding

/**
 * @Description
 * @ClassName   OperataionImgThreeSubTwoViewHolder
 * @Author      luoyi
 * @Time        2020/10/12 14:04
 */
class OperataionImgThreeSubTwoViewHolder(
    binding: ItemOperationStyleImgThreeSubTwoBinding
) :
    BaseOperationViewHolder<ItemOperationStyleImgThreeSubTwoBinding>(binding) {

    override fun bindDataToUI(
        commonTemlate: CommonTemlate
    ) {
        binding.template = commonTemlate
    }

}