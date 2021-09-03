package com.daqsoft.travelCultureModule.venuecollect.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * @Description
 * @ClassName   FocusPageAdapter
 * @Author      luoyi
 * @Time        2020/9/10 20:30
 */
class MsgPageAdapter(fm: FragmentManager, private val fragments: List<Fragment>) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}