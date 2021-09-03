package com.daqsoft.venuesmodule.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.RegexUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.Contact
import com.daqsoft.provider.bean.VenueOrderViewInfo
import com.daqsoft.provider.businessview.base.BusinessCode
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ActivityVenueReservationBinding
import com.daqsoft.venuesmodule.databinding.ActivityVenueResevationInfoBinding
import com.daqsoft.venuesmodule.model.VenueOrderModel
import com.daqsoft.venuesmodule.viewmodel.VenueResevationInfoViewModel
import com.trello.rxlifecycle3.android.ActivityEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_venue_activity.*
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * @Description 场馆预约详情
 * @ClassName   VenueResevationInfoActivity
 * @Author      luoyi
 * @Time        2020/5/12 15:09
 */
@Route(path = ARouterPath.VenuesModule.VENUES_RESERVATION_INFO_ACTIVITY)
class VenueResevationInfoActivity :
    TitleBarActivity<ActivityVenueResevationInfoBinding, VenueResevationInfoViewModel>() {

    /**
     * type 1 个人 2 团队
     */
    @JvmField
    @Autowired
    var type: Int = 1
    /**
     * 预订日期
     */
    @JvmField
    @Autowired
    var date: String = ""
    /**
     * 场馆id
     */
    @JvmField
    @Autowired
    var venueId: String = ""
    /**
     *  0 到场人数  1 成人 2 儿童 3 青少年 4 老人
     */
    var selectNumType: Int = 0
    /**
     * 场馆订单视图
     */
    var venueOrderViewInfo: VenueOrderViewInfo? = null

    var venueOrderModel: VenueOrderModel? = null
    /**
     * 是否需要短信验证码
     */
    var isNeedSmsCode: Boolean = true

    override fun getLayout(): Int {
        return R.layout.activity_venue_resevation_info
    }

    override fun setTitle(): String {
        return "场馆预约"
    }

    override fun injectVm(): Class<VenueResevationInfoViewModel> {
        return VenueResevationInfoViewModel::class.java
    }

    override fun initPageParams() {
        isInitImmerBar = false
    }

    override fun initView() {
        initViewModel()
        initViewEvent()
        venueOrderModel = VenueOrderModel()
        if (type == 2) {
            venueOrderModel?.reservationType = "TEAM"
        } else {
            venueOrderModel?.reservationType = "PERSON"
        }
        venueOrderModel?.orderDate = date
        venueOrderModel?.orderNum = 1

    }

    override fun initData() {
        showLoadingDialog()
        mModel.getVenueOrderView(venueId, date)
    }

    private fun initViewEvent() {
        mBinding.vPersonReservationInfo.imgSelectVenueRtnName.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_CONTACT_MANAGEMENT)
                    .withInt("type", 2)
                    .navigation(this@VenueResevationInfoActivity, 2)
            }
        }

        // 在手机号输入之后验证是否有预订的订单
        mBinding.vPersonReservationInfo.edtVenueRtnPphoneValue.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (s!!.length == 11) {
                    mModel.checkExistNumber(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        mBinding.vPersonReservationInfo.tvVenueRtnSendCode.onNoDoubleClick {
            var phoneNumer: String? =
                mBinding.vPersonReservationInfo.edtVenueRtnPphoneValue.text.toString()
            if (phoneNumer.isNullOrEmpty()) {
                ToastUtils.showMessage("请先输入手机号码")
            } else if (!RegexUtil.isMobilePhone(phoneNumer)) {
                ToastUtils.showMessage("请输入正确的手机号码")
            } else {
                mModel.sendPhoneCode(phoneNumer)
            }
        }
        // 选择到场人数
        mBinding.vPersonReservationInfo.tvVenueRtvInpnumValue.onNoDoubleClick {
            selectNumType = 0
            showSelectNumber()
        }
        // 选择成人数量
        mBinding.vPersonReservationInfo.tvVenueRtnInAdultVlaue.onNoDoubleClick {
            selectNumType = 1
            showSelectNumber()
        }
        // 选择儿童数量
        mBinding.vPersonReservationInfo.tvVenueRtnInChildVlaue.onNoDoubleClick {
            selectNumType = 2
            showSelectNumber()
        }
        // 选择青少年
        mBinding.vPersonReservationInfo.tvVenueRtnInYouthVlaue.onNoDoubleClick {
            selectNumType = 3
            showSelectNumber()
        }
        // 老人
        mBinding.vPersonReservationInfo.tvVenueRtnInOldVlaue.onNoDoubleClick {
            selectNumType = 4
            showSelectNumber()
        }
        mBinding.llvVenueRtnBottom.onNoDoubleClick {
            // 提交场馆预约信息
            var phoneNumber: String? =
                mBinding.vPersonReservationInfo.edtVenueRtnPphoneValue.text.toString()
            var userName: String? =
                mBinding.vPersonReservationInfo.edtVenueRtnPnameValue.text.toString()
            var smsCode: String? =
                mBinding.vPersonReservationInfo.edtVenueReservationPpcodeValue.text.toString()
            var idCard: String? =
                mBinding.vPersonReservationInfo.edtVenueRtnIdcardValue.text.toString()
            var commpanyName: String? =
                mBinding.vPersonReservationInfo.edtVenueRtnInCompanyNameVlaue.text.toString()
            var expectTime: String? = mBinding.edtVenueRtnInTime.text.toString()
            var remark: String? = mBinding.edtVenueRtnRemarkValue.text.toString()
            if (userName.isNullOrEmpty()) {
                ToastUtils.showMessage("请选择或输入预订人姓名!")
                return@onNoDoubleClick
            } else if (phoneNumber.isNullOrEmpty()) {
                ToastUtils.showMessage("请输入手机号!")
                return@onNoDoubleClick
            } else if (!RegexUtil.isMobilePhone(phoneNumber)) {
                ToastUtils.showMessage("请输入正确的手机号!")
                return@onNoDoubleClick
            } else if (idCard.isNullOrEmpty()) {
                ToastUtils.showMessage("请输入身份证信息!")
                return@onNoDoubleClick
            }
            if (venueOrderModel!!.useNum <= 0) {
                ToastUtils.showMessage("请选择到场人数!")
                return@onNoDoubleClick
            }
            if (isNeedSmsCode) {
                when {
                    smsCode.isNullOrEmpty() -> {
                        ToastUtils.showMessage("请输入验证码!")
                        return@onNoDoubleClick
                    }
                    smsCode.length < 6 -> {
                        ToastUtils.showMessage("请输入正确的验证码!")
                        return@onNoDoubleClick
                    }
                    else -> {
                        venueOrderModel?.code = smsCode
                    }
                }
            }
            showLoadingDialog()
            venueOrderModel?.userName = userName
            venueOrderModel?.userPhone = phoneNumber
            venueOrderModel?.orderType = "CONTENT_TYPE_VENUE"
            venueOrderModel?.venueId = venueId.toInt()
            venueOrderModel?.idCard = idCard
            if (!commpanyName.isNullOrEmpty()) {
                venueOrderModel?.companyName = commpanyName
            }
            if (!expectTime.isNullOrEmpty()) {
                venueOrderModel?.expectedTime = expectTime
            }
            if (!remark.isNullOrEmpty()) {
                venueOrderModel?.remark = remark
            }
//            mModel.createVenueOrder(venueOrderModel)
        }

    }

    private fun initViewModel() {
        mModel.generVenuOrderLiveData.observe(this, Observer {
            //            dissMissLoadingDialog()
            if (!it.orderCode.isNullOrEmpty()) {
                mModel.payOrder(it.orderCode)
//                ToastUtils.showMessage("恭喜您，预约成功!")
            } else {
                dissMissLoadingDialog()
                ToastUtils.showMessage("预约失败，请稍后再试!")
            }
        })
        mModel.orderSuccess.observe(this, Observer {
            dissMissLoadingDialog()
            finish()
        })
        mModel.venueOrderViewInfoLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null) {
                venueOrderViewInfo = it
                bindOrderView(it!!)
                mBinding.vVenueMainReservationInfo.visibility = View.VISIBLE
            } else {
                ToastUtils.showMessage("非常抱歉，为获取到相关场馆信息~")
                mBinding.vVenueMainReservationInfo.visibility = View.GONE
            }
        })

        // 是否需要发送验证码
        mModel.isNeedCode.observe(this, Observer {
            isNeedSmsCode = it
            if (it) {
                mBinding.vPersonReservationInfo.edtVenueReservationPpcodeValue.visibility =
                    View.VISIBLE
                mBinding.vPersonReservationInfo.tvVenueRtnPhoneCode.visibility = View.VISIBLE
                mBinding.vPersonReservationInfo.tvVenueRtnSendCode.visibility = View.VISIBLE
                mBinding.vPersonReservationInfo.vLineThree.visibility = View.VISIBLE
            } else {
                mBinding.vPersonReservationInfo.edtVenueReservationPpcodeValue.visibility =
                    View.GONE
                mBinding.vPersonReservationInfo.tvVenueRtnPhoneCode.visibility = View.GONE
                mBinding.vPersonReservationInfo.tvVenueRtnSendCode.visibility = View.GONE
                mBinding.vPersonReservationInfo.vLineThree.visibility = View.GONE
            }
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null) {
                when (it.requestFlag) {
                    BusinessCode.PAY_ERROR_CODE -> {
                        ToastUtils.showMessage("恭喜您，预约成功!")
                        finish()
                    }
                    BusinessCode.ORDER_CALLBACK_ERRO_CODE -> {
                        ToastUtils.showMessage("恭喜您，预约成功!")
                        finish()
                    }
                }
            }
        })
    }

    private fun bindOrderView(data: VenueOrderViewInfo) {
        // 场馆tup
        loadVenueImage(data)
        mBinding.txtVenueName.text = "" + data.venueName
        mBinding.txtVenueType.text = "" + data.type
        mBinding.txtVenueMaxPerson.text = "最大接待人数：" + data.maxNum + "人"
        // 预约信息
        loadResevationInfo(data)

        // 预约讲解员信息
        loadCommentatorInfo(data)

        loadPersonResvationInfo(data)
    }

    /**
     * 加载个人信息配置
     */
    private fun loadPersonResvationInfo(data: VenueOrderViewInfo) {
        mBinding.vPersonReservationInfo.tvVenueRtnInChildVlaue.hint =
            getString(R.string.venue_reservation_hint_children, data.childNum.toString())
    }

    /**
     * 预约信息
     */
    @SuppressLint("SetTextI18n")
    private fun loadResevationInfo(data: VenueOrderViewInfo) {
        // 目前预约免费
        mBinding.vReservationInfo.txtVenueReservationMoneyValue.text = "免费"
        when (type) {
            1 -> {
                mBinding.vReservationInfo.txtVenueReservationTypeValue.text = "个人预约"
            }
            2 -> {
                mBinding.vReservationInfo.txtVenueReservationTypeValue.text = "团队预约"
            }
        }
        if (!date.isNullOrEmpty()) {
            mBinding.vReservationInfo.txtVenueReservationDateValue.text = "$date " +
                    "${DateUtil.getDayOfWeek(DateUtil.formatDate("yy-MM-dd", date))}"
        }
        var timeInfos = data.times
        if (!timeInfos.isNullOrEmpty()) {
            var timeInfo = timeInfos[0]
            mBinding.vReservationInfo.txtVenueReservationTimeValue.text = "" +
                    timeInfo.orderTimeSubStart + "-" + timeInfo.orderTimeSubEnd
            if (!timeInfo.remark.isNullOrEmpty()) {
                mBinding.vReservationInfo.txtVenueReservationRemarkValue.text = timeInfo.remark
                venueOrderModel?.venueRuleId = timeInfo.id
            }
        }
    }

    /**
     * 预约讲解员信息
     */
    private fun loadCommentatorInfo(data: VenueOrderViewInfo) {

    }

    /**
     * 加载场馆图片
     */
    private fun loadVenueImage(data: VenueOrderViewInfo) {
        var imageUrl = ""
        if (!data.images.isNullOrEmpty()) {
            var images = data.images.split(",")
            if (!images.isNullOrEmpty()) {
                imageUrl = images[0]
            }
            Glide.with(this@VenueResevationInfoActivity)
                .load(imageUrl)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.imgVenue)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 2) {
            // 选择联系人返回
            if (data?.getBundleExtra("bundle") != null) {
                var bundle = data?.getBundleExtra("bundle")
                var item = bundle.getParcelable<Contact>("object")
                if (item != null) {
                    mBinding.vPersonReservationInfo.edtVenueRtnPnameValue.setText("" + item!!.name)
                    mBinding.vPersonReservationInfo.edtVenueRtnPphoneValue.setText("" + item!!.phone)
                    if (!item.phone.isNullOrEmpty()) {
                        mModel.checkExistNumber(item.phone!!)
                    }
                }
            }
        }
    }

    /**
     * 计数器
     */
    private var timerDisposable: Disposable? = null

    private fun initTimer() {
        var timeLong: Long = 60
        timerDisposable = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .observeOn(AndroidSchedulers.mainThread())
            .map { count ->
                timeLong--
            }
            .subscribe { time ->
                if (time > 0) {
                    var to = getString(R.string.user_str_count_down, time)
                    mBinding.vPersonReservationInfo.tvVenueRtnSendCode.text = to
                    mBinding.vPersonReservationInfo.tvVenueRtnSendCode.isClickable = false
                    mBinding.vPersonReservationInfo.tvVenueRtnSendCode.isEnabled = false
                } else {
                    mBinding.vPersonReservationInfo.tvVenueRtnSendCode.text =
                        getString(R.string.user_label_send_code)
                    mBinding.vPersonReservationInfo.tvVenueRtnSendCode.isClickable = true
                    mBinding.vPersonReservationInfo.tvVenueRtnSendCode.isEnabled = true
                }

            }
    }

    /**
     * 数量选择器
     */
    private val numberPv by lazy {

        val pV = OptionsPickerBuilder(this, OnOptionsSelectListener { s1, s2, s3, v ->
            // 0 到场人数  1 成人 2 儿童 3 青少年 4 老人
            var num = s1 + 1
            when (selectNumType) {
                0 -> {
                    mBinding.vPersonReservationInfo.tvVenueRtvInpnumValue.setText("${num}人")
                    venueOrderModel?.useNum = num
                }
                1 -> {
                    mBinding.vPersonReservationInfo.tvVenueRtnInAdultVlaue.setText("${num}人")
                    venueOrderModel?.adultNum = num
                }
                2 -> {
                    mBinding.vPersonReservationInfo.tvVenueRtnInChildVlaue.setText("${num}人")
                    venueOrderModel?.childNum = num
                }
                3 -> {
                    mBinding.vPersonReservationInfo.tvVenueRtnInYouthVlaue.setText("${num}人")
                    venueOrderModel?.teenagerNum = num
                }
                4 -> {
                    mBinding.vPersonReservationInfo.tvVenueRtnInOldVlaue.setText("${num}人")
                    venueOrderModel?.oldManNum = num
                }
            }
        }).build<String>()

        pV
    }

    /**
     * 线上选择数量弹窗
     */
    private fun showSelectNumber() {
        if (selectNumType != 0 && venueOrderModel!!.useNum <= 0) {
            ToastUtils.showMessage("请先选择入场人数！")
            return
        } else if (selectNumType != 0 && venueOrderModel!!.useNum == venueOrderModel!!.getHaveSelectUserNum()) {
            ToastUtils.showMessage("选择人数已达到到入场人数！")
            return
        }

        if (venueOrderViewInfo != null) {
            when (type) {
                1 -> {
                    // 个人预约
                    numberPv?.setPicker(getPersonRervationNum())
                }
                2 -> {
                    // 团队预约
                    numberPv?.setPicker(getTempResevationNum())
                }
            }
            numberPv?.show()
        }
    }

    /**
     * 获取团队预约到场人数
     */
    private fun getTempResevationNum(): MutableList<String> {
        var numbers: MutableList<String> = mutableListOf()
        if (selectNumType == 0) {
            for (i in venueOrderViewInfo!!.teamNumMin..venueOrderViewInfo!!.teamNumMax) {
                numbers.add(i.toString())
            }
        } else {
            var max = venueOrderModel!!.useNum - venueOrderModel!!.getHaveSelectUserNum()
            if (selectNumType == 2) {
                max = if (max < venueOrderViewInfo!!.childNum) {
                    max
                } else {
                    venueOrderViewInfo!!.childNum
                }
            }
            for (i in 1..max) {
                numbers.add(i.toString())
            }
        }
        return numbers
    }

    /**
     * 获取个人预约到场人数
     */
    private fun getPersonRervationNum(): MutableList<String> {
        var numbers: MutableList<String> = mutableListOf()
        if (selectNumType == 0) {
            for (i in venueOrderViewInfo!!.personNumMix..venueOrderViewInfo!!.personNumMax) {
                numbers.add(i.toString())
            }
        } else {
            var max = venueOrderModel!!.useNum - venueOrderModel!!.getHaveSelectUserNum()
            if (selectNumType == 2) {
                max = if (max < venueOrderViewInfo!!.childNum) {
                    max
                } else {
                    venueOrderViewInfo!!.childNum
                }
            }
            for (i in 1..max) {
                numbers.add(i.toString())
            }
        }
        return numbers
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            venueOrderModel = null
            timerDisposable?.dispose()
            timerDisposable = null
        } catch (e: Exception) {
        }

    }
}