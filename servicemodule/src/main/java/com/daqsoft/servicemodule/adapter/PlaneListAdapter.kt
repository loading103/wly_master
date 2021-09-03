package com.daqsoft.servicemodule.adapter

import android.annotation.SuppressLint
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ItemPlaneListBinding
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.servicemodule.bean.PlaneLists
import com.daqsoft.servicemodule.uitils.TextFontUtil
import com.scwang.smart.refresh.layout.util.SmartUtil

/**
 * desc :飞机航班列表适配器
 * @author 江云仙
 * @date 2020/4/9 14:58
 */
class PlaneListAdapter(var startCityName: String, var endCityName: String) : RecyclerViewAdapter<ItemPlaneListBinding, PlaneLists>(R.layout.item_plane_list){
    @SuppressLint("SetTextI18n", "CheckResult")
    override fun setVariable(mBinding: ItemPlaneListBinding, position: Int, item: PlaneLists) {
        mBinding.tvPlaneStartTime.text=item.departtime
        mBinding.tvPlaneEndTime.text=item.arrivaltime
        mBinding.tvStartStation.text=startCityName
        mBinding.tvEndStation.text=endCityName
        val priceStr = "￥" + item.minprice
        mBinding.tvPrice.text= TextFontUtil.setTextSize(priceStr, SmartUtil.dp2px(22f), 1, priceStr.length)
        mBinding.tvAirLine.text=item.airline
        mBinding.root.onNoDoubleClick {

        }
    }
}