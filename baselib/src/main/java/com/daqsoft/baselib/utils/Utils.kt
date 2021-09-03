package com.daqsoft.baselib.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.JsonParser
import org.jsoup.Jsoup
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * 工具类
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019/4/20
 * @since JDK 1.8
 */
object Utils {

    /**
     * 转换公里
     * @param metres 单位米
     */
    fun toKm(metres: Double): Double{
        val df = DecimalFormat("0.00")
        val cc = metres / 1000.0
        return df.format(cc).toDouble()
    }

    /**
     * 解决Gson解析集合泛型擦除问题（用于解析集合参数）
     * @param string 需要转换的字符串
     * @param clazz 需要转换的泛型类
     * @return 转换过后的集合
     */
    fun <T> jsonStringToList(string: String, clazz: Class<T>): List<T> {
        val gson = Gson()
        val lst = ArrayList<T>()
        val array = JsonParser().parse(string).asJsonArray
        array.forEach {
            lst.add(gson.fromJson(it, clazz))
        }
        return lst
    }

    /**
     * 将dp转换成px
     * @param context 上下文
     * @param dpValue dp值
     * @return
     */
    @JvmStatic
    fun dip2px(context: Context, dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }

    @JvmStatic
    fun sp2px(context: Context, spValue: Float): Float {
        if (spValue <= 0) {
            return 0f
        }
        val scale = context.resources.displayMetrics.scaledDensity
        return spValue * scale
    }

    /**
     * 将像素转换成dp
     * @param context 上下文
     * @param pxValue px值
     * @return
     */
    @JvmStatic
    fun px2dip(context: Context, pxValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f)
    }

    /**
     * 获取手机高度px
     *
     * @param context
     * @return
     */
    fun getHeightInPx(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    fun getStateBarHeight(context: Context): Int {
        var result = 0
        try {
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
        } catch (e: Exception) {
        }
        return result
    }

    /**
     * 姓名脱敏
     */
    fun nameInvisible(name: String?): String {
        if (name?.length ?: 0 < 2) {
            return ""
        } else {
            return name?.replaceRange(0, 1, "*") ?: ""
        }
    }

    fun phoneInvisible(phone: String?): String {
        if (phone.isNullOrEmpty()) {
            return ""
        }
        if (phone.length == 11) {
            return phone.replaceRange(3, 7, "****") ?: ""
        }
        return phone
    }

    /**
     * 身份证信息脱敏
     */
    fun IDNumberInvisible(num: String?): String {
        if (num.isNullOrEmpty()) {
            return ""
        }
        var number: String = num.trimEnd()
        if (num.length > 18) {
            number = num.substring(0, 18)
        }
        if (RegexUtils.isIDCard15(number)) {
            return number?.replaceRange(7, 11, "****") ?: ""
        } else if (RegexUtils.isIDCard18(number)) {
            return number?.replaceRange(8, 14, "*******") ?: ""
        } else {
            return ""
        }
    }

    /**
     * 获取宽度px
     *
     * @param context
     * @return
     */
    fun getWidthInPx(context: Context): Int {
        val manager = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay
        val point = Point()
        if (Build.VERSION.SDK_INT < 17) {
            display.getSize(point)
        } else {
            display.getRealSize(point)
        }
        return point.x
    }

    /**
     * html文档屏幕宽度自适应
     *
     * @param htmltext
     * @return
     */
    fun getNewContent(htmltext: String): String {
        val doc = Jsoup.parse(htmltext)
        val elements = doc.getElementsByTag("img")
        for (element in elements) {
            element.attr("width", "100%").attr("height", "auto")
        }
        val textSizes = doc.getElementsByTag("font-size")
        for (textSize in textSizes) {
            textSize.attr("font-size", "32px")
        }
        return doc.toString()
    }


    /**
     * 拨打电话
     *
     * 请在清单文件中注册CALL_PHONE权限
     * @param context 上下文
     * @param phone 电话号码
     */
    fun callPhone(context: Context, phone: String) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.CALL_PHONE),
                0
            )
            return
        }
        val intent = Intent("android.intent.action.CALL", Uri.parse("tel:$phone"))
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /**
     * 初始化PopupWindow
     * @param activity 当前activity界面
     * @param alpha 透明度
     * @param width 弹框宽度
     * @param height 弹框高度
     */
    fun initPopupWindow(
        activity: Activity,
        alpha: Float,
        width: Int,
        height: Int
    ): PopupWindow {
        setWindowBackGroud(activity, alpha)

        val popupWindows = PopupWindow(width, height)
        popupWindows.isOutsideTouchable = false
        popupWindows.isTouchable = true
        // 防止点击事件穿透
        popupWindows.isFocusable = true
        popupWindows.setBackgroundDrawable(ColorDrawable(0))
        // 适配虚拟键盘
        popupWindows.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE

        return popupWindows
    }

    /**
     * 设置当前窗口的背景的透明度
     * @param activity 当前activity界面
     * @param alpha 透明度
     */
    fun setWindowBackGroud(activity: Activity, alpha: Float) {
        val lp = activity.window.attributes
        lp.alpha = alpha
        if (alpha == 1f) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        } else {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
        activity.window.attributes = lp
    }


    /**
     * 半角空格的值，在ASCII中为32(Decimal)
     * 半角空格
     */
    const val DBC_SPACE = ' '

    /**
     * 全角空格的值，它没有遵从与ASCII的相对偏移，必须单独处理
     * 全角空格 12288
     */
    const val SBC_SPACE: Char = 12288.toChar()

    /**
     * ASCII表中可见字符从!开始，偏移位值为33(Decimal)
     * 半角!
     */
    const val DBC_CHAR_START: Char = 33.toChar()

    /**
     * ASCII表中可见字符到~结束，偏移位值为126(Decimal)
     * 半角~
     */
    const val DBC_CHAR_END: Char = 126.toChar()

    /**
     * ASCII表中除空格外的可见字符与对应的全角字符的相对偏移
     * 全角半角转换间隔
     */
    const val CONVERT_STEP = 65248

    /**
     * 半角转为全角
     */
    fun toDBC(src: String): String {
        val buf = StringBuilder(src.length)
        val ca = src.toCharArray()
        for (i in ca.indices) {
            if (ca[i] == DBC_SPACE) {
                // 如果是半角空格，直接用全角空格替代
                buf.append(SBC_SPACE)
            } else if ((ca[i] >= DBC_CHAR_START) && (ca[i] <= DBC_CHAR_END)) {
                // 字符是!到~之间的可见字符
                buf.append((ca[i] + CONVERT_STEP))
            } else {
                // 不对空格以及ascii表中其他可见字符之外的字符做任何处理
                buf.append(ca[i])
            }
        }
        return buf.toString()
    }


    const val datePattern = "yyyy-MM-dd HH:mm:ss"
    const val dateYMD = "yyyy-MM-dd"
    const val YMD = "yyyy年MM月dd日"
    const val MD = "MM月dd日"
    const val YM = "yyyy-MM"

    /**
     * 精确到毫秒
     */
    const val MS_FORMART = "yyyy-MM-dd HH:mm:ss SSS"
    const val timePattern = "$datePattern HH:MM a"
    const val YMDHM = "yyyy-MM-dd HH:mm"

    /**
     * Data->String
     * @param aMask 转换规则
     * @param aDate Date时间
     */
    fun getDateTime(aMask: String, aDate: Date?): String {
        var df: SimpleDateFormat? = null
        var returnValue = ""

        if (aDate == null) {
            print("aDate is null!")
        } else {
            df = SimpleDateFormat(aMask)
            returnValue = df.format(aDate)
        }

        return returnValue
    }

    /**
     * 两个日期比较，格式 yyyy-MM-dd
     * @param dateOne 第一个日期
     * @param dateTwo 第二个日期
     * @return -1第一个日期小于第二个日期
     */
    fun dateCompare(dateOne: String, dateTwo: String): Int{
        return try {
            val format = SimpleDateFormat(dateYMD)
            val date1 = format.parse(dateOne)
            val date2 = format.parse(dateTwo)
            return date1.compareTo(date2)
        } catch (ex :ParseException){
            ex.printStackTrace()
            2
        }
    }

    /**
     * Data->String
     * @param aMask 转换规则
     * @param aDate Date时间
     */
    fun getDateTime(aMask: String, aDate: Int?): String {
        var df: SimpleDateFormat? = null
        var returnValue = ""

        if (aDate == null) {
            print("aDate is null!")
        } else {
            df = SimpleDateFormat(aMask)
            returnValue = df.format(aDate)
        }

        return returnValue
    }

    /**
     *毫秒转化为当前时间
     */
    fun transferLongToDate(dateFormat: String, millSecond: Long): String? {
        val time = Date(millSecond)
        val formats = SimpleDateFormat(dateFormat)
        return formats.format(time)
    }

    @SuppressLint("SimpleDateFormat")
    fun isValidDate(str: String, aMask: String): Boolean {
        var convertSuccess = true
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        val format = SimpleDateFormat(aMask)
        try {

            format.isLenient = false
            format.parse(str)
        } catch (e: ParseException) {
            convertSuccess = false
        }

        return convertSuccess
    }


    /**将日期中的 - 替换成 . */
    fun replacePoint(text: String?): String {
        if (text.isNullOrEmpty()) {
            return ""
        }
        var result = text
        try {
            if (!text.isNullOrEmpty() && text.contains("-")) {
                result = text.replace("-", ".")
            }
        } catch (e: java.lang.Exception) {

        }

        return result!!
    }

    /**
     * 转换公里
     * @param metres 单位米
     */
    fun toKm(metres: Float): Float {
        val df = DecimalFormat("0.00")
        return df.format(metres / 1000f).toFloat()
    }

    /**
     * 获取url 指定name的value
     * @param url
     * @param name
     */
    fun getValueByName(url: String, name: String): String? {
        if (url.isEmpty() || name.isEmpty())
            return ""
        var result = ""
        val index = url.indexOf("?")
        val temp = url.substring(index + 1)
        val keyValue = temp.split("&").toTypedArray()
        for (str in keyValue) {
            if (str.contains(name)) {
                result = str.replace("$name=", "")
                break
            }
        }
        return result
    }

    /**
     * 月份->英文缩写 转换
     * @param month String 月份
     * @return 缩写
     */
    fun coverMonthAbbreviation(month:String):String{
        return "${when(month){
            "1"->"Jan"
            "2"->"Feb"
            "3"->"Mar"
            "4"->"Apr"
            "5"->"May"
            "6"->"Jun"
            "7"->"Jul"
            "8"->"Aug"
            "9"->"Sep"
            "10"->"Oct"
            "11"->"Nov"
            "12"->"Dec"
            else->""
        }}."
    }
}