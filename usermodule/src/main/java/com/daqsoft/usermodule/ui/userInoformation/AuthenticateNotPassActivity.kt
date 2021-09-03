package com.daqsoft.usermodule.ui.userInoformation

import android.view.View
import cc.shinichi.library.ImagePreview
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.UsermoduleActivityAuthenticateNotPassBinding

/**
 * 认证不通过
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-18
 * @since JDK 1.8.0_191
 */
@Route(path = ARouterPath.UserModule.AUTHENTICATE_NOT_PASS_ACTIVITY)
class AuthenticateNotPassActivity :
    TitleBarActivity<UsermoduleActivityAuthenticateNotPassBinding, AuthenticateReviewActivityVm>(),
    View.OnClickListener {

    override fun getLayout(): Int = R.layout.usermodule_activity_authenticate_not_pass

    override fun setTitle(): String = getString(R.string.user_real_name)

    override fun injectVm(): Class<AuthenticateReviewActivityVm> = AuthenticateReviewActivityVm::class.java

    @JvmField
    @Autowired
    var isDraw: Boolean= false


    override fun initView() {
        mBinding.vm = mModel
        mBinding.view = this
        if(isDraw){
            mBinding.llTop.visibility=View.GONE
            mBinding.mChangeInfo.background=resources.getDrawable(R.drawable.user_shape_red_btn1)
        }else{
            mBinding.llTop.visibility=View.VISIBLE
            mBinding.mChangeInfo.background=resources.getDrawable(R.drawable.user_shape_red_btn)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIdCardDownIv -> {
                if(mModel.data.value?.idCardDown.isNullOrEmpty()){
                    return
                }
                ImagePreview.getInstance()
                    .setContext(this)
                    .setImage(mModel.data.value?.idCardDown ?: "")
                    .start()
            }
            R.id.mIdCardUpIv -> {
                if(mModel.data.value?.idCardUp.isNullOrEmpty()){
                    return
                }
                ImagePreview.getInstance()
                    .setContext(this)
                    .setImage(mModel.data.value?.idCardUp ?: "")
                    .start()
            }
            R.id.mChangeInfo -> {
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.AUTHENTICATE_COMMIT_ACTIVITY)
                    .withString("id", mModel.data.value?.id.toString())
                    .withString("name", mModel.data.value?.name)
                    .withString("card", mModel.data.value?.idCard)
                    .withString("cardup", mModel.data.value?.idCardUp)
                    .withString("carddown", mModel.data.value?.idCardDown)
                    .navigation()
                finish()
            }
        }
    }

    override fun initData() {
        mModel.getRealNameInfo()
    }
}