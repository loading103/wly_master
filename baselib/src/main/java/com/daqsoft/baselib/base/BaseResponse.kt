package com.daqsoft.baselib.base

/**
 * 网络请求返回的数据
 *
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019/4/19
 * @since JDK 1.8
 */

class BaseResponse<T> {
    var code: Int? = -1
    var responseTime: Long = 0
    var data: T? = null
    var datas: MutableList<T>? = null
    var requestCode: Int = -1
    /**
     * 可选字段，用于区分请求接口返回
     */
    var type: String? = null
    /**
     * 若数据加密，则此变量对应data解密后的数据
     */
    var decryptData: T? = null
    /**
     * 若数据加密，则此变量对应datas解密后的数据
     */
    var decryptDatas: List<T>? = null
    var page: PageBean? = null
    var message: String? = null
    var msg: String? = null
    /**
     * 请求标识
     */
    var requestFlag: Int = 0

    class PageBean {
        var total: Int = 0
        var currPage: Int = 0
        var pageSize: Int = 0
        var totalPage: Int = 0
    }
}