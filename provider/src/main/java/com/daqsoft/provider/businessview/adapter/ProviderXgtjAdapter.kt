package com.daqsoft.provider.businessview.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.arouter.utils.TextUtils
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ExhibitionTagBean
import com.daqsoft.provider.bean.MapResBean
import com.daqsoft.provider.bean.VenueCollectBean
import com.daqsoft.provider.databinding.ItemProviderRecommendRecBinding
import com.daqsoft.provider.databinding.ItemShowListBinding
import com.daqsoft.provider.databinding.ItemXgListBinding
import com.daqsoft.provider.utils.AddressUtil

/**
 * 适配器
 */
class ProviderXgtjAdapter(private val context: Context): RecyclerViewAdapter<ItemXgListBinding, VenueCollectBean>(R.layout.item_xg_list) {

    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemXgListBinding, position: Int, item: VenueCollectBean) {
        mBinding.url=item.getRealImages()
        if(!TextUtils.isEmpty(item.typeName)){
            mBinding.tvTag.text=item.typeName
        }
        if(!TextUtils.isEmpty(item.name)){
            mBinding.tvTitle.text=item.name
        }
        mBinding.root.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.COLLECT_SHOW_DETAIL)
                .withString("id",item.id)
                .navigation()
        }
    }
}