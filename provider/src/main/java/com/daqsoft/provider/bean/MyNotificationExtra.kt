package com.daqsoft.provider.bean

import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import kotlinx.android.parcel.Parcelize

/**
 *  classify = 1;
content = "自定义内容";
relationId = 555;
resourceType = "CONTENT_TYPE_STORY";
title = "自定义标题";
type = 1;
 */
@Parcelize
data class MyNotificationExtra(

    val classify : String?,
    val content :String?,
    val relationId : String?,
    val resourceType:String?,
    val type:String?,
    val id : String?,
    val title : String?,
    val jumpType : String?,
//    val messageType: String?,
//    val resourceStatus: String?,

    val jumpUrl : String?


) : Parcelable {
}

@Parcelize
data class MessageInfo(
    val empId : String,
    val messageId : String
) : Parcelable {
}
