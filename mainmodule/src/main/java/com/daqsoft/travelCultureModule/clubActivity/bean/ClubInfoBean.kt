package com.daqsoft.travelCultureModule.clubActivity.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ClubInfoBean(
    //收藏数
    var collectionNum: String?,
    //地区
    val regionName: String?,

    //标志图片
    val icon: String?,
    //	等级
    val associaLevel: String?,
    //	视频
    val video: String?,
    //类型
    val type: String?,
    var likeNum: String?,
    //地区
    val dutyPerson: String?,
    //	名称
    val startTime: String?,
    //标志图片
    val id: String?,
    //	等级
    val associaSummary: String?,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val associaAudio: String?,
    val images: String?,
    val manageUnit: String?,
    val address: String?,
    val fansNum:Int?,
    val introduce: String?,
    val resourceFansStatus: resourceFansStatus?,
    val commentNum: String?,
    val resourceStatus: resourceStatus?,
    val phone: String?,
    val name: String?,
    val videoCover: String?,
    val teamPerson: Int?

) : Parcelable

@Parcelize
data class resourceFansStatus(
    var fansStaus: Boolean
) : Parcelable

@Parcelize
data class resourceStatus(
    var likeStatus: Boolean,
    var collectionStatus: Boolean
) : Parcelable

@Parcelize
class curActivit(
) : Parcelable