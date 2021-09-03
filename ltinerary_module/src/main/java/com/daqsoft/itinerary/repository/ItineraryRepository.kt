package com.daqsoft.itinerary.repository

import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.itinerary.api.ItineraryApi
import com.daqsoft.itinerary.bean.*
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.provider.bean.ScenicDetailBean
import com.daqsoft.provider.network.RetrofitFactory
import com.daqsoft.provider.network.venues.VenuesApi
import com.daqsoft.venuesmodule.repository.bean.VenuesListBean
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * @Author：      邓益千
 * @Create by：   2020/4/22 10:32
 * @Description：
 */
class ItineraryRepository {

    companion object {
        val service: ItineraryServices = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.baseUrl)
            .addInterceptor(HeaderInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(ItineraryServices::class.java)
    }

    
    interface ItineraryServices {

        @GET(ItineraryApi.ITINERARY_LIST)
        fun getItineraryList(
            @Query("sourceType") sourceType: String,
            @Query("processDay") day: String,
            @Query("fitTags") fitTags: String,
            @Query("personalTags") tags: String,
            @Query("pageSize") pageSize: Int,
            @Query("currPage") currPage: Int,
            @Query("enablePage") enablePage: Boolean
        ): Observable<BaseResponse<ItineraryBean>>

        /**行程更新*/
        @POST(ItineraryApi.ITINERARY_UPDATE)
        fun itineraryUpdate(
            @Query("id") itineraryId: String,
            @Query("name") renamed: String
        ): Observable<ResponseBody>

        /**行程详情*/
        @GET(ItineraryApi.ITINERARY_DETAIL)
        fun getItineraryDetail(@Query("id") itineraryId: String): Observable<BaseResponse<ItineraryDetailBean>>

        /**查询景区门票*/
        @GET(ItineraryApi.QUERY_TICKET)
        fun queryTicket(
            @Query("sysCode") sysCode: String,
            @Query("resourceCode") resourceCode: String
        ): Observable<ResponseBody>

        /**
         * 目的地,城市列表
         * @param type ALL(所有)
         *             nation(国家)
         *             province(省)
         *             city(市)
         *             county(县)
         */
        @GET(ItineraryApi.DESTINATION_LIST)
        fun getDestination(@Query("type") type: String): Observable<BaseResponse<CityBean>>

        /**省份*/
        @GET(ItineraryApi.LOCATIONS)
        fun getProvince(): Observable<BaseResponse<ProvinceBean>>

        /**
         * 站点下级区域(两层)
         */
        @GET(ItineraryApi.SITE_CHILD_REGION)
        fun getChildRegions(): Observable<BaseResponse<ChildRegion>>

        /**
         * 加入到我的行程中
         * @param itineraryId 行程Id
         * @param startTime 出发日期
         */
        @POST(ItineraryApi.ADD_MY_ITINERARY)
        fun addMyItinerary(
            @Query("id") itineraryId: Int,
            @Query("startTime") startTime: String
        ): Observable<ResponseBody>

        /**查询交通*/
        @GET(ItineraryApi.TRAFFIC)
        fun queryTraffic(
            @Query("startId") startId: Int,
            @Query("endId") endId: Int,
            @Query("travelType") travelType: String): Observable<BaseResponse<Traffbean>>

        /**
         * 推荐行程页-标签列表
         */
        @GET(ItineraryApi.FILTER_LABEL)
        fun getFilterLabel(): Observable<BaseResponse<RecommFilterLabelBean>>

        @GET(ItineraryApi.RES_LABEL)
        fun getLabels(
            @Query("labelType") labelType: String,
            @Query("resourceType") resourceType: String
        ): Observable<BaseResponse<LabelBean>>

        /**取得站点code*/
        @GET(ItineraryApi.SITE_INOF)
        fun getSiteCode(): Observable<BaseResponse<SiteInfoBean>>

        /**根据标签查询场馆和景区*/
        @GET(ItineraryApi.VENUE_SCENIC)
        fun getVenueAndScenic(
            @Query("labelNames") labelNames: String
        ): Observable<BaseResponse<CustomBean>>

        /**根据地区编码查景区*/
        @GET(ItineraryApi.CUSTOM_SCENIC)
        fun getCustomScenic(
            @Query("region") region: String,
            @Query("size") size: String
        ): Observable<BaseResponse<CunsomScenicBean>>

        /**
         * 景区列表
         * @param type 主题类型
         * @param crowd 人群类型
         * @param region 地区编码
         */
        @GET(ItineraryApi.SCENIC_LIST)
        fun getScenicList(
            @Query("type") type: String,
            @Query("crowd") crowd: String,
            @Query("currPage") currPage: String,
            @Query("region") region: String,
            @Query("pageSize") pageSize: String = "10"
        ): Observable<BaseResponse<ScenicBean>>

        /**景区详情*/
        @GET(ItineraryApi.SCENIC_DETAIL)
        fun getScenicDetail(@Query("id") id: String): Observable<BaseResponse<ScenicDetailBean>>


        /**
         * 场馆列表
         * @param tag 主题类型
         * @param crowd 人群类型
         * @param region 地区编码
         */
        @GET(VenuesApi.VENUES_LIST)
        fun getVenueList(@Query("tag") tag: String,
                         @Query("crowd") crowd: String,
                         @Query("currPage") currPage: String,
                         @Query("region") region: String,
                         @Query("pageSize") pageSize: String = "10"
        ): Observable<BaseResponse<VenuesListBean>>

        /**保存自定义行程*/
        @POST(ItineraryApi.ITINERARY_SAVE)
        fun saveItinerary(@Body body:RequestBody): Observable<ResponseBody>

        /**餐馆列表*/
        @GET(ItineraryApi.DINER_LIST)
        fun getDinerList(@Query("lat") lat: String,
                         @Query("lng") lng: String,
                         @Query("currPage") currPage: Int,
                         @Query("pageSize") pageSize: Int = 10,
                         @Query("sortType") sortType: String = "disNum"
        ): Observable<BaseResponse<NearbyBean>>

        /**酒店列表*/
        @GET(ItineraryApi.HOTEL_LIST)
        fun getHotelList(@Query("lat") lat: String,
                         @Query("lng") lng: String,
                         @Query("currPage") currPage: Int,
                         @Query("pageSize") pageSize: Int = 10,
                         @Query("sortType") sortType: String = "disNum"
        ): Observable<BaseResponse<NearbyBean>>

        /**资源操作列表*/
        @POST(ItineraryApi.OPERATE)
        fun operateSource(@Body body:RequestBody): Observable<ResponseBody>
    }

}
