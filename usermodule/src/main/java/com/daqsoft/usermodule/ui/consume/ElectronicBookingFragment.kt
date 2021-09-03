package com.daqsoft.usermodule.ui.consume

import android.annotation.SuppressLint
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.provider.bean.ElectronicDetailBean
import com.daqsoft.provider.bean.Record
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FragmentConsumeDetailBinding
import com.daqsoft.usermodule.databinding.FragmentElectronicBookingBinding
import com.daqsoft.usermodule.databinding.FragmentSaleListBinding
import com.daqsoft.usermodule.databinding.ItemSalesBinding

/**
 * @des 小电商预订填写信息的页面
 * @author PuHua
 * @date
 */
class ElectronicBookingFragment() : BaseFragment<FragmentElectronicBookingBinding, BaseViewModel>() {

    var consume: ElectronicDetailBean? = null

    constructor(dr: ElectronicDetailBean) : this() {
        this.consume = dr
    }

    override fun getLayout(): Int = R.layout.fragment_electronic_booking

    override fun injectVm(): Class<BaseViewModel> = BaseViewModel::class.java

    override fun initView() {

        if (consume != null)
            mBinding.validNumber.maxNumber =10
            when (consume!!.orderType) {
//                ResourceType.CONTENT_TYPE_ACTIVITY_SHIU -> {
//                    mBinding.name = consume!!.activityRoom.venueName
//                    mBinding.image = consume!!.activityRoom.image
//                    mBinding.address = consume!!.activityRoom.address
//                    mBinding.time = consume!!.activityRoom.useStartTime
//                    mBinding.productName = consume!!.activityRoom.name
//                }
//                ResourceType.CONTENT_TYPE_ACTIVITY -> {
//                    mBinding.name = consume!!.activity.name
//                    mBinding.image = consume!!.activity.image
//                    mBinding.address = consume!!.activity.address
//                    mBinding.time = consume!!.activity.useStartTime
//                    mBinding.productName = consume!!.activity.name
//                }
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
