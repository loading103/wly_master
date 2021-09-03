package com.daqsoft.venuesmodule.activity

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AESOperator
import com.daqsoft.baselib.utils.SM4Util
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.venues.VenuesRepository
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ActivityRoomOrderBinding
import com.jakewharton.rxbinding2.view.RxView
import com.trello.rxlifecycle3.android.ActivityEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.toast
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

/**
 *@package:com.daqsoft.venuesmodule.activity
 *@date:2020/4/21 9:39
 *@author: caihj
 *@des:活动室预约
 **/
@Route(path = ARouterPath.VenuesModule.ACTIVITY_ROOM_ORDER)
class ActivityRoomOrderActivity :
    TitleBarActivity<ActivityRoomOrderBinding, ActivityRoomOrderVM>() {

    @Autowired
    @JvmField
    var id = ""

    /**
     * 活动室名称
     */
    @Autowired
    @JvmField
    var name = ""

    /**
     * 活动室图片
     */
    @Autowired
    @JvmField
    var images = ""

    /**
     * 活动室标签
     */
    @Autowired
    @JvmField
    var labelName = ""

    /**
     * 诚信面审
     */
    @Autowired
    @JvmField
    var faithAuditStatus = ""

    /**
     * 地址
     */
    @Autowired
    @JvmField
    var address = ""

    /**
     * 预订日期
     */
    @Autowired
    @JvmField
    var orderDate = ""

    /**
     * 预订时间
     */
    @Autowired
    @JvmField
    var orderTime = ""

    /**
     * 时间段id
     */
    @Autowired
    @JvmField
    var roomOrderTimeId = ""
    @Autowired
    @JvmField
    var venueName = ""

    override fun getLayout(): Int = R.layout.activity_room_order

    override fun setTitle(): String = getString(R.string.activity_room_order_title)

    override fun injectVm(): Class<ActivityRoomOrderVM> = ActivityRoomOrderVM::class.java

    @SuppressLint("CheckResult")
    override fun initView() {
        mModel.viewListener = object : ActivityRoomOrderVM.ViewListener {
            override fun onCountDown() {
                toast("验证码发送成功!")
                if (d != null) {
                    shutDown = true
                }
                initTimer()
            }
        }
        mModel.isNeedCode.observe(this, Observer {
            dissMissLoadingDialog()
            if (!it) {
                mBinding.tvRoomCode.visibility = View.GONE
                mBinding.etCode.visibility = View.GONE
                mBinding.bindPhoneCodeAvail.visibility = View.GONE
            } else {
                mBinding.tvRoomCode.visibility = View.VISIBLE
                mBinding.etCode.visibility = View.VISIBLE
                mBinding.bindPhoneCodeAvail.visibility = View.VISIBLE
            }
        })
        mModel.vipInfold.observe(this, Observer {
            if (it != null) {
                if (!it.name.isNullOrEmpty()) {
                    mBinding.etRoomPeople.setText("" + it.name)
                }
                var phone: String? = SPUtils.getInstance().getString(SPKey.PHONE)
                if (!phone.isNullOrEmpty()) {
                    mBinding.etPhone.setText("" + phone)
                    mModel.phone = phone
                    mModel.checkExistNumber()
                }
                if (!it.idCard.isNullOrEmpty()) {
                    mBinding.etId.setText("" + it.idCard)
                }
            } else {
                var phone: String? = SPUtils.getInstance().getString(SPKey.PHONE)
                if (!phone.isNullOrEmpty()) {
                    mBinding.etPhone.setText("" + phone)
                    mModel.phone = phone
                    mModel.checkExistNumber()
                }
            }
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
        mBinding.etPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.length == 11) {
                        mModel.phone = s.toString()
                        showLoadingDialog()
                        mModel.checkExistNumber()
                    }
                }
            }

        })
        //提交订单
        RxView.clicks(mBinding.tvSubmit)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                checkInput()
            }
        mModel.orderSuccess.observe(this, Observer {
            if (it) {
                dissMissLoadingDialog()
            }
        })
    }

    open fun sendCode(v: View) {
        val phone = mBinding.etPhone.text.toString()
        if (TextUtils.isEmpty(phone)) {
            toast("请输入手机号!")
            return
        }
        mModel.phone = phone
        mModel.senCode()

    }

    /**
     *提交订单
     */
    private fun checkInput() {
        val userName = mBinding.etRoomPeople.text.toString()
        if (userName.isNullOrEmpty()) {
            toast("请输入预订人!")
            return
        }
        val phone = mBinding.etPhone.text.toString()
        if (phone.isNullOrEmpty()) {
            toast("请输入电话号码!")
            return
        }
        if (phone.length < 11) {
            toast("请输入正确的电话号码!")
            return
        }
        val code = mBinding.etCode.text.toString()
        if (mModel.isNeedCode.value!!) {
            if (code.isNullOrEmpty()) {
                toast("请输入验证码!")
                return
            }
        }
        val idCard = mBinding.etId.text.toString()
        if (idCard.isNullOrEmpty()) {
            toast("请输入证件号!")
            return
        }
        if (idCard.length < 18) {
            toast("请输入正确的证件号!")
            return
        }
        val useNum = mBinding.etCount.text.toString()
        if (useNum.isNullOrEmpty()) {
            toast("请输入使用人数!")
            return
        }
        mModel.roomId = id
        mModel.phone = phone
        mModel.userName = userName
        mModel.code = code
        mModel.idCard = idCard
        mModel.useNum = useNum
        mModel.remarks = mBinding.etRemarks.text.toString()
        mModel.roomOrderTimeId = roomOrderTimeId
        showLoadingDialog()
        mModel.generateOrder()
    }

    @SuppressLint("CheckResult")
    override fun initData() {
        mBinding.tvOrderDate.text = orderDate
        mBinding.tvOrderTime.text = orderTime
        Glide.with(this)
            .load(images.split(",")[0])
            .into(mBinding.aiRoomImg)
        mBinding.tvRoomLabelName.text = labelName
        mBinding.tvRoomTitle.text = name
        mBinding.tvRoomAddress.text = address
        if (!venueName.isNullOrEmpty()) {
            mBinding.tvOrderFee.text = "" + venueName
        }
        if (faithAuditStatus == "1") {
            mBinding.vLine.visibility = View.GONE
            mBinding.tvRoomFaithAuditStatus.visibility = View.GONE
            mBinding.imgAiRooMianshen.visibility = View.VISIBLE
        }
        mModel.faithAuditStatus = faithAuditStatus
//        mBinding.etPhone.text = Editable.Factory.getInstance()
//            .newEditable(SPUtils.getInstance().getString(SPKey.PHONESTR))
//        mModel.phone = SPUtils.getInstance().getString(SPKey.PHONESTR)
        mModel.getVipInfo()
    }

    /**
     * 计时器相关
     */
    var d: Disposable? = null
    var shutDown = false
    fun initTimer() {
        var timeLong: Long = 60
        d = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .observeOn(AndroidSchedulers.mainThread())
            .takeWhile { timeLong >= 0 }
            .map { count ->
                timeLong--
            }
            .subscribe { time ->
                if (time > 0) {
                    var to = getString(R.string.user_str_count_down, time)
                    mBinding.bindPhoneCodeAvail.text = to
                    mBinding.bindPhoneCodeAvail.isEnabled = false
                } else {
                    mBinding.bindPhoneCodeAvail.text = getString(R.string.user_get_code)
                    mBinding.bindPhoneCodeAvail.isEnabled = true
                }

            }
        shutDown = false
    }
}


class ActivityRoomOrderVM : BaseViewModel() {

    var phone = ""

    // 预订人
    var userName = ""

    // 使用人数
    var useNum = ""

    // 证件号
    var idCard = ""

    // 验证码
    var code = ""

    // 活动室id
    var roomId = ""

    // 时间段id
    var roomOrderTimeId = ""
    var remarks = ""
    var orderResultBean = MutableLiveData<OrderResultBean>()
    var faithAuditStatus = ""
    val isNeedCode = MutableLiveData<Boolean>(true)

    val vipInfold = MutableLiveData<VipInfoBean>()
    /**
     * 发送验证码
     */
    fun senCode() {
        mPresenter.value?.loading = true
        HomeRepository.service
            .getSendCode(phone)
            .excute(object : BaseObserver<String>(mPresenter) {
                override fun onSuccess(response: BaseResponse<String>) {
                    if (response.code == 0) {
                        viewListener?.onCountDown()
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
    fun generateOrder() {
        mPresenter.value?.loading = false
        val param = HashMap<String, String>()
        param["code"] = code
        param["roomId"] = roomId
        param["roomOrderTimeId"] = roomOrderTimeId
        param["userName"] = userName
        param["useNum"] = useNum
        param["userPhone"] = SM4Util.encryptByEcb(phone)
        param["orderNum"] = "1"
        param["orderType"] = ResourceType.CONTENT_TYPE_ACTIVITY_SHIU
        param["channel"] = Constant.HOME_CLIENT_TYPE
        param["idCard"] = SM4Util.encryptByEcb(idCard)
        param["remark"] = remarks
        HomeRepository.service.generateOrder(param)
            .excute(object : BaseObserver<OrderResultBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderResultBean>) {
                    if (response.code == 0) {
                        orderResultBean.postValue(response.data)
                        toast.postValue("订单已提交!")
                        response.data?.orderCode?.let { payOrder(it) }
                    } else {
                        mError.postValue(response)
                        toast.postValue(response.message)
                    }

                }

                override fun onFailed(response: BaseResponse<OrderResultBean>) {
                    mError.postValue(response)
                }
            })
    }

    /**
     * 支付订单
     */
    fun payOrder(orderCode: String) {
        mPresenter.value?.loading = false
        HomeRepository.service.payOrder(orderCode)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    saveOrder(orderCode)
                }
            })
    }

    /**
     * 保存订单
     */
    var orderSuccess = MutableLiveData<Boolean>(false)

    fun saveOrder(orderCode: String) {
        mPresenter.value?.loading = false
        HomeRepository.service.saveOrder(orderCode)
            .excute(object : BaseObserver<OrderSaveBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderSaveBean>) {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_ACTIVITY_SUCCESS)
                        .withString("orderCode", response.data!!.orderCode)
                        .withString("orderType", ResourceType.CONTENT_TYPE_ACTIVITY_SHIU)
                        .withString("faithAuditStatus", faithAuditStatus)
                        .navigation()
                    orderSuccess.postValue(true)

                }
            })
    }

    /**
     * 查询当前手机号存在的订单数
     */
    fun checkExistNumber() {
        HomeRepository.service.getCheckExistOrderNumbers(
            phone,
            ResourceType.CONTENT_TYPE_ACTIVITY_SHIU
        )
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
        VenuesRepository.venuesService.getVipInfo()
            .excute(object : BaseObserver<VipInfoBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<VipInfoBean>) {
                    if (response.data != null) {
                        if (!response.data!!.idCard.isNullOrEmpty()) {
                            response.data!!.idCard =
                                SM4Util.decryptByEcb(response.data!!.idCard)
                        }
                    }
                    vipInfold?.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<VipInfoBean>) {
                    vipInfold?.postValue(null)
                }
            })
    }

    var viewListener: ViewListener? = null
        get() = field
        set(value) {
            field = value
        }

    interface ViewListener {
        fun onCountDown()
    }
}