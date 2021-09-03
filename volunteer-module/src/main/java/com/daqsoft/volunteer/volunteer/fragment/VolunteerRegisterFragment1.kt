package com.daqsoft.volunteer.volunteer.fragment

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.daqsoft.baselib.base.AppManager
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.databinding.LayoutDialogNoticeBinding
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerBriefInfoBean
import com.daqsoft.volunteer.databinding.FragmentVolunteerRegister1Binding
import com.daqsoft.volunteer.databinding.LayoutDialogIdcardNoticeBinding
import com.daqsoft.volunteer.volunteer.VolunteerRegisterActivity
import com.daqsoft.volunteer.volunteer.vm.VolunteerRegisterVM
import com.trello.rxlifecycle3.android.ActivityEvent
import com.trello.rxlifecycle3.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit

/**
 *@package:com.daqsoft.volunteer.volunteer.fragment
 *@date:2020/6/3 14:13
 *@author: caihj
 *@des:注册界面1
 **/
class VolunteerRegisterFragment1:BaseFragment<FragmentVolunteerRegister1Binding,VolunteerRegisterVM>() {
    override fun getLayout(): Int = R.layout.fragment_volunteer_register_1

    override fun injectVm(): Class<VolunteerRegisterVM> = VolunteerRegisterVM::class.java

    var volunteer: VolunteerBriefInfoBean? = null


    /**
     * 性别选择器
     */
    private val genderPv by lazy {
        val gender = resources.getStringArray(R.array.gender_volunteer)
        val pV = OptionsPickerBuilder(context, OnOptionsSelectListener { s1, s2, s3, v ->
            mBinding.tvSex.text = gender[s1]
            mModel.gender = gender[s1]
        }).build<String>()
        pV.setPicker(gender.asList())
        pV
    }

    /**
     * 民族选择器
     */
    private val nationalPv by lazy {
        val pV = OptionsPickerBuilder(context, OnOptionsSelectListener{ s1, s2, s3, v ->
        mBinding.tvNational.text  = mModel.nationals.value?.get(s1) ?: ""
        mModel.national = mModel.nationals.value?.get(s1) ?: ""
        }).build<String>()
        pV.setPicker(mModel.nationals.value)
        pV
    }


    /**
     * 性别点击修改
     */
    fun updateSex() {
        var selectIndex: Int = when (mModel.gender) {
            "男" -> 0
            "女" -> 1
            else -> 0
        }
        genderPv.setSelectOptions(selectIndex)
        UIHelperUtils.showOptionsPicker(activity!!, genderPv)
    }

    private fun showNationPv(){
        nationalPv.setSelectOptions(0)
        UIHelperUtils.showOptionsPicker(activity!!, nationalPv)
    }

    override fun initView() {
        initEvent()
        mModel.validateStatus.observe(this, Observer {
            if(it){
                val registerActivity = activity as VolunteerRegisterActivity
                registerActivity.next(
                    mModel.name,
                    mModel.gender,
                    mModel.national,
                    mModel.idCard,
                    mModel.phone,
                    mModel.code
                )
            }
        })
        noticeConfirm(activity!!)
    }

    override fun initData() {
        mModel.getNations()
    }

    private fun initEvent(){

        mBinding.tvSex.onNoDoubleClick {
            updateSex()
        }
        mBinding.tvNational.onNoDoubleClick {
            showNationPv()
        }
        mBinding.bindPhoneCodeAvail.onNoDoubleClick {
            mModel.phone = mBinding.etPhone.text.toString()
            if(mModel.phone.isNotEmpty()){
                mModel.senCode()
            }else{
                ToastUtils.showMessage("请输入手机号!")
            }
        }
        mModel.viewListener = object : VolunteerRegisterVM.ViewListener {
            override fun onCountDown() {
                toast("验证码发送成功")
                if (d != null) {
                    shutDown = true
                }
                initTimer()
            }
        }
        mBinding.tvNext.onNoDoubleClick {
            if(checkInput()) {
                mModel.validateCode()
            }
        }
    }

    /**
     * 检测输入
     */
    private fun checkInput():Boolean{
        mModel.name = mBinding.etName.text.toString()
        if(mModel.name.isEmpty()){
            ToastUtils.showMessage("请输入真实姓名!")
            return false
        }
        if(mModel.gender.isEmpty()){
            ToastUtils.showMessage("请选择性别!")
            return false
        }

        if(mModel.national.isEmpty()){
            ToastUtils.showMessage("请选择民族!")
            return false
        }
        mModel.idCard = mBinding.etIdcard.text.toString()
        if(mModel.idCard.isEmpty()){
            ToastUtils.showMessage("请输入身份证号码!")
            return false
        }
        mModel.phone = mBinding.etPhone.text.toString()
        if(mModel.phone.isEmpty()){
            ToastUtils.showMessage("请输入手机号!")
            return false
        }
        mModel.code = mBinding.etCode.text.toString()
        if(mModel.code.isEmpty()){
            ToastUtils.showMessage("请输入验证码!")
            return false
        }

        if(!mBinding.check.isChecked){
            ToastUtils.showMessage("请选择同意服务协议!")
            return false
        }

        return true
    }

    /**
     * 弹出确认框
     */
    private fun noticeConfirm(context: Activity) {
        val dialog = AlertDialog.Builder(AppManager.instance.currentActivity()).create()
        dialog.show()
        val window = dialog.window
        val binding = DataBindingUtil.inflate<LayoutDialogIdcardNoticeBinding>(
            context.layoutInflater,
            R.layout.layout_dialog_idcard_notice, null, false
        )

        window!!.setContentView(binding.root)
        binding.versionSure.onNoDoubleClick {
            dialog.dismiss()
        }
    }

    /**
     * 计时器相关
     */
    var d: Disposable? = null
    var shutDown = false
    fun initTimer() {
        var timeLong: Long = 60
        d = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .compose(bindUntilEvent(FragmentEvent.DESTROY))
            .observeOn(AndroidSchedulers.mainThread())
            .takeWhile { timeLong >= 0 }
            .map { count ->
                timeLong--
            }
            .subscribe { time ->
                if (time > 0) {
                    var to = getString(R.string.volunteer_module_str_count_down, time)
                    mBinding.bindPhoneCodeAvail.text = to
                    mBinding.bindPhoneCodeAvail.isEnabled = false
                } else {
                    mBinding.bindPhoneCodeAvail.text = getString(R.string.volunteer_module_register_label_hint_send_code)
                    mBinding.bindPhoneCodeAvail.isEnabled = true
                }

            }
        shutDown = false
    }
}