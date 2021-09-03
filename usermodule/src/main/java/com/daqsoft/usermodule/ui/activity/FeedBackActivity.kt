package com.daqsoft.usermodule.ui.activity

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.net.UserService
import com.daqsoft.provider.view.LabelsView
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.LayoutFeedBackBinding
import me.nereo.multi_image_selector.MultiFileSelectorActivity
import me.nereo.multi_image_selector.bean.Constrant
import me.nereo.multi_image_selector.bean.Image
import me.nereo.multi_image_selector.utils.FileUtils

/**
 * @Description
 * @ClassName   FeedBackActivity
 * @Author      luoyi
 * @Time        2020/5/20 15:49
 */
@Route(path = ARouterPath.UserModule.USER_FEED_BACK_ACTIVITY)
class FeedBackActivity : TitleBarActivity<LayoutFeedBackBinding, FeedBackViewModel>() {
    /**
     * 反馈类型
     */
    var feedBackTypes: MutableList<String> = mutableListOf()

    /**
     * 选择类型
     */
    var selectType: Int = 0

    override fun getLayout(): Int {
        return R.layout.layout_feed_back
    }

    override fun setTitle(): String {
        return "意见反馈"
    }

    override fun injectVm(): Class<FeedBackViewModel> {
        return FeedBackViewModel::class.java
    }

    override fun initPageParams() {
        isInitImmerBar = false
    }

    override fun initView() {
        mBinding.recyFeedBackUplaods.setMaxVideoNumer(1)
        mBinding.recyFeedBackUplaods.setPicNumber(9)
        mBinding.recyFeedBackUplaods.init(this@FeedBackActivity, true, true)
        if(BaseApplication.appArea=="xj"){
            mBinding.llvFeedbackTypes.visibility=View.GONE
        }else{
            mBinding.llvFeedbackTypes.visibility=View.VISIBLE
            mBinding.llvFeedbackTypes.setOnLabelSelectChangeListener { label, data, isSelect, position ->
                if (isSelect) {
                    selectType = position
                    label?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.color_36cd64))
                    label?.setBackgroundResource(com.daqsoft.provider.R.drawable.shape_feed_back_r15_selected)
                } else {
                    label?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.color_333))
                    label?.setBackgroundResource(com.daqsoft.provider.R.drawable.shape_feed_back_r15_normal)
                }
            }
        }
        mBinding.tvFeedBackRecoder.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.UserModule.USER_FEED_BACK_LS_ACTIVITY)
                .navigation()
        }
        initViewModel()
    }

    private fun initViewModel() {
        mModel.addFeedBackLiveData.observe(this, Observer {
            // 跳转我的反馈意见列表
            dissMissLoadingDialog()
            ToastUtils.showMessage("提交成功，感谢您的宝贵意见~")
            finish()
            ARouter.getInstance().build(ARouterPath.UserModule.USER_FEED_BACK_LS_ACTIVITY)
                .navigation()

        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            ToastUtils.showMessage("" + it?.message)
        })
        mModel.feedBackNumsLiveData.observe(this, Observer {
            if (it > 0) {
                mBinding.tvFeedBackRecoder.visibility = View.VISIBLE
            } else {
                mBinding.tvFeedBackRecoder.visibility = View.GONE
            }
        })
        mBinding.edtFeedBack.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var length = s.toString().length
                if (length >= 500) {
                    mBinding.txtInputNumber.text = "500/500"
                } else {
                    mBinding.txtInputNumber.text = "$length/500"
                }
                if (length > 0) {
                    mBinding.tvPostFeedBack.setBackgroundResource(R.drawable.shape_corlor_primary_btn_bg)
                } else {
                    mBinding.tvPostFeedBack.setBackgroundResource(R.drawable.shape_provider_btn_r5_unselect)
                }
            }

        })
        mBinding.tvPostFeedBack.onNoDoubleClick {
            if (!AppUtils.isLogin()) {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
                return@onNoDoubleClick
            }
            showLoadingDialog()
//            var typeCodes = resources.getStringArray(R.array.feedback_typecodes)
//            var type: String = ""
//            if (selectType < typeCodes.size) {
//                type = typeCodes[selectType]
//            }

            var typeCodes = resources.getStringArray(R.array.feedback_typecodes)
            var type: String = ""
            if(BaseApplication.appArea=="xj"){
                type = typeCodes[0]
            }else{
                if (selectType < typeCodes.size) {
                    type = typeCodes[selectType]
                }
            }



            var content = mBinding.edtFeedBack.text.toString()
            if (content.isNullOrEmpty()) {
                ToastUtils.showMessage("请输入您的宝贵意见~")
                dissMissLoadingDialog()
                return@onNoDoubleClick
            }
            var urls = mBinding.recyFeedBackUplaods.path
            mModel.addFeedBack(type, content, urls)

        }
    }

    override fun initData() {
        var feedBackT = resources.getStringArray(R.array.feedback_types)
        feedBackTypes.clear()
        feedBackTypes.addAll(feedBackT)
        mBinding.llvFeedbackTypes.setLabels(feedBackTypes)
        mBinding.llvFeedbackTypes.setSelects(0)
        mModel.getFeedBackNums()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constrant.ADD_IMAGE) {
            if (data != null && data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)) {
                var list: ArrayList<Image> =
                    data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                mBinding.recyFeedBackUplaods.onActivityResult(list)
            }
        } else if (requestCode == Constrant.ADD_VIDEO) {
            if (data != null && data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)) {
                var list: ArrayList<Image> =
                    data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                if (list.size > 0) {
                    mBinding.recyFeedBackUplaods.insertAtFirst(list[0].apply {
                        type = 1
                    })
                }
            }
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

}

class FeedBackViewModel : BaseViewModel() {
    /**
     * 反馈次数
     */
    var feedBackNumsLiveData: MutableLiveData<Int> = MutableLiveData()

    /**
     * 添加反馈
     */
    var addFeedBackLiveData: MutableLiveData<Any> = MutableLiveData()


    /**
     * 获取意见反馈次数
     */
    fun getFeedBackNums() {
        mPresenter?.value?.loading = false
        UserRepository().userService?.getFeedBackNum()
            ?.excute(object : BaseObserver<Int>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Int>) {
                    feedBackNumsLiveData.postValue(response.data)
                }


            })
    }

    /**
     * 添加意见反馈
     */
    fun addFeedBack(type: String, content: String, urls: String? = "") {
        var params: HashMap<String, String> = hashMapOf()
        params["type"] = type
        params["content"] = content
        if (!urls.isNullOrEmpty()) {
            var imags = urls.split(",")
            var imageUrls = ""
            for (item in imags) {
                if (FileUtils.isVideo(item)) {
                    params["video"] = item
                } else {
                    imageUrls += "$item,"
                }
            }
            if (!imageUrls.isNullOrEmpty()) {
                imageUrls = imageUrls.substring(0, imageUrls.lastIndexOf(","))
                params["image"] = imageUrls
            }
        }
        mPresenter?.value?.loading = false
        UserRepository().userService?.addFeedBack(params)
            ?.excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    addFeedBackLiveData.postValue(response)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    mError.postValue(response)
                }
            })

    }

}