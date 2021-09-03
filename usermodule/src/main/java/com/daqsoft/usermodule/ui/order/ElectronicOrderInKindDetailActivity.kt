package com.daqsoft.usermodule.ui.order

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.ContractInfo
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.LayoutElectronicInKindInformationBinding
import com.daqsoft.provider.bean.OrderDetailBean
import org.jetbrains.anko.toast

/**
 * 小电商订单详情（实物）包括 订单状态，收件人信息，商品信息，订单信息
 *
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-12-5
 * @since JDK 1.8.0_191
 */
@Route(path = ARouterPath.UserModule.USER_ELECTRONIC_IN_KIND_DETAIL_ACTIVITY)
class ElectronicOrderInKindDetailActivity : ElectronicOrderDetailActivity() {
    @JvmField
    @Autowired
    var id: Int = -1

    override fun initData() {
        super.initData()
        mModel.orderDetail(id)
    }

    override fun addOrderInformationView(it: OrderDetailBean?) {
        val inflate =
            DataBindingUtil.inflate<LayoutElectronicInKindInformationBinding>(layoutInflater, R.layout.layout_electronic_in_kind_information, null, false)
//        setImageUrl(inflate.mCoverIv, it?.thumbImageUrl, getDrawable(R.drawable.placeholder_img_fail_h300), 5)
        inflate.url = it?.thumbImageUrl
        inflate.mFreightValueTv.text = "￥${it?.freightAmount}"
        inflate.mShopNameTv.text = it?.productName
        inflate.mStandardNameTv.text = it?.standardName
        inflate.mNumberValueTv.text = "${it?.productNum}"
        inflate.mRealPayValueTv.text = "￥${it?.orderPayAmount}"
        inflate.mPriceTv.text = "￥${it?.productPrice}"
        inflate.mTotalPriceValueTv.text = "￥${it?.productAmount}"

        inflate.tvCustomerName.text = it?.contactsName
        inflate.tvPhone.text = it?.contactsTel
        inflate.tvAddress.text = it?.contactsAddress
        if(it?.refundNum!=null&& it?.refundNum!!.toInt()>0){
            inflate.mTagTv.text = getString(R.string.order_has_refund,it?.refundNum)
        }

        inflate.layoutOrderInformation.mOrderValueTv.text = it?.orderSn
        inflate.layoutOrderInformation.mOrderTimeValueTv.text = it?.gmtCreateStr


        inflate.layoutOrderInformation.mRealMarkValueTv.text = if (!it?.orderRemarks.isNullOrEmpty()){it?.orderRemarks}else{"无"}
        inflate.layoutOrderInformation.mCopyTv.setOnClickListener {
            val clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipBoardManager.primaryClip = ClipData.newPlainText(null,  inflate.layoutOrderInformation.mOrderValueTv.text
                    .toString())
            toast(getString(R.string.order_copy_complete))
        }
        inflate.layoutOrderInformation.mHaveSendTv.text = getString(R.string.order_shipped_number)
        inflate.layoutOrderInformation.mHaveSendValueTv.text = "${it?.deliveryProductNum}"
        inflate.layoutOrderInformation.mBuyNumberValueTv.text = "${it?.needDeliveryProductNum}"

        addView(inflate.root, mBinding.flCommodityInformation)

    }

}