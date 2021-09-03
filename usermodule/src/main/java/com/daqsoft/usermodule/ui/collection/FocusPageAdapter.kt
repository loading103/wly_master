package com.daqsoft.usermodule.ui.collection

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * @Description
 * @ClassName   FocusPageAdapter
 * @Author      luoyi
 * @Time        2020/9/10 20:30
 */
class FocusPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    var fragments:MutableList<Fragment> = mutableListOf()

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}