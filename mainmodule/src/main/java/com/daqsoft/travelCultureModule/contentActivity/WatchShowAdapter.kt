package com.daqsoft.travelCultureModule.contentActivity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemShowCardBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.BrandMDD
import com.daqsoft.travelCultureModule.contentActivity.bean.ShowBean
import com.daqsoft.travelCultureModule.contentActivity.bean.WatchShowBean
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * 看演出适配器
 */
class WatchShowAdapter() : PagerAdapter() {

    private var citys = mutableListOf<ShowBean>()

    constructor(citys: ArrayList<ShowBean>) : this() {
        this.citys = citys
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun getCount(): Int = Int.MAX_VALUE

    @SuppressLint("CheckResult")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var position = position % citys.size
        var mBinding: ItemShowCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.item_show_card,
            null,
            false
        )
//        var city = citys[position]
//        var url = ""
//        if (!city.url.isNullOrEmpty()) {
//            if (city.url.contains(",")) {
//                url = city.url.split(",")[0]
//            } else {
//                url = city.url
//            }
            Glide.with(container.context)
                .load(citys[position].backgroundImg)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.viewCovers)
//        }
        RxView.clicks(mBinding.root)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe { o ->
                run {
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT)
                        .withString("titleStr", citys[position].name)
                        .withString("channelCode", citys[position].channelCode)
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