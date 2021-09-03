package com.daqsoft.travelCultureModule.hotActivity.adapter

import android.view.View
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemActivityOverViewMonthBinding
import com.daqsoft.provider.bean.ValueKeyBean

/**
 * @Description 选择月份
 * @ClassName   ActivityOverViwMonthAdapter
 * @Author      luoyi
 * @Time        2020/6/10 13:43
 */
class ActivityOverViwMonthAdapter : RecyclerViewAdapter<ItemActivityOverViewMonthBinding, ValueKeyBean>(R.layout.item_activity_over_view_month) {

    var selectPos: Int = -1
    var onOverViewMonthClickListener: OnOverViewMonthClickListener? = null

    override fun setVariable(mBinding: ItemActivityOverViewMonthBinding, position: Int, item: ValueKeyBean) {
        item?.let {
            if (selectPos == position) {
                mBinding.vUnselectOverViewMonth.visibility = View.GONE
                mBinding.tvActivityOverViewMonthSelected.visibility = View.VISIBLE
            } else {
                mBinding.tvActivityOverViewMonthSelected.visibility = View.GONE
                mBinding.vUnselectOverViewMonth.visibility = View.VISIBLE
            }
            mBinding.tvActivityOverViewMonthSelected.text = "${item.name}月"
            mBinding.tvItemOverViewMonth.text = item.value
            mBinding.tvItemOverViewMonthValue.text = "${item.name}"
        }
        mBinding.root.onNoDoubleClick {
            if (selectPos != position) {
                selectPos = position
                notifyItemRangeChanged(0, getData().size, "updateSelectPos")
                onOverViewMonthClickListener?.onSelectPos(item.name,position)
            }
        }
    }

    override fun payloadUpdateUi(mBinding: ItemActivityOverViewMonthBinding, position: Int, payloads: MutableList<Any>) {
        if (payloads[0] == "updateSelectPos") {
            if (selectPos == position) {
                mBinding.vUnselectOverViewMonth.visibility = View.GONE
                mBinding.tvActivityOverViewMonthSelected.visibility = View.VISIBLE
            } else {
                mBinding.tvActivityOverViewMonthSelected.visibility = View.GONE
                mBinding.vUnselectOverViewMonth.visibility = View.VISIBLE
            }
        }
    }

    interface OnOverViewMonthClickListener {
        fun onSelectPos(month: String,position: Int)
    }
}