package com.daqsoft.travelCultureModule.sidetour

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
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.maps.AMap
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivitySideTourMapBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.MapResBean
import com.daqsoft.provider.bean.MapResouceContant
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.provider.mapview.bean.MapLocation
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.travelCultureModule.resource.model.MarketModel
import com.daqsoft.travelCultureModule.sidetour.adapter.*
import com.daqsoft.travelCultureModule.sidetour.viewmodel.SideTourViewModel
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.RxPermissions
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

/**
 * @Description 身边游 地图模式
 * @ClassName   SideTourMapActivity
 * @Author      luoyi
 * @Time        2020/3/18 16:06
 */
@Route(path = MainARouterPath.MAIN_SIDE_TOUR)
class SideTourMapActivity : TitleBarActivity<ActivitySideTourMapBinding, SideTourViewModel>(),
    AMap.OnMapClickListener, AMap.OnMarkerClickListener {


    /**
     * 定位的经纬度
     */
    @JvmField
    @Autowired
    var lat = 0.0

    @JvmField
    @Autowired
    var lon = 0.0

    /**
     * 是否从享服务进来
     */
    @JvmField
    @Autowired
    var isService = false

    // rx权限对象
    private var permissions: RxPermissions? = null

    /**
     * 当前选择tab位置
     */
    private var mSelectTabPos = 0

    /**
     * 当前选中类型 默认停车场
     */
    private var mSelectType = ResourceType.CONTENT_TYPE_PARKING

    /**
     *  找厕所适配器
     */
    var mSideTourTilentFragAdapter: SideTourToilentPageAdapter? = null

    /**
     * 找车位适配器
     */
    var mSideTourParkingPageAdapter: SideTourParkingPageAdapter? = null

    /**
     * 医疗点适配器
     */
    val mSideTourCaservacPageAdapter: SideTourCasevacPageAdapter by lazy {
        SideTourCasevacPageAdapter(supportFragmentManager)
    }

    /**加油站Adapter*/
    private val gasstationsAdapter: GasstationsAdapter by lazy {
        GasstationsAdapter(supportFragmentManager)
    }

    /**乘车点**/
    val mSideTourBusStopPageAdapter: SideTourBusPageAdapter by lazy {
        SideTourBusPageAdapter(supportFragmentManager)
    }

    /**购物点**/
    val mSideTourShopMailPageAdapter: SideTourShopMailPageAdapter by lazy {
        SideTourShopMailPageAdapter(supportFragmentManager)
    }

    /**
     * 搜索关键字
     */
    var mKeySearchWord: String? = null

    /**
     * 当前选择的position
     */
    var mCurrentPosition: Int = 0

    companion object {
        const val TAB_POS = "TAB_POS"

        // 厕所
        const val TAB_TOILET = MapResouceContant.MAP_OF_TOILET

        // 停车场
        const val TAB_PARK = MapResouceContant.MAP_OF_PARK

        // 加油站
        const val TAB_GASTATION = MapResouceContant.MAP_OF_GASTATION

        // 医疗点
        const val TAB_CASEVAC = MapResouceContant.MAP_OF_CASEVAC

    }

    override fun getLayout(): Int {
        return R.layout.activity_side_tour_map
    }

    override fun setTitle(): String = getString(R.string.side_tour_title)


    override fun injectVm(): Class<SideTourViewModel> {
        return SideTourViewModel::class.java
    }

    override fun initView() {
        try {
            mSelectTabPos = intent.extras.getInt(TAB_POS)
            changeSearchType()
        } catch (e: Exception) {
        }
        permissions = RxPermissions(this)
        initModel()
        // 添加marker点击事件
        mBinding.mapView.mapManager.setOnMarkerClickListener(this)
        // 添加地图点击事件
        mBinding.mapView.mapManager.setOnMapClickListener(this)
        mBinding.mapView.mapView.map?.mapType = AMap.MAP_TYPE_NIGHT
        mBinding.mapView.hideLocationIcon()
//        mBinding.mapView.mapView.map?.uiSettings?.isZoomControlsEnabled=false
        // 找厕所适配器
        mSideTourTilentFragAdapter = SideTourToilentPageAdapter(supportFragmentManager)
        mBinding?.auVpagerSideTourToilent.adapter = mSideTourTilentFragAdapter
        // 找车位适配器
        mSideTourParkingPageAdapter = SideTourParkingPageAdapter(supportFragmentManager)
        mBinding?.auVpagerSideTourParking.adapter = mSideTourParkingPageAdapter
        mSideTourTilentFragAdapter?.isService = isService
        mSideTourParkingPageAdapter?.isService = isService
        //医疗点
        mBinding?.auVpagerCasevac?.adapter = mSideTourCaservacPageAdapter
        mSideTourCaservacPageAdapter?.isService = isService
        //加油站
        mBinding.auVpagerGasstation.adapter = gasstationsAdapter
        gasstationsAdapter?.isService = isService
        // 乘车点
        mBinding.auVpagerBusStop.adapter = mSideTourBusStopPageAdapter
        mSideTourBusStopPageAdapter.isService = isService
        // 购物点
        mBinding.auVpagerShopMail.adapter = mSideTourShopMailPageAdapter
        mSideTourShopMailPageAdapter.isService = isService

        if (lat != 0.0 && lon != 0.0) {
            mSideTourTilentFragAdapter?.lat = lat.toString()
            mSideTourTilentFragAdapter?.lng = lon.toString()
            mSideTourParkingPageAdapter?.lat = lat.toString()
            mSideTourParkingPageAdapter?.lng = lon.toString()
            mSideTourCaservacPageAdapter?.lat = lat.toString()
            mSideTourCaservacPageAdapter?.lng = lon.toString()
            mSideTourBusStopPageAdapter.lat = lat.toString()
            mSideTourBusStopPageAdapter.lng = lon.toString()
            mSideTourShopMailPageAdapter.lng = lon.toString()
            mSideTourShopMailPageAdapter.lat = lat.toString()
            mBinding.mapView.location(lat, lon)
            gasstationsAdapter.lat = lat
            gasstationsAdapter.lon = lon
            getMapSearchData()
        } else {
            location()
        }
        changeSelectTab()
    }

    private fun changeSearchType() {
        when (mSelectTabPos) {
            MapResouceContant.MAP_OF_PARK -> { //找停车场
                mSelectType = ResourceType.CONTENT_TYPE_PARKING
                title = "停车场"
            }
            MapResouceContant.MAP_OF_TOILET -> { //找厕所
                mSelectType = ResourceType.CONTENT_TYPE_TOILET
                title = "厕所"
            }
            MapResouceContant.MAP_OF_GASTATION -> { //加油站
                mSelectType = ResourceType.TYPE_GAS_STATION
                title = "加油站"
            }
            MapResouceContant.MAP_OF_CASEVAC -> {
                // 医疗点
                mSelectType = ResourceType.CONTENT_TYPE_MEDICAL
                title = "医疗点"
            }
            MapResouceContant.MAP_OF_BUS -> {
                // 乘车点
                mSelectType = ResourceType.CONTENT_TYPE_BUS_STOP
                title = "乘车点"
            }
            MapResouceContant.MAP_OF_SHOP_MAIL -> {
                // 购物点
                mSelectType = ResourceType.CONTENT_TYPE_SHOP_MALL
                title = "购物点"
            }
        }
    }


    /**
     * 初始model
     */
    private fun initModel() {
        mModel?.mPresenter?.value?.loading = true
        mModel?.mPresenter?.value?.isNeedRecyleView = false
        initViewEvent()
        mModel.mapList.observe(this, Observer {
            mBinding.mapView.clearMarket()
            mBinding.mapView.mapManager.aMap.clear()
            mCurrentPosition = 0
            // 添加当前位置标记
            addLocationMarket()
            // 添加所有的marker size>1 避免出现 叠印
            if (!it.isNullOrEmpty()) {
                for (i in it.indices) {
                    val el = it[i]
                    if (el != null && !el.longitude.isNullOrEmpty() && !el.latitude.isNullOrEmpty()) {
                        addMapMarket(i, el)
                    }
                }
            }
            dealPageData(it)
        })
    }

    private fun dealPageData(it: MutableList<MapResBean>?) {
        if (it.isNullOrEmpty()) {
            return
        }
        when (mSelectType) {
            ResourceType.CONTENT_TYPE_PARKING -> {
                // 找车位
                mBinding.auVpagerSideTourParking.offscreenPageLimit = it.size
                mSideTourParkingPageAdapter!!.mDatas.clear()
                if (it != null && it.size > 0) {
                    mSideTourParkingPageAdapter!!.mDatas.addAll(it)
                    mBinding.auVpagerSideTourParking.visibility = View.VISIBLE
                } else {
                    mBinding.auVpagerSideTourParking.visibility = View.GONE
                    mModel?.toast.postValue(getString(R.string.toast_no_search_info))
                }
                mSideTourParkingPageAdapter!!.notifyDataSetChanged()
            }
            ResourceType.CONTENT_TYPE_TOILET -> {
                // 找厕所
                mBinding.auVpagerSideTourToilent.offscreenPageLimit = it.size
                mSideTourTilentFragAdapter!!.mDatas.clear()
                if (it != null && it.size > 0) {
                    mSideTourTilentFragAdapter!!.mDatas.addAll(it)
                    mBinding.auVpagerSideTourToilent.visibility = View.VISIBLE
                } else {
                    mBinding.auVpagerSideTourToilent.visibility = View.GONE
                    mModel?.toast.postValue(getString(R.string.toast_no_search_info))
                }
                mSideTourTilentFragAdapter!!.notifyDataSetChanged()
            }
            // 加油站
            ResourceType.TYPE_GAS_STATION -> {
                if (it.isNullOrEmpty()) {
                    mBinding.auVpagerGasstation.visibility = View.GONE
                    mModel.toast.postValue(getString(R.string.toast_no_search_info))
                } else {
                    mBinding.auVpagerGasstation.visibility = View.VISIBLE
                    mBinding.auVpagerGasstation.offscreenPageLimit = it.size
                    gasstationsAdapter.setDataList(it)
                }
            }
            // 医疗点
            ResourceType.CONTENT_TYPE_MEDICAL -> {
                mSideTourCaservacPageAdapter?.mDatas?.clear()
                if (it.isNullOrEmpty()) {
                    mBinding.auVpagerCasevac.visibility = View.GONE
                    mModel.toast.postValue(getString(R.string.toast_no_search_info))
                } else {

                    mBinding.auVpagerCasevac.visibility = View.VISIBLE
                    mBinding.auVpagerCasevac.offscreenPageLimit = it.size
                    mSideTourCaservacPageAdapter?.mDatas.addAll(it)
                    mSideTourCaservacPageAdapter?.notifyDataSetChanged()
                }
            }
            // 乘车点
            ResourceType.CONTENT_TYPE_BUS_STOP -> {
                mSideTourBusStopPageAdapter?.mDatas.clear()
                if (it.isNullOrEmpty()) {
                    mBinding.auVpagerBusStop.visibility = View.GONE
                    mModel.toast.postValue(getString(R.string.toast_no_search_info))
                } else {
                    mBinding.auVpagerBusStop.visibility = View.VISIBLE
                    mBinding.auVpagerBusStop.offscreenPageLimit = it.size
                    mSideTourBusStopPageAdapter?.mDatas.addAll(it)
                    mSideTourBusStopPageAdapter?.notifyDataSetChanged()
                }
            }
            // 购物点
            ResourceType.CONTENT_TYPE_SHOP_MALL -> {
                mSideTourShopMailPageAdapter?.mDatas?.clear()
                if (it.isNullOrEmpty()) {
                    mBinding.auVpagerShopMail.visibility = View.GONE
                    mModel.toast.postValue(getString(R.string.toast_no_search_info))
                } else {
                    mBinding.auVpagerShopMail.visibility = View.VISIBLE
                    mBinding.auVpagerShopMail.offscreenPageLimit = it.size
                    mSideTourShopMailPageAdapter?.mDatas.addAll(it)
                    mSideTourShopMailPageAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    /**
     * 动态添加地图标记
     */
    private fun addMapMarket(i: Int, el: MapResBean) {
        val marketView = LayoutInflater.from(this@SideTourMapActivity).inflate(
            R.layout.main_item_map_market,
            null
        )
        // 将活动对象传入，点击marker时可以依次判定

        val imgMarket = marketView.findViewById<ImageView>(R.id.iv_market)
        if (i == 0) {
            // 默认选中第一个
            imgMarket.setImageResource(MarketModel.getSelectedMarketRec(mSelectType))
            if (!el.latitude.isNullOrEmpty() && !el.longitude.isNullOrEmpty()) {
                mBinding.mapView.location(
                    LatLng(
                        el.latitude!!.toDouble(),
                        el.longitude!!.toDouble()
                    )
                )
            }
        } else {
            imgMarket.setImageResource(MarketModel.getMarketRec(mSelectType))
        }
        if (!el.latitude.isNullOrEmpty() && !el.longitude.isNullOrEmpty()) {
            val location =
                MapLocation<MapResBean>(
                    el.latitude!!.toDouble(),
                    el.longitude!!.toDouble()
                )
            location.t = el
            location.title = el.name
            mBinding.mapView.addMarket(location, marketView)
        }

    }

    /**
     *  增加一个当前位置market
     */
    @Synchronized
    private fun addLocationMarket() {
        if (lat != null && lon != null) {
            val marketView = LayoutInflater.from(this@SideTourMapActivity).inflate(
                R.layout.main_item_map_market,
                null
            )
            val imgMarket = marketView.findViewById<ImageView>(R.id.iv_market)
            imgMarket.setImageResource(R.mipmap.map_location)
            val location = MapLocation<ScenicBean>(lat, lon)
            mBinding.mapView.addMarket(location, marketView)
        }
    }


    /**
     * 获取地图资源
     */
    private fun getMapSearchData() {
        val param = HashMap<String, String>()
        param["type"] = mSelectType
        param["latitude"] = lat.toString()
        param["longitude"] = lon.toString()
        if (!mKeySearchWord.isNullOrEmpty()) {
            param["name"] = mKeySearchWord!!
        }
        mModel?.searchMapList(param)
    }

    /**
     * 改变选择的tab
     *  mselecTabPos 当前选择的tab
     */
    private fun changeSelectTab() {
        // 重新获取搜索关键字
        mKeySearchWord = mBinding?.edtSearchMapInfo.text?.toString()
        initSelectTab()
        mBinding.auVpagerSideTourParking.visibility = View.GONE
        mBinding.auVpagerSideTourToilent.visibility = View.GONE
        mBinding.auVpagerGasstation.visibility = View.GONE
        mBinding.auVpagerCasevac.visibility = View.GONE
        mBinding.auVpagerShopMail.visibility = View.GONE
        mBinding.auVpagerBusStop.visibility = View.GONE
        when (mSelectTabPos) {
            0 -> { //厕所
                mBinding.imgMapTabToilet.setBackgroundResource(R.mipmap.map_tab_toilet_selected)
                mBinding.txtMapTabToilet.isSelected = true
            }
            1 -> { //停车场
                mBinding.imgMapTabPark.setBackgroundResource(R.mipmap.map_tab_park_selected)
                mBinding.txtMapTabPark.isSelected = true
            }
            2 -> { //加油站
                mBinding.imgMapTabGasoline.setBackgroundResource(R.mipmap.near_icon_gas_selected)
                mBinding.txtMapTabGasoline.isSelected = true
            }
            3 -> { // 医疗点
                mBinding.imgMapTabCasevac.setBackgroundResource(R.mipmap.near_icon_hospital_selected)
                mBinding.txtMapTabCasevac.isSelected = true
            }
            4 -> { // 乘车点
                mBinding.imgMapTabBusStop.setBackgroundResource(R.mipmap.near_icon_bus_selected)
                mBinding.txtMapTabBusStop.isSelected = true
            }
            5 -> { // 购物点
                mBinding.imgMapTabShopMail.setBackgroundResource(R.mipmap.near_icon_shopping_selected)
                mBinding.txtMapTabShopMail.isSelected = true
            }
        }

    }

    /**
     * 初始化底部tab
     */
    private fun initSelectTab() {
        mBinding.imgMapTabPark.setBackgroundResource(R.mipmap.map_tab_park_normal)
        mBinding.txtMapTabPark.isSelected = false
        mBinding.imgMapTabToilet.setBackgroundResource(R.mipmap.map_tab_toilet_normal)
        mBinding.txtMapTabToilet.isSelected = false
        mBinding.imgMapTabGasoline.setBackgroundResource(R.mipmap.map_tab_gasoline_normal)
        mBinding.txtMapTabGasoline.isSelected = false
        mBinding.imgMapTabCasevac.setBackgroundResource(R.mipmap.near_icon_hospital_normal)
        mBinding.txtMapTabCasevac.isSelected = false
        mBinding.imgMapTabBusStop.setBackgroundResource(R.mipmap.near_icon_bus_normal)
        mBinding.txtMapTabBusStop.isSelected = false
        mBinding.imgMapTabShopMail.setBackgroundResource(R.mipmap.near_icon_shopping_normal)
        mBinding.txtMapTabShopMail.isSelected = false
    }

    /**
     * 初始views 事件
     */
    private fun initViewEvent() {
        RxView.clicks(mBinding.vTabPark)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (mSelectTabPos != TAB_PARK) {
                    mSelectTabPos = TAB_PARK
                    changeSearchType()
                    changeSelectTab()
                    getMapSearchData()
                }
            }
        RxView.clicks(mBinding.vTabToilet)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (mSelectTabPos != TAB_TOILET) {
                    mSelectTabPos = TAB_TOILET
                    changeSearchType()
                    changeSelectTab()
                    getMapSearchData()
                }
            }
        mBinding.vTabGasoline.setOnClickListener {
            if (mSelectTabPos != TAB_GASTATION) {
                mSelectType = ResourceType.TYPE_GAS_STATION
                mSelectTabPos = TAB_GASTATION
                changeSearchType()
                changeSelectTab()
                getMapSearchData()
            }
        }
        mBinding.vTabCasevac.onNoDoubleClick {
            if (mSelectTabPos != TAB_CASEVAC) {
                mSelectType = ResourceType.CONTENT_TYPE_MEDICAL
                mSelectTabPos = TAB_CASEVAC
                changeSearchType()
                changeSelectTab()
                getMapSearchData()
            }
        }
        mBinding.vTabBusShop.onNoDoubleClick {
            if (mSelectTabPos != MapResouceContant.MAP_OF_BUS) {
                mSelectType = ResourceType.CONTENT_TYPE_BUS_STOP
                mSelectTabPos = MapResouceContant.MAP_OF_BUS
                changeSearchType()
                changeSelectTab()
                getMapSearchData()
            }
        }
        mBinding.vTabShopMail.onNoDoubleClick {
            if (mSelectTabPos != MapResouceContant.MAP_OF_SHOP_MAIL) {
                mSelectType = ResourceType.CONTENT_TYPE_SHOP_MALL
                mSelectTabPos = MapResouceContant.MAP_OF_SHOP_MAIL
                changeSearchType()
                changeSelectTab()
                getMapSearchData()
            }
        }
        mBinding.ivMapPosition.setOnClickListener {
            location()
        }
        //执行搜索
        mBinding.edtSearchMapInfo?.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //点击搜索的时候隐藏软键盘
                    UIHelperUtils.hideKeyboard(mBinding?.edtSearchMapInfo)
                    if (!v?.text.isNullOrEmpty()) {
//                        mKeySearchWord = v!!.text.toString()
                        getMapSearchData()
                    } else {
//                        if (mKeySearchWord != null) {
//                            mKeySearchWord = null
//                            getMapSearchData()
//                        }
                    }

                    return true;
                }

                return false;
            }
        })

        mBinding?.auVpagerSideTourToilent.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                //发送消息关闭离开页面
            }

            override fun onPageSelected(position: Int) {
                try {
                    EventBus.getDefault().post(SideTourClosePageEvent().apply {
                        type = ResourceType.CONTENT_TYPE_TOILET
                    })
//                mBinding?.auVpagerSideTourToilent.requestLayout()
                    mCurrentPosition = position
                    changeMarketStatusByPosition(mSideTourTilentFragAdapter!!.mDatas[position])
                    var bean = mSideTourTilentFragAdapter!!.mDatas[position]
                    if (bean != null && !bean.longitude.isNullOrEmpty() && !bean.latitude.isNullOrEmpty()) {
                        mBinding.mapView?.location(
                            LatLng(
                                bean.latitude!!.toDouble(), bean.longitude!!.toDouble()
                            )
                        )
                    }
                } catch (e: java.lang.Exception) {
                }

            }

        })

        mBinding?.auVpagerSideTourParking.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                try {
                    EventBus.getDefault().post(SideTourClosePageEvent().apply {
                        type = ResourceType.CONTENT_TYPE_PARKING
                    })
//                mBinding?.auVpagerSideTourParking.requestLayout()
                    mCurrentPosition = position
                    var bean = mSideTourParkingPageAdapter!!.mDatas[position]
                    changeMarketStatusByPosition(mSideTourParkingPageAdapter!!.mDatas[position])
                    if (bean != null && !bean.longitude.isNullOrEmpty() && !bean.latitude.isNullOrEmpty()) {
                        mBinding.mapView?.location(
                            LatLng(
                                bean.latitude!!.toDouble(), bean.longitude!!.toDouble()
                            )
                        )
                    }
                } catch (e: java.lang.Exception) {
                }

            }
        })

        mBinding.auVpagerGasstation.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                EventBus.getDefault()
                    .post(SideTourClosePageEvent().apply { type = ResourceType.TYPE_GAS_STATION })
                val bean = gasstationsAdapter.getData(position)
                if (bean != null) {
                    changeMarketStatusByPosition(bean)
                    if (bean != null && !bean.longitude.isNullOrEmpty() && !bean.latitude.isNullOrEmpty()) {
                        mBinding.mapView?.location(
                            LatLng(
                                bean.latitude!!.toDouble(), bean.longitude!!.toDouble()
                            )
                        )
                    }
                }
            }
        })
        mBinding.auVpagerCasevac.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                try {
                    EventBus.getDefault().post(SideTourClosePageEvent().apply {
                        type = ResourceType.CONTENT_TYPE_MEDICAL
                    })
                    mCurrentPosition = position
                    var bean = mSideTourCaservacPageAdapter.mDatas[position]
                    changeMarketStatusByPosition(mSideTourCaservacPageAdapter.mDatas[position])
                    if (bean != null && !bean.longitude.isNullOrEmpty() && !bean.latitude.isNullOrEmpty()) {
                        mBinding.mapView?.location(
                            LatLng(
                                bean.latitude!!.toDouble(), bean.longitude!!.toDouble()
                            )
                        )
                    }
                } catch (e: java.lang.Exception) {
                }

            }
        })
        mBinding.auVpagerBusStop.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                try {
                    EventBus.getDefault().post(SideTourClosePageEvent().apply {
                        type = ResourceType.CONTENT_TYPE_BUS_STOP
                    })
                    mCurrentPosition = position
                    var bean = mSideTourBusStopPageAdapter.mDatas[position]
                    changeMarketStatusByPosition(mSideTourBusStopPageAdapter.mDatas[position])
                    if (bean != null && !bean.longitude.isNullOrEmpty() && !bean.latitude.isNullOrEmpty()) {
                        mBinding.mapView?.location(
                            LatLng(
                                bean.latitude!!.toDouble(), bean.longitude!!.toDouble()
                            )
                        )
                    }
                } catch (e: java.lang.Exception) {
                }

            }
        })
        mBinding.auVpagerShopMail.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                try {
                    EventBus.getDefault().post(SideTourClosePageEvent().apply {
                        type = ResourceType.CONTENT_TYPE_SHOP_MALL
                    })
                    mCurrentPosition = position
                    var bean = mSideTourShopMailPageAdapter.mDatas[position]
                    changeMarketStatusByPosition(mSideTourShopMailPageAdapter.mDatas[position])
                    if (bean != null && !bean.longitude.isNullOrEmpty() && !bean.latitude.isNullOrEmpty()) {
                        mBinding.mapView?.location(
                            LatLng(
                                bean.latitude!!.toDouble(), bean.longitude!!.toDouble()
                            )
                        )
                    }
                } catch (e: java.lang.Exception) {
                }

            }
        })
    }

    /**
     * 点击页面时隐藏底部详情
     */
    override fun onMapClick(p0: LatLng?) {
        if (mBinding.auVpagerSideTourToilent.isVisible) {
            mBinding.auVpagerSideTourToilent.visibility = View.GONE
        } else {
            mBinding.auVpagerSideTourToilent.visibility = View.VISIBLE
        }
    }

    /**
     * 点击marker
     */
    override fun onMarkerClick(p0: Marker?): Boolean {
        val bean = p0!!.`object`
        if (bean is MapResBean) {
            var index = 0
            if (mSelectType == ResourceType.CONTENT_TYPE_PARKING) {
                index = mSideTourParkingPageAdapter?.mDatas!!.indexOf(bean)
                mBinding.auVpagerSideTourParking.visibility = View.VISIBLE
                if (mCurrentPosition != index) {
                    mBinding.auVpagerSideTourParking.currentItem = index
                    mCurrentPosition = index
                    changeMarkerIcon(p0, true)
                    changeOldMarketIcon(p0)
                }
            } else if (mSelectType == ResourceType.CONTENT_TYPE_TOILET) {
                index = mSideTourTilentFragAdapter?.mDatas!!.indexOf(bean)
                mBinding.auVpagerSideTourToilent.visibility = View.VISIBLE
                if (mCurrentPosition != index) {
                    mBinding.auVpagerSideTourToilent.currentItem = index
                    mCurrentPosition = index
                    changeMarkerIcon(p0, true)
                    changeOldMarketIcon(p0)
                }
            } else if (mSelectType == ResourceType.CONTENT_TYPE_MEDICAL) {
                index = mSideTourCaservacPageAdapter.mDatas!!.indexOf(bean)
                mBinding.auVpagerCasevac.visibility = View.VISIBLE
                if (mCurrentPosition != index) {
                    mBinding.auVpagerCasevac.currentItem = index
                    mCurrentPosition = index
                    changeMarkerIcon(p0, true)
                    changeOldMarketIcon(p0)
                }
            } else if (mSelectType == ResourceType.TYPE_GAS_STATION) {
                if (!gasstationsAdapter.getDataList().isNullOrEmpty()) {
                    index = gasstationsAdapter.getDataList()!!.indexOf(bean)
                    mBinding.auVpagerGasstation.visibility = View.VISIBLE
                    if (mCurrentPosition != index) {
                        mBinding.auVpagerGasstation.currentItem = index
                        mCurrentPosition = index
                        changeMarkerIcon(p0, true)
                        changeOldMarketIcon(p0)
                    }
                }
            } else if (mSelectType == ResourceType.CONTENT_TYPE_BUS_STOP) {
                if (!mSideTourBusStopPageAdapter.mDatas.isNullOrEmpty()) {
                    index = mSideTourBusStopPageAdapter.mDatas!!.indexOf(bean)
                    mBinding.auVpagerBusStop.visibility = View.VISIBLE
                    if (mCurrentPosition != index) {
                        mBinding.auVpagerBusStop.currentItem = index
                        mCurrentPosition = index
                        changeMarkerIcon(p0, true)
                        changeOldMarketIcon(p0)
                    }
                }
            } else if (mSelectType == ResourceType.CONTENT_TYPE_SHOP_MALL) {
                if (!mSideTourShopMailPageAdapter.mDatas.isNullOrEmpty()) {
                    index = mSideTourShopMailPageAdapter.mDatas!!.indexOf(bean)
                    mBinding.auVpagerShopMail.visibility = View.VISIBLE
                    if (mCurrentPosition != index) {
                        mBinding.auVpagerShopMail.currentItem = index
                        mCurrentPosition = index
                        changeMarkerIcon(p0, true)
                        changeOldMarketIcon(p0)
                    }
                }
            }

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
                    if (bean is MapResBean) {
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
    private fun changeMarketStatusByPosition(p0: MapResBean) {
        var temps: List<Marker> = mBinding.mapView.mapManager.markets
        if (!temps.isNullOrEmpty()) {
            for (obj in temps) {
                val bean = obj.`object`
                if (obj != null && bean != null) {
                    if (bean is MapResBean) {
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
        try {
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
        } catch (e: java.lang.Exception) {
        }

    }

    /**
     * 定位自己当前的位置
     */
    fun location() {
        if (
            ContextCompat.checkSelfPermission(
                this@SideTourMapActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            //未开启定位权限
            permissions?.request(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )!!.subscribe {
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
        var locationString = ""
        GaoDeLocation.getInstance().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {

            override fun onResult(
                adCode: String, result: String, lat_: Double,
                lon_: Double, adcode: String
            ) {
                lat = lat_
                lon = lon_
                gasstationsAdapter.lat = lat_
                gasstationsAdapter.lon = lon_
                mSideTourTilentFragAdapter?.lat = lat.toString()
                mSideTourTilentFragAdapter?.lng = lon.toString()
                mSideTourParkingPageAdapter?.lat = lat.toString()
                mSideTourParkingPageAdapter?.lng = lon.toString()
                mSideTourCaservacPageAdapter?.lat = lat.toString()
                mSideTourCaservacPageAdapter?.lng = lon.toString()
                mSideTourBusStopPageAdapter.lat = lat.toString()
                mSideTourBusStopPageAdapter.lng = lon.toString()
                mSideTourShopMailPageAdapter.lng = lon.toString()
                mSideTourShopMailPageAdapter.lat = lat.toString()
                locationString = result
                mBinding.mapView.location(lat, lon)
                getMapSearchData()
            }

            override fun onError(errormsg: String) {
                ToastUtils.showMessage("")
            }
        })
    }

    override fun initData() {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            mBinding.mapView?.mapView?.onCreate(savedInstanceState)
        } catch (e: java.lang.Exception) {
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mBinding.mapView?.mapManager?.setOnMapClickListener(null)
            mBinding.mapView?.mapManager?.setOnMarkerClickListener(null)
            mBinding.mapView?.release()
            mSideTourParkingPageAdapter = null
            GaoDeLocation.getInstance().release()
        } catch (e: java.lang.Exception) {
        }
    }

    override fun onResume() {
        try {
            super.onResume()
            // 在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
            mBinding.mapView?.mapView?.onResume()
        } catch (e: java.lang.Exception) {

        }

    }

    override fun onPause() {
        try {
            super.onPause()
            // 在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
            mBinding.mapView?.mapView?.onPause()
        } catch (e: java.lang.Exception) {

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {

        try {
            super.onSaveInstanceState(outState)
            // 在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
            mBinding.mapView?.mapView?.onSaveInstanceState(outState)
        } catch (e: java.lang.Exception) {
        }
    }
}