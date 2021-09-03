package com.daqsoft.thetravelcloudwithculture.ui.utils

import android.util.Log
import android.webkit.JavascriptInterface
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.provider.ARouterPath

/**
 * @Description
 * @ClassName   WebShopContact
 * @Author      luoyi
 * @Time        2020/5/11 20:26
 */
class WebShopContact {

    @JavascriptInterface
    fun loadAppWebUrl(url: String, title: String = "") {
        Log.e("iUrl=", url)
            ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("html", url)
                .withString("mTitle", title)
                .navigation()
    }

    @JavascriptInterface
    fun redirectAppLogin() {
        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
            .navigation()
    }
}