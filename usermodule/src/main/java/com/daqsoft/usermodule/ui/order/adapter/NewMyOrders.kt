package com.daqsoft.usermodule.ui.order.adapter

import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ActivityBookBean
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ItemNewOrdersBinding

/**
 * @Author：      邓益千
 * @Create by：   2020/7/7 14:43
 * @Description： 新版 我的预约Adapter
 */
class NewMyOrders : RecyclerViewAdapter<ItemNewOrdersBinding, ActivityBookBean>(R.layout.item_new_orders) {

    override fun setVariable(mBinding: ItemNewOrdersBinding, position: Int, item: ActivityBookBean) {
        mBinding.bookingNumber.content = item.orderCode
        mBinding.name.text = item.venueInfo.venueName
        mBinding.tvPeople.visibility = View.GONE
        if (!item.venueInfo.image.isNullOrEmpty()) {
            mBinding.url = item.venueInfo.image.split(",")[0]
        }

        mBinding.numberView.content = item.venueInfo.useNum
        mBinding.tvPeople.content = item.activity.signNum


        if (item.isGuideOrder == 1) {
            mBinding.tvTotal.content = "线下支付"
            mBinding.mReviewsTv.visibility = View.GONE
        } else {
            mBinding.mReviewsTv.visibility = View.VISIBLE
            if (item.venueInfo != null) {
                mBinding.mReviewsTv.onNoDoubleClick {
                    ARouter.getInstance().build(ARouterPath.VenuesModule.VENUES_RESERVATION_COM_ACTIVITY)
                        .withInt("resourceType", 1)
                        .withString("venueId", item.venueInfo.venueId)
                        .withString("orderCode", item.id.toString())
                        .navigation()
                }
            }
            mBinding.tvTotal.content = "免费"
        }

        when (item.orderType) {
            ResourceType.CONTENT_TYPE_VENUE -> {
                mBinding.tvTime.content = item.createTime
            }

            ResourceType.CONTENT_TYPE_ACTIVITY -> {
                mBinding.tvTime.content = item.activity.useStartTime
            }

            ResourceType.CONTENT_TYPE_ACTIVITY_SHIU -> {
                mBinding.tvTime.content = item.activity.useStartTime
            }
        }


        mBinding.root.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.UserModule.NEW_ORDER_DETAIL)
                .withString("orderId", item.id.toString())
                .withString("orderType", "0")
                .navigation()
        }

    }
}