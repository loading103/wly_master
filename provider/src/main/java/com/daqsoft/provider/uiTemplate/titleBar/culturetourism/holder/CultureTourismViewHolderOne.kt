package com.daqsoft.provider.uiTemplate.titleBar.culturetourism.holder

import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.HomeBranchBean
import com.daqsoft.provider.databinding.ItemCultureTourismStyleBinding
import com.daqsoft.provider.uiTemplate.titleBar.culturetourism.adapter.CultureTourismOneAdapter
import com.daqsoft.provider.uiTemplate.titleBar.view.TitleBarFactoryView

/**
 * 文旅品牌view holder1
 */
class CultureTourismViewHolderOne(binding: ItemCultureTourismStyleBinding) : BaseCultureTourismViewHolder<ItemCultureTourismStyleBinding>(binding) {

    private val cultureTourismOneAdapter by lazy { CultureTourismOneAdapter() }

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

        binding.recycleTourismType.visibility = View.VISIBLE
        binding.rlTourismType2.visibility = View.GONE
        binding.rlTourismType3.visibility = View.GONE

        binding.recycleTourismType.run {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = cultureTourismOneAdapter
            cultureTourismOneAdapter.clear()
            cultureTourismOneAdapter.add(dataLineType)
            cultureTourismOneAdapter.notifyDataSetChanged()
        }
    }
}
