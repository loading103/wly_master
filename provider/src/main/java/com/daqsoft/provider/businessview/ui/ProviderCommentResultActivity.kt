package com.daqsoft.provider.businessview.ui

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.businessview.viewmodel.ProviderCommentResultViewModel
import com.daqsoft.provider.databinding.ProviderActivityCommentResultBinding

/**
 * @Description 评论结果
 * @ClassName   ProviderCommentResultActivity
 * @Author      luoyi
 * @Time        2020/4/13 11:00
 */
@Route(path = ARouterPath.Provider.PROVIDER_COMMENT_RESULT)
class ProviderCommentResultActivity : TitleBarActivity<ProviderActivityCommentResultBinding, ProviderCommentResultViewModel>() {
    @JvmField
    @Autowired
    var id: String = ""

    @JvmField
    @Autowired
    var contentTitle: String = "" // 内容标题

    override fun getLayout(): Int {
        return R.layout.provider_activity_comment_result
    }

    override fun setTitle(): String {
        return contentTitle
    }

    override fun injectVm(): Class<ProviderCommentResultViewModel> {
        return ProviderCommentResultViewModel::class.java
    }

    override fun initView() {
        mBinding.tvWyts.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_COMPLAINT_ACTIVITY)
                .navigation()
        }
        mBinding.tvFhdp.onNoDoubleClick {
            finish()
        }
    }

    override fun initData() {
    }
}