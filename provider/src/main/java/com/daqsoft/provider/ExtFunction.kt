package com.daqsoft.provider

import android.content.res.Resources
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.lang.Exception

fun String?.getRealImages(): String {
    if (!this.isNullOrEmpty()) {
        if (!this.contains(","))
            return this
        val imgList = this.split(",")
        if (!imgList.isNullOrEmpty()) {
            return imgList[0]
        }
        return ""
    }
    return ""
}

/**
 * 扩展函数，字符串转整形
 */
fun String.toLegalInt(): Int {
    return if (this.isNullOrEmpty()) {
        0
    } else {
        try {
            this.toInt()
        } catch (e: Exception) {
            0
        }

    }
}

/**
 * 扩展函数，字符是否为合法整形
 */
fun String.isLegalInt(): Boolean {
    return if (this.isNullOrEmpty()||this=="NULL") {
        false
    } else {
        this != "0"
    }
}

internal fun ImageView.loadWithDefault(url: String) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.placeholder_img_fail_h158)
        .into(this)
}


internal inline val Int.sp2px: Float
    get() = this.toFloat().sp2px

internal inline val Int.px2sp: Float
    get() = this.toFloat().px2sp

internal inline val Int.dp: Int
    get() = this.toFloat().dp.toInt()

internal inline val Int.px: Int
    get() = this.toFloat().px.toInt()


internal inline val Float.sp2px: Float
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity + 0.5f)

internal inline val Float.px2sp: Float
    get() = (this / Resources.getSystem().displayMetrics.scaledDensity + 0.5f)

internal inline val Float.dp: Float
    get() = (this / Resources.getSystem().displayMetrics.density)

internal inline val Float.px: Float
    get() = (this * Resources.getSystem().displayMetrics.density)
