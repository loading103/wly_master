package com.daqsoft.itinerary.bean

import android.os.Parcelable
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.venuesmodule.repository.bean.VenuesListBean
import kotlinx.android.parcel.Parcelize


/**根据选择的标签响应实体类*/
@Parcelize
data class CustomBean(
  var scenic: MutableList<ScenicBean>?,
  var venue: MutableList<VenuesListBean>?
) : Parcelable