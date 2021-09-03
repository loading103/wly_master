package com.daqsoft.venuesmodule.adapter

import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.WriteOffsBean
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemWriterOffOrderBinding

/**
 * @Description 核销订单列表
 * @ClassName   WriterOffOrderAdapter
 * @Author      luoyi
 * @Time        2020/7/7 13:36
 */
class WriterOffOrderAdapter : RecyclerViewAdapter<ItemWriterOffOrderBinding, WriteOffsBean> {
    var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_writer_off_order) {
        this.mContext = context
        emptyViewShow = false
    }

    override fun setVariable(mBinding: ItemWriterOffOrderBinding, position: Int, item: WriteOffsBean) {
        mBinding.tvReservationTime.text = "预约时段：${item.startTime}—${item.endTime}"
        mBinding.tvReservationPnum.text = "预约人数：${item.useNum}"
        mBinding.tvReservationPname.text = "预约人：${item.userName}"
        mBinding.tvOperationBtn.text = "查看详情"
        when (item.orderStatus) {
            11 -> { //待核销
                mBinding.tvOperationBtn.text = "点击核销"
                mBinding.tvReservationStatus.apply {
                    text = "待核销"
                    setTextColor(ResourceUtils.getColor(this, R.color.color_1995FF))
                    setBackgroundColor(ResourceUtils.getColor(this, R.color.color_E8F4FF))
                }
            }

            12 -> { //已核销
                mBinding.tvOperationBtn.text = "查看详情"
                mBinding.tvReservationStatus.apply {
                    text = "核销成功"
                    setTextColor(ResourceUtils.getColor(this, R.color.color_36CD64))
                    setBackgroundColor(ResourceUtils.getColor(this, R.color.color_E2FCE9))
                }
            }

            13 -> { //已失效
                mBinding.tvOperationBtn.text = "查看详情"
                mBinding.tvReservationStatus.apply {
                    text = "预约时间已过，失效"
                    setTextColor(ResourceUtils.getColor(this, R.color.color_FF4E4E))
                    setBackgroundColor(ResourceUtils.getColor(this, R.color.color_FFEDED))
                }
            }
        }

        mBinding.root.onNoDoubleClick {
            if (item != null) {
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.WRITE_OFF_DETAIL)
                    .withString("orderId", item.orderId.toString())
                    .navigation()
            }
        }
    }
}