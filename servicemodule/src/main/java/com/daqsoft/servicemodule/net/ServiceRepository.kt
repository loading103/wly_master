package com.daqsoft.servicemodule.net

import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.provider.bean.HomeAd
import com.daqsoft.provider.network.RetrofitFactory
import com.daqsoft.servicemodule.bean.*
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

/**
 * 服务类
 * @author hoklam
 * @date 20200330pm
 */
class ServiceRepository {
    //    companion object {
//    }
    val service: ServiceInfo = RetrofitFactory.Builder()
        .setBaseUrl(BaseApplication.baseUrl)
        .addInterceptor(HeaderInterceptor())
        .addConvertFactory(FaultToleranceConvertFactory.create())
        .build(ServiceInfo::class.java)
}

interface ServiceInfo {
    /**
     * @des  服务---求助信息
     * @author hoklam
     * @Date 20200330
     */
    @POST(ServiceApi.SERVICE_SOS_MSG)
    fun uploadSosMsg(@QueryMap param: HashMap<String, Any>): Observable<BaseResponse<Any>>

    /**
     * 导游列表
     * @param map
     */
    @GET(ServiceApi.SERVICE_TOUR_GUIDE_LIST)
    fun tourGuideList(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<TourGuideBean>>

    /**
     * 旅行社列表
     * @param map
     */
    @GET(ServiceApi.SERVICE_TRAVEL_AGENCY_LIST)
    fun agencyList(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<TravelAgencyBean>>

    /**
     *附近公交站
     * @param map
     */
    @GET(ServiceApi.SERVICE_NEAR_BUS_LIST)
    fun getBusAround(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<NearBusBean>>

    /**
     *根据地址获取经纬度
     * @param map
     */
    @GET(ServiceApi.SERVICE_BUS_ADDRESS_LIST)
    fun getBusAddress(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<BusAddressBean>>

    /**
     *根据起始点获取公交路线
     * @param map
     */
    @GET(ServiceApi.SERVICE_BUS_LINE_LIST)
    fun getBusLineUrl(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<BusLineBean>>

    /**
     * 站点下级区域(两层)
     */
    @GET(ServiceApi.SITE_CHILD_REGION)
    fun getChildRegions(): Observable<BaseResponse<ChildRegion>>

    /**
     *根据起始点获取公交路线
     * @param map
     */
    @GET(ServiceApi.SEARCH_RECORD)
    fun getSearchRecord(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<String>>

    /**
     *删除历史记录
     * @param map
     */
    @GET(ServiceApi.SEARCH_DELETE_RECORD)
    fun clearSearchRecord(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<String>>

    /**
     *保存历史记录
     * @param map
     */
    @GET(ServiceApi.SEARCH_SAVE_RECORD)
    fun saveSearchRecord(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<String>>

    /**
     *火车城市列表
     * @param map
     */
    @GET(/*"{path}"*/ServiceApi.SEARCH_STATION_NAME)
    fun getStationName(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<StationBean>>
    /**
     *汽车城市列表
     * @param map
     */
    @GET(ServiceApi.SEARCH_CITY_LIST)
    fun cityList(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<StationBean>>

    /**
     *火车列表
     * @param map
     */
    @GET(ServiceApi.TRAIN_LISt)
    fun getTrainList(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<TrainListBean>>

    /**
     *火车车次列表
     * @param map
     */
    @GET(ServiceApi.TRAIN_DETAIL)
    fun getStationLine(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<TrainListBean>>

    /**
     *火车车次详情
     * @param map
     */
    @GET(ServiceApi.STATION_LINE_INFO)
    fun getStationLineInfo(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<TrainDetailBean>>

    /**
     *汽车车次列表
     * @param map
     */
    @GET(ServiceApi.SUBWAY_LIST)
    fun stationAndStationQuery(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<BusWayListData>>

    /**
     *航班城市
     * @param map
     */
    @GET(ServiceApi.PLAIN_city_LIST)
    fun aviationCityList(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<StationBean>>

    /**
     *飞机航班列表
     * @param map
     */
    @GET(ServiceApi.PLANE_LISt)
    fun aviationFlight(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<PlaneListBean>>
    /**
     *艺术基金
     * @param map
     */
    @GET(ServiceApi.ART_FOUND)
    fun channelSubset(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<ArtFoundListBean>>
    /**
     *用户输入提示
     * @param map
     */
    @GET(ServiceApi.INPUT_TIPS)
    fun getInputTips(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<InputTipsBean>>

    /**
     * 首页广告
     */
    @GET(ServiceApi.HOME_AD_URL)
    fun getServiceAd(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<HomeAd>>
}
