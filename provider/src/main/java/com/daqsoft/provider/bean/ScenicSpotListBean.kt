package com.daqsoft.provider.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 景观点列表
 */
@Parcelize
data class ScenicSpotListBean(
    var address: String,
    var audio: String,
    var audioTime: String,
    var briefing: String,
    var id: Int,
    var images: String,
    var introduce: String,
    var latitude: Double,
    var longitude: Double,
    var name: String,
    var openTimeEnd: String,
    var openTimeStart: String,
    var panoramaUrl: String,
    var phone: String,
    var remarks: String,
    var resourceCode: String,
    var ruralId: Int,
    var siteId: Int,
    var suggestedTime: String,
    var ticketPolicy: String
) : Parcelable