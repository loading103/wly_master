package com.daqsoft.travelCultureModule.sidetour.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.daqsoft.provider.bean.MapResBean
import com.daqsoft.travelCultureModule.sidetour.fragment.SideTourParkingFragment
import com.daqsoft.travelCultureModule.sidetour.fragment.SideTourToilentFragment
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.travelCultureModule.sidetour.fragment.SideTourCaservacFragment

/**
 * @Description
 * @ClassName   SideTourCasevacPageAdapter
 * @Author      luoyi
 * @Time        2020/8/28 16:59
 */
class SideTourCasevacPageAdapter : FragmentStatePagerAdapter {
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
        val bean = mDatas[position]
        return SideTourCaservacFragment.newInstance(
            bean,
            position,
            mDatas.size,
            lat,
            lng,
            isService
        )
    }

    override fun getCount(): Int {
        return mDatas.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
    }

}
