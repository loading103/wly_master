package com.daqsoft.volunteer.volunteer

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.bean.LocationData
import com.daqsoft.baselib.utils.LocationUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.utils.SelectImageUtils
import com.daqsoft.provider.view.BasePopupWindow
import com.daqsoft.provider.view.ItemView
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.databinding.ActivityVolunteerInformationBinding
import com.daqsoft.volunteer.databinding.VolunteerRegisterSelectPopuBinding
import com.daqsoft.volunteer.utils.JumpUtils
import com.daqsoft.volunteer.utils.StringUtils
import com.daqsoft.volunteer.volunteer.vm.VolunteerInformationVM
import com.daqsoft.volunteer.volunteer.vm.VolunteerUpdateInformationVM
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import java.io.File

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/29 9:13
 *@author: caihj
 *@des:志愿者基本资料
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_INFORMATION)
class VolunteerInformationActivity:TitleBarActivity<ActivityVolunteerInformationBinding,VolunteerInformationVM>() {
    override fun getLayout(): Int = R.layout.activity_volunteer_information

    override fun setTitle(): String = getString(R.string.volunteer_information_title)

    override fun injectVm(): Class<VolunteerInformationVM> = VolunteerInformationVM::class.java

    override fun initView() {
        mModel.volunteer.observe(this, Observer {
            dissMissLoadingDialog()
            Glide.with(this).load(it.head).placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.avUserHead)
            mBinding.ivName.content = it.name
            mBinding.ivSex.content = it.sex
            mBinding.ivNational.content = it.nation
            mBinding.ivRegDate.content = it.operateTime
            mBinding.ivVolunteerId.content = it.sn
            mBinding.ivIdCard.content = it.idCard
            mModel.serviceRegion = it.serviceRegion
            mModel.getAttributions()

            if(!it.idCardNationalEmblem.isNullOrEmpty()){
                mBinding.ivIdCardPhoto.content = "已上传"
            }else{
                mBinding.ivIdCardPhoto.content = "未上传"
            }
            if(!it.emergencyContactName.isNullOrEmpty()){
                mBinding.ivContact.content = "${it.emergencyContactName}(${it.emergencyContactPhone})"
            }else{
                mBinding.ivContact.content = "去完善"
            }
            if(it.level == 0){
                mBinding.ivLevel.content = "暂无"
            }else{
                mBinding.ivLevel.content = StringUtils.getVolunteerLevelStr(it.level.toString())

            }
            parseData(it.serviceRegionName, mBinding.ivServiceRegion)
            parseData(it.attributionName, mBinding.ivVolunteerRegion)
            parseData(it.education, mBinding.ivEducation)
            parseData(it.school,mBinding.ivSchool)
            parseData(it.employmentStatus,mBinding.ivJob)
            parseData(it.specialty.joinToString(","),mBinding.ivSpecial)
            parseData(it.healthStatus,mBinding.ivHealth)
            parseData(it.language.joinToString(","),mBinding.ivLanguage)
            parseData(StringUtils.getServiceTypeName(it.serviceTimeType),mBinding.ivServiceTime)
            parseData(it.serviceIntention.joinToString(","),mBinding.ivServiceType)
            parseData(it.email,mBinding.ivEmail)
            parseData(it.qq,mBinding.ivQq)
            parseData(it.address,mBinding.ivAddressDetail)


        })

        mModel.headLocal.observe(this, Observer {
            val file = File(it)
            mModel.upLoadFile(file)
        })

        mModel.updateStatus.observe(this, Observer {
            dissMissLoadingDialog()
            if(it){
                mModel.getVolunteerInfo()
            }
        })

        mModel.skills.observe(this, Observer {
            initSkillSelectPopup(it)
        })

        mModel.languages.observe(this, Observer {
            initLanguageSelectPopup(it)
        })

        mModel.attributions.observe(this, Observer {
            if(!it.isNullOrEmpty()){
                val attrs = it
                val attributions = attrs.map {it1 ->
                    it1.name
                }
                attributionPv.setPicker(attributions.toList())
            }
        })
        mModel.educations.observe(this, Observer {
            if(!it.isNullOrEmpty()){
                educationPv.setPicker(it)
            }
        })

        mModel.jobStatus.observe(this, Observer {
            jobStatusPv.setPicker(it)
        })

        mModel.healths.observe(this, Observer {
            healthStatusPv.setPicker(it)
        })
        mModel.serviceIntentions.observe(this, Observer {
            initServiceSelectPopup(it)
        })
        initEvent()
    }

    private fun parseData(value:String, view:ItemView){
        if(!value.isNullOrEmpty()){
            view.content = value
        }else{
            view.content = "去完善"
        }
    }

    override fun onResume() {
        super.onResume()
        mModel.getVolunteerInfo()
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getVolunteerInfo()
        mModel.getEducations()
        mModel.getJobStatus()
        mModel.getSkills()
        mModel.getHealths()
        mModel.getLanguages()
        mModel.getServiceIntentions()
    }

    private fun initEvent(){
        mBinding.clHead.onNoDoubleClick {
            headUrlUpdate()
        }
        mBinding.ivContact.onNoDoubleClick {
            JumpUtils.gotoUpdateVolunteerContact(mModel.volunteer?.value?.emergencyContactName!!,mModel.volunteer?.value?.emergencyContactPhone!!)
        }
        mBinding.ivServiceRegion.onNoDoubleClick {
            updateArea()
        }
        mBinding.ivVolunteerRegion.onNoDoubleClick {
            showAttribution()
        }
        mBinding.ivEducation.onNoDoubleClick {
            showEducation()
        }
        mBinding.ivSchool.onNoDoubleClick {
            JumpUtils.gotoUpdateVolunteerInfo(VolunteerUpdateInformationVM.SCHOOL,mModel.volunteer?.value?.school!!)
        }
        mBinding.ivEmail.onNoDoubleClick {
            JumpUtils.gotoUpdateVolunteerInfo(VolunteerUpdateInformationVM.EMAIL,mModel.volunteer?.value?.email!!)
        }
        mBinding.ivQq.onNoDoubleClick {
            JumpUtils.gotoUpdateVolunteerInfo(VolunteerUpdateInformationVM.QQ,mModel.volunteer?.value?.qq!!)
        }
        mBinding.ivJob.onNoDoubleClick {
            showJobStatus()
        }
        mBinding.ivSpecial.onNoDoubleClick {
            skillPopupWindow?.showAtLocation(mBinding.root,Gravity.BOTTOM,0,0)
        }
        mBinding.ivHealth.onNoDoubleClick {
            showHealthStatus()
        }
        mBinding.ivLanguage.onNoDoubleClick {
            languagePopupWindow?.showAtLocation(mBinding.root,Gravity.BOTTOM,0,0)
        }
        mBinding.ivServiceTime.onNoDoubleClick {
            showServiceTimes()
        }
        mBinding.ivServiceType.onNoDoubleClick {
            servicePopupWindow?.showAtLocation(mBinding.root,Gravity.BOTTOM,0,0)
        }
    }


    /**
     * 跳转相机请求码
     */
    private val REQUEST_CAMERA_CODE = 0
    /**
     * 跳转相册请求码
     */
    private val REQUEST_ALBUM_CODE = 1

    /**
     * 底部头像选择弹框
     */
    private val popupWindow by lazy {
        SelectImageUtils.initPictureSelectPop(this, REQUEST_CAMERA_CODE, REQUEST_ALBUM_CODE)
    }

    private fun headUrlUpdate() {
        // 头像
        popupWindow.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ALBUM_CODE) {
            // 相册
            if (data == null) {
                return
            }
            mModel.headLocal.postValue(
                SelectImageUtils.getFileFromAlbum(this, data)
                    ?: ""
            )
        }
        if (requestCode == REQUEST_CAMERA_CODE) {
            // 相机
            val file = SelectImageUtils.getFileFromCamera()
            if (file?.exists() == false) {
                return
            }
            mModel.headLocal.postValue(file?.absolutePath ?: "")
        }
    }

    /**
     * 地区点击修改
     */
    private fun updateArea() {
        LocationUtil(this!! as RxAppCompatActivity, object : LocationUtil.OnLocationSelectListener {
            override fun onLocationSelect(region: LocationData) {
                if(region.memo == region.name){
                    ToastUtils.showMessage("不能只选择省级!")
                }else{
                    mBinding.ivServiceRegion.content = region.memo
                    mModel.serviceRegion = region.region
                    mModel.attributions.value?.clear()
                    mModel.getAttributions()
                }
            }

        }, mModel.mPresenter)

    }

    private fun showAttribution(){
        attributionPv.setSelectOptions(0)
        UIHelperUtils.showOptionsPicker(this, attributionPv)
    }

    /**
     * 志愿归属选择器
     */
    private val attributionPv by lazy {
        val pV = OptionsPickerBuilder(this, OnOptionsSelectListener { s1, s2, s3, v ->
            mBinding.ivVolunteerRegion.content = mModel.attributions.value?.get(s1)?.name ?: ""
            mModel.attribution = mModel.attributions.value?.get(s1)?.id ?: ""
            showLoadingDialog()
            mModel.updateServiceRegionAndAttribution()
        }).build<String>()
        pV
    }

    /**
     * 学历选择器
     */
    private val educationPv by lazy {
        val pV = OptionsPickerBuilder(this, OnOptionsSelectListener { s1, s2, s3, v ->
            mBinding.ivEducation.content = mModel.educations.value?.get(s1) ?: ""
            showLoadingDialog()
            mModel.updateVolunteerInfo(VolunteerUpdateInformationVM.education,mModel.educations.value?.get(s1) ?: "")
        }).build<String>()
        pV
    }

    private fun showEducation(){
        educationPv.setSelectOptions(0)
        UIHelperUtils.showOptionsPicker(this, educationPv)
    }


    /**
     * 就业选择器
     */
    private val jobStatusPv by lazy {
        val pV = OptionsPickerBuilder(this, OnOptionsSelectListener { s1, s2, s3, v ->
            mBinding.ivJob.content = mModel.jobStatus.value?.get(s1) ?: ""
            showLoadingDialog()
            mModel.updateVolunteerInfo(VolunteerUpdateInformationVM.EMPLOYMENTSTATUS,mModel.jobStatus.value?.get(s1) ?: "")
        }).build<String>()
        pV
    }

    private fun showJobStatus(){
        jobStatusPv.setSelectOptions(0)
        UIHelperUtils.showOptionsPicker(this, jobStatusPv)
    }


    var skillPopupWindow: BasePopupWindow? = null

    /**
     * 初始化个人特长选择窗体
     */
    private fun initSkillSelectPopup(skills:List<String>){
        val inflater = LayoutInflater.from(this)
        val mSelectBinding: VolunteerRegisterSelectPopuBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.volunteer_register_select_popu,
            null,
            false
        )
        skillPopupWindow = BasePopupWindow(
            mSelectBinding.root, LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT, true
        )
        // 设置背景
        skillPopupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 设置是否能够响应外部点击事件
        skillPopupWindow!!.isOutsideTouchable = true
        // 设置能否响应点击事件
        skillPopupWindow!!.isTouchable = true
        skillPopupWindow!!.isFocusable = true

        skillPopupWindow!!.resetDarkPosition()
        skillPopupWindow!!.darkBelow(mBinding.ivSpecial)
        mSelectBinding.rvDatas.setLabels(skills)
        mSelectBinding.rvDatas.setOnLabelClickListener { label, data, position ->
        }

        mSelectBinding.tvReset.onNoDoubleClick {
            mSelectBinding.rvDatas.clearAllSelect()
        }
        mSelectBinding.tvSure.onNoDoubleClick {
           val specialty = mSelectBinding.rvDatas.getSelectLabelDatas<String>().joinToString(",")
            mBinding.ivSpecial.content = specialty
            showLoadingDialog()
            mModel.updateVolunteerInfo(VolunteerUpdateInformationVM.SPECIALTY,specialty)
            skillPopupWindow!!.dismiss()
        }
    }



    /**
     * 健康状况选择器
     */
    private val healthStatusPv by lazy {
        val pV = OptionsPickerBuilder(this, OnOptionsSelectListener { s1, s2, s3, v ->
            mBinding.ivHealth.content = mModel.healths.value?.get(s1) ?: ""
            showLoadingDialog()
            mModel.updateVolunteerInfo(VolunteerUpdateInformationVM.HEALTHSTATUS,mModel.healths.value?.get(s1) ?: "")
        }).build<String>()
        pV
    }

    private fun showHealthStatus(){
        healthStatusPv.setSelectOptions(0)
        UIHelperUtils.showOptionsPicker(this, healthStatusPv)
    }

    var languagePopupWindow: BasePopupWindow? = null

    /**
     * 初始化语言选择窗体
     */
    private fun initLanguageSelectPopup(language:List<String>){
        val inflater = LayoutInflater.from(this)
        val mSelectBinding: VolunteerRegisterSelectPopuBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.volunteer_register_select_popu,
            null,
            false
        )
        languagePopupWindow = BasePopupWindow(
            mSelectBinding.root, LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT, true
        )
        // 设置背景
        languagePopupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 设置是否能够响应外部点击事件
        languagePopupWindow!!.isOutsideTouchable = true
        // 设置能否响应点击事件
        languagePopupWindow!!.isTouchable = true
        languagePopupWindow!!.isFocusable = true

        languagePopupWindow!!.resetDarkPosition()
        languagePopupWindow!!.darkBelow(mBinding.ivLanguage)
        mSelectBinding.tvTypeLabel.text ="擅长语言"
        mSelectBinding.rvDatas.setLabels(language)
        mSelectBinding.rvDatas.setOnLabelClickListener { label, data, position ->
        }

        mSelectBinding.tvReset.onNoDoubleClick {
            mSelectBinding.rvDatas.clearAllSelect()
        }
        mSelectBinding.tvSure.onNoDoubleClick {
            val language = mSelectBinding.rvDatas.getSelectLabelDatas<String>().joinToString(",")
            mBinding.ivLanguage.content = language
            languagePopupWindow!!.dismiss()
            showLoadingDialog()
            mModel.updateVolunteerInfo(VolunteerUpdateInformationVM.LANGUAGE,language)
        }
    }

    /**
     * 服务时间选择器
     */
    private val serviceTimePv by lazy {
        val pV = OptionsPickerBuilder(this, OnOptionsSelectListener { s1, s2, s3, v ->
            mBinding.ivServiceTime.content = mModel.serviceTimes[s1]
            showLoadingDialog()
            mModel.updateVolunteerInfo(VolunteerUpdateInformationVM.SERVICETIMETYPE,mModel.serviceType[s1])
        }).build<String>()
        pV.setPicker(mModel.serviceTimes)
        pV
    }

    private fun showServiceTimes(){
        serviceTimePv.setSelectOptions(0)
        UIHelperUtils.showOptionsPicker(this, serviceTimePv)
    }

    var servicePopupWindow: BasePopupWindow? = null

    /**
     * 初始化个人特长选择窗体
     */
    private fun initServiceSelectPopup(services:List<String>){
        val inflater = LayoutInflater.from(this)
        val mSelectBinding: VolunteerRegisterSelectPopuBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.volunteer_register_select_popu,
            null,
            false
        )
        servicePopupWindow = BasePopupWindow(
            mSelectBinding.root, LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT, true
        )
        // 设置背景
        servicePopupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 设置是否能够响应外部点击事件
        servicePopupWindow!!.isOutsideTouchable = true
        // 设置能否响应点击事件
        servicePopupWindow!!.isTouchable = true
        servicePopupWindow!!.isFocusable = true

        servicePopupWindow!!.resetDarkPosition()
        servicePopupWindow!!.darkBelow(mBinding.ivServiceType)
        mSelectBinding.tvTypeLabel.text ="志愿服务意向"
        mSelectBinding.rvDatas.setLabels(services)
        mSelectBinding.rvDatas.setOnLabelClickListener { label, data, position ->
        }

        mSelectBinding.tvReset.onNoDoubleClick {
            mSelectBinding.rvDatas.clearAllSelect()
        }
        mSelectBinding.tvSure.onNoDoubleClick {
             val serviceIntention = mSelectBinding.rvDatas.getSelectLabelDatas<String>().joinToString(",")
            mBinding.ivServiceType.content = serviceIntention
            servicePopupWindow!!.dismiss()
            showLoadingDialog()
            mModel.updateVolunteerInfo(VolunteerUpdateInformationVM.SERVICEINTENTION,serviceIntention)
        }
    }


}