package com.daqsoft.usermodule.ui.consume

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.FullyLinearLayoutManager
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FragmentConsumeDetailBinding
import com.daqsoft.usermodule.databinding.ItemConsumePersonBinding
import com.daqsoft.usermodule.ui.order.adapter.MineConsumePersonAdapter
import kotlinx.android.synthetic.main.fragment_consume_detail.*
import kotlinx.android.synthetic.main.fragment_order_detail.*
import org.jetbrains.anko.support.v4.toast

/**
 * @des 活动室的详情fragment
 * @author PuHua
 * @date
 */
class ConsumeActivityRoomFragment() :
    BaseFragment<FragmentConsumeDetailBinding, ConsumeActivityRoomViewModel>() {

    var consume: ConsumeDetail? = null

    var consumePersonAdapter: MineConsumePersonAdapter? = null

    constructor(dr: ConsumeDetail) : this() {
        this.consume = dr
    }

    override fun getLayout(): Int = R.layout.fragment_consume_detail

    override fun injectVm(): Class<ConsumeActivityRoomViewModel> =
        ConsumeActivityRoomViewModel::class.java

    override fun initView() {
        initViewModel()
        mBinding.tvVenueName.text = consume?.activityRoom?.venueName
        if (consume != null)
            when (consume!!.orderType) {
                ResourceType.CONTENT_TYPE_ACTIVITY_SHIU -> {
                    mBinding.name = consume!!.activityRoom.venueName
                    if (consume!!.activityRoom.image.isNotEmpty()) {
                        mBinding.image = consume!!.activityRoom.image.split(",")[0]
                    }
                    mBinding.tvTime.setLabel("预约日期:")
                    val dateBegin = DateUtil.formatDateByString(
                        "yyyy-MM-dd",
                        "yyyy-MM-dd HH:mm",
                        consume!!.orderStartTime
                    )
                    mBinding.time = dateBegin
                    var timeBegin = DateUtil.formatDateByString(
                        "HH:mm",
                        "yyyy-MM-dd HH:mm",
                        consume!!.orderStartTime
                    )
                    var timeEnd = DateUtil.formatDateByString(
                        "HH:mm",
                        "yyyy-MM-dd HH:mm",
                        consume!!.orderEndTime
                    )
                    mBinding.tvAddress.setLabel("预约时间：")
                    mBinding.address = "${timeBegin}-${timeEnd}"
                    mBinding.productName = consume!!.activityRoom.name
                    if (!consume!!.activityRoom.lat.isNullOrEmpty()) {
                        mBinding.tvGuide.visibility = View.VISIBLE
                    } else {
                        mBinding.tvGuide.visibility = View.GONE
                    }
                    setTourist()
                    if (!consume!!.activityRoom.phone.isNullOrEmpty()) {
                        mBinding.tvPhone.visibility = View.VISIBLE
                    } else {
                        mBinding.tvPhone.visibility = View.GONE
                    }
                }
                ResourceType.CONTENT_TYPE_ACTIVITY -> {
                    if (consume!!.activity != null) {
                        mBinding.name = consume!!.activity.name
                        mBinding.image = consume!!.activity.image
                        mBinding.address = consume!!.activity.address
                        mBinding.time = consume!!.activity.useStartTime
                        mBinding.productName = consume!!.activity.name
                        mBinding.labelBooking.text =
                            context!!.getString(R.string.order_activity_information)
                        mBinding.tvTimeBookingStartTime.setLabel(context!!.getString(R.string.order_belong_venue))
                        mBinding.startTime = consume!!.activity.resourceName
                        mBinding.tvTimeBookingEndTime.visibility = View.GONE
                        mBinding.number = context!!.getString(
                            R.string.order_activity_room_number_stamp,
                            consume!!.orderNum
                        )
                        if (!consume!!.activity.lat.isNullOrEmpty()) {
                            mBinding.tvGuide.visibility = View.VISIBLE
                        } else {
                            mBinding.tvGuide.visibility = View.GONE
                        }
                        if (!consume!!.activity.phone.isNullOrEmpty()) {
                            mBinding.tvPhone.visibility = View.VISIBLE
                        } else {
                            mBinding.tvPhone.visibility = View.GONE
                        }
                    }
                    setTourist()
                }
                ResourceType.CONTENT_TYPE_VENUE, ResourceType.CONTENT_TYPE_SCENERY -> {
                    if (consume?.venueInfo != null) {
                        mBinding.name = consume!!.venueInfo.venueName
                        mBinding.productName = consume!!.venueInfo.venueName
                        if (!consume!!.venueInfo.image.isNullOrEmpty()) {
                            mBinding.image = consume!!.venueInfo.image.getRealImages()
                        }
                        mBinding.llBookingInfo.visibility = View.GONE
                        if (!consume!!.venueInfo.useStartTime.isNullOrEmpty() && !consume!!.venueInfo.useEndTime.isNullOrEmpty()) {
                            mBinding.userTime = DateUtil.getTwoDateDayStrs(
                                consume!!.venueInfo.useStartTime,
                                consume!!.venueInfo.useEndTime
                            )
                        }
                        if (!consume!!.orderStartTime.isNullOrEmpty() && !consume!!.orderEndTime.isNullOrEmpty()) {
                            mBinding.time = DateUtil.getTwoDateDayStrs(
                                consume!!.orderStartTime,
                                consume!!.orderEndTime
                            )
                        }
                        mBinding.tvAddress.tvLabel.text = "预约类型："
                        if (!consume!!.venueInfo.reservationType.isNullOrEmpty()) {
                            mBinding.address =
                                if (consume!!.venueInfo.reservationType == "PERSON") {
                                    "个人预约"
                                } else {
                                    "团队预约"
                                }
                        }
                        mBinding.inputPerson = "${consume!!.venueInfo.useNum}人"
                        // 讲解员预约
                        if (consume != null && consume!!.isGuideOrder == 1) {
                            bindCommtatorData(consume!!.venueInfo)
                        }
                        if (!consume!!.venueInfo.lat.isNullOrEmpty()) {
                            mBinding.tvGuide.visibility = View.VISIBLE
                        } else {
                            mBinding.tvGuide.visibility = View.GONE
                        }
                        if (!consume!!.venueInfo.phone.isNullOrEmpty()) {
                            mBinding.tvPhone.visibility = View.VISIBLE
                        } else {
                            mBinding.tvPhone.visibility = View.GONE
                        }

                        if (consume!!.hasAttached == 1) {
                            consumePersonAdapter = MineConsumePersonAdapter().apply {
                                var timeBegin = DateUtil.formatDateByString(
                                    "yyyy-MM-dd HH:mm",
                                    "yyyy-MM-dd HH:mm",
                                    consume!!.orderStartTime
                                )
                                var timeEnd =
                                    DateUtil.formatDateByString(
                                        "HH:mm",
                                        "yyyy-MM-dd HH:mm",
                                        consume!!.orderEndTime
                                    )
                                userTime = context!!.getString(
                                    R.string.order_activity_room_time_stamp,
                                    timeBegin,
                                    timeEnd
                                )
                            }
                            consumePersonAdapter?.emptyViewShow = false
                            mBinding.mRecyclerView.adapter = consumePersonAdapter
                            mBinding.mRecyclerView.layoutManager = FullyLinearLayoutManager(
                                context!!,
                                FullyLinearLayoutManager.VERTICAL,
                                false
                            )
                            mModel.getAttachPersonInfos(consume!!.orderId)
                        } else {
                            setTourist()
                        }
                    }
                }
            }

        mBinding.tvPhone.onNoDoubleClick {
            consume?.let {
                when (it.orderType) {
                    ResourceType.CONTENT_TYPE_ACTIVITY_SHIU -> {
                        it.activityRoom?.let { it2 ->
                            if (!it2.phone.isNullOrEmpty()) {
                                SystemHelper.callPhone(context!!, it2.phone)
                            }
                        }
                    }
                    ResourceType.CONTENT_TYPE_ACTIVITY -> {
                        it.activity?.let { it2 ->
                            if (!it2.phone.isNullOrEmpty()) {
                                SystemHelper.callPhone(context!!, it2.phone)
                            }
                        }
                    }
                    ResourceType.CONTENT_TYPE_VENUE, ResourceType.CONTENT_TYPE_SCENERY -> {
                        it.venueInfo?.let { it2 ->
                            if (!it2.phone.isNullOrEmpty()) {
                                SystemHelper.callPhone(context!!, it2.phone)
                            }
                        }
                    }
                }
            }
        }
        mBinding.tvGuide.onNoDoubleClick {
            if (MapNaviUtils.isGdMapInstalled()) {
                consume?.let {
                    when (it.orderType) {
                        ResourceType.CONTENT_TYPE_ACTIVITY_SHIU -> {
                            it.activityRoom?.let { it2 ->
                                if (!it2.lat.isNullOrEmpty() && !it2.lon.isNullOrEmpty()) {
                                    MapNaviUtils.openGaoDeNavi(
                                        BaseApplication.context,
                                        0.0,
                                        0.0,
                                        null,
                                        it2.lat.toDouble(),
                                        it2.lon.toDouble(),
                                        it2.address
                                    )
                                }
                            }
                        }
                        ResourceType.CONTENT_TYPE_ACTIVITY -> {
                            it.activity?.let { it2 ->
                                if (!it2.lat.isNullOrEmpty() && !it2.lon.isNullOrEmpty()) {
                                    MapNaviUtils.openGaoDeNavi(
                                        BaseApplication.context,
                                        0.0,
                                        0.0,
                                        null,
                                        it2.lat.toDouble(),
                                        it2.lon.toDouble(),
                                        it2.address
                                    )
                                }
                            }
                        }
                        ResourceType.CONTENT_TYPE_VENUE, ResourceType.CONTENT_TYPE_SCENERY -> {
                            it.venueInfo?.let { it2 ->
                                if (!it2.lat.isNullOrEmpty() && !it2.lon.isNullOrEmpty()) {
                                    MapNaviUtils.openGaoDeNavi(
                                        BaseApplication.context,
                                        0.0,
                                        0.0,
                                        null,
                                        it2.lat.toDouble(),
                                        it2.lon.toDouble(),
                                        it2.address
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                toast("非常抱歉，系统未安装地图软件")
            }
        }
        mBinding.vConsumeDetail.onNoDoubleClick {
            consume?.let {
                it.orderType?.let { type ->
                    when (type) {
                        ResourceType.CONTENT_TYPE_VENUE, ResourceType.CONTENT_TYPE_SCENERY -> {
                            it.venueInfo?.let { info ->
                                if (!info.resourceType.isNullOrEmpty() && !info.resourceId.isNullOrEmpty()) {
                                    MenuJumpUtils.gotoResourceDetail(
                                        info.resourceType,
                                        info.resourceId
                                    )
                                }
                            }
                        }
                        ResourceType.CONTENT_TYPE_ACTIVITY_SHIU -> {
                            it.activityRoom?.let { info ->
                                if (!info.resourceType.isNullOrEmpty() && !info.resourceId.isNullOrEmpty()) {
                                    MenuJumpUtils.gotoResourceDetail(
                                        info.resourceType,
                                        info.resourceId
                                    )
                                }
                            }
                        }
                        ResourceType.CONTENT_TYPE_ACTIVITY -> {
                            it.activity?.let { info ->
                                if (!info.activityType.isNullOrEmpty() && !info.activityId.isNullOrEmpty()) {
                                    MenuJumpUtils.toActivityDetail(
                                        info.activityId,
                                        info.activityType
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    private fun initViewModel() {
        mModel.attachPersonsLd.observe(this, Observer {
            consumePersonAdapter?.clear()
            var temps: MutableList<OrderAttacthPersonBean> = mutableListOf()
            if (!it.wait.isNullOrEmpty()) {
                temps.addAll(it.wait!!)
            }
            if (!it.lose.isNullOrEmpty()) {
                temps.addAll(it.lose!!)
            }
            if (!it.cancel.isNullOrEmpty()) {
                temps.addAll(it.cancel!!)
            }
            if (!it.end.isNullOrEmpty()) {
                temps.addAll(it.end!!)
            }
            consumePersonAdapter?.add(temps)
        })
    }

    private fun setTourist() {
        val list = mutableListOf<ConsumeDetail>()
        list.add(consume!!)
        adapter.add(list)
        mBinding.mRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mBinding.mRecyclerView.adapter = adapter
    }


    override fun initData() {

    }

    fun updateHealth(it: HelathInfoBean) {
        if (it == null) {
            mBinding.jiankangView.visibility = View.GONE
            return
        }
        if (it.enableHealthyCode || it.enableTravelCode) {
            mBinding.jiankangView.visibility = View.VISIBLE
        }
        if (it.enableHealthyCode) {
            mBinding.llvHealthCodeInfo.visibility = View.VISIBLE
        } else {
            mBinding.llvHealthCodeInfo.visibility = View.GONE
        }
        if (it.enableTravelCode) {
            mBinding.llvTravelCodeInfo.visibility = View.VISIBLE
            mBinding.tvTravelCodeName.text = "${it.smartTravelName}状况"
        } else {
            mBinding.llvHealthCodeInfo.visibility = View.GONE
        }
        if (it.healthCode.isNullOrEmpty()) {
            mBinding.tvLabelAddress.visibility = View.GONE
            mBinding.registAddres.visibility = View.GONE
            mBinding.healthState.text = "未注册"
            mBinding.healthState.setTextColor(resources.getColor(R.color.color_999))
            var drawable = ResourceUtils.getDrawable(context!!, R.mipmap.icon_health_unkn)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            mBinding.healthState.setCompoundDrawables(drawable, null, null, null)
        } else {
            mBinding.registAddres.text = "" + it.regionName
            var drawable: Drawable
            when (it.healthCode) {
                "01" -> {
                    mBinding.healthState.text = "低风险"
                    mBinding.healthState.setTextColor(resources.getColor(R.color.c_36cd64))
                    drawable =
                        ResourceUtils.getDrawable(context!!, R.mipmap.icon_health_normal)
                }
                "11" -> {
                    mBinding.healthState.text = "中风险"
                    mBinding.healthState.setTextColor(resources.getColor(R.color.color_ff9e05))
                    drawable =
                        ResourceUtils.getDrawable(context!!, R.mipmap.icon_health_warn)
                }
                "31" -> {
                    mBinding.healthState.text = "高风险"
                    mBinding.healthState.setTextColor(resources.getColor(R.color.ff4e4e))
                    drawable =
                        ResourceUtils.getDrawable(context!!, R.mipmap.icon_health_danger)
                }
                else -> {
                    mBinding.healthState.text = "未知"
                    mBinding.healthState.setTextColor(resources.getColor(R.color.color_999))
                    drawable =
                        ResourceUtils.getDrawable(context!!, R.mipmap.icon_health_unkn)
                }
            }
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            mBinding.healthState.setCompoundDrawables(drawable, null, null, null)
        }
        // 旅游码状态
        var trvalDrawable: Drawable
        if (it.smartTravelRegisterStatus) {
            mBinding.travelCodeState.text = "已注册"
            mBinding.travelCodeState.setTextColor(resources.getColor(R.color.c_36cd64))
            trvalDrawable =
                ResourceUtils.getDrawable(context!!, R.mipmap.venue_book_condition_icon_low)
        } else {
            mBinding.travelCodeState.setTextColor(resources.getColor(R.color.color_999))
            mBinding.travelCodeState.text = "未注册"
            trvalDrawable =
                ResourceUtils.getDrawable(
                    context!!,
                    R.mipmap.venue_book_condition_icon_unknown
                )
        }
        trvalDrawable.setBounds(
            0,
            0,
            trvalDrawable.minimumWidth,
            trvalDrawable.minimumHeight
        )
        mBinding.travelCodeState.setCompoundDrawables(trvalDrawable, null, null, null)
        mBinding.travelCodeState.compoundDrawablePadding =
            resources.getDimension(R.dimen.dp_5).toInt()

    }

    private val adapter = object :
        RecyclerViewAdapter<ItemConsumePersonBinding, ConsumeDetail>(R.layout.item_consume_person) {
        @SuppressLint("CheckResult")
        override fun setVariable(
            mBinding: ItemConsumePersonBinding,
            position: Int,
            item: ConsumeDetail
        ) {
            mBinding.name = item.userName
            mBinding.phone = item.userPhone
            var timeBegin = DateUtil.formatDateByString(
                "yyyy-MM-dd HH:mm",
                "yyyy-MM-dd HH:mm",
                consume!!.orderStartTime
            )
            var timeEnd =
                DateUtil.formatDateByString(
                    "HH:mm",
                    "yyyy-MM-dd HH:mm",
                    consume!!.orderEndTime
                )
            mBinding.time =
                context!!.getString(
                    R.string.order_activity_room_time_stamp,
                    timeBegin,
                    timeEnd
                )
            mBinding.number = item.orderNum
            if (!item.cardType.isNullOrEmpty()) {
                mBinding.idNumber = "(${CertTypes.getCertTypeName(item.cardType)})${item.idCard}"
            } else {
                mBinding.idNumber = item.idCard
            }

            if (item.orderRemark.isNotEmpty()) {
                mBinding.tvRemaks.content = item.orderRemark
            } else {
                mBinding.tvRemaks.visibility = View.GONE
            }
            mBinding.status = when (item.status.toInt()) {
                // 判断当前订单的状态
                4 -> {
                    // 待审核

                    resources.getString(R.string.order_waite_valid)
                }
                79 -> {
                    // 未通过

                    resources.getString(R.string.order_no_pass)
                }
                11 -> {
                    // 待消费

                    resources.getString(R.string.order_waite_cost)
                }
                12 -> {
                    // 已完成

                    resources.getString(R.string.order_finish)
                }
                13 -> {
                    // 已失效

                    resources.getString(R.string.order_no_effect)
                }
                14 -> {
                    // 已取消

                    resources.getString(R.string.order_canceled)
                }

                else -> resources.getString(R.string.order_waite_valid)
            }


        }
    }

    private fun bindCommtatorData(venueInfo: VenueInfo) {
        ll_consume_commentator_info.visibility = View.VISIBLE
        if (!venueInfo.guideOrderLanguage.isNullOrEmpty()) {
            mBinding.llOrderCommentatorInfo.tvComtatorLgValue.text =
                if (venueInfo.guideOrderLanguage == "CH") {
                    "中文"
                } else {
                    "英文"
                }
            if (!venueInfo.guideExhibitions.isNullOrEmpty()) {
                for (item in venueInfo.guideExhibitions!!) {
                    var exhibitionView = LayoutInflater.from(context!!)
                        .inflate(R.layout.item_order_haved_exhall, null)
                    var tvExhibitionName: TextView =
                        exhibitionView!!.findViewById(R.id.tv_exhall_name)
                    var tvExhibitionIsRecommed: TextView =
                        exhibitionView!!.findViewById(R.id.tv_exhall_recommend)
                    if (item.recommend == 1) {
                        tvExhibitionIsRecommed.visibility = View.VISIBLE
                    } else {
                        tvExhibitionIsRecommed.visibility = View.GONE
                    }
                    tvExhibitionName.text = "${item.name}"
                    mBinding.llOrderCommentatorInfo.lvComtatorExhallValue.addView(
                        exhibitionView
                    )

                }

            }
            // 到馆时间
            if (!venueInfo.expectedTime.isNullOrEmpty()) {
                mBinding.llOrderCommentatorInfo.tvComtatorIntimeValue.text =
                    "" + venueInfo.expectedTime
                mBinding.llOrderCommentatorInfo.tvComtatorIntimeLabel.visibility =
                    View.VISIBLE
                mBinding.llOrderCommentatorInfo.tvComtatorIntimeValue.visibility =
                    View.VISIBLE
            } else {
                mBinding.llOrderCommentatorInfo.tvComtatorIntimeLabel.visibility = View.GONE
                mBinding.llOrderCommentatorInfo.tvComtatorIntimeValue.visibility = View.GONE
            }
            // 讲解员
            if (!venueInfo.guideName.isNullOrEmpty()) {
                try {
                    if (venueInfo.guideName!!.endsWith(",")) {
                        venueInfo.guideName = venueInfo.guideName!!.removeRange(
                            venueInfo.guideName!!.length - 1,
                            venueInfo.guideName!!.length
                        )
                    }
                } catch (e: Exception) {
                }
                mBinding.llOrderCommentatorInfo.tvComtatorPersonValue.text =
                    "" + venueInfo.guideName
                mBinding.llOrderCommentatorInfo.tvComtatorPersonValue.visibility =
                    View.VISIBLE
                mBinding.llOrderCommentatorInfo.tvComtatorPersonLabel.visibility =
                    View.VISIBLE
            } else {
                mBinding.llOrderCommentatorInfo.tvComtatorPersonValue.visibility = View.GONE
                mBinding.llOrderCommentatorInfo.tvComtatorPersonLabel.visibility = View.GONE
            }
        }
        mBinding.llOrderCommentatorInfo.tvComtatorCostValue.text =
            "${venueInfo.guideOrderPayMoney}元(到馆支付)"
    }

}

class ConsumeActivityRoomViewModel : BaseViewModel() {
    var attachPersonsLd = MutableLiveData<OrderAttachPMapBean>()

    fun getAttachPersonInfos(orderId: String) {
        UserRepository().userService.getOrderAttachedList(orderId)
            .excute(object : BaseObserver<OrderAttachPMapBean>() {
                override fun onSuccess(response: BaseResponse<OrderAttachPMapBean>) {
                    attachPersonsLd.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<OrderAttachPMapBean>) {
                    attachPersonsLd.postValue(null)
                }
            })
    }
}
