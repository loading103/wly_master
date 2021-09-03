package com.daqsoft.travelCultureModule.resource.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.daqsoft.travelCultureModule.resource.fragment.ScenicSpotFragment

/**
 * @Description 景点详情页面适配器
 * @ClassName   ScenicSpotPagerAdapter
 * @Author      luoyi
 * @Time        2020/4/2 10:42
 */
class ScenicSpotPagerAdapter : FragmentPagerAdapter {

    private var datas: MutableList<ScenicSpotFragment> = mutableListOf()

    constructor(data: MutableList<ScenicSpotFragment>, manager: FragmentManager) : super(manager) {
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