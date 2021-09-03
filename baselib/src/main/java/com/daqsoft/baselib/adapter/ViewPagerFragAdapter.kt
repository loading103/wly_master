package com.daqsoft.baselib.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * TabLayout与ViewPager结合需要用到的Adapter
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-5-7
 * @since JDK 1.8.0_191
 */
class ViewPagerFragAdapter(
    private val fm: FragmentManager,
    private val fragments: MutableList<Fragment>
) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence? = position.toString()
}