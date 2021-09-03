package com.daqsoft.travelCultureModule.onLineClick.bean

import com.daqsoft.travelCultureModule.country.bean.ImageUrls

/**
 * desc :
 * @author 江云仙
 * @date 2020/4/20 14:49
 */
data class OnLineClickBean(
    var title:String,
    var id:String,
    var summary:String,
    var channelName:String,
    var imageUrls:MutableList<ImageUrls>
    )