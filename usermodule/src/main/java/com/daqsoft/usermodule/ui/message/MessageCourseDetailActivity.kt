package com.daqsoft.usermodule.ui.message

import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityMessageRequestDetailBinding

/**
 * @Description
 * @ClassName   MessageRequestDetailActivity
 * @Author      luoyi
 * @Time        2020/12/17 17:09
 */
@Route(path = ARouterPath.UserModule.USER_MEASSAGE_COURSE_ACTIVITY)
class MessageCourseDetailActivity : TitleBarActivity<ActivityMessageRequestDetailBinding, MessageCourseDetailViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_message_request_detail
    }

    override fun setTitle(): String {
        return getString(R.string.mine_message_center)
    }

    override fun injectVm(): Class<MessageCourseDetailViewModel> {
        return MessageCourseDetailViewModel::class.java
    }

    override fun initView() {
    }

    override fun initData() {
    }


}

class MessageCourseDetailViewModel : BaseViewModel() {

}