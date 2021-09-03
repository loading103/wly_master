package com.daqsoft.travelCultureModule.story

import android.content.Intent
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityStoryAddressBinding
import com.daqsoft.mainmodule.databinding.ItemMainAddressBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.home.bean.ItemAddressBean
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.travelCultureModule.story.vm.StoryAddressModel

/**
 * @Description 时光的添加地点页面
 * @ClassName   StoryAddressActivity
 * @Author      HuangXi
 * @Time        2020/2/19 13:47
 */
@Route(path = MainARouterPath.MAIN_STORY_ADDRESS)
class StoryAddressActivity : TitleBarActivity<ActivityStoryAddressBinding, StoryAddressModel>() {


    @JvmField
    @Autowired
    var resourceId = ""

    /**
     * 每页数量
     */
    var pageSize = 6

    /**
     * 关键字
     */
    var keyword = ""

    /**
     * 经纬度
     */
    var longitude: Double = 0.0

    /**
     * 纬度
     */
    var latitude: Double = 0.0

    /**
     * 距离
     */
    var distance = 1000000

    /**
     * 当前页码
     */
    var currPage = 1

    override fun getLayout(): Int = R.layout.activity_story_address

    override fun setTitle(): String = resources.getString(R.string.home_story_address_title)

    override fun injectVm(): Class<StoryAddressModel> = StoryAddressModel::class.java

    var mAdapter = object : RecyclerViewAdapter<ItemMainAddressBinding, ItemAddressBean>(
        R.layout.item_main_address
    ) {
        override fun setVariable(
            mBinding: ItemMainAddressBinding,
            position: Int,
            item: ItemAddressBean
        ) {
            mBinding.item = item
            if (!item.name.isNullOrEmpty()) {
                mBinding.name = item.name.replace("<em>", "").replace("</em>", "")
            }
            var drawable: Drawable? = null
            when (item.resourceType) {
                ResourceType.CONTENT_TYPE_SCENIC_SPOTS -> drawable =
                    ResourcesCompat.getDrawable(resources, R.mipmap.add_attractions, null)!!
                ResourceType.CONTENT_TYPE_VENUE -> drawable =
                    ResourcesCompat.getDrawable(resources, R.mipmap.add_cultural_venues, null)!!
                ResourceType.CONTENT_TYPE_HOTEL -> drawable =
                    ResourcesCompat.getDrawable(resources, R.mipmap.add_hotel, null)!!
                ResourceType.CONTENT_TYPE_RESTAURANT -> drawable =
                    ResourcesCompat.getDrawable(resources, R.mipmap.add_food, null)!!
                ResourceType.CONTENT_TYPE_SCENERY -> drawable =
                    ResourcesCompat.getDrawable(resources, R.mipmap.add_scenic, null)!!
                ResourceType.CONTENT_TYPE_SITE_INDEX -> drawable =
                    ResourcesCompat.getDrawable(resources, R.mipmap.add_destination, null)!!
                ResourceType.CONTENT_TYPE_COUNTRY -> drawable =
                    ResourcesCompat.getDrawable(resources, R.mipmap.add_country, null)!!
                ResourceType.CONTENT_TYPE_RURAL_SPOTS -> drawable =
                    ResourcesCompat.getDrawable(resources, R.mipmap.add_country_scenery, null)!!
                ResourceType.CONTENT_TYPE_AGRITAINMENT -> drawable =
                    ResourcesCompat.getDrawable(resources, R.mipmap.add_house, null)!!
                ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE -> drawable =
                    ResourcesCompat.getDrawable(resources, R.mipmap.add_legacy_history, null)!!
                ResourceType.CONTENT_TYPE_HERITAGE_TEACHING_BASE -> drawable =
                    ResourcesCompat.getDrawable(resources, R.mipmap.add_legacy_study, null)!!
                ResourceType.CONTENT_TYPE_ENTERTRAINMENT -> drawable =
                    ResourcesCompat.getDrawable(resources, R.mipmap.add_entertainment, null)!!
                ResourceType.CONTENT_TYPE_SPECIALTY -> drawable =
                    ResourcesCompat.getDrawable(resources, R.mipmap.add_speciality, null)!!
                ResourceType.CONTENT_TYPE_RESEARCH_BASE -> drawable =
                    ResourcesCompat.getDrawable(resources, R.mipmap.add_study_base, null)!!
            }
            mBinding.ivItemAddressIcon.setImageDrawable(drawable)
            if (item.status) {
                mBinding.ivItemAddressStatus.visibility = View.VISIBLE
            } else {
                mBinding.ivItemAddressStatus.visibility = View.GONE
            }
            mBinding.root.setOnClickListener {
                var intent = Intent()
                intent.putExtra("address", item)
                setResult(0x0004, intent)
                finish()
            }
        }

    }

    override fun initView() {
        mBinding.recyclerList.apply {
            layoutManager = LinearLayoutManager(
                this@StoryAddressActivity, LinearLayoutManager
                    .VERTICAL, false
            )
            adapter = mAdapter
        }
        mBinding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrEmpty()) {
                    keyword = p0.toString().trim()
                    currPage = 1
                    mModel.getSearchAround(
                        pageSize,
                        keyword,
                        longitude,
                        latitude,
                        distance,
                        currPage
                    )
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        mAdapter.setOnLoadMoreListener {
            currPage++
            mModel.getSearchAround(pageSize, keyword, longitude, latitude, distance, currPage)
        }
        initSelfLocation()
        mBinding.tvShowSelfAddress.setOnClickListener {
            if (!mBinding.tvShowSelfAddress.isSelected) {
                mBinding.tvSelfAddress.visibility = View.GONE
                mBinding.tvShowSelfAddress.setText("显示所在位置")
                mBinding.tvShowSelfAddress.isSelected = true
            } else {
                mBinding.tvSelfAddress.visibility = View.VISIBLE
                mBinding.tvShowSelfAddress.setText("不显示所在位置")
                mBinding.tvShowSelfAddress.isSelected = false
            }

        }
        mBinding.tvSelfAddress.setOnClickListener {
            initSelfLocation()
        }
    }

    override fun initData() {
        mBinding.vm = mModel
    }

    override fun onDestroy() {
        super.onDestroy()
        GaoDeLocation.getInstance().release()
    }

    /**
     * 获取当前位置
     */
    fun initSelfLocation() {
        GaoDeLocation.getInstance().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {
            override fun onResult(
                adCode: String?,
                result: String?,
                lat: Double,
                lon: Double,
                adcode: String?
            ) {
                latitude = lat
                longitude = lon
                mBinding.tvSelfAddress.setText("我的位置：" + result.toString())
                mModel.getSearchAround(pageSize, keyword, longitude, latitude, distance, currPage)
            }

            override fun onError(errorMsg: String?) {
                latitude = 0.0
                longitude = 0.0
                mBinding.tvSelfAddress.setText(resources.getString(R.string.address_default))
                mModel.getSearchAround(pageSize, keyword, longitude, latitude, distance, currPage)
            }

        })
    }

    override fun notifyData() {
        super.notifyData()
        mModel.datas.observe(this, Observer {
            pageDeal(currPage, it, mAdapter)
            if (it != null && it.code == 0 && it.datas != null && it.datas!!.size > 0) {
                for (item in it.datas!!) {
                    item.status = false
                    if (item.resourceId.toString().equals(resourceId)) {
                        item.status = true
                    }
                }
                mAdapter.add(it.datas!!)
//                mAdapter.notifyDataSetChanged()
            }

        })
    }

    /**
     * 列表数据页码处理
     *
     * @param page     当前页
     * @param response 返回数据体
     * @param adapter  适配器
     */
    private fun pageDeal(
        page: Int?,
        response: BaseResponse<*>,
        adapter: RecyclerViewAdapter<*, *>
    ) {
        if (page == null) {
            return
        }
        if (page == 1) {
            adapter.clear()
        }
        if (response.page == null) {
            adapter.loadEnd()
            return
        }
        if (response.page!!.currPage < response.page!!.totalPage) {
            adapter.loadComplete()
        } else {
            adapter.loadEnd()
        }
    }
}