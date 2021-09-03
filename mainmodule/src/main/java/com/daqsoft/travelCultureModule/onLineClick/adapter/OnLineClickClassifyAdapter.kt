package com.daqsoft.travelCultureModule.onLineClick.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemCountryTravelGuideBinding
import com.daqsoft.mainmodule.databinding.ItemOnLineClickClassifyBinding
import com.daqsoft.travelCultureModule.onLineClick.bean.Subset
import java.lang.Exception

/**
 * desc :网红打卡分类适配器
 * @author 江云仙
 * @date 2020/4/20 14:05
 */
class OnLineClickClassifyAdapter: PagerAdapter(){

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
        val mBinding: ItemOnLineClickClassifyBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.item_on_line_click_classify,
            null,
            false
        )
        try {
            Glide.with(mBinding.root.context).load(menus[position].backgroundImg)
                .centerCrop()
                .placeholder(R.drawable.placeholder_img_fail_h300)
                .into(mBinding.imgOnlineClickClassify)
        } catch (e: Exception) {
        }
        container.addView(mBinding.root)
        return mBinding.root
    }
}

