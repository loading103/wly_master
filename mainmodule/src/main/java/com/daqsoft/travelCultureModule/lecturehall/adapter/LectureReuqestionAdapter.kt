package com.daqsoft.travelCultureModule.lecturehall.adapter

import android.content.Context
import android.view.View
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemLectureRequestionBinding
import com.daqsoft.provider.bean.LectureRequestion

/**
 * @Description
 * @ClassName   LectureReuqestionAdapter
 * @Author      luoyi
 * @Time        2020/6/17 16:20
 */
class LectureReuqestionAdapter : RecyclerViewAdapter<ItemLectureRequestionBinding, LectureRequestion> {
    var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_lecture_requestion) {
        this.mContext = context
    }

    override fun setVariable(mBinding: ItemLectureRequestionBinding, position: Int, item: LectureRequestion) {
        item?.let {
            Glide.with(mContext!!)
                .load(it.userHead)
                .placeholder(R.mipmap.mine_profile_photo_default)
                .into(mBinding.imgHeader)
            mBinding.tvUserName.text = "" + it.userName
            mBinding.tvFeedBackContent.text = "" + it.question
            mBinding.tvFeedBackTime.text = "" + it.createTime
            if (it.reply.isNullOrEmpty()) {
                mBinding.vFeedBackReply.visibility = View.GONE
            } else {
                mBinding.vFeedBackReply.visibility = View.VISIBLE
                mBinding.tvReplyContent.text = "" + it.reply
                mBinding.tvReplyTime.text = "" + it.replyTime
            }

        }
    }
}