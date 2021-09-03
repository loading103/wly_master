package com.daqsoft.usermodule.ui.order

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.baselib.widgets.FullyLinearLayoutManager
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.CertTypes
import com.daqsoft.provider.bean.OrderDetail
import com.daqsoft.provider.bean.ValideInfoBean
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityPartWritterOffSuccessBinding
import com.daqsoft.usermodule.ui.order.adapter.MineWriterOffAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   PartWritterOffSuccessActivity
 * @Author      luoyi
 * @Time        2020/8/21 15:12
 */
@Route(path = ARouterPath.UserModule.PART_WRITER_OFF_SUCCESS)
class PartWritterOffSuccessActivity :
    TitleBarActivity<ActivityPartWritterOffSuccessBinding, PartWrutterOffViewModel>() {

    @Autowired
    @JvmField
    var orderId: String = ""
    /**
     * 倒计时
     */
    private var cutdownDisable: Disposable? = null
    private lateinit var detail: OrderDetail
    private val adapter by lazy {
        MineWriterOffAdapter()
    }


    override fun getLayout(): Int {
        return R.layout.activity_part_writter_off_success
    }

    override fun setTitle(): String {
        return "核销成功"
    }

    override fun injectVm(): Class<PartWrutterOffViewModel> {
        return PartWrutterOffViewModel::class.java
    }

    override fun initView() {
        adapter?.emptyViewShow = false
        mBinding.recyclerDetailsMore.adapter = adapter
        mBinding.recyclerDetailsMore.layoutManager = FullyLinearLayoutManager(
            this@PartWritterOffSuccessActivity,
            FullyLinearLayoutManager.VERTICAL,
            false
        )
        mBinding.coverView.onNoDoubleClick {
            if (detail != null && !detail.resourceId.isNullOrEmpty()) {
                MenuJumpUtils.gotoResourceDetail(detail.resourceType, detail.resourceId!!)
            }
        }
        mBinding.titleView.onNoDoubleClick {
            if (detail != null && !detail.resourceId.isNullOrEmpty()) {
                MenuJumpUtils.gotoResourceDetail(detail.resourceType, detail.resourceId!!)
            }
        }
        initViewModel()
    }

    private fun initViewModel() {
        mModel.orderDetail.observe(this, Observer {
            mBinding.detail = it
            detail = it
            if (it.payMoney == "0.0") {
                mBinding.costTypeView.text = ""
                mBinding.costView.text = ""
            } else {
                mBinding.costTypeView.text = it.payMoney
                mBinding.costView.text = "支付：${it.payMoney.toDouble() * it.orderNum.toDouble()}"
            }

            if (!it.images.isNullOrEmpty()) {
                mBinding.urlcover = it.images.split(",")[0]
            }
            if (it.reservationType == ResourceType.PERSON) {
                mBinding.appointType.content = "个人预约"
            } else {
                mBinding.appointType.content = "团队预约"
            }
            mBinding.appointTime.apply {
                content = DateUtil.getTwoDateDayStrs(it.orderStartTime, it.orderEndTime)
            }
            if (!it.useNum.isNullOrEmpty()) {
                mBinding.appointNum.content = "${it.useNum}人"
            }
            if (!it.cardType.isNullOrEmpty()) {
                mBinding.idCardView.setLabel("" + CertTypes.getCertTypeName(it.cardType))
            }
            mBinding.appointExpiry.apply {
                content = DateUtil.getTwoDateDayStrs(it.useStartTime, it.useEndTime)
            }
            mBinding.orderStateView.text = "核销成功！欢迎入园"
            mBinding.labelTitle3.visibility = View.VISIBLE
            mBinding.electronCodeMore.apply {
                visibility = View.VISIBLE
                content = it.code
            }
            if (it.validList.isNullOrEmpty()) {
                mBinding.recyclerDetailsMore.visibility = View.GONE
            } else {
                mBinding.recyclerDetailsMore.visibility = View.VISIBLE
                adapter.clear()
                var temps: MutableList<ValideInfoBean> = mutableListOf()
                temps.add(it.validList!![0])
                adapter.add(temps)
            }
        })
    }

    private fun showCutDownView() {
        cutdownDisable?.dispose()
        cutdownDisable = Observable.interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mBinding.appraiseView.text = DateUtil.getNowTimeString()
                mBinding.orderDateView.text = DateUtil.getNowWeekTimeString()
            }
    }

    override fun initData() {
        mModel.getWriteOffDetail(orderId)
    }

    override fun onResume() {
        super.onResume()
        showCutDownView()
    }

    override fun onPause() {
        super.onPause()
        cutdownDisable?.dispose()
    }

}

class PartWrutterOffViewModel : BaseViewModel() {

    /**订单详情*/
    val orderDetail = MutableLiveData<OrderDetail>()

    /**核销详情*/
    fun getWriteOffDetail(orderId: String) {
        UserRepository().userService.getWriteOffDetail(orderId)
            .excute(object : BaseObserver<OrderDetail>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderDetail>) {
                    orderDetail.postValue(response.data)
                }
            })
    }
}