package com.daqsoft.venuesmodule.repository.bean

/**
 * 开放时间的实体类
 * @author 黄熙
 * @date 2019/12/26 0026
 * @version 1.0.0
 * @since JDK 1.8
 */
class OpenWeek(
    val setType: Int,
    val week: List<Week>
)

class Week(
    /**
     * 时间名称周几
     */
    val name: String,
    /**
     * 时间范围
     */
    val time: List<String>,
    /**
     * 备注
     */
    val remarks: String
)