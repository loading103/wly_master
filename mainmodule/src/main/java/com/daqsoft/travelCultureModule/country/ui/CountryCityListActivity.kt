package com.daqsoft.travelCultureModule.country.ui

import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityCountryCityListBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.travelCultureModule.country.adapter.CountryCityAdapter
import com.daqsoft.travelCultureModule.country.model.CountryCityCardViewModel

/**
 * desc :乡村游旅游名片
 * @author 江云仙
 * @date 2020/4/29 11:11
 */
@Route(path = ARouterPath.CountryModule.COUNTRY_CITY_LIST_ACTIVITY)
class CountryCityListActivity : TitleBarActivity<ActivityCountryCityListBinding, CountryCityCardViewModel>() {
    @Autowired
    @JvmField
    var siteId = ""
    override fun getLayout(): Int = R.layout.activity_country_city_list

    override fun setTitle(): String = "乡村旅游名片"

    override fun injectVm(): Class<CountryCityCardViewModel> = CountryCityCardViewModel::class.java

    override fun initView() {
        mModel.mddCityList.observe(this, Observer {
            if (it != null) {
                if (it.size > 0) {
                    mBinding.emptyView.visibility = View.GONE
                    mBinding.mSwipeRefreshLayout.visibility = View.VISIBLE
                    mBinding.llMddCity.visibility = View.VISIBLE
                    mBinding.mgvMddCity.numColumns = 2
                    val adapter = CountryCityAdapter(it)
                    mBinding.mgvMddCity.adapter = adapter
                } else {
                    mBinding.llMddCity.visibility = View.GONE
                    mBinding.tvMddCity.visibility = View.GONE
                    mBinding.mSwipeRefreshLayout.visibility = View.GONE
                    mBinding.emptyView.visibility = View.VISIBLE
                }
            }
        })
        mModel.mddDQXList.observe(this, Observer {
            if (it != null) {
                if (it.size > 0) {
                    mBinding.emptyView.visibility = View.GONE
                    mBinding.mSwipeRefreshLayout.visibility = View.VISIBLE
                    mBinding.llMddDqx.visibility = View.VISIBLE
                    mBinding.mgvMddDqx.numColumns = 2
                    var adapter = CountryCityAdapter(it)
                    mBinding.mgvMddDqx.adapter = adapter
                } else {
                    mBinding.llMddDqx.visibility = View.GONE
                    mBinding.tvMddDqx.visibility = View.GONE
                    mBinding.mSwipeRefreshLayout.visibility = View.GONE
                    mBinding.emptyView.visibility = View.VISIBLE
                }
            }
        })
        mModel.getMDDCity(siteId, "50", "city")
        mModel.getMDDCity(siteId, "50", "county")
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
//            mBinding.mSwipeRefreshLayout.isRefreshing = false
            reloadData()
        }
    }

    override fun initData() {
    }
}