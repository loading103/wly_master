package com.daqsoft.volunteer.volunteer

import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.databinding.ActivityUpdateVolunteerContactBinding
import com.daqsoft.volunteer.databinding.ActivityUpdateVolunteerInformationBinding
import com.daqsoft.volunteer.volunteer.vm.VolunteerUpdateInformationVM
import org.jetbrains.anko.toast

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/29 15:48
 *@author: caihj
 *@des:修改信息
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_UPDATE_INFORMATION)
class VolunteerUpdateInformationActivity:TitleBarActivity<ActivityUpdateVolunteerInformationBinding, VolunteerUpdateInformationVM>() {

    @Autowired
    @JvmField
    var type =""
    @Autowired
    @JvmField
    var data = ""
    override fun getLayout(): Int = R.layout.activity_update_volunteer_information

    override fun setTitle(): String = when(type){
        VolunteerUpdateInformationVM.SCHOOL ->"修改毕业院校与专业"
        VolunteerUpdateInformationVM.EMAIL ->"修改邮箱"
        VolunteerUpdateInformationVM.QQ ->"修改qq"
        else -> "修改信息"
    }

    override fun injectVm(): Class<VolunteerUpdateInformationVM> = VolunteerUpdateInformationVM::class.java

    override fun initView() {
        when(type){
            VolunteerUpdateInformationVM.SCHOOL ->{
                mBinding.label.text = "毕业院校和专业"
            }
            VolunteerUpdateInformationVM.EMAIL ->{
                mBinding.label.text = "邮箱"
            }
            VolunteerUpdateInformationVM.QQ ->{
                mBinding.label.text = "qq"
                mBinding.edit.maxElement = 12
            }
        }
        mBinding.edit.setText(data)
        mModel.updateStatus.observe(this, Observer {
            dissMissLoadingDialog()
            if(it){
                finish()
            }
        })
    }

    override fun initData() {

    }


    fun submit() {
        val content = mBinding.edit.text.toString()
        if (content.isEmpty()) {
            toast("请输入修改内容!")
            return
        }
        mModel.updateVolunteerInfo(
            this!!.type,
            content
        )
    }

}