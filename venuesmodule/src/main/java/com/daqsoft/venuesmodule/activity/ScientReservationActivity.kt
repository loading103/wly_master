package com.daqsoft.venuesmodule.activity

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.*
import com.daqsoft.baselib.widgets.FullyLinearLayoutManager
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.layoutmanager.FullyGridLayoutManager
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.businessview.fragment.HealthInfoFragment
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.activity.widgets.ReseravtionInfoPopWindow
import com.daqsoft.venuesmodule.adapter.VenueSelectDateAdapter
import com.daqsoft.venuesmodule.adapter.VenueSelectTimeAdapter
import com.daqsoft.venuesmodule.databinding.ActivityReservationInfoBinding
import com.daqsoft.venuesmodule.databinding.ActivityScenicReservationInfoBinding
import com.daqsoft.venuesmodule.fragment.AppointmentInfoFragment
import com.daqsoft.venuesmodule.model.VenueResOrderModel
import com.daqsoft.venuesmodule.viewmodel.ScenicReseravtionViewModel
import com.daqsoft.venuesmodule.viewmodel.VenueResevationInfoViewModel
import com.google.gson.GsonBuilder
import com.trello.rxlifecycle3.android.ActivityEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_reservation_info.*
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @Description ?????????????????????
 * @ClassName   ScientReservationActivity
 * @Author      luoyi
 * @Time        2020/7/2 30:56
 */
@Route(path = ARouterPath.VenuesModule.SCENIC_RESERVATION_ACTIVITY)
class ScientReservationActivity :
    TitleBarActivity<ActivityScenicReservationInfoBinding, ScenicReseravtionViewModel>() {
    /**
     * ?????????????????????ID
     */
    @JvmField
    @Autowired
    var scenicId: String = ""
    /**
     * ??????????????????
     */
    @JvmField
    @Autowired
    var selectDate: String = ""
    /**
     * ???????????? 1?????? 2??????
     */
    private var reservationType: Int = 1
    /**
     * ??????????????????
     */
    var inDate: String = ""

    var inPutType: Boolean = false

    var currentDate: String = ""
    /**
     * ?????????????????????
     */
    var venueSelectDateAdapter: VenueSelectDateAdapter? = null
    /**
     * ?????????????????????
     */
    var venueSelectTimeAdapter: VenueSelectTimeAdapter? = null


    /**
     * ???????????????????????????
     */
    var isNeedSmsCode: Boolean = true

    /**
     * ??????????????????
     */
    var venueOrderViewInfo: VenueOrderViewInfo? = null
    /**
     * ??????????????????
     */
    var venueReservationInfo: VenueReservationInfo? = null

    var venueResOrderModel: VenueResOrderModel? = null
    /**
     * ???????????????????????????
     */
    var mDatasVenueDateList: MutableList<VenueDateInfo> = mutableListOf()
    /**
     * ??????????????????
     */
    var reseravtionPop: ReseravtionInfoPopWindow? = null

    var healthInfoFragment: HealthInfoFragment? = null

    var appointInfoFrag: AppointmentInfoFragment? = null

    var currentNumbers: MutableList<String> = mutableListOf()

    override fun getLayout(): Int {
        return R.layout.activity_scenic_reservation_info
    }

    override fun setTitle(): String {
        return "????????????"
    }

    override fun injectVm(): Class<ScenicReseravtionViewModel> {
        return ScenicReseravtionViewModel::class.java
    }


    override fun initPageParams() {
        isInitImmerBar = false
    }

    override fun initView() {
        venueResOrderModel = VenueResOrderModel(
            reservationType, scenicId, currentDate, "", "", "", "",
            "", "", "", ""
        )
        initSelectDateView()
        initSelectTypeView()
        initSelectTimeView()
        initViewEvent()
        initViewModel()
        // ????????????
        appointInfoFrag = AppointmentInfoFragment.newInstance(1,ResourceType.CONTENT_TYPE_SCENERY)
        transactFragment(R.id.fl_venue_health_info, appointInfoFrag!!)
    }


    private fun initViewModel() {
        // ????????????????????????
        mModel.generVenuOrderLiveData.observe(this, Observer {
            if (!it.orderCode.isNullOrEmpty()) {
                mModel.payOrder(it.orderCode)
            } else {
                dissMissLoadingDialog()
                mBinding.tvConfirmToReseravation.isClickable = true
                ToastUtils.showMessage("??????????????????????????????!")
            }
        })
        mModel.activityFinishLd.observe(this, Observer {
            finish()
        })
        // ??????????????????
        mModel.venueOrderViewInfoLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null) {
                venueOrderViewInfo = it
                getVenueOrderDateList()
                bindVenueInfo(it)
                appointInfoFrag?.setData(
                    it, reservationType
                )
            }
        })
        // ????????????????????????
        mModel.venueOrderDateNumLiveData.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                var tempListDateInfo: MutableList<VenueDateInfo> = mutableListOf()
                for (i in mDatasVenueDateList.indices) {
                    var currday = venueReservationInfo!!.currDay
                    var item = it[i]
                    var itemDate = mDatasVenueDateList[i]
                    var stock = itemDate.maxNum - item.orderNum
                    itemDate.num = if (stock >= 0) {
                        stock
                    } else {
                        0
                    }
                    itemDate.isHavedReservation = item.existOrder == 1
                    venueSelectDateAdapter?.clear()
                    tempListDateInfo.add(itemDate)
                }
                if (tempListDateInfo.size > 7) {
                    var temp = tempListDateInfo.subList(0, 7)
                    if (venueReservationInfo!!.futureOrderDayNum > 7) {
                        temp.add(VenueDateInfo(-1, 1))
                    }
                    for (i in temp.indices) {
                        var itemDate = temp[i]
                        if (selectDate.isNullOrEmpty()) {
                            if (itemDate.openStatus == 1 && itemDate.num > 0) {
                                venueSelectDateAdapter?.selectPos = i
                                currentDate = itemDate.date
                                break
                            }
                        } else {
                            if (selectDate == itemDate.date) {
                                venueSelectDateAdapter?.selectPos = i
                                currentDate = itemDate.date
                                selectDate = ""
                                break
                            }
                        }
                    }
                    venueSelectDateAdapter?.add(temp)
                } else {
                    if (venueReservationInfo!!.futureOrderDayNum > 7) {
                        tempListDateInfo.add(VenueDateInfo(-1, 1))
                    }
                    for (i in tempListDateInfo.indices) {
                        var itemDate = tempListDateInfo[i]
                        if (selectDate.isNullOrEmpty()) {
                            if (itemDate.openStatus == 1 && itemDate.num > 0) {
                                venueSelectDateAdapter?.selectPos = i
                                currentDate = itemDate.date
                                break
                            }
                        } else {
                            if (selectDate == itemDate.date) {
                                venueSelectDateAdapter?.selectPos = i
                                currentDate = itemDate.date
                                selectDate = ""
                                break
                            }
                        }
                    }
                    venueSelectDateAdapter?.add(tempListDateInfo)
                }
                if (!currentDate.isNullOrEmpty()) {
                    mModel.getVenueOrderTimes(scenicId, currentDate, reservationType)
                } else {
                    showTipNoTimes()
                }
            }
        })
        // ????????????
        mModel.venueOrderDateListLiveData.observe(this, Observer
        {
            dissMissLoadingDialog()
            if (it != null && !it.dateInfo.isNullOrEmpty()) {
                mDatasVenueDateList.clear()
                mDatasVenueDateList.addAll(it.dateInfo)
                venueReservationInfo = it

                // ????????????????????????????????????
                if (venueReservationInfo!!.teamOrderStatus == 0 || venueReservationInfo!!.personOrderStatus == 0) {
                    ll_venue_select_type?.visibility = View.GONE
                    if (venueReservationInfo!!.teamOrderStatus == 1) {
                        reservationType = 2
                    } else {
                        reservationType = 1
                    }
                  changeVenueTipPersonNum()
                } else {
                    ll_venue_select_type?.visibility = View.VISIBLE
                }
                getVenueOrderDateNum()
            } else {

            }
        })
        // ?????????????????????
        mModel.venueOrderTimesLiveData.observe(this, Observer
        {
            dissMissLoadingDialog()
            // ????????????????????????
            if (!it.isNullOrEmpty()) {
                mBinding.llVenueResTime.rvVenueResTimes.visibility = View.VISIBLE
                mBinding.llVenueResTime.tvTipNoTimes.visibility = View.GONE

                venueSelectTimeAdapter?.clear()
                venueSelectTimeAdapter?.selectTimePos = -1
                venueResOrderModel?.timesId = ""
                venueResOrderModel?.timeStr = ""
                for (i in it.indices) {
                    var item = it[i]
                    if (item.stock != 0 && item.currTimeOrderStatus) {
                        // ??????????????????????????????????????????
                        venueSelectTimeAdapter?.selectTimePos = i
                        venueResOrderModel?.timesId = item.id.toString()
                        venueResOrderModel?.timeStr =
                            item.orderTimeSubStart + "-" + item.orderTimeSubEnd
                        if (item.guideOrderStatus == 1) {
                            mBinding.tvConfirmToResCommentator.visibility = View.GONE
                        } else {
                            mBinding.tvConfirmToResCommentator.visibility = View.GONE
                        }
                        break
                    }
                }
                venueSelectTimeAdapter?.add(it)
            } else {
                showTipNoTimes()
            }
        })

        // ???????????????????????????
        mModel.isNeedCode.observe(this, Observer
        {
            isNeedSmsCode = it
            if (it) {
                mBinding.vPersonReservationInfo.edtVenueReservationPpcodeValue.visibility =
                    View.VISIBLE
                mBinding.vPersonReservationInfo.tvVenueRtnPhoneCode.visibility = View.VISIBLE
                mBinding.vPersonReservationInfo.tvVenueRtnSendCode.visibility = View.VISIBLE
                mBinding.vPersonReservationInfo.vLineThree.visibility = View.VISIBLE
            } else {
                mBinding.vPersonReservationInfo.edtVenueReservationPpcodeValue.visibility =
                    View.GONE
                mBinding.vPersonReservationInfo.tvVenueRtnPhoneCode.visibility = View.GONE
                mBinding.vPersonReservationInfo.tvVenueRtnSendCode.visibility = View.GONE
                mBinding.vPersonReservationInfo.vLineThree.visibility = View.GONE
            }
        })
        // ???????????????
        mModel.vipInfold.observe(this, Observer
        {
            if (it != null) {
                var phone: String? = SPUtils.getInstance().getString(SPKey.PHONE)
                if (!phone.isNullOrEmpty()) {
                    mBinding.vPersonReservationInfo.edtVenueRtnPphoneValue.setText("" + phone)
                    mModel.checkExistNumber(phone)
                } else {
                    var phoneOld: String? =
                        SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_RESERATION_INFO)
                            .getString(SPUtils.Config.APP_RESERATION_PHONE)
                    if (!phoneOld.isNullOrEmpty()) {
                        mBinding.vPersonReservationInfo.edtVenueRtnPphoneValue.setText("" + phoneOld)
                        mModel.checkExistNumber(phoneOld)
                        phone = phoneOld
                    }
                }
                if (!it.name.isNullOrEmpty()) {
                    mBinding.vPersonReservationInfo.edtVenueRtnPnameValue.setText("" + it.name)
                    mBinding.vPersonReservationInfo.edtVenueRtnPnameValue.isEnabled = false
                    mBinding.vPersonReservationInfo.imgSelectVenueRtnName.visibility = View.GONE
                } else {
                    var name: String? =
                        SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_RESERATION_INFO)
                            .getString(SPUtils.Config.APP_RESERATION_NAME)
                    if (!name.isNullOrEmpty()) {
                        it.name = name
                        mBinding.vPersonReservationInfo.edtVenueRtnPnameValue.setText("" + name)
                        mBinding.vPersonReservationInfo.edtVenueRtnPnameValue.isEnabled = true
                    }
                }
                if (!it.idCard.isNullOrEmpty()) {
                    mBinding.vPersonReservationInfo.edtVenueRtnIdcardValue.setText("" + it.idCard)
                    mBinding.vPersonReservationInfo.edtVenueRtnIdcardValue.isEnabled = false
                } else {
                    var idCard: String? =
                        SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_RESERATION_INFO)
                            .getString(SPUtils.Config.APP_RESERATION_IDCARD)
                    if (!idCard.isNullOrEmpty()) {
                        it.idCard = idCard
                        mBinding.vPersonReservationInfo.edtVenueRtnIdcardValue.setText("" + idCard)
                        mBinding.vPersonReservationInfo.edtVenueRtnIdcardValue.isEnabled = true
                    }
                }
                if (!phone.isNullOrEmpty() && !it.name.isNullOrEmpty() && !it.idCard.isNullOrEmpty()) {
                    healthInfoFragment?.setIdCardAndPhone(phone, it.idCard, it.name)
                }

            }
        })
        mModel.sendPhoneCodeLd.observe(this, Observer
        {
            dissMissLoadingDialog()
            if (it) {
                ToastUtils.showMessage("?????????????????????")
                initTimer()
            } else {
                ToastUtils.showMessage("???????????????????????????????????????")
            }
        })
        // ????????????
        mModel.mError.observe(this, Observer
        {
            dissMissLoadingDialog()
            mBinding.tvConfirmToReseravation.isClickable = true
        })
    }

    /**
     * ??????????????????
     */
    private fun bindVenueInfo(it: VenueOrderViewInfo) {
        loadVenueImage(it)
        changeVenueTipPersonNum()
        appointInfoFrag?.changeResertionType(reservationType)
        mBinding.txtVenueName.text = "" + it.scenicName
        mBinding.txtVenueType.text = "" + it.type
        mBinding.txtVenueMaxPerson.text = "?????????????????????" + it.maxNum + "???"
        if (it.orderNotice.isNullOrEmpty()) {
            mBinding.tvVenueNotify.visibility = View.GONE
        } else {
            mBinding.tvVenueNotify.visibility = View.VISIBLE
        }

        // ???????????????????????????????????????
        mBinding.llVenueSelectType.cbxVenueResPernum.text = getString(
            R.string.venue_reservation_person_num,
            "" + it.personNumMix, "" + it.personNumMax
        )
        mBinding.llVenueSelectType.cbxVenueResTeamnum.text = getString(
            R.string.venue_reservation_team_num,
            "" + it.teamNumMin, "" + it.teamNumMax
        )
    }

    private fun changeVenueTipPersonNum() {
        appointInfoFrag?.changeResertionType(reservationType)
        if (reservationType == 1) {
            mBinding.vPersonReservationInfo.tvVenueRtnCompanyName.visibility = View.GONE
            mBinding.vPersonReservationInfo.edtVenueRtnInCompanyNameVlaue.visibility = View.GONE
            mBinding.vPersonReservationInfo.tvVenuePersonNumValue.text =
                "" + venueOrderViewInfo!!.personNumMix
            mBinding.vPersonReservationInfo.tvVenuePersonTip.text = getString(
                R.string.venue_reservation_tip_num,
                "" + venueOrderViewInfo!!.personNumMix, "" + venueOrderViewInfo!!.personNumMax
            )
        } else {
            mBinding.vPersonReservationInfo.tvVenuePersonNumValue.text =
                "" + venueOrderViewInfo!!.teamNumMin
            mBinding.vPersonReservationInfo.tvVenuePersonTip.text = getString(
                R.string.venue_reservation_tip_num,
                "" + venueOrderViewInfo!!.teamNumMin, "" + venueOrderViewInfo!!.teamNumMax
            )
            mBinding.vPersonReservationInfo.tvVenueRtnCompanyName.visibility = View.VISIBLE
            mBinding.vPersonReservationInfo.edtVenueRtnInCompanyNameVlaue.visibility = View.VISIBLE
        }
    }

    /**
     * ??????????????????
     */
    private fun loadVenueImage(data: VenueOrderViewInfo) {
        var imageUrl = ""
        if (!data.images.isNullOrEmpty()) {
            var images = data.images.split(",")
            if (!images.isNullOrEmpty()) {
                imageUrl = images[0]
            }
        }
        Glide.with(this@ScientReservationActivity)
            .load(imageUrl)
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(mBinding.imgVenue)
    }

    /**
     * ??????????????????
     */
    private fun initSelectTimeView() {
        venueSelectTimeAdapter = VenueSelectTimeAdapter(this@ScientReservationActivity)
        mBinding.llVenueResTime.rvVenueResTimes.adapter = venueSelectTimeAdapter
        mBinding.llVenueResTime.rvVenueResTimes.layoutManager = FullyLinearLayoutManager(
            this@ScientReservationActivity,
            FullyLinearLayoutManager.VERTICAL, false
        )
        venueSelectTimeAdapter?.onSelectTimeListener =
            object : VenueSelectTimeAdapter.OnSelectTimeListener {
                override fun onSelectTime(item: VenueOrderTime) {
                    venueResOrderModel?.timesId = item.id.toString()
                    venueResOrderModel?.timeStr =
                        item.orderTimeSubStart + "-" + item.orderTimeSubEnd
                    // ?????????????????????????????????
                    if (item.guideOrderStatus == 1) {
                        mBinding.tvConfirmToResCommentator.visibility = View.GONE
                    } else {
                        mBinding.tvConfirmToResCommentator.visibility = View.GONE
                    }
                }

            }
    }

    /**
     * ?????????????????????
     */
    private fun initSelectTypeView() {
        mBinding.llVenueSelectType.cbxVenueResPernum.isChecked = true
        mBinding.llVenueSelectType.cbxVenueResTeamnum.isChecked = false
    }

    /**
     * ?????????????????????
     */
    private fun initSelectDateView() {
        venueSelectDateAdapter = VenueSelectDateAdapter(this@ScientReservationActivity, scenicId)
        mBinding.llVenueResDate.rvVenueSelectDates.adapter = venueSelectDateAdapter
        mBinding.llVenueResDate.rvVenueSelectDates.layoutManager = FullyGridLayoutManager(
            this@ScientReservationActivity, 4,
            FullyGridLayoutManager.VERTICAL, false
        )
        venueSelectDateAdapter?.onVenueSelectDateListener =
            object : VenueSelectDateAdapter.OnVenueSelectDateListener {
                override fun onErrorTip(code: Int) {
                    if (reservationType == 1) {
                        // ????????????
                        ToastUtils.showMessage(
                            if (code == 2) {
                                getString(
                                    R.string.venue_reservation_tip_need_time,
                                    "" + venueReservationInfo!!.personAdvanceOrderDay
                                )
                            } else {
                                getString(
                                    R.string.venue_reservation_tip_in_time,
                                    "" + venueReservationInfo!!.futureOrderDayNum
                                )
                            }
                        )
                    } else {
                        // ????????????
                        if (code == 2) {
                            getString(
                                R.string.venue_reservation_tip_need_time,
                                "" + venueReservationInfo!!.teamAdvanceOrderDay
                            )
                        } else {
                            getString(
                                R.string.venue_reservation_tip_in_time,
                                "" + venueReservationInfo!!.futureOrderDayNum
                            )
                        }
                    }
                }

                override fun onChangedDate(item: VenueDateInfo) {
                    showLoadingDialog()
                    currentDate = item.date
                    mModel.getVenueOrderTimes(scenicId, item.date, reservationType)
                }

                override fun onToSelectDate() {
                    ARouter.getInstance()
                        .build(ARouterPath.VenuesModule.SCENIC_RES_SELECT_TIME_ACTIVITY)
                        .withString("scenicId", scenicId)
                        .withInt("type", reservationType)
                        .navigation(this@ScientReservationActivity, 3)
                }

            }
    }


    override fun initData() {
        if (inDate.isNullOrEmpty()) {
            inDate = DateUtil.getDateDayString("yyyy-MM-dd", Date())
            inPutType = true
        }
        mModel.getVenueOrderView(scenicId, inDate)
        mModel.getVipInfo()

    }

    private fun initViewEvent() {
//        mBinding.llvHealthCodeInfo.vZytfCodeInfo.onNoDoubleClick {
//            showZyTfCodeTipDialog()
//        }
        // ???????????????
        mBinding.tvConfirmToResCommentator.onNoDoubleClick {
            toOrderOrCommentator(1)
        }
        mBinding.tvConfirmToReseravation.onNoDoubleClick {
            toOrderOrCommentator(0)
        }
        // ??????????????????????????????????????????????????????
        mBinding.vPersonReservationInfo.edtVenueRtnPphoneValue.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length == 11) {
                    mModel.checkExistNumber(s.toString())
                }
            }

        })
        // ???????????????
        mBinding.vPersonReservationInfo.tvVenueRtnSendCode.onNoDoubleClick {
            var phoneNumer: String? =
                mBinding.vPersonReservationInfo.edtVenueRtnPphoneValue.text.toString()
            if (phoneNumer.isNullOrEmpty()) {
                ToastUtils.showMessage("????????????????????????")
            } else if (!RegexUtil.isMobilePhone(phoneNumer)) {
                ToastUtils.showMessage("??????????????????????????????")
            } else {
                showLoadingDialog()
                mModel.sendPhoneCode(phoneNumer)
            }
        }
        // ???????????????
        mBinding.vPersonReservationInfo.imgSelectVenueRtnName.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_CONTACT_MANAGEMENT)
                    .withInt("type", 2)
                    .navigation(this@ScientReservationActivity, 2)
            }
        }
        // ?????????????????????
        mBinding.llVenueSelectType.cbxVenueResPernum.onCheckedChange { buttonView, isChecked ->
            if (isChecked) {
                mBinding.llVenueSelectType.cbxVenueResPernum.isClickable = false
                mBinding.llVenueSelectType.cbxVenueResTeamnum.isClickable = true
                mBinding.llVenueSelectType.cbxVenueResTeamnum.isChecked = false
                reservationType = 1
                showLoadingDialog()
                getVenueOrderDateList()
                if (venueOrderViewInfo != null) {
                    appointInfoFrag?.changeResertionType(reservationType)
                }

            }
        }
        // ?????????????????????
        mBinding.llVenueSelectType.cbxVenueResTeamnum.onCheckedChange { buttonView, isChecked ->
            if (isChecked) {
                mBinding.llVenueSelectType.cbxVenueResTeamnum.isClickable = false
                mBinding.llVenueSelectType.cbxVenueResPernum.isClickable = true
                mBinding.llVenueSelectType.cbxVenueResPernum.isChecked = false
                reservationType = 2
                showLoadingDialog()
                getVenueOrderDateList()
                if (venueOrderViewInfo != null) {
                    appointInfoFrag?.changeResertionType(reservationType)
                }
            }
        }
        // ????????????
        mBinding.tvVenueNotify.onNoDoubleClick {
            showBookNotice()
        }
        mBinding.vPersonReservationInfo.edtVenueRtnPphoneValue.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.length == 11) {
                    onInputValueChanged()
                } else {
                    healthInfoFragment?.hideHealthInfoView()
                }
            }

        })
        mBinding.vPersonReservationInfo.edtVenueRtnIdcardValue.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.length == 18) {
                    onInputValueChanged()
                } else {
                    healthInfoFragment?.hideHealthInfoView()
                }
            }

        })
        mBinding.vPersonReservationInfo.edtVenueRtnPnameValue.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    onInputValueChanged()
                } else {
                    healthInfoFragment?.hideHealthInfoView()
                }
            }

        })
        mBinding.vPersonReservationInfo.tvVenuePersonNumValue.onNoDoubleClick {
            showSelectNumber()
        }
    }

    private fun showTipNoTimes() {
        venueResOrderModel?.timesId = ""
        venueResOrderModel?.timeStr = ""
        venueSelectTimeAdapter?.clear()
        mBinding.llVenueResTime?.rvVenueResTimes.visibility = View.GONE
        mBinding.llVenueResTime?.tvTipNoTimes.visibility = View.VISIBLE
    }

    private fun toOrderOrCommentator(type: Int) {
        if (currentDate.isNullOrEmpty() || venueResOrderModel!!.timesId.isNullOrEmpty()) {
            ToastUtils.showMessage("?????????????????????????????????,?????????????????????")
            return
        }

        if (venueOrderViewInfo != null) {
            if (venueOrderViewInfo!!.orderUserStatus == 1) {
                // ????????????
                var singlePeople: ReseartionContactExo? =
                    appointInfoFrag?.getSinglePeopleReseration()
                if (singlePeople == null) {
                    return
                } else {
                    venueResOrderModel!!.phone = singlePeople.userPhone
                    venueResOrderModel!!.name = singlePeople.userName
                    venueResOrderModel!!.idCardNum = singlePeople.userCardNumber
                    venueResOrderModel!!.cardType = singlePeople.userCardType
                    venueResOrderModel!!.userNum = singlePeople.userNum
                    venueResOrderModel!!.companyName = singlePeople.companyName
//                    var temps: MutableList<ReseartionContact> = mutableListOf()
//                    temps.add(singlePeople.getReserationContact())
//                    var json = GsonBuilder().create().toJson(temps)
//                    venueResOrderModel!!.attachedJsonStr = AESOperator.encryptWLYCBC(json)
                    venueResOrderModel!!.healthCodeRegion = singlePeople.healthRegion
                }
            } else {
                // ????????????
                var attachPeoples = appointInfoFrag?.getManyPeopleReserataion()
                if (attachPeoples.isNullOrEmpty()) {
                    ToastUtils.showMessage("??????????????????????????????????????????")
                    return
                } else {
                    var item = attachPeoples[0]
                    venueResOrderModel!!.phone = item.userPhone
                    venueResOrderModel!!.name = item.userName
                    venueResOrderModel!!.idCardNum = item.userCardNumber
                    venueResOrderModel!!.cardType = item.userCardType
                    if (reservationType == 2 && item.companyName.isNullOrEmpty()) {
                        ToastUtils.showMessage("???????????????????????????????????????????????????~")
                        return
                    }
                    venueResOrderModel!!.companyName = item.companyName
                    venueResOrderModel!!.userNum = attachPeoples.size.toString()
                    var json = GsonBuilder().create().toJson(attachPeoples)
                    venueResOrderModel!!.attachedJsonStr = SM4Util.encryptByEcb(json)
                    venueResOrderModel!!.healthCodeRegion = item.healthCodeRegion
                }
            }
        }
        venueResOrderModel!!.type = reservationType
        venueResOrderModel!!.date = currentDate
        if (type == 0) {
            showLoadingDialog()
            mModel.createVenueOrder(venueResOrderModel)
        } else {
            ARouter.getInstance()
                .build(ARouterPath.VenuesModule.VENUES_RESERVATION_COM_ACTIVITY)
                .withInt("resourceType", 0)
                .withString("venueId", scenicId)
                .withInt("reservationType", reservationType)
                .withString("venueSelectDate", currentDate)
                .withParcelable("venueOrder", venueResOrderModel)
                .navigation()
        }
    }


    /**
     * ?????????
     */
    private var timerDisposable: Disposable? = null

    private fun initTimer() {
        var timeLong: Long = 60
        timerDisposable = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .observeOn(AndroidSchedulers.mainThread())
            .map { count ->
                timeLong--
            }
            .subscribe { time ->
                if (time > 0) {
                    var to = getString(R.string.user_str_count_down, time)
                    mBinding.vPersonReservationInfo.tvVenueRtnSendCode.text = to
                    mBinding.vPersonReservationInfo.tvVenueRtnSendCode.isClickable = false
                    mBinding.vPersonReservationInfo.tvVenueRtnSendCode.isEnabled = false
                } else {
                    mBinding.vPersonReservationInfo.tvVenueRtnSendCode.text =
                        getString(R.string.user_label_send_code)
                    mBinding.vPersonReservationInfo.tvVenueRtnSendCode.isClickable = true
                    mBinding.vPersonReservationInfo.tvVenueRtnSendCode.isEnabled = true
                }

            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        appointInfoFrag?.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 2) {
//            // ?????????????????????
//            if (data?.getBundleExtra("bundle") != null) {
//                var bundle = data?.getBundleExtra("bundle")
//                var item = bundle.getParcelable<Contact>("object")
//                if (item != null) {
//                    mBinding.vPersonReservationInfo.edtVenueRtnPnameValue.setText("" + item!!.name)
//                    mBinding.vPersonReservationInfo.edtVenueRtnPphoneValue.setText("" + item!!.phone)
//                    if (!item.certType.isNullOrEmpty() && item.certType.equals("?????????") && !item.certNumber.isNullOrEmpty()) {
//                        mBinding.vPersonReservationInfo.edtVenueRtnIdcardValue.setText("" + item!!.certNumber)
//                        if (!item!!.name.isNullOrEmpty() && !item.phone.isNullOrEmpty()) {
//                            healthInfoFragment?.setIdCardAndPhone(item.phone, item.certNumber, item.name)
//                        }
//                    }
//                }
//            }
        } else if (resultCode == 3) {
            // ??????????????????
            if (data != null) {
                var dateInfo: String? = data.getStringExtra("dateStr")
                if (!dateInfo.isNullOrEmpty() && currentDate != dateInfo) {
                    currentDate = dateInfo
                    inDate = currentDate
                    inPutType = false
                    showLoadingDialog()
                    mModel.getVenueOrderDateList(scenicId, inDate, reservationType, 7)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerDisposable?.dispose()
        reseravtionPop = null
    }

    private fun onInputValueChanged() {
        var phone: String? = mBinding.vPersonReservationInfo.edtVenueRtnPphoneValue.text.toString()
        var name: String? = mBinding.vPersonReservationInfo.edtVenueRtnPnameValue.text.toString()
        var idCard: String? = mBinding.vPersonReservationInfo.edtVenueRtnIdcardValue.text.toString()
        if (!phone.isNullOrEmpty() && !name.isNullOrEmpty() && !idCard.isNullOrEmpty()) {
            healthInfoFragment?.setIdCardAndPhone(phone, idCard, name)
        }
    }

    /**
     * ??????????????????
     */
    private fun showBookNotice() {
        if (venueOrderViewInfo != null && !venueOrderViewInfo!!.orderNotice.isNullOrEmpty()) {
            if (reseravtionPop == null) {
                reseravtionPop = ReseravtionInfoPopWindow(this@ScientReservationActivity)
            }
            reseravtionPop!!.updateData(venueOrderViewInfo!!.orderNotice)
            if (!reseravtionPop!!.isShowing) {
                reseravtionPop!!.showAtLocation(mBinding.root, 0, 0, 0)
            }
        }
    }

    /**
     * ???????????????
     */
    private val numberPv by lazy {

        val pV = OptionsPickerBuilder(this, OnOptionsSelectListener { s1, s2, s3, v ->
            // 1 ?????? 2 ??????
            var num = currentNumbers.get(s1)
            when (reservationType) {
                1 -> {
                    mBinding.vPersonReservationInfo.tvVenuePersonNumValue.setText("${num}")
                    venueResOrderModel?.userNum = num.toString()
                }
                2 -> {
                    mBinding.vPersonReservationInfo.tvVenuePersonNumValue.setText("${num}")
                    venueResOrderModel?.userNum = num.toString()
                }
            }
        }).build<String>()

        pV
    }

    /**
     * ??????????????????????????????
     */
    private fun getTempResevationNum(): MutableList<String> {
        var numbers: MutableList<String> = mutableListOf()
        if (venueOrderViewInfo != null) {
            var max = venueOrderViewInfo!!.teamNumMax
            var min = venueOrderViewInfo!!.teamNumMin
            for (i in min..max) {
                numbers.add(i.toString())
            }
        }
        return numbers
    }

    /**
     * ??????????????????????????????
     */
    private fun getPersonRervationNum(): MutableList<String> {
        var numbers: MutableList<String> = mutableListOf()
        if (venueOrderViewInfo != null) {
            var max = venueOrderViewInfo!!.personNumMax
            var min = venueOrderViewInfo!!.personNumMix
            for (i in min..max) {
                numbers.add(i.toString())
            }
        }
        return numbers
    }

    private fun showSelectNumber() {
        UIHelperUtils.hideKeyboard(mBinding.vPersonReservationInfo.tvVenuePersonNumValue)
        if (venueOrderViewInfo != null) {
            when (reservationType) {
                1 -> {
                    // ????????????
                    numberPv?.setPicker(getPersonRervationNum())
                    currentNumbers.clear()
                    currentNumbers.addAll(getPersonRervationNum())
                }
                2 -> {
                    // ????????????
                    currentNumbers.clear()
                    currentNumbers.addAll(getTempResevationNum())
                }
            }
            numberPv?.setPicker(currentNumbers)
            var numStr: String? =
                mBinding.vPersonReservationInfo.tvVenuePersonNumValue.text.toString()
            if (!numStr.isNullOrEmpty() && !currentNumbers.isNullOrEmpty()) {
                var index = currentNumbers.indexOf(numStr)
                if (index >= 0) {
                    numberPv.setSelectOptions(index)
                }
            }
            numberPv?.show()
        }
    }

    private fun getVenueOrderDateList() {
        if (inPutType) {
            var toDay = DateUtil.getFutureDay(
                inDate, if (reservationType == 1) {
                    venueOrderViewInfo!!.personAdvanceOrderDay
                } else {
                    venueOrderViewInfo!!.teamAdvanceOrderDay
                }
            )
            if (toDay != null) {
                mModel.getVenueOrderDateList(scenicId, toDay, reservationType, 7)
            } else {
                mModel.getVenueOrderDateList(scenicId, inDate, reservationType, 7)
            }
        } else {
            mModel.getVenueOrderDateList(scenicId, inDate, reservationType, 7)
        }
    }

    private fun getVenueOrderDateNum() {
        if (inPutType) {
            var toDay = DateUtil.getFutureDay(
                inDate, if (reservationType == 1) {
                    venueOrderViewInfo!!.personAdvanceOrderDay
                } else {
                    venueOrderViewInfo!!.teamAdvanceOrderDay
                }
            )
            if (toDay != null) {
                mModel.getVenueOrderDateNum(scenicId, toDay, 7)
            } else {
                mModel.getVenueOrderDateNum(scenicId, inDate, 7)
            }
        } else {
            mModel.getVenueOrderDateNum(scenicId, inDate, 7)
        }
    }
}