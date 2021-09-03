package com.daqsoft.baselib.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import com.daqsoft.baselib.base.BaseApplication

/**
 * @Description
 * @ClassName   AppUtils
 * @Author      luoyi
 * @Time        2020/4/14 19:09
 */
object AppUtils {
    /**
     * 根据是否有token判断是否登录
     */
    fun isLogin(): Boolean {
        var token = SPUtils.getInstance().getString(SPUtils.Config.TOKEN)
        return !token.isNullOrEmpty()
    }

    fun getAreaCode():String{
        return  BaseApplication.appArea
    }


    /**
     * 判断 app 是否在运行
     * @param context Context
     * @param pkgName String
     * @return Boolean
     */
    fun isAppRunning(context: Context, pkgName: String):Boolean {
        if (pkgName.isBlank()){
            return false
        }
        val ai = context.applicationInfo
        val uid = ai.uid
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (am != null) {
            val taskInfo = am.getRunningTasks(Int.MAX_VALUE)
            if (taskInfo != null && taskInfo.size > 0) {
                for (aInfo in taskInfo) {
                    if (aInfo.baseActivity != null) {
                        if (pkgName == aInfo.baseActivity!!.packageName) {
                            return true
                        }
                    }
                }
            }
            val serviceInfo = am.getRunningServices(Int.MAX_VALUE)
            if (serviceInfo != null && serviceInfo.size > 0) {
                for (aInfo in serviceInfo) {
                    if (uid == aInfo.uid) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * 判断 app 是否在前台
     * @param context Context
     * @return Boolean
     */
    fun isAppForeground(context: Context):Boolean{
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcessInfoList = activityManager.runningAppProcesses
        for (appProcessInfo in appProcessInfoList) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                && appProcessInfo.processName == context.applicationInfo.processName
            ) {
                return true
            }
        }
        return false
    }

    /**
     * 将app 置为前台
     * @param context Context
     */
    @SuppressLint("MissingPermission")
    fun setTopApp(context: Context){
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val taskInfoList = activityManager.getRunningTasks(100)
        for (taskInfo in taskInfoList) {
            if (taskInfo.topActivity!!.packageName == context.packageName) {
                activityManager.moveTaskToFront(taskInfo.id, 0)
                break
            }
        }
    }
    /**
     * activity stack 中是是否有 activity
     */
    fun  isExistActivity(context: Context,cls: Class<*>) : Boolean{
        val intent = Intent(context, cls)
        val componentName =  intent.resolveActivity(context.packageManager)
        var flag = false
        if (componentName != null){
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val stackInfo = activityManager.getRunningTasks(10)
            stackInfo.forEach{
                if (it.baseActivity != null && it.baseActivity == componentName){
                    flag = true
                }
            }
        }
        return flag
    }
}