package com.daqsoft.legacyModule.bean

data class LegacyHeritageExperienceBaseListBean(
    var collectionStatus: Int = 0, // 收藏状态
    val address:String, // 详细地址
    val baseRegionName:String, // 地区名字
    val heritageItemName:String,// 非遗项目名
    val name:String , // 体验基地名称
    val videoCover:String, // 视频封面图
    val video:String, // 视频
    val images:String, // 图片
    val id:String, // 体验基地ID
    val resourceType:String,
    val region:String


)