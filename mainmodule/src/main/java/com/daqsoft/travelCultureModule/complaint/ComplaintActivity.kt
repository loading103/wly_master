package com.daqsoft.travelCultureModule.complaint

import android.annotation.SuppressLint
import android.content.Intent
import android.view.Gravity
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.bean.LocationData
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.LocationUtil
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityComplaintBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.utils.HtmlUtils
import com.daqsoft.provider.utils.StringUtils
import com.jakewharton.rxbinding2.view.RxView
import me.nereo.multi_image_selector.MultiFileSelectorActivity
import me.nereo.multi_image_selector.bean.Constrant
import me.nereo.multi_image_selector.bean.Image
import me.nereo.multi_image_selector.utils.BitmapUtils
import org.jetbrains.anko.toast
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * @Description 旅游投诉页面
 * @ClassName   ComplaintActivity
 * @Author      Huangxi
 * @Time        2020/2/28
 */
@Route(path = MainARouterPath.MAIN_COMPLAINT_ACTIVITY)
class ComplaintActivity : TitleBarActivity<ActivityComplaintBinding, ComplaintViewModel>(),
    View.OnClickListener {


    /**
     * 性别 1：男 2：女 0：未知
     */
    var sex: Int? = null


    /**
     * 投诉类型值
     */
    var complaintsType = ""
    /**
     * 是否公开
     */
    var isPublic = true
    /**
     * 地区编码
     */
    var region = ""
    /**
     * 发生时间
     */
    var incidentTime = ""
    /**
     * 资源id
     */
    var resourceId = ""
    /**
     * 资源类型
     */
    var resourceType = ""
    /**
     * 被投诉方
     */
    var respondent = ""
    /**
     * 投诉人姓名
     */
    var name = ""

    /**
     * 电话
     */
    var phone = ""
    /**
     * 图片证据
     */
    var evidenceImages = ""

    /**
     * 视频证据
     */
    var evidenceVideo = ""
    /**
     * 投诉事由
     */
    var complaintsReasons = ""
    /**
     * 详细地址
     */
    var address = ""

    override fun injectVm(): Class<ComplaintViewModel> = ComplaintViewModel::class.java

    override fun getLayout(): Int = R.layout.activity_complaint

    override
    fun setTitle(): String = "旅游投诉"

    @SuppressLint("CheckResult")
    override fun initView() {
        mBinding.tvSex.setOnClickListener(this)
        mBinding.tvType.setOnClickListener(this)
        mBinding.tvRegion.setOnClickListener(this)
        mBinding.tvComplaintName.setOnClickListener(this)
        mBinding.tvTime.setOnClickListener(this)
        mBinding.tvPublic.setOnClickListener(this)
        // 点击最新
        RxView.clicks(mBinding.tvSaveComplaint)
            .throttleFirst(2, TimeUnit.SECONDS)
            .subscribe {
                saveComplaint()
            }
        mBinding.recyclerImg.setPicNumber(9)
        mBinding.recyclerImg.init(this, true,true)
        StringUtils().setProhibitEmoji(mBinding.etAddress)
//        StringUtils().setProhibitEmoji(mBinding.etInfo)
        StringUtils().setProhibitEmoji(mBinding.etName)
    }

    override fun initData() {
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_sex -> {
                // 性别
                BitmapUtils.hideInputWindow(this)
                UIHelperUtils.showOptionsPicker(this, genderWindow)
            }
            R.id.tv_type -> {
                // 投诉类型
                BitmapUtils.hideInputWindow(this)
                UIHelperUtils.showOptionsPicker(this, complaintTypeWindow)
            }
            R.id.tv_region -> {
                // 事发地
                BitmapUtils.hideInputWindow(this)
                LocationUtil(this, object : LocationUtil.OnLocationSelectListener {
                    override fun onLocationSelect(data: LocationData) {
                        region = data.region
                        mBinding.tvRegion.setText(data.memo)
                        mBinding.tvComplaintName.setText("")
                        respondent = ""
                        resourceType = ""
                        resourceId = ""
                    }

                }, mModel.mPresenter)
            }
            R.id.tv_time -> {
                // 发生时间
                BitmapUtils.hideInputWindow(this)
                UIHelperUtils.showOptionsPicker(this, timePickerView)
            }
            R.id.tv_public -> {
                // 允许公开
                BitmapUtils.hideInputWindow(this)
                UIHelperUtils.showOptionsPicker(this, complaintPublicWindow)
            }
            R.id.tv_complaint_name -> {
                // 被投诉方
                BitmapUtils.hideInputWindow(this)
                if (region.isNotEmpty()) {
                    ARouter.getInstance().build(MainARouterPath.MAIN_COMPLAINT_RESOURCE_ACTIVITY)
                        .withString("region", region)
                        .navigation(this@ComplaintActivity, 0x0001)
                } else {
                    toast("请先选择发生地").setGravity(Gravity.CENTER, 0, 0)
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0x0001 && resultCode == 0x0002 && data != null) {
            resourceId = data.getStringExtra("resourceId")
            resourceType = data.getStringExtra("resourceType")
            respondent = data.getStringExtra("respondent")
            mBinding.tvComplaintName.setText(HtmlUtils.html2Str(respondent))
        } else if (requestCode == Constrant.ADD_IMAGE) {
            // 選擇图片视频上传
            if (data != null && data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)) {
                var list: ArrayList<Image> =
                    data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                mBinding.recyclerImg.onActivityResult(list)
            }
        } else if (requestCode == Constrant.ADD_VIDEO) {
            // 選擇图片视频上传
            if (data != null && data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)) {
                var list: ArrayList<Image> =
                    data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                if (list.size > 0) {
//                    mBinding.recyclerImg.insertAtFirst(list[0])
                    mBinding.recyclerImg.onActivityResult(list)
                }
            }
        }
    }

    /**
     * 性别选择器
     */
    private val genderWindow by lazy {
        val gender = resources.getStringArray(R.array.gender)
        val pV = OptionsPickerBuilder(this, OnOptionsSelectListener { s1, s2, s3, v ->
            sex = s1
            mBinding.tvSex.setText(gender.get(s1))
        }).build<String>()
        pV.setPicker(gender.asList())
        pV
    }
    /**
     * 投诉类型弹框
     */
    private val complaintTypeWindow by lazy {
        var complaintTypeName = resources.getStringArray(R.array.complaint_type_name)
        var complaintTypeLabel = resources.getStringArray(R.array.complaint_type)
        var pV = OptionsPickerBuilder(this, OnOptionsSelectListener { s1, s2, s3, v ->
            complaintsType = complaintTypeLabel.get(s1)
            mBinding.tvType.setText(complaintTypeName.get(s1))
        }).build<String>()
        pV.setPicker(complaintTypeName.asList())
        pV
    }

    /**
     * 投诉类型弹框
     */
    private val complaintPublicWindow by lazy {
        var complaintPublic = resources.getStringArray(R.array.complaint_public)
        var pV = OptionsPickerBuilder(this, OnOptionsSelectListener { s1, s2, s3, v ->
            isPublic = complaintPublic.get(s1) == "公开"
            mBinding.tvPublic.setText(complaintPublic.get(s1))
        }).build<String>()
        pV.setPicker(complaintPublic.asList())
        pV
    }

    /**
     * 时间选择器
     */
    private val timePickerView by lazy {
        TimePickerBuilder(this, OnTimeSelectListener { date, v ->
            // 选中事件回调
            incidentTime = Utils.getDateTime(Utils.dateYMD, date)
            mBinding.tvTime.setText(incidentTime)
        }).setRangDate(null, Calendar.getInstance())
            .build()
    }

    /**
     * 提交保存
     */
    fun saveComplaint() {
        name = mBinding.etName.text.toString().trim()
        phone = mBinding.etPhone.text.toString().trim()
        address = mBinding.etAddress.text.toString().trim()
        complaintsReasons = mBinding.etInfo.text.toString().trim()
        if (mBinding.etName.text.toString().trim().isNullOrEmpty()) {
            toast("请输入姓名").setGravity(Gravity.CENTER, 0, 0)
            return
        }
        if (mBinding.etPhone.text.toString().trim().isNullOrEmpty()) {
            toast("请输入手机号码").setGravity(Gravity.CENTER, 0, 0)
            return
        }
        if (mBinding.etAddress.text.toString().trim().isNullOrEmpty()) {
            toast("请输入事件发生详细地址").setGravity(Gravity.CENTER, 0, 0)
            return
        }
        if (mBinding.etInfo.text.toString().trim().isNullOrEmpty()) {
            toast("请输入投诉事由").setGravity(Gravity.CENTER, 0, 0)
            return
        }
        if (sex == null) {
            toast("请选择性别").setGravity(Gravity.CENTER, 0, 0)
            return
        }
        if (complaintsType.isNullOrEmpty()) {
            toast("请选择投诉类型").setGravity(Gravity.CENTER, 0, 0)
            return
        }
        if (region.isNullOrEmpty()) {
            toast("请选择发生地").setGravity(Gravity.CENTER, 0, 0)
            return
        }
        if (respondent.isNullOrEmpty()) {
            toast("请选择被投诉方").setGravity(Gravity.CENTER, 0, 0)
            return
        }
        if (incidentTime.isNullOrEmpty()) {
            toast("请选择事件发生时间").setGravity(Gravity.CENTER, 0, 0)
            return
        }
        var urls = mBinding.recyclerImg.path.split(",")
        evidenceImages = ""
        evidenceVideo = ""
        if (urls != null && urls.isNotEmpty()) {
            for (url in urls) {
                if (url.endsWith(".mp4")) {
                    if (evidenceVideo.isNotEmpty()) {
                        evidenceVideo += ","
                    }
                    evidenceVideo += url
                } else {
                    if (evidenceImages.isNotEmpty()) {
                        evidenceImages += ","
                    }
                    evidenceImages += url
                }
            }
        }
        var map = HashMap<String, Any>()
        map["isPublic"] = isPublic
        map["evidenceImages"] = evidenceImages
        map["evidenceVideo"] = evidenceVideo
        map["complaintsReasons"] = complaintsReasons
        map["incidentTime"] = incidentTime
        map["address"] = address
        map["region"] = region
        map["resourceType"] = resourceType
        map["resourceId"] = resourceId
        map["respondent"] = respondent
        map["name"] = name
        map["phone"] = phone
        map["sex"] = sex!!
        map["complaintsType"] = complaintsType
        mModel.saveData(map)
    }

    override fun notifyData() {
        super.notifyData()
        mModel.result.observe(this, Observer {
            if (it != null) {
                if (it.code == 0) {
                    toast("投诉提交成功").setGravity(Gravity.CENTER, 0, 0)
                    finish()
                } else {
                    toast(it.message.toString()).setGravity(Gravity.CENTER, 0, 0)
                }
            }
        })
    }

}

/**
 * @Description 旅游投诉ViewModel
 * @Author      Huangxi
 * @Time        2020/2/28
 */
class ComplaintViewModel : BaseViewModel() {
    /**
     * 提交结果
     */
    var result = MutableLiveData<BaseResponse<Any>>()

    /**
     * 提交数据
     */
    fun saveData(map: HashMap<String, Any>) {
        HomeRepository.service.saveComplaint(map)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    result.postValue(response)
                }

            })
    }
}
