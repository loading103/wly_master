package com.daqsoft.usermodule

import com.daqsoft.baselib.base.BaseApplication

/**
 * 用户中心模块Application
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-7-16
 * @since JDK 1.8.0_191
 */
class UserApplication :BaseApplication() {
    override fun initRetrofit() {

    }

    override fun initTitleBar() {
        titleBarColor = R.color.white
        titleBarTextColor = R.color.txt_black
        titleBarBackImg = R.mipmap.provider_return_back
    }
}