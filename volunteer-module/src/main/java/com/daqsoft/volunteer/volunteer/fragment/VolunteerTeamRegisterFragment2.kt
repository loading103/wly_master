package com.daqsoft.volunteer.volunteer.fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.bean.LocationData
import com.daqsoft.baselib.utils.LocationUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.network.home.bean.ItemAddressBean
import com.daqsoft.provider.utils.SelectImageUtils
import com.daqsoft.provider.view.BasePopupWindow
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.databinding.FragmentVolunteerTeamRegister2Binding
import com.daqsoft.volunteer.databinding.VolunteerRegisterSelectPopuBinding
import com.daqsoft.volunteer.volunteer.AddressActivity
import com.daqsoft.volunteer.volunteer.vm.VolunteerTeamRegisterVM
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import java.io.File
import java.util.*

/**
 *@package:com.daqsoft.volunteer.volunteer.fragment
 *@date:2020/6/3 14:13
 *@author: caihj
 *@des:注册界面2
 **/
class VolunteerTeamRegisterFragment2:BaseFragment<FragmentVolunteerTeamRegister2Binding, VolunteerTeamRegisterVM>() {


    companion object{
        val ADDLOCATION = 0x0004
        val CODE = "CODE"

        fun newInstance(code:String):VolunteerTeamRegisterFragment2{
            val volunteerTeamRegisterFragment2 = VolunteerTeamRegisterFragment2()
            val bundle = Bundle()
            bundle.putString(CODE,code)
            volunteerTeamRegisterFragment2.arguments = bundle
            return volunteerTeamRegisterFragment2
        }
    }

    override fun getLayout(): Int = R.layout.fragment_volunteer_team_register_2

    override fun injectVm(): Class<VolunteerTeamRegisterVM> = VolunteerTeamRegisterVM::class.java

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
        mModel.serviceTypes.observe(this, Observer {
            initServiceTypeSelectPopup(it)
        })
        mModel.headLocal.observe(this, Observer {
            val file = File(it)
            mModel.upLoadFile(file)
        })
        mModel.uploadUrl.observe(this, Observer {
            dissMissLoadingDialog()
            if(it.isNullOrEmpty()){
                ToastUtils.showMessage("图片上传失败!")
            }else{
                Glide.with(context!!).load(it).into(mBinding.ivLogo)
            }
        })
        mModel.brands.observe(this, Observer { it1 ->
            val brands = mutableListOf<String>()
            it1.map {
                brands.add(it.name)
            }
            initBrandSelectPopup(brands)
        })
        mModel.code = arguments?.getString(CODE).toString()
        initEvent()
    }

    override fun initData() {
        mModel.getAttributions()
        mModel.getServiceTypes()
        mModel.getBrandList()
    }


    private fun initEvent(){
        mBinding.tvServiceRegion.onNoDoubleClick {
            updateArea()
        }
        mBinding.tvServiceType.onNoDoubleClick {
            serviceTypePopupWindow?.showAtLocation(mBinding.root, Gravity.BOTTOM,0,0)
        }
        mBinding.tvTeamCreateTime.onNoDoubleClick {
            createTime.show()
        }
        mBinding.ivLogo.onNoDoubleClick {
            choiceLogo()
        }

        mBinding.tvSubmit.onNoDoubleClick {
            if(checkInput()){
                mModel.register()
            }
        }
        mBinding.tvAddress.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.VolunteerModule.ADDRESS_SEARCH)
                .navigation(activity, ADDLOCATION)
        }
        mBinding.tvBrand.onNoDoubleClick {
            brandPopupWindow?.showAtLocation(mBinding.root, Gravity.BOTTOM,0,0)
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
    }

    /**
     * 地区点击修改
     */
    private fun updateArea() {
        LocationUtil(activity!! as RxAppCompatActivity, object : LocationUtil.OnLocationSelectListener {
            override fun onLocationSelect(region: LocationData) {
                if(region.memo == region.name){
                    ToastUtils.showMessage("不能只选择省级!")
                }else{
                    mBinding.tvServiceRegion.text = region.memo
                    mModel.serviceRegion = region.region
                    mModel.getAttributions()
                }
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


    var serviceTypePopupWindow: BasePopupWindow? = null

    /**
     * 初始化服务类型学选择窗体
     */
    private fun initServiceTypeSelectPopup(serviceTypes:List<String>){
        val inflater = LayoutInflater.from(context)
        val mSelectBinding: VolunteerRegisterSelectPopuBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.volunteer_register_select_popu,
            null,
            false
        )
        serviceTypePopupWindow = BasePopupWindow(
            mSelectBinding.root, LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT, true
        )
        // 设置背景
        serviceTypePopupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 设置是否能够响应外部点击事件
        serviceTypePopupWindow!!.isOutsideTouchable = true
        // 设置能否响应点击事件
        serviceTypePopupWindow!!.isTouchable = true
        serviceTypePopupWindow!!.isFocusable = true
        mSelectBinding.tvTypeLabel.text ="服务类型"
        serviceTypePopupWindow!!.resetDarkPosition()
        serviceTypePopupWindow!!.darkBelow(mBinding.tvServiceType)
        mSelectBinding.rvDatas.setLabels(serviceTypes)
        mSelectBinding.rvDatas.maxSelect = 3
        mSelectBinding.rvDatas.setOnLabelClickListener { label, data, position ->
        }

        mSelectBinding.tvReset.onNoDoubleClick {
            mSelectBinding.rvDatas.clearAllSelect()
            mBinding.tvServiceType.text = getString(R.string.volunteer_module_team_register_hint_service_type)
            mModel.serviceType = ""
        }
        mSelectBinding.tvSure.onNoDoubleClick {
            mModel.serviceType = mSelectBinding.rvDatas.getSelectLabelDatas<String>().joinToString(",")
            mBinding.tvServiceType.text = mModel.serviceType
            serviceTypePopupWindow!!.dismiss()
        }
    }


    var brandPopupWindow: BasePopupWindow? = null

    /**
     * 初始化服务类型学选择窗体
     */
    private fun initBrandSelectPopup(brands:List<String>){
        val inflater = LayoutInflater.from(context)
        val mSelectBinding: VolunteerRegisterSelectPopuBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.volunteer_register_select_popu,
            null,
            false
        )
        brandPopupWindow = BasePopupWindow(
            mSelectBinding.root, LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT, true
        )
        // 设置背景
        brandPopupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 设置是否能够响应外部点击事件
        brandPopupWindow!!.isOutsideTouchable = true
        // 设置能否响应点击事件
        brandPopupWindow!!.isTouchable = true
        brandPopupWindow!!.isFocusable = true
        mSelectBinding.tvTypeLabel.text ="服务品牌"
        brandPopupWindow!!.resetDarkPosition()
        brandPopupWindow!!.darkBelow(mBinding.tvBrand)
        mSelectBinding.rvDatas.setLabels(brands)
        mSelectBinding.rvDatas.maxSelect = 3
        mSelectBinding.rvDatas.setOnLabelClickListener { label, data, position ->
            if(mModel.brandSelected.contains(mModel.brands.value?.get(position)?.id.toString())){
                mModel.brandSelected.remove(mModel.brands.value?.get(position)?.id.toString())
            }else{
                mModel.brandSelected.add(mModel.brands.value?.get(position)?.id.toString())
            }
        }

        mSelectBinding.tvReset.onNoDoubleClick {
            mSelectBinding.rvDatas.clearAllSelect()
            mBinding.tvBrand.text = getString(R.string.volunteer_module_team_register_hint_brand)
            mModel.brand = ""
            mModel.brandItemIds = ""
        }
        mSelectBinding.tvSure.onNoDoubleClick {
            mModel.brand = mSelectBinding.rvDatas.getSelectLabelDatas<String>().joinToString(",")
            mBinding.tvBrand.text = mModel.brand
            mModel.brandItemIds = mModel.brandSelected.joinToString(",")
            brandPopupWindow!!.dismiss()
        }
    }



    private val createTime by lazy {

        TimePickerBuilder(activity!!, OnTimeSelectListener { date, v ->
            // 选中事件回调
            mBinding.tvTeamCreateTime.text =  Utils.getDateTime(Utils.dateYMD, date)
            mModel.establishTime =  Utils.getDateTime(Utils.dateYMD, date)
        })
            .setRangDate(null,Calendar.getInstance())
            .build()
    }

    private fun checkInput():Boolean{
        if(mModel.serviceRegion.isEmpty()){
            ToastUtils.showMessage("请选择服务地区!")
            return false
        }
        if(mModel.attribution.isEmpty()){
            ToastUtils.showMessage("请选择志愿者团队归属!")
            return false
        }
        mModel.teamName = mBinding.etTeamname.text.toString()
        if(mModel.teamName.isNullOrEmpty()){
            ToastUtils.showMessage("请输入团队名称!")
            return false
        }
        if(mModel.serviceType.isNullOrEmpty()){
            ToastUtils.showMessage("请选择服务类型!")
            return false
        }

        if(mModel.establishTime.isNullOrEmpty()){
            ToastUtils.showMessage("请选择成立时间!")
            return false
        }

        if(mModel.teamRegion.isNullOrEmpty()){
            ToastUtils.showMessage("请选择团队地址!")
            return false
        }
        mModel.teamAddress = mBinding.etTeamAddressDetail.text.toString()
        if(mModel.teamAddress.isEmpty()){
            ToastUtils.showMessage("请输入详细地址!")
            return false
        }
        mModel.teamPhone = mBinding.etContact.text.toString()
        if(mModel.teamPhone.isEmpty()){
            ToastUtils.showMessage("请输入团队联系电话!")
            return false
        }
        mModel.manageUnit = mBinding.etSchool.text.toString()
        mModel.teamIntroduce = mBinding.etIntroduce.text.toString()
        return true
    }


    /**
     * 底部头像选择弹框
     */
    private val popupWindow by lazy {
        SelectImageUtils.initPictureSelectPop(activity!!, REQUEST_CAMERA_CODE, REQUEST_ALBUM_CODE)
    }

    private fun choiceLogo() {
        // logo
        popupWindow.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
    }

    /**
     * 跳转相机请求码
     */
    private val REQUEST_CAMERA_CODE = 0
    /**
     * 跳转相册请求码
     */
    private val REQUEST_ALBUM_CODE = 1
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(data != null){
            if (requestCode == REQUEST_ALBUM_CODE) {
                // 相册
                if (data == null) {
                    return
                }
                mModel.headLocal.postValue(
                    SelectImageUtils.getFileFromAlbum(activity!!, data)
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
            if(requestCode == ADDLOCATION){
                if (data.hasExtra("address")) {
                    val address = data.getParcelableExtra<ItemAddressBean>("address")
                    mModel.teamRegion = address.region
                    mModel.longitude = address.longitude.toString()
                    mModel.latitude = address.latitude.toString()
                    mBinding.tvAddress.text = address.address
                }
            }
        }
    }
}