package com.daqsoft.baselib.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View

/**
 * @Author：      邓益千
 * @Create by：   2020/5/19 16:32
 * @Description：
 */
object ResourceUtils {

    fun getColor(context: Context,colorId: Int): Int{
        return context.resources.getColor(colorId)
    }

    fun getColor(view: View, colorId: Int): Int{
        return view.context.resources.getColor(colorId)
    }

    fun getDrawable(context: Context,drawableId: Int): Drawable {
        return context.resources.getDrawable(drawableId)
    }

    fun getDrawable(view: View,drawableId: Int): Drawable {
        return view.context.resources.getDrawable(drawableId)
    }

    fun getDimension(context: Context,resId: Int): Int{
        return context.resources.getDimensionPixelSize(resId)
    }

    fun getDimension(view: View,resId: Int): Int{
        return view.context.resources.getDimensionPixelSize(resId)
    }

}