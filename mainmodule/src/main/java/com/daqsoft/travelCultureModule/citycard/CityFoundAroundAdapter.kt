package com.daqsoft.travelCultureModule.citycard

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemCityFoundAroundBinding
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.FoundAroundBean
import com.daqsoft.provider.utils.AddressUtil
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.MenuJumpUtils
import java.lang.StringBuilder

/**
 * @Description
 * @ClassName   CityFoundAroundAdapter
 * @Author      luoyi
 * @Time        2020/10/26 15:50
 */
class CityFoundAroundAdapter :
    RecyclerViewAdapter<ItemCityFoundAroundBinding, FoundAroundBean>(R.layout.item_city_found_around) {

    var currentPostion: LatLng? = null
    override fun setVariable(
        mBinding: ItemCityFoundAroundBinding,
        position: Int,
        item: FoundAroundBean
    ) {
        mBinding.data = item
        item?.let {
            val spb = SpannableStringBuilder()
            val sb = StringBuilder()
            val typeStr = ResourceType.getName(it.resourceType)
            val distance =
                if (currentPostion != null) {
                    AddressUtil.getLocationDistanceCh(
                        currentPostion!!,
                        LatLng(it.latitude, it.longitude)
                    )
                } else {
                    ""
                }

            val text = DividerTextUtils.convertDotString(sb, typeStr, distance)
            spb.append(text)
            mBinding?.tvMddNameContent?.text = spb
            mBinding?.root.onNoDoubleClick {
                MenuJumpUtils.gotoResourceDetail(item.resourceType?:"",item.resourceId?:"")
            }
        }
    }

}