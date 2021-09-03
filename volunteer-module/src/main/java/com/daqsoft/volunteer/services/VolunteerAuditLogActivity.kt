package com.daqsoft.volunteer.services

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.databinding.ActivityVolunteerAuditLogBinding
import com.daqsoft.volunteer.services.adapter.VolunteerAuditLogAdapter
import com.daqsoft.volunteer.services.vm.VolunteerAuditLogVM

/**
 *@package:com.daqsoft.volunteer.services
 *@date:2020/7/1 9:58
 *@author: caihj
 *@des:审核记录
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_AUDIT_LOG)
class VolunteerAuditLogActivity:TitleBarActivity<ActivityVolunteerAuditLogBinding,VolunteerAuditLogVM>() {
    override fun getLayout(): Int = R.layout.activity_volunteer_audit_log

    override fun setTitle(): String = getString(R.string.volunteer_apply_record)

    override fun injectVm(): Class<VolunteerAuditLogVM> = VolunteerAuditLogVM::class.java

    override fun initView() {
        mBinding.rvAuditLog.apply {
            layoutManager = LinearLayoutManager(this@VolunteerAuditLogActivity)
            adapter = auditLogAdapter
        }
        mModel.auditLog.observe(this, Observer {
            auditLogAdapter.add(it)
        })
    }

    override fun initData() {
        mModel.getVolunteerAuditLog()
    }

    val auditLogAdapter = VolunteerAuditLogAdapter()
}
