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
import com.daqsoft.guidemodule.GuideSpeakPlayer
import com.daqsoft.guidemodule.GuideSpeakStatusEvent
import com.daqsoft.guidemodule.GuideVpChangePosEvent
import com.daqsoft.guidemodule.R
import com.daqsoft.guidemodule.bean.GuideScenicListBean
import com.daqsoft.guidemodule.databinding.GuideFragmentGuideVpSpotBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.provider.utils.MenuJumpUtils
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.RxPermissions
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.toast
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
 * @Description  全域地图下的viewpager中景点
 * @ClassName   GuideVpSpotFragment
 * @Author      wongxd
 * @Time        2020/4/7 16:51
 */
internal class GuideVpSpotFragment() : BaseFragment<GuideFragmentGuideVpSpotBinding, GuideVpToiletViewModel>() {

    /**
     *  地图实体
     */
    lateinit var bean: GuideScenicListBean

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

    override fun getLayout(): Int = R.layout.guide_fragment_guide_vp_spot

    override fun injectVm(): Class<GuideVpToiletViewModel> {
        return GuideVpToiletViewModel::class.java
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
//        Log.d("wongxd", "spot-$currentIndex  ${event.tag} onVpGuideSpeakStatus")
    }

    override fun initView() {

        mBinding.currentIndex = (currentIndex + 1).toString()
        mBinding.totalIndex = totalSize.toString()
        mBinding.tvSpotName.isSelected = true

        renderSpeakStatus()

        mBinding.item = bean

        Glide.with(mBinding.iv)
            .load(bean.getImages())
            .placeholder(R.drawable.placeholder_img_fail_240_180)
            .into(mBinding.iv)

        bean.let {
            // 初始化数据
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

            if (bean.getAudio().isNullOrEmpty()) {
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
            } else if (bean.resourceType == ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE
                || bean.resourceType == ResourceType.CONTENT_TYPE_HERITAGE_PROTECT_BASE
                || bean.resourceType == ResourceType.CONTENT_TYPE_HERITAGE_TEACHING_BASE
            ) {
                ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_EXPERIENCE_DETAIL_ACTIVITY)
                    .withString("id", bean.resourceId)
                    .withString("type", ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE)
                    .navigation()
            } else {
                toast(R.string.toast_function_is_not_ready)
            }

        }

        mBinding.llRoute.onNoDoubleClick {

            if (!bean.latitude.isNullOrEmpty() && !bean.longitude.isNullOrEmpty()) {
                if (MapNaviUtils.isGdMapInstalled()) {
                    MapNaviUtils.openGaoDeNavi(
                        context, 0.0, 0.0, null,
                        bean.latitude.toDouble(), bean.longitude.toDouble(),
                        bean.address
                    )
                } else {
                    toast("非常抱歉，系统未安装高德地图")
                }
            } else {
                toast("非常抱歉，暂无位置信息")
            }
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
 * @ClassName   GuideVpSpotViewModel
 * @Author      Wongxd
 * @Time        2020/4/7 16:52
 */
internal class GuideVpSpotViewModel : BaseViewModel() {


}