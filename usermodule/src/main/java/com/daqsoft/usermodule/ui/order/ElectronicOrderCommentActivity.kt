package com.daqsoft.usermodule.ui.order

import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityElectronicCommentOrderBinding

/**
 * @Description 评价晒单
 * @ClassName   OrderCommentActivity
 * @Author      PuHua
 * @Time        2019/12/16 16:01
 */
@Route(path = ARouterPath.UserModule.USER_ELECTRONIC_COMMENT_ACTIVITY)
class ElectronicOrderCommentActivity :TitleBarActivity<ActivityElectronicCommentOrderBinding,OrderCommentActivityViewModel>(){
    override fun getLayout(): Int  = R.layout.activity_electronic_comment_order

    override fun setTitle(): String  = getString(R.string.order_electronic_comment)

    override fun injectVm(): Class<OrderCommentActivityViewModel>  = OrderCommentActivityViewModel::class.java

    override fun initView() {

    }

    override fun initData() {

    }
}

class OrderCommentActivityViewModel:BaseViewModel(){

}