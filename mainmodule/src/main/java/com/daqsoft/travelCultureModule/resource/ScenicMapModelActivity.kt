package com.daqsoft.travelCultureModule.resource

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MyLocationStyle
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainActivitiesSenicMapBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.provider.mapview.bean.MapLocation
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.travelCultureModule.resource.adapter.MapModeTabAdapter
import com.daqsoft.travelCultureModule.resource.adapter.ScenicMapModelItemAdapter
import com.daqsoft.travelCultureModule.resource.bean.CurLocationBean
import com.daqsoft.travelCultureModule.resource.bean.LocationInfoBean
import com.daqsoft.travelCultureModule.resource.bean.MapModeData
import com.daqsoft.travelCultureModule.resource.model.MarketModel
import com.daqsoft.travelCultureModule.resource.viewmodel.ScenicMapMViewModel
import com.tbruyelle.rxpermissions2.RxPermissions


/**
 * @Description 活动列表地图模式
 * @ClassName   HotActivitiesMapModelActivity
 * @Author      PuHua
 * @Time        2020/3/16 14:38
 * @update      luoyi
 */
@Route(path = MainARouterPath.MAIN_SCENIC_LIST_MAP)
class ScenicMapModelActivity :
    TitleBarActivity<MainActivitiesSenicMapBinding, ScenicMapMViewModel>(), AMap
.OnMarkerClickListener,
    AMap.OnMapClickListener {

    /**
     * 定位的经纬度
     */
    var lat = 0.0
    var lon = 0.0
    /**
     * 搜索关键字
     */
    var name = ""
    /**
     * 位置信息描述
     */
    var locationString = ""
    // rx权限对象
    private var permissions: RxPermissions? = null
    /**
     * 当前选择tab位置
     */
    @JvmField
    @Autowired
    var mSelectTabPos = 0

    /**
     * 当前选中类型 默认景区
     */
    @JvmField
    @Autowired
    var mSelectType = ResourceType.CONTENT_TYPE_SCENERY
    /**
     * 底部浏览适配器
     */
    var hotActivityAdapter: ScenicMapModelItemAdapter? = null
    /**
     * 地图模式底部适配器
     */
    var mapModeTabAdapter: MapModeTabAdapter? = null
    /**
     * 搜索关键字
     */
    var mKeySearchWord: String? = null

    override fun getLayout(): Int = R.layout.main_activities_senic_map

    override fun setTitle(): String = getString(R.string.home_activity_map_model)

    override fun injectVm(): Class<ScenicMapMViewModel> = ScenicMapMViewModel::class.java

    override fun initView() {
        permissions = RxPermissions(this)
        initModel()
        // 添加marker点击事件
        mBinding.mapView.mapManager.setOnMarkerClickListener(this)
        // 添加地图点击事件
        mBinding.mapView.mapManager.setOnMapClickListener(this)
        mBinding.mapView.mapView.map?.mapType = AMap.MAP_TYPE_NORMAL
        // 底部热门活动适配器
        hotActivityAdapter = ScenicMapModelItemAdapter(this)
        val myLocationStyle: MyLocationStyle = MyLocationStyle()
        myLocationStyle.showMyLocation(false)
        mBinding.mapView.hideLocationIcon()
        mBinding?.xGallery.setAdapter(hotActivityAdapter)
        mBinding.recyMapModeTabs.layoutManager = LinearLayoutManager(this@ScenicMapModelActivity, LinearLayoutManager.HORIZONTAL, false)
        mapModeTabAdapter = MapModeTabAdapter()
        mapModeTabAdapter?.selectType = mSelectType
        mBinding.recyMapModeTabs.adapter = mapModeTabAdapter
        mapModeTabAdapter?.onMapModeTabClickListener = object : MapModeTabAdapter.OnMapModeTabClickListener {
            override fun onMapModeClick(resourceType: String, position: Int, name: String) {
                mBinding.recyMapModeTabs?.smoothScrollToPosition(position)
                mSelectType = resourceType
                getMapSearchData()
                setTitleContent(name)
            }

        }
        mapModeTabAdapter?.clear()
        mapModeTabAdapter?.add(MapModeData.mapModeData())
        // 滑动到选择得tab
        var currentPos = mapModeTabAdapter?.getItemPosition(mSelectType)
        if (currentPos != -1) {
            mBinding.recyMapModeTabs?.smoothScrollToPosition(currentPos!!)
            var name = mapModeTabAdapter?.getData()?.get(currentPos!!)?.name
            if (!name.isNullOrEmpty()) {
                setTitleContent(name)
            }
        }
        initLocaltion()
        changeSelectTab()
    }

    /**
     * 初始化地图和定位相关
     */
    private fun initLocaltion() {
        location()
    }


    /**
     * 初始model
     */
    private fun initModel() {
        mModel?.mPresenter?.value?.loading = false
        mModel?.mPresenter?.value?.isNeedRecyleView = false
        initViewEvent()
        mModel.secnicList.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.mapView?.mapView?.map?.clear()
            mBinding.mapView.setZoom("18")
            // 添加当前位置标记
            addLocationMarket()
            // 添加所有的marker size>1 避免出现 叠印
            if (!it.isNullOrEmpty()) {
                for (i in it.indices) {
                    val el = it[i]
                    if (el != null && el.longitude.isNotEmpty() && el.latitude.isNotEmpty()) {
                        addMapMarket(i, el)
                    }
                    if (i == 0 && !el.latitude.isNullOrEmpty() && !el.longitude.isNullOrEmpty()) {
                        mBinding.mapView.location(LatLng(el.latitude.toDouble(), el.longitude.toDouble()))
                        mBinding.mapView.setZoom("18")
//                        mBinding.mapView.map?.moveCamera(CameraUpdateFactory.zoomTo(17f))
//                       CameraUpdateFactory.zoomTo(20f)
                    }
                }
            }
            mBinding.xGallery.setPageOffscreenLimit(it.size)
            // 底部列表页面的适配器
            hotActivityAdapter!!.menus.clear()
            if (it != null && it.size > 0) {
                mBinding.xGallery.visibility = View.VISIBLE
                hotActivityAdapter!!.menus.addAll(it)

            } else {
                mBinding.xGallery.visibility = View.GONE
                mModel?.toast.postValue(getString(R.string.toast_no_search_info))
            }
            hotActivityAdapter!!.notifyDataSetChanged()
        })
        mModel?.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
    }

    /**
     * 动态添加地图标记
     */
    private fun addMapMarket(i: Int, el: ScenicBean) {
        val marketView = LayoutInflater.from(this@ScenicMapModelActivity).inflate(
            R.layout.main_item_map_market,
            null
        )
        val imgMarket = marketView.findViewById<ImageView>(R.id.iv_market)
        if (i == 0) {
            // 默认选中第一个
            imgMarket.setImageResource(MarketModel.getSelectedMarketRec(mSelectType))
        } else {
            imgMarket.setImageResource(MarketModel.getMarketRec(mSelectType))
        }
        // 将活动对象传入，点击marker时可以依次判定
        val location =
            MapLocation<ScenicBean>(
                el.latitude.toDouble(),
                el.longitude.toDouble()
            )
        location.t = el
        location.title = el.name
        mBinding.mapView.addMarket(location, marketView)
    }

    /**
     *  增加一个当前位置market
     */
    @Synchronized
    private fun addLocationMarket() {
        if (lat != null && lon != null) {
            val marketView = LayoutInflater.from(this@ScenicMapModelActivity).inflate(
                R.layout.main_item_map_market,
                null
            )
            val imgMarket = marketView.findViewById<ImageView>(R.id.iv_market)
            imgMarket.setImageResource(R.mipmap.map_current_position)
            val location =
                MapLocation<CurLocationBean>(lat, lon)
            mBinding.mapView.addMarket(location, marketView)
        }
    }


    /**
     * 获取地图资源
     */
    private fun getMapSearchData() {
        showLoadingDialog()
        val param = HashMap<String, String>()
        param["type"] = mSelectType
        param["latitude"] = lat.toString()
        param["longitude"] = lon.toString()
        if (!mKeySearchWord.isNullOrEmpty()) {
            param["name"] = mKeySearchWord!!
        }
        mModel?.searchScenicMapList(param)
    }

    /**
     * 改变选择的tab
     *  mselecTabPos 当前选择的tab
     */
    private fun changeSelectTab() {
        // 重新获取搜索关键字
        mKeySearchWord = mBinding?.edtSearchMapInfo.text?.toString()

    }



    /**
     * 初始views 事件
     */
    private fun initViewEvent() {
//        if (mSelectTabPos != 0) {
//            mSelectType = ResourceType.CONTENT_TYPE_SCENERY
//            mSelectTabPos = 0
//            changeSelectTab()
//            getMapSearchData()
//        }
        mBinding.ivMapPosition.setOnClickListener {
            location()
        }
        //执行搜索
        mBinding.edtSearchMapInfo?.setOnEditorActionListener(object : TextView
        .OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //点击搜索的时候隐藏软键盘
                    UIHelperUtils.hideKeyboard(mBinding?.edtSearchMapInfo)
                    if (!v?.text.isNullOrEmpty()) {
                        mKeySearchWord = v!!.text.toString()
                        getMapSearchData()
                    } else {
                        if (mKeySearchWord != null) {
                            mKeySearchWord = null
                            getMapSearchData()
                        }
                    }

                    return true;
                }

                return false;
            }
        })
        mBinding?.xGallery.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                val bean = hotActivityAdapter!!.menus[position]
                changeMarketStatusByPosition(bean)
                if (!bean.longitude.isNullOrEmpty() && !bean.latitude.isNullOrEmpty()) {
                    mBinding.mapView?.location(
                        LatLng(
                            bean.latitude.toDouble(), bean.longitude.toDouble()
                        )
                    )
//                    CameraUpdateFactory.zoomTo(20f)
                    mBinding.mapView.setZoom("18")
                }
            }

        })
    }

    /**
     * 点击页面时隐藏底部详情
     */
    override fun onMapClick(p0: LatLng?) {
        if (mBinding.xGallery.isVisible) {
            mBinding.xGallery.visibility = View.GONE
        } else {
            mBinding.xGallery.visibility = View.VISIBLE
        }
    }

    /**
     * 点击marker
     */
    override fun onMarkerClick(p0: Marker?): Boolean {
        val bean = p0!!.`object`
        if (bean is ScenicBean) {
            val index = hotActivityAdapter?.menus!!.indexOf(bean)
            mBinding.xGallery.visibility = View.VISIBLE
            if (mBinding.xGallery.currentPosition != index) {
                mBinding.xGallery.setSelection(index, false)
                changeMarkerIcon(p0, true)
                changeOldMarketIcon(p0)
            }
        } else if (bean is CurLocationBean) {
            bean.location?.let {
                addLocationDetailMark(it)
            }
        } else if (bean is LocationInfoBean) {

        }
        return true
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
                    if (bean is ScenicBean) {
                        changeMarkerIcon(obj, false)
                    }
                }
            }
        }
    }

    /**
     * 根据选中的位置，反选market
     */
    @Synchronized
    private fun changeMarketStatusByPosition(p0: ScenicBean) {
        var temps: List<Marker> = mBinding.mapView.mapManager.markets
        if (!temps.isNullOrEmpty()) {
            for (obj in temps) {
                val bean = obj.`object`
                if (obj != null && bean != null) {
                    if (bean is ScenicBean) {
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
     * 定位自己当前的位置
     */
    fun location() {
        if (
            ContextCompat.checkSelfPermission(this@ScenicMapModelActivity, Manifest.permission.ACCESS_FINE_LOCATION)
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

            override fun onResult(
                adCode: String, result: String, lat_: Double,
                lon_: Double, adcode: String
            ) {
                lat = lat_
                lon = lon_
                locationString = result
                mBinding.mapView.location(lat, lon)
                getMapSearchData()
            }

            override fun onError(errormsg: String) {
                Log.e("TAG", errormsg)
            }
        })
    }

    /**
     * 增加地图详情地址
     */
    private fun addLocationDetailMark(location: MapLocation<LocationInfoBean>) {
        val marketView = LayoutInflater.from(this@ScenicMapModelActivity).inflate(
            R.layout.layout_map_my_location,
            null
        )
        mBinding.mapView.addMarket(location, marketView)
    }

    override fun initData() {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.mapView.create(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mBinding.mapView.mapManager.iMapLifeCycleManager.onDestroy()
        GaoDeLocation.getInstance().release()
        hotActivityAdapter?.menus?.clear()
        hotActivityAdapter = null
        permissions = null
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

