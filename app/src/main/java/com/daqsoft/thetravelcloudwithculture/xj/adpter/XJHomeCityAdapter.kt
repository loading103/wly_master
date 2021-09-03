package com.daqsoft.thetravelcloudwithculture.xj.adpter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.BrandMDD
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeCityXjBinding
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 *@package:com.daqsoft.thetravelcloudwithculture.xj.adpter
 *@date:2020/4/18 11:15
 *@author: caihj
 *@des:城市名片 adapter
 **/
class XJHomeCityAdapter() :PagerAdapter(){

    private var citys = mutableListOf<BrandMDD>()

    constructor(citys:ArrayList<BrandMDD>) : this() {
               this.citys = citys
           }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun getCount(): Int = Int.MAX_VALUE

    @SuppressLint("CheckResult")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var position = position % citys.size
        var mBinding:ItemHomeCityXjBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.item_home_city_xj,
            null,
            false
            )
        var city = citys[position]
        var url = ""
        if (!city.images.isNullOrEmpty()) {
            if (city.images.contains(",")){
                url = city.images.split(",")[0]
            } else {
                url = city.images
            }
            Glide.with(container.context)
                .load(url)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.viewCovers)
        }
        RxView.clicks(mBinding.root)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe { o ->
                run {
                    ARouter.getInstance().build(MainARouterPath.MAIN_MDD_CITY_INFO)
                        .withString("id", city.id.toString())
                        .navigation()
                }
            }
        container.addView(mBinding.root)
        return mBinding.root
    }


    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, ob: Any) {
//        super.destroyItem(container, position, ob)
        if (ob != null && ob is View) {
            container.removeView(ob as View?)
        }
    }
}