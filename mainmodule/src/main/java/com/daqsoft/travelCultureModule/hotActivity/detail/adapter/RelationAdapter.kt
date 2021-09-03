package com.daqsoft.travelCultureModule.hotActivity.detail.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainItemHotActivityRelationBinding
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.travelCultureModule.hotActivity.bean.ActivityRelationBean

/**
 * @Description
 * @ClassName   RelationAdapter
 * @Author      luoyi
 * @Time        2020/3/20 16:16
 */
class RelationAdapter :
    RecyclerViewAdapter<MainItemHotActivityRelationBinding, ActivityRelationBean>(
        R.layout.main_item_hot_activity_relation
    ) {
    var isShowMore: Boolean = false
    override fun setVariable(
        mBinding: MainItemHotActivityRelationBinding,
        position: Int,
        item: ActivityRelationBean
    ) {
        mBinding.name = item.resourceName
        var imageUrl = ""
        if (!item.images.isNullOrEmpty()) {
            var imageUrLs = item.images.split(",")
            if (!imageUrLs.isNullOrEmpty()) {
                imageUrl = imageUrLs[0]
            }
        }
        Glide.with(BaseApplication.context)
            .load(imageUrl)
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(mBinding.ivImage)

        /**
         * 标签适配器
         */
        // 标签
        if (!item.tags.isNullOrEmpty()) {
            val tags = item.tags.split(",")
            mBinding.rvTags.visibility = View.VISIBLE
            mBinding.rvTags.setLabels(tags)
        } else {
            mBinding.rvTags.visibility = View.GONE
        }
        if (item.activityCount > 0) {
            var content = when (item.resourceType) {
                ResourceType.CONTENT_TYPE_VENUE -> {
                    // 场馆
                    "可预订活动" + item.activityCount + "个"
                }
                ResourceType.CONTENT_TYPE_HOTEL -> {
                    ""
                }
                ResourceType.CONTENT_TYPE_SCENERY -> {
                    "玩乐点" + item.activityCount + "个"
                }
                ResourceType.CONTENT_TYPE_RESTAURANT -> {
                    "人均消费" + item.count + "元"
                }

                else -> ""
            }
            mBinding.palceHolder =
                BaseApplication.context.getDrawable(R.mipmap.placeholder_img_fail_240_180)
            mBinding.content = content
            mBinding.rvContent.visibility = View.VISIBLE
        } else if (item.count > 0) {
            var content = when (item.resourceType) {
                ResourceType.CONTENT_TYPE_HOTEL,
                ResourceType.CONTENT_TYPE_RESTAURANT -> {
                    "人均消费" + item.count + "元"
                }
                else -> ""
            }
            mBinding.content = content
            mBinding.rvContent.visibility = View.VISIBLE
        } else {
            mBinding.rvContent.visibility = View.GONE
        }
        mBinding.location = item.regionName
        // 是否游金牌解说
        if (item.goldFlag == "1") {
            mBinding.ivGoldFlag.visibility = View.VISIBLE
        } else {
            mBinding.ivGoldFlag.visibility = View.GONE
        }
        // 视频
        if (!item.videoUrl.isNullOrEmpty()) {
            mBinding.ivVideo.visibility = View.VISIBLE
        } else {
            mBinding.ivVideo.visibility = View.GONE
        }
        // 720
        if (!item.panoramaUrl.isNullOrEmpty()) {
            mBinding.iv720.visibility = View.VISIBLE
        } else {
            mBinding.iv720.visibility = View.GONE
        }
        mBinding.root.onNoDoubleClick {
            MenuJumpUtils.gotoResourceDetail(item.resourceType, item.resourceId.toString())
        }

    }

    override fun getItemCount(): Int {
        return if (isShowMore) {
            if (getData().size > 3) {
                3
            } else {
                getData().size
            }
        } else {
            getData().size
        }
    }

}