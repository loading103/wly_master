package com.daqsoft.volunteer.team

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.view.ListPopupWindow
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerBean
import com.daqsoft.volunteer.bean.VolunteerTeamListBean
import com.daqsoft.volunteer.databinding.ActivityVolunteerListBinding
import com.daqsoft.volunteer.databinding.ActivityVolunteerTeamListBinding
import com.daqsoft.volunteer.main.adapter.VolunteerActivityAdapter
import com.daqsoft.volunteer.net.VolunteerRepository
import com.daqsoft.volunteer.team.adapter.VolunteerTeamAdapter
import com.daqsoft.volunteer.utils.JumpUtils
import com.daqsoft.volunteer.volunteer.fragment.VolunteerRegisterFragment1

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/3 10:01
 *@author: caihj
 *@des:志愿者团队列表
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_TEAM_LIST)
class VolunteerTeamListActivity:TitleBarActivity<ActivityVolunteerTeamListBinding,VolunteerTeamVM>() {
    override fun getLayout(): Int = R.layout.activity_volunteer_team_list

    override fun setTitle(): String = getString(R.string.volunteer_module_volunteer_team_list_title)

    override fun injectVm(): Class<VolunteerTeamVM> = VolunteerTeamVM::class.java

    /**
     * 弹窗
     */
    private var areaListPopupWindow: AreaSelectPopupWindow? = null
    val teamAdapter = VolunteerTeamAdapter(this)

    /**
     * 排序窗口
     */
    private var sortListPopupWindow: ListPopupWindow<Any>? = null

    /**
     * 筛选窗口
     */
    private var filterListPopupWindow: ListPopupWindow<Any>? = null

    override fun initView() {
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
                        mModel.mCurrPage = 1
                        mModel.areaSiteSwitch = item.siteId
                        mBinding?.rvTeams?.visibility = View.GONE
                        mBinding?.rvTeams?.smoothScrollToPosition(0)
                        teamAdapter?.clear()
                        mModel.getTeamList()
                    }
                areaListPopupWindow!!.firstData = it
                var temp: MutableList<ChildRegion> = mutableListOf()
                temp.add(0, ChildRegion("", "不限", "", "", emptyList()))
                areaListPopupWindow!!.secondData = ArrayList(temp)
            }
        })

        mModel.teams.observe(this, Observer {
            mBinding.swRefreshTeams.isRefreshing = false
            pageDel(it)
        })

        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.swRefreshTeams.isRefreshing = false
        })

        mBinding.rvTeams.layoutManager = LinearLayoutManager(this)
        mBinding.rvTeams.adapter = teamAdapter
        initSortPopWindow()
        initFilterPopWindow()
        initEvent()
    }

    override fun initData() {
        mModel.getChildRegions()
        mModel.getTeamList()
    }

    private fun pageDel(datas:MutableList<VolunteerTeamListBean>){
        if (mModel.mCurrPage == 1) {
            mBinding.rvTeams.smoothScrollToPosition(0)
            mBinding.rvTeams.visibility = View.VISIBLE
            teamAdapter.clear()
            teamAdapter.emptyViewShow = datas.isNullOrEmpty()
        }
        if (!datas.isNullOrEmpty()) {
            teamAdapter.add(datas)
        }
        if (datas.isNullOrEmpty() || datas.size < mModel.mPageSize) {
            teamAdapter.loadEnd()
        } else {
            teamAdapter.loadComplete()
        }
        dissMissLoadingDialog()
    }


    private fun initEvent(){
        mBinding.tvArea.onNoDoubleClick {
            areaListPopupWindow?.show(mBinding.tvArea)
        }
        mBinding.tvSort.onNoDoubleClick {
            sortListPopupWindow?.show()
        }
        mBinding.tvFilter.onNoDoubleClick {
            filterListPopupWindow?.show()
        }
        mBinding.llSearch.onNoDoubleClick {
            JumpUtils.gotoSearch()
        }
        mBinding.swRefreshTeams.setOnRefreshListener {
            mModel.mCurrPage = 1
            teamAdapter.clear()
            mModel.getTeamList()
        }
        teamAdapter.setOnLoadMoreListener {
            mBinding.swRefreshTeams.isRefreshing = false
            mModel.mCurrPage ++
            mModel.getTeamList()
        }
    }

    /**
     * 初始化排序窗口
     */
    private fun initSortPopWindow(){
        sortListPopupWindow = ListPopupWindow.getInstance(mBinding.tvSort, mModel.sorts) { item ->
            mBinding.tvSort.text = (item as ValueKeyBean).name
            // 当选择距离优先时需要加入自己的经纬度
            showLoadingDialog()
            mModel.mCurrPage = 1
            mModel.currentSort = item.value
            teamAdapter.clear()
            mModel.getTeamList()
        }
    }


    private fun initFilterPopWindow(){
        filterListPopupWindow = ListPopupWindow.getInstance(mBinding.tvSort, mModel.filters) { item ->
            mBinding.tvFilter.text = (item as ValueKeyBean).name
            // 当选择距离优先时需要加入自己的经纬度
            showLoadingDialog()
            mModel.mCurrPage = 1
            mModel.currentFilter = item.value
            teamAdapter.clear()
            mModel.getTeamList()
        }
    }
}

class VolunteerTeamVM:BaseViewModel(){

    /**
     * 选中的地区
     */
    var currentArea = ""

    /**
     * 当前排序
     */
    var currentSort = ""

    /**
     * 当前筛选
     */
    var currentFilter = ""
    /**
     * 获取页码大小
     */
    var mPageSize = 10

    /**
     * 活动分页页码
     */
    var mCurrPage = 1

    var areaSiteSwitch: String? = ""

    var teams = MutableLiveData<MutableList<VolunteerTeamListBean>>()

    /**
     * 状态类型
     */
    val sorts = mutableListOf(
        ValueKeyBean("不限", ""),
        ValueKeyBean("人数优先", "memberNumDesc"),
        ValueKeyBean("时长优先", "serviceTimeDesc"),
        ValueKeyBean("次数优先", "serviceNumDesc")
    )

    /**
     * 筛选
     */
    val filters = mutableListOf(
        ValueKeyBean("不限", ""),
        ValueKeyBean("可签到", "signIn"),
        ValueKeyBean("有活动", "existActivity")
    )


    /**
     * 地区
     */
    val areas = MutableLiveData<MutableList<ChildRegion>>()

    /**
     * 站点下级区域(两层)
     */
    fun getChildRegions() {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        VolunteerRepository.service.getChildRegions().excute(object : BaseObserver<ChildRegion>() {
            override fun onSuccess(response: BaseResponse<ChildRegion>) {
                areas.postValue(response.datas)
            }
        })
    }

    /**
     * 获取团队列表
     */
    fun getTeamList(){
        val params = HashMap<String,String>()
        if(currentArea!=""){
            params["region"] = currentArea
        }
        if(currentSort != ""){
            params["teamSortType"] = currentSort
        }
        if(currentFilter != ""){
            params["teamFilterType"] = currentFilter
        }
        params["pageSize"] = mPageSize.toString()
        params["currPage"] = mCurrPage.toString()

        VolunteerRepository.service.getVolunteerTeamList(params).excute(object :BaseObserver<VolunteerTeamListBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerTeamListBean>) {
                teams.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<VolunteerTeamListBean>) {
                super.onFailed(response)
                mError.value = response
            }
        })
    }
}