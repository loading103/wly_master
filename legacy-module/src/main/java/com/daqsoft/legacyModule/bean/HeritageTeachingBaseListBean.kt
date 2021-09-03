package com.daqsoft.legacyModule.bean

internal data class HeritageTeachingBaseListBean(
    var collectionStatus: Int = 0, // 收藏状态
    val region:String, // 地区名
    val heritageItemName:String,// 非遗项目名
    val name:String , // 体验基地名称
    val videoCover:String, // 视频封面图
    val video:String, // 视频
    val images:String, // 图片
    val id:String, // 体验基地ID
    val resourceType:String
)