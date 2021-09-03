package com.daqsoft.venuesmodule.adapter

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.RelativeLayout
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ContentBean
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemOtherColumnsInformationBinding
import com.jakewharton.rxbinding2.view.RxView
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import org.jetbrains.anko.centerInParent
import java.util.concurrent.TimeUnit

/**
 * @package name：com.daqsoft.venuesmodule.adapter
 * @date 2020/9/15 18:19
 * @author zp
 * @describe 其他栏目下 recycleView adapter
 */
class OtherColumnsRecycleViewAdapter : RecyclerViewAdapter<ItemOtherColumnsInformationBinding, ContentBean>(R.layout.item_other_columns_information) {


    override fun getItemCount(): Int {
        if (getData().size > 4){
            return 4
        }
        return super.getItemCount()
    }

    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemOtherColumnsInformationBinding, position: Int, item: ContentBean) {

        // 标题
        mBinding.title.text = item.title

        // 时间
        mBinding.content.text = DateUtil.formatDateByString("yyyy-MM-dd", "yyyy-MM-dd HH:mm", item.publishTime)

        // 图片
        mBinding.llCiImg.removeAllViews()
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT)
        layoutParams.centerInParent()
        when(item.contentType) {
            "IMAGE", "CONTENT" -> {
                val imageView = ImageView(mBinding.root.context)
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                imageView.layoutParams = layoutParams
                val url = if (item.imageUrls.isNullOrEmpty()) "" else item.imageUrls[0].url
                Glide
                    .with(mBinding.root.context)
                    .load(url)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5.dp)))
                    .placeholder(R.drawable.placeholder_img_fail_240_180)
                    .into(imageView)
                mBinding.llCiImg.addView(imageView)
            }
            "AUDIO" -> {
                val imageView = ImageView(mBinding.root.context)
//                imageView.layoutParams = layoutParams
                Glide
                    .with(mBinding.root.context)
                    .load(item.audio?.imgUrl)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5.dp)))
                    .placeholder(R.drawable.placeholder_img_fail_240_180)
                    .into(imageView)
                mBinding.llCiImg.addView(imageView)

                val audio = ImageView(mBinding.root.context)
                layoutParams.width = Utils.dip2px(mBinding.root.context, 75f).toInt()
                layoutParams.height = Utils.dip2px(mBinding.root.context, 18f).toInt()
                audio.layoutParams = layoutParams
                Glide
                    .with(mBinding.root.context)
                    .load(R.mipmap.community_audio)
                    .into(audio)
                mBinding.llCiImg.addView(audio)

                val play = ImageView(mBinding.root.context)
                layoutParams.width = Utils.dip2px(mBinding.root.context, 40f).toInt()
                layoutParams.height = Utils.dip2px(mBinding.root.context, 40f).toInt()
                play.layoutParams = layoutParams
                Glide
                    .with(mBinding.root.context)
                    .load(R.mipmap.activity_news_icon_play_small)
                    .into(play)
                mBinding.llCiImg.addView(play)
            }
            "VIDEO" -> {
                var videoView = JCVideoPlayerStandard(mBinding.root.context)
                videoView.layoutParams = layoutParams
                videoView.setUp(item.video?.url, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
                Glide
                    .with(mBinding.root.context)
                    .load(if (!item.video?.imgUrl.isNullOrEmpty()) item.video?.imgUrl else item.video?.url)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5.dp)))
                    .placeholder(R.drawable.placeholder_img_fail_240_180)
                    .into(videoView.thumbImageView)
                mBinding.llCiImg.addView(videoView)
            }
        }

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
    }
}