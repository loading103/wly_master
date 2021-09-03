package com.daqsoft.volunteer.net

import android.util.Log
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.network.RetrofitFactory
import com.daqsoft.provider.network.home.HomeApi
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean
import com.daqsoft.volunteer.bean.*
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*

/**
 *@package:com.daqsoft.volunteer.net
 *@date:2020/6/1 15:06
 *@author: caihj
 *@des:TODO
 **/
internal class VolunteerRepository {
    companion object{
        val service: VolunteerService = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.baseUrl)   //13340951622
            .addInterceptor(HeaderInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(VolunteerService::class.java)

    }
}

internal fun logE(info: String, tag: String = "VolunteerModule") {  //信息太长,分段打印

    var msg = info

    //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，

    //  把4*1024的MAX字节打印长度改为2001字符数

    val max_str_length = 2001 - tag.length

    //大于4000时

    while (msg.length > max_str_length) {

        Log.e(tag, msg.substring(0, max_str_length))

        msg = msg.substring(max_str_length)

    }

    //剩余部分

    Log.e(tag, msg)

}

interface VolunteerService{

    /**
     * 根据地区编码获取志愿者统计数
     */
    @GET(VolunteerApi.VOLUNTEER_COUNT)
    fun getVolunteerCount(@Query("region") region:String):Observable<BaseResponse<VolunteerCountBean>>

    /**
     * 获取我的关注列表
     * @param queryType 查询方式 默认 我的关注 1 团队详情
     */
    @GET(VolunteerApi.VOLUNTEER_FOCUS)
    fun getMyVolunteerFocusList(
        @Query("queryType") queryType:String = "",
        @Query("teamId") teamId:String = ""
    ):Observable<BaseResponse<VolunteerTeamBean>>

    /**
     * 获取志愿者、志愿者团队排行榜
     *@param teamId	团队id
     *@time	榜单时间	number	默认最近统计时间
     *@listTypeEnum	榜单类型	string	【必填】week:周榜 month:月榜 quarter:季度榜单 year:年榜单
     *@rankingTypeEnum	排名类型	string	【必填】integral:积分排名 serviceTime:服务时长排名
     *@listClassEnum	榜单分类	string	【必填】volunteer:志愿者榜单 volunteerTeam:志愿者团队榜单 volunteerTeamMember:志愿者团队成员排名榜单
     *@pageSize	每页shul	number	默认 10
     *@currPage	页码	number	默认 1
     *@region	地区编码	string
     */
    @GET(VolunteerApi.VOLUNTEER_RANK_LIST)
    fun getVolunteerRankList(
        @QueryMap param:HashMap<String,String>
    ):Observable<BaseResponse<VolunteerRankBean>>

    /**
     * 个人排行榜
     * time	榜单时间	number
     *listTypeEnum	榜单类型	string	【必填】week:周榜 month:月榜 quarter:季度榜单 year:年榜单
     *listClassEnum	榜单分类	string	【必填】volunteer:志愿者榜单 volunteerTeam:志愿者团队榜单
     *rankingTypeEnum	排名类型	string	【必填】integral:积分排名 serviceTime:服务时长排名
     */
    @GET(VolunteerApi.VOLUNTEER_RANK)
    fun getVolunteerRank(
        @QueryMap param:HashMap<String,String>
    ):Observable<BaseResponse<RankData>>

    /**
     * 志愿招募（志愿活动）
     * @param status 【选填】 3 招募中 4 待审核 7 未通过
     * activityId	活动ID	number	【选填】 排除指定活动ID
     *notEndStatus	不查询结束活动	number	【选填】默认为 0 不查询为 1
     *resourceType	资源类型	string	【选填】
     *resourceId	资源ID	string	【选填】
     *siteId	指定站点ID	number	【选填】
     *activityStatus	活动状态	number	【选填】
     *type	活动类型	string	【选填】 ACTIVITY_TYPE_VOLUNT 志愿者招募
     *associationId	社团ID	number	【选填】
     *latitude	纬度	string	【选填】
     *longitude	经度	string	【选填】
     *methods	活动方式	string	【选填】
     *orderType	排序类型	string	【选填】 为空(默认排序) 1 首页列表 2 距离优先 3 人气优先(活动模板中筛选) 4 最新 5 志愿团队 6 社团活动 7 非遗活动
     *region	地区编码	string	【选填】
     *classifyId	活动分类ID	number	【选填】
     *keyword	关键字	string	【选填】
     *token	令牌	string	【选填】
     *voluntTeamId	志愿团队ID	number	【选填】
     *faithUseStatus	优享状态	number	【选填】 1 筛选 0 不筛选
     *faithAuditStatus	免审状态	number	【选填】 1 筛选 0 不筛选
     *alreadyActivityId	活动ID 查询往期活动	number	【选填】
     *queryRegion	查询地区编码	string	【选填】 外部对接必填
     *queryType	查询类型	number	【选填】 默认列表 1 我的关注
     *endTime	结束时间	string	【选填】yyyy-MM-dd
     *startTime	开始时间	string	【选填】 yyyy-MM-dd
     *year	年	string	【选填】
     *belongBrand	品牌ID	number	【选填】 品牌ID
     */
    @GET(VolunteerApi.VOLUNTEER_ACTIVITY_LIST)
    fun getVolunteerActivityList(
        @QueryMap param:HashMap<String,String>
    ):Observable<BaseResponse<VolunteerActivityBean>>

    /**
     * 获取志愿者资讯
     * orderType	排序方式	string	sort：按序号排序 publishTime：按发布时间排序（精确到秒） publishDate:按发布时间排序（精确到天）
     * top	置顶	number
     *recommendChannelHomePage	推荐栏目首页	number
     *recommendHomePage	推荐首页	number
     *title	内容标题	string
     *channelId	栏目id	number
     *channelCode	栏目代码	string   // 志愿资讯 Constant 类中的const val HOME_CONTENT_TYPE_volunteerNews = "volunteerNews"
     *pageSize	每页数量	number
     *currPage	当前页	number
     *linksResourceType	关联资源类型	string
     *linksResourceId	关联资源id	number
     *tagId	标签Id	number
     *
     */
    @GET(HomeApi.HOME_CONTENT_LIST)
    fun getVolunteerNewsList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<HomeContentBean>>

    /**
     * 获取志愿者列表
     *pageSize	每页数量	number	默认 10
     *currPage	页码	number	默认 1
     *sortType	排序类型	string	integralDesc:按积分降序排列 serviceTimeDesc:按服务时长降序排列 serviceNumDesc:按服务次数降序排列
     *level	等级	number	0-5
     *region	地区筛选	string
     */
    @GET(VolunteerApi.VOLUNTEER_LIST)
    fun getVolunteerList(@QueryMap param: HashMap<String, String>):Observable<BaseResponse<VolunteerBean>>

    /**
     * 获取志愿者参数类型
     * @param dictType EDUCATION:学历,HEALTH_STATUS:健康状态,LANGUAGE:语言,NATION:民族,POLITICS_FACE:政治面貌,PROFESSION:职业,SERVICE_INTENTION:服务意向,SPECIALTY:特长,VOLUNTEER_SERVICE_TYPE:服务类型
     */
    @GET(VolunteerApi.VOLUNTEER_TYPE)
    fun getVolunteerType(@Query("dictType") dictType:String):Observable<BaseResponse<String>>

    /**
     * 志愿者归属列表
     */
    @GET(VolunteerApi.VOLUNTEER_REGIONS)
    fun getVolunteerRegions(@Query("region") region:String):Observable<BaseResponse<VolunteerRegion>>

    /**
     * 志愿者注册申请
     *serviceIntention	服务意向	string	【必填】多个英文逗号隔开 最多10个
     *language	擅长语言	string	多个英文逗号隔开 最多10个
     *specialty	特长	string	【必填】多个英文逗号隔开 最多10个
     *personalProfile	个人简介	string	个人简介最多填写100个字符
     *address	地址	string	最多填写200个字符
     *region	地区	string
     *qq	QQ号码	string	最多填写20个字符
     *email	邮箱	string
     *serviceTimeType	服务时间类型	string	【必填】WorkingDay:工作日 RestDay:休息日 Unlimited:不限
     *healthStatus	健康状态	string
     *emergencyContactPhone	紧急联系人电话	string	【必填】
     *emergencyContactName	紧急联系人名称	string	【必填】
     *employmentStatus	就业状态	string	【必填】
     *discipline	专业	string	最多填写100个字符
     *school	学校	string	最多填写100个字符
     *education	学历	string
     *attribution	志愿者归属	string	【必填】
     *serviceRegion	服务地区编码	string	【必填】
     *phone	电话号码	string	【必填】
     *idCardNationalEmblem	身份证国徽面图片	string	【必填】
     *idCardPortrait	身份证人像面图片	string	【必填】
     *idCard	身份证号码	string	【必填】
     *nation	民族	string	【必填】
     *name	名称	string	【必填】
     *head	头像	string
     *
     *
     */
    @FormUrlEncoded
    @POST(VolunteerApi.VOLUNTEER_REGISTER)
    fun volunteerRegister(@FieldMap param: HashMap<String, String>):Observable<BaseResponse<String>>

    @Headers("Content-type:application/json;charset=UTF-8")
    @POST(VolunteerApi.VOLUNTEER_UPDATE_INFO)
    fun updateVolunteerInfo(@Body requestBody: RequestBody):Observable<BaseResponse<String>>

    /**
     * 志愿者团队注册
     *brandItemIds	品牌项目id集合	string	多个英文逗号隔开
     *teamPhone	团队电话	string	【必填】
     *teamIntroduce	团队介绍	string
     *serviceRegion	服务地区	string	【必填】
     *attribution	志愿归属	number	【必填】
     *teamName	团队名称	string	【必填】
     *serviceType	服务类型	string	【必填】多个英文逗号隔开 最多3个
     *establishTime	成立时间	string	【必填】
     *manageUnit	主管单位	string
     *teamIcon	团队图标	string
     *teamRegion	团队所在地区	string	【必填】
     *teamAddress	团队地址	string	【必填】
     *longitude	经度	string	【必填】
     *latitude	纬度	string	【必填】
     *
     */
    @POST(VolunteerApi.VOLUNTEER_TEAM_REGISTER)
    fun volunteerTeamRegister(@FieldMap param: HashMap<String, String>):Observable<BaseResponse<String>>

    /**
     * 取消申请
     */
    @GET(VolunteerApi.VOLUNTEER_CANCEL_APPLY)
    fun volunteerCancel():Observable<BaseResponse<String>>

    /**
     * 取消申请
     */
    @POST(VolunteerApi.VOLUNTEER_TEAM_CANCEL_APPLY)
    fun volunteerTeamCancel():Observable<BaseResponse<String>>

    /**
     * 获取团队列表
     * currPage	页码	number	默认1
     *teamName	团队名称	string
     *region	团队所在地区编码	string
     *level	等级	number
     *teamSortType	排序类型	string	memberNumDesc：团队成员人数降序，serviceNumDesc：服务次数降序，serviceTimeDesc：服务时长降序，integralNumDesc：总积分降序
     *teamFilterType	筛选类型	string	signIn：可以签到，existActivity：有活动
     *pageSize	每页数量	number	默认10
     */
    @GET(VolunteerApi.VOLUNTEER_TEAM_LIST)
    fun getVolunteerTeamList(@QueryMap param: HashMap<String, String>):Observable<BaseResponse<VolunteerTeamListBean>>

    /**
     * 获取团队详情
     */
    @GET(VolunteerApi.VOLUNTEER_TEAM_DETAIL)
    fun getVolunteerTeamDetail(@Query("id") id:String):Observable<BaseResponse<VolunteerTeamDetailBean>>

    /**
     * 获取志愿者详情
     */
    @GET(VolunteerApi.VOLUNTEER_DETAIL)
    fun getVolunteerDetail(@Query("id") id:String):Observable<BaseResponse<VolunteerBean>>

    /**
     * 获取志愿者所属团队列表
     */
    @GET(VolunteerApi.VOLUNTEER_DETAIL_TEAM_LIST)
    fun getVolunteerTeams(@Query("volunteerId") volunteerId:String):Observable<BaseResponse<VolunteerTeamListBean>>

    /**
     * 获取服务记录
     *volunteerId	志愿者ID	number	【选填】查询指定志愿者服务记录
     */
    @GET(VolunteerApi.VOLUNTEER_SERVICE_LIST)
    fun getVolunteerServices(@Query("volunteerId") volunteerId:String = ""):Observable<BaseResponse<VolunteerServiceRecordBean>>

    /**
     * 志愿者团队签到列表
     * pageSize	每页数量	number	默认10
     *currPage	页码	number	默认1
     *dayOfWeek	日期筛选	string	1-7 分别对应 周一至周日
     *dateOfDay	时间筛选	string	morning：上午，afternoon：下午，night：晚上
     *teamRegion	团队所在地区编码	string
     *teamName	团队名称	string
     */
    @GET(VolunteerApi.VOLUNTEER_TEAM_SIGN_LIST)
    fun getVolunteerTeamSignList(@QueryMap param: HashMap<String, String>):Observable<BaseResponse<VolunteerTeamSignInBean>>

    /**
     *
     * sortType	排序方式	number	【选填】0. 默认 1. 最新 2. 人气优先 3. 点赞 4. 评论
     *region	地区编码	string	【选填】 同城传用户region
     *teamId	团队ID	number	【选填】
     *queryType	查询方式	number	【选填】 默认资源列表 1 我的关注
     * 志愿者风采列表
     */
    @GET(VolunteerApi.VOLUNTEER_SERVICE_RECORD_LIST)
    fun getVolunteerServiceRecordList(@QueryMap param: HashMap<String, String>):Observable<BaseResponse<ServiceRecordBean>>


    /**
     * 获取志愿服务记录详情ss
     *id	数据ID	string	【选填】
     *queryType	查询方式	number	【选填】 审核详情 1
     */
    @GET(VolunteerApi.VOLUNTEER_SERVICE_RECORD_DETAIL)
    fun getVolunteerServiceRecordDetail(@QueryMap param: HashMap<String, String>):Observable<BaseResponse<ServiceRecordDetailBean>>

    /**
     * 志愿品牌列表
     */
    @GET(VolunteerApi.VOLUNTEER_BRAND_LIST)
    fun getVolunteerBrandList(
        @Query("pageSize") pageSize:Int = 10,
        @Query("currPage") currPage:Int = 1
    ):Observable<BaseResponse<VolunteerBrandBean>>

    /**
     * 志愿品牌详情
     */
    @GET(VolunteerApi.VOLUNTEER_BRAND_DETAIL)
    fun getVolunteerBrandDetail(@Query("id") id:String):Observable<BaseResponse<VolunteerBrandDetailBean>>

    /**
     * 获取团队成员
     * currPage	页码	number	默认1
     *pageSize	每页数量	number	默认10
     *teamId	团队Id	number	【必填】
     *manage	是否是查询负责人	boolean	true:查询团队负责人 false：查询非团队负责人 不填查询所有
     *nameSort	是否按名称排序	boolean	true 按名称排序
     */
    @GET(VolunteerApi.VOLUNTEER_TEAM_MEMBER_LIST)
    fun getVolunteerTeamMembers(@QueryMap param: HashMap<String, String>):Observable<BaseResponse<VolunteerMemberBean>>

    /**
     * 获取签到月历
     * teamId	团队id	number	【必填】
     *yearMoth	年月	string	默认当月 格式 yyyy-MM-dd
     */
    @GET(VolunteerApi.VOLUNTEER_SIGN_DATE)
    fun getVolunteerSignDate():Observable<BaseResponse<VolunteerSignApplyBean>>

    /**
     * 签到申请
     * applyServiceDate	申请服务时间	string	【必填】yyyy-MM-dd
     *teamId	团队id	number	【必填】
     *dateOfDay	服务时间段	string	morning，afternoon，night
     */
    @POST(VolunteerApi.VOLUNTEER_SIGN_APPLY)
    fun postVolunteerSignApply(@FieldMap param: HashMap<String, String>):Observable<BaseResponse<String>>

    /**
     * 站点下级区域(两层)
     */
    @GET(VolunteerApi.SITE_CHILD_REGION)
    fun getChildRegions(): Observable<BaseResponse<ChildRegion>>

    /**
     * 短信发送接口
     *
     * @param phone 电话号码
     * @param type  短信发送类型 PHONE_LOGIN -登录
     */
    @POST(VolunteerApi.SEND_CODE)
    fun sendMsg(
        @Query("phone") phone: String,
        @Query("type") type: String
    ): Observable<BaseResponse<String>>


    /**
     * 校验验证码
     *
     * @param phone 电话号码
     * @param type  短信发送类型 PHONE_LOGIN -登录
     */
    @POST(VolunteerApi.VALIDATE_CODE)
    fun validateCode(
        @Query("phone") phone: String,
        @Query("code") code: String,
        @Query("type") type: String
    ): Observable<BaseResponse<String>>

    /**
     * 获取志愿者资料
     */
    @GET(VolunteerApi.VOLUNTEER_BRIEF_INFO)
    fun getVolunteerInfo():Observable<BaseResponse<VolunteerBriefInfoBean>>

    /**
     * 获取团队完整信息
     */
    @GET(VolunteerApi.VOLUNTEER_TEAM_INFO)
    fun getVolunteerTeamInfo():Observable<BaseResponse<VolunteerTeamInfoBean>>

    /**
     * 获取志愿者基本信息
     */
    @GET(VolunteerApi.VOLUNTEER_BASIC_INFO)
    fun getVolunteerBasicInfo():Observable<BaseResponse<VolunteerBasicBean>>

    /**
     * 获取志愿者操作状态
     */
    @GET(VolunteerApi.VOLUNTEER_OPERATE_STATUS)
    fun getVolunteerOperateStatus():Observable<BaseResponse<OperateStatusBean>>

    /**
     * 获取志愿者团队操作状态
     */
    @GET(VolunteerApi.VOLUNTEER_TEAM_OPERATE_STATUS)
    fun getVolunteerTeamOperateStatus():Observable<BaseResponse<OperateStatusBean>>

    /**
     * 获取审核记录
     */
    @GET(VolunteerApi.VOLUNTEER_AUDIT_LOG)
    fun getVolunteerAuditLog():Observable<BaseResponse<OperateStatusBean>>

}