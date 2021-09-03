package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   MenuCode
 * @Author      PuHua
 * @Time        2020/1/6 18:30
 */
class MenuCode {
    companion object {
        // 首页
        const val INDEX = "INDEX"

        //  活动首页
        const val ACTIVITY = "ACTIVITY"

        //  时光首页
        const val TIME = "TIME"

        //  商城主页
        // 跳转小电商
        const val MALL = "MALL"
        const val EXTERNAL = "EXTERNAL"

        // 商品详情	MALL_DETAIL	MALL_DETAIL	3	// 跳转小电商
        // 个人中心首页
        const val USER = "USER"

        //  积分首页
        const val INTEGRAL = "INTEGRAL"

        // 我的订单页
        const val ORDERS = "ORDERS"

        //  我的活动页
        const val MY_ACTIVITY = "MY_ACTIVITY"

        // 我的预订页
        const val BOOKING = "BOOKING"

        //  电子码页
        const val CONSUMPTION_CODE = "CONSUMPTION_CODE"

        //  用户诚信页
        const val HONESTY = "HONESTY"

        //  目的地首页	DESTINATION		1
        const val DESTINATION = "DESTINATION"

        //  景区列表	SCENIC		1
        const val SCENIC = "SCENIC"

        //  景区详情	SCENIC_DETIAL	CONTENT_TYPE_SCENERY	3	跳转景区详情
        const val SCENIC_DETIAL = "SCENIC_DETIAL"

        // 资源类型
        const val CONTENT_TYPE_SCENERY = "CONTENT_TYPE_SCENERY"

        //  文化场馆列表	VENUE		1
        const val VENUE = "VENUE"

        //  文化场馆详情	VENUE_DETAIL	CONTENT_TYPE_VENUE	3
        const val VENUE_DETAIL = "VENUE_DETAIL"
        const val CONTENT_TYPE_VENUE = "CONTENT_TYPE_VENUE"

        //  酒店列表	HOTEL		1
        const val HOTEL = "HOTEL"

        //  酒店详情	HOTEL_DETAIL	CONTENT_TYPE_HOTEL	3
        const val HOTEL_DETAIL = "HOTEL_DETAIL"
        const val CONTENT_TYPE_HOTEL = "CONTENT_TYPE_HOTEL"

        //  餐饮列表	FOOD		1
        const val FOOD = "FOOD"

        //  餐饮详情	FOOD_DETAIL	CONTENT_TYPE_RESTAURANT	3
        const val FOOD_DETAIL = "FOOD_DETAIL"
        const val CONTENT_TYPE_RESTAURANT = "CONTENT_TYPE_RESTAURANT"

        //  攻略列表	RAIDERS		1
        const val RAIDERS = "RAIDERS"

        //  攻略详情	RAIDERS_DETAIL	strategy	3
        const val RAIDERS_DETAIL = "RAIDERS_DETAIL"

        //  话题列表	TOPIC		1
        const val TOPIC = "TOPIC"

        //  话题详情	TOPIC_DETAIL	CONTENT_TYPE_TOPIC	3
        const val TOPIC_DETAIL = "TOPIC_DETAIL"
        const val CONTENT_TYPE_TOPIC = "CONTENT_TYPE_TOPIC"

        //  志愿者首页	VOLUNTEER		1
        const val VOLUNTEER = "VOLUNTEER"

        //  志愿者列表	VOLUNTEER_LIST		1
        const val VOLUNTEER_LIST = "VOLUNTEER_LIST"

        //  文化社团列表	COMMUNITY		1
        const val COMMUNITY = "COMMUNITY"

        //  文化社团详情	COMMUNITY_DETAIL	CONTENT_TYPE_ASSOCIATION	3
        const val COMMUNITY_DETAIL = "COMMUNITY_DETAIL"
        const val CONTENT_TYPE_ASSOCIATION = "CONTENT_TYPE_ASSOCIATION"

        //  自驾游列表	zijiayou		1
        const val zijiayou = "zijiayou"

        // 公共服务首页	COMMON		1
        const val COMMON = "COMMON"

        /**
         * 新服务页
         */
        const val NEW_SERVICE = "DEFAULT_SERVICE"

        /**
         * 个人中心
         */
        const val NEW_MINE_CENTER = "PERSONAL"

        //   720列表	PANORAMIC		1
        const val PANORAMIC = "PANORAMIC"

        //  直播列表	LIVE		1
        const val LIVE = "LIVE"

        //   网红打卡首页	HOT		1
        const val HOT = "HOT"

        //   开发中的功能	DEVELOP		1	 // 需要弹窗提示 敬请期待 不跳转
        const val DEVELOP = "DEVELOP"
        //  外部链接			2

    }
}