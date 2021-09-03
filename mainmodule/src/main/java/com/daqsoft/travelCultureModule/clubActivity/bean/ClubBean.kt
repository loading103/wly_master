package com.daqsoft.travelCultureModule.clubActivity.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ClubBean(
    //概况
    val summary: String,
    val image: String,
    //粉丝数
    val fansNum: String,
    //	名称
    val name: String,
    val id:Int,
    //	类型
    val type:String,
    //	是否关注状态
    val resourceFansStatus: resourceFansStatusBean,
    //浏览量
    val lookNum:String
) : Parcelable

@Parcelize
data class resourceFansStatusBean(

    var fansStaus:Boolean
) : Parcelable