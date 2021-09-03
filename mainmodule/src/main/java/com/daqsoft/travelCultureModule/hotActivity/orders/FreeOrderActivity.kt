package com.daqsoft.travelCultureModule.hotActivity.orders

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
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.RegexUtil
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainActivityOrderFreeBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.trello.rxlifecycle3.android.ActivityEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.textColor
import java.util.concurrent.TimeUnit

/**
 * @des 免费预订活动
 * @author PuHua
 * @Date 2020/1/6 10:20
 */

@Route(path = MainARouterPath.MAIN_ACTIVITY_ORDER)
class FreeOrderActivity :
    TitleBarActivity<MainActivityOrderFreeBinding, FreeOrderActivityViewModel>() {

    @JvmField
    @Autowired
    var id: String = ""

    @JvmField
    @Autowired
    var type: String = ""

    var maxSeatNum = 1
    /**
     * 可以选择的最大的数
     */
    var numbers = mutableListOf<String>()

    override fun getLayout(): Int = R.layout.main_activity_order_free

    override fun setTitle(): String = getString(R.string.main_activity_order_title)

    override fun injectVm(): Class<FreeOrderActivityViewModel> =
        FreeOrderActivityViewModel::class.java

    override fun initView() {
        mBinding.vm = mModel
        if (!type.isNullOrEmpty()) {
            when (type) {
                ActivityMethod.ACTIVITY_MODE_FREE -> {

                }
            }
        }
        initViewModel()
        initViewEvent()
    }

    private fun initViewEvent() {
        // 在手机号输入之后验证是否有预订的订单
        mBinding.tvOrderPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length == 11) {
                    mModel.checkExistNumber(s.toString())
                }
            }

        })
        mBinding.tvCheckPrice.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.IntegralModule.INTEGRAL_DETAIL_ACTIVITY)
                .navigation()
        }
    }

    private fun initViewModel() {

        mModel.activityDetailBean.observe(this, Observer {
            mBinding.url = it.images
            var imageUrl = ""
            if (!it.images.isNullOrEmpty()) {
                var imageUrls = it.images.split(",")
                if (!imageUrls.isNullOrEmpty()) {
                    imageUrl = imageUrls[0]
                }
            }
            Glide.with(this@FreeOrderActivity)
                .load(imageUrl)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.ivImage)

            mBinding.activityName = it.name
            // 时间
            mBinding.time = DateUtil.getTwoDateStrs(it.useStartTime, it.useEndTime)
            mBinding.date = DateUtil.getTwoDateStrs(it.orderValidStart, it.orderValidEnd)
            mBinding.address = it.address
            mBinding.recreation = it.resourceNames
            if (it.limitNum.toInt() > 0) {
                mBinding.notice = getString(R.string.home_activity_limit_numbers, it.limitNum)
                mBinding.tvOrderNumberNotice.visibility = View.VISIBLE
            }

            if (!it.seatId.isNullOrEmpty() && it.seatId.toInt() > 0) {
                mBinding.tvOrderSeatLabel.visibility = View.VISIBLE
                mBinding.tvOrderSeat.visibility = View.VISIBLE
            } else {
                mBinding.tvOrderSeatLabel.visibility = View.GONE
                mBinding.tvOrderSeat.visibility = View.GONE
            }

            if (it.integral.toInt() == 0) {
                mBinding.price = getString(R.string.order_free)
                mBinding.tvPrice.textColor = resources.getColor(R.color.colorPrimary)
                mBinding.tvTotal.textColor = resources.getColor(R.color.colorPrimary)
                mBinding.rlPrice.visibility = View.GONE
                mBinding.total = getString(R.string.order_free)

            } else {
                mBinding.price = it.integral
                mBinding.total = it.integral
                mBinding.tvPrice.textColor = resources.getColor(R.color.ff9e05)
                mBinding.tvTotal.textColor = resources.getColor(R.color.ff9e05)
                mBinding.tvPriceIntegral.visibility = View.VISIBLE
                mBinding.tvTotalIntegral.visibility = View.VISIBLE
            }

            for (i in 0 until it.limitPayCount.toInt()) {
                numbers.add((i + 1).toString())
            }
            numberPv.setPicker(numbers)
        })



        if (mModel.seatId.value.isNullOrEmpty()) {

        }
        mModel.timer.observe(this, Observer {
            initTimer()
        })
        mModel.isNeedCode.observe(this, Observer {
            if (it) {
                mBinding.tvOrderVerifyLabel.visibility = View.VISIBLE
                mBinding.tvOrderVerify.visibility = View.VISIBLE
                mBinding.tvSendCode.visibility = View.VISIBLE
            } else {
                mBinding.tvOrderVerifyLabel.visibility = View.GONE
                mBinding.tvOrderVerify.visibility = View.GONE
                mBinding.tvSendCode.visibility = View.GONE
            }
        })
        mModel.vipInfold.observe(this, Observer {
            if (it != null) {
                if (!it.name.isNullOrEmpty()) {
                    mModel.userFiled.set(it.name)
                }
                if (!it.phone.isNullOrEmpty()) {
                    mModel.phoneField.set(it.phone)
                    mModel.checkExistNumber(it.phone!!)
                } else {
                    val phone = SPUtils.getInstance().getString(SPKey.PHONE)
                    mModel.phoneField.set(phone)
                    mModel.checkExistNumber(phone)
                }
                if (!it.idCard.isNullOrEmpty()) {
                    mBinding.tvIdCard.setText(it.idCard)
                }
            } else {
                val phone = SPUtils.getInstance().getString(SPKey.PHONE)
                mModel.phoneField.set(phone)
                mModel.checkExistNumber(phone)
            }
        })
        mModel.finishPage.observe(this, Observer {
            finish()
        })
    }

    override fun initData() {

        mModel.getActivityDetail(id)
        mModel.getVipInfo()
        mBinding.tvOrder.onNoDoubleClick {
            generateOrder()
        }
    }

    /**
     * 显示数量选择器
     */
    fun selectNumberCommand(v: View) {
        numberPv.show()
    }


    /**
     * 生成订单
     */
    fun generateOrder() {
        var phoneNumber: String? = mBinding.tvOrderPhone.text.toString()
        var idCard: String? = mBinding.tvIdCard.text.toString()
        if (phoneNumber.isNullOrEmpty()) {
            ToastUtils.showMessage("请先输入手机号码")
        } else if (!RegexUtil.isMobilePhone(phoneNumber)) {
            ToastUtils.showMessage("请先输入正确的手机号码")
        } else if (idCard.isNullOrEmpty()) {
            ToastUtils.showMessage("请先输入正确的身份证信息")
        } else {
            mModel.phoneField.set(phoneNumber)
            mModel.generateOrder(id, type)
        }
    }

    /**
     * 去到座位选择页面
     */
    fun selectSeat(v: View) {

        // 时间
        var startTime = mModel.activityDetailBean.value!!.useStartTime.substring(
            0, mModel.activityDetailBean.value!!
                .useStartTime.length - 3
        )
        var endTime = mModel.activityDetailBean.value!!.useEndTime.substring(
            0,
            mModel.activityDetailBean.value!!.useEndTime.length - 3
        )

        ARouter.getInstance()
            .build(MainARouterPath.MAIN_SEAT_SELECT_ACTIVITY)
            .withString("id", mModel.activityDetailBean.value!!.id)
            .withString("name", mModel.activityDetailBean.value!!.name)
            .withString("time", "$startTime~$endTime")
            .withInt("maxSelect", maxSeatNum)
            .navigation(this, 0)


    }


    /**
     * 数量选择器
     */
    private val numberPv by lazy {

        val pV = OptionsPickerBuilder(this, OnOptionsSelectListener { s1, s2, s3, v ->

            mModel.selectNumber.set(numbers[s1])
            var total = mModel.activityDetailBean.value!!.integral.toInt() * numbers[s1].toInt()
            mBinding.total =
                if (total == 0) {
                    getString(R.string.order_free)
                } else {
                    (mModel.activityDetailBean.value!!.integral.toInt() * numbers[s1].toInt()).toString()
                }
            // 设置座位最大选座
            maxSeatNum = numbers[s1].toInt()
        }).build<String>()

        pV
    }
    /**
     * 计数器
     */
    var d: Disposable? = null

    private fun initTimer() {
        var timeLong: Long = 60
        d = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .observeOn(AndroidSchedulers.mainThread())
            .map { count ->
                timeLong--
            }
            .subscribe { time ->
                if (time > 0) {
                    var to = getString(R.string.user_str_count_down, time)
                    mBinding.tvSendCode.text = to
                    mBinding.tvSendCode.isClickable = false
                    mBinding.tvSendCode.isEnabled = false
                } else {
                    mBinding.tvSendCode.text = getString(R.string.user_label_code)
                    mBinding.tvSendCode.isClickable = true
                    mBinding.tvSendCode.isEnabled = true
                }

            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0 && data != null) {
            var string = data!!.getStringExtra("ids")
            mModel.seatId.postValue(string)
            var seatNames = data!!.getStringExtra("sites")
            if (seatNames.isNullOrEmpty()) {
                mBinding.tvOrderSeat.text = "已选"
            } else {
                mBinding.tvOrderSeat.text = seatNames
            }
        }
    }

}

