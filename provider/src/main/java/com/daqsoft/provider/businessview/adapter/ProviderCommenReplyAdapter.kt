package com.daqsoft.provider.businessview.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.R
import com.daqsoft.provider.databinding.ItemProviderReplyBinding
import com.daqsoft.provider.network.comment.beans.ReplyBean

class ProviderCommenReplyAdapter(context: Context, showicon:Boolean) : RecyclerViewAdapter<ItemProviderReplyBinding, ReplyBean>(R.layout.item_provider_reply) {
    val mContext = context
    override fun setVariable(
        mBinding: ItemProviderReplyBinding,
        position: Int,
        item: ReplyBean
    ) {

        mBinding.name = if (!item.name.isNullOrEmpty()) {
            item.name
        } else {
            ""
        }

        mBinding.time = item.createTime
        val replyContent = item.getReplyContent();
        mBinding.content = replyContent

        Glide.with(mContext).load("" + item.head)
            .placeholder(R.mipmap.mine_profile_photo_default)
            .into(mBinding.aivImage)
        // 评论表情
        if (item.emoticonsUrl.isNullOrEmpty()) {
            mBinding.vCommentEmoticons.visibility = View.GONE
        } else {
            var emoticonsCommentAdapter: CommentEmoticonsAdapter = CommentEmoticonsAdapter().apply {
                emptyViewShow = false
            }
            mBinding.vCommentEmoticons.visibility = View.VISIBLE
            mBinding.rvEmoticons.adapter = emoticonsCommentAdapter
            mBinding.rvEmoticons.layoutManager =
                GridLayoutManager(mContext!!, 5, GridLayoutManager.VERTICAL, false)
            emoticonsCommentAdapter.clear()
            emoticonsCommentAdapter.add(item.emoticonsUrl!!)
        }

        mBinding?.root?.setOnClickListener {
            onLisitener?.onItemClick(item)
        }
    }



    interface OnItemLisitener {
        fun  onItemClick( item: ReplyBean)
    }

    var onLisitener: OnItemLisitener? = null
}