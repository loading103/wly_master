package com.daqsoft.travelCultureModule.country.ui

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityCountryHappinessMoreBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.view.ListPopupWindow
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import com.daqsoft.travelCultureModule.country.CONTENT_TYPE_AGRITAINMENT
import com.daqsoft.travelCultureModule.country.adapter.CountryHappinessMoreAdapter
import com.daqsoft.travelCultureModule.country.bean.AgritainmentBean
import com.daqsoft.travelCultureModule.country.bean.ResourceTypeLabel
import com.daqsoft.travelCultureModule.country.bean.ValueKeyBean
import com.daqsoft.travelCultureModule.country.model.CountryHapVideoViewModel
import com.daqsoft.travelCultureModule.country.view.TypeSelectPopupWindow
import com.jakewharton.rxbinding2.view.RxView
import org.jetbrains.anko.support.v4.onRefresh
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * desc :农家乐更多列表
 * @author 江云仙
 * @date 2020/4/13 17:43
 */
@Route(path = ARouterPath.CountryModule.COUNTRY_HAPPINESS_LIST)
class CountryHappinessMoreActivity : TitleBarActivity<ActivityCountryHappinessMoreBinding, CountryHapVideoViewModel>() {
    var countryHapAdapter: CountryHappinessMoreAdapter? = null
    //当前经纬度
    var currentLat = ""
    var currentLon = ""
    /**
     * 地区选择弹窗窗口
     */
    private var areaListPopWindow: AreaSelectPopupWindow? = null
    /**
     * 类型选择弹窗
     */
    private var typeListPopWindow: TypeSelectPopupWindow? = null
    val sorts = mModel.sorts
    //排序弹窗
    var sortPopupWindow: ListPopupWindow<Any>? = null

    override fun getLayout(): Int = R.layout.activity_country_happiness_more

    override fun setTitle(): String {
        return "农家乐"
    }

    override fun injectVm(): Class<CountryHapVideoViewModel> = CountryHapVideoViewModel::class.java

    override fun initView() {
        mBinding.recyCountryHappiness.visibility=View.GONE
        countryHapAdapter = CountryHappinessMoreAdapter(mModel)
        mBinding.recyCountryHappiness.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.recyCountryHappiness.adapter = countryHapAdapter
        initViewModel()
        initModel()
        initClick()
        location()
    }

    /**
     *初始化排序窗口
     */
    private fun initViewModel() {
        sortPopupWindow = ListPopupWindow.getInstance(mBinding.tvSort, sorts) { item ->
            kotlin.run {
                mBinding.tvSort.text = (item as ValueKeyBean).name
                mModel.sortType = item.value
                showLoadingDialog()
                mModel.getAgritainmentList()
            }
        }
        if (typeListPopWindow == null) {
            typeListPopWindow = TypeSelectPopupWindow.getInstance(this, false, object : TypeSelectPopupWindow.WindowDataBack {
                override fun select(region: ResourceTypeLabel?, firstPos: Int) {
                    mModel.mCurrPage = 1
                    mModel.type = region!!.id
                    showLoadingDialog()
                    mModel.getAgritainmentList()
                }

                override fun reset() {
                    mModel.type = ""
                    mModel.mCurrPage = 1
                    showLoadingDialog()
                    mModel.getAgritainmentList()
                }
            })
            typeListPopWindow!!.firstData = mModel.types
        }
    }

    /**
     * 定位
     */
    fun location() {
        GaoDeLocation.getInstance().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {
            override fun onResult(
                adCode: String, result: String, lat_: Double,
                lon_: Double, adcode: String
            ) {
                try {
                    var selfLocation = LatLng(lat_, lon_)
                    countryHapAdapter?.selfLocation = selfLocation
                    mModel?.lat = lat_.toString()
                    mModel?.lon = lon_.toString()
                } catch (e: Exception) {
                }

            }

            override fun onError(errormsg: String) {

            }
        })
    }

    /**
     *点击事件
     */
    @SuppressLint("CheckResult")
    private fun initClick() {
        //点击地区
        RxView.clicks(mBinding.tvArea).throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (areaListPopWindow != null) {
                    areaListPopWindow!!.show(mBinding.tvArea)
                }
            }
        //点击类型
        mBinding.tvType.onNoDoubleClick {
            typeListPopWindow?.show(mBinding.tvType)
        }
        //点击排序
        mBinding.tvSort.onNoDoubleClick {
            sortPopupWindow?.show()
        }
        //点击地图
        mBinding.ivMap.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SCENIC_LIST_MAP)
                .withInt("mSelectTabPos", 3)
                .withString("mSelectType", CONTENT_TYPE_AGRITAINMENT)
                .navigation()
        }
        //上拉刷新
        mBinding.swprefreshCountryHappiness.setOnRefreshListener {
//            mBinding.swprefreshCountryHappiness.isRefreshing = true
            mModel.mCurrPage = 1
            mModel.getAgritainmentList()
        }
        //下拉加载
        countryHapAdapter?.setOnLoadMoreListener {
            mModel.mCurrPage++
            mModel.getAgritainmentList()
        }
        //点击搜素
        mBinding.txtSearchCountryHappiness.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }
    }

    /**
     *初始化数据
     */
    private fun initModel() {
        // 地区
        mModel.areas.observe(this, Observer {
            if (areaListPopWindow == null) {
                it.add(0, ChildRegion("", "全部", "", "", emptyList()))
                areaListPopWindow =
                    AreaSelectPopupWindow.getInstance(
                        BaseApplication.context, false
                    ) { item ->
                        mModel.areaSiteSwitch = item.siteId
//                        mModel.region = item.region
                        mModel.mCurrPage = 1
                        showLoadingDialog()
                        mBinding.tvArea.text = item.name
                        mBinding.recyCountryHappiness.visibility = View.GONE
                        mModel.getAgritainmentList()
                    }
                areaListPopWindow!!.firstData = it
                var temp: MutableList<ChildRegion> = mutableListOf()
                temp.add(0, ChildRegion("", "不限", "", "", emptyList()))
                areaListPopWindow!!.secondData = ArrayList(temp)
            }
        })

        // 农家乐类型
        mModel.countryHappinessTypes.observe(this, Observer {
            try {
                val temp: MutableList<ResourceTypeLabel> = mutableListOf()
                temp.add(0, ResourceTypeLabel("", "", "", "", "不限"))
                if (!it.isNullOrEmpty()) {
                    temp.addAll(it)
                    typeListPopWindow!!.firstData[0]!!.childList = it
                }
                typeListPopWindow?.secondData = temp

            } catch (e: Exception) {
            }
        })

        // 农家乐列表
        mModel.agritainment.observe(this, Observer {
            pageDealed(it)
        })

        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
    }

    /**
     *数据处理
     */
    private fun pageDealed(it: MutableList<AgritainmentBean>?) {
        dissMissLoadingDialog()
        if (!mBinding.recyCountryHappiness.isVisible) {
            mBinding.recyCountryHappiness.visibility = View.VISIBLE
        }
//        mBinding.swprefreshCountryHappiness.isRefreshing = false
        mBinding.swprefreshCountryHappiness.finishRefresh()
        if (mModel.mCurrPage == 1) {
            mBinding.recyCountryHappiness.smoothScrollToPosition(0)
            countryHapAdapter!!.clear()
        }
        if (!it.isNullOrEmpty()) {
            countryHapAdapter!!.add(it!!)
        }
        if (it.isNullOrEmpty() || it.size < mModel.mPageSize) {
            countryHapAdapter?.loadEnd()
        } else {
            countryHapAdapter?.loadComplete()
        }
    }

    override fun initData() {
        showLoadingDialog()
        //获取地区数据
        mModel.getChildRegions()
        //农家乐类型数据
        mModel.getCountryHappinessTypes()
        //农家乐集合数据
        mModel.getAgritainmentList()
    }

    override fun onDestroy() {
        super.onDestroy()
        GaoDeLocation.getInstance().release()
    }
}
