package com.daqsoft.provider.businessview.adapter

import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.EmoticonsBean
import com.daqsoft.provider.databinding.ItemEmoticonsListBinding
import com.daqsoft.provider.getRealImageUrl

/**
 * @Description
 * @ClassName   EmoticonsAllAdapter
 * @Author      luoyi
 * @Time        2020/11/2 11:32
 */
class EmoticonsAllAdapter :
    RecyclerViewAdapter<ItemEmoticonsListBinding, EmoticonsBean>(R.layout.item_emoticons_list) {
    var onEmotionItemListener: OnEmotionItemListener? = null

    override fun setVariable(
        mBinding: ItemEmoticonsListBinding,
        position: Int,
        item: EmoticonsBean
    ) {
        item?.let {
            GlideModuleUtil.loadDqImage(item.url ?: "".getRealImageUrl(), mBinding.imgEmoticon)
            mBinding.tvEmoticonName.text = "${item.name}"
            mBinding.root.onNoDoubleClick {
                onEmotionItemListener?.onItemClick(item)
            }
        }
    }

    interface OnEmotionItemListener {
        fun onItemClick(bean: EmoticonsBean)
    }
}