package com.daqsoft.provider.businessview.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.EmoticonsBean
import com.daqsoft.provider.databinding.ItemEmoticonsEditBinding

/**
 * @Description
 * @ClassName   EmoticonsEditBigAdapter
 * @Author      luoyi
 * @Time        2020/11/2 17:07
 */
class EmoticonsEditBigAdapter :
    RecyclerViewAdapter<ItemEmoticonsEditBinding, EmoticonsBean>(R.layout.item_emoticons_edit) {

    var onEmoticonsEditListener: OnEmoticonsEditListener? = null

    override fun setVariable(
        mBinding: ItemEmoticonsEditBinding,
        position: Int,
        item: EmoticonsBean
    ) {
        item?.let {
            GlideModuleUtil.loadDqImage(item.url, mBinding.imgEmoticon)
            mBinding.root.onNoDoubleClick {
                if (position in getData().indices) {
                    getData().removeAt(position)
                    notifyDataSetChanged()
                    onEmoticonsEditListener?.onDeleteEmoticons(position)
                    if (getData().isEmpty()) {
                        onEmoticonsEditListener?.onDeleteAllEmoticons()
                    }
                }
            }
        }
    }

    interface OnEmoticonsEditListener {
        fun onDeleteAllEmoticons()
        fun onDeleteEmoticons(position: Int)
    }
}