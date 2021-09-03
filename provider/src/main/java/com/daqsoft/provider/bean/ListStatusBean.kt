package com.daqsoft.provider.bean

/**
 * @Description 数据类别状态对象
 * @ClassName   ListStatusBean
 * @Author      luoyi
 * @Time        2020/4/27 13:35
 */
data class ListStatusBean(val position: Int, val status: Boolean, val message: String?) {
    /**
     * 用于标识关键状态
     */
    var key: Int = -1

}