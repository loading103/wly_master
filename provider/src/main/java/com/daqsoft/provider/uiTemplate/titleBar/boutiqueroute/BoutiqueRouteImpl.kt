package com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface BoutiqueRouteImpl {
    fun create(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    fun getItemViewType(style: String): Int
}