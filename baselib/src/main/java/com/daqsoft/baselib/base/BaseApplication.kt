package com.daqsoft.baselib.base

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.widgets.MyHeadView
import com.daqsoft.baselib.widgets.PandaRefreshHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import daqsoft.com.baselib.BuildConfig
import daqsoft.com.baselib.R
import timber.log.Timber

/**
 * BaseApplication
 *
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019/4/15
 * @since JDK 1.8
 */
abstract class BaseApplication : Application() {

    companion object {

        var titleBarColor: Int = R.color.white
        lateinit var context: Context
        var titleBarTextColor: Int = R.color.white
        var titleBarBackImg: Int = R.mipmap.arrow_back
        var theme = R.string.theme_dark
        var loginPath = ""
        var baseUrl = ""
        var appUpdateUrl = ""
        var electronicUrl: String = ""
        var isDebug: Boolean = false
        var webUrl: String = ""
        var dataUploadUrl: String = ""
        var defaultAdCode:String=""
        var webSiteUrl: String = ""
        @kotlin.jvm.JvmField
        var siteCode = ""
        @kotlin.jvm.JvmField
        var appArea : String= "sc"
        var complaintUrl = ""
        var isMustUpdate: Boolean = false


        // 下拉刷新/上拉加载  文案
        var refreshTips = ""
        // 全局下拉刷新头部
        var pandaRefreshHeader: PandaRefreshHeader? = null
        /**
         * 初始化 下拉刷新控件
         * 设置全局默认配置（优先级最低，会被其他设置覆盖）
         */
        fun initSmartRefreshLayout() {
            pandaRefreshHeader = PandaRefreshHeader(context)
            //禁止加载更多
            SmartRefreshLayout.setDefaultRefreshInitializer { context, layout ->
                layout.setEnableAutoLoadMore(false)
            }
        }


    }

    override fun onCreate() {
        super.onCreate()

        context = this

        initARouter()
        initTimber()
        initRetrofit()
        initTitleBar()
        initOther()

        initSmartRefreshLayout()
    }

    /**
     * 初始化ARouter
     */
    protected open fun initARouter() {
        if (isDebug) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
    }

    /**
     * 初始化日志打印框架Timber
     */
    protected open fun initTimber() {
//        if (BuildConfig.DEBUG) {
//            Timber.plant(Timber.DebugTree())
//        } else {
////            Timber.plant( CrashReportingTree());
//        }
    }

    /**
     * 初始化RetrofitFactory
     */
    abstract fun initRetrofit()

    /**
     * 初始化TitBar
     */
    abstract fun initTitleBar()

    /**
     * 初始化其他第三方SDK等，自己App中用到什么SDK需要在Application中初始化的，覆写此方法
     */
    protected open fun initOther() {}



}