package com.daqsoft.thetravelcloudwithculture.ws.adapter

import android.media.Image
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.AdInfo
import com.daqsoft.provider.bean.BrandMDD
import com.daqsoft.provider.bean.HomeAd
import com.daqsoft.provider.databinding.ScCityCardStyleThreeViewPager2ItemBinding
import com.daqsoft.provider.databinding.WsCityCardStyleThreeViewPager2ItemBinding
import com.daqsoft.provider.uiTemplate.titleBar.cityCard.SCCityCardStyleThree
import com.youth.banner.adapter.BannerAdapter
import timber.log.Timber

/**
 * @ClassName    WSADbannerAdapter
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/1/4
 */
class WSADbannerAdapter : BannerAdapter<AdInfo, WSADbannerAdapter.BannerItemHolder>(arrayListOf()) {

    inner class BannerItemHolder(val binding: WsCityCardStyleThreeViewPager2ItemBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BannerItemHolder {
        val mBinding = DataBindingUtil.inflate<WsCityCardStyleThreeViewPager2ItemBinding>(
            LayoutInflater.from(parent?.context),
            R.layout.ws_city_card_style_three_view_pager_2_item,
            parent,
            false
        )
        return BannerItemHolder(mBinding)
    }

    //todo jx
    override fun onBindView(
        holder: BannerItemHolder,
        data: AdInfo,
        position: Int,
        size: Int
    ) {
        Timber.e("size ${size}")
        holder.binding.apply {
            // 图
            Glide
                .with(root.context)
                .load(data.imgUrl)
                .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5)))
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .fitCenter()
                .into(image)


            root.onNoDoubleClick {
                ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", "详情")
                    .withString("html", data.jumpUrl)
                    .navigation()
            }
        }
    }

    fun String.getRealImageUrl(): String {
        if (!this.isNullOrEmpty()) {

            if (!this.contains(","))
                return this

            val imgList = this.split(",")
            if (!imgList.isNullOrEmpty()) {
                return imgList[0]
            }
        }
        return ""
    }
}