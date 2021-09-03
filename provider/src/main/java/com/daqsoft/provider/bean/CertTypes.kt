package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   CertTypes
 * @Author      luoyi
 * @Time        2020/8/12 18:56
 */
object CertTypes {

    var certTypes: MutableList<CertType> = mutableListOf(
        CertType("身份证", "ID_CARD"),
        CertType("护照", "PASSPORT"),
        CertType("军官证或士官证", "SERGEANT_CERTIFICATE")
        , CertType("港澳通行证", "HONG_KONG_MACAO_PASS"),
        CertType("港澳台身份证", "HONG_KMTIC"),
        CertType("台胞证", "TAIWAN_COMPATRIOTS")
    )

    /**
     * 获取证件类型
     */
    fun getCertTypeName(value: String?): String {
        if (value.isNullOrEmpty()) {
            return ""
        }
        var name: String = ""

        for (item in certTypes) {
            if (value == item.value) {
                name = item.name
                break
            }
        }
        return name
    }

    fun isIdCardType(value: String): Boolean {
        if (value.toLowerCase() == "id_card") {
            return true
        }
        return false
    }
}

data class CertType(var name: String, var value: String)