package com.daqsoft.venuesmodule.activity.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.maps.model.LatLng
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.MapResBean
import com.daqsoft.provider.model.RecommendModel
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.adapter.VenueActivityAdapter
import com.daqsoft.venuesmodule.adapter.VenueRecRecAdapter
import com.daqsoft.venuesmodule.adapter.VenueRecTabAapter
import com.daqsoft.venuesmodule.databinding.IncludeVenueDetailsActivityBinding
import com.daqsoft.venuesmodule.databinding.IncludeVenueDetailsRecommendBinding

/**
 * @Description 周边推荐
 * @ClassName   VenueRecommendView
 * @Author      luoyi
 * @Time        2020/3/27 17:43
 */
class VenueRecommendView : FrameLayout, VenueRecTabAapter.OnItemClickListener {

    var mContext: Context? = null

    var binding: IncludeVenueDetailsRecommendBinding? = null
    /**
     *  场馆推荐tab适配器
     */
    var tabsAdapter: VenueRecTabAapter? = null

    /**
     * 景区数据
     */
    var datasSencerys: MutableList<MapResBean> = mutableListOf()

    /**
     * 酒店信息
     */
    var datasHotels: MutableList<MapResBean> = mutableListOf()
    /**
     * 美食信息
     */
    var datasRestaurant: MutableList<MapResBean> = mutableListOf()

    /**
     * 场馆信息
     */
    var datasVenues: MutableList<MapResBean> = mutableListOf()

    /**
     * 娱乐场所信息
     */
    var datasEnterTains: MutableList<MapResBean> = mutableListOf()
    /**
     * 自己的位置
     */
    var latLng: LatLng? = null
    /**
     * 适配器
     */
    var adapter: VenueRecRecAdapter? = null

    var onItemClickTabListener: OnItemClickTabListener? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.include_venue_details_recommend,
            this,
            false
        )
        addView(binding!!.root)
        tabsAdapter = VenueRecTabAapter(mContext!!)
        tabsAdapter!!.add(RecommendModel.recommendsVenue)
        tabsAdapter!!.onItemClickListener = this
        binding?.recyclerVenuesDetailsAroundLabel?.layoutManager = LinearLayoutManager(
            mContext!!, LinearLayoutManager.HORIZONTAL,
            false
        )
        binding?.recyclerVenuesDetailsAroundLabel?.adapter = tabsAdapter
        adapter = VenueRecRecAdapter(mContext!!)
        binding?.recyclerVenuesDetailsAround?.layoutManager = GridLayoutManager(mContext, 2)
        binding?.recyclerVenuesDetailsAround?.adapter = adapter
        binding?.recyclerVenuesDetailsAround?.isNestedScrollingEnabled = false
    }

    /**
     * 设置数据
     */
    fun setData(type: String?, datas: MutableList<MapResBean>?) {
        if (type != null) {
            when (type) {
                ResourceType.CONTENT_TYPE_SCENERY -> {
                    // 景区
                    if (!datas.isNullOrEmpty()) {
                        datasSencerys.clear()
                        datasSencerys.addAll(datas)
                    }
                }
                ResourceType.CONTENT_TYPE_HOTEL -> {
                    // 酒店
                    if (!datas.isNullOrEmpty()) {
                        datasHotels.clear()
                        datasHotels.addAll(datas)
                    }
                }
                ResourceType.CONTENT_TYPE_RESTAURANT -> {
                    // 美食
                    if (!datas.isNullOrEmpty()) {
                        datasRestaurant.clear()
                        datasRestaurant.addAll(datas)
                    }
                }
                ResourceType.CONTENT_TYPE_VENUE -> {
                    // 场馆
                    if (!datas.isNullOrEmpty()) {
                        datasVenues.clear()
                        datasVenues.addAll(datas)
                    }
                }
                ResourceType.CONTENT_TYPE_ENTERTRAINMENT -> {
                    // 场馆
                    if (!datas.isNullOrEmpty()) {
                        datasEnterTains.clear()
                        datasEnterTains.addAll(datas)
                    }
                }
            }
            updateUi(type)
        }
    }

    fun updateUi(type: String) {
        if (tabsAdapter != null) {
            var selectType = RecommendModel.recommendsVenue[tabsAdapter!!.selectPos].value
            if (type == selectType) {
                when (type) {
                    ResourceType.CONTENT_TYPE_SCENERY -> {
                        // 景区
                        adapter?.clear()
                        adapter?.add(datasSencerys)
                    }
                    ResourceType.CONTENT_TYPE_HOTEL -> {
                        // 酒店
                        adapter?.clear()
                        adapter?.add(datasHotels)
                    }
                    ResourceType.CONTENT_TYPE_RESTAURANT -> {
                        // 美食
                        adapter?.clear()
                        adapter?.add(datasRestaurant)
                    }
                    ResourceType.CONTENT_TYPE_VENUE -> {
                        // 场馆
                        adapter?.clear()
                        adapter?.add(datasVenues)
                    }
                    ResourceType.CONTENT_TYPE_ENTERTRAINMENT -> {
                        // 场馆
                        adapter?.clear()
                        adapter?.add(datasEnterTains)
                    }
                }
            }
        }
    }

    /**
     * 设置定位
     */
    fun setLocation(lat: LatLng) {
        latLng = lat
        if (adapter != null) {
            adapter?.latLng = latLng
//            adapter?.notifyItemRangeChanged(0, adapter!!.itemCount, "updateLocationInfo")
        }
    }

    override fun onItemClick(pos: Int) {
        var selectType = RecommendModel.recommendsVenue[tabsAdapter!!.selectPos].value
        when (selectType) {
            ResourceType.CONTENT_TYPE_SCENERY -> {
                // 景区
                if (!datasSencerys.isNullOrEmpty()) {
                    adapter?.clear()
                    adapter?.add(datasSencerys)
                } else {
                    onItemClickTabListener?.getMapResourceRecommend(selectType)
                }
            }
            ResourceType.CONTENT_TYPE_HOTEL -> {
                // 酒店
                if (!datasHotels.isNullOrEmpty()) {
                    adapter?.clear()
                    adapter?.add(datasHotels)
                } else {
                    onItemClickTabListener?.getMapResourceRecommend(selectType)
                }
            }
            ResourceType.CONTENT_TYPE_RESTAURANT -> {
                // 美食
                if (!datasRestaurant.isNullOrEmpty()) {
                    adapter?.clear()
                    adapter?.add(datasRestaurant)
                } else {
                    onItemClickTabListener?.getMapResourceRecommend(selectType)
                }
            }
            ResourceType.CONTENT_TYPE_VENUE -> {
                // 场馆
                if (!datasVenues.isNullOrEmpty()) {
                    adapter?.clear()
                    adapter?.add(datasVenues)
                } else {
                    onItemClickTabListener?.getMapResourceRecommend(selectType)
                }
            }
            ResourceType.CONTENT_TYPE_ENTERTRAINMENT -> {
                // 场馆
                if (!datasEnterTains.isNullOrEmpty()) {
                    adapter?.clear()
                    adapter?.add(datasEnterTains)
                } else {
                    onItemClickTabListener?.getMapResourceRecommend(selectType)
                }
            }
        }
    }

    interface OnItemClickTabListener {
        fun getMapResourceRecommend(type: String)
    }

}