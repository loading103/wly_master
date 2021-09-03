package com.daqsoft.venuesmodule.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.VenueDateInfo
import com.daqsoft.provider.bean.VenueDateNumBean
import com.daqsoft.provider.bean.VenueReservationInfo
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.activity.widgets.ReseravtionInfoPopWindow
import com.daqsoft.venuesmodule.databinding.FragVenueSelectReservationBinding
import com.daqsoft.venuesmodule.databinding.ItemVenueSelectTimeBinding
import com.daqsoft.venuesmodule.viewmodel.ScenicReseravtionTimeViewModel
import com.daqsoft.venuesmodule.viewmodel.VenueReseravtionTimeViewModel
import org.jetbrains.anko.textColor
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max

/**
 * @Description 场馆预约 选择时间
 * @ClassName   VenueSelectResevationFragment
 * @Author      luoyi
 * @Time        2020/5/6 9:12
 */
class ScenicSelectResevationFragment : BaseFragment<FragVenueSelectReservationBinding, ScenicReseravtionTimeViewModel>() {

    /**
     * 场馆id
     */
    var venueId: String? = ""
    /**
     * 当前选择时间
     */
    var dateInfo: String? = ""
    /**
     * 场馆预订信息
     */
    var venueReservationInfo: VenueReservationInfo? = null
    /**
     * 选择得场馆信息
     */
    var selectVenueDateInfo: String = ""
    /**
     * 场馆预约日期信息
     */
    var mDateDatas: MutableList<VenueDateInfo> = mutableListOf()

    var selectTimeItemListener: OnSelectTimeItemListener? = null
    /**
     * 预约类型
     *  type 1 个人预约 2 团队预约
     */
    var type: Int? = 1

    /**
     * 选择状态
     */
//    var selectMap: HashMap<Int, Int> = hashMapOf()
    /**
     * 预约须知弹窗
     */
    var reseravtionPop: ReseravtionInfoPopWindow? = null

    var isHaveResevationRecord: Boolean = false

    companion object {
        const val VENUE_ID = "venue_id"
        const val TYPE = "type"
        fun newInstance(venueId: String, type: Int): ScenicSelectResevationFragment {

            var bundle: Bundle = Bundle()
            var frag = ScenicSelectResevationFragment()
            bundle.putString(VENUE_ID, venueId)
            bundle.putInt(TYPE, type)
            frag.arguments = bundle
            return frag
        }

    }

    override fun getLayout(): Int {
        return R.layout.frag_venue_select_reservation
    }


    override fun injectVm(): Class<ScenicReseravtionTimeViewModel> {
        return ScenicReseravtionTimeViewModel::class.java
    }

    override fun initView() {
        adapter.emptyViewShow = false
        mBinding.recyVenueSelecTimes.layoutManager = GridLayoutManager(context!!, 7, GridLayoutManager.VERTICAL, false)
        mBinding.recyVenueSelecTimes.adapter = adapter
        initViewModel()
        initVieWEvent()
    }

    private fun initVieWEvent() {
        mBinding.vNextMonth.onNoDoubleClick {
            if (venueReservationInfo != null) {
                if (!venueReservationInfo!!.nextMonth.isNullOrEmpty()) {
                    // 获取下一个月的日期信息
                    showLoadingDialog()
                    dateInfo = venueReservationInfo!!.nextMonth
                    mModel.getVenueOrderDateList(venueId!!, dateInfo!!, type)
                } else {
                    ToastUtils.showMessage("非常抱歉，暂无相关预约信息~")
                }
            } else {
                ToastUtils.showMessage("稍等，正在获取月份信息~")
            }
        }
        mBinding.vPreMonth.onNoDoubleClick {

            if (venueReservationInfo != null) {
                if (!venueReservationInfo!!.preMonth.isNullOrEmpty()) {
                    // 取上一个月的信息
                    showLoadingDialog()
                    dateInfo = venueReservationInfo!!.preMonth
                    mModel.getVenueOrderDateList(venueId!!, dateInfo!!, type)
                } else {
                    ToastUtils.showMessage("非常抱歉，暂无相关预约信息~")
                }

            } else {
                ToastUtils.showMessage("稍等，正在获取月份信息~")
            }
        }
        mBinding.txtVenueReservationTipMore.onNoDoubleClick {
            if (venueReservationInfo != null && !venueReservationInfo!!.orderNotice.isNullOrEmpty()) {
                showReseravtionPop(venueReservationInfo!!.orderNotice)
            }
        }
    }

    private fun initViewModel() {
        mModel.venueOrderDateListLiveData.observe(this, Observer {
            if (it != null) {
                venueReservationInfo = it
                selectTimeItemListener?.resevationStatus(it.teamOrderStatus, it.personOrderStatus)
                dealReservationType(it.teamOrderStatus, it.personOrderStatus)
                mModel.getVenueOrderDateNum(venueId!!, dateInfo!!)
                if (!it.dateInfo.isNullOrEmpty()) {
                    if (!dateInfo.isNullOrEmpty()) {
                        var date = DateUtil.getFormatDateByString("yyyy-MM-dd", dateInfo)
                        var mothDay = DateUtil.getMothFirstDay(date)
                        if (mothDay != null) {
                            var week = DateUtil.getDayOfWeekDate(mothDay)
                            var datas: MutableList<VenueDateInfo> = mutableListOf()
                            if (week > 1) {
                                for (index in 1 until week) {
                                    datas.add(VenueDateInfo(-1))
                                }
                            }
                            datas.addAll(it.dateInfo)
                            mDateDatas.clear()
                            mDateDatas.addAll(datas)
                        }
                    }
                }
                mModel.getVenueOrderDateNum(venueId!!, dateInfo!!)
                if (!it.orderNotice.isNullOrEmpty())
                    mBinding.txtVenueReservationTip.text = it.orderNotice
                if (!it.currMonth.isNullOrEmpty()) {
                    mBinding.txtVenueTimes.text = DateUtil.formatDateByString(
                        "yyyy年MM月",
                        "yyyy-MM-dd", it.currMonth
                    )
                }

            } else {
                // 没有预订日期相关信息，显示空布局
//                venueReservationInfo = null
//                adapter.clear()
//                adapter.emptyViewShow = true
//                mBinding.txtVenueReservationTip.visibility = View.GONE
                dissMissLoadingDialog()
                ToastUtils.showMessage("非常抱歉,未找到${dateInfo}的场馆预约信息~")
            }
        })

        mModel.venueOrderDateNumLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            if (!it.isNullOrEmpty()) {
                var date = DateUtil.getFormatDateByString("yyyy-MM-dd", dateInfo)
                var mothDay = DateUtil.getMothFirstDay(date)
                if (mothDay != null) {
                    var week = DateUtil.getDayOfWeekDate(mothDay)
                    var datas: MutableList<VenueDateNumBean> = mutableListOf()
                    // 处理星期
                    if (week > 1) {
                        for (index in 1 until week) {
                            datas.add(VenueDateNumBean(-1))
                        }
                    }
                    //
                    datas.addAll(it)
                    for (i in mDateDatas.indices) {
                        var currday = venueReservationInfo!!.currDay
                        var item = datas[i]
                        var itemDate = mDateDatas[i]
                        if (item.orderNum > 0) {
                            isHaveResevationRecord = true
                        }
                        if (item.index != -1 && item.index == itemDate.index) {
                            if (item.index < currday) {
                                // 天数小于当前日期 不可以预约 不显示状态和数目
                                itemDate.status = 2
                            } else {
                                if (itemDate.openStatus == 0) {
                                    itemDate.status = 3
                                } else {
                                    itemDate.status = 4
                                    var num = itemDate.maxNum - item.orderNum
                                    // 可预约数量
                                    if (num <= 0) {
                                        itemDate.num = 0
                                    } else {
                                        itemDate.num = num
                                    }
                                    // 是否存在已预约的订单
                                    itemDate.isHavedReservation = item.existOrder == 1
                                }
                            }
                        }
                    }
                    // 设置数据
                    adapter?.clear()
                    adapter?.add(mDateDatas)
                    mBinding.txtVenueReservationTip.visibility = View.VISIBLE
                    mBinding.txtVenueReservationTipMore.visibility = View.VISIBLE
                    selectTimeItemListener?.controlResevationRecordShow(isHaveResevationRecord)
                }
            }
        })

        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            ToastUtils.showMessage("" + it?.msg)
        })
    }

    /**
     * 当团队预约或者个人预约 其中一项不可预约的时候
     * 处理当前预约类型
     */
    private fun dealReservationType(teamOrderStatus: Int, personOrderStatus: Int) {
        if (teamOrderStatus != 1 && personOrderStatus == 1) {
            type = 0
        }
        if (personOrderStatus != 1 && teamOrderStatus == 1) {
            type = 1
        }
    }

    override fun initData() {
        showLoadingDialog()
        try {
            venueId = arguments?.getString(VENUE_ID)
            type = arguments?.getInt(TYPE)
            // 当月第一天
            dateInfo = DateUtil.getDateDayString("yyyy-MM-dd", DateUtil.getMothFirstDay(Date()))
            if (!venueId.isNullOrEmpty() && !dateInfo.isNullOrEmpty()) {
                mModel.getVenueOrderDateList(venueId!!, dateInfo!!, type)
            } else {
                dissMissLoadingDialog()
            }
        } catch (e: Exception) {
            dissMissLoadingDialog()
        }
    }


    /**
     * 修改预订类型
     */
    fun updateType(typeV: Int) {
        type = typeV
        adapter.notifyItemRangeChanged(0, adapter.getData().size, "updateSelectType")
    }


    var adapter = object :
        RecyclerViewAdapter<ItemVenueSelectTimeBinding, VenueDateInfo>(R.layout.item_venue_select_time) {
        var selectPos = -1;
        override fun setVariable(mBinding: ItemVenueSelectTimeBinding, position: Int, item: VenueDateInfo) {
            mBinding.root.setBackgroundResource(R.drawable.shape_venue_resevation_normal)
            when (item.status) {
                1 -> {
                    // 不在当前月的日期
                    mBinding.txtVenueTime.text = ""
                    mBinding.txtVenueSelectStauts.text = ""
                    mBinding.vHavedResevation.visibility = View.GONE
                }

                2 -> {
                    // 小于当前时间的日期
                    mBinding.txtVenueTime.text = "${item.index}"
                    mBinding.vHavedResevation.visibility = View.GONE
                    mBinding.txtVenueSelectStauts.visibility = View.VISIBLE
                    mBinding.txtVenueSelectStauts.text = ""
                    mBinding.txtVenueSelectStauts.textColor = resources.getColor(R.color.bdbdbd)
                    mBinding.txtVenueTime.textColor = resources.getColor(R.color.color_999)
                }

                3 -> {
                    // 闭馆
                    mBinding.txtVenueTime.text = "${item.index}"
                    mBinding.vHavedResevation.visibility = View.GONE
                    mBinding.txtVenueSelectStauts.visibility = View.VISIBLE
                    mBinding.txtVenueSelectStauts.text = "闭馆"
                    mBinding.txtVenueSelectStauts.textColor = resources.getColor(R.color.bdbdbd)
                    mBinding.txtVenueTime.textColor = resources.getColor(R.color.color_999)
                }
                4 -> {
                    // 正常显示
                    mBinding.txtVenueTime.text = "${item.index}"
                    // 是否预约状态
                    mBinding.vHavedResevation.visibility = View.GONE
                    if (item.isHavedReservation) {
                        mBinding.vHavedResevation.visibility = View.VISIBLE
                    }
                    // 提前多少天预约
                    var maxDate = if (type == 0) {
                        venueReservationInfo!!.currDay + venueReservationInfo!!.personAdvanceOrderDay
                    } else {
                        venueReservationInfo!!.currDay + venueReservationInfo!!.teamAdvanceOrderDay
                    }
                    mBinding.txtVenueSelectStauts.visibility = View.VISIBLE
                    if (item.openStatus != 1) {
                        mBinding.txtVenueSelectStauts.textColor = resources.getColor(R.color.bdbdbd)
                        mBinding.txtVenueTime.textColor = resources.getColor(R.color.color_999)
                        mBinding.txtVenueSelectStauts.text = "不可预约"
                    } else {
                        mBinding.txtVenueTime.textColor = resources.getColor(R.color.color_333)
                        mBinding.txtVenueSelectStauts.textColor = resources.getColor(R.color.color_999)
                        if (item.num == 0) {
                            // 数量约满
                            if (item.maxNum == 0) {
                                mBinding.txtVenueSelectStauts.textColor = resources.getColor(R.color.bdbdbd)
                                mBinding.txtVenueTime.textColor = resources.getColor(R.color.color_999)
                                mBinding.txtVenueSelectStauts.text = "不可预约"
                            } else {
                                mBinding.txtVenueSelectStauts.text = "约满"
                            }
                        } else {
                            mBinding.txtVenueSelectStauts.text = "${item.num}"
                        }
                    }
                }
            }


            // 设置选中的样式
            if (!selectVenueDateInfo.isNullOrEmpty() && !item.date.isNullOrEmpty()) {
                if (selectVenueDateInfo == item.date) {
                    mBinding.root.setBackgroundResource(R.drawable.shape_venue_resevation_selected)
                    mBinding.txtVenueTime.textColor = resources.getColor(R.color.white)
                    mBinding.txtVenueSelectStauts.textColor = resources.getColor(R.color.white)
                }
            }

            if (venueReservationInfo!!.currDay == item.index) {
                mBinding.txtVenueTime.textColor = resources.getColor(R.color.colorPrimary)
            }
            mBinding.root.onNoDoubleClick {
                if (item.openStatus != 0 && item.num > 0) {
                    selectVenueDateInfo = item.date
                    if (item.openStatus != 1) {
//                        if (type == 1) {
//                            ToastUtils.showMessage("个人预约至少提前${venueReservationInfo!!.personAdvanceOrderDay}天")
//                        } else {
//                            ToastUtils.showMessage("团队预约至少提前${venueReservationInfo!!.teamAdvanceOrderDay}天")
//                        }
                        ToastUtils.showMessage("暂不支持预约")
                    } else {
                        // 可以预约
                        notifyItemRangeChanged(0, getData().size, "updateSelectStatus")
                        selectTimeItemListener?.selectTimeItem(item)
                    }
                }
            }
        }

        override fun payloadUpdateUi(mBinding: ItemVenueSelectTimeBinding, position: Int, payloads: MutableList<Any>) {
            if (payloads[0] == "updateSelectStatus") {
                var item = getData()[position]
                if (selectVenueDateInfo != null && item.date != null) {
                    if (selectVenueDateInfo == item.date) {
                        // 已选择
                        mBinding.root.setBackgroundResource(R.drawable.shape_venue_resevation_selected)
                        mBinding.txtVenueTime.textColor = resources.getColor(R.color.white)
                        mBinding.txtVenueSelectStauts.textColor = resources.getColor(R.color.white)
                    } else {
                        // 取消选择
                        mBinding.root.setBackgroundResource(R.drawable.shape_venue_resevation_normal)
                        var maxDate = if (type == 0) {
                            venueReservationInfo!!.currDay + venueReservationInfo!!.personAdvanceOrderDay
                        } else {
                            venueReservationInfo!!.currDay + venueReservationInfo!!.teamAdvanceOrderDay
                        }
                        if (item.num == 0 || item.maxNum == 0 || item.index < maxDate) {
                            mBinding.txtVenueSelectStauts.textColor = resources.getColor(R.color.bdbdbd)
                            mBinding.txtVenueTime.textColor = resources.getColor(R.color.color_999)
                        } else {
                            mBinding.txtVenueTime.textColor = resources.getColor(R.color.color_333)
                            mBinding.txtVenueSelectStauts.textColor = resources.getColor(R.color.color_999)
                        }
                    }
                }
            } else if (payloads[0] == "updateSelectType") {
                var item = getData()[position]
                if (item.openStatus != 1 && item.status == 4) {
                    mBinding.txtVenueSelectStauts.textColor = resources.getColor(R.color.bdbdbd)
                    mBinding.txtVenueTime.textColor = resources.getColor(R.color.color_999)
                    mBinding.txtVenueSelectStauts.text = "不可预约"
                }
            }
        }
    }

    interface OnSelectTimeItemListener {
        fun selectTimeItem(item: VenueDateInfo)
        fun resevationStatus(teamOrderStatus: Int, personOrderStatus: Int)
        fun controlResevationRecordShow(isHaveOrderRecorder: Boolean)
    }

    override fun onDestroy() {
        super.onDestroy()
        reseravtionPop = null
    }

    private fun showReseravtionPop(resravtionInfo: String) {
        if (reseravtionPop == null) {
            reseravtionPop = ReseravtionInfoPopWindow(context!!)
        }
        reseravtionPop!!.updateData(resravtionInfo)
        if (!reseravtionPop!!.isShowing) {
            reseravtionPop!!.showAtLocation(mBinding.root, 0, 0, 0)
        }
    }
}