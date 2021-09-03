package com.daqsoft.guidemodule.guideTourMap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.guidemodule.*
import com.daqsoft.guidemodule.adapter.GuideTourResourceAdapter
import com.daqsoft.guidemodule.bean.*
import com.daqsoft.guidemodule.bean.safeSubstring
import com.daqsoft.guidemodule.databinding.GuideGuideTourMapActivityBinding
import com.daqsoft.guidemodule.fragment.GuideLineFragment
import com.daqsoft.guidemodule.fragment.GuideVpScenicFragment
import com.daqsoft.guidemodule.guideTourMode.GuideTourModeActivity
import com.daqsoft.guidemodule.net.logI
import com.daqsoft.guidemodule.speakPlayer.SpeakPlayReceiver
import com.daqsoft.guidemodule.speakPlayer.SpeakPlayer
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.mapview.bean.MapLocation
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

/**
 *
 * 全域版：景区，非遗，厕所，停车场。景区版：景点，厕所，停车场
 *
 * @Description  导览地图
 * @ClassName   GuideTourMapActivity
 * @Author      Wongxd
 * @Time        2020/4/1 15:07
 */
@Route(path = ARouterPath.GuideModule.GUIDE_TOUR_ACTIVITY)
internal class GuideTourMapActivity :
    TitleBarActivity<GuideGuideTourMapActivityBinding, GuideTourMapViewModel>(),
    AMap.OnMapClickListener, AMap.OnMarkerClickListener {


    /**
     * 标记需要接收事件的 实例
     */
    private var eventTag = javaClass.name

    /**
     * tourId
     */
    @JvmField
    @Autowired
    var tourId: String = ""


    /**
     * 上级全域地图的tourId，只有当前是 景区地图才会显示
     */
    @JvmField
    @Autowired
    var allAreaTourId: String = ""

    /**
     * 是否是 线路-预览模式
     */
    @JvmField
    @Autowired
    var isLinePreviewMode: Boolean = false


    /**
     * 是否是 线路-导览模式
     */
    @JvmField
    @Autowired
    var isLineTourMode: Boolean = false


    /**
     * 定位的经纬度
     */
    private var lat = 0.0
    private var lon = 0.0

    private var isInitLine: Boolean = false

    // rx权限对象
    private var permissions: RxPermissions? = null

    private var mSelectType: String = GuideMapShowType.GUIDE_SHOW_TYPE_SCENIC

    /**
     * 场馆资源 类型
     */
    private var currVenueType: String? = ""

    /**
     * 场馆资源 具体类型
     */
    private var currVenueTypeMode: String? = ""
    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null

    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
            tourId?.let {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this)
                }
                // 设置分享数据
                sharePopWindow?.setShareContent(
                   "导游导览详情", "点击查看详情", "",
                    ShareModel.getDaoYouDesc(tourId)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }
            }
        })
    }
    private val guideTourResourceAdapter: GuideTourResourceAdapter by lazy {
        GuideTourResourceAdapter().apply {
            emptyViewShow = false
            onItemclickListener = object : GuideTourResourceAdapter.OnItemClickListener {
                override fun onItemClick(resource: GuideResouceBean) {
                    resource.resourceType?.let {
                        mSelectType = it
                        if (it != ResourceType.CONTENT_TYPE_LINE) {
                            currVenueType = resource.type.toString()
                            currVenueTypeMode = resource.value
                            prepare2NetData()
                        } else {
                            // 线路处理
                            mBinding.flGuideVp.visibility = View.GONE
                            mBinding.mapView.getaMap().clear()
                            mModel.infoData.value?.let {
                                setCustomMapBg(it)
                            }
                            mModel.getLine(HashMap<String, String>().apply {
                                put("tourId", tourId)
                                put("lat", lat.toString())
                                put("lon", lon.toString())
                            })
                        }
                    }

                }

                override fun onItemSelect(position: Int) {
                    mBinding.rvGuideTourResources.smoothScrollToPosition(position)
                }

            }
        }
    }

    /**
     *  vp适配器
     */
    private val mVpAdapter: GuideVpAdapter by lazy {
        GuideVpAdapter(
            supportFragmentManager,
            eventTag
        )
    }


    /**
     * 当前选中的 vp index
     */
    private var mCurrentPosition = 0


    /**
     * 语音讲解的 event
     */
    private var mGuideVpSpeakStatusEvent: GuideSpeakStatusEvent? = null


    /**
     * 线路-map线路信息
     */
    private var mPolyLine: Polyline? = null

    /**
     * 线路-线路fragment
     */
    private var mLineFragment: GuideLineFragment? = null


    /**
     * 可以显示所有marker 的合适的缩放比例
     */
    private var mZoomSize = 13f

    /**
     * 是否是全域
     */
    var area = false

    /**
     * 返回 资源类型
     */
    private var backResouceType: String? = ""

    /**
     * 返回资源id
     */
    private var backResouceId: String? = ""

    private var backResourceVenueType: String? = ""

    private var currentMapMode: Int = 0

    override fun getLayout(): Int = R.layout.guide_guide_tour_map_activity

    override fun setTitle(): String {
        return getString(R.string.guide_all_area_title)
    }

    override fun injectVm(): Class<GuideTourMapViewModel> {
        return GuideTourMapViewModel::class.java
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        if (allAreaTourId.isNotBlank() && mSelectType == GuideMapShowType.GUIDE_SHOW_TYPE_SCENIC) {
//            tourId = allAreaTourId
//            allAreaTourId = ""
//        }
//
//        mModel.getInfo(tourId)
//    }

    override fun initData() {

        // Arouter 自动获取字段，可能为 null
        if (allAreaTourId == null) {
            allAreaTourId = ""
        }

        if (isLineTourMode == null) {
            isLineTourMode = false
        }

        if (isLinePreviewMode == null) {
            isLinePreviewMode = false
        }
    }


    override fun initView() {

        eventTag = "${javaClass.name}-${System.currentTimeMillis()}"

        mBinding.ivMapPosition.setOnClickListener {
            location(true)
        }

        hideAllFunctionalView()

        permissions = RxPermissions(this)

        // 添加marker点击事件
        mBinding.mapView.mapManager.setOnMarkerClickListener(this)
        // 添加地图点击事件
        mBinding.mapView.mapManager.setOnMapClickListener(this)
        //
        mBinding.rvGuideTourResources.run {
            adapter = guideTourResourceAdapter
            layoutManager = LinearLayoutManager(this@GuideTourMapActivity, LinearLayoutManager.VERTICAL, false)
        }

        // VP适配器
        mBinding.auVpGuide.adapter = mVpAdapter
        mBinding.auVpGuide.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                mBinding.flGuideVp.visibility = View.VISIBLE
                if (mCurrentPosition != position) {
                    mCurrentPosition = position

                    val bean = mModel.homeList.value?.get(position) ?: return
                    if (!bean.latitude.isNullOrEmpty() && !bean.longitude.isNullOrEmpty())
                        changeCamera(LatLng(bean.latitude.toDouble(), bean.longitude.toDouble()))

                    mBinding.flGuideVp.postDelayed({
                        changeMarkerStatusByPosition(bean)
                    }, 600)

                }

            }
        })


        mBinding.tvShowAllArea.setOnClickListener {

            finish()

//            mSelectType = GuideMapShowType.GUIDE_SHOW_TYPE_SCENIC
//            ARouter.getInstance()
//                .build(ARouterPath.GuideModule.GUIDE_TOUR_ACTIVITY)
//                .withString("tourId", tourId)
//                .withString("allAreaTourId", allAreaTourId)
//                .navigation()
        }


        mBinding.tvShowScenicList.setOnClickListener {
            ARouter.getInstance()
                .build(ARouterPath.GuideModule.GUIDE_SCENIC_LIST_ACTIVITY)
                .navigation()
        }



        mBinding.llSearch.setOnClickListener {
            val isShowLegacy = mModel.infoData.value?.type == 1
            ARouter.getInstance()
                .build(ARouterPath.GuideModule.GUIDE_SEARCH_ACTIVITY)
                .withBoolean("isShowLegacy", isShowLegacy)
                .withString("defaultType", GuideMapShowType.GUIDE_SHOW_TYPE_PARK)
                .withString("tourId", tourId)
                .withInt("mapMode", currentMapMode)
                .navigation(this@GuideTourMapActivity, 2)
        }

        initModel()

        location(false)

    }


    /**
     * 初始model
     */
    private fun initModel() {


        mModel.mPresenter.value?.loading = true
        mModel.mPresenter.value?.isNeedRecyleView = false

        //地图信息
        mModel.infoData.observe(this, Observer {

            mZoomSize = it.zoom.toFloat()

            if (it.zoomsMax >= mZoomSize)
                mBinding.mapView.getaMap().maxZoomLevel = it.zoomsMax.toFloat()

            if (it.zoomsMin.toFloat() in 0f..mZoomSize)
                mBinding.mapView.getaMap().minZoomLevel = it.zoomsMin.toFloat()

            mBinding.mapView.getaMap().moveCamera(CameraUpdateFactory.zoomTo(mZoomSize))

            renderMapFunctionUiAndGetListInfo(it)

            setCustomMapBg(it)

            if (!it.latitude.isNullOrEmpty() && !it.longitude.isNullOrEmpty()) {
                mBinding.mapView.getaMap().moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            it.latitude.toDouble(),
                            it.longitude.toDouble()
                        ), mZoomSize
                    )
                )
            }
        })


        //景区 非遗 景点 厕所  停车场
        mModel.homeList.observe(this, Observer {

            dealViewPager(it)

            setMarkerInfoWindowAdapter()


            // 添加所有的marker
            if (!it.isNullOrEmpty()) {

//                setSuitZoom(it)

                it.forEachWithIndex { i, el ->
                    if (!el.longitude.isNullOrEmpty() && !el.latitude.isNullOrEmpty()) {
                        addMapMarker(i, el)
                    }
                }

                val bean = it[mCurrentPosition]


                mBinding.mapView.getaMap()
                    .animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                bean.latitude.toDouble(),
                                bean.longitude.toDouble()
                            ), mZoomSize
                        )
                        , 400, null
                    )

                mBinding.mapView.postDelayed({
                    changeMarkerStatusByPosition(bean)
                }, 600)


            }


            // 添加当前位置标记
            addLocationMarker()


        })

        //线路
        mModel.guideLineData.observe(this, Observer {

            if (isInitLine) {
                isInitLine = false
                if (!it.isNullOrEmpty()) {
                    guideTourResourceAdapter.addItem(GuideResouceBean().apply {
                        resourceType = ResourceType.CONTENT_TYPE_LINE
                    })
                    if (!mBinding.rvGuideTourResources.isVisible) {
                        mBinding.rvGuideTourResources.visibility = View.VISIBLE
                    }
                    // 只有线路，默认加载线路数据
                    if (guideTourResourceAdapter.getData().size == 1) {
                        mBinding.flGuideVp.visibility = View.GONE
                        mBinding.mapView.getaMap().clear()
                        mModel.infoData.value?.let {
                            setCustomMapBg(it)
                        }
//                        mModel.getLine(HashMap<String, String>().apply {
//                            put("tourId", tourId)
//                            put("lat", lat.toString())
//                            put("lon", lon.toString())
//                        })
                    } else {
                        return@Observer
                    }
                }

            }


            if (it.isNullOrEmpty()) {
                mModel.toast.postValue(getString(R.string.guide_no_line_data))
                return@Observer
            }

            mBinding.flGuideVp.isVisible = false

            if (!isLinePreviewMode) {
                mBinding.flGuideLine.isVisible = true
                val isAllArea = mModel.infoData.value?.type == 1
                mLineFragment = GuideLineFragment(eventTag, it, isAllArea)
                mLineFragment?.let {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fl_guide_line, it, "line").commit()
                }
            } else {
                mBinding.flGuideLine.visibility = View.GONE
            }

            renderLineInfoOnMap(it[0].details)


        })
        mModel.tourResourceListLd.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.rvGuideTourResources.visibility = View.VISIBLE
                guideTourResourceAdapter.clear()
                guideTourResourceAdapter.add(it)
                var firstBean = it[0]
                if (!firstBean.resourceType.isNullOrEmpty()) {
                    mSelectType = firstBean.resourceType!!
                    currVenueTypeMode = firstBean.value
                    prepare2NetData()
                }
            } else {
                mBinding.rvGuideTourResources.visibility = View.GONE
            }
            isInitLine = true
            mModel.getLine(HashMap<String, String>().apply {
                put("tourId", tourId)
                put("lat", lat.toString())
                put("lon", lon.toString())
            })
        })
    }


    private var mCacheHandPaintedMapPath = ""

    /**
     * 自定义地图背景
     */
    @SuppressLint("CheckResult")
    private fun setCustomMapBg(it: GuideScenicDetailBean) {

        /**
         * 绘制地图-整张图
         */
        fun setCustomMapLayer(data: GuideScenicDetailBean, filePath: String) {

            if (data.latitudeBottomLeft.isNullOrEmpty() || data.longitudeBottomLeft.isNullOrEmpty() || data.latitudeTopRight.isNullOrEmpty() || data.longitudeTopRight.isNullOrEmpty()) {
                return
            }

            mCacheHandPaintedMapPath = filePath

            val aMap = mBinding.mapView.getaMap()

            aMap.showBuildings(false)
//            val latLngBounds = LatLngBounds(
//                LatLng(data.latitudeBottomLeft.toDouble(), data.longitudeBottomLeft.toDouble()),
//                LatLng(data.latitudeTopRight.toDouble(), data.longitudeTopRight.toDouble())
//            )

//            aMap.setMapStatusLimits(latLngBounds)

//            if (data.latitude.isNotBlank() && data.longitude.isNotBlank()) {
//                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(data.latitude.toDouble(), data.longitude.toDouble()), mZoomSize))
//            } else {
//                aMap.moveCamera(CameraUpdateFactory.zoomTo(mZoomSize))
//            }

            //设置显示在屏幕中的地图地理范围,地图中点显示为两个点的中点
            val bounds = LatLngBounds.Builder()
                .include(
                    LatLng(
                        data.latitudeBottomLeft.toDouble(),
                        data.longitudeBottomLeft.toDouble()
                    )
                )
                .include(
                    LatLng(
                        data.latitudeTopRight.toDouble(),
                        data.longitudeTopRight.toDouble()
                    )
                )


//            val tempPointList = mVpAdapter.mDataList.map {
//                if (it.latitude.isNotBlank() && it.longitude.isNotBlank())
//                    LatLng(it.latitude.toDouble(), it.longitude.toDouble())
//                else {
//                    LatLng(0.0, 0.0)
//                }
//            }
//            tempPointList.forEach {
//                if (it.latitude != 0.0 && it.longitude != 0.0)
//                    bounds.include(it)
//            }

            aMap.addGroundOverlay(
                GroundOverlayOptions()
                    // 设置ground覆盖物的锚点比例，默认为0.5f，水平和垂直方向都居中对齐
//                .anchor(10.0f, 10.0f)
                    // 设置覆盖物的透明度，范围：0.0~1.0
                    .transparency(0.0f)
                    // 设置覆盖物的层次，zIndex值越大越在上层；
                    .zIndex(-9999f)
                    // 覆盖物图片
                    .image(BitmapDescriptorFactory.fromPath(filePath))
                    .positionFromBounds(bounds.build())
            )


        }

        /**
         * 绘制地图-瓦片图
         */
        fun setCustomMapLayerByTile(data: GuideScenicDetailBean) {

            val oriTileUrl = data.tilesMap

            if (oriTileUrl.isNullOrEmpty()) return

            //后台的瓦片图路径
            val tileOverlayOptions =
                TileOverlayOptions().tileProvider(object : UrlTileProvider(256, 256) {
                    override fun getTileUrl(x: Int, y: Int, zoom: Int): URL {
                        try {
                            // x横坐标 ，y纵坐标，zoom缩放比
                            val url = oriTileUrl + zoom + "/" + x + "_" + y + ".png";
                            logI(url)
                            return URL(url)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        return URL("")
                    }
                })

            tileOverlayOptions.diskCacheEnabled(true)
                //添加缓存路径
                .diskCacheDir(cacheDir.absolutePath + "/amap/tile")
                //瓦片图数量
                .diskCacheSize(100000)
                .memoryCacheEnabled(true)
                .memCacheSize(100000)
                //显示的优先级
                .zIndex(-9999f)

            //添加到地图
            val mtileOverlay = mBinding.mapView.getaMap().addTileOverlay(tileOverlayOptions)

        }


        if (!mCacheHandPaintedMapPath.isNullOrEmpty()) {
            setCustomMapLayer(it, mCacheHandPaintedMapPath)
        } else if (!it.handPaintedMap.isNullOrEmpty()) {
            try {
                Observable.create(ObservableOnSubscribe<File> { e ->
                    e.onNext(
                        Glide.with(this@GuideTourMapActivity)
                            .load(it.handPaintedMap)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get()
                    )
                    e.onComplete()
                }).subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribe(object : io.reactivex.Observer<File> {
                        override fun onComplete() {
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(t: File) {
                            val destFile = File(filesDir, "handPaintedMap.jpg")
                            t?.apply {
                                copy(this, destFile)
                                runOnUiThread {
                                    setCustomMapLayer(it, destFile.absolutePath)
                                }

                            }
                        }

                        override fun onError(e: Throwable) {
                        }

                    })
            } catch (e: Exception) {

            }
        } else
            if (!it.tilesMap.isNullOrEmpty())
                setCustomMapLayerByTile(it)
    }

    /**
     * 隐藏界面上的功能性view
     */
    private fun hideAllFunctionalView() {

        mBinding.llSpeakTip.isVisible = false

        mBinding.tvShowAllArea.isVisible = false

        mBinding.llExit.isVisible = false

        mBinding.llSearch.isVisible = false

        mBinding.vShowTypeMenu.isVisible = false

        mBinding.llShowTypeScenic.isVisible = false

        mBinding.llShowTypeLegacy.isVisible = false

        mBinding.llShowTypeSpot.isVisible = false

        mBinding.llShowTypeLine.isVisible = false

        mBinding.llShowTypeToilet.isVisible = false

        mBinding.llShowTypePark.isVisible = false


    }

    /**
     * 渲染界面，处理界面的显示逻辑 ，获取 景区、非遗、景点、厕所、停车场 的列表信息
     */
    private fun renderMapFunctionUiAndGetListInfo(data: GuideScenicDetailBean) {


        mBinding.flGuideLine.isVisible = false

        mLineFragment?.let {
            supportFragmentManager.beginTransaction().remove(it).commit()
            mLineFragment = null
        }


        hideAllFunctionalView()

        // 类型（1全域，2景区）
        if (data.type == 1) {
            area = true
            currentMapMode = data.type
//            mBinding.tvShowScenicList.isVisible = true
//            mBinding.llShowTypeScenic.isVisible = true
//            mBinding.llShowTypeLegacy.isVisible = true
//            mBinding.llShowTypeLine.isVisible = true
//            mBinding.llShowTypeToilet.isVisible = true
//            mBinding.llShowTypePark.isVisible = true
            mModel.getGuideResoureType(tourId ?: "", 1)
            mBinding.llSearch.isVisible = true

            if (isLinePreviewMode != true && isLineTourMode != true)
                mBinding.llShowTypeScenic.performClick()

        } else {
            currentMapMode = 2
            area = false
            mBinding.tvShowAllArea.isVisible = !allAreaTourId.isNullOrEmpty()
            mBinding.tvShowScenicList.isVisible = true
//            mBinding.llShowTypeSpot.isVisible = true
//            mBinding.llShowTypeLine.isVisible = true
//            mBinding.llShowTypeToilet.isVisible = true
//            mBinding.llShowTypePark.isVisible = true
            mModel.getGuideResoureType(tourId ?: "", 2)
            mBinding.llSearch.isVisible = true

            if (isLinePreviewMode != true && isLineTourMode != true)
                mBinding.llShowTypeSpot.performClick()

        }

        mBinding.vShowTypeMenu.isVisible = true


        if (isLinePreviewMode) {
            hideAllFunctionalView()
            mBinding.llExit.visibility = View.VISIBLE

            mBinding.llShowTypeLine.performClick()

        }

        if (isLineTourMode) {
            hideAllFunctionalView()
            mBinding.llShowTypeSpot.visibility = View.VISIBLE
            mBinding.llShowTypeToilet.visibility = View.VISIBLE
            mBinding.llShowTypePark.visibility = View.VISIBLE
            mBinding.llExit.visibility = View.VISIBLE


        }
    }


    private fun changeCamera(latLng: LatLng) {
        mBinding.mapView.getaMap().animateCamera(CameraUpdateFactory.newLatLng(latLng), 400, null)
    }


    /**
     * 在地图上渲染线路marker 和 线路 路径
     */
    private fun renderLineInfoOnMap(lineDetailList: List<GuideLineBean.Detail>) {

        mBinding.mapView.clearMarket()


        mPolyLine?.remove()

        if (!lineDetailList.isNullOrEmpty()) {

            //地图上绘制线路
            addLineInfo(lineDetailList)

            //地图上marker的操作
            lineDetailList
                .filter {
                    it.resourceType != GuideResType.CONTENT_TYPE_GUIDED_TOUR_ROUTE
                }
                .forEachWithIndex { i, el ->
                    if (el.longitude != 0.0 && el.latitude != 0.0) {
                        addMapLineMarker(i, el)
                    }
                }


            mBinding.mapView.getaMap()
                .animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            lineDetailList[0].latitude,
                            lineDetailList[0].longitude
                        ), mZoomSize
                    )
                    , 400, null
                )

        }

        // 添加当前位置标记
        addLocationMarker()


    }


    /**
     * 获取网络数据及其准备
     */
    private fun prepare2NetData() {
        mCurrentPosition = 0

        mBinding.flGuideVp.visibility = View.GONE
        mBinding.flGuideLine.visibility = View.GONE



        mBinding.llSpeakTip.visibility = View.GONE
        mGuideVpSpeakStatusEvent = null
        GuideSpeakPlayer.stop()


        mBinding.mapView.getaMap().clear()

        mModel.infoData.value?.let {
            setCustomMapBg(it)
        }


        if (lat == 0.0 || lon == 0.0) return

        val params = HashMap<String, String>()


        params["resourceType"] = mSelectType
        if (mSelectType == ResourceType.CONTENT_TYPE_VENUE && !currVenueType.isNullOrEmpty()) {
            params["type"] = currVenueType!!
        }
        params["tourId"] = tourId
        params["lon"] = lon.toString()
        params["lat"] = lat.toString()


        mModel.getGuideHomeList(params)


    }


    /**
     * 顶部讲解
     */
    private fun topSpeakLogic() {

        mBinding.llSpeakTip.isVisible = mGuideVpSpeakStatusEvent?.isPlaying ?: false

        mGuideVpSpeakStatusEvent ?: return

        val animationDrawable = mBinding.ivSpeak.background as AnimationDrawable
        if (!animationDrawable.isRunning) {
            animationDrawable.start()
        }


        mBinding.tvSpeakTip.text = """ 正在为你播放“${mGuideVpSpeakStatusEvent?.name ?: ""}”语音导览 """

        mBinding.ivCloseSpeakTip.onNoDoubleClick {
            GuideSpeakPlayer.stop()
        }

    }


    /**
     * 底部viewpager填充
     */
    private fun dealViewPager(it: List<GuideScenicListBean>) {

        mVpAdapter.setNewData(it)

        if (it.isNullOrEmpty()) {
            mModel.toast.postValue(getString(R.string.toast_no_search_info))
        } else {
            if (!backResouceId.isNullOrEmpty()) {
                var pos = mVpAdapter.getItemCurrPositionById(backResouceId!!)
                backResouceId = ""
                backResouceType = ""
                if (pos != -1) {
                    mBinding.auVpGuide.currentItem = pos
                    mCurrentPosition = pos
                }
            } else {
                mBinding.auVpGuide.currentItem = mCurrentPosition
            }
            mBinding.auVpGuide.requestLayout()
            mBinding.flGuideVp.requestLayout()
            mBinding.flGuideVp.visibility = View.VISIBLE
        }


    }


    private fun isInArea(pointList: List<LatLng>): Boolean {
        /**
         *获取地图边界
         */
        fun getBounds(pointList: List<LatLng>): LatLngBounds {

            val b: LatLngBounds.Builder = LatLngBounds.builder()

            var i = 0
            while (i in pointList.indices) {
                b.include(pointList[i])
                i++
            }

            return b.build()
        }

        return getBounds(pointList).contains(LatLng(lat, lon))
    }


    /**
     * 景区地图-线路切换
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGuideLineChangeWay(event: GuideLineFragment.GuideLineChangeWayEvent) {
        if (event.tag != eventTag) return
        mModel.guideLineData.value?.let { lineList ->
            renderLineInfoOnMap(lineList[event.pos].details)
        }
    }


    /**
     * 景区地图-线路中 的景点 点击，需要定位该景点到地图中心
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGuideLineClickSpot(event: GuideLineFragment.GuideLineClickSpotEvent) {
        if (event.tag != eventTag) return
        mModel.guideLineData.value?.let { lineList ->
            changeCamera(LatLng(event.data.latitude, event.data.longitude))
            mBinding.flGuideLine.visibility = View.GONE
        }
    }


    /**
     * 景区地图-线路-预览
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGuideLinePreview(event: GuideLineFragment.GuideLinePreviewEvent) {
        if (event.tag != eventTag) return

        mModel.guideLineData.value?.let { lineList ->

            //            val spotLatLonList = lineList.flatMap { it.details }.map { LatLng(it.latitude, it.longitude) }
//
//            if (!isInArea(spotLatLonList)) {
//                toast(getString(R.string.guide_you_are_not_in_scenic_area))
//                return@let
//            }


//
//            ARouter.getInstance()
//                .build(ARouterPath.GuideModule.GUIDE_TOUR_ACTIVITY)
//                .withString("tourId", tourId)
//                .withString("allAreaTourId", tourId)
//                .withBoolean("isLinePreviewMode", true)
//                .withBoolean("isLineTourMode", false)
//                .navigation()


            GuideTourModeActivity.apply {
                mLat = lat
                mLon = lon
                infoData = mModel.infoData.value!!
                mLineInfo = lineList[event.pos]
            }

            //todo  线路 预览
            ARouter.getInstance()
                .build(ARouterPath.GuideModule.GUIDE_TOUR_MODE_ACTIVITY)
                .withString("tourId", tourId)
                .withBoolean("isLineTourMode", false)
                .navigation()


        }
    }


    /**
     * 景区地图-线路-开始导览
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGuideLineStartTour(event: GuideLineFragment.GuideLineTourEvent) {
        if (event.tag != eventTag) return

        mModel.guideLineData.value?.let { lineList ->

            //            val spotLatLonList = lineList.flatMap { it.details }.map { LatLng(it.latitude, it.longitude) }
//
//            if (!isInArea(spotLatLonList)) {
//                toast(getString(R.string.guide_you_are_not_in_scenic_area))
//                return@let
//            }

//            ARouter.getInstance()
//                .build(ARouterPath.GuideModule.GUIDE_TOUR_ACTIVITY)
//                .withString("tourId", tourId)
//                .withString("allAreaTourId", tourId)
//                .withString("tourId", tourId)
//                .withBoolean("isLinePreviewMode", false)
//                .withBoolean("isLineTourMode", true)
//                .navigation()


            //todo  线路 导览
            GuideTourModeActivity.apply {
                mLat = lat
                mLon = lon
                infoData = mModel.infoData.value!!
                mLineInfo = lineList[event.pos]
            }

            ARouter.getInstance()
                .build(ARouterPath.GuideModule.GUIDE_TOUR_MODE_ACTIVITY)
                .withString("tourId", tourId)
                .withBoolean("isLineTourMode", true)
                .navigation()

        }
    }

    /**
     * 更换viewpager 中 fragment
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onVpNeedGoToPos(event: GuideVpChangePosEvent) {
        if (event.tag != eventTag) return
        mBinding.auVpGuide.currentItem = event.pos
    }


    /**
     * 进入景区详情
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onVpGuideIntoScenicDetail(event: GuideVpScenicFragment.GuideVpIntoScenicDetailEvent) {
//        if (event.tag != eventTag) return
//
//        if (mSelectType == GuideMapShowType.GUIDE_SHOW_TYPE_LEGACY) {
//            toast("功能开发中")
//            return
//        }
//
//        ARouter.getInstance()
//            .build(MainARouterPath.MAIN_SCENIC_DETAIL)
//            .withString("id", event.resourceId)
//            .navigation()

        MenuJumpUtils.gotoResourceDetail(event.resourceType, event.resourceId)
//        mModel.infoData.value?.let {
//            if (it.scenicId.isBlank()) {
//                toast(R.string.toast_can_not_deal_cause_by_data_error)
//                return
//            }
//            ARouter.getInstance()
//                .build(MainARouterPath.MAIN_SCENIC_DETAIL)
//                .withString("id", it.scenicId)
//                .navigation()
//
//        } ?: toast(R.string.toast_can_not_deal_cause_by_data_error)

    }


    /**
     * 进入导览
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onVpGuideTour(event: GuideVpScenicFragment.GuideVpIntoGuideTourEvent) {
        if (event.tag != eventTag) return
        if (event.tourId.isNullOrEmpty()) {
            toast(R.string.toast_can_not_deal_cause_by_data_error)
            return
        }

        ARouter.getInstance()
            .build(ARouterPath.GuideModule.GUIDE_TOUR_ACTIVITY)
            .withString("tourId", event.tourId)
            .withString("allAreaTourId", tourId)
            .navigation()

    }


    /**
     * 语音讲解状态变化
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onVpGuideSpeakStatus(event: GuideSpeakStatusEvent) {
        if (event.tag != eventTag) return
        mGuideVpSpeakStatusEvent = event
        topSpeakLogic()

        mModel.homeList.value?.let {

            var bean = it[event.pos]

            if (bean.name != event.name || bean.getAudio() != event.audioUrl) {
                try {
                    bean =
                        it.first { item -> item.name == event.name && item.getAudio() == event.audioUrl }
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@let
                }
            }

            changeMarkerStatusByPosition(bean, false)
        }
    }


    /**
     * 动态添加 线路模式下的景点  地图 线路
     */
    private fun addLineInfo(list: List<GuideLineBean.Detail>) {

        fun drawPoints(points: List<LatLng>) {
            // 获取轨迹坐标点
//            val mPathSmoothTool = PathSmoothTool()
//            //设置平滑处理的等级
//            mPathSmoothTool.intensity = 4
//            val pathOptimizeList = mPathSmoothTool.pathOptimize(points)
            //绘制轨迹，移动地图显示
            if (!points.isNullOrEmpty()) {
                mPolyLine = mBinding.mapView.getaMap()
                    ?.addPolyline(
                        PolylineOptions().addAll(points).color(resources.getColor(R.color.guide_green_6ac302)).zIndex(
                            2f
                        )
                            .width(10f)
                    )
            }
        }


        val points = list.map { LatLng(it.latitude, it.longitude) }.toList()

        drawPoints(points)

    }


    /**
     * 动态添加 线路模式下的景点  地图标记
     */
    private fun addMapLineMarker(i: Int, el: GuideLineBean.Detail) {

        val markerView =
            LayoutInflater.from(this).inflate(R.layout.guide_layout_map_line_marker, null)
        // 将活动对象传入，点击marker时可以依次判定
        val location =
            MapLocation<GuideLineBean.Detail>(
                el.latitude,
                el.longitude
            ).apply {
                t = el
                isShowInforWindow = false
            }
        val tvMarker = markerView.findViewById<TextView>(R.id.tv_marker)

        var text = ""
        if (area) {
            text = "景区"
        } else {
            text = "景点"
        }
        tvMarker.text =
            if (el.resourceType == GuideResType.CONTENT_TYPE_GUIDED_TOUR_ROUTE) "" else "$text${i + 1}"

        mBinding.mapView.addMarket(location, markerView)
    }


    private fun setMarkerInfoWindowAdapter() {

        mBinding.mapView.getaMap().setInfoWindowAdapter(object : AMap.InfoWindowAdapter {
            override fun getInfoContents(marker: Marker?): View? {
                return null
            }

            override fun getInfoWindow(marker: Marker?): View {
                val v = View.inflate(
                    this@GuideTourMapActivity,
                    R.layout.guide_layout_map_marker_info_window,
                    null
                )
                val tv = v.findViewById<TextView>(R.id.tv_marker)

                marker?.let { markerView ->
                    val bean = markerView.`object`

                    if (bean is GuideScenicListBean) {
                        tv.text = bean.name.safeSubstring(0, 12)
                    }

                }


                return v
            }
        })

    }


    /**
     * 动态添加地图标记
     */
    private fun addMapMarker(i: Int, el: GuideScenicListBean) {
        val markerView = LayoutInflater.from(this).inflate(R.layout.guide_layout_map_marker, null)
        // 将活动对象传入，点击marker时可以依次判定
        val location =
            MapLocation<GuideScenicListBean>(
                el.latitude.toDouble(),
                el.longitude.toDouble()
            ).apply {
                t = el
                isShowInforWindow = true
            }
        val imgMarker = markerView.findViewById<ImageView>(R.id.iv_marker)

        imgMarker.setImageResource(getMarkerRec(mSelectType, i, el))

        val markerOptions = MarkerOptions()
            .icon(BitmapDescriptorFactory.fromView(markerView))
            .position(LatLng(el.latitude.toDouble(), el.longitude.toDouble()))
            .setInfoWindowOffset(0, 0)
            .draggable(false)
            .zIndex(2f)
            .infoWindowEnable(
                mSelectType != ResourceType.CONTENT_TYPE_LINE
            )

        mBinding.mapView.addMarket(location, markerView, markerOptions)
    }


    /**
     * 点击页面时隐藏底部详情
     */
    override fun onMapClick(p0: LatLng?) {

        if (isLinePreviewMode) {
            mBinding.flGuideLine.visibility = View.GONE
            return
        }


        if (mSelectType == GuideMapShowType.GUIDE_SHOW_TYPE_LINE) {
            mBinding.flGuideVp.visibility = View.GONE

            mBinding.flGuideLine.visibility = if (mBinding.flGuideLine.isVisible) {
                View.GONE
            } else {
                if (mLineFragment != null)
                    View.VISIBLE
                else
                    View.GONE
            }

        } else {

            if (mBinding.flGuideVp.isVisible) {
                mBinding.flGuideVp.visibility = View.GONE
            } else {
                if (!mVpAdapter.mDataList.isNullOrEmpty())
                    mBinding.flGuideVp.visibility = View.VISIBLE
            }

        }


        if (isLineTourMode) {
            mBinding.flGuideLine.visibility = View.GONE
            return
        }
    }

    /**
     * 点击marker
     */
    override fun onMarkerClick(p0: Marker?): Boolean {
        val bean = p0!!.`object`
        if (bean is GuideScenicListBean) {
            var index = 0
            index = mVpAdapter.mDataList.indexOf(bean) ?: 0
            mBinding.flGuideVp.visibility = View.VISIBLE
            if (mCurrentPosition != index) {
                mBinding.auVpGuide.currentItem = index
                mCurrentPosition = index


                changeMarkerStatusByPosition(bean)
            }
        }
        return true
    }


    /**
     * 改变marker凸显效果
     */
    @Synchronized
    private fun changeMarkerIcon(
        marker: Marker,
        select: Boolean,
        i: Int,
        bean: GuideScenicListBean
    ) {
//        logI("changeMarkerStatusByPosition  ${bean.toString()} $select $i")
        try {
            marker.options.icon.bitmap.recycle()
            marker.title = bean.name
            marker.isDraggable = false
            if (select) {
                marker.setIcon(
                    BitmapDescriptorFactory.fromResource(
                        getSelectedMarketRec
                            (mSelectType, i, bean)
                    )
                )
                marker.showInfoWindow()
            } else
                marker.setIcon(
                    BitmapDescriptorFactory.fromResource(
                        getMarkerRec
                            (mSelectType, i, bean)
                    )
                )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 根据选中的位置，反选market
     */
    @Synchronized
    private fun changeMarkerStatusByPosition(
        p0: GuideScenicListBean,
        isNeedToLocationTo: Boolean = true
    ) {
//        logI("changeMarkerStatusByPosition  ${p0.toString()} $isNeedToLocationTo")

        val temps: List<Marker> = mBinding.mapView.mapManager.markets
        if (!temps.isNullOrEmpty()) {
            temps.forEachWithIndex { i, obj ->
                val bean = obj.`object`
                if (bean != null) {
                    if (bean is GuideScenicListBean) {
                        if (bean != p0) {
                            changeMarkerIcon(obj, false, i, bean)
                        } else {
                            changeMarkerIcon(obj, true, i, bean)
//                            if (isNeedToLocationTo)
//                            changeCamera(obj.position)
                        }
                    }
                }
            }
        }
    }


    /**
     * @param type String 类型
     *  获取记图片
     */
    private fun getMarkerRec(type: String, i: Int, bean: GuideScenicListBean): Int {
        var marketImgRec = R.drawable.guide_map_icon_scenic_normal
        when (type) {
            GuideMapShowType.GUIDE_SHOW_TYPE_SCENIC -> {
                marketImgRec = R.drawable.guide_map_icon_scenic_normal
            }

            GuideMapShowType.GUIDE_SHOW_TYPE_LEGACY -> {
                marketImgRec = R.drawable.guide_map_icon_legacy_normal
            }

            GuideMapShowType.GUIDE_SHOW_TYPE_SPOT -> {
                if (!bean.getAudio().isNullOrEmpty()) {
                    marketImgRec = R.drawable.guide_map_icon_spot_normal_voice

                    mGuideVpSpeakStatusEvent?.let {
                        val isThisMarker = bean.name == it.name && bean.getAudio() == it.audioUrl
//                        val isPlaying = GuideSpeakPlayer.isPlayingByUrl(bean.audio, bean.name, i)
//                        logI("getMarkerRec  isThisMarker- $isThisMarker   isPlaying- $isPlaying")
                        if (isThisMarker) {
//                            if (isPlaying)
                            if (it.isPlaying)
                                marketImgRec = R.drawable.guide_map_icon_spot_normal_voice_playing
                        }
                    }

                } else marketImgRec = R.drawable.guide_map_icon_spot_normal_novoice


            }

            GuideMapShowType.GUIDE_SHOW_TYPE_TOILET -> {
                marketImgRec = R.drawable.guide_map_icon_toilet_normal
            }

            GuideMapShowType.GUIDE_SHOW_TYPE_PARK -> {
                marketImgRec = R.drawable.guide_map_icon_park_normal
            }
            ResourceType.CONTENT_TYPE_SHOP_MALL -> {
                // 购物点
                marketImgRec = R.drawable.guide_map_icon_shopping_normal
            }
            ResourceType.CONTENT_TYPE_BUS_STOP -> {
                // 乘车点
                marketImgRec = R.drawable.guide_map_icon_bus_normal
            }
            ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE -> {
                // 非遗基地
                marketImgRec = R.drawable.guide_map_icon_legacy_normal
            }
            ResourceType.CONTENT_TYPE_HOTEL -> {
                // 酒店
                marketImgRec = R.drawable.guide_map_icon_hotel_normal
            }
            ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                // 农家乐
                marketImgRec = R.drawable.guide_map_icon_house_normal
            }
            ResourceType.CONTENT_TYPE_RESTAURANT -> {
                // 餐饮
                marketImgRec = R.drawable.guide_map_icon_food_normal
            }
            ResourceType.CONTENT_TYPE_ENTERTRAINMENT -> {
                // 娱乐场所
                marketImgRec = R.drawable.map_entertainment_normal
            }
            ResourceType.CONTENT_TYPE_VENUE -> {
                if (currVenueTypeMode != null) {
                    when (currVenueTypeMode) {
                        "gallery" -> { // 美术馆
                            marketImgRec = R.drawable.guide_map_icon_paint_normal
                        }
                        "cultureCenter" -> { // 文化馆
                            marketImgRec = R.drawable.guide_map_icon_culture_normal
                        }
                        "artGallery" -> {//艺术馆
                            marketImgRec = R.drawable.guide_map_icon_arts_normal
                        }
                        "library" -> { // 图书馆
                            marketImgRec = R.drawable.guide_map_icon_library_normal
                        }
                        "troupe" -> {// 剧院/剧团
                            marketImgRec = R.drawable.guide_map_icon_theatre_normal
                        }
                        "museum" -> {// 博物馆
                            marketImgRec = R.drawable.guide_map_icon_museum_normal
                        }
                        "culturalStation" -> { // 文化站/文化活动中心
                            marketImgRec = R.drawable.guide_map_icon_culture_station_normal
                        }
                        "scienicMuseum" -> {// 科技馆
                            marketImgRec = R.drawable.guide_map_icon_science_normal
                        }
                        "gymnasium" -> { //体育馆
                            marketImgRec = R.drawable.guide_map_icon_sports_normal
                        }
                        "memorial" -> {
                            marketImgRec = R.drawable.guide_map_icon_jinian_normal
                        }
                        "heritageCenter" -> {
                            marketImgRec = R.drawable.guide_map_icon_feyibaohu_normal
                        }
                    }
                }
            }
        }
        return marketImgRec
    }

    /**
     * @param type String 类型
     *  获取选中标记图片
     */
    private fun getSelectedMarketRec(type: String, i: Int, bean: GuideScenicListBean): Int {
        var marketImgRec = R.drawable.guide_map_icon_scenic_selected
        when (type) {
            ResourceType.CONTENT_TYPE_SCENERY -> {
                marketImgRec = R.drawable.guide_map_icon_scenic_selected
            }

            ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE -> {
                marketImgRec = R.drawable.guide_map_icon_legacy_selected
            }

            ResourceType.CONTENT_TYPE_SCENIC_SPOTS -> {
                if (!bean.getAudio().isNullOrEmpty()) {
                    marketImgRec = R.drawable.guide_map_icon_spot_selected_voice

                    mGuideVpSpeakStatusEvent?.let {
                        val isThisMarker = bean.name == it.name && bean.getAudio() == it.audioUrl
//                        val isPlaying = GuideSpeakPlayer.isPlayingByUrl(bean.audio, bean.name, i)
//                        logI("getSelectedMarketRec  isThisMarker- $isThisMarker   isPlaying- $isPlaying")
                        if (isThisMarker) {
//                            if (isPlaying)
                            if (it.isPlaying)
                                marketImgRec = R.drawable.guide_map_icon_spot_selected_voice_playing
                        }
                    }

                } else marketImgRec = R.drawable.guide_map_icon_spot_selected_novoice


            }

            ResourceType.CONTENT_TYPE_TOILET -> {
                marketImgRec = R.drawable.guide_map_icon_toilet_selected
            }

            ResourceType.CONTENT_TYPE_PARKING -> {
                marketImgRec = R.drawable.guide_map_icon_park_selected
            }
            ResourceType.CONTENT_TYPE_SHOP_MALL -> {
                // 购物点
                marketImgRec = R.drawable.guide_map_icon_shopping_selected
            }
            ResourceType.CONTENT_TYPE_BUS_STOP -> {
                // 乘车点
                marketImgRec = R.drawable.guide_map_icon_bus_selected
            }
            ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE -> {
                // 非遗基地
                marketImgRec = R.drawable.guide_map_icon_legacy_selected
            }
            ResourceType.CONTENT_TYPE_HOTEL -> {
                // 酒店
                marketImgRec = R.drawable.guide_map_icon_hotel_selected
            }
            ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                // 农家乐
                marketImgRec = R.drawable.guide_map_icon_house_selected
            }
            ResourceType.CONTENT_TYPE_RESTAURANT -> {
                // 餐饮
                marketImgRec = R.drawable.guide_map_icon_food_selected
            }
            ResourceType.CONTENT_TYPE_ENTERTRAINMENT -> {
                // 娱乐场所
                marketImgRec = R.drawable.map_entertainment_selected
            }
            ResourceType.CONTENT_TYPE_VENUE -> {
                if (currVenueTypeMode != null) {
                    when (currVenueTypeMode) {
                        "gallery" -> { // 美术馆
                            marketImgRec = R.drawable.guide_map_icon_paint_selected
                        }
                        "cultureCenter" -> { // 文化馆
                            marketImgRec = R.drawable.guide_map_icon_culture_selected
                        }
                        "artGallery" -> {//艺术馆
                            marketImgRec = R.drawable.guide_map_icon_arts_selected
                        }
                        "library" -> { // 图书馆
                            marketImgRec = R.drawable.guide_map_icon_library_selected
                        }
                        "troupe" -> {// 剧院/剧团
                            marketImgRec = R.drawable.guide_map_icon_theatre_selected
                        }
                        "museum" -> {// 博物馆
                            marketImgRec = R.drawable.guide_map_icon_museum_selected
                        }
                        "culturalStation" -> { // 文化站/文化活动中心
                            marketImgRec = R.drawable.guide_map_icon_culture_station_selected
                        }
                        "scienicMuseum" -> {// 科技馆
                            marketImgRec = R.drawable.guide_map_icon_science_selected
                        }
                        "gymnasium" -> { //体育馆
                            marketImgRec = R.drawable.guide_map_icon_sports_selected
                        }
                        "memorial" -> {
                            marketImgRec = R.drawable.guide_map_icon_jinian_selected
                        }
                        "heritageCenter" -> {
                            marketImgRec = R.drawable.guide_map_icon_feyibaohu_selected
                        }
                    }
                }
            }
        }
        return marketImgRec
    }


    /**
     * 定位自己当前的位置
     */
    @SuppressLint("CheckResult")
    private fun location(boolean: Boolean) {


        fun doLocation() {
            var locationString = ""
            GaoDeLocation.getInstance()
                .init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {

                    override fun onResult(
                        adCode: String, result: String, lat_: Double,
                        lon_: Double, adcode: String
                    ) {
                        lat = lat_
                        lon = lon_

                        mVpAdapter.let {
                            it.lat = lat.toString()
                            it.lng = lon.toString()
                        }

                        if (!boolean) {
                            afterLocation()
                        } else {
                            mBinding.mapView.location(lat_, lon_)
                        }

                    }

                    override fun onError(errormsg: String) {
                        ToastUtils.showMessage(errormsg)
                    }
                })
        }

        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
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
                    mModel.toast.postValue("非常抱歉，正常授权才能使用地图模式")
                }
            }
        } else {
            doLocation()
        }
    }

    /**
     * 获取到当前位置信息后
     */
    private fun afterLocation() {
        mModel.getInfo(tourId)
    }

    /**
     *  增加一个当前位置marker
     */
    @Synchronized
    private fun addLocationMarker() {
        if (lat != 0.0 && lon != 0.0) {
            val markerView =
                LayoutInflater.from(this).inflate(R.layout.guide_layout_map_marker, null)
            val imgMarker = markerView.findViewById<ImageView>(R.id.iv_marker)
            imgMarker.setImageResource(R.drawable.guide_map_icon_location)
            val location = MapLocation<ScenicBean>(lat, lon)
            mBinding.mapView.addMarket(location, markerView)
        }
    }


    /**
     * 复制文件
     *
     * @param source 输入文件
     * @param target 输出文件
     */
    private fun copy(source: File, target: File) {
        var fileInputStream: FileInputStream? = null
        var fileOutputStream: FileOutputStream? = null
        try {
            fileInputStream = FileInputStream(source);
            fileOutputStream = FileOutputStream(target);
            val buffer = ByteArray(1024)
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer)
            }
        } catch (e: Exception) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream?.close()
                fileOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    /**
     * 讲解播放 receiver
     */
    private val playReceiver by lazy {
        object : SpeakPlayReceiver() {
            override fun onStateRinging() {
                Log.d("SpeakPlayReceiver", "CALL-onStateRinging")
            }

            override fun onStateIdle() {
                Log.d("SpeakPlayReceiver", "CALL-onStateIdle")
            }

            override fun onBufferPrgUpdate(percent: Int) {
                Log.d("SpeakPlayReceiver", "BUFFER-onBufferPrgUpdate：$percent")
            }

            override fun onProgressUpdate(progress: Int) {
                Log.d("SpeakPlayReceiver", "onProgressUpdate-$progress")
            }

            override fun onPlayPrepared(duration: Int) {
                Log.d("SpeakPlayReceiver", "onPlayPrepared-$duration")
            }

            override fun onPlayCompleted() {
                Log.d("SpeakPlayReceiver", "onPlayCompleted")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SpeakPlayer.startPlayService(this, packageName)
        SpeakPlayer.registerPlayReceiver(this, playReceiver)

        try {
            mBinding.mapView.mapView?.onCreate(savedInstanceState)
            EventBus.getDefault().register(this)
        } catch (e: java.lang.Exception) {
        }

    }

    override fun onDestroy() {
        SpeakPlayer.unregisterPlayReceiver(this, playReceiver)
        SpeakPlayer.stopPlayService(this)

        try {
            EventBus.getDefault().unregister(this)
            super.onDestroy()
            mBinding.mapView.mapView?.onDestroy()
            permissions = null
            GaoDeLocation.getInstance().release()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        SpeakPlayer.notifyPause(this)
    }

    override fun onResume() {
        try {
            super.onResume()
            // 在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
            mBinding.mapView.mapView?.onResume()
        } catch (e: java.lang.Exception) {
        }
    }

    override fun onPause() {
        try {
            super.onPause()
            // 在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
            mBinding.mapView.mapView?.onPause()
            GuideSpeakPlayer.stop()
        } catch (e: java.lang.Exception) {

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {

        try {
            super.onSaveInstanceState(outState)
            // 在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
            mBinding.mapView.mapView?.onSaveInstanceState(outState)
        } catch (e: java.lang.Exception) {
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == 1002) {
            data?.let {
                backResouceType = data.getStringExtra("resouceType")
                backResouceId = data.getStringExtra("resourceId")
                backResourceVenueType = data.getStringExtra("venueTypeValue")

                if (!backResouceType.isNullOrEmpty() && !backResouceId.isNullOrEmpty()) {
                    if (mSelectType == backResouceType!!) {
                        //
                        if (mSelectType != ResourceType.CONTENT_TYPE_VENUE) {
                            var pos = mVpAdapter.getItemCurrPositionById(backResouceId!!)
                            if (pos > -1) {
                                mBinding.auVpGuide.currentItem = pos
                            }
                        } else {
                            if (!backResourceVenueType.isNullOrEmpty() && backResourceVenueType == currVenueTypeMode) {
                                var pos = mVpAdapter.getItemCurrPositionById(backResouceId!!)
                                if (pos > -1) {
                                    mBinding.auVpGuide.currentItem = pos
                                }
                            } else {
                                guideTourResourceAdapter.setSelectResouceType(backResouceType!!, backResourceVenueType)
                            }
                        }

                    } else {
                        guideTourResourceAdapter.setSelectResouceType(backResouceType!!, backResourceVenueType)
                    }
                }
            }
        }
    }

}
