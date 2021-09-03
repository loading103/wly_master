package com.daqsoft.provider.businessview.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.R
import com.daqsoft.provider.databinding.ItemReplyNewBinding
import com.daqsoft.provider.network.comment.beans.ReplyBean

class ProviderReplyAdapter(context: Context) : RecyclerViewAdapter<ItemReplyNewBinding, ReplyBean>(R.layout.item_reply_new) {
    val mContext = context
    var totleSize:Int=3
    override fun setVariable(
        binding: ItemReplyNewBinding,
        position: Int,
        item: ReplyBean) {
        if(position==3){
            binding?.tvName?.text="查看${totleSize}条回复"
            val drawableLeft: Drawable = mContext.resources.getDrawable(R.mipmap.activity_details_right)
            binding?.tvName?.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableLeft, null)
            binding?.tvName?.compoundDrawablePadding = 15
            binding?.root?.setOnClickListener {
                onLisitener?.onLastItemClick(true, item)
            }
        }else {
            binding?.tvName?.text = item.getContent()
            // 评论表情
            if (item.emoticonsUrl.isNullOrEmpty()) {
                binding?.rvEmoticons?.visibility = View.GONE
            } else {
                var emoticonsCommentAdapter: CommentEmoticonsAdapter = CommentEmoticonsAdapter().apply { emptyViewShow = false }
                binding?.rvEmoticons?.visibility = View.VISIBLE
                binding?.rvEmoticons?.adapter = emoticonsCommentAdapter
                binding?.rvEmoticons?.layoutManager = GridLayoutManager(mContext!!, 5, GridLayoutManager.VERTICAL, false)
                emoticonsCommentAdapter.clear()
                emoticonsCommentAdapter.add(item.emoticonsUrl!!)
            }
            binding?.root?.setOnClickListener {
                onLisitener?.onLastItemClick(false,item)
            }
        }
    }


    interface OnLastItemLisitener {
        fun  onLastItemClick(boolean: Boolean, item: ReplyBean)
    }

    var onLisitener: OnLastItemLisitener? = null
}