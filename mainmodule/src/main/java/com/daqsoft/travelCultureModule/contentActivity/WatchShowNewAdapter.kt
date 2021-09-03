package com.daqsoft.travelCultureModule.contentActivity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemContentNewBigPictureBinding
import com.daqsoft.mainmodule.databinding.ItemContentNewBinding
import com.daqsoft.mainmodule.databinding.ItemContentNewSmallPictureBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubZixunBean
import com.jakewharton.rxbinding2.view.RxView
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import org.jetbrains.anko.centerInParent
import java.util.concurrent.TimeUnit

/**
 * @package name：com.daqsoft.travelCultureModule.contentActivity
 * @date 2020/9/8 17:06
 * @author zp
 * @describe
 */
class WatchShowNewAdapter :
    RecyclerViewAdapter<ItemContentNewBinding, ClubZixunBean>(R.layout.item_content_new) {

    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemContentNewBinding, position: Int, item: ClubZixunBean) {

        mBinding.frameLayout.removeAllViews()
        var binding: ViewDataBinding? = null
//        if (position == 0){
//            // 第一个item加载大图
//            binding = DataBindingUtil.inflate(LayoutInflater.from(mBinding.root.context),R.layout.item_content_new_big_picture,mBinding.frameLayout,false)
//        }else{
        // 其余item加载小图
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mBinding.root.context),
            R.layout.item_content_new_small_picture,
            mBinding.frameLayout,
            false
        )
//        }

        // 视图绑定
        if (binding is ItemContentNewBigPictureBinding) {
            bindDataToView(binding, item)
        }
        if (binding is ItemContentNewSmallPictureBinding) {
            bindDataToView(binding, item)
        }

        // item 点击事件
        RxView.clicks(binding.root)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (item.contentType.equals("IMAGE")) {
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_IMG)
                        .withString("id", item.id.toString())
                        .navigation()
                } else {
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                        .withString("id", item.id.toString())
                        .withString("contentTitle", "资讯详情")
                        .navigation()
                }
            }
        mBinding.frameLayout.addView(binding.root)
    }

    /**
     * 绑定数据到视图
     * @param binding ItemContentNewBigPictureBinding
     */
    private fun bindDataToView(binding: ItemContentNewBigPictureBinding, item: ClubZixunBean) {
        binding.author.text = if (item.author.isNullOrEmpty()) item.createCompany else item.author
        binding.title.text = item.title
        binding.time.text =
            DateUtil.formatDateByString("yyyy-MM-dd", "yyyy-MM-dd HH:mm", item.publishTime)

        binding.llCiImg.removeAllViews()
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.centerInParent()
        when (item.contentType) {
            "IMAGE", "CONTENT" -> {
                val imageView = ImageView(binding.root.context)
                imageView.layoutParams = layoutParams
                if (item.imageUrls.isNotEmpty()) {
                    Glide
                        .with(binding.root.context)
                        .load(item.imageUrls[0].url)
                        .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5)))
                        .placeholder(R.drawable.placeholder_img_fail_h300)
                        .into(imageView)
                } else {
                    imageView.setImageResource(R.mipmap.community_audio)
                }
                binding.llCiImg.addView(imageView)
            }
            "AUDIO" -> {
                val imageView = ImageView(binding.root.context)
                imageView.layoutParams = layoutParams
                Glide
                    .with(binding.root.context)
                    .load(item.audio?.imgUrl)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5)))
                    .placeholder(R.drawable.placeholder_img_fail_h300)
                    .into(imageView)
                binding.llCiImg.addView(imageView)

                val audio = ImageView(binding.root.context)
                layoutParams.width = Utils.dip2px(binding.root.context, 75f).toInt()
                layoutParams.height = Utils.dip2px(binding.root.context, 18f).toInt()
                audio.layoutParams = layoutParams
                Glide
                    .with(binding.root.context)
                    .load(R.mipmap.community_audio)
                    .into(audio)
                binding.llCiImg.addView(audio)

                val play = ImageView(binding.root.context)
                layoutParams.width = Utils.dip2px(binding.root.context, 40f).toInt()
                layoutParams.height = Utils.dip2px(binding.root.context, 40f).toInt()
                play.layoutParams = layoutParams
                Glide
                    .with(binding.root.context)
                    .load(R.mipmap.activity_news_icon_play_big)
                    .into(play)
                binding.llCiImg.addView(play)
            }
            "VIDEO" -> {
                var videoView = JCVideoPlayerStandard(binding.root.context)
                videoView.layoutParams = layoutParams
                videoView.setUp(item.video?.url, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
                Glide
                    .with(binding.root.context)
                    .load(if (!item.video?.imgUrl.isNullOrEmpty()) item.video?.imgUrl else item.video?.url)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5)))
                    .placeholder(R.drawable.placeholder_img_fail_h300)
                    .into(videoView.thumbImageView)
                binding.llCiImg.addView(videoView)
            }
        }
    }

    /**
     * 绑定数据到视图
     * @param binding ItemContentNewSmallPictureBinding
     */
    private fun bindDataToView(binding: ItemContentNewSmallPictureBinding, item: ClubZixunBean) {
        binding.author.text = if (item.author.isNullOrEmpty()) item.createCompany else item.author
        binding.title.text = item.title
        binding.time.text =
            DateUtil.formatDateByString("yyyy-MM-dd", "yyyy-MM-dd HH:mm", item.publishTime)

        binding.llCiImg.removeAllViews()
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.centerInParent()
        when (item.contentType) {
            "IMAGE", "CONTENT" -> {
                val imageView = ImageView(binding.root.context)
                imageView.layoutParams = layoutParams
                if (item.imageUrls.isNotEmpty()) {
                    Glide
                        .with(binding.root.context)
                        .load(item.imageUrls[0].url)
                        .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5)))
                        .placeholder(R.drawable.placeholder_img_fail_h300)
                        .into(imageView)
                } else {
                    imageView.setImageResource(R.mipmap.community_audio)
                }
                binding.llCiImg.addView(imageView)
            }
            "AUDIO" -> {
                val imageView = ImageView(binding.root.context)
//                imageView.layoutParams = layoutParams
                Glide
                    .with(binding.root.context)
                    .load(item.audio?.imgUrl)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5)))
                    .placeholder(R.drawable.placeholder_img_fail_h300)
                    .into(imageView)
                binding.llCiImg.addView(imageView)

                val audio = ImageView(binding.root.context)
                layoutParams.width = Utils.dip2px(binding.root.context, 75f).toInt()
                layoutParams.height = Utils.dip2px(binding.root.context, 18f).toInt()
                audio.layoutParams = layoutParams
                Glide
                    .with(binding.root.context)
                    .load(R.mipmap.community_audio)
                    .into(audio)
                binding.llCiImg.addView(audio)

                val play = ImageView(binding.root.context)
                layoutParams.width = Utils.dip2px(binding.root.context, 40f).toInt()
                layoutParams.height = Utils.dip2px(binding.root.context, 40f).toInt()
                play.layoutParams = layoutParams
                Glide
                    .with(binding.root.context)
                    .load(R.mipmap.activity_news_icon_play_small)
                    .into(play)
                binding.llCiImg.addView(play)
            }
            "VIDEO" -> {
                var videoView = JCVideoPlayerStandard(binding.root.context)
                videoView.layoutParams = layoutParams
                videoView.setUp(item.video?.url, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
                Glide
                    .with(binding.root.context)
                    .load(if (!item.video?.imgUrl.isNullOrEmpty()) item.video?.imgUrl else item.video?.url)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5)))
                    .placeholder(R.drawable.placeholder_img_fail_h300)
                    .into(videoView.thumbImageView)
                binding.llCiImg.addView(videoView)
            }
        }
    }

}