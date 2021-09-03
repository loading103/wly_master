package com.daqsoft.provider.bean

import android.os.Parcelable
import com.daqsoft.provider.network.venues.bean.AudioInfo
import kotlinx.android.parcel.Parcelize

/**
 * 乡村详情
 */
@Parcelize
data class CountryDetailBean(
    var activity: List<ActivityBean>,
    var address: String,
    var audio: String,
    var audioInfo: MutableList<AudioInfo> = mutableListOf(),
    var audioTime: String,
    var bestTravel: String,
    var briefing: String,
    var code: String,
    var collectionNum: Int,
    var commentNum: Int,
    var cutRegionName: String,
    var id: Int,
    var images: String,
    var introduce: String,
    var isCharge: Int,
    var latitude: Double,
    var likeNum: Int,
    var longitude: Double,
    var name: String,
    var openTimeEnd: String,
    var openTimeStart: String,
    var panoramaUrl: String,
    var panoramaCover: String? = "",
    var phone: String,
    var region: String,
    var remarks: String,
    var resourceCode: String,
    var siteId: Int,
    var spotsNum: Int,
    var suggestedTime: String,
    var suggestedTimeHour: Int,
    var ticketPolicy: String,
    var trafficInfo: String,
    var video: String,
    var videoCover: String,
    var summary: String,
    var vipResourceStatus: VipResourceStatus
) : Parcelable