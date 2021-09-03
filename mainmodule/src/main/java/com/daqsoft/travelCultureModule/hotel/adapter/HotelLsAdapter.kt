package com.daqsoft.travelCultureModule.hotel.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemMyHotelListBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.HotelBean
import com.daqsoft.provider.utils.AddressUtil
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.travelCultureModule.hotel.util.AnyUtils
import org.jetbrains.anko.textColor
import java.lang.StringBuilder

/**
 * @Description
 * @ClassName   HotelLsAdapter
 * @Author      luoyi
 * @Time        2020/4/22 11:29
 */
class HotelLsAdapter : RecyclerViewAdapter<ItemMyHotelListBinding, HotelBean> {
    public var selfLocation: LatLng? = null
    var mContext: Context? = null
    var lister: OnHotelLsItemClickListener? = null

    constructor(context: Context) : super(R.layout.item_my_hotel_list) {
        mContext = context
    }

    @SuppressLint("CheckResult")
    override fun setVariable(
        mBinding: ItemMyHotelListBinding,
        position: Int,
        item: HotelBean
    ) {
        mBinding.item = item
        mBinding.flowLayoutT.removeAllViews()
        var levelList: MutableList<String> = mutableListOf()
        if (!item.hotelLevel.isNullOrEmpty()) {
            var levels = item.hotelLevel?.split(",")
            if (!levels.isNullOrEmpty()) {
                for (item in levels) {
                    if (!item.isNullOrEmpty() && item != "无星级") {
                        levelList.add(item)
                    }
                }
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
                mContext!!.resources.getDimension(R.dimen.dp_3).toInt(),
                mContext!!.resources.getDimension(
                    R
                        .dimen.dp_1
                ).toInt(),
                mContext!!.resources.getDimension(R.dimen.dp_3).toInt(),
                mContext!!.resources
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
        var strBuilder = StringBuilder("")
        if (!item.regionName.isNullOrEmpty()) {
            strBuilder.append(item.regionName)
        }
        if (selfLocation != null) {
            if (!item.latitude.isNullOrEmpty() && !item.longitude.isNullOrEmpty()) {
                var distance = AddressUtil.getLocationDistanceCh(
                    selfLocation!!,
                    LatLng(item.latitude.toDouble(), item.longitude.toDouble())
                )
                if (distance != "") {
                    strBuilder.append(" | 距您$distance")
                }
            }
        }
        if (item.scenic != null && !TextUtils.isEmpty(item.scenic.toString())) {
            strBuilder.append(" |  靠近${AnyUtils.getString(item.scenic, "name")}")
        }
        mBinding.tvAddress.text = strBuilder

        if (!item.floorPrice.isNullOrEmpty()) {
            var spanStr = SpannableString("￥ " + item.floorPrice + " 起")
            spanStr.setSpan(
                ForegroundColorSpan(Color.parseColor("#ff9e05")),
                0,
                2,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            spanStr.setSpan(RelativeSizeSpan(0.6f), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            spanStr.setSpan(
                ForegroundColorSpan(Color.parseColor("#ff9e05")),
                2,
                2 + item.floorPrice.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            spanStr.setSpan(
                RelativeSizeSpan(1.0f),
                2,
                2 + item.floorPrice.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            spanStr.setSpan(
                ForegroundColorSpan(Color.parseColor("#333333")),
                2 + item.floorPrice.length,
                spanStr.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            spanStr.setSpan(
                RelativeSizeSpan(0.6f),
                2 + item.floorPrice.length,
                spanStr.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )

            mBinding.tvSpan.text = spanStr
            mBinding.tvSpan.visibility = View.VISIBLE
        } else {
            mBinding.tvSpan.visibility = View.GONE
        }
        mBinding.clItemVenuesImg.setOnClickListener {
            ARouter.getInstance()
                .build(ZARouterPath.ZMAIN_HOTEL_DETAIL)
                .withString("id", item.id.toString())
                .navigation()
        }
        if (!item.panoramaUrl.isNullOrEmpty()) {
            mBinding.imgHotelLs720.visibility = View.VISIBLE
        } else {
            mBinding.imgHotelLs720.visibility = View.GONE
        }
        if (!item.video.isNullOrEmpty()) {
            mBinding.imgHotelVideoPlayer.visibility = View.VISIBLE
        } else {
            mBinding.imgHotelVideoPlayer.visibility = View.GONE
        }
        if (!item.audio.isNullOrEmpty()) {
            mBinding.imgHotelLsJpjs.visibility = View.GONE
        } else {
            mBinding.imgHotelLsJpjs.visibility = View.GONE
        }

        // 收藏
        if (getData()[position].vipResourceStatus != null) {
            val status = getData()[position].vipResourceStatus.collectionStatus
            if (status) {
                mBinding.imgItemHotelCollect.setBackgroundResource(R.mipmap.provider_collect_selected)
            } else {
                mBinding.imgItemHotelCollect.setBackgroundResource(R.mipmap.provider_collect_normal)
            }
        }
        mBinding.imgItemHotelCollect.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                if (item.vipResourceStatus != null) {
                    lister?.onCollectClick(
                        item.id,
                        position,
                        item.vipResourceStatus.collectionStatus
                    )
                }
            } else {
                ToastUtils.showMessage("该操作需要登录，请先登录")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        mBinding.vToActivity.onNoDoubleClick {
                if(!item.activity.isNullOrEmpty()){
                    var act :ActivityBean?= item.activity[0]
                    act?.let {
                        MenuJumpUtils.toActivityDetail(it.id,it.type)
                    }

                }
        }
    }

    override fun payloadUpdateUi(
        mBinding: ItemMyHotelListBinding,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads[0] == "updateCollect") {
            if (getData()[position].vipResourceStatus != null) {
                val status = getData()[position].vipResourceStatus.collectionStatus
                if (status) {
                    mBinding.imgItemHotelCollect.setBackgroundResource(R.mipmap.provider_collect_selected)
                } else {
                    mBinding.imgItemHotelCollect.setBackgroundResource(R.mipmap.provider_collect_normal)
                }
            }
        }
    }

    public interface OnHotelLsItemClickListener {
        fun onCollectClick(id: Int, position: Int, collectionStatus: Boolean)
    }

}