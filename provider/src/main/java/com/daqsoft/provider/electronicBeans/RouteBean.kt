package com.daqsoft.provider.electronicBeans

import java.math.BigDecimal

/**
 * 路线实体
 */
data class RouteResult(
    // 产品信息
    val productListVO: List<ProductBean>,
    // 总条数
    val totalNum: Int
)

/**
 * 产品信息
 */
data class ProductBean(
    // 当前时间时间戳毫秒
    val currentDate: String,
    // 产品id
    val id: String,
    // 下架时间
    val lowerTime: String,
    // 产品名称
    val name: String,
    // 产品价格
    val price: BigDecimal,
    // 用户是否已开启提醒 true开启提醒 false未开启提醒
    val remindStatus: Boolean,
    // 销售方式 1普通销售 2秒杀；当为秒杀时表示为活动
    val saleType: String,
    // 产品编号
    val sn: String,
    // 商品缩略图
    val thumbImageUrl: String,
    // 上架时间
    val upperTime: String,
    // 图片服务器url
    val url: String
)