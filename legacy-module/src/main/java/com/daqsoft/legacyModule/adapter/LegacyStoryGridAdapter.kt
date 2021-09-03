package com.daqsoft.legacyModule.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.bean.LegacyStoryListBean
import com.daqsoft.legacyModule.databinding.LegacyModuleItemStoryFlowBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.Constant
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * @Description 瀑布流样式的故事攻略适配器
 * @ClassName   LegacyStoryGridAdapter
 * @Author      Wongxd
 * @Time        2020/4/22 20:46
 */
internal class LegacyStoryGridAdapter(context: Context) :
    RecyclerViewAdapter<LegacyModuleItemStoryFlowBinding, LegacyStoryListBean>
        (R.layout.legacy_module_item_story_flow) {

    private val mContext = context

    @SuppressLint("CheckResult")
    override fun setVariable(
        mBinding: LegacyModuleItemStoryFlowBinding,
        position: Int,
        item: LegacyStoryListBean
    ) {
        Glide.with(mContext).load(item.vipHead).placeholder(R.mipmap.mine_profile_photo_default)
            .into(mBinding.ivUser)
        mBinding.name = item.vipNickName
        if (item.images.isNotEmpty()) {
            mBinding.url = item.images[0]
        } else {
            mBinding.url = item.cover
        }
        if (item.resourceRegionName.isNullOrEmpty()) {
            mBinding.locationName.visibility = View.GONE
        } else {
            mBinding.locationName.text = item.resourceRegionName
        }
        if (!item.video.isNullOrEmpty()) {
            mBinding.ivVideo.visibility = View.VISIBLE
        } else {
            mBinding.ivVideo.visibility = View.GONE
        }


        if (!item.images.isNullOrEmpty()) {
            mBinding.tvImageNumber.text = mContext.getString(
                R.string.legacy_module_story_flow_img_num_format,
                item.images.size.toString()
            )
            mBinding.tvImageNumber.visibility = View.VISIBLE
        } else {
            mBinding.tvImageNumber.visibility = View.GONE
        }


        // 判断是否需要添加表填
        val ssb = SpannableStringBuilder()
        if (!item.tagName.isNullOrEmpty()) {
            ssb.append("#" + item.tagName + "#")
            ssb.setSpan(
                ForegroundColorSpan(mContext.resources.getColor(R.color.legacy_module_34ac9e)),
                0,
                ssb.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        ssb.append(item.title)
        mBinding.tvContent.text = ssb

        if (ssb.isNullOrEmpty()) {
            mBinding.tvContent.visibility = View.GONE
        } else {
            mBinding.tvContent.visibility = View.VISIBLE
        }

        if (item.storyType == Constant.STORY_TYPE_STORY) {
            // 是故事
            val cityTypeStr = SpannableStringBuilder().apply {
                if (item.ich) {
                    append("非遗作品")
                    setSpan(
                        ForegroundColorSpan(mContext.resources.getColor(R.color.legacy_module_color_4586c0)),
                        0,
                        this.length,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                }
                if (!item.resourceRegionName.isNullOrEmpty()) {
                    append(" | ")
                    append(item.resourceRegionName)
                    append("·")
                }
                if (!item.resourceType.isNullOrEmpty())
                    append(ResourceType.getName(item.resourceType))
            }

            mBinding.tvCityType.text = cityTypeStr
            mBinding.tvCityType.setTextColor(mContext.resources.getColor(R.color.color_333))


        } else {
            // 是攻略
            mBinding.tvCityType.visibility = View.VISIBLE
            mBinding.tvCityType.setTextColor(mContext.resources.getColor(R.color.f3ae09))
            mBinding.tvCityType.text =
                mContext.getString(R.string.legacy_module_story_type_strategy)

            if (!item.strategyDetail.isNullOrEmpty()) {
                val strategyDetail = item.strategyDetail[0]
                when (strategyDetail.contentType) {
                    Constant.STORY_STRATEGY_IMAGE_TYPE -> {
                        val images = strategyDetail.content.split(",")
                        if (!item.images.isNullOrEmpty()) {
                            mBinding.tvImageNumber.text = mContext.getString(
                                R.string.legacy_module_story_flow_img_num_format,
                                images.size.toString()
                            )
                            mBinding.tvImageNumber.visibility = View.VISIBLE
                        } else {
                            mBinding.tvImageNumber.visibility = View.GONE
                        }
                        mBinding.tvContent.text = item.content

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

        }

        mBinding.likeNumber = item.likeNum.toString()

//        if (item.vipResourceStatus.likeStatus)
//            mBinding.ivLike.setImageResource(R.drawable.legacy_like_selected)



        RxView.clicks(mBinding.root)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                var path =
                    if (item.storyType == "strategy") MainARouterPath.MAIN_STRATEGY_DETAIL else MainARouterPath.MAIN_STORY_DETAIL
                ARouter.getInstance()
                    .build(path)
                    .withString("id", item.id.toString())
                    .withInt("type", 1)
                    .navigation()

            }

    }

}