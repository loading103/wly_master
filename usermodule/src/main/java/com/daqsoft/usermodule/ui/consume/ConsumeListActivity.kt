package com.daqsoft.usermodule.ui.consume

import android.annotation.SuppressLint
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.*
import com.daqsoft.provider.bean.Consume
import com.daqsoft.provider.bean.ElectronicTicketData
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.net.excut
import com.daqsoft.baselib.adapter.MultipleRecyclerViewAdapter
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_consume_list.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

/**
 * 消费码列表
 */
@Route(path = ARouterPath.UserModule.USER_CONSUME_LIST_ACTIVITY)
class ConsumeListActivity :
    TitleBarActivity<ActivityConsumeListBinding, ConsumeListActivityViewModel>() {
    override fun getLayout(): Int = R.layout.activity_consume_list

    override fun setTitle(): String = getString(R.string.order_my_consume)

    override fun injectVm(): Class<ConsumeListActivityViewModel> =
        ConsumeListActivityViewModel::class.java

    /**
     * 当前状态
     */
    var currentStatus = ""
    var isNoMoreOrder = false
    var isNoMoreTiket = false

    override fun initView() {

        initViewModel()
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
            isNoMoreOrder = false
            isNoMoreTiket = false
            page = 1
            adapter?.emptyViewShow = false
            adapter?.loadComplete()
            adapter?.clear()
            mModel.getElectronicTickets(currentStatus, page, pageSize, false)
//            reloadData()
//            mSwipeRefreshLayout.isRefreshing = false
        }

        mRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        adapter = object : MultipleRecyclerViewAdapter<ViewDataBinding, Any>() {
            @SuppressLint("CheckResult")
            override fun setVariable(mBinding: ViewDataBinding, position: Int, item: Any) {
                when (mBinding) {
                    is ItemConsumeActivityRoomBinding -> {
                        // 是活动室
                        (item as Consume)
                        mBinding.name = item.activityRoom.venueName
                        mBinding.venueName = item.activityRoom.name
                        if (item.activityRoom.image.isNotEmpty()) {
                            mBinding.url = item.activityRoom.image.split(",")[0]
                        }
                        mBinding.orderTime = getString(
                            R.string.order_activity_room_time_stamp, item.activityRoom
                                .useStartTime, item.activityRoom.useEndTime
                        )
                        if (item.status == "12") {
                            mBinding.tvUse.isEnabled = false
                            mBinding.tvUse.text = "已完成"
                        }
                        RxView.clicks(mBinding.root)
                            .throttleFirst(1, TimeUnit.SECONDS)
                            .subscribe { o ->
                                run {
                                    ARouter.getInstance()
                                        .build(ARouterPath.UserModule.USER_CONSUME_DETAIL)
                                        .withString("orderCode", item.orderCode)
                                        .navigation()
                                }
                            }

                    }
                    is ItemConsumeActivityBinding -> {
                        // 是活动
                        (item as Consume)
                        if (item.activity == null) {
                            return
                        }
                        mBinding.name = item.activity.name
                        mBinding.venueName = item.activity.resourceName
                        var imageUrl = ""
                        if (!item.activity.image.isNullOrEmpty()) {
                            var images = item.activity.image.split(",")
                            if (!images.isNullOrEmpty()) {
                                imageUrl = images[0]
                            }
                        }
                        mBinding.url = imageUrl
                        mBinding.address = item.activity.address
                        mBinding.orderTime = getString(
                            R.string.order_activity_room_time_stamp, item.activity
                                .useStartTime, item.activity.useEndTime
                        )
                        RxView.clicks(mBinding.root)
                            .throttleFirst(1, TimeUnit.SECONDS)
                            .subscribe { o ->
                                run {
                                    ARouter.getInstance()
                                        .build(ARouterPath.UserModule.USER_CONSUME_DETAIL)
                                        .withString("orderCode", item.orderCode)
                                        .navigation()
                                }
                            }
                    }
                    is ItemConsumeVenueBinding -> {
                        (item as Consume)
                        if (item.venueInfo == null) {
                            return
                        }

                        mBinding.name = item.venueInfo.venueName + if (item.isGuideOrder == 1) {
                            "讲解"
                        } else {
                            "预约"
                        }
                        var imageUrl = ""
                        if (!item.venueInfo.image.isNullOrEmpty()) {
                            var images = item.venueInfo.image.split(",")
                            if (!images.isNullOrEmpty()) {
                                imageUrl = images[0]
                            }
                        }
                        mBinding.url = imageUrl
                        if (!item.startTime.isNullOrEmpty() && !item.endTime.isNullOrEmpty()) {
                            mBinding.orderTime =
                                DateUtil.getTwoDateStrs(item.startTime, item.endTime)
                        }
                        if (!item.venueInfo.useEndTime.isNullOrEmpty() && !item.venueInfo.useStartTime.isNullOrEmpty()) {
                            mBinding.userTime = DateUtil.getTwoDateStrs(
                                item.venueInfo.useStartTime,
                                item.venueInfo.useEndTime
                            )
                        }
                        if (!item.venueInfo.reservationType.isNullOrEmpty()) {
                            mBinding.type = if (item.venueInfo.reservationType == "PERSON") {
                                "个人预约"
                            } else {
                                "团队预约"
                            }
                        }
                        mBinding.useNum = "${item.venueInfo.useNum}人"
                        if (item.status == "12") {
                            mBinding.tvUse.isEnabled = false
                            mBinding.tvUse.text = "已完成"
                        }

                        RxView.clicks(mBinding.root)
                            .throttleFirst(1, TimeUnit.SECONDS)
                            .subscribe { o ->
                                run {
                                    ARouter.getInstance()
                                        .build(ARouterPath.UserModule.USER_CONSUME_DETAIL)
                                        .withString("orderCode", item.orderCode)
                                        .navigation()
                                }
                            }
                    }
                    is ItemConsumeElectronicBinding -> {
                        // 是小电商
                        (item as ElectronicTicketData)
                        ElectronicTicketItemHelper(
                            this@ConsumeListActivity,
                            mBinding,
                            position,
                            item
                        )
                    }
                }
            }

        }

        adapter?.emptyViewShow = false
        mRecyclerView.adapter = adapter
        adapter!!.setOnLoadMoreListener {
            page++
            mModel.getElectronicTickets(currentStatus, page, pageSize)
//            reloadData()
        }
        // 数据及取值存放在本地array文件里面，用逗号隔开，前为值，后为value
        var orderStatus = resources.getStringArray(R.array.consume_status)
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
                adapter?.emptyViewShow = false
                currentStatus = tabs[position].statu
                adapter?.loadComplete()
                adapter?.clear()
                mModel.getElectronicTickets(currentStatus, page, pageSize)
            }

            override fun onTabReselect(position: Int) {

            }

        })
    }

    private fun initViewModel() {
        mModel.orders.observe(this, Observer {
            adapter?.emptyViewShow = true
//            if(it.datas.isNullOrEmpty()){
//                adapter?.loadEnd()
//                return@Observer
//            }
            if (it.datas != null) {
                // 以下为判断总共有多少种类型的布局，将当前所属的布局的id作为viewType传入到适配器里
                // 之后有其它的布局可以直接根据类型添加到适配器中
                for (i in it.datas!!.indices) {
                    var data = it.datas!![i]

                    if (data.orderType == ResourceType.CONTENT_TYPE_ACTIVITY_SHIU) {
                        adapter!!.addItem(data)
                        adapter!!.addViewType(R.layout.item_consume_activity_room)
                    } else if (data.orderType == ResourceType.CONTENT_TYPE_ACTIVITY) {
                        adapter!!.addItem(data)
                        adapter!!.addViewType(R.layout.item_consume_activity)
                    } else if (data.orderType == ResourceType.CONTENT_TYPE_VENUE ||
                        data.orderType == ResourceType.CONTENT_TYPE_SCENERY
                    ) {
                        adapter!!.addItem(data)
                        adapter!!.addViewType(R.layout.item_consume_venue)
                    }

                }
                if (it.page?.currPage!! >= it.page?.totalPage!!) {
                    isNoMoreOrder = true
                }
            } else {
                isNoMoreOrder = true
            }
            adapter!!.notifyDataSetChanged()
            if (isNoMoreOrder && isNoMoreTiket && it.datas.isNullOrEmpty()) {
                adapter?.loadEnd()
            } else {
                if (it.page?.currPage!! < it.page?.totalPage!!) {
                    adapter?.loadComplete()
                }
            }
            if (adapter?.getData().isNullOrEmpty()) {
                adapter?.emptyViewShow = true
            }
        })

        mModel.electronicTickets.observe(this, Observer {
            adapter?.emptyViewShow = true
            mBinding.mSwipeRefreshLayout.finishRefresh()
            //            pageDeal(page, it, adapter!!)
            if (it.data != null) {
                // 添加小电商的item
                for (i in it.data!!.indices) {
                    var data = it.data!![i]
                    adapter!!.addViewType(R.layout.item_consume_electronic)
                    adapter!!.addItem(data)
                }
                if (it.data!!.size < pageSize) {
                    isNoMoreTiket = true
                }
            } else {
                isNoMoreTiket = true
            }
            if (currentStatus != "40") {
                mModel.getOrders(currentStatus, page, pageSize)
            } else {
                // 待预约 活动订单，暂时没有预约，不用请求
                adapter!!.notifyDataSetChanged()
                if (isNoMoreTiket && it.datas.isNullOrEmpty()) {
                    adapter?.loadEnd()
                } else {
                    if (it.page?.currPage!! < it.page?.totalPage!!)
                        adapter?.loadComplete()
                }
                if (adapter?.getData().isNullOrEmpty()) {
                    adapter?.emptyViewShow = true
                }
            }
        })
    }


    /**
     * 列表数据页码处理
     *
     * @param page     当前页
     * @param response 返回数据体
     * @param adapter  适配器
     */
    private fun pageDeal(
        page: Int?,
        response: BaseResponse<*>,
        adapter: MultipleRecyclerViewAdapter<*, *>
    ) {
        if (page == null) {
            return
        }
        if (page == 1) {
            adapter.clear()
        }
        if (response.page == null) {
            adapter.loadEnd()
            return
        }
        if (response.page!!.currPage < response.page!!.totalPage) {
            adapter.loadComplete()
        } else {
            adapter.loadEnd()
        }
    }

    override fun initData() {
        mModel.getElectronicTickets("-1", page, pageSize)
    }

    /**
     * 数据集合
     */
    protected val data = arrayListOf<Consume>()
    protected var page = 1
    protected var pageSize = 10


    private var adapter: MultipleRecyclerViewAdapter<ViewDataBinding, Any>? = null

}

/**
 * @des 我的消费码列表的viewmodel
 * @author PuHua
 * @date
 */
class ConsumeListActivityViewModel : BaseViewModel() {
    val orders = MediatorLiveData<BaseResponse<Consume>>()

    val electronicTickets = MediatorLiveData<BaseResponse<MutableList<ElectronicTicketData>>>()

    var isLoading: Boolean = true
    /**
     * 获取列表
     * 未消费(11) 已完成(12) 已失效(13) 已取消(14)
     */
    fun getOrders(status: String, page: Int, pageSize: Int) {
        mPresenter.value?.loading = isLoading
        // 消费状态(40:待预约 10:待消费 2:已完成 -1:全部)
        val map = HashMap<String, Any>()
        var statu = when (status) {
            "0" -> ""
            "40" -> status
            "10" -> "11"
            "2" -> "12"
            else -> ""
        }

        map["status"] = statu
        map["currPage"] = page
        map["pageSize"] = pageSize
        UserRepository().userService.getConsumeList(map)
            .excute(object : BaseObserver<Consume>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Consume>) {
                    orders.postValue(response)
                }

                override fun onFailed(response: BaseResponse<Consume>) {
                    orders.postValue(response)
                }
            })
    }

    /**
     * 获取电子码列表--小电商的
     */
    fun getElectronicTickets(status: String, page: Int, pageSize: Int, loading: Boolean = true) {
        // 消费状态(40:待预约 10:待消费 2:已完成 -1:全部)

        // 以下为文旅云的消费状态对应的小电商的消费状态
//        if (status != "0")
//            map["status"] = status
        isLoading = loading
        mPresenter.value?.loading = isLoading
        var statu = ""
        if (status != "0") {
            statu = status
        }

        ElectronicRepository.electronicService.getElectronicTickets(
            statu,
            pageSize.toString(),
            page.toString()
        )
            .excut(object : ElectronicObserver<MutableList<ElectronicTicketData>>(mPresenter) {
                override fun onSuccess(response: BaseResponse<MutableList<ElectronicTicketData>>) {
                    electronicTickets.postValue(response)
                }

                override fun onFailed(response: BaseResponse<MutableList<ElectronicTicketData>>) {
                    electronicTickets.postValue(response)
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