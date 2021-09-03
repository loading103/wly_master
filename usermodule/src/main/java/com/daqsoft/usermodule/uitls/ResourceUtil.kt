package com.daqsoft.usermodule.uitls

import android.content.Context

/**
 * @Description 获取value等资源的工具
 * @ClassName   ResourceUtil
 * @Author      PuHua
 * @Time        2019/10/31 18:04
 */
object ResourceUtil {
    /**
     * 根据id获取字符串
     */
    fun getStringResource(context:Context,id:Int):String{
       return context.resources.getString(id)
    }
}