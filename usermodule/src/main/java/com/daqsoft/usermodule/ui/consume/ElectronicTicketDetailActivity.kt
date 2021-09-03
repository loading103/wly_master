package com.daqsoft.usermodule.ui.consume

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.ElectronicDetailBean
import com.daqsoft.provider.bean.ElectronicQrCode
import com.daqsoft.provider.bean.HelathInfoBean
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.net.excut
import com.daqsoft.provider.view.BasePopupWindow
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityConsumeDetailBinding
import com.daqsoft.usermodule.databinding.PopupConsumeTicketIntroduceBinding
import com.daqsoft.usermodule.repository.constant.IntentConstant
import org.jetbrains.anko.image


/**
 * 消费码详情页面--小电商
 */
@Route(path = ARouterPath.UserModule.USER_ELECTRONIC_TICKET_DETAIL)
class ElectronicTicketDetailActivity :
    TitleBarActivity<ActivityConsumeDetailBinding, ElectronicTicketDetailActivityViewModel>() {

    @JvmField
    @Autowired(name = IntentConstant.OBJECT)
    var id: String? = null
    @JvmField
    @Autowired(name = IntentConstant.TYPE)
    var type: String? = null

    var popupWindow:BasePopupWindow? = null

    var bean:ElectronicDetailBean? = null
    /**
     * 下部的fragment
     */
    var consumeElectronicTicketFragment: ConsumeElectronicTicketFragment? = null

    override fun getLayout(): Int = R.layout.activity_consume_detail

    override fun setTitle(): String = getString(R.string.order_consume_detail)

    override fun injectVm(): Class<ElectronicTicketDetailActivityViewModel> =
        ElectronicTicketDetailActivityViewModel::class.java

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {

        mModel.detail.observe(this, Observer {
            mBinding.code = it.consumeCode
            bean = it

            if(it.orderTicketInfoDto != null){
                if(it.orderTicketInfoDto.guideTips.isNotEmpty()){
                    mBinding.tvGuideTip.text = it.orderTicketInfoDto.guideTips
                }
                initPopuWindow()
            }else{
                mBinding.tvView.visibility = View.GONE
                mBinding.tvGuideTip.visibility = View.GONE

            }
            var timeContent = ""

            when (it.orderStatus) {
                // 退款中
                10->{
                    mBinding.bookingTime.visibility = View.GONE
                  if(it.refundStatus){
                      mBinding.status.visibility = View.VISIBLE
                      mBinding.status.image = getDrawable(R.mipmap.mine_code_detail_tag_big_tuikuanzhong)
                  }

                }
                // 已消费
                11 -> {
                    mBinding.status.visibility = View.VISIBLE
                    mBinding.bookingTime.setLabel(getString(R.string.order_consume_use_time))
                    mBinding.status.image = getDrawable(R.mipmap.mine_code_detail_tag_big_yihexiao)
                    timeContent = it.reservationUseTime

                }
                // 已失效
                12 -> {
                    if(it.bookingTimeStart!=null){
                        mBinding.bookingTime.setLabel(getString(R.string.order_consume_effect_time))
                        timeContent = getString(
                            R.string.order_activity_room_time_stamp_, it.bookingTimeStart, it
                                .bookingTimeEnd
                        )
                    }else{
                        mBinding.bookingTime.visibility = View.GONE
                    }
                    mBinding.status.visibility = View.VISIBLE
                    mBinding.status.image = getDrawable(R.mipmap.mine_code_detail_tag_big_yishixiao)
                }
                1,2 ->{
                    mBinding.status.visibility = View.GONE
                    mBinding.bookingTime.visibility = View.GONE
                }
                else -> {
                    mBinding.status.visibility = View.VISIBLE
                    mBinding.bookingTime.visibility = View.GONE
                    mBinding.bookingTime.setLabel(getString(R.string.order_consume_effect_time))
                    mBinding.status.image = getDrawable(R.mipmap.mine_code_detail_tag_big_yihexiao)
                    timeContent = getString(
                        R.string.order_activity_room_time_stamp_, it.bookingTimeStart, it
                            .bookingTimeEnd
                    )
                }
            }
            mBinding.content = timeContent
            consumeElectronicTicketFragment = ConsumeElectronicTicketFragment(it)
            transactFragment(R.id.fragment_information,consumeElectronicTicketFragment!!)
        })
        mModel.electronicQrCode.observe(this, Observer {
            mBinding.image.scaleType = ImageView.ScaleType.FIT_XY
            consumeElectronicTicketFragment?.setQrCode(it.qrCode)
            Glide.with(mBinding.image)
                .load(base64ToImage(it.qrCode))
                .into(mBinding.image)
        })
        mBinding.tvView.onNoDoubleClick {
            if (popupWindow != null) {
                popupWindow!!.resetDarkPosition()
                popupWindow!!.darkAbove(mBinding.tvView)
                popupWindow!!.showAtLocation(mBinding.root,Gravity.BOTTOM,0,0)
            }
        }
    }

    override fun initData() {
        if (id != null) {
            // TODO：type值到底是啥不确定
            mModel.getOrderDetail(id!!, type?:"1")
        }
    }

    /**
     * 对字节数组字符串进行Base64解码并生成图片
     */
    private fun base64ToImage(imgStr: String): Bitmap? {
        // Base64解码
        val s =imgStr.split(",")[1]
        val bytes = Base64.decode(s.toByteArray(),Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun initPopuWindow(){
        // 筛选
        val inflater = LayoutInflater.from(this@ElectronicTicketDetailActivity)
        val selectBinding: PopupConsumeTicketIntroduceBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.popup_consume_ticket_introduce,
            null,
            false
        )
        popupWindow = BasePopupWindow(selectBinding.root, LinearLayout.LayoutParams.MATCH_PARENT,
            Utils.dip2px(this@ElectronicTicketDetailActivity,500.toFloat()).toInt(), true)
        popupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow!!.isOutsideTouchable = true
        // 设置能否响应点击事件
        popupWindow!!.isTouchable = true
        popupWindow!!.isFocusable = true
        popupWindow!!.resetDarkPosition()
        popupWindow!!.darkAbove(mBinding.tvView)
       if(bean!!.orderTicketInfoDto!= null){
           selectBinding.tvMoneyInfo.text = bean!!.orderTicketInfoDto.costIncludes
           selectBinding.ivRypz.content =  when(bean!!.orderTicketInfoDto.exchangeVoucherType){
               "sms"->"短信"
               "idcard"->"身份证"
               "sms_and_idcard"->"短信和身份证"
               else -> "电子邮件"
           }
           selectBinding.ivRyfs.content = when(bean!!.orderTicketInfoDto.passType){
               "virtual"->"凭兑换凭证直接入园"
               else ->"凭兑换凭证换票"
           }
           selectBinding.tvTitle.text = bean!!.productName
           selectBinding.ivRycs.content = if(bean!!.orderTicketInfoDto.perdayCheckTimes == "1") "单次入园" else "多次入园"
           selectBinding.ivRydz.content = bean!!.orderTicketInfoDto.passAddress
           selectBinding.ivRyrs.content = if(bean!!.orderTicketInfoDto.adultTicket == "1") "一码1人" else "无限制"
           selectBinding.ivRyxz.content = bean!!.orderTicketInfoDto.enterSightLimit
       }
    }

}

/**
 * 消费码详情页面的viewModel--小电商
 */
class ElectronicTicketDetailActivityViewModel : BaseViewModel() {
    var detail = MediatorLiveData<ElectronicDetailBean>()

    var electronicQrCode = MediatorLiveData<ElectronicQrCode>()

    /**健康信息*/
    val healthInfoLd: MutableLiveData<HelathInfoBean> = MutableLiveData()

    /**
     * 获取详情
     */
    fun getOrderDetail(orderId: String, type: String) {
        mPresenter.value?.loading = true
        ElectronicRepository.electronicService.getElectronicTicketDetail(orderId,type)
            .excut(object : ElectronicObserver<ElectronicDetailBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ElectronicDetailBean>) {
                    val detailBean = response.data
                    detail.postValue(detailBean)
                    var consumeCode = ""
                    var type = when (detailBean!!.orderType) {
                        3 -> {
                            consumeCode = detailBean!!.consumeCode
                            "ticket"
                        }
                        2 -> "order"
                        else -> "booking"
                    }

                    getQrCode(orderId, type, consumeCode)
                }
            })
    }

    fun getQrCode(orderId: String, type: String, consumeCode: String) {

        mPresenter.value?.loading = true
        ElectronicRepository.electronicService.getElectronicTicketQRCode(orderId, type, consumeCode)
            .excut(object : ElectronicObserver<ElectronicQrCode>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ElectronicQrCode>) {
                    electronicQrCode.postValue(response.data)
                }
            })
    }

    /**通过订单获取健康信息*/
    fun getUserHealthInfo(orderId: String) {
        UserRepository().userService.getUserHealthInfo(orderId)
            .excute(object : BaseObserver<HelathInfoBean>() {
                override fun onSuccess(response: BaseResponse<HelathInfoBean>) {
                    healthInfoLd.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<HelathInfoBean>) {
                    super.onFailed(response)
                    healthInfoLd.postValue(null)
                    Log.e("消费码详情", response.message)
                }
            })
    }
}