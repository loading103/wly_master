package com.daqsoft.itinerary.adapter

import android.util.Log
import android.view.View
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.bean.TrafficItem
import com.daqsoft.itinerary.databinding.ItineraryItemTrafficBinding
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author：      邓益千
 * @Create by：   2020/5/23 10:23
 * @Description：
 */
class TrafficAdapter : RecyclerViewAdapter<ItineraryItemTrafficBinding, TrafficItem> {

    constructor():super(R.layout.itinerary_item_traffic)

    override fun setVariable(mBinding: ItineraryItemTrafficBinding, position: Int, item: TrafficItem) {
        mBinding.traffic = item
        mBinding.bookingView.visibility = View.GONE
        if (item.type == "bus"){
            mBinding.labelTraffic.text = "长途公交"
            mBinding.bookingView.visibility = View.GONE
        } else if (item.type == "selfDrive") {
            mBinding.labelTraffic.text = "自驾"
        } else if (item.type == "train") {
            mBinding.labelTraffic.text = "火车"
        } else if (item.type == "aviation") {
            mBinding.labelTraffic.text = "航空"
        } else if (item.type == "autoBus") {
            mBinding.labelTraffic.text = "公共交通"
            mBinding.bookingView.visibility = View.GONE
        }
        try {
            val ss = item.consumeTime.toLong()
            mBinding.consumiTime.text = "预计耗时${DateUtil.formatTime(ss * 1000)}"
        } catch (e: Exception) {
            mBinding.consumiTime.text = item.consumeTime
        }
    }
}