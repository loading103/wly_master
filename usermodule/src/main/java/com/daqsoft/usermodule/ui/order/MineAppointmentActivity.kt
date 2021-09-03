package com.daqsoft.usermodule.ui.order

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.OrderListDataBean
import com.daqsoft.provider.businessview.event.UpdateOrderCommentStatus
import com.daqsoft.provider.databinding.LayoutDialogNoticeBinding
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.provider.ProviderRepository
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityOrderListBinding
import com.daqsoft.usermodule.ui.order.adapter.MineAppointmentAdapter
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import kotlinx.android.synthetic.main.activity_recevie_address_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

/**
 * @Description 我的预约列表
 * @ClassName   MineAppointmentActivity
 * @Author      luoyi
 * @Time        2020/5/15 10:44
 */
@Route(path = ARouterPath.UserModule.USER_APPOINTMENT_LIST)
class MineAppointmentActivity :
    TitleBarActivity<ActivityOrderListBinding, AppointmentListViewModel>() {

    //    @Autowired
    //    @JvmField
    //    var type: String = ""
    var orderStatusType = "0"

    override fun getLayout(): Int = R.layout.activity_order_list

    override fun setTitle(): String = getString(R.string.order_my_book)

    override fun injectVm(): Class<AppointmentListViewModel> = AppointmentListViewModel::class.java
    /**
     * 订单详情链接
     */
    protected var orderInfoLink: String? = ""

    override fun initView() {
        EventBus.getDefault().register(this)


        mModel.resourceIdLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            if (!it.isNullOrEmpty()) {
                var siteId = SPUtils.getInstance().getString(SPKey.SITE_ID)
                ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", "预约详情")
                    .withString("html", StringUtil.getJingxinUrl(orderInfoLink!!, it, siteId))
                    .navigation()
            } else {
                ToastUtils.showMessage("获取用户验证信息失败，请稍后再试~")
            }
        })
        mModel.orders.observe(this, Observer {
            adapter?.emptyViewShow = true
            mBinding.mSwipeRefreshLayout.finishRefresh()
            dissMissLoadingDialog()
            if (it != null) {
                if (page == 1) {
                    adapter?.clear()
                }
                if (it.datas != null) {
                    mRecyclerView.visibility = View.VISIBLE
                    adapter?.add(it.datas!!)
                }
                if (it.page != null) {
                    if (it.page!!.currPage < it.page!!.totalPage) {
                        adapter!!.loadComplete()
                    } else {
                        adapter!!.loadEnd()
                    }
                }
            }
        })
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
            //            showLoadingDialog()
            page = 1
            adapter?.emptyViewShow = false
            adapter?.clear()
            mModel.getOrders(
                orderStatusType,
                page,
                pageSize,
                ResourceType.CONTENT_TYPE_VENUE,
                false
            )
//            mSwipeRefreshLayout.isRefreshing = false
        }
        adapter = MineAppointmentAdapter(this@MineAppointmentActivity)
        mRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?
        mRecyclerView.adapter = adapter
        adapter?.setOnLoadMoreListener {
            page++
            mModel.getOrders(orderStatusType, page, pageSize)
        }
        adapter?.onAppointmentItemListener =
            object : MineAppointmentAdapter.OnAppointmentItemListener {
                override fun onCancelItem(orderCode: String) {
                    noticeConfirm(this@MineAppointmentActivity, orderCode)
                }

                override fun toOrderInfo(linkUrl: String?) {
                    orderInfoLink = linkUrl
                    showLoadingDialog()
                    mModel.getResourceIdentity()
                }


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
//                showLoadingDialog()
                page = 1
                adapter!!.emptyViewShow = false
                adapter!!.clear()
                orderStatusType = tabs[position].statu
                mModel.getOrders(orderStatusType, page, pageSize)
            }

            override fun onTabReselect(position: Int) {

            }

        })
        mModel.cancelFinish.observe(this, Observer {
            mModel.getOrders(orderStatusType, page, pageSize)
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.mSwipeRefreshLayout.finishRefresh()
        })
    }

    override fun initData() {
//        showLoadingDialog()
        mModel.getOrders(orderStatusType, page, pageSize)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateCommentStatus(event: UpdateOrderCommentStatus) {
        if (mModel != null) {
//            showLoadingDialog()
            page = 1
            adapter!!.emptyViewShow = false
            adapter!!.clear()
            mModel.getOrders(orderStatusType, page, pageSize)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    /**
     * 数据集合
     */
    protected val data = arrayListOf<OrderListDataBean>()
    protected var page = 1
    protected var pageSize = 10

    protected var adapter: MineAppointmentAdapter? = null
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


class AppointmentListViewModel : BaseViewModel() {
    val orders = MediatorLiveData<BaseResponse<OrderListDataBean>>()
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
        UserRepository().userService.getOrdersV2(map, type)
            .excute(object : BaseObserver<OrderListDataBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderListDataBean>) {
                    orders.postValue(response)
                }

                override fun onFailed(response: BaseResponse<OrderListDataBean>) {
                    mError.postValue(response)
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


    /**
     * 获取资源加密信息
     */
    val resourceIdLiveData = MutableLiveData<String>()
    fun getResourceIdentity() {
        ProviderRepository.service.getResourceIdentity()
            .excute(object : BaseObserver<String>(mPresenter) {
                override fun onSuccess(response: BaseResponse<String>) {

                    resourceIdLiveData.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<String>) {
                    resourceIdLiveData.postValue(null)
                }

            })
    }
}
