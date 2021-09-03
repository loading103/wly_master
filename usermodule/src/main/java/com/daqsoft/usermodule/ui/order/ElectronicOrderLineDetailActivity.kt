package com.daqsoft.usermodule.ui.order

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.*
import com.daqsoft.provider.bean.AllQuantity
import com.daqsoft.provider.bean.ContractInfo
import com.daqsoft.provider.bean.OrderDetailBean
import com.daqsoft.provider.bean.OrderRouteTourists
import com.jakewharton.rxbinding2.view.RxView
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit

/**
 * @des 小电商订单详情页面-- 路线
 * @author PuHua
 * @Date 2019/12/13 16:52
 */
@Route(path = ARouterPath.UserModule.USER_ELECTRONIC_LINE_DETAIL_ACTIVITY)
class ElectronicOrderLineDetailActivity : ElectronicOrderDetailActivity() {


    @JvmField
    @Autowired
    var id: Int = -1
    @JvmField
    @Autowired
    var orderType: Int = -1

    override fun initData() {
        super.initData()
        mModel.orderDetail(id)
        // 合同点击事件
    }

    /**
     * 线路价格集合
     */
    private val typesAdapter by lazy {
        object :
            RecyclerViewAdapter<ItemElectronicLineTypesBinding, AllQuantity>(R.layout.item_electronic_line_types) {
            override fun setVariable(mBinding: ItemElectronicLineTypesBinding, position: Int, item: AllQuantity) {
                mBinding.name = item.name
                mBinding.price = "￥" + item.price
                mBinding.number = "x" + item.quantity
            }
        }
    }
    /**
     * 游客集合
     */
    private val membersAdapter by lazy {
        object :
            RecyclerViewAdapter<ItemElectronicLineMembersBinding, OrderRouteTourists>(R.layout.item_electronic_line_members) {
            override fun setVariable(
                mBinding: ItemElectronicLineMembersBinding, position: Int, item:
                OrderRouteTourists
            ) {
                mBinding.name = item.touristName
                mBinding.phone = item.touristMobile
                mBinding.idNumber = item.credentialsNumber
            }
        }
    }

    @SuppressLint("CheckResult")
    override fun addOrderInformationView(it: OrderDetailBean?) {
        val inflate =
            DataBindingUtil.inflate<LayoutElectronicLineInformationBinding>(
                layoutInflater, R.layout
                    .layout_electronic_line_information, null, false
            )
//        setImageUrl(inflate.mCoverIv, it?.thumbImageUrl, getDrawable(R.drawable.placeholder_img_fail_h300), 5)
        inflate.url = it?.thumbImageUrl
        inflate.mShopNameTv.text = it?.productName
        inflate.mStandardNameTv.text = it?.standardName
        if (it?.refundNum != null && it?.refundNum!!.toInt() > 0) {
            inflate.mTagTv.text = getString(R.string.order_has_refund, it?.refundNum)
        }
        inflate.mRealPayValueTv.text = "￥${it?.orderPayAmount}"
        inflate.mTotalPriceValueTv.text = "￥${it?.productAmount}"

        // 订单信息
        inflate.layoutOrderInformation.mOrderValueTv.text = it?.orderSn
        inflate.layoutOrderInformation.mOrderTimeValueTv.text = it?.gmtCreate?.let { it1 -> Utils.transferLongToDate(Utils.datePattern, it1) }
        inflate.layoutOrderInformation.mRealMarkValueTv.text = if (!it?.orderRemarks.isNullOrEmpty()){it?.orderRemarks}else{"无"}
        inflate.layoutOrderInformation.mCopyTv.setOnClickListener {
            val clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipBoardManager.primaryClip = ClipData.newPlainText(
                null, inflate.layoutOrderInformation.mOrderValueTv.text
                    .toString()
            )
            toast(getString(R.string.order_copy_complete))
        }
        inflate.layoutOrderInformation.mHaveSendTv.text = getString(R.string.order_cost_number)
        inflate.layoutOrderInformation.mHaveSendValueTv.text = "${it?.consumeProductNum}"
        inflate.layoutOrderInformation.mBuyNumberValueTv.text = "${it?.productNum}"
        if(it!!.orderRoute!=null&&it!!.orderRoute!!.contractId>0) {
            mModel.getContractInfo(it!!.orderRoute!!.contractId)
        }else{
            inflate.tvContractName.text = "暂无合同"
        }
        //合同详情
        mModel.contractInfo.observe(this, Observer { childIt ->
            if (!childIt.context.isNullOrEmpty()) {
                inflate.tvContractName.text = it!!.orderRoute!!.contractName
            } else {
                inflate.tvContractName.text = "暂无合同"
            }
            RxView.clicks(inflate.tvContractName)
                .throttleFirst(1, TimeUnit.SECONDS).subscribe { clickId ->
                    ARouter.getInstance()
                        .build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString("mTitle", it!!.orderRoute!!.contractName)
                        .withString("html", childIt.context)
                        .navigation()
                }
        })

        // 是否限制预约操作时间 0不限制 1限制
        inflate.layoutOrderInformation.isNeedBooking = it!!.needBookingTime != "0"
        inflate.layoutOrderInformation.bean = it
        // 订单类型列表
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        inflate.rlTypes.layoutManager = layoutManager
        inflate.rlTypes.adapter = typesAdapter
        typesAdapter.add(it!!.orderRoute!!.allQuantity as MutableList<AllQuantity>)
        // 游客列表
        val memberLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        inflate.rlMembers.layoutManager = memberLayoutManager
        inflate.rlMembers.adapter = membersAdapter
        membersAdapter.add(it.orderRouteTourists!!)

        addView(inflate.root, mBinding.flCommodityInformation)
    }

}
