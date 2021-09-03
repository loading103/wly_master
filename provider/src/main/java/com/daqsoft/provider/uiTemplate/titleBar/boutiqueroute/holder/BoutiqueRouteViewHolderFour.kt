package com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.holder

import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.LineTagBean
import com.daqsoft.provider.databinding.ItemBoutiqueRouteStyleBinding
import com.daqsoft.provider.databinding.ItemBoutiqueRouteStyleFourBinding
import com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.adapter.LineStyleOneAdapter
import com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.adapter.LineTypeAdapter
import com.daqsoft.provider.uiTemplate.titleBar.view.TitleBarFactoryView
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean

/**
 * @Description
 * @ClassName   BoutiqueRouteViewHolderFour
 * @Author      luoyi
 * @Time        2020/12/9 11:40
 */
class BoutiqueRouteViewHolderFour(binding: ItemBoutiqueRouteStyleFourBinding) :
    BaseBoutiqueRouteViewHolder<ItemBoutiqueRouteStyleFourBinding>(binding) {

    val lineTypeAdapter by lazy { LineTypeAdapter() }


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
        binding.datas = dataLine
        binding.rlRoot.onNoDoubleClick {
            if (!dataLine.isNullOrEmpty()) {
                ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                    .withString("id", dataLine[0].id.toString())
                    .withString("contentTitle", "资讯详情")
                    .navigation()
            }

        }
    }
}