package com.daqsoft.usermodule.ui.credit

import android.os.Parcelable
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.net.excut
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityMineCreditBinding
import com.daqsoft.usermodule.databinding.CreditActivityRoomItemBinding
import com.daqsoft.usermodule.databinding.CreditWdItemBinding
import com.daqsoft.venuesmodule.repository.bean.VenuesListBean
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.textColorResource
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.daqsoft.provider.getRealImages

/**
 *@package:com.daqsoft.usermodule.ui.credit
 *@date:2020/4/9 10:43
 *@author: caihj
 *@des:我的信用
 **/

@Route(path = ARouterPath.UserModule.USER_CREDIT_ACTIVITY)
class MineCreditActivity : TitleBarActivity<ActivityMineCreditBinding, MineCreditViewModel>() {

    override fun getLayout(): Int = R.layout.activity_mine_credit
    override fun injectVm(): Class<MineCreditViewModel> = MineCreditViewModel::class.java

    //信用维度adapter
    var creditWdAdapter =
        object : RecyclerViewAdapter<CreditWdItemBinding, CreditWdBean>(R.layout.credit_wd_item) {
            override fun setVariable(
                mBinding: CreditWdItemBinding,
                position: Int,
                item: CreditWdBean
            ) {
                Glide.with(this@MineCreditActivity).load(item.icon).into(mBinding.ivCreditWdIcon)
                mBinding.tvCreditWdTitle.text = item.title
                mBinding.tvCreditWdContent.text = item.content
                mBinding.tvCreditWdLeftTxt.text = item.leftTxt
                if (item.rightTxt.isNullOrEmpty()) {
                    mBinding.tvCreditWdRightTxt.visibility = View.GONE
                } else {
                    mBinding.tvCreditWdRightTxt.text = item.rightTxt
                }
                when (item.type) {
                    // 预约行为
                    "yyxw" -> {
                        mBinding.tvCreditWdLeftTxt.onNoDoubleClick {
                            ARouter.getInstance()
                                .build(ARouterPath.VenuesModule.VENUES_LIST_ACTIVITY).navigation()
                        }
                        // 跳到活动
                        mBinding.tvCreditWdRightTxt.onNoDoubleClick {
                            ARouter.getInstance().build(MainARouterPath.MAIN_ACTIVITY_LS)
                                .navigation()
                        }

                    }
                    "sftz" -> {
                        var path = ""
                        if (item.realNameStatus) {
                            mBinding.tvCreditWdLeftTxt.text = "已实名认证"
                            path = ARouterPath.UserModule.AUTHENTICATE_COMPLETE_ACTIVITY
                        } else {
                            path = ARouterPath.UserModule.AUTHENTICATE_COMMIT_ACTIVITY
                            // 身份特征
                        }
                        mBinding.root.onNoDoubleClick {
                            ARouter.getInstance().build(path).navigation()
                        }
                    }
                    "syls" -> {
                        // 守约历史
                        mBinding.tvCreditWdLeftTxt.onNoDoubleClick {
                            gotoRecord("", "KEEP_PROMISE")
                        }
                        mBinding.tvCreditWdRightTxt.onNoDoubleClick {
                            gotoRecord("", "UN_KEEP_PROMISE")
                        }
                    }
                }


            }

        }

    // 活动预约
    var activityAdapter = object :
        RecyclerViewAdapter<CreditActivityRoomItemBinding, ActivityBean>(R.layout.credit_activity_room_item) {
        override fun setVariable(
            mBinding: CreditActivityRoomItemBinding,
            position: Int,
            item: ActivityBean
        ) {
            Glide.with(this@MineCreditActivity).load(item.images.getRealImages()).placeholder(R.mipmap.placeholder_img_fail_240_180).into(mBinding.miImage)
            mBinding.tvContent.text = item.name
            if (item.faithAuditStatus != "0") {
                mBinding.ivCreditType.background = getDrawable(R.mipmap.mine_credit_xyqy_exempt)
                mBinding.tvScore.text = item.faithAuditValue
                mBinding.tvScore.text =
                    String.format(getString(R.string.credit_activit_score), item.faithAuditValue)
                mBinding.tvScore.textColorResource = R.color.color_title_line
            }
            if (item.faithUseStatus != "0") {
                mBinding.ivCreditType.background = getDrawable(R.mipmap.mine_credit_xyqy_enjoy)
                mBinding.tvScore.text =
                    String.format(getString(R.string.credit_activit_score), item.faithUseValue)
                mBinding.tvScore.textColorResource = R.color.credit_activity_score
            }

            mBinding.root.onNoDoubleClick {
                if (!item.jumpUrl.isNullOrEmpty()) {
                    ARouter.getInstance()
                        .build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString("mTitle", item.jumpName)
                        .withString("html", item.jumpUrl)
                        .navigation()
                } else {
                    when (item.type) {
                        // 志愿活动
                        ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                            ARouter.getInstance()
                                .build(MainARouterPath.MAIN_VOLUNTEER_ACTIVITY)
//                                    .build( MainARouterPath.MAIN_SEAT_SELECT_ACTIVITY)
                                .withString("id", item.id)
                                .withString("classifyId", item.classifyId)
                                .navigation()
                        }
                        // 预订活动
                        ActivityType.ACTIVITY_TYPE_RESERVE -> {
                            // 预订
                            when (item.method) {
                                // 付费预订活动
                                ActivityMethod.ACTIVITY_MODE_INTEGRAL_PAY -> {

                                    ARouter.getInstance()
                                        .build(MainARouterPath.MAIN_ACTIVITY_PAY_ORDER)
                                        .withString("jumpUrl", item.jumpUrl)
                                        .navigation()
                                }
                                else -> {
                                    ARouter.getInstance()
                                        .build(MainARouterPath.MAIN_HOT_ACITITY)
                                        .withString("id", item.id)
                                        .withString("classifyId", item.classifyId)
                                        .withString("region", item.region)
                                        .navigation()
                                }
                            }
                        }
                        else -> {
                            ARouter.getInstance()
                                .build(MainARouterPath.MAIN_HOT_ACITITY)
                                .withString("id", item.id)
                                .withString("classifyId", item.classifyId)
                                .navigation()
                        }

                    }
                }
            }
        }

    }

    // 活动室预约
    var activityRoomAdapter = object :
        RecyclerViewAdapter<CreditActivityRoomItemBinding, ActivityRoomBean>(R.layout.credit_activity_room_item) {
        override fun setVariable(
            mBinding: CreditActivityRoomItemBinding,
            position: Int,
            item: ActivityRoomBean
        ) {
            Glide.with(this@MineCreditActivity).load(item.images.getRealImages())
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.miImage)
            mBinding.tvContent.text = item.venueName + if (item.name.isNullOrEmpty()) {
                ""
            } else {
                "-" + item.name
            }
            if (!item.faithAuditStatus.isNullOrEmpty() && item.faithAuditStatus != "0") {
                mBinding.ivCreditType.background = getDrawable(R.mipmap.mine_credit_xyqy_exempt)
                mBinding.tvScore.text =
                    String.format(getString(R.string.credit_activit_score), item.faithAuditValue)
                mBinding.tvScore.textColorResource = R.color.color_title_line
            }
            if (!item.faithUseStatus.isNullOrEmpty() && item.faithUseStatus != "0") {
                mBinding.ivCreditType.background = getDrawable(R.mipmap.mine_credit_xyqy_enjoy)
                mBinding.tvScore.text =
                    String.format(getString(R.string.credit_activit_score), item.faithUseValue)
                mBinding.tvScore.textColorResource = R.color.credit_activity_score
            }
            mBinding.root.onNoDoubleClick {
                // 活动室
                ARouter.getInstance().build(ARouterPath.VenuesModule.ACTIVITY_ROOM_DETAIL)
                    .withString("id", item.id.toString())
                    .navigation()
            }
        }


    }
    var creditWds = mutableListOf<CreditWdBean>()

    override fun setTitle(): String = getString(R.string.credit_title)
    override fun initView() {
        mBinding.rvCreditWd.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvCreditWd.adapter = creditWdAdapter
        mModel.creditLevels.observe(this, Observer {
            if (it.size > 0) {
                var scales = IntArray(it.size)
                var scaleTxts = arrayOfNulls<String>(it.size)
                for (i in 0 until it.size) {
                    scales[i] = it[i].maxScore.toInt()
                    scaleTxts[i] = it[i].name
                }
                mBinding.creditCircleProgress.setScales(scales, scaleTxts)
                val date = getCurrentDate("yyyy/MM/dd")
                mBinding.creditCircleProgress.setContentTxt("更新时间：${date}")
            }
        })
        mBinding.tvNowMonthCountTxt.text =
            String.format(getString(R.string.credit_month_count), getCurrentMonth())
        mBinding.tvNowMonthCount.text = "0"
        mBinding.tvTotalCount.text = "0"
        mModel.currentCreditBean.observe(this, Observer {
            mBinding.tvNowMonthCount.text = it.monthRecordNum
            mBinding.tvTotalCount.text = it.totalRecordNum
            mBinding.creditCircleProgress.setCurrentNumAnim(it.creditScore.toInt())

        })
        mModel.creditBean.observe(this, Observer {
            if (it != null) {
                creditWds.add(
                    CreditWdBean(
                        "syls", R.mipmap.mine_credit_xywd_history,
                        getString(R.string.credit_wd_ls_title),
                        getString(R.string.credit_wd_ls_content),
                        String.format(
                            getString(R.string.credit_wd_ls_left_txt),
                            it.keepPromiseCount
                        ),
                        String.format(
                            getString(R.string.credit_wd_ls_right_txt),
                            it.losePromiseCount
                        )
                    )
                )
                creditWdAdapter.add(creditWds)
            }
        })

        mBinding.rvActivity.layoutManager = GridLayoutManager(this@MineCreditActivity, 2)
        mBinding.rvActivity.adapter = activityAdapter
        mModel.activities.observe(this, Observer {
            if (it.size > 0) {
                activityAdapter.add(it)
            } else {
                mBinding.clActivity.visibility = View.GONE
            }
        })
        mBinding.rvActivityRoom.layoutManager = GridLayoutManager(this@MineCreditActivity, 2)
        mBinding.rvActivityRoom.adapter = activityRoomAdapter

        mModel.activityRooms.observe(this, Observer {
            if (it.size > 0) {
                activityRoomAdapter.add(it)
            } else {
                mBinding.clActivityRoom.visibility = View.GONE
            }
        })

        mModel.idStatus.observe(this, Observer {
            creditWds.add(
                CreditWdBean(
                    "sftz", R.mipmap.mine_credit_xywd_identity,
                    getString(R.string.credit_wd_sf_title),
                    getString(R.string.credit_wd_sf_content),
                    getString(R.string.credit_wd_sf_left_txt),
                    "",
                    it == "6"
                )
            )
            creditWdAdapter.notifyDataSetChanged()
        })

        // 更多活动
        mBinding.tvActivityMore.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_ACTIVITY_LS)
                .navigation()
        }

        //更多活动室
        mBinding.tvRoomMore.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.VenuesModule.VENUES_LIST_ACTIVITY).navigation()
        }
        mBinding.tvKnowCreditScore.onNoDoubleClick {
            gotoRecord()
        }
        mBinding.llNowCount.onNoDoubleClick {
            gotoRecord()

        }
        mBinding.llTotal.onNoDoubleClick {
            gotoRecord()
        }
    }

    override fun initData() {
        creditWds.add(
            CreditWdBean(
                "yyxw", R.mipmap.mine_credit_xywd_reservation,
                getString(R.string.credit_wd_yy_title),
                getString(R.string.credit_wd_yy_content),
                getString(R.string.credit_wd_yy_left_txt),
                getString(R.string.credit_wd_yy_right_txt)
            )
        )
        mModel.getCreditLevel()
        mModel.getActivityList()
        mModel.getActivityRoom()
        mModel.refresh()

    }

    override fun onResume() {
        super.onResume()
        mModel.getIdStatus()

    }

    /**
     * 获取当前月份
     */
    private fun getCurrentMonth(): Int {
        var calendar = Calendar.getInstance()
        return calendar.get(Calendar.MONTH) + 1
    }

    private fun getCurrentDate(format: String): String {
        val time = Calendar.getInstance().timeInMillis
        var date = SimpleDateFormat(format).format(Date(time))
        return date
    }

    /**
     * 跳转到信用记录界面
     */
    private fun gotoRecord(date: String = "", type: String = "KEEP_PROMISE") {
        if (!mModel.creditLevels.value.isNullOrEmpty()) {
            ARouter.getInstance().build(ARouterPath.UserModule.USER_CREDIT_RECORD_ACTIVITY)
                .withString("date", date)
                .withString("type", type)
                .withString("creditScore", mModel.creditBean.value?.creditScore)
                .withParcelableArrayList("levels", ArrayList(mModel.creditLevels.value))
                .navigation()
        }

    }
}
