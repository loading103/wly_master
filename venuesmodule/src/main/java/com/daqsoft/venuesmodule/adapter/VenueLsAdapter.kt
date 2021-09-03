package com.daqsoft.venuesmodule.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.adapter.BindingViewHolder
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.HtmlUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.network.venues.bean.LabelBean
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.AddressUtil
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.Utils
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.activity.VenuesActivity
import com.daqsoft.venuesmodule.databinding.ItemVenuesListBinding
import com.daqsoft.venuesmodule.repository.bean.OrderRoomInfo
import com.daqsoft.venuesmodule.repository.bean.VenuesListBean
import com.jakewharton.rxbinding2.view.RxView
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   VenueLsAdapter
 * @Author      luoyi
 * @Time        2020/3/23 19:29
 */
class VenueLsAdapter : RecyclerViewAdapter<ItemVenuesListBinding, VenuesListBean> {
    var context: Context? = null
    /**
     * 用户当前位置
     */
    var selfLocation: LatLng? = null
    var onItemClick: OnItemClickListener? = null

    constructor(context: Context) : super(R.layout.item_venues_list) {
        this.context = context
    }

    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemVenuesListBinding, position: Int, item: VenuesListBean) {
        mBinding.item = item
        var imageUrl = ""
        if (!item.images.isNullOrEmpty()) {
            var images = item.images.split(",")
            if (!images.isNullOrEmpty()) {
                imageUrl = images[0]
            }
        }
        mBinding.venueImage = imageUrl
        mBinding.image = imageUrl
        if (item.orderRoomInfo.isNotEmpty() && item.orderRoomInfo.size >= 2) {
            mBinding.pagerItemVenuesActivity.id = item.id.toInt()
            mBinding.indicatorItemVenuesActivity.setTotal(item.orderRoomInfo.size, intArrayOf(R.mipmap.index_icon_lunbo_normal, R.mipmap.index_icon_lunbo_selected))
            mBinding.indicatorItemVenuesActivity.binViewPager(mBinding.pagerItemVenuesActivity)
            var imageAdapter = ImageRoomPagerAdapter(item.orderRoomInfo as MutableList<OrderRoomInfo>, context!!)
            mBinding.pagerItemVenuesActivity.adapter = imageAdapter
            mBinding.pagerItemVenuesActivity.id = item.id.toInt()
            mBinding.roomName = item.orderRoomInfo[0].name
//            if(position==0) {
//                mBinding.roomName = item.orderRoomInfo[position].name
//            }
            mBinding.pagerItemVenuesActivity.setOnPageChangeListener(object : OnPageChangeListener, ViewPager.OnPageChangeListener {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                }

                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(index: Int) {
//                    VenuesActivity.ViewPagerPageChangeListener(
//                        mBinding,
//                        item.orderRoomInfo as MutableList<OrderRoomInfo>
//                    )
                    if (index < item.orderRoomInfo.size) {
                        mBinding.roomName = item.orderRoomInfo[index].name
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                }

            })
        }

        if (selfLocation != null) {
            if (!item.latitude.isNullOrEmpty() && !item.longitude.isNullOrEmpty() && item.latitude != "0.0" &&
                item.longitude != "0.0"
            ) {
                var distance = AddressUtil.getLocationDistanceCh(selfLocation!!, LatLng(item.latitude.toDouble(), item.longitude.toDouble()))
                if (distance != "") {
                    mBinding.tvItemVenuesDistance.text = "距您$distance"
                }
            }
        }
        if (item.vipResourceStatus != null) {
            if (item.vipResourceStatus!!.collectionStatus) {
                mBinding.imgVenuesCollect.setImageResource(R.mipmap.dropsc_hover)
            } else {
                mBinding.imgVenuesCollect.setImageResource(R.mipmap.dropsc)
            }
            mBinding.imgVenuesCollect.visibility = View.VISIBLE
        } else {
            mBinding.imgVenuesCollect.visibility = View.GONE
        }
        mBinding.time = Utils().OpenWeekUtils(item.openWeek)
        // 地址信息
        var address: String? = DividerTextUtils.convertDotString(
            StringBuilder(), if (item.regionName.isNullOrEmpty()) {
                ""
            } else {
                item.regionName
            }, if (item.address.isNullOrEmpty()) {
                ""
            } else {
                item.address
            }
        )
        mBinding.address = "" + address
        // 标签显示
        var labelList = mutableListOf<LabelBean>()
        var labelAdapter = LabelAdapter(context!!, labelList)
        labelAdapter.emptyViewShow = false
        mBinding.recyclerVenuesLabel.layoutManager =
            LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        mBinding.recyclerVenuesLabel.adapter = labelAdapter
        labelList.clear()
        // 场馆类型
        if (!item.type.isNullOrEmpty()) {
            labelList.add(LabelBean(item.type, 1))
        }
//        if (item.todayOrderStatus == 1) {
//            labelList.add(LabelBean("可预约", 1))
//        }
        if (!item.venueLevel.isNullOrEmpty()) {
            labelList.add(LabelBean(item.venueLevel, 1))
        }
        if(item.isOpen==1){
            labelList.add(LabelBean("预订预约", 1))
        }
        if (item.guideIsOpen == 1) {
            labelList.add(LabelBean("讲解预约", 1))
        }
        if (!item.activityInfo.isNullOrEmpty()) {
            labelList.add(LabelBean(context!!.getString(R.string.venue_can_res_activtity) + item.activityInfo.size + "个", 1))
        }
        if (!item.orderRoomInfo.isNullOrEmpty()) {
            labelList.add(LabelBean("可预约活动室" + item.orderRoomInfo.size + "个", 1))
        }


        // 场馆标签
//        if (!item.tagName.isNullOrEmpty()) {
//            for (tag in item.tagName) {
//                labelList.add(LabelBean(tag, 2))
//            }
//        }
        if (!labelList.isNullOrEmpty()) {
            mBinding.recyclerVenuesLabel.visibility = View.VISIBLE
            labelAdapter!!.notifyDataSetChanged()
        } else {
            mBinding.recyclerVenuesLabel.visibility = View.GONE
        }
        //
        var valuePages: MutableList<ValueKeyBean> = mutableListOf()
        valuePages.clear()
        if (!item.activityInfo.isNullOrEmpty()) {
            valuePages.add(ValueKeyBean("活动:", "${item.activityInfo[0].name}"))
        }
        if (item.todayOrderStatus == 1) {
            valuePages.add(ValueKeyBean("预约：", "今日剩余可预约数：${item.suprsNum}"))
        }

        if (!item.contentDataList.isNullOrEmpty()) {
            valuePages.add(ValueKeyBean("资讯：", "${item.contentDataList[0].title}"))
        }
        if (!valuePages.isNullOrEmpty()) {
            mBinding.vfVenues.visibility = View.VISIBLE
            mBinding.vfVenues.removeAllViews()
            for (data in valuePages) {
                var view = LayoutInflater.from(context!!).inflate(R.layout.item_venues_flipper, null)
                var tvName = view.findViewById<TextView>(R.id.tv_item_venues_activity_name)
                tvName.text = "" + data.name
                var tvValues = view.findViewById<TextView>(R.id.tv_item_venues_activity_info)
                tvValues.text = "" + data.value
                if (data.name.contains("预约")) {
                    view.onNoDoubleClick {
                        if (AppUtils.isLogin()) {
                            ARouter.getInstance().build(ARouterPath.VenuesModule.VENUES_RESERVATION_V1_ACTIVITY)
                                .withString("venueId", item.id)
                                .navigation()
                        } else {
                            ToastUtils.showUnLoginMsg()
                            ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                .navigation()
                        }
                    }
                }
                mBinding.vfVenues.addView(view)
            }
            mBinding.vfVenues.setFlipInterval(3000)
            mBinding.vfVenues.isAutoStart = true
        } else {
            mBinding.vfVenues.visibility = View.GONE
            mBinding.vfVenues.removeAllViews()
        }


        RxView.clicks(mBinding.root)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                ARouter.getInstance()
                    .build(ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY)
                    .withString("id", item.id)
                    .withString("type",item.type)
                    .navigation()
            }
        RxView.clicks(mBinding.imgVenuesCollect)// 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                if (item.vipResourceStatus != null && onItemClick != null) {
                    onItemClick!!.onItemClick(item.id, position, item.vipResourceStatus!!.collectionStatus)
                }
            }

    }

    override fun payloadUpdateUi(mBinding: ItemVenuesListBinding, position: Int, payloads: MutableList<Any>) {
        if (payloads[0] == "updateCollect") {
            try {
                val item = getData()[position]
                if (item.vipResourceStatus != null) {
                    if (item.vipResourceStatus!!.collectionStatus) {
                        mBinding.imgVenuesCollect.setImageResource(R.mipmap.dropsc_hover)
                    } else {
                        mBinding.imgVenuesCollect.setImageResource(R.mipmap.dropsc)
                    }
                }
            } catch (e: Exception) {

            }

        }
    }

    fun notifyCollectStatus(postion: Int) {
        try {
            if (!getData().isNullOrEmpty()) {
                var bean = getData()[postion]
                if (bean.vipResourceStatus != null) {
                    getData()[postion].vipResourceStatus!!.collectionStatus = !bean.vipResourceStatus!!.collectionStatus
                    notifyItemChanged(postion, "updateCollect")
                }
            }
        } catch (e: Exception) {

        }
    }

    interface OnItemClickListener {
        fun onItemClick(id: String, position: Int, status: Boolean)
    }
}