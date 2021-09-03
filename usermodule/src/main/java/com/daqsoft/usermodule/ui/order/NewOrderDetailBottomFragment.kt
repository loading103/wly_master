package com.daqsoft.usermodule.ui.order

import androidx.lifecycle.Observer
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.OrderDetail
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FragmentNewOrderDetailBottomBinding

/**
 * @Author：      邓益千
 * @Create by：   2020/7/7 16:07
 * @Description： 新版-订单详情Bottom部分
 */
class NewOrderDetailBottomFragment : BaseFragment<FragmentNewOrderDetailBottomBinding, OrdersBookViewModel>() {

    private var orderCode = ""

    override fun getLayout(): Int = R.layout.fragment_new_order_detail_bottom

    override fun injectVm(): Class<OrdersBookViewModel> = OrdersBookViewModel::class.java

    override fun initView() {
        mBinding.soonView.onNoDoubleClick {
            mModel.postWriteOff(orderCode)
        }
    }

    override fun initData() {
        //健康信息
        mModel.helathInfo.observe(this, Observer {
//            if (it.smartTravelRegisterStat){
//                mBinding.healthLayout.visibility = View.VISIBLE
//                mBinding.addressView.content = it.regionName
//
//                //未注册健康信息
//            } else {
//                mBinding.healthLayout.visibility = View.GONE
//            }
//
//
//            mBinding.healthView.setContentIcon(ResourceUtils.getDrawable(context!!, R.mipmap.refund_application_selected))
//            mBinding.travelHealth.setContentIcon(ResourceUtils.getDrawable(context!!, R.mipmap.refund_application_selected))
        })


    }

    fun bindData(detail: OrderDetail, orderType: String) {
        mBinding.detail = detail

        //表示是核销的详情
        if (orderType == "1") {
            orderCode = detail.code
            //获取健康码
            mModel.getUserHealthInfo(detail.orderId)
//            mBinding.moreLayout.visibility = View.GONE
//            mBinding.soonView.visibility = View.VISIBLE
//
//            detail.validInfo?.let {
//                mBinding.writeOffsView.setLabel("已核销数量")
//                mBinding.writeOffsDate.setLabel("可核销数量")
//                if (it.validFlag) {
//                    mBinding.writeOffsView.content = it.validNum
//                    mBinding.writeOffsDate.content = "0"
//                    mBinding.writeOffsPeople.apply {
//                        visibility = View.VISIBLE
//                        setLabel("电子核销码")
//                        content = it.consumeCode
//                    }
//                    mBinding.writeOffsTime.apply {
//                        visibility = View.VISIBLE
//                        setLabel("核销时间")
//                        content = it.validTime
//                    }
//                } else {
//                    mBinding.writeOffsView.content = "0"
//                    mBinding.writeOffsDate.content = it.validNum
//                }

//            }

            //预订详情
//        } else {
            //获取健康码
//            mModel.getUserHealthInfo(detail.id)
//            mBinding.phoneView.content = detail.userPhone
//            mBinding.idCardView.content = detail.idCard
//            mBinding.writeOffsDate.content = detail.consumeNum
//            mBinding.qrCodeUrl = detail.orderQrCode
//            mBinding.consuCode.text = "电子码:${detail.consumeCode}"
//            mBinding.writeOffsPeople.apply {
//                visibility = View.VISIBLE
//                content = detail.userName
//            }
//
//            mBinding.copyView.setOnClickListener {
//                Utils.copy(detail.orderCode, context!!)
//                ToastUtils.showMessage("复制成功")
//            }
        }
    }
}