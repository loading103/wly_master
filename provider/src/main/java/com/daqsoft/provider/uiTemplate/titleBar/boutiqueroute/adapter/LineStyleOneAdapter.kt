package com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.adapter

import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.databinding.ItemBoutiqueRouteStyleOneContentBinding
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean

/**
 * 精品路线内容适配器样式一
 */
class LineStyleOneAdapter :
    RecyclerViewAdapter<ItemBoutiqueRouteStyleOneContentBinding, HomeContentBean>(R.layout.item_boutique_route_style_one_content) {
    override fun setVariable(mBinding: ItemBoutiqueRouteStyleOneContentBinding, position: Int, item: HomeContentBean) {
        if (!item.tagName.isNullOrEmpty()) {
            mBinding.info = item.tagName[0]
            mBinding.tvInfo.visibility = View.VISIBLE
        } else {
            mBinding.tvInfo.visibility = View.GONE
        }
        mBinding.name = item.title
        mBinding.url = item.getContentCoverImageUrl()
        mBinding.root.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                .withString("id", item.id.toString())
                .withString("contentTitle", "资讯详情")
                .navigation()
        }
    }
}