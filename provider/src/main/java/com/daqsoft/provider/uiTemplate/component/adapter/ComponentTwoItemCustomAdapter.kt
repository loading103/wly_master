package com.daqsoft.provider.uiTemplate.component.adapter

import android.graphics.Color
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CommponentDetail
import com.daqsoft.provider.databinding.ItemTemplateMenuTwoCustomBinding
import org.jetbrains.anko.backgroundColor

/**
 * @Description
 * @ClassName   ComponentOneItemAdapter
 * @Author      luoyi
 * @Time        2020/10/16 16:16
 */
class ComponentTwoItemCustomAdapter :
    RecyclerViewAdapter<ItemTemplateMenuTwoCustomBinding, CommponentDetail>(R.layout.item_template_menu_two_custom) {

    var onItemClickListener:OnItemClickListener?=null
    override fun setVariable(
        mBinding: ItemTemplateMenuTwoCustomBinding,
        position: Int,
        item: CommponentDetail
    ) {
        item?.let {
//            mBinding.rvServiceCustom.background
            mBinding.item = item
            if(!it.backgroundColor.isNullOrEmpty()){
                try {
                    mBinding.rvServiceCustom.backgroundColor = Color.parseColor(it.backgroundColor)
                }catch (e:Exception){}

            }
            mBinding.root.onNoDoubleClick {
                onItemClickListener?.onItemClick(it)
            }
        }
    }
    interface OnItemClickListener{
        fun onItemClick(item:CommponentDetail)
    }
}