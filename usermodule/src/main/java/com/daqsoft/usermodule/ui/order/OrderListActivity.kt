package com.daqsoft.usermodule.ui.order

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.JavaUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityOrderListBinding
import com.daqsoft.usermodule.databinding.ItemOrdersBinding
import com.daqsoft.usermodule.repository.constant.IntentConstant
import com.daqsoft.provider.bean.OrderRoom
import com.daqsoft.provider.databinding.LayoutDialogNoticeBinding
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_order_list.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

/**
 * 我的预订列表
 */
@Route(path = ARouterPath.UserModule.USER_ORDER_LIST)
class OrderListActivity : TitleBarActivity<ActivityOrderListBinding, OrderListViewModel>() {

    @Autowired
    @JvmField
    var type: String = ""


    override fun getLayout(): Int = R.layout.activity_order_list

    override fun setTitle(): String =
        if (type == ResourceType.CONTENT_TYPE_ACTIVITY) "我的活动" else getString(R.string.order_my_book)

    override fun injectVm(): Class<OrderListViewModel> = OrderListViewModel::class.java
    var orderStatusType = "0"
    override fun initView() {

        mModel.orders.observe(this, Observer {
            mBinding.mSwipeRefreshLayout.finishRefresh()
            adapter.emptyViewShow = true
            JavaUtils.pageDeal(page, it, adapter)
            if (it.datas != null) {
                mRecyclerView.visibility = View.VISIBLE
                adapter.add(it.datas!!)
            }
        })
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
            page = 1
            adapter.emptyViewShow = false
            adapter.clear()
            mModel.getOrders(orderStatusType, page, pageSize, type, false)
        }
        mRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?
        mRecyclerView.adapter = adapter
        adapter.setOnLoadMoreListener {
            page++
            reloadData()
        }
        // 数据及取值存放在本地array文件里面，用逗号隔开，前为值，后为value
        var orderStatus = resources.getStringArray(R.array.order_status)
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
                adapter.emptyViewShow = false
                page = 1
                adapter.clear()
                orderStatusType = tabs[position].statu
                mModel.getOrders(orderStatusType, page, pageSize, type)
            }

            override fun onTabReselect(position: Int) {

            }

        })
        mModel.cancelFinish.observe(this, Observer {
            mModel.getOrders(orderStatusType, page, pageSize, type)
        })
    }

    override fun initData() {
        mModel.getOrders(orderStatusType, page, pageSize, type)
    }

    /**
     * 数据集合
     */
    protected val data = arrayListOf<OrderRoom>()
    protected var page = 1
    protected var pageSize = 10

    protected val adapter = object :
        RecyclerViewAdapter<ItemOrdersBinding, OrderRoom>(
            R.layout.item_orders
        ) {
        @SuppressLint("CheckResult")
        override fun setVariable(
            mBinding: ItemOrdersBinding,
            position: Int,
            item: OrderRoom
        ) {
            mBinding.item = item
            mBinding.status = when (item.status) {
                // 判断当前订单的状态
                4 -> {
                    // 待审核
                    mBinding.statusColor = resources.getColor(R.color.colorPrimary)
                    mBinding.tvCancel.visibility = View.GONE
                    resources.getString(R.string.order_waite_valid)
                }
                79 -> {
                    // 未通过
                    mBinding.statusColor = resources.getColor(R.color.red)
                    mBinding.tvCancel.visibility = View.GONE
                    resources.getString(R.string.order_no_pass)
                }
                11 -> {
                    // 待消费
                    mBinding.statusColor = resources.getColor(R.color.colorPrimary)
                    mBinding.tvCancel.visibility = View.GONE
                    resources.getString(R.string.order_waite_cost)
                }
                12 -> {
                    // 已完成
                    mBinding.statusColor = resources.getColor(R.color.colorPrimary)
                    mBinding.tvCancel.visibility = View.GONE
                    mBinding.mReviewsTv.visibility = View.GONE
                    //评价晒单 暂时未做
                    RxView.clicks(mBinding.mReviewsTv)
                        .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                            ARouter.getInstance()
                                .build(ARouterPath.UserModule.USER_ELECTRONIC_COMMENT_ACTIVITY)
                                .navigation()
                        }
                    resources.getString(R.string.order_finish)
                }
                13 -> {
                    // 已失效
                    mBinding.statusColor = resources.getColor(R.color.red)
                    mBinding.tvCancel.visibility = View.GONE
                    resources.getString(R.string.order_no_effect)
                }
                14 -> {
                    // 已取消
                    mBinding.statusColor = resources.getColor(R.color.txt_gray)
                    mBinding.tvCancel.visibility = View.GONE
                    resources.getString(R.string.order_canceled)
                }

                else -> resources.getString(R.string.order_waite_valid)
            }

            var image = ""
            var address = ""
            var name = ""
            var money = 0.0
            var integral = 0
            val timeBegin = DateUtil.formatDateByString(
                "yyyy.MM.dd HH:mm",
                "yyyy-MM-dd HH:mm:ss",
                item.startTime
            )
            var timeEnd = DateUtil.formatDateByString("HH:mm", "yyyy-MM-dd HH:mm:ss", item.endTime)


            if (type == ResourceType.CONTENT_TYPE_ACTIVITY) {
                item.activity?.let {
                    if (!item.activity.image.isNullOrEmpty()) {
                        image = item.activity.image
                    }
                    address = "地点：${item.activity.address}"
                    name = item.activity.name
                    money = item.activity.money.toDouble()
                    integral = item.activity.integral.toInt()
                    timeEnd = DateUtil.formatDateByString(
                        "yyyy.MM.dd HH:mm",
                        "yyyy-MM-dd HH:mm:ss",
                        item.endTime
                    )
                    if (item.activity.faithUseStatus == "1") {
                        mBinding.ivCxyx.visibility = View.VISIBLE
                    }
                    var cance = getCanceStatus(item.status, it.cancelStatus, it.cancelTime)
                    if (cance) {
                        mBinding.tvCancel.visibility = View.VISIBLE
                    } else {
                        mBinding.tvCancel.visibility = View.GONE
                    }
                }
            } else if (type == ResourceType.CONTENT_TYPE_VENUE) {
                item.activityRoom?.let {
                    if (!item.activityRoom.image.isNullOrEmpty()) {
                        image = item.activityRoom.image
                    }
                    address = "所属场馆：${item.activityRoom.venueName}"
                    name = item.activityRoom.name
                    money = item.activityRoom.money.toDouble()
                    integral = item.activityRoom.integral.toInt()
                    // 待审核或者待消费 判断是否可以取消
                    var cance = getCanceStatus(item.status, it.cancelStatus, it.cancelTime)
                    if (cance) {
                        mBinding.tvCancel.visibility = View.VISIBLE
                    } else {
                        mBinding.tvCancel.visibility = View.GONE
                    }
                }

            }

            var timeStart = "$timeBegin-$timeEnd"
            mBinding.time = timeStart!!

            if (!image.isNullOrEmpty()) {
                Glide.with(this@OrderListActivity).load(image.split(",")[0])
                    .placeholder(R.mipmap.placeholder_img_fail_h158)
                    .into(mBinding.image)
            }
            if (!address.isNullOrEmpty()) {
                mBinding.address.text = address
            }
            if (!name.isNullOrEmpty()) {
                mBinding.name.text = name
            }
            if (!item.orderNum.isNullOrEmpty()) {
                mBinding.tvPeople.content = "${item.orderNum}人"
            }

            // 判断当前订单是已积分还是人民币计价的，和各种展示方式
            if (money == 0.0 && integral == 0) {
                mBinding.price = getString(R.string.order_free)
            } else if (money != 0.0 && integral == 0) {
                mBinding.price = getString(R.string.order_yuan) + money.toString()
            } else if (money == 0.0 && integral != 0) {
                mBinding.price = integral.toString() + getString(R.string.order_integral)
            } else {
                mBinding.price =
                    getString(R.string.order_yuan) + money.toString() + integral.toString() + getString(
                        R.string.order_integral
                    )
            }
            // 判断当前待支付的总价展示方式
            if (item.payMoney == 0.0 && item.payIntegral == "0") {
                mBinding.total = getString(R.string.order_free)
            } else if (item.payMoney != 0.0 && item.payIntegral == "0") {
                mBinding.total = getString(R.string.order_yuan) + item.payMoney.toString()
            } else if (item.payMoney == 0.0 && item.payIntegral != "0") {
                mBinding.total = item.payIntegral.toString() + getString(R.string.order_integral)
            } else {
                mBinding.total =
                    getString(R.string.order_yuan) + item.payMoney.toString() + item.payIntegral.toString() + getString(
                        R.string.order_integral
                    )
            }


            RxView.clicks(mBinding.root)
                // 1秒内不可重复点击或仅响应一次事件
                .throttleFirst(1, TimeUnit.SECONDS).subscribe { o ->
                    run {
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.USER_ORDER_DETAIL)
                            .withString("orderId", item.id)
                            .withString("type", type)
                            .navigation()
                    }
                }
            mBinding.tvCancel.onNoDoubleClick {
                noticeConfirm(this@OrderListActivity, item.orderCode)
            }
        }

        /**
         * 是否可以取消预约
         * @param status 订单状态
         * @param canceStatus 取消订单状态
         * @param startTime 订单生效时间 用于判断是否再取消订单有效期内
         */
        private fun getCanceStatus(status: Int, canceStatus: Int, startTime: String?): Boolean {
            if (status == 11 || status == 4) {
                when (canceStatus) {
                    0 -> {
                        // 不可取消
                        return false
                    }
                    1 -> {
                        // 随时取消
                        return true
                    }
                    2 -> {
                        // 24小时内
                        return true
//                    DateUtil.
                    }
                    3 -> {
                        // 自定义取消时间
                        return true
                    }
                }
            }
            return false

        }
    }

    /**
     * 弹出确认框
     * @param sureCommand 动作
     */
    fun noticeConfirm(context: Activity, orderCode: String) {
        val dialog = AlertDialog.Builder(AppManager.instance.currentActivity()).create()
        dialog.show()
        val window = dialog.window
        val binding = DataBindingUtil.inflate<LayoutDialogNoticeBinding>(
            context.layoutInflater,
            R.layout.layout_dialog_notice, null, false
        )

        window!!.setContentView(binding.root)
        binding.label = context.getString(R.string.order_cancel_notice_label)
        binding.notice = context.getString(R.string.order_cancel_notice)
        binding.cancel = context.getString(R.string.order_cancel)
        binding.sure = context.getString(R.string.user_str_conform)

        binding.cancelSubmit = object : ReplyCommand {
            override fun run() {
                dialog.dismiss()
            }
        }
        binding.sureSubmit = object : ReplyCommand {
            override fun run() {
                dialog.dismiss()
                mModel.cancelOrder(orderCode)
            }
        }
    }

}


class OrderListViewModel : BaseViewModel() {
    val orders = MediatorLiveData<BaseResponse<OrderRoom>>()
    var cancelFinish = MutableLiveData<Boolean>()
    /**
     * 获取列表
     */
    fun getOrders(
        status: String,
        page: Int,
        pageSize: Int,
        type: String = ResourceType.CONTENT_TYPE_VENUE,
        isLoading: Boolean = true
    ) {
        mPresenter.value?.loading = isLoading
        val map = HashMap<String, Any>()
        if (status != "0")
            map["status"] = status
        map["currPage"] = page
        map["pageSize"] = pageSize
        UserRepository().userService.getOrders(map, type)
            .excute(object : BaseObserver<OrderRoom>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderRoom>) {
                    orders.postValue(response)
                }
            })
    }

    /**
     * 取消预订
     */
    fun cancelOrder(orderCode: String) {
        mPresenter.value?.loading = true
        UserRepository().userService.postCancelOrder(orderCode)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    toast.postValue("取消成功!")
                    cancelFinish.postValue(true)
                }
            })
    }
}


class TabEntity(var title: String, var statu: String) :
    CustomTabEntity {

    override fun getTabTitle(): String {

        return title
    }

    override fun getTabSelectedIcon(): Int {
        return R.mipmap.arrow_back
    }

    override fun getTabUnselectedIcon(): Int {
        return R.mipmap.arrow_back
    }
}