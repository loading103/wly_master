package com.daqsoft.servicemodule.adapter

import android.annotation.SuppressLint
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ItemTrainDetailBinding
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.servicemodule.bean.TrainDetailList

/**
 * desc :火车车次详情适配器
 * @author 江云仙
 * @date 2020/4/8 19:15
 */
class TrainDetailAdapter : RecyclerViewAdapter<ItemTrainDetailBinding, TrainDetailList>(R.layout.item_train_detail) {
    @SuppressLint("SetTextI18n", "CheckResult")
    override fun setVariable(mBinding: ItemTrainDetailBinding, position: Int, item: TrainDetailList) {
        if (position + 1 < 10) {
            mBinding.tvStationNum.text = "0" + (position + 1).toString()
        } else {
            mBinding.tvStationNum.text = (position + 1).toString()
        }
        mBinding.tvStation.text = item.station
        mBinding.tvArrivalTime.text = item.arrivaltime
        mBinding.tvDepartureTime.text = item.departuretime
        mBinding.tvStopTime.text = item.stoptime + "分钟"
    }

}