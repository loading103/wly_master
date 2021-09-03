package com.daqsoft.guidemodule.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.guidemodule.R
import com.daqsoft.guidemodule.bean.GuideResouceBean
import com.daqsoft.guidemodule.databinding.ItemGuideTourResourceBinding
import com.daqsoft.provider.base.ResourceType

/**
 * @Description
 * @ClassName   GuideTourResourceAdapter
 * @Author      luoyi
 * @Time        2020/11/23 9:56
 */
class GuideTourResourceAdapter : RecyclerViewAdapter<ItemGuideTourResourceBinding, GuideResouceBean>(R.layout.item_guide_tour_resource) {

    var onItemclickListener: OnItemClickListener? = null

    // 默认选择第一个
    var selectPos: Int = 0

    override fun setVariable(mBinding: ItemGuideTourResourceBinding, position: Int, item: GuideResouceBean) {
        item?.let {
//              mBinding.imgGuideTourResource
            var name: String? = item.name
            var drawable: Drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_scenic)
            when (item.resourceType) {
                ResourceType.CONTENT_TYPE_SCENERY -> {
                    // 景区
                    name = getResourceName("景区", name)
                    drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_scenic)
                }
                ResourceType.CONTENT_TYPE_TOILET -> {
                    // 厕所
                    name = getResourceName("厕所", name)
                    drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_toilet)
                }
                ResourceType.CONTENT_TYPE_PARKING -> {
                    // 停车场
                    name = getResourceName("停车场", name)
                    drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_park)
                }
                ResourceType.CONTENT_TYPE_SHOP_MALL -> {
                    // 购物点
                    name = getResourceName("购物点", name)
                    drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_shopping)
                }
                ResourceType.CONTENT_TYPE_BUS_STOP -> {
                    // 乘车点
                    name = getResourceName("乘车点", name)
                    drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_bus)
                }
                ResourceType.CONTENT_TYPE_VENUE -> {
                    // 博物馆
                    name = getResourceName("博物馆", name)
                    drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_musume)
                    if (!item.value.isNullOrEmpty()) {
                        when (item.value) {
                            "gallery" -> { // 美术馆
                                drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_paint)
                            }
                            "cultureCenter" -> { // 文化馆
                                drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_culture)
                            }
                            "artGallery" -> {//艺术馆
                                drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_arts)
                            }
                            "library" -> { // 图书馆
                                drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_library)
                            }
                            "troupe" -> {// 剧院/剧团
                                drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_theatre)
                            }
                            "museum" -> {// 博物馆
                                drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_musume)
                            }
                            "culturalStation" -> { // 文化站/文化活动中心
                                drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_culture_station)
                            }
                            "scienicMuseum" -> {// 科技馆
                                drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_science)
                            }
                            "gymnasium" -> { //体育馆
                                drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_sports)
                            }
                            "memorial" -> {
                                drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_jinian)
                            }
                            "heritageCenter" -> {
                                drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_feiyibaohu)
                            }
                        }
                    }
                }
                ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE -> {
                    // 非遗基地
                    name = getResourceName("非遗基地", name)
                    drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_legacy)
                }
                ResourceType.CONTENT_TYPE_HOTEL -> {
                    // 酒店
                    name = getResourceName("酒店", name)
                    drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_hotel)
                }
                ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                    // 农家乐
                    name = getResourceName("农家乐", name)
                    drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_house)
                }
                ResourceType.CONTENT_TYPE_RESTAURANT -> {
                    // 餐饮
                    name = getResourceName("餐饮", name)
                    drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_food)
                }
                ResourceType.CONTENT_TYPE_LINE -> {
                    // 线路
                    name = getResourceName("线路", name)
                    drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_line)
                }
                ResourceType.CONTENT_TYPE_SCENIC_SPOTS -> {
                    name = getResourceName("景点", name)
                    drawable = mBinding.root.context.getDrawable(R.drawable.guide_map_sidebar_icon_spot)
                }
                ResourceType.CONTENT_TYPE_ENTERTRAINMENT-> {
                    name = getResourceName("娱乐场所", name)
                    drawable = mBinding.root.context.getDrawable(R.drawable.map_tab_entertainment_xj_selected)
                }
                else -> {
                    // 线路
                }

            }
            mBinding.tvGuideTourResourceName.text = "$name"
            mBinding.imgGuideTourResource.background = drawable
            if (position == selectPos) {
                mBinding.root.setBackgroundColor(ContextCompat.getColor(mBinding.root.context, R.color.f5))
            } else {
                mBinding.root.setBackgroundColor(ContextCompat.getColor(mBinding.root.context, R.color.white))
            }
        }
        mBinding.root.onNoDoubleClick {
            if (item != null) {
                onItemclickListener?.onItemClick(item)
                selectPos = position
                notifyItemRangeChanged(0, getData().size, "update_select_pos")
            }
        }
    }

    override fun payloadUpdateUi(mBinding: ItemGuideTourResourceBinding, position: Int, payloads: MutableList<Any>) {
        super.payloadUpdateUi(mBinding, position, payloads)
        if (payloads[0] == "update_select_pos") {
            if (position == selectPos) {
                mBinding.root.setBackgroundColor(ContextCompat.getColor(mBinding.root.context, R.color.f5))
            } else {
                mBinding.root.setBackgroundColor(ContextCompat.getColor(mBinding.root.context, R.color.white))
            }
        }
    }

    private fun getResourceName(s: String, name: String?): String? {
        return if (name.isNullOrEmpty()) {
            s
        } else {
            name
        }
    }

    interface OnItemClickListener {
        fun onItemClick(resource: GuideResouceBean)
        fun onItemSelect(position: Int)
    }

    public fun setSelectResouceType(resourceType: String, venueTypeValue: String? = "") {
        for (i in getData().indices) {
            var bean = getData()[i]
            if (bean.resourceType == resourceType) {
                if (bean.resourceType == ResourceType.CONTENT_TYPE_VENUE) {
                    if (venueTypeValue != null && venueTypeValue == bean.value) {
                        updateSelectData(i, bean)
                        break
                    }
                } else {
                    updateSelectData(i, bean)
                    break
                }
            }
        }
    }

    private fun updateSelectData(i: Int, bean: GuideResouceBean) {
        selectPos = i
        notifyDataSetChanged()
        onItemclickListener?.onItemClick(bean)
        return
    }
}