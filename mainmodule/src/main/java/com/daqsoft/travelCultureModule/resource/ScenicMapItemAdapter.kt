package com.daqsoft.travelCultureModule.resource

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.ArcImageView
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemScenicListBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.DividerTextUtils
import com.jakewharton.rxbinding2.view.RxView
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit


/**
 * @Description 地图模式里面的活动item的适配器
 * @ClassName   XGalleryAdapter
 * @Author      PuHua
 * @Time        2019/12/9 15:36
 */
class ScenicMapItemAdapter(
    context: Context, lat_: Double,
    lon_: Double
) : PagerAdapter() {
    val mContext = context

    /**
     * 定位的经纬度
     */
    var mLat = lat_
    var mLon = lon_


    val menus = mutableListOf<ScenicBean>()

    override fun getCount(): Int {
        return menus.size
    }

    override fun isViewFromObject(view: View, ob: Any): Boolean {
        return view === ob
    }

    @SuppressLint("CheckResult")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mBinding: ItemScenicListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.item_scenic_list,
            null,
            false
        )
        mBinding.item = menus[position]
        var item = menus[position]
        // 判断景点是否为空
        if (item.spots.isNotEmpty()) {
            mBinding.pagerItemVenuesActivity.id = item.id.toInt()
            mBinding.indicatorItemVenuesActivity.setTotal(
                item.spots.size, intArrayOf(
                    R.mipmap.index_icon_lunbo_normal,
                    R.mipmap.index_icon_lunbo_selected
                )
            )
            mBinding.indicatorItemVenuesActivity.binViewPager(mBinding.pagerItemVenuesActivity)
            val imagePagerAdapter = object : PagerAdapter() {
                override fun getCount(): Int = item.spots.size

                override fun isViewFromObject(view: View, o: Any): Boolean = view == o

                override fun instantiateItem(container: ViewGroup, position: Int): Any {
                    var index = position % item.spots.size
                    // 根据条目所在，从imgDatas集合中获取相对应的图片
                    var imageView: ArcImageView = ArcImageView(mContext)
                    imageView.setCornerTopRightRadius(Utils.dip2px(mContext!!,2.5f))
                    imageView.setCornerBottomRightRadius(Utils.dip2px(mContext!!,2.5f))
                    Glide.with(mContext)
                        .asBitmap()
                        .load(item.spots.get(index).images)
                        .centerCrop()
                        .into(imageView)
                    // 把得到的imageView的对象，添加到viewPager，也就是contrainer中
                    container.addView(imageView)
                    return imageView
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

                }

            })
        }

        var distance = GaoDeLocation.CalculateLineDistance(
            mLat.toString
                (), mLon.toString(), item.latitude, item.longitude
        )
        if (distance != "") {
            mBinding.tvItemVenuesDistance.setText("距您" + distance)
        }

        val status = DividerTextUtils.convertString(
            StringBuilder(), item.regionName, if (item
                    .chargeStatus.equals(
                    "0"
                )
            ) "免费" else "收费", if (item
                    .orderStatus.equals("0")
            ) "不可预订" else "可预订", "开放时间：" + item.openTimeStart + "~" + item.openTimeEnd
        )
        mBinding.status = status
        RxView.clicks(mBinding.root)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_SCENIC_DETAIL)
                    .withString("id", item.id.toString())
                    .withInt("type", 1)
                    .navigation()
            }

        container.addView(mBinding.root)
        return mBinding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, ob: Any) {
//        super.destroyItem(container, position, ob)
        if (ob != null && ob is View) {
            container.removeView(ob as View?)
        }
    }

}