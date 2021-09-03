package com.daqsoft.provider.uiTemplate.titleBar.found.holder

import androidx.core.view.children
import com.amap.api.maps.model.LatLng
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.FoundAroundBean
import com.daqsoft.provider.databinding.ItemFoundTemplateOneBinding
import com.daqsoft.provider.databinding.ItemFoundTemplateThreeBinding
import com.daqsoft.provider.uiTemplate.operation.holder.BaseOperationViewHolder
import com.daqsoft.provider.uiTemplate.titleBar.found.FoundItemThreeAdapter
import com.daqsoft.provider.uiTemplate.titleBar.view.TitleBarFactoryView

/**
 * @Description
 * @ClassName   FoundTemplateThreeHolder
 * @Author      luoyi
 * @Time        2020/12/2 16:23
 */
class FoundTemplateThreeHolder(binding: ItemFoundTemplateThreeBinding) :
    BaseOperationViewHolder<ItemFoundTemplateThreeBinding>(binding) {

    val foundAdapter: FoundItemThreeAdapter by lazy {
        FoundItemThreeAdapter().apply {
            emptyViewShow = false
        }
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
        var views = binding.llFoundTemplateThree.children
        for (v in views) {
            if (v is TitleBarFactoryView) {
                binding.llFoundTemplateThree.removeView(v)
            }
        }

        binding.llFoundTemplateThree.addView(
            titlbarView,
            0
        )
        binding.recyFounds.run {
            adapter = foundAdapter
        }
    }

    fun setFoundsToUI(founds: MutableList<FoundAroundBean>, currentLoction: LatLng) {
        foundAdapter.currentPostion = currentLoction
        foundAdapter.add(founds)
    }
}