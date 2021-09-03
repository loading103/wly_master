package com.daqsoft.venuesmodule.adapter

import android.content.Context
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.MapResBean
import com.daqsoft.provider.utils.AddressUtil
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemVenueRecommendRecBinding
import kotlinx.android.synthetic.main.item_venue_recommend_rec.view.*

/**
 * @Description 场馆附近推荐
 * @ClassName   VenueRecRecAdapter
 * @Author      luoyi
 * @Time        2020/3/27 20:10
 */
class VenueRecRecAdapter : RecyclerViewAdapter<ItemVenueRecommendRecBinding, MapResBean> {

    var context: Context? = null

    constructor(context: Context) : super(R.layout.item_venue_recommend_rec) {
        this.context = context
    }

    var latLng: LatLng? = null
    override fun setVariable(
        mBinding: ItemVenueRecommendRecBinding,
        position: Int,
        item: MapResBean
    ) {
        mBinding.placeholder = context!!.getDrawable(R.drawable.placeholder_img_fail_240_180)
        if (!item.images.isNullOrEmpty()) {
            val images = item.images!!.split(",")
            if (images.isNotEmpty()) {
                mBinding.url = images[0]
            } else {
                mBinding.url = ""
            }
        } else {
            mBinding.url = ""
        }
        // 景区级别
        if (!item.resourceLevel.isNullOrEmpty()) {
            mBinding.txtItemVenueLevel.text = item.resourceLevel
            mBinding.txtItemVenueLevel.visibility = View.VISIBLE

            mBinding.level.visibility = View.VISIBLE
            var levels = item.resourceLevel!!.split(",")
            mBinding.level.text = if (levels.isNullOrEmpty()) {
                item.resourceLevel
            } else {
                levels[0]
            }
        } else {
            mBinding.txtItemVenueLevel.visibility = View.GONE
            mBinding.level.visibility = View.GONE
        }
        // 标题
        mBinding.txtItemVenuRecommendTitle.text = item.name
        // 地址信息
        if (latLng != null && !item.longitude.isNullOrEmpty() && !item.latitude.isNullOrEmpty()) {
            mBinding.txtItemVenuRecommendInfo.text = "距离该场所" + AddressUtil.getLocationDistanceCh(
                latLng!!, LatLng(
                    item.latitude!!.toDouble(),
                    item.longitude!!.toDouble()
                )
            )
        } else {
            mBinding.txtItemVenuRecommendInfo.text = "暂无位置信息"
        }

        mBinding?.root.onNoDoubleClick {
            if (!item.resourceType.isNullOrEmpty()) {
                when (item.resourceType) {
                    ResourceType.CONTENT_TYPE_SCENERY -> {
                        // 景区
                        ARouter.getInstance().build(MainARouterPath.MAIN_SCENIC_DETAIL)
                            .withString("id", item.id.toString())
                            .navigation()
                    }
                    ResourceType.CONTENT_TYPE_VENUE -> {
                        // 场馆
                        ARouter.getInstance()
                            .build(ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY)
                            .withString("id", item.id.toString())
                            .navigation()
                    }
                    ResourceType.CONTENT_TYPE_HOTEL -> {
                        // 酒店
                        ARouter.getInstance()
                            .build(ZARouterPath.ZMAIN_HOTEL_DETAIL)
                            .withString("id", item.id.toString())
                            .navigation()
                    }
                    ResourceType.CONTENT_TYPE_RESTAURANT -> {
                        // 美食
                        ARouter.getInstance().build(MainARouterPath.MAIN_FOOD_DETAIL)
                            .withString("id", item.id.toString())
                            .navigation()
                    }
                }
            }
        }
    }

    override fun payloadUpdateUi(
        mBinding: ItemVenueRecommendRecBinding,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads[0] == "updateLocationInfo") {
            val item = getData()[position]
            if (latLng != null && !item.longitude.isNullOrEmpty() && !item.latitude.isNullOrEmpty()) {
                mBinding.txtItemVenuRecommendInfo.text =
                    "距离该场所" + AddressUtil.getLocationDistanceCh(
                        latLng!!, LatLng(
                            item.latitude!!.toDouble(),
                            item.longitude!!.toDouble()
                        )
                    )
            }
        }
    }
}