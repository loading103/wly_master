package com.daqsoft.travelCultureModule.hotActivity.bean

import com.daqsoft.provider.bean.Seat

/**
 * @Description 查看当前账号的单数
 * @ClassName   OrderNumberBean
 * @Author      PuHua
 * @Time        2020/1/6 17:31
 */
data class OrderNumberBean(
    // 该手机号预订过的订单数量
    val existNum: Int
)
/**
 * @des 预订结果
 * @author PuHua
 * @Date 2020/1/12 16:34
 */
data class OrderResultBean(
    // 订单code
    val orderCode: String
)
/**
 * @des 预订后的简要信息
 * @author PuHua
 * @Date 2020/1/12 16:35
 */
data class OrderSimpleResult(
    // 创建时间
    val createTime: String?,
    // 诚信状态  诚信过滤状态 1 诚信优享 2诚信免审 0 正常
    val creditOrderType: Int,
    // 订单ID
    val id: Int,
    // 手机号
    val phone: String
)

/**
 * 订单保存实体
 */
data class OrderSaveBean(
    // 活动ID
    val activityId: String,
    // 地址
    val address: String,
    // 核销码
    val code: String,
    // 订单创建时间
    val createTime: String,
    val id: String,
    // 封面URL
    val image: String,
    // 订单编号
    val orderCode: String,
    // 订单有效期结束时间
    val orderIndateEnd: String,
    // 订单有效期开始时间
    val orderIndateStart: String,
    // 订单名称
    val orderName: String,
    // 二维码URL
    val orderQrCode: String,
    // 资源类型
    val resourceType: String,
    // 座位ID
    val seatId: String,
    // 预订人姓名
    val userName: String,
    // 预订人手机号
    val userPhone: String
)

/**
 * 座位模板详情
 */
data class SeatTemplateBean(
    // 座位信息 二维数组
    val seatInfo: List<List<Seat>>,
    // 排序: 0从左到右；1从右到左
    val sort: Int
)
