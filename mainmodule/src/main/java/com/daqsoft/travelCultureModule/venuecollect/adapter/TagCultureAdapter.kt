package com.daqsoft.travelCultureModule.venuecollect.adapter

import android.view.View
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemTagExhibitBinding

class TagCultureAdapter : RecyclerViewAdapter<ItemTagExhibitBinding, String>(R.layout.item_tag_exhibit) {

    override fun setVariable(mBinding: ItemTagExhibitBinding, position: Int, item: String) {
        mBinding.name = "${item}"

        if(position==1){
            mBinding.txtLiveName.visibility=View.GONE
            mBinding.txtLiveName1.visibility=View.VISIBLE
        }else{
            mBinding.txtLiveName.visibility=View.VISIBLE
            mBinding.txtLiveName1.visibility=View.GONE
        }
    }
}