package com.daqsoft.usermodule.ui.consume

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ItemConsumeElectronicBinding
import com.daqsoft.provider.bean.ElectronicTicketData
import com.daqsoft.usermodule.repository.constant.IntentConstant
import com.daqsoft.provider.utils.TimeUtils
import com.jakewharton.rxbinding2.view.RxView
import org.jetbrains.anko.image
import java.util.concurrent.TimeUnit

/**
 * @Description 小电商消费码item展示帮助类
 * @ClassName   ElectronicTicketItemHelper
 * @Author      PuHua
 * @Time        2019/11/22 15:31
 */
class ElectronicTicketItemHelper {

    @SuppressLint("CheckResult")
    constructor(context: Activity, mBinding: ItemConsumeElectronicBinding, position: Int, item: ElectronicTicketData) {
        mBinding.name = item.productName
        var venueName = ""
        if(item.businessName!=null)
            venueName = item.businessName
        if(!item.scenicName.isNullOrEmpty()){
            venueName = item.scenicName
        }
        mBinding.venueName =venueName
        mBinding.url = item.thumbImageUrl
        mBinding.number = item.productNum
        mBinding.venueName = item.scenicName
        if (item.standardName.isNullOrEmpty()) {
            mBinding.goodsStandard.visibility = View.GONE
        } else {
            mBinding.goodsStandard.content = item.standardName
        }
        if (item.useStartTime != null && item.useEndTime != null)
            mBinding.effectTime = context.getString(
                R.string.order_activity_room_time_stamp, TimeUtils.timeStamp2Date(item.useStartTime)
                , TimeUtils.timeStamp2Date(item.useEndTime)
            )
        else
            mBinding.effectTime ="暂无"
        RxView.clicks(mBinding.root)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                run {
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_ELECTRONIC_TICKET_DETAIL)
                        .withString(IntentConstant.OBJECT, item.id)
                        .withString(IntentConstant.TYPE, item.type?:"1")
                        .navigation()
                }
            }

        if (item.needPrecontract && item.orderStatus == "40") {
            // 当订单需要预约且订单状态为待预约，展示立即预约框
            mBinding.rlOrderImitate.visibility = View.VISIBLE
            RxView.clicks(mBinding.rlOrderImitate)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe { o ->
                    run {
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.USER_CONSUME_ELECTRONIC_BOOKING_WEB)
                            .withString(IntentConstant.OBJECT, item.id)
                            .navigation()
                    }
                }

        } else {
            mBinding.rlOrderImitate.visibility = View.GONE
        }

        if (item.existWaitConfirmedStatus) {
            // 当有待商家确认的预约，展示有新的预约待确认提示文字
            mBinding.tvNoticeValid.visibility = View.VISIBLE
        } else {
            mBinding.tvNoticeValid.visibility = View.GONE
        }

        if (item.existRejectStatus) {
            // 当有被商家驳回的预约，展示驳回提示文字
            mBinding.tvNoticeReapply.visibility = View.VISIBLE
        } else {
            mBinding.tvNoticeReapply.visibility = View.GONE
        }

        // 以下when为判断是否展示失效和消费
        when (item.orderStatus) {
            "2" -> {
                mBinding.tvUse.visibility = View.VISIBLE
                if(item.refundStatus){
                    mBinding.ivTag.visibility = View.VISIBLE
                    mBinding.ivTag.image = context.getDrawable(R.mipmap.mine_code_tag_tuikuanzhong)
                    mBinding.tvUse.isEnabled = true
                }else {
                    mBinding.tvUse.isEnabled = false
                    mBinding.ivTag.visibility = View.GONE
                }
                mBinding.tvUse.text = item.orderStatusName
            }
            "10" -> {

                if(item.refundStatus){
                    mBinding.ivTag.visibility = View.VISIBLE
                    mBinding.ivTag.image = context.getDrawable(R.mipmap.mine_code_tag_tuikuanzhong)
                    mBinding.tvUse.text = item.orderStatusName
                }else {
                    if (!item.reservationUseTime.isNullOrEmpty()){
                        mBinding.tvUse.text = context.getString(
                            R.string.order_consume_waite_cost,
                            TimeUtils.timeStamp2Date(item.reservationUseTime)
                        )
                    }else{
                        mBinding.tvUse.text=context.getString(R.string.order_use_imitation)
                    }
                    mBinding.ivTag.visibility = View.GONE
                }
                // 待消费
                mBinding.tvUse.visibility = View.VISIBLE
                mBinding.tvUse.isEnabled = true
                return
            }
            "12" -> {
                // 已失效
                mBinding.ivTag.visibility = View.VISIBLE
                mBinding.ivTag.image = context.getDrawable(R.mipmap.mine_code_tag_yishixiao)
                mBinding.tvUse.visibility = View.VISIBLE
                mBinding.tvUse.text = context.getString(R.string.order_has_no_effect)
                mBinding.tvUse.isEnabled = false
            }
            "11" -> {
                // 已消费
                mBinding.ivTag.visibility = View.VISIBLE
                mBinding.ivTag.image = context.getDrawable(R.mipmap.mine_code_tag_yixiaofei)
                mBinding.tvUse.visibility = View.VISIBLE
                mBinding.tvUse.text = context.getString(R.string.order_has_use)
                mBinding.tvUse.isEnabled = false
            }
            "32" -> {
                // 待确认
                mBinding.tvUse.visibility = View.VISIBLE
                mBinding.tvUse.text = context.getString(R.string.order_consume_waite_confirm)
                mBinding.tvUse.isEnabled = false

            }
            else -> {
                mBinding.ivTag.visibility = View.GONE
                mBinding.tvUse.visibility = View.GONE
            }
        }

    }


}