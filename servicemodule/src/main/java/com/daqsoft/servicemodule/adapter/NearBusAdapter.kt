package com.daqsoft.servicemodule.adapter

import android.graphics.Color
import android.view.View
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ItemNearBusBinding
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.JavaUtils
import com.daqsoft.servicemodule.bean.NearBusBean

/**
 * desc :附近公交站适配器
 * @author 江云仙
 * @date 2020/4/3 14:20
 * @version 1.0.0
 * @since JDK 1.8
 */
class NearBusAdapter : RecyclerViewAdapter<ItemNearBusBinding, NearBusBean>(R.layout.item_near_bus) {
    override fun setVariable(mBinding: ItemNearBusBinding, position: Int, item: NearBusBean) {
        mBinding.tvBusName.text = item.name
        //设置部分字体颜色
        val distanceStr = "距您" + item.distance + "米"
        val distanceColor = JavaUtils.setTextColor(distanceStr, Color.parseColor("#36cd64"), 2, distanceStr.length - 1)
        mBinding.tvDistance.text = distanceColor

        if (!item.address.isNullOrEmpty()) {
            val address = item.address.split(";")
            mBinding.recyAddress.setLabels(address)
            mBinding.recyAddress.visibility = View.VISIBLE
        } else {
            mBinding.recyAddress.visibility = View.GONE
        }
    }

}