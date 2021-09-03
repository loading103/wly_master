package com.daqsoft.guidemodule.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.util.Log
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
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.guidemodule.*
import com.daqsoft.guidemodule.GuideSpeakPlayer
import com.daqsoft.guidemodule.GuideSpeakStatusEvent
import com.daqsoft.guidemodule.GuideTourNextSpotEvent
import com.daqsoft.guidemodule.GuideVpChangePosEvent
import com.daqsoft.guidemodule.bean.GuideLineBean
import com.daqsoft.guidemodule.bean.GuideScenicListBean
import com.daqsoft.guidemodule.databinding.GuideFragmentGuideVpSoptGuideModeBinding
import com.daqsoft.guidemodule.databinding.GuideFragmentGuideVpSpotBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.utils.MapNaviUtils
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.RxPermissions
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.toast
import java.lang.StringBuilder
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
 * @Description  线路预览及导览模式下的viewpager中景点
 * @ClassName   GuideVpSpotTourFragment
 * @Author      wongxd
 * @Time        2020/4/10 20:13
 */
internal class GuideVpSpotTourFragment() : BaseFragment<GuideFragmentGuideVpSoptGuideModeBinding, GuideVpSpotTourViewModel>() {


    /**
     *  地图实体
     */
    lateinit var bean: GuideLineBean.Detail

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

    constructor(tag: String, bean: GuideLineBean.Detail, index: Int, total: Int, lat: String, lng: String) : this() {
        this.tagFlag = tag
        this.bean = bean
        this.totalSize = total
        this.currentIndex = index
        this.lat = lat
        this.lng = lng
    }

    override fun getLayout(): Int = R.layout.guide_fragment_guide_vp_sopt_guide_mode

    override fun injectVm(): Class<GuideVpSpotTourViewModel> {
        return GuideVpSpotTourViewModel::class.java
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        permissions = RxPermissions(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    /**
     * 语音讲解关闭
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onVpGuideSpeakStatus(event: GuideSpeakStatusEvent) {
        if (event.tag != tagFlag) return
        renderSpeakStatus()
//        Log.d("wongxd", "spot-tour- $currentIndex  ${event.tag} onVpGuideSpeakStatus")
    }

    override fun initView() {

        mBinding.llNextStop.isVisible = currentIndex + 1 != totalSize
        mBinding.vNextStop.isVisible = currentIndex + 1 != totalSize

        renderSpeakStatus()

        mBinding.tvSpotName.isSelected = true

        mBinding.item = bean


        Glide.with(mBinding.iv)
            .load(bean.getImages())
            .placeholder(R.drawable.placeholder_img_fail_240_180)
            .into(mBinding.iv)

        bean.let {
            // 初始化数据
            mBinding.tvIndicator.text = Html.fromHtml("<font color='#333333'>${currentIndex + 1}</font>/<font color='#999999'>$totalSize</font>")

            if (it.latitude != 0.0 && it.longitude != 0.0) {
                val latLng = LatLng(lat.toDouble(), lng.toDouble())
                val end = LatLng(it.latitude.toDouble(), it.longitude.toDouble())
                val dis = AMapUtils.calculateLineDistance(latLng, end)
                val disDistance = if (dis > 1000) {
                    val df = DecimalFormat("0.00")
                    df.format(dis / 1000) + "KM"
                } else {
                    dis.toInt().toString() + "M"
                }
                mBinding.tvDesOnIv.text = disDistance
            } else {
                val netDis = bean.distance.toDouble() ?: 0.0
                val disDistance = if (netDis > 1000) {
                    val df = DecimalFormat("0.00")
                    df.format(netDis / 1000) + "KM"
                } else {
                    netDis.toInt().toString() + "M"
                }
                mBinding.tvDesOnIv.text = disDistance
            }
            // 下站信息
            var disDistanceStr:String?=""
            if(bean.nextDistance!=null&&bean.nextDistance>0) {
                val netDistr = bean.nextDistance
                 disDistanceStr = if (bean.nextDistance > 1000) {
                    val df = DecimalFormat("0.00")
                    df.format(bean.nextDistance / 1000) + "KM"
                } else {
                    netDistr.toInt().toString() + "M"
                }
            }

            var strDesc = StringBuilder("")
            strDesc.append(
                if (!disDistanceStr.isNullOrEmpty()) "距下一景点:${disDistanceStr}  " else ""
            )
            strDesc.append(
                if (bean.playTime != null && bean.playTime > 0) {
                    "建议游览时间：${getTime(bean.playTime)}"
                } else {
                    ""
                }
            )
            if (!strDesc.isNullOrEmpty()) {
                mBinding.tvSpotInfoDesc.text = strDesc
                mBinding.tvSpotInfoDesc.visibility = View.VISIBLE
            } else {
                mBinding.tvSpotInfoDesc.visibility = View.INVISIBLE
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


        mBinding.llSpeak.isVisible = !bean.getAudio().isNullOrEmpty()
        mBinding.vSpeak.isVisible = !bean.getAudio().isNullOrEmpty()

        mBinding.llSpeak.onNoDoubleClick {

            if (bean.getAudio().isBlank()) {
                toast(getString(R.string.guide_no_speak_info))
                return@onNoDoubleClick
            }

            val isPlaying = GuideSpeakPlayer.isPlayingByUrl(bean.getAudio(), bean.name, currentIndex)

            if (isPlaying) {
                mBinding.ivSpeak.setImageResource(R.drawable.guide_map_button_voice_play)
                GuideSpeakPlayer.stop()
            } else {
                GuideSpeakPlayer.prepareWithUrl(tagFlag, currentIndex, bean.getAudio(), bean.name)
                GuideSpeakPlayer.play()
                mBinding.ivSpeak.setImageResource(R.drawable.guide_map_button_voice_pause)
            }
        }



        mBinding.llViewDetail.onNoDoubleClick {

            if (bean.resourceType == ResourceType.CONTENT_TYPE_SCENIC_SPOTS) {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_SCENIC_SPOT_DETAI)
                    .withString("id", bean.resourceId)
                    .navigation()
            } else if (bean.resourceType == ResourceType.CONTENT_TYPE_SCENERY) {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_SCENIC_DETAIL)
                    .withString("id", bean.resourceId)
                    .navigation()
            } else {
                toast(R.string.toast_function_is_not_ready)
            }

        }

        mBinding.llRoute.onNoDoubleClick {

            if (bean.latitude != 0.0 && bean.longitude != 0.0) {
                if (MapNaviUtils.isGdMapInstalled()) {
                    MapNaviUtils.openGaoDeNavi(
                        context, 0.0, 0.0, null,
                        bean.latitude.toDouble(), bean.longitude.toDouble(),
                        bean.name
                    )
                } else {
                    toast("非常抱歉，系统未安装高德地图")
                }
            } else {
                toast("非常抱歉，暂无位置信息")
            }
        }


        mBinding.llNextStop.onNoDoubleClick {
            EventBus.getDefault().post(GuideTourNextSpotEvent(tagFlag, currentIndex, bean.oriInPointsIndex))
        }
    }


    /**
     * 渲染播放按钮的状态
     */
    private fun renderSpeakStatus() {
        if (GuideSpeakPlayer.isPlayingByUrl(bean.getAudio(), bean.name, currentIndex)) {
            mBinding.ivSpeak.setImageResource(R.drawable.guide_map_button_voice_pause)
        } else {
            mBinding.ivSpeak.setImageResource(R.drawable.guide_map_button_voice_play)
        }
    }


    override fun initData() {
    }

}


/**
 * @Description
 * @ClassName   GuideVpSpotTourViewModel
 * @Author      Wongxd
 * @Time        2020/4/10 20:14
 */
internal class GuideVpSpotTourViewModel : BaseViewModel() {


}