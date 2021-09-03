package com.daqsoft.usermodule.ui.order.adapter

import android.view.View
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.bean.Record
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ItemMineCanceBinding

/**
 * @Description
 * @ClassName   MineCanceAppointAdapter
 * @Author      luoyi
 * @Time        2020/8/21 18:32
 */
class MineCanceAppointAdapter :
    RecyclerViewAdapter<ItemMineCanceBinding, Record>(R.layout.item_mine_cance) {

    override fun setVariable(mBinding: ItemMineCanceBinding, position: Int, item: Record) {
        item?.let {
            mBinding.detail = item
            if (it.money.isNullOrEmpty() && it.integral == 0) {
                mBinding.tvBackMoney.visibility = View.GONE
            } else {
                mBinding.tvBackMoney.visibility = View.VISIBLE
                if (!it.money.isNullOrEmpty()) {
                    mBinding.tvBackMoney.content = it.money
                }
                if (it.integral != 0) {
                    mBinding.tvBackMoney.content = "${it.integral}积分"
                }
            }
        }
        if (position == itemCount - 1) {
            mBinding.vLine.visibility = View.GONE
        } else {
            mBinding.vLine.visibility = View.VISIBLE
        }
    }
}