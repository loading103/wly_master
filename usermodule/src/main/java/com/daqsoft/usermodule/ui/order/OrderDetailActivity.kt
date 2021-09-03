package com.daqsoft.usermodule.ui.order

import android.graphics.drawable.Drawable
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
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.HelathInfoBean
import com.daqsoft.provider.bean.HelathSetingBean
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityOrderDetailBinding
import com.daqsoft.provider.bean.OrderDetail
import com.daqsoft.provider.bean.OrderStatusConstant
import com.daqsoft.provider.businessview.event.UpdateOrderCanceStatus
import com.daqsoft.usermodule.repository.constant.IntentConstant
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.venues.VenuesRepository
import com.daqsoft.usermodule.ui.fragment.AttachePersonInfoFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * 订单详情页面
 *
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-12-4
 * @since JDK 1.8.0_191
 */
@Route(path = ARouterPath.UserModule.USER_ORDER_DETAIL)
class OrderDetailActivity :
    TitleBarActivity<ActivityOrderDetailBinding, OrderDetailActivityViewModel>() {
    @JvmField
    @Autowired
    var orderId: String? = null
    @JvmField
    @Autowired
    var type: String = ""

    private var healthSetingInfo: HelathSetingBean? = null
    private var healthInfo: HelathInfoBean? = null

    override fun getLayout(): Int = R.layout.activity_order_detail

    override fun setTitle(): String = getString(R.string.order_detail)

    override fun injectVm(): Class<OrderDetailActivityViewModel> =
        OrderDetailActivityViewModel::class.java

    override fun initPageParams() {
        isInitImmerBar = false
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        mModel.detail.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.vLineContainer.visibility = View.VISIBLE
            // 初始化信息fragment
            val detailFragment = OrderDetailFragment.newInstance(it, type)
            isHideAnother = false
            transactFragment(R.id.fragment_information, detailFragment)
//            // 预订时间/预订信息/活动信息页面
            val statusFragment = OrderStatusFragment.newInstance(it)
            transactFragment(R.id.status, statusFragment)
            // 订单信息
            val orderInformationFragment = OrderInformationFragment.newInstance(it)
            transactFragment(R.id.fragment_booking_information, orderInformationFragment)
            it?.let { it1 ->
                if (it1.resourceType == ResourceType.CONTENT_TYPE_VENUE || it1.resourceType == ResourceType.CONTENT_TYPE_SCENERY) {
                    if (it1.hasAttached == 1) {
                        val userInformationFragment = AttachePersonInfoFragment.newInstance(it1.id)
                        transactFragment(R.id.fragment_user_information, userInformationFragment)
                        mBinding.fragmentUserInformation.visibility = View.VISIBLE
                    } else {
                        if (healthInfo != null && it.cardType != null && it.cardType == "ID_CARD") {
                            val orderHealthInfomationFragment =
                                OrderHealthInfomationFragment.newInstance(
                                    healthInfo!!,
                                    healthSetingInfo!!
                                )
                            transactFragment(
                                R.id.fragment_health_information,
                                orderHealthInfomationFragment
                            )
                            mBinding.fragmentHealthInformation.visibility = View.VISIBLE
                        }
                    }
                }
            }
            // 订单状态（审核），消费码等
            when (it!!.orderStatus) {
                OrderStatusConstant.ORDER_STATUS_WAITE_COST -> {
                    // 待消费
                    val orderQrCodeFragment = OrderQrCodeFragment.newInstance(it)
                    transactFragment(R.id.fragment_valid_information, orderQrCodeFragment)
                }
                OrderStatusConstant.ORDER_STATUS_NO_PASS -> {
                    // 未通过
                    val valideFrament = OrderValidFragment.newInstance(it)
                    transactFragment(R.id.fragment_valid_information, valideFrament)
                }
                OrderStatusConstant.ORDER_STATUS_CANCELED -> {
                    // 已取消
                    val orderCancelFragment = OrderCancelInformationFragment.newInstance(it)
                    transactFragment(R.id.fragment_valid_information, orderCancelFragment)
                }
                OrderStatusConstant.ORDER_STATUS_FINISHED -> {
                    // 已完成
                    val orderQrCodeFragment = OrderQrCodeFragment.newInstance(it)
                    transactFragment(R.id.fragment_valid_information, orderQrCodeFragment)

                    val orderSalesFragment = OrderSaleListFragment.newInstance(it)
                    transactFragment(R.id.fragment_sale_information, orderSalesFragment)
                    mBinding.fragmentSaleInformation.visibility = View.VISIBLE
                }
                OrderStatusConstant.ORDER_STATUS_NO_EFFEFECT -> {
                    val orderQrCodeFragment = OrderQrCodeFragment.newInstance(it)
                    transactFragment(R.id.fragment_valid_information, orderQrCodeFragment)
                }
                else -> {
                    mBinding.fragmentValidInformation.visibility = View.GONE
                }

            }

        })
        mModel.helathInfo.observe(this, Observer {
            if (it != null) {
                healthInfo = it
            } else {
                mBinding.fragmentHealthInformation.visibility = View.GONE
            }
            mModel.getOrderDetail(orderId!!)
        })

        mModel.healthSetingLd.observe(this, Observer {
            if (it != null) {
                healthSetingInfo = it
                if (it.enableHealthyCode || it.enableTravelCode) {
                    orderId?.let { it1 -> mModel.getUserHealthInfo(it1) }
                } else {
                    mModel.getOrderDetail(orderId!!)
                }
            } else {
                mModel.getOrderDetail(orderId!!)
            }
        })

        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
    }

    fun getRootView(): View {
        return mBinding.root
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateCanceStatus(event: UpdateOrderCanceStatus) {
        showLoadingDialog()
        clearFragment()
        mBinding.vLineContainer.visibility = View.GONE
        mBinding.status.removeAllViews()
        mBinding.fragmentValidInformation.removeAllViews()
        mModel.getHealthSetingInfo()
    }

    override fun initData() {

        orderId?.let {
            showLoadingDialog()
            mModel.getHealthSetingInfo()
//            mModel.getOrderDetail(it)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()

    }
}

class OrderDetailActivityViewModel : BaseViewModel() {

    var detail = MutableLiveData<OrderDetail>()
    /**健康信息*/
    val helathInfo = MutableLiveData<HelathInfoBean>()
    /**
     * 健康码设置信息
     */
    val healthSetingLd: MutableLiveData<HelathSetingBean> = MutableLiveData()

    /**
     * 获取详情
     */
    fun getOrderDetail(orderId: String) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        val map = HashMap<String, Any>()
        UserRepository().userService.getOrderDetail(map, orderId)
            .excute(object : BaseObserver<OrderDetail>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderDetail>) {
                    detail.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<OrderDetail>) {
                    mError.postValue(null)
                }

            })
    }

    /**获取健康信息*/
    fun getUserHealthInfo(orderId: String) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        mPresenter.value?.isNeedToastMessage = false
        UserRepository().userService.getUserHealthInfo(orderId)
            .excute(object : BaseObserver<HelathInfoBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HelathInfoBean>) {
                    helathInfo.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<HelathInfoBean>) {
                    helathInfo.postValue(null)
                }
            })
    }

    /**
     * 获取健康码配置信息
     */
    fun getHealthSetingInfo() {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        var siteId = SPUtils.getInstance().getString(SPKey.SITE_ID)
        VenuesRepository.venuesService.getHealthSetingInfo(siteId)
            .excute(object : BaseObserver<HelathSetingBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HelathSetingBean>) {
                    healthSetingLd.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<HelathSetingBean>) {
                    healthSetingLd.postValue(null)
                }
            })
    }


}