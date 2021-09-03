package com.daqsoft.venuesmodule.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Description 场馆预约订单数据
 * @ClassName   VenueResOrderModel
 * @Author      luoyi
 * @Time        2020/7/10 11:11
 */
@Parcelize
data class VenueResOrderModel(
    /**
     * 场馆预约类型
     */
    var type: Int = 0,
    /**
     * 场馆id
     */
    var venueId: String? = "",
    /**
     * 场馆预约日期
     */
    var date: String? = "",
    /**
     * 预约时间段
     */
    var timesId: String? = "",
    /**
     * 手机号
     */
    var phone: String? = "",
    /**
     * 短信验证码
     */
    var smsCode: String? = "",
    /**
     * 身份证号
     */
    var idCardNum: String? = "",
    /**
     * 公司名称
     */
    var companyName: String? = "",
    var userNum: String? = "",
    var name: String? = "",
    var timeStr: String? = "",
    var healthCodeRegion: String? = "",
    var attachedJsonStr: String? = "",
    var cardType:String?=""
) : Parcelable

