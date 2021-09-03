package com.daqsoft.usermodule.ui.order

import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.utils.TimeUtils
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityMineOrderDetailBinding
import com.daqsoft.usermodule.ui.order.viewmodel.OrderRefundDetailViewModel
import org.jetbrains.anko.backgroundResource

/**
 * @ClassName    OrderRefundDetailActivity
 * @Description  购物退款详情
 * @Author       yuxc
 * @CreateDate   2020/12/2
 */
@Route(path = ARouterPath.UserModule.MINE_ORDER_REFUND_DETAIL)
class OrderRefundDetailActivity : TitleBarActivity<ActivityMineOrderDetailBinding, OrderRefundDetailViewModel>() {

    @JvmField
    @Autowired
    var orderSn: String = ""

    override fun getLayout(): Int {
        return R.layout.activity_mine_order_detail
    }

    override fun setTitle(): String {
        return "退款详情"
    }

    override fun injectVm(): Class<OrderRefundDetailViewModel> {
        return OrderRefundDetailViewModel::class.java
    }

    override fun initView() {
        mModel.orderRefundDetail.observe(this, Observer {
            dissMissLoadingDialog()
            it?.let { mBinding.orderDetailBean = it }

            when (it.auditStatus) {
                // 待审核
                0 -> {
                    mBinding.tv1.text = ""
                    mBinding.tv1.backgroundResource = R.mipmap.mine_code_book_result_step_success

                    mBinding.tv2.text = "02"
                    mBinding.tv2.backgroundResource = R.drawable.shape_white_oval
                    mBinding.tvStatus2.text = "待审核"

                    mBinding.tv3.text = "03"
                    mBinding.tv3.backgroundResource = R.drawable.shape_white_oval
                    mBinding.tvStatus3.text = "申退完成"

                    mBinding.tvContentLabel.text = "你的退款申请已提交，待卖家审核，关注微信公众号，退款状态随时查。"

                    mBinding.vStatusLine1.backgroundResource = R.drawable.shape_white_line
                    mBinding.vStatusLine2.backgroundResource = R.drawable.shape_white_line
                }
                // 审核通过
                1 -> {
                    mBinding.tv2.text = "02"
                    mBinding.tv2.backgroundResource = R.mipmap.mine_result_step_highlighted
                    mBinding.tvStatus2.text = "同意退款"
                    mBinding.tvTime2.text = TimeUtils.timeStamp2Date(it.gmtAudit.toString(), "yyyy-MM-dd HH:mm:ss")
                    when (it.refundStatus) {
                        // 待退款
                        0 -> {
                            mBinding.tv3.text = "03"
                            mBinding.tv3.backgroundResource = R.mipmap.mine_code_book_result_step_failed
                            mBinding.tvStatus3.text = "退款中"
                            mBinding.tvTime3.text = ""

                            mBinding.tvContentLabel.text = "你的退款申请已通过，关注微信公众号，退款状态随时查。"

                            mBinding.vStatusLine2.backgroundResource = R.drawable.shape_white_line
                        }
                        // 已退款
                        1 -> {
                            mBinding.tv3.text = ""
                            mBinding.tv3.backgroundResource = R.mipmap.mine_code_book_result_step_success
                            mBinding.tvStatus3.text = "退款成功"
                            mBinding.tvTime3.text = TimeUtils.timeStamp2Date(it.gmtRefund.toString(), "yyyy-MM-dd HH:mm:ss")

                            mBinding.tvContentLabel.text = "退款成功，资金已原路退回，请注意查收。关注微信公众号，退款状态随时查。"
                        }
                        // 退款失败
                        else -> {
                            mBinding.tv3.text = ""
                            mBinding.tv3.backgroundResource = R.mipmap.mine_code_book_result_step_failed
                            mBinding.tvStatus3.text = "退款失败"
                            mBinding.tvTime3.text = TimeUtils.timeStamp2Date(it.gmtRefund.toString(), "yyyy-MM-dd HH:mm:ss")

                            mBinding.tvContentLabel.text = "非常抱歉，你的退款处理存在异常，建议联系客服。关注微信公众号，退款状态随时查。"
                        }
                    }
                }
                // 审核不通过
                2 -> {
                    mBinding.tv2.text = ""
                    mBinding.tv2.backgroundResource = R.mipmap.mine_code_book_result_step_failed
                    mBinding.tvStatus2.text = "退款驳回"
                    mBinding.tvTime2.text = TimeUtils.timeStamp2Date(it.gmtAudit.toString(), "yyyy-MM-dd HH:mm:ss")

                    mBinding.tv3.text = ""
                    mBinding.tv3.backgroundResource = R.mipmap.mine_code_book_result_step_failed
                    mBinding.tvStatus3.text = "退款失败"
                    mBinding.tvTime3.text = ""

                    var str = ""
                    if (!it.auditRemark.isNullOrEmpty()) {
                        str = "店主留言：" + it.auditRemark
                    }
                    mBinding.tvContentLabel.text = "你的退款申请已驳回。%@关注微信公众号，退款状态随时查。$str"
                }
            }

            mBinding.tvTime1.text = TimeUtils.timeStamp2Date(it.gmtCreate.toString(), "yyyy-MM-dd HH:mm:ss")

            mBinding.tvApplyAmount.text = "￥" + it.applyAmount
            mBinding.tvServiceAmount.text = "￥" + it.serviceAmount
            mBinding.tvActualPayment.text = "￥" + it.amount
            mBinding.tvCreateTime.text = TimeUtils.timeStamp2Date(it.gmtCreate.toString(), "yyyy-MM-dd HH:mm:ss")
            mBinding.tvResultTime.text = TimeUtils.timeStamp2Date(it.gmtRefund.toString(), "yyyy-MM-dd HH:mm:ss")

            mBinding.tvRefundResult.text = when (it.refundStatus) {
                0 -> "待退款"
                1 -> "已退款"
                2 -> "退款失败"
                else -> "未知状态"
            }
        })

        mModel.qRCodeBean.observe(this, Observer {
            it.qrcode?.let {
                Glide.with(this)
                    .load(it)
                    .into(mBinding.ivQrcode)
            }
        })
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getOrderRefundDetail(orderSn)
        mModel.getTalentSubscribe()
    }
}