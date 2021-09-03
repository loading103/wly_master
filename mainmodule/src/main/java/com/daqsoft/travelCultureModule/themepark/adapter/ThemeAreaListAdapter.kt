package com.daqsoft.travelCultureModule.themepark.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.databinding.ItemThemeAreaListBinding
import com.daqsoft.provider.bean.ThemeAreaListBean

class ThemeAreaListAdapter : RecyclerViewAdapter<ItemThemeAreaListBinding, ThemeAreaListBean>(com.daqsoft.mainmodule.R.layout.item_theme_area_list) {


    override fun setVariable(mBinding: ItemThemeAreaListBinding, position: Int, item: ThemeAreaListBean) {

    }

}
