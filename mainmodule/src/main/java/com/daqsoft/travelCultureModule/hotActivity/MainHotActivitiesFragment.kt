package com.daqsoft.travelCultureModule.hotActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.widgets.FullyLinearLayoutManager
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.LayoutSelectPopupWindowBinding
import com.daqsoft.mainmodule.databinding.MainActivityHotActivitiesListBinding
import com.daqsoft.mainmodule.databinding.MainItemHotActivityClassifyBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ActivityCalenderBean
import com.daqsoft.provider.bean.Classify
import com.daqsoft.provider.bean.HomeAd
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.databinding.ItemPopTagBinding
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.view.BasePopupWindow
import com.daqsoft.provider.view.ListPopupWindow
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.AdvImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import com.daqsoft.travelCultureModule.hotActivity.adapter.ActivityCanlenderInAdapter
import com.daqsoft.travelCultureModule.hotActivity.model.HotActivityModel
import com.daqsoft.travelCultureModule.hotActivity.view.ActCalSelectPopWindow
import com.google.android.material.appbar.AppBarLayout
import com.jakewharton.rxbinding2.view.RxView
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/**
 * @Description 热门活动列表
 * @ClassName   MainHotActivitiesFragment
 * @Author      PuHua
 * @Time        2019/12/25 10:25
 *
 *
 */
class MainHotActivitiesFragment :
    BaseFragment<MainActivityHotActivitiesListBinding, HotActivitiesFragmentViewModel>() {
    var classifyId: String? = ""
    /**
     * 热门活动适配器
     */
    private var hotActivityAdapter: HotActivityAdapter? = null

    private var hotActCanlenderAdapter: ActivityCanlenderInAdapter? = null

    override fun getLayout(): Int = R.layout.main_activity_hot_activities_list

    override fun injectVm(): Class<HotActivitiesFragmentViewModel> =
        HotActivitiesFragmentViewModel::class.java

    /**
     * 弹窗
     */
    private var areaListPopupWindow: AreaSelectPopupWindow? = null
    /**
     * 排序
     */
    val sorts = HotActivityModel.sorts

    /**
     * 排序窗口
     */
    private var sortListPopupWindow: ListPopupWindow<Any>? = null
    /**
     * 获取日历选择
     */
    private var actCalSelectPopupWindow: ActCalSelectPopWindow? = null

    /**
     * 选择 类型
     */
    val selectMethods = HotActivityModel.selectMethods

    /**
     * 月份
     */
    val months = HotActivityModel.months

    /**
     * 选择窗口
     */
    var selectPopupWindow: BasePopupWindow? = null

    /**
     * 关键字
     */
    var mKeyWords: String? = ""

    @SuppressLint("CheckResult")
    override fun initView() {
        mBinding.vm = mModel
        hotClassifyAdapter?.emptyViewShow = false
        // 活动类型
        val classifyLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvActivityTypes.layoutManager = classifyLayoutManager
        mBinding.rvActivityTypes.adapter = hotClassifyAdapter

        // 活动日历
        hotActCanlenderAdapter = ActivityCanlenderInAdapter(context!!)
        hotActCanlenderAdapter?.emptyViewShow = false
        mBinding.rvActivityCanlender.adapter = hotActCanlenderAdapter
        mBinding.rvActivityCanlender.layoutManager = GridLayoutManager(
            context!!, 5,
            GridLayoutManager.VERTICAL, false
        )
        hotActCanlenderAdapter?.onActCalItemClickListener =
            object : ActivityCanlenderInAdapter.OnActCalItemClickListener {
                override fun selectItem(item: ActivityCalenderBean) {
                    if (item != null) {
                        changeDateTime(item.dateTime)
                    }
                }

            }

        // 活动
        hotActivityAdapter = HotActivityAdapter(context!!, mModel)
        hotActivityAdapter!!.emptyViewShow = false
        val activityLayoutManager: StaggeredGridLayoutManager =
            StaggeredGridLayoutManager(1, FullyLinearLayoutManager.VERTICAL)
        mBinding.rvActivity.layoutManager = activityLayoutManager
        mBinding.rvActivity.adapter = hotActivityAdapter
        (mBinding.rvActivity.itemAnimator as SimpleItemAnimator).setSupportsChangeAnimations(false);

        // 筛选
        val inflater = LayoutInflater.from(context)
        val selectBinding: LayoutSelectPopupWindowBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_select_popup_window,
            null,
            false
        )
        if (selectPopupWindow == null) {
            initSelectPopWindow(selectBinding)
        }

        selectMethods?.forEach {
            it.select=false
        }
        // 排序方式
        initSortPopWindow()
        initViewModel()
        initViewEvent()
    }

    /**
     * 初始化viewModel
     */
    private fun initViewModel() {

        mModel.topAdsLiveData.observe(this, Observer {
            setTopAdvBanner(it)
        })
        // 地区选择
        mModel.areas.observe(this, Observer {
            if (areaListPopupWindow == null) {
                it.add(0, ChildRegion("", "全部", "", "", emptyList()))
                areaListPopupWindow =
                    AreaSelectPopupWindow.getInstance(
                        BaseApplication.context, false
                    ) { item ->
                        showLoadingDialog()
                        mBinding.tvArea.text = (item as ChildRegion).name
                        mModel.currentArea = item.region
                        mModel.mActivityCurrPage = 1
                        mModel.areaSiteSwitch = item.siteId
                        mBinding?.rvActivity?.visibility = View.GONE
                        mBinding?.rvActivity?.smoothScrollToPosition(0)
                        hotActivityAdapter?.clear()
                        mModel.getActivityList()
                    }
                areaListPopupWindow!!.firstData = it
                var temp: MutableList<ChildRegion> = mutableListOf()
                temp.add(0, ChildRegion("", "不限", "", "", emptyList()))
                areaListPopupWindow!!.secondData = ArrayList(temp)
            }
        })

        // 活动日历
//        mModel.activityCalenderLd?.observe(this, Observer {
//            if (!it.isNullOrEmpty()) {
//                mBinding.vActivityIndeCanlender.visibility = View.VISIBLE
//                mBinding.vActivityIndexLine.visibility = View.VISIBLE
//            } else {
//                mBinding.vActivityIndeCanlender.visibility = View.GONE
//                mBinding.vActivityIndexLine.visibility = View.GONE
//            }
//        })
        // 活动列表
        mModel.activities.observe(this, Observer {
//            mBinding?.swRefreshActivities.isRefreshing = false
            mBinding.swRefreshActivities.finishRefresh()
            if (mModel.mActivityCurrPage == 1) {
                hotActivityAdapter!!.clear()
                if (!mBinding?.rvActivity?.isVisible) {
                    mBinding?.rvActivity.visibility = View.VISIBLE
                }
//                hotActivityAdapter!!.emptyViewShow = it.isNullOrEmpty()
                if (it.isNullOrEmpty()){
                    mBinding.rvActivity.visibility = View.GONE
                    mBinding.llActivityEmpty.visibility = View.VISIBLE
                }else{
                    mBinding.llActivityEmpty.visibility = View.GONE
                }
            }
            if (!it.isNullOrEmpty()) {
                hotActivityAdapter!!.add(it)
            }
            if (it.isNullOrEmpty() || it.size < mModel.mActivityPageSize) {
                hotActivityAdapter!!.loadEnd()
            } else {
                hotActivityAdapter!!.loadComplete()
            }
            dissMissLoadingDialog()
        })
        // 活动分类
        mModel.activityClassifies.observe(this, Observer {

            val allClassify = Classify(0, "", "全部", false)
            it.add(0, allClassify)
            var selectPos = -1
            if (it.size > 0) {
                mModel.mActivityCurrPage = 1
                if (classifyId.isNullOrEmpty()) {
                    it[0].select = true
                    hotClassifyAdapter.currentClassify = it[0]
                    mModel.currentClassifyId = it[0].id
                    mModel.getActivityList()
                } else {
                    for (index in it.indices) {
                        var item = it[index]
                        if (item.id == classifyId) {
                            selectPos = index
                            item.select = true
                            hotClassifyAdapter.currentClassify = item
                            break
                        }
                    }
                    mModel.currentClassifyId = classifyId
                    mModel.getActivityList()
                }
            } else {
                dissMissLoadingDialog()
            }
            var temps: MutableList<Classify> = mutableListOf()
            for (bean in it) {
                if ((!bean.labelName.isNullOrEmpty() && bean.labelName == "全部") || bean.activityCount > 0) {
                    temps.add(bean)
                }
            }
            hotClassifyAdapter.add(temps)
            if (selectPos > -1) {
                mBinding.rvActivityTypes.scrollToPosition(selectPos)
            }
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
//            mBinding?.swRefreshActivities.isRefreshing = false
            mBinding.swRefreshActivities.finishRefresh()
        })
        mModel.collectLiveData.observe(this, Observer {
            hotActivityAdapter?.notifyUpdateCollectStatus(it, true)
        })
        mModel.canceCollectLiveData.observe(this, Observer {
            hotActivityAdapter?.notifyUpdateCollectStatus(it, false)
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })

        mModel.activityCalenderLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (hotActCanlenderAdapter!!.selectTime.isNullOrEmpty()) {
                if (it.isNullOrEmpty()) {
                    mBinding.vActivityIndexLine.visibility = View.GONE
                    mBinding.vActivityIndeCanlender.visibility = View.GONE
                } else {
                    mBinding.vActivityIndexLine.visibility = View.VISIBLE
                    mBinding.vActivityIndeCanlender.visibility = View.VISIBLE
                    hotActCanlenderAdapter?.clear()
                    var datas = getCurrentActCanlenderLs(it)
                    if(BaseApplication.appArea!="sc"){
                        datas.add(0,ActivityCalenderBean(-1,"全部"))
                    }
                    var selectTime: String? = ""
                    if (!datas.isNullOrEmpty()) {
                        if (datas[0] != null) {
                            selectTime = datas[0].dateTime
                            hotActCanlenderAdapter?.selectTime = selectTime
                        }
                        hotActCanlenderAdapter?.add(
                            datas
                        )
                        setActCalSelectDatas(it, selectTime)
                    } else {
                        setActCalSelectDatas(it, "")
                    }
                }
            } else {
                dissMissLoadingDialog()
                var selectTime: String? = hotActCanlenderAdapter?.selectTime
                setActCalSelectDatas(it, selectTime)
            }

        })
    }

    /**
     * 获取当前日历数据
     */
    private fun getCurrentActCanlenderLs(it: MutableList<ActivityCalenderBean>): MutableList<ActivityCalenderBean> {
        var datas: MutableList<ActivityCalenderBean> = mutableListOf()
        var currDateStr: String? = DateUtil.getDqDateString(Date())
        if (!currDateStr.isNullOrEmpty()) {
            var position = -1
            for (i in it.indices) {
                var item = it[i]
                if (item != null && item.dateTime == currDateStr) {
                    position = i
                }
            }
            var size = it.size
            var outPos = position + 3
            var maxPosition = if (outPos < size) {
                outPos
            } else {
                size - 1
            }
            if (position >= 0) {
                for (pos in position..maxPosition) {
                    datas.add(it[pos])
                }
            }
        }
        return datas
    }


    /**
     * 初始化View事件处理
     */
    private fun initViewEvent() {

//        mBinding?.swRefreshActivities.setProgressViewOffset(true, -20, 100);
        mBinding?.swRefreshActivities.isNestedScrollingEnabled = true
        mBinding?.appbarHotActivities.addOnOffsetChangedListener(object :
            AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
                if (p1 >= 0) {
                    mBinding.swRefreshActivities.setEnabled(true);
                } else {
                    mBinding.swRefreshActivities.setEnabled(false);
                }
            }

        })
        mBinding?.vActivityIndeCanlender?.onNoDoubleClick {
            actCalSelectPopupWindow?.showAtLocation(mBinding.root, Gravity.TOP, 0, 0)

        }

        mBinding?.rvActivity.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                var topRowVerticalPosition: Int = 0
                if (recyclerView == null || recyclerView.childCount == 0) {
                    topRowVerticalPosition = 0;
                } else {
                    topRowVerticalPosition = recyclerView.getChildAt(0).getTop()
                }
                mBinding.swRefreshActivities.isEnabled = topRowVerticalPosition >= 0;
            }
        });
        RxView.clicks(mBinding.tvArea)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (areaListPopupWindow != null) {
                    areaListPopupWindow!!.show(mBinding.tvArea)
                }
            }
        RxView.clicks(mBinding.tvType)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (selectPopupWindow != null) {
                    selectPopupWindow!!.resetDarkPosition()
                    selectPopupWindow!!.darkBelow(mBinding.tvSort)
                    selectPopupWindow!!.showAsDropDown(mBinding.tvSort)
                }
            }
        RxView.clicks(mBinding.tvSort)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {

                if (sortListPopupWindow != null) {
                    sortListPopupWindow!!.show()
                }
            }

        mBinding.swRefreshActivities.setOnRefreshListener {
//            mBinding?.swRefreshActivities.isRefreshing = true
            mModel.mActivityCurrPage = 1
            mModel.getActivityList()
        }
        hotActivityAdapter!!.setOnLoadMoreListener {
            mModel.mActivityCurrPage = 1 + mModel.mActivityCurrPage
            mModel.getActivityList()
        }
        mBinding?.edtSearchActivities.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }
        mBinding.tvSearchActivities.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }
    }


    /**
     * 热门活动分类适配器
     */
    private val hotClassifyAdapter = object :
        RecyclerViewAdapter<MainItemHotActivityClassifyBinding, Classify>(R.layout.main_item_hot_activity_classify) {
        var currentClassify: Classify? = null

        override fun setVariable(
            mBinding: MainItemHotActivityClassifyBinding,
            position: Int,
            item: Classify
        ) {
            mBinding.label.text = item.labelName
            mBinding.label.isSelected = item.select

            var d = RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    run {
                        if (currentClassify != item) {
                            item.select = true
                            if (currentClassify != null) {
                                currentClassify!!.select = false
                            }
                            currentClassify = item
                            mModel.mActivityCurrPage = 1
                            mModel.currentClassifyId = item.id
                            selectToClassfiyPos(position)
                            showLoadingDialog()
                            mModel.getActivityList()
                            notifyDataSetChanged()
                        }
                    }
                }
        }
    }

    /**
     *  选择点击的class
     */
    fun selectToClassfiyPos(position: Int) {
        mBinding?.rvActivityTypes.smoothScrollToPosition(position)
    }

    /**
     *  初始化搜索窗口
     */
    @SuppressLint("CheckResult")
    private fun initSelectPopWindow(selectBinding: LayoutSelectPopupWindowBinding) {
        selectPopupWindow = BasePopupWindow(
            selectBinding.root, LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT, true
        )
        // 设置背景
        selectPopupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 设置是否能够响应外部点击事件
        selectPopupWindow!!.isOutsideTouchable = true
        // 设置能否响应点击事件
        selectPopupWindow!!.isTouchable = true
        selectPopupWindow!!.isFocusable = true
        selectPopupWindow!!.setOnDismissListener {

            view!!.isSelected = false
        }
        selectPopupWindow!!.resetDarkPosition()
        selectPopupWindow!!.darkBelow(mBinding.tvSort)
        // 活动方式
        val methodGridLayoutManager = GridLayoutManager(context, 4)
        val methodAdapter = object : RecyclerViewAdapter<ItemPopTagBinding, ValueKeyBean>(
            R.layout
                .item_pop_tag
        ) {

            var previous: View? = null
            var previousData: ValueKeyBean? = null
            override fun setVariable(
                mBinding: ItemPopTagBinding,
                position: Int,
                item: ValueKeyBean
            ) {
                mBinding.name = item.name

                mBinding.tvVolunteer.isSelected = item.select
                mBinding.root.setOnClickListener {
                    var isSelect = getData()[position].select
                    getData()[position].select = !isSelect
                    notifyItemChanged(position, "updateSelectPos")
                }
            }

            override fun payloadUpdateUi(
                mBinding: ItemPopTagBinding,
                position: Int,
                payloads: MutableList<Any>
            ) {
                if (payloads[0] == "updateSelectPos") {
                    var item = getData()[position]
                    mBinding.tvVolunteer.isSelected = item.select
                }
            }

        }
        methodAdapter.add(selectMethods)
        selectBinding.rvTypes.layoutManager = methodGridLayoutManager
        selectBinding.rvTypes.adapter = methodAdapter

        // 月份
        val monthGridLayoutManager = GridLayoutManager(context, 4)
        val monthAdapter = object : RecyclerViewAdapter<ItemPopTagBinding, ValueKeyBean>(
            R.layout
                .item_pop_tag
        ) {

            var previous: View? = null
            var previousData: ValueKeyBean? = null
            override fun setVariable(
                mBinding: ItemPopTagBinding,
                position: Int,
                item: ValueKeyBean
            ) {
                mBinding.name = item.name

                mBinding.tvVolunteer.isSelected = item.select

                val d = RxView.clicks(mBinding.tvVolunteer)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        if (!mBinding.tvVolunteer.isSelected) {
                            mBinding.tvVolunteer.isSelected = true
                            if (previous != null) {
                                previous!!.isSelected = false
                                previousData!!.select = false
                            }
                            item.select = true
                            mModel.currentMonth = item.value
                            previous = mBinding.tvVolunteer
                            previousData = item
                        }
                    }

            }

        }
        monthAdapter.add(months)
        selectBinding.rvMonth.layoutManager = monthGridLayoutManager
        selectBinding.rvMonth.adapter = monthAdapter

        RxView.clicks(selectBinding.tvSure)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                selectPopupWindow!!.dismiss()
                showLoadingDialog()
                mModel.mActivityCurrPage = 1
                mBinding.rvActivity?.visibility = View.GONE
                mBinding.rvActivity?.smoothScrollToPosition(0)
                hotActivityAdapter?.clear()
                mModel.currentMethod = getCurrentMethod()
                mModel.getActivityList()
            }
        RxView.clicks(selectBinding.tvReset)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                mModel.currentMonth = ""
                mModel.currentMethod = ""
                if (methodAdapter.previous != null) {
                    methodAdapter.previous!!.isSelected = false
                    methodAdapter.previousData!!.select = false
                }
                if (monthAdapter.previous != null) {
                    monthAdapter.previous!!.isSelected = false
                    monthAdapter.previousData!!.select = false
                }
                if(selectMethods!=null && selectMethods.size>0){
                    for (i in selectMethods.indices) {
                        selectMethods[i].select=false
                    }
                }
                methodAdapter.notifyDataSetChanged()

                selectPopupWindow!!.dismiss()
                mModel.mActivityCurrPage = 1
                showLoadingDialog()
                mBinding.rvActivity?.visibility = View.GONE
                mBinding.rvActivity?.smoothScrollToPosition(0)
                hotActivityAdapter?.clear()
                mModel.getActivityList()
            }

    }

    private fun getCurrentMethod(): String {
        var result: StringBuilder = StringBuilder("")
        for (i in selectMethods.indices) {
            var item = selectMethods[i]
            if (selectMethods[i].select) {
                if (result.isNullOrEmpty()) {
                    result.append(item.value)
                } else {
                    result.append(",${item.value}")
                }
            }
        }
        return result.toString()
    }

    /**
     *   初始化排序Popwindow
     */
    private fun initSortPopWindow() {
        sortListPopupWindow = ListPopupWindow.getInstance(mBinding.tvArea, sorts) { item ->
            mBinding.tvSort.text = (item as ValueKeyBean).name
            // 当选择距离优先时需要加入自己的经纬度
            if (item.value == "2") {
                GaoDeLocation.getInstance().init(context, object : GaoDeLocation.OnGetCurrentLocationLisner {
                    override fun onResult(
                        adCode: String?,
                        result: String?,
                        lat: Double,
                        lon: Double,
                        adcode: String?
                    ) {
                        showLoadingDialog()
                        mModel.currentLat = lat
                        mModel.currentLon = lon
                        mModel.currentSort = item.value
                        mBinding?.rvActivity?.visibility = View.GONE
                        mBinding?.rvActivity?.smoothScrollToPosition(0)
                        hotActivityAdapter?.clear()
                        mModel.getActivityList()
                    }

                    override fun onError(errorMsg: String?) {

                    }

                })
            } else {
                showLoadingDialog()
                mModel.currentSort = item.value
                mModel.getActivityList()
            }
        }
    }

    /**
     * 初始化数据
     */
    override fun initData() {
        showLoadingDialog()
        getClassifyId(activity)
        if (!classifyId.isNullOrEmpty()) {
            mModel.currentClassifyId = classifyId!!
        }
        var dateTime: String = DateUtil.getDqDateString("yyyy-MM-dd", Date())
        mModel.startTime = ""
        mModel.endTime = ""
        mModel.getActivityClassify()
        mModel.getActivityTopAds()
        mModel.getChildRegions()
        mModel.getActivityCanlender(DateUtil.getDqDateString("yyyy-MM", Date()))
    }

    override fun onDestroy() {
        super.onDestroy()
        actCalSelectPopupWindow = null
        GaoDeLocation.getInstance().release()
    }

    fun setActCalSelectDatas(datas: MutableList<ActivityCalenderBean>, selectTime: String?) {
        if (actCalSelectPopupWindow == null) {
            actCalSelectPopupWindow = ActCalSelectPopWindow(context!!)
            actCalSelectPopupWindow?.onActCalOpeationListener =
                object : ActCalSelectPopWindow.OnActCalOpeationListener {
                    override fun onChangeMonth(dateTime: String) {
                        showLoadingDialog()
                        mModel.getActivityCanlender(dateTime)
                    }

                    override fun onSelectDate(datas: MutableList<ActivityCalenderBean>) {
                        if (!datas.isNullOrEmpty()) {
                            hotActCanlenderAdapter?.clear()
                            if (datas[0] != null) {
                                var dateTime = datas[0].dateTime
                                hotActCanlenderAdapter?.selectTime = dateTime
                                changeDateTime(dateTime)
                            }
                            hotActCanlenderAdapter?.add(
                                datas
                            )
                        }
                    }

                }
        }
        actCalSelectPopupWindow?.setDataInfo(datas, selectTime)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        getClassifyId(activity)
    }

    private fun getClassifyId(activity: Activity?) {
        if (activity != null && activity is HotActivitiesActivity)
            classifyId = activity?.classifyId
    }

    /**
     * 设置顶部banner显示
     */
    private fun setTopAdvBanner(it: HomeAd?) {
        if (it == null || it.adInfo.isNullOrEmpty()) {
            mBinding.vActivityListNoAdv.visibility = View.VISIBLE
            mBinding.vActivityHaveAdv.visibility = View.GONE
        } else {
            mBinding.cbanerActivitiesTopAdv
                .setPages(object : CBViewHolderCreator {
                    override fun createHolder(itemView: View?): Holder<*> {
                        return AdvImageHolder(itemView!!)
                    }

                    override fun getLayoutId(): Int {
                        return com.daqsoft.provider.R.layout.layout_common_adv
                    }
                }, it.adInfo)
                .setCanLoop(it.adInfo.size > 1)
                .setPointViewVisible(it.adInfo.size > 1)
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setOnItemClickListener { it2 ->
                    run {
                        // 跳转事件
                        MenuJumpUtils.adJump(it.adInfo[it2])
                    }

                }
                .setPageIndicator(null)
                .startTurning(3000)

            if (it.adInfo.isNotEmpty()) {
                mBinding.vActivityListNoAdv.visibility = View.GONE
                mBinding.vActivityHaveAdv.visibility = View.VISIBLE
            }
        }
    }

    private fun changeDateTime(dateTime: String) {
        showLoadingDialog()
        if(dateTime!="全部"){
            mModel.startTime = dateTime
            mModel.endTime = dateTime
        }else{
            mModel.startTime = ""
            mModel.endTime = ""
        }
        mModel.mActivityCurrPage = 1
        hotActivityAdapter?.clear()
        mModel.getActivityList()
    }
}