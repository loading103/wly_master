package com.daqsoft.legacyModule.bean


data class LegacyStoryListBean(
    var activityNum: String = "",
    var activityRoomNum: String = "",
    var attractionsNum: String = "",
    var auditResult: String = "",
    var auditStatus: Int = 0, // 6
    var auditType: String = "",
    var collectionNum: Int = 0, // 0
    var commentNum: Int = 0, // 0
    var consumePerson: String = "",
    var content: String = "", // 发布攻略标签发布攻略标签发布攻略标签发布攻略标签发布攻略标签
    var cover: String = "", // https://file.geeker.com.cn/cultural-tourism-cloud/1584723887479/1ea448e4ee4b409d10ffd12e74ddec33.jpg_r_720x400x95_f9af5b08.jpeg
    var createDate: String = "", // 2020年03月21日
    var foodNum: Int = 0, // 0
    var heirAttentionStatus: Boolean = false, // false
    var hotelNum: Int = 0, // 0
    var ich: Boolean = false, // true
    var ichHp: Boolean = false, // true
    var ichHpName: String = "", // 九龙传承人邹倩
    var ichWorks: Boolean = false, // false
    var id: Int = 0, // 16700
    var images: List<String> = listOf(),
    var latitude: String = "",
    var likeNum: Int = 0, // 0
    var longitude: String = "",
    var pkId: String? = null, // null
    var pkNum: Int = 0, // 0
    var pkStoryTitle: String = "",
    var playPointNum: Int = 0, // 0
    var regionNum: Int = 0, // 0
    var resourceCompleteRegionName: String = "",
    var resourceId: Any = Any(), // null
    var resourceImage: String = "",
    var resourceName: String = "",
    var resourceRegion: String = "",
    var resourceRegionName: String = "",
    var resourceSiteId: Any = Any(), // null
    var resourceType: String = "",
    var resourceTypeName: String = "",
    var showNum: Int = 0, // 1
    var status: Int = 0, // 1
    var storyType: String = "", // strategy
    var strategyDetail: List<StrategyDetail> = listOf(),
    var tag: Any = Any(), // null
    var tagName: String = "", // 品非遗
    var title: String = "", // 发布攻略标签
    var top: Int = 0, // 0
    var topicInfo: List<Any> = listOf(),
    var video: String = "",
    var videoCover: String = "",
    var vipHead: String = "", // http://thirdwx.qlogo.cn/mmopen/vi_32/o7M9drMKflfW78mJrM9NX9VowmiaQAQeD4BOAAdFKN3ahG8nuzFaEesHH3vLTONkbDUoOk2AO9opCtETlBUUrzQ/132
    var vipNickName: String = "", // 眉眼如初
    var vipPhone: String = "", // 18228358933
    var vipResourceStatus: VipResourceStatus = VipResourceStatus()
) {
    data class VipResourceStatus(
        var collectionStatus: Boolean = false, // false
        var likeStatus: Boolean = false // false
    )

    data class StrategyDetail(
        var content: String,
        val contentType: String,
        val resourceId: String,
        val resourceName: String,
        // CONTENT：内容 IMAGE：图片 VIDEO:视频 使用引用Constant
        val resourceType: String,
        val title: String,
        val videoCover: String
    )
}
