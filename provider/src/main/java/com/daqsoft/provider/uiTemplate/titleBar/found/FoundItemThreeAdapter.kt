package com.daqsoft.provider.uiTemplate.titleBar.found

import android.text.SpannableStringBuilder
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.FoundAroundBean
import com.daqsoft.provider.databinding.ItemFoundThreeRecyBinding
import com.daqsoft.provider.utils.AddressUtil
import com.daqsoft.provider.utils.DividerTextUtils
import java.lang.StringBuilder

/**
 * @Description
 * @ClassName   FoundItemThreeAdapter
 * @Author      luoyi
 * @Time        2020/12/2 17:23
 */
class FoundItemThreeAdapter : RecyclerViewAdapter<ItemFoundThreeRecyBinding, FoundAroundBean>(R.layout.item_found_three_recy) {

    var currentPostion: LatLng? = null

    override fun setVariable(mBinding: ItemFoundThreeRecyBinding, position: Int, item: FoundAroundBean) {
        item?.let {
            mBinding.found = it
            val typeStr = ResourceType.getName(it.resourceType)
            var distance = ""
            if (currentPostion != null) {
                distance = "距您" + AddressUtil.getLocationDistanceEn(
                    currentPostion!!,
                    LatLng(it.latitude, it.longitude)
                )
            }
            val spb = SpannableStringBuilder()
            val sb = StringBuilder()
            val text = DividerTextUtils.convertDotString(sb, distance, typeStr)
            spb.append(text)
            mBinding.tvFoundDisatance.text = spb
            mBinding.root.onNoDoubleClick {

                var path = ""
                when (item.resourceType) {
                    // 场馆
                    ResourceType.CONTENT_TYPE_VENUE -> {
                        path = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY
                    }
                    // 农家乐
                    ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                        path = ARouterPath.CountryModule.COUNTRY_HAPPINESS_DETAIL
                    }
                    // 	活动室
                    ResourceType.CONTENT_TYPE_ACTIVITY_SHIU -> {
                        path = ARouterPath.VenuesModule.ACTIVITY_ROOM_DETAIL
                    }
                    // 酒店
                    ResourceType.CONTENT_TYPE_HOTEL -> {
                        path = ZARouterPath.ZMAIN_HOTEL_DETAIL
                    }
                    // 景区
                    ResourceType.CONTENT_TYPE_SCENERY -> {
                        path = MainARouterPath.MAIN_SCENIC_DETAIL
                    }
                    // 景点
                    ResourceType.CONTENT_TYPE_SCENIC_SPOTS -> {
                        path = MainARouterPath.MAIN_SCENIC_SPOT_DETAI
                    }
                    // 餐饮
                    ResourceType.CONTENT_TYPE_RESTAURANT -> {
                        path = MainARouterPath.MAIN_FOOD_DETAIL
                    }
                    // 活动
                    ResourceType.CONTENT_TYPE_ACTIVITY -> {
                        path = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY
                    }
                    // 特产
                    ResourceType.CONTENT_TYPE_SPECIALTY -> {
                        path = MainARouterPath.MAIN_SPECIAL_DETAIL
                    }
                    else -> {
                        ToastUtils.showMessage("功能开发中，敬请期待!")
                    }
                }
                if (!path.isNullOrEmpty())
                    ARouter.getInstance().build(path)
                        .withString("id", item.resourceId)
                        .navigation()
            }
        }
    }


}