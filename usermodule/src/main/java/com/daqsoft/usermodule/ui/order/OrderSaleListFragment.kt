package com.daqsoft.usermodule.ui.order

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FragmentSaleListBinding
import com.daqsoft.usermodule.databinding.ItemSalesBinding
import com.daqsoft.provider.bean.OrderDetail
import com.daqsoft.provider.bean.Record

class OrderSaleListFragment() : BaseFragment<FragmentSaleListBinding, BaseViewModel>() {
    /**
     * 订单详情
     */
    private var orderDetail: OrderDetail? = null

    companion object {
        const val ORDER_DETAIL = "OrderDetail"
        fun newInstance(dr: OrderDetail): OrderSaleListFragment {
            val bundle = Bundle()
            bundle.putParcelable(ORDER_DETAIL, dr)
            val orderDetailFragment = OrderSaleListFragment()
            orderDetailFragment.arguments = bundle
            return orderDetailFragment
        }
    }

    override fun getLayout(): Int = R.layout.fragment_sale_list

    override fun injectVm(): Class<BaseViewModel> = BaseViewModel::class.java

    override fun initView() {
        orderDetail = arguments?.getParcelable(ORDER_DETAIL)
        if (orderDetail != null && !orderDetail!!.recordList.isNullOrEmpty()) {
            adapter.add(orderDetail!!.recordList as MutableList<Record>)
            mBinding.mRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            mBinding.mRecyclerView.adapter = adapter
        }
    }

    override fun initData() {

    }

    private val adapter = object :
        RecyclerViewAdapter<ItemSalesBinding, Record>(
            R.layout.item_sales
        ) {
        @SuppressLint("CheckResult")
        override fun setVariable(
            mBinding: ItemSalesBinding,
            position: Int,
            item: Record
        ) {
            mBinding.item = item
        }
    }


}
