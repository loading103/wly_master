package com.daqsoft.provider.network.venues

import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.RetrofitFactory
import com.daqsoft.provider.network.venues.bean.VenuesDetailsBean
import com.daqsoft.venuesmodule.repository.bean.VenuesListBean
import io.reactivex.Observable
import retrofit2.http.*

/**
 * 文化场馆的网络接口工具
 * @author luoyi
 * @date 2020/3/14 18：12
 * @version 1.1.0
 * @since JDK 1.8
 */
class VenuesRepository {


    companion object {
        /**
         * 初始化网络请求的对象
         */
        val venuesService: VenuesService = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.baseUrl)
            .addInterceptor(HeaderInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(VenuesService::class.java)

    }
}

interface VenuesService {


    /**
     * 文化场馆列表
     * @param pageSize 分页参数
     * @param currPage 分页参数
     * @param region 地区编码
     * @param type 类型id
     * @param lng    经度
     * @param lat    纬度
     * @param hot    热度
     * @param activity 可预订活动
     * @param orderRoom 可预订活动室
     * @param tag 场馆标签
     * @param keyword 关键字搜索
     */
    @GET(VenuesApi.VENUES_LIST)
    fun getVenuesList(
        @QueryMap param: HashMap<String, String>
    ): Observable<BaseResponse<VenuesListBean>>

    @GET(VenuesApi.VENUES_LIST)
    fun getCityVenuesList(
        @Query("pageSize") pageSize: String,
        @Query("areaSiteSwitch") areaSiteSwitch: String
    ): Observable<BaseResponse<VenuesListBean>>

    /**
     *获取下单地址信息
     *
     */
    @GET(VenuesApi.ODER_ADDRES_INFO)
    fun getOrderAddressInfo(@Query("type") resourceType: String, @Query("id") id: String): Observable<BaseResponse<OderAddressInfoBean>>

    /**
     *  场馆详情
     */

    @GET(VenuesApi.VENUES_DETAILS)
    fun getVenuesDetails(@Query("id") id: String): Observable<BaseResponse<VenuesDetailsBean>>

    /**
     * 站点下级区域(两层)
     */
    @GET(VenuesApi.SITE_CHILD_REGION)
    fun getChildRegions(): Observable<BaseResponse<ChildRegion>>

    /**
     *  获取文化馆等级
     */
    @GET(VenuesApi.VENUES_DICTTYPE)
    fun getVenueLevel(@Query("type") type: String): Observable<BaseResponse<VenueLevelBean>>

    /**
     *  获取文化馆等级
     */
    @GET(VenuesApi.VENUES_DICTTYPE)
    fun getVenueType(@Query("type") type: String): Observable<BaseResponse<VenueTypeBean>>

    /**
     * 收藏文化馆
     */
    @FormUrlEncoded
    @POST(VenuesApi.VENUES_COLLECT)
    fun collectVenue(@Field("resourceType") resourceType: String, @Field("resourceId") resourceId: String): Observable<BaseResponse<Any>>

    /**
     * 取消收藏
     */
    @FormUrlEncoded
    @POST(VenuesApi.VENUES_CANCE_COLLECT)
    fun canceCollectVenue(
        @Field("resourceId") resourceId: String,
        @Field("resourceType") resourceType: String
    ): Observable<BaseResponse<Any>>

    /**
     *  通用获取地图资源
     * 根据类型和经纬度获取 类型信息
     * * lng	经度	string
     * lat	纬度	string
     * type 类型
     */
    @GET(VenuesApi.VENUES_MAP_RES)
    fun getCommonMapRecInfo(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<MapResBean>>

    /**
     *获取故事列表数据
     * 	tagId	标签id	number	【选填】标签id 0 查询tagId不为空的数据
     *listCover	列表封面	number	【选填】是否列表封面1：是 0：否
     *homeCover	首页封面	number	【选填】是否首页封面1：是 0：否
     *topicId	话题id	number	【选填】
     *currPage	当前页	number	【选填】默认 1
     *pageSize	每页数量	number	【选填】默认 10
     *orderType	排序方式	string	默认按发布时间降序排列 likeNum:按点赞数量降序排序
     * likeNumAndCommentNum:按点赞+评论降序排列 likeNumAndCommentNumAndShowNum:按点赞数+评论数+浏览数排序
     *storyType	故事类型	string	strategy：攻略 story：故事
     *resourceId	资源id	number
     *resourceType	资源类型	string
     *ich	是否非遗	boolean	【选填】是否只查询非遗数据
     */
    @GET(VenuesApi.VENUES_STORES_LIST)
    fun getCommonStories(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<HomeStoryBean>>

    /**
     * venueId	场馆 ID	number	必填
     *  date	月份	string	yyyy-MM-dd 默认当月
     *  num    date时间开始，返回多少天的数据	number	不填默认返回当月
     */
    @GET(VenuesApi.VENUE_ORDER_DATE_LIST)
    fun getOrderDateList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<VenueReservationInfo>>

    /**
     * venueId	场馆 ID	number	必填
     *  date	月份	string	yyyy-MM-dd 默认当月
     *  num    date时间开始，返回多少天的数据	number	不填默认返回当月
     */
    @GET(VenuesApi.SCENIC_ORDER_DATE_LIST)
    fun getScenicOrderDateList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<VenueReservationInfo>>

    /**
     * 	venueId	场馆ID	number	必填
     * date	月份	string	yyyy-MM-dd 默认当月
     */
    @GET(VenuesApi.VENUE_ORDER_DATE_NUM)
    fun getOrderDateNum(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<VenueDateNumBean>>

    /**
     * 	venueId	场馆ID	number	必填
     * date	月份	string	yyyy-MM-dd 默认当月
     */
    @GET(VenuesApi.SCENIC_ORDER_DATE_NUM)
    fun getScenicOrderDateNum(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<VenueDateNumBean>>

    /** 获取场馆下单信息（时段）
     * @param venueId 场馆id 必填
     * @param date 日期 必填
     */
    @GET(VenuesApi.VENUE_ORDER_VIEW)
    fun getVenueOrderView(@Query("venueId") venueId: String): Observable<BaseResponse<VenueOrderViewInfo>>

    /** 获取场馆下单信息（时段）
     * @param venueId 场馆id 必填
     * @param date 日期 必填
     */
    @GET(VenuesApi.SCENIC_ORDER_VIEW)
    fun getScenicOrderView(@Query("scenicId") venueId: String): Observable<BaseResponse<VenueOrderViewInfo>>

    /**
     * 获取场馆时段
     * @param venueId 场馆id
     * @param date 日期
     */
    @GET(VenuesApi.VENUE_ORDER_TIMES)
    fun getVenueOrderTimes(
        @Query("venueId") venueId: String, @Query("date") date: String,
        @Query("reservationType") type: String
    ): Observable<BaseResponse<VenueOrderTime>>

    /**
     * 获取场馆时段
     * @param venueId 场馆id
     * @param date 日期
     */
    @GET(VenuesApi.SCENIC_ORDER_TIMES)
    fun getScenicOrderTimes(
        @Query("scenicId") venueId: String, @Query("date") date: String,
        @Query("reservationType") type: String
    ): Observable<BaseResponse<VenueOrderTime>>

    /**
     * 预订场馆
     */
    @FormUrlEncoded
    @POST(VenuesApi.VENUE_GENER_ORDER)
    fun generateOrder(@FieldMap param: HashMap<String, String>): Observable<BaseResponse<OrderResultBean>>

    /**
     * 该手机号预订过的订单数量
     * @param orderType 【必填】预订活动：CONTENT_TYPE_ACTIVITY；预订活动室：CONTENT_TYPE_ACTIVITY_SHIU
     */
    @GET(VenuesApi.VENUE_CHECK_ORDER_PHONE_NUMBER)
    fun getCheckExistOrderNumbers(
        @Query("phone") phone: String,
        @Query("orderType") orderType: String
    ): Observable<BaseResponse<OrderNumberBean>>

    /**
     * 预订活动发送验证码
     */
    @GET(VenuesApi.VENUE_ORDER_SEND_CODE)
    fun sendCode(@Query("phone") phone: String): Observable<BaseResponse<Any>>

    /**
     * 资讯列表
     */
    @GET(VenuesApi.VENUE_CONTENT_LS)
    fun getContentLs(
        @Query("orderType") orderType: String,
        @Query("linksResourceType") linksResourceType: String,
        @Query("linksResourceId") linksResourceId: String,
        @Query("pageSize") pageSize: String = "2",
        @Query("channelCode") channelCode:String? = null
    ): Observable<BaseResponse<ContentBean>>

    /**
     * 资讯列表(研学基地)
     */
    @GET(VenuesApi.STUDY_CONTENT_LS)
    fun getStudyContentLs(
        @Query("linksResourceType") linksResourceType: String,
        @Query("linksResourceId") linksResourceId: String,
        @Query("currPage") currPage: String = "1",
        @Query("pageSize") pageSize: String = "2",
        @Query("channelCode") channelCode:String? = null
    ): Observable<BaseResponse<ContentBean>>





    /**
     * 获取vip用户信息
     */
    @GET(VenuesApi.GET_VIP_INFO)
    fun getVipInfo(): Observable<BaseResponse<VipInfoBean>>

    /**
     * 获取健康信息
     * phone	电话号码	string	【必填】
     * region	地区编码	string	【必填】
     * name	名称	string	【必填】
     * idCard	身份证号码	string	【必填】
     *
     */
    @GET(VenuesApi.GET_USER_HELATH_INFO)
    fun getUserHealthInfo(
        @Query("phone") phone: String, @Query("region") region: String,
        @Query("name") name: String, @Query("idCard") idCard: String, @Query("siteId") siteId: String
    ): Observable<BaseResponse<HelathInfoBean>>

    /**
     * 获取健康信息并注册
     *  phone	电话号码	string	【必填】
     *  region	地区编码	string	【必填】
     *  name	名称	string	【必填】
     *  idCard	身份证号码	string	【必填】
     */
    @GET(VenuesApi.GET_USER_HELATH_INFO_AND_REGISTER)
    fun getUserHealthInfoAndRegister(
        @Query("phone") phone: String, @Query("region") region: String,
        @Query("name") name: String, @Query("idCard") idCard: String, @Query("siteId") siteId: String
    ): Observable<BaseResponse<HelathInfoBean>>

    /**
     * 获取出行配置信息
     */
    @GET(VenuesApi.GET_HELATH_SETING_INFO)
    fun getHealthSetingInfo(@Query("siteId") siteInfo: String, @Query("orgId") orgId: String = ""):
            Observable<BaseResponse<HelathSetingBean>>

    /**
     * 获取健康区域
     */
    @GET(VenuesApi.GET_HELATH_REGION_INFO)
    fun getHealthRegionInfo(@Query("siteId") siteId: String): Observable<BaseResponse<HelathRegionBean>>

    /**
     * 获取旅游码信息
     */
    @GET(VenuesApi.GET_HEAL_TRAVEL_INFO)
    fun getHealthTravlInfo(
        @Query("phone") phone: String,
        @Query("name") name: String, @Query("idCard") idCard: String, @Query("siteId") siteId: String
    ): Observable<BaseResponse<Boolean>>

    /**
     * 获取讲解员预约信息
     */
    @GET(VenuesApi.VENUE_GUIDE_INFO)
    fun getGuiderInfo(
        @Query("venueId") venueId: String, @Query("reservationType") reservationType: String,
        @Query("date") date: String
    ): Observable<BaseResponse<CommetaryInfoBean>>

    /**
     * 已预约订单信息
     */
    @GET(VenuesApi.VENUE_GUIDE_ORDER_AND_VENUE_ORDER)
    fun getHavedOrderInfo(@Query("orderId") orderId: String): Observable<BaseResponse<CommentaryOrderInfoBean>>

    /**
     * 获取已存在的讲解员订单信息
     */
    @GET(VenuesApi.VENUE_GUIDE_ORDER)
    fun getHavedGuideInfo(@Query("venueId") venueId: String, @Query("guideDate") guideDate: String)
            : Observable<BaseResponse<GuideOrderInfo>>

    /**
     * 用户当日预约列表
     */
    @GET(VenuesApi.GET_SELF_VALID_ORDER_LIST)
    fun getSelfValidOrderList(
        @Query("resourceType") resourceType: String, @Query("resourceId") resourceId: String
    ): Observable<BaseResponse<WriteOffsBean>>

    /**
     * 身份证识别
     */
    @FormUrlEncoded
    @POST(VenuesApi.IDCARD_IDENTIFY)
    fun postIdCardIdentify(
        @Field("type") type: String, @Field("headerUrl") headerUrl: String, @Field("nationalEmblemUrl") nationalEmblemUrl: String,
        @Field("userId") userId: String
    ): Observable<BaseResponse<IdCardIdentifyBean>>

    /**
     * 获取订单随行人订单
     * @param orderId 订单ID
     * @param token 用户标识
     */
    @GET(VenuesApi.ORDER_USER_LIST)
    fun getOrderUserList(
        @Query("orderId") orderId: String,
        @Query("token") token: String,
        @Query("siteCode") siteCode: String,
        @Query("name") name: String
    ): Observable<BaseResponse<OrderUserListBean>>


    /**
     * 获取资源栏目列表
     * @param linksResourceId 关联资源id
     * @param linksResourceType 关联资源类型
     */
    @GET(VenuesApi.RESOURCE_CHANNEL_LIST)
    fun getResourceChannelList(
        @Query("linksResourceId") linksResourceId: String,
        @Query("linksResourceType") linksResourceType: String
    ): Observable<BaseResponse<ResourceChannel>>


    /**
     * 通过 channelCode 获取资讯
     * @param channelCode 栏目代码
     */
    @GET(VenuesApi.VENUE_CONTENT_LS)
    fun getContentListByChannelCode(
        @Query("channelCode") channelCode: String
    ): Observable<BaseResponse<ContentBean>>



    /**
     * 社团列表
     * @param venueId 场馆id
     */
    @GET(VenuesApi.SOCIETIES_LIST)
    fun getSocietiesList(
        @Query("venueId") venueId: String
    ): Observable<BaseResponse<SocietiesBean>>
}