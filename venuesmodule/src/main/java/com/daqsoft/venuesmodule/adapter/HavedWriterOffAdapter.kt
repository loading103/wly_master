package com.daqsoft.venuesmodule.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.bean.*
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemHavedWriterOffBinding

/**
 * @Description  已核销列表适配器
 * @ClassName   HavedWriterOffAdapter
 * @Author      luoyi
 * @Time        2020/8/5 14:30
 */
class HavedWriterOffAdapter :
    RecyclerViewAdapter<ItemHavedWriterOffBinding, OrderAttacthPersonBean>(R.layout.item_haved_writer_off) {
    override fun setVariable(
        mBinding: ItemHavedWriterOffBinding,
        position: Int,
        item:OrderAttacthPersonBean
    ) {
        mBinding.data = item
        mBinding.cardNum = "${CertTypes.getCertTypeName(item.userCardType)}(${item.userCardNumber})"
    }
}