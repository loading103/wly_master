package com.daqsoft.legacyModule.smriti.bean

import com.daqsoft.provider.bean.GoldStory
import com.daqsoft.provider.network.venues.bean.AudioInfo

/**
 *@package:com.daqsoft.legacyModule.smriti.bean
 *@date:2020/5/15 16:55
 *@author: caihj
 *@des:基地详情实体类
 **/
data class LegacyBaseDetailBean(
    val activityNumber:Int, // 体验活动人数
    val address:String,
    val audioInfo: ArrayList<AudioInfo>,
    val baseRegionName:String,
    val capacity:Int,// 容纳人数
    var collectionNum: Int,
    var collectionStatus: Int,
    val commentNum:Int,
    val contactPhone:String,
    val examineNumber:String,
    val goldStory: GoldStory,
    val heritageItemId:String,
    val heritageItemName:String,
    val id:String,
    val images:String,
    val introduce:String,
    val latitude:String,
    val level:String,
    var likeNum:Int,
    var likeStatus:Int,
    val longitude:String,
    val name:String,
    val reportCompany:String,
    val resourceCode:String,
    var resourceType:String,
    var video:String,
    var videoCover:String,
    var voice:String,
    var voiceTime:String,
    val region:String
) {
    fun getCollectionStatus():Boolean = collectionStatus == 1

    fun getLikeStatus():Boolean = likeStatus == 1
}