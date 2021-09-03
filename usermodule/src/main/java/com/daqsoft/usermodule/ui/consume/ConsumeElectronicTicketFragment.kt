package com.daqsoft.usermodule.ui.consume

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.MediatorLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.ElectronicDetailBean
import com.daqsoft.provider.bean.ElectronicQrCode
import com.daqsoft.provider.bean.OrderTicketTourist
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.excut
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.provider.utils.TimeUtils
import com.daqsoft.provider.view.dialog.ProviderTipDialog
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FragmentConsumeDetailBinding
import com.daqsoft.usermodule.databinding.ItemElectronicPersonBinding
import io.reactivex.Observer
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.support.v4.toast

/**
 * @des 小电商的消费码的详情fragment
 * @author PuHua
 * @date
 */
class ConsumeElectronicTicketFragment() : BaseFragment<FragmentConsumeDetailBinding, ElectronicTicketDetailActivityViewModel>() {

    var bean: ElectronicDetailBean? = null

    constructor(dr: ElectronicDetailBean) : this() {
        this.bean = dr
    }

    override fun getLayout(): Int = R.layout.fragment_consume_detail

    override fun injectVm(): Class<ElectronicTicketDetailActivityViewModel> = ElectronicTicketDetailActivityViewModel::class.java

    @SuppressLint("StringFormatMatches")
    override fun initView() {
        mBinding.mRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?
        mBinding.mRecyclerView.adapter = adapter

        if (bean != null){
            if (bean!!.businessDto!=null){
                mBinding.name = bean!!.businessDto.name
            }
            mBinding.productName = bean!!.productName
            mBinding.address = bean!!.standardName

            if (bean!!.useStartTime!=null&&bean!!.useEndTime!=null) {
                mBinding.time = bean!!.canRefundDate
            } else {
                mBinding.time = "永久有效"
            }
            mBinding.tvNumber.text = "X${bean!!.productNum}"
            mBinding.image = bean!!.thumbImageUrl
            if (bean!!.useStartTime!=null)
            mBinding.startTime =  TimeUtils.timeStamp2Date(bean!!.useStartTime)
            if(bean!!.useEndTime!=null)
            mBinding.endTime =  TimeUtils.timeStamp2Date(bean!!.useEndTime)
        }
        if (bean!!.orderTicketTouristDtoList!=null){
            adapter.add(bean!!.orderTicketTouristDtoList as MutableList<OrderTicketTourist>)
        }else{
            mBinding.label.visibility = View.GONE
            mBinding.mRecyclerView.visibility = View.GONE
        }


        when (bean!!.orderType) {

        }
        var phone = ""
        var address = ""
        var latitude = ""
        var longitude = ""
        var name = ""
        if(bean!!.scenicResourcesDto!=null){
            phone = bean!!.scenicResourcesDto.scenicPhone
            name = bean!!.scenicResourcesDto.scenicName
            latitude = bean!!.scenicResourcesDto!!.scenicLatitude
            longitude = bean!!.scenicResourcesDto.scenicLongitude
            address = bean!!.scenicResourcesDto.scenicAddress
        }
        if(bean!!.businessDto!=null){
            phone = bean!!.businessDto.tel
            name = bean!!.businessDto.name
            latitude = bean!!.businessDto!!.latitude
            longitude = bean!!.businessDto.longitude
            address = bean!!.businessDto.address
        }

        mBinding.tvVenueName.text = name
        if(phone.isNotEmpty()){
            mBinding.tvPhone.onNoDoubleClick {
                val dialog = ProviderTipDialog.Builder()
                    .setContent("拨打电话：${phone}")
                    .setOnTipConfirmListener(object : ProviderTipDialog.OnTipConfirmListener{
                        override fun onConfirm() {
                            SystemHelper.callPhone(context!!, phone)
                        }
                    }).create(context!!)
                dialog.show()
            }
        }else{
            mBinding.tvPhone.visibility = View.GONE
        }
        if(latitude.isNotEmpty()){

            mBinding.tvGuide.onNoDoubleClick {
                if (MapNaviUtils.isGdMapInstalled()) {
                    MapNaviUtils.openGaoDeNavi(
                        BaseApplication.context, 0.0, 0.0, null,
                        latitude.toDouble(), longitude.toDouble(),
                        address
                    )
                } else {
                    toast("非常抱歉，系统未安装地图软件")
                }
            }
        }else{
            mBinding.tvGuide.visibility = View.GONE
        }
    }

    override fun initData() {

    }

    fun setQrCode(code: String){
        adapter.setQrCodeUrl(code)
    }

    private val adapter = object : RecyclerViewAdapter<ItemElectronicPersonBinding, OrderTicketTourist>(R.layout.item_electronic_person) {

        private var qrCode = ""

        fun setQrCodeUrl(url: String){
            this.qrCode = url
            notifyDataSetChanged()
        }

        @SuppressLint("CheckResult")
        override fun setVariable(
            mBinding: ItemElectronicPersonBinding,
            position: Int,
            item: OrderTicketTourist
        ) {
            mBinding.name = item.touristName
            mBinding.idNumber = item.credentialsNumber

            if (!qrCode.isNullOrEmpty()){
                Glide.with(mBinding.ivQr)
                    .load(base64ToImage(qrCode))
                    .into(mBinding.ivQr)
            }

            mBinding.phone = item.touristMobile
            if(item.consumeDate!=null){
                mBinding.time = TimeUtils.timeStamp2Date(item.consumeDate.toString())
            }else{
                mBinding.tvTime.visibility = View.GONE
            }
            mBinding.number = item.consumeNum
            when(item.consumeStatus){
                // 0待支付 1已取消 2已支付 3驳回预约 4确认预约 5已过期 6已退款
                "CANCEL"->{
                    mBinding.status = context!!.getString( R.string.order_canceled)
                    mBinding.tvStatus.backgroundResource = R.drawable.user_back_gray_strok_gray_round_large
                }
                "NORMAL"->{
                    mBinding.status = context!!.getString( R.string.order_waite_cost)
                }
                "USED"->{
                    mBinding.status = "已使用"
                }
                "AUDITING"->{
                    if(bean!!.refundStatus){
                        mBinding.status = "退款中"
                    }else{
                        mBinding.status = "审核中"
                    }
                }
                "INVALID"->{
                    mBinding.status = "已失效"
                    mBinding.tvStatus.backgroundResource = R.drawable.user_back_gray_strok_gray_round_large
                }
            }
        }

        private fun base64ToImage(imgStr: String): Bitmap? {
            // Base64解码
            val s =imgStr.split(",")[1]
            val bytes = Base64.decode(s.toByteArray(), Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)


        }
    }
}
