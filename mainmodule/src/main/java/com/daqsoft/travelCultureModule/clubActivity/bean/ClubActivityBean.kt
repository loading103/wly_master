package com.daqsoft.travelCultureModule.clubActivity.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ClubActivityBean(
    val activityStatus:String,
    val status:String,
    val sort:String,
    val address:String,
    val classifyId:String,
    val classifyName:String,
    val signStartTime:String,
    val signEndTime:String,
    val useStartTime:String,
    val useEndTime:String,
    val id:String,
    val images:String,
    val integral:String,
    val latitude:String,
    val longitude:String,
    val method:String,
    val name:String,
    val recommendChannelHomePage:String,
    val resourceId:String,
    val recommendHomePage:String,
    val siteId:String,
    val stock:String,
    val tag:String,
    val tagNames:String,
    val top:String,
    val type:String,
    val totalStock:String,
    val orgId:String,
    val recruitedCount:String,
    val faithUseStatus:String,
    val faithUseValue:String,
    val faithAuditStatus:String,
    val faithAuditValue:String,
    val jumpType:String,
    val jumpUrl:String,
    val jumpName:String,
    val money:String,
    val userResourceStatus:userResourceStatus,
    val regionName:String,
    val region:String,
    val resourceNameStr:String,
    val resourceCount:String,
    val lineFlag:String,
    val sourceSiteId:String
) : Parcelable

@Parcelize
data class userResourceStatus(
    val collectionStatus:String,
    val thumbStatus:String
): Parcelable

data class TypeBean(
    val english: String,
    val name: String,
    val id: String,
    val type: String,
    val value: String
){
    override fun toString(): String {
        return name
    }
}
