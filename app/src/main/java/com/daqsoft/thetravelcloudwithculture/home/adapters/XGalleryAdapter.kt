package com.daqsoft.thetravelcloudwithculture.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeTripBinding
import com.daqsoft.provider.bean.HomeTrip

/**
 * @Description 智能行程的适配器
 * @ClassName   XGalleryAdapter
 * @Author      PuHua
 * @Time        2019/12/9 15:36
 */
class XGalleryAdapter : PagerAdapter() {

    val menus = mutableListOf<HomeTrip>()

    override fun getCount(): Int {
        return menus.size
    }

    override fun isViewFromObject(view: View, ob: Any): Boolean {
        return view === ob
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mBinding: ItemHomeTripBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.item_home_trip,
            null,
            false)

        container.addView(mBinding.root)
        return mBinding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, ob: Any) {
//        super.destroyItem(container, position, ob)
        if (ob != null && ob is View) {
            container.removeView(ob as View?)
        }
    }

}