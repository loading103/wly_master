package com.daqsoft.provider.net

/**
 * 电商接口的数据返回实体类
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-12-4
 * @since JDK 1.8.0_191
 */
class ShopResponse<T> {
    var code: Int? = -1
    var msg: String = ""
    var data: T? = null

    var version: String? = null
}