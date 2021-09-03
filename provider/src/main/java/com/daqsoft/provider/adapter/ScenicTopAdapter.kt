package com.daqsoft.provider.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter

/**
 * @Description
 * @ClassName   ScenicTopAdapter
 * @Author      PuHua
 * @Time        2020/3/25 13:53
 */
class ScenicTopAdapter : FragmentStatePagerAdapter {

    var fragments: MutableList<Fragment>? = null

    constructor(fragMg: FragmentManager) : super(fragMg)

    constructor(list: MutableList<Fragment>, fragMg: FragmentManager) : super(fragMg) {
        this.fragments = list
    }

    fun setList(list: MutableList<Fragment>){
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