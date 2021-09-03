package com.daqsoft.travelCultureModule.contentActivity

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import java.text.DecimalFormat

fun formatTosepara(data: Int): String? {
    val df = DecimalFormat("#,###")
    return df.format(data)
}
fun hideKeyboard(context: Activity) {
    var  imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    // 隐藏软键盘
    imm.hideSoftInputFromWindow(context.getWindow().getDecorView().getWindowToken(), 0);
}
