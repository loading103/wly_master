package com.daqsoft.provider.bean

/**
 * 我的投诉详情页面实体类
 * @author 黄熙
 * @date 2020/3/3 0003
 * @version 1.0.0
 * @since JDK 1.8
 */
data class MineComplaintDetailsBean(
    /**
     *受理结果
     */
    val acceptResult: String,
    /**
     *受理时间
     */
    val acceptTime: String,
    /**
     *详细地址
     */
    val address: String,
    /**
     *投诉事由
     */
    val complaintsReasons: String,
    /**
     *投诉类型
     */
    val complaintsType: String,
    /**
     *图片凭证
     */
    val evidenceImages: List<String>,
    /**
     *	视频凭证
     */
    val evidenceVideo: List<String>,
    /**
     *视频封面图
     */
    val evidenceVideoCover: List<Any>,
    /**
     *处理结果
     */
    val handleResult: String,
    /**
     *	处理时间
     */
    val handleTime: String,
    /**
     *数据id
     */
    val id: Int,
    /**
     *	事发时间
     */
    val incidentTime: String,
    /**
     *是否公开
     */
    val isPublic: Boolean,
    /**
     *	投诉人名称
     */
    val name: String,
    /**
     *投诉人电话
     */
    val phone: String,
    /**
     *地区编码
     */
    val region: String,
    /**
     *	被投诉方
     */
    val respondent: String,
    /**
     *满意度 1：满意 0：不满意
     */
    val satisfied: Int,
    /**
     *投诉人性别 1：男 2：女 0：未知
     */
    val sex: Int,
    /**
     *状态 投诉未处理：4 投诉已受理：5 投诉已处理：6
     */
    val status: Int
)