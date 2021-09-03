package com.daqsoft.provider.utils

import android.annotation.SuppressLint
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Description 添加竖向分隔符“|”格式工具
 * @ClassName   TimeUtils
 * @Author      PuHua
 * @Time        2019/11/22 14:29
 */
object DividerTextUtils {
    /**
     * 将多个字符串合成一个带有"|"的字符串
     */
    fun convertString(sb: StringBuilder, vararg args: String): String {
        for (it in args) {
            if (!it.isNullOrEmpty())
                sb.append(it).append("  |  ")
        }
        val sbb = StringBuilder(sb.trim())
        var s = ""
        if (!sb.isNullOrEmpty()) {
            s = sbb.deleteCharAt(sbb.lastIndex).toString()
        }
        return s
    }

    /**
     * 将多个字符串合成一个带有"·"的字符串
     */
    fun convertDotString(sb: StringBuilder, vararg args: String): String {
        for (it in args) {
            if (!it.isNullOrEmpty())
                sb.append(it).append("·")
        }
        val sbb = StringBuilder(sb.trim())
        var s = ""
        if (!sb.isNullOrEmpty()) {
            s = sbb.deleteCharAt(sbb.lastIndex).toString()
        }
        return s
    }

    /**
     * 将多个字符串合成一个带有"·"的字符串
     */
    fun converttDivideString(sb: StringBuilder, divider: String, vararg args: String): String {
        for (it in args) {
            if (!it.isNullOrEmpty())
                sb.append(it).append(divider)
        }
        val sbb = StringBuilder(sb.trim())
        var s = ""
        if (!sb.isNullOrEmpty()) {
            s = sbb.deleteCharAt(sbb.lastIndex).toString()
        }
        return s
    }

    /**
     * 将多个字符串合成一个带有"·"的字符串
     */
    @JvmSuppressWildcards
    fun convertDotString(args: List<String>): String {

        val sb = StringBuilder()
        for (it in args) {
            if (!it.isNullOrEmpty())
                sb.append(it).append("·")
        }
        val sbb = StringBuilder(sb.trim())
        var s = ""
        if (!sb.isNullOrEmpty()) {
            s = sbb.deleteCharAt(sbb.lastIndex).toString()
        }
        return s
    }

    /**
     * 将多个字符串合成一个带有"·"的字符串
     */
    @JvmSuppressWildcards
    fun convertCommaString(args: List<String>): String {

        val sb = StringBuilder()
        for (it in args) {
            if (!it.isNullOrEmpty())
                sb.append(it).append(",")
        }
        val sbb = StringBuilder(sb.trim())
        var s = ""
        if (!sb.isNullOrEmpty()) {
            s = sbb.deleteCharAt(sbb.lastIndex).toString()
        }
        return s
    }

    /**
     * 将多个字符串合成一个带有"·"的字符串
     */
    @JvmSuppressWildcards
    fun convertString(args: List<String>, divideStr: String): String {

        val sb = StringBuilder()
        for (it in args) {
            if (!it.isNullOrEmpty())
                sb.append(it).append(divideStr)
        }
        val sbb = StringBuilder(sb.trim())
        var s = ""
        if (!sb.isNullOrEmpty()) {
            s = sbb.deleteCharAt(sbb.lastIndex).toString()
        }
        return s
    }

    /**
     * 将多个字符串合成一个带有"·"的字符串
     */
    @JvmSuppressWildcards
    fun convertMuCommaString(args: MutableList<String>): String {

        val sb = StringBuilder()
        for (it in args) {
            if (!it.isNullOrEmpty())
                sb.append(it).append(",")
        }
        val sbb = StringBuilder(sb.trim())
        var s = ""
        if (!sb.isNullOrEmpty()) {
            s = sbb.deleteCharAt(sbb.lastIndex).toString()
        }
        return s
    }
}