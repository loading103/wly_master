package com.daqsoft.provider.uiTemplate.titleBar.culturetourism.holder

import android.view.View
import androidx.core.view.children
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.HomeBranchBean
import com.daqsoft.provider.databinding.ItemCultureTourismStyleBinding
import com.daqsoft.provider.uiTemplate.titleBar.culturetourism.adapter.CultureTourismTwoAdapter
import com.daqsoft.provider.uiTemplate.titleBar.culturetourism.adapter.HomeBrandAdapter
import com.daqsoft.provider.uiTemplate.titleBar.view.TitleBarFactoryView
import java.util.*

/**
 * 文旅品牌view holder2
 */
class CultureTourismViewHolderTwo(binding: ItemCultureTourismStyleBinding) : BaseCultureTourismViewHolder<ItemCultureTourismStyleBinding>(binding) {
    override fun bindDataToUI(commonTemplate: CommonTemlate, dataLineType: MutableList<HomeBranchBean>) {
        val titleBarView = TitleBarFactoryView(binding.root.context).apply {
            setData(commonTemplate)
            onTitileBarClickListener =
                object : TitleBarFactoryView.OnTitleBarClickListener {
                    override fun toMoreInfo(commonTemlate: CommonTemlate) {
                        ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_LIST).navigation()
                    }
                }
        }

        val views = binding.rlRoot.children
        for (v in views) {
            if (v is TitleBarFactoryView) {
                binding.rlRoot.removeView(v)
            }
        }

        binding.rlRoot.addView(
            titleBarView,
            0
        )

        binding.recycleTourismType.visibility = View.GONE
        binding.rlTourismType2.visibility = View.VISIBLE
        binding.rlTourismType3.visibility = View.GONE

        binding.pagerHomeBrand.run {
            offscreenPageLimit = 4
            setPageTransformer(false, CultureTourismTwoAdapter(this))
            adapter = HomeBrandAdapter(context, dataLineType as ArrayList<HomeBranchBean>?)
        }
    }
}
