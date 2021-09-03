package com.daqsoft.provider.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.ArrayList

/**
 * @Description fragment+viewpager的适配器
 * @ClassName   ViewPagerAdapter
 * @Author      PuHua
 * @Time        2019/12/6 10:07
 */
class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val mTitles = mutableListOf<String>()
    /**
     * fragment集合
     */
    var mFragments = mutableListOf<Fragment>()

    fun addFragment(fragment: Fragment) {
        mFragments.add(fragment)
    }


    override fun getCount(): Int = mFragments.size

    override fun getPageTitle(position: Int): CharSequence = if (mTitles.size == mFragments.size) mTitles[position] else ""

    override fun getItem(position: Int): Fragment = mFragments[position]

    fun clear() {
        mFragments.clear()
    }
}