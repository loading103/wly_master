package com.daqsoft.venuesmodule.activity.widgets

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.launcher.ARouter
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.daqsoft.baselib.utils.RegexUtil
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.businessview.view.UnRegsiterHealthCodeDialog
import com.daqsoft.provider.businessview.view.ZyTfCodeTipDialog
import com.daqsoft.provider.view.LabelsView
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.LayoutPersonAppointMentInfoBinding
import kotlinx.android.synthetic.main.layout_person_appoint_ment_info.view.*

/**
 * @Description 单人预约
 * @ClassName   SingleReserationInfoView
 * @Author      luoyi
 * @Time        2020/8/3 16:34
 */
class SingleReserationInfoView : FrameLayout {
    var mContext: Context? = null

    var binding: LayoutPersonAppointMentInfoBinding? = null
    /**
     * 当前选择数据集
     */
    var currentNumbers: MutableList<String> = mutableListOf()
    /**
     * 预约类型
     */
    var reservationType: Int = 0
    /**
     * 使用人数
     */
    var userNum: String = ""
    /**
     * 预约信息
     */
    var data: VenueOrderViewInfo? = null
    /**
     * 是否需要短信验证码
     */
    var isNeedSmsCode: Boolean = true
    /**
     * 健康码设置信息
     */
    var healthSetingInfo: HelathSetingBean? = null
    /**
     * 智游天府弹窗
     */
    var zyTfCodeTipDialog: ZyTfCodeTipDialog? = null
    /**
     * 未注册健康码弹窗
     */
    var unregisterHealthCodeDialog: UnRegsiterHealthCodeDialog? = null

    var isInitHealth: Boolean = true
    /**
     * 健康码地区数据集
     */
    var mDatasHealthRegions: MutableList<HelathRegionBean> = mutableListOf()
    /**
     * 健康码信息
     */
    var healthInfo: HelathInfoBean? = null

    var currentRegion: HelathRegionBean? = null
    /**
     * 是否可以预约
     */
    var isCanReservation: Int = 0
    /**
     * 单人预约信息
     */
    private lateinit var singleResPerson: SingleResPerson

    var onSingleReserationListener: OnSingleReserationListener? = null

    var crdentTypes: MutableList<ConstellationBean> = mutableListOf()

    var typeSelector: OptionsPickerView<ConstellationBean>? = null

    /**
     * 支持预约得证件类型
     */
    var supportCredentTypes: MutableList<String> = mutableListOf()

    var currentCrdentType: String? = ""

    var currentCrdentName: String? = ""

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.layout_person_appoint_ment_info,
            this,
            false
        )
        addView(binding!!.root)
        initViewEvent()
    }

    private fun initViewEvent() {
        binding?.vPersonReservationInfo?.tvVenuePersonNumValue?.onNoDoubleClick {
            showSelectNumber()
        }
        binding?.vPersonReservationInfo?.tvVenueRtnSendCode?.onNoDoubleClick {
            var phoneNumer: String? =
                binding?.vPersonReservationInfo?.edtVenueRtnPphoneValue?.text.toString()
            if (phoneNumer.isNullOrEmpty()) {
                ToastUtils.showMessage("请先输入手机号码")
            } else if (!RegexUtil.isMobilePhone(phoneNumer)) {
                ToastUtils.showMessage("请输入正确的手机号码")
            } else {
                onSingleReserationListener?.onSendSmsCode(phoneNumer)
            }
        }
        binding?.vPersonReservationInfo?.imgSelectVenueRtnName?.onNoDoubleClick {
            onSingleReserationListener?.toSelectUserContact()
        }

        binding?.vPersonReservationInfo?.tvVenueRtnTypeValue?.onNoDoubleClick {
            typeSelector?.show()
        }
        binding?.tvTipToRegisterHealthCode?.onNoDoubleClick {
            showUnregisterHealthDialog()
        }

        binding?.vPersonReservationInfo?.edtVenueRtnPphoneValue?.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.length == 11 && currentCrdentType == "ID_CARD") {
                    onInputValueChanged()
                    onSingleReserationListener?.onCheckPhoneCode(s.toString())
                } else {
                    hideHealthInfoView()
                }
            }

        })
        binding?.vPersonReservationInfo?.edtVenueRtnIdcardValue?.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.length == 18 && currentCrdentType == "ID_CARD") {
                    onInputValueChanged()
                } else {
                    hideHealthInfo()
                }
            }
        })
        binding?.vPersonReservationInfo?.edtVenueRtnPnameValue?.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())
                ) {
                    var s: String? =
                        binding?.vPersonReservationInfo?.edtVenueRtnPnameValue?.text.toString()
                    if (!s.isNullOrEmpty()) {
                        onInputValueChanged()
                    } else {
                        hideHealthInfoView()
                    }
                }
                return false
            }

        })

        llv_health_typies.setOnLabelSelectChangeListener(object :
            LabelsView.OnLabelSelectChangeListener {
            override fun onLabelSelectChange(
                label: TextView?,
                data: Any?,
                isSelect: Boolean,
                position: Int
            ) {
                if (isSelect) {
                    label?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.c_36cd64))
                    label?.setBackgroundResource(com.daqsoft.provider.R.drawable.shape_venue_selected_r3)
                    if (position < mDatasHealthRegions.size) {
                        var region = mDatasHealthRegions[position]
                        if (region != null) {
                            currentRegion = region
                            // 取消第一次初始化判断
                            if (!isInitHealth && singleResPerson != null) {
                                if (singleResPerson.name.isNullOrEmpty()) {
                                    ToastUtils.showMessage("请输入您的真实姓名")
                                } else if (singleResPerson.phone.isNullOrEmpty()) {
                                    ToastUtils.showMessage("请输入您的手机号")
                                } else if (singleResPerson.idCard.isNullOrEmpty()) {
                                    ToastUtils.showMessage("请输入您的身份证信息")
                                } else {
                                    isCanReservation = 0
                                    if (healthSetingInfo!!.enableHealthyCode && healthSetingInfo!!.enableTravelCode) {
                                        onSingleReserationListener?.getHelathInfoAndReister(
                                            singleResPerson.name!!,
                                            singleResPerson.idCard!!,
                                            singleResPerson.phone!!,
                                            currentRegion!!.getCurrentRegion()
                                        )
                                    } else {
                                        onSingleReserationListener?.getHealthInfo(
                                            singleResPerson.phone!!,
                                            currentRegion!!.getCurrentRegion(),
                                            singleResPerson.name!!,
                                            singleResPerson.idCard!!
                                        )
                                    }
                                }
                            } else {
                                setIdCardAndPhone(
                                    singleResPerson!!.phone,
                                    singleResPerson!!.idCard,
                                    singleResPerson!!.name
                                )
                            }
                        }
                    }
                } else {
                    label?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.color_333))
                    label?.setBackgroundResource(com.daqsoft.provider.R.drawable.shape_venue_default_r3)
                }
                isInitHealth = false
            }
        })
        v_zytf_code_info?.onNoDoubleClick {
            if (healthSetingInfo != null) {
                var name = healthSetingInfo?.smartTravelName
                ARouter.getInstance().build(ARouterPath.VenuesModule.VENUES_ZYTF_CODE_INFO_ACTIVITY)
                    .withString("introduce", healthSetingInfo!!.healthIntroduce)
                    .withString("name", name)
                    .navigation()
            }
        }
        llv_health_typies?.setmOnLabelShowMoreListener(object :
            LabelsView.OnLabelShowMoreListener {
            override fun onLabeShowLoadMore() {
                tv_more_health_tyoe?.visibility = View.VISIBLE
            }
        })
        tv_more_health_tyoe?.onNoDoubleClick {
            if (llv_health_typies?.maxLines == -1) {
                llv_health_typies?.maxLines = 2
                val topDrawable =
                    resources.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_down)
                topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
                tv_more_health_tyoe?.setCompoundDrawables(null, null, topDrawable, null)
                tv_more_health_tyoe?.text = "查看更多"
            } else {
                tv_more_health_tyoe?.text = "收起更多"
                llv_health_typies?.maxLines = -1
                val topDrawable =
                    resources.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_up)
                topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
                tv_more_health_tyoe?.setCompoundDrawables(null, null, topDrawable, null)
            }
        }
    }


    /**
     * 隐藏健康信息
     */
    fun hideHealthInfo() {
        v_person_health_info?.visibility = View.GONE
    }

    /**
     * 健康码设置信息
     */
    fun setHealthSetInfo(helathSetingBean: HelathSetingBean?) {
        helathSetingBean?.let {
            if (helathSetingBean != null) {
                healthSetingInfo = helathSetingBean
                if (!it.enableHealthyCode && !it.enableTravelCode) {
                    v_person_health_info?.visibility = View.GONE
                    isCanReservation = 1
                } else {
                    v_person_health_info?.visibility = View.GONE
                    if (it.enableHealthyCode) {
                        onSingleReserationListener?.getHealthRegion()
                        if (!it.reserveOption.isNullOrEmpty() && it.reserveOption == "LowRisk") {
                            onShowHealthTip()
                        }
                    } else {
                        if (it.enableTravelCode) {
                            tv_un_input_info.visibility = View.VISIBLE
                            setIdCardAndPhone(
                                singleResPerson?.phone,
                                singleResPerson?.idCard,
                                singleResPerson?.name
                            )
                        }
                    }
                }
                if (!healthSetingInfo!!.smartTravelName.isNullOrEmpty()) {
                    tv_travel_code_name.text = context.getString(
                        com.daqsoft.provider.R.string.travel_code_name,
                        healthSetingInfo!!.smartTravelName
                    )
                }
            } else {
                v_person_health_info?.visibility = View.GONE
            }
        }
    }

    private fun onShowHealthTip() {

    }


    /**
     * 改变预约方式
     * @param reservationType 预约类型
     */
    fun changeVenueTipPersonNum(reservationType: Int) {
        this.reservationType = reservationType
        data?.let {
            if (reservationType == 1) {
                binding?.vPersonReservationInfo?.tvVenueRtnCompanyName?.visibility = View.GONE
                binding?.vPersonReservationInfo?.edtVenueRtnInCompanyNameVlaue?.visibility =
                    View.GONE
                binding?.vPersonReservationInfo?.tvVenuePersonNumValue?.text =
                    "" + it.personNumMix
                if (it.personNumMax == 1) {
                    binding?.vPersonReservationInfo?.imgVenuePesonNum?.visibility = View.GONE
                    binding?.vPersonReservationInfo?.tvVenuePersonTip?.text = context.getString(
                        R.string.venue_reservation_only_tip_num
                    )
                } else {
                    binding?.vPersonReservationInfo?.imgVenuePesonNum?.visibility = View.VISIBLE
                    binding?.vPersonReservationInfo?.tvVenuePersonTip?.text = context.getString(
                        R.string.venue_reservation_max_tip_num,
                        "" + it.personNumMax
                    )
                }
            } else {
                binding?.vPersonReservationInfo?.tvVenuePersonNumValue?.text =
                    "" + it.teamNumMin
                binding?.vPersonReservationInfo?.tvVenueRtnCompanyName?.visibility =
                    View.VISIBLE
                binding?.vPersonReservationInfo?.edtVenueRtnInCompanyNameVlaue?.visibility =
                    View.VISIBLE
                if (it.teamNumMax == 1) {
                    binding?.vPersonReservationInfo?.imgVenuePesonNum?.visibility = View.GONE
                    binding?.vPersonReservationInfo?.tvVenuePersonTip?.text = context.getString(
                        R.string.venue_reservation_only_tip_num
                    )
                } else {
                    binding?.vPersonReservationInfo?.imgVenuePesonNum?.visibility = View.VISIBLE
                    binding?.vPersonReservationInfo?.tvVenuePersonTip?.text = context.getString(
                        R.string.venue_reservation_tip_num,
                        "" + it.teamNumMin, "" + it.teamNumMax
                    )
                }

            }
        }

    }


    /**
     * 设置是否需要验证码
     * @param isNeedSmsCode 是否需要验证码
     */
    fun setIsNeedSmsCode(isNeedSmsCode: Boolean) {
        this.isNeedSmsCode = isNeedSmsCode
        if (isNeedSmsCode) {
            binding?.vPersonReservationInfo?.edtVenueReservationPpcodeValue?.visibility =
                View.VISIBLE
            binding?.vPersonReservationInfo?.tvVenueRtnPhoneCode?.visibility = View.VISIBLE
            binding?.vPersonReservationInfo?.tvVenueRtnSendCode?.visibility = View.VISIBLE
            binding?.vPersonReservationInfo?.vLineThree?.visibility = View.VISIBLE
        } else {
            binding?.vPersonReservationInfo?.edtVenueReservationPpcodeValue?.visibility =
                View.GONE
            binding?.vPersonReservationInfo?.tvVenueRtnPhoneCode?.visibility = View.GONE
            binding?.vPersonReservationInfo?.tvVenueRtnSendCode?.visibility = View.GONE
            binding?.vPersonReservationInfo?.vLineThree?.visibility = View.GONE
        }
    }


    /**
     * 数量选择器
     */
    private val numberPv by lazy {

        val pV = OptionsPickerBuilder(context, OnOptionsSelectListener { s1, s2, s3, v ->
            // 1 个人 2 团队
            var num = currentNumbers.get(s1)
            when (reservationType) {
                1 -> {
                    binding?.vPersonReservationInfo?.tvVenuePersonNumValue?.setText("${num}")
                    userNum = num.toString()
                }
                2 -> {
                    binding?.vPersonReservationInfo?.tvVenuePersonNumValue?.setText("${num}")
                    userNum = num.toString()
                }
            }
        }).build<String>()

        pV
    }

    /**
     * 获取团队预约到场人数
     */
    private fun getTempResevationNum(): MutableList<String> {
        var numbers: MutableList<String> = mutableListOf()
        if (data != null) {
            var max = data!!.teamNumMax
            var min = data!!.teamNumMin
            for (i in min..max) {
                numbers.add(i.toString())
            }
        }
        return numbers
    }

    /**
     * 获取个人预约到场人数
     */
    private fun getPersonRervationNum(): MutableList<String> {
        var numbers: MutableList<String> = mutableListOf()
        if (data != null) {
            var max = data!!.personNumMax
            var min = data!!.personNumMix
            for (i in min..max) {
                numbers.add(i.toString())
            }
        }
        return numbers
    }

    /**
     * 选择数量适配器
     */
    private fun showSelectNumber() {

        if (binding?.vPersonReservationInfo?.tvVenuePersonNumValue != null) {
            UIHelperUtils.hideKeyboard(binding?.vPersonReservationInfo?.tvVenuePersonNumValue!!)
        }
        if (data != null) {
            when (reservationType) {
                1 -> {
                    // 个人预约
                    numberPv?.setPicker(getPersonRervationNum())
                    currentNumbers.clear()
                    currentNumbers.addAll(getPersonRervationNum())
                }
                2 -> {
                    // 团队预约
                    currentNumbers.clear()
                    currentNumbers.addAll(getTempResevationNum())
                }
            }
            numberPv?.setPicker(currentNumbers)
            var numStr: String? =
                binding?.vPersonReservationInfo?.tvVenuePersonNumValue?.text.toString()
            if (!numStr.isNullOrEmpty() && !currentNumbers.isNullOrEmpty()) {
                var index = currentNumbers.indexOf(numStr)
                if (index >= 0) {
                    numberPv.setSelectOptions(index)
                }
            }
            numberPv?.show()
        }
    }

    /**
     * 设置短信验证码状态
     * @param status  状态
     * @param time 时间
     */
    fun setSmsCodeStatus(status: Boolean, time: Long) {
        if (status) {
            var to = context.getString(R.string.user_str_count_down, time)
            binding?.vPersonReservationInfo?.tvVenueRtnSendCode?.text = to
            binding?.vPersonReservationInfo?.tvVenueRtnSendCode?.isClickable = false
            binding?.vPersonReservationInfo?.tvVenueRtnSendCode?.isEnabled = false
        } else {
            binding?.vPersonReservationInfo?.tvVenueRtnSendCode?.text =
                context.getString(R.string.user_label_send_code)
            binding?.vPersonReservationInfo?.tvVenueRtnSendCode?.isClickable = true
            binding?.vPersonReservationInfo?.tvVenueRtnSendCode?.isEnabled = true
        }
    }

    fun setAuthenticationInfo(name: String?, idCard: String?) {
        var phone: String? = SPUtils.getInstance().getString(SPKey.PHONE)
        if (!phone.isNullOrEmpty()) {
            binding?.vPersonReservationInfo?.edtVenueRtnPphoneValue?.setText(
                "" + phone
            )
            onSingleReserationListener?.onCheckPhoneCode(phone)
        } else {
            var phoneOld: String? =
                SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_RESERATION_INFO)
                    .getString(SPUtils.Config.APP_RESERATION_PHONE)
            if (!phoneOld.isNullOrEmpty()) {
                binding?.vPersonReservationInfo?.edtVenueRtnPphoneValue?.setText(
                    "" + phoneOld
                )
                onSingleReserationListener?.onCheckPhoneCode(phoneOld)
                phone = phoneOld
            }
        }
        if (!name.isNullOrEmpty()) {
            binding?.vPersonReservationInfo?.edtVenueRtnPnameValue?.setText(
                "" + name
            )
            binding?.vPersonReservationInfo?.edtVenueRtnPnameValue?.isEnabled =
                true
            binding?.vPersonReservationInfo?.imgSelectVenueRtnName?.visibility =
                View.GONE
        } else {
            var name: String? =
                SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_RESERATION_INFO)
                    .getString(SPUtils.Config.APP_RESERATION_NAME)
            if (!name.isNullOrEmpty()) {
                binding?.vPersonReservationInfo?.edtVenueRtnPnameValue?.setText(
                    "" + name
                )
                binding?.vPersonReservationInfo?.edtVenueRtnPnameValue?.isEnabled =
                    true
            }
        }
        if (!idCard.isNullOrEmpty()) {
            binding?.vPersonReservationInfo?.edtVenueRtnIdcardValue?.setText(
                "" + idCard
            )
            binding?.vPersonReservationInfo?.edtVenueRtnIdcardValue?.isEnabled =
                false
        } else {
            var idCard: String? =
                SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_RESERATION_INFO)
                    .getString(SPUtils.Config.APP_RESERATION_IDCARD)
            if (!idCard.isNullOrEmpty()) {
                binding?.vPersonReservationInfo?.edtVenueRtnIdcardValue?.setText(
                    "" + idCard
                )
                binding?.vPersonReservationInfo?.edtVenueRtnIdcardValue?.isEnabled =
                    true
            }
        }
        if (!phone.isNullOrEmpty() && !name.isNullOrEmpty() && !idCard.isNullOrEmpty()) {
            setIdCardAndPhone(phone, idCard, name)
        }
    }

    fun setData() {
        // 单人预约信息
        singleResPerson = SingleResPerson()
        data?.let {
            if (!it.orderUserCredentialsType.isNullOrEmpty()) {
                var temps = it.orderUserCredentialsType!!.split(",")
                if (!temps.isNullOrEmpty()) {
                    supportCredentTypes.clear()
                    supportCredentTypes.addAll(temps)
                }
                if (!supportCredentTypes.isNullOrEmpty()) {
                    currentCrdentType = supportCredentTypes[0]
                    if (supportCredentTypes.size == 1) {
                        binding?.vPersonReservationInfo?.imgVenueSelectType?.visibility = View.GONE
                    } else {
                        binding?.vPersonReservationInfo?.imgVenueSelectType?.visibility =
                            View.VISIBLE
                    }
                }
            }
        }
    }


    /**
     * 设置用户信息
     */
    @Synchronized
    public fun setIdCardAndPhone(phone: String?, idCard: String?, name: String?) {
        if (singleResPerson == null) {
            singleResPerson = SingleResPerson()
        }
        if (isIdCardType()&&!phone.isNullOrEmpty() && !idCard.isNullOrEmpty() && !name.isNullOrEmpty()) {
            singleResPerson.phone = phone
            singleResPerson.idCard = idCard
            singleResPerson.name = name
            if (healthSetingInfo != null && (healthSetingInfo!!.enableTravelCode || healthSetingInfo!!.enableHealthyCode)) {
                v_person_health_info?.visibility = View.VISIBLE
            }

        } else {
            return
        }
        if (isIdCardType()&&healthSetingInfo != null && !(!healthSetingInfo!!.enableHealthyCode && !healthSetingInfo!!.enableTravelCode)) {
            if (healthSetingInfo!!.enableHealthyCode && healthSetingInfo!!.enableTravelCode) {
                if (!mDatasHealthRegions.isNullOrEmpty() && currentRegion != null) {
                    onSingleReserationListener?.getHelathInfoAndReister(
                        name,
                        idCard,
                        phone,
                        currentRegion!!.getCurrentRegion()
                    )
                }
            } else {
                if (healthSetingInfo!!.enableHealthyCode) {
                    if (!mDatasHealthRegions.isNullOrEmpty() && currentRegion != null) {
                        onSingleReserationListener?.getHealthInfo(
                            phone,
                            currentRegion!!.region,
                            name,
                            idCard
                        )
                    }
                } else {
                    if (healthSetingInfo!!.enableTravelCode) {
                        onSingleReserationListener?.getTravelInfo(phone, name, idCard)
                    } else {

                    }
                }

            }
        }
    }

    interface OnSingleReserationListener {
        /**
         * 发送手机验证码
         */
        fun onSendSmsCode(phone: String)

        /**
         * 检查手机号码
         */
        fun onCheckPhoneCode(phone: String)

        /**
         * 输入信息变更
         */
        fun onInputValueChanged()

        /**
         * 获取健康码注册信息
         */
        fun getHelathInfoAndReister(name: String, idCard: String, phone: String, region: String)

        /**
         * 获取健康码信息
         */
        fun getHealthInfo(phone: String, region: String, name: String, idCard: String)

        /**
         * 获取旅游信息
         */
        fun getTravelInfo(phone: String, name: String, idCard: String)

        fun getHealthRegion()

        fun toSelectUserContact()
    }

    /**
     * 显示健康码信息
     */
    private fun showHealthCodeInfo(data: HelathInfoBean) {
        rv_un_register_health_code?.visibility = View.GONE
        tv_un_input_info?.visibility = View.GONE
        if (healthSetingInfo != null && (healthSetingInfo!!.enableHealthyCode || healthSetingInfo!!.enableTravelCode)) {
            v_person_health_info?.visibility = View.VISIBLE
            // 旅游码
            dealTravelCodeInfo(data)
            // 健康码
            dealHealthCodeInfo(data)
        } else {
            v_person_health_info?.visibility = View.GONE
        }
    }

    /**
     * 处理旅游码
     */
    private fun dealTravelCodeInfo(data: HelathInfoBean) {
        if (healthSetingInfo!!.enableTravelCode) {
            llv_zy_code_info?.visibility = View.VISIBLE
            if (!data.smartTravelCodeRegisterStatus) {
                // 未注册
                img_health_code_status?.setImageResource(com.daqsoft.provider.R.mipmap.venue_book_condition_icon_unknown)
                tv_health_code_status?.text = "未注册"
                tv_health_code_status?.setTextColor(context!!.resources.getColor(com.daqsoft.provider.R.color.color_999))
                if (!healthSetingInfo!!.enableHealthyCode) {
                    isCanReservation = 1
                }
                showZyTfCodeTipDialog()
            } else {
                // 注册
                img_health_code_status?.setImageResource(com.daqsoft.provider.R.mipmap.venue_book_condition_icon_low)
                tv_health_code_status?.text = "已注册"
                tv_health_code_status?.setTextColor(context!!.resources.getColor(com.daqsoft.provider.R.color.c_36cd64))
                if (!healthSetingInfo!!.enableHealthyCode) {
                    isCanReservation = 1
                }
            }
        } else {
            llv_zy_code_info?.visibility = View.GONE
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
    }

    /**
     * 处理健康码
     */
    private fun dealHealthCodeInfo(data: HelathInfoBean) {
        if (healthSetingInfo!!.enableHealthyCode) {
            tv_health_address?.visibility = View.VISIBLE
            llv_health_typies?.visibility = View.VISIBLE
            llv_health_code_info?.visibility = View.VISIBLE
            // 健康状态 01 正常 11 黄码 31红码
            if (!data.healthCode.isNullOrEmpty()) {
                when (data.healthCode) {
                    "01" -> {
                        // 绿码
                        tv_health_status?.text =
                            context.getString(com.daqsoft.provider.R.string.health_normal)
                        tv_health_status?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.c_36cd64))
                        img_health_status?.setImageResource(com.daqsoft.provider.R.mipmap.venue_book_condition_icon_low)
                        tv_health_can_reservation?.text =
                            context.getString(com.daqsoft.provider.R.string.health_can_reseravtion)
                        tv_health_can_reservation?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.c_36cd64))
                        isCanReservation = 1
                    }
                    "11" -> {
                        // 黄码
                        tv_health_status?.text =
                            context.getString(com.daqsoft.provider.R.string.health_middle)
                        tv_health_status?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.color_ff9e05))
                        tv_health_can_reservation?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.color_ff9e05))
                        img_health_status?.setImageResource(com.daqsoft.provider.R.mipmap.venue_book_condition_icon_middle)
                        isCanReservation()
                    }
                    "31" -> {
                        // 红码
                        tv_health_status?.text =
                            context.getString(com.daqsoft.provider.R.string.health_bad)
                        tv_health_status?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.ff4e4e))
                        tv_health_can_reservation?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.ff4e4e))
                        img_health_status?.setImageResource(com.daqsoft.provider.R.mipmap.venue_book_condition_icon_high)
                        isCanReservation()
                    }
                }
            } else {
                // 未注册健康码
                showUnregisterHealthDialog()
                showUnRegiseterHealthCodeInfo()
            }
        } else {
            tv_health_address?.visibility = View.GONE
            llv_health_typies?.visibility = View.GONE
            llv_health_code_info?.visibility = View.GONE
        }
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
                            DownLoadFileUtil.downNetworkImage(
                                url,
                                object : DownLoadFileUtil.DownImageListener {
                                    override fun onDownLoadImageSuccess() {
                                        ToastUtils.showMessage("保存二维码成功~")
                                    }
                                })
                        } catch (e: Exception) {
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
     * 设置证件类型
     * @param beans 所有证件类型
     */
    fun setCrdenTypes(beans: MutableList<ConstellationBean>?) {
        if (!beans.isNullOrEmpty()) {
            crdentTypes.clear()
            for (item in beans) {
                if (!supportCredentTypes.isNullOrEmpty()) {
                    for (data in supportCredentTypes) {
                        if (!data.isNullOrEmpty() && data.toLowerCase() == item.value.toLowerCase()) {
                            crdentTypes.add(item)
                            break
                        }
                    }
                }
            }
            initCrdentTypes()
        }

    }

    /**
     * 判断是否可以预约
     */
    fun getHealthStatus(): Int {
        return isCanReservation
    }

    /**
     * 隐藏健康信息
     */
    fun hideHealthInfoView() {
        v_person_health_info?.visibility = View.GONE
    }

    /**
     * 是否可以预订处理
     */
    private fun isCanReservation() {
        if (healthSetingInfo != null) {
            if (!healthSetingInfo!!.reserveOption.isNullOrEmpty()) {
                if (healthSetingInfo!!.reserveOption == "All") {
                    tv_health_can_reservation.text = ""
                    isCanReservation = 1
                } else {
                    tv_health_can_reservation.text =
                        context.getString(com.daqsoft.provider.R.string.health_canot_reseravtion)
                    isCanReservation = 2
                }
            }
        }
    }

    /**
     * 显示未注册健康信息
     */
    private fun showUnRegiseterHealthCodeInfo() {
        rv_un_register_health_code?.visibility = View.VISIBLE
        tv_un_input_info?.visibility = View.GONE
        llv_health_code_info?.visibility = View.GONE
    }

    fun setHeathInfo(healthInfo: HelathInfoBean?) {
        healthInfo?.let {
            if (it != null) {
                showHealthCodeInfo(it)
            } else {
                showUnregisterHealthDialog()
                showUnRegiseterHealthCodeInfo()
            }
        }
    }

    fun setHealthRegions(datas: MutableList<HelathRegionBean>?) {
        if (!datas.isNullOrEmpty()) {
            mDatasHealthRegions.clear()
            mDatasHealthRegions.addAll(datas)
            var temps: MutableList<String> = mutableListOf()
            for (item in mDatasHealthRegions) {
                if (item != null && !item.name.isNullOrEmpty())
                    temps.add(item.name)
            }
            llv_health_typies?.setLabels(temps)
            llv_health_typies?.maxLines = 2
            tv_health_address?.visibility = View.VISIBLE

        }
    }

    fun setTravelCode(isTravelCode: Boolean?) {
        llv_zy_code_info?.visibility = View.VISIBLE
        tv_un_input_info?.visibility = View.GONE
        // 旅游码
        if (isTravelCode != null && isTravelCode) {
            // 注册
            img_health_code_status?.setImageResource(com.daqsoft.provider.R.mipmap.venue_book_condition_icon_low)
            tv_health_code_status?.text = "已注册"
            tv_health_code_status?.setTextColor(context!!.resources.getColor(com.daqsoft.provider.R.color.c_36cd64))
        } else {
            // 未注册
            img_health_code_status?.setImageResource(com.daqsoft.provider.R.mipmap.venue_book_condition_icon_unknown)
            tv_health_code_status?.text = "未注册"
            tv_health_code_status?.setTextColor(context!!.resources.getColor(com.daqsoft.provider.R.color.color_999))
        }
        if (!healthSetingInfo!!.enableHealthyCode) {
            isCanReservation = 1
        }
    }

    private fun onInputValueChanged() {
        var phone: String? =
            binding?.vPersonReservationInfo?.edtVenueRtnPphoneValue?.text?.toString()
        var name: String? = binding?.vPersonReservationInfo?.edtVenueRtnPnameValue?.text?.toString()
        var idCard: String? =
            binding?.vPersonReservationInfo?.edtVenueRtnIdcardValue?.text?.toString()
        if (!phone.isNullOrEmpty() && !name.isNullOrEmpty() && !idCard.isNullOrEmpty()) {
            setIdCardAndPhone(phone, idCard, name)
        }
    }

    fun getSinglePeoleReravtionInfo(): ReseartionContactExo? {
        var phone: String? =
            binding?.vPersonReservationInfo?.edtVenueRtnPphoneValue?.text.toString()
        var name: String? = binding?.vPersonReservationInfo?.edtVenueRtnPnameValue?.text.toString()
        var idCard: String? =
            binding?.vPersonReservationInfo?.edtVenueRtnIdcardValue?.text.toString()
        var smsCode: String? =
            binding?.vPersonReservationInfo?.edtVenueReservationPpcodeValue?.text.toString()
        var companyName: String? =
            binding?.vPersonReservationInfo?.edtVenueRtnInCompanyNameVlaue?.text.toString()
        var userNum: String? =
            binding?.vPersonReservationInfo?.tvVenuePersonNumValue?.text.toString()
        if (name.isNullOrEmpty()) {
            ToastUtils.showMessage("请输入预订人姓名")
        } else if (phone.isNullOrEmpty()) {
            ToastUtils.showMessage("请输入预订人手机号码")
        } else if (!RegexUtil.isMobilePhone(phone)) {
            ToastUtils.showMessage("请输入正确的手机号")
        } else if (isNeedSmsCode && smsCode.isNullOrEmpty()) {
            ToastUtils.showMessage("请输入手机验证码")
        } else if (idCard.isNullOrEmpty()) {
            ToastUtils.showMessage("请输入预订人身份证号")
        } else if (reservationType == 2 && companyName.isNullOrEmpty()) {
            ToastUtils.showMessage("团队预约,需要输入公司名称")
        } else if (isCanReservation != 1) {
            // 健康码信息 不存在
            if (isCanReservation == 2) {
                ToastUtils.showMessage("非常抱歉，你当前的健康状况，不能预约~")
            } else {
                ToastUtils.showMessage("非常抱歉，未获取到您的健康码信息，不能预约~")
            }
        } else {
            return ReseartionContactExo(
                currentCrdentType,
                phone,
                name,
                idCard,
                1,
                currentRegion?.region,
                smsCode, userNum, companyName
            )
        }
        return null
    }

    /**
     * 回填联系人信息
     */
    fun setContactUs(item: Contact?) {
        if (item != null) {
            var isInit = false
            for (i in crdentTypes.indices) {
                var bean = crdentTypes[i]
                if ((bean.name == item.certType || bean.value == item.certType) && !item.certNumber.isNullOrEmpty()) {
                    typeSelector?.setSelectOptions(i)
                    currentCrdentType = bean.value
                    currentCrdentName = bean.name
                    binding?.vPersonReservationInfo?.tvVenueRtnTypeValue?.text = bean.name
                    binding?.vPersonReservationInfo?.edtVenueRtnIdcardValue?.setText("" + item!!.certNumber)
                    isInit = true
                }
            }
            if (!isInit) {
                binding?.vPersonReservationInfo?.edtVenueRtnIdcardValue?.setText("")
            }
            if (item.certType == "身份证" || item.certType == "ID_CARD") {
                if (!item!!.name.isNullOrEmpty() && !item.phone.isNullOrEmpty() && !item.certNumber.isNullOrEmpty()) {
                    isCanReservation = 0
                    healthInfo = null
                    setIdCardAndPhone(item.phone, item.certNumber, item.name)
                }
            } else {
                healthInfo = null
                isCanReservation = 1
                hideHealthInfo()
            }
            binding?.vPersonReservationInfo?.edtVenueRtnPnameValue?.setText("" + item!!.name)
            binding?.vPersonReservationInfo?.edtVenueRtnPphoneValue?.setText("" + item!!.phone)
        }

    }

    /**
     * 证件类型
     */
    private fun initCrdentTypes() {
        typeSelector = OptionsPickerBuilder(context) { o1, o2, o3, v ->
            if (o1 < crdentTypes.size) {
                var item = crdentTypes[o1]
                binding?.vPersonReservationInfo?.tvVenueRtnTypeValue?.text = item.name
                binding?.vPersonReservationInfo?.edtVenueRtnIdcardValue?.isEnabled = true
                hideHealthInfo()
                healthInfo = null
                currentCrdentType = item.value
                currentCrdentName = item.name
                if (item.value != "ID_CARD") {
                    isCanReservation = 1
                } else {
                    isCanReservation = 0
                    var idcardNum: String =
                        binding?.vPersonReservationInfo?.edtVenueRtnIdcardValue?.text.toString()
                    if (!idcardNum.isNullOrEmpty()) {
                        binding?.vPersonReservationInfo?.edtVenueRtnIdcardValue?.setText("" + idcardNum)
                    }
                }

                updateCertNum(item)
            }
        }.build<ConstellationBean>()
        if (!crdentTypes.isNullOrEmpty()) {
            typeSelector?.setPicker(crdentTypes)
            var item = crdentTypes[0]
            if (item != null) {
                currentCrdentType = item.value
                currentCrdentName = item.name
                binding?.vPersonReservationInfo?.tvVenueRtnTypeValue?.text = item.name
                updateCertNum(item)
            }
        }
    }
    /**
     * 当前选择类型是否是身份证号码
     */
    fun isIdCardType(): Boolean {
        if (currentCrdentType == null) {
            return false
        }
        try {
            if (currentCrdentType!!.toLowerCase() == "id_card") {
                return true
            }
        } catch (e: java.lang.Exception) {
        }

        return false
    }

    private fun updateCertNum(item: ConstellationBean) {

    }
}