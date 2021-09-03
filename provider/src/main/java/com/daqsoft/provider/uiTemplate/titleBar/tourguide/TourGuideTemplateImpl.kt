package com.daqsoft.provider.uiTemplate.titleBar.tourguide

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface TourGuideTemplateImpl {

    fun getItemViewType(style:String):Int

    fun create(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
}