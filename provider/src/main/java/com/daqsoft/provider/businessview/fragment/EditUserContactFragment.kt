package com.daqsoft.provider.businessview.fragment

import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.nfc.cardemulation.HostNfcFService
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.daqsoft.baselib.base.BaseDialogFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.utils.*
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.businessview.view.UnRegsiterHealthCodeDialog
import com.daqsoft.provider.businessview.view.ZyTfCodeTipDialog
import com.daqsoft.provider.businessview.viewmodel.EditUserContactViewModel
import com.daqsoft.provider.databinding.PopWindowEditUserContactBinding
import com.daqsoft.provider.view.LabelsView
import com.trello.rxlifecycle3.RxLifecycle.bindUntilEvent
import com.trello.rxlifecycle3.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.pop_window_edit_user_contact.*
import kotlinx.android.synthetic.main.pop_window_edit_user_contact.view.*
import org.jetbrains.anko.support.v4.intentFor
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   EditUserContactFragment
 * @Author      luoyi
 * @Time        2020/8/4 14:29
 */
class EditUserContactFragment :
    BaseDialogFragment<PopWindowEditUserContactBinding, EditUserContactViewModel>() {

    var onEditUserContactListener: OnEditUserContactListener? = null

    var userContact: Contact? = null

    /**
     * 用户名称
     */
    var userName: String? = ""

    /**
     * 手机号码
     */
    var phone: String? = ""

    /**
     * 证件号码
     */
    var certNumber: String? = ""

    /**
     * 用户预订需要的证件类型
     */
    var orderUserCredentialsType: String? = ""

    /**
     * 支持预约得证件类型
     */
    var supportCredentTypes: MutableList<String> = mutableListOf()

    /**
     * 用户预订所需要的基本信息
     */
    var orderUserInfoType: String? = ""

    var healthSetingInfo: HelathSetingBean? = null

    var zyTfCodeTipDialog: ZyTfCodeTipDialog? = null

    var unregisterHealthCodeDialog: UnRegsiterHealthCodeDialog? = null

    var isInitHealth: Boolean = true

    /**
     * 健康码地区数据集
     */
    var mDatasHealthRegions: MutableList<HelathRegionBean> = mutableListOf()

    var healthInfo: HelathInfoBean? = null

    var currentRegion: HelathRegionBean? = null

    /**
     * 是否可以预约
     */
    var isCanReservation: Int = 0

    /**
     * 是否需要短信验证码
     */
    var isNeedSmsCode: Boolean = true

    var typeSelector: OptionsPickerView<ConstellationBean>? = null

    var crdentTypes: MutableList<ConstellationBean> = mutableListOf()

    /**
     * 操作模式
     *  0 修改模式 1 编辑模式
     */
    var mode: Int = 0

    /**
     * 编辑位置
     */
    var position: Int = -1

    var isShowZyCode: Boolean = false

    /**
     * 个人预约和单人预约
     */
    var reserationType: Int = 0

    var orgId: Int = 0
    var orderEditType: String = ResourceType.CONTENT_TYPE_VENUE


    override fun getLayout(): Int {
        return R.layout.pop_window_edit_user_contact
    }

    override fun injectVm(): Class<EditUserContactViewModel> {
        return EditUserContactViewModel::class.java
    }

    override fun initView() {
        userContact?.let {
            if (!it.name.isNullOrEmpty()) {
                mBinding.edtUserName.setText("" + it.name)
                userName = it.name
            }
            if (!it.phone.isNullOrEmpty()) {
                var phone: String = SM4Util.decryptByEcb(it.phone)
                mModel.checkExistNumber(phone, orderEditType)
                mBinding.edtPhoneNumber.setText("" + phone)
            }
        }
        if (position != 0 && position != -1) {
            isNeedSmsCode = false
            setIsNeedSmsCodeUi(isNeedSmsCode)
        }
        initViewEvent()
        initViewModel()
    }

    private fun initViewModel() {
        // 健康码配置信息
        mModel.healthSetingLd.observe(this, Observer {
            if (it != null) {
                healthSetingInfo = it
                if (!it.enableHealthyCode && !it.enableTravelCode) {
                    mBinding.vPersonHealthInfo.visibility = View.GONE
                    isCanReservation = 1
                } else {
                    if (it.enableHealthyCode) {
                        ll_health_code_info.visibility = View.VISIBLE
                        mModel.getHealthRegion()
                        if (!it.reserveOption.isNullOrEmpty() && it.reserveOption == "LowRisk") {
//                            onHealthInfoListener?.onShowHealthTip()
                        }
                    } else {
                        llv_zy_code_info.visibility = View.VISIBLE
                        setIdCardAndPhone(
                            userContact?.phone,
                            userContact?.certNumber,
                            userContact?.name
                        )
                        if (!healthSetingInfo!!.smartTravelName.isNullOrEmpty()) {
                            mBinding.tvTravelCodeName.text = getString(
                                R.string.travel_code_name,
                                healthSetingInfo!!.smartTravelName
                            )
                        }
                    }
                }

            } else {
                mBinding.vPersonHealthInfo.visibility = View.GONE
            }
        })
        mModel.healthInfoLd.observe(this, Observer {
            dissMissLoadingDialog()
            healthInfo = it
            healthInfo?.healthSetingBean = healthSetingInfo
            if (it != null) {
                showHealthCodeInfo(it)
            } else {
                showUnregisterHealthDialog()
                showUnRegiseterHealthCodeInfo()
            }
        })
        mModel.healthInfoAndRegisterLd.observe(this, Observer {
            dissMissLoadingDialog()
            healthInfo = it
            healthInfo?.healthSetingBean = healthSetingInfo
            if (it != null) {
                showHealthCodeInfo(it)
            }
        })
        mModel.healthRegionsLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (!it.isNullOrEmpty()) {
                mDatasHealthRegions.clear()
                mDatasHealthRegions.addAll(it)
                var selectPos = -1
                var temps: MutableList<String> = mutableListOf()
                for (i in mDatasHealthRegions.indices) {
                    var item = mDatasHealthRegions[i]
                    if (item != null && !item.name.isNullOrEmpty()) {
                        if (userContact != null && userContact!!.healthInfoBean != null) {
                            if (!item.region.isNullOrEmpty() && !userContact!!.healthInfoBean!!.region.isNullOrEmpty()) {
                                if (item.region == userContact!!.healthInfoBean!!.region) {
                                    selectPos = i
                                }
                            }
                        }
                        temps.add(item.name)
                    }
                }

                // 编辑模式，回填健康码信息
                if (mode == 1 && selectPos != -1 && selectPos in temps.indices) {
                    mBinding.llvHealthTypies.setLabels(temps, selectPos)
                } else {
                    mBinding.llvHealthTypies.setLabels(temps)
                }
                mBinding.llvHealthTypies.maxLines = 2
                mBinding.tvHealthAddress.visibility = View.VISIBLE

            }
        })
        mModel.travelCodeInfoLd.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.llvZyCodeInfo.visibility = View.VISIBLE
            mBinding.tvUnInputInfo.visibility = View.GONE
            // 旅游码
            if (it) {
                // 注册
                mBinding.imgHealthCodeStatus.setImageResource(R.mipmap.venue_book_condition_icon_low)
                mBinding.tvHealthCodeStatus.text = "已注册"
                mBinding.tvHealthCodeStatus.setTextColor(context!!.resources.getColor(R.color.c_36cd64))
            } else {
                // 未注册
                mBinding.imgHealthCodeStatus.setImageResource(R.mipmap.venue_book_condition_icon_unknown)
                mBinding.tvHealthCodeStatus.text = "未注册"
                mBinding.tvHealthCodeStatus.setTextColor(context!!.resources.getColor(R.color.color_999))
            }
            if (!healthSetingInfo!!.enableHealthyCode) {
                isCanReservation = 1
            }
        })
        // 异常处理
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
        // 是否需要验证码
        mModel.isNeedCode.observe(this, Observer {
            dissMissLoadingDialog()
            if (position == 0 || position == -1) {
                isNeedSmsCode = it
                setIsNeedSmsCodeUi(it)
            }
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
        // 证件类型列表
        mModel.typeList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                crdentTypes.clear()
                for (item in it) {
                    if (!supportCredentTypes.isNullOrEmpty()) {
                        if (supportCredentTypes.contains(item.value)) {
                            crdentTypes.add(item)
                        }
                    }
                }
                initCrdentTypes()
            }
        })
        mModel.vipInfold.observe(this, Observer {
            setAuthenticationInfo(it?.name, it?.idCard)
        })
    }

    private fun setIsNeedSmsCodeUi(it: Boolean) {
        if (it && (position == 0 || position == -1)) {
            mBinding.edtVenueReservationPpcodeValue.visibility = View.VISIBLE
            mBinding.tvVenueRtnPhoneCode.visibility = View.VISIBLE
            mBinding.tvVenueRtnSendCode.visibility = View.VISIBLE
            mBinding.vLineThree.visibility = View.VISIBLE
        } else {
            mBinding.edtVenueReservationPpcodeValue.visibility = View.GONE
            mBinding.tvVenueRtnPhoneCode.visibility = View.GONE
            mBinding.tvVenueRtnSendCode.visibility = View.GONE
            mBinding.vLineThree.visibility = View.GONE
        }
    }

    /**
     * 证件类型
     */
    private fun initCrdentTypes() {
        typeSelector = OptionsPickerBuilder(context) { o1, o2, o3, v ->
            if (o1 < crdentTypes.size) {
                var item = crdentTypes[o1]
                mModel.currentCrdentType = item.value
                mModel.currentCrdentName = item.name
                mBinding.tvVenueRtnTypeValue.text = item.name
                mBinding.llvHealthTypies.clearAllSelect()
                hideHealthInfoView()
                if (item.value != "ID_CARD") {
                    isCanReservation = 1
                } else {
                    var idcardNum: String = mBinding.edtVenueRtnIdcardValue.text.toString()
                    if (!idcardNum.isNullOrEmpty()) {
                        mBinding.edtVenueRtnIdcardValue.setText("" + idcardNum)
                    }
                }
                healthInfo = null
                updateCertNum(item)
            }
        }.setDecorView(mBinding.rlEditUserContactMain).build<ConstellationBean>()
        if (!crdentTypes.isNullOrEmpty()) {
            typeSelector?.setPicker(crdentTypes)
            backfillCredType()
        }
        // 回显实名认证信息  1.编辑模式 不需要回填  2.预约人才需要回填 3.传入有联系人信息不需要回填
        if (position == 0 &&
            userContact?.phone.isNullOrEmpty() && userContact?.certNumber.isNullOrEmpty() && userContact?.name.isNullOrEmpty()
        ) {
            mModel.getVipInfo()
        }
    }

    /**
     * 回填证件号码信息
     */
    private fun updateCertNum(item: ConstellationBean) {
        if (!item.name.isNullOrEmpty() && !userContact?.certType.isNullOrEmpty() && (item.name == userContact!!.certType
                    || item.value == userContact!!.certType)
        ) {
            try {
                var idCardNum = SM4Util.decryptByEcb(
                    userContact?.certNumber
                )
                mBinding.edtVenueRtnIdcardValue.setText(
                    "" + idCardNum
                )
                // 如果是身份证，查看健康码信息是否可以用
                if (isIdCardType()) {
                    if (healthSetingInfo != null) {
                        setIdCardAndPhone(phone, SM4Util.encryptByEcb(idCardNum), userName)
                    } else {
                        mModel.getHealthSetingInfo(orgId)
                    }
                }
            } catch (e: Exception) {
            }

        }
    }


    private fun initViewEvent() {
        mBinding.llvHealthTypies.setOnLabelSelectChangeListener(object :
            LabelsView.OnLabelSelectChangeListener {
            override fun onLabelSelectChange(
                label: TextView?,
                data: Any?,
                isSelect: Boolean,
                position: Int
            ) {
                if (isSelect) {
                    label?.setTextColor(resources.getColor(R.color.c_36cd64))
                    label?.setBackgroundResource(R.drawable.shape_venue_selected_r3)
                    if (position < mDatasHealthRegions.size) {
                        var region = mDatasHealthRegions[position]
                        if (region != null) {
                            currentRegion = region
                            // 取消第一次初始化判断
                            if (!isInitHealth) {
                                if (userName.isNullOrEmpty()) {
                                    ToastUtils.showMessage("请输入您的真实姓名")
                                } else if (phone.isNullOrEmpty()) {
                                    ToastUtils.showMessage("请输入您的手机号")
                                } else if (certNumber.isNullOrEmpty()) {
                                    ToastUtils.showMessage("请输入您的身份证信息")
                                } else {
                                    if (healthSetingInfo!!.enableHealthyCode && healthSetingInfo!!.enableTravelCode) {
                                        showLoadingDialog()
                                        mModel.getHelathInfoAndReister(
                                            userName!!,
                                            certNumber!!,
                                            phone!!,
                                            currentRegion!!.getCurrentRegion()
                                        )
                                    } else {
                                        showLoadingDialog()
                                        mModel.getHealthInfo(
                                            phone!!,
                                            currentRegion!!.getCurrentRegion(),
                                            userName!!,
                                            certNumber!!
                                        )
                                    }
                                }
                            } else {
                                setIdCardAndPhone(phone, certNumber, userName)
                            }
                        }
                    }
                } else {
                    label?.setTextColor(resources.getColor(R.color.color_333))
                    label?.setBackgroundResource(R.drawable.shape_venue_default_r3)
                }
                isInitHealth = false
            }
        })

        mBinding.vZytfCodeInfo.onNoDoubleClick {
            if (healthSetingInfo != null) {
                var name = healthSetingInfo?.smartTravelName
                ARouter.getInstance().build(ARouterPath.VenuesModule.VENUES_ZYTF_CODE_INFO_ACTIVITY)
                    .withString("introduce", healthSetingInfo!!.healthIntroduce)
                    .withString("name", name)
                    .navigation()
            }
        }

        mBinding.tvTipToRegisterHealthCode.onNoDoubleClick {
            showUnregisterHealthDialog()
        }
        mBinding.llvHealthTypies.setmOnLabelShowMoreListener(object :
            LabelsView.OnLabelShowMoreListener {
            override fun onLabeShowLoadMore() {
                mBinding.tvMoreHealthTyoe.visibility = View.VISIBLE
            }
        })
        mBinding.imgCloseDialog.onNoDoubleClick {
            dismiss()
        }
        mBinding.tvConfirmInput.onNoDoubleClick {
            var phone: String? = mBinding.edtPhoneNumber.text.toString()
            var name: String? = mBinding.edtUserName.text.toString()
            var idCard: String? = mBinding.edtVenueRtnIdcardValue.text.toString()
            var companyName: String? = mBinding.edtVenueRtnInCompanyNameVlaue.text.toString()
            var smsCode: String? = mBinding.edtVenueReservationPpcodeValue.text.toString()
            if (name.isNullOrEmpty()) {
                ToastUtils.showMessage("请输入姓名~")
                return@onNoDoubleClick
            }
            if (phone.isNullOrEmpty()) {
                ToastUtils.showMessage("请输入手机号~")
                return@onNoDoubleClick
            }
            if (!RegexUtil.isMobilePhone(phone)) {
                ToastUtils.showMessage("请输入正确的手机号~")
                return@onNoDoubleClick
            }
            if (idCard.isNullOrEmpty()) {
                ToastUtils.showMessage("请输入证件号~")
                return@onNoDoubleClick
            }
            if (isIdCardType() && idCard.length != 18) {
                ToastUtils.showMessage("请输入正确的身份证号~")
                return@onNoDoubleClick
            }
            if (RegexUtil.isChinese(idCard)) {
                ToastUtils.showMessage("证件号不能输入中文字符~")
                return@onNoDoubleClick
            }
            if (isNeedSmsCode && smsCode.isNullOrEmpty()) {
                ToastUtils.showMessage("请输入验证码~")
                return@onNoDoubleClick
            }
            if (mode == 0 && onEditUserContactListener != null && onEditUserContactListener!!.isContainCertNum(
                    idCard
                )
            ) {
                ToastUtils.showMessage("非常抱歉，已添加相同证件号的用户！")
                return@onNoDoubleClick
            }
            if (isIdCardType() && healthSetingInfo != null && isCanReservation != 1) {
                ToastUtils.showMessage("当前用户健康码状况，不可预约，请换一位用户试试~")
                return@onNoDoubleClick
            }

            if (!phone.isNullOrEmpty() && !name.isNullOrEmpty() && !idCard.isNullOrEmpty() && !mModel.currentCrdentType.isNullOrEmpty()) {
                healthInfo?.healthRegionAddress = currentRegion?.name
                onEditUserContactListener?.backContactData(
                    Contact(
                        idCard, mModel.currentCrdentType, "", if (userContact != null) {
                            userContact!!.id
                        } else {
                            -1
                        }, name, phone, 0,
                        1, 0, mModel.currentCrdentName, healthInfo, companyName
                    ), mode, position
                )
                dismiss()
            }
        }
        mBinding.tvMoreHealthTyoe.onNoDoubleClick {
            if (mBinding.llvHealthTypies.maxLines == -1) {
                mBinding.llvHealthTypies.maxLines = 2
                val topDrawable = resources.getDrawable(R.mipmap.provider_arrow_down)
                topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
                mBinding.tvMoreHealthTyoe.setCompoundDrawables(null, null, topDrawable, null)
                mBinding.tvMoreHealthTyoe.text = "查看更多"
            } else {
                mBinding.tvMoreHealthTyoe.text = "收起更多"
                mBinding.llvHealthTypies.maxLines = -1
                val topDrawable = resources.getDrawable(R.mipmap.provider_arrow_up)
                topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
                mBinding.tvMoreHealthTyoe.setCompoundDrawables(null, null, topDrawable, null)
            }
        }
        // 发送验证码
        mBinding.tvVenueRtnSendCode.onNoDoubleClick {
            var phoneNumer: String? = mBinding.edtPhoneNumber.text.toString()
            if (phoneNumer.isNullOrEmpty()) {
                ToastUtils.showMessage("请先输入手机号码")
            } else if (!RegexUtil.isMobilePhone(phoneNumer)) {
                ToastUtils.showMessage("请输入正确的手机号码")
            } else {
                showLoadingDialog()
                mModel.sendPhoneCode(phoneNumer)
            }
        }
        mBinding.tvVenueRtnTypeValue.onNoDoubleClick {
            typeSelector?.show()
        }
        mBinding.edtPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.length == 11) {
                    onInputValueChanged()
                    mModel.checkExistNumber(s.toString(), orderEditType)
                } else {
                    hideHealthInfoView()
                }
            }

        })
        mBinding.edtVenueRtnIdcardValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.length == 18) {
                    onInputValueChanged()
                } else {
                    hideHealthInfoView()
                }
            }

        })
        mBinding.edtUserName.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())
                ) {
                    var s: String? = mBinding.edtUserName.text.toString()
                    if (!s.isNullOrEmpty()) {
                        onInputValueChanged()
                    } else {
                        hideHealthInfoView()
                    }
                }
                return false
            }

        })
    }

    private fun onInputValueChanged() {
        var phone: String? = mBinding.edtPhoneNumber.text.toString()
        var name: String? = mBinding.edtUserName.text.toString()
        var idCard: String? = mBinding.edtVenueRtnIdcardValue.text.toString()
        if (isIdCardType() && !phone.isNullOrEmpty() && !name.isNullOrEmpty() && !idCard.isNullOrEmpty()) {
            setIdCardAndPhone(SM4Util.encryptByEcb(phone), SM4Util.encryptByEcb(idCard), name)
        }
    }


    override fun initData() {
        if (!orderUserCredentialsType.isNullOrEmpty()) {
            var crdentTypes = orderUserCredentialsType!!.split(",")
            if (!crdentTypes.isNullOrEmpty()) {
                supportCredentTypes.clear()
                supportCredentTypes.addAll(crdentTypes)
                if (supportCredentTypes.size == 1) {
                    mBinding?.imgVenueRtnType?.visibility = View.GONE
                } else {
                    mBinding?.imgVenueRtnType?.visibility =
                        View.VISIBLE
                }
                if (crdentTypes.contains("ID_CARD") || crdentTypes.contains("id_card")) {
                    mModel.getHealthSetingInfo(orgId)
                } else {
                    hideHealthInfoView()
                }
            }
            mModel.getCertTypeList()
        }


    }

    override fun onStart() {
        super.onStart()
        initDialogWindow()
    }

    private fun initDialogWindow() {
        val win = dialog!!.window
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.transparent)))
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        val params = win.attributes
        params.gravity = Gravity.BOTTOM
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        win.attributes = params
        win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            typeSelector = null
        } catch (e: Exception) {
        }

    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onEditUserContactListener?.ondissMiss()
    }

    interface OnEditUserContactListener {
        fun ondissMiss()
        fun backContactData(contact: Contact, mode: Int = 0, position: Int = -1)
        fun isContainCertNum(certNum: String): Boolean
    }

    /**
     * 计数器
     */
    private var timerDisposable: Disposable? = null

    private fun initTimer() {
        var timeLong: Long = 60
        try {
            timerDisposable?.dispose()
        } catch (e: Exception) {
        }

        timerDisposable = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .compose(bindUntilEvent(FragmentEvent.DESTROY))
            .observeOn(AndroidSchedulers.mainThread())
            .map { count ->
                timeLong--
            }
            .subscribe { time ->
                if (time > 0) {
                    var to = getString(R.string.user_str_count_down, time)
                    mBinding.tvVenueRtnSendCode.text = to
                    mBinding.tvVenueRtnSendCode.isClickable = false
                    mBinding.tvVenueRtnSendCode.isEnabled = false
                } else {
                    mBinding.tvVenueRtnSendCode.text = getString(R.string.user_label_send_code)
                    mBinding.tvVenueRtnSendCode.isClickable = true
                    mBinding.tvVenueRtnSendCode.isEnabled = true
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timerDisposable?.dispose()
    }

    /**
     * 显示未输入身份证号和手机信息
     */
    private fun showUnInputView() {
        mBinding.rvUnRegisterHealthCode.visibility = View.GONE
        mBinding.tvUnInputInfo.visibility = View.VISIBLE
        mBinding.llvHealthCodeInfo.visibility = View.GONE
        mBinding.llvZyCodeInfo.visibility = View.GONE
    }

    /**
     * 显示健康码信息
     */
    private fun showHealthCodeInfo(data: HelathInfoBean) {
        mBinding.rvUnRegisterHealthCode.visibility = View.GONE
        mBinding.tvUnInputInfo.visibility = View.GONE
        if (healthSetingInfo != null && (healthSetingInfo!!.enableHealthyCode || healthSetingInfo!!.enableTravelCode)) {
            mBinding.vPersonHealthInfo.visibility = View.VISIBLE
            // 旅游码
            dealTravelCodeInfo(data)
            // 健康码
            dealHealthCodeInfo(data)
        } else {
            mBinding.vPersonHealthInfo.visibility = View.GONE
        }
    }

    /**
     * 处理旅游码
     */
    private fun dealTravelCodeInfo(data: HelathInfoBean) {
        if (healthSetingInfo!!.enableTravelCode) {
            mBinding.llvZyCodeInfo.visibility = View.VISIBLE
            // 智游码
            mBinding.llvZyCodeInfo.visibility = View.VISIBLE
            if (!data.smartTravelCodeRegisterStatus) {
                // 未注册
                mBinding.imgHealthCodeStatus.setImageResource(R.mipmap.venue_book_condition_icon_unknown)
                mBinding.tvHealthCodeStatus.text = "未注册"
                mBinding.tvHealthCodeStatus.setTextColor(context!!.resources.getColor(R.color.color_999))
                if (!healthSetingInfo!!.enableHealthyCode) {
                    isCanReservation = 1
                }
                if (!isShowZyCode) {
                    showZyTfCodeTipDialog()
                }
            } else {
                // 注册
                mBinding.imgHealthCodeStatus.setImageResource(R.mipmap.venue_book_condition_icon_low)
                mBinding.tvHealthCodeStatus.text = "已注册"
                mBinding.tvHealthCodeStatus.setTextColor(context!!.resources.getColor(R.color.c_36cd64))
                if (!healthSetingInfo!!.enableHealthyCode) {
                    isCanReservation = 1
                }
            }
        } else {
            mBinding.llvZyCodeInfo.visibility = View.GONE
        }
    }

    /**
     * 处理健康码
     */
    private fun dealHealthCodeInfo(data: HelathInfoBean) {
        if (healthSetingInfo!!.enableHealthyCode) {
            mBinding.tvHealthAddress.visibility = View.VISIBLE
            mBinding.llvHealthTypies.visibility = View.VISIBLE
            mBinding.llvHealthCodeInfo.visibility = View.VISIBLE
            // 健康状态 01 正常 11 黄码 31红码
            if (!data.healthCode.isNullOrEmpty()) {
                when (data.healthCode) {
                    "01" -> {
                        // 绿码
                        mBinding.tvHealthStatus.text = getString(R.string.health_normal)
                        mBinding.tvHealthStatus.setTextColor(resources.getColor(R.color.c_36cd64))
                        mBinding.imgHealthStatus.setImageResource(R.mipmap.venue_book_condition_icon_low)
                        mBinding.tvHealthCanReservation.text =
                            getString(R.string.health_can_reseravtion)
                        mBinding.tvHealthCanReservation.setTextColor(resources.getColor(R.color.c_36cd64))
                        isCanReservation = 1
                    }
                    "11" -> {
                        // 黄码
                        mBinding.tvHealthStatus.text = getString(R.string.health_middle)
                        mBinding.tvHealthStatus.setTextColor(resources.getColor(R.color.color_ff9e05))
                        mBinding.tvHealthCanReservation.setTextColor(resources.getColor(R.color.color_ff9e05))
                        mBinding.imgHealthStatus.setImageResource(R.mipmap.venue_book_condition_icon_middle)
                        isCanReservation()
                    }
                    "31" -> {
                        // 红码
                        mBinding.tvHealthStatus.text = getString(R.string.health_bad)
                        mBinding.tvHealthStatus.setTextColor(resources.getColor(R.color.ff4e4e))
                        mBinding.tvHealthCanReservation.setTextColor(resources.getColor(R.color.ff4e4e))
                        mBinding.imgHealthStatus.setImageResource(R.mipmap.venue_book_condition_icon_high)
                        isCanReservation()
                    }
                }
            } else {
                // 未注册健康码
                showUnregisterHealthDialog()
                showUnRegiseterHealthCodeInfo()
            }
        } else {
            mBinding.tvHealthAddress.visibility = View.GONE
            mBinding.llvHealthTypies.visibility = View.GONE
            mBinding.llvHealthCodeInfo.visibility = View.GONE
        }
    }

    /**
     * 是否可以预订处理
     */
    private fun isCanReservation() {
        if (healthSetingInfo != null) {
            if (!healthSetingInfo!!.reserveOption.isNullOrEmpty()) {
                if (healthSetingInfo!!.reserveOption == "All") {
                    mBinding.tvHealthCanReservation.text = ""
                    isCanReservation = 1
                } else {
                    mBinding.tvHealthCanReservation.text =
                        getString(R.string.health_canot_reseravtion)
                    isCanReservation = 2
                }
            }
        }
    }

    /**
     * 显示未注册健康信息
     */
    private fun showUnRegiseterHealthCodeInfo() {
        mBinding.rvUnRegisterHealthCode.visibility = View.VISIBLE
        mBinding.tvUnInputInfo.visibility = View.GONE
        mBinding.llvHealthCodeInfo.visibility = View.GONE
    }

    /**
     * 设置用户信息
     */
    @Synchronized
    public fun setIdCardAndPhone(phone: String?, idCard: String?, name: String?) {
        if (!phone.isNullOrEmpty() && !idCard.isNullOrEmpty() && !name.isNullOrEmpty() && isIdCardType()) {
            userName = name
            this.phone = phone
            this.certNumber = idCard
            if (isIdCardType()) {
                mBinding.vPersonHealthInfo.visibility = View.VISIBLE
            }
        } else {
            return
        }
        if (isIdCardType() && healthSetingInfo != null && !(!healthSetingInfo!!.enableHealthyCode && !healthSetingInfo!!.enableTravelCode)) {
            if (healthSetingInfo!!.enableHealthyCode && healthSetingInfo!!.enableTravelCode) {
                if (!mDatasHealthRegions.isNullOrEmpty() && currentRegion != null) {
                    showLoadingDialog()
                    mModel.getHelathInfoAndReister(
                        name,
                        idCard,
                        phone,
                        currentRegion!!.getCurrentRegion()
                    )
                }
            } else {
                if (healthSetingInfo!!.enableHealthyCode) {
                    if (!mDatasHealthRegions.isNullOrEmpty() && currentRegion != null) {
                        showLoadingDialog()
                        mModel.getHealthInfo(phone, currentRegion!!.region, name, idCard)
                    }
                } else {
                    showLoadingDialog()
                    if (healthSetingInfo!!.enableTravelCode) {
                        mModel.getTravelInfo(phone, name, idCard)
                    }
                }

            }
        } else {
            hideHealthInfoView()
        }
    }

    /**
     * 显示智游天府码
     */
    private fun showZyTfCodeTipDialog() {
        if (zyTfCodeTipDialog == null) {
            var name = healthSetingInfo!!.smartTravelName
            zyTfCodeTipDialog = ZyTfCodeTipDialog.Builder().setName(name)
                .setOnTipConfirmListener(object : ZyTfCodeTipDialog.OnTipConfirmListener {
                    override fun onConfirm() {
                        ARouter.getInstance()
                            .build(ARouterPath.VenuesModule.VENUES_ZYTF_CODE_INFO_ACTIVITY)
                            .withString("introduce", healthSetingInfo!!.healthIntroduce)
                            .withString("name", name)
                            .navigation()
                    }

                }).create(context!!)
        }
        zyTfCodeTipDialog?.show()
        isShowZyCode = true
    }


    /**
     * 显示未注册健康码信息
     */
    private fun showUnregisterHealthDialog() {
        if (unregisterHealthCodeDialog == null) {
            unregisterHealthCodeDialog = UnRegsiterHealthCodeDialog.Builder()
                .onDownLoadListener(object : UnRegsiterHealthCodeDialog.OnDownLoadListener {
                    override fun onDownLoadImage(url: String) {
                        try {
                            showLoadingDialog()
                            DownLoadFileUtil.downNetworkImage(
                                url,
                                object : DownLoadFileUtil.DownImageListener {
                                    override fun onDownLoadImageSuccess() {
                                        dissMissLoadingDialog()
                                        ToastUtils.showMessage("保存二维码成功~")
                                    }
                                })
                        } catch (e: Exception) {
                            dissMissLoadingDialog()
                            ToastUtils.showMessage("保存二维码失败~")
                        }
                    }
                })
                .build(context!!)
        }
        if (currentRegion != null) {
            if (!currentRegion!!.healthRegisterUrl.isNullOrEmpty()) {
                unregisterHealthCodeDialog?.updateData(currentRegion!!.healthRegisterUrl, "")
                unregisterHealthCodeDialog?.show()
            } else {
                ToastUtils.showMessage("未获取的健康码注册地址，稍后再试")
            }
        } else {
            ToastUtils.showMessage("未获取的健康码注册地址，稍后再试")
        }
    }

    /**
     * 当前选择类型是否是身份证号码
     */
    fun isIdCardType(): Boolean {
        if (mModel.currentCrdentType == null) {
            return false
        }
        try {
            if (mModel.currentCrdentType!!.toLowerCase() == "id_card") {
                return true
            }
        } catch (e: Exception) {
        }

        return false
    }

    /**
     * 隐藏健康信息
     */
    fun hideHealthInfoView() {
        mBinding.vPersonHealthInfo.visibility = View.GONE
    }

    fun showHealthInfoView() {
        mBinding.vPersonHealthInfo.visibility = View.VISIBLE
    }

    fun setAuthenticationInfo(name: String?, idCard: String?) {
        var phone: String? = SPUtils.getInstance().getString(SPKey.PHONE)
        if (!phone.isNullOrEmpty()) {
            mBinding?.edtPhoneNumber?.setText(
                "" + phone
            )
            mModel.checkExistNumber(phone, orderEditType)
        } else {
            var phoneOld: String? =
                SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_RESERATION_INFO)
                    .getString(SPUtils.Config.APP_RESERATION_PHONE)
            if (!phoneOld.isNullOrEmpty()) {
                mBinding?.edtPhoneNumber?.setText(
                    "" + phoneOld
                )
                phone = phoneOld
                mModel.checkExistNumber(phoneOld, orderEditType)
            }
        }
        if (!name.isNullOrEmpty()) {
            mBinding?.edtUserName?.setText(
                "" + name
            )
//            mBinding?.edtUserName?.isEnabled =
//                false
//            binding?.vPersonReservationInfo?.imgSelectVenueRtnName?.visibility =
//                View.GONE
        } else {
            var name: String? =
                SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_RESERATION_INFO)
                    .getString(SPUtils.Config.APP_RESERATION_NAME)
            if (!name.isNullOrEmpty()) {
                mBinding?.edtUserName?.setText(
                    "" + name
                )
//                binding?.vPersonReservationInfo?.edtVenueRtnPnameValue?.isEnabled =
//                    true
            }
        }
        if (!idCard.isNullOrEmpty() && isIdCardType()) {
            mBinding?.edtVenueRtnIdcardValue?.setText(
                "" + idCard
            )
//            mBinding?.edtVenueRtnIdcardValue?.isEnabled =
//                false
        } else {
            var idCard: String? =
                SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_RESERATION_INFO)
                    .getString(SPUtils.Config.APP_RESERATION_IDCARD)
            if (!idCard.isNullOrEmpty()) {
                mBinding?.edtVenueRtnIdcardValue?.setText(
                    "" + idCard
                )
//                binding?.vPersonReservationInfo?.edtVenueRtnIdcardValue?.isEnabled =
//                    true
            }
        }
        if (!phone.isNullOrEmpty() && !name.isNullOrEmpty() && !idCard.isNullOrEmpty()) {
            setIdCardAndPhone(SM4Util.encryptByEcb(phone), SM4Util.encryptByEcb(idCard), name)
        }
    }

    /**
     * 回显证件信息
     */
    fun backfillCredType() {
        var isInit = false
        if (userContact != null) {
            // 初始化用户已有的证件
            for (i in crdentTypes.indices) {
                var item = crdentTypes[i]
                if ((item.value == userContact!!.certType || item.name == userContact!!.certType) &&
                    !userContact!!.certNumber.isNullOrEmpty()
                ) {
                    mModel.currentCrdentType = item.value
                    mModel.currentCrdentName = item.name
                    mBinding.tvVenueRtnTypeValue.text = item.name
                    updateCertNum(item)
                    typeSelector?.setSelectOptions(i)
                    isInit = true
                    break
                }
            }
        }
        if (isInit) {
            return
        } else {
            if (userContact != null && !userContact!!.certType.isNullOrEmpty() && !userContact!!.certNumber.isNullOrEmpty()) {
                ToastUtils.showMessage("请输入正确的证件号码")
            }
        }

        var item = crdentTypes[0]
        if (item != null) {
            mModel.currentCrdentType = item.value
            mModel.currentCrdentName = item.name
            mBinding.tvVenueRtnTypeValue.text = item.name
            updateCertNum(item)
        }
    }

}
