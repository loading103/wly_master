package com.daqsoft.servicemodule.bean

/**
 * desc :用户输入提示实体类
 * @author 江云仙
 * @date 2020/4/10 17:54
 */
data class InputTipsBean(
    var name:String,
    var district:String
){
    override fun toString(): String {
        return name
    }
}