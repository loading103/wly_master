package com.daqsoft.provider.uiTemplate.titleBar.culturetourism.holder

import android.util.Log
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.HomeBranchBean
import com.daqsoft.provider.databinding.ItemCultureTourismStyleBinding
import com.daqsoft.provider.uiTemplate.titleBar.culturetourism.adapter.CultureTourismThreeAdapter
import com.daqsoft.provider.uiTemplate.titleBar.view.TitleBarFactoryView
import com.google.android.material.tabs.TabLayout

/**
 * 文旅品牌view holder3
 */
class CultureTourismViewHolderThree(binding: ItemCultureTourismStyleBinding) : BaseCultureTourismViewHolder<ItemCultureTourismStyleBinding>(binding) {
    private val cultureTourismThreeAdapter by lazy { CultureTourismThreeAdapter() }
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
        binding.rlTourismType2.visibility = View.GONE
        binding.rlTourismType3.visibility = View.VISIBLE

        binding.recycleTourismType2.run {
            val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            layoutManager = linearLayoutManager
            adapter = cultureTourismThreeAdapter
            cultureTourismThreeAdapter.clear()
            cultureTourismThreeAdapter.add(dataLineType)
            cultureTourismThreeAdapter.notifyDataSetChanged()
        }
    }
}
