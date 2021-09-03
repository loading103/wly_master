package com.daqsoft.provider.businessview.adapter

import android.annotation.SuppressLint
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.arouter.utils.TextUtils
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CultureListBean
import com.daqsoft.provider.databinding.ItemCultureList1Binding

/**
 * 适配器
 */
class ProviderRecommCultureAdapter(): RecyclerViewAdapter<ItemCultureList1Binding, CultureListBean>(R.layout.item_culture_list1) {

    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemCultureList1Binding, position: Int, item: CultureListBean) {
        mBinding.url=item.getRealImages()
        mBinding.threeDimensionalUrl=item.threeDimensionalUrl
        if(!TextUtils.isEmpty(item.typeName)){
            mBinding.tvTag.text=item.typeName
        }
        if(!TextUtils.isEmpty(item.name)){
            mBinding.tvTitle.text=item.name
        }
        mBinding.root.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.COLLECT_CULYURE_DETAIL)
                .withString("id",item.id)
                .navigation()
        }
    }
}