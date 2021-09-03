package com.daqsoft.travelCultureModule.citycard

import android.os.Build
import android.util.Log
import android.webkit.WebSettings
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityMywebviewBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.travelCultureModule.citycard.vm.CityCardViewModel

@Route(path = MainARouterPath.MAIN_MY_WEB)
class WebViewActivity : TitleBarActivity<ActivityMywebviewBinding, CityCardViewModel>(){
    override fun getLayout(): Int = R.layout.activity_mywebview

    override fun setTitle(): String =intent.getStringExtra("title")

    override fun injectVm(): Class<CityCardViewModel> =CityCardViewModel::class.java

    override fun initView() {


        var url = intent.getStringExtra("url");
        Log.e("url=", url)
        url = StringUtil.getDqUrl(url!!, SPUtils.getInstance().getString(SPKey.UUID))
        Log.e("web处理url=", url)
        mBinding.myWebview.loadUrl(url)

    }

    override fun initData() {

    }

}