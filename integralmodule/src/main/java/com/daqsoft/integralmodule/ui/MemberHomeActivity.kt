package com.daqsoft.integralmodule.ui

import android.view.Gravity
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.integralmodule.R
import com.daqsoft.integralmodule.common.TaskTypeCode
import com.daqsoft.integralmodule.databinding.IntegralmoduleActivityMemberHomeBinding
import com.daqsoft.integralmodule.databinding.IntegralmoduleItemShopBinding
import com.daqsoft.integralmodule.databinding.IntegralmoduleItemTaskBinding
import com.daqsoft.integralmodule.repository.bean.TaskListBean
import com.daqsoft.integralmodule.ui.view.CompleteTaskDialog
import com.daqsoft.integralmodule.ui.vm.MemberHomeActivityVm
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.ChangeTabEvent
import com.daqsoft.provider.bean.ProductListBean
import kotlinx.android.synthetic.main.integralmodule_activity_member_home.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast
import java.util.*

/**
 * 积分会员商城首页
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-21
 * @since JDK 1.8.0_191
 * todo  任务提醒暂时未做
 */
@Route(path = ARouterPath.IntegralModule.MEMBER_HOME_ACTIVITY)
class MemberHomeActivity :
    TitleBarActivity<IntegralmoduleActivityMemberHomeBinding, MemberHomeActivityVm>(),
    View.OnClickListener {
    /**
     * 积分兑换查看更多
     */
    var shoppingUrl = ""

    override fun getLayout(): Int = R.layout.integralmodule_activity_member_home

    override fun setTitle(): String = getString(R.string.integralmodule_member)

    override fun injectVm(): Class<MemberHomeActivityVm> = MemberHomeActivityVm::class.java

    override fun initView() {
        mBinding.view = this
        mBinding.vm = mModel
        initViewModel()
        setAdapter()
    }

    private fun initViewModel() {
        // 领取任务
        mModel.getTaskLiveData.observe(this, Observer {
            if (it != null) {
                if (it.status) {
                    // 重新请求任务列表
                    mModel.getApiTaskList()
                    ToastUtils.showMessage(it.message)
                } else {
                    dissMissLoadingDialog()
                    var msg = "领取任务失败~，请稍后再试"
                    if (!it.message.isNullOrEmpty()) {
                        msg = it.message!!
                    }
                    ToastUtils.showMessage(msg)
                }
            } else {
                dissMissLoadingDialog()
            }
        })
        // 完成任务
        mModel.completeLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            if (it.status) {
                if (it.position < taskAdapter.getData().size) {
                    var item = taskAdapter.getData()[it.position]
                    dialog?.updateContent(item.name, item.synopsis, item.rewardIntegral.toString())
                    //  0 不能再领取 1 可以再领取 2 去完成
                    // 完成状态 0 用户未领取 1 待完成 2 已完成未领取 3 已过期 4 已完成且用户领取
                    var finishStatus = 0
                    if (it.key == 0) {
                        finishStatus = 4
                    } else if (it.key == 1) {
                        finishStatus = 0
                    } else if (it.key == 2) {
                        finishStatus = 1
                    }
                    // 重新获取我的积分
                    mModel.pointCount()
                    taskAdapter.getData()[it.position].finishStatus = finishStatus
                    taskAdapter.notifyItemChanged(it.position, "updateStatus")
                }
                dialog?.show()
            } else {
                // 重新刷新数据
                ToastUtils
                    .showMessage("" + it.message)
            }
        })
    }

    /**
     * 任务列表适配器
     */
    private val taskAdapter by lazy {
        object :
            RecyclerViewAdapter<IntegralmoduleItemTaskBinding, TaskListBean>(R.layout.integralmodule_item_task) {
            override fun setVariable(
                mBinding: IntegralmoduleItemTaskBinding,
                position: Int,
                item: TaskListBean
            ) {
                mBinding.item = item
                mBinding.vm = mModel
                if (item.icon.isNullOrEmpty())
                    mBinding.ivTypeIcon.setImageResource(
                        TaskTypeCode.TaskTypeCodeToImg(
                            item.taskTypeCode!!
                        )
                    ) else {
                    Glide.with(this@MemberHomeActivity)
                        .load(item.icon)
                        .into(mBinding.ivTypeIcon)
                }
                if (item.taskLastStatus == 2 && !item.startTime.isNullOrEmpty() && !item.endTime.isNullOrEmpty()) {
                    // 显示进度条
                    try {
                        mBinding.progressTaskSj.visibility = View.VISIBLE
                        mBinding.tvTaskSj.visibility = View.VISIBLE
                        var startDate = DateUtil.formatDate("yyyy-MM-dd HH:mm:ss", item.startTime)
                        var endDate = DateUtil.formatDate("yyyy-MM-dd HH:mm:ss", item.endTime)
                        mBinding.progressTaskSj.max = DateUtil.getGapCount(startDate, endDate)
                        mBinding.progressTaskSj.progress = DateUtil.getGapCount(startDate, Date())
                        mBinding.tvTaskSj.text = DateUtil.getDateDayString("yyyy-mm-dd", startDate) + "至" + DateUtil.getDateDayString("yyyy-mm-dd", endDate)
                    } catch (e: Exception) {
                    }

                } else {
                    // 隐藏进度调
                    mBinding.progressTaskSj.visibility = View.GONE
                    mBinding.tvTaskSj.visibility = View.GONE
                }
                /**
                 *
                 * 设置任务状态背景
                 */
                setTvStyle(item, mBinding)
                mBinding.statustr = setStatus(item.finishStatus!!)
                mBinding.root.setOnClickListener {
                    // 1 手动领取
                    if (item.autoDraw == 0 && item.finishStatus == 2) {
                        // 领取积分
                        showLoadingDialog()
                        mModel.completeTask(item.taskId.toString(), position)
                    } else if (item.autoDraw == 0 && item.finishStatus == 0) {
                        // 领取任务
                        showLoadingDialog()
                        mModel.getTask(item.taskId.toString(), position)
                    } else if (item.finishStatus == 0 || item.finishStatus == 1) {
                        if (item.taskTypeCode.equals(TaskTypeCode.TASK_TYPE_SIGN)) {
                            mModel.getCheckIn()
                        } else {
                            if (!item.jumpImmatureUrl.isNullOrEmpty()) {
                                // 跳转网页
                                ARouter.getInstance()
                                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                                    .withString("html", item.jumpImmatureUrl)
                                    .navigation()
                            } else {
                                // 跳转对应页面取完成任务
                                var path = ""
                                when (item.taskTypeCode) {
                                    TaskTypeCode.TASK_TYPE_STORY_STORY, TaskTypeCode.TASK_TYPE_STORY_COVER -> {
                                        // 写故事,上封面
                                        path = MainARouterPath.MAIN_STORY_STRATEGY_ADD
                                    }
                                    TaskTypeCode.TASK_TYPE_STORY_TOPIC -> {
                                        // 话题
                                        path = MainARouterPath.MAIN_TOPIC_LIST
                                    }
                                    TaskTypeCode.TASK_TYPE_STORY_INTERACT, TaskTypeCode.TASK_TYPE_THUMB -> {
                                        //点赞,话题互动
                                        path = MainARouterPath.MAIN_TIME

                                    }
                                    TaskTypeCode.TASK_TYPE_COMMENT -> {
                                        // 点评 ->回首页
                                        EventBus.getDefault().post(ChangeTabEvent("INDEX"))
                                        finish()
//                                        path = MainARouterPath.MAINE_SCENIC_LIST
                                    }
                                    TaskTypeCode.TASK_TYPE_EXTERNAL_TASK -> {
                                        // 外部任务
                                        if (item.externalUrl.isNullOrEmpty()) {
                                            ToastUtils.showMessage("非常抱歉，管理员为配置，外部链接~")
                                        } else {
                                            ARouter.getInstance()
                                                .build(ARouterPath.Provider.WEB_ACTIVITY)
                                                .withString("html", item.externalUrl)
                                                .navigation()
                                        }
                                    }
                                    TaskTypeCode.TASK_TYPE_ACTIVITY_ROOM -> {
                                        // 活动室 ->场馆列表
                                        ARouter.getInstance().build(ARouterPath.VenuesModule.VENUES_LIST_ACTIVITY)
                                            .navigation()
                                    }
                                    TaskTypeCode.TASK_TYPE_ACTIVITY -> {
                                        // 活动->首页 活动频道
                                        EventBus.getDefault().post(ChangeTabEvent("ACTIVITY"))
                                        finish()
                                    }
                                    TaskTypeCode.TASK_TYPE_FOLLOW_PUBLIC_NUMBER -> {
                                        // 关注公众号
                                        toast("暂未开放")
                                    }
                                    else -> { // 其他功能
                                        toast("暂未开放")
                                    }

                                }
                                if (!path.isNullOrEmpty()) {
                                    ARouter.getInstance()
                                        .build(path)
                                        .navigation()
                                    finish()
                                }

                            }
                        }
                    }

                }
            }

            private fun setTvStyle(item: TaskListBean, mBinding: IntegralmoduleItemTaskBinding) {
                when (item.finishStatus) {
                    0 -> {
                        mBinding.mGetTv.setBackgroundResource(R.drawable.shape_ecc783_large)
                        mBinding.mGetTv.setTextColor(resources.getColor(R.color.c_654427))
                    }
                    1 -> {
                        mBinding.mGetTv.setBackgroundResource(R.drawable.shape_ecc783_large)
                        mBinding.mGetTv.setTextColor(resources.getColor(R.color.c_654427))
                    }
                    2 -> {
                        mBinding.mGetTv.setBackgroundResource(R.drawable.shape_504747_large)
                        mBinding.mGetTv.setTextColor(resources.getColor(R.color.ffe3af))
                    }
                    3 -> {
                        mBinding.mGetTv.setBackgroundResource(R.drawable.shape_cbcbcb_large)
                        mBinding.mGetTv.setTextColor(resources.getColor(R.color.white))
                    }
                    4 -> {
                        mBinding.mGetTv.setBackgroundResource(R.drawable.shape_cbcbcb_large)
                        mBinding.mGetTv.setTextColor(resources.getColor(R.color.white))
                    }
                }
            }

            override fun payloadUpdateUi(mBinding: IntegralmoduleItemTaskBinding, position: Int, payloads: MutableList<Any>) {
                if (payloads[0] == "updateStatus") {
                    setTvStyle(getData()[position], mBinding)
                    mBinding.mGetTv.text = setStatus(getData()[position]!!.finishStatus!!)
                }
            }

            /**
             * 设置任务状态
             */
            fun setStatus(finishStatus: Int): String {
                return when (finishStatus) {
                    0 -> return BaseApplication.context.getString(R.string.integralmodule_unaccalimed)
                    1 -> return BaseApplication.context.getString(R.string.integralmodule_pending)
                    2 -> return BaseApplication.context.getString(R.string.integralmodule_completed_not_reveived)
                    3 -> return BaseApplication.context.getString(R.string.integralmodule_expired)
                    4 -> return BaseApplication.context.getString(R.string.integralmodule_completed_received)
                    else -> return ""
                }
            }
        }
    }


    private val dialog: CompleteTaskDialog by lazy {
        val dialog1 = CompleteTaskDialog(this)
        dialog1.onClickCompleteListener = object : CompleteTaskDialog.OnClickCompleteListener {
            override fun onClickIntegralDetail() {
                // 积分明细
                ARouter.getInstance()
                    .build(ARouterPath.IntegralModule.INTEGRAL_DETAIL_ACTIVITY)
                    .navigation()
            }

        }
        dialog1
    }
    /**
     * 商品列表适配器
     */
    private val productAdapter by lazy {
        object :
            RecyclerViewAdapter<IntegralmoduleItemShopBinding, ProductListBean.Product>(
                R.layout.integralmodule_item_shop
            ) {
            override fun setVariable(
                mBinding: IntegralmoduleItemShopBinding,
                position: Int,
                item: ProductListBean.Product
            ) {
                mBinding.item = item
                var encry = SPUtils.getInstance().getString(SPKey.ENCRYPTION)
                mBinding.root.setOnClickListener {
                    ARouter.getInstance()
                        .build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString("mTitle", item.productName)
                        .withString(
                            "html",
                            item.url + "&unid=${SPUtils.getInstance().getString(SPKey.UID)
                            }&token=${SPUtils.getInstance().getString(SPKey.USERCENTERTOKEN)}&encryption=${encry}"
                        )
                        .navigation()
                }
            }
        }
    }

    override fun initData() {
        mModel.pointCount()
        mModel.getApiTaskList()
        mModel.siteInfo()
        mModel.pointConfigInfo()
    }


    private fun setAdapter() {
        mTaskRv.apply {
            layoutManager = LinearLayoutManager(this@MemberHomeActivity) as RecyclerView.LayoutManager?
            adapter = taskAdapter
        }
        mShopRv.apply {
            layoutManager = GridLayoutManager(this@MemberHomeActivity, 2)
            adapter = productAdapter

        }
    }

    override fun notifyData() {
        super.notifyData()
        mModel.taskListData.observe(this, Observer {
            dissMissLoadingDialog()
            if (it.isNotEmpty()) {
                taskAdapter.clear()
                taskAdapter.add(it)
                mBinding.llTask.visibility = View.VISIBLE
            } else {
                mBinding.llTask.visibility = View.GONE
            }
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })

        mModel.data.observe(this, Observer {
            if (it.nextLevelPoint.isNullOrEmpty()) {
                mBinding.mLevelPb.max = 0
            } else {
                mBinding.mLevelPb.max = it.nextLevelPoint.toInt()
            }

            mBinding.mLevelPb.progress = it.currPoint ?: 0
        })

        mModel.productListData.observe(this, Observer {
            if(it!=null) {
                mBinding.clShopping.visibility = View.VISIBLE
                mBinding.mShopTv.visibility = View.VISIBLE
                shoppingUrl = it.mallUrl ?: ""
                productAdapter.clear()
                productAdapter.add(it.productList!!.toMutableList())
            }
        })
        mModel.checkInResult.observe(this, Observer {
            if (it.code == 0) {
                toast("签到成功").setGravity(Gravity.CENTER, 0, 0)
                mModel.getApiTaskList()
            } else {
                if (it?.message != null)
                    toast(it.message.toString()).setGravity(Gravity.CENTER, 0, 0)
            }
        })

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mDetailTv -> {
                // 积分明细
                ARouter.getInstance()
                    .build(ARouterPath.IntegralModule.INTEGRAL_DETAIL_ACTIVITY)
                    .navigation()
            }
            R.id.mRuleTv -> {
                // 积分规则
                if (mModel.rule.isNullOrEmpty()) {
                    toast(getString(R.string.integralmodule_no_rule))
                    return
                }
                ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", getString(R.string.integralmodule_rule))
                    .withString("html", mModel.rule ?: "")
                    .navigation()
            }
            R.id.mShopTv, R.id.mAllShopTv -> {
                // 小电商页面
                if (shoppingUrl.isNotEmpty()) {
                    var unid =SPUtils.getInstance().getString(SPKey.UUID)
                    ARouter.getInstance()
                        .build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString("mTitle", getString(R.string.integralmodule_integral_shop))
                        .withString(
                            "html",
                            StringUtil.getShopUrl(shoppingUrl,unid)
                        )
                        .navigation()
                }
            }
        }
    }
}