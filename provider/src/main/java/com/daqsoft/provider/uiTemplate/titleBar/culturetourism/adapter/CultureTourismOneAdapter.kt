package com.daqsoft.provider.uiTemplate.titleBar.culturetourism.adapter

import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.HomeBranchBean
import com.daqsoft.provider.databinding.ItemCultureTourismStyleOneAdapterBinding
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.view.extend.onModuleNoDoubleClick

/**
 * 文旅品牌适配器样式一
 */
class CultureTourismOneAdapter :
    RecyclerViewAdapter<ItemCultureTourismStyleOneAdapterBinding, HomeBranchBean>(R.layout.item_culture_tourism_style_one_adapter) {
    override fun setVariable(mBinding: ItemCultureTourismStyleOneAdapterBinding, position: Int, item: HomeBranchBean) {
        if (position == 0) {
            mBinding.rlRoot.setPadding(
                mBinding.root.context.resources.getDimensionPixelSize(R.dimen.dp_20),
                0,
                mBinding.root.context.resources.getDimensionPixelSize(R.dimen.dp_12),
                0
            )
        } else if (position == itemCount - 1) {
            mBinding.rlRoot.setPadding(0, 0, mBinding.root.context.resources.getDimensionPixelSize(R.dimen.dp_20), 0)
        } else {
            mBinding.rlRoot.setPadding(0, 0, mBinding.root.context.resources.getDimensionPixelSize(R.dimen.dp_12), 0)
        }
        Glide.with(mBinding.root.context)
            .load(item.brandImage)
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .error(R.mipmap.placeholder_img_fail_240_180)
            .into(mBinding.image)
        mBinding.tvBrandName.text = item.name
        mBinding.tvBrandInfo.text = item.slogan
        if (!item.relationResourceNameStr.isNullOrEmpty()) {
            val strs = item.relationResourceNameStr.split(",")
            if (strs.size == 1) {
                mBinding.tvRelation.text = strs[0]
            } else {
                mBinding.tvRelation.text = strs[0]
                mBinding.tvRelation1.text = strs[1]
            }
        } else {
            mBinding.llRelation.visibility = View.GONE
        }
        mBinding.root.onModuleNoDoubleClick(
//            mBinding.root.context.  resources.getString(R.string.home_branch_name),
            "印象天府",
            ProviderApi.REGION_MAIN_COLUMNS
        ) {
            ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_DETAIL)
                .withString("id", item.id)
                .navigation()
        }
    }
}