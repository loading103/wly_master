package com.daqsoft.provider.bean

/**
 * 我的投诉列表实体类
 * @author 黄熙
 * @date 2020/3/3 0003
 * @version 1.0.0
 * @since JDK 1.8
 */
class MineComplaintListBean(
    /**
     *
     */
    var status: Int,
    /**
     * 图片凭证
     */
    var evidenceImages: List<String>,
    /**
     * 视频凭证
     */
    var evidenceVideo: List<String>,
    /**
     * 图片凭证
     */
    var evidenceVideoCover: List<String>,
    /**
     * 投诉事由
     */
    var complaintsReasons: String,
    /**
     * 事发时间
     */
    var incidentTime: String,
    /**
     * 被投诉方
     */
    var respondent: String,
    /**
     * ID
     */
    var id: Int
)