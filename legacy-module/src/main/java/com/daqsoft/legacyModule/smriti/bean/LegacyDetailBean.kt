package com.daqsoft.legacyModule.smriti.bean

import com.daqsoft.provider.bean.GoldStory
import com.daqsoft.provider.network.venues.bean.AudioInfo

/**
 * desc :
 * @author 江云仙
 * @date 2020/4/24 11:56
 */
data class LegacyDetailBean(
    var audioInfo:ArrayList<AudioInfo>,
    var commentNum:Int,
    var goldStory:GoldStory,
    var id:String,
    var images:String,
    var introduce:String,
    var level:String,
    var likeNum:Int,
    var likeStatus:Int,
    var name:String,
    var peopleNum:String,
    var regionName:String,
    var reportCompany:String,
    var resourceCode:String,
    var resourceType:String,
    var showNum:String,
    var storyNum:String,
    var type:String,
    var video:String,
    var videoCover:String,
    var voice:String,
    var voiceTime:String,
    var batch: String,
    var collectionNum: Int,
    var collectionStatus: Int

){
    fun getCollectionStatus():Boolean = collectionStatus == 1

    fun getLikeStatus():Boolean = likeStatus == 1
}
