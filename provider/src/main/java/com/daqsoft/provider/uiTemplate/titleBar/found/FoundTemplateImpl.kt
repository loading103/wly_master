package com.daqsoft.provider.uiTemplate.titleBar.found

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface FoundTemplateImpl {

    fun getItemViewType(style:String):Int

    fun create(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
}