package com.daqsoft.provider

/**
 * @Author：      邓益千
 * @Create by：   2020/4/21 11:58
 * @Description：
 */
class ItineraryRouter {

    /** 智能行程 */
    companion object {
        /**
         * 行程列表
         * 我的行程和推荐行程
         */
        const val ITINERARY_LIST = "/ItineraryModule/ItineraryListActivity"

        /**系统推荐列表*/
        const val SYST_RECOMM_LIST = "/ItineraryModule/RecommListActivity"

        /**
         * 行程定制
         * 自定义行程计划
         */
        const val ITINERARY_CUSTOM = "/ItineraryModule/ItineraryCustom"

        /**定制详情*/
        const val ITINERARY_DETAIL = "/ItineraryModule/ItineraryDetailActivity"

        /**行程调整*/
        const val ITINERARY_ADJUST = "/ItineraryModule/ItineraryAdjustActivity"

        /**自定义目的地*/
        const val CUSTOM_DESTINATION = "/ItineraryModule/ItineraryCustomDestination"

        /**分配天数*/
        const val ASSIGN_DAY = "/ItineraryModule/AssignDayActivity"

        /**智能推荐*/
        const val SMART_RECOMM = "/ItineraryModule/SmartRecommActivity"

        /**添加景点*/
        const val ITINERARY_ADD_SCENIC = "/ItineraryModule/ItineraryAddScenic"

        /**景区详情*/
        const val ITINERARY_SCENIC_DETAIL = "/ItineraryModule/ScenicDetail"

        /**交通列表*/
        const val  TRAFFIC_LIST = "/ItineraryModule/trafficActivity"
    }
}