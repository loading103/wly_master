package com.daqsoft.provider.view.web;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import timber.log.Timber;


public class ProgressWebView extends WebView {
    private WebViewProgressBar progressBar;
    private Handler handler;
    private WebView _this;
    /**
     * 高度回调
     */
    private HeightCallBack callBack = null;

    public void setCallBack(HeightCallBack callBack) {
        this.callBack = callBack;
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        notSavePassword();
        progressBar = new WebViewProgressBar(context);
        progressBar.setLayoutParams(new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        progressBar.setVisibility(GONE);
        addView(progressBar);
        handler = new Handler();
        _this = this;
        //setWebChromeClient(new MyWebChromeClient());

    }

    public void setProgress(int newProgress) {
        if (newProgress == 100) {
            progressBar.setProgress(100);
            handler.postDelayed(runnable, 200);
        } else if (progressBar.getVisibility() == GONE) {
            progressBar.setVisibility(VISIBLE);
        }
        if (newProgress < 5) {
            newProgress = 5;
        }
        progressBar.setProgress(newProgress);
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressBar.setProgress(100);
                handler.postDelayed(runnable, 200);
            } else if (progressBar.getVisibility() == GONE) {
                progressBar.setVisibility(VISIBLE);
            }
            if (newProgress < 5) {
                newProgress = 5;
            }
            progressBar.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.GONE);
        }
    };

    /**
     * 回调接口
     */
    public interface HeightCallBack {
        void callback(int h);
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        Timber.e("高度---" + h);
        if (callBack != null) {
            callBack.callback(h);
        }
    }

    private void notSavePassword(){
        WebSettings webSetting = this.getSettings();
        webSetting.setSavePassword(false);
    }
}