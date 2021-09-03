package com.daqsoft.travelCultureModule.net

/**
 * @des 主页app里的api
 * @author PuHua
 * @Date 2019/12/5 14:15
 */
object MainApi {
    /**
     * 品牌展播列表
     */
    const val HOME_BRANCH_LIST = "res/api/brand/getApiBrandList"

    /**
     * 品牌详情
     */
    const val HOME_BRANCH_DETAIL = "res/api/brand/getApiBrandInfo"

    /**
     * 活动分类列表
     */
    const val ACTIVITY_CLASSIFY = "res/api/activity/activityClassifyCount"

    /**
     * 活动日历接口
     */
    const val ACTIVITY_CALENDER = "res/api/activity/activityCalendar"

    /**
     * 获取活动列表
     */
    const val ACTIVITY_LIST = "res/api/activity/getActivityList"

    /**
     * 活动详情
     */
    const val ACTIVITY_DETAIL = "res/api/activity/getActivityInfo"
//    const val  ACTIVITY_DETAIL = "res/api/activity/getActivityInfoByCommodity"
    /**
     * 活动日历
     * https://www.xn--efvu3ql9f.com/api/res/api/activity/getActivityList?orderType=1&currPage=1&classifyId=&pageSize=3&siteCode=site688790
     * https://www.xn--efvu3ql9f.com/api/res/api/activity/getActivityList?orderType=1&pageSize=3&currPage=1&classifyId=
     */
    const val ACTIVITY_CALUENDAR = "res/api/activity/activityClassifyCountCalendar"

    /**
     * 活动栏目
     * api/res/api/activity/getActivityList?orderType=1&pageSize=3&currPage=1&classifyId=
     * api/res/api/activity/getActivityList?orderType=1&currPage=1&classifyId=&pageSize=3&token=&source=android&siteCode=site688790
     */
    const val ACTIVITY_SUBSET = "res/api/content/channelSubset"

    /**
     * 活动关联的资源（活动场地）
     */
    const val ACTIVITY_DETAIL_RELATIONLIST = "res/api/activity/activityRelationList"

    /**
     * 预订活动发送验证码
     */
    const val ACTIVITY_ORDER_SEND_CODE = "act/api/activityOrder/sendSmsCode"

    /**
     * 生成订单
     */
    const val ACTIVITY_ORDER_GENERATE = "act/api/activityOrder/generateOrderSM4"

    /**
     * 查看当前填写的手机号的预订的单数
     */
    const val ACTIVITY_CHECK_EXITST_NUMBER = "act/api/activityOrder/existOrderNum"

    /**
     * 站点下级区域(两层)
     */
    const val SITE_CHILD_REGION = "res/api/region/siteChildRegion"

    /**
     * 获取志愿者团队列表
     */
    const val VOLUNTEER_TEAM_LIST = "res/api/volunteerTeam/list"

    /**
     * 已占用座位列表
     */
    const val USER_SEAT_SELECTED = "act/api/activityOrder/useSeat"

    /**
     * 预订结果简要信息查询
     */
    const val USER_ORDER_CHECK = "act/api/activityOrder/saveResult"

    /**
     * 支付订单
     */
    const val ORDER_PAY = "user/api/activityOrder/pay"

    /**
     * 保存订单
     */
    const val SAVE_ORDER = "act/api/activityOrder/saveGenerateOrder"

    /**
     * 座位模板
     */
    const val SEAT_TEMPLATE = "res/api/resSeatTemplate/view"

    /**
     * 社团列表
     */
    const val CLUB_LIST = "res/api/association/getAssociationList"

    /**
     * 获取搜索分类
     */
    const val SEARCH_TYPE = "config/api/dict/dictType"

    /**
     * 社团详情
     */
    const val CLUB_INFO = "res/api/association/getAssociationInfo"

    /**
     * 社团活动
     */
    const val CLUB_ACTIVITY = "res/api/activity/getActivityList"

    /**
     * 社团资讯
     */
    const val CLUB_ZIXUN = "res/api/content/list"

    /**
     * 看演出
     */
    const val WATCH_SHOW = "res/api/content/channelSubset"

    /**
     * 大讲堂
     */
    const val UNIVERSITY_LS = "res/api/content/channelSubset"

    /**
     * 课程类型
     */
    const val LECTURE_HALL_TYPE = "res/api/schoolCourse/select"

    /**
     * 课程列表
     */
    const val LECTURE_HALL_LIST = "res/api/schoolCourse/getAssociationList"

    /**
     * 课程详情
     */
    const val LECTURE_HALL_DETIALL = "res/api/schoolCourse/getSchoolCourseInfo"

    /**
     * 课程章节
     */
    const val LECTURE_HALL_CHAPTER = "res/api/schoolCourse/listUserChapter"

    /**
     * 课程记录
     */
    const val LECTURE_HALL_RECOUDER = "res/api/schoolCourse/courseRecord"

    /**
     * 课程问题列表
     */
    const val LECTURE_HALL_REQUESTION_LS = "res/api/schoolUser/questionList"

    /**
     * 课程提问
     */
    const val LECUTRE_HALL_REQUEST = "res/api/schoolUser/question"

    /**
     * 我的学习列表
     */
    const val MINE_LECTURE_LIST = "res/api/schoolUser/recordList"

    /**
     * 我的学习-收藏列表
     */
    const val MINE_LECTURE_COLLECT = "res/api/interactManage/vipCollectionList"

    /**
     * 我的学习统计
     */
    const val MINE_LECTURE_RECORD_TOTAL = "res/api/schoolUser/recordCount"

    /**
     * 我的问答列表
     */
    const val MINE_LECUTRE_REQ = "res/api/schoolUser/questionList"

    /**
     * 社团资讯详情
     */
    const val CLUB_ZIXUN_INFO = "res/api/content/detail"

    /**
     * 社团成员
     */
    const val CLUB_PERSON = "res/api/association/getAssociationPersonList"

    /**
     * 社团成员详情
     */
    const val CLUB_PERSON_INFO = "res/api/association/getAssociationPersonInfo"

    /**
     * 获取搜索历史记录
     */
    const val SEARCH_RECORD = "res/api/es/getSearchRecord"

    /**
     * 搜索
     */
    const val SEARCH = "res/api/es/search"

    /**
     * 删除历史记录
     */
    const val SEARCH_DELETE_RECORD = "res/api/es/clearSearchRecord"

    /**
     * 保存历史记录
     */
    const val SEARCH_SAVE_RECORD = "res/api/es/saveSearchRecord"

    /**
     * 景区列表
     */
    const val SCENIC_LIST = "res/api/scenic/getApiScenicList"
    /**
     * 验血基地列表
     */
    const val RESEARCH_LIST = "res/api/researchBase/getApiResearchBaseList"

    /**
     * 特产列表
     */
    const val SPECIAL_LIST = "res/api/speciality/getApiSpecialityList"
    /**
     * 景区直播列表
     */
    const val SCENIC_SPOT_LIVES = " res/api/scenic/getScenicSpotsLive"

    /**
     * 品牌景区玩乐列表
     */
    const val BRAND_SCENIC_LIST = "res/api/brand/getBrandRelationList"

    /**
     * 酒店列表
     */
    const val HOTEL_LIST = "res/api/hotel/getApiHotelList"

    /**
     * 酒店详情
     */
    const val HOTEL_DETAIL = "res/api/hotel/getApiHotelInfo"

    /**
     * 下订单地址信息
     */
    const val ODER_ADDRES_INFO = "res/api/scenic/getOrderAddressInfo"

    /**
     * 美食列表
     */
    const val FOOD_LIST = "res/api/dining/getApiDiningList"

    /**
     * 美食详情
     */
    const val FOOD_DETAIL = "res/api/dining/getApiDiningInfo"

    /**
     * 娱乐场所列表
     */
    const val PLAYGROUNGD_LIST = "res/api/entertainment/getApiEntertainList"

    /**
     * 娱乐场所详情
     */
    const val PLAYGROUNGD_DETAIL = "res/api/entertainment/getApiEntertainmentInfo"

    /**
     * 查询站点标签（云端+站点）
     */
    const val SELECT_LABEL = "config/api/resLabel/selectResLabel"

    /**
     * 查询站点标签（云端+站点）
     */
    const val SELECT_LABEL_VENUE = "config/api/dict/dictType"

    /**
     * 景区详情
     */
    const val SCENIC_DETAIL = "res/api/scenic/getApiScenicInfo"

    /**
     * 景区详情
     */
    const val SEREARCH_DETAIL = "res/api/researchBase/getApiResearchBaseInfo"

    /**
     * 景区养生度
     */
    const val SCENIC_HEALTH_INDEX = "res/api/scenic/getScenicHealthIndex"

    /**
     * 景区舒适度
     */
    const val SCENIC_COMFORT_URL = "config/api/forward/realpeople"

    /**
     * 获取景区景点列表
     */
    const val SCENIC_SPOTS = "res/api/scenic/getApiScenicSpotsList"

    /**
     * 获取查询景区景点的720全景
     */
    const val SCENIC_SPOTS_PANOR = "res/api/scenic/getScenicSpotsPanor"
    /**
     * 获取查询景区景点的720全景
     */
    const val SCENIC_ZB = "res/api/live/liveList"
    /**
     * 景点详情
     */
    const val SCENIC_SPOTS_DETAIL = "res/api/scenic/getApiScenicSpotsInfo"

    /**
     * 景点的音频
     */
    const val SCENIC_VOICE =
        "res/api/venue/getResourceAudioAndAudioTime?resourceType=CONTENT_TYPE_SCENERY&resourceId=32"

    /**
     * 景区品牌列表
     */
    const val SCENIC_BRAND_LIST = "res/api/brand/getApiBrandList"

    /**
     * 获取地图资源（地图模式）
     */
    const val SCENIC_MAP_REC_INFO = "res/api/venue/getMapResourceInfo"

    /**
     * 地图资源
     */
    const val MAP_RESOURCE_ICON = "res/api/venue/getMapResourceIcon"

    /**
     * 获取地图资源（厕所）
     */
    const val TOIENT_MAP_REC_INFO = "res/api/venue/getMapResourceInfo"

    /**获取地图资源（加油站详情）*/
    const val GAS_STATION = "res/api/gasStation/getApiGasStationInfo"

    /**
     * 厕所详情
     */
    const val TOIENT_MAP_DETAIL = "res/api/toilet/getApiToiletInfo"

    /**
     * 停车位详情
     */
    const val PAKING_MAP_DETAIL = "res/api/parking/getApiParkingInfo"

    /**
     * 医疗点详情
     */
    const val CASERVAC_MAP_DETAIL = "res/api/medical/getApiMedicalInfo"

    /**
     * 乘车点
     */
    const val BUS_SHOP_DETAIL = "res/api/busStop/getApiBusStopInfo"

    /**
     * 购物点
     */
    const val SHOP_MAIL_DETAIL = "res/api/shopMall/getApiShopMallInfo"

    /**
     * 获取地图资源（停车位）
     */
    const val COMMON_MAP_REC_INFO = "res/api/venue/getMapResourceInfo"

    /**
     * 获取标签分类
     */
    const val RES_LABEL = "config/api/resLabel/selectResLabel"


      /**
     * 获取全景漫游列表
     */
    const val GET_PANORAMIC_LIST = "res/api/scenic/getScenicSpotsPanor"

    /**
     * 获取场馆漫游列表
     */
    const val GET_VENUE_LIST = "res/api/venue/getVenuePanor"

    /**
     * 获取机器人信息
     */
    const val GET_IT_ROBOT_INFO = "res/api/es/robotInfo"

    /**
     * 机器人问答
     */
    const val GET_IT_ROBOT_REQUEST = "res/api/es/robotAnswer"

    /**
     *
     */
    const val GET_VIP_INFO = "res/api/realNameAuth/getRealNameInfoUseEbc"

    /**
     *红黑榜顶部背景图
     */
    const val GET_RED_BG = "res/api/content/channelDetail"

    /**
     *地区榜单
     */
    const val GET_AREA_DATA = "res/api/rankingList/getMunicipalList"
    /**
     *资源榜单
     */
    const val GET_RESOURE_DATA = "res/api/rankingList/getResourceRankingList"
    /**
     *特产详情
     */
    const val SPECIAL_DETAIL = "res/api/speciality/getApiSpecialityInfo"

}