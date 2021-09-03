package com.daqsoft.usermodule.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FocusItemActivityBinding
import com.daqsoft.usermodule.databinding.ItemActivityTagBinding
import com.daqsoft.usermodule.databinding.MineFocusListBinding
import com.jakewharton.rxbinding2.view.RxView
import org.jetbrains.anko.textColor
import java.util.concurrent.TimeUnit

/**
 *@package:com.daqsoft.usermodule.ui.fragment
 *@date:2020/5/28 14:04
 *@author: caihj
 *@des:TODO
 **/
class FocusActivityFragment: BaseFragment<MineFocusListBinding,FocusActivityVm>(){
    override fun getLayout(): Int = R.layout.mine_focus_list

    override fun injectVm(): Class<FocusActivityVm> = FocusActivityVm::class.java

    override fun initView() {
       mBinding.recyclerFocus.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        mBinding.recyclerFocus. adapter = adapter
        mBinding.refreshFocus.setOnRefreshListener {
            mModel.currPage = 1
            mModel.getActivityList()
        }
        adapter.setOnLoadMoreListener {
            mModel.currPage ++
            mModel.getActivityList()
        }
        mModel.activities.observe(this, Observer {
            dissMissLoadingDialog()
            if(it!=null){
                pageDeal(it)
            }
        })
        mModel.cancelStatus.observe(this, Observer {
            if(it != -1){
                ToastUtils.showMessage("取消成功!")
                adapter.removeItem(it)
            }else{
                ToastUtils.showMessage("取消失败!")
            }
        })
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getActivityList()
    }



    private fun pageDeal(it: MutableList<ActivityBean>?) {
        mBinding.refreshFocus.isRefreshing = false
        if (mModel.currPage == 1) {
            mBinding.recyclerFocus.smoothScrollToPosition(0)
            adapter!!.clear()
        }
        if (!it.isNullOrEmpty()) {
            adapter!!.add(it!!)
        }
        if (it.isNullOrEmpty() || it.size < mModel.pageSize) {
            adapter?.loadEnd()
        } else {
            adapter?.loadComplete()
        }
    }


    var adapter  =object :RecyclerViewAdapter<FocusItemActivityBinding, ActivityBean>(
    R.layout.focus_item_activity
    ) {
        @SuppressLint("CheckResult")
        override fun setVariable(mBinding: FocusItemActivityBinding, position: Int, item: ActivityBean) {
            val images = item.images.split(",")
            var url = ""
            if (images.isNotEmpty()) {
                url = images[0]
            }
            mBinding.imageUrl = url
//        var options = RequestOptions().transform(CenterCrop(),RoundedCorners(12))
//        Glide.with(mContext).load(url)
//            .apply(options)
//            .placeholder(R.mipmap.placeholder_img_fail_240_180)
//            .into(mBinding.image)

            mBinding.name = item.name
            // 当活动结束时
            if (item.activityStatus == "2") {
                mBinding.tvIsOver.visibility = View.VISIBLE
                mBinding.tvPrice.visibility = View.GONE
                mBinding.tvMoneyUnit.visibility = View.GONE
                mBinding.tvIntegral.visibility = View.GONE
                mBinding.tvPrice.visibility = View.GONE
            } else {
                mBinding.tvIsOver.visibility = View.GONE
                mBinding.tvPrice.visibility = View.VISIBLE
                // 显示价格/积分/免费
                if (item.money.toDouble() == 0.0 && item.integral == "0") {
                    mBinding.price = resources.getString(R.string.order_free)
                    mBinding.tvMoneyUnit.visibility = View.GONE
                    mBinding.tvIntegral.visibility = View.GONE
                } else if (item.money.toDouble() > 0.0) {
                    mBinding.price = item.money
                    mBinding.tvMoneyUnit.visibility = View.VISIBLE
                    mBinding.tvIntegral.visibility = View.GONE
                } else {
                    mBinding.price = item.integral
                    mBinding.tvMoneyUnit.visibility = View.GONE
                    mBinding.tvIntegral.visibility = View.VISIBLE
                }
            }
            /**
             * 活动特色
             */
            if (!item.remark.isNullOrEmpty()) {
                mBinding.tvRemark.visibility = View.VISIBLE
                mBinding.tvRemark.text = item.remark
            } else {
                mBinding.tvRemark.visibility = View.GONE
            }

            val tags = mutableListOf<String>()
            if (item.faithAuditStatus == "1") {
                tags.add("诚信免审")
            }

            if (item.faithUseStatus == "1") {
                tags.add("诚信优享")
            }

            if (item.tagNames.isNotEmpty() && item.tagNames != null) {
                val tag = item.tagNames.split(",")
                for (el in tag) {
                    tags.add(el)
                }
            }

            var startTime = DateUtil.formatDateByString("MM.dd", "yyyy-MM-dd", item.useStartTime)
            var endTime = DateUtil.formatDateByString("MM.dd", "yyyy-MM-dd", item.useEndTime)
            var time = resources.getString(R.string.order_activity_room_time_stamp_, startTime, endTime)
            when (item.type) {
                ActivityType.ACTIVITY_TYPE_PLAIN -> {
                    mBinding.tvPrice.visibility = View.GONE
                    mBinding.tvIntegral.visibility = View.GONE

                    mBinding.totalTime = time
                }
                ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                    mBinding.tvPrice.visibility = View.INVISIBLE
                    mBinding.tvIntegral.visibility = View.GONE
                    var total =
                        resources.getString(R.string.home_activity_total_number, item.recruitedCount, item.totalStock)
                    mBinding.totalTime = DividerTextUtils.convertString(StringBuilder(), total, time)
                    var left = resources.getString(R.string.home_activity_rest_number, item.stock.toString())
                    mBinding.left = left
                    tags.add(0, left)
                }
                else -> {
                    mBinding.tvPrice.visibility = if (mBinding.tvPrice.isVisible) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    mBinding.totalTime = time
                }
            }
            tags.add(getActivityStauts(item.activityStatus, item.type))
            if (!tags.isNullOrEmpty()) {
                val tagLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                /**
                 * 标签适配器包括 剩余名额，是否开启诚信面审 诚信优享，tags
                 */
                val tagAdapter =
                    object : RecyclerViewAdapter<ItemActivityTagBinding, String>(R.layout.item_activity_tag) {
                        @SuppressLint("CheckResult")
                        override fun setVariable(mBinding: ItemActivityTagBinding, position: Int, item: String) {

                            if (item == "诚信免审" || item == "诚信优享") {
                                //诚信
                                mBinding.tvVolunteer.background =
                                    resources.getDrawable(R.drawable.home_b_36cd64_stroke_null_round_2)
                                mBinding.tvVolunteer.textColor = resources.getColor(R.color.white)

                                if (item == "诚信优享") {
                                    var d = resources.getDrawable(R.mipmap.activity_enjoy)
                                    mBinding.tvVolunteer.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null)
                                } else {
                                    var d = resources.getDrawable(R.mipmap.activity_exempt)
                                    mBinding.tvVolunteer.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null)
                                }
                            } else if (item.startsWith("还剩")) {
                                // 剩余
                                mBinding.name = item
                                mBinding.tvVolunteer.background =
                                    resources.getDrawable(R.drawable.home_b_ff9e05_stroke_null_round_2)
                                mBinding.tvVolunteer.textColor = resources.getColor(R.color.white)
                            } else {
                                // tag
                                mBinding.name = item
                                mBinding.tvVolunteer.background =
                                    resources.getDrawable(R.drawable.shape_label_primary_color_bg_2)
                                mBinding.tvVolunteer.textColor = resources.getColor(R.color.colorPrimary)
                            }
                        }
                    }
                tagAdapter.add(tags)
                mBinding.rvTags.layoutManager = tagLayoutManager
                mBinding.rvTags.adapter = tagAdapter
                mBinding.rvTags.visibility = View.VISIBLE
            } else {
                mBinding.rvTags.visibility = View.GONE
            }


            var address: String? = ""
            if (!item.cityRegionNames.isNullOrEmpty()) {
                address = item.cityRegionNames + "·"
            }
            if (!item.address.isNullOrEmpty()) {
                address += item.address + " "
            }
            if (!item.resourceNameStr.isNullOrEmpty()) {
                address += item.resourceNameStr
            }

            mBinding.tvCancelFocus.onNoDoubleClick {
                mModel.attentionResourceCancel(position,item.id)
            }
            if (address.isNullOrEmpty()) {
                mBinding.tvAddress.visibility = View.GONE
            } else {
                mBinding.address = address
                mBinding.tvAddress.visibility = View.VISIBLE
            }
            RxView.clicks(mBinding.root)
                // 1秒内不可重复点击或仅响应一次事件
                .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                    run {
                        // 如果有跳转链接 直接跳转webactivity
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

        private fun getActivityStauts(activityStatus: String, type: String): String {
            // 活动状态 0(未开始) 1(进行中) 2(已结束) 3 (报名中/招募中)
            return when (activityStatus) {
                "0" -> {
                    "未开始"
                }
                "1" -> {
                    "进行中"
                }
                "2" -> {
                    "已结束"
                }
                "3" -> {
                    if (type == ActivityType.ACTIVITY_TYPE_VOLUNT) {
                        "招募中"
                    } else {
                        "报名中"
                    }
                }
                else -> {
                    ""
                }
            }
        }


        override fun getItemId(position: Int): Long {
            return position.toLong()
        }


    }
}

class FocusActivityVm:BaseViewModel(){

    var activities = MutableLiveData<MutableList<ActivityBean>>()

    var currPage = 1

    val pageSize = 20
    /**
     * 获取活动列表
     */
    fun getActivityList() {

        val param = HashMap<String, String>()

        param["currPage"] = currPage.toString()
        param["pageSize"] = pageSize.toString()
        param["queryType"] = "1"

        UserRepository.userService.getActivityList(param)
            .excute(object : BaseObserver<ActivityBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ActivityBean>) {
                    activities.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<ActivityBean>) {
                    mError.postValue(response)
                }

            })
    }
    val cancelStatus = MutableLiveData<Int>()
    // 取消关注资源
    fun attentionResourceCancel(position:Int,resourceId: String, resourceType: String = ResourceType.CONTENT_TYPE_ACTIVITY) {
        CommentRepository.service.attentionResourceCancle(resourceId, resourceType).excute(object : BaseObserver<Any>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Any>) {
                cancelStatus.postValue(position)
            }

            override fun onFailed(response: BaseResponse<Any>) {
                cancelStatus.postValue(-1)
            }
        })
    }
}