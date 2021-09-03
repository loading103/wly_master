package com.daqsoft.servicemodule.net

/**
 * @des service api
 * @author hoklam
 * @Date 20200330
 */
object ServiceApi {
    /**
     * 求助提交信息
     */
    const val SERVICE_SOS_MSG = "config/api/emergency/save"
    /**
     * 导游列表
     */
    const val SERVICE_TOUR_GUIDE_LIST = "res/api/travel/tourGuideList"
    /**
     * 旅行社列表
     */
    const val SERVICE_TRAVEL_AGENCY_LIST = "res/api/travel/agencyList"
    /**
     * 附近公交站
     */
    const val SERVICE_NEAR_BUS_LIST = "resExt/api/gov/train/getBusAround"
    /**
     * 根据地址获取经纬度
     */
    const val SERVICE_BUS_ADDRESS_LIST = "resExt/api/gov/train/getBusAddress"
    /**
     * 根据起始点获取公交路线
     */
    const val SERVICE_BUS_LINE_LIST = "resExt/api/gov/train/getBusLineUrl"
    /**
     * 站点下级区域(两层)
     */
    const val SITE_CHILD_REGION = "res/api/region/siteChildRegion"
    /**
     * 获取搜索历史
     */
    const val SEARCH_RECORD = "res/api/es/getSearchRecord"

    /**
     * 删除历史记录
     */
    const val SEARCH_DELETE_RECORD = "res/api/es/clearSearchRecord"
    /**
     * 保存历史记录
     */
    const val SEARCH_SAVE_RECORD = "res/api/es/saveSearchRecord"
    /**
     * 火车城市列表
     */
    const val SEARCH_STATION_NAME = "resExt/api/gov/train/getStationName"
    /**
     * 汽车城市列表
     */
    const val SEARCH_CITY_LIST = "resExt/api/traffic/busSubways/cityList"
    /**
     * 火车列表
     */
    const val TRAIN_LISt = "resExt/api/gov/train/getTrainList"
    /**
     * 火车车次列表
     */
    const val TRAIN_DETAIL = "resExt/api/gov/train/getStationLine"
    /**
     * 火车车次详情
     */
    const val STATION_LINE_INFO = "resExt/api/gov/train/getStationLineInfo"

    /**
     * 汽车车次列表
     */
    const val SUBWAY_LIST = "resExt/api/traffic/coach/stationAndStationQuery"
    /**
     * 航班城市
     */
    const val PLAIN_city_LIST = "resExt/api/traffic/aviation/aviationCityList"

    /**
     * 机票列表
     */
    const val PLANE_LISt = "resExt/api/traffic/aviation/aviationFlight"
    /**
     * 艺术基金
     */
    const val ART_FOUND = "res/api/content/channelSubset"
    /**
     * 用户输入提示
     */
    const val INPUT_TIPS = "resExt/api/traffic/busSubways/getInputTips"
    /**
     * 广告图片
     */
    const val HOME_AD_URL = "res/api/ad/view"
}