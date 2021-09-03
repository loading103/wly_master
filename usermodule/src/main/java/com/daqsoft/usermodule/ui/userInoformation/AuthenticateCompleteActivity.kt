package com.daqsoft.usermodule.ui.userInoformation

import android.view.View
import androidx.lifecycle.Observer
import cc.shinichi.library.ImagePreview
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.UsermoduleActivityAuthenticateCompleteBinding

/**
 * 实名认证完成
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-18
 * @since JDK 1.8.0_191
 */
@Route(path = ARouterPath.UserModule.AUTHENTICATE_COMPLETE_ACTIVITY)
class AuthenticateCompleteActivity :
    TitleBarActivity<UsermoduleActivityAuthenticateCompleteBinding, AuthenticateReviewActivityVm>(),
    View.OnClickListener {


    override fun getLayout(): Int = R.layout.usermodule_activity_authenticate_complete

    override fun setTitle(): String = getString(R.string.user_real_name)

    override fun injectVm(): Class<AuthenticateReviewActivityVm> = AuthenticateReviewActivityVm::class.java

    override fun initView() {
        mBinding.view = this
        mBinding.vm = mModel
    }

    override fun initData() {
        mModel.getCertTypeList()
        mModel.getRealNameInfo()
        mModel.data.observe(this, Observer {
            if(mModel.typeList.value?.size ?: 0 > 0){
                for(item in mModel.typeList.value!!){
                    if(item.value == it.cardType){
                        if(item.name=="ID_CARD"){
                            mBinding.tvIdcard.text = "身份证"
                        }else{
                            mBinding.tvIdcard.text = item.name
                        }
                    }
                }
            }

        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
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
        }
    }
}