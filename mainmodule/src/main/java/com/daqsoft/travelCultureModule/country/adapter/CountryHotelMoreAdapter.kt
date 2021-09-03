package com.daqsoft.travelCultureModule.country.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemHotelMoreListBinding
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.utils.AddressUtil
import com.daqsoft.travelCultureModule.country.bean.ApiHoteltBean
import com.daqsoft.travelCultureModule.country.model.CountryHotelMoreViewModel
import com.jakewharton.rxbinding2.view.RxView
import org.jetbrains.anko.textColor
import java.util.concurrent.TimeUnit

/**
 * desc :住名宿适配器
 * @author 江云仙
 * @date 2020/4/14 19:23
 */
class CountryHotelMoreAdapter(var mModel: CountryHotelMoreViewModel,var mContext:Context) : RecyclerViewAdapter<ItemHotelMoreListBinding, ApiHoteltBean>(R.layout.item_hotel_more_list) {
    var selfLocation: LatLng? = null
    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemHotelMoreListBinding, position: Int, item: ApiHoteltBean) {
        mBinding.txtCountryHapName.text = "${item.name}"
        var strBuilder = StringBuilder("")
        if (!item.regionName.isNullOrEmpty()) {
            strBuilder.append(item.regionName)
        }
        if (selfLocation != null) {
            if (item.latitude != 0.0 && item.longitude != 0.0) {
                var distance = AddressUtil.getLocationDistanceCh(
                    selfLocation!!,
                    LatLng(item.latitude, item.longitude)
                )
                if (distance != "") {
                    strBuilder.append(" | 距您$distance")
                }
            }
        }
//        if (!item.consumeAvg.isNullOrEmpty()) {
//            strBuilder.append(" | " + String.format(BaseApplication.context!!.getString(R.string.food_ls_price), item.consumeAvg))
//        }
        if (!item.audioTime.isNullOrEmpty()) {
            strBuilder.append(" | " + String.format(BaseApplication.context!!.getString(R.string.food_ls_opentime, item.audioTime)))
//            mBinding.txtCountryHapTime.text = String.format(BaseApplication.context!!.getString(R.string.food_ls_opentime, item.businessTime))
//            mBinding.txtCountryHapTime.visibility = View.VISIBLE
        }
        mBinding.txtCountryHapInfo.text = strBuilder.toString()
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
            Glide.with(BaseApplication.context!!).load(images[0]).placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.imgCountryHapLs)
        } else {
            Glide.with(BaseApplication.context!!).load(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.imgCountryHapLs)
        }
        if (item.vipResourceStatus != null) {
            if (item.vipResourceStatus.collectionStatus) {
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
                if (item.vipResourceStatus.collectionStatus != null) {
                    if (item.vipResourceStatus.collectionStatus) {
//                        item.collectionStatus="0"
                        mModel.collectionCancel(item.id, mBinding.imgCountryHapCollect,item,this)
                    } else {
                        mModel.collection(item.id, mBinding.imgCountryHapCollect,item,this)
                    }

                }
            }
        mBinding.flowLayoutT.removeAllViews()
        var levelList: MutableList<String> = mutableListOf()
        if (!item.hotelLevel.isNullOrEmpty()) {
            var levels = item.hotelLevel?.split(",")
            if (!levels.isNullOrEmpty()) {
                levelList.addAll(levels)
            }
        }
        var levelCount = levelList.size
        if (!item.type.isNullOrEmpty()) {
            levelList.addAll(item.type)
        }
        for (index in levelList.indices) {
            var vi = TextView(mContext)
            var lp = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams
                    .WRAP_CONTENT
            )
            lp.rightMargin = mContext!!.resources.getDimension(R.dimen.dp_4).toInt()
            lp.topMargin = mContext!!.resources.getDimension(R.dimen.dp_4).toInt()
            vi.layoutParams = lp
            vi.setPadding(
                mContext!!.resources.getDimension(R.dimen.dp_3).toInt(), mContext!!.resources.getDimension(
                    R
                        .dimen.dp_1
                ).toInt(), mContext!!.resources.getDimension(R.dimen.dp_3).toInt(), mContext!!.resources
                    .getDimension(R.dimen.dp_1).toInt()
            )
            vi.textSize = 10f
            if (index < levelCount) {
                vi.text = levelList[index]
                vi.background = mContext!!.resources.getDrawable(R.drawable.shape_hotel_list_item1)
                vi.textColor = mContext!!.resources.getColor(R.color.colorPrimary)
            } else {
                vi.text = levelList[index]
                vi.background = mContext!!.resources.getDrawable(R.drawable.shape_hotel_list_item)
                vi.textColor = mContext!!.resources.getColor(R.color.txt_black)
            }
            mBinding.flowLayoutT.addView(vi)
        }
//        if (!item.type.isNullOrEmpty()||!item.hotelLevel.isNullOrEmpty()){
//            // 处理标签
//            val tags:MutableList<String> = mutableListOf()
//            if (!item.hotelLevel.isNullOrEmpty()) {
//                tags.add(item.hotelLevel)
//            }
//            for (i in item.type) {
//                if (!i.isNullOrEmpty()){
//                    tags.add(i)
//                }
//            }
//            mBinding.recyCountryHapLabels.setLabels(tags)
//            mBinding.recyCountryHapLabels.setLabelBackgroundDrawable(BaseApplication.context.resources.getDrawable(R.drawable.country_ff9e05_stroke_1_round_2), Color.parseColor("#ff9e05"))
//            mBinding.recyCountryHapLabels.visibility = View.VISIBLE
//        }else{
//            mBinding.recyCountryHapLabels.visibility = View.GONE
//        }
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
        mBinding.root.onNoDoubleClick {
            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    ARouter.getInstance()
                        .build(ZARouterPath.ZMAIN_HOTEL_DETAIL)
                        .withString("id", item.id)
                        .navigation()
                }
        }

    }


}