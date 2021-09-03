package com.daqsoft.travelCultureModule.country.bean

import java.math.BigDecimal

/**
 * desc :
 * @author 江云仙
 * @date 2020/4/18 14:48
 */

data class FoodProductBean(
    // 商品主键
    val productId: Long,
    // 商品编号
    val productSn: String,
    // 商品名称
    val productName: String,
    // 商品类型
    val productType: Int,
    // 销售方式
    val saleType: Int,
    // 销售方式名称
    val saleTypeValue: String,
    // 商品缩略图
    val image: String,
    // 最高售卖价
    val maxSalePrice: BigDecimal,
    // 最低售卖价
    val minSalePrice: BigDecimal,
    // 市场价
    val marketPrice: BigDecimal,
    // 上架时间
    val upperTime: Long,
    // 下架时间
    val lowerTime: Long,
    // 上架倒计时
    val upperCountTime: Long,
    // 下架倒计时
    val lowerCountTime:Long,
    //
    val categoryId: String,
    // 类目名称
    val categoryName: String,
    // 售卖价
    val salePrice: BigDecimal,
    // 是否开启提醒
    var remindStatus: Boolean,
    // 商品类型
    val freightType: String,
    // 前端详情页面地址
    val url: String,
    // 是否允许退款
    val allowRefund: Boolean,
    // 免预约
    val needPrecontract: Boolean
)