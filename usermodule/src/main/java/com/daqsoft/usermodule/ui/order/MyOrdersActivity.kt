package com.daqsoft.usermodule.ui.order

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.OrderRoom
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityOrderListBinding
import com.daqsoft.usermodule.ui.order.adapter.NewMyOrders
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import java.util.ArrayList

/**
 * @Author：      邓益千
 * @Create by：   2020/7/7 11:48
 * @Description： 我的预约 改版
 */
@Route(path = ARouterPath.UserModule.USER_MY_ORDERS)
class MyOrdersActivity : TitleBarActivity<ActivityOrderListBinding, OrdersBookViewModel>() {

    @Autowired
    @JvmField
    var type: String = ""

    private var page = 1
    private var orderStatusType = "0"

    private val orderAdapter by lazy {
        NewMyOrders()
    }

    private val ordersType by lazy {
        resources.getStringArray(R.array.new_my_orders)
    }

    private var currType = ""

    override fun setTitle(): String = "我的预约"

    override fun getLayout(): Int = R.layout.activity_order_list

    override fun injectVm(): Class<OrdersBookViewModel> = OrdersBookViewModel::class.java

    override fun initView() {
        //初始化Tab
        val status = mutableListOf<CustomTabEntity>()
        ordersType.forEach {
            val order = it.split(",")
            status.add(OrdersState(order[0]))
        }
        mBinding.commonTab.setTabData(status as ArrayList<CustomTabEntity>)
        mBinding.commonTab.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabReselect(position: Int) {}
            override fun onTabSelect(position: Int) {
                currType = ordersType[position].split(",")[1]
                mModel.getOrderBook(currType, page, type)
            }
        })

        //初始化RecyclerView
        mBinding.mRecyclerView.visibility = View.VISIBLE
        mBinding.mRecyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.mRecyclerView.adapter = orderAdapter

        //刷新监听
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
            mModel.getOrderBook(currType, page, type)
        }
    }

    override fun initData() {
        mModel.getOrderBook(orderStatusType, page, type)

        mModel.ordersBook.observe(this, Observer {
            orderAdapter.setNewData(it)
//            mBinding.mSwipeRefreshLayout.isRefreshing = false
        })
    }

    /**Tab实体*/
    inner class OrdersState(private val tabTitle: String) : CustomTabEntity{

        override fun getTabTitle(): String = tabTitle

        override fun getTabSelectedIcon(): Int = 0

        override fun getTabUnselectedIcon(): Int = 0
    }
}