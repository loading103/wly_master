package com.daqsoft.venuesmodule.viewmodel

import com.daqsoft.provider.bean.ValueKeyBean

/**
 * @des 讲解员预约model
 * @author luoyi
 * @Date 2020/7/6 16:25
 */
object CommtentatorModel {

    /**
     * 语言列表
     */
    val languages: MutableList<ValueKeyBean> = mutableListOf(
        ValueKeyBean("中文", "CH"),
        ValueKeyBean
            ("英文", "EN")
    )

    /**
     * 讲解时间模拟数据
     */
    val dates: MutableList<ValueKeyBean> = mutableListOf(
        ValueKeyBean("14:30-15:30", "1"), ValueKeyBean("15:30-14:30", "1")
        , ValueKeyBean("16:30-17:30", "1")
        , ValueKeyBean("14:30-15:30", "1")
        , ValueKeyBean("14:30-15:30", "1")
        , ValueKeyBean("14:30-15:30", "1")
    )

    /**
     * 展厅数据
     */
    val exhalls: MutableList<ValueKeyBean> = mutableListOf(
        ValueKeyBean("画室1", "1"), ValueKeyBean("画室2", "1")
        , ValueKeyBean("画室3", "1")
    )
}