package com.daqsoft.integralmodule.repository.bean

/**
 * 积分详情实体类
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-22
 * @since JDK 1.8.0_191
 */
data class DetailBean(
    /**
     * 任务简述
     */
    val desc: String?,
    /**
     * 	图片
     */
    val icon: String?,
    /**
     * 变更积分
     */
    val modifyPoint: Int?,
    /**
     * 变更来源
     */
    val modifySource: String?,
    /**
     * 标题
     */
    val name: String?,
    /**
     * 数量
     */
    val quantity: String?,
    /**
     * 资源iD
     */
    val resourceId: String?,
    /**
     * 任务类型编码
     */
    val taskTypeCode: String?,
    /**
     * 资源类型
     */
    var resourceType:String?,
    /**
     * 变更日期
     */
    val time: String?
)