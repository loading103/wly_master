package com.daqsoft.usermodule.ui.order.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.bean.ValideInfoBean
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ItemWriterOffUserBinding

/**
 * @Description
 * @ClassName   MineWriterOffAdapter
 * @Author      luoyi
 * @Time        2020/8/18 17:42
 */
class MineWriterOffAdapter :
    RecyclerViewAdapter<ItemWriterOffUserBinding, ValideInfoBean>(R.layout.item_writer_off_user) {
    override fun setVariable(
        mBinding: ItemWriterOffUserBinding,
        position: Int,
        item: ValideInfoBean
    ) {
        item?.let {
            mBinding.num = "" + item.validNum
            mBinding.time = "" + item.validTime
        }
    }
}