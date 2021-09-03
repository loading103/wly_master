package com.daqsoft.travelCultureModule.playground.adapter
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemPlaygroundLsBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.PlayGroundBean
import com.daqsoft.provider.utils.AddressUtil
import com.daqsoft.travelCultureModule.resource.adapter.ScenicSpotLiveAdapter
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor
import java.lang.Exception
import java.lang.StringBuilder

class PlayGroundLsAdapter : RecyclerViewAdapter<ItemPlaygroundLsBinding, PlayGroundBean> {

    var context: Context? = null
    var selfLocation: LatLng? = null
    var region: String? = ""
    var onFoodLsItemClickListener: OnFoodLsItemClickListener? = null

    constructor(context: Context) : super(R.layout.item_playground_ls) {
        this.context = context
    }

    override fun setVariable(mBinding: ItemPlaygroundLsBinding, position: Int, item: PlayGroundBean) {
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
                if(region==item.region){
                    if (distance != "") {
                        strBuilder.append(" | 距您$distance")
                    }
                }
            }
        }
//        if (!item.consumPerson.isNullOrEmpty()) {
//            strBuilder.append(" | " + String.format(context!!.getString(R.string.food_ls_price), item.consumPerson))
//        }

        mBinding.txtFoodsInfo.text = strBuilder.toString()
        if (!item.openStartTime.isNullOrEmpty()) {
            mBinding.txtFoodsTime.visibility = View.VISIBLE
        } else {
            mBinding.txtFoodsTime.visibility = View.GONE
        }
        if (!item.consumPerson.isNullOrEmpty()) {
            mBinding.txtHotelRoomPrice.text = item.consumPerson
            mBinding.txtHotelRoomRmb.visibility = View.VISIBLE
            mBinding.txtHotelRoomQi.visibility = View.VISIBLE
            mBinding.txtHotelRoomPrice.visibility = View.VISIBLE
            mBinding.tv1.visibility = View.VISIBLE
        } else {
            mBinding.txtHotelRoomRmb.visibility = View.INVISIBLE
            mBinding.txtHotelRoomQi.visibility = View.INVISIBLE
            mBinding.txtHotelRoomPrice.visibility = View.INVISIBLE
            mBinding.tv1.visibility = View.INVISIBLE
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
//        if (!item.typeName.isNullOrEmpty()) {
//            var types: MutableList<String> = mutableListOf()
//            types.add(item.typeName)
//            mBinding.recyFoodsLabels.setLabels(types)
//            mBinding.recyFoodsLabels.visibility = View.VISIBLE
//        } else {
//            mBinding.recyFoodsLabels.visibility = View.GONE
//        }
        var types: MutableList<String> = mutableListOf()
        if (!item.typeName.isNullOrEmpty()) {
            types.add(item.typeName)
        }
        if(item.applyTagName.isNotEmpty()){
            types.add(item.applyTagName[0])
        }
        if(item.entEqtTagName.isNotEmpty()){
            types.add(item.entEqtTagName[0])
        }
        if(item.featureName.isNotEmpty()){
            types.add(item.featureName[0])
        }
        if(types.size==0){
            mBinding.recyFoodsLabels.visibility = View.GONE
            mBinding.flowLayoutT.visibility=View.GONE

        }else{
//            mBinding.recyFoodsLabels.setLabels(types)
//            mBinding.recyFoodsLabels.visibility = View.VISIBLE
            setType(types,position,mBinding)
        }
        // 活动
        if (!item.activity.isNullOrEmpty()) {
            mBinding.vFoodsAcitvity.visibility = View.VISIBLE
            mBinding.txtItemFoodsActivityWords.text = "${item.activity[0].name}"
            mBinding.vFoodsAcitvity.setOnClickListener{
                // 如果有跳转链接 直接跳转webactivity
                if (!item.activity[0].jumpUrl.isNullOrEmpty()) {
                    ARouter.getInstance()
                        .build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString("mTitle", item.activity[0].jumpName)
                        .withString("html", item.activity[0].jumpUrl)
                        .navigation()
                } else {
                    when (item.type) {
                        // 志愿活动
                        ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                            ARouter.getInstance()
                                .build(MainARouterPath.MAIN_VOLUNTEER_ACTIVITY)
//                                    .build( MainARouterPath.MAIN_SEAT_SELECT_ACTIVITY)
                                .withString("id", item.activity[0].id)
                                .withString("classifyId", item.activity[0].classifyId)
                                .navigation()
                        }
                        // 预订活动
                        ActivityType.ACTIVITY_TYPE_RESERVE -> {
                            // 预订
                            when (item.activity[0].method) {
                                // 付费预订活动
                                ActivityMethod.ACTIVITY_MODE_INTEGRAL_PAY -> {

                                    ARouter.getInstance()
                                        .build(MainARouterPath.MAIN_ACTIVITY_PAY_ORDER)
                                        .withString("jumpUrl", item.activity[0].jumpUrl)
                                        .navigation()
                                }
                                else -> {
                                    ARouter.getInstance()
                                        .build(MainARouterPath.MAIN_HOT_ACITITY)
                                        .withString("id", item.activity[0].id)
                                        .withString("classifyId", item.activity[0].classifyId)
                                        .withString("region", item.region)
                                        .navigation()
                                }
                            }
                        }
                        else -> {
                            ARouter.getInstance()
                                .build(MainARouterPath.MAIN_HOT_ACITITY)
                                .withString("id", item.activity[0].id)
                                .withString("classifyId", item.activity[0].classifyId)
                                .navigation()
                        }
                    }
                }
            }
        } else {
            mBinding.vFoodsAcitvity.visibility = View.GONE
        }

        if (!item.video.isNullOrEmpty()){
            mBinding.imgFoodsVideo.visibility=View.VISIBLE
        }else{
            mBinding.imgFoodsVideo.visibility=View.GONE
        }
        if (!item.audio.isNullOrEmpty()) {
            mBinding.imgFoodsGoldStore.visibility = View.VISIBLE
        } else {
            mBinding.imgFoodsGoldStore.visibility = View.GONE
        }
        if (!item.panoramaUrl.isNullOrEmpty()) {
            mBinding.imgFoods720.visibility = View.VISIBLE
        } else {
            mBinding.imgFoods720.visibility = View.GONE
        }
        if (!item.openStartTime.isNullOrEmpty()) {
            mBinding.txtFoodsTime.text = String.format(context!!.getString(R.string.food_ls_opentime, item.openStartTime+"-"+item.openEndTime))
            mBinding.txtFoodsTime.visibility = View.VISIBLE
        } else {
            mBinding.txtFoodsTime.visibility = View.GONE
        }

        mBinding.root.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_PLAYGROUND_DETAIL)
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

    var adapter: TagAdapter? = null
    private fun setType(types: MutableList<String>, position: Int, mBinding: ItemPlaygroundLsBinding) {
//        mBinding.flowLayoutT.removeAllViews()
//        var levelCount = types.size
//
//        for (index in types.indices) {
//            var vi = TextView(context)
//            var lp = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams
//                .WRAP_CONTENT)
//            lp.rightMargin = context!!.resources.getDimension(R.dimen.dp_4).toInt()
//            lp.topMargin = context!!.resources.getDimension(R.dimen.dp_4).toInt()
//            vi.layoutParams = lp
//            vi.setPadding(
//                context!!.resources.getDimension(R.dimen.dp_3).toInt(),
//                context!!.resources.getDimension(R.dimen.dp_1).toInt(),
//                context!!.resources.getDimension(R.dimen.dp_3).toInt(),
//                context!!.resources.getDimension(R.dimen.dp_1).toInt())
//            vi.textSize = 12f
//            vi.text = types[index]
//            vi.background = context!!.resources.getDrawable(R.drawable.shape_hotel_list_item)
//            vi.textColor = context!!.resources.getColor(R.color.txt_black)
////            }
//            mBinding.flowLayoutT.addView(vi)
//        }
        adapter = TagAdapter()
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rv.layoutManager = linearLayoutManager
        mBinding.rv.adapter = adapter
        adapter?.setNewData(types)
    }

    override fun payloadUpdateUi(mBinding: ItemPlaygroundLsBinding, position: Int, payloads: MutableList<Any>) {
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