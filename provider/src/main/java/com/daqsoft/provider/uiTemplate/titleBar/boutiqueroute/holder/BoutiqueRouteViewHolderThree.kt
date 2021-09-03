package com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.holder

import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.extend.dp
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.LineTagBean
import com.daqsoft.provider.databinding.ItemBoutiqueRouteStyleBinding
import com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.adapter.LineStyleThreeAdapter
import com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.adapter.LineTypeAdapter
import com.daqsoft.provider.uiTemplate.titleBar.view.TitleBarFactoryView
import com.daqsoft.provider.view.GridDecoration
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean

/**
 * 精品线路view holder3
 */
class BoutiqueRouteViewHolderThree(binding: ItemBoutiqueRouteStyleBinding) : BaseBoutiqueRouteViewHolder<ItemBoutiqueRouteStyleBinding>(binding) {

    val lineTypeAdapter by lazy { LineTypeAdapter() }

    private val lineStyleThreeAdapter by lazy { LineStyleThreeAdapter() }

    override fun bindDataToUI(commonTemplate: CommonTemlate, dataLineType: MutableList<LineTagBean>, dataLine: MutableList<HomeContentBean>) {
        val titleBarView = TitleBarFactoryView(binding.root.context).apply {
            setData(commonTemplate)
            onTitileBarClickListener =
                object : TitleBarFactoryView.OnTitleBarClickListener {
                    override fun toMoreInfo(commonTemlate: CommonTemlate) {
                        ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT)
                            .withString("channelCode", "lineChannel")
                            .navigation()
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

        binding.recycleLineType.run {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = lineTypeAdapter
            lineTypeAdapter.clear()
            lineTypeAdapter.add(dataLineType)
            lineTypeAdapter.notifyDataSetChanged()
        }

        binding.recyclerHomeLine.run {
            layoutManager = GridLayoutManager(context, 2)
            adapter = lineStyleThreeAdapter
            lineStyleThreeAdapter.clear()
            lineStyleThreeAdapter.add(dataLine)
            lineStyleThreeAdapter.notifyDataSetChanged()
        }
    }
}