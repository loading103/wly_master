package com.daqsoft.venuesmodule.activity.writeroff

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.network.venues.VenuesRepository
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.adapter.WriterOffOrderAdapter
import com.daqsoft.provider.bean.WriteOffsBean
import com.daqsoft.provider.businessview.event.UpdateWriterOffStatus
import com.daqsoft.venuesmodule.databinding.ActivityVenueWriterOffBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

/**
 * @Description 核销订单列表页
 * @ClassName   VenueWriterOffActivity
 * @Author      luoyi
 * @Time        2020/7/7 9:13
 */
@Route(path = ARouterPath.VenuesModule.VENUE_WRITER_OFF_ACTIIVTY)
class VenueWriterOffActivity :
    TitleBarActivity<ActivityVenueWriterOffBinding, VenueWriterOffViewModel>() {

    /**
     * 倒计时
     */
    private var cutdownDisable: Disposable? = null

    /**资源类型-扫码*/
    @Autowired
    @JvmField
    var resourceType: String = ""

    /**资源Id-扫码*/
    @Autowired
    @JvmField
    var resourceId: String = "0"

    var type: Int = 1

    /**核销适配器*/
    private val orderAdapter by lazy {
        WriterOffOrderAdapter(this)
    }

    override fun getLayout(): Int {
        return R.layout.activity_venue_writer_off
    }

    override fun setTitle(): String {
        return "核销订单"
    }

    override fun injectVm(): Class<VenueWriterOffViewModel> {
        return VenueWriterOffViewModel::class.java
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        mBinding.rvWriterOffOrders.apply {
            adapter = orderAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun initData() {
        mModel.writeOffs.observe(this, Observer {
            if (it.isNullOrEmpty()) {
                mBinding.rvWriterOffOrders.visibility = View.GONE
                mBinding.rllEmpty.visibility = View.VISIBLE
            } else {
                // type 避免陷入死循环
                if (it.size == 1 && type == 1) {
                    // 直接跳转订单详情
                    var item = it[0]
                    if (item != null) {
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.WRITE_OFF_DETAIL)
                            .withString("orderId", item.orderId.toString())
                            .navigation()
                        finish()
                    }
                } else {
                    orderAdapter.clear()
                    orderAdapter.add(it)
                    mBinding.rvWriterOffOrders.visibility = View.VISIBLE
                    mBinding.rllEmpty.visibility = View.GONE
                }
                type = 1

            }
        })

        //核销列表
        mModel.getWriteOffsList(resourceType, resourceId)
    }

    private fun showCutDownView() {
        cutdownDisable?.dispose()
        cutdownDisable = Observable.interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mBinding.tvWriterOffTimer.visibility = View.VISIBLE
                mBinding.tvWriterOffTime.visibility = View.VISIBLE
                mBinding.tvWriterOffTimer.text = DateUtil.getNowTimeString()
                mBinding.tvWriterOffTime.text = DateUtil.getNowWeekTimeString()
            }
    }

    override fun onResume() {
        super.onResume()
        showCutDownView()
    }

    override fun onPause() {
        super.onPause()
        cutdownDisable?.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        cutdownDisable?.dispose()

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun updateWriterOffStatus(event: UpdateWriterOffStatus) {
        if (event != null && mModel != null) {
            type = 2
            mModel.getWriteOffsList(resourceType, resourceId)
        }
    }

}

class VenueWriterOffViewModel : BaseViewModel() {

    /**核销列表*/
    val writeOffs = MutableLiveData<MutableList<WriteOffsBean>>()

    fun getWriteOffsList(resourceType: String, resourceId: String) {
        VenuesRepository.venuesService.getSelfValidOrderList(resourceType, resourceId.toString())
            .excute(object : BaseObserver<WriteOffsBean>() {
                override fun onSuccess(response: BaseResponse<WriteOffsBean>) {
                    writeOffs.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<WriteOffsBean>) {
                    mError.postValue(response)
                }

            })


    }
}