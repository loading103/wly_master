package com.daqsoft.usermodule.ui.order

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.view.View
import androidx.core.view.isVisible
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
import com.daqsoft.provider.bean.AppointMentBean
import com.daqsoft.provider.bean.OrderListDataBean
import com.daqsoft.provider.databinding.LayoutDialogNoticeBinding
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.provider.ProviderRepository
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityOrderListBinding
import com.daqsoft.usermodule.ui.order.adapter.MineAppointmentScAdapter
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener

/**
 * @Description 我的预约列表
 * @ClassName    MineAppointmentScActivity
 * @Author      luoyi
 * @Time        2020/5/15 10:44
 */
@Route(path = ARouterPath.UserModule.USER_APPOINTMENT_SC_LIST)
class MineAppointmentScActivity :
    TitleBarActivity<ActivityOrderListBinding, AppointmentScListViewModel>() {

    var orderStatusType = "-1"
    /**
     * 数据集合
     */
    protected var page = 1
    protected var pageSize = 10

    protected var adapter: MineAppointmentScAdapter? = null
    /**
     * 订单详情链接
     */
    protected var orderInfoLink: String? = ""

    override fun getLayout(): Int = R.layout.activity_order_list

    override fun setTitle(): String = getString(R.string.order_my_book)

    override fun injectVm(): Class<AppointmentScListViewModel> =
        AppointmentScListViewModel::class.java

    override fun initView() {

        adapter = MineAppointmentScAdapter(this@MineAppointmentScActivity)
        mBinding.mRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?
        mBinding.mRecyclerView.adapter = adapter
        initViewEvent()
        initViewTab()
        initViewModel()
    }

    private fun initViewModel() {
        mModel.appointOrders.observe(this, Observer {
            dissMissLoadingDialog()
            pageDealed(it)
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
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
    }

    private fun initViewTab() {
        // 数据及取值存放在本地array文件里面，用逗号隔开，前为值，后为value
        var orderStatus = resources.getStringArray(R.array.mine_appointment_status)

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
                adapter!!.emptyViewShow = false
                adapter!!.clear()
                orderStatusType = tabs[position].statu
                showLoadingDialog()
                mModel.getAppointMentList(orderStatusType, pageSize, page)
            }

            override fun onTabReselect(position: Int) {

            }

        })
        mModel.cancelFinish.observe(this, Observer {
            mModel.getAppointMentList(orderStatusType, pageSize, page)
        })
    }

    private fun initViewEvent() {
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
            page = 1
//            mBinding.mSwipeRefreshLayout.isRefreshing = true
            mModel.getAppointMentList(orderStatusType, pageSize, page)
        }

        adapter?.setOnLoadMoreListener {
            page++
            mModel.getAppointMentList(orderStatusType, pageSize, page)
        }
        adapter?.onAppointmentItemListener =
            object : MineAppointmentScAdapter.OnAppointmentItemListener {
                override fun onCancelItem(orderCode: String) {
                    noticeConfirm(this@MineAppointmentScActivity, orderCode)
                }

                override fun toOrderInfo(linkUrl: String?) {
                    orderInfoLink = linkUrl
                    showLoadingDialog()
                    mModel.getResourceIdentity()
                }

            }
    }

    private fun pageDealed(datas: MutableList<AppointMentBean>) {
//        mBinding.mSwipeRefreshLayout.isRefreshing = false
        adapter!!.emptyViewShow = true
        if (!mBinding.mRecyclerView.isVisible) {
            mBinding.mRecyclerView.visibility = View.VISIBLE
        }
        if (page == 1) {
            adapter!!.clear()
            mBinding.mRecyclerView.smoothScrollToPosition(0)
        }
        if (!datas.isNullOrEmpty()) {
            adapter!!.add(datas)
        } else {
            if (page == 1) {
                adapter!!.emptyViewShow = true
            }
        }
        if (datas.isNullOrEmpty() || datas.size < pageSize) {
            adapter!!.loadEnd()
        } else {
            adapter!!.loadComplete()

        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        page = 1
        adapter!!.emptyViewShow = false
        adapter!!.clear()
        mModel.getAppointMentList(orderStatusType, pageSize, page)
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


class AppointmentScListViewModel : BaseViewModel() {
    var cancelFinish = MutableLiveData<Boolean>()
    var appointOrders = MutableLiveData<MutableList<AppointMentBean>>()
    val resourceIdLiveData = MutableLiveData<String>()
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
     * 获取我的预约列表
     * @param status 状态 0待使用，1已使用，2已取消，3已失效，4待支付，5部分消费，6已退款，7部分退款
     * @param currPage 当前页码
     */
    fun getAppointMentList(status: String, pageSize: Int, currPage: Int) {

        var param: HashMap<String, String> = HashMap()
        if (status != "-1") {
            param["status"] = status
        }
        param["pageSize"] = "$pageSize"
        param["currPage"] = "$currPage"

        UserRepository().userService.getScMineAppointMents(param)
            .excute(object : BaseObserver<AppointMentBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<AppointMentBean>) {
                    appointOrders.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<AppointMentBean>) {
                    appointOrders.postValue(null)
                }
            })
    }

    /**
     * 获取资源加密信息
     */
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
