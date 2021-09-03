package com.daqsoft.servicemodule.ui

import android.annotation.SuppressLint
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityTravelAgencyListBinding
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.FullyLinearLayoutManager
import com.daqsoft.provider.ARouterPath
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.view.ListPopupWindow
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import com.daqsoft.servicemodule.adapter.TravelAgencyAdapter
import com.daqsoft.servicemodule.model.ServiceTravelModel
import com.daqsoft.servicemodule.model.TravelAgencyViewModel
import com.daqsoft.servicemodule.uitils.JavaUtil.hideKeyboard
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * desc :旅行社列表
 * @author 江云仙
 * @date 2020/4/2 16:56
 */
@Route(path = ARouterPath.ServiceModule.SERVICE_TRAVEL_AGENCY_LIST_ACTIVITY)
class TravelAgencyListActivity : TitleBarActivity<ActivityTravelAgencyListBinding, TravelAgencyViewModel>() {
    /**
     * 旅行社适配器
     */
    private var travelAgencyAdapter: TravelAgencyAdapter? = null
    /**
     * 地区选择弹窗窗口
     */
    private var areaListPopWindow: AreaSelectPopupWindow? = null
    /**
     * 排序
     */
    val sorts = ServiceTravelModel.sorts
    /**
     * 等级
     */
    val levels = ServiceTravelModel.levels

    /**
     * 排序窗口
     */
    private var sortListPopupWindow: ListPopupWindow<Any>? = null
    /**
     * 等级排序窗口
     */
    private var levelListPopupWindow: ListPopupWindow<Any>? = null

    /**
     * 关键字
     */
    var mKeyWords: String? = ""

    override fun getLayout(): Int {
        return R.layout.activity_travel_agency_list
    }

    override fun setTitle(): String {
        return "旅行社"
    }

    override fun injectVm(): Class<TravelAgencyViewModel> = TravelAgencyViewModel::class.java
    override fun initView() {
        mBinding?.recyTravel.visibility = View.GONE
        mBinding.vm = mModel
        // 旅行社
        travelAgencyAdapter = TravelAgencyAdapter(this)
        travelAgencyAdapter!!.emptyViewShow = false
        val activityLayoutManager =
            StaggeredGridLayoutManager(1, FullyLinearLayoutManager.VERTICAL)
        mBinding.recyTravel.layoutManager = activityLayoutManager
        mBinding.recyTravel.adapter = travelAgencyAdapter
        (mBinding.recyTravel.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        // 排序方式
        initSortPopWindow()
        initViewModel()
        initViewEvent()

    }

    /**
     * 初始化View事件处理
     */
    @SuppressLint("CheckResult")
    private fun initViewEvent() {
        //地区选择
        mModel.areas.observe(this, Observer {
            if (areaListPopWindow == null) {
                it.add(0, ChildRegion("", "全部", "", "", emptyList()))
                areaListPopWindow =
                    AreaSelectPopupWindow.getInstance(
                        BaseApplication.context, false
                    ) { item ->
                        mBinding.tvArea.text = (item as ChildRegion).name
                        mModel.region = item.region
                        mModel.mCurrPage = 1
                        if (mBinding.edtSearchTour.text.toString().trim().isNullOrEmpty()) {
                            mModel.mKeyWords = mBinding.edtSearchTour.text.toString()
                        }
                        mModel.getTravelAgencyList()
                    }
                areaListPopWindow!!.firstData = it
                val temp: MutableList<ChildRegion> = mutableListOf()
                temp.add(0, ChildRegion("", "不限", "", "", emptyList()))
                areaListPopWindow!!.secondData = ArrayList(temp)
            }
        })
        RxView.clicks(mBinding.tvArea).throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (areaListPopWindow != null) {
                    areaListPopWindow!!.show(mBinding.tvArea)
                }
            }
        RxView.clicks(mBinding.tvDistance)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {

                if (sortListPopupWindow != null) {
                    sortListPopupWindow!!.show()
                }
            }
        mBinding.tvLevel.onNoDoubleClick {
            if (levelListPopupWindow != null) {
                levelListPopupWindow!!.show()
            }
        }
        mBinding.swRefreshActivities.setOnRefreshListener {
            if (mBinding.edtSearchTour.text.toString().trim().isNullOrEmpty()) {
                mModel.mKeyWords = mBinding.edtSearchTour.text.toString()
            }
//            mBinding?.swRefreshActivities.isRefreshing = true
            mModel.mCurrPage = 1
            mModel.getTravelAgencyList()
        }
        travelAgencyAdapter!!.setOnLoadMoreListener {
            mModel.mCurrPage = 1 + mModel.mCurrPage
            mModel.getTravelAgencyList()
        }

        //旅行社搜索
        mBinding.edtSearchTour.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo
                    .IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT
            ) {
                mModel.mKeyWords = mBinding.edtSearchTour.text.toString()
                mModel.mCurrPage = 1
                mModel.getTravelAgencyList()
                hideKeyboard(this)
            }
            false
        }
    }

    /**
     * 初始化viewModel
     */
    private fun initViewModel() {
        // 活动列表
        mModel.result.observe(this, Observer {
            mBinding?.recyTravel.visibility = View.VISIBLE
//            mBinding?.swRefreshActivities.isRefreshing = false
            mBinding.swRefreshActivities.finishRefresh()
            if (mModel.mCurrPage == 1) {
                travelAgencyAdapter!!.clear()
                if (it.isNullOrEmpty()) {
                    travelAgencyAdapter!!.emptyViewShow = true
                    mBinding.swRefreshActivities.isEnabled = false
                } else {
                    travelAgencyAdapter!!.emptyViewShow = false
                }

            }
            if (!it.isNullOrEmpty()) {
                travelAgencyAdapter!!.add(it)
            }
            if (it.isNullOrEmpty() || it.size < mModel.mPageSize) {
                travelAgencyAdapter!!.loadEnd()
            } else {
                travelAgencyAdapter!!.loadComplete()
            }
            dissMissLoadingDialog()
        })

        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
//            mBinding?.swRefreshActivities.isRefreshing = false
            mBinding.swRefreshActivities.finishRefresh()
        })
    }

    /**
     * 初始化排序Popwindow
     */
    private fun initSortPopWindow() {
        sortListPopupWindow = ListPopupWindow.getInstance(mBinding.tvArea, sorts) { item ->
            mBinding.tvDistance.text = (item as ValueKeyBean).name
            // 当选择距离优先时需要加入自己的经纬度
            if (item.value == "1") {
                GaoDeLocation.getInstance().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {
                    override fun onResult(adCode: String?, result: String?, lat: Double, lon: Double, adcode: String?) {

                        mModel.currentLat = lat.toString()
                        mModel.currentLon = lon.toString()
                        if (mBinding.edtSearchTour.text.toString().trim().isNullOrEmpty()) {
                            mModel.mKeyWords = mBinding.edtSearchTour.text.toString()
                        }
                        mModel.getTravelAgencyList()
                    }

                    override fun onError(errorMsg: String?) {

                    }

                })
            } else {
                mModel.currentLat = ""
                mModel.currentLon = ""
                if (mBinding.edtSearchTour.text.toString().trim().isNullOrEmpty()) {
                    mModel.mKeyWords = mBinding.edtSearchTour.text.toString()
                }
                mModel.getTravelAgencyList()
            }
        }
        levelListPopupWindow = ListPopupWindow.getInstance(mBinding.tvLevel, levels) { item ->
            if (item != null) {
                var itemValue = item as ValueKeyBean
                mModel.mCurrPage = 1
                mModel.level = itemValue.value
                mModel.getTravelAgencyList()
            }

        }
    }

    /**
     * 初始化数据
     */
    override fun initData() {
        mModel.getTravelAgencyList()
        mModel.getChildRegions()
    }

    override fun onDestroy() {
        super.onDestroy()
        GaoDeLocation.getInstance().release()
    }
}
