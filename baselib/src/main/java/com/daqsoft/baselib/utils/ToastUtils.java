package com.daqsoft.baselib.utils;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.daqsoft.baselib.base.BaseApplication;

import daqsoft.com.baselib.R;


/**
 * @author ly
 * @version V1.0
 * @Title: ToastUtils
 * @Package com.daqsoft.baselib.utils
 * @Description: 自定义toast工具
 * @date 2015/9/16 16:23
 */

public class ToastUtils {

    private static Handler handler = new Handler(Looper.getMainLooper());

    private static Toast toast = null;
    private static Toast imgToast = null;
    private static Toast txtEveryDayToast = null;
    private static Object synObj = new Object();

    public static void showMessage(final String msg) {
        showMessage(msg, Toast.LENGTH_SHORT);
    }

    public static void showMessage(final int msg) {
        showMessage(msg, Toast.LENGTH_SHORT);
    }

    public static void showMessage(final CharSequence msg, final int len) {
        if (msg == null || msg.equals("")) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (synObj) { // 加上同步是为了每个toast只要有机会显示出来
                    if (toast != null) {
                        toast.setText(msg);
                        toast.setDuration(len);
                    } else {
                        toast = Toast.makeText(BaseApplication.context, msg, len);
                    }
                    toast.show();
                }
            }
        });
    }

    public static void showMessage(final int msg, final int len) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (synObj) {
                    if (toast != null) {
                        // toast.cancel();
                        toast.setText(msg);
                        toast.setDuration(len);
                    } else {
                        toast = Toast.makeText(BaseApplication.context, msg, len);
                    }
                    toast.show();
                }
            }
        });
    }

    /**
     * 未登录提示
     */
    public static void showUnLoginMsg() {
        showMessage(R.string.unlogin_tip);
    }

    /**
     * 未登录提示
     */
    public static void showInProgressMsg() {
        showMessage(R.string.in_progress);
    }


    /**
     * 显示toast 提示
     * @param message 提示
     * @param height 要展示的屏幕高度比例 0-1 之间 从上到下
     */
    public static void showMessageScreenBased(final String message, final Float height ){
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (synObj) {
                    if (toast != null) {
                        toast.setText(message);
                        toast.setDuration(Toast.LENGTH_SHORT);
                    } else {
                        toast = Toast.makeText(BaseApplication.context, message, Toast.LENGTH_SHORT);
                    }

                    if (height != null && height > 0 && height <=1){
                        WindowManager windowManager = (WindowManager) BaseApplication.context.getSystemService(Context.WINDOW_SERVICE);
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
                        int heightPixels = displayMetrics.heightPixels;
                        toast.setGravity(Gravity.TOP, 0, (int)(heightPixels * height));
                    }

                    toast.show();
                }
            }
        });
    }
}
