package com.daqsoft.travelCultureModule.specialty.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemTagBinding
import com.daqsoft.mainmodule.databinding.ItemTagSpecialBinding

class SpecialTagAdapter : RecyclerViewAdapter<ItemTagSpecialBinding, String>(R.layout.item_tag_special) {

    override fun setVariable(mBinding: ItemTagSpecialBinding, position: Int, item: String) {
        mBinding.name = "${item}"
    }
}