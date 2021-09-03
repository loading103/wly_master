package com.daqsoft.provider.uiTemplate.titleBar.culturetourism

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface CultureTourismImpl {
    fun create(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    fun getItemViewType(style: String): Int
}