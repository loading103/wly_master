package com.daqsoft.volunteer.volunteer

import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.utils.SoftHideKeyBoardUtil
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.databinding.ActivityVolunteerRegisterBinding
import com.daqsoft.volunteer.volunteer.fragment.VolunteerRegisterFragment1
import com.daqsoft.volunteer.volunteer.fragment.VolunteerRegisterFragment2
import com.daqsoft.volunteer.volunteer.fragment.VolunteerTeamRegisterFragment1
import com.daqsoft.volunteer.volunteer.fragment.VolunteerTeamRegisterFragment2
import com.daqsoft.volunteer.volunteer.vm.VolunteerRegisterVM

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/3 14:05
 *@author: caihj
 *@des:志愿者团队注册界面
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_TEAM_REGISTER)
class VolunteerTeamRegisterActivity:TitleBarActivity<ActivityVolunteerRegisterBinding,VolunteerRegisterVM>() {
    override fun getLayout(): Int = R.layout.activity_volunteer_register

    override fun setTitle(): String = getString(R.string.volunteer_module_team_register_title)

    override fun injectVm(): Class<VolunteerRegisterVM> = VolunteerRegisterVM::class.java
    var volunteerRegisterFragment2 :VolunteerTeamRegisterFragment2?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoftHideKeyBoardUtil.assistActivity(this)
    }

    override fun initView() {
        val volunteerRegisterFragment1 = VolunteerTeamRegisterFragment1()
        transactFragment(R.id.fl_content,volunteerRegisterFragment1)
    }

    override fun initData() {
    }

    fun next(code:String){
         volunteerRegisterFragment2 = VolunteerTeamRegisterFragment2.newInstance(code)
         transactFragment(R.id.fl_content,volunteerRegisterFragment2!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        volunteerRegisterFragment2?.onActivityResult(requestCode,resultCode,data)

    }
}