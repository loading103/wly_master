package com.daqsoft.thetravelcloudwithculture.jpush

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.provider.bean.MyNotificationExtra
import com.daqsoft.provider.utils.StartActivityUtils
import com.daqsoft.thetravelcloudwithculture.ui.MainActivity
import com.google.gson.Gson

/**
 * @package name：com.daqsoft.module_main.uitls
 * @date 8/1/2021 上午 10:09
 * @author zp
 * @describe
 */
object NotifyMessageOpenHelper {
    private var   PAKE_BAG = "com.daqsoft.thetravelcloudwithculture.test"

    /**
     * 处理跳转
     * @param context Context
     * @param message NotificationMessage?
     *todo qql
     */
    fun handleJump(context: Context,message : String?){
        when(BaseApplication.appArea){
            "sc"-> PAKE_BAG = "com.daqsoft.thetravelcloudwithculture.sc"
            "ws"-> PAKE_BAG = "com.daqsoft.thetravelcloudwithculture.ws"
            "xj"-> PAKE_BAG = "com.daqsoft.thetravelcloudwithculture.xj"
            "test"-> PAKE_BAG = "com.daqsoft.thetravelcloudwithculture.test"
        }
        val extra = Gson().fromJson(message, MyNotificationExtra::class.java)
        if (AppUtils.isAppRunning(context,PAKE_BAG)) {
//            if (!AppUtils.isAppForeground(context)) {
//                AppUtils.setTopApp(context)
//            }
            if (AppUtils.isExistActivity(context, MainActivity::class.java)){
                pageJump(extra)
            }else{
                var launchIntent =  context.packageManager.getLaunchIntentForPackage(PAKE_BAG)
                if (launchIntent == null){
                    launchIntent = Intent(Intent.ACTION_MAIN)
                    launchIntent.component = ComponentName(PAKE_BAG, "com.daqsoft.thetravelcloudwithculture.ui.SplashActivity")
                    launchIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                }
                launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                val bundle = Bundle()
                bundle.putParcelable("notifyExtra",extra)
                launchIntent.putExtra("notifyBundle",bundle)
                context.startActivity(launchIntent)
            }

        } else {
            var launchIntent =  context.packageManager.getLaunchIntentForPackage(PAKE_BAG)
            if (launchIntent == null){
                launchIntent = Intent(Intent.ACTION_MAIN)
                launchIntent.component = ComponentName(PAKE_BAG, "com.daqsoft.thetravelcloudwithculture.ui.SplashActivity")
                launchIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            }
            launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            val bundle = Bundle()
            bundle.putParcelable("notifyExtra",extra)
            launchIntent.putExtra("notifyBundle",bundle)
            context.startActivity(launchIntent)
        }
    }

    /**
     * 页面跳转
     * @param extra MyNotificationExtra
     *
    }
     *
     */
    fun pageJump(extra: MyNotificationExtra){
        Log.e("pageJump---------",Gson().toJson(extra))
        StartActivityUtils.startNoticeActivity(extra)
    }

}