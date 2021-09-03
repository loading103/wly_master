package com.daqsoft.thetravelcloudwithculture.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.*
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.businessview.event.LoginStatusEvent
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentShopBinding
import com.daqsoft.thetravelcloudwithculture.ui.utils.WebShopContact
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.toast
import java.lang.Exception

/**
 * 商城
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-10-22
 * @since JDK 1.8.0_191
 */
class ShopFragment() : BaseFragment<FragmentShopBinding, BaseViewModel>() {

    override fun getLayout(): Int = R.layout.fragment_shop

    var isLogin: Boolean = false
    var shopUrl: String? = null

    companion object {
        fun newInstance(shopUrl: String?): ShopFragment {
            var frag = ShopFragment()
            var bundle = Bundle()
            bundle.putString("shopUrl", shopUrl)
            frag.arguments = bundle
            return frag
        }
    }

    override fun initData() {
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        if (BaseApplication.appArea == "xj") {
            mBinding.tvShopTitle.text = "新疆礼物"
        } else {
            mBinding.tvShopTitle.text = resources.getString(R.string.tab_four)
        }
        isLogin = AppUtils.isLogin()
        if (!isLogin) {
            ToastUtils.showUnLoginMsg()
            ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                .navigation()
        }
        getParam()
        dealWebShop()
    }

    private fun getParam() {
        try {
            shopUrl = arguments?.getString("shopUrl")
        } catch (e: Exception) {
        }

    }

    private fun dealWebShop() {
        var uuid = SPUtils.getInstance().getString(SPKey.UUID)
        if (shopUrl.isNullOrEmpty()) {
            var tshopUrl = SPUtils.getInstance().getString(SPKey.SHOP_URL)
            if (!tshopUrl.isNullOrEmpty()) {
                shopUrl = tshopUrl
            }
        }
        initWebSetting()
        initShopWebView()
        mBinding.webViewShop.addJavascriptInterface(WebShopContact(), "dqAppJs")
        var shopUrl = StringUtil.getShopUrl(shopUrl, uuid)
        mBinding.webViewShop.loadUrl(shopUrl)
    }

    private fun initShopWebView() {
        // 设置setWebChromeClient对象
        mBinding.webViewShop.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
            }

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }
        }
        // 设置此方法可在WebView中打开链接，反之用浏览器打开
        mBinding.webViewShop.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (!mBinding.webViewShop.settings.loadsImagesAutomatically) {
                    mBinding.webViewShop.settings.loadsImagesAutomatically = true
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onLoadResource(view: WebView, url: String) {
                super.onLoadResource(view, url)
            }

            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                return super.shouldInterceptRequest(view, request)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                Log.e("url=", "" + request?.url)
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
    }

    override fun injectVm(): Class<BaseViewModel> = BaseViewModel::class.java

    override fun onResume() {
        super.onResume()
//        if (isLogin != AppUtils.isLogin()) {
//            dealWebShop()
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun initWebSetting() {
        var webSettings: WebSettings = mBinding.webViewShop.getSettings()
        webSettings.loadsImagesAutomatically = android.os.Build.VERSION.SDK_INT >= 19
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) { // 软件解码
            mBinding.webViewShop.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        // 硬件解码
        mBinding.webViewShop.setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
        // 不使用缓存：
        webSettings.cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
        // 设置支持javascript脚本
        webSettings.javaScriptEnabled = true
        webSettings.textZoom = 100
        // 设置可以支持缩放
        webSettings.setSupportZoom(true)
        // 设置出现缩放工具 是否使用WebView内置的缩放组件，由浮动在窗口上的缩放控制和手势缩放控制组成，默认false
        webSettings.builtInZoomControls = true
        // 隐藏缩放工具
        webSettings.displayZoomControls = false
        // 扩大比例的缩放
        webSettings.useWideViewPort = true
        // 自适应屏幕
        webSettings.layoutAlgorithm = android.webkit.WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webSettings.loadWithOverviewMode = true
        webSettings.databaseEnabled = true
        webSettings.savePassword = false
        webSettings.domStorageEnabled = true
        mBinding.webViewShop.isSaveEnabled = true
        mBinding.webViewShop.keepScreenOn = true
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun updateDataStatus(event: LoginStatusEvent) {
//        if (event != null) {
//            mModel?.getActivityDetail(id, false)
//        }
        dealWebShop()
    }
}