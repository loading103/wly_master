package com.daqsoft.travelCultureModule.themepark.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.databinding.ItemThemePlayListBinding
import com.daqsoft.provider.bean.ThemeAreaListBean

class ThemePlayListAdapter : RecyclerViewAdapter<ItemThemePlayListBinding, ThemeAreaListBean>(com.daqsoft.mainmodule.R.layout.item_theme_play_list) {


    override fun setVariable(mBinding: ItemThemePlayListBinding, position: Int, item: ThemeAreaListBean) {

        mBinding.ratingbar.setStar(3f)
    }

}
