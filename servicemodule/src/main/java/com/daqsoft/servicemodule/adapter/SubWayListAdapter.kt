package com.daqsoft.servicemodule.adapter

import android.annotation.SuppressLint
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ItemSubwayListBinding
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.servicemodule.bean.BusWayListData
import com.daqsoft.servicemodule.uitils.TextFontUtil
import com.scwang.smart.refresh.layout.util.SmartUtil

/**
 * desc :汽车列表适配器
 * @author 江云仙
 * @date 2020/4/9 11:21
 */
class SubWayListAdapter : RecyclerViewAdapter<ItemSubwayListBinding, BusWayListData>(R.layout.item_subway_list){
    @SuppressLint("SetTextI18n", "CheckResult")
    override fun setVariable(mBinding: ItemSubwayListBinding, position: Int, item: BusWayListData) {
        mBinding.tvSubwayTime.text=item.starttime
        mBinding.tvStartStation.text=item.startstation
        mBinding.tvEndStation.text=item.endstation
        val priceStr = "￥" + item.price
                mBinding.tvPrice.text=TextFontUtil.setTextSize(priceStr, SmartUtil.dp2px(22f), 1, priceStr.length)
        if (!item.ticket.isNullOrEmpty()){
            mBinding.tvType.text=item.bustype+"("+item.ticket+")"
        }else{
            mBinding.tvType.text=item.bustype
        }

    }
}