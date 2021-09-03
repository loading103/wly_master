package com.daqsoft.servicemodule.ui

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityWeatherQueryBinding
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.view.web.WebUtils
import com.daqsoft.servicemodule.QUERY_EXAM
import com.daqsoft.servicemodule.WEB_EXAM
import com.daqsoft.servicemodule.WEB_WEATHER

/**
 * desc :天气查询和查艺考
 * @author 江云仙
 * @date 2020/4/9 19:42
 */
@Route(path = ARouterPath.ServiceModule.SERVICE_WEATHER_QUERY_ACTIVITY)
class WeatherQueryActivity : TitleBarActivity<ActivityWeatherQueryBinding, BaseViewModel>() {
    @JvmField
    @Autowired(name = "jumpType")
    var jumpType: String = ""

    @JvmField
    @Autowired(name = "jumpTitle")
    var jumpTitle: String = ""

    override fun getLayout(): Int {
        return R.layout.activity_weather_query
    }



    override fun initView() {
        WebUtils.setWebInfo3(mBinding.webViewWeather, jumpType?:"")


    }

    override fun initData() {
    }

    override fun setTitle(): String {
        return jumpTitle
    }

    override fun injectVm(): Class<BaseViewModel> =BaseViewModel::class.java
}
