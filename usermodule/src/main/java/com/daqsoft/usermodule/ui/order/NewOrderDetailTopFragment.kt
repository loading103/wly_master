package com.daqsoft.usermodule.ui.order

import android.content.Intent
import android.net.Uri
import android.view.View
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.OrderDetail
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FragmentNewOrderDetailTopBinding

/**
 * @Author：      邓益千
 * @Create by：   2020/7/7 16:07
 * @Description： 新版-订单详情TOP部分
 */
class NewOrderDetailTopFragment : BaseFragment<FragmentNewOrderDetailTopBinding, OrdersBookViewModel>() {

    override fun getLayout(): Int = R.layout.fragment_new_order_detail_top

    override fun injectVm(): Class<OrdersBookViewModel> = OrdersBookViewModel::class.java

    override fun initView() {
    }

    override fun initData() {
    }

    fun bindData(detail: OrderDetail, orderType: String) {
        mBinding.detail = detail

        //是核销订单详情
        if (orderType == "1") {
            mBinding.appraiseView.visibility = View.GONE
            mBinding.titleView.text = detail.resourceName
            mBinding.appointDate.content = detail.useStartTime
            detail.validInfo?.let {
                if (it.validFlag) {
                    mBinding.orderStateView.text = "核销成功，欢迎入园"
                } else {
                    mBinding.orderStateView.text = "待核销，等待入园"
                }
            }
            mBinding.appointNum.apply {
                setLabel("预约人数")
                content = "${detail.useNum}人"
            }
            mBinding.phoneView.apply {
                setLabel("有效期")
                showDivider()
                content = "${detail.orderStartTime}—${detail.useEndTime.substring(
                    detail.useEndTime.lastIndexOf(" "), detail.useEndTime.length
                )}"
            }
            mBinding.addressView.apply {
                hidArrow()
                setLabel("预   约  人")
                content = detail.userName
            }
            mBinding.mobilePhone.apply {
                visibility = View.VISIBLE
                content = detail.phone
            }
            mBinding.idCardView.apply {
                visibility = View.VISIBLE
                content = detail.idCard
            }

            if (!detail.images.isNullOrEmpty()) {
                mBinding.coverUrl = detail.images.split(",")[0]
            }

            //是预订详情订单
        } else {
            when (detail.orderStatus.toInt()) {
                4 -> {
                    mBinding.orderStateView.text = "待审核"
                    mBinding.appraiseView.text = "取消订单"
                    mBinding.orderDateView.visibility = View.GONE
                }
                11 -> {
                    mBinding.orderStateView.text = "待消费"
                }
                12 -> {
                    mBinding.orderStateView.text = "已经完成"
                }
            }

            mBinding.addressView.content = detail.address
            mBinding.phoneView.content = detail.userPhone
            if (detail.validInfo != null)
                mBinding.titleView.text = detail.venueInfo!!.venueName
            mBinding.appointDate.content = detail.createTime

            mBinding.phoneView.onNoDoubleClick {
                val intent = Intent(Intent.ACTION_CALL)
                val uri = Uri.parse("tel:" + detail.userPhone)
                intent.data = uri
                startActivity(intent)
            }

            if (!detail.image.isNullOrEmpty()) {
                mBinding.coverUrl = detail.image.split(",")[0]
            }
        }

        if (detail.reservationType == ResourceType.PERSON) {
            mBinding.appointType.content = "个人预约"
        } else {
            mBinding.appointType.content = "团体预约"
        }
    }
}