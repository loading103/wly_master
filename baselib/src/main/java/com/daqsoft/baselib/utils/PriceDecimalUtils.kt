package com.daqsoft.baselib.utils

import java.math.BigDecimal

/**
 * @Description
 * @ClassName   PriceDecimalUtl
 * @Author      luoyi
 * @Time        2020/7/22 10:39
 */
object PriceDecimalUtils {

    // 需要精确至小数点后几位
    const val DECIMAL_POINT_NUMBER: Int = 2

    // 加法运算
    @JvmStatic
    fun add(d1: Double, d2: Double): Double = BigDecimal(d1.toString()).add(BigDecimal(d2.toString())).setScale(DECIMAL_POINT_NUMBER, BigDecimal.ROUND_DOWN).toDouble()

    // 减法运算
    @JvmStatic
    fun sub(d1: Double, d2: Double): Double = BigDecimal(d1.toString()).subtract(BigDecimal(d2.toString())).setScale(DECIMAL_POINT_NUMBER, BigDecimal.ROUND_DOWN).toDouble()

    // 乘法运算
    @JvmStatic
    fun mul(d1: Double, d2: Double, decimalPoint: Int): Double = BigDecimal(d1.toString()).multiply(BigDecimal(d2.toString())).setScale(decimalPoint, BigDecimal.ROUND_DOWN).toDouble()

    // 除法运算
    @JvmStatic
    fun div(d1: Double, d2: Double): Double = BigDecimal(d1.toString()).divide(BigDecimal(d2.toString())).setScale(DECIMAL_POINT_NUMBER, BigDecimal.ROUND_DOWN).toDouble()

}