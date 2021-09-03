package com.daqsoft.guidemodule.net

/**
 * @des Guide里的api
 * @author Wongxd
 * @Date 2020/4/1 14:15
 */
internal object GuideApi {


    /**
     * 查询站点标签（云端+站点）
     */
    const val SELECT_LABEL = "config/api/resLabel/selectResLabel"

    /**
     * 导览-景点列表
     */
    const val GUIDE_SCENIC_LIST = "res/api/guidedTour/getApiScenic"


    /**
     * 导游导览 搜索新版本
     */
    const val GUIDE_SEARCH_RESOUCE_NEW = "res/api/guidedTour/resourcePageNSearch"

    /**
     * 导览详情
     */
    const val GUIDE_DETAIL = "res/api/guidedTour/getApiDetail"


    /**
     * 导览首页列表
     */
    const val GUIDE_LIST = "res/api/guidedTour/getApiList"


    /**
     * 导览-景点搜索
     */
    const val GUIDE_RESOURCE_SEARCH = "res/api/guidedTour/resourceSearch"

    /**
     * 导览-景点搜索
     */
    const val GUIDE_RESOURCE_NEW_SEARCH = "res/api/guidedTour/resourceNSearch"

    /**
     * 导览-线路列表
     */
    const val GUIDE_ROUTE = "res/api/guidedTour/getApiRoute"

    /**
     * 资源标题
     */
    const val GUIDE_RESOURCE_LIST = "res/api/guidedTour/reTitleSearch"

}