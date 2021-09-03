package com.daqsoft.provider.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Description
 * @ClassName   HelathSetingBean
 * @Author      luoyi
 * @Time        2020/7/9 9:47
 */
@Parcelize
data class HelathSetingBean(
    /**
     * 预约选项	string
     */
    var reserveOption: String? = "",
    /**
     * 数据id
     */
    var id: Int,
    /**
     * 是否启用旅游码
     */
    var enableTravelCode: Boolean,
    /**
     * 是否启用健康码
     */
    var enableHealthyCode: Boolean,
    /**
     * 介绍
     */
    var healthIntroduce: String,
    /**
     * 旅游码名称
     */
    var smartTravelName: String
) : Parcelable

/**
 * @Description 健康码信息
 * @ClassName   HelathSetingBean
 * @Author      luoyi
 * @Time        2020/7/9 9:47
 */
@Parcelize
data class HelathInfoBean(
    /**
     * 智游码id 不为空代表已注册
     */
    var userId: String?,
    /**
     * 地区编码
     */
    var region: String?,
    /**
     * 健康说明
     */
    var healthMSG: String?,
    /**
     * 健康状态 01 正常 11 黄码 31红码
     */
    var healthCode: String?,
    var regionName: String?,
    var smartTravelRegisterStat: Boolean,
    var smartTravelRegisterStatus: Boolean,
    var smartTravelCodeRegisterStatus: Boolean,
    var enableTravelCode: Boolean,
    var enableHealthyCode: Boolean,
    var smartTravelName: String?,
    var healthRegionAddress: String?,
    var healthSetingBean: HelathSetingBean?
) : Parcelable

/**
 * 健康地址数据
 */
data class HelathRegionBean(
    /**
     * 使用地区（为空表示是地区码取region字段）
     */
    var useRegion: String,
    /**
     *地区编码
     */
    var region: String,
    /**
     * 名称
     */
    var name: String,
    /**
     * 数据id
     */
    var id: Int,
    /**
     * 健康码注册地址
     */
    var healthRegisterUrl: String,
    /**
     * 健康码开放注册状态
     */
    var healthRegisterOpen: Boolean
) {
    fun getCurrentRegion(): String {
        if (useRegion.isNullOrEmpty()) {
            if (!region.isNullOrEmpty()) {
                return region
            }
            return ""
        }
        return useRegion
    }
}