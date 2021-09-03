package com.daqsoft.itinerary.api

/**
 * @Author：      邓益千
 * @Create by：   2020/4/22 11:36
 * @Description： 行程相关接口
 */
object ItineraryApi {

    /**行程列表（系统推荐，定制行程）*/
    const val ITINERARY_LIST = "resExt/api/journey/list"

    /**行程更新*/
    const val ITINERARY_UPDATE = "resExt/api/journey/update"

    /**加入到我的行程中*/
    const val ADD_MY_ITINERARY = "resExt/api/journey/copy"

    /**系统推荐-筛选标签*/
    const val FILTER_LABEL= "resExt/api/journey/journeyLabel"

    /** 获取标签分类 */
    const val RES_LABEL="config/api/resLabel/selectResLabel"

    /**获取城市列表*/
    const val LOCATIONS = "res/api/region/cityRegionModule"

    /**站点下级区域(两层)*/
    const val SITE_CHILD_REGION = "res/api/region/siteChildRegion"

    /**目的地,城市列表*/
    const val DESTINATION_LIST = "res/api/brand/getBrandSiteCityList"

    /**查询交通*/
    const val TRAFFIC = "resExt/api/journey/travelQuery"

    /**站点信息*/
    const val SITE_INOF = "user/api/site/siteInfo"

    /**场馆和景区*/
    const val VENUE_SCENIC = "res/api/venue/getVenueAndScenicExtendsList"

    /**根据地区编码查景区*/
    const val CUSTOM_SCENIC= "res/api/scenic/getApiScenicListByRegion"

    /**景区列表*/
    const val SCENIC_LIST = "res/api/scenic/getApiScenicList"

    /**景区详情*/
    const val SCENIC_DETAIL = "res/api/scenic/getApiScenicInfo"

    /**
     * 酒店列表
     */
    const val HOTEL_LIST = "res/api/hotel/getApiHotelList"

    /**创建自定义行程*/
    const val ITINERARY_SAVE = "resExt/api/journey/save"

    /**行程详情*/
    const val ITINERARY_DETAIL = "resExt/api/journey/detail"

    /**查询景区门票*/
    const val QUERY_TICKET = "culturalcloud/1.0/product/ticketListForResourceCode"

    /**附近餐馆*/
    const val DINER_LIST= "res/api/dining/getApiDiningList"

    /**操作行程资源*/
    const val OPERATE = "resExt/api/journey/operateSource"
}