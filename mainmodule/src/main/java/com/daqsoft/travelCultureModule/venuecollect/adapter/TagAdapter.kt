package com.daqsoft.travelCultureModule.venuecollect.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemTagExhibitBinding

class TagAdapter : RecyclerViewAdapter<ItemTagExhibitBinding, String>(R.layout.item_tag_exhibit) {

    override fun setVariable(mBinding: ItemTagExhibitBinding, position: Int, item: String) {
        mBinding.name = "${item}"
    }
}