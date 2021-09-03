package com.daqsoft.servicemodule.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * desc :火车列表适配器
 * @author 江云仙
 * @date 2020/4/8 19:05
 */
@Parcelize
data class TrainListBean(
    var arrStationName: String,
    var arrTime: String,
    var deptStationName: String,
    var arrStationCode: String,
    var deptStationCode: String,
    var deptTime: String,
    var runTime: String,
    var trainCode: String,
    var minPrice: String,
    var seatList: MutableList<SeatList>
) : Parcelable

@Parcelize
data class SeatList(
    var seatName: String,
    var seatNum: String,
    var seatPrice: String
) : Parcelable