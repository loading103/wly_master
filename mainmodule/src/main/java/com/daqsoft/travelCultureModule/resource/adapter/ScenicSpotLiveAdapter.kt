package com.daqsoft.travelCultureModule.resource.adapter

import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemSpotLiveBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.SpotsLiveBean
import com.daqsoft.travelCultureModule.resource.bean.LiveListBean

/**
 * @Description
 * @ClassName   ScenicSpotLiveAdapter
 * @Author      luoyi
 * @Time        2020/4/26 10:09
 */
class ScenicSpotLiveAdapter : RecyclerViewAdapter<ItemSpotLiveBinding, LiveListBean>(R.layout.item_spot_live) {

    override fun setVariable(mBinding: ItemSpotLiveBinding, position: Int, item: LiveListBean) {
        mBinding.name = "${item.scenicSpotsName}"

        mBinding.root.onNoDoubleClick {
            mBinding.root.setOnClickListener {
                ARouter.getInstance()
                    .build(ARouterPath.SlowLiveModule.SLOW_LIVE_DETAIL_ACTIVITY)
                    .withInt("scenicSpotsId", item.scenicSpotsId)
                    .withString("scenicSpotsName", item.scenicSpotsName)
                    .navigation()
            }
        }
    }
}