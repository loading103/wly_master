package com.daqsoft.provider.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.PlayChooseType
import com.daqsoft.provider.databinding.ItemPopupPlayChooseBinding

class PlayTypeChooseAdapter : RecyclerViewAdapter<ItemPopupPlayChooseBinding, PlayChooseType>(R.layout.item_popup_play_choose) {

    private var bean : PlayChooseType?=null

    override fun setVariable(mBinding: ItemPopupPlayChooseBinding, position: Int, item: PlayChooseType) {

        mBinding.tvTag.text=item.name

        mBinding.tvTag.isSelected = !bean?.id.isNullOrBlank() && bean?.id == item.id

        mBinding.root.setOnClickListener {
            itemOnClickListener?.onClick(position, item)
            bean = item
            notifyDataSetChanged()
        }
    }
    /**
     * 设置选中位置
     */
    fun setSelectedPosition(bean: PlayChooseType?){
        this.bean = bean
        notifyDataSetChanged()
    }

    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int,bean : PlayChooseType)
    }
}
