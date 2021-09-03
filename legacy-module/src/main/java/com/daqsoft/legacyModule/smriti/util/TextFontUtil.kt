package com.daqsoft.legacyModule.smriti.util

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
    fun setTextBoldAndSize(content: String?, colorId: Int?,textSize: Int?, startIndex: Int, endIndex: Int,isBold:Boolean): SpannableString {
        val spannable = SpannableString(content)
        if (textSize!=null){
            val ab = AbsoluteSizeSpan(textSize, true)
            spannable.setSpan(ab, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (colorId!=null){
            val colorSpan = ForegroundColorSpan(colorId)
            spannable.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (isBold){
            val boldSp = StyleSpan(Typeface.BOLD)
            spannable.setSpan(boldSp, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return spannable
    }
}