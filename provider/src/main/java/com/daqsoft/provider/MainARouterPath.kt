package com.daqsoft.provider

import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils

/**
 * 保存首页模块activity的路径
 */
object MainARouterPath {
    /**
     * 品牌详情页面
     */
    const val MAIN_BRANCH_DETAIL = "/homeModule/BranchDetailActivity"
    /**
     * 品牌介绍、目的地等二级目录
     */
    const val MAIN_BRANCH_OTHER = "/homeModule/BrandIntroduceActivity"
    /**
     * 品牌列表页面
     */
    const val MAIN_BRANCH_LIST = "/homeModule/BranchListsActivity"
    /**
     * 下一站目的地
     */
    const val MAIN_MDD_LIST = "/homeModule/CityListActivity"
    /**
     *城市名片详情
     */
    const val MAIN_MDD_CITY_INFO = "/homeModule/CityInfoActivity"
    /**
     *当前城市名片详情
     */
    const val MAIN_MDD_CURRENT_CITY_INFO = "/homeModule/CurrentCityInfoActivity"
    /**
     *城市名片详情
     */
    const val MAIN_MY_WEB = "/homeModule/WebViewActivity"
    /**
     * 热门活动详情
     */
    const val MAIN_HOT_ACITITY = "/homeModule/hotActivitiesActivity"
    /**
     * 志愿活动详情
     */
    const val MAIN_VOLUNTEER_ACTIVITY = "/homeModule/volunteerActivityDetail"
    /**
     * 免费预订活动
     */
    const val MAIN_ACTIVITY_ORDER = "/homeModule/freeOrderActivity"
    /**
     * 报名活动
     */
    const val MAIN_ACTIVITY_SIGN = "/homeModule/signActivity"
    /**
     * 志愿者活动报名页面
     */
    const val MAIN_VOLUNTEER_SIGN = "/homeModule/volunteerSignActivity"
    /**
     * 座位选择页面
     */
    const val MAIN_SEAT_SELECT_ACTIVITY = "/homeModule/seatSelectActivity"
    /**
     * 付费预订活动
     */
    const val MAIN_ACTIVITY_PAY_ORDER = "/homeModule/payOrderActivity"
    /**
     * 活动的地图模式
     */
    const val MAIN_ACTIVITY_MAP = "/homeModule/mapModelActivity"
    /**
     * 活动列表页面
     */
    const val MAIN_ACTIVITY_LS = "/homeModule/HotActivitiesActivity"
    /**
     * 活动概览列表页面
     */
    const val MAIN_ACTIVITY_GL_LS = "/homeModule/HotGaiLanActivitiesActivity"
    /**
     * 活动首页
     */
    const val MAIN_ACTIVITY_INDEX = "/homeModule/ActIndexActivity"
    /**
     * 活动概览列表
     */
    const val MAIN_ACTIVITY_OVER_VIEW = "/homeModule/HotActivityOverView"
    /**
     * 预订成功页面
     */
    const val MAIN_ACTIVITY_SUCCESS = "/homeModule/orderSuccessActivity"
    /**
     * 评论页面
     */
    const val MAIN_ACTIVITY_COMMENT = "/homeModule/commentListActivity"
    /**
     * 添加评论
     */
    const val MAIN_COMMENT_ADD = "/homeModule/addCommentActivity"
    /**
     * 标签墙页面
     */
    const val MAIN_STORY_TAG = "/homeModule/storyTagActivity"
    /**
     * 添加标签页
     */
    const val ADD_STORY_TAG = "/homeModule/addStoryTagActivity"
    /**
     * 标签详情页面
     */
    const val MAIN_STORY_TAG_DETAIL = "/homeModule/storyTagDetailActivity"

    /**
     * 时光
     */
    const val MAIN_TIME = "/homeModule/TimeActivity"

    /**
     * 故事详情页面
     */
    const val MAIN_STORY_DETAIL = "/homeModule/storyDetailActivity"

    /**
     * 攻略列表
     */
    const val MAIN_STRATEGY_LIST = "/homeModule/StrategyListActivity"

    /**
     * 攻略详情页面
     */
    const val MAIN_STRATEGY_DETAIL = "/homeModule/StrategyDetailActivity"

    /**
     * 话题列表页面
     */
    const val MAIN_TOPIC_LIST = "/homeModule/TopicActivity"

    /**
     * 话题详情页面
     */
    const val MAIN_TOPIC_DETAIL = "/homeModule/TopicDetailActivity"

    /**
     * 添加地点也没
     */
    const val MAIN_STORY_ADDRESS = "/homeModule/storyAddressActivity"
    /**
     * 分享时光故事
     */
    const val MAIN_STORY_STRATEGY_ADD = "/homeModule/shareStoryStrategyActivity"
    /**
     * 我的故事列表页面
     */
    const val MINE_STORY_LIST = "/homeModule/mineStoryListActivity"
    /**
     * 社团
     */
    const val MAIN_CLUB = "/homeModule/resource/COMMUNITY"
    /**
     * 社团详情
     */
    const val MAIN_CLUB_INFO = "/homeModule/clubInfoActivity"

    /**
     * 社团成员列表
     */
    const val MAIN_CLUB_PERSON_LIST = "/homeModule/ClubPersonListActivity"

    /**
     * 社团介绍
     */
    const val MAIN_CLUB_INFO_INTRODUCE = "/homeModule/clubInfoIntroduceActivity"
    /**
     * 社团成员详情
     */
    const val MAIN_CLUB_PERSON_INFO = "/homeModule/ClubPersonInfoActivity"
    /**
     * 全局搜索
     */
    const val MAIN_ALL_SEARCH = "/homeModule/SearchActivity"
    /**
     * 基础资源的基础路径
     */
    const val MAINE_RESOURCE_BASE = "/homeModule/resource/"

    /**
     * 安逸游景区列表
     */
    const val MAINE_SCENIC_LIST = MAINE_RESOURCE_BASE + "SCENIC"
    /**
     * 实景直播列表
     */
    const val MAIN_SCENIC_SPOTS_LIVES = MAINE_RESOURCE_BASE + "SPOT_LIVES"

    const val MAIN_FIND_ACTIVITY = MAINE_RESOURCE_BASE + "FIND_ACTICITY"
    /**
     * 安逸住页面
     */
    const val MAIN_HOTEL_LIST = MAINE_RESOURCE_BASE + "HOTEL"
    /**
     * 查攻略列表页面
     */
    const val MAIN_STRATEGY_FIND = MAINE_RESOURCE_BASE + "RAIDERS"
    const val MAIN_COMMON_STOREY_LIST = MAINE_RESOURCE_BASE + "COMMON_STOREY_LIST"
    /**
     * 身边游
     */
    const val MAIN_SIDE_TOUR = MAINE_RESOURCE_BASE + "SIDETOUR"
    /**
     * 地图模式的景区列表
     */
    const val MAIN_SCENIC_LIST_MAP = "/homeModule/scenicResource"
    /**
     * 景区详情页面
     */
    const val MAIN_SCENIC_DETAIL = "/homeModule/scenicDetail"
    /**
     * 景区详情更多页面
     */
    const val MAIN_SCENIC_DETAIL_MORE = "/homeModule/ScenicDetailMore"

    /**
     * 景区详情更多页面
     */
    const val MAIN_RESEARCH_DETAIL_MORE = "/homeModule/ResearchDetailMore"
    /**
     * 景点详情
     */
    const val MAIN_SCENIC_SPOT_DETAI = "/homeModule/scenicSpotDetail"
    /**
     * 景点图片相册
     */
    const val MAIN_SCENIC_SPOT_IMAGES = "/homeModule/scenicSpotImages"
    /**
     * 投诉
     */
    const val MAIN_COMPLAINT_ACTIVITY = "/homeModule/ComplaintActivity"
    /**
     * 被投诉方
     */
    const val MAIN_COMPLAINT_RESOURCE_ACTIVITY = "/homeModule/ComplaintResourceActivity"
    /**
     * 内容
     */
    const val MAIN_CONTENT = "/homeModule/ContentActivity"
    const val MAIN_CONTENT_NEW = "/homeModule/NewContentActivity"
    const val MAIN_CONTENT_STUDY = "/homeModule/ContentStudyActivity"

    const val WATCH_SHOW = "/homeModule/WatchShowActivity"

    const val UNIVERSITY_LS = "/homeModule/UniversityActivity"
    /**
     * 内容详情
     */
    const val MAIN_CONTENT_INFO = "/homeModule/ContentInfoActivity"
    /**
     * 内容详情
     */
    const val MAIN_CONTENT_INFO2 = "/homeModule/ContentInfoActivity2"

    /**
     * 内容详情页评论列表
     */
    const val MAIN_CONTENT_COMMENT_LIST = "/homeModule/ContentCommentListActivity"


    /**
     * 内容图片详情
     */
    const val MAIN_CONTENT_IMG = "/homeModule/ContentImgActivity"

    /**
     * 美食列表页
     */
    const val MAIN_FOOD_LS = "/homeModule/foodslsActivity"

    /**
     * 美食详情
     */
    const val MAIN_FOOD_DETAIL = "/homeModule/foodsDetailActivity"

    /**
     * 美食介绍
     */
    const val MAIN_FOOD_INFO = "/homeModule/foodInfoActivity"

    /**
     * 娱乐场所列表页
     */
    const val MAIN_PLAYGROUND_LS = "/homeModule/PlayGroundLsActivity"

    /**
     * 娱乐场所详情
     */
    const val MAIN_PLAYGROUND_DETAIL = "/homeModule/PlayGroundDetailActivity"

    /**
     * 娱乐场所介绍
     */
    const val MAIN_PLAYGROUND_INFO = "/homeModule/PlayGroundInfoActivity"

    /**
     * 全景漫游
     */
    const val PANORAMIC_LIST = "/homeModule/PanoramicActivity"
    /**
     * 机器人页面
     */
    const val IT_ROBOT_PAGE = "/homeModule/ItRobotActivity"
    /**
     * 大讲堂列表
     */
    const val LECTURE_HALL_LS = "/homeModule/LectureHallLsActivity"
    /**
     * 大讲堂详情
     */
    const val LECTURE_HALL_DETIAL = "/homeModule/LectureHallDetailActivity"
    /**
     * 大讲堂提问详情
     */
    const val LECTURE_HALL_REQ = "/homeModule/LectureHallReqActivity"
    /**
     * 我的课程列表
     */
    const val MINE_LECTURE_LIST = "/homeModule/MineLectureActivity"
    /**
     * 我的提问列表
     */
    const val MINE_LECTURE_REQ = "/homeModule/MineLectureReqActivity"
    /**
     * 展览列表
     */
    const val COLLECT_SHOW_LIST = "/homeModule/ExhibitionLsActivity"
    /**
     * 展览详情
     */
    const val COLLECT_SHOW_DETAIL = "/homeModule/ExhibitionDetailActivity"
    /**
     * 文物列表
     */
    const val COLLECT_CULYURE_LIST = "/homeModule/CulturalRelicLsActivity"
    /**
     * 文物详情
     */
    const val COLLECT_CULYURE_DETAIL = "/homeModule/CulturalRelicDetailActivity"
    /**
     * 问卷调查列表
     */
    const val MINE_QUESTION_REQ = "/homeModule/QuestionNaureLsActivity"

    /**
     * 我的问卷调查列表
     */
    const val MINE_QUESTION_REQ_LIST = "/homeModule/QuestionNaureMineLsActivity"
    /**
     * 我的问卷调查(提交))
     */
    const val MINE_QUESTION_TX_LIST = "/homeModule/QuestionLsActivity"
    /**
     * 我的问卷调查结果
     */
    const val MINE_QUESTION_RESULT_LIST = "/homeModule/QuestionResultLsActivity"

    /**
     * 主题区域列表揭界面
     */
    const val THEME_AREA_LIST = "/homeModule/ThemeAreaListActivity"
    /**
     * 主题区域详情列表界面
     */
    const val THEME_AREA_LIST_DETAIL = "/homeModule/ThemeAreaDetalActivity"
    /**
     * 主题游玩项目列表界面
     */
    const val THEME_PLAY_LIST = "/homeModule/ThemePlayListActivity"

    /**
     * 主题区域详情列表界面
     */
    const val THEME_PROJECT_DETAIL = "/homeModule/ThemeProjectDetailActivity"

    /**
     * 研学基地列表界面
     */
    const val RESEARCH_BASELIST = "/homeModule/ReseachListActivity"
    /**
     * 研学基地列表界面
     */
    const val RESEARCH_DETAIL = "/homeModule/ReseachDetailActivity"

    /**
     * 特产列表界面
     */
    const val SPECIAL_BASELIST = "/homeModule/SpecialListActivity"

    /**
     * 特产详情
     */
    const val MAIN_SPECIAL_DETAIL = "/homeModule/SpecialDetailActivity"
    /**
     * 跳转机器人页面
     */
    fun toItRobotPage() {
        ARouter.getInstance().build(IT_ROBOT_PAGE)
            .navigation()
    }

    /**
     * 跳转机器人
     */
    fun toLectureHallLs() {
        ARouter.getInstance().build(LECTURE_HALL_LS)
            .navigation()
    }

    /**
     * 课程详情
     */
    fun toLectureHallDetail(id: String) {
        ARouter.getInstance().build(LECTURE_HALL_DETIAL)
            .withString("id", id)
            .navigation()
    }

    /**
     * 提问题页面
     */
    fun toLectureHallReq(id: String) {
        ARouter.getInstance().build(LECTURE_HALL_REQ)
            .withString("id", id)
            .navigation()
    }

    /**
     * 我的课程列表
     */
    fun toMineLectureLs() {
        if (AppUtils.isLogin()) {
            ARouter.getInstance().build(MINE_LECTURE_LIST)
                .navigation()
        } else {
            ToastUtils.showUnLoginMsg()
            ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                .navigation()
        }
    }

    /**
     * 我的提问列表
     */
    fun toMineLectureReq() {
        if (AppUtils.isLogin()) {
            ARouter.getInstance().build(MINE_LECTURE_REQ)
                .navigation()
        } else {
            ToastUtils.showUnLoginMsg()
            ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                .navigation()
        }
    }
}