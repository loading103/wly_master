package com.daqsoft.usermodule.ui.credit

import android.annotation.SuppressLint
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.CreditLevelBean
import com.daqsoft.provider.bean.CreditScoreBean
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityMineCreditHistoryBinding
import com.daqsoft.usermodule.databinding.ActivityMineCreditHistoryItemBinding
import com.jakewharton.rxbinding2.view.RxView
import org.jetbrains.anko.textColorResource
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 *@package:com.daqsoft.usermodule.ui.credit
 *@date:2020/4/10 11:07
 *@author: caihj
 *@des:信用历史
 **/
@Route(path = ARouterPath.UserModule.USER_CREDIT_RECORD_ACTIVITY)
class MineCreditHistoryActivity : TitleBarActivity<ActivityMineCreditHistoryBinding, MineCreditViewModel>() {

    // 查询时间
    @Autowired
    @JvmField
    var date = ""

    // 当前分数
    @Autowired
    @JvmField
    var creditScore = "0"

    // 守约类型
    @Autowired
    @JvmField
    var type = ""

    @Autowired
    @JvmField
    var levels: ArrayList<CreditLevelBean>? = null

    // 当前刻度文本
    var levelTxt = ""
    private var time = "本月"

    private val calendar by lazy {

        TimePickerBuilder(this, OnTimeSelectListener { date, view ->
            time = Utils.getDateTime("MM", date)
            Timber.d("$time")
            mBinding.tvCalendar.text = "${time}月"
            mModel.time = Utils.getDateTime("yyyy-MM", date)
            recordAdapter.clear()
            mModel.getCreditRecords()
        })
            .setType(booleanArrayOf(true, true, false, false, false, false))
            .setRangDate(null, Calendar.getInstance())
            .build()
    }


    override fun getLayout(): Int = R.layout.activity_mine_credit_history


    override fun setTitle(): String = getString(R.string.credit_history_title)

    override fun injectVm(): Class<MineCreditViewModel> = MineCreditViewModel::class.java
    private var recordAdapter = object : RecyclerViewAdapter<ActivityMineCreditHistoryItemBinding, CreditScoreBean>(R.layout.activity_mine_credit_history_item) {
        override fun setVariable(mBinding: ActivityMineCreditHistoryItemBinding, position: Int, item: CreditScoreBean) {
            Glide.with(this@MineCreditHistoryActivity).load(item.icon).into(mBinding.ivCreditIcon)
            mBinding.tvCreditTitle.text = item.ruleName
            if (item.modifyScore.toInt() > 0) {
                mBinding.tvCreditContent.text = item.keepPromiseDesc
                mBinding.tvCreditScore.text = "+${item.modifyScore}"
            } else {
                mBinding.tvCreditContent.text = item.losePromiseDesc
                mBinding.tvCreditScore.text = item.modifyScore
                mBinding.tvCreditScore.textColorResource = R.color.color_lost_core
            }
            mBinding.tvDate.text = timeString2MD(item.modifyTime)
            if (item.platformName.isNotEmpty()) {
                mBinding.tvSite.text = item.platformName
            }
            if(item.isThisPlatform==1) {
                when (item.resourceType) {
                    ResourceType.CONTENT_TYPE_ACTIVITY,
                    ResourceType.CONTENT_TYPE_ACTIVITY_SHIU -> {
                        mBinding.tvCreditDetail.visibility = View.VISIBLE
                        mBinding.root.onNoDoubleClick {
                            ARouter.getInstance()
                                .build(ARouterPath.UserModule.USER_ORDER_DETAIL)
                                .withString("orderId", item.resourceValue)
                                .withString("type", item.resourceType)
                                .navigation()
                        }
                    }
                    else -> {
                        mBinding.tvCreditDetail.visibility = View.GONE
                    }
                }

            }else{
                mBinding.tvCreditDetail.visibility = View.GONE
            }
        }

    }

    @SuppressLint("CheckResult")
    override fun initView() {

        if (type == "KEEP_PROMISE") {
            mBinding.tvKeep.isSelected = true
            mBinding.tvLost.isSelected = false
            changeIndicator(mBinding.tvKeep)
        } else {
            mBinding.tvLost.isSelected = true
            mBinding.tvKeep.isSelected = false
            changeIndicator(mBinding.tvLost)
        }
        // 点击守约
        RxView.clicks(mBinding.tvKeep)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (!mBinding.tvKeep.isSelected) {
                    mBinding.tvKeep.isSelected = true
                    mBinding.tvLost.isSelected = false
                    mModel.currPage = 1
                    changeIndicator(mBinding.tvKeep)
                    mModel.recordType = "KEEP_PROMISE"
                    recordAdapter.clear()
                    showLoadingDialog()
                    mModel.getCreditRecords()
                }
            }
        // 点击失约
        RxView.clicks(mBinding.tvLost)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (!mBinding.tvLost.isSelected) {
                    mBinding.tvLost.isSelected = true
                    mBinding.tvKeep.isSelected = false
                    mModel.currPage = 1
                    changeIndicator(mBinding.tvLost)
                    mModel.recordType = "LOSE_PROMISE"
                    showLoadingDialog()
                    recordAdapter.clear()
                    mModel.getCreditRecords()
                }
            }

        mBinding.rvCreditRecords.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvCreditRecords.adapter = recordAdapter
        mModel.creditRecords.observe(this, Observer {
            dissMissLoadingDialog()
            if (it.size > 0) {
                pageDeal(it)
            }
        })
        recordAdapter.setOnLoadMoreListener {
            mModel.currPage++
            mModel.getCreditRecords()
        }

        if (date.isNotEmpty()) {
            mBinding.tvCalendar.text = "日历(本月)"
        } else {
            mBinding.tvCalendar.text = "日历"
        }
        mBinding.tvCalendar.onNoDoubleClick {
            calendar.show()
        }
        mBinding.tvPreMonth.text = String.format(getString(R.string.credit_history_pre_score), "0")
        mModel.creditPreMonthBean.observe(this, Observer {
            it.creditScore?.takeIf { it.isNotBlank() }?.apply { mBinding.tvPreMonth.text = String.format(getString(R.string.credit_history_pre_score), this) }
        })

        mBinding.tvNowMonthScore.text = creditScore
    }

    fun pageDeal(data: MutableList<CreditScoreBean>) {

        if (mModel.currPage == 1) {
            recordAdapter.clear()
        }
        if (data.size == 0) {
            recordAdapter.loadEnd()
            return
        }
        recordAdapter.add(data)
        if (data.size < mModel.pageSize) {
            recordAdapter.loadEnd()
        } else {
            recordAdapter.loadComplete()
        }
    }


    override fun initData() {
        mModel.time = date
        mModel.recordType = type
        mModel.getCreditRecords()
//        mModel.getCreditLevel()
        mModel.getPreCreditRecord()
        initLevelData()

    }

    fun timeString2MD(time: String): String {
        val format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

        if (time!!.isNullOrEmpty() || time == "null") {
            return format.format(Date())
        }
        val sdf: Date = format.parse(time)
        return format.format(sdf)
    }

    private fun changeIndicator(view: View) {
        val set = ConstraintSet()
        set.clone(mBinding.clCreditTypeSelect)
        set.connect(mBinding.vIndicator.id, ConstraintSet.LEFT, view.id, ConstraintSet.LEFT)
        set.connect(mBinding.vIndicator.id, ConstraintSet.RIGHT, view.id, ConstraintSet.RIGHT)
        set.applyTo(mBinding.clCreditTypeSelect)
    }


    /**
     * 初始化信用等级数据
     */
    private fun initLevelData() {
        if (levels?.size!! > 0) {
            var scales = IntArray(levels!!.size + 1)
            var scaleTxts = arrayOfNulls<String>(levels!!.size)
            for (i in 0 until levels!!.size) {
                scales[i + 1] = levels!![i].maxScore.toInt()
                scaleTxts[i] = levels!![i].name
                if (creditScore.toInt() >= levels!![i].minScore.toInt() && creditScore.toInt() <= scales[i + 1]) {
                    mBinding.tvCreditLevel.text = levels!![i].description
                    levelTxt = levels!![i].name
                }
            }
            mBinding.lpCreditLevel.setScales(scales, scaleTxts)
            mBinding.lpCreditLevel.setCurrentNum(creditScore.toInt())
            mBinding.lpCreditLevel.setScaleLevel(levelTxt)
        }
    }
}