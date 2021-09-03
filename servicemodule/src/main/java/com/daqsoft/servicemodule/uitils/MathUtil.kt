package com.daqsoft.servicemodule.uitils

import java.math.BigDecimal

/**
 * desc :运算
 * @author 江云仙
 * @version 1.0.0
 * @date 2020/4/7 11:21
 * @since JDK 1.8
 */
object MathUtil {
    /**
     *除法运算 保留一位小数
     */
    fun div(v1: Double, v2: Double, scale: Int): Double {
        require(scale >= 0) { "The scale must be a positive integer or zero" }
        val b1 = BigDecimal(v1.toString())
        val b2 = BigDecimal(v2.toString())
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toDouble()
    }
}