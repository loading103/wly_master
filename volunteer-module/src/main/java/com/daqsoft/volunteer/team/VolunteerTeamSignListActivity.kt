package com.daqsoft.volunteer.team

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
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
import com.daqsoft.provider.view.BasePopupWindow
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerTeamSignInBean
import com.daqsoft.volunteer.databinding.ActivityVolunteerListBinding
import com.daqsoft.volunteer.databinding.ActivityVolunteerSignListBinding
import com.daqsoft.volunteer.databinding.ActivityVolunteerTeamListBinding
import com.daqsoft.volunteer.databinding.VolunteerTimeSelectPopuBinding
import com.daqsoft.volunteer.net.VolunteerRepository
import com.daqsoft.volunteer.team.adapter.VolunteerTeamSignAdapter
import com.daqsoft.volunteer.utils.JumpUtils
import com.daqsoft.volunteer.volunteer.fragment.VolunteerRegisterFragment1

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/3 10:01
 *@author: caihj
 *@des:志愿者团队签到列表
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_SIGN_LIST)
class VolunteerTeamSignListActivity:TitleBarActivity<ActivityVolunteerSignListBinding,VolunteerTeamSignVM>() {
    override fun getLayout(): Int = R.layout.activity_volunteer_sign_list

    override fun setTitle(): String = getString(R.string.volunteer_module_volunteer_team_list_sign_title)

    private val volunteerTeamSignAdapter = VolunteerTeamSignAdapter(this)
    private var areaListPopupWindow: AreaSelectPopupWindow? = null
    var selectPopupWindow: BasePopupWindow? = null

    override fun injectVm(): Class<VolunteerTeamSignVM> = VolunteerTeamSignVM::class.java

    override fun initView() {
        mModel.teamList.observe(this, Observer {
            mBinding.swRefreshActivities.isRefreshing = false
            dissMissLoadingDialog()
            if(!it.isNullOrEmpty()){
                pageDel(it)
            }
        })

        mModel.mError.observe(this, Observer {
            mBinding.swRefreshActivities.isRefreshing = false
            dissMissLoadingDialog()
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
                        mModel.mCurrPage = 1
                        mModel.areaSiteSwitch = item.siteId
                        mBinding?.rvSigns?.visibility = View.GONE
                        mBinding?.rvSigns?.smoothScrollToPosition(0)
                        volunteerTeamSignAdapter?.clear()
                        mModel.getTeamSignList()
                    }
                areaListPopupWindow!!.firstData = it
                var temp: MutableList<ChildRegion> = mutableListOf()
                temp.add(0, ChildRegion("", "不限", "", "", emptyList()))
                areaListPopupWindow!!.secondData = ArrayList(temp)
            }
        })

        mBinding.rvSigns.apply {
            layoutManager = LinearLayoutManager(this@VolunteerTeamSignListActivity)
            adapter = volunteerTeamSignAdapter
        }
        mBinding.llSearch.onNoDoubleClick {
            JumpUtils.gotoSearch()
        }
        mBinding.llArea.onNoDoubleClick {
            areaListPopupWindow?.show(mBinding.llArea)
        }
        mBinding.llTime.onNoDoubleClick {
            if (selectPopupWindow != null) {
                selectPopupWindow!!.resetDarkPosition()
                selectPopupWindow!!.darkBelow(mBinding.llTime)
                selectPopupWindow!!.showAsDropDown(mBinding.llTime)
            }
        }
        mBinding.swRefreshActivities.setOnRefreshListener {
            mModel.mCurrPage = 1
            volunteerTeamSignAdapter.clear()
            mModel.getTeamSignList()
        }

        volunteerTeamSignAdapter.setOnLoadMoreListener {
            mModel.mCurrPage ++
            mModel.getTeamSignList()
        }

        // 筛选
        val inflater = LayoutInflater.from(this)
        val selectBinding: VolunteerTimeSelectPopuBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.volunteer_time_select_popu,
            null,
            false
        )
        if (selectPopupWindow == null) {
            initTimeSelectPopup(selectBinding)
        }
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getTeamSignList()
        mModel.getChildRegions()
    }


    /**
     * 初始化时间选择窗体
     */
    private fun initTimeSelectPopup(mSelectBinding:VolunteerTimeSelectPopuBinding){
        selectPopupWindow = BasePopupWindow(
            mSelectBinding.root, LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT, true
        )
        // 设置背景
        selectPopupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 设置是否能够响应外部点击事件
        selectPopupWindow!!.isOutsideTouchable = true
        // 设置能否响应点击事件
        selectPopupWindow!!.isTouchable = true
        selectPopupWindow!!.isFocusable = true

        selectPopupWindow!!.resetDarkPosition()
        selectPopupWindow!!.darkBelow(mBinding.llTime)
        mSelectBinding.rvDays.setLabels(mModel.days)
        mSelectBinding.rvTimes.setLabels(mModel.times)
        mSelectBinding.rvDays.setOnLabelClickListener { label, data, position ->
            if(mModel.dayOfWeeks.contains("${position+1}")){
                mModel.dayOfWeeks.remove("${position+1}")
            }else{
                mModel.dayOfWeeks.add("${position+1}")
            }
        }
        mSelectBinding.rvTimes.setOnLabelClickListener { label, data, position ->
            if(mModel.dayOfTimes.contains(mModel.timesKey[position])){
                mModel.dayOfTimes.remove(mModel.timesKey[position])
            }else{
                mModel.dayOfTimes.add(mModel.timesKey[position])
            }
        }
        mSelectBinding.tvReset.onNoDoubleClick {
            mSelectBinding.rvDays.clearAllSelect()
            mSelectBinding.rvTimes.clearAllSelect()
            mBinding.tvTime.text = "时间筛选"
            mModel.dayOfWeeks.clear()
            mModel.dayOfTimes.clear()
        }
        mSelectBinding.tvSure.onNoDoubleClick {
            selectPopupWindow!!.dismiss()
            getSelectLabel()
            mModel.getTeamSignList()
        }
    }

    fun getSelectLabel(){
        val selectLabels = mutableListOf<String>()
        for (item in mModel.dayOfWeeks){
            selectLabels.add(mModel.days[item.toInt() - 1])
        }
        var labels = ""
        if(selectLabels.isNotEmpty()){
            labels = selectLabels.joinToString(",")
        }
        val selectTimes = mutableListOf<String>()
        for (item in mModel.dayOfTimes){
            selectTimes.add(mModel.times[mModel.timesKey.indexOf(item)])
        }
        if(labels.isNotEmpty()){
            if(selectTimes.isNotEmpty()){
                labels += "-" + selectTimes.joinToString(",")
            }
            mBinding.tvTime.text = labels
            return
        }else{
            if(selectTimes.isNotEmpty()){
                labels = selectTimes.joinToString(",")
                mBinding.tvTime.text = labels
                return
            }
        }
    }


    private fun pageDel(datas:MutableList<VolunteerTeamSignInBean>){
        if (mModel.mCurrPage == 1) {
            mBinding.rvSigns.smoothScrollToPosition(0)
            mBinding.rvSigns.visibility = View.VISIBLE
            volunteerTeamSignAdapter.clear()
            volunteerTeamSignAdapter.emptyViewShow = datas.isNullOrEmpty()
        }
        if (!datas.isNullOrEmpty()) {
            volunteerTeamSignAdapter.add(datas)
        }
        if (datas.isNullOrEmpty() || datas.size < mModel.mPageSize) {
            volunteerTeamSignAdapter.loadEnd()
        } else {
            volunteerTeamSignAdapter.loadComplete()
        }
        dissMissLoadingDialog()
    }
}

class VolunteerTeamSignVM:BaseViewModel(){
    var teamList = MutableLiveData<MutableList<VolunteerTeamSignInBean>>()
    var mCurrPage = 1
    var mPageSize = 10

    var dayOfWeeks = mutableListOf<String>()
    var dayOfTimes = mutableListOf<String>()
    val days = listOf<String>(
        "周一","周二","周三","周四","周五","周六","周日"
    )

    val times = listOf<String>(
       "上午","下午","晚上"
    )
    val timesKey = listOf<String>(
        "morning","afternoon","night"
    )
    fun getTeamSignList(){
        val params = HashMap<String,String>()
        params["pageSize"] = mPageSize.toString()
        params["currPage"] = mCurrPage.toString()
        if(currentArea!=""){
            params["teamRegion"] = currentArea
        }
        if(dayOfWeeks.isNotEmpty()){
            params["dayOfWeek"] = dayOfWeeks.joinToString(",")
        }
        if(dayOfTimes.isNotEmpty()){
            params["dateOfDay"] = dayOfTimes.joinToString(",")
        }
        VolunteerRepository.service.getVolunteerTeamSignList(params).excute(object :BaseObserver<VolunteerTeamSignInBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerTeamSignInBean>) {
                teamList.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<VolunteerTeamSignInBean>) {
                super.onFailed(response)
                mError.value = response
            }
        })
    }

    /**
     * 地区
     */
    val areas = MutableLiveData<MutableList<ChildRegion>>()
    /**
     * 选中的地区
     */
    var currentArea = ""

    var areaSiteSwitch: String? = ""

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