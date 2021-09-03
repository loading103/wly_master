package com.daqsoft.travelCultureModule.story.story

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemHomeStoryBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.HomeStoryBean
import com.jakewharton.rxbinding2.view.RxView
import org.jsoup.helper.StringUtil
import java.util.concurrent.TimeUnit

/**
 * @Description 瀑布流样式的故事攻略适配器
 * @ClassName   GridStoryAdapter
 * @Author      PuHua
 * @Time        2020/2/22 10:41
 */
class GridHomeStoryAdapter(context: Context) : RecyclerViewAdapter<ItemHomeStoryBinding, HomeStoryBean>(R.layout.item_home_story) {

    val mContext = context
    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemHomeStoryBinding, position: Int, item: HomeStoryBean) {
        Glide.with(mContext).load(item.vipHead).placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.ivUser)
        mBinding.name = item.vipNickName
        if (item.images.isNotEmpty()) {
            mBinding.url = item.images[0]
        } else {
            if(item.cover.isNotEmpty()){
                mBinding.url = item.cover
            }else{
                mBinding.image.visibility = View.GONE
            }
        }
        if (item.resourceRegionName.isNullOrEmpty()) {
            mBinding.locationName.visibility = View.GONE
        } else {
            mBinding.locationName.text = item.resourceRegionName
        }
        if (item.video.isNotEmpty()) {
            mBinding.ivVideo.visibility = View.VISIBLE
        } else {
            mBinding.ivVideo.visibility = View.GONE
        }
        if (item.resourceRegionName.isNullOrEmpty()) {
            // 判断是否关联地址和类型
            mBinding.tvCityType.visibility = View.VISIBLE
        } else {
            mBinding.tvCityType.visibility = View.VISIBLE
        }

        if (item.storyType == Constant.STORY_TYPE_STORY) {
            // 是故事
            if(!item.resourceCompleteRegionName.isNullOrEmpty()) {
                mBinding.tvCityType.text = item.resourceRegionName + "·" + ResourceType.getName(item.resourceType)
                mBinding.tvCityType.setTextColor(mContext.resources.getColor(R.color.txt_gray))
                mBinding.tvCityType.visibility=View.VISIBLE
            }else{
                mBinding.tvCityType.visibility=View.GONE
            }
            // 判断是否需要添加表填
            var ssb = SpannableStringBuilder()
            if (item.tagName.isNotEmpty()) {
                ssb.append("#" + item.tagName + "#")
                ssb.setSpan(
                    ForegroundColorSpan(mContext.resources.getColor(R.color.colorPrimary)), 0, ssb
                        .length, Spanned
                        .SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
            ssb.append(item.title)
            mBinding.tvContent.text = ssb

            if (ssb.isNullOrEmpty()) {
                mBinding.tvContent.visibility = View.INVISIBLE
            } else {
                mBinding.tvContent.visibility = View.VISIBLE
            }

            if (item.images.isNotEmpty()&&item.images.size >1) {
                mBinding.tvImageNumber.text = mContext.getString(
                    R.string
                        .home_story_image_number, item.images.size.toString()
                )
                mBinding.tvImageNumber.visibility = View.VISIBLE
            } else {
                mBinding.tvImageNumber.visibility = View.GONE
            }
            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                    run {
                        ARouter.getInstance()
                            .build(MainARouterPath.MAIN_STORY_DETAIL)
                            .withString("id", item.id)
                            .withInt("type", 1)
                            .navigation()
                    }
                }

        } else {
            // 是攻略
            mBinding.tvCityType.visibility=View.VISIBLE
            mBinding.tvCityType.visibility = View.VISIBLE
            mBinding.tvCityType.setTextColor(mContext.resources.getColor(R.color.f3ae09))
            mBinding.tvCityType.text = mContext.getString(R.string.home_story_type_strategy)
            if(!item.title.isNullOrEmpty()){
                mBinding.tvContent.text= Html.fromHtml(item.title)
            }

            if (item.strategyDetail.isNotEmpty()) {
                val strategyDetail = item.strategyDetail[0]
                when (strategyDetail.contentType) {
                    Constant.STORY_STRATEGY_IMAGE_TYPE -> {
                        val images = strategyDetail.content.split(",")
                        if (item.images.isNotEmpty()&&images.size > 1 ) {
                            mBinding.tvImageNumber.text = mContext.getString(
                                R.string
                                    .home_story_image_number, images.size.toString()
                            )
                            mBinding.tvImageNumber.visibility = View.VISIBLE
                        } else {
                            mBinding.tvImageNumber.visibility = View.GONE
                        }

                    }
                    Constant.STORY_STRATEGY_CONTENT_TYPE -> {
                        mBinding.tvContent.text = strategyDetail.content
                        mBinding.tvContent.visibility = View.VISIBLE
                    }
                    Constant.STORY_STRATEGY_VIDEO_TYPE -> {
                        mBinding.tvContent.visibility = View.GONE
                    }
                }
            }
            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                    run {
                        ARouter.getInstance()
                            .build(MainARouterPath.MAIN_STRATEGY_DETAIL)
                            .withString("id", item.id)
                            .withInt("type", 1)
                            .navigation()
                    }
                }
        }

        mBinding.likeNumber = item.likeNum.toString()


    }

}