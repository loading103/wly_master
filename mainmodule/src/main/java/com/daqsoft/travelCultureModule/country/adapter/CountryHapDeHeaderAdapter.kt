package com.daqsoft.travelCultureModule.country.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter

/**
 * desc :农家乐详情页viewpager适配器
 * @author 江云仙
 * @date 2020/4/15 16:30
 */
class CountryHapDeHeaderAdapter : FragmentStatePagerAdapter {

    var fragments: MutableList<Fragment>? = null

    constructor(list: MutableList<Fragment>, fragMg: FragmentManager) : super(fragMg) {
        this.fragments = list
    }

    override fun getItem(position: Int): Fragment {
        return fragments!![position]
    }

    override fun getCount(): Int {
        return fragments!!.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}