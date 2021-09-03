package com.daqsoft.provider.network.net

import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.electronicBeans.RouteResult
import com.daqsoft.provider.net.ShopResponse
import com.daqsoft.provider.network.RetrofitFactory
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * @des 小电商的网络工具
 * @author PuHua
 * @date
 */
class ElectronicRepository {
    companion object {
//        /**
//         * ip地址
//         */
//        var baseUrl:String = ""

        val electronicService: ElectronicService = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.electronicUrl)
            .addInterceptor(ElectronicHeaderInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(ElectronicService::class.java)
    }


}

/**
 * @des 免登成功后，返回的sessionId，在调用另外接口的时候，要在header里面带上
 *
 *
 *
 * @author PuHua
 * @date
 */
interface ElectronicService {

    /**
     * 免登接口
     * @param unid 用户unid
     * @param token 用户中心token
     */
    @GET(ElectronicApi.LOGIN)
    fun login(
        @Query("unid") unid: String,
        @Query("token") token: String,
        @Query("encryption") ecryption: String
    ): Observable<BaseResponse<ElectronicLogin>>

    /**
     * 获取电子票列表
     */
    @GET(ElectronicApi.ETICKET_LIST)
    fun getElectronicTickets(
        @Query("orderStatus") orderStatus: String,
        @Query("pageSize") pageSize: String,
        @Query("pageNum") pageNum: String
    ): Observable<BaseResponse<MutableList<ElectronicTicketData>>>

    /**
     * 获取消费码详情
     */
    @GET(ElectronicApi.ELECTRONIC_TICKET_DETAIL)
    fun getElectronicTicketDetail(@Query("orderId") orderId: String, @Query("type") type: String): Observable<BaseResponse<ElectronicDetailBean>>

    /**
     * 游玩人：虚拟   若bookingRecordDtoList==null   orderId取订单id   type=order
     *                若bookingRecordDtoList!=null   orderId就取list中的id   type=="booking”  consumeCode==""  type=booking
     *         门票   orderId取订单id   consumeCode取orderTicketTouristDtoList中的 若otaCode=='gds' 取值outETicket，type=ticket； 
     *        其他取值localETicket  type=gds
     * @param type 核销码类型（order虚拟，booking预约，ticket门票）
     * @param consumeCode type=ticket时，consumeCode不为空
     */
    @GET(ElectronicApi.ELECTRONIC_TICKET_QRCODE)
    fun getElectronicTicketQRCode(
        @Query("orderId") orderId: String,
        @Query("type") type: String,
        @Query("consumeCode") consumeCode: String
    ): Observable<BaseResponse<ElectronicQrCode>>

    /**
     * 获取预订列表
     * @param type 订单分类：all全部、unpaid待支付、unshipped待发货、shipped待收货、unconsumed待消费
     */
    @GET(ElectronicApi.ELECTRONIC_ORDER_LIST)
    fun getElectronicOrders(
        @Query("type") status: String,
        @Query("pageSize") pageSize: String,
        @Query("pageNum") pageNum: String,
        @Query("token") token: String
    ): Observable<ShopResponse<OrderListBean>>

    /**
     * 实物订单详情
     * @param orderId 订单id
     */
    @GET(ElectronicApi.ELECTRONIC_ORDER_DETAIL)
    fun orderDetail(
        @Query("orderId") orderId: Int
    ): Observable<ShopResponse<OrderDetailBean>>

    /**
     * 申请退款
     */
    @GET(ElectronicApi.ELECTRONIC_ORDER_REBACK_APPLY)
    fun applyElectronicReback(@Query("orderId") orderId: String)
            : Observable<BaseResponse<OrderRefund>>

    /**
     * 线路申请退款
     */
    @GET(ElectronicApi.ELECTRONIC_ROUTE_ORDER_REBACK_APPLY)
    fun applyRouteOrderRefund(@QueryMap map: HashMap<String, String>)
            : Observable<BaseResponse<OrderRefund>>

    /**
     * 门票申请退款
     */
    @GET(ElectronicApi.ELECTRONIC_TICKET_ORDER_REBACK_APPLY)
    fun applyTicketOrderRefund(@QueryMap map: HashMap<String, String>)
            : Observable<BaseResponse<OrderRefund>>

    /**
     * 酒店申请退款
     */
    @GET(ElectronicApi.ELECTRONIC_HOTEL_ORDER_REBACK_APPLY)
    fun applyHotelOrderRefund(@QueryMap map: HashMap<String, String>)
            : Observable<BaseResponse<OrderRefund>>

    /**
     * 获取退款理由
     * @param reasonType 订单类型 1实物订单 2 虚拟订单 3门票 4直通车 5酒店 6线路
     */
    @GET(ElectronicApi.ORDER_REFUND_REASON)
    fun getRefunReasons(@Query("reasonType") reasonType: String): Observable<BaseResponse<MutableList<RefundReason>>>

    /**
     * 提交退款申请
     */
    @GET(ElectronicApi.ELECTRONIC_ORDER_REBACK_VERTURL)
    fun submitRefund(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<Any>>

    /**
     * 提交酒店退款
     */
    @GET(ElectronicApi.ELECTRONIC_HOTEL_REBACK_VERTURL)
    fun submitHotelReFound(@QueryMap map: HashMap<String, String>)
            : Observable<BaseResponse<Any>>

    /**
     * 提交线路退款
     */
    @FormUrlEncoded
    @POST(ElectronicApi.ELECTRONIC_ROUTE_REBACK_VERTURL)
    fun submitRouteReFound(@FieldMap map: HashMap<String, String>)
            : Observable<BaseResponse<Any>>


    @POST(ElectronicApi.ELECTRONIC_ROUTE_REBACK_VERTURL)
    fun submitRouteReFound(@Body info: RequestBody)
            : Observable<BaseResponse<Any>>

    /**
     * 提交门票退款
     */
    @GET(ElectronicApi.ELECTRONIC_TICKET_REBACK_VERTURL)
    fun submitTicketReFound(@QueryMap map: HashMap<String, String>)
            : Observable<BaseResponse<Any>>

    /**
     * 获取门票列表
     */
    @GET(ElectronicApi.TICKET_LIST)
    fun getTicketList(@Query("resourceCode") resourceCode: String, @Query("sysCode") sysCode: String, @Query("token") token: String)
            : Observable<BaseResponse<SptTicketBean>>

    /**
     *   酒店房型商品
     */
    @GET(ElectronicApi.HOTEL_ROOM_LS)
    fun getHotelRoomLs(
        @Query("roomNum") roomNum: Int, @Query("outTime") outTime: String, @Query("inTime") inTime: String,
        @Query("sysCode") sysCode: String, @Query("resourceCode") resourceCode: String
    ): Observable<BaseResponse<MutableList<HotelRoomBean>>>

    /**
     * 酒店房间详情
     * 	outTime	离店时间	string	2019-10-25
    inTime	入店时间	string	2019-10-24
    roomSn	房型编号	string	201909121039155093
     */
    @GET(ElectronicApi.HOTEL_ROOM_DETAIL)
    fun getHotelRoomDetail(@Query("outTime") outTime: String, @Query("inTime") inTime: String, @Query("roomSn") roomSn: String):
            Observable<BaseResponse<HotelRoomInfoBean>>

    /**
     * 获取路线
     * sysCode	      系统编号	string	必填
     * limit	      查询限制条数，不填表示查询所有	number	选填
     * resourceCode	  资源编号	string	必填
     * type	          查询类型 酒店：hotel，景区：scenic	string	必填
     */
    @GET(ElectronicApi.ROUTE_LIST)
    fun getRouteList(
        @Query("resourceCode") resourceCode: String, @Query("sysCode") sysCode: String,
        @Query("type") type: String, @Query("limit") limit: String
    ): Observable<BaseResponse<RouteResult>>


    /**
     * 积分商品列表
     * @param sysCode 小电商店铺编号
     */
    @GET(ElectronicApi.INTEGRAL_PRODUCT_LIST)
    fun integralProductList(
        @Query("sessionId") sessionId: String?
    ): Observable<BaseResponse<ProductListBean>>

    /**
     * 获取美食商品列表
     */
    @GET(ElectronicApi.FOOD_PROUDCT_LIST)
    fun getFoodProductLs(@Query("resourceCode") resourceCode: String, @Query("sysCode") sysCode: String)
            : Observable<BaseResponse<MutableList<FoodProductBean>>>

    /**
     * 获取非遗商品列表
     */
    @GET(ElectronicApi.LEGACY_PRODUCT_LIST)
    fun getLegacyProductLs(
        @Query("resourceCode") resourceCode: String = "",
        @Query("sysCode") sysCode: String,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    )
            : Observable<BaseResponse<LegacyProducts>>

    /**
     * 景区预约须知
     */
    @GET(ElectronicApi.SCENIC_RESERVATIONINfO)
    fun getScenicReservationInfo(@Query("productSn") productSn: String): Observable<BaseResponse<ScenicReservationBean>>

    /**
     * 线路预订须知
     */
    @GET(ElectronicApi.ROUTER_RESERVATIONINfO)
    fun getRouterReservationInfo(
        @Query("sysCode") sysCode: String,
        @Query("productSn") productSn: String
    ): Observable<BaseResponse<RouterReservationBean>>

    /**
     * 合同内容
     */
    @GET(ElectronicApi.CONTRACT_INFO)
    fun getContractInfo(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<ContractInfo>>

    /**
     * 取消订单
     */
    @GET(UserApi.CANCEL_ORDER)
    fun cancelOrder(@Query("orderId") orderCode: String): Observable<BaseResponse<Any>>

    /**
     * 取消酒店订单
     */
    @GET(UserApi.CANCEL_HOTEL_ORDER)
    fun cancelHotelOrder(@Query("orderId") orderCode: String): Observable<BaseResponse<Any>>

    /**
     * 取消线路订单
     */
    @GET(UserApi.CANCEL_LINE_ORDER)
    fun cancelLineOrder(@Query("orderId") orderCode: String): Observable<BaseResponse<Any>>

    /**
     * 购物退款
     */
    @GET(UserApi.MINE_ORDER_REFUND)
    fun getOrderRefund(@Query("pageSize") pageSize: String, @Query("pageNum") currPage: String): Observable<BaseResponse<OrderRefundBean>>

    /**
     * 购物退款详情
     */
    @GET(UserApi.MINE_ORDER_REFUND_DETAIL)
    fun getOrderRefundDetail(@Query("refundId") refundId: String): Observable<BaseResponse<OrderRefundDetailBean>>

    /**
     * 二维码
     */
    @GET(UserApi.MINE_ORDER_TALENT_SUBSCRIBE)
    fun getTalentSubscribe(): Observable<BaseResponse<QRCodeBean>>


}