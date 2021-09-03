package com.daqsoft.baselib.base

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import java.util.*

/**
 * Activity管理器
 * @author 周俊蒙
 * @date 2019/7/2
 */
class AppManager private constructor() {

    private val activityStack: Stack<Activity> = Stack()

    companion object {
        val instance: AppManager by lazy { AppManager() }
    }

    /**
     *  Activity入栈
     */
    fun addActivity(activity: Activity) {
        activityStack.add(activity)
    }

    /**只出栈，不做操作*/
    fun removeActivity(activity: Activity){
        activityStack.remove(activity)
    }

    /**
     * Activity出栈
     */
    fun finishActivity(activity: Activity) {
        activity.finish()
        activityStack.remove(activity)
    }

    /**
     * 获取当前栈顶
     */
    fun currentActivity(): Activity {
        return activityStack.lastElement()
    }

    /**
     * 清理栈
     */
    fun finishAllActivity() {
        for (activity in activityStack) {
            activity.finish()
        }
        activityStack.clear()
    }

    /**
     * 清除除了当前Activity以外的activityStack栈
     */
    fun finishAllActivityExceptThis(activity: Activity) {
        for (item in activityStack) {
            if (item != activity) {
                item.finish()
            }
        }
    }

    /**
     * 跳转到首页
     */
    fun gotoHomeActivity(){
        for (item in activityStack) {
            if(item != activityStack.firstElement()){
                item.finish()
            }
        }
    }

    /**
     * 退出应用程序
     */
    fun exitApp(context: Context) {
        finishAllActivity()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.killBackgroundProcesses(context.packageName)
        System.exit(0)
    }
}
