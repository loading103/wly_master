package com.daqsoft.provider.net

/**
 * @Description
 * @ClassName   TemplateApi
 * @Author      luoyi
 * @Time        2020/10/9 10:11
 */
object TemplateApi {

    /**
     * 客户端类型
     */
    const val CLIENT_TYPE = "APP"

    /**
     * 首页
     */
    const val HOME = "HOME"
    /**
     * 服务页
     */
    const val SERVICE_PAGE = "SERVICE_PAGE"
    /**
     * 个人中心
     */
    const val PERSONAL_CENTER = "PERSONAL_CENTER"


    /**
     * 获取布局
     */
    const val GET_TEMPLATE = "config/api/clientCustomConfig/getLayout"
    /**
     * 获取个人信息配置
     */
    const val GET_PERSONAL_TEMPLATE = "config/api/clientCustomConfig/getPersonalInfoConfig"
    /**
     * 获取底部菜单
     */
    const val GET_BOTTOM_MENU = "config/api/clientCustomConfig/getBottomMenuLayout"

    /**
     * 模块类型
     */
    object MoudleType {
        /**
         * 普通菜单
         */
        const val menu = "menu"
        /**
         * 功能组件
         */
        const val component = "component"
        /**
         * 顶部菜单
         */
        const val topMenu = "topMenu"
        /**
         * 运营专区
         */
        const val operation = "operation"
        /**
         * 底部菜单
         */
        const val bottomMenu = "bottomMenu"
        /**
         * 栏目标题
         */
        const val channelTitle = "channelTitle"
        /**
         * 轮播图
         */
        const val carousel = "carousel"
        /**
         * 时光故事
         */
        const val customize = "customize"
    }

    /**
     * 业务类型
     */
    object BusinessType {
        /**
         * 热门活动
         */
        const val HOT_ACTIVITY = "custom_hotActivity"
        /**
         * 资讯
         */
        const val INFORMATION = "custom_information"
        /**
         * 栏目
         */
        const val CUSTOM_INFORMATION_CHANNEL = "custom_information_channel"
        /**
         * 城市名片
         */
        const val CITY_CARD = "custom_cityCard"
        /**
         * 文旅品牌
         */
        const val BRAND = "custom_cultureTourismBrand"
        /**
         * 精品线路
         */
        const val ROUTER = "custom_boutiqueRoute"
        /**
         * 话题
         */
        const val CONVERSATION = "custom_conversation"
        /**
         * 话题
         */
        const val TIME_STORY = "custom_timeStory"
        /**
         * 导游导览
         */
        const val TOUR_GUIDE = "custom_tourGuide"

        /**
         * 发现身边
         */
        const val FOUND_AROUND="custom_find"
    }

}