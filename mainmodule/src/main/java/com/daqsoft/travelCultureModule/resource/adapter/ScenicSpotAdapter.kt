package com.daqsoft.travelCultureModule.resource.adapter

import android.text.Html
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemSpotBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ScenicTags
import com.daqsoft.provider.bean.Spots
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * @Description 景点适配器
 * @ClassName   ScenicSotAdapter
 * @Author      luoyi
 * @Time        2020/3/31 9:35
 */
class ScenicSpotAdapter : RecyclerViewAdapter<ItemSpotBinding, Spots>(R.layout.item_spot) {


    var scenicName: String? = ""
    var scenicImageUrls: String? = ""
    var scenicTags: ScenicTags? = null
    var scenicId: Int? = 0

    override fun setVariable(mBinding: ItemSpotBinding, position: Int, item: Spots) {
        if (item.shootStatus.equals("1")) {
            mBinding.tvBest.visibility = View.VISIBLE
        } else {
            mBinding.tvBest.visibility = View.GONE
        }
        var imageUrl = ""
        if (!item.images.isNullOrEmpty()) {
            var images = item.images?.split(",")
            if (!images.isNullOrEmpty()) {
                imageUrl = images[0]
            }
        }
        Glide.with(mBinding.root.context)
            .load(imageUrl)
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(mBinding.ivCover)

        if (item.shootStatus == "1") {
            mBinding.tvBest.visibility = View.VISIBLE
        } else {
            mBinding.tvBest.visibility = View.GONE
        }
        if (!item.briefing.isNullOrEmpty()) {
            mBinding.tvCityType.text = Html.fromHtml(item.briefing)
            mBinding.tvCityType.visibility = View.VISIBLE
        } else {
            mBinding.tvCityType.visibility = View.GONE
        }
        mBinding.spots = item
        RxView.clicks(mBinding.root)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_SCENIC_SPOT_DETAI)
                    .withString("id", item.id)
                    .withString("scenicName", scenicName)
                    .withParcelable("tags", scenicTags)
                    .withString("scenicUrl", scenicImageUrls)
                    .withInt(
                        "scenicId", if (scenicId == null) {
                            -1
                        } else {
                            scenicId!!
                        }
                    )
                    .navigation()
            }
    }


}