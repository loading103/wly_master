package com.daqsoft.provider.bean

/**
 * 审核信息查看实体类
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-19
 * @since JDK 1.8.0_191
 */
data class ReviewBean(
    /**
     * 证件类型
     */
    val cardType: String?,
    /**
     * 提交时间
     */
    val createTime: String?,
    /**
     * 头像
     */
    val headImg: String?,
    val id: Int?,
    /**
     * 	身份证号
     */
    var idCard: String?,
    /**
     * 反面身份证
     */
    val idCardDown: String?,
    /**
     * 正面身份证
     */
    val idCardUp: String?,
    /**
     * 	名称
     */
    val name: String?,
    /**
     * 昵称
     */
    val nickName: String?,
    /**
     * 	站点id
     */
    val siteId: Int?,
    /**
     * 状态（审核通过6，撤回8，待审核4，审核不通过79）
     */
    val status: Int?,
    /**
     * 审核不通过原因
     */
    val auditResult: String?,
    /**
     * 用户id
     */
    val userId: Int?
){
    fun getCardName():String{
        if(cardType=="ID_CARD"){
            return "身份证"
        }else{
            return  cardType.toString()
        }
    }
}