package com.daqsoft.travelCultureModule.country.net

import com.daqsoft.baselib.base.BaseApplication
import io.reactivex.Observable
import retrofit2.http.QueryMap
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.RetrofitFactory
import com.daqsoft.provider.network.home.HomeApi
import com.daqsoft.travelCultureModule.country.bean.*
import com.daqsoft.travelCultureModule.country.bean.FoodProductBean
import com.daqsoft.travelCultureModule.country.bean.ResourceTypeLabel
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * desc :乡村游
 * @author 江云仙
 * @date 2020/4/13 15:25
 */
class CountryRepository {
    companion object {
        val service: CountryInfo = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.baseUrl)
            .addInterceptor(HeaderInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(CountryInfo::class.java)
        val electronicService: CountryInfo = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.electronicUrl)
            .addInterceptor(HeaderInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(CountryInfo::class.java)
    }

}

interface CountryInfo {
    /**
     *农家乐列表
     * @param map
     */
    @GET(CountryApi.COUNTRY_AGRITAINMENT_LIST)
    fun agritainmentList(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<AgritainmentBean>>

    /**
     * 获取资源标签
     */
    @GET(CountryApi.RES_LABEL)
    fun getSelectResLabel(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<ResourceTypeLabel>>

    /**
     * 站点下级区域(两层)
     */
    @GET(CountryApi.SITE_CHILD_REGION)
    fun getChildRegions(): Observable<BaseResponse<ChildRegion>>

    /**
     * 农家乐详情
     */
    @GET(CountryApi.AGRITAIN_DETAIL)
    fun getCountryHapDetail(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<CountryHapDetailBean>>

    /**
     * 获取美食商品列表
     */
    @GET(CountryApi.FOOD_PROUDCT_LIST)
    fun getFoodProductLs(@Query("resourceCode") resourceCode: String, @Query("sysCode") sysCode: String)
            : Observable<BaseResponse<MutableList<FoodProductBean>>>

    /**
     * 预约提醒
     */
    @GET(CountryApi.PRODUCT_NOTIFIY)
    fun notifyProduct(@Query("status") status: Boolean, @Query("productId") productId: String):
            Observable<BaseResponse<Any>>

    /**
     * 获取美食商品列表
     */
    @GET(CountryApi.API_HOTEL_LIST)
    fun getApiHotelList(@QueryMap map: HashMap<String, Any>)
            : Observable<BaseResponse<ApiHoteltBean>>

    /**
     * 获取资源标签
     */
    @GET(CountryApi.API_HOTEL_TYPE_LIST)
    fun getHotelTypeResLabel(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<ResourceTypeLabel>>

    /**
     * 乡村游头部信息
     */
    @GET(CountryApi.VISITING_CARD)
    fun getVisitingCard(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<VisitingCardBean>>

    /**
     * 乡村游记攻略
     */
    @GET(CountryApi.TRAVEL_GUIDE)
    fun getTravelGuide(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<TravelGuideBean>>

    /**
     * 获取民宿标签
     */
    @GET(CountryApi.RES_CLOUD_LABEL)
    fun getCloudLabelId(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<LabelBean>>

    /**
     * 获取资讯列表
     */
    @GET(CountryApi.INFO_LIST)
    fun getInfoList(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<InformationBean>>

    /**
     *获取栏目列表
     */
    @GET(HomeApi.HOME_CHANNEL_LIST)
    fun getHomeChannelList(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<Any>>

    /**
     * 目的地城市，县区
     */
    @GET(HomeApi.HOME_BRACNCH_DETAIL)
    fun getMDDCity(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<BrandMDD>>

    /**
     * 获取乡村游城市信息
     */
    @GET(HomeApi.HOME_BRACNCH_LIST)
    fun getTOURISMCity(
        @Query("pageSize") pageSize: String,
        @Query("type") type: String,
        @Query("siteId") siteId: String = "",
        @Query("cardType") cardType: String = "TOURISM"
    ): Observable<BaseResponse<BrandMDD>>

    /**
     * 获取乡村列表
     */
    @GET(HomeApi.GET_API_RURAL_LIST)
    fun getApiRuralList(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<CountryListBean>>

    /**
     * 获取乡村详情
     */
    @GET(HomeApi.GET_API_RURAL_INFO)
    fun getApiRuralInfo(@Query("id") id: String): Observable<BaseResponse<CountryDetailBean>>

    /**
     * 获取景观点列表
     */
    // ScenicSpotListBean
    @GET(HomeApi.GET_API_RURAL_SPOTS_LIST)
    fun getApiRuralSpotsList(@Query("id") id: String): Observable<BaseResponse<Spots>>

    /**
     * 获取景观点详情
     */
    // ScenicSpotInfoBean
    @GET(HomeApi.GET_API_RURAL_SPOTS_INFO)
    fun getApiRuralSpotsInfo(@Query("id") id: String): Observable<BaseResponse<Spots>>
}
