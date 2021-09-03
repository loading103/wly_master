package com.daqsoft.provider.bean

/**
 * @Description  文化馆类型
 * @ClassName   VenueTypeBean
 * @Author      luoyi
 * @Time        2020/3/23 19:56
 */
data class VenueTypeBean (
    /**
     *  id
     */
    val id:String,
    /**
     * 名称
     */
    val name:String,
    /**
     * 文化馆类型
     */
    val type:String,
    /**
     *
     */
    val value:String,
    /**
     * 英文
     */
    val english:String
){
    override fun toString(): String {
        return name
    }
}
/**
 * @Description  文化馆等级
 * @ClassName   VenueTypeBean
 * @Author      luoyi
 * @Time        2020/3/23 19:56
 */
data class VenueLevelBean(
    /**
     *  id
     */
    val id:String,
    /**
     * 名称
     */
    val name:String,
    /**
     * 文化馆类型
     */
    val type:String,
    /**
     *
     */
    val value:String,
    /**
     * 英文
     */
    val english:String
){
    override fun toString(): String {
        return name
    }
}
