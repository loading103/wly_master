package com.daqsoft.provider.network.home

import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.RetrofitFactory
import com.daqsoft.provider.network.home.bean.*
import com.daqsoft.thetravelcloudwithculture.home.bean.*
import com.daqsoft.travelCultureModule.story.bean.TopicBean
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*
import java.util.*
import kotlin.collections.HashMap

/**
 * @des 主页的网络工具
 * @author PuHua
 * @date
 */
class HomeRepository {

    companion object {
        /**
         * ip地址
         */
//        var baseUrl:String = ""
        val service: HomeService = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.baseUrl)
            .addInterceptor(HeaderInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(HomeService::class.java)

    }


}

/**
 * @des 主页网络api
 * @author PuHua
 * @Date 2019/12/5 14:23
 */
interface HomeService {
    /**
     *@param clientType 客户端类型 APP
     * @param location HOME_TOP HOME_BOTTOM 使用见Constant
     */
    @GET(HomeApi.APP_MENU_LIST)
    fun getAPPMenuList(
        @Query("clientType") clientType: String,
        @Query("location") location: String
    ): Observable<BaseResponse<HomeMenu>>

    /**
     * @param clientType 客户端类型 APP
     * @param location HOME_TOP
     * @param areaSiteSwitch 城市siteID
     */
    @GET(HomeApi.APP_MENU_LIST)
    fun getCityMenuList(
        @Query("clientType") clientType: String,
        @Query("location") location: String,
        @Query("areaSiteSwitch") areaSiteSwitch: String
    ): Observable<BaseResponse<HomeMenu>>

    /**
     * 获取首页菜单模块列表
     */
    @GET(HomeApi.HOME_MODULE_LIST)
    fun getHomeModule(@Query("location") location: String): Observable<BaseResponse<HomeModule>>

    /**
     * 城市名片(列表包含当前城市)
     */
    @GET(HomeApi.HOME_CITY_CARDS)
    fun getCityCardS(@Query("region") region: String?): Observable<BaseResponse<CityCardBean>>

    /**
     * 城市名片
     */
    @GET(HomeApi.HOME_CITY_CARD)
    fun getCityCard(@Query("siteId") siteId: String): Observable<BaseResponse<CityCardDetail>>


    /**
     * 获取活动分类列表
     */
    @GET(HomeApi.ACTIVITY_CLASSIFY)
    fun getActivityClassify(
    ): Observable<BaseResponse<Classify>>

    /**
     * 获取活动列表
     */
    @GET(HomeApi.ACTIVITY_LIST)
    fun getActivityList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<ActivityBean>>


    /**
     * 获取活动列表
     */
    @GET(HomeApi.HOME_FOUND_AROUND)
    fun getFoundList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<FoundAroundBean>>

    /**
     * 导览详情
     */
    @GET(HomeApi.GUIDE_DETAIL)
    fun getGuideDetail(@Query("id") id: String): Observable<BaseResponse<GuideProviderScenicDetailBean>>

    /**
     * 导览详情
     */
    @GET(HomeApi.GUIDE_HOME)
    fun getGuideHomeDetail(@Query("tourId") id: String): Observable<BaseResponse<TourHomeBean>>

    /**
     * 获取预约活动室列表
     */
    @GET(HomeApi.ACTIVITY_ROOM)
    fun getActivityRoomList(
    ): Observable<BaseResponse<ActivityRoomBean>>

    /**
     * 获取活动室详情
     */
    @GET(HomeApi.ACTIVITY_ROOM_DETAIL)
    fun getActivityRoomDetail(@Query("id") id: String): Observable<BaseResponse<ActivityRoomBean>>

    /**
     * 获取活动室解说音频
     */
    @GET(HomeApi.ACTIVITY_ROOM_VOICE)
    fun getActivityRoomVoice(
        @Query("resourceId") resourceId: String,
        @Query("resourceType") resourceType: String = "CONTENT_TYPE_ACTIVITY_SHIU"
    ): Observable<BaseResponse<ActivityRoomAudioBean>>


    @GET(HomeApi.ACTIVITY_SEND_CODE)
    fun getSendCode(@Query("phone") phone: String): Observable<BaseResponse<String>>

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
    @POST(HomeApi.ACTIVITY_ORDER_GENERATE)
    fun generateOrder(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<OrderResultBean>>

    /**
     * 支付订单
     */
    @POST(HomeApi.ORDER_PAY)
    fun payOrder(@Query("orderCode") orderCode: String): Observable<BaseResponse<Any>>

    /**
     * 保存订单
     */
    @POST(HomeApi.SAVE_ORDER)
    fun saveOrder(@Query("orderCode") orderCode: String): Observable<BaseResponse<OrderSaveBean>>

    /**
     * 该手机号预订过的订单数量
     * @param orderType 【必填】预订活动：CONTENT_TYPE_ACTIVITY；预订活动室：CONTENT_TYPE_ACTIVITY_SHIU
     */
    @GET(HomeApi.ACTIVITY_CHECK_EXITST_NUMBER)
    fun getCheckExistOrderNumbers(
        @Query("phone") phone: String,
        @Query("orderType") orderType: String
    ): Observable<BaseResponse<OrderNumberBean>>


    /**
     * @des 首页品牌展播列表
     * @author PuHua
     * @Date 2019/12/11 9:59
     */
    @GET(HomeApi.HOME_BRANCH_LIST)
    fun getBranchList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<HomeBranchBean>>

    /**
     * 首页获取话题列表
     */
    @GET(HomeApi.HOME_TOPIC_LIST)
    fun getTopicList(
        @Query("size") size: String = "",
        @Query("typeName") typeName: String = ""
    ): Observable<BaseResponse<HomeTopicBean>>

    /**
     * 获取首页故事标签列表
     */
    @GET(HomeApi.HOME_STORY_TAG_LIST)
    fun getHotStoryTagList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<HomeStoryTagBean>>

    /**
     * 获取用户创建或使用过的标签列表
     */
    @GET(HomeApi.MINE_START_TAG_LIST)
    fun getVIPTagList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<TagBean>>

    /**
     * 获取常用标签列表
     */
    @GET(HomeApi.HOME_STORY_TAG_RECORD_LIST)
    fun getHomeTagRecordList(): Observable<BaseResponse<String>>

    /**
     * 获取故事标签列表
     */
    @GET(HomeApi.STORY_TAG_LIST)
    fun getStoryTagList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<HomeStoryTagBean>>

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
    fun getStoryList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<HomeStoryBean>>


    /**
     * 获取故事封面
     * tagId	    标签id	    number
     * listCover	列表封面	number	1: 查询标签列表封面数据
     * homeCover	主页封面	number	1:查询时光首页封面数据
     */
    @GET(HomeApi.STORY_COVER)
    fun getStoryCover(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<HomeStoryBean>>

    /**
     * 获取首页故事封面
     * tagId	    标签id	    number
     * listCover	列表封面	number	1: 查询标签列表封面数据
     * homeCover	主页封面	number	1:查询时光首页封面数据
     */
    @GET(HomeApi.STORY_COVER)
    fun getHomeStoryCover(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<String>>

    /**
     * 首页广告
     */
    @GET(HomeApi.HOME_AD_URL)
    fun getHomeAd(
        @Query("publishChannel") publishChannel: String,
        @Query("adCode") adCode: String
    ): Observable<BaseResponse<HomeAd>>

    /**
     * 首页获取内容（安逸走四川等）
     */
    @GET(HomeApi.HOME_CONTENT_LIST)
    fun getHomeContentList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<HomeContentBean>>

    /**
     * 首页获取精品线路tag 列表
     */
    @GET(HomeApi.HOME_CHANNEL_TAG_LIST)
    fun getHomeLineTagList(@Query("channelCode") channelCode: String): Observable<BaseResponse<LineTagBean>>

    /**
     * 智能行程
     */
    @GET(HomeApi.HOME_QNW_LIST)
    fun getHomeQNWList(): Observable<BaseResponse<HomeContentBean>>

    /**
     *
     */
    @GET(HomeApi.HOME_CHANNEL_LIST)
    fun getHomeChannelList()

    /**
     * 品牌详情目的地
     */
    @GET(HomeApi.HOME_BRACNCH_DETAIL)
    fun getHomeBranchDetail(
        @Query("pageSize") pageSize: String,
        @Query("brandId") brandId: String,
        @Query("type") type: String
    ): Observable<BaseResponse<BrandSiteInfo>>

    /**
     * 目的地城市，县区
     */
    @GET(HomeApi.HOME_BRACNCH_LIST)
    fun getMDDCity(
        @Query("pageSize") pageSize: String,
        @Query("type") type: String,
        @Query("siteId") siteId: String = ""
    ): Observable<BaseResponse<BrandMDD>>

    /**
     * 获取地区天气
     */
    @GET(HomeApi.CITY_WEATHER)
    fun getWeather(@Query("region") region: String): Observable<BaseResponse<WeatherBean>>

    /**
     * 获取标签详情
     */
    @GET(HomeApi.STORY_TAG_DITAIL)
    fun getStoryTagDetail(@Query("tagId") adCode: String): Observable<BaseResponse<HomeStoryTagDetail>>

    /**
     * 获取故事详情
     */
    @GET(HomeApi.STORY_DETAIL)
    fun getStoryDetail(@Query("id") id: String): Observable<BaseResponse<HomeStoryBean>>

    /**
     * 获取点赞列表
     */
    @GET(HomeApi.RESOURCE_THUMB_LIST)
    fun getThumbList(
        @Query("resourceId") resourceId: String,
        @Query("resourceType") resourceType: String
    ): Observable<BaseResponse<ThumbBean>>

    /**
     * 获取我的故事
     */
    @GET(HomeApi.STORY_DETAIL_MINE)
    fun getMineStoryDetail(@Query("id") id: String): Observable<BaseResponse<HomeStoryBean>>

    /**
     * 获取添加地点（搜索周边）
     * @param pageSize 页面大小
     * @param keyword 关键字
     * @param longitude 经度
     * @param latitude 纬度
     * @param distance 距离
     * @param currPage 当前页
     */
    @GET(HomeApi.SEARCH_AROUND)
    fun getSearchAround(
        @Query("pageSize") pageSize: Int,
        @Query("keyword") keyword: String,
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Query("distance") distance: Int,
        @Query("currPage") currPage: Int,
        @Query("closeHighlight") closeHighlight: Boolean = true
    ): Observable<BaseResponse<ItemAddressBean>>

    /**
     * 发布故事攻略
     * content	      故事内容	string	故事必填
     * latitude	      故事纬度	number
     * storyType	  类型	string	【必填】story:故事，strategy:攻略
     * resourceType	  故事资源类型	number
     * resourceId	  故事资源id	number
     * status	      数据状态	number	3：保存为草稿
     * strategy	      攻略信息（JSON数组）	array<object>	攻略必填
     * contentType	  内容类型	string	CONTENT：内容 IMAGE：图片 VIDEO:视频
     * title	      标题	string
     * videoCover	  视频封面图	string
     * resourceType	  资源类型	string
     * resourceId	  资源id	number
     * content	      内容	string
     * video	      故事视频	string
     * token	      token	string	【必填】
     * longitude	  故事经度	number
     * title	      故事或攻略标题	number	攻略必填
     * topicId	      话题id	number
     * images	      故事图集	string
     * cover	      封面图	string	攻略必填
     * id	          故事/攻略id	number	修改时必填
     * videoCover	  视频封面图	string
     */
    @FormUrlEncoded
    @POST(HomeApi.POST_STORY_STRATEGY)
    fun postStoryStrategy(@FieldMap map: Map<String, @JvmSuppressWildcards Any>)
            : Observable<BaseResponse<String>>

    /**
     * 获取话题列表接口
     * name	        话题名称	string
     * recommend	推荐	number	1推荐 0不推荐
     * top	        置顶	number
     * orderType	排序方式	string	sort：按顺序升序 createTime:按创建时间降序
     * topicStatus	话题状态	number	话题状态 0：未开始 1：进行中 2：已结束
     */
    @GET(HomeApi.GET_TOPIC_LIST)
    fun getTopicList(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<HomeTopicBean>>


    /**
     * 获取话题详情接口
     * id 话题id
     */
    @GET(HomeApi.GET_TOPIC_DETAIL)
    fun getTopicDetail(@Query("id") id: String): Observable<BaseResponse<TopicBean>>

    /**
     * 获取故事检测不通过原因
     * @param id    故事ID
     */
    @GET(HomeApi.GET_NO_PASS_MSG)
    fun getNoPassMsg(@Query("id") id: String): Observable<BaseResponse<NoPassMsgBean>>


    /**
     * 保存投诉
     * @param map    资源
     */
    @POST(HomeApi.SAVE_COMPLAINT)
    fun saveComplaint(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<Any>>

    /**
     * 被投诉方对象资源
     * @param map    资源
     */
    @GET(HomeApi.COMPLAINT_RESOURCE_SEARCH)
    fun complaintResourceSearch(
        @Query("currPage") currPage: Int,
        @Query("keyword") keyword: String,
        @Query("resourceType") resourceType: String,
        @Query("region") region: String,
        @Query("pageSize") pageSize: Int = 10,
        @Query("closeHighlight") closeHighlight: Boolean = true
    ): Observable<BaseResponse<ComplaintResourceBean>>


    /**
     * 获取当前积分
     */
    @GET(HomeApi.HOME_CURR_POINT)
    fun getCurrPoint(
    ): Observable<BaseResponse<UserCurrPoint>>

    /**
     * 签到状态和未领取任务数
     */
    @GET(HomeApi.HOME_POINT_TASK_INFO)
    fun getPointTaskInfo(
    ): Observable<BaseResponse<UserPointTaskInfoBean>>


    /**
     * 签到
     */
    @POST(HomeApi.HOME_USER_CHECK_IN)
    fun getUserCheckIn(
    ): Observable<BaseResponse<Any>>

    /**
     * 获取问候语
     * region	用户当前位置地区编码	string
     * token	用户token	string
     * lon	经度	number
     * lat	纬度	number
     * distance	距离	number	默认：1 单位：km
     * sign	唯一标识	string	【必填】建议生成uuid值，在页面没有清除缓存前该值生成后不改变
     */
    @GET(HomeApi.GET_IT_ROBOT_GREETINGS)
    fun getItRobotGreetings(
        @QueryMap map: Map<String, String>
    ): Observable<BaseResponse<ItRobotGreeting>>


    /**
     * 获取下拉刷新文案
     * @return Observable<BaseResponse<String>>
     */
    @GET(HomeApi.GET_PULL_DOWN_TO_REFRESH_TIP)
    fun getPullDownToRefreshTip(): Observable<BaseResponse<PullDownToRefreshTip>>


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
    fun getStoryListNeW(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<StoreBean>>


    @GET(HomeApi.EXHIBIT_LIST)
    fun getExhibitionShowList(
        @Query("name") name: String?=null,
        @Query("onId") onId: Int,
        @Query("currPage") currPage: Int=1,
        @Query("pageSize") pageSize: Int = 10,
        @Query("type") type: String="exhibition_always"
    ): Observable<BaseResponse<VenueCollectBean>>

    /**
     * 文物列表
     */
    @GET(HomeApi.CULTURE_LIST)
    fun getCultureList(
        @Query("onId") onId: Int =0,
        @Query("type") type: String="",
        @Query("name") name: String="",
        @Query("exhibitionId") exhibitionId: Int=0,
        @Query("currPage") currPage: Int=1,
        @Query("pageSize") pageSize: Int = 10
    ): Observable<BaseResponse<CultureListBean>>


    /**
     * 文物列表（推荐）
     */
    @GET(HomeApi.CULTURE_LIST)
    fun getCultureListTJ(
        @Query("onId") onId: Int =0,
        @Query("type") type: String="",
        @Query("name") name: String="",
        @Query("currPage") currPage: Int=1,
        @Query("pageSize") pageSize: Int = 4
    ): Observable<BaseResponse<CultureListBean>>


    @GET(HomeApi.EXHIBIT_LIST_ONLINE)
    fun getExhibitionOnlineList(
        @Query("type") type: String,
        @Query("name") name: String,
        @Query("onId") onId: Int,
        @Query("currPage") currPage: Int,
        @Query("pageSize") pageSize: Int = 10,
        @Query("panorama") panorama: Int=1
    ): Observable<BaseResponse<VenueCollectBean>>


    @GET(HomeApi.EXHIBIT_DETAIL)
    fun getExhibitionDetail(
        @Query("id") id: Int
    ): Observable<BaseResponse<VenueCollectDetailBean>>

    @GET(HomeApi.CULTURE_DETAIL)
    fun getCultureDetail(
        @Query("id") id: Int
    ): Observable<BaseResponse<CultureDetailBean>>


    @GET(HomeApi.CULTURE_TOP)
    fun getCultureTopList(
        @Query("type") type: String
    ): Observable<BaseResponse<ExhibitionTagBean>>


    /**
     * 获取问卷调查列表
     */
    @GET(HomeApi.QUSESTION_LIST)
    fun getQuestionListNeW(
        @Query("name") name: String,
        @Query("currPage") currPage: Int,
        @Query("pageSize") pageSize: Int = 10
    ): Observable<BaseResponse<QuestionListBean>>

    /**
     * 问卷调查提交
     */
    @POST(HomeApi.QUSESTION_SUBMIT)
    fun getQuestionSubmit(
        @Body body: RequestBody
    ): Observable<BaseResponse<SubmitResultBean>>



    @FormUrlEncoded
    @POST(HomeApi.QUSESTION_SUBMIT)
    fun getQuestionSubmit(@FieldMap param: HashMap<String, String>): Observable<BaseResponse<SubmitResultBean>>
    /**
     * 获取用户列表
     */
    @GET(HomeApi.QUSESTION_USER_LIST)
    fun getQuestionUserListNeW(
        @Query("currPage") currPage: Int,
        @Query("pageSize") pageSize: Int = 10
    ): Observable<BaseResponse<QuestionListBean>>
    /**
     * 获取结果统计
     */
    @GET(HomeApi.QUSESTION_RESULT)
    fun getQuestionResult(
        @Query("groupCode") groupCode: String?=null,
        @Query("paperId") paperId: String?=null
    ): Observable<BaseResponse<QuestionSubmitBean>>
    /**
     * 获取问卷调查列表
     */
    @GET(HomeApi.QUSESTION_DETAIL)
    fun getQuestionDetail(
        @Query("paperId") paperId: String
    ): Observable<BaseResponse<QuestionSubmitRoot>>


}

//