package com.daqsoft.provider.uiTemplate.titleBar.culturetourism.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.HomeBranchBean
import com.daqsoft.provider.databinding.ItemCultrueTourismFourStyleElementBinding

/**
 * @Description
 * @ClassName   CultrueTourismFourAdapter
 * @Author      luoyi
 * @Time        2020/12/3 11:48
 */
class CultrueTourismFourAdapter :
    RecyclerViewAdapter<ItemCultrueTourismFourStyleElementBinding, HomeBranchBean>(R.layout.item_cultrue_tourism_four_style_element) {

    override fun setVariable(mBinding: ItemCultrueTourismFourStyleElementBinding, position: Int, item: HomeBranchBean) {
        mBinding.data = item
    }
}