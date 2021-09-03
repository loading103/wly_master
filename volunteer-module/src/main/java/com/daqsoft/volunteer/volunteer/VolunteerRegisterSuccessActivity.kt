package com.daqsoft.volunteer.volunteer

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.timepicker.CalendarUtil
import com.daqsoft.provider.ARouterPath
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.databinding.VolunteerRegisterApplySuccessBinding
import com.daqsoft.volunteer.utils.JumpUtils
import java.nio.file.Path
import java.util.*

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/3 16:59
 *@author: caihj
 *@des:志愿者申请成功界面
 **/
@Route(path = ARouterPath.VolunteerModule.APPLY_SUCCESS_RESULT)
class VolunteerRegisterSuccessActivity:TitleBarActivity<VolunteerRegisterApplySuccessBinding,VolunteerRegSuccessVM>() {
    @Autowired
    @JvmField
    var type = 0 // 0 志愿者申请 1 志愿者团队申请

    @Autowired
    @JvmField
    var id = ""

    override fun getLayout(): Int = R.layout.volunteer_register_apply_success

    override fun setTitle(): String = when(type){
        0 -> getString(R.string.volunteer_module_volunteer_apply_title)
        1 -> getString(R.string.volunteer_module_volunteer_team_apply_title)
        2 -> "志愿者服务申请"
        else -> "其他审核信息"
    }

    override fun injectVm(): Class<VolunteerRegSuccessVM> = VolunteerRegSuccessVM::class.java

    override fun initView() {
    }

    override fun initData() {
        mBinding.tvData.text = DateUtil.getDateDayString("yyyy-MM-dd HH:mm:ss",Date())
        mBinding.tvNotice.text = getTipString()
        mBinding.tvCheck.onNoDoubleClick {
            if(type == 0){
                JumpUtils.gotoVolunteerApplyDetail()
            }else if(type == 1){
                JumpUtils.gotoVolunteerTeamApplyDetail()
            }else{

            }
        }
        mBinding.tvBack.onNoDoubleClick {
            finish()
        }
    }

    private fun getTipString():String=when(type){
        0 -> "您的志愿者注册信息需要审核，审核通过后我们将用短信告知您，请及时关注短信通知，谢谢！"
        1 -> "您的志愿团队注册信息需要审核，审核通过后我们将用短信告知您，请及时关注短信通知，谢谢！"
        2 -> "您的志愿者服务申请信息需要审核，审核通过后我们将用短信告知您，请及时关注短信通知，谢谢！"
        else -> "其他审核信息"
    }
}

class VolunteerRegSuccessVM:BaseViewModel()