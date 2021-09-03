package com.daqsoft.travelCultureModule.playground.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemTagBinding

class TagAdapter : RecyclerViewAdapter<ItemTagBinding, String>(R.layout.item_tag) {

    override fun setVariable(mBinding: ItemTagBinding, position: Int, item: String) {
        mBinding.name = "${item}"
    }
}