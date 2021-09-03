package com.daqsoft.integralmodule.repository.bean

/**
 * 站点详情
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-22
 * @since JDK 1.8.0_191
 */
data class SiteInfoBean(
    /**
     * 机审剩余数量
     */
    val auditNum: String?,
    /**
     * 审核状态
     */
    val auditStatus: Int?,
    /**
     * 企业数
     */
    val companyCount: Int?,
    /**
     * 企业端域名
     */
    val companyDomain: String?,
    /**
     * 企业名称
     */
    val companyName: String?,
    /**
     * 联系人
     */
    val contact: String?,
    /**
     * 联系人手机号
     */
    val contactPhone: String?,
    val createTime: String?,
    /**+
     * 诚信开通状态 0 未开通 1 已开通 2 已关闭
     */
    val creditStatus: Int?,
    /**
     * 电子邮箱
     */
    val email: String?,
    /**
     * h5域名
     */
    val h5Domain: String?,
    val id: Int?,
    /**
     * 0 未加入 1 加入
     */
    val joinUnion: Int?,
    /**
     * 名称
     */
    val name: String?,
    /**
     * 720全景
     */
    val panoramaUrl: String?,
    /**
     * 小程序域名
     */
    val programDomain: String?,
    /**
     * 绑定公众号二维码链接
     */
    val qrCode: String?,
    /**
     * 地区编码
     */
    val region: String?,
    /**
     * 地区名称
     */
    val regionName: String?,
    /**
     * 审核备注
     */
    val remark: String?,
    /**
     * 店铺code
     */
    val shopCode: String?,
    /**
     * 店铺名称
     */
    val shopName: String?,
    /**
     * 	店铺url
     */
    val shopUrl: String?,
    /**
     * 站点编码
     */
    val siteCode: String?,
    /**
     * 站点介绍
     */
    val siteIntroduce: String?,
    /**
     * 站点LOGO
     */
    val siteLogo: String?,
    /**
     * 站点手机号
     */
    val sitePhone: String?,
    /**
     * 站点类型
     */
    val siteType: String?,
    /**
     * 	短信条数
     */
    val smsNum: Int?,
    /**
     * 状态
     */
    val status: Int?,
    /**
     * 机审历史总数
     */
    val totalAuditNum: String?,
    /**
     * 短信历史总数
     */
    val totalNum: Int?,
    /**
     * 修改时间
     */
    val updateTime: String?,
    /**
     * vip状态
     */
    val vipPlayStatus: String,
    /**
     * 是否强制更新
     */
    var mandatoryUpdate: Boolean,
    /**
     * 是否使用新模板
     */
    var useNewMenu: Boolean,
    /**
     * 机器人是否打开
     */
    var serviceRobot: Boolean,
    /**
     * 图片水印
     */
    var watermark:String?
)