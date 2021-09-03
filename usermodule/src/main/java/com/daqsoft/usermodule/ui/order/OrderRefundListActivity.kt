package com.daqsoft.usermodule.ui.order

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import androidx.lifecycle.Observer
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.ui.order.adapter.OrderRefundAdapter
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityMineOrderRefundBinding
import com.daqsoft.usermodule.ui.order.viewmodel.OrderRefundListViewModel

/**
 * @ClassName    OrderRefundListActivity
 * @Description  购物退款
 * @Author       yuxc
 * @CreateDate   2020/12/1
 */
@Route(path = ARouterPath.UserModule.MINE_ORDER_REFUND)
class OrderRefundListActivity : TitleBarActivity<ActivityMineOrderRefundBinding, OrderRefundListViewModel>() {
    var orderRefundAdapter: OrderRefundAdapter? = null
    var currPage: Int = 1
    override fun getLayout(): Int {
        return R.layout.activity_mine_order_refund
    }

    override fun setTitle(): String {
        return "退款管理"
    }

    override fun injectVm(): Class<OrderRefundListViewModel> {
        return OrderRefundListViewModel::class.java
    }

    override fun initView() {
        orderRefundAdapter = OrderRefundAdapter().apply {
            setOnLoadMoreListener {
                currPage += 1
                mModel.getOrderRefundList(currPage.toString())
            }
        }
        mBinding.recycleView.adapter = orderRefundAdapter
        mBinding.recycleView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        initViewModel()
    }

    private fun initViewModel() {
        mModel.orderRefundList.observe(this, Observer {
            dissMissLoadingDialog()
            //PageDealUtils().pageDeal(currPage, it.refundData, orderRefundAdapter)
            if (it != null && !it.refundData.isNullOrEmpty()) {
                orderRefundAdapter?.add(it.refundData!!)
            }
            if (!mBinding.recycleView.isVisible) {
                mBinding.recycleView.visibility = View.VISIBLE
            }
        })
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getOrderRefundList(currPage.toString())
    }
}