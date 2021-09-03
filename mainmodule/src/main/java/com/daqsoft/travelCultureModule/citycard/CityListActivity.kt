package com.daqsoft.travelCultureModule.citycard

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.baselib.widgets.DividerItemDecoration
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityCityListBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.travelCultureModule.citycard.vm.CityCardViewModel

@Route(path = MainARouterPath.MAIN_MDD_LIST)
class CityListActivity : TitleBarActivity<ActivityCityListBinding, CityCardViewModel>() {

    @JvmField
    @Autowired
    var regionId = ""
    @JvmField
    @Autowired
    var toptitle: String = ""
    /**地区县Adapter*/
    private val dqxAdapter: DestinCityAdapter by lazy {
        DestinCityAdapter()
    }

    /**城市Adapter*/
    private val cityAdapter: DestinCityAdapter by lazy {
        DestinCityAdapter()
    }

    override fun getLayout(): Int = R.layout.activity_city_list

    override fun setTitle(): String {
        if(toptitle.isNullOrEmpty()){
            toptitle = getString(R.string.culture_citycard_ls)
        }
        return toptitle;
    }

    override fun injectVm(): Class<CityCardViewModel> = CityCardViewModel::class.java

    override fun initView() {

        mBinding.mgvMddDqx.apply {
            adapter = dqxAdapter
            addItemDecoration(DividerItemDecoration(0, ResourceUtils.getDimension(this, R.dimen.dp_10)))
        }

        mBinding.mgvMddCity.apply {
            adapter = cityAdapter
            addItemDecoration(DividerItemDecoration(0, ResourceUtils.getDimension(this, R.dimen.dp_10)))
        }

        mModel.mddCityList.observe(this, Observer {
            if (it != null) {
                if (it.size > 0) {
                    mBinding.llMddCity.visibility = View.VISIBLE
                    cityAdapter?.clear()
                    cityAdapter.add(it)
                } else {
                    mBinding.llMddCity.visibility = View.GONE
                    mBinding.tvMddCity.visibility = View.GONE
                }
            }
        })
        mModel.mddDQXList.observe(this, Observer {
            if (it != null) {
                if (it.size > 0) {
                    mBinding.llMddDqx.visibility = View.VISIBLE
                    dqxAdapter.clear()
                    dqxAdapter.add(it)
                } else {
                    mBinding.llMddDqx.visibility = View.GONE
                    mBinding.tvMddDqx.visibility = View.GONE
                }
            }
        })
        mModel.siteId.observe(this, Observer {
            mBinding.mSwipeRefreshLayout.finishRefresh()
            var siteId = if (!regionId.isNullOrEmpty()) {
                regionId
            } else {
                it
            }
            if (intent.hasExtra("country")) {
                mBinding.tvMddCity.visibility = View.GONE
                mModel.getMDDCity(siteId, "50", "county", regionId)
            } else {
                if (!intent.hasExtra("dqx")) {
                    mModel.getMDDCity(siteId, "50", "city", regionId)
                } else {
                    mBinding.tvMddCity.visibility = View.GONE
                }
                mModel.getMDDCity(it, "50", "county", regionId)
            }
        })
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
//            mBinding.mSwipeRefreshLayout.isRefreshing = false
            reloadData()
        }
    }

    override fun initData() {
        mModel.refresh()
    }
}