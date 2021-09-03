package com.daqsoft.usermodule.ui.userInoformation

import android.content.Intent
import android.view.Gravity
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityPersonalInformationBinding
import com.daqsoft.baselib.bean.LocationData
import com.daqsoft.baselib.utils.LocationUtil
import com.daqsoft.usermodule.uitls.ResourceUtil
import com.daqsoft.usermodule.uitls.SelectImageUtils
import java.io.File

/**
 * @Description 个人信息页面
 * @ClassName   PersonalInformationActivity
 * @Author      PuHua
 * @Time        2019/10/31 16:53
 */
@Route(path = ARouterPath.UserModule.USER_INFORMATION_ACTIVITY)
class PersonalInformationActivity :
    TitleBarActivity<ActivityPersonalInformationBinding, PersonalInformationViewModel>(),
    View.OnClickListener {


    override fun setTitle(): String =
        ResourceUtil.getStringResource(this, R.string.user_module_personal_info)

    override fun getLayout(): Int = R.layout.activity_personal_information

    override fun injectVm(): Class<PersonalInformationViewModel> =
        PersonalInformationViewModel::class.java

    override fun initData() {

        mModel.head.observe(this, Observer {
            Glide.with(this@PersonalInformationActivity)
                .load(it)
                .placeholder(R.drawable.mine_profile_photo_default)
                .into(mBinding.avUserHead)
        })
    }

    override fun initView() {
        mBinding.vm = mModel
        mBinding.view = this
        mModel.headLocal.observe(this, Observer {
            val file = File(it)
            mModel.upLoadFile(file)
        })

    }

    /**
     * 设置实名认证状态
     */
    private fun setStatus(realNameStatus: Int): String {
        return when (realNameStatus) {
            0 -> getString(R.string.user_not_authenticate)
            6 -> getString(R.string.user_authenticated)
            4 -> getString(R.string.user_pending_review)
            79 -> getString(R.string.user_not_pass)
            8 -> getString(R.string.user_with_draw)
            else -> ""
        }
    }

    override fun notifyData() {
        super.notifyData()
        mModel.realNameStatus.observe(this, Observer {
            mBinding.mRealName.tvContent.text = setStatus(it)
        })

    }

    override fun onResume() {
        super.onResume()

        mModel.getUserInformation()
    }

    /**
     * 性别选择器
     */
    private val genderPv by lazy {
        val gender = resources.getStringArray(R.array.gender)
        val pV = OptionsPickerBuilder(this, OnOptionsSelectListener { s1, s2, s3, v ->
            mModel.genderCode.value = s1
            mModel.gender.postValue(gender[s1])
            mModel.updatePsersonalInformation(
                UpdatePersonalInformationViewModel.sex,
                s1.toString()!!
            )
        }).build<String>()
        pV.setPicker(gender.asList())
        pV
    }

    /**
     * 性别点击修改
     */
    fun updateSex(v: View) {
        var selectIndex: Int = when (mModel.gender.value) {
            "男" -> 1
            "女" -> 2
            else -> 0
        }
        genderPv.setSelectOptions(selectIndex)
        UIHelperUtils.showOptionsPicker(this, genderPv)
    }

    /**
     * 地区点击修改
     */
    fun updateArea(v: View) {
        LocationUtil(this, object : LocationUtil.OnLocationSelectListener {
            override fun onLocationSelect(region: LocationData) {
                mBinding.mArea.content = region.memo
                mModel.updatePsersonalInformation(
                    UpdatePersonalInformationViewModel.placeLocation,
                    region.region
                )
            }


        }, mModel.mPresenter)

    }

    /**
     * 跳转相机请求码
     */
    private val REQUEST_CAMERA_CODE = 0
    /**
     * 跳转相册请求码
     */
    private val REQUEST_ALBUM_CODE = 1
    /**
     * 底部头像选择弹框
     */
    private val popupWindow by lazy {
        SelectImageUtils.initPictureSelectPop(this, REQUEST_CAMERA_CODE, REQUEST_ALBUM_CODE)
    }

    fun headUrlUpdate(v: View) {
        // 头像
        popupWindow.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ALBUM_CODE) {
            // 相册
            if (data == null) {
                return
            }
            mModel.headLocal.postValue(
                SelectImageUtils.getFileFromAlbum(this, data)
                    ?: ""
            )
        }
        if (requestCode == REQUEST_CAMERA_CODE) {
            // 相机
            val file = SelectImageUtils.getFileFromCamera()
            if (file?.exists() == false) {
                return
            }
            mModel.headLocal.postValue(file?.absolutePath ?: "")
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mRealName -> {
                // 实名认证
                when (mModel.realNameStatus.value) {
                    6 -> {
                        // 已实名
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.AUTHENTICATE_COMPLETE_ACTIVITY)
                            .navigation()
                    }
                    4 -> {
                        // 待审核
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.AUTHENTICATE_REVIEW_ACTIVITY)
                            .navigation()
                    }
                    79 -> {
                        // 审核未通过
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.AUTHENTICATE_NOT_PASS_ACTIVITY)
                            .navigation()
                    }
                    8 -> {
                        // 已经撤回消息
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.AUTHENTICATE_NOT_PASS_ACTIVITY)
                            .withBoolean("isDraw",true)
                            .navigation()
                    }
                    else -> {
                        // 未实名，审核已撤回
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.AUTHENTICATE_COMMIT_ACTIVITY)
                            .navigation()
                    }
                }
            }
        }
    }
}
