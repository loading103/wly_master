package com.daqsoft.provider.businessview.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.maps.model.LatLng
import com.daqsoft.provider.R
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.MapResBean
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.businessview.adapter.ProviderRecRecAdapter
import com.daqsoft.provider.businessview.adapter.ProviderRecTabAdapter
import com.daqsoft.provider.databinding.LayoutProviderDetailsRecommendBinding
import com.daqsoft.provider.model.RecommendModel

/**
 * @Description 周边推荐
 * @ClassName   ProviderRecommendView
 * @Author      luoyi
 * @Time        2020/4/1 19:14
 */
class ProviderRecommendView : FrameLayout, ProviderRecTabAdapter.OnItemClickListener {

    var mContext: Context? = null

    var binding: LayoutProviderDetailsRecommendBinding? = null
    /**
     *  场馆推荐tab适配器
     */
    var tabsAdapter: ProviderRecTabAdapter? = null

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
     * 娱乐场所信息
     */
    var specialTains: MutableList<MapResBean> = mutableListOf()
    /**
     * 自己的位置
     */
    var latLng: LatLng? = null
    /**
     * 适配器
     */
    var adapter: ProviderRecRecAdapter? = null

    var onItemClickTabListener: OnItemClickTabListener? = null
    /**
     * 0 景区 1 酒店 2 美食 3 场馆
     */
    var defaultType: Int = 0

    /**
     * 当前推荐tags
     */
    var currentRecommendTags: MutableList<ValueKeyBean> = mutableListOf()

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        var typedArray = context?.obtainStyledAttributes(attrs, R.styleable.RecommendView)
        var type: Int? = typedArray?.getInt(R.styleable.RecommendView_defaultType, 0)
        if (type != null) {
            defaultType = type
        }
        typedArray?.recycle()
        this.mContext = context
        initView()

    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.layout_provider_details_recommend,
            this,
            false
        )
        addView(binding!!.root)
        tabsAdapter = ProviderRecTabAdapter(mContext!!)
        currentRecommendTags.clear()
        currentRecommendTags.addAll(getRecommendTabs())
        tabsAdapter!!.add(currentRecommendTags)
        tabsAdapter!!.onItemClickListener = this
        binding?.recyclerProviderDetailsAroundLabel?.layoutManager = LinearLayoutManager(
            mContext!!, LinearLayoutManager.HORIZONTAL,
            false
        )
        binding?.recyclerProviderDetailsAroundLabel?.adapter = tabsAdapter
        adapter = ProviderRecRecAdapter(mContext!!)
        binding?.recyclerProviderDetailsAround?.layoutManager = GridLayoutManager(mContext, 2)
        binding?.recyclerProviderDetailsAround?.adapter = adapter
        binding?.recyclerProviderDetailsAround?.isNestedScrollingEnabled = false
    }

    private fun getRecommendTabs(): MutableList<ValueKeyBean> {
        return when (defaultType) {
            0 -> {
                RecommendModel.recommendsScenic
            }
            1 -> {
                RecommendModel.recommendsHotel
            }
            2 -> {
                RecommendModel.recommendsFood
            }
            3 -> {
                RecommendModel.recommendsVenue
            }
            else -> {
                RecommendModel.recommendsScenic
            }
        }
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
                    // 娱乐场所
                    if (!datas.isNullOrEmpty()) {
                        datasEnterTains.clear()
                        datasEnterTains.addAll(datas)
                    }
                }
                ResourceType.CONTENT_TYPE_SPECIALTY -> {
                    // 娱乐场所
                    if (!datas.isNullOrEmpty()) {
                        specialTains.clear()
                        specialTains.addAll(datas)
                    }
                }
            }
            updateUi(type)
        }
    }

    fun updateUi(type: String) {
        if (tabsAdapter != null) {
            var selectType = currentRecommendTags[tabsAdapter!!.selectPos].value
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
                    ResourceType.CONTENT_TYPE_SPECIALTY -> {
                        // 场馆
                        adapter?.clear()
                        adapter?.add(specialTains)
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
        }
    }

    override fun onItemClick(pos: Int) {
        var selectType = currentRecommendTags[tabsAdapter!!.selectPos].value
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
                // 娱乐场所
                if (!datasEnterTains.isNullOrEmpty()) {
                    adapter?.clear()
                    adapter?.add(datasEnterTains)
                } else {
                    onItemClickTabListener?.getMapResourceRecommend(selectType)
                }
            }
            ResourceType.CONTENT_TYPE_SPECIALTY -> {
                // 娱乐场所
                if (!specialTains.isNullOrEmpty()) {
                    adapter?.clear()
                    adapter?.add(specialTains)
                } else {
                    onItemClickTabListener?.getMapResourceRecommend(selectType)
                }
            }
        }
    }

    interface OnItemClickTabListener {
        fun getMapResourceRecommend(type: String)
    }

    fun clear() {
        datasHotels?.clear()
        datasRestaurant?.clear()
        datasSencerys?.clear()
        datasVenues?.clear()
        datasEnterTains?.clear()
        specialTains?.clear()
        adapter = null
    }
}