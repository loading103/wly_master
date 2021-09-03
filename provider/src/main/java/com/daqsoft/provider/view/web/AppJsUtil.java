package com.daqsoft.provider.view.web;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.daqsoft.baselib.utils.AppUtils;
import com.daqsoft.baselib.utils.SPUtils;
import com.daqsoft.baselib.utils.ToastUtils;
import com.daqsoft.provider.ARouterPath;
import com.daqsoft.provider.base.ResourceType;

import java.lang.ref.WeakReference;


/**
 * @author luoyi
 * @des AppJsUtil
 * @date
 */
public class AppJsUtil {

    private WeakReference<Activity> weakReference = null;
    private WebView webView = null;
    public AppJsUtil(Activity activity) {
        weakReference = new WeakReference<Activity>(activity);
    }
    public AppJsUtil(Activity activity, WebView webView) {
        this.webView=webView;
    }

    /**
     * 返回个人中心
     */
    @JavascriptInterface
    public void backOrderCenter() {
        if (AppUtils.INSTANCE.isLogin()) {
            if (AppUtils.INSTANCE.getAreaCode() == "sc") {
//                ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
//                        .withString("html", StringUtil.INSTANCE.getWeChatUrl("https://site241962.c.tsichuan.com/#/user-reservation"))
//                        .navigation();
                ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_APPOINTMENT_SC_LIST)
                        .withString("type", ResourceType.CONTENT_TYPE_VENUE)
                        .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .navigation();
            } else {
                ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_APPOINTMENT_LIST)
                        .navigation();
            }
            if (weakReference.get() != null) {
                weakReference.get().finish();
            }
        } else {
            ToastUtils.showMessage("非常抱歉，登录后才能访问~");
            ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation();
        }
    }

    @JavascriptInterface
    public void setAppToken() {

    }

    @JavascriptInterface
    public void backToMainPage() {
        ARouter.getInstance().build(ARouterPath.MainModule.MAIN_ACTIVITY)
                .navigation();
    }

    /**
     * 重定向到app登录
     */
    @JavascriptInterface
    public void redirctAppLogin() {
        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                .navigation();
    }

    /**
     * 获取apptoken
     *
     * @return
     */
    @JavascriptInterface
    public String getAppToken() {
        String token = SPUtils.getInstance().getString(SPUtils.Config.TOKEN);
        return token;
    }


    @JavascriptInterface
    public void setWebViewHeight(int height) {
//        if(webView!=null){
//            ViewGroup.LayoutParams layoutParams = webView.getLayoutParams();
//            layoutParams.height=height;
//            webView.setLayoutParams(layoutParams);
//        }
    }
}
