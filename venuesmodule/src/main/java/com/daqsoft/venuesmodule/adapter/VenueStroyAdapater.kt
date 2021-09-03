package com.daqsoft.venuesmodule.adapter

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.StoreBean
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemVenueStoryBinding
import com.jakewharton.rxbinding2.view.RxView
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   VenueStroyAdapater
 * @Author      PuHua
 * @Time        2020/3/28 10:59
 */
class VenueStroyAdapater : RecyclerViewAdapter<ItemVenueStoryBinding, StoreBean> {
    var context: Context? = null

    constructor(context: Context) : super(R.layout.item_venue_story) {
        this.context = context
    }

    override fun setVariable(mBinding: ItemVenueStoryBinding, position: Int, item: StoreBean) {

        mBinding.headUrl = item.vipHead
        mBinding.name = item.vipNickName
        mBinding.likeNum = item.likeNum

        // 图片数量
        if (!item.images.isNullOrEmpty()) {
            mBinding.txtVenueStoreImgNum.text = "${item.images.size}图"
            mBinding.txtVenueStoreImgNum.visibility = View.VISIBLE
            mBinding.url =item.images[0]
        } else {
            mBinding.txtVenueStoreImgNum.visibility = View.GONE
        }
        // 视频
        if (item.video.isNotEmpty()) {
            // 当有视频时
            mBinding.imgVenueStoryVideo.visibility = View.VISIBLE
        } else {
            mBinding.imgVenueStoryVideo.visibility = View.GONE

        }
        // 判断是否需要添加表填
        var ssb = SpannableStringBuilder()

        if (item.tagName.isNotEmpty()) {
            ssb.append("#" + item.tagName + "#")
            ssb.setSpan(
                ForegroundColorSpan(context!!.resources.getColor(R.color.colorPrimary)),
                0,
                ssb.length,
                Spanned
                    .SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        ssb.append(item.content)
        mBinding.txtVenueStoreContent.text = ssb

        if (ssb.isNullOrEmpty()) {
            mBinding.txtVenueStoreContent.visibility = View.GONE
        } else {
            mBinding.txtVenueStoreContent.visibility = View.VISIBLE
        }
        // 地址
        if (item.resourceRegionName.isNullOrEmpty()) {
            // 判断是否关联地址和类型
            mBinding.txtVenueStoryAddress.visibility = View.GONE
        } else {
            val address = DividerTextUtils.convertDotString(
                StringBuilder(), item.resourceRegionName,
                item.resourceName
            )
            mBinding.txtVenueStoryAddress.text = address
            mBinding.txtVenueStoryAddress.visibility = View.VISIBLE
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

    }
}