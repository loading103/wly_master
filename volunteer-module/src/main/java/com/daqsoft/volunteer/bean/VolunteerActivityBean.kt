package com.daqsoft.volunteer.bean

import com.daqsoft.provider.bean.UserResourceStatus

/**
 *@package:com.daqsoft.volunteer.bean
 *@date:2020/6/1 16:33
 *@author: caihj
 *@des:志愿者活动实体
 **/
data class VolunteerActivityBean(
    val lineFlag:Int,// 线上，线下
    val liveUrl: String,
    // 活动状态 0(未开始) 1(进行中) 2(已结束) 3 (报名中/招募中)
    val activityStatus: String,
    // 地址
    val address: String,
    val classifyId: String,
    // 分类名
    val classifyName: String,
    // 诚信免审状态
    val faithAuditStatus: String,
    // 诚信免审分值
    val faithAuditValue: String,
    // 诚信优享状态 0 关闭 1 开启
    val faithUseStatus: String,
    // 诚信优享分值
    val faithUseValue: String,
    // 活动ID
    val id: String,
    // 封面图
    val images: String,
    // 积分价格
    val integral: String,
    // 跳转名称
    val jumpName: String,
    // 跳转类型
    val jumpType: String,
    // 跳转URL
    val jumpUrl: String,
    // 纬度
    val latitude: String,
    // 经度
    val longitude: String,
    // 活动方式
    val method: String,
    // 价格
    val money: String,
    // 活动名称
    val name: String,
    val cityRegionNames: String,
    // 组织ID
    val orgId: String,
    // 推荐至频道首页
    val recommendChannelHomePage: String,
    // 推荐至首页
    val recommendHomePage: String,
    // 已招募人数
    val recruitedCount: String,
    // 地区编码
    val region: String,
    // 地区名称(多个，分隔)
    val regionName: String,
    // 资源数量
    val resourceCount: String,
    // 数据管理ID
    val resourceId: String,
    // 资源字符串(以,分隔)
    val resourceNameStr: String,
    // 报名/招募结束时间
    val signEndTime: String,
    // 报名/招募开始时间
    val signStartTime: String,
    // 站点ID
    val siteId: String,
    // 排序
    val sort: String,
    // 数据状态
    val status: String,
    // 剩余库存
    val stock: String,
    val tag: String,
    // 标签字符串
    val tagNames: String,
    // 置顶
    val top: String,
    // 总库存
    val totalStock: String,
    // 活动类型
    val type: String,
    // 活动结束时间
    val useEndTime: String,
    // 活动开始时间
    val useStartTime: String,
    // 用户收藏状态
    val userResourceStatus: UserResourceStatus,
    val remark: String

)