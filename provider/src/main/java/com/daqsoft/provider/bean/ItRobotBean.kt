package com.daqsoft.provider.bean

import com.google.gson.annotations.Expose


/**
 * @Description
 * @ClassName   ItRobotBean
 * @Author      luoyi
 * @Time        2020/5/18 9:54
 */
data class ItRobotBean(
    /**
     * 人工服务
     */
    var humanServices: String,
    /**
     * 欢迎词
     */
    var welcome: String,
    /**
     * 背景图
     */
    var backgroundImage: String,
    /**
     * 头像
     */
    var headUrl: String,
    /**
     * logo
     */
    var logo: String,
    /**
     * 机器人名称
     */
    var name: String,
    /**
     * 候选词 英文逗号分隔
     */
    var candidateWord: String
)

/**
 * @Description 机器人问答
 * @ClassName   ItRobotBean
 * @Author      luoyi
 * @Time        2020/5/18 9:54
 */
class ItRobotDataBean {
    /**
     * 集合处理
     */
    var data: MutableList<ItRobotRequestBean> = mutableListOf()
    /**
     * 回答
     */
    var answer: String? = ""
    /**
     * 返回数据类型
     */
    var type: String? = ""
    /**
     * 1：获取到数据 0：未找到回答信息
     */
    var success: Int = 0
    /**
     * 问答分类	string	complaint：投诉
     */
    var qaType: String? = ""
    /**
     * 资源类型
     */
    var resourceType: String? = ""
    /**
     *  0 问题 1 回复
     */
    var sourceType: Int = 0
}


/**
 * @Description 机器人问答实体
 * @ClassName   ItRobotRequestBean
 * @Author      luoyi
 * @Time        2020/5/18 9:54
 */
data class ItRobotRequestBean(
    /**
     * 外链地址
     */
    var jumpUrl: String,
    /**
     * 简介
     */
    var summary: String,
    /**
     *
     */
    var image: String,
    /**
     * 地址
     */
    var address: String,
    /**
     * 纬度
     */
    var latitude: Double,
    /**
     * 经度
     */
    var longitude: Double,
    /**
     * 名称
     */
    var name: String,
    /**
     * 内容类型
     */
    var contentType: String,
    /**
     * 资源类型
     */
    var resourceType: String,
    /**
     * 资源id
     */
    var resourceId: Int,
    /**
     * 预定状态（景区类型独有）
     */
    var orderStatus: String,
    /**
     * 	最低价格（酒店、餐饮类型数据可能有值）
     */
    var floorPrice: Double,
    var siteId: Int
)

/**
 * @Description 问候语
 * @ClassName   ItRobotRequestBean
 * @Author      luoyi
 * @Time        2020/5/18 9:54
 */
data class ItRobotGreeting(
    /**
     * 问候语
     */
    var greetings: String,
    /**
     * APP跳转地址
     */
    var appUrl: String,
    /**
     * H5跳转地址
     */
    var url: String,
    /**
     * 内容类型
     */
    var contentType: String,
    /**
     * 资源id
     */
    var resourceId: String,
    /**
     * 资源类型
     */
    var resourceType: String
)