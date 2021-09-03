package com.daqsoft.travelCultureModule.country.util

import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan

/**
 * desc :设置部分字体
 * @author 江云仙
 * @date 2020/4/20 19:36
 */
object TextFontUtil {
    /**
     * 设置一个字符串中部分字体大小和颜色
     *
     * @param content    内容
     * @param colorSize  字体大小
     * @param startIndex 从第几个开始
     * @param endIndex   到第几个结束
     * @return
     */
    fun setTextSizeAndColor(content: String?, colorSize: Int, colorId: Int, startIndex: Int, endIndex: Int): SpannableString? {
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
    fun setTextSize(content: String?, colorSize: Int, startIndex: Int, endIndex: Int): SpannableString? {
        val result = SpannableString(content)
        result.setSpan(AbsoluteSizeSpan(colorSize), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return result
    }

    /**
     *设置部分字体加粗和大小
     */
    fun setTextBoldAndSize(content: String?, colorSize: Int, startIndex: Int, endIndex: Int): SpannableStringBuilder {
        val spannable = SpannableStringBuilder(content)
        val ab = AbsoluteSizeSpan(colorSize, true)
        val boldSp = StyleSpan(Typeface.BOLD)
        spannable.setSpan(ab, 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(boldSp, 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }
}