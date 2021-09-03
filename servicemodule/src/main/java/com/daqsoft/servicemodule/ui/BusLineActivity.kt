package com.daqsoft.servicemodule.ui

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityBusLineBinding
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.servicemodule.adapter.BusLineAdapter
import com.daqsoft.servicemodule.bean.BusAddressBean
import com.daqsoft.servicemodule.model.BusLineViewModel

/**
 * desc :公交线路列表
 * @author 江云仙
 * @date 2020/4/3 17:41
 */
@Route(path = ARouterPath.ServiceModule.SERVICE_BUS_LINE_ACTIVITY)
class BusLineActivity : TitleBarActivity<ActivityBusLineBinding, BusLineViewModel>() {
    @JvmField
    @Autowired(name = "startAddressBean")
    var startAddressBean: BusAddressBean? = null
    @JvmField
    @Autowired(name = "endAddress")
    var endAddress: String=""
    @JvmField
    @Autowired(name = "startAddress")
    var startAddress: String=""
    @JvmField
    @Autowired(name = "endAddressBean")
    var endAddressBean: BusAddressBean? = null//

    override fun getLayout(): Int {
        return R.layout.activity_bus_line
    }

    override fun setTitle(): String {
        return "查询结果"
    }

    override fun injectVm(): Class<BusLineViewModel> = BusLineViewModel::class.java
    override fun initView() {
        mBinding?.recyBusLine.visibility = View.GONE
        mBinding.startAddress = startAddress
//        mBinding.endAddress = endAddressBean
        mModel.city = startAddressBean?.city ?: ""
        mModel.cityd = endAddressBean?.city ?: ""
        mModel.origin = startAddressBean?.location ?: ""
        mModel.destination = endAddressBean?.location ?: ""
        setAdapter()


    }

    /**
     *设置适配器
     */
    private fun setAdapter() {
        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.recyBusLine.layoutManager = tagLayoutManager
        val  busLineAdapter = BusLineAdapter(startAddress,endAddress)
        mBinding.recyBusLine.adapter=busLineAdapter
        mModel.result.observe(this, Observer {
            mBinding?.recyBusLine.visibility = View.VISIBLE
            val data=it.transits
            if (data!=null){
                busLineAdapter.add(data)
                busLineAdapter.notifyDataSetChanged()
            }

        })

    }

    override fun initData() {
        mModel.getBusLineUrlList()
    }
}
