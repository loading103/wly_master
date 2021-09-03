package com.daqsoft.provider.uiTemplate.operation

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.provider.bean.CommonTemlate

interface OperationTemplateImpl {

    fun create(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    fun getItemType(commonTemlate: CommonTemlate):Int
}