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
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.activity.widgets.ReseravtionInfoPopWindow
import com.daqsoft.venuesmodule.adapter.VenueSelectDateAdapter
import com.daqsoft.venuesmodule.adapter.VenueSelectTimeAdapter
import com.daqsoft.venuesmodule.databinding.ActivityReservationInfoBinding
import com.daqsoft.venuesmodule.fragment.AppointmentInfoFragment
import com.daqsoft.venuesmodule.model.VenueResOrderModel
import com.daqsoft.venuesmodule.viewmodel.VenueResevationInfoViewModel
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_reservation_info.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
import java.util.*

/**
 * @Description 新场馆预约详情
 * @ClassName   VenueReservationV1Activity
 * @Author      luoyi
 * @Time        2020/7/2 30:56
 */
@Route(path = ARouterPath.VenuesModule.VENUES_RESERVATION_V1_ACTIVITY)
class VenueReservationV1Activity :
    TitleBarActivity<ActivityReservationInfoBinding, VenueResevationInfoViewModel>() {
    /**
     * 文化场馆的资源ID
     */
    @JvmField
    @Autowired
    var venueId: String = ""

    /**
     * 预约类型 1个人 2团队
     */
    private var reservationType: Int = 1

    /**
     * 当前选中日期
     */
    @JvmField
    @Autowired
    var selectDate: String = ""

    var inDate: String = ""

    var currentDate: String = ""

    var inPutType: Boolean = false

    /**
     * 选择时间适配器
     */
    var venueSelectDateAdapter: VenueSelectDateAdapter? = null

    /**
     * 选择时间段适配
     */
    var venueSelectTimeAdapter: VenueSelectTimeAdapter? = null


    /**
     * 是否需要短信验证码
     */
    var isNeedSmsCode: Boolean = true

    /**
     * 场馆订单视图
     */
    var venueOrderViewInfo: VenueOrderViewInfo? = null

    /**
     * 场馆预订信息
     */
    var venueReservationInfo: VenueReservationInfo? = null

    var venueResOrderModel: VenueResOrderModel? = null

    /**
     * 场馆预约日期数据集
     */
    var mDatasVenueDateList: MutableList<VenueDateInfo> = mutableListOf()

    /**
     * 预约须知弹窗
     */
    var reseravtionPop: ReseravtionInfoPopWindow? = null


    var appointInfoFrag: AppointmentInfoFragment? = null

    var currentNumbers: MutableList<String> = mutableListOf()

    override fun getLayout(): Int {
        return R.layout.activity_reservation_info
    }

    override fun setTitle(): String {
        return "场馆预约"
    }

    override fun injectVm(): Class<VenueResevationInfoViewModel> {
        return VenueResevationInfoViewModel::class.java
    }

    override fun initPageParams() {
        isInitImmerBar = false
    }

    override fun initView() {
        venueResOrderModel = VenueResOrderModel(
            reservationType, venueId, currentDate, "", "", "", "",
            "", "", "", "", ""
        )
        initSelectDateView()
        initSelectTypeView()
        initSelectTimeView()
        initViewEvent()
        initViewModel()
        // 健康信息
        appointInfoFrag = AppointmentInfoFragment.newInstance(1, ResourceType.CONTENT_TYPE_VENUE)
        transactFragment(R.id.fl_venue_health_info, appointInfoFrag!!)
    }


    private fun initViewModel() {
        // 生成预约订单信息
        mModel.generVenuOrderLiveData.observe(this, Observer {
            if (!it.orderCode.isNullOrEmpty()) {
                mModel.payOrder(it.orderCode)
            } else {
                dissMissLoadingDialog()
                ToastUtils.showMessage("预约失败，请稍后再试!")
            }
        })
        // 场馆预约信息
        mModel.venueOrderViewInfoLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null) {
                venueOrderViewInfo = it
                // 预约类型
                //默认个人预约
                getVenueOrderDateList()
                bindVenueInfo(it)
                appointInfoFrag?.setData(
                    it, reservationType
                )
            }
        })
        mModel.activityFinishLd.observe(this, Observer {
            finish()
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
        // 日期对应预约数量
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
                    mModel.getVenueOrderTimes(venueId, currentDate, reservationType)
                } else {
                    showTipNoTimes()
                }
            }
        })
        // 日期列表
        mModel.venueOrderDateListLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null && !it.dateInfo.isNullOrEmpty()) {
                mDatasVenueDateList.clear()
                mDatasVenueDateList.addAll(it.dateInfo)
                venueReservationInfo = it
                // 判断团队和个是否能够预约
                if (venueReservationInfo!!.teamOrderStatus == 0 || venueReservationInfo!!.personOrderStatus == 0) {
                    ll_venue_select_type?.visibility = View.GONE
                    if (venueReservationInfo!!.teamOrderStatus == 1) {
                        reservationType = 2
                    } else {
                        reservationType = 1
                    }
                    changeVenueTipPersonNum()
                    appointInfoFrag?.changeResertionType(reservationType)
                } else {
                    ll_venue_select_type?.visibility = View.VISIBLE
                }
                getVenueOrderDateNum()
            } else {

            }
        })
        // 时间段请求数据
        mModel.venueOrderTimesLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            // 预约时段信息设置
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
                        // 取第一个有库存的时间段为选择
                        venueSelectTimeAdapter?.selectTimePos = i
                        venueResOrderModel?.timesId = item.id.toString()
                        venueResOrderModel?.timeStr =
                            item.orderTimeSubStart + "-" + item.orderTimeSubEnd
                        if (item.guideOrderStatus == 1) {
                            mBinding.tvConfirmToResCommentator.visibility = View.VISIBLE
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

    }

    private fun showTipNoTimes() {
        venueResOrderModel?.timesId = ""
        venueResOrderModel?.timeStr = ""
        venueSelectTimeAdapter?.clear()
        mBinding.llVenueResTime?.rvVenueResTimes.visibility = View.GONE
        mBinding.llVenueResTime?.tvTipNoTimes.visibility = View.VISIBLE
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
                mModel.getVenueOrderDateList(venueId, toDay, reservationType, 7)
            } else {
                mModel.getVenueOrderDateList(venueId, inDate, reservationType, 7)
            }
        } else {
            mModel.getVenueOrderDateList(venueId, inDate, reservationType, 7)
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
                mModel.getVenueOrderDateNum(venueId, toDay, 7)
            } else {
                mModel.getVenueOrderDateNum(venueId, inDate, 7)
            }
        } else {
            mModel.getVenueOrderDateNum(venueId, inDate, 7)
        }
    }

    /**
     * 绑定场馆信息
     */
    private fun bindVenueInfo(it: VenueOrderViewInfo) {
        loadVenueImage(it)
        changeVenueTipPersonNum()
        mBinding.txtVenueName.text = "" + it.venueName
        mBinding.txtVenueType.text = "" + it.type
        mBinding.txtVenueMaxPerson.text = "最大接待人数：" + it.maxNum + "人"
        if (it.orderNotice.isNullOrEmpty()) {
            mBinding.tvVenueNotify.visibility = View.GONE
        } else {
            mBinding.tvVenueNotify.visibility = View.VISIBLE
        }
        // 团队和个人最大预约区间设置
        mBinding.llVenueSelectType.cbxVenueResPernum.text = getString(
            R.string.venue_reservation_person_num,
            "" + it.personNumMix, "" + it.personNumMax
        )
        mBinding.llVenueSelectType.cbxVenueResTeamnum.text = getString(
            R.string.venue_reservation_team_num,
            "" + it.teamNumMin, "" + it.teamNumMax
        )

    }

    /**
     * 加载场馆图片
     */
    private fun loadVenueImage(data: VenueOrderViewInfo) {
        var imageUrl = ""
        if (!data.images.isNullOrEmpty()) {
            var images = data.images.split(",")
            if (!images.isNullOrEmpty()) {
                imageUrl = images[0]
            }
        }
        Glide.with(this@VenueReservationV1Activity)
            .load(imageUrl)
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(mBinding.imgVenue)
    }

    /**
     * 初始化时间段
     */
    private fun initSelectTimeView() {
        venueSelectTimeAdapter = VenueSelectTimeAdapter(this@VenueReservationV1Activity)
        mBinding.llVenueResTime.rvVenueResTimes.adapter = venueSelectTimeAdapter
        mBinding.llVenueResTime.rvVenueResTimes.layoutManager = FullyLinearLayoutManager(
            this@VenueReservationV1Activity,
            FullyLinearLayoutManager.VERTICAL, false
        )
        venueSelectTimeAdapter?.onSelectTimeListener =
            object : VenueSelectTimeAdapter.OnSelectTimeListener {
                override fun onSelectTime(item: VenueOrderTime) {
                    venueResOrderModel?.timesId = item.id.toString()
                    venueResOrderModel?.timeStr =
                        item.orderTimeSubStart + "-" + item.orderTimeSubEnd
                    // 判断是否可以预约讲解员
                    if (item.guideOrderStatus == 1) {
                        mBinding.tvConfirmToResCommentator.visibility = View.VISIBLE
                    } else {
                        mBinding.tvConfirmToResCommentator.visibility = View.GONE
                    }
                }

            }
    }

    /**
     * 初始化选择类型
     */
    private fun initSelectTypeView() {
        mBinding.llVenueSelectType.cbxVenueResPernum.isChecked = true
        mBinding.llVenueSelectType.cbxVenueResTeamnum.isChecked = false
    }

    /**
     * 初始化选择日期
     */
    private fun initSelectDateView() {
        venueSelectDateAdapter = VenueSelectDateAdapter(this@VenueReservationV1Activity, venueId)
        mBinding.llVenueResDate.rvVenueSelectDates.adapter = venueSelectDateAdapter
        mBinding.llVenueResDate.rvVenueSelectDates.layoutManager = FullyGridLayoutManager(
            this@VenueReservationV1Activity, 4,
            FullyGridLayoutManager.VERTICAL, false
        )
        venueSelectDateAdapter?.onVenueSelectDateListener =
            object : VenueSelectDateAdapter.OnVenueSelectDateListener {
                override fun onErrorTip(code: Int) {
                    if (reservationType == 1) {
                        // 个人预约
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
                        // 团队预约
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
                    mModel.getVenueOrderTimes(venueId, item.date, reservationType)
                }

                override fun onToSelectDate() {
                    ARouter.getInstance()
                        .build(ARouterPath.VenuesModule.VENUE_RES_SELECT_TIME_ACTIVITY)
                        .withString("venueId", venueId)
                        .withInt("type", reservationType)
                        .navigation(this@VenueReservationV1Activity, 3)
                }

            }
    }


    override fun initData() {
        if (inDate.isNullOrEmpty()) {
            inDate = DateUtil.getDateDayString("yyyy-MM-dd", Date())
            inPutType = true
        }
        mModel.getVenueOrderView(venueId, inDate)
        mModel.getVipInfo()

    }

    private fun initViewEvent() {
//        mBinding.llvHealthCodeInfo.vZytfCodeInfo.onNoDoubleClick {
//            showZyTfCodeTipDialog()
//        }
        // 预约讲解员
        mBinding.tvConfirmToResCommentator.onNoDoubleClick {
            toOrderOrCommentator(1)
        }
        mBinding.tvConfirmToReseravation.onNoDoubleClick {
            toOrderOrCommentator(0)
        }
        // 切换为个人预约
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
        // 切换为团队预约
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
        // 预订须知
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
//                    healthInfoFragment?.hideHealthInfoView()
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
//                    healthInfoFragment?.hideHealthInfoView()
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
                }
            }

        })
        mBinding.vPersonReservationInfo.tvVenuePersonNumValue.onNoDoubleClick {
            showSelectNumber()
        }
    }

    private fun toOrderOrCommentator(type: Int) {

        if (currentDate.isNullOrEmpty() || venueResOrderModel!!.timesId.isNullOrEmpty()) {
            ToastUtils.showMessage("请确认是否选择预约时间,或无可预约时间")
            return
        }

        if (venueOrderViewInfo != null) {
            if (venueOrderViewInfo!!.orderUserStatus == 1) {
                // 单人预约
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
                    var temps: MutableList<ReseartionContact> = mutableListOf()
                    temps.add(singlePeople.getReserationContact())
//                    var json = GsonBuilder().create().toJson(temps)
//                    venueResOrderModel!!.attachedJsonStr = AESOperator.encryptWLYCBC(json)
                    venueResOrderModel!!.healthCodeRegion = singlePeople.healthRegion
                }
            } else {
                // 多人预约
                var attachPeoples = appointInfoFrag?.getManyPeopleReserataion()
                if (attachPeoples.isNullOrEmpty()) {
                    ToastUtils.showMessage("请完成填写预约人和随行人信息")
                    return
                } else {
                    var item = attachPeoples[0]
                    venueResOrderModel!!.phone = item.userPhone
                    venueResOrderModel!!.name = item.userName
                    venueResOrderModel!!.idCardNum = item.userCardNumber
                    venueResOrderModel!!.cardType = item.userCardType
                    if (reservationType == 2 && item.companyName.isNullOrEmpty()) {
                        ToastUtils.showMessage("非常抱歉，团队预约必须填写单位名称~")
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
                .withString("venueId", venueId)
                .withInt("reservationType", reservationType)
                .withString("venueSelectDate", currentDate)
                .withParcelable("venueOrder", venueResOrderModel)
                .navigation()
        }
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        appointInfoFrag?.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 3) {
            // 选择日期返回
            if (data != null) {
                var dateInfo: String? = data.getStringExtra("dateStr")
                if (!dateInfo.isNullOrEmpty() && currentDate != dateInfo) {
                    currentDate = dateInfo
                    inDate = currentDate
                    inPutType = false
                    showLoadingDialog()
                    mModel.getVenueOrderDateList(venueId, inDate, reservationType, 7)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        reseravtionPop = null
    }

    private fun onInputValueChanged() {
        var phone: String? = mBinding.vPersonReservationInfo.edtVenueRtnPphoneValue.text.toString()
        var name: String? = mBinding.vPersonReservationInfo.edtVenueRtnPnameValue.text.toString()
        var idCard: String? = mBinding.vPersonReservationInfo.edtVenueRtnIdcardValue.text.toString()
        if (!phone.isNullOrEmpty() && !name.isNullOrEmpty() && !idCard.isNullOrEmpty()) {
//            healthInfoFragment?.setIdCardAndPhone(phone, idCard, name)
        }
    }

    /**
     * 显示预订须知
     */
    private fun showBookNotice() {
        if (venueOrderViewInfo != null && !venueOrderViewInfo!!.orderNotice.isNullOrEmpty()) {
            if (reseravtionPop == null) {
                reseravtionPop = ReseravtionInfoPopWindow(this@VenueReservationV1Activity)
            }
            reseravtionPop!!.updateData(venueOrderViewInfo!!.orderNotice)
            if (!reseravtionPop!!.isShowing) {
                reseravtionPop!!.showAtLocation(mBinding.root, 0, 0, 0)
            }
        }
    }

    /**
     * 数量选择器
     */
    private
    val numberPv by lazy {

        val pV = OptionsPickerBuilder(this, OnOptionsSelectListener { s1, s2, s3, v ->
            // 1 个人 2 团队
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
     * 获取团队预约到场人数
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
     * 获取个人预约到场人数
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


}