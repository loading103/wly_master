package com.daqsoft.travelCultureModule.country.adapter

import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemCountryHapinessNewBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.travelCultureModule.country.bean.AgritainmentBean

/**
 * 乡村详情农家乐适配器
 */
class CountryHappinessNewAdapter (): RecyclerViewAdapter<ItemCountryHapinessNewBinding, AgritainmentBean>(R.layout.item_country_hapiness_new) {
    override fun setVariable(mBinding: ItemCountryHapinessNewBinding, position: Int, item: AgritainmentBean) {
        mBinding.tvName.text = item.name
//        mBinding.tvGoodFailPoint.text="诚信分"
        if (item.images.split(",").isNotEmpty()) {
            mBinding.url = item.images.split(",")[0]
        }
        if (!item.level.isNullOrEmpty()) {
            mBinding.tvStarCountry.visibility = View.VISIBLE
            mBinding.tvStarCountry.text = item.level
        } else {
            mBinding.tvStarCountry.visibility = View.GONE
        }
        // 处理标签
        if (!item.type.isNullOrEmpty()) {
            mBinding.recyCountryHapLabels.text =  item.type.replace(',','·')
            mBinding.recyCountryHapLabels.visibility = View.VISIBLE
        } else {
            mBinding.recyCountryHapLabels.text = ""
            mBinding.recyCountryHapLabels.visibility = View.GONE
        }
        //点击跳转到农家乐详情
        mBinding.root.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_HAPPINESS_DETAIL)
                .withString("id", item.id)
                .navigation()
        }
    }

}