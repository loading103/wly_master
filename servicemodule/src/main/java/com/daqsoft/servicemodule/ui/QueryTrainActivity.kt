package com.daqsoft.servicemodule.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityQueryTrainBinding
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.servicemodule.*
import com.daqsoft.servicemodule.uitils.TextFontUtil
import com.daqsoft.servicemodule.uitils.TimeSwitch
import com.daqsoft.servicemodule.uitils.TimeSwitch.getWeekDay
import com.scwang.smart.refresh.layout.util.SmartUtil
import org.jetbrains.anko.toast


/**
 * desc :火车票查询
 * @author 江云仙
 * @date 2020/4/8 20:15
 */
@SuppressLint("SetTextI18n")
@Route(path = ARouterPath.ServiceModule.SERVICE_QUERY_TRAIN_ACTIVITY)
class QueryTrainActivity : TitleBarActivity<ActivityQueryTrainBinding, BaseViewModel>(),
    TextWatcher {
    /**
     * 发生时间
     */
    var incidentTime = ""
    var chooseTime = ""
    // 起点城市碼
    var startCityCode = ""
    // 起点城市
    var startCityName = ""
    // 终点城市碼
    var endCityCode = ""
    // 终点城市
    var endCityName = ""

    @JvmField
    @Autowired(name = "queryTraffic")
    var queryTraffic: String = ""

    override fun getLayout(): Int {
        return R.layout.activity_query_train
    }


    @SuppressLint("SetTextI18n")
    override fun initView() {
        setTrafficData()

        setCoverHeight()
        setExchangeImg()
        setClick()
    }

    /**
     *根据交通类型初始化数据据
     */
    private fun setTrafficData() {
        when (queryTraffic) {
            SERVICE_TRAIN -> {//火车列表
                mBinding.tvTourComplaint.text = setAttention("火车票")
                mBinding.tvTodayTime.visibility = View.VISIBLE
                mBinding.tvTitle.text = "火车票查询"
                mBinding.imgCover.setImageResource(R.mipmap.service_train_index_banner)
                //设置部分字体变色
                val distanceStr = TimeSwitch.getToday() + "（今天）"
                chooseTime = TimeSwitch.getChooseToday()
                val distanceColor = TextFontUtil.setTextSizeAndColor(
                    distanceStr,
                    SmartUtil.dp2px(12f),
                    Color.parseColor("#999999"),
                    distanceStr.length - 8,
                    distanceStr.length
                )
                mBinding.tvTodayTime.text = distanceColor
            }
            SERVICE_SUBWAY -> {//长途汽车列表
                mBinding.tvTourComplaint.text = setAttention("汽车票")
                mBinding.tvTodayTime.visibility = View.GONE
                mBinding.tvTitle.text = "汽车票查询"
                mBinding.imgCover.setImageResource(R.mipmap.service_car_index_banner)

            }
            SERVICE_PLANE -> {
                // 机票查询
                mBinding.tvTourComplaint.text = setAttention("机票")
                mBinding.tvTodayTime.visibility = View.VISIBLE
                mBinding.tvTitle.text = "机票查询"
                //设置部分字体变色
                val distanceStr = TimeSwitch.getToday() + "（今天）"
                chooseTime = TimeSwitch.getChooseToday()
                val distanceColor = TextFontUtil.setTextSizeAndColor(
                    distanceStr,
                    SmartUtil.dp2px(12f),
                    Color.parseColor("#999999"),
                    distanceStr.length - 8,
                    distanceStr.length
                )
                mBinding.tvTodayTime.text = distanceColor
                mBinding.imgCover.setImageResource(R.mipmap.service_airplane_index_banner)
            }
        }

    }

    /**
     *设置车票查询提示文字
     */
    private fun setAttention(attention: String): CharSequence? {
        return String.format(resources.getString(R.string.service_train_hint), attention)
    }


    /**
     *控件点击事件
     */
    private fun setClick() {
        //位置切换点击事件
        mBinding.imgExchange.setOnClickListener {
            val startPointStr = mBinding.editStartPoint.text.toString()
            val endPointStr = mBinding.editEndPoint.text.toString()
            //交换位置
            val temp = startCityCode
            startCityCode = endCityCode
            endCityCode = temp
            val tempCity = startCityName
            startCityName = endCityName
            endCityName = tempCity

            mBinding.editStartPoint.text = endPointStr
            mBinding.editEndPoint.text = startPointStr
        }
        //点击查询
        mBinding.tvQuery.onNoDoubleClick {
            val editStartPoint = mBinding.editStartPoint.text.toString().trim()
            val editEndPoint = mBinding.editEndPoint.text.toString().trim()
            if (editStartPoint.isNullOrEmpty()) {
                toast("请选择出发地").setGravity(Gravity.CENTER, 0, 0)
                return@onNoDoubleClick
            }
            if (editEndPoint.isNullOrEmpty()) {
                toast("请选择到达地").setGravity(Gravity.CENTER, 0, 0)
                return@onNoDoubleClick
            }
            setQueryJump()


        }
        //点击选择城市
        mBinding.editStartPoint.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.ServiceModule.SERVICE_CHOOSE_CITY_ACTIVITY)
                .withString("jumpType", "start")
                .withString("queryTraffic", queryTraffic)
                .navigation(this, 1)

        }
        mBinding.editEndPoint.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.ServiceModule.SERVICE_CHOOSE_CITY_ACTIVITY)
                .withString("jumpType", "end")
                .withString("queryTraffic", queryTraffic)
                .navigation(this, 1)
        }
        //选择时间
        mBinding.tvTodayTime.onNoDoubleClick {
            UIHelperUtils.showOptionsPicker(this, timePickerView)
        }
    }


    /**
     *根据不同交通类型跳转页面
     */
    private fun setQueryJump() {
        when (queryTraffic) {
            SERVICE_TRAIN -> {//火车列表
                ARouter.getInstance()
                    .build(ARouterPath.ServiceModule.SERVICE_TRAIN_LIST_ACTIVITY)
                    .withString("start", startCityCode)
                    .withString("end", endCityCode)
                    .withString("startCityName", startCityName)
                    .withString("endCityName", endCityName)
                    .withString("time", chooseTime)
                    .navigation()
            }
            SERVICE_SUBWAY -> {//长途汽车列表
                ARouter.getInstance()
                    .build(ARouterPath.ServiceModule.SERVICE_SUBWAY_LIST_ACTIVITY)
                    .withString("startCityName", startCityName)
                    .withString("endCityName", endCityName)
                    .navigation()
            }
            SERVICE_PLANE -> {//机票列表
                ARouter.getInstance()
                    .build(ARouterPath.ServiceModule.SERVICE_PLANE_LIST_ACTIVITY)
                    .withString("startCityName", startCityName)
                    .withString("endCityName", endCityName)
                    .withString("time", chooseTime)
                    .navigation()
            }
        }
    }

    /**
     * 时间选择器
     */
    private val timePickerView by lazy {
        TimePickerBuilder(this, OnTimeSelectListener { date, v ->
            // 选中事件回调
            incidentTime = Utils.getDateTime(Utils.YMD, date)
            chooseTime = Utils.getDateTime(Utils.dateYMD, date)
            val content = incidentTime + getWeekDay("星期", date)
            val distanceColor = TextFontUtil.setTextSizeAndColor(
                content,
                SmartUtil.dp2px(12f),
                Color.parseColor("#999999"),
                content.length - 3,
                content.length
            )
            mBinding.tvTodayTime.text = distanceColor
        })
            .build()
    }

    /**
     *设置切换图标
     */
    private fun setExchangeImg() {
        mBinding.editStartPoint.addTextChangedListener(this)
        mBinding.editEndPoint.addTextChangedListener(this)
    }

    /**
     *设置封面图高度
     */
    private fun setCoverHeight() {
        //获取屏幕宽度
        //获取屏幕宽度
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val width: Int = metrics.widthPixels
        //定义布局参数
        //定义布局参数
        val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.width = width
        layoutParams.height = (width * 0.5).toInt()
        mBinding.imgCover.layoutParams = layoutParams
    }

    override fun initData() {


    }


    override fun afterTextChanged(s: Editable?) {
        val endPointStr = mBinding.editEndPoint.text.toString().trim()
        val startPointStr = mBinding.editStartPoint.text.toString().trim()
        if (!endPointStr.isNullOrEmpty() && !startPointStr.isNullOrEmpty()) {
            mBinding.imgExchange.setImageResource(R.mipmap.service_train_index_button_exchange_highlighted)
        } else {
            mBinding.imgExchange.setImageResource(R.mipmap.service_train_index_button_exchange)
        }

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val cityCode = data?.getStringExtra("cityCode") ?: ""
            val cityName = data?.getStringExtra("cityName") ?: ""
            if (resultCode == CHOOSE_CITY_START_RESULT_CODE) {
                startCityCode = cityCode
                startCityName = cityName
                mBinding.editStartPoint.text = cityName
            } else if (resultCode == CHOOSE_CITY_END_RESULT_CODE) {
                endCityCode = cityCode
                endCityName = cityName
                mBinding.editEndPoint.text = cityName
            }


        }
    }

    override fun setTitle(): String {
        when (queryTraffic) {
            SERVICE_TRAIN -> {//火车列表
                return "火车票查询"
            }
            SERVICE_SUBWAY -> {//长途汽车列表
                return "汽车票查询"
            }
            SERVICE_PLANE -> {//机票查询
                return "机票查询"
            }
        }
        return ""
    }

    override fun injectVm(): Class<BaseViewModel> = BaseViewModel::class.java


}
