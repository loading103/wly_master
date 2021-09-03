package com.daqsoft.travelCultureModule.food.adapter

import android.content.Context
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemFoodsLsBinding
import com.daqsoft.mainmodule.databinding.ItemScenicListBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.FoodBean
import com.daqsoft.provider.utils.AddressUtil
import com.daqsoft.travelCultureModule.resource.adapter.ScenicLsAdapter
import org.jetbrains.anko.backgroundResource
import java.lang.Exception
import java.lang.StringBuilder

/**
 * @Description
 * @ClassName   FoodLsAdapter
 * @Author      luoyi
 * @Time        2020/4/10 11:18
 */
class FoodLsAdapter : RecyclerViewAdapter<ItemFoodsLsBinding, FoodBean> {

    var context: Context? = null
    var selfLocation: LatLng? = null
    var onFoodLsItemClickListener: OnFoodLsItemClickListener? = null

    constructor(context: Context) : super(R.layout.item_foods_ls) {
        this.context = context
    }

    override fun setVariable(mBinding: ItemFoodsLsBinding, position: Int, item: FoodBean) {
        mBinding.txtFoodsName.text = "${item.name}"
        var strBuilder = StringBuilder("")
        if (!item.regionName.isNullOrEmpty()) {
            strBuilder.append(item.regionName)
        }
        if (selfLocation != null) {
            if (item.latitude != 0.0 && item.longitude != 0.0) {
                var distance = AddressUtil.getLocationDistanceCh(
                    selfLocation!!,
                    LatLng(item.latitude.toDouble(), item.longitude.toDouble())
                )
                if (distance != "") {
                    strBuilder.append(" | 距您$distance")
                }
            }
        }
        if (!item.consumPerson.isNullOrEmpty()) {
            strBuilder.append(" | " + String.format(context!!.getString(R.string.food_ls_price), item.consumPerson))
        }

        mBinding.txtFoodsInfo.text = strBuilder.toString()
        if (!item.openTime.isNullOrEmpty()) {
            mBinding.txtFoodsTime.visibility = View.VISIBLE
        } else {
            mBinding.txtFoodsTime.visibility = View.GONE
        }
        if (!item.floorPrice.isNullOrEmpty()) {
            mBinding.txtHotelRoomPrice.text = item.floorPrice
            mBinding.txtHotelRoomRmb.visibility = View.VISIBLE
            mBinding.txtHotelRoomQi.visibility = View.VISIBLE
            mBinding.txtHotelRoomPrice.visibility = View.VISIBLE
        } else {
            mBinding.txtHotelRoomRmb.visibility = View.GONE
            mBinding.txtHotelRoomQi.visibility = View.GONE
            mBinding.txtHotelRoomPrice.visibility = View.GONE
        }
        val images = item.images.split(",")
        if (!images.isNullOrEmpty()) {
            GlideModuleUtil.loadDqImageWaterMark(images[0],mBinding.imgFoodsLs)
        } else {
            Glide.with(context!!).load(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.imgFoodsLs)
        }
        if (item.vipResourceStatus != null) {
            if (item.vipResourceStatus.collectionStatus) {
                mBinding.imgFoodsCollect.backgroundResource = R.mipmap.activity_collect_selected
            } else {
                mBinding.imgFoodsCollect.backgroundResource = R.mipmap.activity_collect_normal
            }
            mBinding.imgFoodsCollect.visibility = View.VISIBLE
        } else {
            mBinding.imgFoodsCollect.visibility = View.GONE
        }
        if (!item.type.isNullOrEmpty()) {
            mBinding.recyFoodsLabels.setLabels(item.type)
            mBinding.recyFoodsLabels.visibility = View.VISIBLE
        } else {
            mBinding.recyFoodsLabels.visibility = View.GONE
        }
        // 活动
        if (!item.activity.isNullOrEmpty()) {
            mBinding.vFoodsAcitvity.visibility = View.VISIBLE
            mBinding.txtItemFoodsActivityWords.text = "${item.activity[0].name}"
        } else {
            mBinding.vFoodsAcitvity.visibility = View.GONE
        }
        if (!item.audio.isNullOrEmpty()) {
            mBinding.imgFoodsGoldStore.visibility = View.GONE
        } else {
            mBinding.imgFoodsGoldStore.visibility = View.GONE
        }
        if (!item.panoramaUrl.isNullOrEmpty()) {
            mBinding.imgFoods720.visibility = View.VISIBLE
        } else {
            mBinding.imgFoods720.visibility = View.GONE
        }
        if (!item.video.isNullOrEmpty()) {
            mBinding.imgFoodsVideo.visibility = View.VISIBLE
        } else {
            mBinding.imgFoodsVideo.visibility = View.GONE
        }
        if (!item.openTime.isNullOrEmpty()) {
            mBinding.txtFoodsTime.text = String.format(context!!.getString(R.string.food_ls_opentime, item.openTime))
            mBinding.txtFoodsTime.visibility = View.VISIBLE
        } else {
            mBinding.txtFoodsTime.visibility = View.GONE
        }

        mBinding.root.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_FOOD_DETAIL)
                .withString("id", item.id.toString())
                .navigation()
        }
        mBinding.imgFoodsCollect.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                if (onFoodLsItemClickListener != null && item.vipResourceStatus != null) {
                    onFoodLsItemClickListener!!.onCollectClick(item.id.toString(), position, item.vipResourceStatus.collectionStatus)
                }
            } else {
                ToastUtils.showMessage("该操作需要登录，请先登录")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
    }

    override fun payloadUpdateUi(mBinding: ItemFoodsLsBinding, position: Int, payloads: MutableList<Any>) {
        if (payloads[0] == "updateCollect") {
            if (getData()[position].vipResourceStatus != null) {
                val status = getData()[position].vipResourceStatus.collectionStatus
                if (status) {
                    mBinding.imgFoodsCollect.backgroundResource = R.mipmap.activity_collect_selected
                } else {
                    mBinding.imgFoodsCollect.backgroundResource = R.mipmap.activity_collect_normal
                }
            }
        }
    }


    /**
     * 更新收藏状态
     */
    fun notifyCollectStatus(position: Int, status: Boolean) {
        try {
            if (position < getData().size) {
                if (getData()[position].vipResourceStatus != null) {
                    getData()[position].vipResourceStatus.collectionStatus = status
                    notifyItemChanged(position, "updateCollect")
                }
            }
        } catch (e: Exception) {

        }
    }

    interface OnFoodLsItemClickListener {
        fun onCollectClick(id: String, postion: Int, status: Boolean)
    }
}