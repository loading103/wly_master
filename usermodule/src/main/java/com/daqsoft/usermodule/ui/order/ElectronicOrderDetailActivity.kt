package com.daqsoft.usermodule.ui.order

import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.daqsoft.baselib.adapter.setImageUrl
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.net.ShopObserver
import com.daqsoft.provider.net.ShopResponse
import com.daqsoft.provider.net.excute
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.*
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.net.excut

/**
 * @des 小电商订单详情页面公共基础页面
 * @author PuHua
 * @Date 2019/12/13 13:52
 */
abstract class ElectronicOrderDetailActivity : TitleBarActivity<ActivityElectronicOrderDetailBinding,
        ElectronicOrderDetailActivityViewModel>() {

    protected var shopUrl = ""

    override fun getLayout(): Int = R.layout.activity_electronic_order_detail

    override fun setTitle(): String = getString(R.string.order_detail)

    override fun injectVm(): Class<ElectronicOrderDetailActivityViewModel> =
        ElectronicOrderDetailActivityViewModel::class.java

    override fun initView() {

    }

    override fun initData() {
    }

    override fun notifyData() {
        super.notifyData()
        mModel.data.observe(this, Observer {
            // 头部状态信息
                transactFragment(R.id.fl_status_information, ElectronicOrderStatusFragment(it))
                addOrderInformationView(it)
        })

        mModel.siteInfo.observe(this, Observer {
            shopUrl = it.shopUrl
        })
    }

    /**
     * 订单信息
     */
    open  abstract fun addOrderInformationView(it: OrderDetailBean?)

    fun addView(target: View, to: FrameLayout) {
        to.removeAllViews()
        to.addView(target)
    }


    /**
     * 商品信息
     */
    open fun shopInfo(it: OrderDetailBean?): ShopShopInfoBinding {
        val inflate = DataBindingUtil.inflate<ShopShopInfoBinding>(layoutInflater, R.layout.shop_shop_info, null, false)
        setImageUrl(inflate.mCoverIv, it?.thumbImageUrl, getDrawable(R.drawable.placeholder_img_fail_h300), 5)
        inflate.mShopNameTv.text = it?.productName
        inflate.mStandardNameTv.text = it?.standardName
        inflate.mNumberValueTv.text = "${it?.productNum}"
        inflate.mRealPayValueTv.text = "￥${it?.orderPayAmount}"
        inflate.mPriceTv.text = "￥${it?.productPrice}"
        inflate.mTotalPriceValueTv.text = "￥${it?.productAmount}"
        //TODO:已申退标签暂未写

//        addView(inflate.root, mBinding.flCommodityInformation)
        return inflate
    }

    /**
     * 收件人信息
     */
    open fun acceptPeopleInfo(it: OrderDetailBean) {

    }
}

/**
 * @des 小电商详情页面的公共基础viewModel
 * @author PuHua
 * @Date 2019/12/13 13:51
 */
open class ElectronicOrderDetailActivityViewModel : BaseViewModel() {

    val repository = ElectronicRepository

    val data = MutableLiveData<OrderDetailBean>()

    val siteInfo = MutableLiveData<SiteInfo>()

    /**
     * 订单详情
     */
    fun orderDetail(id: Int) {
        ElectronicRepository.electronicService
            .orderDetail(id)
            .excute(object : ShopObserver<OrderDetailBean>() {
                override fun onSuccess(response: ShopResponse<OrderDetailBean>) {
                    data.postValue(response.data)
                }
            })
    }
    /**
     * 合同内容
     */
    val contractInfo = MutableLiveData<ContractInfo>()
    fun getContractInfo(id: Int) {
        val map=HashMap<String,String>()
        map["id"]=id.toString()
        ElectronicRepository.electronicService.getContractInfo(map)
            .excut(object : ElectronicObserver<ContractInfo>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ContractInfo>) {
                    contractInfo.postValue(response.data)
                }
            })
    }

    /**获取站点信息*/
    fun getSiteInfo(){
        UserRepository().userService.getSiteInfo()
            .excut(object : ElectronicObserver<SiteInfo>(mPresenter) {
                override fun onSuccess(response: BaseResponse<SiteInfo>) {

                }

                override fun onFailed(response: BaseResponse<SiteInfo>) {
                    super.onFailed(response)
                    if (response.code == 0){
                        siteInfo.postValue(response.data)
                    }
                }
            })
    }


}