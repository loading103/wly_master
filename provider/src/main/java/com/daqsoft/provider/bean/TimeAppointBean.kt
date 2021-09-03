package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   TimeAppointBean
 * @Author      luoyi
 * @Time        2020/7/13 17:18
 */
data class TimeAppointBean(
    /**
     * 	开放状态,（0闭馆，1开放，2需提前?天进行预约，3只能预约?天内场馆，-1无预约信息）
     */
    var openStatus: Int,
    /**
     * 库存
     */
    var maxNum: Int,
    /**
     * 下标
     */
    var index: Int,
    /**
     * 日期
     */
    var date: String
)