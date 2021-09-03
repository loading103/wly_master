package com.daqsoft.provider.base

/**
 * @Description 资源类型(这个是平台的类型)
 * @ClassName   ResourceType
 * @Author      PuHua
 * @Time        2019/11/19 15:23
 */
object ResourceType {

    //个人
    const val PERSON = "PERSON"

    //加油站
    const val TYPE_GAS_STATION = "CONTENT_TYPE_GAS_STATION"

    // 停车场
    const val CONTENT_TYPE_PARKING = "CONTENT_TYPE_PARKING"

    // 	厕所
    const val CONTENT_TYPE_TOILET = "CONTENT_TYPE_TOILET"

    // 餐饮
    const val CONTENT_TYPE_RESTAURANT = "CONTENT_TYPE_RESTAURANT"
    // 娱乐场所
    const val CONTENT_TYPE_ENTERTRAINMENT = "CONTENT_TYPE_ENTERTAINMENT"

    // 农家乐
    const val CONTENT_TYPE_AGRITAINMENT = "CONTENT_TYPE_AGRITAINMENT"

    // 酒店房间
    const val CONTENT_TYPE_HOTEL_ROOM = "CONTENT_TYPE_HOTEL_ROOM"

    // 酒店
    const val CONTENT_TYPE_HOTEL = "CONTENT_TYPE_HOTEL"

    // 景点
    const val CONTENT_TYPE_SCENIC_SPOTS = "CONTENT_TYPE_SCENIC_SPOTS"

    // 景区
    const val CONTENT_TYPE_SCENERY = "CONTENT_TYPE_SCENERY"

    // 研学基地
    const val CONTENT_TYPE_RESEARCH_BASE = "CONTENT_TYPE_RESEARCH_BASE"

    // 特产
    const val CONTENT_TYPE_SPECIALTY = "CONTENT_TYPE_SPECIALITY"
    // 	活动室
    const val CONTENT_TYPE_ACTIVITY_SHIU = "CONTENT_TYPE_ACTIVITY_SHIU"

    // 场馆
    const val CONTENT_TYPE_VENUE = "CONTENT_TYPE_VENUE"

    // 乡村
    const val CONTENT_TYPE_COUNTRY = "CONTENT_TYPE_RURAL"

    // 购物点
    const val CONTENT_TYPE_SHOP_MALL = "CONTENT_TYPE_SHOP_MALL"

    // 乘车点
    const val CONTENT_TYPE_BUS_STOP = "CONTENT_TYPE_BUS_STOP"


    // 景观点
    const val CONTENT_TYPE_RURAL_SPOTS = "CONTENT_TYPE_RURAL_SPOTS"

    // 活动
    const val CONTENT_TYPE_ACTIVITY = "CONTENT_TYPE_ACTIVITY"
    // 内容
    const val CONTENT = "CONTENT"

    // 媒体(图片、视频、音频)		标签资源类型
    const val MEDIA = "MEDIA"

    // 志愿者团队
    const val CONTENT_TYPE_VOLUNTEER_TEAM = "CONTENT_TYPE_VOLUNTEER_TEAM"

    // 志愿者
    const val CONTENT_TYPE_VOLUNTEER = "CONTENT_TYPE_VOLUNTEER"

    // 社团成员
    const val CONTENT_TYPE_ASSOCIATION_PERSON = "CONTENT_TYPE_ASSOCIATION_PERSON"

    // 	社团
    const val CONTENT_TYPE_ASSOCIATION = "CONTENT_TYPE_ASSOCIATION"
    const val TYPE_ASSOCIATION = "ASSOCIATE_TYPE"

    // 活动 - 志愿者
    const val CONTENT_TYPE_ACTIVITY_VOLUNT = "CONTENT_TYPE_ACTIVITY_VOLUNT"

    // 话题
    const val CONTENT_TYPE_TOPIC = "CONTENT_TYPE_TOPIC"

    // 故事
    const val CONTENT_TYPE_STORY = "CONTENT_TYPE_STORY"

    //攻略
    const val CONTENT_TYPE_STRATEGY = "CONTENT_TYPE_STRATEGY"

    /**
     * 名片、目的地
     */
    const val CONTENT_TYPE_SITE_INDEX = "CONTENT_TYPE_SITE_INDEX"

    /**
     * 品牌
     */
    const val CONTENT_TYPE_BRAND = "CONTENT_TYPE_BRAND"

    /**
     * 标签
     */
    const val CONTENT_TYPE_STORY_TAG = "CONTENT_TYPE_STORY_TAG"

    /**
     * 非遗
     */
    const val HERITAGE = "HERITAGE"

    /**
     * 非遗保护基地
     */
    const val CONTENT_TYPE_HERITAGE_PROTECT_BASE = "CONTENT_TYPE_HERITAGE_PROTECT_BASE"

    /**
     * 非遗传习基地
     */
    const val CONTENT_TYPE_HERITAGE_TEACHING_BASE = "CONTENT_TYPE_HERITAGE_TEACHING_BASE"

    /**
     * 非遗体验基地
     */
    const val CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE = "CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE"

    /**
     * 非遗传承人
     */
    const val CONTENT_TYPE_HERITAGE_PEOPLE = "CONTENT_TYPE_HERITAGE_PEOPLE"

    /**
     * 非遗项目
     */
    const val CONTENT_TYPE_HERITAGE_ITEM = "CONTENT_TYPE_HERITAGE_ITEM"

    /**
     * 医疗点
     */
    const val CONTENT_TYPE_MEDICAL = "CONTENT_TYPE_MEDICAL"

    /**
     * 第三方预约
     */
    const val CONTENT_TYPE_TRIPARTITE = "CONTENT_TYPE_TRIPARTITE"

    /**
     * 课程
     */
    const val CONTENT_TYPE_COURSE = "CONTENT_TYPE_COURSE"

    /**
     * 志愿服务
     */
    const val TEAM_ACTIVITY_SERVICE = "TEAM_ACTIVITY_SERVICE"

    /**
     * 活动成果
     */
    const val CONTENT_TYPE_ACTIVITY_RESULT = "CONTENT_TYPE_ACTIVITY_RESULT"

    /**
     * 志愿活动 个人服务
     */
    const val USER_ACTIVITY_SERVICE = "USER_ACTIVITY_SERVICE"

    /**
     * 签到补录
     */
    const val VOLUNTEER_SIGN_REVAMP = "VOLUNTEER_SIGN_REVAMP"

    /**
     * 线路
     */
    const val CONTENT_TYPE_LINE = "CONTENT_TYPE_LINE"

    /**
     * 展厅
     */
    const val CONTENT_TYPE_EXHIBITION = "CONTENT_TYPE_EXHIBITION"
    /**
     * 馆藏
     */
    const val CONTENT_TYPE_COLLECTION = "CONTENT_TYPE_COLLECTION"
    /**
     * 展览
     */
    const val CONTENT_TYPE_EXHIBIT_SHOW = "CONTENT_TYPE_EXHIBIT_SHOW"

    /**
     * 志愿者
     */
    const val ACTIVITY_TYPE_VOLUNT = "ACTIVITY_TYPE_VOLUNT"

    /**
     * 根据对应的type获取对应的中文名
     */
    fun getName(type: String): String {
        return when (type) {
            CONTENT_TYPE_PARKING -> "停车场"
            CONTENT_TYPE_TOILET -> "厕所"
            CONTENT_TYPE_RESTAURANT -> "餐饮"
            CONTENT_TYPE_HOTEL_ROOM -> "酒店房间"
            CONTENT_TYPE_HOTEL -> "酒店"
            CONTENT_TYPE_SCENIC_SPOTS -> "景点"
            CONTENT_TYPE_SCENERY -> "景区"
            CONTENT_TYPE_ACTIVITY_SHIU -> "活动室"
            CONTENT_TYPE_VENUE -> "场馆"
            CONTENT_TYPE_ACTIVITY -> "活动"
            CONTENT -> "内容"
            MEDIA -> "媒体"
            CONTENT_TYPE_VOLUNTEER_TEAM -> "志愿者团队"
            CONTENT_TYPE_VOLUNTEER -> "志愿者"
            CONTENT_TYPE_ASSOCIATION_PERSON -> "社团成员"
            CONTENT_TYPE_ASSOCIATION -> "社团"
            CONTENT_TYPE_ACTIVITY_VOLUNT -> "志愿者活动"
            CONTENT_TYPE_TOPIC -> "话题"
            CONTENT_TYPE_STORY -> "故事"
            CONTENT_TYPE_BRAND -> "品牌"
            CONTENT_TYPE_STORY_TAG -> "标签"
            CONTENT_TYPE_HERITAGE_PROTECT_BASE -> "非遗保护基地"
            CONTENT_TYPE_HERITAGE_TEACHING_BASE -> "非遗传习基地"
            CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE -> "非遗体验基地"
            CONTENT_TYPE_HERITAGE_PEOPLE -> "非遗传承人"
            CONTENT_TYPE_HERITAGE_ITEM -> "非遗项目"
            CONTENT_TYPE_SITE_INDEX -> "城市名片"
            CONTENT_TYPE_AGRITAINMENT -> "农家乐"
            CONTENT_TYPE_COURSE -> "大讲堂"
            CONTENT_TYPE_ACTIVITY_RESULT -> "活动成果"
            TEAM_ACTIVITY_SERVICE -> "志愿活动 团队服务"
            USER_ACTIVITY_SERVICE -> "志愿活动 个人服务"
            VOLUNTEER_SIGN_REVAMP -> "签到补录"
            CONTENT_TYPE_COUNTRY -> "乡村"
            CONTENT_TYPE_RURAL_SPOTS -> "景观点"
            CONTENT_TYPE_ENTERTRAINMENT -> "娱乐场所"
            CONTENT_TYPE_SPECIALTY -> "特产"
            CONTENT_TYPE_RESEARCH_BASE -> "研学基地"
            else -> ""
        }

    }
}

/**
 * 活动方式
 */
class ActivityMethod {
    companion object {
        // CREDIT_USE(诚信优享)
        const val CREDIT_USE = "CREDIT_USE"

        // CREDIT_AUDIT(诚信免审)
        const val CREDIT_AUDIT = "CREDIT_AUDIT"

        // ACTIVITY_MODE_OTHER(其他活动)
        const val ACTIVITY_MODE_OTHER = "ACTIVITY_MODE_OTHER"

        // ACTIVITY_MODE_FREE(预订免费)
        const val ACTIVITY_MODE_FREE = "ACTIVITY_MODE_FREE"

        // ACTIVITY_MODE_ENROLL_FREE(报名免费)
        const val ACTIVITY_MODE_ENROLL_FREE = "ACTIVITY_MODE_ENROLL_FREE"

        // ACTIVITY_MODE_INTEGRAL(预订积分兑换)
        const val ACTIVITY_MODE_INTEGRAL = "ACTIVITY_MODE_INTEGRAL"

        // ACTIVITY_MODE_ENROLL_INTEGRAL(报名积分兑换)
        const val ACTIVITY_MODE_ENROLL_INTEGRAL = "ACTIVITY_MODE_ENROLL_INTEGRAL"

        // ACTIVITY_MODE_PLAIN(普通)
        const val ACTIVITY_MODE_PLAIN = "ACTIVITY_MODE_PLAIN"

        // ACTIVITY_MODE_VOLUNT(志愿招募)
        const val ACTIVITY_MODE_VOLUNT = "ACTIVITY_MODE_VOLUNT"

        // ACTIVITY_MODE_ENROLL_PAY(报名付费)
        const val ACTIVITY_MODE_ENROLL_PAY = "ACTIVITY_MODE_ENROLL_PAY"

        // ACTIVITY_MODE_INTEGRAL_PAY(预订付费)
        const val ACTIVITY_MODE_INTEGRAL_PAY = "ACTIVITY_MODE_INTEGRAL_PAY"
    }
}

/**
 * @des 活动类型
 * @author PuHua
 * @Date 2020/1/7 10:28
 */
class ActivityType {
    // ACTIVITY_TYPE_RESERVE(预订) ACTIVITY_TYPE_ENROLL(报名) ACTIVITY_TYPE_PLAIN(宣传) ACTIVITY_TYPE_VOLUNT(志愿)
    companion object {
        //  ACTIVITY_TYPE_RESERVE(预订)
        const val ACTIVITY_TYPE_RESERVE = "ACTIVITY_TYPE_RESERVE"

        // ACTIVITY_TYPE_ENROLL(报名)
        const val ACTIVITY_TYPE_ENROLL = "ACTIVITY_TYPE_ENROLL"

        // ACTIVITY_TYPE_PLAIN(宣传)
        const val ACTIVITY_TYPE_PLAIN = "ACTIVITY_TYPE_PLAIN"

        // ACTIVITY_TYPE_VOLUNT(志愿)
        const val ACTIVITY_TYPE_VOLUNT = "ACTIVITY_TYPE_VOLUNT"

        // ACTIVITY_TYPE_SERVICE(普通)
        const val ACTIVITY_TYPE_SERVICE = "ACTIVITY_TYPE_SERVICE"


    }
}

/**
 * @des 主要是表示本地需要展示的图片，音视频的类型
 * @author PuHua
 * @Date 2019/12/27 15:05
 */
class LocalResourceType {
    companion object {

        const val IMAGE = "image"
        const val VIDEO = "video"
        const val VIOCE = "voice"
    }
}

/**
 * 广告图Type类型
 */
class AdvCodeType {
    companion object {
        /**
         * 首页第一个广告图
         */
        const val INDEX_TOP_ADV = "app_home_top_adv"

        /**
         * 首页中间广告图
         */
        const val INDEX_CENTER_ADV = "app_home_center_adv"

        /**
         * 首页顶部广告
         */
        const val APP_INDEX_LOG = "app_index_logo"

        /**
         * 首页顶部广告
         */
        const val APP_TOP_BANNER = "APP_index_top_ad"

        /**
         * 景区广告
         */
        const val SCENIC_LIST_TOP_ADV = "app_scenic_adv"

        /**
         * 酒店广告
         */
        const val HOTEL_LIST_TOP_ADV = "app_hotel_adv"

        /**
         * 美食广告
         */
        const val FOOD_LIST_TOP_ADV = "app_food_adv"

        /**
         * 博物馆列表广告
         */
        const val VENUE_LIST_TOP_ADV = "app_venues_adv"

        /**
         * 活动顶部广告
         */
        const val ACTIVITY_LIST_TOP_ADV = "app_activity_top_adv"

        /**
         * 活动列表顶部广告
         */
        const val ACTIVITY_INDEX_LIST_TOP_ADV = "app_activity_adv"

        /**
         * app 闪屏广告
         */
        const val APP_SPLASH_ADV = "app_splashscreen_adv"
        /**
         * app 竖屏图片（优先）
         */
        const val APP_SPLASH_VS_ADV = "app_video_vs_adv"
        /**
         * app横屏图片
         */
        const val APP_SPLASH_LE_ADV = "app_video_le_adv "
    }
}

/**
 * 发起的类型
 */
class PublishChannel {
    companion object {
        /**
         * 站点
         */
        const val MICRO_SITE = "APP"
    }
}


/**
 * 发起的类型
 */
class StudyChannel {
    companion object {
        /**
         * 研学主题
         */
        const val STUDY_THEME = "sysYxzt"
        /**
         * 研学课程
         */
        const val STUDY_CLASS = "sysYxkc"
        /**
         * 研学资讯
         */
        const val STUDY_ZIXUN = "sysYxzx"
        /**
         * 研学线路
         */
        const val STUDY_LINE= "sysYxxl"
    }
}
