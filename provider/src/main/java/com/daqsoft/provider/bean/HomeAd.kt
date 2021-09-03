package com.daqsoft.provider.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Description 首页广告
 * @ClassName   HomeAd
 * @Author      PuHua
 * @Time        2019/12/18 18:20
 */
data class HomeAd(
    val adInfo: List<AdInfo>,
    val positionMethod: String
)

/**
 * @des 广告的详细信息
 * @author PuHua
 * @Date 2019/12/18 18:20
 */
@Parcelize
data class AdInfo(
    val adId: Int,
    val imgUrl: String? = "",
    val jumpUrl: String? = "",
    val resourceId: String? = "",
    val type: String? = "",
    val resourceType: String? = ""
) : Parcelable