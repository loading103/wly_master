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
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.bean.LegacyStoryListBean
import com.daqsoft.legacyModule.databinding.LegacyModuleItemStoryFlowBinding
import com.daqsoft.legacyModule.databinding.LegacyModuleItemStoryLinearBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.utils.DividerTextUtils
import com.jakewharton.rxbinding2.view.RxView
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

/**
 * @Description liner样式的故事攻略适配器
 * @ClassName   LegacyStoryLinerAdapter
 * @Author      Wongxd
 * @Time        2020/4/24 14:28
 */
internal class LegacyStoryLinerAdapter(context: Context) : RecyclerViewAdapter<LegacyModuleItemStoryLinearBinding, LegacyStoryListBean>
    (R.layout.legacy_module_item_story_linear) {

    private val mContext = context

    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: LegacyModuleItemStoryLinearBinding, position: Int, item: LegacyStoryListBean) {
        mBinding.tvLike.text = item.likeNum.toString()
        mBinding.tvComment.text = item.commentNum.toString()
        mBinding.likeNumber = item.likeNum.toString()
        mBinding.commentNumber = item.commentNum.toString()
        Glide.with(mContext).load(item.vipHead)
            .placeholder(R.mipmap.mine_profile_photo_default)
            .into(mBinding.ivUser)
        mBinding.name = item.vipNickName


        // 图片数量
        if (!item.images.isNullOrEmpty()) {
            mBinding.tvImageNumber.text =
                mContext.getString(
                    R.string.home_story_image_number,
                    item.images.size.toString()
                )
            mBinding.tvImageNumber.visibility = View.VISIBLE
        } else {
            mBinding.tvImageNumber.visibility = View.GONE
        }

        //  封面图
        if (!item.video.isNullOrEmpty()) {
            // 当有视频时
            mBinding.ivVideo.visibility = View.VISIBLE
            mBinding.aiImage.setVideoImage(item.video)

        } else {
            mBinding.ivVideo.visibility = View.GONE
            // 当没有视频只有图片时
            mBinding.aiImage.setImages(item.images)

        }




        // 地址
        if (item.resourceRegionName.isEmpty()) {
            // 判断是否关联地址和类型
            mBinding.locationName.visibility = View.GONE
        } else {
            mBinding.locationName.visibility = View.VISIBLE
        }
        mBinding.locationName.text = DividerTextUtils.convertDotString(
            StringBuilder(), item.resourceRegionName,
            item.resourceName
        )

        var ssb = SpannableStringBuilder()
        if (item.tagName.isNotEmpty()) {
            ssb.append("#" + item.tagName + "#")
            ssb.setSpan(
                ForegroundColorSpan(mContext.resources.getColor(R.color.legacy_module_primary_color)),
                0,
                ssb.length,
                Spanned
                    .SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        ssb.append(item.title)
        mBinding.tvContent.text = ssb

        if (ssb.isEmpty()) {
            mBinding.tvContent.visibility = View.GONE
        } else {
            mBinding.tvContent.visibility = View.VISIBLE
        }
        mBinding.root.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.LegacyModule.LEGACY_WORKS_DETAIL)
                .withInt("type",0)
                .withString("id",item.id.toString())
                .navigation()
        }

    }

}