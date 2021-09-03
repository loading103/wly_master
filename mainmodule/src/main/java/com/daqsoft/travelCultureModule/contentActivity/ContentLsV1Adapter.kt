package com.daqsoft.travelCultureModule.contentActivity

import android.content.Context
import android.text.Html
import android.text.SpannableStringBuilder
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.adapter.setCenterCropImage
import com.daqsoft.baselib.adapter.setImageUrlqwx
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemContentBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.utils.StringUtils
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubZixunBean
import com.google.gson.internal.LinkedTreeMap
import com.jakewharton.rxbinding2.view.RxView
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   ContentLsV1Adapter
 * @Author      luoyi
 * @Time        2020/6/10 9:15
 */
class ContentLsV1Adapter : RecyclerViewAdapter<ItemContentBinding, ClubZixunBean> {

    private var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_content) {
        mContext = context
    }

    override fun setVariable(mBinding: ItemContentBinding, position: Int, item: ClubZixunBean) {
        RxView.clicks(mBinding.llContent)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (item.contentSource == "reprint") {
                    ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString("html", StringUtils().addHttp(item.sourceUrl))
                        .navigation()
                } else {
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
        mBinding.tvCiTilteName.text = item.title
        if (item.summary.isNullOrEmpty()) {
            mBinding.tvCiContent.visibility = View.GONE
            mBinding.tvCiContent.text = ""
        } else {
            mBinding.tvCiContent.visibility = View.VISIBLE
            mBinding.tvCiContent.text = Html.fromHtml(item.summary)
        }
        mBinding.tvCiName.text = item.createCompany
        setCenterCropImage(
            mBinding.ivCiCommanyImg, item.createCompanyLogo, AppCompatResources.getDrawable(
                BaseApplication.context, R.drawable.placeholder_img_fail_h158
            ), true
        )
        //formatTosepara(item.showNum)+"浏览·"+
        mBinding.tvCiLook.text =
            formatTosepara(item.likeNum) + "赞·" + formatTosepara(item.commentNum) + "评论"
        if (item.video != null) {
            var video = item.video
            mBinding.llCiImg.removeAllViews()
            var imageView: fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard =
                fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard(mContext!!)
            var width = mContext!!.resources.displayMetrics.widthPixels
            var new_width = width - mContext!!.resources.getDimension(R.dimen.dp_20) * 2
            var new_hight = (new_width * 167 / 335)
            var param = LinearLayout.LayoutParams(new_width.toInt(), new_hight.toInt(), 1F)
            imageView.layoutParams = param as ViewGroup.LayoutParams?
            imageView.setUp(video.url, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
            if (!video.imgUrl.isNullOrEmpty()) {
                imageView.thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                setImageUrlqwx(
                    imageView.thumbImageView, video.imgUrl, AppCompatResources.getDrawable(
                        BaseApplication.context, R.drawable.placeholder_img_fail_h300
                    ), 5
                )
            } else {
                // 异步加载视频缩略图
                Glide.with(mContext!!)
                    .setDefaultRequestOptions(RequestOptions().frame(1000000).centerCrop())
                    .load(video.url)
                    .into(imageView.thumbImageView)
            }
            mBinding.llCiImg.addView(imageView)
        } else {
            mBinding.llCiImg.removeAllViews()
            var img_num = item.imageUrls.size
            for (position in item.imageUrls.indices) {
                val imageView: ImageView = ImageView(mContext!!)
                var width = mContext!!.resources.displayMetrics.widthPixels
                var hight = width - mContext!!.resources.getDimension(R.dimen.dp_20) * 2
                var new_wid = (hight - 60) / 3
                var new_hight = (new_wid * 81 / 109)
                var param = LinearLayout.LayoutParams(new_wid.toInt(), new_hight.toInt())
                if (img_num == 1) {
                    new_wid = hight
                    new_hight = new_wid * 167 / 335
                    param = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        new_hight.toInt()
                    )
                } else if (img_num == 2) {
                    new_wid = (hight - 40) / 2
                    new_hight = new_wid * 124 / 165
                    param = LinearLayout.LayoutParams(new_wid.toInt(), new_hight.toInt())
                } else {
                    param = LinearLayout.LayoutParams(new_wid.toInt(), new_hight.toInt())
                }
                param.leftMargin = 18
                param.rightMargin = 18
                imageView.layoutParams = param
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                setImageUrlqwx(
                    imageView, item.imageUrls[position].url, AppCompatResources.getDrawable(
                        BaseApplication.context, R.drawable.placeholder_img_fail_h300
                    ), 5
                )
                mBinding.llCiImg.addView(imageView)
            }
            if (img_num == 0) {
                var audio = item.audio

                if (audio == null) {

                } else {
                    mBinding.rvCiAudio.visibility = View.VISIBLE
                }
            }
        }
    }

}