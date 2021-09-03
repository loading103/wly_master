package com.daqsoft.provider.uiTemplate.titleBar.culturetourism.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.content.res.AppCompatResources
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.adapter.setImageUrlqwx
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.HomeBranchBean
import com.daqsoft.provider.databinding.ItemCultureTourismStyleThreeAdapterBinding
import com.daqsoft.provider.utils.DividerTextUtils
/**
 * 文旅品牌适配器样式三
 */
class CultureTourismThreeAdapter :
    RecyclerViewAdapter<ItemCultureTourismStyleThreeAdapterBinding, HomeBranchBean>(R.layout.item_culture_tourism_style_three_adapter) {
    override fun setVariable(mBinding: ItemCultureTourismStyleThreeAdapterBinding, position: Int, item: HomeBranchBean) {
        mBinding.tvItemBrandNameCity.text = item.name
        mBinding.tvItemBrandIntroduceCity.text = item.slogan
        if (!item.relationResourceNameStr.isNullOrEmpty()) {
            if (item.relationResourceNameStr.contains(",")) {
                var names = item.relationResourceNameStr.split(",")
                if (names.size >= 3) {
                    names = names.subList(0, 3)
                }
                item.relationResourceNameStr = DividerTextUtils.convertDotString(names)
            }
        }
        mBinding.tvItemBrandOtherCity.text = item.relationResourceNameStr
        setImageUrlqwx(
            mBinding.ivItemBrandBackgroundCity, item.brandImage, AppCompatResources.getDrawable(
                BaseApplication.context, R.drawable.placeholder_img_fail_h300
            ), 5
        )
        mBinding.rlItemBrandCity.setOnClickListener {
            ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_DETAIL)
                .withString("id", item.id)
                .navigation()
        }
    }
}