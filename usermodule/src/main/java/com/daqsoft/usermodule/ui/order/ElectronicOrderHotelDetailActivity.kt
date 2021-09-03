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
import com.daqsoft.usermodule.databinding.LayoutElectronicHotelInformationBinding
import com.daqsoft.provider.bean.OrderDetailBean
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.provider.utils.TimeUtils
import org.jetbrains.anko.toast

/**
 * @des 小电商订单详情页面-- 酒店
 * @author PuHua
 * @Date 2019/12/13 16:52
 */
@Route(path = ARouterPath.UserModule.USER_ELECTRONIC_HOTEL_DETAIL_ACTIVITY)
class ElectronicOrderHotelDetailActivity : ElectronicOrderDetailActivity() {
    override fun addOrderInformationView(it: OrderDetailBean?) {

        val inflate =
            DataBindingUtil.inflate<LayoutElectronicHotelInformationBinding>(layoutInflater, R.layout
                .layout_electronic_hotel_information, null, false)
//        setImageUrl(inflate.mCoverIv, it?.thumbImageUrl, getDrawable(R.drawable.placeholder_img_fail_h300), 5)
        inflate.url = it?.thumbImageUrl
        inflate.mShopNameTv.text = it?.productName
        inflate.mStandardNameTv.text = it?.standardName!!.replace("|","·")
        inflate.mRealPayValueTv.text = "￥${it?.orderPayAmount}"
        inflate.mTotalPriceValueTv.text = "￥${it?.productAmount}"
        if(it?.refundNum!=null&& it?.refundNum!!.toInt()>0){
            inflate.mTagTv.text = getString(R.string.order_has_refund,it?.refundNum)
        }
        if(it!!.orderHotel!=null){
            inflate.tvEnterTime.text = getString(R.string.order_hotel_time_number, TimeUtils.timeStamp2Date(it!!
                .orderHotel!!.inDate.toString(),"yyyy.MM.dd"), TimeUtils.timeStamp2Date(it.orderHotel!!.outDate.toString(),
                "yyyy.MM.dd"), it.orderHotel!!.days.toString())
        }

        inflate.layoutOrderInformation!!.mOrderValueTv.text = it?.orderSn
        inflate.layoutOrderInformation!!.mOrderTimeValueTv.text = it?.gmtCreate?.let { it1 -> Utils.transferLongToDate(Utils.datePattern, it1) }
        // 订单信息
        inflate.layoutOrderInformation!!.mRealMarkValueTv.text = if (!it?.orderRemarks.isNullOrEmpty()){it?.orderRemarks}else{"无"}
        inflate.layoutOrderInformation!!.mCopyTv.setOnClickListener {
            val clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipBoardManager.primaryClip = ClipData.newPlainText(null,  inflate.layoutOrderInformation!!.mOrderValueTv.text
                .toString())
            toast(getString(R.string.order_copy_complete))
        }

        inflate.layoutOrderInformation.mBuyNumberTv.text = getString(R.string.order_enter_time)
       if(it.orderHotel!=null){
           inflate.layoutOrderInformation.mBuyNumberValueTv.text = getString(R.string.order_activity_room_time_stamp, TimeUtils.timeStamp2Date(it!!
               .orderHotel!!.inDate.toString(),"yyyy-MM-dd"),
               TimeUtils.timeStamp2Date(it.orderHotel!!.outDate.toString(),"yyyy-MM-dd"))
           inflate.layoutOrderInformation.mHaveSendTv.text = getString(R.string.order_goods_number_2)
           inflate.layoutOrderInformation.mHaveSendValueTv.text = getString(R.string.order_hotel_number, it.orderHotel!!.days.toString(),it.productNum)
       }



        // 服务商信息
        inflate.layoutService.tvServiceName.text=it.businessTel
        inflate.layoutService.tvServiceLocation.text=it.businessAddress




        if(it!!.hotel == null){
            inflate.layoutService.tvServiceName.visibility = View.GONE
            inflate.layoutService.tvServiceName.visibility = View.GONE
            inflate.layoutService.tvServiceLocation.visibility = View.GONE
        }else{
            inflate.layoutService.name = it!!.hotel!!.hotelName
            inflate.layoutService.location = it!!.hotel!!.areaCodeAddress+","+it!!.hotel!!.address
            inflate.layoutService.tvServiceName.onNoDoubleClick {
                it!!.hotel!!.hotelPhone?.let { it1 -> SystemHelper.callPhone(this, it1) }
            }
            inflate.layoutService.tvServiceLocation.onNoDoubleClick {
                if ( !it!!.hotel!!.latitude.isNullOrEmpty() && !it!!.hotel!!.longitude.isNullOrEmpty()) {
                    if (MapNaviUtils.isGdMapInstalled()) {
                        MapNaviUtils.openGaoDeNavi(
                            BaseApplication.context, 0.0, 0.0, null,
                            it!!.hotel!!.latitude!!.toDouble(), it!!.hotel!!.longitude!!.toDouble(),
                            it!!.hotel!!.areaCodeAddress+","+it!!.hotel!!.address
                        )
                    } else {
                        toast("非常抱歉，系统未安装地图软件")
                    }
                } else {
                    toast("非常抱歉，暂无位置信息")
                }
            }
        }


        // 入住人信息
        inflate.tvContactName.text = it.contactsName
        inflate.tvPhone.content = it.contactsTel

        addView(inflate.root, mBinding.flCommodityInformation)

    }

    @JvmField
    @Autowired
    var id: Int = -1

    override fun initData() {
        super.initData()
        mModel.orderDetail(id)
    }


}
