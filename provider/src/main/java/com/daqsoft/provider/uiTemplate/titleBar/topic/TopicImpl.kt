package com.daqsoft.provider.uiTemplate.titleBar.topic

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface TopicImpl {
    fun create(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    fun getItemViewType(style: String): Int
}