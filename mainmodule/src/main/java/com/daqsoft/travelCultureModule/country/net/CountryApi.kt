package com.daqsoft.travelCultureModule.country.net

/**
 * desc :乡村游请求api
 * @author 江云仙
 * @date 2020/4/13 15:25
 */
object CountryApi {
    /**
     * 农家乐列表
     */
    const val COUNTRY_AGRITAINMENT_LIST = "res/api/agritainment/list"
    /**
     * 获取标签分类
     */
    const val RES_LABEL = "config/api/resLabel/selectResLabel"
    /**
     * 站点下级区域(两层)
     */
    const val SITE_CHILD_REGION = "res/api/region/siteChildRegion"
    /**
     * 农家乐详情
     */
    const val AGRITAIN_DETAIL = "res/api/agritainment/view"

    /**
     * 美食&虚拟商品
     */
    const val FOOD_PROUDCT_LIST = "product/commodityListForResourceCode"
    /**
     * 预约提醒
     */
    const val PRODUCT_NOTIFIY = "product/openAndClose"
    /**
     * 住民宿
     */
    const val API_HOTEL_LIST = "res/api/hotel/getApiHotelList"

    const val API_HOTEL_TYPE_LIST = "config/dict/dictType"
    /**
     * 乡村游头部信息
     */
    const val VISITING_CARD = "user/api/siteVisitingCard/getVisitingCard"
    /**
     * 乡村游记攻略
     */
    const val TRAVEL_GUIDE = "res/api/content/list"

    /**
     *获取民宿标签
     */
    const val RES_CLOUD_LABEL = "config/api/resLabel/getCloudLabelId"
    /**
     *资讯列表
     */
    const val INFO_LIST = "res/api/content/list"

}