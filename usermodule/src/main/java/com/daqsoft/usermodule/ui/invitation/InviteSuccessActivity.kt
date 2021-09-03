package com.daqsoft.usermodule.ui.invitation

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.AppManager
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityInviteSuccessBinding

/**
 * @Description
 * @ClassName   InviteSuccessActivity
 * @Author      luoyi
 * @Time        2020/7/29 11:34
 */
@Route(path = ARouterPath.UserModule.USER_INVITE_SUCCESS_ACTIVITY)
class InviteSuccessActivity : TitleBarActivity<ActivityInviteSuccessBinding, InviteSuccessViewModel>() {

    @JvmField
    @Autowired
    var ownHeader: String? = ""
    @JvmField
    @Autowired
    var otherHeader: String? = ""

    override fun getLayout(): Int {
        return R.layout.activity_invite_success
    }

    override fun setTitle(): String {
        return getString(R.string.mine_invite_to_polite)
    }

    override fun injectVm(): Class<InviteSuccessViewModel> {
        return InviteSuccessViewModel::class.java
    }

    override fun initView() {
        mBinding.ownHeader = ownHeader
        mBinding.ownHeader = otherHeader
        mBinding.tvMineToInvite.onNoDoubleClick {
            ARouterPath.UserModule.toMineInvitePage()
            finish()
        }
        mBinding.tvMineToIndex.onNoDoubleClick {
            AppManager.instance.gotoHomeActivity()
        }
    }

    override fun initData() {
    }
}

class InviteSuccessViewModel : BaseViewModel() {


}