package com.daqsoft.servicemodule.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityChooseCityBinding
import com.daqsoft.android.scenic.servicemodule.databinding.ItemLocationBinding
import com.daqsoft.android.scenic.servicemodule.databinding.ItemStationBinding
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.servicemodule.*
import com.daqsoft.servicemodule.bean.ChooseCityBean
import com.daqsoft.servicemodule.bean.StationBean
import com.daqsoft.servicemodule.model.ChooseCityViewModel
import com.daqsoft.servicemodule.uitils.GaoDeLocation
import com.daqsoft.servicemodule.uitils.JavaUtil
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.RxPermissions
import java.util.concurrent.TimeUnit

/**
 * desc :城市选择
 * @author 江云仙
 * @date 2020/4/8 9:10
 */
@Route(path = ARouterPath.ServiceModule.SERVICE_CHOOSE_CITY_ACTIVITY)
class ChooseCityActivity : TitleBarActivity<ActivityChooseCityBinding, ChooseCityViewModel>() {
    @JvmField
    @Autowired
    var jumpType = ""//跳转类型
    //当前城市
    var currentCity = ""
    //城市地址
    @JvmField
    @Autowired(name = "queryTraffic")
    var queryTraffic: String = ""
    var searchType: String = ""
    // rx权限对象
    private var permissions: RxPermissions? = null
    private var cityCode = ""
    private var cityName = ""

    override fun getLayout(): Int {
        return R.layout.activity_choose_city
    }

    override fun setTitle(): String {
        return "出发城市"
    }

    override fun injectVm(): Class<ChooseCityViewModel> = ChooseCityViewModel::class.java
    override fun initView() {
        permissions = RxPermissions(this)
        mModel.queryTraffic = queryTraffic
        //字母搜索
        mBinding.recyLetter.apply {
            layoutManager = GridLayoutManager(this@ChooseCityActivity, 6)
            adapter = letterAdapter
        }
        letterAdapter.add(mModel.getLetter())
        //车站列表
        mBinding.recyStation.apply {
            layoutManager = LinearLayoutManager(this@ChooseCityActivity)
            adapter = stationAdapter
        }

        mModel.clear.observe(this, Observer { reloadData() })

        //历史记录
        mModel.result.observe(this, Observer {
            mBinding.recyRecord.removeAllViews()
            if (it.size > 0) {
                for (position in it.indices) {
                    val parame = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    val tv = TextView(this)
                    var record = it[position].record
                    if (!record.isNullOrEmpty()&&record.contains("||")) {
                        try {
                            record = record.substring(record.lastIndexOf("||")+2, record.length)
                        } catch (e: Exception) {
                        }

                    }
                    tv.text = record
                    tv.isSelected = it[position].isChoose
                    tv.textSize = 13.toFloat()
                    tv.background = resources.getDrawable(R.drawable.service_e2_bg)
                    tv.setPadding(
                        resources.getDimensionPixelOffset(R.dimen.dp_22), resources.getDimensionPixelOffset(R.dimen.dp_7),
                        resources.getDimensionPixelOffset(R.dimen.dp_22), resources.getDimensionPixelOffset(R.dimen.dp_7)
                    )
                    parame.rightMargin = resources.getDimensionPixelOffset(R.dimen.dp_8)
                    parame.bottomMargin = resources.getDimensionPixelOffset(R.dimen.dp_8)
                    mBinding.recyRecord.addView(tv, parame)
                    tv.setOnClickListener { it1 ->
                        //跳转到查询界面
                        setRecordJump(tv.text.toString())

                    }
                }
            }
        })
        setClick()
        // 车站列表
        initSortPopWindow()


    }

    /**
     *搜索记录点击事件
     */
    private fun setRecordJump(tv: String) {
        cityName = tv
        if (queryTraffic == SERVICE_TRAIN) {
            mModel.trainStationBean.value?.clear()
            mModel.name = tv
            mModel.getTrainStationName()
            mModel.trainStationBean.observe(this@ChooseCityActivity, Observer {
                if (it.size == 0) {
//                    ToastUtils.showMessage("请选择正确的城市")
                } else {
                    cityCode = it[0].code ?: ""
                    jumpClick()
                }

            })
        } else {
            cityCode = ""
            jumpClick()

        }
    }

    private fun initSortPopWindow() {
        mModel.stationBean.observe(this, Observer {
            mBinding.recyStation.visibility = View.VISIBLE
            stationAdapter.clear()
            stationAdapter.add(it)
        })

    }

    private fun setClick() {
        //旅行社搜索
        mBinding.edtSearchCity.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val content = mBinding.edtSearchCity.text.toString().trim()
                if (content.isNullOrEmpty()) {
                    setSearchVisible(View.VISIBLE)
                    mBinding.recyLetter.visibility = View.VISIBLE
                    mBinding.recyStation.visibility = View.GONE
                    mModel.name = ""
                } else {
                    stationAdapter.clear()
                    mBinding.recyLetter.visibility = View.GONE
                    setSearchVisible(View.GONE)
                    mModel.name = content
                    mModel.initial = ""
                    //保存搜索记录
                    mModel.saveSearchRecord(content)
                    mModel.getStationName()
                    //发起搜索

                }
                JavaUtil.hideKeyboard(this)
                true
            } else {
                false
            }

        }


        mBinding.tvDelete.onNoDoubleClick {
            mModel.clearSearchRecord()
            //            val dialog = AlertDialog.Builder(BaseApplication.context).create()
//            dialog.show()
//            val window = dialog.window
//            window.setContentView(R.layout.dialog_delete)
//            dialog.setCancelable(false)
//            dialog.setCanceledOnTouchOutside(false)
//            val tvDescription = window.findViewById<View>(com.daqsoft.provider.R.id.tv_dialog_wap_content) as TextView
//            val btnCancel = window.findViewById<View>(com.daqsoft.provider.R.id.btn_dialog_wap_cancel) as Button
//            val btnSure = window.findViewById<View>(com.daqsoft.provider.R.id.btn_dialog_wap_sure) as Button
//            tvDescription.text = "删除后数据将无法恢复"
//            btnCancel.setOnClickListener {
//                dialog.dismiss()
//            }
//            btnSure.setOnClickListener {
//
//                dialog.dismiss()
//            }
        }

    }

    @SuppressLint("CheckResult")
    override fun initData() {
        //定位当前位置
        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            //未开启定位权限
            permissions?.request(Manifest.permission.ACCESS_FINE_LOCATION)!!.subscribe {
                if (it) {
                    doLocation()
                } else {
                    mModel?.toast.postValue("非常抱歉，正常授权才能使用地图模式")
                }
            }
        } else {
            doLocation()
        }
        //设置搜索类型
        setSearchType()
        if (AppUtils.isLogin()) {
            // 获取搜索记录
            mModel.getRecordList()
        }

    }

    private fun doLocation() {
        GaoDeLocation().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {
            override fun onResult(result: String?, lat: Double, lon: Double, adcode: String?, city: String?, cityCode: String?) {
                currentCity = city.toString()
                mBinding.tvLocation.text = currentCity
                val cityNames = currentCity.split("市")
                mModel.name = if (cityNames.isNotEmpty()) {
                    cityNames[0]
                } else {
                    ""
                }
                mModel.getTrainStationName()
                mBinding.rvLocation.onNoDoubleClick {
                    if (queryTraffic == SERVICE_TRAIN) {
                        //点击定位
                        mModel.trainStationBean.observe(this@ChooseCityActivity, Observer {
                            if (it.size > 0) {
                                this@ChooseCityActivity.cityCode = it[0].code ?: ""
                                this@ChooseCityActivity.cityName = it[0].name ?: ""
                            } else {
                                this@ChooseCityActivity.cityCode = ""
                                this@ChooseCityActivity.cityName = if (cityNames.isNotEmpty()) {
                                    cityNames[0]
                                } else {
                                    ""
                                }
                            }
                            //必须放在此处
                            jumpClick()
                        })
                    } else {
                        this@ChooseCityActivity.cityCode = ""
                        this@ChooseCityActivity.cityName = if (cityNames.isNotEmpty()) {
                            cityNames[0]
                        } else {
                            ""
                        }
                        jumpClick()
                    }

                }
            }

            override fun onError(errorMsg: String?) {
            }

        })
    }

    /**
     *跳转到火车票查询界面
     */
    private fun jumpClick() {
        intent.putExtra("cityCode", cityCode)
        intent.putExtra("cityName", cityName)
        if (jumpType == "start") {
            setResult(CHOOSE_CITY_START_RESULT_CODE, intent)
        } else {
            setResult(CHOOSE_CITY_END_RESULT_CODE, intent)
        }
        finish()
    }

    private val letterAdapter = object : RecyclerViewAdapter<ItemLocationBinding, ChooseCityBean>(R.layout.item_location) {
        var currentChooseCity: ChooseCityBean? = null
        @SuppressLint("CheckResult")
        override fun setVariable(mChildBinding: ItemLocationBinding, position: Int, item: ChooseCityBean) {
            mChildBinding.tvCity.text = item.name
            mChildBinding.tvCity.isSelected = item.isChoose
            RxView.clicks(mChildBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    setSearchVisible(View.GONE)
                    mModel.initial = item.name
                    mModel.name = ""
                    mModel.getStationName()
                    if (currentChooseCity != item) {
                        item.isChoose = true
                        if (currentChooseCity != null) {
                            currentChooseCity!!.isChoose = false
                        }
                        currentChooseCity = item
                        notifyDataSetChanged()
                    }
                }
        }
    }

    /**
     *搜索时隐藏控件
     */
    private fun setSearchVisible(visibility: Int) {
        mBinding.tvLocationTitle.visibility = visibility
        mBinding.rvLocation.visibility = visibility
        mBinding.tvDelete.visibility = visibility
        mBinding.recyRecord.visibility = visibility
        mBinding.tvSearch.visibility = visibility
    }


    private val stationAdapter = object : RecyclerViewAdapter<ItemStationBinding, StationBean>(R.layout.item_station) {
        @SuppressLint("CheckResult")
        override fun setVariable(mBinding: ItemStationBinding, position: Int, item: StationBean) {
            if (queryTraffic == SERVICE_PLANE) {
                mBinding.tvStation.text = item.cityname

            } else {
                mBinding.tvStation.text = item.name
            }

            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {

                    //跳转到火车票查询界面
                    cityCode = item.code
                    cityName = if (queryTraffic == SERVICE_PLANE) {
                        item.cityname ?: ""
                    } else {
                        item.name ?: ""
                    }
                    //保存搜索记录
                    mModel.saveSearchRecord(item.code + "||" + item.cityid + "||" + cityName)
                    jumpClick()

                }
        }
    }

    /**
     *设置搜索类型
     */
    private fun setSearchType() {
        when (queryTraffic) {
            SERVICE_PLANE -> {
                searchType = "plane_city"
            }
            SERVICE_SUBWAY -> {
                searchType = "cars_city"
            }
            SERVICE_TRAIN -> {
                searchType = "train_city"
            }

        }
        mModel.searchType = searchType
    }

    override fun onDestroy() {
        super.onDestroy()
        permissions = null
    }

}


