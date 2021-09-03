package com.daqsoft.guidemodule.scenicList

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.guidemodule.R
import com.daqsoft.guidemodule.bean.GuideHomeListBean
import com.daqsoft.guidemodule.databinding.GuideItemScenicListBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.MapNaviUtils
import com.jakewharton.rxbinding2.view.RxView
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit


/**
 * @Description
 * @ClassName   GuideScenicLsAdapter
 * @Author      Wongxd
 * @Time        2020/4/3 9:55
 */
internal class GuideScenicListAdapter(context: Context) : RecyclerViewAdapter<GuideItemScenicListBinding, GuideHomeListBean>
    (R.layout.guide_item_scenic_list) {
    var mContext: Context? = context

    /**
     * 当前经纬度
     */
    var selfLocation: LatLng? = null


    @SuppressLint("CheckResult")
    override fun setVariable(
        mBinding: GuideItemScenicListBinding,
        position: Int,
        item: GuideHomeListBean
    ) {

        mBinding.item = item
        mContext?.let {
            Glide.with(it).load(item.getImages()).placeholder(R.mipmap.placeholder_img_fail_h158).into(mBinding.iv)
        }
        if (selfLocation != null && selfLocation?.latitude != null && selfLocation?.longitude != null && item.latitude != null && item.longitude != null) {
            val distance = GaoDeLocation.CalculateLineDistance(
                selfLocation?.latitude.toString(), selfLocation?.longitude.toString(), item.latitude, item.longitude
            )

            if (!distance.isNullOrEmpty()) {
                mBinding.tvDis.visibility = View.VISIBLE
                mBinding.tvDis.text = "距您${distance}"
            } else {
                mBinding.tvDis.visibility = View.INVISIBLE
            }

        } else {

            if (!item.distance.isNullOrEmpty()) {
                mBinding.tvDis.visibility = View.VISIBLE
                mBinding.tvDis.text = "距您${item.distance}KM"
            } else {
                mBinding.tvDis.visibility = View.INVISIBLE
            }

        }

        RxView.clicks(mBinding.root)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_SCENIC_DETAIL)
                    .withString("id", item.id.toString())
                    .navigation()
            }


        if (!item.latitude.isNullOrEmpty() && !item.latitude.isNullOrEmpty()) {
            mBinding.llRoute.visibility = View.VISIBLE
        } else {
            mBinding.llRoute.visibility = View.GONE
        }

        RxView.clicks(mBinding.llRoute)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                if (!item.latitude.isNullOrEmpty() && !item.latitude.isNullOrEmpty()) {
                    if (MapNaviUtils.isGdMapInstalled()) {
                        MapNaviUtils.openGaoDeNavi(
                            BaseApplication.context, 0.0, 0.0, null,
                            item.latitude.toDouble(), item.longitude.toDouble(),
                            item.name
                        )
                    } else {
                        mContext?.toast("非常抱歉，系统未安装地图软件")
                    }
                } else {
                    mContext?.toast("非常抱歉，暂无位置信息")
                }
            }

        mBinding.root.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.GuideModule.GUIDE_TOUR_ACTIVITY)
                .withString("tourId", item.id)
                .withString("allAreaTourId", "")
                .navigation()
        }

    }

}