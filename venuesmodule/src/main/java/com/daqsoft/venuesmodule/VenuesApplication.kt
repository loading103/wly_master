package com.daqsoft.venuesmodule

import com.daqsoft.baselib.base.BaseApplication

/**
 * 文化场馆的APPlication
 * @author 黄熙
 * @date 2019/12/14 0014
 * @version 1.0.0
 * @since JDK 1.8
 */
class VenuesApplication : BaseApplication() {
    override fun initRetrofit() {

    }

    override fun initTitleBar() {
        titleBarColor = R.color.white
        titleBarTextColor = R.color.txt_black
        titleBarBackImg = R.mipmap.provider_return_back
    }
}