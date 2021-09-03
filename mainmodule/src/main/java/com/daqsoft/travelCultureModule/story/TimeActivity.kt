package com.daqsoft.travelCultureModule.story

import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseActivity
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityTimeBinding
import com.daqsoft.provider.MainARouterPath

/**
 *@package:com.daqsoft.travelCultureModule.story
 *@date:2020/4/13 10:20
 *@author: caihj
 *@des:时光页面
 **/
@Route(path = MainARouterPath.MAIN_TIME)
class TimActivity : TitleBarActivity<ActivityTimeBinding, TimeViewModel>() {
    override fun getLayout(): Int = R.layout.activity_time

    override fun setTitle(): String = getString(R.string.time_title)

    override fun injectVm(): Class<TimeViewModel> = TimeViewModel::class.java

    override fun initView() {

    }

    override fun initData() {

    }

}

class TimeViewModel : BaseViewModel() {

}