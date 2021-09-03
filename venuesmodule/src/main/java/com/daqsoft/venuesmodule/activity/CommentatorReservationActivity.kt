package com.daqsoft.venuesmodule.activity

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.PriceDecimalUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.FullyLinearLayoutManager
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.layoutmanager.FullyGridLayoutManager
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.CommetaryInfoBean
import com.daqsoft.provider.network.comment.beans.CommentTagsBean
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.adapter.*
import com.daqsoft.venuesmodule.databinding.ActivityCommentatorReservationBinding
import com.daqsoft.venuesmodule.fragment.VenueResInfoFragment
import com.daqsoft.venuesmodule.model.VenueCttrOrderModel
import com.daqsoft.venuesmodule.model.VenueResOrderModel
import com.daqsoft.venuesmodule.viewmodel.CommentatorReservationViewModel
import com.daqsoft.venuesmodule.viewmodel.CommtentatorModel
import java.math.BigDecimal
import kotlin.math.max
import kotlin.math.min

/**
 * @Description 讲解预约
 * @ClassName   CommentatorReservationActivity
 * @Author      luoyi
 * @Time        2020/7/4 13:40
 */
@Route(path = ARouterPath.VenuesModule.VENUES_RESERVATION_COM_ACTIVITY)
class CommentatorReservationActivity :
    TitleBarActivity<ActivityCommentatorReservationBinding, CommentatorReservationViewModel>() {

    /**
     * 文化场馆的资源ID
     */
    @JvmField
    @Autowired
    var venueId: String? = ""
    /**
     * 页面来源 0 正常来源 1 订单来源
     * 用于区分场馆预约信息展现方式
     */
    @JvmField
    @Autowired
    var resourceType: Int = 0
    /**
     * 预约类型
     */
    @JvmField
    @Autowired
    var reservationType: Int = 0

    @JvmField
    @Autowired
    var venueSelectDate: String? = ""

    @JvmField
    @Autowired
    var venueOrder: VenueResOrderModel? = null
    @JvmField
    @Autowired
    var orderCode: String? = ""
    @JvmField
    @Autowired
    var orderId: String? = ""
    /**
     * 讲解时间适配器
     */
    var cttrDateAdapter: CttrDateAdapter? = null
    /**
     * 讲解语言适配器
     */
    var cttrLanguageAdapter: CttrLanguageAdapter? = null
    /**
     * 展厅数据
     */
    var cttrExhallAdapter: CttrExhallAdapter? = null
    /**
     * 已预约的讲解员信息
     */
    var cttrHavedResAdapter: CttrHavedResAdapter? = null
    /**
     * 展厅价格规则
     */
    var cttrExhallRulesAdapter: CttrExhallRulesAdapter? = null

    var venueCttResBean: CommetaryInfoBean? = null
    /**
     * 使用人数
     */
    var userNum: String? = "0"

    var currentPrice: Double = 0.0

    var venueResInfoFrag: VenueResInfoFragment? = null
    /**
     * 提交讲解预约实体
     */
    var venueCttrOrderModel: VenueCttrOrderModel? = null

    override fun getLayout(): Int {
        return R.layout.activity_commentator_reservation
    }

    override fun setTitle(): String {
        return "讲解预约"
    }

    override fun initPageParams() {
        isInitImmerBar = false
    }

    override fun injectVm(): Class<CommentatorReservationViewModel> {
        return CommentatorReservationViewModel::class.java
    }

    override fun initView() {

        initVenueRervationInfo()
        initSelectLanguageView()
        initSelectDateView()
        initHavedResCttrOrder()
        initExhallRlues()
        initExhallView()
        initViewEvent()
        initViewModel()
    }

    private fun initHavedResCttrOrder() {
        cttrHavedResAdapter = CttrHavedResAdapter(this@CommentatorReservationActivity)
        mBinding.rvHavedResCommentators.adapter = cttrHavedResAdapter
        mBinding.rvHavedResCommentators.layoutManager = FullyLinearLayoutManager(
            this@CommentatorReservationActivity,
            FullyLinearLayoutManager.VERTICAL, false
        )
    }

    private fun initExhallRlues() {
        cttrExhallRulesAdapter = CttrExhallRulesAdapter(this@CommentatorReservationActivity)
        mBinding.llvExhallRules.rvExlainRules.adapter = cttrExhallRulesAdapter
        mBinding.llvExhallRules.rvExlainRules.layoutManager = FullyLinearLayoutManager(
            this@CommentatorReservationActivity,
            FullyLinearLayoutManager.VERTICAL, false
        )
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
        // 单独请求讲解预约的数据
        mModel.guideOrderInfoLd.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                // 有已经预约的讲解员信息
                mBinding.rvHavedResCommentators.visibility = View.VISIBLE
                mBinding.llCommentatorResevationInfo.visibility = View.GONE
                mBinding.tvReservationAgin.visibility = View.VISIBLE
                mBinding.tvTopayOrder.visibility = View.GONE
                cttrHavedResAdapter?.clear()
                cttrHavedResAdapter?.add(it)
            } else {
                // 没有已预约的讲解员信息
                mBinding.rvHavedResCommentators.visibility = View.GONE
            }
        })
        // 订单详情请求的数据
        mModel.venueOderInfoLd.observe(this, Observer {
            if (it != null) {
                if (it.venueOrder != null) {

                    mBinding.llvNormallVenueReservationInfo.visibility = View.GONE
                    mBinding.flVenueReservationInfo.visibility = View.VISIBLE
                    venueResInfoFrag = VenueResInfoFragment.newIntance(it.venueOrder!!)
                    transactFragment(R.id.fl_venue_reservation_info, venueResInfoFrag!!)
                    if (it.venueOrder.reservationType == "PERSON") {
                        reservationType = 1
                    } else {
                        reservationType = 2
                    }
                    venueId = it.venueOrder.venueId
                    userNum = it.venueOrder!!.useNum.toString()
                    venueSelectDate = it.venueOrder.orderDate
                    mModel.getGuideInfo(venueId!!, reservationType!!, venueSelectDate!!)
                    venueOrder = VenueResOrderModel(
                        reservationType,
                        it.venueOrder.venueId,
                        it.venueOrder.orderDate,
                        it.venueOrder.venueRuleId,
                        it.venueOrder.userPhone,
                        "",
                        it.venueOrder.idCard,
                        it.venueOrder.companyName,
                        it.venueOrder.useNum.toString(),
                        it.venueOrder.userName,
                        "",
                        it.venueOrder.healthCodeRegion
                    )
                }

                if (it.venueGuideOrder != null) {
                    mBinding.rvHavedResCommentators.visibility = View.VISIBLE
                    mBinding.llCommentatorResevationInfo.visibility = View.GONE
                    mBinding.tvReservationAgin.visibility = View.VISIBLE
                    mBinding.tvTopayOrder.visibility = View.GONE
                }

            } else {
                ToastUtils.showMessage("未获取到场馆预约和讲解员信息,请稍后再试~")
            }

        })
        mModel.mError.observe(this, Observer {
            mBinding.tvTopayOrder.isClickable = true
            dissMissLoadingDialog()
        })
        mModel.activityFinishLd.observe(this, Observer {
            finish()
        })
        mModel.guideInfoLd.observe(this, Observer {
            if (it != null) {
                venueCttResBean = it
                // 预约时间段
                if (!it.guideOrderTimes.isNullOrEmpty()) {
                    cttrDateAdapter?.clear()
                    for (i in it.guideOrderTimes.indices) {
                        var item = it.guideOrderTimes[i]
                        if (item != null && item.currTimeOrderStatus) {
                            cttrDateAdapter?.selectPos = i
                            break
                        }
                    }
                    cttrDateAdapter?.add(it.guideOrderTimes)
                }
                // 展馆
                if (!it.guideExhibitions.isNullOrEmpty()) {
                    it.guideExhibitions[0].isSelect = true
                    cttrExhallAdapter?.clear()
                    cttrExhallAdapter?.add(it.guideExhibitions)
                    cttrExhallAdapter?.maxSelect = it.exhibitionNum
                }
                // 展厅价格信息
                if (!it.guideExplainCosts.isNullOrEmpty()) {
                    cttrExhallRulesAdapter?.clear()
                    cttrExhallRulesAdapter?.add(it.guideExplainCosts)
                    setOrderPriceTxT(it)
                }
                var advanceDay: Int = if (reservationType == 1) {
                    it.personAdvanceOrderDay!!
                } else {
                    it.teamAdvanceOrderDay!!
                }
                if (advanceDay > 0) {
                    mBinding.tvVenueReservationInfoTip.text = resources!!.getString(
                        R.string.venue_reservation_commentator_tip,
                        "" + advanceDay,
                        "" + it.futureOrderDayNum
                    )
                } else {
                    if (it.futureOrderDayNum > 0) {
                        mBinding.tvVenueReservationInfoTip.text = "只能预约${it.futureOrderDayNum}天内的讲解"
                    } else {
                        mBinding.tvVenueReservationInfoTip.visibility = View.GONE
                    }
                }


            }
        })
    }

    private fun setOrderPriceTxT(it: CommetaryInfoBean) {
        if (it.guideExhibitions.isNullOrEmpty()) {
            return
        }
        var currentUserNum = 0
        if (!userNum.isNullOrEmpty())
            currentUserNum = userNum!!.toInt()
        var minIndex = 0
        var minCurrentNum = 0
        var priceStr: String? = ""
        for (i in it.guideExplainCosts.indices) {
            var item = it.guideExplainCosts[i]
            var minNum: Int = 0
            if (!item.minNum.isNullOrEmpty()) {
                minNum = item.minNum.toInt()
            }
            if (minCurrentNum == 0) {
                minIndex = i
            } else {
                if (minNum < minCurrentNum) {
                    minCurrentNum = minNum
                    minIndex = i
                }
            }
            var maxNum: Int = 0
            if (!item.maxNum.isNullOrEmpty()) {
                maxNum = item.maxNum.toInt()
            }
            if (currentUserNum in minNum..maxNum) {
                priceStr = resources!!.getString(
                    R.string.venue_res_cttr_order_tv,
                    if (venueCttrOrderModel!!.guideLanguage == "CH") {
                        item.chnExplain?.let {
                            currentPrice = it.toDouble()
                        }
                        PriceDecimalUtils.mul(
                            currentPrice,
                            cttrExhallAdapter!!.getSelectExhallSize().toDouble(),
                            2
                        ).toString()
                    } else {
                        item.engExplain?.let {
                            currentPrice = it.toDouble()
                        }
                        PriceDecimalUtils.mul(
                            currentPrice,
                            cttrExhallAdapter!!.getSelectExhallSize().toDouble(),
                            2
                        ).toString()
                    },
                    if (venueCttrOrderModel!!.guideLanguage == "CH") {
                        "中文"
                    } else {
                        "英文"
                    }
                )
                break
            }
        }
        if (priceStr.isNullOrEmpty()) {
            var item = it.guideExplainCosts[minIndex]
            priceStr = resources!!.getString(
                R.string.venue_res_cttr_order_tv, if (venueCttrOrderModel!!.guideLanguage == "CH") {
                    item.chnExplain?.let {
                        currentPrice = it.toDouble()
                    }
                    PriceDecimalUtils.mul(
                        currentPrice,
                        cttrExhallAdapter!!.getSelectExhallSize().toDouble(),
                        2
                    ).toString()
                } else {
                    item.engExplain?.let {
                        currentPrice = it.toDouble()
                    }
                    PriceDecimalUtils.mul(
                        currentPrice,
                        cttrExhallAdapter!!.getSelectExhallSize().toDouble(),
                        2
                    ).toString()
                },
                if (venueCttrOrderModel!!.guideLanguage == "CH") {
                    "中文"
                } else {
                    "英文"
                }
            )
            if (priceStr.isNullOrEmpty()) {
                priceStr = "确认提交"
            }
        }
        mBinding.tvTopayOrder.text = "" + priceStr
    }

    private fun initVenueRervationInfo() {
        if (resourceType == 0) {
            // 正常预约流程
            mBinding.flVenueReservationInfo?.visibility = View.GONE
            mBinding.llvNormallVenueReservationInfo?.visibility = View.VISIBLE
        } else {
            // 订单预约流程
            mBinding.flVenueReservationInfo?.visibility = View.VISIBLE
            mBinding.llvNormallVenueReservationInfo?.visibility = View.GONE
            venueResInfoFrag = VenueResInfoFragment()
            transactFragment(R.id.fl_venue_reservation_info, venueResInfoFrag!!)
        }
    }

    private fun initSelectDateView() {
        cttrDateAdapter = CttrDateAdapter(this@CommentatorReservationActivity)
        mBinding.rvCommentatorTimes.layoutManager =
            FullyGridLayoutManager(this@CommentatorReservationActivity, 3)
        mBinding.rvCommentatorTimes.adapter = cttrDateAdapter

    }

    private fun initSelectLanguageView() {
        cttrLanguageAdapter = CttrLanguageAdapter(this@CommentatorReservationActivity)
        mBinding.rvCommentatorLanguages.layoutManager = FullyGridLayoutManager(
            this@CommentatorReservationActivity,
            3
        )
        mBinding.rvCommentatorLanguages.adapter = cttrLanguageAdapter
        cttrLanguageAdapter?.add(CommtentatorModel.languages)
        cttrLanguageAdapter?.onCttrLgListener = object : CttrLanguageAdapter.OnCttrLgListener {
            override fun onClick(key: String) {
                venueCttrOrderModel?.guideLanguage = key
                if (venueCttResBean != null) {
                    setOrderPriceTxT(venueCttResBean!!)
                }
            }

        }
    }

    private fun initExhallView() {
        cttrExhallAdapter = CttrExhallAdapter(this@CommentatorReservationActivity)
        mBinding.rvCommentatorHalls.layoutManager = FullyLinearLayoutManager(
            this@CommentatorReservationActivity
            , FullyLinearLayoutManager.VERTICAL, false
        )
        cttrExhallAdapter?.onSelectExhallListener =
            object : CttrExhallAdapter.OnSelectExhallListener {
                override fun onSelectExhallListener(size: Int) {
                    if (currentPrice != null) {
                        var priceStr = resources!!.getString(
                            R.string.venue_res_cttr_order_tv,
                            PriceDecimalUtils.mul(currentPrice, size.toDouble(), 2).toString()
                            , if (venueCttrOrderModel!!.guideLanguage == "CH") {
                                "中文"
                            } else {
                                "英文"
                            }
                        )
                        if (!priceStr.isNullOrEmpty()) {
                            mBinding.tvTopayOrder.text = priceStr
                        }
                    }
                }

            }
        mBinding.rvCommentatorHalls.adapter = cttrExhallAdapter
    }

    private fun initViewEvent() {
        mBinding.tvReservationAgin?.onNoDoubleClick {
            mBinding.tvReservationAgin?.visibility = View.GONE
            mBinding.llCommentatorResevationInfo?.visibility = View.VISIBLE
            mBinding.tvTopayOrder?.visibility = View.VISIBLE
        }
        mBinding.tvTopayOrder?.onNoDoubleClick {
            showLoadingDialog()
            var inExhallTIme: String? = mBinding.edtVenueRtnInTime.text.toString()
            var remark: String? = mBinding.edtVenueRtnRemarkValue.text.toString()
            if (!inExhallTIme.isNullOrEmpty()) {
                venueCttrOrderModel?.inExhallTime = inExhallTIme
            }
            if (!remark.isNullOrEmpty()) {
                venueCttrOrderModel?.remark = remark
            }
            var timesId: String? = cttrDateAdapter?.getSelectTimesId()
            if (timesId.isNullOrEmpty()) {
                ToastUtils.showMessage("非常抱歉，无可预约时间段")
                return@onNoDoubleClick
            }
            venueCttrOrderModel?.guideOrderTimeId = timesId

            venueCttrOrderModel?.guideExhibitionIds = cttrExhallAdapter?.getSelectExhallList()
            mBinding.tvTopayOrder.isClickable = false
            mModel.createVenueOrder(venueOrder, venueCttrOrderModel)
        }
    }

    override fun initData() {
        venueCttrOrderModel = VenueCttrOrderModel(
            "CH", "", "", "", !orderCode.isNullOrEmpty(),
            if (orderCode.isNullOrEmpty()) {
                ""
            } else {
                orderCode
            }
            , venueId, ""
        )
        if (resourceType == 0) {
            if (venueOrder != null) {
                mBinding.tvVenueResPnum.text = "预约人数:${venueOrder!!.userNum}"
                mBinding.tvVenueResTime.text = "预约时间:${venueOrder!!.date} ${venueOrder!!.timeStr}"
                mModel.getGuiderOrderInfo(venueId!!, venueOrder!!.date!!)
                userNum = venueOrder!!.userNum
            }
            mModel.getGuideInfo(venueId!!, reservationType!!, venueSelectDate!!)

            mBinding.llvNormallVenueReservationInfo.visibility = View.VISIBLE
            mBinding.flVenueReservationInfo.visibility = View.GONE
        } else {
            mBinding.llvNormallVenueReservationInfo.visibility = View.GONE
            mBinding.flVenueReservationInfo.visibility = View.VISIBLE

            if (!orderId.isNullOrEmpty() && !orderCode.isNullOrEmpty()) {
                mModel.isOnlyCommtator = true
                venueCttrOrderModel?.existVenueRelationOrderCode = true
                venueCttrOrderModel?.venueOrdeCode = orderCode
                mModel.getHavedOderInfo(orderId!!)
            }
        }

    }
}