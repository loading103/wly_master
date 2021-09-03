package com.daqsoft.usermodule.ui.userInoformation

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityMoreInformationBinding
import com.daqsoft.usermodule.repository.constant.IntentConstant
import com.daqsoft.provider.bean.ConstellationBean
import com.daqsoft.provider.bean.UserBean
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.usermodule.uitls.ResourceUtil
import java.util.*
import kotlin.collections.HashMap

/**
 * @Description 个人信息更多
 * @ClassName   MoreInformationActivity
 * @Author      PuHua
 * @Time        2019/10/31 16:53
 */
@Route(path = ARouterPath.UserModule.USER_UPDATE_MORE)
class MoreInformationActivity :
    TitleBarActivity<ActivityMoreInformationBinding, MoreInformationViewModel>() {

    override fun setTitle(): String = ResourceUtil.getStringResource(this, R.string.user_more)

    override fun getLayout(): Int = R.layout.activity_more_information

    override fun injectVm(): Class<MoreInformationViewModel> = MoreInformationViewModel::class.java

    override fun initData() {

    }

    override fun initView() {
        mBinding.vm = mModel
    }

    override fun onResume() {
        super.onResume()
        mModel.getUserInformation()
    }
//
//    /**
//     * 地区点击修改
//     */
//    fun updateArea(v: View){
//        var locationUtil = LocationUtil(this,object : LocationUtil.OnLocationSelectListener {
//            override fun onLocationSelect(region: LocationBean) {
//
//            }
//
//        },mModel.mPresenter)
//
//    }

    /**
     * 修改生日
     */
    fun updateBirthday(v: View) {
        UIHelperUtils.showOptionsPicker(this, birthdayPickerView)
    }

    /**
     * 修改星座
     */
    fun updateConstellation(v: View) {
        mModel.getConstellations(object :
            MoreInformationViewModel.OnDataGetListener {
            override fun onDataGet(list: MutableList<ConstellationBean>) {
                constellationPickerView.setPicker(list)
                UIHelperUtils.showOptionsPicker(
                    this@MoreInformationActivity,
                    constellationPickerView
                )
            }

        })
    }

    private val birthdayPickerView by lazy {
        // 时间选择器
        val selectedDate = Calendar.getInstance()
        // 正确设置方式 原因：注意事项有说明
        if (!mModel.userBean.value!!.birthday.isNullOrEmpty()) {
            var birthdays = mModel.userBean.value!!.birthday?.split("-".toRegex())?.dropLastWhile {
                it.isNullOrEmpty()
            }?.toTypedArray()
            if (birthdays != null && birthdays?.size == 3) {
                selectedDate.set(
                    birthdays?.get(0)?.toInt() ?: 0,
                    ((birthdays?.get(1))?.toInt()?.minus(1)) ?: 0,
                    (birthdays?.get(2)?.toInt()) ?: 0
                )
            }
        }

        TimePickerBuilder(this, OnTimeSelectListener { date, v ->
            // 选中事件回调
            mModel.updatePsersonalInformation(
                UpdatePersonalInformationViewModel.birthday,
                Utils.getDateTime(Utils.dateYMD, date)
            )
        })
            .build()
    }

    /**
     * 星座选择器
     */
    private val constellationPickerView by lazy {

        val pV = OptionsPickerBuilder(this, OnOptionsSelectListener { s1, s2, s3, v ->
            mModel.updatePsersonalInformation(
                UpdatePersonalInformationViewModel.constellation,
                mModel.constellationBeans[s1].value
            )
        }).build<ConstellationBean>()
        pV
    }


}

/**
 *
 */
class MoreInformationViewModel : BaseViewModel() {
    /**
     * 个人信息
     */
    var userBean = MutableLiveData<UserBean>()
    /**
     * 星座列表
     */
    var constellationBeans = mutableListOf<ConstellationBean>()

    /**
     * 修改个人信息
     */
    fun updatePsersonalInformation(key: String, value: String) {
        mPresenter.value?.loading = true
        val map: HashMap<String, String> = HashMap()
        map[key] = value
        UserRepository().userService.updateUserInformation(map)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        toast.postValue("修改成功!")
                        getUserInformation()
                    }
                }
            })
    }

    /**
     * 获取更多信息
     */
    fun getUserInformation() {
        mPresenter.value?.loading = true
        UserRepository().userService.getUserInformationMore()
            .excute(object : BaseObserver<UserBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<UserBean>) {
                    userBean.value = response.data

                }
            })
    }

    /**
     * 修改学校
     */
    var updateSchool: ReplyCommand = object :
        ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_UPDATE_INFORMATION)
                .withString(IntentConstant.OBJECT, userBean.value!!.school)
                .withString(
                    IntentConstant.TYPE,
                    UpdatePersonalInformationViewModel.school
                )
                .navigation()
        }

    }
    /**
     * 修改家庭住址
     */
    var updateAddress: ReplyCommand = object :
        ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_UPDATE_INFORMATION)
                .withString(IntentConstant.OBJECT, userBean.value!!.address)
                .withString(
                    IntentConstant.TYPE,
                    UpdatePersonalInformationViewModel.address
                )
                .navigation()
        }

    }

    /**
     * 修改公司名称
     */
    var updateCompany: ReplyCommand = object :
        ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_UPDATE_INFORMATION)
                .withString(IntentConstant.OBJECT, userBean.value!!.workplace)
                .withString(
                    IntentConstant.TYPE,
                    UpdatePersonalInformationViewModel.workplace
                )
                .navigation()
        }

    }
    /**
     * 修改邮箱
     */
    var updateEmail: ReplyCommand = object :
        ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_UPDATE_INFORMATION)
                .withString(IntentConstant.OBJECT, userBean.value!!.email)
                .withString(
                    IntentConstant.TYPE,
                    UpdatePersonalInformationViewModel.email
                )
                .navigation()
        }

    }

    /**
     * 获取用户星座列表
     */
    fun getConstellations(onDataGetListener: OnDataGetListener) {

        mPresenter.value?.loading = true
        UserRepository().userService.getConstellations()
            .excute(object : BaseObserver<ConstellationBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ConstellationBean>) {
                    if (response.code == 0) {
                        constellationBeans.addAll(response.datas!!)
                        onDataGetListener?.onDataGet(response.datas!!)
                    }
                }
            })
    }

    /**
     * 星座数据获取后回调方法
     */
    interface OnDataGetListener {
        fun onDataGet(list: MutableList<ConstellationBean>)
    }


}
