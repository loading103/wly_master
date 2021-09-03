package com.daqsoft.provider.view.web;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by yulh on 2016/6/14.
 */
public class MyWebViewClient extends WebViewClient {


    private OnWebviewPageFinished onWebviewPageFinished;

    private static MyWebViewClient myWebViewClient;
    private Context context;

    public static MyWebViewClient getInstance(OnWebviewPageFinished onWebviewPageFinished) {
        if (myWebViewClient == null) {
            myWebViewClient = new MyWebViewClient();
        }
        myWebViewClient.onWebviewPageFinished = onWebviewPageFinished;
        return myWebViewClient;
    }

    public MyWebViewClient(OnWebviewPageFinished onWebviewPageFinished) {
        super();
        this.onWebviewPageFinished = onWebviewPageFinished;
    }

    public MyWebViewClient() {
        super();
    }

    public MyWebViewClient(Context context) {
        this.context = context;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String
            failingUrl) {
    }


    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, String url) {

        if (url.contains("weixin://wap/pay?")) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
            return true;
        } else {
            Map<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Referer", "http://wxPay.wxutil.com");
            view.loadUrl(url, extraHeaders);
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (onWebviewPageFinished != null) {
            onWebviewPageFinished.onFinished();
        }
    }

    public interface OnWebviewPageFinished {
        void onFinished();
    }

    ;

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return super.shouldInterceptRequest(view, url);

    }

}
