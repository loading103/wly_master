package com.daqsoft.provider

/**
 * 保存首页模块activity的路径
 */
object ZARouterPath {

    /**
     * 基础资源的基础路径
     */
    const val ZMAINE_RESOURCE_BASE = "/homeModule/resource/"

    /**
     * 安逸游景区列表
     */
    const val ZMAINE_SCENIC_LIST = ZMAINE_RESOURCE_BASE +"SCENIC"
    /**
     * 安逸住页面
     */
    const val ZMAIN_HOTEL_LIST = ZMAINE_RESOURCE_BASE +"HOTEL"
    /**
     * 查攻略列表页面
     */
    const val ZMAIN_STRATEGY_FIND = ZMAINE_RESOURCE_BASE +"RAIDERS"

    /**
     * 安逸住页面
     */
    const val ZMAIN_HOTEL_DETAIL = "/homeZModule/resource/hotel/detail"
    /**
     * 酒店更多信息页面
     */
    const val HOTEL_INFO_MORE= ZMAINE_RESOURCE_BASE+"HOTELINFO"
    /**
     * 红黑榜详情列表
     */
    const val RED_BLACK_LIST= ZMAINE_RESOURCE_BASE+"RedBlacklRankDetailListActivity"
    /**
     * 红黑榜页面
     */
    const val RED_BLACK_AREA_LIST= ZMAINE_RESOURCE_BASE+"RedBlackRankListActivity"
}