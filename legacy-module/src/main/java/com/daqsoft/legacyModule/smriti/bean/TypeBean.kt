package com.daqsoft.legacyModule.smriti.bean

/**
 * desc :搜索分类实体类
 * @author 江云仙
 * @date 2020/4/21 16:10
 */
data class TypeBean(
    val english: String,
    val name: String,
    val id: String,
    val type: String,
    val value: String
){
    override fun toString(): String {
        return name
    }
}

data class SexBean(
    val name:String,
    val value:String
){
    override fun toString(): String {
        return name
    }
}