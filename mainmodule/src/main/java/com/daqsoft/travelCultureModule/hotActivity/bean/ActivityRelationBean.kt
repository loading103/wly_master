package com.daqsoft.travelCultureModule.hotActivity.bean

/**
 * @Description 活动场地
 * @ClassName   ActivityRelationBean
 * @Author      PuHua
 * @Time        2019/12/26 18:03
 */
data class ActivityRelationBean(
    // 活动数量
    val activityCount: Int,
    // 活动室/玩乐/价格等
    val count: Int,
    // 金版解说 0 没有 1 有
    val goldFlag: String,
    // 图片
    val images: String,
    // 720全景地址
    val panoramaUrl: String,
    // 地区名称
    val regionName: String,
    // 资源ID
    val resourceId: Int,
    // 资源名称
    val resourceName: String,
    // 资源类型
    val resourceType: String,
    // 标签
    val tags: String,
    // 视频URL
    val videoUrl: String
)