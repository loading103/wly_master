package com.daqsoft.travelCultureModule.lecturehall.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemLectureCommentBinding
import com.daqsoft.provider.businessview.adapter.CommentEmoticonsAdapter
import com.daqsoft.provider.network.comment.beans.CommentBean

/**
 * @Description
 * @ClassName   LectureCommentAdapter
 * @Author      luoyi
 * @Time        2020/6/17 16:20
 */
class LectureCommentAdapter : RecyclerViewAdapter<ItemLectureCommentBinding, CommentBean> {
    var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_lecture_comment) {
        mContext = context
    }

    override fun setVariable(
        mBinding: ItemLectureCommentBinding,
        position: Int,
        item: CommentBean
    ) {
        item?.let {
            Glide.with(mContext!!)
                .load(it.vipHead)
                .placeholder(R.mipmap.mine_profile_photo_default)
                .into(mBinding.imgHeader)
            mBinding.tvFeedBackTime.text = "" + item.commentTime
            mBinding.tvFeedBackContent.text = "" + item.content
            mBinding.tvUserName.text = "" + item.vipNickName ?: "游客"

            // 加载回复列表
            if(item.commentReplyPageData!=null && !item.commentReplyPageData.rows.isNullOrEmpty()){
                mBinding.cvReply.visibility=View.VISIBLE
                mBinding.cvReply.setData(item.commentReplyPageData.rows,item)
            }else{
                mBinding.cvReply.visibility=View.GONE
            }

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
        }
    }
}