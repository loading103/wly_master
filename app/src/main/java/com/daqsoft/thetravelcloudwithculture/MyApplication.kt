package com.daqsoft.thetravelcloudwithculture

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import cn.jpush.android.api.JPushInterface
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ViewTarget
import com.daqsoft.android.CommonURL
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.config.Config
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.utils.JpushAliasUtils
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.PlatformConfig
import timber.log.Timber

/**
 * Application
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-10-22
 * @since JDK 1.8.0_191
 */
class MyApplication : BaseApplication() {


    override fun initRetrofit() {
        // ip地址配置。禁止module之间的双向绑定
        electronicUrl = CommonURL.getElectronicBaseUrl()
        baseUrl = CommonURL.getBaseUrl()
        dataUploadUrl = CommonURL.getUploadDataUrl()
        siteCode = CommonURL.SITE_CODE
        appArea = CommonURL.APP_AREA
        defaultAdCode = CommonURL.DEFAULT_ADCODE
        appUpdateUrl = CommonURL.VERSION_URL
        isDebug = CommonURL.DEBUG
        complaintUrl = CommonURL.COMPLAINT_URL
        webSiteUrl = CommonURL.getWebSiteUrl()


    }

    override fun initTitleBar() {
        ViewTarget.setTagId(R.id.tag_glide)
        Config.theme = R.string.theme_dark
        Config.titleBarTextColor = R.color.txt_black
        Config.titleBarBackImg = R.mipmap.provider_return_back
        // 给loginPath赋值，自定义网络请求token失效时的跳转页面
        loginPath = ARouterPath.UserModule.USER_LOGIN_ACTIVITY
    }

    override fun initARouter() {
        if (isDebug) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
    }

    override fun initTimber() {
        if (true) {
            Timber.plant(Timber.DebugTree())
        } else {
//            Timber.plant( CrashReportingTree());
        }
    }

    override fun initOther() {
        try {
//            if (!isDebug) {
            // 初始化友盟统计sdk
            var applicationInfo: ApplicationInfo? =
                packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                    ?: return;
            val value = applicationInfo!!.metaData.getString("com.dqsoft.main.umkey");
            UMConfigure.init(context, value, null, UMConfigure.DEVICE_TYPE_PHONE, null);
//            }
        } catch (e: Exception) {
            UMConfigure.init(
                context,
                "5e83eb880cafb2b3af00009f",
                null,
                UMConfigure.DEVICE_TYPE_PHONE,
                null
            );
        }
        initShare()
        initJPush()
    }

    private fun initJPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory()
        }
        Glide.get(this).trimMemory(level)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }

    private fun initShare() {
        when (appArea) {
            "sc" -> {
                PlatformConfig.setWeixin("wx3f775b88bc01101b", "3baf1193c85774b3fd9d18447d76cab0")
                PlatformConfig.setQQZone("1110450973", "KEY4FkhFPwN26hVu24Z")
            }
            "xj" -> {
                PlatformConfig.setWeixin("wx7eb497fab8d39260", "1b3b89c69373072160d03ee5d1d74e50")
                PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba")
            }
        }
    }
}
