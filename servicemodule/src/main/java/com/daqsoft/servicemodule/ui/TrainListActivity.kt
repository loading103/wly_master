package com.daqsoft.servicemodule.ui

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityTrainListBinding
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.servicemodule.adapter.TrainListAdapter
import com.daqsoft.servicemodule.model.TrainListViewModel
import com.daqsoft.servicemodule.uitils.TimeSwitch
import com.daqsoft.servicemodule.uitils.TimeSwitch.MD
import com.daqsoft.servicemodule.uitils.TimeSwitch.dateYMD
import com.daqsoft.servicemodule.uitils.TimeSwitch.getBeforeWeek
import com.daqsoft.servicemodule.uitils.TimeSwitch.getNextChooseDate
import com.daqsoft.servicemodule.uitils.TimeSwitch.getNextWeek
import com.daqsoft.servicemodule.uitils.TimeSwitch.getPastChooseDate
import com.daqsoft.servicemodule.uitils.TimeSwitch.getTimeDiff
import java.util.*
import kotlin.math.abs

/**
 * desc :火车列表查询
 * @author 江云仙
 * @date 2020/4/8 19:02
 */
@Route(path = ARouterPath.ServiceModule.SERVICE_TRAIN_LIST_ACTIVITY)
class TrainListActivity : TitleBarActivity<ActivityTrainListBinding, TrainListViewModel>() {
    @JvmField
    @Autowired
    var start = ""//开始城市简码
    @JvmField
    @Autowired
    var end = ""//结束城市简码
    @JvmField
    @Autowired
    var startCityName = ""//开始时间
    @JvmField
    @Autowired
    var endCityName = ""//结束时间
    @JvmField
    @Autowired
    var time = ""//选择时间
    //当前日期的下表位置
    private var currentDayIndex = 0

    override fun getLayout(): Int {
        return R.layout.activity_train_list
    }

    override fun setTitle(): String {
        return "$startCityName-$endCityName"
    }

    override fun injectVm(): Class<TrainListViewModel> = TrainListViewModel::class.java

    override fun initView() {
        mBinding.recyTrainTime.visibility = View.GONE
        setAdapter()
        val distanceStr = TimeSwitch.getMonthToday(time)
        mBinding.txtChooseTime.text = distanceStr
        currentDayIndex= getTimeDiff(time)?.toInt()?:0
        setClick()
    }

    private fun setClick() {
        //选择时间
        mBinding.txtChooseTime.onNoDoubleClick {
            UIHelperUtils.showOptionsPicker(this, timePickerView)
        }

        //前一天
        mBinding.txtBeforeDay.onNoDoubleClick {
            currentDayIndex -= 1
            setChooseDate()

        }
        //后一天
        mBinding.txtNextDay.onNoDoubleClick {
            currentDayIndex += 1
            setChooseDate()
        }
    }

    /**
     *设置前一天后一天点击数据
     */
    private fun setChooseDate() {
        val chooseDate = if (currentDayIndex > 0) {
            getNextChooseDate(Utils.getDateTime(dateYMD, Date()),currentDayIndex,dateYMD)
        } else {
            getPastChooseDate(abs(currentDayIndex),dateYMD)
        }
        val txtChooseDate = if (currentDayIndex > 0) {
            getNextChooseDate(Utils.getDateTime(dateYMD, Date()),currentDayIndex,MD)+getNextWeek(Utils.getDateTime(dateYMD, Date()),"周",currentDayIndex)
        } else {
            getPastChooseDate(abs(currentDayIndex),MD)+getBeforeWeek("周",abs(currentDayIndex))
        }
        mBinding.txtChooseTime.text =txtChooseDate
        mModel.time = chooseDate
        trainListAdapter.clear()
        mBinding.recyTrainTime.visibility = View.GONE
        mModel.getStationName()
    }

    /**
     * 时间选择器
     */
    private val timePickerView by lazy {
        TimePickerBuilder(this, OnTimeSelectListener { date, v ->
            // 选中事件回调
            val incidentTime = Utils.getDateTime(Utils.MD, date)
            time = Utils.getDateTime(Utils.dateYMD, date)
            val content = incidentTime + TimeSwitch.getWeekDay("周", date)
            mBinding.txtChooseTime.text = content
            currentDayIndex= getTimeDiff(time)?.toInt()?:0
//            getTimeDiff(time)?:""
            mModel.time = time
            mBinding.recyTrainTime.visibility = View.GONE
            trainListAdapter.clear()
            mModel.getStationName()
        })
            .build()
    }

    /**
     *设置适配器
     */
    private val trainListAdapter = TrainListAdapter()
    private fun setAdapter() {
        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.recyTrainTime.layoutManager = tagLayoutManager
        mBinding.recyTrainTime.adapter = trainListAdapter
        mModel.result.observe(this, Observer {
            mBinding.recyTrainTime.visibility = View.VISIBLE
            val data = it
//            trainListAdapter.clear()
            trainListAdapter.add(data)
            trainListAdapter.notifyDataSetChanged()
        })
    }

    override fun initData() {
        mModel.end = end
        mModel.start = start
        mModel.time = time
        mModel.getStationName()
    }
}
