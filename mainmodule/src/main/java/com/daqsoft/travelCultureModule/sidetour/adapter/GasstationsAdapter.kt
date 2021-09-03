package com.daqsoft.travelCultureModule.sidetour.adapter

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.daqsoft.provider.bean.MapResBean
import com.daqsoft.travelCultureModule.sidetour.fragment.GasstationFragment

/**
 * @Author：      邓益千
 * @Create by：   2020/6/24 10:41
 * @Description：加油站Adapter
 */
class GasstationsAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    /** 当前位置的纬度 */
    var lat = 0.0

    /** 当前位置的经度 */
    var lon = 0.0
    var isService: Boolean = false
    private var dataList: MutableList<MapResBean>? = null

    fun setDataList(dataList: MutableList<MapResBean>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }
    fun getDataList():MutableList<MapResBean>?{
        return dataList
    }

    fun getData(position: Int): MapResBean? {
        return if (dataList.isNullOrEmpty()) null else dataList!![position]
    }

    override fun getCount(): Int = if (dataList.isNullOrEmpty()) 0 else dataList!!.size

    override fun getItem(position: Int): Fragment {
        val bean = dataList!![position]
        val args = Bundle().apply {
            putInt("id", bean.id)
            if (!bean.longitude.isNullOrEmpty()) {
                putDouble("lon", bean.longitude!!.toDouble())
            }
            if (!bean.latitude.isNullOrEmpty()) {
                putDouble("lat", bean.latitude!!.toDouble())
            }

            putDouble("startLon", lon)
            putDouble("startLat", lat)
            putInt(GasstationFragment.CURRENT, (position + 1))
            putInt(GasstationFragment.TOTAL, dataList!!.size)
            putString(GasstationFragment.SITE_NAME, bean.name)
            putString(GasstationFragment.ADDRESS, bean.address)
            putBoolean("isService", isService)
        }
        return GasstationFragment.getInstance(args)
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}