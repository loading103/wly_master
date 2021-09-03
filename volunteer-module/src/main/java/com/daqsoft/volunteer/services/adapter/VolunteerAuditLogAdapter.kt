package com.daqsoft.volunteer.services.adapter

import android.view.View
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.OperateStatusBean
import com.daqsoft.volunteer.databinding.ActivityVolunteerAuditLogItemBinding
import com.daqsoft.volunteer.utils.JumpUtils

/**
 *@package:com.daqsoft.volunteer.services.adapter
 *@date:2020/7/1 10:26
 *@author: caihj
 *@des:TODO
 **/
class VolunteerAuditLogAdapter : RecyclerViewAdapter<ActivityVolunteerAuditLogItemBinding, OperateStatusBean>(R.layout.activity_volunteer_audit_log_item) {
    override fun setVariable(mBinding: ActivityVolunteerAuditLogItemBinding, position: Int, item: OperateStatusBean) {
        mBinding.tvTime.text = item.operateTime
        mBinding.tvStatus.text = when (item.operateStatus) {
            "4" -> "审核中"
            "8" -> "已撤销"
            "79" -> "审核不通过"
            else -> "其他"
        }
        if (item.operateStatus == "79" && !item.operateResult.isNullOrEmpty()) {
            mBinding.tvContent.visibility = View.VISIBLE
            mBinding.tvContent.text = item.operateResult
        } else {
            mBinding.tvContent.visibility = View.GONE
        }
        if (item.operateStatus == "4") {
            mBinding.tvViewDetail.visibility = View.VISIBLE
        } else {
            mBinding.tvViewDetail.visibility = View.GONE
        }
        mBinding.tvViewDetail.onNoDoubleClick {
            JumpUtils.gotoVolunteerApplyDetail()
        }
    }
}