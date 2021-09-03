package com.daqsoft.venuesmodule.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.bean.CertTypes
import com.daqsoft.provider.bean.OrderAttachPMapBean
import com.daqsoft.provider.bean.OrderAttacthPersonBean
import com.daqsoft.provider.bean.UserContact
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemWaitWriterOffUserBinding

/**
 * @Description
 * @ClassName   WaitWriterOffAdapter
 * @Author      luoyi
 * @Time        2020/8/5 14:29
 */
class WaitWriterOffAdapter :
    RecyclerViewAdapter<ItemWaitWriterOffUserBinding, OrderAttacthPersonBean>(R.layout.item_wait_writer_off_user) {

    override fun setVariable(
        mBinding: ItemWaitWriterOffUserBinding,
        position: Int,
        item: OrderAttacthPersonBean
    ) {
        mBinding.data = item
        mBinding.cardNum = "${CertTypes.getCertTypeName(item.userCardType)}(${item.userCardNumber})"
    }
}