package com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.holder

import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.LineTagBean
import com.daqsoft.provider.databinding.ItemBoutiqueRouteStyleBinding
import com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.adapter.LineStyleTwoAdapter
import com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.adapter.LineTypeAdapter
import com.daqsoft.provider.uiTemplate.titleBar.view.TitleBarFactoryView
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean

/**
 * 精品路线view holder2
 */
class BoutiqueRouteViewHolderTwo(binding: ItemBoutiqueRouteStyleBinding) : BaseBoutiqueRouteViewHolder<ItemBoutiqueRouteStyleBinding>(binding) {

    val lineTypeAdapter by lazy { LineTypeAdapter() }

    private val lineStyleTwoAdapter by lazy { LineStyleTwoAdapter() }

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
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = lineStyleTwoAdapter
            lineStyleTwoAdapter.clear()
            lineStyleTwoAdapter.add(dataLine)
            lineStyleTwoAdapter.notifyDataSetChanged()
        }
    }
}