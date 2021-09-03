package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   FeedBackBean
 * @Author      luoyi
 * @Time        2020/5/21 16:59
 */
data class FeedBackBean(
    /**
     * 回复时间
     */
    var replyTime: String,
    /**
     * 回复内容
     */
    var replyContent: String,
    /**
     * 图片
     */
    var image: String,
    /**
     * 视频
     */
    var video: String,
    /**
     * 	反馈内容
     */
    var content: String,
    /**
     * 反馈时间
     */
    var createTime: String,
    /**
     * 反馈类型
     */
    var type: String,
    /**
     *  头像
     */
    var headUrl: String,
    /**
     * 昵称
     */
    var nickName: String,
    /**
     * 视频封面图
     */
    var coverImage: String
)