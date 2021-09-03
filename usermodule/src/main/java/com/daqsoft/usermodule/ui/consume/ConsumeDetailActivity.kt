package com.daqsoft.usermodule.ui.consume

import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ConsumeDetail
import com.daqsoft.provider.bean.HelathInfoBean
import com.daqsoft.provider.bean.OrderStatusConstant
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityConsumeDetailBinding
import org.jetbrains.anko.image

/**
 * 消费码详情页面
 */
@Route(path = ARouterPath.UserModule.USER_CONSUME_DETAIL)
class ConsumeDetailActivity :
    TitleBarActivity<ActivityConsumeDetailBinding, ConsumeDetailActivityViewModel>() {

    @JvmField
    @Autowired
    var orderCode: String = ""

    override fun getLayout(): Int = R.layout.activity_consume_detail

    override fun setTitle(): String = getString(R.string.order_consume_detail)

    override fun injectVm(): Class<ConsumeDetailActivityViewModel> =
        ConsumeDetailActivityViewModel::class.java

    /**
     * 下部的fragment
     */
    var consumeActivityRoomFragment: ConsumeActivityRoomFragment? = null

    override fun initView() {
        mModel.detail.observe(this, Observer {
            mBinding.qrCode = it.qrCode
            mBinding.code = it.code
            mBinding.tvGuideTip.visibility = View.GONE
            mBinding.tvView.visibility = View.GONE
            var timeContent = ""
            // 根据订单状态选择是活动室还是活动还是其他展示有效期，预约时间，使用时间等
            when (it.orderType) {
                ResourceType.CONTENT_TYPE_ACTIVITY_SHIU -> {
                    timeContent = getString(
                        R.string.order_activity_room_time_stamp_, it.activityRoom.useStartTime, it
                            .activityRoom.useEndTime
                    )
                }
                ResourceType.CONTENT_TYPE_ACTIVITY -> {
                    timeContent = getString(
                        R.string.order_activity_room_time_stamp_, it.activity.useStartTime, it
                            .activity.useEndTime
                    )
                }
                ResourceType.CONTENT_TYPE_SCENERY, ResourceType.CONTENT_TYPE_VENUE -> {
                    if (it?.venueInfo != null) {
                        timeContent = DateUtil.getTwoDateStrs(
                            it.orderStartTime,
                            it.orderEndTime
                        )
                    }
                }
            }
            mBinding.content = timeContent

            when (it.status) {
                // 已消费
                OrderStatusConstant.ORDER_STATUS_FINISHED -> {
                    mBinding.status.visibility = View.VISIBLE
                    mBinding.bookingTime.setLabel(getString(R.string.order_consume_use_time))
                    mBinding.status.image = getDrawable(R.mipmap.mine_code_detail_tag_big_yihexiao)
                }
                // 已失效
                OrderStatusConstant.ORDER_STATUS_NO_EFFEFECT -> {
                    mBinding.status.visibility = View.VISIBLE
                    mBinding.bookingTime.setLabel(getString(R.string.order_consume_effect_time))
                    mBinding.status.image = getDrawable(R.mipmap.mine_code_detail_tag_big_yishixiao)

                }
                else -> {
                    mBinding.bookingTime.setLabel(getString(R.string.order_consume_effect_time))
                    mBinding.status.image = getDrawable(R.mipmap.mine_code_detail_tag_big_yihexiao)
                }
            }
            consumeActivityRoomFragment = ConsumeActivityRoomFragment(it)
            transactFragment(R.id.fragment_information, consumeActivityRoomFragment!!)
            if (!it.orderId.isNullOrEmpty() && it.hasAttached != 1) {
                if (!it.cardType.isNullOrEmpty() && it.cardType!!.toLowerCase() == "id_card") {
                    mModel.getUserHealthInfo(it.orderId)
                }

            }
        })

        mModel.healthInfoLd.observe(this, Observer {
            consumeActivityRoomFragment?.updateHealth(it)
        })
    }

    override fun initData() {
        mModel.getOrderDetail(orderCode)
    }

}

/**
 * 消费码详情页面的viewModel
 */
class ConsumeDetailActivityViewModel : BaseViewModel() {

    /**健康信息*/
    val healthInfoLd: MutableLiveData<HelathInfoBean> = MutableLiveData()

    var detail = MediatorLiveData<ConsumeDetail>()

    /**
     * 获取详情
     */
    fun getOrderDetail(orderId: String) {
        mPresenter.value?.loading = true

        UserRepository().userService.getConsumeDetial(orderId)
            .excute(object : BaseObserver<ConsumeDetail>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ConsumeDetail>) {
                    detail.postValue(response.data)
                }
            })
    }

    /**通过订单获取健康信息*/
    fun getUserHealthInfo(orderId: String) {
        UserRepository().userService.getUserHealthInfo(orderId)
            .excute(object : BaseObserver<HelathInfoBean>() {
                override fun onSuccess(response: BaseResponse<HelathInfoBean>) {
                    healthInfoLd.postValue(response.data)
                }
            })
    }
}