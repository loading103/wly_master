package com.daqsoft.servicemodule.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * desc :公交线路实体类
 * @author 江云仙
 * @date 2020/4/7 9:34
 * @version 1.0.0
 * @since JDK 1.8
 */
@Parcelize
data class BusLineBean(
    var distance: String,
    var origin: String,
    var destination: String,
    var taxi_cost: String,
    var transits: MutableList<TransitS>
) : Parcelable

@Parcelize
data class TransitS(
    var duration: String,
    var walking_distance: Long,
//                    var cost:String,
    var distance: String,
    var missed: String,
    var nightflag: String,
    var segments: MutableList<Segments>
) : Parcelable

@Parcelize
data class Segments(
    var bus: Bus,
    var walking: @RawValue Buslines
//    var walking: @RawValue Any

) : Parcelable

data class Railways(
    var spaces: MutableList<Any>
)

@Parcelize
data class Walking(
    var duration: String,
    var distance: String,
    var origin: String,
    var destination: String,
    var steps: MutableList<Steps>

) : Parcelable

@Parcelize
data class Steps(
    var distance: String
) : Parcelable

@Parcelize
data class Bus(
    var buslines: MutableList<Buslines>
) : Parcelable

@Parcelize
data class Buslines(
    var distance: String?,
    var departure_stop: ViaStops?,
    var via_stops: MutableList<ViaStops>?,
    var type : @RawValue Any?,
    var arrival_stop: ViaStops?,
    var name: String?,


    var duration: String?,
    var origin: String?,
    var destination: String?,
    var steps: MutableList<Steps>?
) : Parcelable

@Parcelize
data class ViaStops(
    var name: String,
    var location: String,
    var id: String
) : Parcelable