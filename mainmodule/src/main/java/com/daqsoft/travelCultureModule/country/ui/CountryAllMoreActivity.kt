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
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityCountryHappinessMoreBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType.CONTENT_TYPE_COUNTRY
import com.daqsoft.provider.bean.CountryListBean
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.view.ListPopupWindow
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import com.daqsoft.travelCultureModule.country.CONTENT_TYPE_AGRITAINMENT
import  com.daqsoft.travelCultureModule.country.bean.ValueKeyBean
import com.daqsoft.travelCultureModule.country.adapter.CountryAllMoreAdapter
import com.daqsoft.travelCultureModule.country.model.CountryAllMoreViewModel
import com.jakewharton.rxbinding2.view.RxView
import org.jetbrains.anko.support.v4.onRefresh
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * 全部乡村列表页面
 */
@Route(path = ARouterPath.CountryModule.COUNTRY_ALL_MORE_ACTIVITY)
class CountryAllMoreActivity() :
    TitleBarActivity<ActivityCountryHappinessMoreBinding, CountryAllMoreViewModel>() {
    // 全部乡村列表适配器
    var countryAllMoreAdapter: CountryAllMoreAdapter? = null

    // 地区选择弹窗窗口
    private var areaListPopWindow: AreaSelectPopupWindow? = null

    // 排序弹窗
    var sortPopupWindow: ListPopupWindow<Any>? = null
    val sorts = mModel.sorts
    override fun getLayout(): Int = R.layout.activity_country_happiness_more

    override fun setTitle(): String = getString(R.string.country_all)

    override fun injectVm(): Class<CountryAllMoreViewModel> = CountryAllMoreViewModel::class.java

    override fun initView() {
        showLoadingDialog()
        initAdapter()
        initModel()
        initClick()
        location()
    }

    private fun initAdapter() {
        mBinding.recyCountryHappiness.visibility = View.GONE
        countryAllMoreAdapter = CountryAllMoreAdapter(this)
        mBinding.recyCountryHappiness.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.recyCountryHappiness.adapter = countryAllMoreAdapter
    }

    /**
     * 初始化数据
     */
    private fun initModel() {
        // 地区
        mModel.areas.observe(this, Observer {
            if (areaListPopWindow == null) {
                it.add(0, ChildRegion("", "全部", "", "", emptyList()))
                areaListPopWindow =
                    AreaSelectPopupWindow.getInstance(BaseApplication.context, false) { item ->
                        mModel.areaSiteSwitch = item.siteId
                        mModel.region = item.region
                        mModel.mCurrPage = 1
                        showLoadingDialog()
                        mBinding.tvArea.text = item.name
                        mBinding.recyCountryHappiness.visibility = View.GONE
                        mModel.getCountryAllList()
                    }
                areaListPopWindow?.firstData = it
                val temp: MutableList<ChildRegion> = mutableListOf()
                temp.add(0, ChildRegion("", "不限", "", "", emptyList()))
                areaListPopWindow?.secondData = ArrayList(temp)
            }
        })

        // 类型
        mBinding.tvType.visibility = View.GONE

        // 排序
        sortPopupWindow = ListPopupWindow.getInstance(mBinding.tvSort, sorts) { item ->
            kotlin.run {
                mBinding.tvSort.text = (item as ValueKeyBean).name
                mModel.sortType = item.value
                showLoadingDialog()
                mModel.getCountryAllList()
            }
        }

        // 农家乐列表
        mModel.countryList.observe(this, Observer {
            pageDealed(it)
        })

        // 收藏
        mModel.collectLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            countryAllMoreAdapter?.notifyCollectStatus(it, true)
        })

        // 取消收藏
        mModel.canceCollectLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            countryAllMoreAdapter?.notifyCollectStatus(it, false)
        })

        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
    }

    /**
     * 点击事件
     */
    @SuppressLint("CheckResult")
    private fun initClick() {
        // 点击地区
        RxView.clicks(mBinding.tvArea).throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (areaListPopWindow != null) {
                    areaListPopWindow?.show(mBinding.tvArea)
                }
            }
        // 点击排序
        mBinding.tvSort.onNoDoubleClick {
            sortPopupWindow?.show()
        }
        // 点击地图
        mBinding.ivMap.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SCENIC_LIST_MAP)
                .withString("mSelectType", CONTENT_TYPE_COUNTRY)
                .navigation()
        }
        // 上拉刷新
        mBinding.swprefreshCountryHappiness.setOnRefreshListener {
//            mBinding.swprefreshCountryHappiness.isRefreshing = true
            mModel.mCurrPage = 1
            mModel.getCountryAllList()
        }
        // 下拉加载
        countryAllMoreAdapter?.setOnLoadMoreListener {
            mModel.mCurrPage++
            mModel.getCountryAllList()
        }
        // 点击搜素
        mBinding.txtSearchCountryHappiness.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }

        countryAllMoreAdapter?.onScenicLsItemClickListener =
            object : CountryAllMoreAdapter.OnScenicLsItemClickListener {
                override fun onCollectClick(id: String, postion: Int, status: Boolean) {
                    if (AppUtils.isLogin()) {
                        showLoadingDialog()
                        if (status) {
                            mModel.collectionCancelScenic(id, postion)
                        } else {
                            mModel.collectionScenic(id, postion)
                        }
                    } else {
                        ToastUtils.showMessage("该操作需要登录，请先登录")
                        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                            .navigation()
                    }
                }
            }
    }

    /**
     * 定位
     */
    fun location() {
        GaoDeLocation.getInstance().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {
            override fun onResult(
                adCode: String,
                result: String,
                lat_: Double,
                lon_: Double,
                adcode: String
            ) {
                try {
                    val selfLocation = LatLng(lat_, lon_)
                    countryAllMoreAdapter?.selfLocation = selfLocation
                    mModel.lat = lat_.toString()
                    mModel.lon = lon_.toString()
                    mModel.getCountryAllList()
                } catch (e: Exception) {
                }
            }

            override fun onError(errormsg: String) {
                mModel.getCountryAllList()
            }
        })
    }

    /**
     * 数据处理
     */
    private fun pageDealed(it: MutableList<CountryListBean>) {
        dissMissLoadingDialog()
        if (!mBinding.recyCountryHappiness.isVisible) {
            mBinding.recyCountryHappiness.visibility = View.VISIBLE
        }
//        mBinding.swprefreshCountryHappiness.isRefreshing = false
        mBinding.swprefreshCountryHappiness.finishRefresh()
        if (mModel.mCurrPage == 1) {
            mBinding.recyCountryHappiness.smoothScrollToPosition(0)
            countryAllMoreAdapter?.clear()
        }
        if (!it.isNullOrEmpty()) {
            countryAllMoreAdapter?.add(it)
        }
        if (it.isNullOrEmpty() || it.size < mModel.mPageSize) {
            countryAllMoreAdapter?.loadEnd()
        } else {
            countryAllMoreAdapter?.loadComplete()
        }
    }

    override fun initData() {
        // 获取地区数据
        mModel.getChildRegions()
    }

    override fun onDestroy() {
        super.onDestroy()
        GaoDeLocation.getInstance().release()
    }
}