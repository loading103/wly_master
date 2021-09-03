package com.daqsoft.provider.businessview.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.EmoticonsBean
import com.daqsoft.provider.databinding.ItemCommentEmoticonsBinding
import com.daqsoft.provider.databinding.ItemProviderCommentBinding

/**
 * @Description
 * @ClassName   CommentEmoticonsAdapter
 * @Author      luoyi
 * @Time        2020/11/2 20:08
 */
class CommentEmoticonsAdapter :
    RecyclerViewAdapter<ItemCommentEmoticonsBinding, String>(R.layout.item_comment_emoticons) {
    override fun setVariable(
        mBinding: ItemCommentEmoticonsBinding,
        position: Int,
        item: String
    ) {
        GlideModuleUtil.loadDqImage(item,mBinding.imgEmoticon)
    }
}