package com.daqsoft.servicemodule.ui

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityArtFoundDetailBinding
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.view.web.WebUtils

@Route(path = ARouterPath.ServiceModule.SERVICE_ART_DETAIL_ACTIVITY)
class ArtFoundDetailActivity  : TitleBarActivity<ActivityArtFoundDetailBinding, BaseViewModel>() {

    @JvmField
    @Autowired(name = "jumpUrl")
    var jumpUrl: String = ""
    //标题
    @JvmField
    @Autowired(name = "jumpTitle")
    var jumpTitle: String = ""

    override fun getLayout(): Int {
        return R.layout.activity_art_found_detail
    }

    override fun injectVm(): Class<BaseViewModel> =BaseViewModel::class.java

    override fun initView() {
        WebUtils.setWebInfo3(mBinding.webViewWeather, jumpUrl)

    }

    override fun initData() {
    }

    override fun setTitle(): String {
        return jumpTitle
    }
}
