package com.daqsoft.venuesmodule.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.R
import com.daqsoft.provider.base.LocalResourceType
import com.daqsoft.provider.commentModule.ImageVideoReadAdapter
import com.daqsoft.provider.commentModule.ResourceBean
import com.daqsoft.provider.databinding.ItemCommentBinding
import com.daqsoft.provider.network.comment.beans.CommentBean
import java.lang.StringBuilder

/**
 * @Description 评论适配器
 * @ClassName   VenuesCommentAdapter
 * @Author      luoyi
 * @Time        2020/3/27 17:26
 */
class VenuesCommentAdapter(context: Context) :
    RecyclerViewAdapter<ItemCommentBinding, CommentBean>(
        R.layout.item_comment
    ) {
    val mContext = context
    override fun setVariable(mBinding: ItemCommentBinding, position: Int, item: CommentBean) {
        mBinding.name = if (!item.vipNickName.isNullOrEmpty()) {
            item.vipNickName
        } else {
            "游客"
        }
        mBinding.time = item.commentTime
        mBinding.content = item.content
        Glide.with(mContext).load(item.vipHead)
            .placeholder(R.mipmap.mine_profile_photo_default)
            .into(mBinding.aivImage)

        // 评论图片
        val imageAdapter = ImageVideoReadAdapter(mContext)
        imageAdapter.emptyViewShow=false
        val resources = mutableListOf<ResourceBean>()
        for (s in item.video){
            resources.add(ResourceBean(s, LocalResourceType.IMAGE))
        }
        for (s in item.image){
            resources.add(ResourceBean(s, LocalResourceType.IMAGE))
        }
        imageAdapter.add(resources)
        mBinding.rvImages.isNestedScrollingEnabled =false
        val gridLayoutManager = GridLayoutManager(mContext, 3)
        mBinding.rvImages.layoutManager = gridLayoutManager
        mBinding.rvImages.adapter = imageAdapter

        // 标签
        val tag = StringBuilder()
        for (i in item.commentTag.indices) {
            tag.append(item.commentTag[i]).append("·")
        }
        if (tag.isNotEmpty()) {
            tag.deleteCharAt(tag.length - 1)
            mBinding.tvTag.visibility = View.VISIBLE
            if (!item.level.isNullOrEmpty()) {
                try {
                    val levelStr: String = getCommentLevel(item.level) + "·"
                    var spannstring = SpannableString("${levelStr}${tag}")
                    spannstring.setSpan(ForegroundColorSpan(getCommentLevelColor(item.level)), 0, levelStr.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    mBinding.tvTag.text = spannstring
                } catch (e: Exception) {
                }

            } else {
                mBinding.tvTag.text = tag
            }
        } else {
            if (!item.level.isNullOrEmpty()) {
                try {
                    val levelStr: String? = getCommentLevel(item.level)
                    var spannstring = SpannableString(levelStr!!)
                    spannstring.setSpan(ForegroundColorSpan(getCommentLevelColor(item.level)), 0, levelStr.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    mBinding.tvTag.visibility = View.VISIBLE
                    mBinding.tvTag.text = spannstring
                } catch (e: Exception) {
                }

            } else {
                mBinding.tvTag.visibility = View.GONE
            }
        }


    }

    private fun getCommentLevelColor(level: String): Int {
        return when (level) {
            "0" -> {
                mContext!!.resources.getColor(R.color.color_ff9e05)
            }
            "1" -> {
                mContext!!.resources.getColor(R.color.color_333)
            }
            "2" -> {
                mContext!!.resources.getColor(R.color.color_ff9e05)
            }
            else -> {
                mContext!!.resources.getColor(R.color.color_ff9e05)
            }
        }
    }

    private fun getCommentLevel(level: String): String? {
        return when (level) {
            "0" -> {
                "好评"
            }
            "1" -> {
                "差评"
            }
            "2" -> {
                "一般"
            }
            else -> {
                ""
            }
        }
    }

}