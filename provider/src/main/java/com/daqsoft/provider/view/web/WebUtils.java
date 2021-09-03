package com.daqsoft.provider.view.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


import com.alibaba.android.arouter.launcher.ARouter;
import com.daqsoft.baselib.base.BaseApplication;
import com.daqsoft.baselib.utils.AppUtils;
import com.daqsoft.baselib.utils.StringUtil;
import com.daqsoft.provider.ARouterPath;
import com.daqsoft.provider.event.UpdateTokenEvent;
import com.daqsoft.provider.event.UpdateWebViewDownLoadEvent;
import com.daqsoft.provider.event.UpdateWebViewPlaceEvent;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * web的初始化设置工具类
 *
 * @author 黄熙
 * @version 1.0.0
 * @date 2020.01.22
 * @since JDK 1.8
 */

public class WebUtils {
    public interface OnfinishListener {
        void finish();

        void onReceivedTitle(String title);
    }

    /**
     * 让WebView的内容宽度，自适应手机屏幕的宽度
     *
     * @param htmltext
     * @return
     */
    public static String getNewContent(String htmltext) {

        Document doc = Jsoup.parse(htmltext);
        Elements elements = doc.getElementsByTag("img");
        for (Element element : elements) {
            element.attr("width", "100%").attr("height", "auto");
        }
        String head = "";
        head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<link href=\"file:///android_asset/dq_content_1.0.0.css\" rel=\"stylesheet\">" +
                "<script type=\"text/javascript\" src=\"file:///android_asset/dq_content_1.0.0.js?" + System.currentTimeMillis() + "\"></script>" +
                "</head>";
        String aa = "<html>" + head + "<body>";
        String bb = "</body></html>";
        android.util.Log.d("VACK", aa + doc.toString() + bb);
        return doc.toString();
    }

    /**
     * @param mWebView 加载网页的地址
     * @param loadUrl  加载的网页
     */
    public static void setWebInfo(final ProgressWebView mWebView, String loadUrl, final OnfinishListener listener) {
        // web设置
        mWebView.requestFocus();
        //  获取WebView的设置
        WebSettings webSettings = mWebView.getSettings();
        // 不使用缓存：
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 将JavaScript设置为可用，这一句话是必须的，不然所做一切都是徒劳的
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        // 设置加载进来的页面自适应手机屏幕
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 给webview添加JavaScript接口
        // 设置WebView支持手势
        mWebView.requestFocusFromTouch();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
        }
        // 设置js可访问文件
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setAppCacheMaxSize(Long.MAX_VALUE);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportZoom(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        String userAgent = webSettings.getUserAgentString();
        webSettings.setUserAgentString(userAgent + " mobile daqsoft.com");
        CookieManager.getInstance().setAcceptCookie(true);
        mWebView.setWebViewClient(new MyWebViewClient(new MyWebViewClient.OnWebviewPageFinished() {
            @Override
            public void onFinished() {
                listener.finish();
            }
        }));
        mWebView.setWebChromeClient(new MWebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mWebView.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });

        // 通过webview加载html页面
        if (loadUrl.startsWith("http://") || loadUrl.startsWith("https://") || loadUrl.startsWith
                ("file:///")) {
            mWebView.loadUrl(loadUrl);
        } else {
            mWebView.loadData(StringUtil.INSTANCE.getHtml(loadUrl), "text/html; charset=UTF-8", null);
        }
    }

    /**
     * @param mWebView 加载网页的地址
     * @param loadUrl  加载的网页
     */
    public static void setWebInfo3(final ProgressWebView mWebView, String loadUrl) {
        // web设置
        mWebView.requestFocus();
        //  获取WebView的设置
        WebSettings webSettings = mWebView.getSettings();
        // 不使用缓存：
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 将JavaScript设置为可用，这一句话是必须的，不然所做一切都是徒劳的
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        // 设置加载进来的页面自适应手机屏幕
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 给webview添加JavaScript接口
        // 设置WebView支持手势
        mWebView.requestFocusFromTouch();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
        }
        // 设置js可访问文件
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setAppCacheMaxSize(Long.MAX_VALUE);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportZoom(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        String userAgent = webSettings.getUserAgentString();
        webSettings.setUserAgentString(userAgent + " mobile daqsoft.com");
        CookieManager.getInstance().setAcceptCookie(true);
        mWebView.setWebViewClient(new MyWebViewClient(new MyWebViewClient.OnWebviewPageFinished() {
            @Override
            public void onFinished() {
            }
        }));
        mWebView.setWebChromeClient(new MWebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mWebView.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });

        // 通过webview加载html页面
        if (loadUrl.startsWith("http://") || loadUrl.startsWith("https://") || loadUrl.startsWith
                ("file:///")) {
            mWebView.loadUrl(loadUrl);
        } else {
            mWebView.loadData(getNewContent(loadUrl), "text/html; charset=UTF-8", null);
        }
    }




    /**
     * 加载网页配置，微信支付拦截
     *
     * @param webBase
     * @param loadUrl
     * @param context
     */

    public static void setWebInfo2(final ProgressWebView webBase, String loadUrl, final Context context, final boolean isweb, final OnfinishListener listener) {
        initWebSetting(webBase);

        // 设置setWebChromeClient对象
        webBase.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (listener != null) {
                    listener.onReceivedTitle(title);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                super.onProgressChanged(view, newProgress);
                webBase.setProgress(newProgress);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                try {
                    callback.invoke(origin, true, true);
                    super.onGeolocationPermissionsShowPrompt(origin, callback);

                } catch (Exception e) {

                }

            }
        });
        // 设置此方法可在WebView中打开链接，反之用浏览器打开
        webBase.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e("--------onPageFinished", url);
                if (listener != null) {
                    listener.finish();
                }
                super.onPageFinished(view, url);
                if (!webBase.getSettings().getLoadsImagesAutomatically()) {
                    webBase.getSettings().setLoadsImagesAutomatically(true);
                }
                //拦截url  在shouldOverrideUrlLoading没发拦截单页面
                if (url.contains("#/login")   && isweb) {
                    if(url.contains(BaseApplication.siteCode)){
                        if(webBase.canGoBack()){
                            webBase.goBack();
                        }else {
                            ((Activity)context).finish();
                        }
                        ARouter.getInstance()
                                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                .withBoolean("isWebView",true)
                                .navigation();
                        }
                }
                EventBus.getDefault().post(new UpdateWebViewPlaceEvent());
                if (url.contains("apply-success")) {
                    EventBus.getDefault().post(new UpdateTokenEvent());
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                Log.e("--------onPageStarted", url);
                EventBus.getDefault().post(new UpdateWebViewPlaceEvent());
                try {
                    if (url.startsWith("weixin://wap/pay?")) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //调起微信页面失败,自己看着办...
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("--------111", url);
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    WebView.HitTestResult hitTestResult = view.getHitTestResult();
                    if (!TextUtils.isEmpty(url) && hitTestResult == null) {
                        view.loadUrl(url);
                        if (url.equals("https://m.ctrip.com/webapp/vacations/tour/vacations")) {

                        }
                        return true;
                    }
                    return super.shouldOverrideUrlLoading(view, url);
                }
                try {
                    // 拦截微信支付
                    if (url.startsWith("weixin://wap/pay?")) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        context.startActivity(intent);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //调起微信页面失败,自己看着办...
                }
                // Otherwise allow the OS to handle things like tel, mailto, etc.
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.e("--------", request.getUrl()+"");
                return super.shouldOverrideUrlLoading(view, request);
            }

        });

        webBase.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                EventBus.getDefault().post(new UpdateWebViewDownLoadEvent(url));
            }
        });

        // 通过webview加载html页面
        if (loadUrl.startsWith("http://") || loadUrl.startsWith("https://") || loadUrl.startsWith
                ("file:///")) {

            webBase.loadUrl(loadUrl);
        } else {
            webBase.loadData(StringUtil.INSTANCE.getHtml(loadUrl), "text/html; charset=UTF-8", null);
        }

    }


    private static void initWebSetting(ProgressWebView webBase) {
        WebSettings webSettings = webBase.getSettings();
        if (Build.VERSION.SDK_INT >= 19) {
            // 图片自动缩放 打开
            webSettings.setLoadsImagesAutomatically(true);
        } else {
            // 图片自动缩放 关闭
            webSettings.setLoadsImagesAutomatically(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // 软件解码
            webBase.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            CookieManager.getInstance().setAcceptThirdPartyCookies(webBase, true);
        // 硬件解码
        webBase.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            CookieManager.getInstance().setAcceptThirdPartyCookies(webBase, true);
        // 不使用缓存：
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 设置支持javascript脚本
        webSettings.setJavaScriptEnabled(true);
        webSettings.setTextZoom(100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            // 设置可以支持缩放
            webSettings.setSupportZoom(true);
            // 设置出现缩放工具 是否使用WebView内置的缩放组件，由浮动在窗口上的缩放控制和手势缩放控制组成，默认false
//        webSettings.setBuiltInZoomControls(true);
            // 隐藏缩放工具
            webSettings.setDisplayZoomControls(false);
            // 扩大比例的缩放
            webSettings.setUseWideViewPort(true);
            String defaultAgent = webSettings.getUserAgentString();
            webSettings.setUserAgentString("android_daqsoft Android " + defaultAgent);
            webSettings.setTextZoom(100);
            // 自适应屏幕
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setDatabaseEnabled(true);

            try {
                String dir = webBase.getContext().getDir("database", Context.MODE_PRIVATE).getPath();
                webBase.getSettings().setGeolocationDatabasePath(dir);
            } catch (Exception e) {
            }

            webSettings.setSavePassword(false);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAppCacheEnabled(true);
            webBase.setSaveEnabled(true);
            webBase.setKeepScreenOn(true);
            webSettings.setGeolocationEnabled(true);

        }

    }

}