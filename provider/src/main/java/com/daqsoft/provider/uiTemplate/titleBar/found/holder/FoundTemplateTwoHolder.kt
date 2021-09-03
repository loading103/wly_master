package com.daqsoft.provider.uiTemplate.titleBar.found.holder

import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.maps.model.LatLng
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.FoundAroundBean
import com.daqsoft.provider.databinding.ItemFoundTemplateTwoBinding
import com.daqsoft.provider.uiTemplate.operation.holder.BaseOperationViewHolder
import com.daqsoft.provider.uiTemplate.titleBar.found.FoundItemTwoAdapter
import com.daqsoft.provider.uiTemplate.titleBar.view.TitleBarFactoryView

/**
 * @Description 发现身边样式2
 * @ClassName   FoundTemplateTwoHolder
 * @Author      luoyi
 * @Time        2020/10/15 15:57
 */
class FoundTemplateTwoHolder(binding: ItemFoundTemplateTwoBinding) :
    BaseOperationViewHolder<ItemFoundTemplateTwoBinding>(binding) {
    val foundAdapter: FoundItemTwoAdapter by lazy {
        FoundItemTwoAdapter()
    }

    override fun bindDataToUI(template: CommonTemlate) {
        var titlbarView = TitleBarFactoryView(binding.root.context).apply {
            isShowMore = false
            setData(template)
            onTitileBarClickListener =
                object : TitleBarFactoryView.OnTitleBarClickListener {
                    override fun toMoreInfo(commonTemlate: CommonTemlate) {

                    }

                }
        }
        var views = binding.llFoundTemplateTwo.children
        for (v in views) {
            if (v is TitleBarFactoryView) {
                binding.llFoundTemplateTwo.removeView(v)
            }
        }

        binding.llFoundTemplateTwo.addView(
            titlbarView,
            0
        )
    }

    fun setFoundsToUI(founds: MutableList<FoundAroundBean>, currentLoction: LatLng) {
        if (!founds.isNullOrEmpty()) {
            binding.root.visibility = View.VISIBLE
            binding.rvFoundResources.run {
                layoutManager = LinearLayoutManager(
                    binding.rvFoundResources.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = foundAdapter.apply {
                    currentPostion = currentLoction
                }
            }
            foundAdapter.clear()
            foundAdapter.add(founds)
        } else {
            binding.root.visibility = View.GONE
        }
    }
}