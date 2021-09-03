package com.daqsoft.volunteer.volunteer

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
import com.daqsoft.volunteer.databinding.ActivityVolunteerListBinding
import com.daqsoft.volunteer.net.VolunteerRepository
import com.daqsoft.volunteer.utils.JumpUtils
import com.daqsoft.volunteer.volunteer.adapter.VolunteerAdapter

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/3 10:01
 *@author: caihj
 *@des:志愿者列表
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_LIST)
class VolunteerListActivity:TitleBarActivity<ActivityVolunteerListBinding,VolunteerVM>() {

    /**
     * 弹窗
     */
    private var areaListPopupWindow: AreaSelectPopupWindow? = null

    /**
     * 排序窗口
     */
    private var sortListPopupWindow: ListPopupWindow<Any>? = null

    /**
     * 等级窗口
     */
    private var levelListPopupWindow: ListPopupWindow<Any>? = null


    var volunteerAdapter:VolunteerAdapter? =null

    override fun getLayout(): Int = R.layout.activity_volunteer_list

    override fun setTitle(): String = getString(R.string.volunteer_module_volunteer_list_title)

    override fun injectVm(): Class<VolunteerVM> = VolunteerVM::class.java

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
                        mBinding?.rvVolunteer?.visibility = View.GONE
                        mBinding?.rvVolunteer?.smoothScrollToPosition(0)
                        volunteerAdapter?.clear()
                        mModel.getVolunteers()
                    }
                areaListPopupWindow!!.firstData = it
                var temp: MutableList<ChildRegion> = mutableListOf()
                temp.add(0, ChildRegion("", "不限", "", "", emptyList()))
                areaListPopupWindow!!.secondData = ArrayList(temp)
            }
        })
        volunteerAdapter = VolunteerAdapter(this)
        mBinding.rvVolunteer.layoutManager = LinearLayoutManager(this)
        mBinding.rvVolunteer.adapter = volunteerAdapter

        mModel.volunteers.observe(this, Observer {
            mBinding.swRefreshActivities.isRefreshing = false
            pageDel(it)
        })

        mModel.mError.observe(this, Observer {
            mBinding.swRefreshActivities.isRefreshing = false
            dissMissLoadingDialog()
        })
        initSortPopWindow()
        initLevelPopWindow()
        initEvent()
    }

    private fun initSortPopWindow(){
        sortListPopupWindow = ListPopupWindow.getInstance(mBinding.llSort, mModel.sorts) { item ->
            mBinding.tvSort.text = (item as ValueKeyBean).name
            // 当选择距离优先时需要加入自己的经纬度
                showLoadingDialog()
            mModel.mCurrPage = 1
            mModel.currentSort = item.value
                mModel.getVolunteers()
        }
    }

    private fun initLevelPopWindow(){
        levelListPopupWindow = ListPopupWindow.getInstance(mBinding.llLevel, mModel.levels) { item ->
            mBinding.tvLevel.text = (item as ValueKeyBean).name
            showLoadingDialog()
            mModel.currentLevel = item.value
            mModel.mCurrPage = 1
            mModel.getVolunteers()
        }
    }

    override fun initData() {
        mModel.getChildRegions()
        showLoadingDialog()
        mModel.getVolunteers()
    }

    private fun initEvent(){
        mBinding.swRefreshActivities.setOnRefreshListener {
            mModel.mCurrPage = 1
            volunteerAdapter!!.clear()
            showLoadingDialog()
            mModel.getVolunteers()
        }

        volunteerAdapter!!.setOnLoadMoreListener {
            mModel.mCurrPage++
            mModel.getVolunteers()
        }

        mBinding.llArea.onNoDoubleClick {
            if (areaListPopupWindow != null) {
                areaListPopupWindow!!.show(mBinding.llArea)
            }
        }
        mBinding.llSort.onNoDoubleClick {
            if(sortListPopupWindow != null){
                sortListPopupWindow!!.show()
            }
        }
        mBinding.llLevel.onNoDoubleClick {
            if(levelListPopupWindow != null){
                levelListPopupWindow!!.show()
            }
        }
        mBinding.llSearch.onNoDoubleClick {
           JumpUtils.gotoSearch()
        }
    }

    private fun pageDel(datas:MutableList<VolunteerBean>){
        if (mModel.mCurrPage == 1) {
            mBinding.rvVolunteer.smoothScrollToPosition(0)
            mBinding.rvVolunteer.visibility = View.VISIBLE
            volunteerAdapter!!.clear()
            volunteerAdapter!!.emptyViewShow = datas.isNullOrEmpty()

        }
        if (!datas.isNullOrEmpty()) {
            volunteerAdapter!!.add(datas)
        }
        if (datas.isNullOrEmpty() || datas.size < mModel.mPageSize) {
            volunteerAdapter!!.loadEnd()
        } else {
            volunteerAdapter!!.loadComplete()
        }
        dissMissLoadingDialog()
    }

}

class VolunteerVM:BaseViewModel(){

    /**
     * 选中的地区
     */
    var currentArea = ""
    /**
     * 选中的排序方式
     */
    var currentSort = ""

    var currentLevel = "0"

    /**
     * 获取页码大小
     */
    var mPageSize = 10

    /**
     * 活动分页页码
     */
    var mCurrPage = 1

    var areaSiteSwitch: String? = ""

    /**
     * 排序类型
     */
    val sorts = mutableListOf(
        ValueKeyBean("不限", ""),
        ValueKeyBean("次数优先", "2"),
        ValueKeyBean("时长优先", "3"),
        ValueKeyBean("最新", "4")
    )

    /**
     * 等级
     */
    val levels = mutableListOf(
        ValueKeyBean("全部", "0"),
        ValueKeyBean("五星级", "5"),
        ValueKeyBean("四星级", "4"),
        ValueKeyBean("三星级", "3"),
        ValueKeyBean("二星级", "2"),
        ValueKeyBean("一星级", "1")
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

    val volunteers = MutableLiveData<MutableList<VolunteerBean>>()

    /**
     * 获取志愿者列表
     */
    fun getVolunteers(){
        val params = HashMap<String,String>()

        params["pageSize"] = mPageSize.toString()
        params["currPage"] = mCurrPage.toString()
        if(currentArea != ""){
            params["region"] = currentArea
        }
        if(currentSort != ""){
            params["sortType"] = currentSort
        }
        params["level"] = currentLevel

        VolunteerRepository.service.getVolunteerList(params)
            .excute(object :BaseObserver<VolunteerBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerBean>) {
                volunteers.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<VolunteerBean>) {
                super.onFailed(response)
                mError.value = response
            }
        })
    }


}