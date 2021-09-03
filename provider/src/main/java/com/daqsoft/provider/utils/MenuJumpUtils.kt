package com.daqsoft.provider.utils

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.*
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.base.WebSiteAouter
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.utils.dialog.ProviderAppointDialog
import java.lang.Exception
import com.daqsoft.provider.view.dialog.ProviderCodeDialog
import com.daqsoft.provider.zxing.CaptureActivity.show

/**
 * @Author：      邓益千
 * @Create by：   2020/4/29 11:07
 * @Description： TOP菜单栏地址配置
 * @update luoyi 修改为整体修改未静态工具类
 */
object MenuJumpUtils {

    //火车城市查询
    const val SERVICE_TRAIN = "service_train"

    //长途汽车城市查询
    const val SERVICE_SUBWAY = "service_subway"

    //飞机城市查询
    const val SERVICE_PLANE = "service_plane"

    /**
     * 功能菜单页面跳转工具类
     *
     */
    fun menuPageJumpUtils(
        item: HomeMenu,
        siteId: String? = "",
        region: String? = "",
        childFragmentManager: FragmentManager? = null
    ) {
        // 跳转网页
        deaJumpOperation(
            item.jumpType,
            item.externalLink,
            item.name,
            item.menuCode,
            siteId,
            childFragmentManager,
            region
        )
    }

    /**
     * 新版菜单跳转
     */
    fun menuPageJumpUtils(
        item: CommonTemlate,
        siteId: String? = "",
        region: String? = "",
        childFragmentManager: FragmentManager? = null
    ) {
        if (item.resourceId > 0 && !item.resourceType.isNullOrEmpty()) {
            gotoResourceDetail(item.resourceType!!, item.resourceId.toString())
        } else {
            // 跳转网页
            deaJumpOperation(
                item.jumpType,
                item.externalLink,
                item.name,
                item.menuCode,
                siteId,
                childFragmentManager,
                region
            )
        }
    }

    /**
     * 运营专区广告跳转
     */
    fun menuPageJumpUtils(
        item: OperationTemplate,
        siteId: String? = "",
        region: String? = "",
        childFragmentManager: FragmentManager? = null
    ) {
        if (!item.resourceName.isNullOrEmpty() && !item.resourceValue.isNullOrEmpty() && !item.resourceType.isNullOrEmpty()) {
            gotoResourceDetail(item.resourceType!!, item.resourceValue!!)
        } else {
            // 跳转网页
            deaJumpOperation(
                item.jumpType,
                item.externalLink,
                "",
                item.menuCode,
                siteId,
                childFragmentManager,
                region
            )
        }
    }

    /**
     * 新版菜单跳转
     */
    fun servicePageJumpUtils(
        item: CommponentDetail,
        siteId: String? = "",
        region: String? = "",
        childFragmentManager: FragmentManager? = null,
        context: Context? = null
    ) {
        // 跳转网页
        deaJumpOperation(
            item.jumpType,
            item.externalLink,
            item.name,
            item.menuCode,
            siteId,
            childFragmentManager,
            region,
            context
        )
    }

    private fun deaJumpOperation(
        jumpType: String?, externalLink: String?, name: String?, menuCode: String?,
        siteId: String?,
        childFragmentManager: FragmentManager?,
        region: String?,
        context: Context? = null
    ) {
        if (jumpType.isNullOrEmpty() && menuCode.isNullOrEmpty()) {
            return
        }
        if (!jumpType.isNullOrEmpty() && jumpType.equals("2")) {
            var url: String? = externalLink
            if (!url.isNullOrEmpty()) {
                if (url.contains("c.jkxds.net")) {
                    // 小电商地址
                    var uuid = SPUtils.getInstance().getString(SPKey.UUID)
                    url = StringUtil.getShopUrl(url, uuid)
                } else {
                    url = StringUtil.getWeChatUrl(url)
                }
            }
            ARouter.getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("mTitle", name)
                .withString("html", url)
                .navigation()
            return
        }
        // 跳转地址
        var path: String = ""
        when (menuCode) {
            "VENUE" -> {
                // 文化馆
                path = ARouterPath.VenuesModule.VENUES_LIST_ACTIVITY
            }
            "SCENIC" -> {
                // 景区
                path = MainARouterPath.MAINE_SCENIC_LIST
            }
            "HOTEL" -> {
                // 酒店
                path = ZARouterPath.ZMAIN_HOTEL_LIST
            }
            "REGION_RANK"->{
                path = ZARouterPath.RED_BLACK_AREA_LIST
            }
            "FOOD" -> {
                // 美食
                path = MainARouterPath.MAIN_FOOD_LS
            }
            "ENTERTAIN"->{
                path = MainARouterPath.MAIN_PLAYGROUND_LS
            }
            "ENTERTAIN_DETAIL"->{
                path = MainARouterPath.MAIN_PLAYGROUND_DETAIL
            }
            "STUDIES"->{
                path =MainARouterPath.RESEARCH_BASELIST
            }
            // 特产跳转
            "SPECIALTY"->{
                path =MainARouterPath.SPECIAL_BASELIST
            }

            // 出行服务
            "CAR_RENTAL" -> {
                val url = BaseApplication.webSiteUrl+"use-car"
                ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", name)
                    .withString("html", url)
                    .navigation()
                return
            }

            "COUNTRY_TOUR" -> {
                // 乡村游
                if (BaseApplication.appArea == "xj" && !BaseApplication.isDebug) {
                    ARouter.getInstance()
                        .build(ARouterPath.CountryModule.COUNTRY_CITY_LIST_ACTIVITY)
                        .withString(
                            "siteId",
                            SPUtils.getInstance().getString(SPKey.SITE_ID)
                        ).navigation()
                } else {
                    path = ARouterPath.CountryModule.COUNTRY_TOUR_LIST
                    ARouter.getInstance()
                        .build(path)
                        .withString("jumpSiteId", siteId)
                        .navigation()
                }


                return
            }
            "SCHOOL" -> {
                MainARouterPath.toLectureHallLs()
                return
            }
            "ROBOT" -> {
                MainARouterPath.toItRobotPage()
                return
            }
            "FARMHOUSE" -> {
                // 农家乐
                path = ARouterPath.CountryModule.COUNTRY_HAPPINESS_LIST
            }
            "COMMON", "DEFAULT_SERVICE" -> {
                // 享服务
                if (BaseApplication.appArea == "sc") {
                    path = ARouterPath.ServiceModule.SERVICE_ACTIVITY
                } else if (BaseApplication.appArea == "xj") {
                    path = ARouterPath.ServiceModule.SERVICE_XJ_ACTIVITY
                }

            }
            "USER_VOTE" -> {
                // 投票
                if (AppUtils.isLogin()) {
                    ARouter.getInstance().build(ARouterPath.VoteModule.MINE_VOTE)
                        .navigation()
                } else {
                    ToastUtils.showUnLoginMsg()
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }
                return
            }
            "VOTE_LIST" -> {
//                if (AppUtils.isLogin()) {
                ARouter.getInstance().build(ARouterPath.VoteModule.VOTE_LS)
                    .navigation()
//                } else {
//                    ToastUtils.showUnLoginMsg()
//                    ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
//                        .navigation()
//                }
                return

            }
            "TOILET" -> {
                // 找厕所
                path = MainARouterPath.MAIN_SIDE_TOUR
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_SIDE_TOUR)
                    .withInt("TAB_POS", 0)
                    .navigation()
                return
            }
            "COMPLAINT" -> {
                // 投诉
                path = MainARouterPath.MAIN_COMPLAINT_ACTIVITY
//                ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
//                    .withString("html", BaseApplication.complaintUrl)
//                    .navigation()
//                return
            }
            "ACTIVITY" -> {
                // 活动
                path = MainARouterPath.MAIN_ACTIVITY_INDEX
            }
            "GUIDE_TOUR" -> {
                // 导游导览
                ARouter.getInstance()
                    .build(ARouterPath.GuideModule.GUIDE_SCENIC_LIST_ACTIVITY)
                    .navigation()
                return
            }
            "DESTINATION" -> {
                // 目的城市
                ARouter.getInstance().build(MainARouterPath.MAIN_MDD_LIST).navigation()
                return
            }

            "LIVE", "SLOW_LIVE" -> {
                // 直播列表
                ARouter.getInstance()
                    .build(ARouterPath.SlowLiveModule.SLOW_LIVE_LIST_ACTIVITY)
                    .navigation()
                return
            }
            "PARKING" -> {
                // 停车场
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_SIDE_TOUR)
                    .withInt("TAB_POS", 1)
                    .navigation()
                return
            }
            "ASK_FOR_HELP" -> {
                // 求助
                ARouter.getInstance().build(ARouterPath.ServiceModule.SERVICE_SOS_ACTIVITY).navigation()
                return
            }
            "GUIDE" -> {
                // 导游
                ARouter.getInstance()
                    .build(ARouterPath.ServiceModule.SERVICE_TOUR_GUIDE_ACTIVITY)
                    .navigation()
                return
            }
            "AGENCY" -> {
                // "旅行社"
                ARouter.getInstance()
                    .build(ARouterPath.ServiceModule.SERVICE_TRAVEL_AGENCY_LIST_ACTIVITY)
                    .navigation()
                return
            }
            "BUS_INQUIRY" -> {
                // 公交查询
                if (BaseApplication.appArea.equals("sc")) {
                    showTipsDialog(1, context)
                } else {
                    ARouter.getInstance()
                        .build(ARouterPath.ServiceModule.SERVICE_QUERY_BUS_ACTIVITY)
                        .navigation()
                }
                return
            }
            "TRAIN_INQUIRY" -> {
                //  "火车票"
                if (BaseApplication.appArea.equals("sc")) {
                    showTipsDialog(2, context)
                } else {
                    ARouter.getInstance()
                        .build(ARouterPath.ServiceModule.SERVICE_QUERY_TRAIN_ACTIVITY)
                        .withString("queryTraffic", SERVICE_TRAIN)
                        .navigation()
                }
                return
            }
            "TICKET_INQUIRY" -> {
                //  "汽车票"
                if (BaseApplication.appArea.equals("sc")) {
                    showTipsDialog(3, BaseApplication.context)
                } else {
                    ARouter.getInstance()
                        .build(ARouterPath.ServiceModule.SERVICE_QUERY_TRAIN_ACTIVITY)
                        .withString("queryTraffic", SERVICE_SUBWAY)
                        .navigation()
                }
                return
            }

            "AIRCRAFT_INQUIRY" -> {
                // 机票
                // "机票"
                if (BaseApplication.appArea.equals("sc")) {
                    showTipsDialog(4, context)
                } else {
                    ARouter.getInstance()
                        .build(ARouterPath.ServiceModule.SERVICE_QUERY_TRAIN_ACTIVITY)
                        .withString("queryTraffic", SERVICE_PLANE)
                        .navigation()
                }
                return
            }
            "SCANME" -> {
                // 弹窗提示
                childFragmentManager?.let {
                    ProviderCodeDialog().run {
                        url = WebSiteAouter.TAKE_PHOTO_TO__FLOWERS
                        this.name = name
                        show(it, "")
                    }
                }
                return
            }
            "AIR_QUALITY" -> {
                // 空气质量
                ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", name)
                    .withString("html", BaseApplication.webSiteUrl + WebSiteAouter.AIR_QUALITY)
                    .navigation()
                return
            }
            "CAR_RENTAL" -> {
                // 租车
                ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", name)
                    .withString("html", BaseApplication.webSiteUrl + WebSiteAouter.USE_CAR)
                    .navigation()
                return
            }
            "HEALTH_CODE" -> {
                // 健康码
                childFragmentManager?.let {
                    ProviderCodeDialog().run {
                        url = WebSiteAouter.HEALTH_CODE_URL
                        this.name = "健康码"
                        show(it, "")
                    }
                }
                return
            }
            "ART_FUND" -> {
                // 艺术基金
                ARouter.getInstance()
                    .build(ARouterPath.ServiceModule.SERVICE_ART_FOUND_ACTIVITY)
                    .navigation()
                return
            }
            "HOT" -> {
                // 网红打卡
                ARouter.getInstance()
                    .build(ARouterPath.OnLineClickModule.ONLINE_CLICK_LIST)
                    .navigation()
                return
            }
            "TIME" -> {
                // 时光
                path = MainARouterPath.MAIN_TIME
            }
            "PANORAMIC" -> {
                // 720全景漫游
                ARouter.getInstance()
                    .build(MainARouterPath.PANORAMIC_LIST)
                    .navigation()
                return
            }
            "INTEGRAL" -> {
                // 积分模块
                if (AppUtils.isLogin()) {
                    ARouter.getInstance().build(ARouterPath.IntegralModule.MEMBER_HOME_ACTIVITY)
                        .navigation()
                } else {
                    ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }
                return
            }

            "INTANGIBLE" -> {
//                if (AppUtils.isLogin()) {
                ARouter.getInstance()
                    .build(ARouterPath.LegacyModule.LEGACY_HOME_ACTIVITY)
                    .navigation()
//                } else {
//                    ToastUtils.showUnLoginMsg()
//                    ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
//                        .navigation()
//                }
                return
            }
            // 非遗传承
            "INTANGIBLE_SUCCESSOR" -> {
                ARouter.getInstance()
                    .build(ARouterPath.LegacyModule.LEGACY_Smrity_ACTIVITY)
                    .withInt("tabIndex", 1)
                    .navigation()
                return
            }
            // 体验基地
            "PROJECT_EXPERIENCE_BASE" -> {
                ARouter.getInstance()
                    .build(ARouterPath.LegacyModule.LEGACY_Smrity_ACTIVITY)
                    .withInt("tabIndex", 2)
                    .navigation()
                return
            }
            // 传习基地
            "MISSION_PROTECTION_BASE" -> {
                ARouter.getInstance()
                    .build(ARouterPath.LegacyModule.LEGACY_Smrity_ACTIVITY)
                    .withInt("tabIndex", 3)
                    .navigation()
                return
            }
            // 非遗项目
            "INTANGIBLE_PROJECT" -> {
                ARouter.getInstance()
                    .build(ARouterPath.LegacyModule.LEGACY_Smrity_ACTIVITY)
                    .withInt("tabIndex", 0)
                    .navigation()
                return
            }
            "QUESTIONNAIRE" -> {
                ARouter.getInstance().build(MainARouterPath.MINE_QUESTION_REQ)
                    .navigation()
                return
            }
            // 精品文物
            "COLLECTION" -> {
                ARouter.getInstance().build(MainARouterPath.COLLECT_CULYURE_LIST)
                    .navigation()
                return
            }

            // 陈列展览
            "EXHIBITION" -> {
                ARouter.getInstance().build(MainARouterPath.COLLECT_SHOW_LIST)
                    .navigation()
                return
            }
            "RAIDERS" -> {
                // 查攻略
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_STRATEGY_FIND)
                    .navigation()
                return
            }
            "ITINTERARY" -> {
                // 智能行程
                if (AppUtils.isLogin()) {
                    ARouter.getInstance()
                        .build(ItineraryRouter.ITINERARY_LIST)
                        .navigation()
                } else {
                    ToastUtils.showUnLoginMsg()
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }
                return
            }
            "TOPIC" -> {
                // 话题
                path = MainARouterPath.MAIN_TOPIC_LIST
            }
            "COMMUNITY" -> {
                // 社团
                path = MainARouterPath.MAIN_CLUB
            }
            "APP_ATTEND_SHOW" -> {
                // 看演出
                path = MainARouterPath.WATCH_SHOW
            }
            "VOLUNTEER" -> {
                // 志愿服务
                if (AppUtils.isLogin()) {
                    ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString(
                            "html", StringUtil.getHttpsUrl("http://" + SPUtils.getInstance().getString(SPKey.SITE_CODE) + ".c.dsichuan.com/#/volunteer-index")
                        )
                        .withString("mTitle", "志愿服务")
                        .navigation()
                } else {
                    ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }
                return
            }
            "FEEDBACK" -> {
                // 一键反馈
                if (AppUtils.isLogin()) {
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_FEED_BACK_ACTIVITY)
                        .navigation()
                } else {
                    ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }
                return
            }
            // 我的预约
            "BOOKING" -> {
                if (!isGotoLogin()) {
                    if (BaseApplication.appArea == "sc") {
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.USER_APPOINTMENT_LIST)
                            .withString("type", ResourceType.CONTENT_TYPE_VENUE)
                            .navigation()
                    } else {
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.USER_APPOINTMENT_LIST)
                            .withString("type", ResourceType.CONTENT_TYPE_VENUE)
                            .navigation()
                    }
                }
                return
            }
            // 我的活动
            "MY_ACTIVITY" -> {
                if (!isGotoLogin()) {
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_ORDER_LIST)
                        .withString("type", ResourceType.CONTENT_TYPE_ACTIVITY)
                        .navigation()
                }
                return
            }
            // 消费码
            "CONSUMPTION_CODE" -> {
                if (!isGotoLogin()) {
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_CONSUME_LIST_ACTIVITY)
                        .navigation()
                }
                return
            }
            // 加油站
            "GAS_STATION" -> {
                if (!isGotoLogin()) {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_SIDE_TOUR)
                        .withInt("TAB_POS", 2)
                        .withBoolean("isService", true)
                        .navigation()
                }
                return
            }
            // 商城
            "MALL" -> {
                if (!isGotoLogin()) {
                    val shopUrl = SPUtils.getInstance().getString(SPKey.SHOP_URL)
                    if (!shopUrl.isNullOrEmpty()) {
                        ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY).withString("mTitle", "商城")
                            .withString("html", shopUrl).navigation()
                    }
                }
                return
            }
            // 全部订单
            "ORDERS" -> {
                if (!isGotoLogin()) {
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_ELECTRONIC_ORDERS)
                        .navigation()
                }
                return
            }
        }
        if (!path.isNullOrEmpty()) {
            if (!siteId.isNullOrEmpty()) {
                // 传递siteid时候，注意 siteid传递类型
                ARouter.getInstance()
                    .build(path)
                    .withString("siteId", siteId)
                    .withString("region", region)
                    .navigation()
            } else {
                ARouter.getInstance()
                    .build(path)
                    .navigation()
            }

        } else {
            ToastUtils.showMessage("正在开发，敬请期待")
            /* ARouter.getInstance()
                 .build(ARouterPath.GuideModule.GUIDE_SCENIC_LIST_ACTIVITY)
                 .navigation()*/
        }
    }

    /**
     * @author luoyi
     * 跳转投诉界面
     */
    fun jumpComplaintPage() {
        ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
            .withString(
                "html",BaseApplication.complaintUrl+"?source=app&appToken=" + SPUtils.getInstance().getString(SPUtils.Config.TOKEN)
            )
            .withString("mTitle", "投诉求助")
            .navigation()
    }

    /**
     * 跳转到我的投诉
     */
    fun gotoComplaint() {
        var url: String = "https://mucomplain.12301.cn/view/complaintmobile#/valid"
        ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
            .withString("mTitle", "我的投诉")
            .withString("html", url)
            .navigation()
    }

    /**
     * 跳转评论页面
     * @param resourceId 资源Id
     * @param resourceType 资源类型
     */
    fun gotoCommentPage(
        resourceType: String,
        resourceId: String,
        title: String? = "",
        orderId: String = ""
    ) {
        if (resourceType.isNullOrEmpty()) {
            return
        }
        when (resourceType) {
            ResourceType.CONTENT_TYPE_VENUE -> {
                ARouter.getInstance()
                    .build(ARouterPath.Provider.PROVIDER_ORDER_COM_ACTIVITY)
                    .withString("id", resourceId)
                    .withString("type", resourceType)
                    .withString("orderId", orderId)
                    .navigation()
            }

            // 2020/9/25 修改
            ResourceType.CONTENT_TYPE_SCENERY -> {
                ARouter.getInstance().build(ARouterPath.Provider.PROVIDER_POST_COMMENT)
                    .withString("id", resourceId)
                    .withString("type", resourceType)
                    .withString("contentTitle", title)
                    .navigation()
            }

//            ResourceType.CONTENT_TYPE_SCENERY,
            ResourceType.CONTENT_TYPE_HOTEL, ResourceType.CONTENT_TYPE_RESTAURANT, ResourceType.CONTENT_TYPE_ACTIVITY -> {
                ARouter.getInstance().build(ARouterPath.Provider.PROVIDER_ORDER_COM_ACTIVITY)
                    .withString("id", resourceId)
                    .withString("type", resourceType)
                    .withString("contentTitle", title)
                    .withString("orderId", orderId)
                    .navigation()
            }
        }

    }

    /**
     * 是否跳转到登录
     */
    fun isGotoLogin(): Boolean {
        if (!AppUtils.isLogin()) {
            ToastUtils.showMessage("非常抱歉，登录后才能访问~")
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                .navigation()
            return true
        }
        return false
    }

    /**
     * 根据资源类型跳转到详情页面
     */
    fun gotoResourceDetail(type: String, id: String, activityType: String? = "") {
        var path = ""
        when (type) {
            // 场馆
            ResourceType.CONTENT_TYPE_VENUE -> {
                path = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY
            }
            // 农家乐
            ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                path = ARouterPath.CountryModule.COUNTRY_HAPPINESS_DETAIL
            }
            // 	活动室
            ResourceType.CONTENT_TYPE_ACTIVITY_SHIU -> {
                path = ARouterPath.VenuesModule.ACTIVITY_ROOM_DETAIL
            }
            ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE -> {
                // 非遗基地
                path = ARouterPath.LegacyModule.LEGACY_EXPERIENCE_DETAIL_ACTIVITY
            }
            // 酒店
            ResourceType.CONTENT_TYPE_HOTEL -> {
                path = ZARouterPath.ZMAIN_HOTEL_DETAIL
            }
            // 景区
            ResourceType.CONTENT_TYPE_SCENERY -> {
                path = MainARouterPath.MAIN_SCENIC_DETAIL
            }
            // 景点
            ResourceType.CONTENT_TYPE_SCENIC_SPOTS -> {
                path = MainARouterPath.MAIN_SCENIC_SPOT_DETAI
            }
            // 餐饮
            ResourceType.CONTENT_TYPE_RESTAURANT -> {
                path = MainARouterPath.MAIN_FOOD_DETAIL
            }
            // 乡村
            ResourceType.CONTENT_TYPE_COUNTRY -> {
                path = ARouterPath.CountryModule.COUNTRY_COUNTRY_DETAIL_ACTIVITY
            }
            // 景观点
            ResourceType.CONTENT_TYPE_RURAL_SPOTS -> {
                path = ARouterPath.CountryModule.COUNTRY_SCENIC_SPOT_ACTIVITY
            }
            // 活动
            ResourceType.CONTENT_TYPE_ACTIVITY -> {
                toActivityDetail(id, activityType)
                return
            }
            // 品牌
            ResourceType.CONTENT_TYPE_BRAND -> {
                path = MainARouterPath.MAIN_BRANCH_DETAIL
            }
            // 话题详情
            ResourceType.CONTENT_TYPE_TOPIC -> {
                path = MainARouterPath.MAIN_TOPIC_DETAIL
            }
            // 故事详情
            ResourceType.CONTENT_TYPE_STORY -> {
                path = MainARouterPath.MAIN_STORY_DETAIL
                ARouter.getInstance().build(path)
                    .withString("id", id)
                    .withInt("type", 1)
                    .navigation()
                return
            }
            // 攻略详情
            ResourceType.CONTENT_TYPE_STRATEGY -> {
                path = MainARouterPath.MAIN_STRATEGY_DETAIL
                ARouter.getInstance().build(path)
                    .withString("id", id)
                    .withInt("type", 1)
                    .navigation()
                return
            }
            ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE -> {
                path = ARouterPath.LegacyModule.LEGACY_EXPERIENCE_DETAIL_ACTIVITY
                ARouter.getInstance().build(path)
                    .withString("id", id)
                    .withString("type", type)
                    .navigation()
                return
            }
            ResourceType.CONTENT_TYPE_HERITAGE_PROTECT_BASE -> {

                path = ARouterPath.LegacyModule.LEGACY_EXPERIENCE_DETAIL_ACTIVITY
                ARouter.getInstance().build(path)
                    .withString("id", id)
                    .withString("type", type)
                    .navigation()
                return
            }
            ResourceType.CONTENT_TYPE_HERITAGE_TEACHING_BASE -> {
                path = ARouterPath.LegacyModule.LEGACY_EXPERIENCE_DETAIL_ACTIVITY
                ARouter.getInstance().build(path)
                    .withString("id", id)
                    .withString("type", type)
                    .navigation()
                return
            }
            ResourceType.CONTENT_TYPE_ENTERTRAINMENT -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_PLAYGROUND_DETAIL)
                    .withString("id", id)
                    .navigation()
                return
            }
            "ENTERTAINMENT_DETAIL" -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_PLAYGROUND_DETAIL)
                    .withString("id", id)
                    .navigation()
                return
            }
            ResourceType.CONTENT_TYPE_ENTERTRAINMENT -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_PLAYGROUND_DETAIL)
                    .withString("id", id)
                    .navigation()
                return
            }
            // 特产
            ResourceType.CONTENT_TYPE_SPECIALTY -> {
                path = MainARouterPath.MAIN_SPECIAL_DETAIL
            }
            ResourceType.CONTENT_TYPE_RESEARCH_BASE -> {
                path = MainARouterPath.RESEARCH_DETAIL
            }
            else -> {
                ToastUtils.showMessage("功能开发中，敬请期待!")
            }
        }
        if (!path.isNullOrEmpty())
            ARouter.getInstance().build(path)
                .withString("id", id)
                .navigation()
    }

    /**
     * 广告位跳转
     */
    fun adJump(adInfo: AdInfo) {
        if (!adInfo.jumpUrl.isNullOrEmpty()) {
            var jumpUrl: String? = adInfo.jumpUrl
            if (!adInfo.resourceType.isNullOrEmpty()) {
                if (adInfo.resourceType == "GOODS") {
                    if (AppUtils.isLogin()) {
                        var uuid = SPUtils.getInstance().getString(SPKey.UUID)
                        jumpUrl = StringUtil.getShopUrl(jumpUrl, uuid)
                    } else {
                        ToastUtils.showUnLoginMsg()
                        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                            .navigation()
                        return
                    }
                } else if (adInfo.resourceType == "OUTSIDE" && jumpUrl != null && jumpUrl.endsWith("feedback")) {
                    if (AppUtils.isLogin()) {
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.USER_FEED_BACK_ACTIVITY)
                            .navigation()
                    } else {
                        ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                            .navigation()
                    }
                    return
                }
            }
            ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("html", StringUtil.getWeChatUrl(jumpUrl!!))
                .navigation()
            return
        }
        if (!adInfo.resourceId.isNullOrEmpty() && !adInfo.resourceType.isNullOrEmpty()) {
            gotoResourceDetail(adInfo.resourceType, adInfo.resourceId)
            return
        }

        ToastUtils.showMessage("跳转地址无效!")
    }

    /**
     * 广告位跳转
     */
    fun adJump(adInfo: OperationTemplate) {
        if (!adInfo.externalLink.isNullOrEmpty()) {
            var jumpUrl: String? = adInfo.externalLink
            if (!adInfo.resourceType.isNullOrEmpty()) {
                if (adInfo.resourceType == "GOODS") {
                    if (AppUtils.isLogin()) {
                        var uuid = SPUtils.getInstance().getString(SPKey.UUID)
                        jumpUrl = StringUtil.getShopUrl(jumpUrl, uuid)
                    } else {
                        ToastUtils.showUnLoginMsg()
                        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                            .navigation()
                        return
                    }
                }
            }
            ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("html", StringUtil.getWeChatUrl(jumpUrl!!))
                .navigation()
            return
        }
        if (!adInfo.resourceValue.isNullOrEmpty() && !adInfo.resourceType.isNullOrEmpty()) {
            gotoResourceDetail(adInfo.resourceType!!, adInfo.resourceValue!!)
            return
        }

        ToastUtils.showMessage("跳转地址无效!")
    }

    /**
     * 广告位跳转
     */
    fun adJump(adInfo: CommonTemlate) {
        if (!adInfo.jumpUrl.isNullOrEmpty()) {
            var jumpUrl: String? = adInfo.jumpUrl
            if (!adInfo.resourceType.isNullOrEmpty()) {
                if (adInfo.resourceType == "GOODS") {
                    if (AppUtils.isLogin()) {
                        var uuid = SPUtils.getInstance().getString(SPKey.UUID)
                        jumpUrl = StringUtil.getShopUrl(jumpUrl, uuid)
                    } else {
                        ToastUtils.showUnLoginMsg()
                        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                            .navigation()
                        return
                    }
                }
            }
            ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("html", StringUtil.getWeChatUrl(jumpUrl!!))
                .navigation()
            return
        }
        if (adInfo.resourceId != 0 && !adInfo.resourceType.isNullOrEmpty()) {
            gotoResourceDetail(adInfo.resourceType!!, adInfo.resourceId.toString())
            return
        }

        ToastUtils.showMessage("跳转地址无效!")
    }


    /**
     * 跳转资源页面
     * @author luoyi
     * @param resourceType 资源类型
     * @param resourceId 资源Id
     */
    fun jumpResourceTypePage(resourceType: String?, resourceId: String, jumpUrl: String? = "") {
        jumpResourceTypePage(resourceType, resourceId, "", jumpUrl)
    }

    /**
     * 跳转资源页面
     *  * @author luoyi
     * @param resourceType 资源类型
     * @param resourceId 资源Id
     * @param siteId 此处是位置id
     */
    fun jumpResourceTypePage(
        resourceType: String?,
        resourceId: String,
        siteId: String,
        jumpUrl: String? = ""
    ) {
        jumpResourceTypePage(resourceType, resourceId, siteId, "", jumpUrl)
    }

    /**
     * 跳转资源页面
     * @author luoyi
     * @param resourceType 资源类型
     * @param resourceId 资源Id
     * @param siteId 此处是位置id
     * @param contentType 内容类型
     */
    fun jumpResourceTypePage(
        resourceType: String?,
        resourceId: String,
        siteId: String,
        contentType: String,
        jumpUrl: String? = ""
    ) {

        when (resourceType) {
            // 酒店
            ResourceType.CONTENT_TYPE_HOTEL -> {
                try {
                    ARouter.getInstance().build(ZARouterPath.ZMAIN_HOTEL_DETAIL)
                        .withString("id", resourceId)
                        .navigation()
                } catch (e: Exception) {
                }

            }
            // 活动
            ResourceType.CONTENT_TYPE_ACTIVITY -> {
                jumpToActivityDetail(resourceId, resourceType, contentType, jumpUrl)
            }
            // 活动室
            ResourceType.CONTENT_TYPE_ACTIVITY_SHIU -> {
                ARouter.getInstance().build(ARouterPath.VenuesModule.ACTIVITY_ROOM_DETAIL)
                    .navigation()
            }
            // 场馆
            ResourceType.CONTENT_TYPE_VENUE -> {
                ARouter.getInstance()
                    .build(ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY)
                    .withString("id", resourceId)
                    .navigation()
            }
            // 乡村游
            ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_HAPPINESS_DETAIL)
                    .withString("id", resourceId)
                    .navigation()
            }
            // 景区
            ResourceType.CONTENT_TYPE_SCENERY -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_SCENIC_DETAIL)
                    .withString("id", resourceId)
                    .navigation()
            }
            ResourceType.CONTENT_TYPE_SCENIC_SPOTS -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_SCENIC_SPOT_DETAI)
                    .withString("id", resourceId)
                    .navigation()
            }
            // 餐饮
            ResourceType.CONTENT_TYPE_RESTAURANT -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_FOOD_DETAIL)
                    .withString("id", resourceId)
                    .navigation()
            }
            // 城市名片详情
            ResourceType.CONTENT_TYPE_SITE_INDEX -> {
                if (!siteId.isNullOrEmpty()) {
                    ARouter.getInstance().build(MainARouterPath.MAIN_MDD_CITY_INFO)
                        .withString("id", siteId)
                        .navigation()
                } else {
                    ToastUtils.showMessage("站点信息异常，请联系管理员")
                }
            }
            // 内容
            ResourceType.CONTENT -> {

            }
            else -> {
                ToastUtils.showInProgressMsg()
                return
            }

        }
    }

    /**
     * 跳转到活动详情
     */
    private fun jumpToActivityDetail(
        resourceId: String,
        resourceType: String?,
        contentType: String?,
        jumpUrl: String?
    ) {
        if (!jumpUrl.isNullOrEmpty()) {
            ARouter.getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("html", jumpUrl)
                .navigation()
        } else {
            when (resourceType) {
                // 志愿活动
                ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_VOLUNTEER_ACTIVITY)
                        .withString("id", resourceId)
                        .navigation()
                }
                // 预订活动
                ActivityType.ACTIVITY_TYPE_RESERVE -> {
                    // 预订
                    when (contentType) {
                        // 付费预订活动
                        ActivityMethod.ACTIVITY_MODE_INTEGRAL_PAY -> {

                            ARouter.getInstance()
                                .build(MainARouterPath.MAIN_ACTIVITY_PAY_ORDER)
                                .withString("jumpUrl", jumpUrl)
                                .navigation()
                        }
                        else -> {
                            ARouter.getInstance()
                                .build(MainARouterPath.MAIN_HOT_ACITITY)
                                .withString("id", resourceId)
                                .navigation()
                        }
                    }
                }
                else -> {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_HOT_ACITITY)
                        .withString("id", resourceId)
                        .navigation()
                }

            }
        }
    }

    /**
     * 跳转资源列表
     */
    fun jumpToResourceList(resourceType: String?, siteId: String = "") {
        var path: String = ""
        when (resourceType) {
            // 酒店
            ResourceType.CONTENT_TYPE_HOTEL -> {
                path = ZARouterPath.ZMAIN_HOTEL_LIST
            }
            // 景区
            ResourceType.CONTENT_TYPE_SCENERY -> {
                path = MainARouterPath.MAINE_SCENIC_LIST
            }
            // 场馆
            ResourceType.CONTENT_TYPE_VENUE -> {
                path = ARouterPath.VenuesModule.VENUES_LIST_ACTIVITY
            }
            // 活动
            ResourceType.CONTENT_TYPE_ACTIVITY -> {
                path = MainARouterPath.MAIN_ACTIVITY_LS
            }
            // 餐饮
            ResourceType.CONTENT_TYPE_RESTAURANT -> {
                path = MainARouterPath.MAIN_FOOD_LS
            }
            // 城市名片
            ResourceType.CONTENT_TYPE_SITE_INDEX -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_MDD_LIST).navigation()
                return
            }
        }
        if (path.isNullOrEmpty()) {
            ToastUtils.showInProgressMsg()
        } else {
            ARouter.getInstance().build(path)
                .navigation()
        }
    }

    fun toActivityDetail(id: String, activityType: String?) {
        when (activityType) {
            // 志愿活动
            ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_VOLUNTEER_ACTIVITY)
                    .withString("id", id)
                    .navigation()
            }
            // 预订活动
            ActivityType.ACTIVITY_TYPE_RESERVE -> {
                // 预订
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_HOT_ACITITY)
                    .withString("id", id)
                    .navigation()
            }
            else -> {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_HOT_ACITITY)
                    .withString("id", id)
                    .navigation()
            }
        }
    }

    /**
     * @time 2020/9/18 新增
     * 显示温馨提示 dialog
     */
    fun showTipsDialog(type: Int, context: Context?) {
        // 提示即将离开此平台
        context?.let {
            ProviderAppointDialog(it).apply {
                onProviderAppointClickListener = object : ProviderAppointDialog.OnProviderAppointClickListener {
                    override fun onProviderAppointClick() {
                        var url = ""
                        var title = ""
                        when (type) {
                            1 -> {
                                // 公交
                                url = "https://m.8684.cn/chengdu_bus"
                                title = "公交"
                            }
                            2 -> {
                                // 火车
                                url = "https://m.ctrip.com/webapp/train"
                                title = "火车票"
                            }
                            3 -> {
                                // 汽车
                                url = "https://m.ctrip.com/webapp/bus"
                                title = "汽车票"
                            }
                            4 -> {
                                // 飞机
                                url = "https://m.ctrip.com/html5/flight/swift/index"
                                title = "机票"
                            }
                        }

                        ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                            .withString("mTitle", title)
                            .withString("html", url)
                            .navigation()
                    }
                }
                setTips("您即将离开智游天府平台，跳转第三方平台")
                show()
            }
        }

    }
}