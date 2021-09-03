package com.daqsoft.provider.bean

import com.daqsoft.provider.electronicBeans.ProductBean
import java.math.BigDecimal

/**
 * @Description 门票集合
 * @ClassName   SptTicketBean
 * @Author      luoyi
 * @Time        2020/4/16 10:16
 */
data class SptTicketBean(
    /**
     * 销量
     */
    val sales: Int,
    /**
     * 票型列表
     */
    val sptTitleList: MutableList<ScenicTiketBean>
    ,
    /**
     * 是否需要人脸识别
     */
    val needFaceRecognition: Boolean
)

/**
 * @Description  景区门票
 * @ClassName   ScenicTiketBean
 * @Author      luoyi
 * @Time        2020/4/16 10:16
 */
data class ScenicTiketBean(
    /**
     * 商品列表
     */
    val productList: MutableList<ScenicTiketProductBean>,
    /**
     * 最低售卖价
     */
    val minSellPrice: BigDecimal,
    /**
     * 票型名称
     */
    val sptTitle: String,
    /**
     * 是否活动
     */
    val isActivity: Boolean,
    /**
     * 是否线上商品全部
     */
    var isShowProduct:Boolean
)

/**
 * @Description  景区门票
 * @ClassName   ScenicTiketBean
 * @Author      luoyi
 * @Time        2020/4/16 12:29
 */
data class ScenicTiketProductBean(
    /**
     * 下架倒计时
     */
    val lowerIntervalTimes: Double,
    /**
     * 今日是否可定（直接展示）
     */
    val purchaseLimit: String,
    /**
     * 是否开启提醒
     */
    val isOnSale: Boolean,
    /**
     * 是否活动秒杀
     */
    val isActivity: Boolean,
    /**
     * 是否可退（直接展示）
     */
    val allowRefund: String,
    /**
     * 开售时间
     */
    val saleTime: Long,
    /**
     * 是否需要人脸识别
     */
    val needFaceRecognition: Boolean,
    /**
     * 商品名称
     */
    val productName: String,
    /**
     * 开始倒计时（秒杀商品）
     */
    val intervalTimes: Long,
    /**
     * 销售价
     */
    var sellPrice: BigDecimal,
    /**
     * 商品编号
     */
    val goodsSn: String,
    /**
     * 商品id
     */
    val productId: Long,
    /**
     * 是否需要取票（直接展示）
     */
    val needTicket: String,
    /**
     * 结束倒计时（秒杀）
     */
    val lowerTime: Long
)