package com.daqsoft.travelCultureModule.hotActivity.orders

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.RegexUtil
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainActivityOrderFreeBinding
import com.daqsoft.mainmodule.databinding.MainActivitySignBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.trello.rxlifecycle3.android.ActivityEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor
import java.util.concurrent.TimeUnit

/**
 * @des 志愿者报名活动
 * @author PuHua
 * @Date 2020/1/6 10:20
 */

@Route(path = MainARouterPath.MAIN_VOLUNTEER_SIGN)
class VolunteerSignActivity : TitleBarActivity<MainActivitySignBinding, FreeOrderActivityViewModel>() {

    @JvmField
    @Autowired
    var id: String = ""

    @JvmField
    @Autowired
    var type: String = ""


//    /**
//     * 可以选择的最大的数
//     */
//    var numbers = mutableListOf<String>()

    override fun getLayout(): Int = R.layout.main_activity_sign

    override fun setTitle(): String = getString(R.string.main_activity_order_title)

    override fun injectVm(): Class<FreeOrderActivityViewModel> = FreeOrderActivityViewModel::class.java

    override fun initView() {
        mBinding.vm = mModel
        // 设置为0，不传这个参数
        mModel.selectNumber.set("0")

        mModel.activityDetailBean.observe(this, Observer {
            mBinding.url = it.images

            mBinding.activityName = it.name
            // 时间
            var startTime = it.useStartTime.substring(0, it.useStartTime.length - 3)
            var endTime = it.useEndTime.substring(0, it.useEndTime.length - 3)
            mBinding.time = getString(R.string.order_activity_room_time_stamp_, startTime, endTime)
            mBinding.date = getString(R.string.order_activity_room_time_stamp_, startTime, endTime)
            mBinding.address = it.address
            mBinding.recreation = it.resourceNames

            val sOrder: String

            if (it.lineFlag.toInt() == 0) {
                // 线下报名
                mBinding.price = getString(R.string.order_off_line)
                mBinding.tvPrice.textColor = resources.getColor(R.color.colorPrimary)
                mBinding.rlPrice.visibility = View.GONE
                sOrder = "线下报名，直接前往"
                mBinding.tvOrder.isEnabled = false
                mBinding.tvOrder.backgroundColor = resources.getColor(R.color.c01_36cd64)
            } else {
                // 线上报名

                sOrder = "提交报名"
                mBinding.price = getString(R.string.order_on_line)
//                mBinding.total = it.integral
                mBinding.tvPrice.textColor = resources.getColor(R.color.ff9e05)
//                mBinding.tvPriceIntegral.visibility = View.VISIBLE
            }
            mBinding.tvOrder.text = sOrder
        })
        val phone = SPUtils.getInstance().getString(SPKey.PHONESTR)
        mModel.phoneField.set(phone)
        mModel.checkExistNumber(phone)
        val nickName = SPUtils.getInstance().getString(SPKey.NICK_NAME)
        mModel.userFiled.set(nickName)
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
        mModel.timer.observe(this, Observer {
            initTimer()
        })
        // 在手机号输入之后验证是否有预订的订单
        mBinding.tvOrderPhone.addTextChangedListener(object : TextWatcher {
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

    }

    override fun initData() {

        mModel.getActivityDetail(id)

    }

//    /**
//     * 显示数量选择器
//     */
//    fun selectNumberCommand(v: View) {
//        numberPv.show()
//    }

    /**
     * 生成订单
     */
    fun generateOrder(v: View) {
        var phoneNumber: String? = mBinding.tvOrderPhone.text.toString()
        if (phoneNumber.isNullOrEmpty()) {
            ToastUtils.showMessage("请先输入手机号码")
        } else if (!RegexUtil.isMobilePhone(phoneNumber)) {
            ToastUtils.showMessage("请先输入正确的手机号码")
        } else {
            mModel.phoneField.set(phoneNumber)
            mModel.generateOrder(id, type)
        }
    }


//    /**
//     * 数量选择器
//     */
//    private val numberPv by lazy {
//
//        val pV = OptionsPickerBuilder(this, OnOptionsSelectListener { s1, s2, s3, v ->
//
//            mModel.selectNumber.set(numbers[s1])
//            mBinding.total = (mModel.activityDetailBean.value!!.integral.toInt() * numbers[s1].toInt()).toString()
//        }).build<String>()
//
//        pV
//    }
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
                    mBinding.tvSendCode.isEnabled = false
                } else {
                    mBinding.tvSendCode.text = getString(R.string.user_label_code)
                    mBinding.tvSendCode.isEnabled = true
                }

            }
    }

}

