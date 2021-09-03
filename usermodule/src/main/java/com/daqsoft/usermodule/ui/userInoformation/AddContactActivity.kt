package com.daqsoft.usermodule.ui.userInoformation

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.View
import android.widget.EditText
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SM4Util
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.ConstellationBean
import com.daqsoft.provider.bean.Contact
import com.daqsoft.provider.bean.UpdateContactPersonEvent
import com.daqsoft.provider.event.UpdateContactEvent
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityAddContactBinding
import com.daqsoft.usermodule.repository.constant.IntentConstant
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.usermodule.uitls.StringUtils
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast
import java.util.regex.Pattern

/**
 * 新增收货地址
 */
@Route(path = ARouterPath.UserModule.USER_ADD_CONTACT)
class AddContactActivity :
    TitleBarActivity<ActivityAddContactBinding, AddContactActivityViewModel>() {

    @JvmField
    @Autowired(name = IntentConstant.OBJECT)
    var receiveAddressBean: Contact? = null

    override fun getLayout(): Int = R.layout.activity_add_contact

    override fun setTitle(): String = getString(R.string.user_add_contact)

    override fun injectVm(): Class<AddContactActivityViewModel> =
        AddContactActivityViewModel::class.java

    override fun initPageParams() {
        isInitImmerBar = false
    }

    override fun initView() {

        if (receiveAddressBean != null) {
            setTitleContent("编辑联系人")
            mBinding.label.text = "编辑联系人"
        }
        mModel.receiveAddressBean.set(this@AddContactActivity.receiveAddressBean)
        mBinding.vm = mModel
        if (receiveAddressBean != null) {
            mBinding.receiver.setText(receiveAddressBean!!.name)
            mBinding.certNumber.setText(receiveAddressBean!!.certNumber)
            mBinding.certType.text = receiveAddressBean!!.certType
            mBinding.phone.setText(receiveAddressBean!!.phone)
        } else {
            mBinding.receiver.setText("")
            mBinding.certNumber.setText("")
            mBinding.certType.text = ""
            mBinding.phone.setText("")
        }
        mBinding.certType.setOnClickListener {
            UIHelperUtils.hideKeyboard(it)
            UIHelperUtils.showOptionsPicker(this, certTypePv)
        }

        StringUtils().setProhibitEmoji(mBinding.receiver)
        StringUtils().setProhibitEmoji(mBinding.certNumber)
        mModel.constellationBeans.observe(this, Observer {
            certTypePv.setPicker(it)
            if (receiveAddressBean != null) {
                for (i in 0 until it.size) {
                    if (receiveAddressBean!!.certType == it[i].value) {
                        mModel.certType = it[i]
                        break
                    }
                }
            }
        })
        mModel.getCertTypeList()
    }

    override fun initData() {
        mBinding.submit.onNoDoubleClick {
            add()
        }
    }

    /**
     * 新增/修改
     */
    fun add() {

        val name = mBinding.receiver.text.toString().trim()
        if (name.isNullOrEmpty()) {
            toast("请输入联系人姓名！")
            return
        }
        val phone = mBinding.phone.text.toString().trim()
        if (phone.isNullOrEmpty()) {
            toast("请输入联系方式！")
            return
        }


        if (receiveAddressBean != null) {
            mModel.updateContact(
                mBinding.receiver.text.toString().trim(),
                mModel.certType?.value,
                mBinding.phone.text.toString().trim(),
                mBinding.certNumber.text.toString().trim(),
                receiveAddressBean!!.id.toString()
            )
        } else {
            mModel.addContact(
                mBinding.receiver.text.toString().trim(),
                mBinding.phone.text.toString().trim(), mBinding.certNumber.text.toString().trim()
            )
        }
    }


    /**
     * 证件选择器
     */
    private val certTypePv by lazy {

        val pV = OptionsPickerBuilder(this, OnOptionsSelectListener { s1, s2, s3, v ->
            mBinding.certType.text = mModel.constellationBeans.value!![s1].name
            mModel.certType = mModel.constellationBeans.value!![s1]

        }).build<ConstellationBean>()
        pV
    }


}

/**
 * 新增收货地址
 */
class AddContactActivityViewModel : BaseViewModel() {


    var receiveAddressBean = ObservableField<Contact>()

    /**
     * 证件类型列表
     */
    var constellationBeans = MutableLiveData<MutableList<ConstellationBean>>()

    /**
     * 当前选中的类型
     */
    var certType: ConstellationBean? = null

    /**
     * 新增联系人
     */
    fun addContact(name: String, phone: String, certNumber: String) {
        mPresenter.value?.loading = true
        var idCard = ""
        if (certType != null) {
            idCard = certType!!.value
        }
        UserRepository().userService.addContact(name!!, idCard, SM4Util.encryptByEcb(phone!!), SM4Util.encryptByEcb(certNumber!!))
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        EventBus.getDefault().post(UpdateContactPersonEvent())
                        finish.postValue(true)
                        toast.postValue("保存成功!")
                    }
                }

            })
    }

    /**
     * 修改联系人
     */
    fun updateContact(
        name: String,
        certType: String?,
        phone: String,
        certNumber: String?,
        id: String
    ) {
        mPresenter.value?.loading = true
        var paramMap = HashMap<String, String>()
        paramMap["name"] = SM4Util.encryptByEcb(name)
        paramMap["phone"] = SM4Util.encryptByEcb(phone)
        paramMap["id"] = id
        if (!certType.isNullOrEmpty()) {
            paramMap["certType"] = certType
        }
        if (!certNumber.isNullOrEmpty()) {
            paramMap["certNumber"] = SM4Util.encryptByEcb(certNumber)
        }
        UserRepository().userService.updateContact(paramMap)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0)
                        finish.postValue(true)
                }

            })
    }

    /**
     * 获取证件类型列表
     */
    fun getCertTypeList() {
        mPresenter.value?.loading = true
        UserRepository().userService.getCertTypeList()
            .excute(object : BaseObserver<ConstellationBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ConstellationBean>) {
                    if (response.code == 0)
                        constellationBeans.postValue(response.datas)

                }

            })
    }


}