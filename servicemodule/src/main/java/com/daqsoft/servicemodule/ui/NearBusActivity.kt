package com.daqsoft.servicemodule.ui

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityNearBusBinding
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.servicemodule.adapter.NearBusAdapter
import com.daqsoft.servicemodule.adapter.TourGuideAdapter
import com.daqsoft.servicemodule.model.NearBusViewModel

/**
 * desc :附近公交
 * @author 江云仙
 * @date 2020/4/3 11:30
 */
@Route(path = ARouterPath.ServiceModule.SERVICE_NEAR_BUS_ACTIVITY)
class NearBusActivity : TitleBarActivity<ActivityNearBusBinding, NearBusViewModel>() {
    @JvmField
    @Autowired
    var currentLat = ""//当前位置经纬度
    @JvmField
    @Autowired
    var currentLon = ""
    override fun getLayout(): Int {
        return R.layout.activity_near_bus
    }

    override fun setTitle(): String {
        return "附近公交站"
    }

    override fun injectVm(): Class<NearBusViewModel> = NearBusViewModel::class.java

    override fun initView() {
        mBinding?.recyNearBus.visibility = View.GONE
        setAdapter()
    }

    /**
     *设置适配器
     */
    private fun setAdapter() {
        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.recyNearBus.layoutManager = tagLayoutManager
        val  newBusAdapter = NearBusAdapter()
        mBinding.recyNearBus.adapter=newBusAdapter
        mModel.result.observe(this, Observer {
            mBinding?.recyNearBus.visibility = View.VISIBLE
            var data=it
            newBusAdapter.add(data)
            newBusAdapter.notifyDataSetChanged()
        })
    }

    override fun initData() {
        mBinding.currentLat = currentLat
        mBinding.currentLon = currentLon
        mModel.currentLat = currentLat
        mModel.currentLon = currentLon
        mModel.getTravelAgencyList()
    }
}
