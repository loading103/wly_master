package com.daqsoft.volunteer.volunteer.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.bean.LocationData
import com.daqsoft.baselib.utils.LocationUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.network.net.UserApi
import com.daqsoft.provider.utils.SelectImageUtils
import com.daqsoft.provider.view.BasePopupWindow
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.databinding.FragmentVolunteerRegister1Binding
import com.daqsoft.volunteer.databinding.FragmentVolunteerRegister2Binding
import com.daqsoft.volunteer.databinding.VolunteerRegisterSelectPopuBinding
import com.daqsoft.volunteer.utils.Constant
import com.daqsoft.volunteer.utils.JumpUtils
import com.daqsoft.volunteer.volunteer.vm.VolunteerRegisterVM
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import me.nereo.multi_image_selector.upload.FileUpload
import me.nereo.multi_image_selector.utils.BitmapUtils

/**
 *@package:com.daqsoft.volunteer.volunteer.fragment
 *@date:2020/6/3 14:13
 *@author: caihj
 *@des:注册界面2
 **/
class VolunteerRegisterFragment2:BaseFragment<FragmentVolunteerRegister2Binding,VolunteerRegisterVM>() {


    companion object{
        val NAME = "NAME"
        val GENDER = "GENDER"
        val NATIONAL = "NATIONAL"
        val IDCARD = "IDCARD"
        val PHONE = "PHONE"
        val CODE = "CODE"
        fun newInstance(name:String,gender:String,national:String,idCard:String,phone:String,code:String):VolunteerRegisterFragment2{
            val volunteerRegisterFragment2 = VolunteerRegisterFragment2()
            val bundle = Bundle()
            bundle.putString(NAME,name)
            bundle.putString(GENDER,gender)
            bundle.putString(NATIONAL,national)
            bundle.putString(IDCARD,idCard)
            bundle.putString(PHONE,phone)
            bundle.putString(CODE,code)
            volunteerRegisterFragment2.arguments = bundle
            return volunteerRegisterFragment2
        }
    }


    override fun getLayout(): Int = R.layout.fragment_volunteer_register_2

    override fun injectVm(): Class<VolunteerRegisterVM> = VolunteerRegisterVM::class.java

    override fun initView() {
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
        mModel.skills.observe(this, Observer {
            initSkillSelectPopup(it)
        })

        mModel.languages.observe(this, Observer {
            initLanguageSelectPopup(it)
        })

        mModel.healths.observe(this, Observer {
            healthStatusPv.setPicker(it)
        })

        mModel.serviceIntentions.observe(this, Observer {
            initServiceSelectPopup(it)
        })
        mModel.idCardDownLocal.observe(this, Observer {
            Glide.with(activity!!).load(it).into(mBinding.mIdCardPositiveIv)
        })
        mModel.idCardUpLocal.observe(this, Observer {
            Glide.with(activity!!).load(it).into(mBinding.mIdCardNegativeIv)
        })
        mModel.doFinish.observe(this, Observer {
            dissMissLoadingDialog()
        })
        initEvent()
    }

    override fun initData() {
        mModel.getEducations()
        mModel.getJobStatus()
        mModel.getSkills()
        mModel.getHealths()
        mModel.getLanguages()
        mModel.getServiceIntentions()
        getParams()
    }

    private fun getParams(){
        mModel.name = arguments?.getString(NAME).toString()
        mModel.national = arguments?.getString(NATIONAL).toString()
        mModel.gender = arguments?.getString(GENDER).toString()
        mModel.idCard = arguments?.getString(IDCARD).toString()
        mModel.phone = arguments?.getString(PHONE).toString()
        mModel.code = arguments?.getString(CODE).toString()
    }

    @SuppressLint("LogNotTimber")
    private fun initEvent(){
        mBinding.tvServiceRegion.onNoDoubleClick {
            updateArea()
        }
        mBinding.tvVolunteerRegion.onNoDoubleClick {
            if(mModel.attributions.value.isNullOrEmpty()){
                val tipMsg = if(mModel.serviceRegion.isEmpty()){
                    "请选择服务地区!"
                }else{
                    "该服务地区没有志愿者归属信息!"
                }

                ToastUtils.showMessage(tipMsg)
            }else{
                showAttribution()
            }
        }
        mBinding.tvEducation.onNoDoubleClick {
            showEducation()
        }
        mBinding.tvJobStatus.onNoDoubleClick {
            showJobStatus()
        }
        mBinding.tvHealth.onNoDoubleClick {
            showHealthStatus()
        }
        mBinding.tvAddress.onNoDoubleClick {
            updateDetailAddress()
        }
        mBinding.tvServiceTime.onNoDoubleClick {
            showServiceTimes()
        }
        mBinding.tvSkill.onNoDoubleClick {
            skillPopupWindow?.showAtLocation(mBinding.root,Gravity.BOTTOM,0,0)
        }
        mBinding.tvLanguage.onNoDoubleClick {
            languagePopupWindow?.showAtLocation(mBinding.root,Gravity.BOTTOM,0,0)
        }
        mBinding.tvServiceType.onNoDoubleClick {
            servicePopupWindow?.showAtLocation(mBinding.root,Gravity.BOTTOM,0,0)
        }
        mBinding.mIdCardNegativeIv.onNoDoubleClick {
            BitmapUtils.hideInputWindow(activity!!)
            imgSelectPosition = 1
            Utils.setWindowBackGroud(activity!!, 0.8f)
            selectImg.showAtLocation(
                mBinding.root,
                Gravity.BOTTOM,
                0,
                UIHelperUtils.getNavigationBarHeight(activity!!)
            )
        }
        mBinding.mIdCardPositiveIv.onNoDoubleClick {
            BitmapUtils.hideInputWindow(activity!!)
            imgSelectPosition = 0
            Utils.setWindowBackGroud(activity!!, 0.8f)
            selectImg.showAtLocation(
                mBinding.root,
                Gravity.BOTTOM,
                0,
                UIHelperUtils.getNavigationBarHeight(activity!!)
            )
        }
        mBinding.tvSubmit.onNoDoubleClick {
            if(checkInput()){
                showLoadingDialog()
                mModel.applyVolunteer()

            }
        }
    }

    private fun checkInput():Boolean{
        if(mModel.serviceRegion.isEmpty()){
            ToastUtils.showMessage("请选择服务地区!")
            return false
        }
        if(mModel.attribution.isEmpty()){
            ToastUtils.showMessage("请选择志愿者归属!")
            return false
        }
        if(mModel.employmentStatus.isEmpty()){
            ToastUtils.showMessage("请选择就业状况!")
            return false
        }
        if(mModel.idCardUpLocal.value.isNullOrEmpty()){
            ToastUtils.showMessage("请上传身份证正面!")
            return false
        }
        if(mModel.idCardDownLocal.value.isNullOrEmpty()){
            ToastUtils.showMessage("请上传身份证反面!")
            return false
        }
        if (mModel.idCardDown.isNullOrEmpty()) {
            ToastUtils.showMessage("文件上传中，请稍后")
            return false
        } else if (mModel.idCardUp.isNullOrEmpty()) {
            ToastUtils.showMessage("文件上传中，请稍后")
            return false
        }
        mModel.emergencyContactName = mBinding.etContactName.text.toString()
        mModel.emergencyContactPhone = mBinding.etContactPhone.text.toString()

        if(mModel.emergencyContactName.isEmpty()){
            ToastUtils.showMessage("请输入紧急联系人姓名!")
            return false
        }
        if(mModel.emergencyContactPhone.isEmpty()){
            ToastUtils.showMessage("请输入紧急联系人电话!")
            return false
        }
        if(mModel.specialty.isEmpty()){
            ToastUtils.showMessage("请选择个人特长!")
            return false
        }
        if(mModel.serviceTimeType.isEmpty()){
            ToastUtils.showMessage("请选择服务时间!")
            return false
        }
        if(mModel.serviceIntention.isEmpty()){
            ToastUtils.showMessage("请选择服务意向!")
            return false
        }
        if(mModel.region.isEmpty()){
            ToastUtils.showMessage("请选择详细地址!")
            return false
        }
        mModel.school = mBinding.etSchool.text.toString()
        mModel.email = mBinding.etEmail.text.toString()
        mModel.qq = mBinding.etQq.text.toString()
        mModel.address = mBinding.etAddressDetail.text.toString()
        return true
    }

    /**
     * 地区点击修改
     */
    private fun updateArea() {
        LocationUtil(activity!! as RxAppCompatActivity, object : LocationUtil.OnLocationSelectListener {
            override fun onLocationSelect(region: LocationData) {
//                if(region.memo == region.name){
//                    ToastUtils.showMessage("不能只选择省级!")
//                }else{
                    mBinding.tvServiceRegion.text = region.memo
                    mModel.serviceRegion = region.region
                    mModel.attributions.value?.clear()
                    mModel.getAttributions()
//                }
            }

        }, mModel.mPresenter)

    }

    /**
     * 详细地址
     */
    private fun updateDetailAddress() {
        LocationUtil(activity!! as RxAppCompatActivity, object : LocationUtil.OnLocationSelectListener {
            override fun onLocationSelect(region: LocationData) {
                    mBinding.tvAddress.text = region.memo
                    mModel.region = region.region

            }

        }, mModel.mPresenter)

    }

    /**
     * 志愿归属选择器
     */
    private val attributionPv by lazy {
        val pV = OptionsPickerBuilder(context, OnOptionsSelectListener { s1, s2, s3, v ->
            mBinding.tvVolunteerRegion.text = mModel.attributions.value?.get(s1)?.name ?: ""
            mModel.attribution = mModel.attributions.value?.get(s1)?.id ?: ""
        }).build<String>()
        pV
    }

    private fun showAttribution(){
        attributionPv.setSelectOptions(0)
            UIHelperUtils.showOptionsPicker(activity!!, attributionPv)
    }


    /**
     * 学历选择器
     */
    private val educationPv by lazy {
        val pV = OptionsPickerBuilder(context, OnOptionsSelectListener { s1, s2, s3, v ->
            mBinding.tvEducation.text = mModel.educations.value?.get(s1) ?: ""
            mModel.education = mModel.educations.value?.get(s1) ?: ""
        }).build<String>()
        pV
    }

    private fun showEducation(){
        educationPv.setSelectOptions(0)
        UIHelperUtils.showOptionsPicker(activity!!, educationPv)
    }

    /**
     * 就业选择器
     */
    private val jobStatusPv by lazy {
        val pV = OptionsPickerBuilder(context, OnOptionsSelectListener { s1, s2, s3, v ->
            mBinding.tvJobStatus.text = mModel.jobStatus.value?.get(s1) ?: ""
            mModel.employmentStatus = mModel.jobStatus.value?.get(s1) ?: ""
        }).build<String>()
        pV
    }

    private fun showJobStatus(){
        jobStatusPv.setSelectOptions(0)
        UIHelperUtils.showOptionsPicker(activity!!, jobStatusPv)
    }

    /**
     * 健康状况选择器
     */
    private val healthStatusPv by lazy {
        val pV = OptionsPickerBuilder(context, OnOptionsSelectListener { s1, s2, s3, v ->
            mBinding.tvHealth.text = mModel.healths.value?.get(s1) ?: ""
            mModel.healthStatus = mModel.healths.value?.get(s1) ?: ""
        }).build<String>()
        pV
    }

    private fun showHealthStatus(){
        healthStatusPv.setSelectOptions(0)
        UIHelperUtils.showOptionsPicker(activity!!, healthStatusPv)
    }

    /**
     * 服务时间选择器
     */
    private val serviceTimePv by lazy {
        val pV = OptionsPickerBuilder(context, OnOptionsSelectListener { s1, s2, s3, v ->
            mBinding.tvServiceTime.text = mModel.serviceTimes[s1]
            mModel.serviceTimeType = mModel.serviceType[s1].toString()
        }).build<String>()
        pV.setPicker(mModel.serviceTimes)
        pV
    }

    private fun showServiceTimes(){
        serviceTimePv.setSelectOptions(0)
        UIHelperUtils.showOptionsPicker(activity!!, serviceTimePv)
    }


    var skillPopupWindow: BasePopupWindow? = null

    /**
     * 初始化个人特长选择窗体
     */
    private fun initSkillSelectPopup(skills:List<String>){
        val inflater = LayoutInflater.from(context)
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
        skillPopupWindow!!.darkBelow(mBinding.tvSkill)
        mSelectBinding.rvDatas.setLabels(skills)
        mSelectBinding.rvDatas.setOnLabelClickListener { label, data, position ->
        }

        mSelectBinding.tvReset.onNoDoubleClick {
            mSelectBinding.rvDatas.clearAllSelect()
            mBinding.tvSkill.text = getString(R.string.volunteer_module_register_hint_skill)
            mModel.specialty = ""
        }
        mSelectBinding.tvSure.onNoDoubleClick {
            mModel.specialty = mSelectBinding.rvDatas.getSelectLabelDatas<String>().joinToString(",")
            mBinding.tvSkill.text = mModel.specialty
            skillPopupWindow!!.dismiss()
        }
    }

    var languagePopupWindow: BasePopupWindow? = null

    /**
     * 初始化语言选择窗体
     */
    private fun initLanguageSelectPopup(language:List<String>){
        val inflater = LayoutInflater.from(context)
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
        languagePopupWindow!!.darkBelow(mBinding.tvLanguage)
        mSelectBinding.tvTypeLabel.text ="擅长语言"
        mSelectBinding.rvDatas.setLabels(language)
        mSelectBinding.rvDatas.setOnLabelClickListener { label, data, position ->
        }

        mSelectBinding.tvReset.onNoDoubleClick {
            mSelectBinding.rvDatas.clearAllSelect()
            mBinding.tvLanguage.text = getString(R.string.volunteer_module_register_hint_language)
            mModel.language = ""
        }
        mSelectBinding.tvSure.onNoDoubleClick {
            mModel.language = mSelectBinding.rvDatas.getSelectLabelDatas<String>().joinToString(",")
            mBinding.tvLanguage.text = mModel.language
            languagePopupWindow!!.dismiss()
        }
    }


    var servicePopupWindow: BasePopupWindow? = null

    /**
     * 初始化个人特长选择窗体
     */
    private fun initServiceSelectPopup(services:List<String>){
        val inflater = LayoutInflater.from(context)
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
        servicePopupWindow!!.darkBelow(mBinding.tvServiceType)
        mSelectBinding.tvTypeLabel.text ="志愿服务意向"
        mSelectBinding.rvDatas.setLabels(services)
        mSelectBinding.rvDatas.setOnLabelClickListener { label, data, position ->
        }

        mSelectBinding.tvReset.onNoDoubleClick {
            mSelectBinding.rvDatas.clearAllSelect()
            mBinding.tvServiceType.text = getString(R.string.volunteer_module_register_hint_service_type)
            mModel.serviceIntention = ""
        }
        mSelectBinding.tvSure.onNoDoubleClick {
            mModel.serviceIntention = mSelectBinding.rvDatas.getSelectLabelDatas<String>().joinToString(",")
            mBinding.tvServiceType.text = mModel.serviceIntention
            servicePopupWindow!!.dismiss()
        }
    }

    /**
     * 图片选择位置
     */
    private var imgSelectPosition = 0
    private val REQUEST_CAMERA_CODE = 10000
    private val REQUEST_ALBUM_CODE = 10001
    private val selectImg by lazy {
        SelectImageUtils.initPictureSelectPop(activity!!, REQUEST_CAMERA_CODE, REQUEST_ALBUM_CODE)
    }

    /**
     * 上传文件
     */
    private fun uploadFile(path: String, type: Int) {
        var list = arrayListOf<String>()
        list.add(path)
        FileUpload.uploadFilePath(UserApi.FILE_UPLOAD, context, list, object : FileUpload
        .UploadFileBack {
            override fun result(value: String?) {
                if (type == 1) {
                    mModel.idCardUp = value
                } else {
                    mModel.idCardDown = value
                }

            }

            override fun resultList(value: MutableList<String>?) {
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA_CODE) {
            // 相机
            val fileFromCamera = SelectImageUtils.getFileFromCamera()
            if (fileFromCamera?.exists() == false) {
                return
            }
            if (imgSelectPosition == 0) {
                // 反面
                mModel.idCardDownLocal.value = fileFromCamera?.absolutePath
                uploadFile(mModel.idCardDownLocal.value!!, 0)
            } else if (imgSelectPosition == 1) {
                // 正面
                mModel.idCardUpLocal.value = fileFromCamera?.absolutePath
                uploadFile(mModel.idCardUpLocal.value!!, 1)
            }


        } else if (requestCode == REQUEST_ALBUM_CODE) {
            // 相册
            val fileFromAlbum = SelectImageUtils.getFileFromAlbum(activity!!, data)
            if (fileFromAlbum.isNullOrEmpty()) {
                return
            }
            if (imgSelectPosition == 0) {
                // 反面
                mModel.idCardDownLocal.value = fileFromAlbum
                uploadFile(mModel.idCardDownLocal.value!!, 0)
            } else if (imgSelectPosition == 1) {
                // 正面
                mModel.idCardUpLocal.value = fileFromAlbum
                uploadFile(mModel.idCardUpLocal.value!!, 1)
            }
        }

    }
}