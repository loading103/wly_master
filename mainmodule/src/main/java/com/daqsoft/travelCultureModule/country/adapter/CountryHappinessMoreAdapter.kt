package com.daqsoft.travelCultureModule.country.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemCountryHapinessListBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.network.venues.bean.ScenicLabelBean
import com.daqsoft.provider.utils.AddressUtil
import com.daqsoft.provider.utils.StringUtils
import com.daqsoft.travelCultureModule.country.bean.AgritainmentBean
import com.daqsoft.travelCultureModule.country.model.CountryHapVideoViewModel
import com.daqsoft.travelCultureModule.country.model.CountryTourViewModel
import com.jakewharton.rxbinding2.view.RxView
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

/**
 * desc :农家乐适配器
 * @author 江云仙
 * @date 2020/4/14 19:23
 */
class CountryHappinessMoreAdapter(var mModel: CountryHapVideoViewModel) : RecyclerViewAdapter<ItemCountryHapinessListBinding, AgritainmentBean>(R.layout.item_country_hapiness_list) {
    var selfLocation: LatLng? = null
    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemCountryHapinessListBinding, position: Int, item: AgritainmentBean) {
        mBinding.txtCountryHapName.text = "${item.name}"
        var strBuilder = StringBuilder("")
        if (!item.region.isNullOrEmpty()) {
            strBuilder.append(item.region)
        }
        if (selfLocation != null) {
            if (item.lat != 0.0 && item.lon != 0.0) {
                var distance = AddressUtil.getLocationDistanceCh(
                    selfLocation!!,
                    LatLng(item.lat, item.lon)
                )
                if (distance != "") {
                    strBuilder.append(" | 距您$distance")
                }
            }
        }
        if (!item.consumeAvg.isNullOrEmpty()) {
            strBuilder.append(" | " + String.format(BaseApplication.context!!.getString(R.string.food_ls_price), item.consumeAvg))
        }
        if (!item.businessTime.isNullOrEmpty()) {
            strBuilder.append(" | " + String.format(BaseApplication.context!!.getString(R.string.food_ls_opentime, item.businessTime)))
//            mBinding.txtCountryHapTime.text = String.format(BaseApplication.context!!.getString(R.string.food_ls_opentime, item.businessTime))
//            mBinding.txtCountryHapTime.visibility = View.VISIBLE
        }
        mBinding.txtCountryHapInfo.text = strBuilder.toString()
        if (!item.shopOrderPrice.isNullOrEmpty()) {
            mBinding.txtHotelRoomPrice.text = item.shopOrderPrice
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
            Glide.with(BaseApplication.context!!).load(StringUtil.getImageUrlFill(images[0],210,252)).placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.imgCountryHapLs)
        } else {
            Glide.with(BaseApplication.context!!).load(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.imgCountryHapLs)
        }
        if (item.collectionStatus.isNotEmpty()) {
            if (item.collectionStatus == "1") {
                mBinding.imgCountryHapCollect.setImageResource(R.mipmap.activity_collect_selected)
            } else {
                mBinding.imgCountryHapCollect.setImageResource(R.mipmap.activity_collect_normal)
            }
            mBinding.imgCountryHapCollect.visibility = View.VISIBLE
        } else {
            mBinding.imgCountryHapCollect.visibility = View.GONE
        }
        //点击收藏
        RxView.clicks(mBinding.imgCountryHapCollect)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (item.collectionStatus != null) {
                    if (item.collectionStatus == "1") {
//                        item.collectionStatus="0"
                        mModel.collectionCancel(item.id, mBinding.imgCountryHapCollect,item,this)
                    } else {
                        mModel.collection(item.id, mBinding.imgCountryHapCollect,item,this)
                    }

                }
            }

//        val tags = mutableListOf<ScenicLabelBean>()
        if (!item.type.isNullOrEmpty()||!item.startStatus.isNullOrEmpty()){
            // 处理标签
            val tags:MutableList<String> = mutableListOf()
            if (item.startStatus == "1") {
                tags.add(item.level)
            }
            if (item.type.split(",").isNotEmpty()){
                for (i in item.type.split(",")) {
                    if (!i.isNullOrEmpty()){
                        tags.add(i)
                    }

                }
            }
            mBinding.recyCountryHapLabels.setLabels(tags)
            mBinding.recyCountryHapLabels.setLabelBackgroundDrawable(BaseApplication.context.resources.getDrawable(R.drawable.country_ff9e05_stroke_1_round_2), Color.parseColor("#ff9e05"))
            mBinding.recyCountryHapLabels.visibility = View.VISIBLE
        }else{
            mBinding.recyCountryHapLabels.visibility = View.GONE
        }
        if (!item.video.isNullOrEmpty()){
            mBinding.imgCountryHapVideo.visibility=View.VISIBLE
        }else{
            mBinding.imgCountryHapVideo.visibility=View.GONE
        }
        if (!item.panoramaUrl.isNullOrEmpty()){
            mBinding.imgCountryHap720.visibility=View.VISIBLE
        }else{
            mBinding.imgCountryHap720.visibility=View.GONE
        }

//
        mBinding.root.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_HAPPINESS_DETAIL)

                .withString("id", item.id)
                .navigation()
        }

    }


}