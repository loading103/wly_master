package com.daqsoft.provider.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.daqsoft.baselib.utils.SPUtils;
import com.daqsoft.provider.ARouterPath;
import com.daqsoft.provider.MainARouterPath;
import com.daqsoft.provider.SPKey;
import com.daqsoft.provider.ZARouterPath;
import com.daqsoft.provider.view.web.AppJsUtil;

import timber.log.Timber;

/**
 * 用一句话来描述功能
 *
 * @author 黄熙
 * @version 1.0.0
 * @date 2020/9/7 0007
 * @since JDK 1.8
 */
public class WebViewUtils {
    static String content = "";
    static Boolean isFirst = true;
    static WebView mweb ;
    static AppJsUtil appJsUtil;
    public static WebView pptWeb(WebView webview, String iframe, String noiframe, Activity activity) {
        content = iframe;
        mweb=webview;
        WebSettings webSettings = webview.getSettings();
        // 设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        appJsUtil = new AppJsUtil(activity,webview);
        webview.addJavascriptInterface(appJsUtil, "AppJs");
        // 可以使用插件
        webSettings.setPluginState(WebSettings.PluginState.ON);
        // 设置用户代理（请求头）使得web端正确判断
        webview.getSettings().setUserAgentString(
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"
                        + "; dccPlan/1.1.5");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 这点可以防止有白边
        webview.setBackgroundColor(0);
        // 设置可以访问文件
        webSettings.setAllowFileAccess(true);
        // 设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        // 设置此属性，可任意比例缩放
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 设置是否可以跨域
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBlockNetworkImage(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setDisplayZoomControls(false);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // 不加上，会显示白边
        webview.setVerticalScrollBarEnabled(false);
        webview.setHorizontalScrollBarEnabled(false);
        // 设置 缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 开启 DOM storage API 功能
        webSettings.setDomStorageEnabled(true);
        webview.setWebChromeClient(new WebChromeClient());
        // 设置Web视图
        webview.setWebViewClient(new webViewClient());
        webSettings.setAppCacheEnabled(true);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            CookieManager.getInstance().setAcceptThirdPartyCookies(webview, true);
        // 考虑到网页的加载速
        // 度，我们可以调用setWebChromeClient（）方法
        // 滚动条在整个page里
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webSettings.setPluginState(WebSettings.PluginState.ON);// 可以使用插件
        if (TextUtils.isEmpty(noiframe)) {
            webview.loadUrl(SPUtils.getInstance().getString(SPKey.H5_DOMAIN)+"/#/app-call");
        }else {
            webview.loadDataWithBaseURL(null, noiframe, "text/html", "utf-8", null);
        }
        return webview;
    }

    public static class webViewClient extends WebViewClient {

//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            return false;
//        }
//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            return false;
//        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Timber.e("页面加载之后---" + url);
            Timber.e(content);
            view.getSettings().setBlockNetworkImage(false);
            view.getSettings().setJavaScriptEnabled(true);
            if (url.contains("app-call")) {
                isFirst = true;
                view.loadUrl("javascript:setAppContent('" + content + "');");
            } else if (url.contains("-detail") && isFirst) {
                isFirst = false;
                goToPage(url);
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Timber.e("页面加载onPageStarted---" + url);
            super.onPageStarted(view, url, favicon);
        }


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Timber.e("页面加载error---" + error);
            // 不要使用super，否则有些手机访问不了，因为包含了一条 handler.cancel()
            super.onReceivedSslError(view, handler, error);
            // 接受所有网站的证书，忽略SSL错误，执行访问网页
            handler.proceed();
        }
    }
    public static class WebViewChormClient extends WebChromeClient{
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            // TODO Auto-generated method stub
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            try {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);

            } catch (Exception e) {

            }

        }
    }

    /**
     * 跳转原生页面
     *
     * @param url 页面地址
     */
    public static void goToPage(String url) {
        if (url == null || url.length() <= 0) {
            return;
        }
        String router = "";
        String id = "";
        String[] splits = url.split("/");
        if (splits != null && splits.length > 0) {
            id = splits[splits.length - 1];
        }
        // 酒店
        if (url.contains("hotel-detail")) {
            router = ZARouterPath.ZMAIN_HOTEL_DETAIL;
        } else if (url.contains("venues-detail")) {
            // 场馆
            router = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY;
        } else if (url.contains("scenic-detail")) {
            // 景区
            router = MainARouterPath.MAIN_SCENIC_DETAIL;
        } else if (url.contains("food-detail")) {
            // 美食
            router = MainARouterPath.MAIN_FOOD_DETAIL;
        }
        if (!router.equals("") && !id.equals("")) {
            ARouter.getInstance().build(router)
                    .withString("id", id)
                    .navigation();
            router = "";
            id = "";
        }
    }

}