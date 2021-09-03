package com.daqsoft.provider.network.venues

/**
 * 文化场馆模块接口Api
 * @author 黄熙
 * @date 2019/12/25 0025
 * @version 1.0.0
 * @since JDK 1.8
 */
object VenuesApi {

    /**
     * 文化场馆列表
     */
    const val VENUES_LIST = "res/api/venue/getApiVenueList"

    /**
     * 文化场馆详情
     */
    const val VENUES_DETAILS = "res/api/venue/getVenueInfo"

    /**
     * 站点下级区域(两层)
     */
    const val SITE_CHILD_REGION = "res/api/region/siteChildRegion"

    /**
     *  获取筛选资源
     */
    const val VENUES_DICTTYPE = "config/api/dict/dictType"

    /**
     * 收藏文化馆
     */
    const val VENUES_COLLECT = "res/api/interactManage/collection"

    /**
     * 取消收藏
     */
    const val VENUES_CANCE_COLLECT = "res/api/interactManage/cancelCollection"
    /**
     * 获取地图资源
     */
    const val VENUES_MAP_RES = "res/api/venue/getMapResourceInfo"

    /**
     * 故事列表
     */
    const val VENUES_STORES_LIST = "res/api/story/list"

    /**
     * 场馆预定时间信息
     */
    const val VENUE_ORDER_DATE_LIST = "res/api/venue/orderDateList"
    /**
     * 景区预约时间信息
     */
    const val SCENIC_ORDER_DATE_LIST = "res/api/scenic/orderDateList"
    /**
     * 场馆日期预定数量
     */
    const val VENUE_ORDER_DATE_NUM = "act/api/activityOrderExtend/venueOrderDateNum"
    /**
     * 景区预订日期数量
     */
    const val SCENIC_ORDER_DATE_NUM = "act/api/activityOrderExtend/scenicOrderDateNum"
    /**
     * 场馆预订详情
     */
    const val VENUE_ORDER_VIEW = "res/api/venue/orderViewInfo"
    /**
     * 景区预约详情
     */
    const val SCENIC_ORDER_VIEW = " res/api/scenic/orderViewInfo"
    /**
     * 场馆预约时间段
     */
    const val VENUE_ORDER_TIMES = "res/api/venue/venueOrderTimes"
    /**
     * 景区时段
     */
    const val SCENIC_ORDER_TIMES = "res/api/scenic/scenicOrderTimes"

    /**
     * 场馆预订生成订单
     */
    const val VENUE_GENER_ORDER = "act/api/activityOrder/generateOrderSM4"

    /**
     * 场馆检查手机号是否存在游订单
     */
    const val VENUE_CHECK_ORDER_PHONE_NUMBER = "act/api/activityOrder/existOrderNum"
    /**
     * 发送手机验证码
     */
    const val VENUE_ORDER_SEND_CODE = "act/api/activityOrder/sendSmsCode"
    /**
     * 资讯列表
     */
    const val VENUE_CONTENT_LS = "res/api/content/list"

    /**
     * 资讯列表(研学基地)
     */
    const val STUDY_CONTENT_LS = "res/api/content/simplifyList"
    /**
     * 获取用户信息
     */
    const val GET_VIP_INFO = "res/api/realNameAuth/getRealNameInfoUseEbc"
    /**
     * 是否已存在预约过的讲解员订单
     */
    const val VENUE_GUIDE_ORDER = "act/api/activityOrderExtend/existVenueGuideOrder"
    /**
     * 已预约订单信息
     */
    const val VENUE_GUIDE_ORDER_AND_VENUE_ORDER = "act/api/activityOrderExtend/existOrder"
    /**
     * 讲解员预订信息
     */
    const val VENUE_GUIDE_INFO = "res/api/venue/guideInfo"
    /**
     * 获取健康信息
     */
    const val GET_USER_HELATH_INFO = "user/api/healthyTravel/getHealthInfo2"
    /**
     * 获取健康信息，并注册
     */
    const val GET_USER_HELATH_INFO_AND_REGISTER =
        "user/api/healthyTravel/getHealthInfoAndRegisterSmartTravelCode2"
    /**
     * 获取健康出行配置信息
     */
    const val GET_HELATH_SETING_INFO = "user/api/healthyTravel/getConfig"
    /**
     * 获取健康地址西悉尼
     */
    const val GET_HELATH_REGION_INFO = "user/api/healthyTravel/dockingCityList"

    /**
     * 获取智游天府码
     */
    const val GET_HEAL_TRAVEL_INFO = "user/api/healthyTravel/smartTravelRegisterInquire2"
    /**
     * 用户当日预约列表
     */
    const val GET_SELF_VALID_ORDER_LIST = "act/api/activityOrder/selfValidOrderList"

    /**
     * 下订单地址信息
     */
    const val ODER_ADDRES_INFO = "res/api/scenic/getOrderAddressInfo"
    /**
     * 身份证扫码信息识别
     */
    const val IDCARD_IDENTIFY = "res/api/idcard/idcardIdentify"

    /**
     * 获取订单随行人
     */
    const val ORDER_USER_LIST = "act/api/order/attached/getAttachedListMap"

    /**
     * 获取资源栏目列表
     */
    const val RESOURCE_CHANNEL_LIST = "res/api/content/resourceChannelList"

    /**
     * 社团列表
     */
    const val SOCIETIES_LIST = "res/api/association/getAssociationList"

}