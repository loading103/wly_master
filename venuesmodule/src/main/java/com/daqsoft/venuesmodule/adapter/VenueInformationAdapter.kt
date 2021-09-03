package com.daqsoft.venuesmodule.adapter

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
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ContentBean
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemVenueImagesBinding
import com.daqsoft.venuesmodule.databinding.ItemVenueInformationBinding
import com.jakewharton.rxbinding2.view.RxView
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import org.jetbrains.anko.centerInParent
import java.util.concurrent.TimeUnit

/**
 * @package name：com.daqsoft.venuesmodule.adapter
 * @date 2020/9/15 16:04
 * @author zp
 * @describe 场馆资讯 adapter
 */
class VenueInformationAdapter  : RecyclerViewAdapter<ItemVenueInformationBinding, ContentBean>(R.layout.item_venue_information) {


    override fun getItemCount(): Int {
        if (getData().size > 2){
            return 2
        }
        return super.getItemCount()
    }

    @SuppressLint("CheckResult")
    override fun setVariable(
        mBinding: ItemVenueInformationBinding,
        position: Int,
        item: ContentBean
    ) {

        // item 点击事件
        RxView.clicks(mBinding.root)
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

        bindDataToView(mBinding, item)
    }


    /**
     * 绑定数据到视图
     * @param binding ItemContentNewSmallPictureBinding
     */
    private fun bindDataToView(binding: ItemVenueInformationBinding, item: ContentBean) {
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
                val url = if (item.imageUrls.isNullOrEmpty()) "" else item.imageUrls[0].url
                Glide
                    .with(binding.root.context)
                    .load(url)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5)))
                    .placeholder(R.drawable.placeholder_img_fail_240_180)
                    .into(imageView)
                binding.llCiImg.addView(imageView)
            }
            "AUDIO" -> {
                val imageView = ImageView(binding.root.context)
//                imageView.layoutParams = layoutParams
                Glide
                    .with(binding.root.context)
                    .load(item.audio?.imgUrl)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5)))
                    .placeholder(R.drawable.placeholder_img_fail_240_180)
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
                    .placeholder(R.drawable.placeholder_img_fail_240_180)
                    .into(videoView.thumbImageView)
                binding.llCiImg.addView(videoView)
            }
        }
    }
}

