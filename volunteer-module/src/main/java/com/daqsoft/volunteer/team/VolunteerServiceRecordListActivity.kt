package com.daqsoft.volunteer.team

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
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
import com.daqsoft.volunteer.bean.ServiceRecordBean
import com.daqsoft.volunteer.databinding.ActivityVolunteerServiceRecordListBinding
import com.daqsoft.volunteer.net.VolunteerRepository
import com.daqsoft.volunteer.team.adapter.VolunteerTeamAdapter
import com.daqsoft.volunteer.team.adapter.VolunteerTeamServiceAdapter

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/8 10:18
 *@author: caihj
 *@des:志愿者风采
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_SERVICE_RECORD_LIST)
class VolunteerServiceRecordListActivity:TitleBarActivity<ActivityVolunteerServiceRecordListBinding, VolunteerServiceRecordListVM>() {

   @Autowired
   @JvmField
   var id = ""
    /**
     * 弹窗
     */
    private var areaListPopupWindow: AreaSelectPopupWindow? = null
    var serviceAdapter = VolunteerTeamServiceAdapter(this)

    override fun getLayout(): Int = R.layout.activity_volunteer_service_record_list

    override fun setTitle(): String = getString(R.string.volunteer_module_volunteer_service_record_list_title)

    override fun injectVm(): Class<VolunteerServiceRecordListVM> = VolunteerServiceRecordListVM::class.java

    override fun initView() {
        mModel.services.observe(this, Observer {
            if(it.size > 0){
                serviceAdapter.add(it)
            }
        })
        mBinding.rvServices.apply {
            layoutManager = GridLayoutManager(this@VolunteerServiceRecordListActivity,2)
            adapter = serviceAdapter
        }
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
                        mBinding?.rvServices?.visibility = View.GONE
                        mBinding?.rvServices?.smoothScrollToPosition(0)
                        serviceAdapter?.clear()
                        mModel.getVolunteerService(id)
                    }
                areaListPopupWindow!!.firstData = it
                var temp: MutableList<ChildRegion> = mutableListOf()
                temp.add(0, ChildRegion("", "不限", "", "", emptyList()))
                areaListPopupWindow!!.secondData = ArrayList(temp)
            }
        })
        initSortPopWindow()
        mBinding.tvArea.onNoDoubleClick {
            if (areaListPopupWindow != null) {
                areaListPopupWindow!!.show(mBinding.tvArea)
            }
        }
        mBinding.tvSort.onNoDoubleClick {
            if(sortListPopupWindow != null){
                sortListPopupWindow!!.show()
            }
        }
    }

    override fun initData() {
        mModel.getVolunteerService(id)
        mModel.getChildRegions()
    }

    /**
     * 排序窗口
     */
    private var sortListPopupWindow: ListPopupWindow<Any>? = null

    private fun initSortPopWindow(){
        sortListPopupWindow = ListPopupWindow.getInstance(mBinding.tvSort, mModel.sorts) { item ->
            mBinding.tvSort.text = (item as ValueKeyBean).name
            // 当选择距离优先时需要加入自己的经纬度
            showLoadingDialog()
            mModel.mCurrPage = 1
            mModel.currentSort = item.value
            mModel.getVolunteerService(id)
        }
    }
}

class VolunteerServiceRecordListVM:BaseViewModel(){

    var services = MutableLiveData<MutableList<ServiceRecordBean>>()


    /**
     * 排序类型
     */
    val sorts = mutableListOf(
        ValueKeyBean("不限", ""),
        ValueKeyBean(" 首页列表 ", "1"),
        ValueKeyBean("距离优先", "2"),
        ValueKeyBean("人气优先", "3")
    )

    fun getVolunteerService(id:String){
        val params = HashMap<String,String>()
        params["teamId"] = id
        params["region"] = currentArea
        params["orderType"] = currentSort
        VolunteerRepository.service.getVolunteerServiceRecordList(params).excute(object : BaseObserver<ServiceRecordBean>(){
            override fun onSuccess(response: BaseResponse<ServiceRecordBean>) {
                services.postValue(response.datas)
            }

        })
    }


    /**
     * 选中的地区
     */
    var currentArea = ""

    var currentStatus = ""

    /**
     * 选中的排序方式
     */
    var currentSort = ""

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



}