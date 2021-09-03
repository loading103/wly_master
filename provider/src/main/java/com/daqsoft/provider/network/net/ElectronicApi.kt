package com.daqsoft.provider.network.net

/**
 * @des 小电商的api
 * @author PuHua
 * @date
 */
object ElectronicApi {

    /**
     * 免登接口
     */
    const val LOGIN = "user/free"
    /**
     * 消费码列表
     */
    const val ETICKET_LIST = "eticket/eTicketList"
    /**
     * 消费码详情
     */
    const val ELECTRONIC_TICKET_DETAIL = "eticket/eticketInfo"
    /**
     * 消费码二维码
     */
    const val ELECTRONIC_TICKET_QRCODE = "eticket/eticketQrcodeUrl"
    /**
     * 我的订单列表
     */
    const val ELECTRONIC_ORDER_LIST = "userOrder/myOrderList"
    /**
     *
     * 实物订单详情
     */
    const val ELECTRONIC_ORDER_DETAIL = "userOrder/orderDetail"
    /**
     * 虚拟订单退款提交
     */
    const val ELECTRONIC_ORDER_REBACK_VERTURL = "userOrder/orderRefundSubmit"
    /**
     * 门票订单退款提交
     */
    const val ELECTRONIC_TICKET_REBACK_VERTURL = "userOrder/ticketOrderRefundSubmit"
    /**
     * 酒店订单退款提交
     */
    const val ELECTRONIC_HOTEL_REBACK_VERTURL = "userOrder/hotelOrderRefundSubmit"
    /**
     * 线路订单退款提交
     */
    const val ELECTRONIC_ROUTE_REBACK_VERTURL = "userOrder/routeOrderRefundSubmit"
    /**
     * 订单退款申请
     */
    const val ELECTRONIC_ORDER_REBACK_APPLY = "userOrder/orderRefundApply"
    /**
     * 线路订单退款申请
     */
    const val ELECTRONIC_ROUTE_ORDER_REBACK_APPLY = "userOrder/routeOrderRefundApply"
    /**
     * 酒店订单退款申请
     */
    const val ELECTRONIC_HOTEL_ORDER_REBACK_APPLY = "userOrder/hotelOrderRefundApply"
    /**
     * 门票订单退款申请
     */
    const val ELECTRONIC_TICKET_ORDER_REBACK_APPLY = "userOrder/ticketOrderRefundApply"
    /**
     * 确认收货--实物订单
     */
    const val ELECTRONIC_ORDER_CONFIRM_RECEIVE = "userOrder/receiptGood"
    /**
     * 退款理由
     *
     *  注意这里保留全路径
     */
    const val ORDER_REFUND_REASON = "/common/1.0/orderrefundreason/list"

    /**
     * 门票列表
     */
    const val TICKET_LIST = "product/ticketListForResourceCode"
    /**
     * 酒店房型
     */
    const val HOTEL_ROOM_LS ="product/hotelRoomListForResourceCode"
    /**
     * 酒店商品详情
     */
    const val HOTEL_ROOM_DETAIL ="product/hotelRoomDetail"
    /**
     * 路线列表
     */
    const val ROUTE_LIST = "product/getRouteList"

    /**
     * 预订界面
     */
    const val ELECTRONIC_WEB = "/ucenter/appointment?id="

    /**0.
     * 积分商品列表地址
     */
    const val INTEGRAL_PRODUCT_LIST = "member/integralProductList"

    /**
     * 美食&虚拟商品
     */
    const val FOOD_PROUDCT_LIST="product/commodityListForResourceCode"

    /**
     * 非遗商品
     */
    const val LEGACY_PRODUCT_LIST="product/heritageListForResourceCode"


    /**
     * 景区预约说明
     */
    const val SCENIC_RESERVATIONINfO="product/reservationInfo"
    /**
     * 线路预订须知
     */
    const val ROUTER_RESERVATIONINfO="product/getRouteReservationNotes"
    /**
     * 合同内容
     */
    const val CONTRACT_INFO="/customer/1.0/route/getContractInfo"

}