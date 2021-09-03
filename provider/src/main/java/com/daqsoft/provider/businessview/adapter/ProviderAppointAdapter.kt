package com.daqsoft.provider.businessview.adapter

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.ResourceDateBean
import com.daqsoft.provider.bean.ResourceOrderBean
import com.daqsoft.provider.databinding.ItemProviderAppointmentBinding

/**
 * @Description
 * @ClassName   ProviderAppointAdapter
 * @Author      luoyi
 * @Time        2020/5/26 14:05
 */
class ProviderAppointAdapter : RecyclerViewAdapter<ItemProviderAppointmentBinding, ResourceOrderBean> {
    private var mContext: Context? = null
    var onProviderAppointAdapter: OnProviderAppointListener? = null

    constructor(context: Context) : super(R.layout.item_provider_appointment) {
        mContext = context
    }

    override fun setVariable(mBinding: ItemProviderAppointmentBinding, position: Int, item: ResourceOrderBean) {
        item?.let {
            mBinding.tvAppointAddress.text = "${it.resourceName}"

            // 门票预订列表
            var adapter: ProviderTicketLsAdapter = ProviderTicketLsAdapter(mContext!!)
            adapter?.onProviderTicketListener = object : ProviderTicketLsAdapter.OnProviderTicketListener {
                override fun toProviderTicket(data: ResourceDateBean) {
                    onProviderAppointAdapter?.toAppointment(data, item.resourceName)
                }

            }
            adapter.emptyViewShow = false
            mBinding.recyAppointmentLs.layoutManager = GridLayoutManager(mContext!!, 3)
            mBinding.recyAppointmentLs.adapter = adapter
            adapter.clear()
            if (!item.orderTimes.isNullOrEmpty()) {
                adapter.add(item.orderTimes)
            }

        }
    }

    interface OnProviderAppointListener {
        fun toAppointment(item: ResourceDateBean, name: String)
    }
}