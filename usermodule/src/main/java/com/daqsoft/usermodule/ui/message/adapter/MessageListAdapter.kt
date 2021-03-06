package com.daqsoft.usermodule.ui.message.adapter

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.MessageListBean
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.*

/**
 * @Description
 * @ClassName   MessageListAdapter
 * @Author      luoyi
 * @Time        2020/12/17 17:15
 */
class MessageListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var datas: MutableList<MessageListBean> = mutableListOf()
    private  var layType=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            MSG_OF_ACTIVITY -> {
                var binding: ItemMessageTypeOfActivityBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context!!),
                        R.layout.item_message_type_of_activity,
                        parent,
                        false
                    )
                return MsgActivityViewHolder(binding)
            }
            MSG_OF_FEED_BACK -> {
                var binding: ItemMessageTypeOfFeedBackBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context!!),
                        R.layout.item_message_type_of_feed_back,
                        parent,
                        false
                    )
                return MsgFeedBackViewHolder(binding)
            }
            MSG_OF_NOTIFY -> {
                var binding: ItemMessageTypeOfNotifyBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context!!),
                        R.layout.item_message_type_of_notify,
                        parent,
                        false
                    )
                return MsgNotifyViewHolder(binding)
            }
            MSG_OF_REPLY -> {
                var binding: ItemMessageTypeOfReplyBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context!!),
                        R.layout.item_message_type_of_reply,
                        parent,
                        false
                    )
                return MsgReplyViewHolder(binding)
            }
            MSG_OF_REQUEST -> {
                var binding: ItemMessageTypeOfRequestBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context!!),
                        R.layout.item_message_type_of_request,
                        parent,
                        false
                    )
                return MsgRequestViewHolder(binding)
            }
            MSG_OF_TASK -> {
                var binding: ItemMessageTypeOfTaskBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context!!),
                        R.layout.item_message_type_of_task,
                        parent,
                        false
                    )
                return MsgTaskViewHolder(binding)
            }
            MSG_OF_LEVEL -> {
                var binding: ItemMessageTypeOfLevelBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context!!),
                        R.layout.item_message_type_of_level,
                        parent,
                        false
                    )
                return MsgLevelViewHolder(binding)
            }
            MSG_OF_GOOD -> {
                var binding: ItemMessageTypeOfGoodBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context!!),
                        R.layout.item_message_type_of_good,
                        parent,
                        false
                    )
                return MsgGoodViewHolder(binding)
            }
            MSG_OF_COLLECT -> {
                var binding: ItemMessageTypeOfCollectBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context!!),
                        R.layout.item_message_type_of_collect,
                        parent,
                        false
                    )
                return MsgCollectViewHolder(binding)
            }
            MSG_OF_FOLLOW -> {
                var binding: ItemMessageTypeOfFollowBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context!!),
                        R.layout.item_message_type_of_follow,
                        parent,
                        false
                    )
                return MsgFollowViewHolder(binding)
            }
            MSG_OF_ASK -> {
                var binding: ItemMessageTypeOfAskBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context!!),
                        R.layout.item_message_type_of_ask,
                        parent,
                        false
                    )
                return MsgAskViewHolder(binding)
            }
            MSG_OF_PK -> {
                var binding: ItemMessageTypeOfPkBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context!!),
                        R.layout.item_message_type_of_pk,
                        parent,
                        false
                    )
                return MsgPkViewHolder(binding)
            }
            else -> {
                var binding: ItemMessageTypeOfNotifyBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context!!),
                        R.layout.item_message_type_of_notify,
                        parent,
                        false
                    )
                return MsgNotifyViewHolder(binding)
            }
        }

    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MsgRequestViewHolder -> {

            }
            // ???????????????
            is MsgActivityViewHolder -> {
                holder.binding?.data=datas[position]
                if(datas[position]?.jumpType=="1"){
                    holder.binding?.tvType1?.visibility=View.VISIBLE
                    holder.binding?.tvType2?.visibility=View.GONE
                    holder.binding?.llRoot?.visibility=View.GONE
                }
                if(datas[position]?.jumpType=="3" || datas[position]?.jumpType=="4"){
                    holder.binding?.tvType1?.visibility=View.GONE
                    holder.binding?.tvType2?.visibility=View.VISIBLE
                    holder.binding?.llRoot?.visibility=View.GONE
                }
                if(datas[position]?.jumpType=="2" ){
                    holder.binding?.tvType1?.visibility=View.GONE
                    holder.binding?.tvType2?.visibility=View.GONE
                    holder.binding?.llRoot?.visibility=View.VISIBLE
                    holder.binding?.tvType3?.text=datas[position].relationTitle
                }
                holder.binding?.llRoot1?.onNoDoubleClick {
                    startActivity(datas[position])
                }
            }
            // ????????????
            is MsgNotifyViewHolder -> {
                holder.binding?.data=datas[position]
                holder.binding?.llRoot?.onNoDoubleClick {
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_MEASSAGE_NOTICE_DETAIL)
                        .withString("id", datas[position].id)
                        .navigation()
                }
            }
            // ????????????
            is MsgTaskViewHolder -> {
                holder.binding?.data=datas[position]
                holder.binding?.llRoot?.onNoDoubleClick {
                    ARouter.getInstance().build(ARouterPath.IntegralModule.MEMBER_HOME_ACTIVITY)
                        .navigation()
                }
            }

            is MsgFeedBackViewHolder -> {
                holder.binding?.data=datas[position]
                holder.binding?.llRoot?.onNoDoubleClick {
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_FEED_BACK_LS_ACTIVITY)
                        .navigation()
                }
            }
            // ????????????
            is MsgLevelViewHolder -> {
                holder.binding?.data=datas[position]
                holder.binding?.llRoot?.onNoDoubleClick {
                    // ?????????????????????
                    if(datas[position].resourceType=="POINT_LEVEL"){
                        ARouter.getInstance().build(ARouterPath.IntegralModule.MEMBER_HOME_ACTIVITY)
                            .navigation()
                    }
                    if(datas[position].resourceType=="CREDIT_LEVEL"){
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.USER_CREDIT_ACTIVITY)
                            .navigation()
                    }
                }
            }
            // ??????
            is MsgGoodViewHolder-> {
                holder.binding?.data=datas[position]
                setCommonContent(holder.binding?.tvContent,datas[position])
                holder.binding?.llRoot?.onNoDoubleClick {
                    startActivity(datas[position])
                }

            }
            // ??????
            is MsgCollectViewHolder-> {
                holder.binding?.data=datas[position]
                setCommonContent(holder.binding?.tvContent,datas[position])
                holder.binding?.llRoot?.onNoDoubleClick {
                    startActivity(datas[position])
                }
            }
            // ??????
            is MsgFollowViewHolder-> {
                holder.binding?.data=datas[position]
                setCommonContent(holder.binding?.tvContent,datas[position])
                holder.binding?.llRoot?.onNoDoubleClick {
                    if(datas[position].resourceType=="CONTENT_TYPE_HERITAGE_PEOPLE"){
                        ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_MINE_FANS)
                            .withString("type","fans")
                            .navigation()
                    }else{
                        startActivity(datas[position])
                    }
                }
            }
            // ??????
            is MsgAskViewHolder-> {
                holder.binding?.data=datas[position]
                setCommonContent(holder.binding?.tvContent,datas[position])
                holder.binding?.llRoot?.onNoDoubleClick {
                    ARouter.getInstance()
                        .build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString("mTitle", "????????????")
                        .withString("html", "${BaseApplication.webSiteUrl}question-detail/${datas[position].relationId}")
                        .navigation()
                }
            }
            // pk
            is MsgPkViewHolder-> {
                holder.binding?.data=datas[position]
                setCommonContent(holder.binding?.tvContent,datas[position])
                holder.binding?.llRoot?.onNoDoubleClick {
//                    startActivity(datas[position])
                    //????????????
                    ARouter.getInstance()
                        .build(ARouterPath.LegacyModule.LEGACY_WORKS_DETAIL)
                        .withInt("type", 0)
                        .withString("id", datas[position].relationId.toString())
                        .navigation()
                }
            }
        }
    }


    /**
     * ??????
     */
    private fun setCommonContent(tvContent: TextView?, bean: MessageListBean) {

        if(TextUtils.isEmpty(bean.content)){
            tvContent?.text=bean.title
            return
        }
        var ssb = SpannableStringBuilder()
        var  start=0
        if(!TextUtils.isEmpty(bean.title)){
            ssb.append(bean.title+":")
            start=bean.title.length+1
        }
        if(!TextUtils.isEmpty(bean.content)){
            ssb.append(bean.content)
        }
        val span = ForegroundColorSpan(BaseApplication.context.resources.getColor(R.color.colorPrimary))
        ssb.setSpan(span, start, ssb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvContent?.text=ssb
    }

    override fun getItemViewType(position: Int): Int {
        return layType;
    }

    fun setLayoutType(classify: String, type: String) {
        if(classify=="1" && type=="1"){
            layType=MSG_OF_NOTIFY
        }else  if(classify=="1" && type=="2"){
            layType=MSG_OF_ACTIVITY
        }else  if(classify=="1" && type=="3"){
            layType=MSG_OF_LEVEL
        }else  if(classify=="1" && type=="4"){
            layType=MSG_OF_FEED_BACK
        }else  if(classify=="1" && type=="5"){
            layType=MSG_OF_TASK
        }else  if(classify=="2" && type=="1"){
            layType=MSG_OF_GOOD
        }else  if(classify=="2" && type=="2"){
            layType= MSG_OF_COLLECT
        }else  if(classify=="2" && type=="3"){
            layType= MSG_OF_FOLLOW
        }else  if(classify=="2" && type=="4"){
            layType= MSG_OF_ASK
        }else  if(classify=="2" && type=="5"){
            layType= MSG_OF_PK
        }
    }

    companion object {
        /**
         * ??????
         */
        const val MSG_OF_NOTIFY = 1
        /**
         * ???????????????
         */
        const val MSG_OF_ACTIVITY = 2

        /**
         * ????????????
         */
        const val MSG_OF_LEVEL=3
        /**
         * ??????
         */
        const val MSG_OF_FEED_BACK = 4
        /**
         * ??????
         */
        const val MSG_OF_TASK = 5
        /**
         * ??????
         */
        const val MSG_OF_GOOD = 6
        /**
         * ??????
         */
        const val MSG_OF_COLLECT = 7
        /**
         * ??????
         */
        const val MSG_OF_FOLLOW =8
        /**
         * ??????
         */
        const val MSG_OF_ASK = 9

        /**
         * ??????PK
         */
        const val MSG_OF_PK = 10
        /**
         * ??????
         */
        const val MSG_OF_REPLY = 11

        const val MSG_OF_REQUEST = 12




    }


    /**
     * ??????????????????
     */
    inner class MsgActivityViewHolder : RecyclerView.ViewHolder {
        var binding: ItemMessageTypeOfActivityBinding? = null

        constructor(bind: ItemMessageTypeOfActivityBinding) : super(bind.root) {
            this.binding = bind
        }
    }

    /**
     * ??????
     */
    inner class MsgCollectViewHolder : RecyclerView.ViewHolder {
        var binding: ItemMessageTypeOfCollectBinding? = null

        constructor(bind: ItemMessageTypeOfCollectBinding) : super(bind.root) {
            this.binding = bind
        }
    }

    /**
     * ????????????
     */
    inner class MsgFeedBackViewHolder : RecyclerView.ViewHolder {
        var binding: ItemMessageTypeOfFeedBackBinding? = null

        constructor(bind: ItemMessageTypeOfFeedBackBinding) : super(bind.root) {
            this.binding = bind
        }
    }

    /**
     * ??????
     */
    inner class MsgNotifyViewHolder : RecyclerView.ViewHolder {
        var binding: ItemMessageTypeOfNotifyBinding? = null

        constructor(bind: ItemMessageTypeOfNotifyBinding) : super(bind.root) {
            this.binding = bind
        }
    }

    /**
     * ????????????
     */
    inner class MsgReplyViewHolder : RecyclerView.ViewHolder {
        var binding: ItemMessageTypeOfReplyBinding? = null

        constructor(bind: ItemMessageTypeOfReplyBinding) : super(bind.root) {
            this.binding = bind
        }
    }

    /**
     * ??????
     */
    inner class MsgRequestViewHolder : RecyclerView.ViewHolder {
        var binding: ItemMessageTypeOfRequestBinding? = null

        constructor(bind: ItemMessageTypeOfRequestBinding) : super(bind.root) {
            this.binding = bind
        }
    }

    /**
     * ??????
     */
    inner class MsgTaskViewHolder : RecyclerView.ViewHolder {
        var binding: ItemMessageTypeOfTaskBinding? = null

        constructor(bind: ItemMessageTypeOfTaskBinding) : super(bind.root) {
            this.binding = bind
        }
    }
    /**
     * ??????`
     */
    inner class MsgLevelViewHolder : RecyclerView.ViewHolder {
        var binding: ItemMessageTypeOfLevelBinding? = null

        constructor(bind: ItemMessageTypeOfLevelBinding) : super(bind.root) {
            this.binding = bind
        }
    }
    /**
     * ??????
     */
    inner class MsgGoodViewHolder : RecyclerView.ViewHolder {
        var binding: ItemMessageTypeOfGoodBinding? = null

        constructor(bind: ItemMessageTypeOfGoodBinding) : super(bind.root) {
            this.binding = bind
        }
    }
    /**
     * ??????
     */
    inner class MsgFollowViewHolder : RecyclerView.ViewHolder {
        var binding: ItemMessageTypeOfFollowBinding? = null

        constructor(bind: ItemMessageTypeOfFollowBinding) : super(bind.root) {
            this.binding = bind
        }
    }
    /**
     * ??????
     */
    inner class MsgAskViewHolder : RecyclerView.ViewHolder {
        var binding: ItemMessageTypeOfAskBinding? = null

        constructor(bind: ItemMessageTypeOfAskBinding) : super(bind.root) {
            this.binding = bind
        }
    }
    /**
     * pk
     */
    inner class MsgPkViewHolder : RecyclerView.ViewHolder {
        var binding: ItemMessageTypeOfPkBinding? = null

        constructor(bind: ItemMessageTypeOfPkBinding) : super(bind.root) {
            this.binding = bind
        }
    }


    /**
     * ??????
     */
    private fun startActivity(bean: MessageListBean) {
        // ??????????????? ???????????????1?????????2?????????3?????????4????????????
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
            // ??????????????????????????????
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
    /**
     * ??????
     */
    private fun chooseTypeStart(bean: MessageListBean) {
        when(bean.resourceType){
            // ??????
            ResourceType.CONTENT_TYPE_SCENERY ->
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_SCENIC_DETAIL)
                    .withString("id", bean.relationId)
                    .navigation()
            // ??????
            ResourceType.CONTENT_TYPE_SCENIC_SPOTS -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_SCENIC_SPOT_DETAI)
                    .withString("id",  bean.relationId.toString())
                    .navigation()
            }
            // ?????????
            ResourceType.CONTENT_TYPE_RURAL_SPOTS -> {
                ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_SCENIC_SPOT_ACTIVITY)
                    .withString("id",  bean.relationId.toString())
                    .navigation()
            }
            // ??????
            ResourceType.CONTENT_TYPE_HOTEL -> {
                ARouter.getInstance()
                    .build(ZARouterPath.ZMAIN_HOTEL_DETAIL)
                    .withString("id",  bean.relationId)
                    .navigation()
            }
            // ??????
            ResourceType.CONTENT_TYPE_VENUE -> {
                ARouter.getInstance()
                    .build(ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY)
                    .withString("id",  bean.relationId)
                    .navigation()
            }
            // ??????(??????)
            ResourceType.CONTENT_TYPE_RESTAURANT -> {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_FOOD_DETAIL)
                    .withString("id",  bean.relationId)
                    .navigation()
            }
            // ??????
            ResourceType.CONTENT -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                    .withString("id",  bean.relationId)
                    .withString("contentTitle", "????????????")
                    .navigation()
            }
            // ??????
            ResourceType.CONTENT_TYPE_STORY -> {
//                ARouter.getInstance()
//                    .build(MainARouterPath.MAIN_STRATEGY_DETAIL)
//                    .withString("id",  bean.relationId)
//                    .withInt("type", 1)
//                    .navigation()

                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_STORY_DETAIL)
                    .withString("id",  bean.relationId)
                    .withInt("type", 1)
                    .navigation()
            }
            // ?????????
            ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_HAPPINESS_DETAIL)
                    .withString("id",  bean.relationId)
                    .navigation()
            }
            // ????????????
            ResourceType.CONTENT_TYPE_BRAND -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_DETAIL)
                    .withString("id",  bean.relationId)
                    .navigation();
            }
            // ?????????
            ResourceType.CONTENT_TYPE_COURSE->{
                MainARouterPath.toLectureHallDetail( bean.relationId)
            }
            // ??????
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
            // ????????????
            ResourceType.CONTENT_TYPE_COUNTRY -> {
                ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_COUNTRY_DETAIL_ACTIVITY)
                    .withString("id",  bean.relationId)
                    .navigation();
            }
            // ???????????????
            ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE,ResourceType.CONTENT_TYPE_HERITAGE_TEACHING_BASE ->
                ARouter.getInstance()
                    .build(ARouterPath.LegacyModule.LEGACY_EXPERIENCE_DETAIL_ACTIVITY)
                    .withString("id", bean.relationId)
                    .withString("type",bean.resourceType)
                    .navigation()
            // ???????????????
            ResourceType.CONTENT_TYPE_HERITAGE_ITEM ->
                ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_Smrity_DETAIL_ACTIVITY)
                    .withString("id", bean.relationId)
                    .navigation()
            // ??????????????????
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
                var url :String=ShareModel.getVoteFengCai( bean.relationId)
                    ARouter.getInstance()
                        .build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString("mTitle","????????????")
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



