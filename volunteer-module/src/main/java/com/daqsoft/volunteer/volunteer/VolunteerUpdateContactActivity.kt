package com.daqsoft.volunteer.volunteer

import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.databinding.ActivityUpdateVolunteerContactBinding
import com.daqsoft.volunteer.volunteer.vm.VolunteerUpdateInformationVM

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/29 14:59
 *@author: caihj
 *@des:修改紧急联系人信息
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_UPDATE_CONTACT)
class VolunteerUpdateContactActivity:TitleBarActivity<ActivityUpdateVolunteerContactBinding,VolunteerUpdateInformationVM>() {

    @Autowired
    @JvmField
    var name = ""
    @Autowired
    @JvmField
    var phone = ""

    override fun getLayout(): Int = R.layout.activity_update_volunteer_contact

    override fun setTitle(): String = getString(R.string.volunteer_update_contact)

    override fun injectVm(): Class<VolunteerUpdateInformationVM> = VolunteerUpdateInformationVM::class.java

    override fun initView() {
        mBinding.ctName.setText(name)
        mBinding.ctPhone.setText(phone)

        mBinding.submit.onNoDoubleClick {
            val nameStr = mBinding.ctName.text.toString()
            val phoneStr = mBinding.ctPhone.text.toString()
            if(nameStr == name && phoneStr == phone){
                finish()
            }else{
                mModel.updateVolunteerInfo(VolunteerUpdateInformationVM.emergencyContactName,nameStr,VolunteerUpdateInformationVM.emergencyContactPhone,phoneStr)
            }
        }
        mModel.updateStatus.observe(this, Observer {
            if(it){
                finish()
            }
        })

    }

    override fun initData() {
    }
}