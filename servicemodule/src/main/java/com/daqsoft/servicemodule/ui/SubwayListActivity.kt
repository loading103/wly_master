package com.daqsoft.servicemodule.ui

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ActivitySubwayListBinding
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.servicemodule.adapter.SubWayListAdapter
import com.daqsoft.servicemodule.model.SubWayListViewModel

/**
 * desc :汽车列表查询
 * @author 江云仙
 * @date 2020/4/9 11:01
 */
@Route(path = ARouterPath.ServiceModule.SERVICE_SUBWAY_LIST_ACTIVITY)
class SubwayListActivity : TitleBarActivity<ActivitySubwayListBinding, SubWayListViewModel>() {
    @JvmField
    @Autowired
    var startCityName = ""//开始时间
    @JvmField
    @Autowired
    var endCityName = ""//结束时间
    override fun getLayout(): Int {
        return R.layout.activity_subway_list
    }

    override fun setTitle(): String {
        return "$startCityName-$endCityName"
    }

    override fun injectVm(): Class<SubWayListViewModel> =SubWayListViewModel::class.java

    override fun initView() {
        mBinding.recySubway.visibility = View.GONE
        setAdapter()
    }
    /**
     *设置适配器
     */
    private fun setAdapter() {
        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.recySubway.layoutManager = tagLayoutManager
        val  subWayListAdapter = SubWayListAdapter()
        mBinding.recySubway.adapter=subWayListAdapter
        mModel.result.observe(this, Observer {
            mBinding.recySubway.visibility = View.VISIBLE
            var data=it
            subWayListAdapter.clear()
            subWayListAdapter.add(data)
            subWayListAdapter.notifyDataSetChanged()
        })
    }
    override fun initData() {
        mModel.startCity=startCityName
        mModel.endCity=endCityName
        mModel.getSubWayListName()
    }
}
