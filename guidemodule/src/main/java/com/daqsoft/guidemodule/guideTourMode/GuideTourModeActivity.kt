package com.daqsoft.guidemodule.guideTourMode

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.CustomRenderer
import com.amap.api.maps.model.*
import com.amap.api.maps.utils.SpatialRelationUtil
import com.amap.api.maps.utils.overlay.MovingPointOverlay
import com.amap.api.maps.utils.overlay.SmoothMoveMarker
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.guidemodule.*
import com.daqsoft.guidemodule.GuideSpeakPlayer
import com.daqsoft.guidemodule.GuideVpAdapter
import com.daqsoft.guidemodule.GuideVpChangePosEvent
import com.daqsoft.guidemodule.bean.*
import com.daqsoft.guidemodule.fragment.GuideVpScenicFragment
import com.daqsoft.guidemodule.databinding.GuideAllAreaActivityBinding
import com.daqsoft.guidemodule.databinding.GuideGuideTourMapActivityBinding
import com.daqsoft.guidemodule.databinding.GuideGuideTourModeActivityBinding
import com.daqsoft.guidemodule.fragment.GuideLineFragment
import com.daqsoft.guidemodule.fragment.GuideVpSpotFragment
import com.daqsoft.guidemodule.net.logI
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.provider.mapview.bean.MapLocation
import com.daqsoft.provider.service.GaoDeLocation
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.time.Duration
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.log

/**
 *
 * @Description  ???????????????????????????????????????????????????
 * @ClassName   GuideTourModeActivity
 * @Author      Wongxd
 * @Time        2020/4/10 19:30
 */
@Route(path = ARouterPath.GuideModule.GUIDE_TOUR_MODE_ACTIVITY)
internal class GuideTourModeActivity : TitleBarActivity<GuideGuideTourModeActivityBinding, GuideTourModeViewModel>(), AMap.OnMapClickListener, AMap.OnMarkerClickListener {


    companion object {
        var infoData: GuideScenicDetailBean = GuideScenicDetailBean()
        var mLineInfo: GuideLineBean = GuideLineBean()
        var mLat = 0.0
        var mLon = 0.0
    }

    /**
     * ??????????????????????????? ??????
     */
    private var eventTag = javaClass.name

    /**
     * tourId
     */
    @JvmField
    @Autowired
    var tourId: String = ""


    /**
     * ????????? ??????-????????????
     */
    @JvmField
    @Autowired
    var isLineTourMode: Boolean = false


    private var mSelectType: String = GuideMapShowType.GUIDE_SHOW_TYPE_SPOT

    /**
     *  spot vp?????????
     */
    private val mVpSpotAdapter: GuideTourModeVpAdapter by lazy { GuideTourModeVpAdapter(supportFragmentManager, eventTag) }


    /**
     *  vp?????????
     */
    private val mVpAdapter: GuideVpAdapter by lazy { GuideVpAdapter(supportFragmentManager, eventTag) }


    /**
     * ??????????????? vp index
     */
    private var mCurrentPosition = 0


    /**
     * ??????????????? event
     */
    private var mGuideVpSpeakStatusEvent: GuideSpeakStatusEvent? = null


    private val lineDetailList: MutableList<GuideLineBean.Detail> = mutableListOf()

    /**
     * ????????? latlon list
     */
    private val mPointList: MutableList<LatLng> = mutableListOf()

    /**
     * ????????? latlon list,???????????????
     */
    private val mAllPointList: MutableList<LatLng> = mutableListOf()


    /**
     * ??????-????????????
     */
    private var mPolyLine: Polyline? = null


    private var mZoomSize = 13f
    /**
     * ??????????????????????????????true??????
     */
    var area = false

    override fun getLayout(): Int = R.layout.guide_guide_tour_mode_activity

    override fun setTitle(): String {
        return getString(R.string.guide_all_area_title)
    }

    override fun injectVm(): Class<GuideTourModeViewModel> {
        return GuideTourModeViewModel::class.java
    }

    override fun initData() {
        //Arouter ?????????????????????????????? null
        if (isLineTourMode == null) {
            isLineTourMode = false
        }

        mLineInfo.details.forEachWithIndex { i, detail ->
            detail.oriInPointsIndex = i
        }

        mAllPointList.addAll(mLineInfo.details.map { LatLng(it.latitude, it.longitude) })

        lineDetailList.addAll(mLineInfo.details.filter {
            it.resourceType != GuideResType.CONTENT_TYPE_GUIDED_TOUR_ROUTE
        })

        mPointList.addAll(lineDetailList.map { LatLng(it.latitude, it.longitude) })

        mZoomSize = infoData.zoom.toFloat()

        if (infoData.zoomsMax >= mZoomSize)
            mBinding.mapView.getaMap().maxZoomLevel = infoData.zoomsMax.toFloat()

        if (infoData.zoomsMin.toFloat() in 0f..mZoomSize)
            mBinding.mapView.getaMap().minZoomLevel = infoData.zoomsMin.toFloat()

        mBinding.mapView.getaMap().moveCamera(CameraUpdateFactory.zoomTo(mZoomSize))

        thisSpotSuitZoom = infoData.zoom.toFloat()

        afterGetLocation()
    }

    override fun initView() {

        eventTag = "${javaClass.name}-${System.currentTimeMillis()}"

        mBinding.ivMapPosition.setOnClickListener {
            location()
        }

        mBinding.llShowTypeMenu.isVisible = isLineTourMode == true


        // ??????marker????????????
        mBinding.mapView.mapManager.setOnMarkerClickListener(this)
        // ????????????????????????
        mBinding.mapView.mapManager.setOnMapClickListener(this)


        // VP?????????
        mBinding.auVpGuide.adapter = mVpAdapter
        mBinding.auVpGuide.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                mBinding.flGuideVp.visibility = View.VISIBLE
                if (mCurrentPosition != position) {
                    mCurrentPosition = position

                    val bean = mModel.homeList.value?.get(position) ?: return

                    changeCamera(LatLng(bean.latitude.toDouble(), bean.longitude.toDouble()))

                    mBinding.flGuideVp.postDelayed({ changeMarkerStatusByPosition(bean) }, 500)
                }

            }
        })


        // Spot VP?????????
        mBinding.auVpGuideSpot.adapter = mVpSpotAdapter
        mBinding.auVpGuideSpot.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                mBinding.flGuideVpSpot.visibility = View.VISIBLE
                if (mCurrentPosition != position) {
                    mCurrentPosition = position
                    changeCamera(mPointList[mCurrentPosition])
                }

            }
        })



        mBinding.llShowTypeSpot.setOnClickListener {
            mSelectType = GuideMapShowType.GUIDE_SHOW_TYPE_SPOT
            prepare2NetData()
        }


        mBinding.llShowTypeToilet.setOnClickListener {
            mSelectType = GuideMapShowType.GUIDE_SHOW_TYPE_TOILET
            prepare2NetData()
        }

        mBinding.llShowTypePark.setOnClickListener {
            mSelectType = GuideMapShowType.GUIDE_SHOW_TYPE_PARK
            prepare2NetData()
        }

        mBinding.llExit.setOnClickListener {
            finish()
        }



        initModel()

        mBinding.mapView.postDelayed({
            mBinding.llShowTypeSpot.performClick()
        }, 800)

        setMarkerInfoWindowAdapter()
    }


    /**
     * ??????model
     */
    private fun initModel() {

        mModel.mPresenter.value?.loading = true
        mModel.mPresenter.value?.isNeedRecyleView = false


        // ??????  ?????????
        mModel.homeList.observe(this, Observer {


            dealViewPager(it)

            //???????????????????????? marker ?????????
            mBinding.mapView.postDelayed({

                // ???????????????marker size>1 ???????????? ??????
                if (!it.isNullOrEmpty()) {


                    it.forEachWithIndex { i, el ->
                        if (!el.longitude.isNullOrEmpty() && !el.latitude.isNullOrEmpty()) {
                            addMapMarker(i, el)
                        }
                    }

                    val bean = it[mCurrentPosition]

                    changeCamera(LatLng(bean.latitude.toDouble(), bean.longitude.toDouble()))

                    mBinding.mapView.postDelayed({
                        changeMarkerStatusByPosition(bean)
                    }, 500)
                }


                // ????????????????????????
                addLocationMarker()

            }, 100)

        })

    }

    private var mCacheHandPaintedMapPath = ""

    /**
     * ?????????????????????
     */
    @SuppressLint("CheckResult")
    private fun setCustomMapBg(it: GuideScenicDetailBean) {

        /**
         * ????????????-?????????
         */
        fun setCustomMapLayer(data: GuideScenicDetailBean, filePath: String) {

            if (data.type == 1) {
                area = true
                mBinding.tourTvScenic.setText("??????")
            } else {
                area = false
                mBinding.tourTvScenic.setText("??????")
            }
            if (data.latitudeBottomLeft.isNullOrEmpty() || data.longitudeBottomLeft.isNullOrEmpty() || data.latitudeTopRight.isNullOrEmpty() || data.longitudeTopRight.isNullOrEmpty()) {
                return
            }

            mCacheHandPaintedMapPath = filePath

            val aMap = mBinding.mapView.getaMap()

            aMap.showBuildings(false)


            if (!data.latitude.isNullOrEmpty() && !data.longitude.isNullOrEmpty()) {
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(data.latitude.toDouble(), data.longitude.toDouble()), mZoomSize))
            } else {
                aMap.moveCamera(CameraUpdateFactory.zoomTo(mZoomSize))
            }

            //?????????????????????????????????????????????,???????????????????????????????????????
            val bounds = LatLngBounds.Builder()
                .include(LatLng(data.latitudeBottomLeft.toDouble(), data.longitudeBottomLeft.toDouble()))
                .include(LatLng(data.latitudeTopRight.toDouble(), data.longitudeTopRight.toDouble()))



            aMap.addGroundOverlay(
                GroundOverlayOptions()
                    //???????????????????????????????????????0.0~1.0
                    .transparency(0.0f)
                    //???????????????????????????zIndex????????????????????????
                    .zIndex(-9999f)
                    //???????????????
                    .image(BitmapDescriptorFactory.fromPath(filePath))
                    .positionFromBounds(bounds.build())
            )


        }

        /**
         * ????????????-?????????
         */
        fun setCustomMapLayerByTile(data: GuideScenicDetailBean) {

            val oriTileUrl = data.tilesMap

            if (oriTileUrl.isNullOrEmpty()) return

            //????????????????????????
            val tileOverlayOptions = TileOverlayOptions().tileProvider(object : UrlTileProvider(256, 256) {
                override fun getTileUrl(x: Int, y: Int, zoom: Int): URL {
                    try {
                        // x????????? ???y????????????zoom?????????
                        val url = oriTileUrl + zoom + "/" + x + "_" + y + ".png"
                        logI(url)
                        return URL(url)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return URL("")
                }
            })

            tileOverlayOptions.diskCacheEnabled(true)
                //??????????????????
                .diskCacheDir(cacheDir.absolutePath + "/amap/tile")
                //???????????????
                .diskCacheSize(100000)
                .memoryCacheEnabled(true)
                .memCacheSize(100000)
                //??????????????????
                .zIndex(-9999f)

            //???????????????
            mBinding.mapView.getaMap().addTileOverlay(tileOverlayOptions)

        }


        when {
            !mCacheHandPaintedMapPath.isNullOrEmpty() -> {
                setCustomMapLayer(it, mCacheHandPaintedMapPath)
            }
            !it.handPaintedMap.isNullOrEmpty() -> {

                Observable.create(ObservableOnSubscribe<File> { e ->
                    e.onNext(
                        Glide.with(this)
                            .load(it.handPaintedMap)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get()
                    )
                    e.onComplete()
                }).subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribe { t ->
                        val destFile = File(filesDir, "handPaintedMap.jpg")
                        t?.apply {
                            copy(this, destFile)
                            runOnUiThread {
                                setCustomMapLayer(it, destFile.absolutePath)
                            }
                        }
                    }

            }
            !it.tilesMap.isNullOrEmpty() -> setCustomMapLayerByTile(it)
        }

        showFirstSpotMarker()
    }

    private fun showFirstSpotMarker() {
        if (mSelectType == GuideMapShowType.GUIDE_SHOW_TYPE_SPOT) {
            mBinding.auVpGuideSpot.currentItem = 0
            mBinding.auVpGuideSpot.postDelayed({
                if(mPointList.size>0) {
                    mBinding.mapView.getaMap()
                        .animateCamera(CameraUpdateFactory.newLatLngZoom(mPointList[0], mZoomSize))
                }
            }, 600)
        }
    }


    /**
     * ????????????????????????marker ??? ?????? ??????
     */
    private fun renderLineInfoOnMap(lineDetailList: List<GuideLineBean.Detail>) {


        mPolyLine?.remove()

        passPolyline?.remove()

        if (!lineDetailList.isNullOrEmpty()) {

            //?????????????????????
            addLineInfo()

            //?????????marker?????????
            lineDetailList.forEachWithIndex { i, el ->
                if (el.longitude != 0.0 && el.latitude != 0.0) {
                    addMapLineMarker(i, el)
                }
            }

        }

        // ????????????????????????
        addLocationMarker()

        movingPointOverlay = MovingPointOverlay(
            mBinding.mapView.getaMap(),
            mBinding.mapView.getaMap().addMarker(
                MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.guide_map_location))
                    .zIndex(10f)
            )
        )
    }


    /**
     * ??????????????????????????????
     */
    private fun prepare2NetData() {
        mCurrentPosition = 0

        mBinding.flGuideVp.visibility = View.GONE
        mBinding.flGuideVpSpot.visibility = View.GONE



        mBinding.llSpeakTip.visibility = View.GONE
        mGuideVpSpeakStatusEvent = null
        GuideSpeakPlayer.stop()


        mBinding.mapView.getaMap().clear()

        setCustomMapBg(infoData)

        //??????line ????????????
        mPolyLine?.remove()

        passedPoints.clear()
        passPolyline?.remove()


        if (mSelectType == GuideMapShowType.GUIDE_SHOW_TYPE_SPOT) {

            if (mPointList.isNullOrEmpty()) return

            if (!lineDetailList.isNullOrEmpty()) {
                //viewPager
                dealSpotViewPager(lineDetailList)
            }

            //???????????????????????? marker ?????????
            mBinding.mapView.postDelayed({
                //??????
                renderLineInfoOnMap(lineDetailList)
            }, 100)


            showFirstSpotMarker()

            return
        }




        if (mLat == 0.0 || mLon == 0.0) return

        val params = HashMap<String, String>()


        params["resourceType"] = mSelectType
        params["tourId"] = tourId
        params["lat"] = mLat.toString()
        params["lon"] = mLon.toString()


        mModel.getGuideHomeList(params)


    }


    /**
     * ????????????
     */
    private fun topSpeakLogic() {

        mBinding.llSpeakTip.isVisible = mGuideVpSpeakStatusEvent?.isPlaying ?: false

        mGuideVpSpeakStatusEvent ?: return

        val animationDrawable = mBinding.ivSpeak.background as AnimationDrawable
        if (!animationDrawable.isRunning) {
            animationDrawable.start()
        }


        mBinding.tvSpeakTip.text = """ ?????????????????????${mGuideVpSpeakStatusEvent?.name ?: ""}??????????????? """

        mBinding.ivCloseSpeakTip.onNoDoubleClick {
            GuideSpeakPlayer.stop()
        }

    }

    /**
     * ??????viewpager??????
     */
    private fun dealSpotViewPager(it: List<GuideLineBean.Detail>) {
        mCurrentPosition = 0

        mVpSpotAdapter.setNewData(it.filter { it.resourceType != GuideResType.CONTENT_TYPE_GUIDED_TOUR_ROUTE })

        if (it.isNullOrEmpty()) {
            mModel.toast.postValue(getString(R.string.toast_no_search_info))
        } else {

            logI("dealSpotViewPager- mBinding.auVpGuideSpot")

            mBinding.flGuideVpSpot.visibility = View.VISIBLE
            mBinding.flGuideVp.visibility = View.GONE


            mBinding.auVpGuideSpot.currentItem = mCurrentPosition

        }


    }


    /**
     * ??????viewpager??????
     */
    private fun dealViewPager(it: List<GuideScenicListBean>) {
        mCurrentPosition = 0

        mVpAdapter.setNewData(it)

        if (it.isNullOrEmpty()) {
            mModel.toast.postValue(getString(R.string.toast_no_search_info))
        } else {

            mBinding.flGuideVp.visibility = View.VISIBLE
            mBinding.flGuideVpSpot.visibility = View.GONE

            mBinding.auVpGuide.currentItem = mCurrentPosition

        }


    }


    /**
     * ??????viewpager ??? fragment
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onVpNeedGoToPos(event: GuideVpChangePosEvent) {
        if (event.tag != eventTag) return
        if (mSelectType == GuideMapShowType.GUIDE_SHOW_TYPE_SPOT)
            mBinding.auVpGuideSpot.currentItem = event.pos
        else
            mBinding.auVpGuide.currentItem = event.pos
    }

    /**
     * ?????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNeedToNextSpot(event: GuideTourNextSpotEvent) {
        if (event.tag != eventTag) return
        if (mSelectType == GuideMapShowType.GUIDE_SHOW_TYPE_SPOT) {

            mCurrentPosition = event.pos + 1

            mBinding.auVpGuideSpot.currentItem = mCurrentPosition


            animationDaoListIndex = 0
            animationDaoList.clear()
            isAnimationRun = false

            val lastTarget = event.oriInPointsIndex
            val target = lineDetailList[mCurrentPosition].oriInPointsIndex
            val subList = mAllPointList.subList(lastTarget, target + 1)

            var lastItem: LatLng? = null
            subList.forEachWithIndex { i, latLng ->
                lastItem?.let {
                    animationDaoList.add(listOf(it, latLng))
                }
                lastItem = latLng
            }

            passPolyline?.remove()
            animationHandler.sendEmptyMessage(1)
        }
    }


    //marker animation


    private val animationHandler by lazy {
        @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                msg?.let {
                    when (it.what) {
                        1 -> {
                            if (animationDaoListIndex < animationDaoList.size)
                                doAnimationEachSpot(animationDaoList[animationDaoListIndex], eachSpotAnimationTime * 1000)
                        }

                        0 -> {
                            removeCallbacksAndMessages(null)
                            animationDaoListIndex = 0
                            animationDaoList.clear()
                            isAnimationRun = false
                        }
                    }
                }

            }
        }
    }

    private var animationDaoListIndex = 0
    private val animationDaoList: MutableList<List<LatLng>> = mutableListOf()
    private var thisSpotSuitZoom = 13f
    private lateinit var movingPointOverlay: MovingPointOverlay
    private val passedPoints = mutableListOf<LatLng>()
    private var passPolyline: Polyline? = null
    private var isAnimationRun = false
    /**
     * ???????????? ????????????
     */
    private var eachSpotAnimationTime = 3


    private fun doAnimationEachSpot(latLngList: List<LatLng>, duration: Int) {

        fun initMapAnimationLayerEachSpot(latLngList: List<LatLng>) {

            mBinding.mapView.getaMap().setCustomRenderer(object : CustomRenderer {
                override fun OnMapReferencechanged() {
                }

                override fun onDrawFrame(gl: GL10?) {

                    if (isAnimationRun) {

                        passedPoints.add(movingPointOverlay.position)
                        passPolyline = mBinding.mapView.getaMap().addPolyline(
                            PolylineOptions()
                                .addAll(passedPoints)
                                .color(Color.RED)
                                .zIndex(3f)
                                .width(10F)
                        )

                    }
                }

                override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
                }

                override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                }
            })

            movingPointOverlay.setPoints(latLngList)
            movingPointOverlay.setTotalDuration(eachSpotAnimationTime)

            movingPointOverlay.setMoveListener {
                if (it == 0.0) {
                    logI("movingPointOverlay  $it")
                    animationDaoListIndex++
                    if (animationDaoListIndex > animationDaoList.lastIndex) {
                        animationHandler.sendEmptyMessage(0)
                    } else {
                        animationHandler.sendEmptyMessage(1)
                    }
                }
            }

        }


        passedPoints.clear()

        initMapAnimationLayerEachSpot(latLngList)

        mBinding.mapView.getaMap().moveCamera(CameraUpdateFactory.changeLatLng(latLngList.first()))

        isAnimationRun = true

        movingPointOverlay.startSmoothMove()


        mBinding.mapView.getaMap().animateCamera(CameraUpdateFactory.changeLatLng(latLngList.last()), duration.toLong(), null)

    }

    //end marker animation


    /**
     * ????????????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onVpGuideSpeakStatus(event: GuideSpeakStatusEvent) {
        if (event.tag != eventTag) return
        mGuideVpSpeakStatusEvent = event
        topSpeakLogic()
    }


    /**
     * ???????????? ????????????????????????  ?????? ??????
     */
    private fun addLineInfo() {

        fun drawPoints(points: List<LatLng>) {
            // ?????????????????????
//            val mPathSmoothTool = PathSmoothTool()
//            //???????????????????????????
//            mPathSmoothTool.intensity = 4
//            val pathOptimizeList = mPathSmoothTool.pathOptimize(points)
            //?????????????????????????????????
            if (!points.isNullOrEmpty()) {
                mPolyLine = mBinding.mapView.getaMap()
                    ?.addPolyline(
                        PolylineOptions().addAll(points).color(resources.getColor(R.color.guide_green_6ac302)).zIndex(2f)
                            .width(10f)
                    )
            }
        }

        drawPoints(mAllPointList)

    }

    private fun setMarkerInfoWindowAdapter() {

        mBinding.mapView.getaMap().setInfoWindowAdapter(object : AMap.InfoWindowAdapter {
            override fun getInfoContents(marker: Marker?): View? {
                return null
            }

            override fun getInfoWindow(marker: Marker?): View {
                val v = View.inflate(this@GuideTourModeActivity, R.layout.guide_layout_map_marker_info_window, null)
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
     * ???????????? ????????????????????????  ????????????
     */
    private fun addMapLineMarker(i: Int, el: GuideLineBean.Detail) {

        val markerView = LayoutInflater.from(this).inflate(R.layout.guide_layout_map_line_marker, null)
        // ??????????????????????????????marker?????????????????????
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
            text = "??????"
        } else {
            text = "??????"
        }
        tvMarker.text = "$text${i + 1}"

        mBinding.mapView.addMarket(location, markerView)
    }


    /**
     * ????????????????????????
     */
    private fun addMapMarker(i: Int, el: GuideScenicListBean) {
        val markerView = LayoutInflater.from(this).inflate(R.layout.guide_layout_map_marker, null)
        // ??????????????????????????????marker?????????????????????
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
                mSelectType == GuideMapShowType.GUIDE_SHOW_TYPE_SCENIC
                        || mSelectType == GuideMapShowType.GUIDE_SHOW_TYPE_LEGACY
                        || mSelectType == GuideMapShowType.GUIDE_SHOW_TYPE_SPOT
            )

        mBinding.mapView.addMarket(location, markerView, markerOptions)
    }


    /**
     * ?????????????????????????????????
     */
    override fun onMapClick(p0: LatLng?) {
//        logI("onMapClick ${mBinding.flGuideVp.isVisible}")
        if (mSelectType == GuideMapShowType.GUIDE_SHOW_TYPE_SPOT) {
            if (mBinding.flGuideVpSpot.isVisible) {
                mBinding.flGuideVpSpot.visibility = View.GONE
            } else {
                mBinding.flGuideVpSpot.visibility = View.VISIBLE
            }
        } else {
            if (mBinding.flGuideVp.isVisible) {
                mBinding.flGuideVp.visibility = View.GONE
            } else {
                mBinding.flGuideVp.visibility = View.VISIBLE
            }
        }
    }

    /**
     * ??????marker
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
     * ??????marker????????????
     */
    @Synchronized
    private fun changeMarkerIcon(marker: Marker, select: Boolean, i: Int, bean: GuideScenicListBean) {
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
     * ??????????????????????????????market
     */
    @Synchronized
    private fun changeMarkerStatusByPosition(p0: GuideScenicListBean, isNeedToLocationTo: Boolean = true) {
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
                            if (isNeedToLocationTo)
                                mBinding.mapView.location(obj.position)
                        }
                    }
                }
            }
        }
    }


    /**
     * @param type String ??????
     *  ???????????????
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

        }
        return marketImgRec
    }

    /**
     * @param type String ??????
     *  ????????????????????????
     */
    private fun getSelectedMarketRec(type: String, i: Int, bean: GuideScenicListBean): Int {
        var marketImgRec = R.drawable.guide_map_icon_scenic_selected
        when (type) {
            GuideMapShowType.GUIDE_SHOW_TYPE_SCENIC -> {
                marketImgRec = R.drawable.guide_map_icon_scenic_selected
            }

            GuideMapShowType.GUIDE_SHOW_TYPE_LEGACY -> {
                marketImgRec = R.drawable.guide_map_icon_legacy_selected
            }

            GuideMapShowType.GUIDE_SHOW_TYPE_SPOT -> {

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

            GuideMapShowType.GUIDE_SHOW_TYPE_TOILET -> {
                marketImgRec = R.drawable.guide_map_icon_toilet_selected
            }

            GuideMapShowType.GUIDE_SHOW_TYPE_PARK -> {
                marketImgRec = R.drawable.guide_map_icon_park_selected
            }
        }
        return marketImgRec
    }


    private fun changeCamera(latLng: LatLng) {
        mBinding.mapView.getaMap().animateCamera(CameraUpdateFactory.newLatLng(latLng), 400, null)
    }

    /**
     * ???????????????????????????
     */
    private fun location() {
        GaoDeLocation().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {

            override fun onResult(
                adCode: String, result: String, lat_: Double,
                lon_: Double, adcode: String
            ) {
                mLat = lat_
                mLon = lon_

                afterGetLocation()

            }

            override fun onError(errormsg: String) {
                ToastUtils.showMessage(errormsg)
            }
        })
    }

    private fun afterGetLocation() {
        mVpAdapter.let {
            it.lat = mLat.toString()
            it.lng = mLon.toString()
        }

        mVpSpotAdapter.let {
            it.lat = mLat.toString()
            it.lng = mLon.toString()
        }

    }

    /**
     *  ????????????????????????marker
     */
    @Synchronized
    private fun addLocationMarker() {
        if (mLat != 0.0 && mLon != 0.0) {
            val markerView = LayoutInflater.from(this).inflate(
                R.layout.guide_layout_map_marker,
                null
            )
            val imgMarker = markerView.findViewById<ImageView>(R.id.iv_marker)
            imgMarker.setImageResource(R.drawable.guide_map_icon_location)
            val location = MapLocation<ScenicBean>(mLat, mLon)
            mBinding.mapView.addMarket(location, markerView)
            logI("${eventTag}--addLocationMarker--")
        }
    }


    /**
     * ????????????
     *
     * @param source ????????????
     * @param target ????????????
     */
    private fun copy(source: File, target: File) {
        var fileInputStream: FileInputStream? = null
        var fileOutputStream: FileOutputStream? = null
        try {
            fileInputStream = FileInputStream(source)
            fileOutputStream = FileOutputStream(target)
            val buffer = ByteArray(1024)
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fileInputStream?.close()
                fileOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            mBinding.mapView.mapView?.onCreate(savedInstanceState)
            EventBus.getDefault().register(this)
        } catch (e: java.lang.Exception) {
        }

    }

    override fun onDestroy() {

        try {
            EventBus.getDefault().unregister(this)
            super.onDestroy()
            mBinding.mapView.mapView?.onDestroy()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        try {
            super.onResume()
            // ???activity??????onResume?????????mMapView.onResume ()???????????????????????????
            mBinding.mapView.mapView?.onResume()

            if (isAnimationRun) {
                movingPointOverlay.startSmoothMove()
            }
        } catch (e: java.lang.Exception) {
        }
    }

    override fun onPause() {
        try {
            super.onPause()
            // ???activity??????onPause?????????mMapView.onPause ()????????????????????????
            mBinding.mapView.mapView?.onPause()
            GuideSpeakPlayer.stop()
        } catch (e: java.lang.Exception) {

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {

        try {
            super.onSaveInstanceState(outState)
            // ???activity??????onSaveInstanceState?????????mMapView.onSaveInstanceState (outState)??????????????????????????????
            mBinding.mapView.mapView?.onSaveInstanceState(outState)
        } catch (e: java.lang.Exception) {
        }

    }


}
