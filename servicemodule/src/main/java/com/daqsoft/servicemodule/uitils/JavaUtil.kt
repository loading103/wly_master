package com.daqsoft.servicemodule.uitils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager


/**
 * desc :
 * @author 江云仙
 * @date 2020/4/8 10:17
 */
object JavaUtil {
    /**
     * 隐藏键盘栏
     */
    fun hideKeyboard(context: Activity) {
        var  imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // 隐藏软键盘
        imm.hideSoftInputFromWindow(context.getWindow().getDecorView().getWindowToken(), 0);
    }

}