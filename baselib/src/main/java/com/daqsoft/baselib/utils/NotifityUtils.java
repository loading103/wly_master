package com.daqsoft.baselib.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import com.daqsoft.baselib.base.BaseApplication;


public class NotifityUtils {


    /**
     * 检查是否开启通知
     */
    public  static boolean isNotificationEnabled( ) {
        boolean isOpened = false;
        try {
            isOpened = NotificationManagerCompat.from(BaseApplication.context).areNotificationsEnabled();
        } catch (Exception e) {
            e.printStackTrace();
            isOpened = false;
        }
        return isOpened;

    }
    /**
     * 打开通知设置
     */
    public static void openNotice(){
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", BaseApplication.context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package",BaseApplication.context.getPackageName());
            intent.putExtra("app_uid",BaseApplication.context.getApplicationInfo().uid);
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package",BaseApplication.context.getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApplication.context.startActivity(intent);
    }
}
