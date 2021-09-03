package com.daqsoft.servicemodule.ui

import android.annotation.SuppressLint
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityGuideTourListBinding
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.view.ListPopupWindow
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import com.daqsoft.servicemodule.adapter.TourGuideAdapter
import com.daqsoft.servicemodule.bean.TourGuideBean
import com.daqsoft.servicemodule.bean.TourLevelBean
import com.daqsoft.servicemodule.model.GuideTourListViewModel
import com.daqsoft.servicemodule.uitils.JavaUtil.hideKeyboard
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * desc :导游列表
 * @author 江云仙
 * @date 2020/4/2 9:51
 */
@Route(path = ARouterPath.ServiceModule.SERVICE_TOUR_GUIDE_LIST_ACTIVITY)
class GuideTourListActivity : TitleBarActivity<ActivityGuideTourListBinding, GuideTourListViewModel>() {
    @JvmField
    @Autowired
    var name = ""//搜索内容
    @JvmField
    @Autowired
    var gender = ""//性别
    var currPage = 1
    var pageSize = 6
    var region = ""//地区选择
    var level = ""//导游等级
//    var content = ""//搜索内容
    /**
     * 地区选择弹窗窗口
     */
    private var areaListPopWindow: AreaSelectPopupWindow? = null
//    var page=1

    lateinit var pop_area: ListPopupWindow<Any>
    lateinit var pop_type:ListPopupWindow<Any>
    override fun getLayout(): Int {
        return R.layout.activity_guide_tour_list
    }

    override fun setTitle(): String {
        return "导游列表"
    }

    override fun injectVm(): Class<GuideTourListViewModel> = GuideTourListViewModel::class.java
    @SuppressLint("CheckResult")
    override fun initView() {
        mBinding.name = name
        mBinding.gender = gender
        //导游搜索
        mBinding.edtSearchTour.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo
                    .IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT
            ) {
                name = mBinding.edtSearchTour.text.toString()
                currPage = 1
                reloadData()
                hideKeyboard(this)
            }
            false
        }
        //地区选择
        mModel.areas.observe(this, Observer {
            if (areaListPopWindow == null) {
                it.add(0, ChildRegion("", "全部", "", "", emptyList()))
                areaListPopWindow =
                    AreaSelectPopupWindow.getInstance(
                        BaseApplication.context, false
                    ) { item ->
                        region = item.region
                        reloadData()
                        if (item.name == "全部") {
                            mBinding.tvTourArea.text = "地区选择"
                        } else {
                            mBinding.tvTourArea.text = item.name
                        }
                        showLoadingDialog()
                    }
                areaListPopWindow!!.firstData = it
                val temp: MutableList<ChildRegion> = mutableListOf()
                temp.add(0, ChildRegion("", "不限", "", "", emptyList()))
                areaListPopWindow!!.secondData = ArrayList(temp)
            }
        })
        RxView.clicks(mBinding.tvTourArea).throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (areaListPopWindow != null) {
                    areaListPopWindow!!.show(mBinding.tvTourArea)
                }
            }
        mModel.getChildRegions()
        //导游等级
        pop_type = ListPopupWindow.getInstance(mBinding.tvClubType, mModel.getLevelData(), ListPopupWindow.WindowDataBack<TourLevelBean> {
            level = it.level
            currPage=1
            reloadData()
            if (it.name == "全部") {
                mBinding.tvClubType.text = "导游等级"
            } else {
                mBinding.tvClubType.text = it.name
            }
        })
        mBinding.tvClubType.setOnClickListener {
            pop_type.show()
        }
        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.recyTourList.layoutManager = tagLayoutManager
        val  tourGuideAdapter = TourGuideAdapter()
       mBinding.recyTourList.adapter=tourGuideAdapter
        mModel.result.observe(this, Observer {
            mBinding.mSwipeRefreshLayout.finishRefresh()
            var data=it
            pageDeal(currPage, it, tourGuideAdapter)
            tourGuideAdapter.add(data)
            tourGuideAdapter.notifyDataSetChanged()
        })
        //下拉加载
        tourGuideAdapter.setOnLoadMoreListener {
            currPage++
            reloadData()
        }
        //刷新
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
//            mBinding.mSwipeRefreshLayout.isRefreshing = false
            currPage=1
            reloadData()
        }

    }


    override fun initData() {
        val map = HashMap<String, Any>()
        map["name"] = name
        map["currPage"] = currPage
        map["pageSize"] = pageSize
        map["level"] = level
        map["region"] = region
        map["gender"] = gender ?: "男"
        mModel.getTourGuideList(map)
    }
    private fun pageDeal(page: Int?, response: MutableList<TourGuideBean>, adapter: RecyclerViewAdapter<*, *>) {
        if (page == null) {
            return
        }
        if (page == 1) {
            adapter.clear()
        }
        if (response == null) {
            adapter.loadEnd()
            return
        }
        if (response.size >=pageSize ) {
            adapter.loadComplete()
        } else {
            adapter.loadEnd()
        }
        dissMissLoadingDialog()
    }
}
