package com.daqsoft.travelCultureModule.hotActivity

import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityActIndexBinding
import com.daqsoft.provider.MainARouterPath

/**
 * @Description 活动首页
 * @ClassName   ActIndexActivity
 * @Author      luoyi
 * @Time        2020/6/23 9:50
 */
@Route(path = MainARouterPath.MAIN_ACTIVITY_INDEX)
class ActIndexActivity : TitleBarActivity<ActivityActIndexBinding, ActIndexViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_act_index
    }

    override fun setTitle(): String {
        return "活动 Events"
    }

    override fun injectVm(): Class<ActIndexViewModel> {
        return ActIndexViewModel::class.java
    }

    override fun initView() {
//        val fragment : Fragment
//        if (BaseApplication.appArea == "sc"){
//            fragment = ActivityIndexFragmentSC()
//        }else{
//            fragment = ActivityIndexFragment()
//        }
//        supportFragmentManager.beginTransaction().add(R.id.v_activity_act_index,fragment).commit()
    }

    override fun initData() {
    }
}

class ActIndexViewModel : BaseViewModel() {

}