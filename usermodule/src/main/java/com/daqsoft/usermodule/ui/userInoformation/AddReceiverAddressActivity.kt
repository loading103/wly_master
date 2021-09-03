package com.daqsoft.usermodule.ui.userInoformation

import android.os.Bundle
import androidx.databinding.ObservableField
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.baselib.bean.LocationData
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityAddReceiveAddressBinding
import com.daqsoft.usermodule.repository.constant.IntentConstant
import com.daqsoft.provider.bean.ReceiveAddressBean
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.baselib.utils.LocationUtil
import com.daqsoft.provider.utils.SoftHideKeyBoardUtil
import com.daqsoft.usermodule.uitls.StringUtils
import me.nereo.multi_image_selector.utils.BitmapUtils
import org.jetbrains.anko.toast

/**
 * 新增收货地址
 */
@Route(path = ARouterPath.UserModule.USER_ADD_RECEIVE_ADDRESS)
class AddReceiverAddressActivity :
    TitleBarActivity<ActivityAddReceiveAddressBinding, AddReceiverAddressViewModel>() {

    /**
     * 地区编码
     */
    var region = ""
    @JvmField
    @Autowired(name = IntentConstant.OBJECT)
    var receiveAddressBean: ReceiveAddressBean? = null

    override fun getLayout(): Int = R.layout.activity_add_receive_address

    override fun setTitle(): String = getString(R.string.user_add_address)

    override fun injectVm(): Class<AddReceiverAddressViewModel> =
        AddReceiverAddressViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoftHideKeyBoardUtil.assistActivity(this)
    }

    override fun initView() {
        mBinding.area.setOnClickListener {
            // 事发地
            BitmapUtils.hideInputWindow(this)
            LocationUtil(this, object : LocationUtil.OnLocationSelectListener {
                override fun onLocationSelect(data: LocationData) {
                    region = data.region
                    mBinding.area.setText(data.memo)
                }

            }, mModel.mPresenter)
        }
        mModel.receiveAddressBean.set(this@AddReceiverAddressActivity.receiveAddressBean)
        mBinding.vm = mModel
        if (receiveAddressBean != null) {
            mBinding.receiver.setText(receiveAddressBean!!.consignee)
            if (receiveAddressBean!!.address.split(",") != null
                && receiveAddressBean!!.address.split(",").size >= 2
            ) {
                mBinding.address.setText(receiveAddressBean!!.address.split(",")[1])
                mBinding.area.setText(receiveAddressBean!!.address.split(",")[0])
            }
            region = receiveAddressBean!!.area
            mBinding.phone.setText(receiveAddressBean!!.phone)
            mBinding.check.isSelected = receiveAddressBean!!.isDefault
        } else {
            mBinding.receiver.setText("")
            mBinding.address.setText("")
            mBinding.area.setText("")
            mBinding.phone.setText("")
            region = ""
            mBinding.check.isSelected = false
        }
        StringUtils().setProhibitEmoji(mBinding.receiver)
        StringUtils().setProhibitEmoji(mBinding.address)

        mBinding.submit.onNoDoubleClick {
        add()
    }

    }

    override fun initData() {
            mBinding.check.onNoDoubleClick {
                mBinding.check.isSelected = !mBinding.check.isSelected
            }
    }

    /**
     * 新增
     */
    fun add() {

        val name = mBinding.receiver.text.toString().trim()
        if(name.isNullOrEmpty()){
            toast("请输入收货人姓名！")
            return
        }
        val phone = mBinding.phone.text.toString().trim()
        if(phone.isNullOrEmpty()){
            toast("请输入收货联系方式！")
            return
        }
        if(region.isNullOrEmpty()){
            toast("请输入收货地址！")
            return
        }

        if (receiveAddressBean != null) {
            mModel.updateReceiveAddres(
                mBinding.check.isSelected,
                mBinding.address.text.toString().trim(),
                mBinding.phone.text.toString().trim(),
                region,
                mBinding.receiver.text.toString().trim(),
                receiveAddressBean!!.id.toString()
            )
        } else {

            mModel.AddReceiverAddress(
                mBinding.check.isSelected,
                mBinding.address.text.toString().trim(),
                mBinding.phone.text.toString().trim(),
                region,
                mBinding.receiver.text.toString().trim()
            )
        }
    }

}

/**
 * 新增收货地址
 */
class AddReceiverAddressViewModel : BaseViewModel() {
//    /**
//     * 收货人
//     */
//    var receiver = ObservableField<String>("")
//    /**
//     * 手机号
//     */
//    var phone = ObservableField<String>("")
//    /**
//     * 地址
//     */
//    var area = ObservableField<LocationBean>()
//    /**
//     * 详细地址
//     */
//    var address = ObservableField<String>()
//    /**
//     * 是否是默认地址
//     */
//    var isDefault = ObservableField<Boolean>()

    var receiveAddressBean = ObservableField<ReceiveAddressBean>()

    /**
     * 新增收货地址
     */
    fun AddReceiverAddress(
        isDefault: Boolean,
        address: String,
        phone: String,
        region: String,
        receiver: String
    ) {
        mPresenter.value?.loading = true
        UserRepository().userService.addReceiveAddres(
            isDefault!!,
            address!!,
            phone!!,
            region!!,
            receiver!!
        )
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0){
                        toast.postValue("保存成功!")
                        finish.postValue(true)
                    }else{
                        toast.postValue(response.message)
                    }
                }

            })
    }

    /**
     * 修改收货地址
     */
    fun updateReceiveAddres(
        isDefault: Boolean,
        address: String,
        phone: String,
        region: String,
        receiver: String,
        id: String
    ) {
        mPresenter.value?.loading = true
        UserRepository().userService.updateReceiveAddres(
            isDefault!!,
            address!!,
            phone!!,
            region!!,
            receiver!!,
            id
        )
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0)
                        finish.postValue(true)
                }

            })
    }

}