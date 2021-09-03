package com.daqsoft.provider.businessview.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.arouter.utils.TextUtils
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.ImageLoadUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.CultureListBean
import com.daqsoft.provider.bean.ExhibitionTagBean
import com.daqsoft.provider.bean.MapResBean
import com.daqsoft.provider.databinding.ItemProviderRecommendRecBinding
import com.daqsoft.provider.databinding.ItemShowListBinding
import com.daqsoft.provider.utils.AddressUtil

/**
 * @Description 所有展品 适配器
 */
class ProviderShowAdapter(private val context: Context): RecyclerViewAdapter<ItemShowListBinding, CultureListBean>(R.layout.item_show_list) {

    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemShowListBinding, position: Int, item: CultureListBean) {

        mBinding.datas=item

        if(!TextUtils.isEmpty(item.typeName)){
            mBinding.tvTag.text=item.typeName
        }
        if(!TextUtils.isEmpty(item.name)){
            mBinding.tvTitle.text=item.name
        }

        ImageLoadUtil.glideStageManage(item.getRealImages(),mBinding.ivContent)

        mBinding.root.onNoDoubleClick {

            ARouter.getInstance()
                .build(MainARouterPath.COLLECT_CULYURE_DETAIL)
                .withString("id",item.id)
                .navigation()
        }
    }
}