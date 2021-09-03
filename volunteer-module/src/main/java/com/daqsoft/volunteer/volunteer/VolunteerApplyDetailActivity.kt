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
import com.daqsoft.provider.ARouterPath.VolunteerModule.Companion.VOLUNTEER_APPLY_DETAIL
import com.daqsoft.provider.bean.UserLogin
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerBriefInfoBean
import com.daqsoft.volunteer.databinding.ActivityVolunteerApplyDetailBinding
import com.daqsoft.volunteer.net.VolunteerRepository

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/17 14:56
 *@author: caihj
 *@des:志愿者审核详细信息
 **/
@Route(path = VOLUNTEER_APPLY_DETAIL)
class VolunteerApplyDetailActivity : TitleBarActivity<ActivityVolunteerApplyDetailBinding, VolunteerApplyDetailVM>() {

    override fun getLayout(): Int = R.layout.activity_volunteer_apply_detail

    override fun setTitle(): String = "申请记录详情"

    override fun injectVm(): Class<VolunteerApplyDetailVM> = VolunteerApplyDetailVM::class.java

    @SuppressLint("ResourceAsColor")
    override fun initView() {
        mModel.volunteerDetail.observe(this, Observer {
            mBinding.tvName.text = it.name
            mBinding.tvSex.text = it.sex
            mBinding.tvNational.text = it.nation
            mBinding.tvIdcard.text = it.idCard
            mBinding.tvPhone.text = it.phone
            mBinding.tvServiceRegion.text = it.serviceRegionName
            mBinding.tvVolunteerRegion.text = it.attributionName
            if (it.education.isNullOrEmpty()) {
                mBinding.llEducation.visibility = View.GONE
            } else {
                mBinding.tvEducation.text = it.education
            }
            if (it.school.isNullOrEmpty()) {
                mBinding.llSchool.visibility = View.GONE
            } else {
                mBinding.tvSchool.text = it.school
            }
            mBinding.tvJobStatus.text = it.employmentStatus
            if (it.idCardPortrait.isNullOrEmpty()) {
                mBinding.tvCard.visibility = View.GONE
                mBinding.llCard.visibility = View.GONE
            } else {
                Glide.with(this).load(it.idCardPortrait).into(mBinding.mIdCardPositiveIv)
                Glide.with(this).load(it.idCardNationalEmblem).into(mBinding.mIdCardNegativeIv)
            }

            mBinding.tvContactName.text = it.emergencyContactName
            mBinding.tvContactPhone.text = it.emergencyContactPhone
            if (it.healthStatus.isNullOrEmpty()) {
                mBinding.llHealth.visibility = View.GONE
            } else {
                mBinding.tvHealth.text = it.healthStatus
            }
            if (it.language.isNullOrEmpty()) {
                mBinding.llLanguage.visibility = View.GONE
            } else {
                mBinding.tvLanguage.text = it.language.joinToString(",")
            }
            mBinding.tvSkill.text = it.specialty.joinToString(",")
            mBinding.tvServiceTime.text = getServiceyStr(it.serviceTimeType)
            mBinding.tvServiceType.text = it.serviceIntention.joinToString(",")
            if (it.email.isNullOrEmpty()) {
                mBinding.llEmail.visibility = View.GONE
            } else {
                mBinding.tvEmail.text = it.email
            }
            if (it.qq.isNullOrEmpty()) {
                mBinding.llQq.visibility = View.GONE
            } else {
                mBinding.tvQq.text = it.qq
            }

            if (it.regionName.isNullOrEmpty()) {
                mBinding.llAddress.visibility = View.GONE
            } else {
                mBinding.tvAddress.text = it.regionName
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
            if (it) {
                finish()
            }
        })

    }

    override fun initData() {
        mModel.getVolunteer()
//        mModel.refreshToken()
    }

    fun getServiceyStr(key: String) = when (key) {
        "Unlimited" -> "不限"
        "RestDay" -> "休息日"
        else -> "工作日"
    }
}

class VolunteerApplyDetailVM : BaseViewModel() {
    var cancelStatus = MutableLiveData<Boolean>()
    fun cancelApply() {
        VolunteerRepository.service.volunteerCancel().excute(object : BaseObserver<String>() {
            override fun onSuccess(response: BaseResponse<String>) {
                ToastUtils.showMessage(response.message)
                cancelStatus.postValue(response.code == 0)
            }

        })
    }

    var volunteerDetail = MutableLiveData<VolunteerBriefInfoBean>()

    /**
     * 获取志愿者信息
     */
    fun getVolunteer() {
        VolunteerRepository.service.getVolunteerInfo().excute(object : BaseObserver<VolunteerBriefInfoBean>() {
            override fun onSuccess(response: BaseResponse<VolunteerBriefInfoBean>) {
                volunteerDetail.postValue(response.data)
            }
        })
    }
//
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