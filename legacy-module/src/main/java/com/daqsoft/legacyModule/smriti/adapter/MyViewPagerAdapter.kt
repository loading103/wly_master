package com.daqsoft.legacyModule.smriti.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.*

/**
 * desc :
 * @author 江云仙
 * @date 2020/4/22 13:55
 */
class MyViewPagerAdapter : FragmentStatePagerAdapter {
    private var mFragments: MutableList<Fragment>? = ArrayList() //添加的Fragment的集合
    private val mFragmentsTitles: MutableList<String> = ArrayList() //每个Fragment对应的title的集合

    constructor(fm: FragmentManager?) : super(fm!!) {}
    constructor(fm: FragmentManager?, mFragments: MutableList<Fragment>?) : super(fm!!) {
        this.mFragments = mFragments
    }

    /**
     * @param fragment      添加Fragment
     * @param fragmentTitle Fragment的标题，即TabLayout中对应Tab的标题
     */
    fun addFragment(fragment: Fragment, fragmentTitle: String) {
        mFragments!!.add(fragment)
        mFragmentsTitles.add(fragmentTitle)
    }

    override fun getItem(position: Int): Fragment { //得到对应position的Fragment
        return mFragments!![position]
    }

    override fun getCount(): Int { //返回Fragment的数量
        return if (mFragments == null) 0 else mFragments!!.size
    }

    override fun getPageTitle(position: Int): CharSequence? { //得到对应position的Fragment的title
        return mFragmentsTitles[position]
    }

    fun refreshData(){

    }
}