package com.daqsoft.usermodule.ui.activity.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.provider.base.LocalResourceType
import com.daqsoft.provider.bean.FeedBackBean
import com.daqsoft.provider.commentModule.ImageVideoReadAdapter
import com.daqsoft.provider.commentModule.ResourceBean
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ItemFeedBackBinding

/**
 * @Description
 * @ClassName   FeedBackAdapter
 * @Author      luoyi
 * @Time        2020/5/21 19:42
 */
class FeedBackAdapter : RecyclerViewAdapter<ItemFeedBackBinding, FeedBackBean> {

    private var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_feed_back) {
        this.mContext = context
    }

    override fun setVariable(mBinding: ItemFeedBackBinding, position: Int, item: FeedBackBean) {
        Glide.with(mContext!!)
            .load(item.headUrl)
            .placeholder(R.drawable.mine_profile_photo_default)
            .into(mBinding.imgHeader)

        mBinding.tvUserName.text = "${item.nickName}"
        mBinding.tvFeedBackTime.text = "${item.createTime}"

        if(BaseApplication.appArea=="xj"){
            mBinding.tvFeedBackLbValue.visibility=View.GONE
            mBinding.tvFeedBackLb.visibility=View.GONE
        }else{
            mBinding.tvFeedBackLbValue.visibility=View.VISIBLE
            mBinding.tvFeedBackLb.visibility=View.VISIBLE
            mBinding.tvFeedBackLbValue.text = "" + getTypes(item.type)
        }

        mBinding.tvFeedBackContent.text = item.content

        // 评论图片
        val imageAdapter = ImageVideoReadAdapter(mContext!!)
        imageAdapter.emptyViewShow = false
        val resources = mutableListOf<ResourceBean>()
        if (!item.video.isNullOrEmpty()) {
            resources.add(ResourceBean(item.video, LocalResourceType.VIDEO))
        }
        if (!item.image.isNullOrEmpty()) {
            var images = item.image.split(",")
            if (!images.isNullOrEmpty()) {
                for (img in images) {
                    if (!img.isNullOrEmpty()) {
                        resources.add(ResourceBean(img, LocalResourceType.IMAGE))
                    }
                }
            }

        }

        if (!resources.isNullOrEmpty()) {
            imageAdapter.add(resources)
            mBinding.recyFeedBackImages.isNestedScrollingEnabled = false
            val gridLayoutManager = GridLayoutManager(mContext, 3)
            mBinding.recyFeedBackImages.layoutManager = gridLayoutManager
            mBinding.recyFeedBackImages.adapter = imageAdapter
            mBinding.recyFeedBackImages.visibility = View.VISIBLE
        } else {
            mBinding.recyFeedBackImages.visibility = View.GONE
        }

        if (!item.replyContent.isNullOrEmpty()) {
            mBinding.vFeedBackReply.visibility = View.VISIBLE
            mBinding.tvReplyContent.text = item.replyContent
            mBinding.tvReplyTime.text = item.replyTime
        } else {
            mBinding.vFeedBackReply.visibility = View.GONE
        }
    }

    /**
     *    @param type 类型
     */
    private fun getTypes(type: String): CharSequence? {
        return when (type) {
            "EXPERIENCE" -> {
                "体验问题"
            }
            "FUNCTION" -> {
                "功能建议"
            }
            "SERVICE" -> {
                "服务建议"
            }
            "OTHER" -> {
                "其它"
            }
            else -> {
                ""
            }
        }
    }
}