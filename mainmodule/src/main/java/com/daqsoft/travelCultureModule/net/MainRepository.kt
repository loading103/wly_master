package com.daqsoft.travelCultureModule.net

import androidx.collection.ArrayMap
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.baselib.net.gsonTypeAdapters.GsonTypeAdapterFactory
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.RetrofitFactory
import com.daqsoft.provider.bean.BranchDetailBean
import com.daqsoft.provider.bean.HomeBranchBean
import com.daqsoft.provider.network.home.HomeApi
import com.daqsoft.provider.network.venues.VenuesApi
import com.daqsoft.travelCultureModule.clubActivity.bean.*
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubBean
import com.daqsoft.travelCultureModule.contentActivity.bean.ContentInfoBean
import com.daqsoft.travelCultureModule.contentActivity.bean.WatchShowBean
import com.daqsoft.travelCultureModule.hotActivity.*
import com.daqsoft.travelCultureModule.net.gson.ItRobotJsonDes
import com.daqsoft.travelCultureModule.hotActivity.bean.*
import com.daqsoft.travelCultureModule.hotActivity.bean.OrderNumberBean
import com.daqsoft.travelCultureModule.hotActivity.bean.OrderResultBean
import com.daqsoft.travelCultureModule.hotActivity.bean.OrderSaveBean
import com.daqsoft.travelCultureModule.panoramic.bean.PanoramicListBean
import com.daqsoft.travelCultureModule.redblack.bean.*
import com.daqsoft.travelCultureModule.resource.bean.LiveListBean
import com.daqsoft.travelCultureModule.search.bean.SearchBean
import com.google.gson.GsonBuilder
import com.daqsoft.travelCultureModule.sidetour.bean.GasStationBean
import io.reactivex.Observable
import retrofit2.http.*

/**
 * @des 主页的网络工具
 * @author PuHua
 * @date
 */
class MainRepository {

    companion object {

        var gson = GsonBuilder()
            .setLenient()
            .registerTypeAdapterFactory(GsonTypeAdapterFactory())
            .registerTypeAdapter(ItRobotDataBean::class.java, ItRobotJsonDes())
            .create()

        val service: MainService = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.baseUrl)
            .addInterceptor(HeaderInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create(gson))
            .build(MainService::class.java)

    }


}

/**
 * @des 主页网络api
 * @author PuHua
 * @Date 2019/12/5 14:23
 */
interface MainService {

    /**
     * @des 首页品牌展播列表
     * @author PuHua
     * @Date 2019/12/11 9:59
     */
    @GET(MainApi.HOME_BRANCH_LIST)
    fun getBranchList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<HomeBranchBean>>

    /**
     * 品牌详情接口
     */
    @GET(MainApi.HOME_BRANCH_DETAIL)
    fun getHomeBranchDetail(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<BranchDetailBean>>

    /**
     * 获取品牌景区玩乐列表
     */
    @GET(MainApi.BRAND_SCENIC_LIST)
    fun getBrandScenicList(
        @Query("brandId") ids: String,
        @Query("pageSize") pageSize: String,
        @Query("type") type: String = "RESOURCE"
    ): Observable<BaseResponse<BrandSiteInfo>>

    /**
     * 获取活动分类列表
     */
    @GET(MainApi.ACTIVITY_CLASSIFY)
    fun getActivityClassify(
    ): Observable<BaseResponse<Classify>>

    @GET(MainApi.ACTIVITY_CALENDER)
    fun getAcivityCalendarLs(@Query("time") time: String): Observable<BaseResponse<ActivityCalenderBean>>

    /**
     * 获取活动分类列表
     * @param orderType 分类
     */
    @GET(MainApi.ACTIVITY_CLASSIFY)
    fun getActivityClassify(
        @Query("orderType") orderType: String,
        @Query("region") region: String = ""
    ): Observable<BaseResponse<Classify>>

    /**
     * 获取活动分类列表
     * @param orderType 分类
     */
    @GET(MainApi.ACTIVITY_CLASSIFY)
    fun getActivityClassify(
        @QueryMap param: HashMap<String, String>
    ): Observable<BaseResponse<Classify>>


    /**
     * 获取活动列表
     * faithAuditStatus	免审状态	  number	【选填】 1 筛选 0 不筛选
     * faithUseStatus	优享状态	  number	【选填】 1 筛选 0 不筛选
     * voluntTeamId	    志愿团队ID	  number	【选填】
     * token	        令牌	      string	【必填】
     * keyword	        关键字	       string	【选填】
     * classifyId	    活动分类ID	   number	【选填】
     * region	        地区编码	   string	【选填】
     * orderType	    排序类型	   string	【选填】 为空(默认排序) 1 首页列表 2 距离优先 3 人气优先(活动模板中筛选)
     *                                                              4 最新 5 志愿团队 6 社团活动
     * methods	        活动方式	    string	【选填】
     * longitude	    经度	        string	【选填】
     * latitude	        纬度	        string	【选填】
     * associationId	社团ID	        number	【选填】
     * type	            活动类型	    string	【选填】
     * activityStatus	活动状态	    number	【选填】
     * siteId	        指定站点ID	    number	【选填】
     * resourceId	    资源ID	        string	【选填】
     * resourceType	    资源类型	    string	【选填】
     * notEndStatus	    不查询结束活动	number	【选填】默认为 0 不查询为 1
     * activityId	    活动ID	        number	【选填】 排除指定活动ID
     * monthValue	    月份值	        number	【选填】
     */
    @GET(MainApi.ACTIVITY_LIST)
    fun getActivityList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<ActivityBean>>

    /**
     * 活动概览
     */
    @GET(MainApi.ACTIVITY_CALUENDAR)
    fun getActivityCalendar(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<ActivityOverView>>


    @GET(MainApi.ACTIVITY_SUBSET)
    fun getActivitySubset(@Query("channelCode") channelCode: String): Observable<BaseResponse<SubChannelBean>>

    /**
     * 活动详情
     */
    @GET(MainApi.ACTIVITY_DETAIL)
    fun getActivityDetail(
        @Query("activityId") activityId: String,
        @Query("siteId") siteId: String
    ): Observable<BaseResponse<HotActivityDetailBean>>


    @GET(MainApi.ACTIVITY_LIST)
    fun getPreviousActivitis(@Query("alreadyActivityId") alreadyActivityId: String): Observable<BaseResponse<ActivityBean>>

    /**
     * 获取活动关联的资源--活动场地
     */
    @GET(MainApi.ACTIVITY_DETAIL_RELATIONLIST)
    fun getActivityRelationList(
        @Query("currPage") currPage: String,
        @Query("pageSize") pageSize: String,
        @Query("activityId") activityId: String
    ): Observable<BaseResponse<ActivityRelationBean>>

    /**
     * 预订活动发送验证码
     */
    @GET(MainApi.ACTIVITY_ORDER_SEND_CODE)
    fun sendCode(@Query("phone") phone: String): Observable<BaseResponse<Any>>

    /**
     * 生成订单
     * token	        令牌	                string	【必填】
     * code	            短信验证码	            string	第一次使用该手机号预订时【必填】
     * activityId	    活动 ID	                number	预订活动时【必填】
     * seatId	        座位ID	                number	预订活动的活动室 时【必填】
     * userName	        用户名称	            string	【必填】
     * userPhone	    手机号	                string	【必填】
     * orderNum	        订单数量	            number	预订活动时【必填】
     * orderType	    订单类型	            string	【必填】CONTENT_TYPE_ACTIVITY:预订活动, CONTENT_TYPE_ACTIVITY_SHIU:预订文化场馆
     * remark	        备注	                string
     * roomId	        活动室 ID	            number	预订文化场馆时 【必填】
     * roomOrderTimeId	活动室的活动预订规则ID	number	预订文化场馆时 【必填】
     * channel	        预订渠道	            string	【必填】查看常量接口
     * idCard	        身份证号	            string	【必填】
     * useNum	        使用人数	            number	预订文化场馆时 【必填】
     *
     */
    @POST(MainApi.ACTIVITY_ORDER_GENERATE)
    fun generateOrder(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<OrderResultBean>>

    /**
     * 支付订单
     */
    @POST(MainApi.ORDER_PAY)
    fun payOrder(@Query("orderCode") orderCode: String): Observable<BaseResponse<Any>>

    /**
     * 保存订单
     */
    @POST(MainApi.SAVE_ORDER)
    fun saveOrder(@Query("orderCode") orderCode: String): Observable<BaseResponse<OrderSaveBean>>

    /**
     * 该手机号预订过的订单数量
     * @param orderType 【必填】预订活动：CONTENT_TYPE_ACTIVITY；预订活动室：CONTENT_TYPE_ACTIVITY_SHIU
     */
    @GET(MainApi.ACTIVITY_CHECK_EXITST_NUMBER)
    fun getCheckExistOrderNumbers(
        @Query("phone") phone: String,
        @Query("orderType") orderType: String
    ): Observable<BaseResponse<OrderNumberBean>>

    /**
     * 站点下级区域(两层)
     */
    @GET(MainApi.SITE_CHILD_REGION)
    fun getChildRegions(): Observable<BaseResponse<ChildRegion>>

    /**
     * 获取志愿者团队列表
     */
    @GET(MainApi.VOLUNTEER_TEAM_LIST)
    fun getVolunteerTeamList(@Query("ids") id: String): Observable<BaseResponse<VolunteerTeamBean>>

    /**
     * 已占用座位列表
     */
    @GET(MainApi.USER_SEAT_SELECTED)
    fun getSelectedSeat(@Query("activityId") activityId: String): Observable<BaseResponse<Seat>>

    /**
     * 获取订单简要信息
     */
    @GET(MainApi.USER_ORDER_CHECK)
    fun getOrderResult(@Query("orderCode") orderCode: String): Observable<BaseResponse<OrderSimpleResult>>

    /**
     * 获取座位模板
     */
    @GET(MainApi.SEAT_TEMPLATE)
    fun getSeatTemplate(@Query("activityId") activityId: String): Observable<BaseResponse<SeatTemplateBean>>

    /**
     * 获取社团列表
     */
    @GET(MainApi.CLUB_LIST)
    fun getClubList(
        @Query("name") name: String,
        @Query("region") region: String,
        @Query("type") type: String,
        @Query("currPage") currPage: String,
        @Query("pageSize") pageSize: String,
        @Query("areaSiteSwitch") areaSiteSwitch: String
    ): Observable<BaseResponse<ClubBean>>

    /**
     * 获取社团详情
     */
    @GET(MainApi.CLUB_INFO)
    fun getClubInfo(@Query("id") id: Int): Observable<BaseResponse<ClubInfoBean>>

    /**
     * 获取社团活动
     *@param orderType 排序 1 首页列表 2 距离优先 3 人气优先(活动模板中筛选) 4 最新 5 志愿团队 6 社团活动
     */
    @GET(MainApi.CLUB_ACTIVITY)
    fun getClubActivityList(
        @Query("associationId") associationId: String,
        @Query("orderType") orderType: String,
        @Query("currPage") currPage: String,
        @Query("pageSize") pageSize: String
    ): Observable<BaseResponse<ClubActivityBean>>

    /**
     * 获取社团资讯列表
     * @param orderType sort：按序号排序 publishTime：按发布时间排序（精确到秒） publishDate:按发布时间排序（精确到天）
     */
    @GET(MainApi.CLUB_ZIXUN)
    fun getClubZixunList(
        @Query("linksResourceId") linksResourceId: String,
        @Query("linksResourceType") linksResourceType: String,
        @Query("orderType") orderType: String,
        @Query("pageSize") pageSize: String,
        @Query("currPage") currPage: String,
        @Query("region") region: String,
        @Query("channelCode") channelCode: String,
        @Query("areaSiteSwitch") areaSiteSwitch: String = ""
    ): Observable<BaseResponse<ClubZixunBean>>



    /**
     * 资讯列表(研学基地)
     */
    @GET(VenuesApi.STUDY_CONTENT_LS)
    fun getStudyAllContent(
        @Query("linksResourceType") linksResourceType: String,
        @Query("linksResourceId") linksResourceId: String,
        @Query("currPage") currPage: String ,
        @Query("pageSize") pageSize: String ,
        @Query("channelCode") channelCode:String? = null
    ): Observable<BaseResponse<ClubZixunBean>>
    /**
     * 获取看演出列表
     */
    @GET(MainApi.WATCH_SHOW)
    fun getWatchShow(@Query("channelCode") channelCode: String): Observable<BaseResponse<WatchShowBean>>

    /**
     * 获取搜索分类
     */
    @GET(MainApi.SEARCH_TYPE)
    fun getSearchType(@Query("type") type: String): Observable<BaseResponse<TypeBean>>

    /**
     * 获取社团资讯详情
     * @param o
     */
    @GET(MainApi.CLUB_ZIXUN_INFO)
    fun getZixunInfo(@Query("id") id: String): Observable<BaseResponse<ContentInfoBean>>


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
     * 获取社团成员列表
     */
    @GET(MainApi.CLUB_PERSON)
    fun getClubPersonList(
        @Query("id") id: String,
        @Query("page") page: String,
        @Query("pageSize") pageSize: String = "3"
    ): Observable<BaseResponse<ClubPersonBean>>


    /**
     * 获取社团成员列表
     */
    @GET(MainApi.CLUB_PERSON)
    fun getClubAllPersonList(
        @Query("id") id: String,
        @Query("currPage") currPage: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<BaseResponse<ClubPersonBean>>

    /**
     * 获取社团成员详情
     */
    @GET(MainApi.CLUB_PERSON_INFO)
    fun getClubPersonInfo(@Query("id") id: String): Observable<BaseResponse<ClubPersonInfoBean>>

    /**
     * 获取搜索历史
     */
    @GET(MainApi.SEARCH_RECORD)
    fun getSearchRecord(
        @Query("size") size: String,
        @Query("searchType") searchType: String
    ): Observable<BaseResponse<String>>

    /**
     * 搜索
     */
    @GET(MainApi.SEARCH)
    fun searchAll(
        @Query("currPage") currPage: String,
        @Query("pageSize") pageSize: String,
        @Query("keyword") keyword: String,
        @Query("searchType") searchType: String,
        @Query("closeHighlight") closeHighlight: Boolean = true
    ): Observable<BaseResponse<SearchBean>>

    /**
     * 清除搜索历史记录
     */
    @GET(MainApi.SEARCH_DELETE_RECORD)
    fun deletSearchRecord(@Query("searchType") searchType: String): Observable<BaseResponse<String>>

    /**
     * 保存搜索记录
     */
    @GET(MainApi.SEARCH_SAVE_RECORD)
    fun saveSearchRecord(
        @Query("keyword") keyword: String,
        @Query("searchType") searchType: String
    ): Observable<BaseResponse<String>>

    /**
     * 景区列表接口
     * type	     主题类型	string
     * region	 地区编码	string
     * keyWord	 景区名称	string
     * sortType	 排序规则	string	recommendHomePage （推荐排序）；hot（热度排行）；disNum(距离排行)
     * lng	     经度	string
     * lat	     纬度	string
     * crowd	 适合人群	string
     * level	 等级	string
     */
    @GET(MainApi.SCENIC_LIST)
    fun getScenicList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<ScenicBean>>


    @GET(MainApi.RESEARCH_LIST)
    fun getResearchList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<ResearchBean>>


    @GET(MainApi.SPECIAL_LIST)
    fun getSpecialList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<SpeaiclBean>>


    /**
     * lng	经度	string	非必填，填上则表示按最近距离排序
     * lat	纬度	string	非必填，填上则表示按最近距离排序
     */
    @GET(MainApi.SCENIC_SPOT_LIVES)
    fun getScenicSpotLives(): Observable<BaseResponse<SpotsLiveBean>>

    /**
     * 酒店列表接口
     * currPage	分页	number
     * pageSize		number
     * region	地区编码	string
     * keyWord	关键字名称	string
     * type	类型id	number
     * eqt	设备id	number
     * special	特殊服务id	number
     * lng	经度	number
     * lat	纬度	number
     * sortType	排序方式	string	hot (热度排序) recommendHomePage（推荐） disNum（距离排序，必须同i时带经纬度）
     */
    @GET(MainApi.HOTEL_LIST)
    fun getHotelList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<HotelBean>>

    /**
     * 获取酒店详情
     */
    @GET(MainApi.HOTEL_DETAIL)
    fun getHotelDetail(@Query("id") id: String): Observable<BaseResponse<HotelDetailBean>>

    /**
     * 获取美食列表
     */
    @GET(MainApi.FOOD_LIST)
    fun getFoodList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<FoodBean>>

    /**
     * 获取美食详情
     */
    @GET(MainApi.FOOD_DETAIL)
    fun getFoodDetail(@Query("id") id: String): Observable<BaseResponse<FoodDetailBean>>

    /**
     * 获取娱乐场所列表
     */
    @GET(MainApi.PLAYGROUNGD_LIST)
    fun getPlayGroundList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<PlayGroundBean>>
    /**
    * 获取娱乐场所详情
    */
    @GET(MainApi.PLAYGROUNGD_DETAIL)
    fun getPlayGroundetail(@Query("id") id: String): Observable<BaseResponse<PlayGroundDetailBean>>

    /**
     * 获取娱乐场所详情
     */
    @GET(MainApi.SPECIAL_DETAIL)
    fun getSpecialDetail(@Query("id") id: String): Observable<BaseResponse<SpeaiclDetailBean>>

    /**
     * 查询站点标签（云端+站点）
     *
     */
    @GET(MainApi.SELECT_LABEL)
    fun getSelectLabel(
        @Query("labelType") labelType: String, @Query("resourceType")
        resourceType: String
    )
            : Observable<BaseResponse<ResourceTypeLabel>>

    @GET(MainApi.SELECT_LABEL_VENUE)
    fun getSelectLabelVenue(
        @Query("type") type: String
    )
            : Observable<BaseResponse<ResourceTypeLabel>>


    /**
     * 获取景区详情
     */
    @GET(MainApi.SCENIC_DETAIL)
    fun getScenicDetail(@Query("id") id: String): Observable<BaseResponse<ScenicDetailBean>>


    /**
     * 获取研学基地详情
     */
    @GET(MainApi.SEREARCH_DETAIL)
    fun getResearchDetail(@Query("id") id: String): Observable<BaseResponse<ResearchDetailBean>>

    /**
     * 获取景区养生度
     */
    @GET(MainApi.SCENIC_HEALTH_INDEX)
    fun getScenicHealthIndex(@Query("areaId") areaId: String): Observable<BaseResponse<ScenicHealthBean>>

    /**
     * 获取景区舒适度
     */
    @GET(MainApi.SCENIC_COMFORT_URL)
    fun getScenicComfort(
        @Query("beginTime") beginTime: String, @Query("endTime") endTime: String, @Query(
            "resourcecode"
        ) resourcecode: String
    )
            : Observable<BaseResponse<ScenicComfortBean>>

    /**
     * 获取景区的解说音频
     */
    @GET(MainApi.SCENIC_VOICE)
    fun getScenicVoice(
        @Query("resourceId") resourceId: String, @Query("resourceType")
        resourceType: String
    ): Observable<BaseResponse<GoldStory>>

    /**
     * 获取景区品牌列表
     */
    @GET(MainApi.SCENIC_BRAND_LIST)
    fun getScenicBrandList(
        @Query("pageSize") pageSize: String,
        @Query("linksResourceId") linksResourceId: String = "", @Query("linksResourceType") linksResourceType: String = ""
    ): Observable<BaseResponse<HomeBranchBean>>

    /**
     * 获取景区品牌列表
     */
    @GET(MainApi.SCENIC_BRAND_LIST)
    fun getScenicSpotBrandList(
        @Query("pageSize") pageSize: String,
        @Query("linksResourceId") linksResourceId: String = "", @Query("linksResourceType") linksResourceType: String = ""
    ): Observable<BaseResponse<HomeBranchBean>>

    /**
     * 获取景点列表
     */
    @GET(MainApi.SCENIC_SPOTS)
    fun getScenicSpots(@Query("id") id: String, @Query("pageSize") page: String = "40"): Observable<BaseResponse<Spots>>

    /**
     * 获取查询景区景点的720全景
     * lng	经度	string	非必填，填了表示距离最近排序
     * lat	纬度	string	非必填，填了表示距离最近排序
     * region	地区编码	string
     * type	类型	string	景区主题类型 id
     */
    @GET(MainApi.SCENIC_SPOTS_PANOR)
    fun getScenicSpotsPanor(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<ScenicSpotsPanor>>


    @GET(MainApi.SCENIC_ZB)
    fun getScenicZb(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<LiveListBean>>

    /**
     * 地图模式
     * 根据类型和经纬度获取 类型信息
     * * lng	经度	string
     * lat	纬度	string
     * type 类型
     */
    @GET(MainApi.SCENIC_MAP_REC_INFO)
    fun getMapRecInfo(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<ScenicBean>>

    @GET(MainApi.MAP_RESOURCE_ICON)
    fun getMapRecIcons(): Observable<BaseResponse<MapResourceIconBean>>

    /**
     * 地图模式 厕所
     * 根据类型和经纬度获取 类型信息
     * * lng	经度	string
     * lat	纬度	string
     * type 类型
     */
    @GET(MainApi.TOIENT_MAP_REC_INFO)
    fun getMapToilentRecInfo(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<MapResBean>>

    /**加油站*/
    @GET(MainApi.GAS_STATION)
    fun getGasstations(@QueryMap param: ArrayMap<String, Any>): Observable<BaseResponse<GasStationBean>>


    /**
     * 地图模式 获取厕所详情
     * 根据类型和经纬度获取 类型信息
     * * lng	经度	string
     * lat	纬度	string
     * type 类型
     */
    @GET(MainApi.TOIENT_MAP_DETAIL)
    fun getToilentDetail(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<ToilentBean>>

    /**
     * 地图模式 停车位
     * 根据类型和经纬度获取 类型信息
     * * lng	经度	string
     * lat	纬度	string
     * type 类型
     */
    @GET(MainApi.COMMON_MAP_REC_INFO)
    fun getCommonRecInfo(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<MapResBean>>

    /**
     * 地图模式 获取停车位详情
     * 根据类型和经纬度获取 类型信息
     * * lng	经度	string
     * lat	纬度	string
     * type 类型
     */
    @GET(MainApi.PAKING_MAP_DETAIL)
    fun getParkingDetail(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<ParkingBean>>

    /**
     * 地图模式 获取医疗点位详情
     * 根据类型和经纬度获取 类型信息
     * * lng	经度	string
     * lat	纬度	string
     * type 类型
     */
    @GET(MainApi.CASERVAC_MAP_DETAIL)
    fun getCaservacDetail(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<CaservacBean>>

    /**
     * 地图模式 获取医疗点位详情
     * 根据类型和经纬度获取 类型信息
     * * lng	经度	string
     * lat	纬度	string
     * type 类型
     */
    @GET(MainApi.SHOP_MAIL_DETAIL)
    fun getShopMailDetail(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<ShopMailInfoBean>>

    /**
     * 地图模式 获取医疗点位详情
     * 根据类型和经纬度获取 类型信息
     * * lng	经度	string
     * lat	纬度	string
     * type 类型
     */
    @GET(MainApi.BUS_SHOP_DETAIL)
    fun getBusShopDetail(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<BusShopInfoBean>>

    /**
     * 获取景点详情
     *  id 景点id
     */
    @GET(MainApi.SCENIC_SPOTS_DETAIL)
    fun getScenicSpotsDetail(@Query("id") id: String): Observable<BaseResponse<Spots>>

    /**
     * 获取我的故事列表
     */
    @GET(HomeApi.STORY_VIP_LIST)
    fun getVipList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<HomeStoryBean>>

    /**
     * 删除我的故事
     */
    @FormUrlEncoded
    @POST(HomeApi.DELETE_VIP_STORY)
    fun postDeleteStory(@FieldMap map: Map<String, @JvmSuppressWildcards Any>)
            : Observable<BaseResponse<String>>

    /**
     * 获取首页故事列表
     * tagId	          标签id	number	【选填】标签id 0 查询tagId不为空的数据
     * listCover	      列表封面	number	【选填】是否列表封面1：是 0：否
     * homeCover	      首页封面	number	【选填】是否首页封面1：是 0：否
     * topicId	          话题id	number	【选填】
     * currPage	          当前页	number	【选填】默认 1
     * pageSize	          每页数量	number	【选填】默认 10
     * orderType	      排序方式	string	 默认按发布时间降序排列 likeNum:按点赞数量降序排序 likeNumAndCommentNum:按点赞+评论降序排列
     *                                                  likeNumAndCommentNumAndShowNum:按点赞数+评论数+浏览数排序
     * storyType	      故事类型	string	strategy：攻略 story：故事
     * resourceId	      资源id	number
     * resourceType	      资源类型	string
     */
    @GET(HomeApi.HOME_STORY_LIST)
    fun getStoryList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<StoreBean>>

    /**
     * 获取资源标签
     */
    @GET(MainApi.RES_LABEL)
    fun getSelectResLabel(@Query("labelType") labelType: String, @Query("resourceType") resourceType: String)
            : Observable<BaseResponse<ResourceTypeLabel>>

    /**
     *  获取娱乐类型等级
     */
    @GET(VenuesApi.VENUES_DICTTYPE)
    fun getVenueLevel(@Query("type") type: String): Observable<BaseResponse<VenueLevelBean>>
    /**
     * 获取全景漫游列表   获取查询景区景点的720全景
     *
     * lng	经度	string	非必填，填了表示距离最近排序
     * lat	纬度	string	非必填，填了表示距离最近排序
     * type	类型	string	景区主题类型 id
     *
     */
    @GET(MainApi.GET_PANORAMIC_LIST)
    fun getPanoramicList(
        @Query("areaSiteSwitch") areaSiteSwitch: String,
        @Query("type") type: String,
        @Query("lat") lat: String,
        @Query("lng") lng: String,
        @Query("currPage") currPage: String,
        @Query("pageSize") pageSize: String
    ): Observable<BaseResponse<PanoramicListBean>>



    @GET(MainApi.GET_VENUE_LIST)
    fun getVenuePanoramicList(
        @Query("areaSiteSwitch") areaSiteSwitch: String,
        @Query("type") type: String,
        @Query("lat") lat: String,
        @Query("lng") lng: String,
        @Query("currPage") currPage: String,
        @Query("pageSize") pageSize: String
    ): Observable<BaseResponse<PanoramicListBean>>
    /**
     *获取下单地址信息
     *
     */
    @GET(MainApi.ODER_ADDRES_INFO)
    fun getOrderAddressInfo(@Query("type") resourceType: String, @Query("id") id: String): Observable<BaseResponse<OderAddressInfoBean>>

    /**
     * 获取机器人问答
     * lon	经度	number	有当前位置信息的情况下优先推荐附近数据
     * lat	纬度	number
     * source	来源	string	【必填】固定值 weChat：微信 app:APP应用 pc：PC端
     * question	问题	string	【必填】
     */
    @GET(MainApi.GET_IT_ROBOT_REQUEST)
    fun getItRobotRequest(
        @QueryMap map: Map<String, String>
    ): Observable<BaseResponse<ItRobotDataBean>>


    /**
     * 获取机器人信息
     * 	source	来源	string	【必填】固定值 weChat：微信 app:APP应用 pc：PC端
     */
    @GET(MainApi.GET_IT_ROBOT_INFO)
    fun getItRobotInfo(@Query("source") source: String = "app"): Observable<BaseResponse<ItRobotBean>>

    /**
     * 获取vip用户信息
     */
    @GET(MainApi.GET_VIP_INFO)
    fun getVipInfo(): Observable<BaseResponse<VipInfoBean>>

    /**
     * 获取大讲堂列表
     */
    @GET(MainApi.UNIVERSITY_LS)
    fun getUniversityLs(@Query("channelCode") channelCode: String = "djt1"): Observable<BaseResponse<WatchShowBean>>

    /**
     * 课程类型
     */
    @GET(MainApi.LECTURE_HALL_TYPE)
    fun getLectureHallType(): Observable<BaseResponse<LectureType>>

    /**
     * 课程列表
     */
    @GET(MainApi.LECTURE_HALL_LIST)
    fun getLectureHallList(@QueryMap map: Map<String, String>): Observable<BaseResponse<LectureHall>>

    /**
     * 课程详情
     * @param courseId 课程id
     */
    @GET(MainApi.LECTURE_HALL_DETIALL)
    fun getLectureHallDetail(@Query("courseId") courseId: String): Observable<BaseResponse<LectureHallDetailBean>>

    /**
     * 课程提问
     * @param courseId 课程Id
     */
    @FormUrlEncoded
    @POST(MainApi.LECUTRE_HALL_REQUEST)
    fun postLectureHallReqeust(@Field("courseId") courseId: String, @Field("question") question: String): Observable<BaseResponse<Any>>

    /**
     * 课程记录
     * @param chapterId 章节Id
     * @param duration 学习时长
     */
    @FormUrlEncoded
    @POST(MainApi.LECTURE_HALL_RECOUDER)
    fun postLectureHallRecorder(@Field("chapterId") chapterId: String, @Field("duration") duration: String):
            Observable<BaseResponse<Any>>

    /**
     * 课程章节列表
     * @param courseId 课程id
     */
    @GET(MainApi.LECTURE_HALL_CHAPTER)
    fun getLectureHallChapterLs(@Query("courseId") courseId: String): Observable<BaseResponse<LectureHallChapterBean>>

    /**
     * 课程提问列表
     * @param courseId 课程id
     */
    @GET(MainApi.LECTURE_HALL_REQUESTION_LS)
    fun getLectureHallRequestionls(
        @Query("courseId") courseId: String,
        @Query("currPage") currPage: String,
        @Query("pageSize") pageSize: String
    ): Observable<BaseResponse<LectureRequestion>>

    /** 我的课程列表
     * @param pageSize 页面大小
     * @param currPage 页码
     */
    @GET(MainApi.MINE_LECTURE_LIST)
    fun getMineLectureList(@Query("pageSize") pageSize: String, @Query("currPage") currPage: String): Observable<BaseResponse<LectureHall>>

    /**
     * 我的课程统计
     */
    @GET(MainApi.MINE_LECTURE_RECORD_TOTAL)
    fun getMineLectureRecoder(): Observable<BaseResponse<LectureRecord>>

    /**
     * 我的提问
     * @param pageSize 页面大小
     * @param currPage 页码
     */
    @GET(MainApi.MINE_LECUTRE_REQ)
    fun getMineLectureReq(@Query("pageSize") pageSize: String, @Query("currPage") currPage: String): Observable<BaseResponse<LectureRequestion>>

    /**
     * 红黑榜单图片
     */
    @GET(MainApi.GET_RED_BG)
    fun getRedBlackBgUrl(@Query("channelCode") channelCode: String): Observable<BaseResponse<RedBgBean>>

    /**
     * 地区榜单
     */
    @GET(MainApi.GET_AREA_DATA)
    fun getAreaListData(
        @Query("region") region: String,
        @Query("currPage") currPage: String,
        @Query("pageSize") pageSize: String): Observable<BaseResponse<AreaListBeanItem>>

    /**
     * 资源榜单
     */
    @GET(MainApi.GET_RESOURE_DATA)
    fun getReSoureListData(
        @Query("region") region: String,
        @Query("resourceType") resourceType: String,
        @Query("sortType") sortType: String,
        @Query("currPage") currPage: String,
        @Query("pageSize") pageSize: String): Observable<BaseResponse<ResoureListBeanItem>>
}