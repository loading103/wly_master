package com.daqsoft.travelCultureModule.hotActivity

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityHotActivityOverViewBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ActivityOverView
import com.daqsoft.travelCultureModule.hotActivity.adapter.ActivityOverViewTypesV1Adapter
import com.daqsoft.travelCultureModule.hotActivity.adapter.ActivityOverViwMonthAdapter
import com.daqsoft.travelCultureModule.hotActivity.model.HotActivityModel
import com.daqsoft.travelCultureModule.hotActivity.model.HotActivityOverViewModel
import java.util.*

/**
 * @Description
 * @ClassName   HotActivityOverViewActivity
 * @Author      luoyi
 * @Time        2020/6/10 10:06
 */
@Route(path = MainARouterPath.MAIN_ACTIVITY_OVER_VIEW)
class HotActivityOverViewActivity : TitleBarActivity<ActivityHotActivityOverViewBinding, HotActivityOverViewModel>() {

    /**
     * 月份适配器
     */
    var overViewSelectMonthAdapter: ActivityOverViwMonthAdapter? = null
    /**
     * 活动分类
     */
    var activityOverViewTypesV1Adapter: ActivityOverViewTypesV1Adapter? = null
    var  timeDate : String=""

    override fun getLayout(): Int {
        return R.layout.activity_hot_activity_over_view
    }

    override fun setTitle(): String {
        return "活动概览"
    }

    override fun injectVm(): Class<HotActivityOverViewModel> {
        return HotActivityOverViewModel::class.java
    }

    override fun initView() {
        initOverViewMonth()
        initViewEvent()
        initViewModel()
    }

    private fun initViewModel() {
        mModel.activityOverViewLd.observe(this, androidx.lifecycle.Observer {
            dissMissLoadingDialog()
            setOverViewData(it)
        })
    }


    private fun initViewEvent() {
        overViewSelectMonthAdapter?.onOverViewMonthClickListener = object : ActivityOverViwMonthAdapter.OnOverViewMonthClickListener {
            override fun onSelectPos(month: String, position: Int) {
                mBinding.rvActivitiesMonth.smoothScrollToPosition(position)
                var year = Calendar.getInstance().get(Calendar.YEAR)
                var monthstr: String = if (month.toInt() < 10) {
                    "0${month}"
                } else {
                    month
                }
                var param = "${year}-${monthstr}"
                timeDate=param
                activityOverViewTypesV1Adapter?.times=param
                showLoadingDialog()
                mModel.getActivitiesOverView(param, param)
            }

        }
    }

    private fun initOverViewMonth() {
        overViewSelectMonthAdapter = ActivityOverViwMonthAdapter()
        overViewSelectMonthAdapter?.emptyViewShow = false
        mBinding.rvActivitiesMonth.adapter = overViewSelectMonthAdapter
        mBinding.rvActivitiesMonth.layoutManager = LinearLayoutManager(
            this@HotActivityOverViewActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        overViewSelectMonthAdapter?.clear()
        var currMonth: Int = Calendar.getInstance().get(Calendar.MONTH)
        overViewSelectMonthAdapter?.selectPos = currMonth
        overViewSelectMonthAdapter?.add(HotActivityModel.enMonths)
        mBinding.rvActivitiesMonth.smoothScrollToPosition(currMonth)
    }

    override fun initData() {
        showLoadingDialog()
        // 获取活动概览数据
        var date1 = DateUtil.getDqDateString("yyyy-MM", Date())
        timeDate=date1
        activityOverViewTypesV1Adapter?.times=date1
        mModel.getActivitiesOverView(date1, date1)
    }

    private fun setOverViewData(it: ActivityOverView) {
        if (it != null) {
            mBinding.llvOverViewInfo.visibility = View.VISIBLE
            mBinding.tvTotalOverViewNum.visibility = View.VISIBLE
            mBinding.tvTotalOverViewNum.text = "全部文旅活动${it.total}场"
            mBinding.tvTipPreActValue.text = "${it.notStartCount}"
            mBinding.tvTipProgressActValue.text = "${it.startCount}"
            mBinding.tvTipEndActValue.text = "${it.endCount}"
            if (!it.result.isNullOrEmpty()) {
                mBinding.rvActivityOverviews.visibility = View.VISIBLE
                activityOverViewTypesV1Adapter = ActivityOverViewTypesV1Adapter()
                activityOverViewTypesV1Adapter?.times= timeDate
                activityOverViewTypesV1Adapter?.emptyViewShow = false
                mBinding.rvActivityOverviews.layoutManager = GridLayoutManager(this@HotActivityOverViewActivity, 3, GridLayoutManager.VERTICAL, false)
                mBinding.rvActivityOverviews.adapter = activityOverViewTypesV1Adapter
                activityOverViewTypesV1Adapter?.clear()
                activityOverViewTypesV1Adapter?.add(it.result)
            } else {
                mBinding.rvActivityOverviews.visibility = View.GONE
            }
        } else {
            mBinding.tvTotalOverViewNum.visibility = View.GONE
            mBinding.llvOverViewInfo.visibility = View.GONE
            ToastUtils.showMessage("非常抱歉，未获取到相关数据~")
        }
    }
}