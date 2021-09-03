package com.daqsoft.usermodule.ui.invitation

import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.view.popupwindow.ShareInviteFriendPopWindow
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityInvitationBinding
import com.daqsoft.usermodule.ui.invitation.viewmodel.InvitationViewModel

/**
 * @Description 邀请有礼页面
 * @ClassName   InvitationActivity
 * @Author      luoyi
 * @Time        2020/7/2 11:44
 */
@Route(path = ARouterPath.UserModule.USER_INVITE_ACTIVITY)
class InvitationActivity : TitleBarActivity<ActivityInvitationBinding, InvitationViewModel>() {


    var inviteFriendPopWindow: ShareInviteFriendPopWindow? = null

    override fun getLayout(): Int {
        return R.layout.activity_invitation
    }

    override fun setTitle(): String {
        return "邀请有礼"
    }

    override fun injectVm(): Class<InvitationViewModel> {
        return InvitationViewModel::class.java
    }

    override fun initView() {
        initViewModel()
        initViewEvent()
    }

    private fun initViewEvent() {
        mBinding.tvMineInviteTopInput.onNoDoubleClick {
            ARouterPath.UserModule.toInPutInviteCodePage()
        }
        mBinding.tvMineInviteLs.onNoDoubleClick {
            ARouterPath.UserModule.toMineInviteLsPage()
        }
        mBinding.btnTopInvite.onNoDoubleClick {
            showShareInvitePopWindow()
        }
    }

    private fun initViewModel() {
        mModel.inviteInfoLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null) {
                // 邀约信息
                mBinding.tvInviteTipIntegral.text = getString(R.string.mine_invite_tip_integral, "" + it.rewardPoints)

                mBinding.inivteCode = it.inviteCode
                if (it.existMaxInvitePeople) {
                    mBinding.tvInviteTipMaxNum.text = getString(R.string.mine_invite_tip_max_num, "" + it.maxInvitePeople)
                    if (it.inviteSuccessPeople == it.maxInvitePeople) {
                        mBinding.btnTopInvite.text = "任务已完成"
                        mBinding.btnTopInvite.setTextColor(resources.getColor(R.color.color_c9a088))
                        mBinding.btnTopInvite.background = resources.getDrawable(R.drawable.shape_invite_complete_r20_btn_bg)
                        mBinding.btnTopInvite.isClickable=false
                    }

                } else {
                    mBinding.tvInviteTipMaxNum.visibility = View.GONE
                }
            } else {

            }
        })
    }

    override fun initData() {
        mModel.getInviteInfo()
    }

    private fun showShareInvitePopWindow() {
        if (inviteFriendPopWindow == null) {
            inviteFriendPopWindow = ShareInviteFriendPopWindow(this)
            inviteFriendPopWindow!!.setShareContent("", "", "", "")
        }
        if (!inviteFriendPopWindow!!.isShowing) {
            inviteFriendPopWindow?.showAtLocation(mBinding.root, 0, 0, 0)
        }
    }

    override fun initCustomTitleBar(mTitleBar: TitleBar) {

    }

    override fun onDestroy() {
        super.onDestroy()
        inviteFriendPopWindow = null
    }
}

