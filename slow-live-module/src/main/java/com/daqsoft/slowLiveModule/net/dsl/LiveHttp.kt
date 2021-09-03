package com.daqsoft.slowLiveModule.net.dsl

import com.bumptech.glide.RequestManager
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.slowLiveModule.net.dsl.BaseOkHttpHelper
import com.daqsoft.slowLiveModule.net.dsl.RequestWrapper

internal object LiveHttp {
    var baseUrl = "http://ctc-api.test.daqsoft.com/v2/"
}

internal fun http(block: RequestWrapper.() -> Unit) {
    val wrapper = RequestWrapper()
    wrapper.method = "GET"
    wrapper.RESPONSE_CODE_NAME = "code"
    wrapper.SUCCESS_CODE = 0

    val accessToken = SPUtils.getInstance().getString(SPUtils.Config.TOKEN)
    wrapper.params["token"] = accessToken
    wrapper.params["siteCode"] = BaseApplication.siteCode


    block(wrapper)
    BaseOkHttpHelper.baseOkhttp(wrapper)
}