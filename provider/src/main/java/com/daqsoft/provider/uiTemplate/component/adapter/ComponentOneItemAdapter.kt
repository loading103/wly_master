package com.daqsoft.provider.uiTemplate.component.adapter

import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CommponentDetail
import com.daqsoft.provider.databinding.ItemTemplateServiceMenuOneBinding

/**
 * @Description
 * @ClassName   ComponentOneItemAdapter
 * @Author      luoyi
 * @Time        2020/10/16 16:16
 */
class ComponentOneItemAdapter :
    RecyclerViewAdapter<ItemTemplateServiceMenuOneBinding, CommponentDetail>(R.layout.item_template_service_menu_one) {
    var onItemClickListener: OnItemClickListener?=null
    override fun setVariable(
        mBinding: ItemTemplateServiceMenuOneBinding,
        position: Int,
        item: CommponentDetail
    ) {
        item?.let {
            Glide.with(mBinding.root.context)
                .load(it.unSelectIcon)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.imgTravel)
            mBinding.tvTravel.text = "" + it.name
            mBinding.root.onNoDoubleClick {
                onItemClickListener?.onItemClick(it)
            }
        }
    }
    interface OnItemClickListener{
        fun onItemClick(item:CommponentDetail)
    }
}