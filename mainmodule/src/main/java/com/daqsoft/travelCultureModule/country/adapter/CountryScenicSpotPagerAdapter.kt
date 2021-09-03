package com.daqsoft.travelCultureModule.country.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.daqsoft.travelCultureModule.country.fragment.CountryScenicSpotFragment
import com.daqsoft.travelCultureModule.resource.fragment.ScenicSpotFragment

/**
 * 景观点详情页面适配器
 */
class CountryScenicSpotPagerAdapter : FragmentPagerAdapter {

    private var datas: MutableList<CountryScenicSpotFragment> = mutableListOf()

    constructor(data: MutableList<CountryScenicSpotFragment>, manager: FragmentManager) : super(manager) {
        datas.clear()
        datas.addAll(data)
    }

    override fun getItem(position: Int): Fragment {
        return datas[position]
    }

    override fun getCount(): Int {
        return datas.size
    }
}