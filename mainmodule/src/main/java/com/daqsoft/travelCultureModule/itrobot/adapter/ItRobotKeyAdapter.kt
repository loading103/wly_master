package com.daqsoft.travelCultureModule.itrobot.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemKeyItRobotBinding

/**
 * @Description
 * @ClassName   ItRobotKeyAdapter
 * @Author      luoyi
 * @Time        2020/5/23 16:11
 */
class ItRobotKeyAdapter : RecyclerViewAdapter<ItemKeyItRobotBinding, String>(R.layout.item_key_it_robot) {

    var onItemCLickListener: OnItemCLickListener? = null

    override fun setVariable(mBinding: ItemKeyItRobotBinding, position: Int, item: String) {
        if (!item.isNullOrEmpty()) {
            mBinding.txtWordsKey.text = item
            mBinding.root.onNoDoubleClick {
                onItemCLickListener?.onItemClick(item)
            }
        }

    }

    interface OnItemCLickListener {
        fun onItemClick(key: String)
    }
}