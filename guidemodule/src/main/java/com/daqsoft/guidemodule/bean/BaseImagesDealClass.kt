package com.daqsoft.guidemodule.bean

import android.text.Html


internal fun String.safeSubstring(includeStart: Int, excludeEnd: Int): String {
    val length = if (!this.isNullOrEmpty()) this.length else 0
    return if (length < excludeEnd)
        this
    else
        this.substring(includeStart, excludeEnd)+ "..."
}

internal fun String.getImages(): String {
    if (!this.isNullOrEmpty()) {

        if (!this.contains(","))
            return this

        val imgList = this.split(",")
        if (!imgList.isNullOrEmpty()) {
            return imgList[0]
        }
    }
    return ""
}

internal fun String.toHtmlCharSequence(): CharSequence {
    return Html.fromHtml(this)
}

//internal fun String.removeHtmlTag(): CharSequence{
//      return Html.fromHtml(this)
//}