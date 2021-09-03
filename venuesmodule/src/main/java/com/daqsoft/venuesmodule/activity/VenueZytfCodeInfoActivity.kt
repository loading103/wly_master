package com.daqsoft.venuesmodule.activity

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.provider.ARouterPath
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ActivityZytfCodeInfoBinding

/**
 * @Description 智游天府码介绍
 * @ClassName   VenueZytfCodeInfoActivity
 * @Author      luoyi
 * @Time        2020/7/4 11:38
 */
@Route(path = ARouterPath.VenuesModule.VENUES_ZYTF_CODE_INFO_ACTIVITY)
class VenueZytfCodeInfoActivity : TitleBarActivity<ActivityZytfCodeInfoBinding, VenueZytfCodeInfoViewModel>() {

    @JvmField
    @Autowired
    var name: String? = ""

    @JvmField
    @Autowired
    var introduce: String? = ""

    override fun getLayout(): Int {
        return R.layout.activity_zytf_code_info
    }

    override fun setTitle(): String {
        return "详情"
    }

    override fun injectVm(): Class<VenueZytfCodeInfoViewModel> {
        return VenueZytfCodeInfoViewModel::class.java
    }

    override fun initView() {
    }

    override fun initData() {
        title = "${name}详情"
        mBinding.wbZytfCodeInfo.settings.javaScriptEnabled = true
        if(!introduce.isNullOrEmpty()){
        mBinding.wbZytfCodeInfo.loadDataWithBaseURL(null, StringUtil.getHtml(introduce!!), "text/html", "utf-8", null)
        }
        }


}


/**
 * @des
 * @author luoyi
 * @Date 2020/7/4 11:40
 */
class VenueZytfCodeInfoViewModel : BaseViewModel() {

}