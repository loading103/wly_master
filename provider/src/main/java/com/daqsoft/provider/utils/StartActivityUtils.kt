package com.daqsoft.provider.utils

import android.text.TextUtils
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.MessageListBean
import com.daqsoft.provider.bean.MyNotificationExtra

/**
 * @Description
 * @ClassName   startActivityUtils
 * @Author      luoyi
 * @Time        2021/3/31 10:42
 */
object StartActivityUtils {
    /**
     * 接收到通知消息点击跳转
     */
    fun startNoticeActivity(extra: MyNotificationExtra) {
        var bean=MessageListBean()

        extra.content?.let { bean.content=it }
        extra.relationId?.let { bean.relationId=it }
        extra.title?.let { bean.title=it }
        extra.id?.let { bean.id=it }
        extra.jumpUrl?.let { bean.jumpUrl=it }

        // 志愿者主要字段  messageType    id   relationId   resourceStatus
        if(extra.classify=="4"){
            extra.resourceType?.let { bean.messageType=it }
            extra.type?.let { bean.resourceStatus=it }
        }else{
            extra.jumpType?.let { bean.jumpType=it }
            extra.resourceType?.let { bean.resourceType=it }
        }


        if(extra.classify=="1"){
            when(extra.type){
                // 通知
                "1"->{
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_MEASSAGE_NOTICE_DETAIL)
                        .withString("id",bean.id)
                        .navigation()
                }
                // 活动与邀请
                "2"->{
                    startActivity(bean)
                }
                // 等级变动
                "3"->{
                    // 跳转到积分界面
                    if(bean.resourceType=="POINT_LEVEL"){
                        ARouter.getInstance().build(ARouterPath.IntegralModule.MEMBER_HOME_ACTIVITY)
                            .navigation()
                    }
                    if(bean.resourceType=="CREDIT_LEVEL"){
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.USER_CREDIT_ACTIVITY)
                            .navigation()
                    }
                }
                // 回复
                "4"->{
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_FEED_BACK_LS_ACTIVITY)
                        .navigation()
                }
                // 任务提醒
                "5"->{
                    ARouter.getInstance().build(ARouterPath.IntegralModule.MEMBER_HOME_ACTIVITY)
                        .navigation()
                }
            }
        }else if (extra.classify=="2"){
            when(extra.type){
                // 点赞
                "1"->{
                    startActivity(bean)
                }
                // 收藏
                "2"->{
                    startActivity(bean)
                }
                // 关注
                "3"->{
                    ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_MINE_FANS)
                        .withString("type","fans")
                        .navigation()
                }
                // 问答
                "4"->{
                    ARouter.getInstance()
                        .build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString("mTitle", "问答详情")
                        .withString("html", "${BaseApplication.webSiteUrl}question-detail/${bean.relationId}")
                        .navigation()
                }
                // 作品pk
                "5"->{
                    startActivity(bean)
                }
            }
        }else if (extra.classify=="3") {
            startActivity(bean)
        }else if (extra.classify=="4") {
            ARouter.getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("mTitle", "消息详情")
                .withString("html", bean.getVotPathUrl())
                .navigation()

        }else if (extra.classify=="5") {
            ARouter.getInstance().build(ARouterPath.UserModule.USER_COMPLAINT_DETAILS_ACTIVITY)
                .withString("id",bean.relationId.toString())
                .navigation()
        }else{
            ARouter.getInstance().build(ARouterPath.MainModule.MAIN_ACTIVITY)
                .navigation()
        }


    }
    /**
     * 跳转
     */
    fun startActivity(bean: MessageListBean) {
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
        }
    }
}