package com.daqsoft.guidemodule.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.guidemodule.GuideVpChangePosEvent
import com.daqsoft.guidemodule.R
import com.daqsoft.guidemodule.bean.GuideScenicListBean
import com.daqsoft.guidemodule.databinding.GuideFragmentGuideVpScenicBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.MapResBean
import com.daqsoft.provider.utils.MapNaviUtils
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.RxPermissions
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit


/**
 * @Description
 * @ClassName   GuideVpScenicFragment
 * @Author      Wongxd
 * @Time        2020/4/3 16:34
 */
internal class GuideVpScenicFragment() : BaseFragment<GuideFragmentGuideVpScenicBinding, GuideVpScenicViewModel>() {

    /**
     * 进入景区详情event
     */
    internal data class GuideVpIntoScenicDetailEvent(val tag: String, val resourceId: String, val resourceType: String)

    /**
     * 进入导览event
     */
    internal data class GuideVpIntoGuideTourEvent(val tag: String, val tourId: String)

    /**
     *  地图实体
     */
    var bean: GuideScenicListBean? = null

    /**
     *  集合大小
     */
    var totalSize: Int = 0

    /**
     * 当前页码
     */
    var currentIndex: Int = 0

    /**
     * 纬度
     */
    var lat: String = ""

    /**
     * 经度
     */
    var lng: String = ""

    /**
     *
     */
    private var permissions: RxPermissions? = null


    private var tagFlag = ""

    constructor(tag: String, bean: GuideScenicListBean, index: Int, total: Int, lat: String, lng: String) : this() {
        this.tagFlag = tag
        this.bean = bean
        this.totalSize = total
        this.currentIndex = index
        this.lat = lat
        this.lng = lng
    }

    override fun getLayout(): Int = R.layout.guide_fragment_guide_vp_scenic

    override fun injectVm(): Class<GuideVpScenicViewModel> = GuideVpScenicViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        permissions = RxPermissions(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initView() {

        mBinding.tvAreaName.isSelected = true

        mBinding.currentIndex = (currentIndex + 1).toString()
        mBinding.totalIndex = totalSize.toString()
        bean?.let {
            // 初始化数据
            mBinding.item = it


            Glide.with(mBinding.iv)
                .load(it.getImages())
                .placeholder(R.drawable.placeholder_img_fail_240_180)
                .into(mBinding.iv)

            mBinding.tvIndicator.text = Html.fromHtml("<font color='#333333'>${currentIndex + 1}</font>/<font color='#999999'>$totalSize</font>")


            var dis = if (it.distance.isNullOrEmpty()) 0f else it.distance.toFloat()
            if (!it.latitude.isNullOrEmpty() && !it.longitude.isNullOrEmpty()) {
                val latLng = LatLng(lat.toDouble(), lng.toDouble())
                val end = LatLng(it.latitude.toDouble(), it.longitude.toDouble())
                dis = AMapUtils.calculateLineDistance(latLng, end)
            }

            val disDistance = if (dis > 1000) {
                val df = DecimalFormat("0.00")
                df.format(dis / 1000) + "KM"
            } else {
                dis.toInt().toString() + "M"
            }
            mBinding.tvDesOnIv.text = disDistance

            mBinding.llViewDetail.isVisible = !it.resourceId.isNullOrEmpty()

            mBinding.vIntoGuidedTour.isVisible = !it.tourId.isNullOrEmpty()
            mBinding.llIntoGuidedTour.isVisible = !it.tourId.isNullOrEmpty()

        }
        initViewEvent()
    }

    @SuppressLint("CheckResult")
    private fun initViewEvent() {

        RxView.clicks(mBinding.ivArrowLeft)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                val newPos = currentIndex - 1
                if (newPos >= 0)
                    EventBus.getDefault().post(GuideVpChangePosEvent(tagFlag, newPos))
            }

        RxView.clicks(mBinding.ivArrowRight)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                val newPos = currentIndex + 1
                if (newPos < totalSize)
                    EventBus.getDefault().post(GuideVpChangePosEvent(tagFlag, newPos))
            }


        RxView.clicks(mBinding.llViewDetail)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                bean?.let {
                    EventBus.getDefault().post(GuideVpIntoScenicDetailEvent(tagFlag, it.resourceId, it.resourceType))
                }
            }


        RxView.clicks(mBinding.llIntoGuidedTour)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                bean?.let {
                    EventBus.getDefault().post(GuideVpIntoGuideTourEvent(tagFlag, it.tourId))
                }

            }
    }


    override fun initData() {
    }

}


/**
 * @Description
 * @ClassName   GuideVpScenicViewModel
 * @Author      Wongxd
 * @Time        2020/4/3 16:37
 */
internal class GuideVpScenicViewModel : BaseViewModel() {

}