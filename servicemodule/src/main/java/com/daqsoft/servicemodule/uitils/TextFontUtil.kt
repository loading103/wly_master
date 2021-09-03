package com.daqsoft.servicemodule.uitils

import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan

object  TextFontUtil {
    /**
     * 设置一个字符串中部分字体大小和颜色
     *
     * @param content    内容
     * @param colorSize  字体大小
     * @param startIndex 从第几个开始
     * @param endIndex   到第几个结束
     * @return
     */
    fun setTextSizeAndColor(content: String?, colorSize: Int,colorId:Int, startIndex: Int, endIndex: Int): SpannableString? {
        val result = SpannableString(content)
        result.setSpan(ForegroundColorSpan(colorId), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        result.setSpan(AbsoluteSizeSpan(colorSize), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return result
    }
    /**
     * 设置一个字符串中部分字体大小改变
     *
     * @param content    内容
     * @param colorSize  字体大小
     * @param startIndex 从第几个开始
     * @param endIndex   到第几个结束
     * @return
     */
    fun setTextSize(content: String?, colorSize: Int,startIndex: Int, endIndex: Int): SpannableString? {
        val result = SpannableString(content)
        result.setSpan(AbsoluteSizeSpan(colorSize), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return result
    }
}