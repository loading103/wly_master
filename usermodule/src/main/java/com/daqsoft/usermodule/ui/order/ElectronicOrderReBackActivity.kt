package com.daqsoft.usermodule.ui.order

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityElectronicReBakOrderBinding
import com.daqsoft.provider.bean.OrderRefund
import com.daqsoft.provider.bean.RefundReason
import com.daqsoft.provider.bean.TicketType
import com.daqsoft.usermodule.repository.constant.IntentConstant
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.excut
import com.daqsoft.provider.utils.SoftHideKeyBoardUtil
import com.daqsoft.usermodule.databinding.ElectronicReBackItemPriceBinding
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.toast
import java.text.DecimalFormat
import kotlin.collections.HashMap

/**
 * @Description 小电商退款的页面
 * @ClassName   ElectronicOrderReBackActivity
 * @Author      PuHua
 * @Time        2019/12/16 16:01
 */
@Route(path = ARouterPath.UserModule.USER_ELECTRONIC_REBACK_ACTIVITY)
class ElectronicOrderReBackActivity :TitleBarActivity<ActivityElectronicReBakOrderBinding,
        ElectronicOrderReBackActivityViewModel>(){

    @JvmField
    @Autowired(name=IntentConstant.OBJECT)
    var id: String = ""
    /**
     * 订单类型 1实物订单 2 虚拟订单 3门票订单 5酒店订单 6线路订单
     */
    @JvmField
    @Autowired(name=IntentConstant.TYPE)
    var orderType: String = ""


    override fun getLayout(): Int  = R.layout.activity_electronic_re_bak_order

    override fun setTitle(): String  = getString(R.string.order_electronic_re_back)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoftHideKeyBoardUtil.assistActivity(this)
    }

    override fun injectVm(): Class<ElectronicOrderReBackActivityViewModel>  = ElectronicOrderReBackActivityViewModel::class.java

    override fun initView() {
        showLoadingDialog()
        mModel.refundDada.observe(this,androidx.lifecycle.Observer {
            dissMissLoadingDialog()
            mBinding.consReFound.visibility=View.VISIBLE
            if(it.ticketTypeMap.isNullOrEmpty()){
                mBinding.noNumber.maxNumber = it.allowRefundNum.toInt()
                mBinding.noNumber.number = it.allowRefundNum.toInt()
                mBinding.ivMoney.content = (it.productPrice * it.allowRefundNum.toInt()).toString()
                mBinding.noNumber.textChangeListener = {it1 ->
                    mBinding.ivMoney.content = (it.productPrice * it1).toString()
                }
            }else{
                initList(it.ticketTypeMap)
            }
        })
        mModel.reasons.observe(this,androidx.lifecycle.Observer {
            reasonPv.setPicker(it)
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            finish()
        })
        mModel.toast.observe(this, Observer {
            toast(it)
            finish()
        })

    }

    override fun initData() {
        when(orderType){
            "1","2"->{
                //虚拟 实物
                mModel.applyReFund(id)
            }
            "3"->{
                // 门票
                mModel.applyTicketOrderRefund(id)
            }
            "5"->{
                // 酒店
                mModel.applyHotelOrderRefund(id)
            }
            "6"->{
                // 线路
                mModel.applyRouteOrderRefund(id)
            }
        }
        mModel.getReasons(orderType)
    }

    val list = arrayListOf<TicketType>()
    private fun initList(ticketMap:HashMap<String, TicketType>){
        mBinding.rvPrice.visibility = View.VISIBLE
        mBinding.noNumber.visibility = View.GONE
        mBinding.rvPrice.layoutManager = LinearLayoutManager(this@ElectronicOrderReBackActivity)
        mBinding.rvPrice.adapter = adapter
        for ((k,v) in ticketMap){
            list.add(v)
        }
        adapter.add(list)
        calPrice()
    }


    private fun calPrice(){
        var res = 0.0
        for (item in list){
            res += item.quantity * item.price
        }
        var dsf = DecimalFormat("######0.00")
        mBinding.ivMoney.content = dsf.format(res).toString()
    }


    var adapter = object : RecyclerViewAdapter<ElectronicReBackItemPriceBinding, TicketType>(R.layout.electronic_re_back_item_price) {
        override fun setVariable(mBinding: ElectronicReBackItemPriceBinding, position: Int, item: TicketType) {
            mBinding.tvPriceType.text = item.name
            mBinding.noTypeNum.maxNumber = item.quantity
            mBinding.noTypeNum.number = item.quantity
            mBinding.noTypeNum.textChangeListener = {it ->
                list[position].quantity=it
                calPrice()
            }
        }

    }

    /**
     * 理由选择器
     */
    private val reasonPv by lazy {
        val pV = OptionsPickerBuilder(this, OnOptionsSelectListener { s1, s2, s3, v ->
            mBinding.ivReason.content = mModel.reasons.value!![s1].name
            mModel.selecReason = mModel.reasons.value!![s1]

        }).build<RefundReason>()
        pV
    }

    /**
     * 理由的点击事件
     */
    fun showReasonPv(v: View){
        reasonPv.show()
    }

    /**
     * 提交审核
     */
    fun submit(v:View){
        var ticketPrice = ""
        if(list.size > 0){
            var temp = arrayListOf<TicketType>()
            for (i in list.indices){
                if(list[i].quantity!=0){
                    // 数量为0 ，后台要求不传
                    temp.add(list[i])
                }
            }
            ticketPrice = Gson().toJson(temp)
        }
        mModel.refundRemark = mBinding.etRefundRemark.text.toString()
        mModel.submitApplyRefund(mBinding.noNumber.number.toString(),id,orderType, ticketPrice)
    }
}
/**
 * @des 小电商退款的viewmodel
 * @author PuHua
 * @Date 2019/12/18 9:59
 */
class ElectronicOrderReBackActivityViewModel:BaseViewModel(){

    val refundDada = MutableLiveData<OrderRefund>()

    val reasons = MutableLiveData<MutableList<RefundReason>>()
    /**
     * 选中的理由
     */
    var selecReason: RefundReason? = null

    var refundRemark:String =""
    /**
     * 申请退款
     */
     fun applyReFund(id:String){
        mPresenter.value?.loading = true
        ElectronicRepository.electronicService.applyElectronicReback(id)
            .excut(object : ElectronicObserver<OrderRefund>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderRefund>) {
                    refundDada.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<OrderRefund>) {
                    mError.postValue(response)
                }

            })
    }
    /**
     * 门票申请退款
     */
     fun applyTicketOrderRefund(id:String){
        mPresenter.value?.loading = true
        val map = HashMap<String, String>()
        map["orderId"]=id
        ElectronicRepository.electronicService.applyTicketOrderRefund(map)
            .excut(object : ElectronicObserver<OrderRefund>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderRefund>) {
                    refundDada.postValue(response.data)
                }
                override fun onFailed(response: BaseResponse<OrderRefund>) {
                    mError.postValue(response)
                }
            })
    }
    /**
     * 线路申请退款
     */
     fun applyRouteOrderRefund(id:String){
        mPresenter.value?.loading = true
        val map = HashMap<String, String>()
        map["orderId"]=id
        ElectronicRepository.electronicService.applyRouteOrderRefund(map)
            .excut(object : ElectronicObserver<OrderRefund>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderRefund>) {
                    refundDada.postValue(response.data)
                }
                override fun onFailed(response: BaseResponse<OrderRefund>) {
                    mError.postValue(response)
                }
            })
    }
    /**
     * 酒店申请退款
     */
     fun applyHotelOrderRefund(id:String){
        mPresenter.value?.loading = true
        val map = HashMap<String, String>()
        map["orderId"]=id
        ElectronicRepository.electronicService.applyHotelOrderRefund(map)
            .excut(object : ElectronicObserver<OrderRefund>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderRefund>) {
                    refundDada.postValue(response.data)
                }
                override fun onFailed(response: BaseResponse<OrderRefund>) {
                    mError.postValue(response)
                }
            })
    }

    /**
     * 获取退款理由
     */
    fun getReasons(orderType:String){
        ElectronicRepository.electronicService.getRefunReasons(orderType)
            .excut(object : ElectronicObserver<MutableList<RefundReason>>() {
                override fun onSuccess(response: BaseResponse<MutableList<RefundReason>>) {
                    reasons.postValue(response.data)
                }
            })
    }

    /**
     * 提交申请退款
     */
    fun submitApplyRefund(refundNumber: String, orderId: String, orderType: String,ticketPrice:String = ""){
        mPresenter.value?.loading = true
        if (selecReason==null){
           ToastUtils.showMessage("请选择退款理由")
            return
        }
        if (refundNumber == "0"&& ticketPrice.isNullOrEmpty()){
            ToastUtils.showMessage("请选择退款数量")
            return
        }


        val map=HashMap<String,String>()
        map["refundReasonId"]=selecReason!!.id.toString()
        if(refundNumber != "0"){
            map["refundNum"]=refundNumber
        }
        if(refundRemark.isNotEmpty()){
            map["refundRemark"]=refundRemark
        }
        map["orderId"]=orderId
        if(ticketPrice.isNotEmpty()){
            map["ticketPrice"] = ticketPrice
        }

        map["roomDiffQuantity"]="0"
        map["surcharge"]="[]"
        when(orderType){
            "1","2"->{
                //虚拟 实物
                ElectronicRepository.electronicService.submitRefund(map)
                    .excut(object : ElectronicObserver<Any>(mPresenter) {
                        override fun onSuccess(response: BaseResponse<Any>) {
                            toast.postValue(response.msg)
                        }
                    })
            }
            "3"->{
                // 门票
                ElectronicRepository.electronicService.submitTicketReFound(map)
                    .excut(object : ElectronicObserver<Any>(mPresenter) {
                        override fun onSuccess(response: BaseResponse<Any>) {
                            toast.postValue(response.msg)
                        }
                    })
            }
            "5"->{
                // 酒店
                ElectronicRepository.electronicService.submitHotelReFound(map)
                    .excut(object : ElectronicObserver<Any>(mPresenter) {
                        override fun onSuccess(response: BaseResponse<Any>) {
                            toast.postValue(response.msg)
                        }
                    })
            }
            "6"->{
                // 线路
                val bodyStr = Gson().toJson(map)
                var body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), bodyStr);
                ElectronicRepository.electronicService.submitRouteReFound(body)
                    .excut(object : ElectronicObserver<Any>(mPresenter) {
                        override fun onSuccess(response: BaseResponse<Any>) {
                            toast.postValue(response.msg)
                        }
                    })
            }
        }



    }


}