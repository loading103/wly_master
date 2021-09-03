package com.daqsoft.travelCultureModule.themepark.adapter

import android.graphics.Color
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.databinding.ItemThemeAreaDetailListBinding
import com.daqsoft.mainmodule.databinding.ItemThemeAreaListBinding
import com.daqsoft.provider.bean.ThemeAreaListBean

class ThemeAreaDetailAdapter : RecyclerViewAdapter<ItemThemeAreaDetailListBinding, ThemeAreaListBean>(com.daqsoft.mainmodule.R.layout.item_theme_area_detail_list) {


    override fun setVariable(mBinding: ItemThemeAreaDetailListBinding, position: Int, item: ThemeAreaListBean) {
    }

}
