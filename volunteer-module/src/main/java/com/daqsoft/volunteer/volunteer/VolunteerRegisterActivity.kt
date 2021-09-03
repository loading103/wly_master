package com.daqsoft.volunteer.volunteer

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.utils.SoftHideKeyBoardUtil
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerBriefInfoBean
import com.daqsoft.volunteer.databinding.ActivityVolunteerRegisterBinding
import com.daqsoft.volunteer.volunteer.fragment.VolunteerRegisterFragment1
import com.daqsoft.volunteer.volunteer.fragment.VolunteerRegisterFragment2
import com.daqsoft.volunteer.volunteer.vm.VolunteerRegisterVM

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/3 14:05
 *@author: caihj
 *@des:志愿者注册界面
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_REGISTER)
class VolunteerRegisterActivity:TitleBarActivity<ActivityVolunteerRegisterBinding,VolunteerRegisterVM>() {

    var volunteer: VolunteerBriefInfoBean? = null
    override fun getLayout(): Int = R.layout.activity_volunteer_register

    override fun setTitle(): String = getString(R.string.volunteer_module_register_title)

    override fun injectVm(): Class<VolunteerRegisterVM> = VolunteerRegisterVM::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoftHideKeyBoardUtil.assistActivity(this)
    }
    var volunteerRegisterFragment2 :VolunteerRegisterFragment2?= null
    override fun initView() {
        val volunteerRegisterFragment1 = VolunteerRegisterFragment1()
        transactFragment(R.id.fl_content,volunteerRegisterFragment1)
        mModel.volunteer.observe(this, Observer {
            volunteer = it
        })
    }

    override fun initData() {
        mModel.getVolunteerInfo()
    }

    fun next(
        name:String,
        sex:String,
        nation:String,
        idCard:String,
        phone:String,
        code:String
    ){
        volunteerRegisterFragment2 = VolunteerRegisterFragment2.newInstance(name,sex,nation,idCard,phone,code)
        transactFragment(R.id.fl_content,volunteerRegisterFragment2!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        volunteerRegisterFragment2?.onActivityResult(requestCode,resultCode,data)
    }
}