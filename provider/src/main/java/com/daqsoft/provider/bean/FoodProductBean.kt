package com.daqsoft.provider.bean

import java.math.BigDecimal


/**
 * @Description 美食商品
 * @ClassName   FoodProductBean
 * @Author      luoyi
 * @Time        2020/4/11 14:17
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
    val upperTime: String,
    // 下架时间
    val lowerTime: String,
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
    val remindStatus: Boolean,
    // 商品类型
    val freightType: String,
    // 前端详情页面地址
    val url: String,
    // 是否允许退款
    val allowRefund: Boolean,
    // 预约说明
    val needPrecontract: String
)

data class LegacyProducts(
    val displayStyle: String,
    val isFirstPage: Boolean,
    val isLastPage: Boolean,
    val pageNum: Int,
    val pageSize: Int,
    val pages: Int,
    val total: Int,
    val list:List<LegacyProductBean>
)


/**
 *@package:com.daqsoft.legacyModule.bean
 *@date:2020/5/16 16:21
 *@author: caihj
 *@des:非遗商品实体类
 **/
data class LegacyProductBean (
    val needPrecontract:Boolean,
    val allowRefund:Boolean,
    val url:Boolean,// 前端详情页面地址
    val freightType:Boolean, // 商品类型
    val categoryName:String, // 类目名称
    val categoryId:Int,
    val lowerCountTime:Int, // 下架倒计时
    val upperCountTime:Int,// 上架倒计时
    val lowerTime:Int, // 下架时间
    val upperTime:Int,// 上架时间
    val businessName:String, // 商家
    val marketPrice:String, // 市场价
    val image:String,
    val saleTypeValue:String,
    val saleType:String,
    val productType:String,
    val productName:String,
    val productSn:String,
    val productId:String,
    val qyCode:String,
    val salePrice:String
)