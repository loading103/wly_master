package com.daqsoft.thetravelcloudwithculture.ws.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.databinding.MainItemHotActivityMapBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.BrandMDD
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeCityWsBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeCityXjBinding
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * @ClassName    WSHomeCityAdapter
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/1/4
 */
class WSHomeCityAdapter : RecyclerViewAdapter<ItemHomeCityWsBinding, BrandMDD>(R.layout.item_home_city_ws) {
    override fun setVariable(mBinding: ItemHomeCityWsBinding, position: Int, item: BrandMDD) {
        var url = ""
        if (!item.images.isNullOrEmpty()) {
            if (item.images.contains(",")) {
                url = item.images.split(",")[0]
            } else {
                url = item.images
            }
            Glide.with(mBinding.root.context)
                .load(url)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.viewCovers)
        }
        RxView.clicks(mBinding.root)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe { o ->
                run {
                    ARouter.getInstance().build(MainARouterPath.MAIN_MDD_CITY_INFO)
                        .withString("id", item.id.toString())
                        .navigation()
                }
            }
        mBinding.textName.text = item.name
        //mBinding.textNameEn.text = item.name
    }
}