package com.daqsoft.venuesmodule.adapter

import android.content.Context
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.VenueOrderTime
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemVenueResTimeBinding
import com.daqsoft.venuesmodule.databinding.ItemVenueSelectTimeBinding
import org.jetbrains.anko.textColor

/**
 * @Description 时间段
 * @ClassName   VenueSelectTimeAdapter
 * @Author      luoyi
 * @Time        2020/7/8 11:08
 */
class VenueSelectTimeAdapter : RecyclerViewAdapter<ItemVenueResTimeBinding, VenueOrderTime> {
    var mContext: Context? = null
    var onSelectTimeListener: OnSelectTimeListener? = null

    constructor(context: Context) : super(R.layout.item_venue_res_time) {
        this.mContext = context
        emptyViewShow = false
    }

    var selectTimePos: Int = -1

    override fun setVariable(
        mBinding: ItemVenueResTimeBinding,
        position: Int,
        item: VenueOrderTime
    ) {
        item?.let {
            // 是否有库存
            if (item.stock == 0) {
                mBinding.tvVenueResTimes.text =
                    item.orderTimeSubStart + "-" + item.orderTimeSubEnd + "（已约满）"
                mBinding.tvVenueResTimes.textColor =
                    mContext!!.resources.getColor(R.color.color_999)
            } else if (!item.currTimeOrderStatus) {
                mBinding.tvVenueResTimes.text =
                    item.orderTimeSubStart + "-" + item.orderTimeSubEnd + "（不可预约）"
                mBinding.tvVenueResTimes.textColor =
                    mContext!!.resources.getColor(R.color.color_999)
            } else {
                // 是否预约过 和产品沟通，暂时不考虑是否预约过
//                if (it.existVipOrder) {
//                    mBinding.tvVenueResTimes.text = item.orderTimeSubStart + "-" + item.orderTimeSubEnd + "（已约）"
//                    mBinding.tvVenueResTimes.textColor = mContext!!.resources.getColor(R.color.color_999)
//                } else {
                mBinding.tvVenueResTimes.text =
                    item.orderTimeSubStart + "-" + item.orderTimeSubEnd + "（余${item.stock}）"
                mBinding.tvVenueResTimes.textColor =
                    mContext!!.resources.getColor(R.color.color_333)
//                }
            }

            if (selectTimePos == position) {
                mBinding.imgSelectTime.setImageResource(R.mipmap.venue_book_button_choose_selected)
            } else {
                mBinding.imgSelectTime.setImageResource(R.mipmap.venue_book_button_choose_normal)
            }
            mBinding.root.onNoDoubleClick {
                // 取消已约判断
                if (selectTimePos != position && item.stock != 0 && item.currTimeOrderStatus) {
                    selectTimePos = position
                    notifyDataSetChanged()
                    onSelectTimeListener?.onSelectTime(item)
                }
            }
        }
    }

    interface OnSelectTimeListener {
        fun onSelectTime(item: VenueOrderTime)
    }
}