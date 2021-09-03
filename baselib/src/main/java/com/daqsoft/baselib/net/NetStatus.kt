package com.daqsoft.baselib.net

import org.jsoup.select.Evaluator

/**
 * 网络请求中的状态
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019/4/19
 * @since JDK 1.8
 */
class NetStatus {
    constructor(errorMessage: String, loading: Boolean, error: Boolean) {
        this.errorMessage = errorMessage
        this.loading = loading
        this.error = error
    }

    constructor()

    /**
     * 错误信息
     */
    var errorMessage: String = ""
    /**
     * 是否需要加载
     */
    var loading: Boolean = true
    /**
     * 如果是需要弹出popwindow是设为true
     */
    var postData: Boolean = true
    /**
     * 是否需要提示弹窗
     */
    var isNeedToastMessage=true
    /**
     * 是否显示错误视图
     *
     * true: 重要接口请求错误，此时显示错误状态视图
     * false：无接口错误或者无关紧要接口错误，此时还是显示正常状态视图
     */
    var error: Boolean = false
    /**
     * 是否需要登录
     *
     * false:不需要(默认)
     * true:需要
     */
    var login: Boolean = false

    /**
     * 是否需要重绘View
     * false ：不需要
     * true:需要
     */
    var isNeedRecyleView: Boolean = true

    /**
     * 是否单独显示加载效果
     * false 不需要
     * true 需要
     */
    var isOnlyShowLoading: Boolean = false

    /**
     *  是否单独显示内容
     *   false 不需要
     *   true 需要
     */
    var isOnlyShowContent: Boolean = false

    var isNeed: Boolean = true
}