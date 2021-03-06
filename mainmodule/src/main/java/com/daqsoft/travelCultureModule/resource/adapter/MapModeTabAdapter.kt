package com.daqsoft.travelCultureModule.resource.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemMapModeTabBinding
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.travelCultureModule.resource.bean.MapModeTabBean

/**
 * @Description
 * @ClassName   MapModeTabAdapter
 * @Author      luoyi
 * @Time        2020/4/23 17:45
 */
class MapModeTabAdapter : RecyclerViewAdapter<ItemMapModeTabBinding, MapModeTabBean>(R.layout.item_map_mode_tab) {

    var selectType: String = ResourceType.CONTENT_TYPE_AGRITAINMENT
    var onMapModeTabClickListener: OnMapModeTabClickListener? = null

    override fun setVariable(mBinding: ItemMapModeTabBinding, position: Int, item: MapModeTabBean) {
        mBinding.txtMapTabName.text = item.name
        if (item.resourceType == selectType) {
            mBinding.imgMapTabIcon.setImageResource(getSelectIcon(item.resourceType))
            mBinding.txtMapTabName.isSelected = true
        } else {
            mBinding.imgMapTabIcon.setImageResource(getUnSelectIcon(item.resourceType))
            mBinding.txtMapTabName.isSelected = false
        }
        mBinding.root.onNoDoubleClick {
            if (onMapModeTabClickListener != null) {
                if (selectType != item.resourceType) {
                    onMapModeTabClickListener?.onMapModeClick(item.resourceType, position, item.name)
                    selectType = item.resourceType
                    notifyDataSetChanged()
                }
            }
        }
    }

    public fun getItemPosition(type: String): Int {
        for (i in getData().indices) {
            if (getData()[i].resourceType == type) {
                return i
            }
        }
        return -1
    }

    public interface OnMapModeTabClickListener {
        fun onMapModeClick(resourceType: String, position: Int, name: String)
    }

    private fun getSelectIcon(resourceType: String): Int {
        return when (resourceType) {
            ResourceType.CONTENT_TYPE_SCENERY -> {
                //??????
                R.mipmap.map_tab_scenic_selected
            }
            ResourceType.CONTENT_TYPE_VENUE -> {
                //??????
                R.mipmap.map_tab_venue_selected
            }
            ResourceType.CONTENT_TYPE_HOTEL -> {
                //??????
                R.mipmap.map_tab_hotel_selected
            }
            ResourceType.CONTENT_TYPE_RESTAURANT -> {
                //??????
                R.mipmap.map_tab_food_selected
            }
            ResourceType.CONTENT_TYPE_COUNTRY -> {
                // ??????
                R.mipmap.map_tab_rural_selected
            }
            ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                // ?????????
                R.mipmap.map_tab_country_selected
            }
            ResourceType.CONTENT_TYPE_BUS_STOP -> {
                // ?????????
                R.mipmap.near_icon_bus_selected
            }
            ResourceType.CONTENT_TYPE_SHOP_MALL -> {
                // ?????????
                R.mipmap.near_icon_shopping_selected
            }
            ResourceType.CONTENT_TYPE_MEDICAL->{
                // ?????????
                R.mipmap.near_icon_hospital_selected
            }
            ResourceType.CONTENT_TYPE_ENTERTRAINMENT->{
                // ?????????
                R.mipmap.map_tab_entertainment_selected
            }
            ResourceType.CONTENT_TYPE_RESEARCH_BASE->{
                // ????????????
                R.mipmap.map_tab_study_selected
            }
            ResourceType.CONTENT_TYPE_SPECIALTY->{
                // ????????????
                R.mipmap.map_tab_specialty_selected
            }
            else -> {
                // ??????
                R.mipmap.map_tab_scenic_selected
            }
        }
    }

    private fun getUnSelectIcon(resourceType: String): Int {
        return when (resourceType) {
            ResourceType.CONTENT_TYPE_SCENERY -> {
                //??????
                R.mipmap.map_tab_scenic_normal
            }
            ResourceType.CONTENT_TYPE_VENUE -> {
                //??????
                R.mipmap.map_tab_venue_normal
            }
            ResourceType.CONTENT_TYPE_HOTEL -> {
                //??????
                R.mipmap.map_tab_hotel_normal
            }
            ResourceType.CONTENT_TYPE_RESTAURANT -> {
                //??????
                R.mipmap.map_tab_food_normal
            }
            ResourceType.CONTENT_TYPE_COUNTRY -> {
                // ??????
                R.mipmap.map_tab_rural_normal
            }
            ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                // ?????????
                R.mipmap.map_tab_house_normal
            }
            ResourceType.CONTENT_TYPE_BUS_STOP -> {
                // ?????????
                R.mipmap.near_icon_bus_normal
            }
            ResourceType.CONTENT_TYPE_SHOP_MALL -> {
                // ?????????
                R.mipmap.near_icon_shopping_normal
            }
            ResourceType.CONTENT_TYPE_MEDICAL->{
                // ?????????
                R.mipmap.near_icon_hospital_normal
            }
            ResourceType.CONTENT_TYPE_ENTERTRAINMENT->{
                // ????????????
                R.mipmap.map_tab_entertainment_normal
            }
            ResourceType.CONTENT_TYPE_RESEARCH_BASE->{
                // ????????????
                R.mipmap.map_tab_study_normal
            }
            ResourceType.CONTENT_TYPE_SPECIALTY->{
                // ????????????
                R.mipmap.map_tab_specialty_normal
            }
            else -> {
                // ??????
                R.mipmap.map_tab_scenic_normal
            }
        }
    }
}