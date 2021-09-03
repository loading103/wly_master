package com.daqsoft.usermodule.ui.userInoformation

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cc.shinichi.library.ImagePreview
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AESOperator
import com.daqsoft.baselib.utils.SM4Util
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.ConstellationBean
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.UsermoduleActivityAuthenticateReviewBinding
import com.daqsoft.provider.bean.ReviewBean
import com.daqsoft.provider.network.net.UserRepository
import timber.log.Timber

/**
 * 实名认证审核中
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-18
 * @since JDK 1.8.0_191
 */
@Route(path = ARouterPath.UserModule.AUTHENTICATE_REVIEW_ACTIVITY)
class AuthenticateReviewActivity :
    TitleBarActivity<UsermoduleActivityAuthenticateReviewBinding, AuthenticateReviewActivityVm>(),
    View.OnClickListener {

    override fun getLayout(): Int = R.layout.usermodule_activity_authenticate_review

    override fun setTitle(): String = getString(R.string.user_real_name)

    override fun injectVm(): Class<AuthenticateReviewActivityVm> = AuthenticateReviewActivityVm::class.java

    override fun initView() {
        mBinding.vm = mModel
        mBinding.view = this
        mModel.typeList.observe(this, Observer {
        })
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

    override fun initData() {
        mModel.getCertTypeList()
        mModel.getRealNameInfo()

    }

    //
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
            R.id.mWithDrawUpdateTv -> {
                mModel.withDrawUpdate()
            }
        }
    }


}

class AuthenticateReviewActivityVm : BaseViewModel() {

    val data = MutableLiveData<ReviewBean>()

    val typeList = MutableLiveData<MutableList<ConstellationBean>>()

    /**
     * 审核查看
     */
    fun getRealNameInfo() {
        UserRepository().userService
            .getRealNameInfo()
            .excute(object : BaseObserver<ReviewBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ReviewBean>) {
                    if (response.data != null) {
                        if (!response.data!!.idCard.isNullOrEmpty()) {
                            response.data!!.idCard = SM4Util.decryptByEcb(response.data!!.idCard)
                        }
                    }
                    data.postValue(response.data)
                }
            })
    }

    fun setStatus(status: Int): String {
        return when (status) {
            0 -> BaseApplication.context.getString(R.string.user_not_authenticate)
            6 -> BaseApplication.context.getString(R.string.user_authenticated)
            4 -> BaseApplication.context.getString(R.string.user_pending_review)
            79 -> BaseApplication.context.getString(R.string.user_not_pass)
            8 -> BaseApplication.context.getString(R.string.user_with_draw)
            else -> ""
        }
    }

    /**
     * 撤回审核
     */
    fun withDrawUpdate() {
        Timber.tag("id11111").d("${data.value?.id ?: -1}")
        mPresenter.value?.loading=true
        UserRepository().userService
            .withDrawUpdate(data.value?.id ?: -1)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    finish.postValue(true)
                    toast.postValue("撤销成功!")
                    SPUtils.getInstance().put(SPKey.REALNAMESTATUS, 8)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    super.onFailed(response)
                    toast.postValue(response.message)
                }
            })
    }

    /**
     * 获取证件类型
     */
    fun getCertTypeList() {
        UserRepository().userService
            .getCertTypeList()
            .excute(object : BaseObserver<ConstellationBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ConstellationBean>) {
                    if (!response.datas.isNullOrEmpty()) {
                        typeList.value = response.datas
                    }
                }
            })
    }
}