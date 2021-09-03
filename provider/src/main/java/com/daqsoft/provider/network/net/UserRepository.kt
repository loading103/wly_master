package com.daqsoft.provider.network.net

import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.bean.LocationData
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.RetrofitFactory
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*


/**
 * @des 文旅云的网络接口工具
 * @author PuHua
 * @date  1121
 */
class UserRepository {

    companion object {
        val userService: UserService = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.baseUrl)
            .addInterceptor(HeaderInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(UserService::class.java)
    }


    val userService: UserService = RetrofitFactory.Builder()
        .setBaseUrl(BaseApplication.baseUrl)
        .addInterceptor(HeaderInterceptor())
        .addConvertFactory(FaultToleranceConvertFactory.create())
        .build(UserService::class.java)
}

interface UserService {
    /**
     * 上传文件
     */
    @Multipart
    @POST(UserApi.FILE_UPLOAD)
    fun upLoad(@Part part: List<MultipartBody.Part>): Observable<UploadBean>

    /**
     * 获取站点信息
     * @param phone 电话号码
     */
    @GET(UserApi.SITE_INOF)
    fun getSiteInfo(): Observable<BaseResponse<SiteInfo>>

    /**
     * 登录/注册
     * @param code 账号
     * @param phone 手机号
     */
    @POST(UserApi.LOGIN)
    fun login(
        @Query("code") code: String,
        @Query("phone") phone: String
    ): Observable<BaseResponse<UserLogin>>

    /**
     * 注册
     * @param code 账号
     * @param phone 手机号
     */
    @POST(UserApi.REGISTER)
    fun register(
        @Query("code") code: String,
        @Query("phone") phone: String
    ): Observable<BaseResponse<RegisterData>>

    /**
     * 短信发送接口
     *
     * @param phone 电话号码
     * @param type  短信发送类型 PHONE_LOGIN -登录
     */
    @POST(UserApi.SEND_MSG)
    fun sendMsg(
        @Query("phone") phone: String,
        @Query("type") type: String
    ): Observable<BaseResponse<Any>>

    /**
     * 绑定手机号短信发送接口
     * @param phone 电话号码
     */
    @GET(UserApi.BIND_SEND_MSG)
    fun sendBindMsg(
        @Query("phone") phone: String
    ): Observable<BaseResponse<Any>>

    /**
     * 绑定手机号
     * @param phone 电话号码
     */
    @POST(UserApi.BIND_PHONE)
    fun bindPhone(
        @Query("phone") phone: String,
        @Query("code") code: String
    ): Observable<BaseResponse<Any>>

    /**
     * 短信验证验证码
     * @param phone   手机号
     * @param type    短信类型
     * @param code 验证码
     */
    @POST(UserApi.CHECK_MSG)
    fun checkMsg(
        @Query("phone") phone: String,
        @Query("type") type: String,
        @Query("code") code: String
    ): Observable<BaseResponse<Any>>

    /**
     * 刷新用户信息
     */
    @GET(UserApi.USER_INFO)
    fun getUserInformation(): Observable<BaseResponse<UserBean>>

    /**
     * 用户信息更多
     */
    @GET(UserApi.USER_INFO_MORE)
    fun getUserInformationMore(): Observable<BaseResponse<UserBean>>


    /**
     * 修改个人信息，需要的参数为需要修改的时候传递
     */
    @POST(UserApi.USER_UPDATE_USER_INFORMATION)
    fun updateUserInformation(@QueryMap map: HashMap<String, String>)
            : Observable<BaseResponse<Any>>

    /**
     * 获取省市区
     */
    @GET(UserApi.LOCATIONS)
    fun getLocations(): Observable<BaseResponse<LocationData>>

    /**
     * 获取用户星座列表
     */
    @GET(UserApi.CONSTELLATION_LIST)
    fun getConstellations(): Observable<BaseResponse<ConstellationBean>>

    /**
     * 获取用户收货地址
     */
    @GET(UserApi.RECEIVER_ADDRESS_LIST)
    fun getReceiveAddress(): Observable<BaseResponse<ReceiveAddressBean>>

    /**
     * 添加用户收货地址
     */
    @POST(UserApi.USER_ADD_ADDRESS)
    fun addReceiveAddres(
        @Query("isDefault") isDefault: Boolean,
        @Query("address") type: String,
        @Query("phone") phone: String,
        @Query("area") area: String,
        @Query("consignee") consignee: String
    ): Observable<BaseResponse<Any>>

    /**
     * 修改用户收货地址
     */
    @POST(UserApi.USER_UPDATE_ADDRESS)
    fun updateReceiveAddres(
        @Query("isDefault") isDefault: Boolean,
        @Query("address") type: String,
        @Query("phone") phone: String,
        @Query("area") area: String,
        @Query("consignee") consignee: String,
        @Query("id") id: String
    ): Observable<BaseResponse<Any>>

    /**
     * 获取用户收货地址
     */
    @GET(UserApi.USER_CONTACT_MANAGEMENT_LIST)
    fun getContactList(): Observable<BaseResponse<Contact>>

    /**
     * 删除收货地址
     */
    @GET(UserApi.USER_DELETE_ADDRESSCT)
    fun deleteAddress(@Query("id") id: String): Observable<BaseResponse<Any>>

    /**
     * 添加联系人
     */
    @POST(UserApi.USER_ADD_CONTACT)
    fun addContact(
        @Query("name") name: String,
        @Query("certType") certType: String,
        @Query("phone") phone: String,
        @Query("certNumber") certNumber: String
    ): Observable<BaseResponse<Any>>

    /**
     * 修改联系人
     */
    @POST(UserApi.USER_UPDATE_CONTACT)
    fun updateContact(
        @QueryMap map: HashMap<String, String>
    ): Observable<BaseResponse<Any>>

    /**
     * 获取证件类型列表
     */
    @GET(UserApi.USER_CERT_TYPE_LIST)
    fun getCertTypeList(): Observable<BaseResponse<ConstellationBean>>

    /**
     * 删除联系人
     */
    @GET(UserApi.USER_DELETE_CONTACT)
    fun deleteContact(@Query("id") id: String): Observable<BaseResponse<Any>>

    /**
     * 获取预订列表
     * @param status 未消费(11) 已完成(12) 已失效(13) 已取消(14)
     * @param orderType 【必填】（CONTENT_TYPE_ACTIVITY：活动，CONTENT_TYPE_ACTIVITY_SHIU：活动室  CONTENT_TYPE_VENUE 场馆及活动室）
     */
    @GET(UserApi.USER_ORDER_LIST)
    fun getOrders(
        @QueryMap map: HashMap<String, Any>,
        @Query("orderType") orderType: String
    ): Observable<BaseResponse<OrderRoom>>

    /**
     * 获取预订列表
     * @param status 未消费(11) 已完成(12) 已失效(13) 已取消(14)
     * @param orderType 【必填】（CONTENT_TYPE_ACTIVITY：活动，CONTENT_TYPE_ACTIVITY_SHIU：活动室  CONTENT_TYPE_VENUE 场馆及活动室）
     */
    @GET(UserApi.USER_ORDER_LIST)
    fun getOrdersV2(
        @QueryMap map: HashMap<String, Any>,
        @Query("orderType") orderType: String
    ): Observable<BaseResponse<OrderListDataBean>>

    @GET(UserApi.USER_ORDER_LIST)
    fun getOrderBook(
        @QueryMap map: HashMap<String, Any>,
        @Query("orderType") orderType: String
    ): Observable<BaseResponse<ActivityBookBean>>

    /**
     * 订单详情
     * @param orderId 订单编号 必填
     */
    @GET(UserApi.USER_ORDER_DETAIL)
    fun getOrderDetail(
        @QueryMap map: HashMap<String, Any>,
        @Query("orderId") orderId: String
    ): Observable<BaseResponse<OrderDetail>>

    /**
     * 核销详情
     * @param orderId 订单编号 必填
     */
    @GET(UserApi.WRITE_OFF_DETAIL)
    fun getWriteOffDetail(
        @Query("orderId") orderId: String
    ): Observable<BaseResponse<OrderDetail>>

    /**
     * 通过订单ID 获取健康信息
     */
    @GET(UserApi.HEALTH_INFO)
    fun getUserHealthInfo(@Query("orderId") orderId: String): Observable<BaseResponse<HelathInfoBean>>

    /**
     * 自主核销
     */
    @FormUrlEncoded
    @POST(UserApi.AUTONOMY)
    fun postWriteOff(@FieldMap param: HashMap<String, String>): Observable<BaseResponse<Any>>

    /**
     * 随行人列表
     */
    @GET(UserApi.ATTACH_PERSON_LIST)
    fun getOrderAttachedList(@Query("orderId") orderId: String): Observable<BaseResponse<OrderAttachPMapBean>>

    /**
     * C端实名认证保存接口
     * @param name 真实姓名
     * @param cardType    证件类型
     * @param idCard 身份证号
     * @param idCardUp 正面
     * @param idCardDown 反面
     * @param id 数据id
     */
    @FormUrlEncoded
    @POST(UserApi.SET_REAL_NAME_INFO)
    fun setRealNameInfo(
        @Field("name") name: String,
        @Field("cardType") cardType: String,
        @Field("idCard") idCard: String,
        @Field("idCardUp") idCardUp: String,
        @Field("idCardDown") idCardDown: String,
        @Field("id") id: String? = null
    ): Observable<BaseResponse<Any>>

    /**
     * 取消订单
     * @param orderCode 订单编号
     */
    @FormUrlEncoded
    @POST(UserApi.ORDER_CANCEL_ORDER)
    fun postCancelOrder(@Field("orderCode") orderCode: String): Observable<BaseResponse<Any>>

    /**
     * 刷新用户信息
     */
    @GET(UserApi.USER_INFO_REFRESH)
    fun refreshToken(): Observable<BaseResponse<UserLogin>>

    /**
     * 撤回审核
     */
    @FormUrlEncoded
    @POST(UserApi.WITH_DRAW_UPDATE)
    fun withDrawUpdate(
        @Field("id") id: Int
    ): Observable<BaseResponse<Any>>

    /**
     * 审核信息查看
     */
    @GET(UserApi.GET_REAL_NAME_INFO)
    fun getRealNameInfo(): Observable<BaseResponse<ReviewBean>>

    /**
     * 获取消费码列表
     * @param status 未消费(11) 已完成(12) 已失效(13) 已取消(14)
     */
    @GET(UserApi.CONSUME_LIST)
    fun getConsumeList(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<Consume>>

    @GET(UserApi.CONSUME_DETAIL)
    fun getConsumeDetial(@Query("orderCode") orderCode: String): Observable<BaseResponse<ConsumeDetail>>

    /**
     * 获取个人诚信分
     * @param platformChannel cultural 文化云
     * @param platformCode 平台code (siteCode)
     */
    @GET(UserApi.CREDIT_SCORE)
    fun getCreditScore(
        @Query("phone") phone: String,
        @Query("platformChannel") platformChannel: String,
        @Query("platformCode") platformCode: String
    ): Observable<BaseResponse<CreditBean>>

    /**
     * 获取用户实名认证状态
     */
    @GET(UserApi.GET_REALNAME_VALIDATE_STATUS)
    fun getRealNameValidateStatus(): Observable<BaseResponse<String>>

    /**
     * 获取个人诚信记录
     * @param platformChannel cultural 文化云
     * @param platformCode 平台code (siteCode)
     */
    @GET(UserApi.GET_USER_CURRENT_CREDIT)
    fun getCurrentCredit(
        @Query("phone") phone: String,
        @Query("platformCode") platformCode: String,
        @Query("platformChannel") platformChannel: String = "cultural"
    ): Observable<BaseResponse<CurrentCreditBean>>


    /**
     * 获取信用等级
     */
    @GET(UserApi.GET_CREDIT_LEVEL)
    fun getCreditLevel(
        @Query("pageSize") pageSize: String
    ): Observable<BaseResponse<CreditLevelBean>>

    /**
     * 获取信用记录
     */
    @GET(UserApi.GET_CREDIT_RECORDS)
    fun getCreditRecord(
        @Query("phone") phone: String,
        @Query("platformCode") platformCode: String,
        @Query("recordType") recordType: String,
        @Query("time") time: String,
        @Query("platformChannel") platformChannel: String = "cultural",
        @Query("currPage") currPage: Int,
        @Query("pageSize") pageSize: Int = 10
    ): Observable<BaseResponse<CreditScoreBean>>


    /**
     * 获取上个月信用记录
     */
    @GET(UserApi.GET_PRE_MONTH_RECORD)
    fun getPreCreditRecord(
        @Query("phone") phone: String,
        @Query("platformCode") platformCode: String,
        @Query("platformChannel") platformChannel: String = "cultural"
    ): Observable<BaseResponse<CreditPreMonthBean>>

    /**
     * 收藏的类型接口
     */
    @GET(UserApi.COLLECTION_TYPE)
    fun getCollectionType(): Observable<BaseResponse<List<String>>>

    /**
     * 收藏的列表接口
     */
    @GET(UserApi.COLLECTION_LIST)
    fun getCollectionList(
        @Query("resourceType") resourceType: String,
        @Query("currPage") currPage: Int,
        @Query("pageSize") pageSize: Int = 10
    ): Observable<BaseResponse<CollectionBean>>

    /**
     * 个人中心互动的数据
     */
    @GET(UserApi.MINE_CENTER_INFO)
    fun getMineCenterInfo(
    ): Observable<BaseResponse<MineUserInfoBean>>
    /**
     * 个人中心互动的数据
     */
    @GET(UserApi.USER_FREE_INFO)
    fun getMineUserFreeInfo(
    ): Observable<BaseResponse<MineFreeInfoBean>>

    /**
     * 收藏的列表接口
     */
    @GET(UserApi.MINE_FOCUS_MASS)
    fun getMineFocusMass(
        @Query("currPage") currPage: Int,
        @Query("pageSize") pageSize: Int = 10
    ): Observable<BaseResponse<MineFocusMassBean>>

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
    @GET(UserApi.ACTIVITY_LIST)
    fun getActivityList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<ActivityBean>>
    /**
     * 获取关注中的动态社团接口
     */
    @GET(UserApi.MINE_DYNAMIC)
    fun getMineDynamic(
        @Query("channelCode") channelCode: String
    ): Observable<BaseResponse<Any>>

    /**
     * 获取我的投诉列表
     * 【选填】 投诉未处理：4 投诉已受理：5 投诉已处理：6
     */
    @GET(UserApi.MINE_COMPLAINT_LIST)
    fun getMineComplaintList(
        @Query("status") status: String,
        @Query("currPage") currPage: Int,
        @Query("pageSize") pageSize: Int = 10
    ): Observable<BaseResponse<MineComplaintListBean>>


    /**
     * 获取我的投诉列表
     * 【选填】 投诉未处理：4 投诉已受理：5 投诉已处理：6
     */
    @GET(UserApi.MINE_COMPLAINT_DETAILS)
    fun getMineComplaintDetails(
        @Query("id") id: String
    ): Observable<BaseResponse<MineComplaintDetailsBean>>

    /**
     * 满意度反馈
     * @param satisfied 1：满意 0：不满意
     */
    @POST(UserApi.MINE_COMPLAINT_DETAILS_SATISFIED_BACK)
    fun getMineComplaintSatisfied(
        @Query("id") id: String,
        @Query("satisfied") satisfied: Int
    ): Observable<BaseResponse<Any>>

    /**
     * 获取反馈次数
     */
    @GET(UserApi.FEED_BACK_NUMS)
    fun getFeedBackNum(): Observable<BaseResponse<Int>>

    /**
     * 新增反馈
     */
    @FormUrlEncoded
    @POST(UserApi.FEED_BACK_ADD)
    fun addFeedBack(
        @FieldMap map: HashMap<String, String>
    ): Observable<BaseResponse<Any>>

    /**
     * 获取我的反馈列表
     */
    @GET(UserApi.FEED_BACK_LS)
    fun getFeedBackLs(@Query("currPage") currPage: Int, @Query("pageSize") pageSize: Int): Observable<BaseResponse<FeedBackBean>>
    /**
     * 获取我的预约列表
     */
    @GET(UserApi.MINE_APPOINT_MENT_SC)
    fun getScMineAppointMents(@QueryMap map: HashMap<String, String>)
            : Observable<BaseResponse<AppointMentBean>>


    @GET(UserApi.LEGACY_PEOPLE)
    fun getLegacyPeopleList(@Query("type") type: String = "watch"): Observable<BaseResponse<LegacyPeopleFansBean>>

    @GET(UserApi.MINE_INVITE)
    fun getMineInvite(): Observable<BaseResponse<InviteInfo>>

    /**
     * 我的邀请列表
     */
    @GET(UserApi.MINE_INVITE_LS)
    fun getMineInviteLs(@Query("pageSize") pageSize: Int, @Query("currPage") currPage: Int):
            Observable<BaseResponse<InviteBean>>

    /**
     * 绑定使用邀请码
     */
    @FormUrlEncoded
    @POST(UserApi.USER_INVITE_CODE)
    fun inputInviteCode(@Field("inviteCode") inviteCode: String): Observable<BaseResponse<InviteInputBean>>

    /**
     * 获取邀请码详情
     */
    @GET(UserApi.MINE_INVITE_CODE_INFO)
    fun getInviteCodeInfo(@Query("inviteCode") inviteCode: String): Observable<BaseResponse<InviteCodeInfo>>
    @FormUrlEncoded
    @POST(UserApi.CANCE_ACTIVITY_ORDER)
    fun canceActivityOrder(@FieldMap map: HashMap<String, String>): Observable<BaseResponse<Any>>


    /**
     * 获取消息列表
     */
    @GET(UserApi.MINE_MESSAGE_LIST)
    fun getMessageList(
        @Query("channel") channel: Int=2
    ): Observable<BaseResponse<MessageRootBean>>

    /**
     * 获取志愿者消息
     */
    @GET(UserApi.MINE_MESSAGE_VOTE)
    fun getVotMessageList(
        @Query("messageType") messageType: String="all",
        @Query("currPage") currPage: Int=1,
        @Query("pageSize") pageSize: Int = 1,
        @Query("channel") channel: Int=2
    ): Observable<BaseResponse<MessageRootBean>>


    /**
     * 获取志愿者消息
     */
    @GET(UserApi.MINE_MESSAGE_VOTE)
    fun getVotMessageList1(
        @Query("messageType") messageType: String="all",
        @Query("currPage") currPage: Int=1,
        @Query("pageSize") pageSize: Int = 10,
        @Query("channel") channel: Int=2
    ): Observable<BaseResponse<MessageListBean>>
    /**
     * 设置消息为已读
     */
    @GET(UserApi.MINE_MESSAGE_ALL_READ)
    fun ReadAllMessage(
        @Query("channel") channel: Int=2
    ): Observable<BaseResponse<String>>

    /**
     * 设置消息为已读
     */
    @GET(UserApi.MINE_MESSAGE_ALL_READ)
    fun ReadMessage(
        @Query("classify") classify: Int,
        @Query("type") type: Int,
        @Query("channel") channel: Int=2
    ): Observable<BaseResponse<String>>
    /**
     * 各类型消息条数统计详情
     */
    @GET(UserApi.MINE_MESSAGE_NUMBER)
    fun getMessageTopNumber(
        @Query("classify") classify: String,
        @Query("channel") channel: Int=2
    ): Observable<BaseResponse<MessageTopNumberBean>>

    /**
     * 获取系统消息
     */
    @GET(UserApi.MINE_MESSAGE_ALL_LIST)
    fun getMessageListData(
        @Query("classify") classify: String,
        @Query("type") type: String,
        @Query("currPage") currPage: Int=1,
        @Query("pageSize") pageSize: Int = 10,
        @Query("channel") channel: Int=2): Observable<BaseResponse<MessageListBean>>


    /**
     * 获取系统消息
     */
    @GET(UserApi.MINE_MESSAGE_NOTICE)
    fun getMessageNoticeDetail(
        @Query("id") id: String,
        @Query("channel") channel: Int=2
    ): Observable<BaseResponse<NoticeDetailBean>>
    /**
     * 获取志愿者未读数
     */
    @GET(UserApi.NO_READ_NUMBER)
    fun getNoReadMessage(
        @Query("channel") channel: Int=2
    ): Observable<BaseResponse<MineMessageBean>>
    /**
     * 刷新志愿者未读数
     */
    @GET(UserApi.NO_VOTE_NUMBER)
    fun UndateReadMessage(
        @Query("channel") channel: Int=2
    ): Observable<BaseResponse<String>>

}