package com.daqsoft.provider.bean

/**
 * @Description 故事列表
 * @ClassName   StoreBean
 * @Author      luoyi
 * @Time        2020/3/28 10:23
 */
class StoreBean(
    /**
     * 人均消费
     */
    val consumePerson: String,
    /**
     * 活动数量
     */
    val activityNum: String,
    /**
     * 活动室数量
     */
    val activityRoomNum: String,
    /**
     *
     */
    val content: String,
    /**
     * 景点数量
     */
    val attractionsNum: String,
    /**
     * 资源类型名称（等级、类型、标签等冗余数据）
     */
    val resourceTypeName: String,
    /**
     * 游玩点数量（攻略特有）
     */
    val playPointNum: String,
    /**
     * 美食数量（攻略独有）
     */
    val foodNum: String,
    /**
     * 酒店数量（攻略独有）
     */
    val hotelNum: String,
    /**
     *
     */
    val resourceName:String,
    /**
     * 地区数量（攻略独有）
     */
    val regionNum: String,
    /**
     * 审核状态 4：待审核 6：审核通过 79：审核不通过
     */
    val auditStatus: String,
    /**
     * 数据状态 3：草稿
     */
    val status: String,
    /**
     * 	用户互动状态
     */
    val vipResourceStatus: VipResourceStatus,
    val collectionStatus: Boolean,
    /**
     * 点赞状态
     */
    val likeStatus: Boolean,
    /**
     * 攻略详情（攻略独有）
     */
    val strategyDetail: List<StrategyDetailBean>,
    /**
     * 	浏览量
     */
    val showNum: String,
    /**
     * 点评量
     */
    val commentNum: String,
    /**
     * 收藏数量
     */
    val collectionNum: String,
    /**
     * 点赞量
     */
    val likeNum: String,
    /**
     * 资源图片
     */
    val resourceImage: String,
    /**
     * 资源地区名称
     */
    val resourceRegionName: String,
    /**
     * 纬度
     */
    val latitude: String,
    /**
     * 经度
     */
    val longitude: String,
    /**
     * 故事类型
     */
    val storyType: String,
    /**
     * 封面图（攻略独有）
     */
    val cover: String,
    /**
     * 视频封面图
     */
    val videoCover: String,
    /**
     * 	创建时间
     */
    val createDate: String,
    /**
     * 会员头像
     */
    val vipHead: String,
    /**
     * 会员昵称
     */
    val vipNickName: String,
    /**
     * 标签id
     */
    val tag: String,
    /**
     * 标签名称
     */
    val tagName: String,
    /**
     * 	视频地址
     */
    val video: String,
    /**
     * 图片地址数组
     */
    val images: List<String>,
    /**
     * 数据id
     */
    val id: String,
    /**
     * 话题信息
     */
    val topicInfo: List<TopicInfo>,
    /**
     * 资源完整地区名称
     */
    val resourceCompleteRegionName: String,
    /**
     *  资源站点id
     */
    val resourceSiteId: String,
    /**
     * 资源地区编码
     */
    val resourceRegion: String,
    /**
     *  审核方式（machine：机器审核）
     */
    val auditType: String,
    /**
     * 审核结果
     */
    val auditResult: String,
    /**
     * 是否是非遗传承人
     */
    val ichHp: Boolean,
    /**
     * 是否是非遗数据
     */
    val ich: Boolean,
    /**
     * 非遗传承人名称
     */
    val ichHpName: String,
    /**
     * 置顶状态
     */
    val top: String,
    /**
     * PK故事标题
     */
    val pkStoryTitle: String,
    /**
     * pk数量
     */
    val pkNum: String,
    val pkId: String,
    /**
     * 非遗传承人关注状态
     */
    val heirAttentionStatus: Boolean,
    /**
     * 会员电话
     */
    val vipPhone: String,

    val ichWorks: Boolean,
    val resourceId: Int,
    val resourceType: String,
    val sourceUrl: String,
    val title: String
) {
}

/**
 * 话题
 */
class TopicInfo(
    topicId: String,
    /**
     * 话题名称
     */
    val topicName: String
) {

}

class StrategyDetailBean(
    /**
     *视频封面
     */
    val videoCover: String,
    /**
     * 资源名称
     */
    val resourceName: String,
    /**
     * 资源类型
     */
    val resourceType: String,
    /**
     * 资源id
     */
    val resourceId: String,
    /**
     * 内容类型
     */
    val contentType: String,
    /**
     * 内容
     */
    val content: String,
    /**
     * 标题
     */
    val title: String
)
