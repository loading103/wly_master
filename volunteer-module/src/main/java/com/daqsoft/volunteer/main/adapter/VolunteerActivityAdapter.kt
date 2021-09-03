package com.daqsoft.volunteer.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerActivityBean
import com.daqsoft.volunteer.databinding.VolunteerActivityItemBinding
import com.daqsoft.volunteer.databinding.VolunteerTagItemBinding
import com.daqsoft.volunteer.main.vm.VolunteerMainVM
import org.jetbrains.anko.textColor

/**
 *@package:com.daqsoft.volunteer.net
 *@date:2020/6/3 11:16
 *@author: caihj
 *@des:志愿招募适配器
 **/
class VolunteerActivityAdapter(context: Context,type:Int = 0):RecyclerViewAdapter<VolunteerActivityItemBinding,VolunteerActivityBean>(R.layout.volunteer_activity_item) {
    val mContext = context
    val mType = type
    @SuppressLint("ResourceType")
    override fun setVariable(
        mBinding: VolunteerActivityItemBinding,
        position: Int,
        item: VolunteerActivityBean
    ) {
        Glide.with(mContext).load(item.images.getRealImages()).placeholder(R.mipmap.placeholder_img_fail_h158).into(mBinding.image)
        mBinding.name = item.name
            // 当活动结束时
        if (item.activityStatus == "2") {
            mBinding.tvIsOver.visibility = View.VISIBLE
            mBinding.ivCollect.visibility = View.GONE
        } else {
            mBinding.ivCollect.visibility = View.VISIBLE
            mBinding.tvIsOver.visibility = View.GONE
        }
        if(mType == 1){
            mBinding.ivCollect.visibility = View.GONE
        }else{
            mBinding.ivCollect.visibility = View.VISIBLE
        }
        val tags = mutableListOf<String>()
        if (item.faithAuditStatus == "1") {
            tags.add("诚信免审")
        }

        if (item.faithUseStatus == "1") {
            tags.add("诚信优享")
        }

        if (item.tagNames.isNotEmpty() && item.tagNames != null) {
            val tag = item.tagNames.split(",")
            for (el in tag) {
                tags.add(el)
            }
        }
        var startTime = DateUtil.formatDateByString("MM.dd", "yyyy-MM-dd", item.useStartTime)
        var endTime = DateUtil.formatDateByString("MM.dd", "yyyy-MM-dd", item.useEndTime)
        mBinding.ivCollect.isSelected = item.userResourceStatus.collectionStatus
        var time = "$startTime - $endTime"
        var count =if(item.recruitedCount.isNullOrEmpty()){
             "0"
        }else{
             item.recruitedCount
        }
        var total =
            mContext.getString(R.string.home_activity_total_number, count, item.totalStock)
        mBinding.totalNum = DividerTextUtils.convertString(StringBuilder(), total, time)
        var left = mContext.getString(R.string.home_activity_rest_number, item.stock.toString())
        mBinding.left = left
        tags.add(0, left)
        tags.add(getActivityStauts(item.activityStatus, item.type))
        if (!tags.isNullOrEmpty()) {
            val tagLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            /**
             * 标签适配器包括 剩余名额，是否开启诚信面审 诚信优享，tags
             */
            val tagAdapter =
                object : RecyclerViewAdapter<VolunteerTagItemBinding, String>(R.layout.volunteer_tag_item) {
                    @SuppressLint("CheckResult")
                    override fun setVariable(mBinding: VolunteerTagItemBinding, position: Int, item: String) {

                        if (item == "诚信免审" || item == "诚信优享") {
                            //诚信
                            mBinding.tvVolunteer.background =
                                mContext.getDrawable(R.drawable.home_b_36cd64_stroke_null_round_2)
                            mBinding.tvVolunteer.textColor = mContext.resources.getColor(R.color.white)

                            if (item == "诚信优享") {
                                var d = mContext.resources.getDrawable(R.mipmap.activity_enjoy)
                                mBinding.tvVolunteer.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null)
                            } else {
                                var d = mContext.resources.getDrawable(R.mipmap.activity_exempt)
                                mBinding.tvVolunteer.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null)
                            }
                        } else if (item.startsWith("还剩")) {
                            // 剩余
                            mBinding.name = item
                            mBinding.tvVolunteer.background =
                                mContext.getDrawable(R.drawable.home_b_ff9e05_stroke_null_round_2)
                            mBinding.tvVolunteer.textColor = mContext.resources.getColor(R.color.white)
                        } else {
                            // tag
                            mBinding.name = item
                            mBinding.tvVolunteer.background =
                                mContext.getDrawable(R.drawable.shape_label_primary_color_bg_2)
                            mBinding.tvVolunteer.textColor = mContext.resources.getColor(R.color.colorPrimary)
                        }
                    }
                }
            tagAdapter.add(tags)
            mBinding.rvTags.layoutManager = tagLayoutManager
            mBinding.rvTags.adapter = tagAdapter
            mBinding.rvTags.visibility = View.VISIBLE
        } else {
            mBinding.rvTags.visibility = View.GONE
        }

        var address: String? = DividerTextUtils.convertDotString(
            StringBuilder(),
            if (!item.cityRegionNames.isNullOrEmpty()) {
                item.cityRegionNames
            } else {
                ""
            },
            if (!item.address.isNullOrEmpty()) {
                item.address
            } else {
                ""
            }
            , if (!item.resourceNameStr.isNullOrEmpty()) {
                item.resourceNameStr
            } else {
                ""
            }
        )

        if (address.isNullOrEmpty()) {
            mBinding.tvAddress.visibility = View.GONE
        } else {
            mBinding.address = address
            mBinding.tvAddress.visibility = View.VISIBLE
        }

        mBinding.ivCollect.onNoDoubleClick {
            if (AppUtils.isLogin()) {
              onItemClick?.onItemClick(item.id,position,item.userResourceStatus.collectionStatus)
            } else {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }

        mBinding.root.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_VOLUNTEER_ACTIVITY)
                .withString("id", item.id)
                .withString("classifyId", item.classifyId)
                .navigation()
        }
    }

    var onItemClick: OnItemClickListener? = null


    interface OnItemClickListener {
        fun onItemClick(id:String,position: Int,status: Boolean)
    }

    private fun getActivityStauts(activityStatus: String, type: String): String {
        // 活动状态 0(未开始) 1(进行中) 2(已结束) 3 (报名中/招募中)
        return when (activityStatus) {
            "0" -> {
                "未开始"
            }
            "1" -> {
                "进行中"
            }
            "2" -> {
                "已结束"
            }
            "3" -> {
                if (type == ActivityType.ACTIVITY_TYPE_VOLUNT) {
                    "招募中"
                } else if (type == ActivityType.ACTIVITY_TYPE_RESERVE) {
                    "预定中"
                } else {
                    "报名中"
                }
            }
            else -> {
                ""
            }
        }
    }

    override fun payloadUpdateUi(mBinding: VolunteerActivityItemBinding, position: Int, payloads: MutableList<Any>) {
        if (payloads[0] == "updateCollect") {
            val item = getData()[position]
            if (item.userResourceStatus != null) {
                mBinding.ivCollect.isSelected = item.userResourceStatus.collectionStatus
            }
        }
    }

    fun notifyUpdateCollectStatus(position: Int, status: Boolean) {
        try {
            val item = getData()[position]
            if (item.userResourceStatus != null) {
                getData()[position]!!.userResourceStatus!!.collectionStatus = status
                notifyItemChanged(position, "updateCollect")
            }
        } catch (e: Exception) {

        }
    }

}