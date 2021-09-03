package com.daqsoft.volunteer.volunteer

import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.ARouterPath
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerActivityBean
import com.daqsoft.volunteer.bean.VolunteerTeamBean
import com.daqsoft.volunteer.databinding.ActivityVolunteerFocusListBinding
import com.daqsoft.volunteer.net.VolunteerRepository

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/8 17:03
 *@author: caihj
 *@des:我的关注
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_FOCUS_LIST)
class VolunteerFocusListActivity:TitleBarActivity<ActivityVolunteerFocusListBinding,VolunteerFocusVM>() {
    override fun getLayout(): Int = R.layout.activity_volunteer_focus_list

    override fun setTitle(): String = getString(R.string.volunteer_module_volunteer_focus_title)

    override fun injectVm(): Class<VolunteerFocusVM> = VolunteerFocusVM::class.java

    override fun initView() {
    }

    override fun initData() {
        mModel.getVolunteerFocusActivityList()
    }
}

class VolunteerFocusVM:BaseViewModel(){
    var volunteerFocus = MutableLiveData<MutableList<VolunteerActivityBean>>()


    /**
     * 获取页码大小
     */
    var mPageSize = 10

    /**
     * 活动分页页码
     */
    var mCurrPage = 1

    /**
     * 志愿
     */
    fun getVolunteerFocusActivityList(){
        val params = HashMap<String,String>()
        params["pageSize"] = mPageSize.toString()
        params["currPage"] =mCurrPage.toString()
        VolunteerRepository.service.getVolunteerActivityList(params).excute(object :
            BaseObserver<VolunteerActivityBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerActivityBean>) {
                volunteerFocus.postValue(response.datas)
            }

        })
    }

}