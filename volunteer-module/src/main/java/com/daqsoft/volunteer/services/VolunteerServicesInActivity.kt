package com.daqsoft.volunteer.services

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.view.BaseDialog
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.databinding.ActivityVolunteerServicesInBinding
import com.daqsoft.volunteer.services.vm.VolunteerServicesVM
import com.daqsoft.volunteer.utils.JumpUtils

/**
 *@package:com.daqsoft.volunteer.services
 *@date:2020/6/30 14:44
 *@author: caihj
 *@des:志愿服务入口
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_SERVICES_IN)
class VolunteerServicesInActivity : TitleBarActivity<ActivityVolunteerServicesInBinding, VolunteerServicesVM>() {

    // 0：不是志愿者 1：是志愿者 2：志愿者身份被禁用 3：草稿  4：待审核 8：撤回志愿者申请 79：志愿者申请不通过
    var volunteerStatus = ""
    var volunteerTeamStatus = ""
    override fun getLayout(): Int = R.layout.activity_volunteer_services_in

    override fun setTitle(): String = getString(R.string.volunteer_services_in_title)

    override fun injectVm(): Class<VolunteerServicesVM> = VolunteerServicesVM::class.java

    override fun initView() {
        volunteerStatus = SPUtils.getInstance().getString(SPKey.VOLUNTEER)
        volunteerTeamStatus = SPUtils.getInstance().getString(SPKey.VOLUNTEER_TEAM)
        mModel.userInfo.observe(this, Observer {
            volunteerStatus = it.volunteerStatus.toString()
            volunteerTeamStatus = it.volunteerTeamStatus.toString()

            // 志愿者
            when (it.volunteerStatus.toString()) {
                "0", "8" -> { // 不是志愿者
                    mBinding.clVolunteer.onNoDoubleClick {
                        JumpUtils.gotoVolunteerRegister()
                    }
                }
                "4" -> { // 待审核
                    mBinding.tvCheckStatus.visibility = View.VISIBLE
                    mBinding.llBottom.visibility = View.VISIBLE
                    mBinding.tvShowApply.onNoDoubleClick {
                        JumpUtils.gotoVolunteerApplyDetail()
                    }
                    mBinding.tvCancelApply.onNoDoubleClick {
                        initDeleteDialog(0)
                        cancelDialog!!.show()
                    }
                }
                "79" -> { // 审核不通过
                    mBinding.llCheck.visibility = View.VISIBLE
                    mBinding.llBottom.visibility = View.VISIBLE
                }
            }

            // 志愿团队
            when (it.volunteerTeamStatus.toString()) {
                "0", "8" -> { // 不是志愿者
                    mBinding.clVolunteerTeam.onNoDoubleClick {
                        JumpUtils.gotoVolunteerTeamRegister()
                    }
                }
                "4" -> { // 待审核
                    mBinding.tvCheckStatusTeam.visibility = View.VISIBLE
                    mBinding.llBottomTeam.visibility = View.VISIBLE
                    mBinding.tvShowApplyTeam.onNoDoubleClick {
                        JumpUtils.gotoVolunteerTeamApplyDetail()
                    }
                    mBinding.tvCancelApplyTeam.onNoDoubleClick {
                        initDeleteDialog(1)
                        cancelDialog!!.show()
                    }
                }
                "79" -> { // 审核不通过
                    mBinding.llCheckTeam.visibility = View.VISIBLE
                    mBinding.llBottomTeam.visibility = View.VISIBLE
                }
            }
        })
        mModel.volunteerStatus.observe(this, Observer {
            if (it != null) {
                mBinding.tvCheckContent.text = it.operateResult
            }
        })
        mModel.volunteerTeamStatus.observe(this, Observer {
            if (it != null) {
                mBinding.tvCheckContenTeamt.text = it.operateResult
            }
        })

        mBinding.tvApplyRecord.onNoDoubleClick {
            JumpUtils.gotoVolunteerAuditLog()
        }

    }

    // 撤销对话框
    var cancelDialog: BaseDialog? = null

    private fun initDeleteDialog(type: Int) {
        cancelDialog = BaseDialog(this)
        cancelDialog!!.contentView(R.layout.dialog_cancel_apply)
            .layoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            .gravity(Gravity.CENTER)
            .animType(BaseDialog.AnimInType.BOTTOM)
            .canceledOnTouchOutside(false)
        val title = if (type == 0) "确定撤回志愿者注册申请?" else "确定撤回志愿者团队注册申请?"
        cancelDialog!!.findViewById<TextView>(R.id.tv_title).text = title
        cancelDialog!!.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            cancelDialog!!.dismiss()
            cancelDialog = null
        }
        cancelDialog!!.findViewById<TextView>(R.id.tv_query).setOnClickListener {
            cancelDialog!!.dismiss()
            if (type == 0) {
                mModel.cancelApplyVolunteer()
            } else {
                mModel.cancelApplyVolunteerTeam()
            }
            cancelDialog = null
        }
    }

    override fun initData() {
        mModel.refreshToken()
        mModel.getVolunteerStatus()
        mModel.getVolunteerTeamStatus()
    }
}