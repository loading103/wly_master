package com.daqsoft.venuesmodule.model

/**
 * @Description
 * @ClassName   VenueCttrOrderModel
 * @Author      luoyi
 * @Time        2020/7/10 17:28
 */
data class VenueCttrOrderModel(
    var guideLanguage: String?,
    var guideOrderTimeId: String?,
    var guideExhibitionIds: String?,
    var remark: String?,
    var existVenueRelationOrderCode: Boolean,
    var venueOrdeCode:String?,
    var venueId:String?,
    var inExhallTime:String?

)