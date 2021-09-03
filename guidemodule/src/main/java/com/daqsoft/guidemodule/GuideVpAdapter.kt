package com.daqsoft.guidemodule

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.daqsoft.guidemodule.bean.GuideScenicListBean
import com.daqsoft.guidemodule.fragment.GuideVpScenicFragment
import com.daqsoft.guidemodule.fragment.GuideVpSpotFragment
import com.daqsoft.guidemodule.fragment.GuideVpToiletFragment
import com.daqsoft.guidemodule.fragment.GuideVpToiletNoImageFragment
import com.daqsoft.provider.base.ResourceType


/**
 * @Description  地图 viewpager 的 adapter
 * @ClassName   GuideVpAdapter
 * @Author      Wongxd
 * @Time        2020/4/2 13:40
 */
internal class GuideVpAdapter(fm: FragmentManager, val tag: String) : FragmentStatePagerAdapter(fm) {
    /**
     * 当前位置的纬度
     */
    var lat = ""

    /**
     * 当前位置的经度
     */
    var lng = ""

    val mDataList: MutableList<GuideScenicListBean> = mutableListOf()


    fun setNewData(newData: List<GuideScenicListBean>) {
        mDataList.clear()
        mDataList.addAll(newData)
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        val bean = mDataList[position]

        when (bean.showTypeForDev) {

            ResourceType.CONTENT_TYPE_SCENERY -> {
                return GuideVpScenicFragment(tag, bean, position, count, lat, lng)
            }


            ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE, ResourceType.CONTENT_TYPE_VENUE, ResourceType.CONTENT_TYPE_HOTEL, ResourceType.CONTENT_TYPE_AGRITAINMENT, ResourceType.CONTENT_TYPE_SCENIC_SPOTS, ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE -> {
                return GuideVpSpotFragment(tag, bean, position, count, lat, lng)
            }


            ResourceType.CONTENT_TYPE_TOILET, ResourceType.CONTENT_TYPE_BUS_STOP, ResourceType.CONTENT_TYPE_SHOP_MALL -> {
                return if (bean.getImages().isNullOrEmpty())
                    GuideVpToiletNoImageFragment(tag, bean, position, count, lat, lng)
                else
                    GuideVpToiletFragment(tag, bean, position, count, lat, lng)
            }


            ResourceType.CONTENT_TYPE_PARKING -> {
                return if (bean.getImages().isNullOrEmpty())
                    GuideVpToiletNoImageFragment(tag, bean, position, count, lat, lng)
                else
                    GuideVpToiletFragment(tag, bean, position, count, lat, lng)
            }

            else -> {
                return GuideVpScenicFragment(tag, bean, position, count, lat, lng)
            }
        }
    }

    override fun getCount(): Int {
        return mDataList.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    fun getItemCurrPositionById(id: String): Int {
        var position = -1
        for (i in mDataList.indices) {
            var bean = mDataList[i]
            if (!bean.resourceId.isNullOrEmpty() && bean.resourceId == id) {
                position = i
                break
            }
        }
        return position
    }
}