package com.daqsoft.travelCultureModule.country.adapter

import android.annotation.SuppressLint
import android.text.SpannableStringBuilder
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemCountryHapinessBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.travelCultureModule.country.bean.ApiHoteltBean
import com.daqsoft.travelCultureModule.country.util.TextFontUtil
import com.daqsoft.travelCultureModule.country.util.TextFontUtil.setTextSize

/**
 * desc :名宿适配器
 * @author 江云仙
 * @date 2020/4/13 16:00
 */
class CountryHotelAdapter : RecyclerViewAdapter<ItemCountryHapinessBinding, ApiHoteltBean>(R.layout.item_country_hapiness) {
    @SuppressLint("SetTextI18n")
    override fun setVariable(mBinding: ItemCountryHapinessBinding, position: Int, item: ApiHoteltBean) {
        mBinding.tvName.text = item.name
//        mBinding.tvGoodFailPoint.text="诚信分"
        if (item.images.split(",").isNotEmpty()) {
            mBinding.url = item.images.split(",")[0]
        }
        if (!item.floorPrice.isNullOrEmpty()) {
            val floorPrice = item.floorPrice
            val spannableStringBuilder = SpannableStringBuilder().append("¥")
            val textBoldAndSize = TextFontUtil.setTextBoldAndSize(floorPrice, 15, 0, floorPrice.length)
            mBinding.tvCountryPrice.text = spannableStringBuilder.append(textBoldAndSize).append("起")
            mBinding.tvCountryPrice.visibility = View.VISIBLE
        } else {
            mBinding.tvCountryPrice.visibility = View.GONE
        }
        //点击跳转到酒店详情
        mBinding.root.onNoDoubleClick {
            ARouter.getInstance()
                .build(ZARouterPath.ZMAIN_HOTEL_DETAIL)
                .withString("id", item.id)
                .navigation()
        }
    }

}