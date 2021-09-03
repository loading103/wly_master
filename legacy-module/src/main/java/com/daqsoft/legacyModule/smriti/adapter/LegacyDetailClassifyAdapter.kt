package com.daqsoft.legacyModule.smriti.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter

/**
 * desc :非遗项目详情分类适配器
 * @author 江云仙
 * @date 2020/4/20 14:05
 */
class LegacyDetailClassifyAdapter: FragmentStatePagerAdapter{

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

