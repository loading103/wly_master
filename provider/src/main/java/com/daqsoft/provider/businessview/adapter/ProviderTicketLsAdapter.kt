package com.daqsoft.provider.businessview.adapter

import android.content.Context
import android.view.View
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.ResourceDateBean
import com.daqsoft.provider.databinding.ItemProviderTicketBinding
import java.lang.Exception

/**
 * @Description
 * @ClassName   ProviderTicketLsAdapter
 * @Author      luoyi
 * @Time        2020/5/26 14:13
 */
class ProviderTicketLsAdapter : RecyclerViewAdapter<ItemProviderTicketBinding, ResourceDateBean> {

    private var mContext: Context? = null
    var onProviderTicketListener: OnProviderTicketListener? = null

    constructor(context: Context) : super(R.layout.item_provider_ticket) {
        mContext = context
    }

    override fun setVariable(mBinding: ItemProviderTicketBinding, position: Int, item: ResourceDateBean) {
        item?.let {
            try {
                mBinding.tvTicketDate.text = DateUtil.formatDateByString("MM-dd", "yyyy-MM-dd", it.date)
            } catch (e: Exception) {
            }
            mBinding.tvTicketWeek.text = DateUtil.getWeek(it.week)
            if (it.stock == 0) {
                mBinding.tvTicketStock.text = "约满"
                mBinding.tvTicketStock.setTextColor( mContext!!.resources.getColor(R.color.color_666))
                mBinding.tvTicketWeek.setTextColor( mContext!!.resources.getColor(R.color.color_666))
                mBinding.imgIscaneAppointment.visibility = View.GONE
                mBinding.vProviderTicket.background = mContext!!.resources.getDrawable(R.drawable.shape_appointment_r5_s1_sf5_d4d4d4)
            } else {
                mBinding.vProviderTicket.background = mContext!!.resources.getDrawable(R.drawable.shape_appointment_r5_s1_d4d4d4)
                mBinding.tvTicketStock.setTextColor( mContext!!.resources.getColor(R.color.color_333))
                mBinding.tvTicketWeek.setTextColor( mContext!!.resources.getColor(R.color.color_333))
                mBinding.imgIscaneAppointment.visibility = View.VISIBLE
                mBinding.tvTicketStock.text = "余${it.stock}"
            }
            mBinding.root.onNoDoubleClick {
                if (it.stock > 0) {
                    onProviderTicketListener?.toProviderTicket(item)
                }
            }
        }
    }

    interface OnProviderTicketListener {
        fun toProviderTicket(item: ResourceDateBean)
    }

}