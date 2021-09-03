package com.daqsoft.usermodule.ui.order

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.OrderListBean
import com.daqsoft.provider.bean.SiteInfo
import com.daqsoft.provider.net.ShopObserver
import com.daqsoft.provider.net.ShopResponse
import com.daqsoft.provider.net.excute
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.utils.TimeUtils
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityOrderListBinding
import com.daqsoft.usermodule.databinding.ItemOrdersShopBinding
import com.daqsoft.usermodule.interfaces.OrderListItemClickListener
import com.daqsoft.usermodule.repository.constant.IntentConstant
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_recevie_address_list.*
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 我的预订列表
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-12-4
 * @since JDK 1.8.0_191
 */
@Route(path = ARouterPath.UserModule.USER_ELECTRONIC_ORDERS)
class ElectronicOrderListActivity :
    TitleBarActivity<ActivityOrderListBinding, ElectronicOrderListActivityViewModel>() {
    /**
     * 当前状态
     */
    var currentStatus: String = "all"

    /**
     * 当前页码
     */
    private var page = 1

    /**
     * 页码大小
     */
    private var pageSize = 10

    private val data = mutableListOf<OrderListBean.X>()

    override fun getLayout(): Int = R.layout.activity_order_list

    override fun setTitle(): String = getString(R.string.order_my_order)

    override fun injectVm(): Class<ElectronicOrderListActivityViewModel> =
        ElectronicOrderListActivityViewModel::class.java

    override fun initView() {
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
//            mBinding.mSwipeRefreshLayout.isRefreshing = true
            page = 1
            adapter.clear()
            mModel.getOrders(currentStatus, page, pageSize)
        }
        mRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mRecyclerView.adapter = adapter
        adapter.setOnLoadMoreListener {
            page++
            mModel.getOrders(currentStatus, page, pageSize)
        }


        // 数据及取值存放在本地array文件里面，用逗号隔开，前为值，后为value
        var orderStatus = resources.getStringArray(R.array.electronic_status)
        val status = mutableListOf<CustomTabEntity>()
        val tabs = mutableListOf<TabEntity>()
        for (i in orderStatus.indices) {
            val ss = orderStatus[i].split(",")
            status.add(TabEntity(ss[0], ss[1]))
            tabs.add(TabEntity(ss[0], ss[1]))
        }
        mBinding.commonTab.setTabData(status as ArrayList<CustomTabEntity>?)
        mBinding.commonTab.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                page = 1
                currentStatus = tabs[position].statu
                showLoadingDialog()
                mModel.getOrders(currentStatus, page, pageSize)
            }

            override fun onTabReselect(position: Int) {

            }
        })

        initViewModel()
    }

    private fun initViewModel() {
        mModel.orders.observe(this, Observer {
            dissMissLoadingDialog()
//            mBinding.mSwipeRefreshLayout.isRefreshing = false
            if (mBinding.mRecyclerView.visibility != View.VISIBLE) {
                mBinding.mRecyclerView.visibility = View.VISIBLE
            }
            mBinding.mSwipeRefreshLayout.finishRefresh()

            if (page == 1) {
                mBinding.mRecyclerView.smoothScrollToPosition(0)
                adapter.clear()
            }
            if (it.data != null) {
                adapter.add(it.data!!.list!!.toMutableList())
            }

            if (it.data?.list.isNullOrEmpty() || it.data?.list?.size!! < pageSize) {
                adapter.loadEnd()
            } else {
                adapter.loadComplete()
            }

        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            if (mBinding.mRecyclerView.visibility != View.VISIBLE) {
                mBinding.mRecyclerView.visibility = View.VISIBLE
            }
        })
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        // 为了刷新数据状态，暂时写在了Onresume里面，建议后期优化为异步刷新或者局部刷新
        showLoadingDialog()
        page = 1
        mModel.getSiteInfo(currentStatus, page, pageSize)
        try {
            mBinding.mRecyclerView.smoothScrollToPosition(0)
        } catch (e: Exception) {
        }
    }

    private val adapter by lazy {
        object : RecyclerViewAdapter<ItemOrdersShopBinding, OrderListBean.X>(
            R.layout.item_orders_shop,
            data
        ),
            OrderListItemClickListener {
            override fun click(item: OrderListBean.X?, view: View?) {
                when (view?.id) {
                    R.id.mDetailTv -> {

                        var path = ""
                        // 详情
                        when (item?.orderType) {
                            1 -> {
                                // 实物
                                path =
                                    ARouterPath.UserModule.USER_ELECTRONIC_IN_KIND_DETAIL_ACTIVITY
                            }
                            2 -> {
                                // 虚拟
                                path =
                                    ARouterPath.UserModule.USER_ELECTRONIC_VULTURE_DETAIL_ACTIVITY
                            }
                            3 -> {
                                // 门票
                                path = ARouterPath.UserModule.USER_ELECTRONIC_TICKET_DETAIL_ACTIVITY
                            }
                            else -> {
                                // 点击查看详情
                                path =
                                    OrderDetailUIFactory.Builder(item?.orderType.toString() ?: "")
                                        .build()
                            }
                        }
                        if (item != null) {
                            ARouter.getInstance()
                                .build(path)
                                .withInt("id", item?.id ?: -1)
                                .withInt("orderType", item.orderType ?: -1)
                                .navigation()
                        } else {
                            toast("数据请求失败!")
                        }
                    }
                    R.id.mLogisticsTv -> {
                        // 查看物流
                    }
                    R.id.mAppointmentCostTv -> {
                        // 预约消费
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.USER_CONSUME_ELECTRONIC_BOOKING_WEB)
                            .withString(IntentConstant.OBJECT, item?.id.toString())
                            .navigation()
                    }
                    R.id.mReviewsTv -> {
                        // 点评晒单
                    }
                    R.id.mPayTv -> {
                        var uuid = SPUtils.getInstance().getString(SPKey.UUID)
                        var token = SPUtils.getInstance().getString(SPKey.USER_CENTER_TOKEN)
                        var encry = SPUtils.getInstance().getString(SPKey.ENCRYPTION)
                        // 支付 调用网页得支付
//                        var payUrl = BaseApplication.electronicPayUrl + "/html/paymentIn.html?orderId=${item!!.id}&payOperate=ORDER_PAY&" +
//                                "unid=${uuid}&token=${token}&url=${BaseApplication.electronicPayBackUrl}"
//                        var payUrl="https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?prepay_id" +
//                                "=wx281943116619563d839b892e1833750300&" +
//                                "package=1864163776&redirect_url=http%3A%2F%2Fsub6966984.c.xds.daqsoft.com" +
//                                "%2Forder%2Fresult%3FpayOperate%3DORDER_PAY%26id%3D592&unid=${uuid}&token=${token}"
                        // TODO 暂时跳转 小电商订单详情
                        var shopUrl = SPUtils.getInstance().getString(SPKey.SHOP_URL)
                        var payUrl =
                            "${shopUrl}/order/detail?id=${item?.id}&unid=${uuid}&token=${token}&encryption=${encry}"
                        ARouter.getInstance()
                            .build(ARouterPath.Provider.WEB_ACTIVITY)
                            .withString("mTitle", "支付")
                            .withString("html", payUrl)
                            .navigation()
                    }
                    R.id.mOrderTv -> {
                        // 预订
                    }

                }
            }


            @SuppressLint("CheckResult")
            override fun setVariable(
                mBinding: ItemOrdersShopBinding,
                position: Int,
                item: OrderListBean.X
            ) {
                mBinding.item = item
                mBinding.view = this
                mBinding.txtOrdersTotalTip.text =
                    getString(R.string.order_total_str, item.productNum.toString())

                if (item.routeOrderNum?.size ?: 0 > 0) {
                    var strBuilder = StringBuilder()
                    for (i in item.routeOrderNum!!.indices) {
                        if (i == item.routeOrderNum!!.size - 1) {
                            strBuilder.append(item.routeOrderNum!![i].name).append("x")
                                .append(item.routeOrderNum!!.get(i).quantity)
                        } else {
                            strBuilder.append(item.routeOrderNum!![i].name).append("x")
                                .append(item.routeOrderNum!!.get(i).quantity).append(",")
                        }
                    }
                    mBinding.txtOrdersTotalTip.text = strBuilder.append(" 合计：")
                } else {
                    mBinding.txtOrdersTotalTip.text =
                        getString(R.string.order_total_str, item.productNum.toString())
                }

                mBinding.txtOrdersTotalPrice.text = "￥" + item.orderPayAmount
                if (item.orderType == 3) {
                    mBinding.label.text = item.scenicName
                } else {
                    mBinding.label.text = item.businessName
                }
                // 处理订单状态
                when (item.orderStatus) {
                    // 取消状态
                    2, 9, 13, 31, 35, 36 -> {
                        if (item.orderType == 5) {
                            mBinding.tvStatus.textColor = resources.getColor(R.color.colorPrimary)
                        } else {
                            mBinding.tvStatus.textColor = resources.getColor(R.color.color_999)
                        }
                        mBinding.tvStatus?.text = item.orderStatusName
                    }
                    else -> {
                        mBinding.tvStatus.textColor = resources.getColor(R.color.colorPrimary)
                        if (item.orderStatusName.equals("待发货")) {
                            // 待发货
                            if (item.needDeliveryProductNum != null) {
                                if (item.needDeliveryProductNum!! > 0) {
                                    mBinding.tvStatus?.text =
                                        item.orderStatusName + "(${item.needDeliveryProductNum})"
                                } else {
                                    mBinding.tvStatus?.text = item.orderStatusName
                                }
                            }
                        } else {
                            mBinding.tvStatus?.text = item.orderStatusName
                        }
                    }

                }


                if (mBinding.label.text.isNullOrEmpty()) {
                    mBinding.label.visibility = View.INVISIBLE
                }


                showContent(mBinding, item)

                // 支付按钮的显隐
                if (item.orderStatus == 0) {
                    mBinding.mPayTv.visibility = View.VISIBLE
                } else {
                    mBinding.mPayTv.visibility = View.GONE
                }

                // 点评晒单按钮的显隐
                if (item.orderStatus == 2 && item.openEvaluate == true && item.isEvaluate == false) {
                    mBinding.mReviewsTv.visibility = View.VISIBLE
                } else {
                    mBinding.mReviewsTv.visibility = View.GONE
                }

                // 查看物流按钮的显隐
                if (item.orderType == 1 && item.orderStatus == 21) {
                    mBinding.mLogisticsTv.visibility = View.VISIBLE
                } else {
                    mBinding.mLogisticsTv.visibility = View.GONE
                }

                // 预约消费按钮显隐
                if (item.orderType == 2 && item.orderStatus == 10) {
                    mBinding.mAppointmentCostTv.visibility = View.VISIBLE
                } else {
                    mBinding.mAppointmentCostTv.visibility = View.GONE
                }

                // 预订按钮显隐
                if (item.orderType == 2 && item.orderStatus == 40) {
                    mBinding.mOrderTv.visibility = View.VISIBLE
                } else {
                    mBinding.mOrderTv.visibility = View.GONE
                }

                // 详情按钮显隐 除了待支付以外显示 (2020/5/8 改为都显示)
                mBinding.mDetailTv.visibility = View.VISIBLE


                RxView.clicks(mBinding.root)
                    // 1秒内不可重复点击或仅响应一次事件
                    .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                        run {
                            ARouter.getInstance()
                                .build(
                                    OrderDetailUIFactory.Builder(item.orderType.toString()).build()
                                )
                                .withInt("id", item.id ?: -1)
                                .withInt("orderType", item.orderType ?: -1)
                                .navigation()
                        }
                    }
            }
        }
    }

    /**
     * 显示名称下方的内容
     */
    @SuppressLint("StringFormatMatches")
    fun showContent(mBinding: ItemOrdersShopBinding, item: OrderListBean.X) {
        when (item.orderType) {
            3 -> {
                mBinding.mSpecification.setLabel(item.scenicSightTypeTitle)
            }
            // 酒店订单
            5 -> {
//                mBinding.mSpecification.setLabel(item.standardName)
                mBinding.mSpecification.setLabel(
                    getString(
                        R.string.order_hotel_time_number,
                        TimeUtils.timeStamp2Date(item!!.useStartTime.toString(), "yyyy.MM.dd"),
                        TimeUtils.timeStamp2Date(
                            item!!
                                .useEndTime.toString(),
                            "yyyy.MM.dd"
                        ),
                        item!!.days.toString()
                    )
                )
            }
            else -> {
                if (item.standardName?.let { Utils.isValidDate(it, Utils.dateYMD) }!!) {
                    mBinding.mSpecification.setLabel("出行日期：" + item.standardName)
                } else {
                    mBinding.mSpecification.setLabel(item.standardName)
                }

            }
        }
    }

}


/**
 * @des 小电扇我的订单列表viewmodel
 * @author PuHua
 * @Date 2019/12/12 11:36
 */
class ElectronicOrderListActivityViewModel : BaseViewModel() {
    val orders = MediatorLiveData<ShopResponse<OrderListBean>>()

    /**
     * 获取列表
     */
    fun getOrders(status: String, page: Int, pageSize: Int) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        val token = SPUtils.getInstance().getString(SPUtils.Config.TOKEN)
        ElectronicRepository.electronicService
            .getElectronicOrders(status, pageSize.toString(), page.toString(), token)
            .excute(object : ShopObserver<OrderListBean>(mPresenter) {
                override fun onSuccess(response: ShopResponse<OrderListBean>) {
                    orders.postValue(response)
                }

                override fun onFailed(response: ShopResponse<OrderListBean>) {
                    orders.postValue(response)
                }
            })
    }

    /**
     * 获取站点信息
     */
    fun getSiteInfo(status: String, page: Int, pageSize: Int) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        UserRepository().userService.getSiteInfo()
            .excute(object : BaseObserver<SiteInfo>(mPresenter) {
                override fun onSuccess(response: BaseResponse<SiteInfo>) {
                    SPUtils.getInstance().put(SPKey.DOMAIN, response.data?.shopUrl)
                    SPUtils.getInstance().put(SPKey.SITE_CODE, response.data?.siteCode)
                    SPUtils.getInstance().put(SPKey.SHOP_URL, response.data?.shopUrl)
                    getOrders(status, page, pageSize)
                }

                override fun onFailed(response: BaseResponse<SiteInfo>) {
                    getOrders(status, page, pageSize)
                }
            })
    }
}
