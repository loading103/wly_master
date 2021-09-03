package com.daqsoft.provider.bean

import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.network.venues.bean.AudioInfo

/**
 * 陈列bean
 */
data class VenueCollectBean(
    val address: String,
    val area: String,
    val audio: String,
    val audioTime: String,
    val briefing: String,
    val chargeStatus: Int,
    val exhibitAddress: String,
    val code: String,
    val createTime: String,
    val createUser: String,
    val exhibitsNum: Int,
    val id: String,
    val images: String,
    val introduce: String,
    val name: String,
    val panorama: String,
    val placeId: String,
    val resourceType: String,
    val ticketPolicy: String,
    val type: String,
    val typeName: String,
    val updateTime: String,
    val updateUser: String,
    val video: String,
    val videoCover: String,
    // 会员资源状态
    val vipResourceStatus: VipResourceStatus
){
    fun getRealImages():String{
        return images.getRealImages()
    }
}


data class VenueCollectDetailBean(
    val address: String,
    val exhibitionId: String,
    val exhibitionAddress: String,
    val area: String,
    val audio: String,
    val audioTime: String,
    val briefing: String,
    val chargeStatus: String,
    val code: String,
    val createTime: String,
    val createUser: String,
    val exhibitsNum: String,
    val id: String,
    var images: String,
    val introduce: String,
    val name: String,
    var panorama: String,
    val placeId: String,
    val resourceType: String,
    val ticketPolicy: String,
    val type: String,
    val typeName: String,
    val updateTime: String,
    val updateUser: String,
    var video: String,
    val latitude: String,
    val longitude: String,
    var videoCover: String,
    // 会员资源状态
    val vipResourceStatus: VipResourceStatus?
){
    fun getRealImages():String{
        return images.getRealImages()
    }
}
/**
 * 陈列头部tag
 *
 */
data class ExhibitionTagBean(

    var id:String,
    val name:String,
    var select: Boolean = false,
    var type:String="",
    var value:String="",
    val english:String=""){
}

/**
 * 文物列表bean
 */
data class CultureListBean(
    val years:String,
    val audio: String,
    val audioTime: String,
    val author: String,
    val brief: String,
    val code: String,
    val collectionInfo: String,
    val exhibitionRelationId: String,
    val exhibitionRelationName: String,
    val id: String,
    var images: String,
    val introduce: String,
    val name: String,
    val placeId: String,
    val resourceType: String,
    val size: String,
    val texture: String,
    val threeDimensionalImage: String,
    val threeDimensionalUrl: String,
    val type: String,
    val typeName: String,
    val unearthedAddress: String,
    val unearthedTime: String,
    val venueScenicType: String,
    val video: String,
    val videoCover: String,
    // 会员资源状态
    val vipResourceStatus: VipResourceStatus
){
    fun getRealImages():String{
        return images.getRealImages()
    }
}
data class CultureDetailBean(
    val years:String,
    val audio: String,
    val audioTime: String,
    val author: String,
    val brief: String,
    val code: String,
    val collectionInfo: String,
    val exhibitionRelationId: String,
    val exhibitionRelationName: String,
    val id: String,
    var images: String,
    val introduce: String,
    val name: String,
    val placeId: String,
    val resourceType: String,
    val size: String,
    val texture: String,
    val threeDimensionalImage: String,
    val threeDimensionalUrl: String,
    val type: String,
    val typeName: String,
    val unearthedAddress: String,
    val unearthedTime: String,
    val venueScenicType: String,
    var video: String,
    var videoCover: String,
    var briefing: String,
    var audioInfo: List<AudioInfo>,
    // 会员资源状态
    val vipResourceStatus: VipResourceStatus
){
    fun getRealImages():String{
        return images.getRealImages()
    }
}