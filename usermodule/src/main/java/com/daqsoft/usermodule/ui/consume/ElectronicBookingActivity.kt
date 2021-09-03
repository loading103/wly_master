package com.daqsoft.usermodule.ui.consume

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityElectronicBookingBinding
import com.daqsoft.provider.bean.ElectronicDetailBean
import com.daqsoft.usermodule.repository.constant.IntentConstant
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.excut
import com.daqsoft.provider.utils.TimeUtils
import com.daqsoft.usermodule.view.calendar.CalendarListener
import com.daqsoft.usermodule.view.calendar.CalenderFragment

/**
 * @des 预约消费的页面--小电商
 * @author PuHua
 * @date
 */
@Route(path = ARouterPath.UserModule.USER_CONSUME_ELECTRONIC_BOOKING)
class ElectronicBookingActivity :
    TitleBarActivity<ActivityElectronicBookingBinding, ElectronicBookingActivityViewModel>() {
    @JvmField
    @Autowired
    var id: Int = 0

    override fun getLayout(): Int = R.layout.activity_electronic_booking

    override fun setTitle(): String = getString(R.string.order_consume_booking)

    override fun injectVm(): Class<ElectronicBookingActivityViewModel> = ElectronicBookingActivityViewModel::class.java

    override fun initView() {
        mModel.detail.observe(this, Observer {
            mBinding.name = it.productName
            mBinding.effectTime = getString(
                R.string.order_activity_room_time_stamp_, TimeUtils.timeStamp2Date(it.useStartTime), TimeUtils
                    .timeStamp2Date(it.useEndTime)
            )
            mBinding.number = it.productNum

            isHideAnother = true
            transactFragment(
                R.id.fl_calender, CalenderFragment(it.useStartTime.toLong(), it.useEndTime.toLong
                    (), CalendarListener { transactFragment(R.id.fl_calender, ElectronicBookingFragment(mModel.detail
                    .value!!)) })
            )
        })


    }

    override fun initData() {
        mModel.getOrderDetail(id.toString(),"1")
    }

}

/**
 * @des 预约消费的页面--小电商ViewModel
 * @author PuHua
 * @date
 */
class ElectronicBookingActivityViewModel : BaseViewModel() {
    var detail = MediatorLiveData<ElectronicDetailBean>()
    /**
     * 获取详情
     */
    fun getOrderDetail(orderId: String,type: String) {
        mPresenter.value?.loading = true
        ElectronicRepository.electronicService.getElectronicTicketDetail(orderId, type)
            .excut(object : ElectronicObserver<ElectronicDetailBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ElectronicDetailBean>) {
                    val detailBean = response.data
                    detail.postValue(detailBean)

                }
            })
    }
}