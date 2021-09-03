package com.daqsoft.usermodule.ui.order

import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityElectronicCommentOrderBinding
import com.daqsoft.usermodule.databinding.ActivityElectronicReBakOrderBinding

/**
 * @Description 小电商退款的页面
 * @ClassName   ElectronicOrderReBackActivity
 * @Author      PuHua
 * @Time        2019/12/16 16:01
 */
@Route(path = ARouterPath.UserModule.USER_ELECTRONIC_REBACK_ACTIVITY)
class ElectronicOrderReBackSelectActivity :TitleBarActivity<ActivityElectronicReBakOrderBinding,
        ElectronicOrderReBackSelectActivityViewModel>(){
    override fun getLayout(): Int  = R.layout.activity_electronic_re_bak_order

    override fun setTitle(): String  = getString(R.string.order_electronic_re_back)

    override fun injectVm(): Class<ElectronicOrderReBackSelectActivityViewModel>  = ElectronicOrderReBackSelectActivityViewModel::class.java

    override fun initView() {

    }

    override fun initData() {

    }
}
/**
 * @des 小电商退款的viewmodel
 * @author PuHua
 * @Date 2019/12/18 9:59
 */
class ElectronicOrderReBackSelectActivityViewModel:BaseViewModel(){

}