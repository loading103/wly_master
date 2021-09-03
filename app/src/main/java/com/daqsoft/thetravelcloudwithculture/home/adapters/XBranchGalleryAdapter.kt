package com.daqsoft.thetravelcloudwithculture.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeBranchBinding
import com.daqsoft.provider.bean.HomeBranchBean

/**
 * @Description 品牌展播的适配器
 * @ClassName   XGalleryAdapter
 * @Author      PuHua
 * @Time        2019/12/9 15:36
 */
class XBranchGalleryAdapter : PagerAdapter() {

    val menus = mutableListOf<HomeBranchBean>()

    override fun getCount(): Int {
        return menus.size
    }

    override fun isViewFromObject(view: View, ob: Any): Boolean {
        return view === ob
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mBinding: ItemHomeBranchBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.item_home_branch,
            null,
            false)
        val homeBranchBean =  menus[position]
        mBinding.url = homeBranchBean.brandImage
        mBinding.logo = homeBranchBean.brandLogo
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