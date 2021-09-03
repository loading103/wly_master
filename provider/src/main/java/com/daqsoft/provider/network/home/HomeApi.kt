package com.daqsoft.provider.network.home

/**
 * @des 主页app里的api
 * @author PuHua
 * @Date 2019/12/5 14:15
 */
object HomeApi {
    /**
     * 获取菜单列表
     */
    const val APP_MENU_LIST = "config/api/clientMenu/menuList"

    /**
     * 发现周边
     */
    const val HOME_FOUND_AROUND = "res/api/es/foundAround"

    /**
     * 首页模块接口
     */
    const val HOME_MODULE_LIST = "config/api/indexModule/moduleInfo"

    /**
     * 活动分类列表
     */
    const val ACTIVITY_CLASSIFY = "res/api/activity/activityClassifyCount"
    /**
     * 获取活动列表
     */
    const val ACTIVITY_LIST = "res/api/activity/getActivityList"


    /**
     * 预约活动室列表
     */
    const val ACTIVITY_ROOM = "res/api/venue/randomNotAuditRoomList"

    /**
     * 活动室详情
     */
    const val ACTIVITY_ROOM_DETAIL = "res/api/activityRoom/getActivityRoomInfo"

    /**
     * 获取活动室解说音频
     */
    const val ACTIVITY_ROOM_VOICE = "res/api/venue/getResourceAudioAndAudioTime"

    /**
     * 发送验证码
     */
    const val ACTIVITY_SEND_CODE = "act/api/activityOrder/sendSmsCode"

    /**
     * 生成订单
     */
    const val ACTIVITY_ORDER_GENERATE = "act/api/activityOrder/generateOrderSM4"

    /**
     * 支付订单
     */
    const val ORDER_PAY = "user/api/activityOrder/pay"
    /**
     * 保存订单
     */
    const val SAVE_ORDER = "act/api/activityOrder/saveGenerateOrder"

    /**
     * 查看当前填写的手机号的预订的单数
     */
    const val ACTIVITY_CHECK_EXITST_NUMBER = "act/api/activityOrder/existOrderNum"

    /**
     * 活动室和场馆状态查询
     */
    const val ACTIVITY_ROOM_AND_VENUE_STATUS = "res/api/activityRoom/getVenueAndActivityRoomStatus"

    /**
     * 品牌展播列表
     */
    const val HOME_BRANCH_LIST = "res/api/brand/getApiBrandList"
    /**
     * 首页话题列表
     */
    const val HOME_TOPIC_LIST = "res/api/topic/apiHomePageList"
    /**
     * 首页故事标签列表
     */
    const val HOME_STORY_TAG_LIST = "res/api/story/hotStoryTagList"
    /**
     * 常用标签列表
     */
    const val HOME_STORY_TAG_RECORD_LIST = "res/api/story/getTagRecord"

    /**
     * 获取用户创建或使用过的标签列表
     */
    const val MINE_START_TAG_LIST = "res/api/story/vipTagList"

    /**
     * 故事标签列表
     */
    const val STORY_TAG_LIST = "res/api/story/tagList"
    /**
     * 首页获取故事列表
     */
    const val HOME_STORY_LIST = "res/api/story/list"
    /**
     * 我的故事列表
     */
    const val STORY_VIP_LIST = "res/api/story/vipStoryList"

    /**
     * 删除故事
     */
    const val DELETE_VIP_STORY = "res/api/story/vipDelete"

    /**
     * 广告图片
     */
    const val HOME_AD_URL = "res/api/ad/view"
    /**
     * 城市名片列表包含当前城市，有天气信息
     */
    const val HOME_CITY_CARDS = "user/api/siteVisitingCard/getNextVisitingCard"
    /**
     * 获取城市名片
     */
    const val HOME_CITY_CARD = "user/api/siteVisitingCard/getVisitingCard"
    /**
     * 内容列表（安逸四川列表）
     */
    const val HOME_CONTENT_LIST = "res/api/content/list"
    /**
     * 首页去哪玩（智能行程）
     */
    const val HOME_QNW_LIST = "res/api/content/apiQnwHomePageList"
    /**
     * 栏目列表
     */
    const val HOME_CHANNEL_LIST = "res/api/content/channelList"

    /**
     * 获取精品线路标签列表
     */
    const val HOME_CHANNEL_TAG_LIST = "res/api/content/channelContentTagList"

    /**
     * 品牌详情目的地
     */
    const val HOME_BRACNCH_DETAIL = "res/api/brand/getBrandRelationList"
    /**
     * 首页品牌城市列表
     */
    const val HOME_BRACNCH_LIST = "res/api/brand/getBrandSiteCityList"
    /**
     * 获取天气
     */
    const val CITY_WEATHER = "res/api/scenic/getApiWeatherInfo"
    /**
     * 获取故事封面
     */
    const val STORY_COVER = "res/api/story/getCover"
    /**
     * 故事标签信息统计
     */
    const val STORY_TAG_DITAIL = "res/api/story/tagCount"
    /**
     * 故事詳情
     */
    const val STORY_DETAIL = "res/api/story/detail"
    /**
     * 故事详情（我的故事，和再审核中的故事）
     */
    const val STORY_DETAIL_MINE = "res/api/story/vipStoryDetail"
    /**
     * 资源点赞列表
     */
    const val RESOURCE_THUMB_LIST = "res/api/interactManage/thumbList"

    /**
     * 添加地点
     */
    const val SEARCH_AROUND = "res/api/es/searchAround"
    /**
     * 发表故事（攻略详情）
     */
    const val POST_STORY_STRATEGY = "res/api/story/saveStoryStrategy"
    /**
     * 获取话题列表
     */
    const val GET_TOPIC_LIST = "res/api/topic/list"


    /**
     * 获取话题详情
     */
    const val GET_TOPIC_DETAIL = "res/api/topic/detail"

    /**
     * 获取故事检测不通过原因
     */
    const val GET_NO_PASS_MSG = "res/api/story/getNoPassMsg"
    /**
     * 通过定位获取周边资源
     */
    const val GET_MAP_RESOURCE =
        "res/api/venue/getMapResourceInfo?token=de2625348ca54d879e616810a9adec37&pageSize=4&latitude=43.397181&longitude=88.144539&type=CONTENT_TYPE_HOTEL"
    /**
     * 保存投诉
     */
    const val SAVE_COMPLAINT = "res/api/complaints/save"

    /**
     * 被投诉方资源对象接口
     */
    const val COMPLAINT_RESOURCE_SEARCH = "res/api/es/search"
    /**
     * 当前积分
     */
    const val HOME_CURR_POINT = "user/api/point/currPoint"
    /**
     * 签到状态和未领取任务数
     */
    const val HOME_POINT_TASK_INFO = "user/api/point/pointTaskInfo"

    /**
     * 签到
     */
    const val HOME_USER_CHECK_IN = "user/api/userBind/checkIn"

    /**
     * 获取问候语
     */
    const val GET_IT_ROBOT_GREETINGS = "res/api/robot/greetings"

    /**
     * 下拉刷新文案
     */
    const val GET_PULL_DOWN_TO_REFRESH_TIP = "res/api/tips/random"

    /**
     * 乡村列表请求
     */
    const val GET_API_RURAL_LIST = "res/api/rural/getApiRuralList"

    /**
     * 乡村详情请求
     */
    const val GET_API_RURAL_INFO = "res/api/rural/getApiRuralInfo"

    /**
     * 景观点列表请求
     */
    const val GET_API_RURAL_SPOTS_LIST = "res/api/rural/getApiRuralSpotsList"

    /**
     * 景观点详情请求
     */
    const val GET_API_RURAL_SPOTS_INFO = "res/api/rural/getApiRuralSpotsInfo"
    /**
     * 导览详情
     */
    const val GUIDE_DETAIL = "res/api/guidedTour/getApiDetail"

    /**
     * 获取导游导览信息
     */
    const val GUIDE_HOME="res/api/guidedTour/guidedTourResourceCount"



    /**
     * 获取问卷调查列表
     */
    const val QUSESTION_LIST="resExt/api/paper/list"
    /**
     * 问卷调查提交
     */
    const val QUSESTION_SUBMIT="resExt/api/paper/submit"
    /**
     * 获取用户列表
     */
    const val QUSESTION_USER_LIST="resExt/api/paper/userList"
    /**
     * 结果统计
     */
    const val QUSESTION_RESULT="resExt/api/paper/resultStatistic"
    /**
     * 获取问卷调查详情
     */
    const val QUSESTION_DETAIL="resExt/api/paper/info"



    /**
     * 展览列表
     */
    const val EXHIBIT_LIST="res/api/exhibition/getApiExhibitionShowList"

    /**
     * 展览详情
     */
    const val EXHIBIT_DETAIL="res/api/exhibition/getApiExhibitionShowInfo"
    /**
     * 展厅列表
     */
    const val EXHIBIT_LIST_ONLINE="res/api/exhibition/getApiExhibitionList"

    /**
     * 文物列表
     */
    const val CULTURE_LIST="res/api/collection/getApiCollectionList"
    /**
     * 文物详情
     */
    const val CULTURE_DETAIL="res/api/collection/getApiCollectionInfo"
    /**
     * 文物分类
     */
    const val CULTURE_TOP="config/api/dict/dictType"
}