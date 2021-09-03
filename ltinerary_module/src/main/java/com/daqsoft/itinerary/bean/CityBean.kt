package com.daqsoft.itinerary.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CityBean(
    val coverImage: String,
    val h5Domain: String,
    val id: Int,
    val images: String,
    val jumpMode: Int,
    val jumpUrl: String,
    val logo: String,
    val name: String,
    val positionFlag: Boolean,
    val region: String,
    val regionMemo: String,
    val regionName: String,
    val scenicCount: Int,
    val scenicNameStr: String,
    val siteLabelNames: String,
    val slogan: String,
    var suggestedDay: Int,
    val videoImage: String,
    val videoUrl: String,
    var isSelected: Boolean = false
) : Parcelable {
    constructor() : this(
        "",
        "",
        0,
        "",
        0,
        "",
        "null",
        "",
        false,
        "",
        "",
        "全部",
        0,
        "",
        "",
        "",
        0,
        "",
        ""
    )
}