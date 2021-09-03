package com.daqsoft.travelCultureModule.country.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.dialog.QrCodeDialog
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.FragCountryDetailTopBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.adapter.ScenicTopAdapter
import com.daqsoft.provider.bean.CountryDetailBean
import com.daqsoft.provider.businessview.fragment.PanoramaFragment
import com.daqsoft.provider.event.UpdateAudioPlayerEvent
import com.daqsoft.provider.event.UpdateScenicVideoPlayerEvent
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.provider.view.BaseDialog
import com.daqsoft.provider.view.ScenicPageTransformer
import com.daqsoft.travelCultureModule.resource.adapter.ScenicLabelAdapter
import com.daqsoft.travelCultureModule.resource.fragment.ScenicImageFragment
import com.daqsoft.travelCultureModule.resource.fragment.ScenicVideoFragment
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

/**
 * 乡村详情顶部
 */
class CountryDetailTopFragment : BaseFragment<FragCountryDetailTopBinding, CountryDetailViewModel>() {
    /**
     * 乡村标签适配器
     */
    var scenicLabelAdapter: ScenicLabelAdapter? = null

    /**
     * 乡村详情实体
     */
    var mCountryDetailBean: CountryDetailBean? = null

    var scenicTopAdapter: ScenicTopAdapter? = null

    /**
     * 乡村图片
     */
    var mScenicImages: MutableList<String>? = mutableListOf()

    /**
     * 标识是否存在视频
     */
    var isHaveVideo: Boolean = false

    /**
     * 滑动乡村视频-720-图片fragments
     */
    var mDatasSenicTopFrags: MutableList<Fragment> = mutableListOf()

    /**
     * 二维码dialog
     */
    var mQrCodeDialog: QrCodeDialog? = null

    /**
     * 图片多少
     */
    var imageSize: Int = 0

    /**
     * 拨打电话dialog
     */
    var phoneDialog: BaseDialog? = null
    var scenicVideoFrag: ScenicVideoFragment? = null

    var index: Int = 0

    // 保存订阅者
    var subscribe: Disposable? = null
    var isContinue = true
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (msg != null) {
                when (msg.what) {
                    1 -> {
                        index++
                        if (index == scenicTopAdapter!!.count) {
                            index = 0
                        }
                        mBinding.vpScenicDetailInfo.currentItem = index
                    }
                }
            }
        }
    }


    companion object {
        fun newInstance(bean: CountryDetailBean): CountryDetailTopFragment {
            var frag = CountryDetailTopFragment()
            var bundle = Bundle()
            bundle.putParcelable("bean", bean)
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayout(): Int {
        return R.layout.frag_country_detail_top
    }

    override fun injectVm(): Class<CountryDetailViewModel> {
        return CountryDetailViewModel::class.java
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        getParam()
        initViewEvent()
        initPhoneDialog()
    }

    override fun onResume() {
        super.onResume()
        try {
            isContinue = true
            subscribe?.dispose()
            subscribe = null
            subscribe = Observable.interval(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (isContinue) {
                        handler.sendEmptyMessage(1)
                    }
                }
        } catch (e: java.lang.Exception) {
        }

    }


    private fun initViewEvent() {
        mBinding.tvScenicDetailsAddressValue.onNoDoubleClick {
            if (mCountryDetailBean != null && (mCountryDetailBean?.latitude ?: 0.0) > 0 && (mCountryDetailBean?.longitude ?: 0.0) > 0) {
                if (MapNaviUtils.isGdMapInstalled()) {
                    MapNaviUtils.openGaoDeNavi(
                        BaseApplication.context, 0.0, 0.0, null,
                        mCountryDetailBean!!.latitude.toDouble(), mCountryDetailBean!!.longitude.toDouble(),
                        mCountryDetailBean!!.cutRegionName
                    )
                } else {
                    mModel.toast.postValue("非常抱歉，系统未安装地图软件")
                }
            } else {
                mModel.toast.postValue("非常抱歉，暂无位置信息")
            }
        }
        mBinding.tvScenicDetailsPhone.onNoDoubleClick {
            phoneDialog?.show()
        }
        // 微信公众号
//        mBinding.tvScenicWexQrcode.onNoDoubleClick {
//            dealShowQrCode()
//        }
        // 官方网址
//        mBinding.tvScenicWebsiteValue.onNoDoubleClick {
//            ARouter.getInstance()
//                .build(ARouterPath.Provider.WEB_ACTIVITY)
//                .withString("mTitle", mCountryDetailBean?.name)
//                .withString("html", StringUtil.formatHtmlUrl(mCountryDetailBean?.websiteUrl))
//                .navigation()
//        }
        mBinding.vpScenicDetailInfo.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {

                index = position
                if (isHaveVideo && position != 0) {
                    EventBus.getDefault().post(UpdateScenicVideoPlayerEvent(1))
                }
                var offsetPos = -1
                if (mCountryDetailBean != null && !mCountryDetailBean!!.panoramaUrl.isNullOrEmpty()) offsetPos += 1
                if (isHaveVideo) offsetPos += 1
                if (position > offsetPos && imageSize > 0) {
                    mBinding.txtScenicDetailTopImgIndex.text = "${position - offsetPos}/${imageSize}"
                }
            }

        })
        mBinding.vScenicDetail720.onNoDoubleClick {
            if (isHaveVideo) {
                mBinding.vpScenicDetailInfo?.setCurrentItem(1, true)
            } else {
                mBinding.vpScenicDetailInfo?.setCurrentItem(0, true)
            }
        }
        mBinding.vScenicDetailVideo.onNoDoubleClick {
            mBinding.vpScenicDetailInfo?.setCurrentItem(0, true)
            if (imageSize > 0) {
                mBinding.txtScenicDetailTopImgIndex.text = "1/${imageSize}"
            }
        }
        mBinding.vScenicDetailImages.onNoDoubleClick {
            var offset: Int = -1
            if (isHaveVideo) offset += 1
            if (mCountryDetailBean != null && !mCountryDetailBean!!.panoramaUrl.isNullOrEmpty()) offset += 1
            mBinding.vpScenicDetailInfo.setCurrentItem(offset + 1, true)
            if (imageSize > 0) {
                mBinding.txtScenicDetailTopImgIndex.text = "1/${imageSize}"
            }
        }
        mBinding.tvWeather.onNoDoubleClick {
            if (mCountryDetailBean != null && !mCountryDetailBean!!.region.isNullOrEmpty()) {
                ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("html", StringUtil.getWeatherUrl(mCountryDetailBean!!.region))
                    .withString("mTitle", "天气信息")
                    .navigation()
            }
        }
    }

    private fun getParam() {
        var bean = arguments?.getParcelable<CountryDetailBean>("bean")
        if (bean != null) {
            mBinding.bean = bean
            mCountryDetailBean = bean
        }
        bindViewData()
    }

    private fun bindViewData() {
        mCountryDetailBean?.let {

            // 状态处理
            val suggestedTime = if (it.suggestedTime.isNullOrEmpty()) {
                ""
            } else {
                "建议游玩时长：${it.suggestedTime}"
            }
            val bestTravelTime = if (it.bestTravel.isNullOrEmpty()) {
                ""
            } else {
                "最佳旅游时间：${it.bestTravel}"
            }
            val cutRegionName = if (it.cutRegionName.isNullOrEmpty()) {
                ""
            } else {
                it.cutRegionName
            }
            var status = DividerTextUtils.convertString(
                StringBuilder(), cutRegionName, if (it
                        .isCharge == 0
                ) "免费" else "收费", if (!it.openTimeStart.isNullOrEmpty()) {
                    "开放时间：" + it
                        .openTimeStart + "~" + it.openTimeEnd
                } else {
                    ""
                }, suggestedTime, bestTravelTime
            )
            //
            try {
                var moreInfo = "更多信息 >"
                status += moreInfo
                var spanStatus = SpannableString(status)
                spanStatus.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.colorPrimary)),
                    status.length - moreInfo.length, status.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
//            var d: Drawable = getResources().getDrawable(R.mipmap.more_right_arrow)
//            d.setBounds(
//                0, 0, Utils.dip2px(this, 4.5f).toInt(), Utils.dip2px(this, 9f).toInt()
//            )
//            var imageSpan = ImageSpan(d, ImageSpan.ALIGN_BASELINE)
//            spanStatus.setSpan(imageSpan, status.length - 3, status.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                mBinding.tvStatus.text = spanStatus
            } catch (e: Exception) {
            }


            RxView.clicks(mBinding.tvStatus)
                // 1秒内不可重复点击或仅响应一次事件
                .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                    ARouter.getInstance()
                        .build(ARouterPath.CountryModule.COUNTRY_DETAIL_MORE_ACTIVITY)
                        .withString("id", mCountryDetailBean?.id.toString())
                        .navigation()
                }
            // 天气信息
//            if (!it.weather.max.isNullOrEmpty() && !it.weather.min.isNullOrEmpty() && !it.weather.pic.isNullOrEmpty()) {
//                mBinding.tvWeather.text = it.weather.txt + "\n" + getString(
//                    R.string.home_weather_str, it.weather.min + "~" + it
//                        .weather.max
//                )
//                Glide.with(this)
//                    .asBitmap()
//                    .load(it.weather.pic)
//                    .centerCrop()
//                    .into(object : CustomTarget<Bitmap>() {
//
//                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                            var drawable = BitmapDrawable(resource)
//                            mBinding.tvWeather.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
//                        }
//
//                        override fun onLoadCleared(placeholder: Drawable?) {
//
//                        }
//                    })
//            } else {
//                mBinding.tvWeather.visibility = View.GONE
//            }
            // 图片相关
            val images = it.images.split(",")
            if (!images.isNullOrEmpty()) {
                mScenicImages?.clear()
                mScenicImages?.addAll(images)
            }
            mDatasSenicTopFrags.clear()
            // 视频 720 和图片
            if (!it.video.isNullOrEmpty()) {
                scenicVideoFrag = ScenicVideoFragment.newInstance(it.video)
                mDatasSenicTopFrags.add(scenicVideoFrag!!)
                isHaveVideo = true
                mBinding.vScenicDetailVideo.visibility = View.VISIBLE
            } else {
                mBinding.vScenicDetailVideo.visibility = View.GONE
            }
            if (!it.panoramaUrl.isNullOrEmpty()) {
                var cover: String ?= it.panoramaCover
                mDatasSenicTopFrags.add(PanoramaFragment.newInstance(it.panoramaUrl, cover, it.name))
                mBinding.vScenicDetail720.visibility = View.VISIBLE
            } else {
                mBinding.vScenicDetail720.visibility = View.GONE
            }
            if (!images.isNullOrEmpty()) {
                try {
                    for (i in images.indices) {
                        val item = images[i]
                        mDatasSenicTopFrags.add(ScenicImageFragment.newInstance(item, images as MutableList<String>, i))
                    }
                } catch (e: Exception) {

                }

                imageSize = images.size
                mBinding.txtScenicDetailTopImgIndex.text = "1/${imageSize}"
                mBinding.vScenicDetailImages.visibility = View.VISIBLE
            } else {
                mBinding.vScenicDetailImages.visibility = View.GONE
            }
            if (!mDatasSenicTopFrags.isNullOrEmpty()) {
                mBinding.vpScenicDetailInfo.setPageTransformer(false, ScenicPageTransformer())
                scenicTopAdapter = ScenicTopAdapter(mDatasSenicTopFrags, childFragmentManager)
                mBinding.vpScenicDetailInfo.adapter = scenicTopAdapter
                mBinding.vpScenicDetailInfo.offscreenPageLimit = mDatasSenicTopFrags.size
                mBinding.vpScenicDetailInfo.visibility = View.VISIBLE

            } else {
                mBinding.vpScenicDetailInfo.visibility = View.GONE
            }
        }
    }

    override fun initData() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateAudioPlayerState(event: UpdateAudioPlayerEvent) {
        if (event.type == 1) {
            mBinding.llVideoBottomButton.visibility = View.GONE
        }
        if (event.type == 2) {
            mBinding.llVideoBottomButton.visibility = View.VISIBLE
        }
    }

    private fun initPhoneDialog() {
        phoneDialog = BaseDialog(context!!)
        phoneDialog!!.contentView(R.layout.dialog_delete_story)
            .layoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            .gravity(Gravity.CENTER)
            .animType(BaseDialog.AnimInType.BOTTOM)
            .canceledOnTouchOutside(false)
        phoneDialog!!.findViewById<TextView>(R.id.tv_title).text = "拨打电话"
        phoneDialog!!.findViewById<TextView>(R.id.tv_content).text = "是否立即拨打电话？"
        phoneDialog!!.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            phoneDialog!!.dismiss()
        }
        phoneDialog!!.findViewById<TextView>(R.id.tv_query).setOnClickListener {
            phoneDialog!!.dismiss()
            mCountryDetailBean?.let {
                SystemHelper.callPhone(context!!, it.phone)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        isContinue = false
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        if (subscribe != null) {
            if (!subscribe!!.isDisposed) {
                subscribe?.dispose()
                subscribe = null
            }
        }

        mDatasSenicTopFrags.clear()
        scenicTopAdapter = null
        EventBus.getDefault().unregister(this)
    }
}

class CountryDetailViewModel : BaseViewModel() {

}