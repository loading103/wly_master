package com.daqsoft.provider.bean

import android.text.SpannableStringBuilder
import com.amap.api.maps.model.LatLng
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.utils.AddressUtil
import com.daqsoft.provider.utils.DividerTextUtils
import java.lang.StringBuilder

/**
 *@package:com.daqsoft.thetravelcloudwithculture.sc.bean
 *@date:2020/5/20 14:19
 *@author: caihj
 *@des:T发现身边实体类
 **/
data class FoundAroundBean(
    val summary: String,
    val image: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val resourceType: String,
    val resourceId: String
) {
    var currentPostion: LatLng? = null

    fun getInfo(): SpannableStringBuilder {
        val typeStr = ResourceType.getName(resourceType)
        var distance = ""
        if (currentPostion != null) {
            distance = "距您" + AddressUtil.getLocationDistanceEn(
                currentPostion!!,
                LatLng(latitude, longitude)
            )
        }
        val spb = SpannableStringBuilder()
        val sb = StringBuilder()
        val text = DividerTextUtils.convertDotString(sb, distance, typeStr)
        spb.append(text)
        return spb
    }
}