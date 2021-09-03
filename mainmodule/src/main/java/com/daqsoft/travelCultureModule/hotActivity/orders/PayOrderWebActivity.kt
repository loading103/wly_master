package com.daqsoft.travelCultureModule.hotActivity.orders

import android.annotation.SuppressLint
import android.os.Build
import android.webkit.CookieManager
import android.webkit.WebSettings
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.Utils.getNewContent
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityPayOrderWebBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.view.web.MWebChromeClient
import com.daqsoft.provider.view.web.MyWebViewClient
import com.daqsoft.provider.view.web.ProgressWebView

/**
 * @des 付费预订活动--小电商(web)
 * @author PuHua
 * @date
 */
@Route(path = MainARouterPath.MAIN_ACTIVITY_PAY_ORDER)
class PayOrderWebActivity :
    TitleBarActivity<ActivityPayOrderWebBinding, BaseViewModel>() {
    @JvmField
    @Autowired(name = "jumpUrl")
    var url: String? = ""

    override fun getLayout(): Int = R.layout.activity_pay_order_web

    override fun setTitle(): String = getString(R.string.main_activity_order_title)

    override fun injectVm(): Class<BaseViewModel> =
        BaseViewModel::class.java

    override fun initView() {
//        mModel.detail.observe(this, Observer {
//            mBinding.name = it.productName
//            mBinding.effectTime = getString(
//                R.string.order_activity_room_time_stamp_, TimeUtils.timeStamp2Date(it.useStartTime), TimeUtils
//                    .timeStamp2Date(it.useEndTime)
//            )
//            mBinding.number = it.productNum
//        })


    }

    override fun initData() {
//        val domain = SPUtils.getInstance().getString(SPKey.DOMAIN)
//        val url =
//            domain + ElectronicApi.ELECTRONIC_WEB + id + "&unid=" + SPUtils.getInstance().getString(SPKey.UUID) + "&token=" +
//                    SPUtils.getInstance().getString(
//                SPKey.USER_CENTER_TOKEN
//            )
        setWebInfo(mBinding.web, url!!,object :OnfinishListener{
            override fun finish() {

            }

        })
        mBinding.web.loadUrl(url)
    }

    /**
     * @param mWebView 加载网页的地址
     * @param loadUrl  加载的网页
     */
    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    fun setWebInfo(mWebView: ProgressWebView, loadUrl: String, listener: OnfinishListener) {
        // web设置
        mWebView.requestFocus()
        //  获取WebView的设置
        val webSettings = mWebView.settings
        // 不使用缓存：
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        // 将JavaScript设置为可用，这一句话是必须的，不然所做一切都是徒劳的
        webSettings.javaScriptEnabled = true
        webSettings.pluginState = WebSettings.PluginState.ON
        // 设置加载进来的页面自适应手机屏幕
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        // 给webview添加JavaScript接口
        // 设置WebView支持手势
        mWebView.requestFocusFromTouch()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.allowFileAccessFromFileURLs = true
        }
        // 设置js可访问文件
        webSettings.allowFileAccess = true
        webSettings.setSupportMultipleWindows(true)
        webSettings.builtInZoomControls = true
        webSettings.setAppCacheMaxSize(java.lang.Long.MAX_VALUE)
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.setSupportZoom(true)
        webSettings.setAppCacheEnabled(true)
        webSettings.domStorageEnabled = true
        webSettings.setGeolocationEnabled(true)
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        val userAgent = webSettings.userAgentString
        webSettings.userAgentString = "$userAgent mobile daqsoft.com"
        CookieManager.getInstance().setAcceptCookie(true)
        mWebView.webViewClient = MyWebViewClient(MyWebViewClient.OnWebviewPageFinished { listener.finish() })
        mWebView.webChromeClient = object : MWebChromeClient() {

        }

        // 通过webview加载html页面
        if (loadUrl.startsWith("http://") || loadUrl.startsWith("https://") || loadUrl.startsWith("file:///")) {
            mWebView.loadUrl(loadUrl)
        } else {
            mWebView.loadData(getNewContent(loadUrl), "text/html; charset=UTF-8", null)
        }
    }

}

interface OnfinishListener {
    fun finish()
}