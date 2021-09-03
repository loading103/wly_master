package com.daqsoft.baselib.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import com.bigkoo.pickerview.view.BasePickerView
import com.daqsoft.baselib.base.BaseApplication

/**
 * 弹框高度的处理
 * @author 黄熙
 * @date 2020/3/10 0010
 * @version 1.0.0
 * @since JDK 1.8
 */
object UIHelperUtils {

    fun showOptionsPicker(activity: Activity, view: BasePickerView) {
        var layoutParams: FrameLayout.LayoutParams =
            view.dialogContainerLayout.layoutParams as FrameLayout.LayoutParams
        layoutParams.bottomMargin = getNavigationBarHeight(activity)
        view.dialogContainerLayout.layoutParams = layoutParams
        view.show()
    }

    /**
     * 获得NavigationBar的高度
     */
    fun getNavigationBarHeight(activity: Activity): Int {
        var result = 0
        val resources: Resources = activity.getResources()
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0 && checkHasNavigationBar(activity)) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**
     * 判断是否有NavigationBar
     * @param activity
     * @return
     */
    fun checkHasNavigationBar(activity: Activity): Boolean {
        val windowManager: WindowManager = activity.getWindowManager()
        val d: Display = windowManager.getDefaultDisplay()
        val realDisplayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics)
        }
        val realHeight: Int = realDisplayMetrics.heightPixels
        val realWidth: Int = realDisplayMetrics.widthPixels
        val displayMetrics = DisplayMetrics()
        d.getMetrics(displayMetrics)
        val displayHeight: Int = displayMetrics.heightPixels
        val displayWidth: Int = displayMetrics.widthPixels
        return realWidth - displayWidth > 0 || realHeight - displayHeight > 0
    }

    /**
     * 隐藏键盘栏
     */
    fun hideKeyboard(view: View) {
        if (view != null) {
            val manager: InputMethodManager = view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }
    }
    /**
     * 获取应用程序名称
     */
    @Synchronized
    fun getAppName(): String? {
        try {
            val packageManager: PackageManager =BaseApplication.context.packageManager
            val packageInfo: PackageInfo = packageManager.getPackageInfo(
                BaseApplication.context.packageName, 0
            )
            val labelRes: Int = packageInfo.applicationInfo.labelRes
            return BaseApplication.context.resources.getString(labelRes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}