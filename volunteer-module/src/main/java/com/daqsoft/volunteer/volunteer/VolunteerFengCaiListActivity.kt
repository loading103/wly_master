package com.daqsoft.volunteer.volunteer

import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.ServiceRecordBean
import com.daqsoft.volunteer.databinding.ActivityVolunteerFcListBinding
import com.daqsoft.volunteer.net.VolunteerRepository
import com.daqsoft.volunteer.volunteer.adapter.VolunteerFengCaiAdapter

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/8 14:22
 *@author: caihj
 *@des:志愿风采 从首页进入
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_FENG_CAI_LIST)
class VolunteerFengCaiListActivity:TitleBarActivity<ActivityVolunteerFcListBinding,VolunteerFCListVM>() {
    override fun getLayout(): Int = R.layout.activity_volunteer_fc_list

    override fun setTitle(): String = getString(R.string.volunteer_module_volunteer_service_record_list_title)

    override fun injectVm(): Class<VolunteerFCListVM> = VolunteerFCListVM::class.java

    override fun initView() {
        mModel.services.observe(this, Observer {
            dissMissLoadingDialog()
            if(it.isNotEmpty()){
                fcAdapter.add(it)
            }
        })
        mBinding.rvVolunteerService.layoutManager = LinearLayoutManager(this)
        mBinding.rvVolunteerService.adapter = fcAdapter

        mBinding.tvAll.onNoDoubleClick {
            val set = ConstraintSet()
            set.clone(mBinding.clTypeSelect)
            if(!mBinding.tvAll.isSelected){
                mBinding.tvAll.isSelected = true
                mBinding.tvCity.isSelected = false
                set.connect(mBinding.vIndicator.id, ConstraintSet.LEFT, mBinding.tvAll.id, ConstraintSet.LEFT)
                set.connect(mBinding.vIndicator.id, ConstraintSet.RIGHT, mBinding.tvAll.id, ConstraintSet.RIGHT)
                set.applyTo(mBinding.clTypeSelect)
            }
            mModel.region = ""
            fcAdapter.clear()
            mModel.getVolunteerService()
        }

        mBinding.tvCity.onNoDoubleClick {
            val set = ConstraintSet()
            set.clone(mBinding.clTypeSelect)
            if(!mBinding.tvCity.isSelected){
                mBinding.tvCity.isSelected = true
                mBinding.tvAll.isSelected = false
                set.connect(mBinding.vIndicator.id, ConstraintSet.LEFT, mBinding.tvCity.id, ConstraintSet.LEFT)
                set.connect(mBinding.vIndicator.id, ConstraintSet.RIGHT, mBinding.tvCity.id, ConstraintSet.RIGHT)
                set.applyTo(mBinding.clTypeSelect)
            }
            mModel.region = region
            fcAdapter.clear()
            mModel.getVolunteerService()
        }
    }

    val fcAdapter = VolunteerFengCaiAdapter(this)
    var region = ""
    override fun initData() {
        showLoadingDialog()
        mModel.getVolunteerService()
        region = SPUtils.getInstance().getString(SPKey.REGION)
    }
}

class VolunteerFCListVM:BaseViewModel(){

    var services = MutableLiveData<MutableList<ServiceRecordBean>>()
    var region = ""
    fun getVolunteerService(){
        val params = HashMap<String,String>()
        if(region.isNotEmpty())
          params["region"] = region
        VolunteerRepository.service.getVolunteerServiceRecordList(params).excute(object :BaseObserver<ServiceRecordBean>(){
            override fun onSuccess(response: BaseResponse<ServiceRecordBean>) {
                services.postValue(response.datas)
            }

        })
    }
}