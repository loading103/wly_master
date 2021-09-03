package com.daqsoft.guidemodule.guideTourMode

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.daqsoft.guidemodule.bean.GuideLineBean
import com.daqsoft.guidemodule.bean.GuideScenicListBean
import com.daqsoft.guidemodule.fragment.*
import com.daqsoft.guidemodule.fragment.GuideVpSpotTourFragment
import java.lang.IllegalArgumentException



internal class GuideTourModeVpAdapter(fm: FragmentManager, val tag: String) : FragmentStatePagerAdapter(fm) {
    /**
     * 当前位置的纬度
     */
    var lat = ""
    /**
     * 当前位置的经度
     */
    var lng = ""

    val mDataList: MutableList<GuideLineBean.Detail> = mutableListOf()


    fun setNewData(newData: List<GuideLineBean.Detail>) {
        mDataList.clear()
        mDataList.addAll(newData)
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return GuideVpSpotTourFragment(tag, mDataList[position], position, count, lat, lng)
    }

    override fun getCount(): Int {
        return mDataList.size
    }

}