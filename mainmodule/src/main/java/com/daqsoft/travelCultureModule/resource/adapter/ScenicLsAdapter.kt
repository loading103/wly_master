package com.daqsoft.travelCultureModule.resource.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.ArcImageView
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemScenicListBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.DividerTextUtils
import com.jakewharton.rxbinding2.view.RxView
import java.lang.Exception
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   ScenicLsAdapter
 * @Author      PuHua
 * @Time        2020/3/30 13:51
 */
class ScenicLsAdapter : RecyclerViewAdapter<ItemScenicListBinding, ScenicBean> {
    var mContext: Context? = null
    /**
     * 当前经纬度
     */
    var selfLocation: LatLng? = null

    var onScenicLsItemClickListener: OnScenicLsItemClickListener? = null

    constructor(context: Context) : super(R.layout.item_scenic_list) {
        mContext = context
    }

    @SuppressLint("CheckResult")
    override fun setVariable(
        mBinding: ItemScenicListBinding,
        position: Int,
        item: ScenicBean
    ) {
        mBinding.item = item
        // 判断景点是否为空
        if (!item.spots.isNullOrEmpty() && item.spots.size >= 2) {
            mBinding.pagerItemVenuesActivity.id = item.id.toInt()
            var count = 0
            if (item.spots.size > 3) {
                count = 3
            } else {
                count = item.spots.size
            }
            mBinding.indicatorItemVenuesActivity.setTotal(count, intArrayOf(R.mipmap.index_icon_lunbo_normal, R.mipmap.index_icon_lunbo_selected))
            mBinding.indicatorItemVenuesActivity.binViewPager(mBinding.pagerItemVenuesActivity)
            val imagePagerAdapter = object : PagerAdapter() {
                override fun getCount(): Int = count

                override fun isViewFromObject(view: View, o: Any): Boolean = view == o

                override fun instantiateItem(container: ViewGroup, position: Int): Any {
                    var index = position % item.spots.size
                    var images = item.spots.get(index).images
                    var imageUrl: String = ""
                    if (!images.isNullOrEmpty()) {
                        var imageUrls = images.split(",")
                        if (!imageUrls.isNullOrEmpty()) {
                            imageUrl = imageUrls[0]
                        }
                    }
                    // 根据条目所在，从imgDatas集合中获取相对应的图片
                    var imageView: ArcImageView = ArcImageView(mContext)
                    imageView.setCornerTopRightRadius(Utils.dip2px(mContext!!, 2.5f))
                    imageView.setCornerBottomRightRadius(Utils.dip2px(mContext!!, 2.5f))
                    Glide.with(mContext!!)
                        .load(imageUrl)
                        .placeholder(R.mipmap.placeholder_img_fail_240_180)
                        .centerCrop()
                        .into(imageView)
                    // 把得到的imageView的对象，添加到viewPager，也就是contrainer中
                    container.addView(imageView)
                    imageView.onNoDoubleClick {
                        ARouter.getInstance()
                            .build(MainARouterPath.MAIN_SCENIC_DETAIL)
                            .withString("id", item.id.toString())
                            .navigation()
                    }
                    return imageView
                }

                override fun destroyItem(container: ViewGroup, position: Int, ob: Any) {
                    if (ob != null && ob is View) {
                        container.removeView(ob as View?)
                    }
                }

            }
            mBinding.pagerItemVenuesActivity.adapter = imagePagerAdapter
            mBinding.pagerItemVenuesActivity.id = item.id.toInt()
            mBinding.tvItemVenuesPageName.setText(item.spots.get(0).name)
            mBinding.pagerItemVenuesActivity.setOnPageChangeListener(object : ViewPager
            .OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {

                }

                override fun onPageSelected(position: Int) {
                    if (position < item.spots.size) {
                        mBinding.tvItemVenuesPageName.setText("" + item.spots[position].name)
                    }
                }

            })
//            mBinding.pagerItemVenuesActivity.onNoDoubleClick {
//                ARouter.getInstance()
//                    .build(MainARouterPath.MAIN_SCENIC_DETAIL)
//                    .withString("id", item.id.toString())
//                    .navigation()
//            }
        }
        if (selfLocation != null) {
            var distance = GaoDeLocation.CalculateLineDistance(
                selfLocation?.latitude.toString
                    (), selfLocation?.longitude.toString(), item.latitude, item.longitude
            )
            if (distance != "") {
                mBinding.tvItemVenuesDistance.text = "距您$distance"
            }
        }
//        val status = DividerTextUtils.convertString(
//            StringBuilder(), item.regionName, if (item
//                    .chargeStatus.equals("0")
//            ) "免费" else "收费", if (!item.openTimeStart.isNullOrEmpty()) {
//                "开放时间：" + item.openTimeStart + "~" + item
//                    .openTimeEnd+"（"+item.remarks+"）"
//            } else {
//                ""
//            }
//        )
        val sb = StringBuilder()
        if (!item.openTimeStart.isNullOrEmpty()) {
            sb.append( "开放时间：" + item.openTimeStart + "~" + item.openTimeEnd)
        }
        if (!item.remarks.isNullOrEmpty()) {
            sb.append( "（"+item.remarks+"）")
        }
        val status = DividerTextUtils.convertString(
            StringBuilder(), item.regionName, if (item
                    .chargeStatus.equals("0")
            ) "免费" else "收费", sb.toString()
        )
        mBinding.status = status

        if (!item.level.isNullOrEmpty()) {
            var num = item.level.count {
                it == 'A'
            }
            if (num > 0) {
                mBinding.level = "${num}A"
                mBinding.tvItemVenuesType.visibility = View.VISIBLE
            } else {
                mBinding.tvItemVenuesType.visibility = View.GONE
            }
        } else {
            mBinding.tvItemVenuesType.visibility = View.GONE
        }

        var valuePages: MutableList<ValueKeyBean> = mutableListOf()
        valuePages.clear()
        if (!item.activity.isNullOrEmpty()) {
            valuePages.add(ValueKeyBean("活动:", "${item.activity[0].name}"))
        }
        if (item.todayOrderStatus == 1) {
            valuePages.add(ValueKeyBean("预约：", "今日剩余可预约数：${item.suprsNum}"))
        }

        if (!item.contentDataList.isNullOrEmpty()) {
            valuePages.add(ValueKeyBean("资讯：", "${item.contentDataList!![0].title}"))
        }
        if (!valuePages.isNullOrEmpty()) {
            mBinding.vfVenues.visibility = View.VISIBLE
            mBinding.vfVenues.removeAllViews()
            for (data in valuePages) {
                var view = LayoutInflater.from(mContext!!).inflate(R.layout.item_scenic_flipper, null)
                var tvName = view.findViewById<TextView>(R.id.tv_item_venues_activity_name)
                tvName.text = "" + data.name
                var tvValues = view.findViewById<TextView>(R.id.tv_item_venues_activity_info)
                tvValues.text = "" + data.value
                if (data.name.contains("预约")) {
                    view.onNoDoubleClick {
                        if (AppUtils.isLogin()) {
                            ARouter.getInstance().build(ARouterPath.VenuesModule.SCENIC_RESERVATION_ACTIVITY)
                                .withString("scenicId", item.id)
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

        RxView.clicks(mBinding.vItemScenicLs)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_SCENIC_DETAIL)
                    .withString("id", item.id.toString())
                    .navigation()
            }
        // 收藏
        if (getData()[position].vipResourceStatus != null) {
            val status = getData()[position].vipResourceStatus.collectionStatus
            if (status) {
                mBinding.imgScenicCollect.setBackgroundResource(R.mipmap.provider_collect_selected)
                mBinding.imgVenusScenicCollect.setBackgroundResource(R.mipmap.provider_collect_selected)
            } else {
                mBinding.imgScenicCollect.setBackgroundResource(R.mipmap.provider_collect_normal)
                mBinding.imgVenusScenicCollect.setBackgroundResource(R.mipmap.provider_collect_normal)
            }
        }

        mBinding.imgScenicCollect.onNoDoubleClick {
            if (onScenicLsItemClickListener != null && item.vipResourceStatus != null) {
                onScenicLsItemClickListener!!.onCollectClick(item.id, position, item.vipResourceStatus.collectionStatus)
            }
        }
        mBinding.imgVenusScenicCollect.onNoDoubleClick {
            if (onScenicLsItemClickListener != null && item.vipResourceStatus != null) {
                onScenicLsItemClickListener!!.onCollectClick(item.id, position, item.vipResourceStatus.collectionStatus)
            }
        }
    }

    override fun payloadUpdateUi(mBinding: ItemScenicListBinding, position: Int, payloads: MutableList<Any>) {
        if (payloads[0] == "updateCollect") {
            if (getData()[position].vipResourceStatus != null) {
                val status = getData()[position].vipResourceStatus.collectionStatus
                if (status) {
                    mBinding.imgScenicCollect.setBackgroundResource(R.mipmap.provider_collect_selected)
                    mBinding.imgVenusScenicCollect.setBackgroundResource(R.mipmap.provider_collect_selected)
                } else {
                    mBinding.imgScenicCollect.setBackgroundResource(R.mipmap.provider_collect_normal)
                    mBinding.imgVenusScenicCollect.setBackgroundResource(R.mipmap.provider_collect_normal)
                }
            }
        }
    }


    /**
     * 更新收藏状态
     */
    fun notifyCollectStatus(position: Int, status: Boolean) {
        try {
            if (position < getData().size) {
                if (getData()[position].vipResourceStatus != null) {
                    getData()[position].vipResourceStatus.collectionStatus = status
                    notifyItemChanged(position, "updateCollect")
                }
            }
        } catch (e: Exception) {

        }
    }

    interface OnScenicLsItemClickListener {
        fun onCollectClick(id: String, postion: Int, status: Boolean)
    }
}