package com.daqsoft.integralmodule.ui

import android.graphics.Typeface
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.integralmodule.R
import com.daqsoft.integralmodule.common.TaskTypeCode
import com.daqsoft.integralmodule.databinding.IntegralmoduleActivityDetailBinding
import com.daqsoft.integralmodule.databinding.IntegralmoduleItemRecordBinding
import com.daqsoft.integralmodule.repository.bean.DetailBean
import com.daqsoft.integralmodule.ui.vm.IntegralDetailActivityVm
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import kotlinx.android.synthetic.main.integralmodule_activity_detail.*
import timber.log.Timber
import java.lang.Exception
import java.util.*

/**
 * 积分明细
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-22
 * @since JDK 1.8.0_191
 */
@Route(path = ARouterPath.IntegralModule.INTEGRAL_DETAIL_ACTIVITY)
class IntegralDetailActivity :
    TitleBarActivity<IntegralmoduleActivityDetailBinding, IntegralDetailActivityVm>(),
    View.OnClickListener {


    override fun getLayout(): Int = R.layout.integralmodule_activity_detail

    override fun setTitle(): String = getString(R.string.integralmodule_integral_detail)

    override fun injectVm(): Class<IntegralDetailActivityVm> = IntegralDetailActivityVm::class.java


    private val adapter by lazy {
        object : RecyclerViewAdapter<IntegralmoduleItemRecordBinding, DetailBean>(
            R.layout.integralmodule_item_record
        ) {
            override fun setVariable(
                mBinding: IntegralmoduleItemRecordBinding,
                position: Int,
                item: DetailBean
            ) {
                mBinding.imgPointArrow.visibility = View.GONE
                mBinding.item = item
                /*  mBinding.ivTypeIcon.setImageResource(
                      TaskTypeCode.TaskTypeCodeToImg(
                          item.taskTypeCode!!
                      )
                  )*/
                var imageUrl = ""
                if (!item.icon.isNullOrEmpty()) {
                    var icons = item.icon.split(",")
                    if (!icons.isNullOrEmpty()) {
                        imageUrl = icons[0]
                    }
                }
                if (imageUrl.isNullOrEmpty())
                    mBinding.ivTypeIcon.setImageResource(
                        TaskTypeCode.TaskTypeCodeToImg(
                            item.taskTypeCode!!
                        )
                    ) else {
                    Glide.with(this@IntegralDetailActivity)
                        .load(imageUrl)
                        .placeholder(R.mipmap.placeholder_img_fail_240_180)
                        .into(mBinding.ivTypeIcon)
                }

                // 获得积分
                if (type == 0) {
                    mBinding.tvPoint.setTextColor(resources.getColor(R.color.db9a4e))
                    mBinding.tvPoint.text = "+" + item.modifyPoint + "积分"
                    mBinding.desc = item.desc
                    if (!item.modifySource.isNullOrEmpty()) {
                        // 退款获取的积分，显示跳转
                        if (item.modifySource == "cultural" || item.modifySource == "shop") {
                            mBinding.imgPointArrow.visibility = View.VISIBLE
                        }
                    }
                } else {
                    // 消耗积分
                    mBinding.tvPoint.setTextColor(resources.getColor(R.color.db9a4e))
                    mBinding.tvPoint.text = "" + item.modifyPoint + "积分"
                    if (!item.quantity.isNullOrEmpty())
                        mBinding.desc = "x${item.quantity}"
                    // 消耗获取的积分，显示跳转
                    if (item.modifySource == "shop") {
                        mBinding.imgPointArrow.visibility = View.VISIBLE
                    }
                }
                mBinding.root.onNoDoubleClick {
                    if (!item.modifySource.isNullOrEmpty()) {
                        when (item.modifySource) {
                            "cultural" -> {
                                // 文旅云订单
                                if (!item.resourceId.isNullOrEmpty()) {
                                    ARouter.getInstance()
                                        .build(ARouterPath.UserModule.USER_ORDER_DETAIL)
                                        .withString("orderId", item.resourceId)
                                        .withString("type", item.resourceType)
                                        .navigation()
                                }
                            }
                            "shop" -> {
                                // 小电商订单
                                if (!item.taskTypeCode.isNullOrEmpty()) {
                                    // 积分兑换
                                    if (item.taskTypeCode == "INTEGRAL_TYPE_EXCHANGE") {
                                        ///integral/orderDetail
                                        var uuid = SPUtils.getInstance().getString(SPKey.UUID)
                                        var token = SPUtils.getInstance().getString(SPKey.USER_CENTER_TOKEN)
                                        var encry = SPUtils.getInstance().getString(SPKey.ENCRYPTION)
                                        var shopUrl = SPUtils.getInstance().getString(SPKey.SHOP_URL)
                                        var url = "${shopUrl}/integral/orderDetail?id=${item?.resourceId}&unid=${uuid}&token=${token}&encryption=${encry}"
                                        ARouter.getInstance()
                                            .build(ARouterPath.Provider.WEB_ACTIVITY)
                                            .withString("mTitle", "积分商品")
                                            .withString("html", url)
                                            .navigation()
                                    } else {
                                        if (item.resourceType.isNullOrEmpty()) {

                                        } else {
                                            var path = when (item.resourceType) {
                                                // 实物订单
                                                "1" -> ARouterPath.UserModule.USER_ELECTRONIC_IN_KIND_DETAIL_ACTIVITY
                                                // 虚拟订单
                                                "2" -> ARouterPath.UserModule.USER_ELECTRONIC_VULTURE_DETAIL_ACTIVITY
                                                // 门票订单
                                                "3" -> ARouterPath.UserModule.USER_ELECTRONIC_TICKET_DETAIL_ACTIVITY
                                                // 酒店订单
                                                "5" -> ARouterPath.UserModule.USER_ELECTRONIC_HOTEL_DETAIL_ACTIVITY
                                                // 路线订单
                                                "6" -> ARouterPath.UserModule.USER_ELECTRONIC_LINE_DETAIL_ACTIVITY

                                                else -> ARouterPath.UserModule.USER_ELECTRONIC_VULTURE_DETAIL_ACTIVITY
                                            }
                                            if (path != null) {
                                                item.resourceType?.toInt()?.let {
                                                    ARouter.getInstance()
                                                        .build(path)
                                                        .withInt("id", item?.resourceId?.toInt() ?: -1)
                                                        .withInt("orderType", it ?: -1)
                                                        .navigation()
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    private var time = Utils.getDateTime("yyyy-MM", Date())
    private var type = 0
    override fun initView() {
        mBinding.vm = mModel
        mBinding.view = this
        setAdapter()
    }

    private fun setAdapter() {
        mRecordRv.apply {
            layoutManager = LinearLayoutManager(this@IntegralDetailActivity)
            adapter = this@IntegralDetailActivity.adapter
        }
        adapter.setOnLoadMoreListener { }
        adapter.loadEnd()
    }


    override fun notifyData() {
        super.notifyData()
        mModel.data.observe(this, Observer {
            adapter.clear()
            adapter.add(it)
        })
    }

    private val calendar by lazy {

        TimePickerBuilder(this, OnTimeSelectListener { date, view ->
            time = Utils.getDateTime("yyyy-MM", date)
            Timber.d("$time")
            setCalendarText()
            initData()
        })
            .setType(booleanArrayOf(true, true, false, false, false, false))
            .setRangDate(null, Calendar.getInstance())
            .build()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mGetRecordTv -> {
                // 获得记录
                mGetRecordTv.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                mIndictor1.visibility = View.VISIBLE

                mCostRecordTv.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mIndictor2.visibility = View.GONE
                type = 0
                mModel.detail(time, type)
            }
            R.id.mCostRecordTv -> {
                // 消耗记录
                mGetRecordTv.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mIndictor1.visibility = View.GONE

                mCostRecordTv.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                mIndictor2.visibility = View.VISIBLE
                type = 1
                mModel.detail(time, type)
            }
            R.id.mCalendarTv -> {
                // 日历
                calendar.show()
            }
        }
    }

    fun setCalendarText(){
         val monthCurrent = Utils.getDateTime("MM", Date())
        val monthSelected = time.split("-")[1]

        if(monthCurrent == monthSelected){
            mBinding.mCalendarTv.text = "日历（本月）"
        }else{
            mBinding.mCalendarTv.text = "日历（${monthSelected}月）"
        }

    }

    override fun initData() {
        mModel.detail(time, type)
        setCalendarText()
        mModel.pointCount()
    }
}