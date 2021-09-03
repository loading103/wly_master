package com.daqsoft.travelCultureModule.country.adapter

import android.content.Context
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.CountryAllBinding
import com.daqsoft.mainmodule.databinding.ItemCountryAllBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.CountryListBean

/**
 * 全部乡村适配器
 */
class CountryAllAdapter(context: Context) :
    RecyclerViewAdapter<ItemCountryAllBinding, CountryListBean>(R.layout.item_country_all) {
    private var mContext: Context? = context
    override fun setVariable(mBinding: ItemCountryAllBinding, position: Int, item: CountryListBean) {
        mBinding.name = item.name
        mBinding.info = item.briefing
        mBinding.address = item.regionName
        if (!item.images.isNullOrEmpty()) {
            mBinding.url = item.images.split(",")[0]
        }
        if (!item.briefing.isNullOrBlank()) {
            mBinding.tvInfo.visibility = View.VISIBLE
        } else {
            mBinding.tvInfo.visibility = View.INVISIBLE
        }
        if (!item.regionName.isNullOrBlank()) {
            mBinding.tvAddress.visibility = View.VISIBLE
        } else {
            mBinding.tvAddress.visibility = View.INVISIBLE
        }
        //点击跳转到乡村详情
        mBinding.root.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_COUNTRY_DETAIL_ACTIVITY)
                .withString("id", item.id.toString())
                .navigation()
        }
    }
}