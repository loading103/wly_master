package com.daqsoft.travelCultureModule.sidetour.bean

/**加油站详情*/
data class GasStationBean(
    val address: String?,
    val disnum: Int,
    val id: Int,
    val images: String?,
    val latitude: Double,
    val longitude: Double,
    val name: String?,
    val oil0: String?,
    val oil92: String?,
    val oil95: String?,
    val oil98: String?,
    val openTime: String?,
    val phone: String?
)