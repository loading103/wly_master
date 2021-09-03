package com.daqsoft.legacyModule.bean

data class LegacyHeritagePeopleListBean(
    var collectionStatus: Int = 0, // 收藏状态
    var fans: Int = 0, // 关注数量
    var fansStatus: Int = 0, // 关注状态(0未关注，1已关注)
    var productNum: String = "", // 作品数量
    var heritageItemName: String = "", // 非遗项目名称
    var nationality: String, // 民族
    var gender: Int, // 性别
    var resourceType: String = "", // CONTENT_TYPE_HERITAGE_ITEM
    var showNum: Int = 0, // 浏览次数
    var storyNum: Int = 0, // 0
    var video: String = "",
    var videoCover: String = "",
    val id:String,
    val isMe:Int,
    val name:String,
    val images:String

)