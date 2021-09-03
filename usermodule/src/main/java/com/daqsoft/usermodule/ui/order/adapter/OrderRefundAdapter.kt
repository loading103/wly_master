package com.daqsoft.usermodule.ui.order.adapter

import android.graphics.Color
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.RefundDataBean
import com.daqsoft.provider.utils.TimeUtils
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ItemOrderRefundBinding

/**
 * @ClassName    OrderRefundAdapter
 * @Description  购物退款适配器
 * @Author       yuxc
 * @CreateDate   2020/12/1
 */
class OrderRefundAdapter : RecyclerViewAdapter<ItemOrderRefundBinding, RefundDataBean>(R.layout.item_order_refund) {
    override fun setVariable(mBinding: ItemOrderRefundBinding, position: Int, item: RefundDataBean) {
        mBinding.orderBean = item

        item.refundStatus?.let {
            when (it) {
                0 -> {
                    mBinding.tvOrderStatus.text = "退款中"
                    mBinding.tvOrderStatus.setTextColor(Color.parseColor("#ff9e05"))
                }
                1 -> {
                    mBinding.tvOrderStatus.text = "退款成功"
                    mBinding.tvOrderStatus.setTextColor(Color.parseColor("#36cd64"))
                }
                2 -> {
                    mBinding.tvOrderStatus.text = "退款失败"
                    mBinding.tvOrderStatus.setTextColor(Color.parseColor("#ff4e4e"))
                }
                else -> ""
            }
        }

        mBinding.tvTime.text = TimeUtils.timeStamp2Date(item.gmtCreate.toString(), "yyyy-MM-dd HH:mm:ss")

        mBinding.root.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.UserModule.MINE_ORDER_REFUND_DETAIL).withString("orderSn", item.id.toString()).navigation()
        }
    }
}