package com.daqsoft.legacyModule.smriti.bean

import com.daqsoft.provider.bean.GoldStory
import com.daqsoft.provider.network.venues.bean.AudioInfo

/**
 * desc :非遗传承人详情实体类
 * @author caihj
 * @date 2020/5/15 11:56
 */
data class LegacyPeopleDetailBean(
    var audioInfo:ArrayList<AudioInfo>,
    var batch: String,
    var collectionNum: Int,
    var collectionStatus: Int,
    var commentNum:Int,
    var fans:Int,
    var fansStatus:Int,
    val gender:Int,
    var goldStory:GoldStory,
    val heritageItem:String,
    val heritageItemName:String,
    var id:String,
    var images:String,
    val isMe:Int,
    var introduce:String,
    var level:String,
    var likeNum:Int,
    var likeStatus:Int,
    var name:String,
    val nationality:String,
    val phone:String,
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
    var voiceTime:String


){
    fun getGender():String= if(gender == 1) "男" else "女"

    fun isMe():Boolean = isMe == 1

    fun getCollectionStatus():Boolean = collectionStatus == 1

    fun getFanStatus():Boolean = fansStatus == 1

    fun getLikeStatus():Boolean = likeStatus == 1
}
