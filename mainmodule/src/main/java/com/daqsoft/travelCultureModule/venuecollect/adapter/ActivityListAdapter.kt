package com.daqsoft.travelCultureModule.venuecollect.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemActivityListBinding
import com.daqsoft.mainmodule.databinding.ItemActivityListNewBinding
import com.daqsoft.mainmodule.databinding.ItemExhibitionListBinding
import com.daqsoft.mainmodule.databinding.ItemExhibitionOnlineBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.ExhibitionTagBean
import com.daqsoft.provider.bean.VenueCollectBean
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.imageResource
import java.lang.Exception

internal class ActivityListAdapter : RecyclerViewAdapter<ItemActivityListNewBinding, ActivityBean>(R.layout.item_activity_list_new) {

    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemActivityListNewBinding, position: Int, item: ActivityBean) {
        mBinding.datas=item
        //标签
        var tags= mutableListOf<String>()
        if(!TextUtils.isEmpty(item.activityStatus)){
            val tagAdapter = TagAdapter()
            if(item.activityStatus=="0"){
                tags.add("预告")
                mBinding.rvTag1.visibility=View.VISIBLE
                mBinding.rvTag1.adapter=tagAdapter
                tagAdapter.setNewData(tags)
            } else if(item.activityStatus=="2"){
                tags.add("已结束")
                mBinding.rvTag1.visibility=View.VISIBLE
                mBinding.rvTag1.adapter=tagAdapter
                tagAdapter.setNewData(tags)
            } else if(item.activityStatus=="3" || item.activityStatus=="1"){
                tags.add("进行中")
                mBinding.rvTag1.visibility=View.VISIBLE
                mBinding.rvTag1.adapter=tagAdapter
                tagAdapter.setNewData(tags)
            }else{
                mBinding.rvTag1.visibility=View.GONE
            }
        }else{
            mBinding.rvTag1.visibility=View.GONE
        }
        //收藏
        if(item.userResourceStatus.collectionStatus){
            mBinding.imgCollect.setImageResource(R.mipmap.activity_collect_selected)
        }else{
            mBinding.imgCollect.setImageResource(R.mipmap.activity_collect_normal)
        }


        mBinding.imgCollect.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                if (onCollectClickListener != null && item.userResourceStatus != null) {
                    onCollectClickListener!!.onCollectClick(item.id.toString(), position, item.userResourceStatus.collectionStatus,"CONTENT_TYPE_ACTIVITY")
                }
            } else {
                ToastUtils.showMessage("该操作需要登录，请先登录")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }

        }
        mBinding.root.onNoDoubleClick {
            if (!item.jumpUrl.isNullOrEmpty()) {
                ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", item.jumpName)
                    .withString("html", item.jumpUrl)
                    .navigation()
            } else {
                when (item.type) {
                    // 志愿活动
                    ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                        ARouter.getInstance()
                            .build(MainARouterPath.MAIN_VOLUNTEER_ACTIVITY)
                            .withString("id", item.id)
                            .withString("classifyId", item.classifyId)
                            .navigation()
                    }
                    // 预订活动
                    ActivityType.ACTIVITY_TYPE_RESERVE -> {
                        // 预订
                        when (item.method) {
                            // 付费预订活动
                            ActivityMethod.ACTIVITY_MODE_INTEGRAL_PAY -> {

                                ARouter.getInstance()
                                    .build(MainARouterPath.MAIN_ACTIVITY_PAY_ORDER)
                                    .withString("jumpUrl", item.jumpUrl)
                                    .navigation()
                            }
                            else -> {
                                ARouter.getInstance()
                                    .build(MainARouterPath.MAIN_HOT_ACITITY)
                                    .withString("id", item.id)
                                    .withString("classifyId", item.classifyId)
                                    .withString("region", item.region)
                                    .navigation()
                            }
                        }
                    }
                    else -> {
                        ARouter.getInstance()
                            .build(MainARouterPath.MAIN_HOT_ACITITY)
                            .withString("id", item.id)
                            .withString("classifyId", item.classifyId)
                            .navigation()
                    }
                }
            }
        }
    }

    override fun payloadUpdateUi(mBinding: ItemActivityListNewBinding, position: Int, payloads: MutableList<Any>) {
        if (payloads[0] == "updateCollect") {
            if (getData()[position].userResourceStatus != null) {
                val status = getData()[position].userResourceStatus.collectionStatus
                if (status) {
                    mBinding.imgCollect.imageResource = R.mipmap.activity_collect_selected
                } else {
                    mBinding.imgCollect.imageResource = R.mipmap.activity_collect_normal
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
                if (getData()[position].userResourceStatus != null) {
                    getData()[position].userResourceStatus.collectionStatus = status
                    notifyItemChanged(position, "updateCollect")
                }
            }
        } catch (e: Exception) {

        }
    }

    var onCollectClickListener: OnCollectClickListener? = null

    interface OnCollectClickListener {
        fun onCollectClick(id: String, postion: Int, status: Boolean,type : String)
    }
}