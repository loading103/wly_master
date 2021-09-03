package com.daqsoft.travelCultureModule.country.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemCountryTravelGuideBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.travelCultureModule.country.bean.TravelGuideBean
import com.jakewharton.rxbinding2.view.RxView
import me.nereo.multi_image_selector.BigImgActivity
import java.lang.Exception
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/**
 * desc :乡村游记攻略适配器
 * @author 江云仙
 * @date 2020/4/18 13:52
 */
class TravelGuideAdapter(var mContext: Context) :PagerAdapter(){

    val menus = mutableListOf<TravelGuideBean>()

    override fun getCount(): Int {
        return menus.size
    }
    override fun isViewFromObject(view: View, ob: Any): Boolean {
        return view === ob
    }


    override fun destroyItem(container: ViewGroup, position: Int, ob: Any) {
//        super.destroyItem(container, position, ob)
        if (ob != null && ob is View) {
            container.removeView(ob as View?)
        }
    }
    @SuppressLint("CheckResult")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mBinding: ItemCountryTravelGuideBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.item_country_travel_guide,
            null,
            false
        )
        val travelGuideBean = menus[position]
        mBinding.travelGuideTitle.text = travelGuideBean.title
        if (travelGuideBean.imageUrls.isNotEmpty()){
            mBinding.url = travelGuideBean.imageUrls[0].url
            try {
                Glide.with(mBinding.root.context).load(travelGuideBean.imageUrls[0].url)
                    .fitCenter()
                    .into(mBinding.imgTravelGuide)
            } catch (e: Exception) {
            }
        }else{
            Glide.with(mBinding.root.context).load(R.drawable.placeholder_img_fail_240_180)
                .fitCenter()
                .into(mBinding.imgTravelGuide)
        }
        RxView.clicks(mBinding.root)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                    .withString("id", travelGuideBean.id.toString())
                    .withString("contentTitle", "游记攻略")
                    .navigation()
            }
        container.addView(mBinding.root)
        return mBinding.root
    }
}