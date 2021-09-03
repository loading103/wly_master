package com.daqsoft.travelCultureModule.hotActivity

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityHotActivitiesBinding
import com.daqsoft.provider.MainARouterPath

/**
 * @Description 活动弹窗页面
 * @ClassName   HotActivitiesActivity
 * @Author      luoyi
 * @Time        2020/4/24 9:32
 */
@Route(path = MainARouterPath.MAIN_ACTIVITY_LS)
class HotActivitiesActivity : TitleBarActivity<ActivityHotActivitiesBinding, HotActivitiesViewModel>() {

    @JvmField
    @Autowired
    var classifyId: String = ""

    override fun getLayout(): Int {
        return R.layout.activity_hot_activities
    }

    override fun setTitle(): String {
        return "活动"
    }

    override fun injectVm(): Class<HotActivitiesViewModel> {
        return HotActivitiesViewModel::class.java
    }

    override fun initView() {
    }

    override fun initData() {
    }
}

class HotActivitiesViewModel : BaseViewModel() {

}