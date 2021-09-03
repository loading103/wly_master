package com.daqsoft.itinerary.adapter

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.bean.ItineraryDetailBean
import com.daqsoft.itinerary.bean.Traffbean
import com.daqsoft.itinerary.databinding.ItineraryItemAgendaBinding
import com.daqsoft.itinerary.databinding.ItineraryItemPlansBinding
import com.daqsoft.itinerary.interfa.OnItemAssetsOperateListener
import com.daqsoft.itinerary.repository.ItineraryRepository
import com.daqsoft.itinerary.ui.ItineraryActivity
import com.daqsoft.itinerary.widget.DividerItemDecoration
import com.daqsoft.provider.*
import kotlinx.coroutines.channels.consumesAll
import java.lang.Exception
import java.lang.StringBuilder
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.Constants
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.ZARouterPath
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author：      邓益千
 * @Create by：   2020/5/13 21:41
 * @Description： 日程
 */
class ItineraryAgendaAdapter :
    RecyclerViewAdapter<ItineraryItemAgendaBinding, ItineraryDetailBean.AgendaBean> {

    /**行程类型，系统推荐或我的行程*/
    private var itineraryType = ""

    /**出游类型*/
    private var travelType = ""

    private var change = false

    private var myOwn = false

    private var myselfFlag = false

    fun setMySelfFlag(booblean: Boolean){
        this.myselfFlag = booblean
    }

    fun setChange(booblean:Boolean){
        this.change = booblean
    }

    constructor() : super(R.layout.itinerary_item_agenda)

    private var listener: OnItemAssetsOperateListener? = null

    private val viewList: MutableList<RecyclerView> by lazy {
        ArrayList<RecyclerView>()
    }

    private val adapterList: ArrayList<PlansAdapter> by lazy {
        ArrayList<PlansAdapter>()
    }

    fun setOnListener(listener: OnItemAssetsOperateListener) {
        this.listener = listener
    }

    fun clearData() {
        adapterList.clear()
    }

    fun setItineraryType(type: String) {
        this.itineraryType = type
    }

    fun setTravelType(type: String) {
        this.travelType = type
    }

    override fun setVariable(
        mBinding: ItineraryItemAgendaBinding,
        position: Int,
        item: ItineraryDetailBean.AgendaBean
    ) {
            val plansAdapter = PlansAdapter(item.id)
            adapterList.add(plansAdapter)
            plansAdapter.emptyViewShow = false
            mBinding.titleView.text = "D${position + 1}"
            mBinding.contentView.text =
                "${item.time.replace("-", ".")}\t${item.title}"

            mBinding.plansListView.apply {
                visibility = if (change) View.VISIBLE else View.GONE
                mBinding.arrowView.setImageResource(R.drawable.itinerary_vector_arrow_down)
                adapter = plansAdapter
                plansAdapter.setParentView(this, mBinding.arrowView)
                if (!viewList.contains(mBinding.plansListView)) {
                    addItemDecoration(
                        DividerItemDecoration(
                            1,
                            resources.getDimensionPixelSize(R.dimen.dp_15)
                        )
                    )
                    viewList.add(mBinding.plansListView)
                }
            }
            if (position == 0) {
                mBinding.arrowView.setImageResource(R.drawable.itinerary_vector_arrow_up)
                mBinding.plansListView.visibility = View.VISIBLE
            } else {
                mBinding.arrowView.setImageResource(R.drawable.itinerary_vector_arrow_down)
                mBinding.plansListView.visibility = View.GONE
            }

            mBinding.arrowView.setOnClickListener {
                if (mBinding.plansListView.isShown) {
                    mBinding.plansListView.visibility = View.GONE
                    mBinding.arrowView.setImageResource(R.drawable.itinerary_vector_arrow_down)
                } else {
                    mBinding.arrowView.setImageResource(R.drawable.itinerary_vector_arrow_up)
                    mBinding.plansListView.visibility = View.VISIBLE
                }
            }
            plansAdapter.add(item.sourceList)
    }


    /**
     * @Author：      邓益千
     * @Create by：   2020/5/13 21:41
     * @Description： 计划
     */
    inner class PlansAdapter :
        RecyclerViewAdapter<ItineraryItemPlansBinding, ItineraryDetailBean.AgendaBean.PlansBean> {

        /**是否有住宿*/
        private var isContainHotel = false

        /**是否有餐厅*/
        private var restaurantTotal = 0

        /**某一天的ID*/
        private var dayId = 0

        /**景区 和 场馆数量*/
        private var total = 0

        private var arrowView: ImageView? = null
        private var parentView: View? = null

        fun setParentView(view: View, arrowView: ImageView) {
            this.parentView = view
            this.arrowView = arrowView
        }

        /**
         * @param dayId 天 ID
         */
        constructor(dayId: Int) : super(R.layout.itinerary_item_plans) {
            this.dayId = dayId
        }

        override fun setVariable(
            mBinding: ItineraryItemPlansBinding,
            position: Int,
            item: ItineraryDetailBean.AgendaBean.PlansBean
        ) {
            if (position == 0) {
                total = 0
                for (type in getData()) {
                    //有没有包含酒店
                    if (type.resourceType == "CONTENT_TYPE_HOTEL") {
                        isContainHotel = true
                    }
                    if (Constants.TYPE_VENUE == type.resourceType || Constants.TYPE_SCENERY == type.resourceType) {
                        total += 1
                    }
                    if (type.resourceType == "CONTENT_TYPE_RESTAURANT"){
                        restaurantTotal+=1
                    }
                }
            }
            item.dayId = dayId
            //默认隐藏 加号，定门票
            mBinding.addView.visibility = View.GONE
            mBinding.deleteView.visibility = View.GONE
            mBinding.nearLiveView.visibility = View.GONE
            mBinding.nearEatView.visibility = View.GONE
            mBinding.ticketingView.visibility = View.GONE
            mBinding.drivingView.visibility = View.GONE
            if (item.timeInterval.isNullOrEmpty()) {
                mBinding.tvTimeInterval.visibility = View.GONE
            } else {
                mBinding.tvTimeInterval.visibility = View.VISIBLE
                mBinding.tvTimeInterval.text = item.timeInterval
            }

            //  是否是自己的（自己定制的  或者 系统推荐的自己的）
            myOwn = itineraryType == ItineraryActivity.CLIENT || (itineraryType == ItineraryActivity.SYSTEM && myselfFlag)


            val next = position + 1
            if (itemCount > 1 && next < itemCount) {
                //取出当前数据
                val one = getData()[position]
                //取出下一个数据
                val two = getData()[next]
                //取出来的两个数据必须是场馆或者景区类型
                if ((one.resourceType == Constants.TYPE_SCENERY || one.resourceType == Constants.TYPE_VENUE) && (two.resourceType == Constants.TYPE_SCENERY || two.resourceType == Constants.TYPE_VENUE)) {
                    //显示导航布局
                    mBinding.drivingView.visibility = View.VISIBLE
                    //自驾
                    if (travelType == Constants.JOURNEY_TRAVEL_SELF) {
                        mBinding.labelDriving.text = "自驾"
                        mBinding.naviView.visibility = View.VISIBLE
                        mBinding.moreView.visibility = View.GONE
                        mBinding.moreView.setOnClickListener(null)
                        mBinding.naviView.setOnClickListener {
                            listener?.onNavi(one, two)
                        }
                        queryTraffic(one.id, two.id, mBinding)


                        mBinding.upstreamView.text = one.name
                        mBinding.lowerView.text = two.name

                        //公共交通
                    } else {
                        mBinding.labelDriving.text = "公共交通"
                        mBinding.moreView.visibility = View.VISIBLE
                        mBinding.naviView.visibility = View.GONE
                        mBinding.naviView.setOnClickListener {
                            listener?.onNavi(one, two)
                        }

                        queryTraffic(one.id, two.id, mBinding)
                        //点击查看更多
                        mBinding.moreView.setOnClickListener {
                            listener?.onMoreTraffic(one, two)
                        }
                    }
                }
            }

            if (itemCount == (position + 1)) {
                mBinding.shrinkView.visibility = View.VISIBLE
            } else {
                mBinding.shrinkView.visibility = View.GONE
            }

           // 删除按钮
            mBinding.deleteView.visibility = if (myOwn) View.VISIBLE else View.GONE

            var type = ""
            when (item.resourceType) {
                "CONTENT_TYPE_SCENERY" -> {    //景区
                    type = "景：${item.name}"
                    mBinding.iconView.setImageResource(R.drawable.itinerary_vector_icon_scenic)
                    if (item.sysCode.isNotEmpty() && item.resourceCode.isNotEmpty() && itineraryType == ItineraryActivity.CLIENT) {
                        //显示[定门票]
                        mBinding.ticketingView.visibility = View.VISIBLE
                    }
                    //  住在附近
                    mBinding.nearLiveView.visibility = if (!isContainHotel && myOwn) View.VISIBLE else View.GONE

                    // 吃在附近
                    mBinding.nearEatView.visibility = if (restaurantTotal<3 && myOwn) View.VISIBLE else View.GONE

                    // 添加按钮
                    mBinding.addView.visibility = if (myOwn && total < 4) View.VISIBLE else View.GONE

                }
                "CONTENT_TYPE_VENUE" -> {      //场馆
                    type = "馆：${item.name}"

                    mBinding.iconView.setImageResource(R.drawable.itinerary_vector_icon_venue)
                    //  住在附近
                    mBinding.nearLiveView.visibility = if (!isContainHotel && myOwn) View.VISIBLE else View.GONE

                    // 吃在附近
                    mBinding.nearEatView.visibility = if (restaurantTotal<3 && myOwn) View.VISIBLE else View.GONE

                    // 添加按钮
                    mBinding.addView.visibility = if (myOwn && total < 4) View.VISIBLE else View.GONE
                }
                "CONTENT_TYPE_RESTAURANT" -> { //餐馆
                    type = "吃：${item.name}"
                    if (itineraryType == ItineraryActivity.CLIENT) {
                        mBinding.nearEatView.visibility = View.GONE
                    }
                    mBinding.iconView.setImageResource(R.drawable.itinerary_vector_icon_eat)
                }
                "CONTENT_TYPE_HOTEL" -> {      //酒店
                    type = "住：${item.name}"
                    mBinding.iconView.setImageResource(R.drawable.itinerary_vector_icon_hoel)
                }
            }

            val ssb = SpannableStringBuilder(type).apply {
                val greenSpan =
                    ForegroundColorSpan(mBinding.root.context.resources.getColor(R.color.color_FF9E05))
                setSpan(greenSpan, 2, type.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }


            val sb = StringBuilder()
            if (!item.openTimeStart.isNullOrEmpty()) {
                sb.append("开放时间：${item.openTimeStart}-${item.openTimeEnd}")
            }
            if (item.adviceTime > 0){
                sb.takeIf { it.isNotEmpty() }?.apply { this.append(",") }
                sb.append("建议游玩${item.adviceTime}小时")
            }
            sb.takeIf { it.isNotEmpty() }?.apply { this.append("\n") }
            item.address?.takeIf { it.isNotEmpty() }?.apply {
                sb.append("地址：${this}")
            }

            mBinding.typeView.text = ssb
            mBinding.periodView.text = sb.toString()
            if (!item.notes.isNullOrEmpty()) {
                mBinding.describeView.visibility = View.VISIBLE
                mBinding.describeView.text = item.notes
            } else if (!item.summary.isNullOrEmpty()) {
                mBinding.describeView.text = item.summary
                mBinding.describeView.visibility = View.VISIBLE
            } else {
                mBinding.describeView.text = ""
                mBinding.describeView.visibility = View.GONE
            }
            mBinding.coversLayout.removeAllViews()
            if (!item.images.isNullOrEmpty()){
                val pics = item.images.split(",")
                if (!pics.isNullOrEmpty()) {
                    mBinding.coversLayout.visibility = View.VISIBLE
                    val params = LinearLayout.LayoutParams(0, -1, 1f)
                    if (pics.size == 1) {
//                        val coversOne = ArcImageView(mBinding.root.context).apply {
//                            layoutParams = params
//                            setCornerRadius(ResourceUtils.getDimension(this, R.dimen.dp_10).toFloat())
//                        }
//                        coversOne.roundRadius


                        val coversOne = ImageView(mBinding.root.context).apply {
                            layoutParams =  LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                                gravity = Gravity.CENTER_VERTICAL
                                width =  mBinding.root.context.resources.displayMetrics.widthPixels / 2
                            } }
                        Glide.with(mBinding.root.context).load(pics[0])
                            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(3.dp)))
                            .placeholder(R.drawable.grid_placeholde)
                            .into(coversOne)
                        mBinding.coversLayout.addView(coversOne)
                    } else {
//                        val coversOne = ArcImageView(mBinding.root.context).apply {
//                            params.rightMargin = ResourceUtils.getDimension(this, R.dimen.dp_5)
//                            layoutParams = params
//                            setCornerRadius(ResourceUtils.getDimension(this, R.dimen.dp_10).toFloat())
//                        }
                        val coversOne = ImageView(mBinding.root.context).apply {
                            layoutParams = params.apply {
                                marginEnd = 5.dp
                            }
                        }
                        Glide.with(mBinding.root.context).load(pics[0])
                            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(3.dp)))
                            .placeholder(R.drawable.grid_placeholde)
                            .into(coversOne)

                        mBinding.coversLayout.addView(coversOne)

//                        val coversTwo = ArcImageView(mBinding.root.context).apply {
//                            params.leftMargin = ResourceUtils.getDimension(this, R.dimen.dp_5)
//                            layoutParams = params
//                            setCornerRadius(ResourceUtils.getDimension(this, R.dimen.dp_10).toFloat())
//                        }
                        val coversTwo = ImageView(mBinding.root.context).apply { layoutParams = params }
                        Glide.with(mBinding.root.context).load(pics[1])
                            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(3.dp)))
                            .placeholder(R.drawable.grid_placeholde)
                            .into(coversTwo)
                        mBinding.coversLayout.addView(coversTwo)
                    }
                }
            } else {
                mBinding.coversLayout.visibility = View.GONE
            }


            mBinding.coversLayout.setOnClickListener {
                    var path = ""
                    when (item.resourceType) {
                        "CONTENT_TYPE_SCENERY" -> {
                            path = MainARouterPath.MAIN_SCENIC_DETAIL
                        }
                        "CONTENT_TYPE_VENUE" -> {
                            path = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY
                        }
                        "CONTENT_TYPE_HOTEL" -> {
                            path = ZARouterPath.ZMAIN_HOTEL_DETAIL
                        }
                    }
                    if (path.isNotEmpty()) {
                        ARouter.getInstance()
                            .build(path)
                            .withBoolean("isShowButton", true)
                            .withString("source", "itinerary")
                            .withString("id", item.resourceId.toString())
                            .navigation()
                    }
                }

            //点击收起
            mBinding.shrinkView.setOnClickListener {
                parentView?.let {
                    it.visibility = View.GONE
                    arrowView?.setImageResource(R.drawable.itinerary_vector_arrow_down)
                }
            }

            //点击删除
            mBinding.deleteView.setOnClickListener {
                AlertDialog.Builder(mBinding.root.context)
                    .setTitle("温馨提示！")
                    .setMessage("请问是否删除该资源")
                    .setNegativeButton("取消") { dialog, which ->
                        dialog.dismiss()
                    }.setPositiveButton("确定删除") { dialog, which ->
                        dialog.dismiss()
                        change = true
                        listener?.onDelete(item)
                    }.show()
            }

            //点击吃在附近
            mBinding.nearEatView.setOnClickListener {
                change = true
                listener?.nearby("吃在附近", item)
            }

            //点击住在附近
            mBinding.nearLiveView.setOnClickListener {
                change = true
                listener?.nearby("住在附近", item)
            }

            //点击+号
            mBinding.addView.setOnClickListener {
                change = true
                listener?.onAdd(item)
            }

            mBinding.typeView.setOnClickListener {
                var path = ""
                when (item.resourceType) {
                    "CONTENT_TYPE_SCENERY" -> {
                        path = MainARouterPath.MAIN_SCENIC_DETAIL
                    }
                    "CONTENT_TYPE_VENUE" -> {
                        path = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY
                    }
                    "CONTENT_TYPE_HOTEL" -> {
                        path = ZARouterPath.ZMAIN_HOTEL_DETAIL
                    }
                }
                if (path.isNotEmpty()) {
                    ARouter.getInstance()
                        .build(path)
                        .withBoolean("isShowButton", true)
                        .withString("source", "itinerary")
                        .withString("id", item.resourceId.toString())
                        .navigation()
                }
            }

            //点击门票
            mBinding.ticketingView.setOnClickListener {
                listener?.nearbyBooking(item)
            }
        }

        private fun queryTraffic(startId: Int, endId: Int,mBinding: ItineraryItemPlansBinding){
            ItineraryRepository.service.queryTraffic(startId,endId,"").excute(object :
                BaseObserver<Traffbean>() {
                override fun onSuccess(response: BaseResponse<Traffbean>) {
                    response.data?.let {
                        it.trafficList.forEach { traffic ->
                            traffic.number = Constants.getOrderBy(traffic.type)
                        }
                        val sorted = it.trafficList.sortedBy { it.number }
                        val bean = if (travelType == Constants.JOURNEY_TRAVEL_SELF) {
                            sorted.last()
                        } else sorted.first()

                        // 如果出行方式是 公共交通 但是查询出来的出行方式是自驾 则隐藏更多 显示导航
                        if (travelType == Constants.JOURNEY_TRAVEL_TRAFFIC &&  bean.type == Constants.selfDrive){
                            mBinding.moreView.visibility = View.GONE
                            mBinding.naviView.visibility = View.VISIBLE
                        }

                        mBinding.labelDriving.text = Constants.getTransport(bean.type)
                        mBinding.upstreamView.text = bean.startName
                        mBinding.lowerView.text = bean.endName

                        val sb = StringBuilder()

                        // 距离
                        if (!bean.distance.isNullOrEmpty()) {
                            sb.append(
                                try {
                                    "${Utils.toKm(bean.distance.toDouble())}KM"
                                } catch (e: Exception) {
                                    bean.distance
                                }
                            )
                        }
                        // 逗号
                        if (sb.isNotEmpty()) {
                            sb.append(",")
                        }
                        // 时间
                        if (!bean.consumeTime.isNullOrEmpty()) {
                            sb.append(
                                "预计耗时${
                                    try {
                                        DateUtil.formatTime(bean.consumeTime.toLong() * 1000)
                                    } catch (e: Exception) {
                                        bean.consumeTime
                                    }
                                }"
                            )
                        }

                        mBinding.distanceView.text = sb.toString()

//                        if (bean.type == Constants.autoBus) {
//                            if (!bean.line.isNullOrEmpty()) {
//                                //显示公交车路线
//                                mBinding.busLineView.text = bean.line
//                                mBinding.busLineView.visibility = View.VISIBLE
//                            } else {
//                                //公交路线值为空，隐藏公交车路线
//                                mBinding.busLineView.visibility = View.GONE
//                            }
//                        } else {
//                            //不是公交，隐藏公交车路线
//                            mBinding.busLineView.visibility = View.GONE
//                        }

//                        //时间不是空
//                        if (!bean.consumeTime.isNullOrEmpty()) {
//                            try {
//                                mBinding.distanceView.text =
//                                    "${DateUtil.formatTime(bean.consumeTime.toLong() * 1000)}(最快)"
//                            } catch (ex: Exception) {
//                                mBinding.distanceView.text = "${bean.consumeTime}(最快)"
//                            }
//                        }
//
//                        //距离不是空 并且时间也不是空
//                        if (!bean.distance.isNullOrEmpty() && !bean.consumeTime.isNullOrEmpty()) {
//                            try {
//                                mBinding.distanceView.text =
//                                    "${Utils.toKm(bean.distance.toDouble())}km，预计耗时${
//                                        DateUtil.formatTime(bean.consumeTime.toLong() * 1000)
//                                    }"
//                            } catch (ex: Exception) {
//                                mBinding.distanceView.text =
//                                    "${Utils.toKm(bean.distance.toDouble())}km，预计耗时${bean.consumeTime}"
//                            }
//                        } else if (!bean.distance.isNullOrEmpty()) {
//                            mBinding.distanceView.text = bean.distance
//                        }

//                        if (bean.type == Constants.selfDrive) {
//                            //时间不是空
//                            if (!bean.consumeTime.isNullOrEmpty() && bean.consumeTime != "0") {
//                                try {
//                                    mBinding.distanceView.text =
//                                        "${DateUtil.formatTime2(bean.consumeTime.toLong() * 1000)}(最快)"
//                                } catch (ex: Exception) {
//                                    mBinding.distanceView.text = "${bean.consumeTime}(最快)"
//                                }
//                            }
//
//                            //距离不是空 并且时间也不是空
//                            if (!bean.distance.isNullOrEmpty() && bean.distance != "0.0" && !bean.consumeTime.isNullOrEmpty() && bean.consumeTime != "0") {
//                                try {
//                                    mBinding.distanceView.text =
//                                        "${Utils.toKm(bean.distance.toDouble())}km，预计耗时${
//                                            DateUtil.formatTime2(
//                                                bean.consumeTime.toLong() * 1000
//                                            )
//                                        }"
//                                } catch (ex: Exception) {
//                                    mBinding.distanceView.text =
//                                        "${bean.distance}km，预计耗时${bean.consumeTime}"
//                                }
//                            }
//                        } else {
//                            mBinding.distanceView.text = bean.distance
//                        }
                    }
                }

                override fun onFailed(response: BaseResponse<Traffbean>) {
                    super.onFailed(response)
                    ToastUtils.showMessage(response.message)
                }
            })
        }
    }
}