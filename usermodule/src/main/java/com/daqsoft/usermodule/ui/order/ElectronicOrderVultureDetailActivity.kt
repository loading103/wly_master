package com.daqsoft.usermodule.ui.order

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.ContractInfo
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.*
import com.daqsoft.provider.bean.OrderDetailBean
import com.daqsoft.provider.utils.MapNaviUtils
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast


/**
 * 小电商订单详情页面-- 虚拟商品
 *
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-12-4
 * @since JDK 1.8.0_191
 */
@Route(path = ARouterPath.UserModule.USER_ELECTRONIC_VULTURE_DETAIL_ACTIVITY)
class ElectronicOrderVultureDetailActivity : ElectronicOrderDetailActivity() {


    @JvmField
    @Autowired
    var id: Int = -1

    override fun initData() {
        super.initData()
        mModel.orderDetail(id)
    }


    override fun addOrderInformationView(it: OrderDetailBean?) {
        val inflate =
            DataBindingUtil.inflate<LayoutElectronicVultureInformationBinding>(
                layoutInflater, R.layout
                    .layout_electronic_vulture_information, null, false
            )
//        setImageUrl(inflate.mCoverIv, it?.thumbImageUrl, getDrawable(R.drawable.placeholder_img_fail_h300), 5)
        inflate.layoutService.name=it?.businessAddress?:""
        inflate.url = it?.thumbImageUrl
        inflate.mShopNameTv.text = it?.productName
        if(it?.standardName.isNullOrEmpty()){
            inflate.mStandardTv.visibility = View.GONE
        }else{
            inflate.mStandardNameTv.text = it?.standardName
        }
        inflate.mNumberValueTv.text = "${it?.productNum}"
        inflate.mRealPayValueTv.text = "￥${it?.orderPayAmount}"
        inflate.mPriceTv.text = "￥${it?.productPrice}"
        inflate.mTotalPriceValueTv.text = "￥${it?.productAmount}"
        if(it?.refundNum!=null&& it?.refundNum!!.toInt()>0){
            inflate.mTagTv.visibility = View.VISIBLE
            inflate.mTagTv.text = getString(R.string.order_has_refund,it?.refundNum)
        }
        inflate.mNameTv.text = it?.contactsName
        inflate.pohone = it?.contactsTel
        inflate.idCard = it?.contactsCredentials


        inflate.bookingRemark.visibility = if (it?.contactsCredentials!!.isNotEmpty()) View.VISIBLE else View
            .GONE

        // 订单信息
        inflate.layoutOrderInformation.mOrderValueTv.text = it?.orderSn
        inflate.layoutOrderInformation.mOrderTimeValueTv.text = it?.gmtCreate?.let { it1 -> Utils.transferLongToDate(Utils.datePattern, it1) }
        inflate.layoutOrderInformation.mRealMarkValueTv.text = if (!it?.orderRemarks.isNullOrEmpty()){it?.orderRemarks}else{"无"}
        inflate.layoutOrderInformation.mCopyTv.setOnClickListener {
            val clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipBoardManager.primaryClip = ClipData.newPlainText(
                null,  inflate.layoutOrderInformation.mOrderValueTv.text
                    .toString()
            )
            toast(getString(R.string.order_copy_complete))

        }
        inflate.layoutOrderInformation.mHaveSendTv.text = getString(R.string.order_cost_number)
        inflate.layoutOrderInformation.mHaveSendValueTv.text = "${it?.consumeProductNum}"
        inflate.layoutOrderInformation.mBuyNumberValueTv.text = "${it?.productNum}"

        // 服务商信息
        inflate.layoutService.tvServiceName.text=it.businessTel
        inflate.layoutService.tvServiceLocation.text=it.businessAddress
        inflate.layoutService.tvServiceName.visibility = if (it!!.businessName == null) View.GONE else
            View.VISIBLE

        inflate.layoutService.tvServiceName.onNoDoubleClick {
            it.businessTel?.let { it1 -> SystemHelper.callPhone(this, it1) }?:toast("电话号码不存在!")
        }
        inflate.layoutService.tvServiceLocation.onNoDoubleClick {
            if (it.businessAddress != null && !it.businessLatitude.isNullOrEmpty() && !it?.businessLongitude.isNullOrEmpty()) {
                if (MapNaviUtils.isGdMapInstalled()) {
                    MapNaviUtils.openGaoDeNavi(
                        BaseApplication.context, 0.0, 0.0, null,
                        it.businessLatitude!!.toDouble(), it.businessLongitude!!.toDouble(),
                        it.businessAddress
                    )
                } else {
                    toast("非常抱歉，系统未安装地图软件")
                }
            } else {
                toast("非常抱歉，暂无位置信息")
            }
        }

        inflate.layoutService.tvServiceLocation.visibility =
            if (it!!.businessAddress == null) View.GONE else View.VISIBLE
        inflate.layoutService.name = it?.businessName
        inflate.layoutService.location = it?.businessAddress
        // 是否限制预约操作时间 0不限制 1限制
        inflate.layoutOrderInformation.isNeedBooking = it.needBookingTime != "0"
        inflate.layoutOrderInformation.bean = it
        addView(inflate.root, mBinding.flCommodityInformation)

    }


}
