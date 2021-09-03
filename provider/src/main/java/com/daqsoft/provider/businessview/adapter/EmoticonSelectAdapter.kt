package com.daqsoft.provider.businessview.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.EmoticonsBean
import com.daqsoft.provider.databinding.ItemEmoticonsSelectBinding
import com.daqsoft.provider.getRealImages

/**
 * @Description
 * @ClassName   EmotcionSelectAdapter
 * @Author      luoyi
 * @Time        2020/11/2 11:41
 */
class EmoticonSelectAdapter :
    RecyclerViewAdapter<ItemEmoticonsSelectBinding, EmoticonsBean>(R.layout.item_emoticons_select) {

    var onEmoticonSelectListener: OnEmoticonSelectListener? = null
    override fun setVariable(
        mBinding: ItemEmoticonsSelectBinding,
        position: Int,
        item: EmoticonsBean
    ) {
        item?.let {
            GlideModuleUtil.loadDqImage(item.url ?: "".getRealImages(), mBinding.imgEmoticon)
            mBinding.root.onNoDoubleClick {
                if (position in getData().indices) {
                    getData().removeAt(position)
                    notifyDataSetChanged()
                    if (getData().isEmpty()) {
                        onEmoticonSelectListener?.ondeleteAllData()
                    }
                }

            }
        }
    }

    interface OnEmoticonSelectListener {
        fun ondeleteAllData()
    }
}