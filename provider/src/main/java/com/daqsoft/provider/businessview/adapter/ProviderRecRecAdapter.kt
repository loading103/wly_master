package com.daqsoft.provider.businessview.adapter

import android.content.Context
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.MapResBean
import com.daqsoft.provider.databinding.ItemProviderRecommendRecBinding
import com.daqsoft.provider.utils.AddressUtil

/**
 * @Description 周边生活资源 适配器
 * @ClassName   ProviderRecRecAdapter
 * @Author      luoyi
 * @Time        2020/4/1 19:23
 */
class ProviderRecRecAdapter : RecyclerViewAdapter<ItemProviderRecommendRecBinding, MapResBean> {

    var context: Context? = null

    var onItemClick: OnItemClickListener? = null

    constructor(context: Context) : super(R.layout.item_provider_recommend_rec) {
        this.context = context
    }

    var latLng: LatLng? = null
    override fun setVariable(mBinding: ItemProviderRecommendRecBinding, position: Int, item: MapResBean) {
        mBinding.placeholder = context!!.getDrawable(R.drawable.placeholder_img_fail_240_180)
        var images = item.images?.split(",")
        if (!images.isNullOrEmpty()) {
            mBinding.url = images[0]
        }
        // 景区级别
        if (!item.resourceLevel.isNullOrEmpty()) {
            mBinding.txtItemProviderLevel.text = item.resourceLevel
            mBinding.txtItemProviderLevel.visibility = View.VISIBLE
        } else {
            mBinding.txtItemProviderLevel.visibility = View.GONE
        }
        // 标题
        mBinding.txtItemProviderRecommendTitle.text = item.name
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
                        ARouter.getInstance().build(ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY)
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
                    ResourceType.CONTENT_TYPE_ENTERTRAINMENT -> {
                        // 美食
                        ARouter.getInstance().build(MainARouterPath.MAIN_PLAYGROUND_DETAIL)
                            .withString("id", item.id.toString())
                            .navigation()
                    }
                    ResourceType.CONTENT_TYPE_SPECIALTY -> {
                        // 特产详情
                        ARouter.getInstance().build(MainARouterPath.MAIN_SPECIAL_DETAIL)
                            .withString("id",item.id.toString())
                            .navigation()
                    }
                }
            }
        }
    }

    override fun payloadUpdateUi(mBinding: ItemProviderRecommendRecBinding, position: Int, payloads: MutableList<Any>) {
        if (payloads[0] == "updateLocationInfo") {
            val item = getData()[position]
            if (latLng != null && !item.longitude.isNullOrEmpty() && !item.latitude.isNullOrEmpty()) {
                mBinding.txtItemVenuRecommendInfo.text = "距离该场所" + AddressUtil.getLocationDistanceCh(
                    latLng!!, LatLng(
                        item.latitude!!.toDouble(),
                        item.longitude!!.toDouble()
                    )
                )
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(id: Int, type: String)
    }
}