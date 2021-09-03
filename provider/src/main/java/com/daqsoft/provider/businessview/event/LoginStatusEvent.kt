package com.daqsoft.provider.businessview.event

/**
 * @Description
 * @ClassName   LoginStatusEvent
 * @Author      luoyi
 * @Time        2020/4/24 17:27
 */
class LoginStatusEvent {
    /**
     * 状态 1 取消登录 0 登录 2 token过期
     */
    var status: Int = LOGIN

    constructor(statusCode: Int) {
        status = statusCode
    }

    companion object {
        /**
         * 登录
         */
        const val LOGIN = 1
        /**
         * 退出登录
         */
        const val EXIT_LOGIN = 2
        /**
         *  token过期
         */
        const val TOKEN_OUT_TIME = 3
    }
}