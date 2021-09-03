package com.daqsoft.guidemodule.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.guidemodule.GuideVpChangePosEvent
import com.daqsoft.guidemodule.R
import com.daqsoft.guidemodule.bean.GuideScenicListBean
import com.daqsoft.guidemodule.databinding.GuideFragmentGuideVpToiletNoImgBinding
import com.daqsoft.provider.utils.MapNaviUtils
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.RxPermissions
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.support.v4.toast
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
 * @Description  全域地图下的viewpager中厕所
 * @ClassName   GuideVpToiletNoImageFragment
 * @Author      wongxd
 * @Time        2020/4/7 16:37
 */
internal class GuideVpToiletNoImageFragment() : BaseFragment<GuideFragmentGuideVpToiletNoImgBinding, GuideVpToiletViewModel>() {

    /**
     *  厕所地图实体
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

    override fun getLayout(): Int = R.layout.guide_fragment_guide_vp_toilet_no_img

    override fun injectVm(): Class<GuideVpToiletViewModel> {
        return GuideVpToiletViewModel::class.java
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        permissions = RxPermissions(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initView() {

        bean?.let {
            // 初始化数据
            mBinding.currentIndex = (currentIndex + 1).toString()
            mBinding.totalIndex = totalSize.toString()
            mBinding.tvToiletName.isSelected = true
            mBinding.tvToiletName.text = it.name
            mBinding.tvIndicator.text = Html.fromHtml("<font color='#333333'>${currentIndex + 1}</font>/<font color='#999999'>$totalSize</font>")

            if (!it.latitude.isNullOrEmpty() && !it.longitude.isNullOrEmpty()) {
                val latLng = LatLng(lat.toDouble(), lng.toDouble())
                val end = LatLng(it.latitude.toDouble(), it.longitude.toDouble())
                val dis = AMapUtils.calculateLineDistance(latLng, end)
                val disDistance = if (dis > 1000) {
                    val df = DecimalFormat("0.00")
                    df.format(dis / 1000) + "KM"
                } else {
                    dis.toInt().toString() + "M"
                }
                mBinding.tvDis.text = "距您:$disDistance"
            } else {
                val netDis = bean?.distance?.toDouble() ?: 0.0
                val disDistance = if (netDis > 1000) {
                    val df = DecimalFormat("0.00")
                    df.format(netDis / 1000) + "KM"
                } else {
                    netDis.toInt().toString() + "M"
                }
                mBinding.tvDis.text = "距您:$disDistance"
            }

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

        RxView.clicks(mBinding.llRoute)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                if (bean != null && !bean?.latitude.isNullOrEmpty() && !bean?.longitude.isNullOrEmpty()) {
                    if (MapNaviUtils.isGdMapInstalled()) {
                        MapNaviUtils.openGaoDeNavi(
                            context, 0.0, 0.0, null,
                            bean!!.latitude.toDouble(), bean!!.longitude.toDouble(),
                            bean?.address
                        )
                    } else {
                        toast("非常抱歉，系统未安装高德地图")
                    }
                } else {
                    toast("非常抱歉，暂无位置信息")
                }
            }
    }


    override fun initData() {
    }

}

