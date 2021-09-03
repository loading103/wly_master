package com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.LineTagBean
import com.daqsoft.provider.databinding.ItemBoutiqueRouteStyleTypeBinding

/**
 * 精品路线顶部type适配器
 */
class LineTypeAdapter : RecyclerViewAdapter<ItemBoutiqueRouteStyleTypeBinding, LineTagBean>(R.layout.item_boutique_route_style_type) {
    override fun setVariable(mBinding: ItemBoutiqueRouteStyleTypeBinding, position: Int, item: LineTagBean) {
        mBinding.label.text = item.tagName
        mBinding.label.isSelected = item.select
        mBinding.root.onNoDoubleClick {
            for (data in getData()) {
                data.select = false
            }
            item.select = true
            changeTypeInterface?.changeType(item.tagId)
            notifyDataSetChanged()
        }
    }

    private var changeTypeInterface: ChangeType? = null

    fun setChangeType(changeTypeInterface: ChangeType) {
        this.changeTypeInterface = changeTypeInterface
    }

    interface ChangeType {
        fun changeType(type: String)
    }
}