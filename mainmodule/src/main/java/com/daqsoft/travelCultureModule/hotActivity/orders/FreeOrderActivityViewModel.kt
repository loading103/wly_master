package com.daqsoft.travelCultureModule.hotActivity.orders

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AESOperator
import com.daqsoft.baselib.utils.SM4Util
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.HotActivityDetailBean
import com.daqsoft.provider.bean.VipInfoBean
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.travelCultureModule.hotActivity.bean.OrderNumberBean
import com.daqsoft.travelCultureModule.hotActivity.bean.OrderResultBean
import com.daqsoft.travelCultureModule.hotActivity.bean.OrderSaveBean
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description 活动预订的viewmodel
 * @ClassName   FreeOrderActivityViewModel
 * @Author      PuHua
 * @Time        2020/1/6 14:53
 */
class FreeOrderActivityViewModel() : BaseViewModel() {

    /**
     * 活动详情
     */
    var activityDetailBean = MutableLiveData<HotActivityDetailBean>()

    /**
     * 预订人
     */
    var userFiled = ObservableField<String>()

    /**
     * 选中的数量
     */
    var selectNumber = ObservableField("1")

    /**
     * 选座的回显
     */
    var selectSeat = ObservableField("")

    /**
     * 手机号码
     */
    val phoneField = ObservableField<String>()

    /**
     * 验证码
     */
    val codeField = ObservableField<String>()

    /**
     * 证件号
     */
    val idCardField = ObservableField<String>()

    /**
     * 座位号
     */
    val seatId = MutableLiveData<String>("")

    /**
     * 计时器
     */
    val timer = MutableLiveData<String>()

    /**
     * 该手机存在的订单数
     */
    val isNeedCode = MutableLiveData<Boolean>(true)

    /**
     * 订单结果
     */
    val orderResultBean = MutableLiveData<OrderResultBean>()

    val vipInfold = MutableLiveData<VipInfoBean>()

    val finishPage = MutableLiveData<Any>()

    /**
     * 发送验证码
     */
    val sendCodeReplyCommand = object : ReplyCommand {
        override fun run() {
            sendCode()
        }

    }


    /**
     * 获取活动详情
     */
    fun getActivityDetail(id: String) {
        mPresenter.value?.loading = true

        MainRepository.service.getActivityDetail(id, "")
            .excute(object : BaseObserver<HotActivityDetailBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HotActivityDetailBean>) {
                    activityDetailBean.postValue(response.data)

                }
            })
    }

    /**
     * 发送验证码
     */
    fun sendCode() {
        mPresenter.value?.loading = false
        if (phoneField.get()!!.isBlank()) {
            toast.postValue("请输入手机号")
            return
        }

        MainRepository.service.sendCode(phoneField.get()!!)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        toast.postValue("验证码发送成功!")
                        timer.postValue("")
                    } else {
                        toast.postValue(response.message)
                    }

                }
            })
    }

    /**
     * 生成订单
     * token	        令牌	                string	【必填】
     * code	            短信验证码	            string	第一次使用该手机号预订时【必填】
     * activityId	    活动 ID	                number	预订活动时【必填】
     * seatId	        座位ID	                number	预订活动的活动室 时【必填】
     * userName	        用户名称	            string	【必填】
     * userPhone	    手机号	                string	【必填】
     * orderNum	        订单数量	            number	预订活动时【必填】
     * orderType	    订单类型	            string	【必填】CONTENT_TYPE_ACTIVITY:预订活动, CONTENT_TYPE_ACTIVITY_SHIU:预订文化场馆
     * remark	        备注	                string
     * roomId	        活动室 ID	            number	预订文化场馆时 【必填】
     * roomOrderTimeId	活动室的活动预订规则ID	number	预订文化场馆时 【必填】
     * channel	        预订渠道	            string	【必填】查看常量接口
     * idCard	        身份证号	            string	【必填】
     * useNum	        使用人数	            number	预订文化场馆时 【必填】
     */
    fun generateOrder(id: String, orderType: String) {
        mPresenter.value?.loading = true
        mPresenter.value?.isNeedRecyleView = true
        val param = HashMap<String, String>()

        if (isNeedCode.value!!) {

            if (codeField.get().toString().isBlank()) {
                toast.postValue("请输入验证码")
                return
            }

            param["code"] = codeField.get().toString()
        }
        param["activityId"] = id

        if (seatId.value != "") {
            param["seatId"] = seatId.value.toString()
        }
        param["userName"] = userFiled.get().toString()
        param["userPhone"] = SM4Util.encryptByEcb(phoneField.get().toString())

        if (selectNumber.get() != "0") {
            param["orderNum"] = selectNumber.get().toString()
        } else {
            param["orderNum"] = "1"
        }
        param["orderType"] = ResourceType.CONTENT_TYPE_ACTIVITY
        param["channel"] = Constant.HOME_CLIENT_TYPE

        if (idCardField.get().toString().isBlank()) {
            toast.postValue("请输入身份证号")
            return
        }
        param["idCard"] = SM4Util.encryptByEcb(idCardField.get().toString())
        MainRepository.service.generateOrder(param)
            .excute(object : BaseObserver<OrderResultBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderResultBean>) {
                    if (response.code == 0) {
                        orderResultBean.postValue(response.data)
                        if (orderType == ActivityType.ACTIVITY_TYPE_RESERVE) {
                            ToastUtils.showMessage("预订成功！")
                        } else {
                            toast.postValue("报名成功!")
                        }
                        payOrder(response.data!!.orderCode, orderType)
                    }
                }
            })
    }

    /**
     * 支付订单
     */
    fun payOrder(orderCode: String, orderType: String) {
        MainRepository.service.payOrder(orderCode)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    saveOrder(orderCode, orderType)
                }
            })
    }

    /**
     * 保存订单
     */
    fun saveOrder(orderCode: String, orderType: String) {
        MainRepository.service.saveOrder(orderCode)
            .excute(object : BaseObserver<OrderSaveBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderSaveBean>) {
                    finishPage?.postValue(null)
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_ACTIVITY_SUCCESS)
                        .withString("orderCode", orderCode)
                        .withString("orderType", ResourceType.CONTENT_TYPE_ACTIVITY)
                        .withString("faithAuditStatus", "")
                        .withString("activityType", orderType)
                        .navigation()
                }
            })
    }

    /**
     * 查询当前手机号存在的订单数
     */
    fun checkExistNumber(phoneNumber: String) {
        MainRepository.service.getCheckExistOrderNumbers(phoneNumber, "CONTENT_TYPE_ACTIVITY")
            .excute(object : BaseObserver<OrderNumberBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderNumberBean>) {
                    if (response.data!!.existNum == 0) {
                        isNeedCode.postValue(true)
                    } else {
                        isNeedCode.postValue(false)
                    }
                }
            })
    }

    /**
     * 获取用户信息
     */
    fun getVipInfo() {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        MainRepository.service.getVipInfo().excute(object : BaseObserver<VipInfoBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<VipInfoBean>) {
                if (response.data != null) {
                    if (!response.data!!.idCard.isNullOrEmpty()) {
                        response.data!!.idCard = SM4Util.decryptByEcb(response.data!!.idCard)
                    }
                }
                vipInfold?.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<VipInfoBean>) {
                vipInfold?.postValue(null)
            }
        })
    }

}