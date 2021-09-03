package com.daqsoft.itinerary.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import java.util.*

/**
 * Activity管理器
 * @author 邓益千
 * @date 2020年5月28日
 */
class ItineraryManager private constructor() {

    private val activityStack: Stack<Activity> = Stack()

    companion object {
        val instance: ItineraryManager by lazy { ItineraryManager() }
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
     * 清理栈
     */
    fun finishAllActivity() {
        for (activity in activityStack) {
            activity.finish()
        }
        activityStack.clear()
    }

}
