package com.daqsoft.travelCultureModule.sidetour.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.daqsoft.provider.bean.MapResBean
import com.daqsoft.travelCultureModule.sidetour.fragment.SideTourBusFragment
import com.daqsoft.travelCultureModule.sidetour.fragment.SideTourToilentFragment

/**
 * @Description
 * @ClassName   SideTourToilentPageAdapter
 * @Author      PuHua
 * @Time        2020/3/19 10:56
 */
class SideTourBusPageAdapter : FragmentStatePagerAdapter {

    /**
     * 当前位置的纬度
     */
    var lat = ""

    /**
     * 当前位置的经度
     */
    var lng = ""

    var mDatas: MutableList<MapResBean> = mutableListOf()
    var isService = false

    constructor(fm: FragmentManager) : super(fm) {

    }

    override fun getItem(position: Int): Fragment {
        return SideTourBusFragment.newInstance(mDatas[position], position, mDatas.size, lat, lng, isService)
    }

    override fun getCount(): Int {
        return mDatas.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}