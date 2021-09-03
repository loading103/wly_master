package com.daqsoft.provider.commentModule

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.R
import com.daqsoft.provider.base.LocalResourceType
import com.daqsoft.provider.businessview.adapter.CommentEmoticonsAdapter
import com.daqsoft.provider.databinding.ItemCommentBinding
import com.daqsoft.provider.getRealImageUrl
import com.daqsoft.provider.network.comment.beans.CommentBean
import java.lang.Exception
import java.lang.StringBuilder

/**
 * @Description 评论适配器
 * @ClassName   CommentAdapter
 * @Author      PuHua
 * @Time        2019/12/27 14:48
 */
class CommentAdapter(context: Context) :
    RecyclerViewAdapter<ItemCommentBinding, CommentBean>(
        R.layout.item_comment
    ) {
    var isShowAll: Boolean = true
    val mContext = context
    override fun setVariable(mBinding: ItemCommentBinding, position: Int, item: CommentBean) {
        mBinding.name = item.vipNickName
        mBinding.time = item.commentTime
        mBinding.content = item.content
        mBinding.url = item.vipHead
        if (item.vipNickName.isNullOrEmpty()) {
            mBinding.name = "游客"
        }
        // 加载回复列表
        if(item.commentReplyPageData!=null && !item.commentReplyPageData.rows.isNullOrEmpty()){
            mBinding.cvReply.visibility=View.VISIBLE
            mBinding.cvReply.setData(item.commentReplyPageData.rows,item)
        }else{
            mBinding.cvReply.visibility=View.GONE
        }

        Glide.with(mBinding.aivImage)
            .load(item.vipHead.getRealImageUrl())
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

        // 评论图片
        val imageAdapter = ImageVideoReadAdapter(mContext)
        val resources = mutableListOf<ResourceBean>()
        for (s in item.video) {
            resources.add(ResourceBean(s, LocalResourceType.VIDEO))
        }
        for (s in item.image) {
            resources.add(ResourceBean(s, LocalResourceType.IMAGE))
        }
        imageAdapter.add(resources)
        if (resources.size == 0) {
            mBinding.rvImages.visibility = View.GONE
        }
        val gridLayoutManager = GridLayoutManager(mContext, 3)
        mBinding.rvImages.layoutManager = gridLayoutManager
        mBinding.rvImages.adapter = imageAdapter

        // 标签
        val tag = StringBuilder()
        for (i in item.commentTag.indices) {
            tag.append(item.commentTag[i]).append("·")
        }
        if (!tag.isNullOrEmpty()) {
            tag.deleteCharAt(tag.length - 1)
            mBinding.tvTag.visibility = View.VISIBLE
        } else {
            mBinding.tvTag.visibility = View.GONE
        }
        mBinding.tvTag.text = tag
        try {
            if (item.star > 0) {
                mBinding.rbarActivity.max = 5
                mBinding.rbarActivity.rating = item.star.toFloat()
                mBinding.rbarActivity.visibility = View.VISIBLE
                mBinding.vRatingActivity.visibility = View.VISIBLE
            } else {
                mBinding.rbarActivity.visibility = View.GONE
                mBinding.vRatingActivity.visibility = View.GONE
            }
        } catch (e: Exception) {
        }

    }

    override fun getItemCount(): Int {
        if (!isShowAll) {
            if (getData().size > 3) {
                return 3
            }
        }
        return super.getItemCount()
    }

}