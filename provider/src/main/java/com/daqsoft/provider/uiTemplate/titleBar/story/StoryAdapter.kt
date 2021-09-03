package com.daqsoft.provider.uiTemplate.titleBar.story

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.extend.dp
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.bean.StoreBean
import com.daqsoft.provider.databinding.ItemVenueStoryNewBinding
import com.daqsoft.provider.databinding.ScStoryStyleOneRecycleViewItemBinding
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.view.extend.onModuleNoDoubleClick

/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.story
 * @date 2020/9/21 15:10
 * @author zp
 * @describe
 */
class StoryAdapter(context: Context) : RecyclerViewAdapter<ScStoryStyleOneRecycleViewItemBinding, StoreBean>
    (R.layout.sc_story_style_one_recycle_view_item) {

    val mContext = context

    @SuppressLint("CheckResult")
    override fun setVariable(
        mBinding: ScStoryStyleOneRecycleViewItemBinding,
        position: Int,
        item: StoreBean
    ) {
        Glide.with(mContext)
            .load(item.vipHead)
            .placeholder(R.mipmap.common_user_headpic_default)
            .into(mBinding.ivUser)

        mBinding.name = item.vipNickName

        if (item.resourceRegionName.isNullOrEmpty()) {
            mBinding.locationName.visibility = View.GONE
        } else {
            mBinding.locationName.visibility = View.VISIBLE
            mBinding.locationName.text = item.resourceName
        }
        if (!item.video.isNullOrEmpty()) {
            mBinding.ivVideo.visibility = View.VISIBLE
        } else {
            mBinding.ivVideo.visibility = View.GONE
        }
        if (item.resourceRegionName.isNullOrEmpty()) {
            // 判断是否关联地址和类型
            mBinding.tvCityType.visibility = View.GONE
        } else {
            mBinding.tvCityType.visibility = View.VISIBLE
        }

        if (item.storyType == Constant.STORY_TYPE_STORY) {
            // 是故事
            mBinding.tvCityType.text =
                item.resourceRegionName + "·" + ResourceType.getName(item.resourceType)
            mBinding.tvCityType.setTextColor(mContext.resources.getColor(R.color.txt_gray))

            // 判断是否需要添加表填
            var ssb = SpannableStringBuilder()
            if (!item.tagName.isNullOrEmpty()) {
                ssb.append("#" + item.tagName + "#")
                ssb.setSpan(
                    ForegroundColorSpan(mContext.resources.getColor(R.color.colorPrimary)), 0, ssb
                        .length, Spanned
                        .SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
            ssb.append(item.content)
            mBinding.tvContent.text = ssb

            if (ssb.isNullOrEmpty()) {
                mBinding.tvContent.visibility = View.INVISIBLE
            } else {
                mBinding.tvContent.visibility = View.VISIBLE
            }

            if (!item.images.isNullOrEmpty() && item.images.size > 1) {
                mBinding.tvImageNumber.text = mContext.getString(
                    R.string.venue_story_image_number, item.images.size.toString()
                )
                mBinding.tvImageNumber.visibility = View.VISIBLE
            } else {
                mBinding.tvImageNumber.visibility = View.GONE
            }
            mBinding.root.onModuleNoDoubleClick(
                mContext!!.resources.getString(R.string.venue_story),
                ProviderApi.REGION_MAIN_COLUMNS
            ) {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_STORY_DETAIL)
                    .withString("id", item.id)
                    .withInt("type", 1)
                    .navigation()
            }

            // 图
            var url = ""
            if (!item.images.isNullOrEmpty()) {
                url = item.images[0]
            } else {
                if (!item.cover.isNullOrEmpty()) {
                    url = item.cover
                } else {
                    mBinding.image.visibility = View.GONE
                }
            }

            Glide
                .with(mBinding.root.context)
                .load(url)
                .into(mBinding.image)

        } else {
            // 是攻略
            mBinding.tvCityType.visibility = View.VISIBLE
            mBinding.tvCityType.setTextColor(mContext.resources.getColor(R.color.f3ae09))
            mBinding.tvCityType.text = mContext.getString(R.string.venue_story_type_strategy)
            // 判断是否需要添加表填
            var ssb = SpannableStringBuilder()
            if (!item.tagName.isNullOrEmpty()) {
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

            if (!item.images.isNullOrEmpty() && item.images.size > 1) {
                mBinding.tvImageNumber.text = mContext.getString(
                    R.string
                        .venue_story_image_number, item.images.size.toString()
                )
                mBinding.tvImageNumber.visibility = View.VISIBLE
            } else {
                mBinding.tvImageNumber.visibility = View.GONE
            }
            mBinding.root.onModuleNoDoubleClick(
                mContext!!.resources.getString(R.string.venue_story),
                ProviderApi.REGION_MAIN_COLUMNS
            ) {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_STRATEGY_DETAIL)
                    .withString("id", item.id)
                    .withInt("type", 1)
                    .navigation()
            }
        }

        if (item.likeNum == "0"){
            mBinding.tvLike.visibility = View.GONE
        }
        mBinding.likeNumber = item.likeNum.toString()


        mBinding.reprint.visibility =
            if (item.sourceUrl.isNullOrBlank() || item.sourceUrl == "undefined") View.GONE else View.VISIBLE
    }
}