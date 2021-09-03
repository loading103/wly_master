package com.daqsoft.travelCultureModule.country.adapter

import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemCountryHapinessBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.travelCultureModule.country.bean.AgritainmentBean

/**
 * desc :农家乐适配器
 * @author 江云仙
 * @date 2020/4/13 16:00
 */
class CountryHappinessAdapter (): RecyclerViewAdapter<ItemCountryHapinessBinding, AgritainmentBean>(R.layout.item_country_hapiness) {
    override fun setVariable(mBinding: ItemCountryHapinessBinding, position: Int, item: AgritainmentBean) {
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
        var tags:MutableList<String> = mutableListOf()
        if (!item.type.isNullOrEmpty()) {
            for (i in item.type.split(",")) {
                tags.add(i)
            }
            mBinding.recyCountryHapLabels.setLabels(tags)
            mBinding.recyCountryHapLabels.visibility = View.VISIBLE
        } else {
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