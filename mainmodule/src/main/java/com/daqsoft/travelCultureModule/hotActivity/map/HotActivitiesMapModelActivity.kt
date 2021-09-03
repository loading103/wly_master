package com.daqsoft.travelCultureModule.hotActivity.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapOptions
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.UiSettings
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainActivitiesMapActivityBinding
import com.daqsoft.mainmodule.databinding.MainItemHotActivityClassifyMapBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.Classify
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.provider.mapview.bean.MapConfigureBean
import com.daqsoft.provider.mapview.bean.MapLocation
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.travelCultureModule.hotActivity.adapter.ActivityNaviAdapter
import com.daqsoft.travelCultureModule.resource.model.MarketModel
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.RxPermissions
import java.util.concurrent.TimeUnit


/**
 * @Description 活动列表地图模式
 * @ClassName   HotActivitiesMapModelActivity
 * @Author      PuHua
 * @Time        2020/1/7 14:38
 */
@Route(path = MainARouterPath.MAIN_ACTIVITY_MAP)
class HotActivitiesMapModelActivity :
    TitleBarActivity<MainActivitiesMapActivityBinding, HotActivitiesMapViewModel>(),
    AMap.OnMarkerClickListener,
    AMap.OnMapClickListener {

    /**
     * 定位的经纬度
     */
    var lat = 0.0
    var lon = 0.0

    /**
     * 选择类型
     */
    var mSelectType = ResourceType.CONTENT_TYPE_ACTIVITY

    /**
     * 标记是否可以显示的地图参照物(marker)
     */
    var isHavedShowMapData: Boolean = false

    /**
     * 搜索关键字
     */
    var mKeySearchWord: String? = null

    // rx权限对象
    private var permissions: RxPermissions? = null

    private var activityList: MutableList<ActivityBean>? = null

    private val naviAdapter: ActivityNaviAdapter by lazy {
        ActivityNaviAdapter()
    }

    /**
     * 点击页面时隐藏底部详情
     */
    override fun onMapClick(p0: LatLng?) {
        if (mBinding.recyclerView.isVisible) {
            mBinding.recyclerView.visibility = View.GONE
        } else {
            if (checkHaveActiviesMaker()) {
                mBinding.recyclerView.visibility = View.VISIBLE
            }
        }
    }

    /**
     * 点击marker
     */
    override fun onMarkerClick(p0: Marker?): Boolean {
        val bean = p0!!.`object`

        val index = mModel.activities.value!!.indexOf(bean)

        if (bean != null && bean is ActivityBean) {
            changeMarkerIcon(p0, true)
            changeOldMarketIcon(p0)
//            mBinding.vActivitiesMapInfo.updateUi(bean)
            mBinding.recyclerView.visibility = View.VISIBLE
        } else {
            mBinding.recyclerView.visibility = View.GONE
            mModel.toast.postValue("抱歉，暂无相关信息~")
        }
        return true
    }


    override fun getLayout(): Int = R.layout.main_activities_map_activity

    override fun setTitle(): String = getString(R.string.home_activity_map_model)

    override fun injectVm(): Class<HotActivitiesMapViewModel> =
        HotActivitiesMapViewModel::class.java

    override fun initView() {
        permissions = RxPermissions(this)
        // 活动类型
        val classifyLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvActivityTypes.layoutManager = classifyLayoutManager
        mBinding.rvActivityTypes.adapter = hotClassifyAdapter
        hotClassifyAdapter?.emptyViewShow = false

        // 添加marker点击事件
        mBinding.mapView.mapManager.setOnMarkerClickListener(this)
        // 添加地图点击事件
        mBinding.mapView.mapManager.setOnMapClickListener(this)
        mBinding.mapView.mapView.map?.mapType = AMap.MAP_TYPE_NIGHT




        val uiSettings: UiSettings = mBinding.mapView.mapView.map.uiSettings
        uiSettings.zoomPosition = AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM

        mModel.activityClassifies.observe(this, Observer {
            // 添加全部类型
//            var total = 0
//            for (e in it) {
//                total += e.activityCount.toInt()
//            }
//            val allClassify = Classify(total, "", "全部", true)
//            it.add(0, allClassify)
            if (it.size > 0) {
                hotClassifyAdapter.clearNotify()
                hotClassifyAdapter.addNoNotify(it)
                hotClassifyAdapter.updateSelectStatus(0)
                hotClassifyAdapter.currentClassify = it[0]
                mModel.currentClassifyId = it[0].id ?: ""
                searchActivities()
                hotClassifyAdapter.notifyDataSetChanged()
                mBinding?.rvActivityTypes.smoothScrollToPosition(0)
            } else {
                hotClassifyAdapter.clear()
            }


        })

        mModel.activities.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.mapView.clearMarket()
            // 添加所有的marker
            addLocationMarket()
            if (!it.isNullOrEmpty()) {
                //标记是否有正常数据
                var k = 0
                for (i in it.indices) {
                    val el = it[i]
                    if (el.latitude.isNotEmpty() && el.longitude.isNotEmpty()) {
                        k += 1
                        addMapMarket(k, el)
                    }
                }
                //检查是否合适的数据
                if (k == 0) {
                    mModel.toast.postValue(getString(R.string.toast_no_search_info))
                }
                activityList = it
                naviAdapter.setNewData(it)
            } else {
                mModel.toast.postValue(getString(R.string.toast_no_search_info))
            }
        })
        mModel.mError?.observe(this, Observer {
            dissMissLoadingDialog()
        })
        mBinding.edtSearchActivities.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //点击搜索的时候隐藏软键盘
                    UIHelperUtils.hideKeyboard(mBinding?.edtSearchActivities)
                    searchActivities()
                    return true;
                }

                return false;
            }
        })


        location(true)

        mBinding.recyclerView.let {
            it.layoutManager = LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,false)
            it.adapter = naviAdapter
            snapHelper.attachToRecyclerView(it)
        }

        mBinding.ivMapPosition.setOnClickListener{
            location(false)
        }
    }

    /**
     * 改变marker凸显效果
     */
    @Synchronized
    private fun changeMarkerIcon(marker: Marker, select: Boolean) {
        marker.options.icon.bitmap.recycle()
        if (select)
            marker.setIcon(
                BitmapDescriptorFactory.fromResource(
                    MarketModel.getSelectedMarketRec
                        (mSelectType)
                )
            )
        else
            marker.setIcon(
                BitmapDescriptorFactory.fromResource(
                    MarketModel.getMarketRec
                        (mSelectType)
                )
            )
    }

    /**
     * 设置非点击market 为未选中
     */
    @Synchronized
    private fun changeOldMarketIcon(p0: Marker) {
        var temps: List<Marker> = mBinding.mapView.mapManager.markets
        if (!temps.isNullOrEmpty()) {
            for (obj in temps) {
                val bean = obj.`object`
                if (obj != null && bean != null && obj != p0) {
                    if (bean is ActivityBean) {
                        changeMarkerIcon(obj, false)
                    }
                }
            }
        }
    }

    /**
     *  增加一个当前位置market
     */
    @Synchronized
    private fun addLocationMarket() {
        if (lat != null && lon != null) {
            val marketView = LayoutInflater.from(this@HotActivitiesMapModelActivity).inflate(
                R.layout.main_item_map_market,
                null
            )
            val imgMarket = marketView.findViewById<ImageView>(R.id.iv_market)
            imgMarket.setImageResource(R.mipmap.map_current_position)
            val location =
                MapLocation<ScenicBean>(lat, lon)
            mBinding.mapView.addMarket(location, marketView)
        }
    }

    /**
     * 检查地图maker列表是否含有活动标记
     */
    private fun checkHaveActiviesMaker(): Boolean {
        var isHaved = false;
        var temps: List<Marker> = mBinding.mapView.mapManager.markets
        if (!temps.isNullOrEmpty()) {
            for (obj in temps) {
                val bean = obj.`object`
                if (obj != null && bean != null) {
                    if (bean is ActivityBean) {
                        isHaved = true
                        break
                    }
                }
            }
        }
        return isHaved
    }

    /**
     * 动态添加地图标记
     */
    private fun addMapMarket(i: Int, el: ActivityBean) {
        val lat = el.latitude.toDouble()
        val loni = el.longitude.toDouble()
        val marketView = LayoutInflater.from(this@HotActivitiesMapModelActivity).inflate(
            R.layout.main_item_map_market,
            null
        )
        // 将活动对象传入，点击marker时可以依次判定
        val location =
            MapLocation<ActivityBean>(
                lat,
                loni
            )
        val imgMarket = marketView.findViewById<ImageView>(R.id.iv_market)
        if (i == 1) {
            // 默认选中第一个
            imgMarket.setImageResource(MarketModel.getSelectedMarketRec(mSelectType))
//            mBinding.vActivitiesMapInfo.updateUi(el)
            mBinding.recyclerView.visibility = View.VISIBLE
            mBinding.mapView.location(LatLng(lat, loni))
        } else {
            imgMarket.setImageResource(MarketModel.getMarketRec(mSelectType))
        }

        location.t = el
        location.title = el.name
        mBinding.mapView.addMarket(location, marketView)
    }

    /**
     * 定位自己的位置
     */
    fun location(isLocal: Boolean) {
        if (
            ContextCompat.checkSelfPermission(
                this@HotActivitiesMapModelActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            //未开启定位权限
            permissions?.request(Manifest.permission.ACCESS_FINE_LOCATION)!!.subscribe {
                if (it) {
                    doLocation(isLocal)
                } else {
                    mModel?.toast.postValue("非常抱歉，正常授权才能使用地图模式")
                }
            }
        } else {
            doLocation(isLocal)
        }


    }

    private fun doLocation(isLocal: Boolean) {
        var locationString = ""
        GaoDeLocation.getInstance().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {

            override fun onResult(
                adCode: String, result: String, lat_: Double,
                lon_: Double, adcode: String
            ) {
                lat = lat_
                lon = lon_
                locationString = result
                naviAdapter.setStartLatLng(LatLng(lat,lon))
//                mBinding.mapView.location(lat, lon)

                mBinding.mapView.mapView.map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat,lon),15f));
                mBinding.mapView
                val location = MapLocation<MapConfigureBean>(lat, lon)
                val marketView = LayoutInflater.from(this@HotActivitiesMapModelActivity).inflate(
                    R.layout.layout_map_my_location_ws,
                    null
                )

                val txt = marketView.findViewById(R.id.tv_location) as TextView
                txt.text = locationString

                mBinding.mapView.addMarket(location, marketView)
//                mBinding.vActivitiesMapInfo.latLng = LatLng(lat, lon)
//                mBinding.vActivitiesMapInfo.currAdress = locationString
                if(isLocal){
                    showLoadingDialog()
                    mModel.getActivityClassify()
                }

            }

            override fun onError(errormsg: String) {

            }
        })
    }

    override fun initData() {

    }

    /**
     * 热门活动分类适配器
     */
    private val hotClassifyAdapter = object :
        RecyclerViewAdapter<MainItemHotActivityClassifyMapBinding, Classify>(R.layout.main_item_hot_activity_classify_map) {
        var currentClassify: Classify? = null

        init {
            emptyViewShow = false
        }

        fun updateSelectStatus(position: Int) {
            if (!getData().isNullOrEmpty() && position < getData().size) {
                for (i in getData().indices) {
                    (getData()[i] as Classify).select = position == i
                }
            }
        }

        override fun setVariable(
            mBinding: MainItemHotActivityClassifyMapBinding,
            position: Int,
            item: Classify
        ) {
            mBinding.label.text = item.labelName + "(" + item.activityCount + ")"
            mBinding.label.isSelected = item.select

            var d = RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    run {
                        if (currentClassify != item) {
                            updateSelectStatus(position)
                            currentClassify = item
                            mModel.currentClassifyId = item.id?:""
                            searchActivities()
                            scrollToSelectPosition(position)
                            notifyDataSetChanged()
                        }
                    }
                }
        }
    }

    private fun scrollToSelectPosition(position: Int) {
        mBinding?.rvActivityTypes?.smoothScrollToPosition(position)
    }

    /**RecyclerView 左右滑动监听*/
    private var snapHelper = object : PagerSnapHelper(){
        override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager?, velocityX: Int, velocityY: Int): Int {
            val position = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
            if (!activityList.isNullOrEmpty()){
                val bean = activityList!![position]
                mBinding.mapView.location(LatLng(bean.latitude.toDouble(), bean.longitude.toDouble()))
                changeMarketStatusByPosition(bean)
            }
            return position
        }
    }
    /**
     * 根据选中的位置，反选market
     */
    @Synchronized
    private fun changeMarketStatusByPosition(p0: ActivityBean) {
        var temps: List<Marker> = mBinding.mapView.mapManager.markets
        if (!temps.isNullOrEmpty()) {
            for (obj in temps) {
                val bean = obj.`object`
                if (obj != null && bean != null) {
                    if (bean is ActivityBean) {
                        if (bean != p0) {
                            changeMarkerIcon(obj, false)
                        } else {
                            changeMarkerIcon(obj, true)
                        }
                    }
                }
            }
        }
    }
    /**
     * 搜索活动
     */
    private fun searchActivities() {
        showLoadingDialog()
        mKeySearchWord = mBinding.edtSearchActivities?.text?.toString()
        mBinding.recyclerView.visibility = View.GONE
        mModel.getActivityList(lat.toString(), lon.toString(), mKeySearchWord)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.mapView.create(savedInstanceState)
//        mModel.getActivityClassify()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mBinding.mapView.mapManager.iMapLifeCycleManager.onDestroy()
        permissions = null
        GaoDeLocation.getInstance().release()

    }

    override fun onResume() {
        super.onResume()
        // 在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mBinding.mapView.mapManager.iMapLifeCycleManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        // 在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mBinding.mapView.mapManager.iMapLifeCycleManager.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mBinding.mapView.mapManager.iMapLifeCycleManager.onSaveInstanceState(outState)
    }

}

