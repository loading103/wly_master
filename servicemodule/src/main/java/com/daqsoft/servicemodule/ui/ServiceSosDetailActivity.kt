package com.daqsoft.servicemodule.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ServiceSosDetailActBinding
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.MineFreeInfoBean
import com.daqsoft.provider.bean.MineUserInfoBean
import com.daqsoft.provider.bean.UserLogin
import com.daqsoft.provider.businessview.event.LoginStatusEvent
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.servicemodule.net.ServiceRepository
import com.jakewharton.rxbinding2.view.RxView
import me.nereo.multi_image_selector.MultiFileSelectorActivity
import me.nereo.multi_image_selector.bean.Constrant
import me.nereo.multi_image_selector.bean.Image
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast
import java.lang.Exception
import java.util.concurrent.TimeUnit


class ServiceSosDetailActivity :
    TitleBarActivity<ServiceSosDetailActBinding, ServiceSosDetailActivity.ServiceSosViewModel>() {
    var serviceImages = ""
    var serviceVideo = ""
    var lon = ""
    var lat = ""
    var address = ""

    private var wordNum: CharSequence? = null//记录输入的字数

    //输入框初始值
    private val num = 0

    //输入框最大值
    var mMaxNum = 200
    private var selectionStart = 0
    private var selectionEnd = 0
    override fun getLayout(): Int {
        return R.layout.service_sos_detail_act
    }

    override fun setTitle(): String {
        return resources.getString(R.string.service_sos_detail_title)
    }

    override fun injectVm(): Class<ServiceSosViewModel> {
        return ServiceSosViewModel::class.java
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        EventBus.getDefault().register(this)
        mBinding.serviceRecyclerImg.setPicNumber(9)
        mBinding.serviceRecyclerImg.init(this, true,true)
        RxView.clicks(mBinding.serviceSosCom)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                if (AppUtils.isLogin()) {
                    if (mBinding.serviceSosContent.length() > 0) {
                        uploadMsg()
                    } else {
                        ToastUtils.showMessage("请输入相关情况")
                    }
                } else {
                    ToastUtils.showUnLoginMsg()
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }
            }

        mBinding.serviceSosContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                wordNum = s//实时记录输入的字数
            }

            override fun afterTextChanged(s: Editable?) {
                //TextView显示剩余字数
                val number: Int = num + s!!.length
                mBinding.serviceSosLength.text = number.toString()
                selectionStart = mBinding.serviceSosContent.selectionStart
                selectionEnd = mBinding.serviceSosContent.selectionEnd
                //判断大于最大值
                if (wordNum!!.length > mMaxNum) { //删除多余输入的字（不会显示出来）
                    s.delete(selectionStart - 1, selectionEnd)
                    val tempSelection = selectionEnd
                    mBinding.serviceSosContent.text = s
                    mBinding.serviceSosContent.setSelection(s.length) //设置光标在最后
                    //吐司最多输入300字
                    ToastUtils.showMessage("已达上限")
                }
            }

        })
        initViewModel()
    }

    private fun initViewModel() {
        mModel.mobliePhoneInfoLd.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.edtServiceSosContactNumber.setText("" + it!!)
            }
        })
    }

    /**
     * 提交数据
     */
    private fun uploadMsg() {
        var urls = mBinding.serviceRecyclerImg.path.split(",")
        if (urls != null && urls.isNotEmpty()) {
            for (url in urls) {
                if (url.endsWith(".mp4")) {
                    if (serviceVideo.isNotEmpty()) {
                        serviceVideo += ","
                    }
                    serviceVideo += url
                } else {
                    if (serviceImages.isNotEmpty()) {
                        serviceImages += ","
                    }
                    serviceImages += url
                }
            }
        }
        var map = HashMap<String, Any>()
        map["longitude"] = lon
        map["latitude"] = lat
        map["address"] = address
        map["content"] = mBinding.serviceSosContent?.text
        map["image"] = serviceImages
        map["video"] = serviceVideo
        map["coverImage"] = ""
        var phone: String? = mBinding.edtServiceSosContactNumber.text.toString()
        if (!phone.isNullOrEmpty()) {
            map["phone"] = phone
        }
        mModel.uploadSosMsg(map)
    }

    override fun notifyData() {
        super.notifyData()
        mModel.result.observe(this, Observer {
            if (it != null)
                if (it.code == 0) {
                    toast("提交成功,请耐心等待").setGravity(Gravity.CENTER, 0, 0)
                    finish()
                } else {
                    toast(it.message.toString()).setGravity(Gravity.CENTER, 0, 0)
                }
        })
    }

    override fun initData() {
        try {
            lon = intent.getStringExtra("lon")
            lat = intent.getStringExtra("lat")
            address = intent.getStringExtra("address")
        } catch (e: Exception) {
        }

        if (AppUtils.isLogin()) {
            mModel.getMineCenterInfo()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateLoginStauts(event: LoginStatusEvent) {
        event?.run {
            if (status == LoginStatusEvent.LOGIN) {
                if (AppUtils.isLogin()) {
                    mModel.getMineCenterInfo()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constrant.ADD_IMAGE) {
            // 選擇图片视频上传
            if (data != null && data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)) {
                var list: ArrayList<Image> =
                    data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                mBinding.serviceRecyclerImg.onActivityResult(list)
            }
        } else if (requestCode == Constrant.ADD_VIDEO) {
            // 選擇图片视频上传
            if (data != null && data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)) {
                var list: ArrayList<Image> =
                    data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                if (list.size > 0) {
//                    mBinding.recyclerImg.insertAtFirst(list[0])
                    mBinding.serviceRecyclerImg.onActivityResult(list)
                }
            }
        }
    }

    class ServiceSosViewModel : BaseViewModel() {
        /**
         * 提交结果
         */
        var result = MutableLiveData<BaseResponse<Any>>()
        var mobliePhoneInfoLd = MutableLiveData<String?>()

        /**
         * 提交数据
         */
        fun uploadSosMsg(map: HashMap<String, Any>) {
            ServiceRepository().service.uploadSosMsg(map)
                .excute(object : BaseObserver<Any>(mPresenter) {
                    override fun onSuccess(response: BaseResponse<Any>) {
                        result.postValue(response)
                    }
                })
        }

        /**
         * 刷新用户信息
         */
        fun getMineCenterInfo() {
            UserRepository().userService
                .refreshToken()
                .excute(object : BaseObserver<UserLogin>() {
                    override fun onSuccess(response: BaseResponse<UserLogin>) {
                        try {
                            if (response?.data != null) {
                                SPUtils.getInstance()
                                    .put(SPKey.USER_CENTER_TOKEN, response.data?.userCenterToken)
                                SPUtils.getInstance().put(SPKey.UUID, response.data?.unid)
                                SPUtils.getInstance().put(SPKey.SITEID, response.data?.siteId ?: -1)
                                SPUtils.getInstance()
                                    .put(SPKey.ENCRYPTION, response.data?.encryption ?: "")
                                SPUtils.getInstance().put(SPKey.VIPID, response.data?.vipId ?: -1)
                                SPUtils.getInstance().put(SPKey.UID, response.data?.unid)
                                SPUtils.getInstance().put(SPKey.PHONE, response.data?.phone)
                                SPUtils.getInstance()
                                    .put(SPKey.USERCENTERTOKEN, response.data?.userCenterToken)
                                SPUtils.getInstance().put(SPKey.HEAD_URL, response.data?.headUrl)
                                SPUtils.getInstance()
                                    .put(SPKey.USERCENTERTOKEN, response.data?.userCenterToken)
                                mobliePhoneInfoLd?.postValue(response.data?.phone)
                            }
                        } catch (e: Exception) {
                        }

                    }
                })
        }

    }
}