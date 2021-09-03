package com.daqsoft.volunteer.volunteer

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.view.ListPopupWindow
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerActivityBean
import com.daqsoft.volunteer.bean.VolunteerBean
import com.daqsoft.volunteer.databinding.ActivityVolunteerActivityListBinding
import com.daqsoft.volunteer.databinding.ActivityVolunteerListBinding
import com.daqsoft.volunteer.main.adapter.VolunteerActivityAdapter
import com.daqsoft.volunteer.net.VolunteerRepository
import com.daqsoft.volunteer.utils.JumpUtils
import com.daqsoft.volunteer.volunteer.fragment.VolunteerRegisterFragment1

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/3 10:01
 *@author: caihj
 *@des:志愿者招募列表
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_ACTIVITY_LIST )
class VolunteerActivityListActivity:TitleBarActivity<ActivityVolunteerActivityListBinding,VolunteerActivityVM>() {
    override fun getLayout(): Int = R.layout.activity_volunteer_activity_list

    override fun setTitle(): String = getString(R.string.volunteer_module_volunteer_activity_title)

    override fun injectVm(): Class<VolunteerActivityVM> = VolunteerActivityVM::class.java

    private val activityAdapter = VolunteerActivityAdapter(this)
    /**
     * 弹窗
     */
    private var areaListPopupWindow: AreaSelectPopupWindow? = null


    /**
     * 状态窗口
     */
    private var statusListPopupWindow: ListPopupWindow<Any>? = null

    override fun initView() {
        mBinding.rvActivities.layoutManager = LinearLayoutManager(this)
        mBinding.rvActivities.adapter = activityAdapter
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
                        mBinding?.rvActivities?.visibility = View.GONE
                        mBinding?.rvActivities?.smoothScrollToPosition(0)
                        activityAdapter?.clear()
                        mModel.getVolunteerActivityList()
                    }
                areaListPopupWindow!!.firstData = it
                var temp: MutableList<ChildRegion> = mutableListOf()
                temp.add(0, ChildRegion("", "不限", "", "", emptyList()))
                areaListPopupWindow!!.secondData = ArrayList(temp)
            }
        })

        mModel.activities.observe(this, Observer {
            mBinding.swRefreshActivities.isRefreshing = false
            pageDel(it)
        })

        mModel.mError.observe(this, Observer {
            mBinding.swRefreshActivities.isRefreshing = false
            dissMissLoadingDialog()
        })

        mModel.collectLiveData.observe(this, Observer {
            activityAdapter?.notifyUpdateCollectStatus(it, true)
        })
        mModel.canceCollectLiveData.observe(this, Observer {
            activityAdapter?.notifyUpdateCollectStatus(it, false)
        })

        mBinding.swRefreshActivities.setOnRefreshListener {
            mModel.mCurrPage = 1
            activityAdapter.clear()
            mModel.getVolunteerActivityList()
        }
        mBinding.llSearch.onNoDoubleClick {
            JumpUtils.gotoSearch()
        }
        mBinding.tvArea.onNoDoubleClick {
            areaListPopupWindow?.show(mBinding.tvArea)
        }
        mBinding.tvSort.onNoDoubleClick {
            statusListPopupWindow?.show()
        }
        initStatusPopWindow()
    }

    private fun pageDel(datas:MutableList<VolunteerActivityBean>){
        if (mModel.mCurrPage == 1) {
            mBinding.rvActivities.smoothScrollToPosition(0)
            mBinding.rvActivities.visibility = View.VISIBLE
            activityAdapter!!.clear()
            activityAdapter!!.emptyViewShow = datas.isNullOrEmpty()
        }
        if (!datas.isNullOrEmpty()) {
            activityAdapter!!.add(datas)
        }
        if (datas.isNullOrEmpty() || datas.size < mModel.mPageSize) {
            activityAdapter!!.loadEnd()
        } else {
            activityAdapter!!.loadComplete()
        }
        dissMissLoadingDialog()
    }


    override fun initData() {
        activityAdapter.onItemClick = object :VolunteerActivityAdapter.OnItemClickListener{
            override fun onItemClick(id: String, position: Int,status:Boolean) {
                if(status){
                    mModel.collectionCancel(id,position)
                }else{
                    mModel.collection(id,position)
                }
            }

        }
        mModel.getChildRegions()
        mModel.getVolunteerActivityList()
    }

    private fun initStatusPopWindow(){
        statusListPopupWindow = ListPopupWindow.getInstance(mBinding.tvSort, mModel.status) { item ->
            mBinding.tvSort.text = (item as ValueKeyBean).name
            // 当选择距离优先时需要加入自己的经纬度
            showLoadingDialog()
            mModel.mCurrPage = 1
            mModel.currentStatus = item.value
            activityAdapter.clear()
            mModel.getVolunteerActivityList()
        }
    }
}

class VolunteerActivityVM:BaseViewModel(){
    /**
     * 选中的地区
     */
    var currentArea = ""

    var currentStatus = ""

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
     * 状态类型
     */
    val status = mutableListOf(
        ValueKeyBean("不限", ""),
        ValueKeyBean("预告", "0"),
        ValueKeyBean("招募中", "3"),
        ValueKeyBean("结束", "2")
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


    var activities = MutableLiveData<MutableList<VolunteerActivityBean>>()

    /**
     * 志愿招募
     */
    fun getVolunteerActivityList(){
        val params = HashMap<String,String>()
        if(currentArea != ""){
            params["region"] = currentArea
        }
        if(currentStatus != ""){
            params["activityStatus"] = currentStatus
        }
        params["pageSize"] = mPageSize.toString()
        params["currPage"] =mCurrPage.toString()
        VolunteerRepository.service.getVolunteerActivityList(params).excute(object :
            BaseObserver<VolunteerActivityBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerActivityBean>) {
                activities.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<VolunteerActivityBean>) {
                super.onFailed(response)
                mError.value = response
            }

        })
    }


    val collectLiveData = MutableLiveData<Int>()

    val canceCollectLiveData = MutableLiveData<Int>()


    /**
     * 收藏接口
     */
    fun collection(resourceId: String, position: Int) {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        CommentRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_ACTIVITY_VOLUNT)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("收藏成功~")
                    collectLiveData.postValue(position)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("收藏失败，请稍后再试~")
                }
            })
    }

    /**
     * 取消收藏接口
     */
    fun collectionCancel(resourceId: String, position: Int) {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        CommentRepository.service.posCollectionCancel(
            resourceId,
            ResourceType.CONTENT_TYPE_ACTIVITY_VOLUNT
        )
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("取消收藏成功~")
                    canceCollectLiveData.postValue(position)

                }

                override fun onFailed(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("取消收藏失败，请稍后再试~")
                }
            })
    }

}