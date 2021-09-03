package com.daqsoft.itinerary

import com.daqsoft.baselib.base.BaseApplication

/**
 * @Author：      邓益千
 * @Create by：   2020/4/21 11:10
 * @Description：
 */
class AppMain : BaseApplication(){

    override fun initRetrofit() {
        electronicUrl = ConfigInfo.getElectronicBaseUrl()
        baseUrl = ConfigInfo.getBaseUrl()
        siteCode = ConfigInfo.SITE_CODE
        appArea = ConfigInfo.APP_AREA
        appUpdateUrl = ConfigInfo.VERSION_URL
    }

    override fun initTitleBar() {}
}