package com.daqsoft.usermodule.ui.order.adapter

import android.view.View
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.provider.bean.CertTypes
import com.daqsoft.provider.bean.OrderAttacthPersonBean
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ItemAttchedPersonInfoBinding

/**
 * @Description
 * @ClassName   MineConsumePersonAdapter
 * @Author      luoyi
 * @Time        2020/8/20 17:05
 */
class MineConsumePersonAdapter :
    RecyclerViewAdapter<ItemAttchedPersonInfoBinding, OrderAttacthPersonBean>(
        R.layout.item_attched_person_info
    ) {
    var userTime: String? = ""
    override fun setVariable(
        mBinding: ItemAttchedPersonInfoBinding,
        position: Int,
        item: OrderAttacthPersonBean
    ) {
        mBinding.name = item.userName
        mBinding.phone = item.userPhone
        mBinding.time =
            "" + userTime
        mBinding.number = "1"
        mBinding.idNumber =
            "${CertTypes.getCertTypeName(item.userCardType)}(${item.userCardNumber})"
        if (item.healthInfo != null) {

            if (item.healthInfo!!.enableTravelCode) {
                mBinding.ivTravelCodeInfo.visibility = View.VISIBLE
            } else {
                mBinding.ivTravelCodeInfo.visibility = View.GONE
            }
            if (item.healthInfo!!.enableHealthyCode) {
                mBinding.ivHealthInfo.visibility = View.VISIBLE
            } else {
                mBinding.ivHealthInfo.visibility = View.GONE
            }


            if (item.healthInfo!!.smartTravelRegisterStatus) {
                mBinding.ivTravelCodeInfo.content = "已注册"
            } else {
                mBinding.ivTravelCodeInfo.content = "未注册"
            }
            if (item.healthInfo!!.healthCode.isNullOrEmpty()) {
                mBinding.ivHealthInfo.content = "未注册"
            } else {
                when (item.healthInfo!!.healthCode) {
                    "01" -> {
                        mBinding.ivHealthInfo.content = "低风险(${item.healthInfo!!.regionName})"
                    }
                    "11" -> {
                        mBinding.ivHealthInfo.content = "中风险(${item.healthInfo!!.regionName})"
                    }
                    "31" -> {
                        mBinding.ivHealthInfo.content = "高风险(${item.healthInfo!!.regionName})"
                    }
                    else -> {
                        mBinding.ivHealthInfo.content = "未知"
                    }
                }
            }
        } else {
            mBinding.ivTravelCodeInfo.visibility = View.GONE
            mBinding.ivHealthInfo.visibility = View.GONE
        }

        mBinding.status = when (item.orderStatus) {
            // 判断当前订单的状态
            4 -> {
                // 待审核
                "待审核"
            }
            79 -> {
                // 未通过
                "未通过"
            }
            11 -> {
                // 待消费
                "待消费"
            }
            12 -> {
                // 已完成

                "已完成"
            }
            13 -> {
                // 已失效
                "已失效"
            }
            14 -> {
                // 已取消
                "已取消"
            }

            else -> ""
        }
    }
}