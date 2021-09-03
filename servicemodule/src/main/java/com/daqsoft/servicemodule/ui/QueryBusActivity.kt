package com.daqsoft.servicemodule.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityQueryBusBinding
import com.daqsoft.baselib.base.BaseActivity
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.servicemodule.adapter.AutoAdapter
import com.daqsoft.servicemodule.model.QueryBusViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import org.jetbrains.anko.toast


/**
 * desc :公交查询
 * @author 江云仙
 * @date 2020/4/3 9:15
 */
@Route(path = ARouterPath.ServiceModule.SERVICE_QUERY_BUS_ACTIVITY)
class QueryBusActivity : TitleBarActivity<ActivityQueryBusBinding, QueryBusViewModel>()/*, TextWatcher*/ {
    /**
     * 当前经纬度
     */
    var currentLat = ""
    var currentLon = ""
    var currentAddress = ""
    // rx权限对象
    private var permissions: RxPermissions? = null
    override fun getLayout(): Int {
        return R.layout.activity_query_bus
    }


    override fun initView() {
        permissions = RxPermissions(this)
        setCoverHeight()
        setExchangeImg()
        setClick()

        mModel.startInputTips.observe(this, Observer { parentIt ->
            if (parentIt.size>0){
                val adapter = AutoAdapter(this, R.layout.item_auto,  parentIt/*parentIt.map { it.name }*/)
                //也可以使用系统自带的
                mBinding.editStartPoint.setAdapter(adapter)
                mBinding.editStartPoint.threshold = 1 //设置输入几个字符后开始出现提示 默认是2
                adapter.notifyDataSetChanged()

            }

        })

        mBinding.editEndPoint.threshold = 1 //设置输入几个字符后开始出现提示 默认是2

        mModel.inputTips.observe(this, Observer { parentIt ->
            //也可以使用系统自带的
            if (parentIt.size>0){
                val adapter = AutoAdapter(this, R.layout.item_auto, parentIt/*parentIt.map { it.name }*/)
                mBinding.editEndPoint.setAdapter(adapter)
                adapter.notifyDataSetChanged()
            }

        })

    }

    /**
     *控件点击事件
     */
    private fun setClick() {
        //位置切换点击事件
        mBinding.imgExchange.setOnClickListener {
            val startPointStr = mBinding.editStartPoint.text.toString()
            val endPointStr = mBinding.editEndPoint.text.toString()
            mBinding.editStartPoint.setText(endPointStr)
            mBinding.editEndPoint.setText(startPointStr)
            //移动光标位置
            mBinding.editStartPoint.setSelection(mBinding.editStartPoint.text.toString().length)
            mBinding.editEndPoint.setSelection(mBinding.editEndPoint.text.toString().length)
            //光标跳转
            if (startPointStr.isNullOrEmpty()) {
                mBinding.editEndPoint.requestFocus()
            } else {
                mBinding.editStartPoint.requestFocus()
            }
        }
        mBinding.imgNearBus.onNoDoubleClick {
            //页面跳转
            ARouter.getInstance()
                .build(ARouterPath.ServiceModule.SERVICE_NEAR_BUS_ACTIVITY)
                .withString("currentLat", currentLat.toString())
                .withString("currentLon", currentLon.toString())
                .navigation()
        }


        //点击查询
        mBinding.tvQuery.onNoDoubleClick {
            val editStartPoint = mBinding.editStartPoint.text.toString().trim()
            val editEndPoint = mBinding.editEndPoint.text.toString().trim()
            if (editStartPoint.isNullOrEmpty()) {
                toast("请输入起点").setGravity(Gravity.CENTER, 0, 0)
                return@onNoDoubleClick
            }
            if (editEndPoint.isNullOrEmpty()) {
                toast("请输入终点").setGravity(Gravity.CENTER, 0, 0)
                return@onNoDoubleClick
            }
            when {

                editEndPoint == "我的位置" -> {
                    mModel.startAddress = editStartPoint
                    mModel.endAddress = "我的位置"
                    mModel.nextAddress=currentAddress
                    mModel.getBusAddress("start", editStartPoint)
//                    mModel.getBusAddress("end", currentAddress)


                }
                editStartPoint == "我的位置" -> {
                    mModel.startAddress = "我的位置"
                    mModel.endAddress = editEndPoint
                    mModel.nextAddress=editEndPoint
                    mModel.getBusAddress("start", currentAddress)
//                    mModel.getBusAddress("end", editEndPoint)


                }
                else -> {
                    mModel.startAddress = editStartPoint
                    mModel.endAddress = editEndPoint
                    mModel.nextAddress=editEndPoint
                    mModel.getBusAddress("start", editStartPoint)
//                    mModel.getBusAddress("end", editEndPoint)
                }
            }

        }
    }

    /**
     *设置切换图标
     */
    private fun setExchangeImg() {
        mBinding.editStartPoint.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val endPointStr = mBinding.editEndPoint.text.toString().trim()
                val startPointStr = mBinding.editStartPoint.text.toString().trim()
                if (startPointStr.isNotEmpty()){
                    mModel.keyword=s.toString()
                    mModel.getStartInputTips()
                }

                if (endPointStr.isNotEmpty() && startPointStr.isNotEmpty()) {
                    mBinding.imgExchange.setImageResource(R.mipmap.service_bus_icon_exchange_highlighted)
                } else {
                    mBinding.imgExchange.setImageResource(R.mipmap.service_bus_index_icon_exchange)
                }
            }

        })
        mBinding.editEndPoint.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val endPointStr = mBinding.editEndPoint.text.toString().trim()
                val startPointStr = mBinding.editStartPoint.text.toString().trim()
                if (s.toString().isNotEmpty()) {
                    mModel.keyword = s.toString()
                    mModel.getInputTips()
                }
                if (endPointStr.isNotEmpty() && startPointStr.isNotEmpty()) {
                    mBinding.imgExchange.setImageResource(R.mipmap.service_bus_icon_exchange_highlighted)
                } else {
                    mBinding.imgExchange.setImageResource(R.mipmap.service_bus_index_icon_exchange)
                }
            }

        })
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

    @SuppressLint("CheckResult")
    override fun initData() {
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



    }

    private fun doLocation() {
        GaoDeLocation.getInstance().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {
            override fun onResult(adCode: String?, result: String?, lat: Double, lon: Double, adcode: String?) {
                currentLat = lat.toString()
                currentLon = lon.toString()
                currentAddress = result.toString()

            }

            override fun onError(errorMsg: String?) {

            }

        })
    }


    override fun setTitle(): String {
        return "公交查询"
    }

    override fun injectVm(): Class<QueryBusViewModel> =QueryBusViewModel::class.java

    override fun onDestroy() {
        super.onDestroy()
        permissions = null
        GaoDeLocation.getInstance().release()
    }
}
