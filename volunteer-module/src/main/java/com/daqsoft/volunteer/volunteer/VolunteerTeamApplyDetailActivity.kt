package com.daqsoft.volunteer.volunteer

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath.VolunteerModule.Companion.VOLUNTEER_TEAM_APPLY_DETAIL
import com.daqsoft.provider.bean.UserLogin
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.BrandInfo
import com.daqsoft.volunteer.bean.VolunteerBriefInfoBean
import com.daqsoft.volunteer.bean.VolunteerTeamInfoBean
import com.daqsoft.volunteer.databinding.ActivityVolunteerTeamApplyDetailBinding
import com.daqsoft.volunteer.net.VolunteerRepository

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/17 14:56
 *@author: caihj
 *@des:志愿者审核详细信息
 **/
@Route(path = VOLUNTEER_TEAM_APPLY_DETAIL)
class VolunteerTeamApplyDetailActivity:TitleBarActivity<ActivityVolunteerTeamApplyDetailBinding,VolunteerTeamApplyDetailVM>(){

    override fun getLayout(): Int = R.layout.activity_volunteer_team_apply_detail

    override fun setTitle(): String = "申请记录详情"

    override fun injectVm(): Class<VolunteerTeamApplyDetailVM> = VolunteerTeamApplyDetailVM::class.java

    @SuppressLint("ResourceAsColor")
    override fun initView() {
        mModel.volunteerDetail.observe(this, Observer {
            mBinding.tvTeamname.text = it.teamName
            mBinding.tvServiceRegion.text = it.serviceRegionName
            mBinding.tvVolunteerRegion.text = it.attributionName
            mBinding.tvServiceType.text = it.serviceType.joinToString(",")
            mBinding.tvTeamCreateTime.text = it.establishTime
            if(it.manageUnit.isNullOrEmpty()){
                mBinding.llUnit.visibility = View.GONE
            }else{
                mBinding.tvNuit.text = it.manageUnit
            }
            if(it.teamIcon.isNullOrEmpty()){
                mBinding.llLogo.visibility = View.GONE
            }else{
                Glide.with(this).load(it.teamIcon).into(mBinding.ivLogo)
            }
            mBinding.tvAddress.text = it.teamRegionName
            mBinding.tvContact.text = it.teamPhone
            if(it.teamItemBrandList.isNullOrEmpty()){
                mBinding.llBrand.visibility = View.GONE
            }else{
                mBinding.tvBrand.text =getBrandStr(it.teamItemBrandList)
            }
            if(it.teamIntroduce.isNullOrEmpty()){
                mBinding.llIntroduce.visibility = View.GONE
            }else{
                mBinding.tvIntroduce.text = it.teamIntroduce
            }
            if (it.status == 1) {
                mBinding.tvSubmit.visibility = View.GONE
            } else {
                if (it.status == 4) {
                    mBinding.tvApplyStatus.visibility = View.VISIBLE
                    mBinding.tvSubmit.onNoDoubleClick {
                        mModel.cancelApply()
                    }
                } else if (it.status == 1) {
                    mBinding.tvSubmit.visibility = View.GONE
                    mBinding.tvApplyStatus.visibility = View.GONE
                }
                if(it.status == 79){
                    mBinding.tvSubmit.setBackgroundColor(R.color.ff4e4e)
                    mBinding.clStatus.visibility = View.VISIBLE
                }

                mBinding.tvSubmit.text = when (it.status) {
                    79 -> "修改申请"
                    else -> "撤回申请"
                }
            }

        })
        mModel.cancelStatus.observe(this, Observer {
            if(it){
                finish()
            }
        })
    }

    override fun initData() {
        mModel.getVolunteer()
    }

    private fun getBrandStr(brands:List<BrandInfo>):String{
       val brand = mutableListOf<String>()
        for (item in brands){
            brand.add(item.itemBrandName)
        }
        return  brand.joinToString(",")
    }

}

class VolunteerTeamApplyDetailVM:BaseViewModel(){
    var cancelStatus = MutableLiveData<Boolean>()
    fun cancelApply(){
        VolunteerRepository.service.volunteerTeamCancel().excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                ToastUtils.showMessage(response.message)
                cancelStatus.postValue(response.code == 0)
            }

        })
    }

    var volunteerDetail = MutableLiveData<VolunteerTeamInfoBean>()

    /**
     * 获取志愿者信息
     */
    fun getVolunteer(){
        VolunteerRepository.service.getVolunteerTeamInfo().excute(object :BaseObserver<VolunteerTeamInfoBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerTeamInfoBean>) {
                volunteerDetail.postValue(response.data)
            }
        })
    }

//    val userInfo = MutableLiveData<UserLogin>()
//
//    /**
//     * 刷新用户信息
//     */
//    fun refreshToken() {
//        UserRepository.userService.refreshToken().excute(object : BaseObserver<UserLogin>() {
//            override fun onSuccess(response: BaseResponse<UserLogin>) {
//                userInfo.postValue(response.data)
//            }
//        })
//    }
}