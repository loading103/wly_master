package com.daqsoft.venuesmodule.fragment

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.*
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.businessview.fragment.EditUserContactFragment
import com.daqsoft.provider.businessview.view.PhotoSelectPopWindow
import com.daqsoft.provider.businessview.view.ProviderTipIdCardDialog
import com.daqsoft.provider.businessview.view.ZyTfCodeTipDialog
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.activity.widgets.ManyPeopleResInfoView
import com.daqsoft.venuesmodule.activity.widgets.SingleReserationInfoView
import com.daqsoft.venuesmodule.databinding.FragAppointmentInfoBinding
import com.daqsoft.venuesmodule.utils.FileUtil
import com.daqsoft.venuesmodule.viewmodel.AppointmentInfoViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import com.trello.rxlifecycle3.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * @Description 分时预约信息输入
 * @ClassName  AppointmentInfoFragment
 * @Author      luoyi
 * @Time        2020/8/3 9:43
 */
class AppointmentInfoFragment :
    BaseFragment<FragAppointmentInfoBinding, AppointmentInfoViewModel>() {

    /**
     * 预约类型
     */
    private var reserationType = 0

    private var editUserContactFrag: EditUserContactFragment? = null

    /**
     * 选择身份证弹窗
     */
    private var tipIdCardPhotoDialog: ProviderTipIdCardDialog? = null

    /**
     * 选择照片弹窗
     */
    private var selectPhotoWindow: PhotoSelectPopWindow? = null

    /**
     * rx权限对象
     */
    private var permissions: RxPermissions? = null

    /**
     * 预约所需填写信息人数
     */
    private var orderNeedUserType: Int = 0

    /**
     * 预约类型
     */
    private var orderType: String = "CONTENT_TYPE_VENUE"
    private var orderInfo: VenueOrderViewInfo? = null

    companion object {
        const val RESERATION_TYPE: String = "reseration_type"
        const val RESERATION_ORDER_TYPE: String = "order_type"

        /**
         * 选择照片返回code
         */
        const val SELECT_PHOTO_CODE: Int = 0x11

        /**
         * 拍摄身份证返回code
         */
        const val TAKE_PHOTO_CODE: Int = 0x12

        /**
         * 裁剪照片返回code
         */
        const val CROP_PHOTO_CODE: Int = 0x13

        /**
         * 单人预约选择联系人
         */
        const val SELECT_SINGLE_CONTACT_CODE: Int = 0x14

        /**
         * 多人预约选择联系人
         */
        const val SELECT_MANY_CONTACT_CODE: Int = 0x15

        fun newInstance(reserationType: Int, orderType: String): AppointmentInfoFragment {
            var frag: AppointmentInfoFragment = AppointmentInfoFragment()
            var bundle = Bundle()
            bundle.putInt(RESERATION_TYPE, reserationType)
            bundle.putString(RESERATION_ORDER_TYPE, orderType)
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayout(): Int {
        return R.layout.frag_appointment_info
    }

    override fun injectVm(): Class<AppointmentInfoViewModel> {
        return AppointmentInfoViewModel::class.java
    }

    override fun initView() {
        permissions = RxPermissions(this)
        EventBus.getDefault().register(this)
        getParams()
        initManyPeopleView()
        initSinglePeopleView()
        initViewModel()
    }

    private fun initViewModel() {
        // 健康码配置信息
        mModel.healthSetingLd.observe(this, Observer {
            mBinding.vSinglePeopleReseration.setHealthSetInfo(it)
            mModel.getVipInfo()
        })
        // 健康码和旅游码信息
        mModel.healthInfoAndRegisterLd.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.vSinglePeopleReseration.setHeathInfo(it)
        })
        mModel.healthInfoLd.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.vSinglePeopleReseration.setHeathInfo(it)
        })
        // 健康码区域
        mModel.healthRegionsLd.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.vSinglePeopleReseration.setHealthRegions(it)
        })
        // 旅游码信息
        mModel.travelCodeInfoLd.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.vSinglePeopleReseration.setTravelCode(it)

        })
        mModel.typeList.observe(this, Observer {
            mBinding?.vSinglePeopleReseration?.setCrdenTypes(it)
        })
        //
        mModel.idCardIndentityInfoLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null) {
                if (!it.name.isNullOrEmpty() && !it.idCard.isNullOrEmpty()) {
                    showEditUserContact(
                        Contact(
                            SM4Util.encryptByEcb(it.idCard),
                            "身份证",
                            "", 0,
                            it.name,
                            "",
                            0,
                            0,
                            0,
                            "",
                            null
                            , ""
                        ), 0, mBinding.vManyPeopleReseration.getIsResationPerson()
                    )
                }

            } else {
                ToastUtils.showMessage("非常抱歉，未识别到身份证信息")
            }
        })
        mModel.uploadImgLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (it.isNullOrEmpty()) {
                ToastUtils.showMessage("非常抱歉，上传图片失败")
            }
        })
        // 联系人
        mModel.contacts.observe(this, Observer {
            mBinding.vManyPeopleReseration.setData(it)
        })
        // 异常处理
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
        // 是否需要验证码
        mModel.isNeedCode.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.vSinglePeopleReseration.setIsNeedSmsCode(it)
        })
        // 发送手机验证码
        mModel.sendPhoneCodeLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (it) {
                ToastUtils.showMessage("发送验证码成功")
                initTimer()
            } else {
                ToastUtils.showMessage("发送验证码失败，请稍后再试")
            }
        })
        // 实名认证信息
        observerVipInfo()
    }

    /**
     * 回显实名认证信息
     */
    private fun observerVipInfo() {
        mModel.vipInfold.observe(this, Observer {
            if (it != null) {
                mBinding.vSinglePeopleReseration.setAuthenticationInfo(it.name, it.idCard)
            } else {
                mBinding.vSinglePeopleReseration.setAuthenticationInfo("", "")
            }
        })
    }

    /**
     * 设置预约信息
     */
    fun setData(
        data: VenueOrderViewInfo, reserationType: Int
    ) {
        orderNeedUserType = data.orderUserStatus
        this.reserationType = reserationType
        orderInfo = data

        if (orderNeedUserType == 1) {
            // 单人预预约
            mBinding.vManyPeopleReseration.visibility = View.GONE
            mBinding.vSinglePeopleReseration.visibility = View.VISIBLE
            mBinding.vSinglePeopleReseration.data = data
            mBinding.vSinglePeopleReseration.setData()
            mBinding.vSinglePeopleReseration.changeVenueTipPersonNum(reserationType)
            mModel.getCertTypeList()
            // 请求健康码设置接口
            mModel.getHealthSetingInfo(orderInfo!!.originalOrgId)

        } else if (orderNeedUserType == 2) {
            // 多人预约信息
            mBinding.vManyPeopleReseration.visibility = View.VISIBLE
            mBinding.vSinglePeopleReseration.visibility = View.GONE
            mBinding.vManyPeopleReseration.data = data
            mModel.getContactList()
            mBinding.vManyPeopleReseration.changeVenueTipPersonNum(reserationType)

        }
    }

    fun changeResertionType(reserationType: Int) {
        this.reserationType = reserationType
        if (orderNeedUserType == 1) {
            // 单人预约
            mBinding.vSinglePeopleReseration.changeVenueTipPersonNum(reserationType)
        } else {
            // 多人预约信息
            mBinding.vManyPeopleReseration.changeVenueTipPersonNum(reserationType)
        }
    }

    private fun initSinglePeopleView() {
        mBinding.vSinglePeopleReseration.onSingleReserationListener =
            object : SingleReserationInfoView.OnSingleReserationListener {
                override fun onSendSmsCode(phone: String) {
                    showLoadingDialog()
                    mModel.sendPhoneCode(phone)
                }

                override fun onCheckPhoneCode(phone: String) {
                    mModel.checkExistNumber(phone, orderType)
                }

                override fun onInputValueChanged() {

                }

                override fun getHelathInfoAndReister(
                    name: String, idCard: String, phone: String, region: String
                ) {
                    showLoadingDialog()
                    mModel.getHelathInfoAndReister(name, idCard, phone, region)
                }

                override fun getHealthInfo(
                    phone: String, region: String, name: String, idCard: String
                ) {
                    showLoadingDialog()
                    mModel.getHealthInfo(phone, region, name, idCard)
                }

                override fun getTravelInfo(phone: String, name: String, idCard: String) {
                    showLoadingDialog()
                    mModel.getTravelInfo(phone, name, idCard)
                }

                override fun getHealthRegion() {
                    mModel.getHealthRegion()
                }

                override fun toSelectUserContact() {
                    if (AppUtils.isLogin()) {
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.USER_CONTACT_MANAGEMENT)
                            .withInt("type", 2)
                            .navigation(activity, SELECT_SINGLE_CONTACT_CODE)
                    } else {
                        ToastUtils.showUnLoginMsg()
                        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                            .navigation()
                    }
                }
            }
    }

    private fun initManyPeopleView() {
        mBinding.vManyPeopleReseration.onManyPeopleViewListener =
            object : ManyPeopleResInfoView.OnManyPeopleViewListener {
                override fun showNumPick() {

                }

                override fun showEditInfo(person: Contact, position: Int) {
                    showEditUserContact(person, 0, position)
                }

                override fun toPhotoIdCard(postion: Int) {
                    showSelectPhotoWindow()
                }

                override fun toSelectContact(postion: Int) {
                    if (AppUtils.isLogin()) {
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.USER_CONTACT_MANAGEMENT)
                            .withInt("type", 2)
                            .navigation(activity, SELECT_MANY_CONTACT_CODE)
                    } else {
                        ToastUtils.showUnLoginMsg()
                        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                            .navigation()
                    }
                }

                override fun toSelectContact(contact: Contact) {
                    showEditUserContact(
                        contact,
                        0,
                        mBinding.vManyPeopleReseration.getIsResationPerson()
                    )
                }

                override fun toEditContact(contact: Contact, position: Int) {
                    showEditUserContact(contact, 1, position)
                }


            }
    }

    private fun getParams() {
        reserationType = arguments?.getInt(RESERATION_TYPE) ?: 0
        orderType = arguments?.getString(RESERATION_ORDER_TYPE) ?: "CONTENT_TYPE_VENUE"
    }

    override fun initData() {

    }

    fun getManyPeopleReserataion(): MutableList<ReseartionContact> {
        return mBinding.vManyPeopleReseration.getManyPeopleReravtionInfo()
    }

    fun getSinglePeopleReseration(): ReseartionContactExo? {
        return mBinding.vSinglePeopleReseration.getSinglePeoleReravtionInfo()
    }


    /**
     * 显示编辑用户信息
     * @param mode 0修改信息 1编辑模式
     * @param position 编辑模式联系人位置
     */
    private fun showEditUserContact(contact1: Contact, mode: Int = 0, position: Int = -1) {
        var edtContact = contact1
        if (editUserContactFrag == null) {
            editUserContactFrag = EditUserContactFragment().apply {
                orgId = orderInfo!!.originalOrgId
                orderEditType = orderType
                onEditUserContactListener =
                    object : EditUserContactFragment.OnEditUserContactListener {
                        override fun ondissMiss() {
                            editUserContactFrag = null
                        }

                        override fun backContactData(contact: Contact, mode: Int, position: Int) {
                            // 返回数据
                            if (mode == 0) {
                                mBinding.vManyPeopleReseration.selectPersons(contact)
                            } else {
                                mBinding.vManyPeopleReseration.updateSelectPersons(
                                    contact,
                                    position
                                )
                            }
                        }

                        override fun isContainCertNum(certNum: String): Boolean {
                            return mBinding.vManyPeopleReseration.isContainCertNum(certNum)
                        }
                    }

                // 编辑模式，需要重新加密信息
                if (mode == 1 && contact1 != null) {
                    var contact = Contact(
                        contact1!!.certNumber,
                        contact1!!.certType,
                        contact1!!.contactSn,
                        contact1!!.id,
                        contact1!!.name,
                        contact1!!.phone
                        ,
                        contact1!!.siteId,
                        contact1!!.type,
                        contact1!!.userId,
                        contact1!!.certTypeName,
                        contact1!!.healthInfoBean
                        , ""
                    )
                    if (!contact!!.certNumber.isNullOrEmpty()) {
                        contact!!.certNumber =
                            SM4Util.encryptByEcb(contact!!.certNumber)
                    }
                    if (!contact!!.phone.isNullOrEmpty()) {
                        contact!!.phone = SM4Util.encryptByEcb(contact!!.phone)
                    }
                    edtContact = contact
                }
                userContact = edtContact
                if (orderInfo != null) {
                    orderUserCredentialsType = orderInfo!!.orderUserCredentialsType
                    orderUserInfoType = orderInfo!!.orderUserInfoType
                }
            }
            editUserContactFrag?.reserationType = reserationType
            editUserContactFrag?.mode = mode
            editUserContactFrag?.position = position
        }
        if (!editUserContactFrag!!.isAdded) {
            activity?.let {
                editUserContactFrag?.show(it.supportFragmentManager, "edit_user_contact")
            }
        } else {
            // 更新数据
            editUserContactFrag?.userContact = edtContact
        }
    }


    /**
     * 显示身份拍摄弹窗
     */
    private fun showTipIdCardDialog() {
        if (tipIdCardPhotoDialog == null) {
            tipIdCardPhotoDialog = ProviderTipIdCardDialog(context!!)
            tipIdCardPhotoDialog?.onTipIdCardListener =
                object : ProviderTipIdCardDialog.OnTipIdCardListener {
                    override fun toContinue() {
                        // 跳转到身份证拍摄界面
                        ARouter.getInstance()
                            .build(ARouterPath.Provider.PROVIDER_PHOTO_IDCARD_ACTIVITY)
                            .navigation(activity, TAKE_PHOTO_CODE)
                    }
                }
        }
        if (!tipIdCardPhotoDialog!!.isShowing) {
            tipIdCardPhotoDialog?.show()
        }
    }

    private fun showSelectPhotoWindow() {
        if (selectPhotoWindow == null) {
            selectPhotoWindow = PhotoSelectPopWindow(context!!).apply {
                onPhotoSelectListener =
                    object : PhotoSelectPopWindow.OnPhotoSelectListener {
                        override fun onTakePhoto() {
                            toPermissionOperration {
                                showTipIdCardDialog()
                            }
                        }

                        override fun onSelectPhoto() {
                            // 从相册选择
                            toPermissionOperration {
                                val albumIntent = Intent(Intent.ACTION_PICK, null)
                                albumIntent.setDataAndType(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    "image/*"
                                )
                                activity?.startActivityForResult(albumIntent, SELECT_PHOTO_CODE)
                            }
                        }
                    }
            }
        }
        if (!selectPhotoWindow!!.isShowing) {
            UIHelperUtils
            selectPhotoWindow?.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
        }
    }

    private fun toPermissionOperration(action: () -> Unit) {
        permissions?.request(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )!!.subscribe {
            if (it) {
                action()
            } else {
                ToastUtils.showMessage("完成授权才能使用该功能")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            TAKE_PHOTO_CODE -> {
                // 拍照返回后处理
                if (data != null) {
                    var fileUrl: String? = data?.getStringExtra("result")
                    if (!fileUrl.isNullOrEmpty()) {
                        try {
                            showLoadingDialog()
                            mModel.upLoadFile(File(fileUrl), 0)
                        } catch (e: Exception) {
                            ToastUtils.showMessage("未知错误，请稍后再试")
                        }
                    } else {
                        ToastUtils.showMessage("未知错误，请稍后再试")
                    }
                }
            }
            SELECT_PHOTO_CODE -> {
                if (data != null && data.data != null) {
                    try {
                        var url: String = FileUtil.getPath(context!!.applicationContext, data.data)
                        if (!url.isNullOrEmpty()) {
                            var file = File(url)
                            showLoadingDialog()
                            mModel.upLoadFile(file, 0)
                        }
                    } catch (e: java.lang.Exception) {
                        ToastUtils.showMessage("未知错误，请稍后再试")
                    }


                }
            }
            CROP_PHOTO_CODE -> {
                // 裁剪图片返回后处理
                var bmap: Bitmap? = null
                var mUriCutImg =
                    Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().path + "/" + "small.jpg")
                try { //处理裁剪图片以后
                    bmap = BitmapFactory.decodeStream(
                        context?.getContentResolver()?.openInputStream(mUriCutImg)
                    ) //通过URI获得的图片
//                    dealImgUrl(bmap)
                } catch (e: java.lang.Exception) {
                }

            }
            SELECT_SINGLE_CONTACT_CODE -> {
                // 选择联系人返回
                if (data?.getBundleExtra("bundle") != null) {
                    var bundle = data?.getBundleExtra("bundle")
                    var item = bundle.getParcelable<Contact>("object")
                    mBinding.vSinglePeopleReseration.setContactUs(item)
                }
            }
            SELECT_MANY_CONTACT_CODE -> {
                if (data?.getBundleExtra("bundle") != null) {
                    var bundle = data?.getBundleExtra("bundle")
                    var item = bundle.getParcelable<Contact>("object")
                    if (item != null) {
                        if (!item.phone.isNullOrEmpty()) {
                            item.phone = SM4Util.encryptByEcb(item.phone)
                        }
                        if (!item.certNumber.isNullOrEmpty()) {
                            item.certNumber =SM4Util.encryptByEcb(item.certNumber)
                        }
                        showEditUserContact(
                            item,
                            0,
                            mBinding.vManyPeopleReseration.getIsResationPerson()
                        )
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

//    /**
//     * 裁剪图片方法实现
//     *
//     * @param data
//     */
//    private fun startPhotoZoom(data: Uri) {
//        try {
//            val intent =
//                Intent("com.android.camera.action.CROP")
//            //android4.4版本
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //android7.0+
//                val url: String = FileUtil.getPath(context!!.applicationContext, data)
//                val photoOutputUri: Uri = FileProvider.getUriForFile(
//                    context!!.applicationContext,
//                    context!!.applicationInfo.packageName + ".fileprovider",
//                    File(url)
//                )
//                intent.setDataAndType(photoOutputUri, "image/*")
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                val url: String = FileUtil.getPath(context!!.applicationContext, data)
//                intent.setDataAndType(Uri.fromFile(File(url)), "image/*")
//            } else {
//                intent.setDataAndType(data, "image/*")
//            }
//            // 设置裁剪
//            intent.putExtra("crop", "true")
//            intent.putExtra("aspectX", 320)
//            intent.putExtra("aspectY", 204)
//            intent.putExtra("outputX", 320)
//            intent.putExtra("outputY", 204)
//            // intent.putExtra("noFaceDetection", true);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//            intent.putExtra("return-data", false)
//            var mUriCutImg =
//                Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().path + "/" + "idcard.jpg")
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriCutImg) //将裁剪好图片，存进该URI中
//            startActivityForResult(
//                intent,
//                CROP_PHOTO_CODE
//            )
//        } catch (e: Exception) {
//            ToastUtils.showMessage("程序异常，请稍后再试~")
//        }
//
//    }

//    /**
//     * 保存裁剪之后的图片数据
//     *
//     * @param photo 返回裁剪后的data
//     */
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    private fun dealImgUrl(photo: Bitmap?) {
//        if (photo != null) {
//            val state = Environment.getExternalStorageState()
//            if (state == Environment.MEDIA_MOUNTED) {
//                val outDir = Environment
//                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//                if (!outDir.exists()) {
//                    outDir.mkdirs()
//                }
//                val timeStamp = System.currentTimeMillis().toString() + ""
//                val random = Random()
//                val imageFileName = "idcardImage" + random.nextInt(10000) + timeStamp + ".jpg"
//                try {
//                    val file: File = FileUtil.saveFile(
//                        photo, imageFileName,
//                        outDir.absolutePath + "/wly/"
//                    )
//                    if (file != null) {
//
//                        mModel.upLoadFile(file, 0)
//                    }
//                } catch (e: Exception) {
//
//                }
//            } else {
//            }
//        }
//    }

    /**
     * 计数器
     */
    private var timerDisposable: Disposable? = null

    private fun initTimer() {
        var timeLong: Long = 60
        try {
            timerDisposable?.dispose()
        }catch (e:java.lang.Exception){}
        timerDisposable = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .compose(bindUntilEvent(FragmentEvent.DESTROY))
            .observeOn(AndroidSchedulers.mainThread())
            .map { count ->
                timeLong--
            }
            .subscribe { time ->
                mBinding.vSinglePeopleReseration.setSmsCodeStatus(time > 0, time)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        tipIdCardPhotoDialog = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        permissions = null
        EventBus.getDefault().unregister(this)
        timerDisposable?.dispose()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateContact(evnet: UpdateContactPersonEvent) {
        mModel?.getContactList()
    }

}