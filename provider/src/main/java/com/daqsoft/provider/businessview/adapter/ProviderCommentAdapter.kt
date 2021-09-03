package com.daqsoft.provider.businessview.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.lottie.parser.ColorParser
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.R
import com.daqsoft.provider.base.LocalResourceType
import com.daqsoft.provider.commentModule.ImageVideoReadAdapter
import com.daqsoft.provider.commentModule.ResourceBean
import com.daqsoft.provider.databinding.ItemProviderCommentBinding
import com.daqsoft.provider.databinding.ItemProviderCommentNewBinding
import com.daqsoft.provider.network.comment.beans.CommentBean
import java.lang.StringBuilder

/**
 * @Description
 * @ClassName   ProviderCommentAdapter
 * @Author      luoyi
 * @Time        2020/4/2 15:32
 */
class ProviderCommentAdapter(context: Context,showicon:Boolean) :
    RecyclerViewAdapter<ItemProviderCommentNewBinding, CommentBean>(
        R.layout.item_provider_comment_new
    ) {
    val mContext = context
    val isShowTj:Boolean =showicon
    override fun setVariable(
        mBinding: ItemProviderCommentNewBinding,
        position: Int,
        item: CommentBean
    ) {

        mBinding.name = if (!item.vipNickName.isNullOrEmpty()) {
            item.vipNickName
        } else {
            "游客"
        }

        // 加载回复列表
        if(item.commentReplyPageData!=null && !item.commentReplyPageData.rows.isNullOrEmpty()){
            mBinding.cvReply.visibility=View.VISIBLE
            mBinding.cvReply.setData(item.commentReplyPageData.rows,item)
        }else{
            mBinding.cvReply.visibility=View.GONE
        }

        mBinding.time = item.commentTime
        mBinding.content = item.content
        Glide.with(mContext).load("" + item.vipHead)
            .placeholder(R.mipmap.mine_profile_photo_default)
            .into(mBinding.aivImage)
//        if(isShowTj){
//            setCommomLevel(item.level,mBinding)
//        }
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
        imageAdapter.emptyViewShow = false
        val resources = mutableListOf<ResourceBean>()
        if (!item.video.isNullOrEmpty()) {
            for (s in item.video) {
                resources.add(ResourceBean(s, LocalResourceType.VIDEO))
            }
        }
        if (!item.image.isNullOrEmpty()) {
            for (s in item.image) {
                resources.add(ResourceBean(s, LocalResourceType.IMAGE))
            }
        }

        if (!resources.isNullOrEmpty()) {
            imageAdapter.add(resources)
            imageAdapter.setData(resources)
            mBinding.rvImages.isNestedScrollingEnabled = false
            val gridLayoutManager = GridLayoutManager(mContext, 3)
            mBinding.rvImages.layoutManager = gridLayoutManager
            mBinding.rvImages.adapter = imageAdapter
            mBinding.rvImages.visibility = View.VISIBLE
        } else {
            mBinding.rvImages.visibility = View.GONE
        }

//        try {



//            if (item.star > 0) {
//                mBinding.rbarActivity.max = 5
//                mBinding.rbarActivity.rating = item.star.toFloat()
//                mBinding.rbarActivity.visibility = View.VISIBLE
//                mBinding.vRatingActivity.visibility = View.VISIBLE
//            } else {
//                mBinding.rbarActivity.visibility = View.GONE
//                mBinding.vRatingActivity.visibility = View.GONE
//            }
//        } catch (e: java.lang.Exception) {
//        }

        // 标签
        val tag = StringBuilder()
        if (!item.commentTag.isNullOrEmpty()) {
            for (i in item.commentTag.indices) {
                tag.append(item.commentTag[i]).append("·")
            }
        }
        if (!tag.isNullOrEmpty()) {
            tag.deleteCharAt(tag.length - 1)
            mBinding.tvTag.visibility = View.VISIBLE
            if (!item.level.isNullOrEmpty()) {
                try {
                    val levelStr: String = getCommentLevel(item.level) + "·"
                    var spannstring = SpannableString("${levelStr}${tag}")
                    spannstring.setSpan(
                        ForegroundColorSpan(getCommentLevelColor(item.level)),
                        0,
                        levelStr.length,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
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
                    spannstring.setSpan(
                        ForegroundColorSpan(getCommentLevelColor(item.level)),
                        0,
                        levelStr.length,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    mBinding.tvTag.visibility = View.VISIBLE
                    mBinding.tvTag.text = spannstring
                } catch (e: Exception) {
                }

            } else {
                mBinding.tvTag.visibility = View.GONE
            }
        }


    }
    fun tintDrawable(drawable: Drawable, colors: ColorStateList): Drawable {
        val wrappedDrawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTintList(wrappedDrawable, colors)
        return wrappedDrawable
    }
    private fun setCommomLevel(level: String, mBinding: ItemProviderCommentBinding) {
        return when (level) {
            "0" -> {
                mBinding.tvPj.text="好评"
                mBinding.ivPj.setImageResource(R.mipmap.comment_list_good_normal)
            }
            "1" -> {
                mBinding.tvPj.text="差评"
                mBinding.ivPj.setImageResource(R.mipmap.comment_list_bad_normal)
            }
            "2" -> {
                mBinding.tvPj.text="一般"
                mBinding.ivPj.setImageResource(R.mipmap.comment_list_general_normal)
            }
            else ->
                mBinding.tvPj.text=""
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