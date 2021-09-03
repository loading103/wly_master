package com.daqsoft.usermodule.ui.order

import android.graphics.drawable.Drawable
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityNewOrderDetailBinding
import com.daqsoft.usermodule.uitls.Utils

/**
 * @Author：      邓益千
 * @Create by：   2020/7/7 15:35
 * @Description： 订单详情 改版
 */
@Route(path = ARouterPath.UserModule.NEW_ORDER_DETAIL)
class NewOrderDetailActivity : TitleBarActivity<ActivityNewOrderDetailBinding, OrdersBookViewModel>() {

    @Autowired
    @JvmField
    var orderId: String = ""

    private var orderCode = ""

    override fun setTitle(): String = getString(R.string.order_detail)

    override fun getLayout(): Int = R.layout.activity_new_order_detail

    override fun injectVm(): Class<OrdersBookViewModel> = OrdersBookViewModel::class.java

    override fun initView() {
        mBinding.copyView.onNoDoubleClick {
            Utils.copy(orderCode, getContext())
            ToastUtils.showMessage("复制成功")
        }

        mBinding.appraiseView.onNoDoubleClick {
            if (mBinding.detail!!.orderStatus.toInt() == 4 ||
                mBinding.detail!!.orderStatus.toInt() == 11
            ) {
                mModel.postCancelOrder(orderCode)
            }
        }


    }

    override fun initData() {
        mModel.getOrderDetail(orderId)
        mModel.getUserHealthInfo(orderId)

        //取消结果
        mModel.result.observe(this, Observer {
            mModel.getOrderDetail(orderId)
        })

        //详情
        mModel.orderDetail.observe(this, Observer {
            mBinding.detail = it
            orderCode = it.orderCode

            mBinding.consuCode.text = "电子码：${it.consumeCode}"
            mBinding.ticketNumView.text = "${it.orderNum}张票"
            if (!it.orderQrCode.isNullOrEmpty()) {
                mBinding.urlqrcode = it.orderQrCode.split(",")[0]
            }
            if (!it.image.isNullOrEmpty()) {
                mBinding.urlcover = it.image.split(",")[0]
            }
            if (it.recordList.isNullOrEmpty()) {
                mBinding.hexiaoLayout.visibility = View.GONE
            }
            when (it.orderStatus.toInt()) {
                4 -> {
                    mBinding.orderStateView.text = "待审核"
                    mBinding.appraiseView.text = "取消订单"
                }
                11 -> {
                    mBinding.orderStateView.text = "待消费"
                    mBinding.appraiseView.text = "取消订单"
                }
                12 -> {
                    mBinding.orderStateView.text = "已完成"
                }
                14 -> {
                    mBinding.orderStateView.text = "已取消"
                    mBinding.appraiseView.visibility = View.GONE
                }
            }
            if (it.venueInfo != null) {
                if (it.venueInfo!!.resourceType == ResourceType.PERSON) {
                    mBinding.appointType.text = "个人预约"
                } else {
                    mBinding.appointType.text = "团体预约"
                }
                if (it.venueInfo!!.money.toDouble() == 0.0 && it.venueInfo!!.integral.toInt() == 0) {
                    mBinding.price = getContext().getString(R.string.order_free)
                } else if (it.venueInfo!!.money.toDouble() != 0.0 && it.venueInfo!!.integral.toInt() == 0) {
                    mBinding.price = getContext().getString(R.string.order_yuan) + it.venueInfo!!.money
                } else if (it.venueInfo!!.money.toDouble() == 0.0 && it.venueInfo!!.integral.toInt() != 0) {
                    mBinding.price = it.venueInfo!!.integral + getContext().getString(R.string.order_integral)
                } else {
                    mBinding.price = getContext().getString(R.string.order_yuan) + it.venueInfo!!.money + it.venueInfo!!.integral + getContext().getString(R.string.order_integral)
                }
            }

            var total = ""
            // 判断当前待支付的总价展示方式
            if (it.payMoney == "0.0" && it.payIntegral == "0") {
                total = getContext().getString(R.string.order_free)
            } else if (it.payMoney != "0.0" && it.payIntegral == "0") {
                total = getContext().getString(R.string.order_yuan) + it.payMoney
            } else if (it.payMoney == "0.0" && it.payIntegral != "0") {
                total = it.payIntegral.toString() + getContext().getString(R.string.order_integral)
            } else {
                total = getContext().getString(R.string.order_yuan) + it.payMoney + it.payIntegral + getContext().getString(R.string.order_integral)
            }
            mBinding.costView.text = "支付：${total}"
        })

        //健康码
        mModel.helathInfo.observe(this, Observer {
            mBinding.helath = it
            if (!it.smartTravelRegisterStat) {
                mBinding.registAddres.text = "未注册"
                mBinding.travelCodeState.text = "未注册"
            } else {
                mBinding.registAddres.text = it.regionName
                mBinding.travelCodeState.text = "已注册"
            }
            var drawable: Drawable
            when (it.healthCode) {
                "01" -> {
                    mBinding.healthState.text = "正常"
                    drawable = ResourceUtils.getDrawable(getContext(), R.mipmap.icon_health_normal)
                }
                "11" -> {
                    mBinding.healthState.text = "黄码"
                    drawable = ResourceUtils.getDrawable(getContext(), R.mipmap.icon_health_warn)
                }
                "31" -> {
                    mBinding.healthState.text = "红码"
                    drawable = ResourceUtils.getDrawable(getContext(), R.mipmap.icon_health_danger)
                }
                else -> {
                    mBinding.healthState.text = "未知"
                    drawable = ResourceUtils.getDrawable(getContext(), R.mipmap.icon_health_unkn)
                }
            }
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            mBinding.healthState.setCompoundDrawables(drawable, null, null, null)
            mBinding.travelCodeState.setCompoundDrawables(drawable, null, null, null)
        })
    }
}