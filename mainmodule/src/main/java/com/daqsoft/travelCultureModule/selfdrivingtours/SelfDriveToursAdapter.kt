package com.daqsoft.travelCultureModule.selfdrivingtours

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemSelfDriveToursBinding
import com.daqsoft.travelCultureModule.onLineClick.bean.Subset

/**
 * desc :自驾游适配器
 * @author 江云仙
 * @date 2020/4/21 14:05
 */
class SelfDriveToursAdapter: PagerAdapter(){

    val menus = mutableListOf<Subset>()

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

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mBinding: ItemSelfDriveToursBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.item_self_drive_tours,
            null,
            false
        )
        mBinding.urls=menus[position].backgroundImg
        container.addView(mBinding.root)
        return mBinding.root
    }
}

