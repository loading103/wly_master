package com.daqsoft.travelCultureModule.resource.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.dialog.QrCodeDialog
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.FragScenicDetialTopBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.adapter.ScenicTopAdapter
import com.daqsoft.provider.bean.FoundAroundBean
import com.daqsoft.provider.bean.ScenicComfortBean
import com.daqsoft.provider.bean.ScenicDetailBean
import com.daqsoft.provider.bean.ScenicHealthBean
import com.daqsoft.provider.businessview.fragment.PanoramaFragment
import com.daqsoft.provider.event.UpdateScenicVideoPlayerEvent
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.provider.network.venues.bean.ScenicLabelBean
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.provider.utils.StringUtils
import com.daqsoft.provider.view.BaseDialog
import com.daqsoft.provider.view.ScenicPageTransformer
import com.daqsoft.travelCultureModule.net.MainRepository
import com.daqsoft.travelCultureModule.resource.adapter.ScenicLabelAdapter
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.textColor
import java.util.concurrent.TimeUnit
import org.jetbrains.anko.append

/**
 * @Description 景区详情顶部
 * @ClassName   ScenicDetailTopFragment
 * @Author      luoyi
 * @Time        2020/6/2 11:14
 */
class ScenicDetailTopFragment : BaseFragment<FragScenicDetialTopBinding, ScenicDetailViewModel>() {
    /**
     * 景区标签适配器
     */
    var scenicLabelAdapter: ScenicLabelAdapter? = null

    /**
     * 景区详情实体
     */
    var mScenicDetailBean: ScenicDetailBean? = null

    var scenicTopAdapter: ScenicTopAdapter? = null

    /**
     * 景区图片
     */
    var mScenicImages: MutableList<String>? = mutableListOf()

    /**
     * 标识是否存在视频
     */
    var isHaveVideo: Boolean = false

    /**
     * 滑动景区视频-720-图片fragments
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
                        if(scenicTopAdapter!=null){
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
    }


    companion object {
        fun newInstance(bean: ScenicDetailBean): ScenicDetailTopFragment {
            var frag = ScenicDetailTopFragment()
            var bundle = Bundle()
            bundle.putParcelable("bean", bean)
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayout(): Int {
        return R.layout.frag_scenic_detial_top
    }

    override fun injectVm(): Class<ScenicDetailViewModel> {
        return ScenicDetailViewModel::class.java
    }

    override fun initView() {
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
            if (mScenicDetailBean != null && !mScenicDetailBean?.latitude.isNullOrEmpty() && !mScenicDetailBean?.longitude.isNullOrEmpty()) {
                if (MapNaviUtils.isGdMapInstalled()) {
                    MapNaviUtils.openGaoDeNavi(
                        BaseApplication.context,
                        0.0,
                        0.0,
                        null,
                        mScenicDetailBean!!.latitude.toDouble(),
                        mScenicDetailBean!!.longitude.toDouble(),
                        mScenicDetailBean!!.regionName
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
        mBinding.tvScenicWexQrcode.onNoDoubleClick {
            dealShowQrCode()
        }
        mBinding.tvScenicWebsiteValue.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("mTitle", mScenicDetailBean?.name)
                .withString("html", StringUtil.formatHtmlUrl(mScenicDetailBean?.websiteUrl))
                .navigation()
        }
        mBinding.vpScenicDetailInfo.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {

                index = position
                if (isHaveVideo && position != 0) {
                    EventBus.getDefault().post(UpdateScenicVideoPlayerEvent(1))
                }
                var offsetPos = -1
                if (mScenicDetailBean != null && !mScenicDetailBean!!.panoramaUrl.isNullOrEmpty()) offsetPos += 1
                if (isHaveVideo) offsetPos += 1
                if (position > offsetPos && imageSize > 0) {
                    mBinding.txtScenicDetailTopImgIndex.text =
                        "${position - offsetPos}/${imageSize}"
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
            if (mScenicDetailBean != null && !mScenicDetailBean!!.panoramaUrl.isNullOrEmpty()) offset += 1
            mBinding.vpScenicDetailInfo.setCurrentItem(offset + 1, true)
            if (imageSize > 0) {
                mBinding.txtScenicDetailTopImgIndex.text = "1/${imageSize}"
            }
        }
        mBinding.tvWeather.onNoDoubleClick {
            if (mScenicDetailBean != null && !mScenicDetailBean!!.region.isNullOrEmpty()) {
                ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("html", StringUtil.getWeatherUrl(mScenicDetailBean!!.region))
                    .withString("mTitle", "天气信息")
                    .navigation()
            }
        }
    }

    private fun getParam() {
        var bean = arguments?.getParcelable<ScenicDetailBean>("bean")
        if (bean != null) {
            mBinding.bean = bean
            mScenicDetailBean = bean
        }
        bindViewData()
    }

    private fun bindViewData() {
        mScenicDetailBean?.let {
            if (it.scenicHealthCode.isNullOrEmpty()) {
                mBinding.scenicHealthCode = null
            } else {
                if (BaseApplication.appArea == "sc") {
                    mModel.getScenicHealthInfo(it.scenicHealthCode!!)
                }
            }
            if (BaseApplication.appArea == "xj") {
                if (!it.resourceCode.isNullOrEmpty()) {
                    var beginTime = DateUtil.getCurrentTime(2)
                    var endTime = DateUtil.getCurrentTime(1)
                    mModel.getScenicCompots(it.resourceCode, beginTime, endTime)
                }
            }

            // 标签处理
            if (!it.level.isNullOrEmpty() && !it.theme.isNullOrEmpty()) {
                val tags = mutableListOf<ScenicLabelBean>()
                var num = it.level.count {
                    it == 'A'
                }
                if (num > 0) {
                    tags.add(ScenicLabelBean("${num}A", 1))
                }


                for (item in it.theme) {
                    tags.add(ScenicLabelBean(item, 3))
                }
                mBinding.flyScenicTags.removeAllViews()
                if (!tags.isNullOrEmpty()) {
                    for (i in tags.indices) {
                        var vi = TextView(context!!)
                        var lp = ViewGroup.MarginLayoutParams(
                            ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams
                                .WRAP_CONTENT
                        )
                        lp.rightMargin = resources.getDimension(R.dimen.dp_4).toInt()
                        lp.topMargin = resources.getDimension(R.dimen.dp_4).toInt()
                        vi.layoutParams = lp
                        vi.setPadding(
                            resources.getDimension(R.dimen.dp_3).toInt(), resources.getDimension(
                                R
                                    .dimen.dp_2
                            ).toInt(), resources.getDimension(R.dimen.dp_3).toInt(), resources
                                .getDimension(R.dimen.dp_2).toInt()
                        )
                        vi.textSize = 11f
                        var tag = tags[i]
                        if (i == 0) {
                            vi.background =
                                resources.getDrawable(R.drawable.shape_scenic_r2_cprimary)
                            vi.textColor = resources.getColor(R.color.colorPrimary)
                        } else {
                            vi.background =
                                resources.getDrawable(R.drawable.shape_scenic_r2_cprimarysecond)
                            vi.textColor = resources.getColor(R.color.colorPrimarySecond)
                        }
                        vi.text = "" + tag.name
                        mBinding.flyScenicTags.addView(vi)
                    }
                }
                mBinding.flyScenicTags.visibility = View.VISIBLE
            } else {
                mBinding.flyScenicTags.visibility = View.GONE
            }


            // 状态处理
            val suggestedTime = if (it.suggestedTime.isNullOrEmpty()) {
                ""
            } else {
                "建议游玩时长：${it.suggestedTime}"
            }
            val bestTravelTime = if (it.bestTravelTime.isNullOrEmpty()) {
                ""
            } else {
                "最佳旅游时间：${it.bestTravelTime}"
            }
            val cutRegionName = if (it.cutRegionName.isNullOrEmpty()) {
                ""
            } else {
                it.cutRegionName
            }
            val crowd = if (it.crowd.isNullOrEmpty()) {
                ""
            } else {
                "适合人群:" + StringUtil.arrayToString(it.crowd!!)
            }
            var status = DividerTextUtils.convertString(
                StringBuilder(), cutRegionName, if (it
                        .chargeStatus == "0"
                ) "免费" else "收费", if (!it.openTimeStart.isNullOrEmpty()) {
                    "开放时间：" + it
                        .openTimeStart + "~" + it.openEndTime
                } else {
                    ""
                }, suggestedTime, bestTravelTime, crowd
            )
            //
            try {
                var moreInfo = "更多信息 >"
                status += moreInfo
                var spanStatus = SpannableString(status)
                spanStatus.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.colorPrimary)),
                    status.length - moreInfo.length,
                    status.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
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
                        .build(MainARouterPath.MAIN_SCENIC_DETAIL_MORE)
                        .withString("id", mScenicDetailBean!!.id)
                        .navigation()
                }
            // 天气信息
            if (!it.weather.max.isNullOrEmpty() && !it.weather.min.isNullOrEmpty() && !it.weather.pic.isNullOrEmpty()) {
                mBinding.tvWeather.text = it.weather.txt + "\n" + getString(
                    R.string.home_weather_str, it.weather.min + "~" + it
                        .weather.max
                )
                Glide.with(this)
                    .asBitmap()
                    .load(it.weather.pic)
                    .centerCrop()
                    .into(object : CustomTarget<Bitmap>() {

                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            var drawable = BitmapDrawable(resource)
                            mBinding.tvWeather.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                drawable,
                                null,
                                null
                            )
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })
            } else {
                mBinding.tvWeather.visibility = View.GONE
            }
            // 图片相关
            val images = it.images.split(",")
            if (!images.isNullOrEmpty()) {
                mScenicImages?.clear()
                mScenicImages?.addAll(images)
            }
            if (it.goldStory != null) {
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
                var cover: String? = it.panoramaCover
                mDatasSenicTopFrags.add(
                    PanoramaFragment.newInstance(
                        it.panoramaUrl,
                        cover,
                        it.name
                    )
                )
                mBinding.vScenicDetail720.visibility = View.VISIBLE
            } else {
                mBinding.vScenicDetail720.visibility = View.GONE
            }
            if (!images.isNullOrEmpty()) {
                try {
                    for (i in images.indices) {
                        val item = images[i]
                        if(!item.isNullOrEmpty()){
                            mDatasSenicTopFrags.add(
                                ScenicImageFragment.newInstance(
                                    item,
                                    images as MutableList<String>,
                                    i
                                )
                            )
                        }

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
                mBinding.vScenicDetailImages.visibility = View.GONE
            }

        }
    }

    override fun initData() {
        mModel.scenicHealthIndexLd.observe(this, Observer {
            if (it != null && !it.ysdValue.isNullOrEmpty()) {
                mBinding.scenicHealthCode = it.ysdValue
                if (!it.ysdTxt.isNullOrEmpty()) {
                    mBinding.scenicHealthLevel = "康养环境舒适度 ${it.ysdTxt}"
                    mBinding.tvScenicDetailsScenicHealthLevel.visibility = View.VISIBLE
                } else {
                    mBinding.tvScenicDetailsScenicHealthLevel.visibility = View.GONE
                }
            } else {
                mBinding.scenicHealthCode = null
            }
        })
        mModel.scenicCompotLd.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                var bean = it[0]

                bean?.let {
                    if (!bean.rtnumber.isNullOrEmpty() && !bean.frontmax.isNullOrEmpty()) {
                        mBinding.tvScenicDetailsScenicCmfort.visibility = View.VISIBLE
                        mBinding.tvScenicDetailsScenicCmfortValue.visibility = View.VISIBLE
                        var stringBuilder = SpannableStringBuilder("")
                        var firstStr = "当前入园人数 "
                        stringBuilder.append(
                            firstStr,
                            ForegroundColorSpan(context!!.resources.getColor(R.color.color_666)),
                            0,
                            firstStr.length
                        )
                        var secondStr = "${bean.rtnumber}"
                        stringBuilder.append(
                            secondStr,
                            ForegroundColorSpan(
                                context!!.resources.getColor(R.color.color_333)
                            ),
                            0,
                            secondStr.length
                        )

                        var threeStr = "/${bean.frontmax} "
                        stringBuilder.append(
                            threeStr,
                            ForegroundColorSpan(context!!.resources.getColor(R.color.color_999)),
                            0,
                            threeStr.length

                        )
                        // 0 - 30 - 60 - 80 - 100
                        if(bean.frontmax!!.toInt()>0) {
                            var percent: Int =
                                (bean.rtnumber!!.toInt() / bean.frontmax!!.toInt()) * 100
                            var fourStr = when (percent) {
                                in 0..30 -> {
                                    context!!.resources.getString(R.string.compot_level_1)
                                }
                                in 31..60 -> {
                                    context!!.resources.getString(R.string.compot_level_2)
                                }
                                in 61..80 -> {
                                    context!!.resources.getString(R.string.compot_level_3)
                                }
                                in 81..100 -> {
                                    context!!.resources.getString(R.string.compot_level_4)
                                }
                                else -> {
                                    ""
                                }
                            }
                            stringBuilder.append(
                                fourStr,
                                ForegroundColorSpan(context!!.resources.getColor(R.color.app_second_color)),
                                BackgroundColorSpan(resources.getColor(R.color.app_second_forbidden))
                            )
                        }

                        mBinding.tvScenicDetailsScenicCmfortValue.text = stringBuilder
                    }

                }
            } else {
                mBinding.tvScenicDetailsScenicCmfort.visibility = View.GONE
                mBinding.tvScenicDetailsScenicCmfortValue.visibility = View.GONE
            }
        })
    }

    /**
     * 处理二维码显示
     */
    private fun dealShowQrCode() {
        if (mScenicDetailBean != null && !mScenicDetailBean!!.officialUrl.isNullOrEmpty()) {
            if (mQrCodeDialog == null) {
                mQrCodeDialog =
                    QrCodeDialog.Builder().qrCodeImageUrl(mScenicDetailBean!!.officialUrl)
                        .title(mScenicDetailBean!!.officialName)
                        .onDownLoadListener(object : QrCodeDialog.OnDownLoadListener {
                            override fun onDownLoadImage(url: String) {
                                try {
                                    showLoadingDialog()
                                    DownLoadFileUtil.downNetworkImage(
                                        url,
                                        object : DownLoadFileUtil.DownImageListener {
                                            override fun onDownLoadImageSuccess() {
                                                dissMissLoadingDialog()
                                                ToastUtils.showMessage("保存二维码成功~")
                                            }
                                        })
                                } catch (e: Exception) {
                                    dissMissLoadingDialog()
                                    ToastUtils.showMessage("保存二维码失败~")
                                }
                            }

                        })
                        .build(context!!)
            } else {
                mQrCodeDialog?.updateData(mScenicDetailBean!!.officialUrl, mScenicDetailBean!!.officialName)
            }
            mQrCodeDialog!!.show()
        }
    }

    private fun initPhoneDialog() {
        phoneDialog = BaseDialog(context!!)
        phoneDialog!!.contentView(R.layout.dialog_delete_story)
            .layoutParams(
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
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
            mScenicDetailBean?.let {
                SystemHelper.callPhone(context!!,it.phone)
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
        if(scenicLabelAdapter!=null){
            scenicTopAdapter = null
        }
    }
}

class ScenicDetailViewModel : BaseViewModel() {
    var scenicHealthIndexLd: MutableLiveData<ScenicHealthBean> = MutableLiveData()

    var scenicCompotLd: MutableLiveData<MutableList<ScenicComfortBean>> = MutableLiveData()

    /**
     * 获取景区氧生度
     */
    fun getScenicHealthInfo(areaHealthId: String) {
        MainRepository.service.getScenicHealthIndex(areaHealthId)
            .excute(object : BaseObserver<ScenicHealthBean>() {
                override fun onSuccess(response: BaseResponse<ScenicHealthBean>) {
                    scenicHealthIndexLd?.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<ScenicHealthBean>) {
                    scenicHealthIndexLd?.postValue(response.data)
                }

            })
    }

    /**
     * 获取景区舒适度
     */
    fun getScenicCompots(resourecCode: String, beginTime: String, endTime: String) {
        StatisticsRepository.instance.service.getMaxRealPeole(
            "fDbcwjxdSJoc",
            "WQDBDcvBRAKMIyaSIWnuTcxWO",
            resourecCode,
            beginTime, endTime
        )
            .excute(object : BaseObserver<ScenicComfortBean>() {
                override fun onSuccess(response: BaseResponse<ScenicComfortBean>) {
                    scenicCompotLd?.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<ScenicComfortBean>) {
                    scenicCompotLd?.postValue(response.datas)
                }

            })
    }
}