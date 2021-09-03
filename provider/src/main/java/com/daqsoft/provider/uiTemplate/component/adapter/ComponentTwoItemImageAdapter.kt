package com.daqsoft.provider.uiTemplate.component.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CommponentDetail
import com.daqsoft.provider.databinding.ItemTemplateMenuTwoImageBinding

/**
 * @Description
 * @ClassName   ComponentOneItemAdapter
 * @Author      luoyi
 * @Time        2020/10/16 16:16
 */
class ComponentTwoItemImageAdapter :
    RecyclerViewAdapter<ItemTemplateMenuTwoImageBinding, CommponentDetail>(R.layout.item_template_menu_two_image) {
    var onItemClickListener:OnItemClickListener?=null
    override fun setVariable(
        mBinding: ItemTemplateMenuTwoImageBinding,
        position: Int,
        item: CommponentDetail
    ) {
        item?.let {
            mBinding.item = item
            mBinding.root.onNoDoubleClick {
                onItemClickListener?.onItemClick(item)
            }
            }
    }

    interface OnItemClickListener{
        fun onItemClick(item:CommponentDetail)
    }
}