package com.daqsoft.integralmodule.repository.bean

/**
 * 积分配置规则实体类
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-25
 * @since JDK 1.8.0_191
 */
data class ConfigInfoBean(
    /**
     * 体系名称
     */
    val configName: String?,
    /**
     * 会员积分规则
     */
    val configRule: String?,
    val configs: List<Config?>?,
    /**
     * 规则体系ID
     */
    val id: Int?,
    /**
     * 是否开启了小电商同步
     */
    val syncStatus: Int?
){
    data class Config(
        /**
         * 积分等级ID
         */
        val id: Int?,
        /**
         * 等级图标
         */
        val img: String?,
        /**
         * 等级
         */
        val level: Int?,
        /**
         * 最大积分
         */
        val maxPoint: Int?,
        /**
         * 最小积分
         */
        val minPoint: Int?,
        /**
         * 等级名称
         */
        val name: String?,
        /**
         * 站点ID
         */
        val siteId: Int?
    )
}

