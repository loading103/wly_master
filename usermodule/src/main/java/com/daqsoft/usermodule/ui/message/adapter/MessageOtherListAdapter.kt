package com.daqsoft.usermodule.ui.message.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.MessageListBean
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.utils.ShareUtils
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.*

/**
 * @Description
 * @ClassName   MessageListAdapter
 * @Author      luoyi
 * @Time        2020/12/17 17:15
 */
class MessageOtherListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var datas: MutableList<MessageListBean> = mutableListOf()

    private  var layType=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            MSG_OF_ACTIVITY -> {
                var binding: ItemMessageTypeChangeActivityBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context!!),
                        R.layout.item_message_type_change_activity,
                        parent,
                        false
                    )
                return MsgActivityViewHolder(binding)
            }
            MSG_OF_VOT -> {
                var binding: ItemMessageTypeChangeVotBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context!!),
                        R.layout.item_message_type_change_vot,
                        parent,
                        false
                    )
                return MsgVotViewHolder(binding)
            }
            else-> {
                var binding: ItemMessageTypeChangeTsBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context!!),
                        R.layout.item_message_type_change_ts,
                        parent,
                        false
                    )
                return MsgTsViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MsgActivityViewHolder -> {
                holder.binding?.data=datas[position]
                holder.binding?.llRoot?.onNoDoubleClick {
                    startActivity(datas[position])
                }
            }
            // 志愿者消息
            is MsgVotViewHolder -> {
                holder.binding?.data=datas[position]
                if(datas[position].resourceStatus=="6"){
                    holder.binding?.ivImage?.setImageResource(R.mipmap.complaint_details_completed)
                }else  if(datas[position].resourceStatus=="79" || datas[position].resourceStatus=="7"){
                    holder.binding?.ivImage?.setImageResource(R.mipmap.volunteer_team_details_result_fail_icon1)
                }else  if(datas[position].messageType=="inviteJoinTeam"){
                    holder.binding?.ivImage?.setImageResource(R.mipmap.volunteer_personal_message_list_icon_invite)
                }
                else{
                    holder.binding?.ivImage?.setImageResource(R.mipmap.mine_notification_complain_icon_normal)
                }
                if(!TextUtils.isEmpty(datas[position].messageTitle)){
                    holder.binding?.tvTitle?.text=datas[position].messageTitle
                }else{
                    holder.binding?.tvTitle?.text=datas[position].getVotMessage()
                }
                onVotClickType( holder.binding?.llRoot,datas[position])
            }

            // 投诉消息
            is MsgTsViewHolder-> {
                holder.binding?.data=datas[position]
                if(datas[position].relationTime.isNotEmpty()){
                    val split = datas[position].relationTime.split(" ")
                    holder.binding?.tvTime?.text=String.format("投诉时间：%s",split[0])
                    holder.binding?.tvTime?.visibility=View.VISIBLE
                }else{
                    holder.binding?.tvTime?.visibility=View.INVISIBLE
                }
                if(datas[position].title.contains("已受理")){
                    holder.binding?.ivHead?.setImageResource(R.mipmap.mine_notification_complain_icon_normal)
                }else  if(datas[position].title.contains("未通过")){
                    holder.binding?.ivHead?.setImageResource(R.mipmap.volunteer_team_details_result_fail_icon1)
                } else{
                    holder.binding?.ivHead?.setImageResource(R.mipmap.complaint_details_completed)
                }
                holder.binding?.tvContent?.text=String.format("投诉对象：%s",datas[position].content)

                holder.binding?.llRoot?.setOnClickListener {
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_COMPLAINT_DETAILS_ACTIVITY)
                        .withString("id", datas[position].relationId.toString())
                        .navigation()
                }

            }
        }
    }



    override fun getItemViewType(position: Int): Int {
        return layType;
    }

    fun setLayoutType(classify: String) {
        if(classify=="3" ){
            layType= MSG_OF_ACTIVITY
        }else  if(classify=="4"){
            layType= MSG_OF_VOT
        }else  if(classify=="5" ){
            layType= MSG_OF_TS
        }
    }

    companion object {
        /**
         * 活动变更
         */
        const val MSG_OF_ACTIVITY = 1
        /**
         * 志愿者
         */
        const val MSG_OF_VOT = 2
        /**
         * 投诉进度
         */
        const val MSG_OF_TS=3
    }


    /**
     * 活动变更
     */
    inner class MsgActivityViewHolder : RecyclerView.ViewHolder {
        var binding: ItemMessageTypeChangeActivityBinding? = null

        constructor(bind: ItemMessageTypeChangeActivityBinding) : super(bind.root) {
            this.binding = bind
        }
    }

    /**
     * 志愿者
     */
    inner class MsgVotViewHolder : RecyclerView.ViewHolder {
        var binding: ItemMessageTypeChangeVotBinding? = null

        constructor(bind: ItemMessageTypeChangeVotBinding) : super(bind.root) {
            this.binding = bind
        }
    }

    /**
     * 投诉
     */
    inner class MsgTsViewHolder : RecyclerView.ViewHolder {
        var binding: ItemMessageTypeChangeTsBinding? = null

        constructor(bind: ItemMessageTypeChangeTsBinding) : super(bind.root) {
            this.binding = bind
        }
    }


    /**
     * 志愿者点击事件
     */
    private fun onVotClickType(llRoot: LinearLayout?, data: MessageListBean) {

        llRoot?.onNoDoubleClick {
            if(TextUtils.isEmpty(data.getVotPathUrl())){
                return@onNoDoubleClick
            }
            ARouter.getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("mTitle", "消息详情")
                .withString("html", data.getVotPathUrl())
                .navigation()
        }

    }
    /**
     * 跳转
     */

    private fun startActivity(bean: MessageListBean) {
        // 活动与邀请 跳转对象：1活动；2话题；3商品；4外部链接
        if (bean.jumpType == "1") {
            if (!TextUtils.isEmpty(bean.jumpUrl)) {
                ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", bean.title)
                    .withString("html", bean.jumpUrl)
                    .navigation()
            } else {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_HOT_ACITITY)
                    .withString("id", bean.relationId)
                    .navigation()
            }
        } else if (bean.jumpType == "2") {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_TOPIC_DETAIL)
                .withString("id", bean.relationId)
                .navigation()
        } else if (bean.jumpType == "3") {
            val shopUrl = SPUtils.getInstance().getString(SPKey.SHOP_URL, "")
            val uuid = SPUtils.getInstance().getString(SPKey.UUID)
            val token = SPUtils.getInstance().getString(SPKey.USER_CENTER_TOKEN)
            val encry = SPUtils.getInstance().getString(SPKey.ENCRYPTION)
            // 拼接跳转商品页面地址
            val url = shopUrl + "/goods/detail?&id=" + bean.relationId + "&unid=" + uuid + "&token=" + token + "&encryption=" + encry
            ARouter.getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("mTitle", bean.title)
                .withString("html", url)
                .navigation()
        } else if (bean.jumpType == "4") {
            ARouter.getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("mTitle", bean.title)
                .withString("html", bean.jumpUrl)
                .navigation()
        }else{
            chooseTypeStart(bean)
        }
    }
    private fun chooseTypeStart(bean: MessageListBean) {
        var path=""
        when(bean.resourceType){
            // 景区
            ResourceType.CONTENT_TYPE_SCENERY ->
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_SCENIC_DETAIL)
                    .withString("id", bean.relationId)
                    .navigation()
            // 景点
            ResourceType.CONTENT_TYPE_SCENIC_SPOTS -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_SCENIC_SPOT_DETAI)
                    .withString("id",  bean.relationId.toString())
                    .navigation()
            }
            // 景观点
            ResourceType.CONTENT_TYPE_RURAL_SPOTS -> {
                ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_SCENIC_SPOT_ACTIVITY)
                    .withString("id",  bean.relationId.toString())
                    .navigation()
            }
            // 酒店
            ResourceType.CONTENT_TYPE_HOTEL -> {
                ARouter.getInstance()
                    .build(ZARouterPath.ZMAIN_HOTEL_DETAIL)
                    .withString("id",  bean.relationId)
                    .navigation()
            }
            // 场馆
            ResourceType.CONTENT_TYPE_VENUE -> {
                ARouter.getInstance()
                    .build(ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY)
                    .withString("id",  bean.relationId)
                    .navigation()
            }
            // 美食(餐饮)
            ResourceType.CONTENT_TYPE_RESTAURANT -> {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_FOOD_DETAIL)
                    .withString("id",  bean.relationId)
                    .navigation()
            }
            // 内容
            ResourceType.CONTENT -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                    .withString("id",  bean.relationId)
                    .withString("contentTitle", "资讯详情")
                    .navigation()
            }
            // 故事
            ResourceType.CONTENT_TYPE_STORY -> {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_STORY_DETAIL)
                    .withString("id",  bean.relationId)
                    .withInt("type", 1)
                    .navigation()
            }
            // 农家乐
            ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_HAPPINESS_DETAIL)
                    .withString("id",  bean.relationId)
                    .navigation()
            }
            // 品牌信息
            ResourceType.CONTENT_TYPE_BRAND -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_DETAIL)
                    .withString("id",  bean.relationId)
                    .navigation();
            }
            // 大讲堂
            ResourceType.CONTENT_TYPE_COURSE->{
                MainARouterPath.toLectureHallDetail( bean.relationId)
            }
            // 活动
            ResourceType.CONTENT_TYPE_ACTIVITY -> {
                if (!bean.jumpUrl.isNullOrEmpty()) {
                    ARouter.getInstance()
                        .build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString("mTitle", bean.relationTitle)
                        .withString("html", bean.jumpUrl)
                        .navigation()
                } else {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_HOT_ACITITY)
                        .withString("id",  bean.relationId)
                        .navigation()

                }
            }
            // 乡村详情
            ResourceType.CONTENT_TYPE_COUNTRY -> {
                ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_COUNTRY_DETAIL_ACTIVITY)
                    .withString("id",  bean.relationId)
                    .navigation();
            }
            // 品非遗基地
            ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE,ResourceType.CONTENT_TYPE_HERITAGE_TEACHING_BASE ->
                ARouter.getInstance()
                    .build(ARouterPath.LegacyModule.LEGACY_EXPERIENCE_DETAIL_ACTIVITY)
                    .withString("id", bean.relationId)
                    .withString("type",bean.resourceType)
                    .navigation()
            // 品非遗项目
            ResourceType.CONTENT_TYPE_HERITAGE_ITEM ->
                ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_Smrity_DETAIL_ACTIVITY)
                    .withString("id", bean.relationId)
                    .navigation()
            // 品非遗传承人
            ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE ->
                ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_PEOPLE_DETAIL_ACTIVITY)
                    .withString("id", bean.relationId)
                    .navigation()
            ResourceType.CONTENT_TYPE_ASSOCIATION ->
                ARouter.getInstance().build(MainARouterPath.MAIN_CLUB_INFO)
                    .withInt("id",  bean.relationId.toInt())
                    .navigation()
            ResourceType.CONTENT_TYPE_ENTERTRAINMENT ->
                ARouter.getInstance().build(MainARouterPath.MAIN_PLAYGROUND_DETAIL)
                    .withString("id",  bean.relationId)
                    .navigation()

            ResourceType.TEAM_ACTIVITY_SERVICE ->{
                var url :String= ShareModel.getVoteFengCai( bean.relationId)
                ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle","志愿服务")
                    .withString("html", url)
                    .navigation()
            }
            ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_VOLUNTEER_ACTIVITY)
                    .withString("id", bean.relationId)
                    .withString("classifyId", bean?.relationUserId)
                    .navigation()
            }

        }
    }
}



