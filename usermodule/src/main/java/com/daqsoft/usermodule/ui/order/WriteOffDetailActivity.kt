package com.daqsoft.usermodule.ui.order

import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.FullyLinearLayoutManager
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.CertTypes
import com.daqsoft.provider.bean.OrderDetail
import com.daqsoft.provider.bean.OrderUserBean
import com.daqsoft.provider.businessview.event.UpdateWriterOffStatus
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityWriteoffDetailBinding
import com.daqsoft.usermodule.ui.order.adapter.MineWriterOffAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit

/**
 * @Author：      邓益千
 * @Create by：   2020/7/14 10:03
 * @Description： 核销详情
 */
@Route(path = ARouterPath.UserModule.WRITE_OFF_DETAIL)
class WriteOffDetailActivity :
    TitleBarActivity<ActivityWriteoffDetailBinding, OrdersBookViewModel>() {

    @Autowired
    @JvmField
    var orderId: String = ""

    private var code = ""

    /**
     * 倒计时
     */
    private var cutdownDisable: Disposable? = null

    private var validFlag = false

    private var chooseDatas: ArrayList<OrderUserBean> = ArrayList()

    private var wrriterOffNum: Int = 0

    private lateinit var detail: OrderDetail

    private var isWriterOffAll: Boolean = true


    private val adapter by lazy {
        MineWriterOffAdapter()
    }

    override fun setTitle(): String = getString(R.string.order_detail)

    override fun getLayout(): Int = R.layout.activity_writeoff_detail

    override fun injectVm(): Class<OrdersBookViewModel> = OrdersBookViewModel::class.java

    override fun initView() {
        mBinding.writeoffBut.setOnClickListener {
            if (validFlag) {
                finish()
            } else if (detail.orderStatus != "13") {
                showLoadingDialog()
                if (chooseDatas.isNullOrEmpty()) {
                    isWriterOffAll = wrriterOffNum >= detail!!.surplusNum
                    mModel.postWriteOff(code, wrriterOffNum, "CONTAIN", "")
                } else {
                    isWriterOffAll = false
                    mModel.postWriteOff(code, wrriterOffNum, "EXCLUDE", getNoWriterOffIds())
                }
            }
        }
        mBinding.vAppointNum.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.VenuesModule.VENUE_APPOINT_USER_ACTIVITY)
                .withString("orderId", orderId)
                .navigation()
        }
        mBinding.llChooseNoUser.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.VenuesModule.NO_WRITER_OFF_USER_ACTIVITY)
                .withString("orderId", orderId)
                .withParcelableArrayList("chooseData", chooseDatas)
                .navigation(this@WriteOffDetailActivity, 0x12)
        }
        mBinding.tvToHexiaoInfo.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.VenuesModule.VENUE_APPOINT_USER_ACTIVITY)
                .withString("orderId", orderId)
                .navigation()
        }
        mBinding.coverView.onNoDoubleClick {
            if (detail != null && !detail.resourceId.isNullOrEmpty()) {
                MenuJumpUtils.gotoResourceDetail(detail.resourceType, detail.resourceId!!)
            }
        }
        mBinding.titleView.onNoDoubleClick {
            if (detail != null && !detail.resourceId.isNullOrEmpty()) {
                MenuJumpUtils.gotoResourceDetail(detail.resourceType, detail.resourceId!!)
            }
        }
        adapter?.emptyViewShow = false
        mBinding.recyclerDetailsMore.adapter = adapter
        mBinding.recyclerDetailsMore.layoutManager = FullyLinearLayoutManager(
            this@WriteOffDetailActivity,
            FullyLinearLayoutManager.VERTICAL,
            false
        )
        mBinding.imgAddSingleNum.onNoDoubleClick {
            operationWriterOffNum(0)
        }
        mBinding.imgReduceSingleNum.onNoDoubleClick {
            operationWriterOffNum(1)
        }
        initViewModel()
    }


    @Synchronized
    private fun operationWriterOffNum(mode: Int) {
        detail?.let {
            if (mode == 0) {
                if (wrriterOffNum < it.surplusNum) {
                    wrriterOffNum = wrriterOffNum + 1
                    mBinding.tvLabelSingleCurrentNum.text = "$wrriterOffNum"
                }
            } else {
                if (wrriterOffNum > 1) {
                    wrriterOffNum = wrriterOffNum - 1
                    mBinding.tvLabelSingleCurrentNum.text = "$wrriterOffNum"
                }
            }
        }
    }

    private fun initViewModel() {
        mModel.orderDetail.observe(this, Observer {
            mBinding.detail = it
            detail = it

            code = it.code
            validFlag = it.orderStatus == "12"
            if (it.payMoney == "0.0") {
//                mBinding.costTypeView.text = "免费"
//                mBinding.costView.text = "支付：免费"
                mBinding.costTypeView.text=""
                mBinding.costView.text = ""
            } else {
                mBinding.costTypeView.text = it.payMoney
                mBinding.costView.text = "支付：${it.payMoney.toDouble() * it.orderNum.toDouble()}"
            }

            if (!it.images.isNullOrEmpty()) {
                mBinding.urlcover = it.images.split(",")[0]
            }
            if (it.reservationType == ResourceType.PERSON) {
                mBinding.appointType.content = "个人预约"
            } else {
                mBinding.appointType.content = "团队预约"
            }
            mBinding.appointTime.apply {
                content = DateUtil.getTwoDateDayStrs(it.orderStartTime, it.orderEndTime)
            }
            if (!it.useNum.isNullOrEmpty()) {
                mBinding.appointNum.content = "${it.useNum}人"
            }
            mBinding.appointExpiry.apply {
                content = DateUtil.getTwoDateDayStrs(it.useStartTime, it.useEndTime)
            }
            if (!it.cardType.isNullOrEmpty()) {
                mBinding.idCardView.setLabel("" + CertTypes.getCertTypeName(it.cardType))
            }

            if (!validFlag) {
                if (it.orderStatus == "13") {
                    mBinding.orderStateView.apply {
                        text = "很抱歉，预约时间已过，订单已失效！"
                        setTextColor(ResourceUtils.getColor(this, R.color.btn_txt1))
                    }
                    mBinding.writeoffBut.setBackgroundColor(
                        ResourceUtils.getColor(
                            this,
                            R.color.colorPrimary_un
                        )
                    )
                    mBinding.labelTitle3.visibility = View.VISIBLE
                    mBinding.hasWriteOff.visibility = View.VISIBLE
                    mBinding.canWriteOff.visibility = View.VISIBLE
                    mBinding.canWriteOff.content = "0"
                    mBinding.hasWriteOff.content = "0"
                    mBinding.imgReduceSingleNum.visibility = View.GONE
                    mBinding.imgAddSingleNum.visibility = View.GONE
                } else {
                    mBinding.orderStateView.text = "待核销，等待入园！"
                    mBinding.labelTitle3.visibility = View.VISIBLE
                    mBinding.hasWriteOff.visibility = View.VISIBLE
                    mBinding.canWriteOff.visibility = View.VISIBLE
                    mBinding.hasWriteOff.content = "0"
                    mBinding.canWriteOff.content = it.orderNum
                }

            } else {
                mBinding.orderStateView.text = "核销成功！欢迎入园"
                mBinding.labelTitle3.visibility = View.VISIBLE
                mBinding.hasWriteOff.visibility = View.VISIBLE
                mBinding.canWriteOff.visibility = View.VISIBLE
                mBinding.canWriteOff.content = "0"
                mBinding.writeoffBut.apply {
                    text = "关闭"
                    setBackgroundColor(ResourceUtils.getColor(this, R.color.color_bdbdbd))
                }
            }
            // 是否包含随行人,有
            if (it.hasAttached == 1) {
                mBinding.clOrderSingle.visibility = View.GONE
                mBinding.clOrderMultiple.visibility = View.VISIBLE
                mBinding.hasWriteOffMore.tvContent.text = "" + it.consumeNum
                mBinding.canWriteOffMore.tvContent.text = "请选择"
                hideHealthInfo()
                if (it.surplusNum > 0 && it.orderStatus != "13") {
                    mBinding.llChooseNoUser.visibility = View.VISIBLE
                    mBinding.hasWriteOffMore.visibility = View.VISIBLE
                } else {
                    if (it.orderStatus == "13") {
                        mBinding.llChooseNoUser.visibility = View.VISIBLE
                        mBinding.hasWriteOffMore.visibility = View.VISIBLE
                        mBinding.hasWriteOffMore.tvContent.text = "0"
                        mBinding.canWriteOffMore.tvContent.text = "0"
                        mBinding.ivChooseNoUser.visibility = View.GONE
                    } else {
                        mBinding.llChooseNoUser.visibility = View.GONE
                        mBinding.hasWriteOffMore.visibility = View.GONE
                    }
                }
                mBinding.electronCodeMore.apply {
                    visibility = View.VISIBLE
                    content = it.code
                }

            } else {
                if (!it.cardType.isNullOrEmpty()) {
                    if (CertTypes.isIdCardType(it.cardType!!)) {
                        mModel.getHealthSetingInfo()
                    }
                } else {
                    mModel.getHealthSetingInfo()
                }
                mBinding.hasWriteOff.tvContent.text = "" + it.consumeNum
                mBinding.canWriteOff.tvContent.text = "" + it.surplusNum
                wrriterOffNum = it.surplusNum
                mBinding.tvLabelSingleCurrentNum.text = "" + wrriterOffNum

                mBinding.writeOffTime.apply {
                    //                    content = if (!it.validList.isNullOrEmpty()) {
//                        visibility = View.VISIBLE
//                        "" + it.validList!![0]?.validTime
//                    } else {
//                        visibility = View.GONE
//                        "暂无"
//                    }
                    visibility = View.GONE
                }
                if (it.surplusNum > 1) {
                    mBinding.vSingleHasMoreNum.visibility = View.VISIBLE
                    mBinding.canWriteOff.visibility = View.GONE
                } else {
                    mBinding.vSingleHasMoreNum.visibility = View.GONE
                    mBinding.canWriteOff.visibility = View.VISIBLE
                }
                mBinding.electronCode.apply {
                    visibility = View.VISIBLE
                    content = it.code
                }
                mBinding.vAppointNum.isClickable = false
                mBinding.ivToApointNum.visibility = View.GONE
                mBinding.tvToHexiaoInfo.visibility = View.GONE
                mBinding.clOrderSingle.visibility = View.VISIBLE
                mBinding.clOrderMultiple.visibility = View.GONE
            }
            if (it.validList.isNullOrEmpty()) {
                mBinding.recyclerDetailsMore.visibility = View.GONE
                mBinding.tvToHexiaoInfo.visibility = View.GONE
                mBinding.electronCodeMore.visibility = View.GONE
            } else {
                if (it.surplusNum == 0) {
                    mBinding.recyclerDetailsMore.visibility = View.VISIBLE
                    if (it.hasAttached == 1) {
                        mBinding.tvToHexiaoInfo.visibility = View.VISIBLE
                    } else {
                        mBinding.tvToHexiaoInfo.visibility = View.GONE
                    }
                    mBinding.electronCodeMore.visibility = View.VISIBLE
                    adapter.clear()
                    adapter.add(it.validList!!)
                } else {
                    mBinding.recyclerDetailsMore.visibility = View.GONE
                    mBinding.tvToHexiaoInfo.visibility = View.GONE
                    mBinding.electronCodeMore.visibility = View.GONE
                }
            }
        })
        mModel.healthSetingLd.observe(this, Observer {
            if (it != null) {
                if (it.enableTravelCode || it.enableHealthyCode) {
                    mBinding.tvLabelTitle2.visibility = View.VISIBLE
                    if (detail != null)
                        mModel.getUserHealthInfo(detail.orderId)
                    if (it.enableHealthyCode) {
                        mBinding.tvHealthState.visibility = View.VISIBLE
                    }
                    if (it.enableTravelCode) {
                        mBinding.tvTravelCodeState.visibility = View.VISIBLE
                    }
                    mBinding.lineView3.visibility = View.VISIBLE
                } else {
                    hideHealthInfo()
                }
            } else {
                hideHealthInfo()
            }
        })
        // 健康码
        mModel.helathInfo.observe(this, Observer {
            if (it != null) {
                mBinding.helath = it
                // 健康码状态
                if (it.healthCode.isNullOrEmpty()) {
                    var drawable: Drawable
                    mBinding.tvHealthState.tvContent.text = "未注册"
                    mBinding.tvRegistAddres.visibility = View.GONE
                    mBinding.tvHealthState.tvContent.setTextColor(resources.getColor(R.color.color_999))
                    drawable = ResourceUtils.getDrawable(getContext(), R.mipmap.icon_health_unkn)
                    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                    mBinding.tvHealthState.setContentIcon(drawable)
                    mBinding.tvHealthState.tvContent.compoundDrawablePadding =
                        resources.getDimension(R.dimen.dp_5).toInt()
                } else {
                    mBinding.tvRegistAddres.visibility = View.VISIBLE
                    mBinding.tvRegistAddres.content = "" + it.regionName
                    var drawable: Drawable
                    when (it.healthCode) {
                        "01" -> {
                            mBinding.tvHealthState.content = getString(R.string.health_normal)
                            mBinding.tvHealthState.tvContent.setTextColor(resources.getColor(R.color.c_36cd64))
                            drawable =
                                ResourceUtils.getDrawable(getContext(), R.mipmap.icon_health_normal)
                        }
                        "11" -> {
                            mBinding.tvHealthState.content = getString(R.string.health_middle)
                            mBinding.tvHealthState.tvContent.setTextColor(resources.getColor(R.color.color_ff9e05))
                            drawable =
                                ResourceUtils.getDrawable(getContext(), R.mipmap.icon_health_warn)
                        }
                        "31" -> {
                            mBinding.tvHealthState.content = getString(R.string.health_bad)
                            mBinding.tvHealthState.tvContent.setTextColor(resources.getColor(R.color.ff4e4e))
                            drawable =
                                ResourceUtils.getDrawable(getContext(), R.mipmap.icon_health_danger)
                        }
                        else -> {
                            mBinding.tvHealthState.content = "未知"
                            mBinding.tvHealthState.tvContent.setTextColor(resources.getColor(R.color.color_999))
                            drawable =
                                ResourceUtils.getDrawable(getContext(), R.mipmap.icon_health_unkn)
                        }
                    }
                    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                    mBinding.tvHealthState.setContentIcon(drawable)
                    mBinding.tvHealthState.tvContent.compoundDrawablePadding =
                        resources.getDimension(R.dimen.dp_5).toInt()
                }
                // 旅游码状态
                var drawable: Drawable
                if (it.smartTravelRegisterStatus) {
                    mBinding.tvTravelCodeState.tvContent.text = "已注册"
                    mBinding.tvTravelCodeState.tvContent.setTextColor(resources.getColor(R.color.c_36cd64))
                    drawable = ResourceUtils.getDrawable(
                        getContext(),
                        com.daqsoft.provider.R.mipmap.venue_book_condition_icon_low
                    )
                } else {
                    mBinding.tvTravelCodeState.tvContent.setTextColor(resources.getColor(R.color.color_999))
                    mBinding.tvTravelCodeState.tvContent.text = "未注册"
                    drawable = ResourceUtils.getDrawable(
                        getContext(),
                        R.mipmap.venue_book_condition_icon_unknown
                    )
                }
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                mBinding.tvTravelCodeState.setContentIcon(drawable)
                mBinding.tvTravelCodeState.tvContent.compoundDrawablePadding =
                    resources.getDimension(R.dimen.dp_5).toInt()
            }
        })


        mModel.result.observe(this, Observer {
            dissMissLoadingDialog()
            if (it) {
                ToastUtils.showMessage("核销成功，欢迎入园")
                chooseDatas.clear()
                EventBus.getDefault().post(UpdateWriterOffStatus())
                mModel.getWriteOffDetail(orderId)
                if (!isWriterOffAll) {
                    // 部分核销显示核销成功页面
                    ARouter.getInstance().build(ARouterPath.UserModule.PART_WRITER_OFF_SUCCESS)
                        .withString("orderId", orderId)
                        .navigation()
                }
            } else {
                ToastUtils.showMessage("核销失败，请稍后再试~")
            }
            isWriterOffAll = true
        })
        mModel.mError.observe(this, Observer {
            isWriterOffAll = true
            dissMissLoadingDialog()
            if (it.message.isNullOrEmpty()) {
                ToastUtils.showMessage("" + it.message)
            }
        })
    }

    private fun getNoWriterOffIds(): String? {
        var temps: MutableList<String> = mutableListOf()
        for (item in chooseDatas) {
            temps.add(item.id.toString())
        }
        return temps.joinToString(",")
    }

    override fun initData() {
        mModel.getWriteOffDetail(orderId)


    }

    private fun hideHealthInfo() {
        mBinding.tvHealthState.visibility = View.GONE
        mBinding.tvRegistAddres.visibility = View.GONE
        mBinding.tvLabelTitle2.visibility = View.GONE
        mBinding.tvTravelCodeState.visibility = View.GONE
        mBinding.lineView3.visibility = View.INVISIBLE
    }

    private fun showCutDownView() {
        cutdownDisable?.dispose()
        cutdownDisable = Observable.interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mBinding.appraiseView.text = DateUtil.getNowTimeString()
                mBinding.orderDateView.text = DateUtil.getNowWeekTimeString()
            }
    }

    override fun onResume() {
        super.onResume()
        showCutDownView()
    }

    override fun onPause() {
        super.onPause()
        cutdownDisable?.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0x12) {
            if (data != null) {
                var choseList: ArrayList<OrderUserBean>? =
                    data.getParcelableArrayListExtra<OrderUserBean>("data")
                var writerOffNumber: Int = data.getIntExtra("writerOffNum", 0)
                if (!choseList.isNullOrEmpty()) {
                    chooseDatas.clear()
                    chooseDatas.addAll(choseList)
                    var temps: MutableList<String> = mutableListOf()
                    for (item in chooseDatas) {
                        if (!item.userName.isNullOrEmpty()) {
                            temps.add(item.userName)
                        }
                    }
                    mBinding.canWriteOffMore.content = temps.joinToString(",")
                }

            }
        }
    }
}