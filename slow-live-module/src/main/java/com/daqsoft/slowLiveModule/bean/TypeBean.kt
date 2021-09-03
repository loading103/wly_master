package com.daqsoft.slowLiveModule.bean

internal data class TypeBean(
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