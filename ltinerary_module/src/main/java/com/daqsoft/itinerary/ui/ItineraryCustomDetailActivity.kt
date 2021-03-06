package com.daqsoft.itinerary.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.collection.ArrayMap
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.AMap
import com.amap.api.maps.model.*
import com.amap.api.navi.*
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.itinerary.ConfigInfo
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.adapter.ItineraryAgendaAdapter
import com.daqsoft.itinerary.bean.ItineraryDetailBean
import com.daqsoft.itinerary.bean.NearbyBean
import com.daqsoft.itinerary.bean.ItineraryDetailBean.AgendaBean.PlansBean
import com.daqsoft.itinerary.databinding.ActivityCustomDetailBinding
import com.daqsoft.itinerary.interfa.OnItemAssetsOperateListener
import com.daqsoft.itinerary.vm.ItineraryViewModel
import com.daqsoft.itinerary.widget.DividerItemDecoration
import com.daqsoft.itinerary.widget.NearbyPopupWindow
import com.daqsoft.provider.*
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import kotlinx.android.synthetic.main.itinerary_map_marker.view.*
import java.lang.Exception
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author???      ?????????
 * @Create by???   2020/5/13 16:43
 * @Description??? ??????????????????
 */
@Route(path = ItineraryRouter.ITINERARY_DETAIL)
class ItineraryCustomDetailActivity :
    TitleBarActivity<ActivityCustomDetailBinding, ItineraryViewModel>() {

    @JvmField
    @Autowired(name = "itineraryId")
    var itineraryId: String = ""

    @JvmField
    @Autowired(name = "tagType")
    var tagType: String = ""

    /**
     * ????????????
     */
    @JvmField
    @Autowired
    var travelMode: String = ""
    /**
     * ????????????
     */
    var sharePopWindow: SharePopWindow? = null

    private lateinit var aMap: AMap

    private var detail: ItineraryDetailBean? = null

    private val agendaAdapter: ItineraryAgendaAdapter by lazy {
        ItineraryAgendaAdapter()
    }

    private val nearbyWindow: NearbyPopupWindow by lazy {
        NearbyPopupWindow(this)
    }

    override fun setTitle(): String = "????????????"

    override fun getLayout(): Int = R.layout.activity_custom_detail

    override fun injectVm(): Class<ItineraryViewModel> = ItineraryViewModel::class.java

    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
            detail?.let {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this)
                }
                var content= Constant.SHARE_DEC
                // ??????????????????
                sharePopWindow?.setShareContent(
                    it.name, content, "",
                    ShareModel.getZnXcDesc(itineraryId)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }
            }
        })
    }
    override fun initView() {


        aMap = mBinding.mapView.map.apply {
            uiSettings.apply {
                isZoomControlsEnabled = false
                isMyLocationButtonEnabled = false
                isScaleControlsEnabled = false
//                setAllGesturesEnabled(false)
            }
        }
        try {
            mBinding.mapView.getChildAt(0)?.setOnTouchListener { p0, p1 ->
                if (p1?.getAction() == MotionEvent.ACTION_UP) {
                    mBinding.nsvCustomDetail.requestDisallowInterceptTouchEvent(false);
                } else {
                    mBinding.nsvCustomDetail.requestDisallowInterceptTouchEvent(true);
                }
                false;
            }
        } catch (e: Exception) {
        }


        mBinding.agendaListView.apply {
            adapter = agendaAdapter.apply {
                emptyViewShow = false
                setOnListener(adapterListener)
                setItineraryType(tagType)
            }
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    1,
                    resources.getDimensionPixelSize(R.dimen.dp_15)
                )
            )
        }

        nearbyWindow.setOnPopupWindowListener(popupListener)
    }

    /**????????????????????????*/
    private fun addMyItinerary(startDate: String) {
        mModel.addMyItinerary(itineraryId, startDate).observe(this, Observer {
            itineraryId = it
            mBinding.tagLayout.removeAllViews()
            mModel.getItineraryDetail(itineraryId)
            mBinding.adjustView.text = "????????????"
            tagType = ItineraryActivity.CLIENT
            agendaAdapter.setItineraryType(tagType)
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initData() {
        showLoadingDialog()
        mModel.getItineraryDetail(itineraryId)

        mModel.refresh.observe(this, Observer {
            dissMissLoadingDialog()
            if (it == 0) {
                showLoadingDialog()
                mModel.getItineraryDetail(itineraryId)
            }
        })

        mModel.itineraryDetail.observe(this, Observer {
            agendaAdapter.setMySelfFlag(it.isMyselfFlag)

            dissMissLoadingDialog()
            if (it == null) {
                return@Observer
            }
            initMapView(it)
            val sb = StringBuilder()
            if (it.isMyselfFlag) {
                mBinding.adjustView.text = "????????????"
            }
            sb.append("????????????${it.processDay}???")
            sb.append(", ???%s??????".format(it.travelCityName))

            if (it.processStart.isNotEmpty() && it.processEnd.isNotEmpty()) {
                if (it.processStart == it.processEnd) {
                    sb.append(",${it.processStart.replace("-", ".")} ")
                } else {
                    sb.append(
                        ",${it.processStart.replace("-", ".")}-${it.processEnd.replace(
                            "-",
                            "."
                        )}"
                    )
                }
            }

            //??????
            var dist = 0.00
            it.dayList.forEach { day ->
                dist += Utils.toKm(day.distance.toDouble())
            }
            if (dist > 0) {
                val df = DecimalFormat("0.00")
                sb.append(",??????${df.format(dist)}??????")
            }

            sb.append("\n${it.regionCount}????????? ?? ${it.scenicCount}?????????")
            if (it.hotelCount > 0) {
                sb.append(" ?? ${it.hotelCount}?????????")
            }
            if (it.diningCount > 0) {
                sb.append(" ?? ${it.diningCount}?????????")
            }
            if (it.venueCount > 0) {
                sb.append(" ?? ${it.venueCount}????????????")
            }

            //???????????????????????????
            mBinding.tagLayout.removeAllViews()
            if (it.personalTagsNames.isNotEmpty()) {
                mBinding.tagLayout.visibility = View.VISIBLE
                val tags = it.personalTagsNames.split(",")
                for (tag in tags) {
                    val tagView = TextView(
                        ContextThemeWrapper(
                            mBinding.root.context,
                            R.style.ItineraryLabel
                        )
                    ).apply {
                        text = tag
                        setTextColor(ResourceUtils.getColor(this, R.color.color_36CD64))
                        setBackgroundResource(R.drawable.itinerary_shape_stroke_green)
                    }
                    mBinding.tagLayout.addView(tagView)
                }
            }

            //???????????????????????????
            if (it.fitTagsNames.isNotEmpty()) {
                mBinding.tagLayout.visibility = View.VISIBLE
                val tags = it.fitTagsNames.split(",")
                for (tag in tags) {
                    val tagView = TextView(
                        ContextThemeWrapper(
                            mBinding.root.context,
                            R.style.ItineraryLabel
                        )
                    ).apply {
                        text = tag
                        setBackgroundResource(R.drawable.itinerary_shape_orange)
                    }
                    mBinding.tagLayout.addView(tagView)
                }
            }

            mBinding.titleView.text = it.name
            if (sb.isBlank()) {
                mBinding.whereView.visibility = View.GONE
            } else {
                mBinding.whereView.visibility = View.VISIBLE
                mBinding.whereView.text = sb.toString()
            }
            mBinding.agendaListView.smoothScrollToPosition(0)
            agendaAdapter.clearData()
            agendaAdapter.clear()
            agendaAdapter.setTravelType(it.travelType)
            var dayList: MutableList<ItineraryDetailBean.AgendaBean> = mutableListOf()
            if (!it.dayList.isNullOrEmpty()) {
                for (item in it.dayList) {
                    if (!item.sourceList.isNullOrEmpty()) {
                        dayList.add(item)
                    }
                }
            }
            agendaAdapter.setNewData(dayList)
            agendaAdapter.setItineraryType(tagType)
            travelMode = it.travelType

        })

        //?????? ?????????
        mModel.nearbyList.observe(this, Observer {
            dissMissLoadingDialog()
            nearbyWindow.showAtLocation(mBinding.adjustView, it)
        })
    }


    /**???????????????????????????*/
    private val adapterListener = object : OnItemAssetsOperateListener {
        /**????????????*/
        override fun onAdd(plan: PlansBean) {
            ARouter.getInstance()
                .build(ItineraryRouter.ITINERARY_ADD_SCENIC)
                .withString("dayId", plan.dayId.toString())
                .withInt("dataId", plan.id)
                .withString("itineraryId", itineraryId)
                .navigation(this@ItineraryCustomDetailActivity, 12)
        }

        /**???????????????*/
        override fun nearby(title: String, plan: PlansBean) {
            showLoadingDialog()
            nearbyWindow.setTitle(title)
            nearbyWindow.setIds(plan.dayId, plan.id)
            nearbyWindow.setStartlatLng(LatLng(plan.latitude.toDouble(), plan.longitude.toDouble()))
            if (title == "????????????") {
                nearbyWindow.setShowType(Constants.TYPE_RESTAURANT)
                mModel.getDinerList(plan, 1)
            } else {
                nearbyWindow.setShowType(Constants.TYPE_HOTEL)
                mModel.getHotelList(plan, 1)
            }
        }

        override fun onDelete(plan: PlansBean) {
            val paramList = ArrayList<Map<String, Any>>()

            val jsonParams = ArrayMap<String, Any>()
            jsonParams["dayId"] = plan.dayId
            jsonParams["journeyId"] = itineraryId
            jsonParams["operateType"] = 0

            val params = ArrayMap<String, Any>()
            params["id"] = plan.id
            params["resourceType"] = "CONTENT_TYPE_RESTAURANT"
            params["sourceId"] = plan.id
            params["type"] = "JOURNEY_SOURCE"
            paramList.add(params)

            jsonParams["params"] = paramList
            mModel.operateSource(jsonParams)
        }

        /**??????*/
        override fun onNavi(start: PlansBean, end: PlansBean) {
            MapNaviUtils.openGaoDeNavi(
                getContext(),
                LatLng(start.latitude.toDouble(), start.longitude.toDouble()),
                LatLng(end.latitude.toDouble(), end.longitude.toDouble()), start.name, end.name
            )
        }

        /**??????????????????*/
        override fun onMoreTraffic(start: PlansBean, end: PlansBean) {
            ARouter.getInstance()
                .build(ItineraryRouter.TRAFFIC_LIST)
                .withInt("startSite", start.id)
                .withInt("endSite", end.id)
                .withString("travelMode", travelMode)
                .navigation()
        }

        /**????????????*/
        override fun onDetail(plan: PlansBean) {
            //????????????????????????
            var path = MainARouterPath.MAIN_SCENIC_DETAIL
            if (plan.resourceType == Constants.TYPE_VENUE) {
                //?????????????????????
                path = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY
            }
            ARouter.getInstance()
                .build(path)
                .withString("id", plan.resourceId.toString())
                .navigation()
        }

        /**???????????????*/
        override fun nearbyBooking(plan: PlansBean) {
            mModel.queryTicket(ConfigInfo.SITE_CODE, plan.resourceCode)
        }
    }

    private val popupListener = object : NearbyPopupWindow.OnPopupWindowListener {
        override fun call(dayId: Int, type: String, data: NearbyBean) {
            showLoadingDialog()
            val paramList = ArrayList<Map<String, Any>>()

            val jsonParams = ArrayMap<String, Any>()
            jsonParams["dayId"] = dayId
            jsonParams["journeyId"] = itineraryId
            jsonParams["operateType"] = 1

            val params = ArrayMap<String, Any>()
            params["id"] = data.id
            params["resourceType"] = type
            params["sourceId"] = data.sourceId
            params["type"] = "JOURNEY_SOURCE"
            paramList.add(params)

            jsonParams["params"] = paramList
            mModel.operateSource(jsonParams)
        }
    }

    /**?????????????????????*/
    private val timePickerView by lazy {
        TimePickerBuilder(this, OnTimeSelectListener { date, view ->
            //????????????
            val startDate = Utils.getDateTime(Utils.dateYMD, date)
            //????????????
            val currentDate = Utils.getDateTime(Utils.dateYMD, Date(System.currentTimeMillis()))
            if (Utils.dateCompare(startDate, currentDate) <= 0) {
                ToastUtils.showMessage("??????????????????????????????")
                return@OnTimeSelectListener
            }
            showLoadingDialog()
            addMyItinerary(startDate)
        }).setTitleText("????????????")
            .setTitleColor(ResourceUtils.getColor(this, R.color.color_FF9E05))
            .build()
    }

    /**??????????????? */
    private fun initMapView(itinerary: ItineraryDetailBean) {
        bootom(itinerary)

        detail = itinerary
        aMap.clear()
        if (!itinerary.dayList.isNullOrEmpty()) {
            var count = 0
            //??????marker
            val markers = ArrayList<MarkerOptions>()
            val latLngs = ArrayList<LatLng>()
            //????????????????????????,???????????????marker
            for (age in itinerary.dayList) {
                if (!age.sourceList.isNullOrEmpty()) {
                    for (index in 0 until age.sourceList.size) {
                        count++
                        val la = age.sourceList[index]
                        val latlng = LatLng(la.latitude.toDouble(), la.longitude.toDouble())
                        latLngs.add(latlng)
                        val markerOption = MarkerOptions().apply {
                            position(latlng)
                            draggable(false)
                            visible(true)
                            isFlat = true
                            icon(BitmapDescriptorFactory.fromBitmap(getBitmap("$count")))
                        }
                        markers.add(markerOption)
                    }
                }
            }
            aMap.addMarkers(markers, true)
            aMap.addPolyline(PolylineOptions().addAll(latLngs).color(Color.parseColor("#1995ff")))
        }
    }

    private fun getBitmap(text: String): Bitmap {
        val view = LayoutInflater.from(this).inflate(R.layout.itinerary_map_marker, null)
        view.number_view.text = text
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(
            0,
            0,
            resources.getDimensionPixelSize(R.dimen.dp_21),
            resources.getDimensionPixelSize(R.dimen.dp_29)
        )
        view.buildDrawingCache()
        return view.getDrawingCache()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12 && resultCode == Activity.RESULT_OK) {
            //????????????
            showLoadingDialog()
            mBinding.tagLayout.removeAllViews()
            mModel.getItineraryDetail(itineraryId)
            agendaAdapter.setChange(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.mapView.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        mBinding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mBinding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.mapView.onDestroy()
        nearbyWindow.setOnPopupWindowListener(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mBinding.mapView.onSaveInstanceState(outState)
    }


    fun bootom(detail: ItineraryDetailBean) {
        //????????????
        mBinding.adjustView.apply {
            text = if ((tagType == ItineraryActivity.SYSTEM && detail.isMyselfFlag) || tagType == ItineraryActivity.CLIENT) {
                "????????????"
            } else {
                "?????????????????????"
            }
            setOnClickListener {
                if ((tagType == ItineraryActivity.SYSTEM && detail.isMyselfFlag) || tagType == ItineraryActivity.CLIENT) {
                    detail?.let { detai ->
                        ARouter.getInstance()
                            .build(ItineraryRouter.ITINERARY_ADJUST)
                            .withParcelable("detail", detai)
                            .navigation(this@ItineraryCustomDetailActivity, 12)
                    }
                } else {
                    UIHelperUtils.showOptionsPicker(
                        this@ItineraryCustomDetailActivity,
                        timePickerView
                    )
                }
            }
        }
    }
}