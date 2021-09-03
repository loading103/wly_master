package com.daqsoft.provider.bean

/**
 * @des 志愿者团队实体
 * @author PuHua
 * @Date 2020/1/9 20:58
 */
data class VolunteerTeamBean(
    // 最新活动id
    val activityId: Int,
    // 最新活动名称
    val activityName: String,
    // 最新活动类型
    val activityType: String,
    // 地址
    val address: String,
    // 	团队id
    val id: Int,
    // 纬度
    val latitude: String,
    // 经度
    val longitude: String,
    // 团队名称
    val teamName: String,
    // 栏目推荐状态
    val recommendChannelHomePage: Int,
    // 首页推荐状态
    val recommendHomePage: Int,
    // 地区编码
    val region: String,
    // 地区名称
    val teamRegionName: String,
    // 排序
    val sort: Int,
    // 处理时间
    val startTime: String,
    // 团队标识
    val teamIcon: String,
    // 团队图片
    val teamImage: List<String>,
    // 团队人数
    val teamPeopleNum: Int,
    /**
     * 团队人数
     */
    val teamMemeberNum: Int,
    // 团队服务时长
    val teamServiceTime: Int,
    // 团队宣言
    val teamSlogan: String,
    // 团队类型
    val teamType: String,
    // 团队类型名称
    val teamTypeName: String,
    // 团队视频
    val teamVideo: String,
    // 团队视频封面图
    val teamVideoCover: String,
    // 置顶状态
    val top: Int
)