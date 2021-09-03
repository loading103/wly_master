package com.daqsoft.travelCultureModule.country.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.AMap
import com.amap.api.maps.model.Marker
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityCountryScenicSpotBinding
import com.daqsoft.mainmodule.databinding.MainSecnicSpotDetailActivityBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ScenicTags
import com.daqsoft.provider.bean.Spots
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.mapview.bean.MapLocation
import com.daqsoft.provider.mapview.impl.GaoDeMapManager
import com.daqsoft.provider.network.venues.bean.AudioInfo
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.HtmlUtils
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.travelCultureModule.country.model.CountryScenicSpotViewModel
import com.daqsoft.travelCultureModule.resource.adapter.ScenicExmpleImgAdapter
import kotlinx.android.synthetic.main.activity_country_scenic_spot.*
import kotlinx.android.synthetic.main.activity_country_scenic_spot.tv_introduce
import kotlinx.android.synthetic.main.main_activity_hot_detail.*

/**
 * 景观点详情页面
 */
@Route(path = ARouterPath.CountryModule.COUNTRY_SCENIC_SPOT_ACTIVITY)
class CountryScenicSpotActivity : TitleBarActivity<ActivityCountryScenicSpotBinding, CountryScenicSpotViewModel>(),
    AMap.OnMarkerClickListener {

    @JvmField
    @Autowired
    var id: String = ""

    @JvmField
    @Autowired
    var scenicId: Int = -1

    @JvmField
    @Autowired
    var tags: ScenicTags? = null

    @JvmField
    @Autowired
    var scenicUrl = ""

    @JvmField
    @Autowired
    var scenicName = ""


    var bean: Spots? = null


    override fun getLayout(): Int = R.layout.activity_country_scenic_spot

    override fun setTitle(): String = getString(R.string.country_scenic_spots)

    private var aMap: AMap? = null

    var mapManager: GaoDeMapManager? = null

    override fun injectVm(): Class<CountryScenicSpotViewModel> = CountryScenicSpotViewModel::class.java

    override fun initView() {
        mBinding.vm = mModel
        if (!scenicName.isNullOrEmpty()) {
            mBinding.secnicUrl = scenicUrl
            mBinding.secnicName = scenicName
            if (tags != null) {
                if (!tags!!.level.isNullOrEmpty()) {
                    var countA = tags!!.level!!.count {
                        it == 'A'
                    }
                    mBinding.txtScenicLevel.text = "${countA}A"
                }

                mBinding.txtScenicSpotNum.text = "${tags!!.spotNum}"
                mBinding.txtScenicSpotTags.text = "·${tags!!.tags}"
            }
            mBinding.vScenicSpotDetailBottom.visibility = View.VISIBLE
        } else {
            mBinding.vScenicSpotDetailBottom.visibility = View.GONE
        }
        aMap = mBinding.mapView.map
        mapManager = GaoDeMapManager(mBinding.mapView)
        // 景点
        mModel.spot.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null) {
                mBinding.bean = it
                bean = it
                mBinding.scrollScenicSpot.visibility = View.VISIBLE
            } else {
                ToastUtils.showMessage("非常抱歉，未找到景点信息~ ")
                finish()
                return@Observer
            }
            // 处理图片
            val images = it.images?.split(",")
            if (!images.isNullOrEmpty() || !it.panoramaUrl.isNullOrEmpty() || !it.liveUrl.isNullOrEmpty()) {
                mBinding?.vSccImgTop?.setData(id, it.name, it.panoramaUrl, it.panoramaCover, images, it.liveUrl ?: "", supportFragmentManager)
                mBinding?.vSccImgTop?.visibility = View.VISIBLE
            } else {
                mBinding?.vSccImgTop?.visibility = View.GONE
            }
            // 处理地图
            mapManager!!.setLocation(it.latitude + 0.035, it.longitude)
            val marketView = LayoutInflater.from(this).inflate(
                R.layout.main_item_map_market,
                null
            )
            val imageView = marketView.findViewById<ImageView>(R.id.iv_market)
            imageView.setImageResource(R.mipmap.map_scenic_normal)
            // 将活动对象传入，点击marker时可以依次判定
            val location = MapLocation<Spots>(it.latitude.toDouble(), it.longitude.toDouble())
            location.title = it.name
            val textView = marketView.findViewById<TextView>(R.id.tv_address)
            textView.visibility = View.VISIBLE
            textView.text = it.name
            mapManager!!.addMarket(location, marketView)
            mapManager?.setOnMarkerClickListener(this)
            if (!it.cutRegionName.isNullOrEmpty()) {
                mBinding.tvTags.text = DividerTextUtils.convertDotString(
                    StringBuilder(), it
                        .cutRegionName!!, "景点玩乐"
                )
            }

            if (!it.openTimeEnd.isNullOrEmpty() && !it.suggestedTime.isNullOrEmpty()) {
                val status = DividerTextUtils.convertString(
                    StringBuilder(), "开放时间：" + it
                        .openTimeStart + "~" + it.openTimeEnd, "游玩时间：" + it.suggestedTime + "小时以上"
                )
                mBinding.tvStatus.text = status
                mBinding.tvStatus.visibility = View.VISIBLE
                //mBinding.ivOpenTime.visibility = View.VISIBLE
            } else {
                mBinding.tvStatus.visibility = View.GONE
                //mBinding.ivOpenTime.visibility = View.GONE
            }

            if (!it.address.isNullOrEmpty()) {
                mBinding.ivOpenTime.visibility = View.VISIBLE
            } else {
                mBinding.ivOpenTime.visibility = View.GONE
            }

            var otherInfo: String = ""
            if (!it.elevation.isNullOrEmpty()) {
                otherInfo = "海拔高度：${it.elevation}米;"
            }

            if (!it.radiationDis.isNullOrEmpty()) {
                otherInfo = "${otherInfo}辐射距离：${it.radiationDis}米"
            }
            mBinding.ivMore.setContent(
                otherInfo
                , ""
            )
            if (!it.phone.isNullOrEmpty()) {
                mBinding.ivPhone.visibility = View.VISIBLE
                mBinding.ivPhone.content = it.phone
            } else {
                mBinding.ivPhone.visibility = View.GONE
            }
            if (it != null && !it.introduce.isNullOrEmpty()) {
                mBinding.tvIntroduce.settings.defaultTextEncodingName = "utf-8"
                mBinding.tvIntroduce.settings.setJavaScriptEnabled(true)
                mBinding.tvIntroduce.loadDataWithBaseURL(null, StringUtil.getHtml(it.introduce!!), "text/html", "utf-8", null)
            }
            // 拍摄图片
            if (!it.shootImgExample.isNullOrEmpty()) {
                mBinding.rvBestPhotoExample.visibility = View.VISIBLE
                val adapter = ScenicExmpleImgAdapter(this@CountryScenicSpotActivity)
                mBinding.rvBestPhotoExample.layoutManager = GridLayoutManager(this@CountryScenicSpotActivity, 2, GridLayoutManager.VERTICAL, false)
                mBinding.rvBestPhotoExample.adapter = adapter
                var imags = it.shootImgExample?.split(",")
                if (!images.isNullOrEmpty()) {
                    adapter.clear()
                    adapter.add(images as MutableList<String>)
                }
            } else {
                mBinding.rvBestPhotoExample.visibility = View.GONE
            }

            // 听解说
            var audios: MutableList<AudioInfo> = mutableListOf()
            // 金牌解说
            if (it.goldStory != null && !it.goldStory?.audio.isNullOrEmpty()) {
                audios.add(AudioInfo().apply {
                    type = 1
                    linkUrl = it.goldStory?.link
                    audio = it.goldStory?.turl
                    name = it.name
                })
            }

            if (!it.audioInfo.isNullOrEmpty()) {
                audios.addAll(it.audioInfo!!)
            }
            if (!audios.isNullOrEmpty()) {
                mBinding.vScenicSpotDetailAudios.visibility = View.VISIBLE
                mBinding.vScenicSpotDetailAudios.setData(audios)
                mBinding.vTabComment.visibility = View.VISIBLE
            } else {
                mBinding.vScenicSpotDetailAudios.visibility = View.GONE
                mBinding.vTabComment.visibility = View.GONE
            }
//            if (!it.shootPointIntroduce.isNullOrEmpty()) {
//                mBinding.ivBestPhotoIntroduce.content = HtmlUtils.html2Str(it.shootPointIntroduce)
//            }
            if (it.ruralId > 0) {
                scenicId = it.ruralId
                mModel.getScenicBrandList(scenicId.toString())
                if (tags == null) {
                    mModel.getScenicDetail(it.ruralId.toString())
                }
            }

        })

        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })

        initViewEvent()


        mModel.storyList.observe(this, Observer {

            if (!it.isNullOrEmpty()) {
//                mBinding.psvScenicSpotStories.setData(it!!)
                mBinding.psvScenicSpotStories.setDataNumber(id,ResourceType.CONTENT_TYPE_RURAL_SPOTS,it,mModel.storyNumber)
                mBinding.psvScenicSpotStories.visibility = View.VISIBLE
                mBinding.vTabStory.visibility = View.VISIBLE
            } else {
                mBinding.psvScenicSpotStories.visibility = View.GONE
                mBinding.vTabStory.visibility = View.GONE
            }
        })
        mModel.scenicBrandListLiveData.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
//            mModel.getScenicBrandList(IT)
                if (bean != null && !bean!!.cutRegionName.isNullOrEmpty()) {
                    // 添加品牌信息
                    var spanBrands: MutableList<String> = mutableListOf()
                    for (item in it) {
                        if (!item.name.isNullOrEmpty()) {
                            spanBrands.add(item.name)
                        }
                    }
                    var spanbrandsResult = ""
                    if (!spanBrands.isNullOrEmpty()) {
                        spanbrandsResult = DividerTextUtils.convertDotString(spanBrands)
                    }
                    mBinding.tvTags.text = DividerTextUtils.convertDotString(

                        StringBuilder(), if (spanbrandsResult.isNullOrEmpty()) {
                            ""
                        } else {
                            spanbrandsResult!!
                        }, bean!!
                            .cutRegionName!!, "景点玩乐"
                    )
                }
            }
        })
        mModel.scenicDetail.observe(this, Observer {
            if (it != null) {
                mBinding.secnicUrl = it.images.getRealImages()
                mBinding.secnicName = "" + it.name
                var spotName: String = ""
                if (it.spotsNum > 0) {
                    spotName = it.spotsNum.toString() + "个景观点"
                }
                mBinding.txtScenicSpotNum.text = "${spotName}"
//                if (!it.level.isNullOrEmpty()) {
//                    var countA = it.level!!.count {
//                        it == 'A'
//                    }
//                    var spotName: String = ""
//                    if (it.spotsNum>0) {
//                        spotName = it.spotsNum.toString() + "个景点"
//                    }
//                    var theme = ""
//                    if (!it.theme.isNullOrEmpty()) {
//                        theme = DividerTextUtils.convertDotString(it.theme)
//                    }
//                    if (countA > 0) {
//                        mBinding.txtScenicLevel.text = "${countA}A"
//                    } else {
//                        mBinding.txtScenicDot.visibility = View.GONE
//                    }
//                    mBinding.txtScenicSpotNum.text = "${spotName}"
//                    if (!theme.isNullOrEmpty()) {
//                        mBinding.txtScenicSpotTags.text = "·${theme}"
//                    }
//                }
                mBinding.vScenicSpotDetailBottom.visibility = View.VISIBLE
            } else {
                mBinding.vScenicSpotDetailBottom.visibility = View.GONE
            }

        })
    }

    private fun initViewEvent() {
        mBinding.ivPhone.onNoDoubleClick {
            if (bean != null) {
                SystemHelper.callPhone(this, bean!!.phone!!)
            }
        }
        mBinding.vScenicSpotDetailBottom.onNoDoubleClick {
            if (tags != null) {
                finish()
            } else {
                if (bean != null) {
                    ARouter.getInstance()
                        .build(ARouterPath.CountryModule.COUNTRY_COUNTRY_DETAIL_ACTIVITY)
                        .withString("id", scenicId.toString())
                        .navigation()
                }
            }
        }
        // 滑动监听 配合tab
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBinding.scrollScenicSpot.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY >= 0) {
                    if (scrollY > v_scenic_spot_detail_audios.top && scrollY < il_spots.top - 100 && v_scenic_spot_detail_audios.visibility == View.VISIBLE
                        && !mBinding.tvComment.isSelected
                    ) { // 听解说
                        resetTabSelectedStatus()
                        mBinding.vStickTop.visibility = View.VISIBLE
                        mBinding.tvComment.isSelected = true
                        changeIndicator(0)
                    } else if (scrollY > il_spots.top - 100 && scrollY < il_route.top - 120 && il_spots.visibility == View.VISIBLE
                        && !mBinding.tvInformation.isSelected
                    ) { // 查信息
                        resetTabSelectedStatus()
                        mBinding.tvInformation.isSelected = true
                        changeIndicator(1)
                    } else if (scrollY > il_route.top - 120 && scrollY < tv_introduce.bottom - 150 && il_route.visibility == View.VISIBLE
                        && !mBinding.tvIntroduction.isSelected
                    ) { // 看简介
                        resetTabSelectedStatus()
                        mBinding.tvIntroduction.isSelected = true
                        changeIndicator(2)
                    } else if (scrollY > tv_introduce.bottom - 150 && scrollY < psv_scenic_spot_stories.bottom && psv_scenic_spot_stories.visibility == View.VISIBLE
                        && !mBinding.tvStory.isSelected
                    ) { // 读故事
                        resetTabSelectedStatus()
                        mBinding.tvStory.isSelected = true
                        changeIndicator(3)
                    } else if (scrollY <= v_scc_img_top.top) {
                        // 重置
                        resetTabSelectedStatus()
                        mBinding.vStickTop.visibility = View.GONE
                    }
                }
            }
        }
        // 顶部点击 滑动跳转
        mBinding.vTabComment.onNoDoubleClick {
            mBinding.scrollScenicSpot.smoothScrollTo(0, v_scenic_spot_detail_audios.top)
        }
        mBinding.vTabInformation.onNoDoubleClick {
            mBinding.scrollScenicSpot.smoothScrollTo(0, il_spots.top)
        }
        mBinding.vTabIntroduction.onNoDoubleClick {
            mBinding.scrollScenicSpot.smoothScrollTo(0, il_route.top)
        }
        mBinding.vTabStory.onNoDoubleClick {
            mBinding.scrollScenicSpot.smoothScrollTo(0, psv_scenic_spot_stories.top)
        }
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getScenicSpotsDetail(id)
        mModel.getStoryList(id, ResourceType.CONTENT_TYPE_RURAL_SPOTS)

    }

    /**
     * 重置滑动tab
     */
    private fun resetTabSelectedStatus() {
        mBinding.tvComment.isSelected = false
        mBinding.tvInformation.isSelected = false
        mBinding.tvIntroduction.isSelected = false
        mBinding.tvStory.isSelected = false
        mBinding.vIndicatorComment.visibility = View.GONE
        mBinding.vIndicatorInformation.visibility = View.GONE
        mBinding.vIndicatorIntroduction.visibility = View.GONE
        mBinding.vIndicatorStory.visibility = View.GONE
    }

    /**
     * 改变滑动标识
     */
    private fun changeIndicator(pos: Int) {
        when (pos) {
            0 -> {
                mBinding.vIndicatorComment.visibility = View.VISIBLE
            }
            1 -> {
                mBinding.vIndicatorInformation.visibility = View.VISIBLE
            }
            2 -> {
                mBinding.vIndicatorIntroduction.visibility = View.VISIBLE
            }
            3 -> {
                mBinding.vIndicatorStory.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding?.mapView?.onCreate(savedInstanceState)
    }

    //
    override fun onResume() {
        super.onResume()
        // 在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mBinding.mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mBinding.mapView.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        try {
            mBinding?.vScenicSpotDetailAudios?.pauseAudioPlayer()
            mBinding?.mapView?.onPause()
        } catch (e: Exception) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mBinding?.mapView?.onDestroy()
            mBinding?.vScenicSpotDetailAudios?.releaseAudioPlayer()
        } catch (e: Exception) {

        }

    }

    // 点击地图maker
    override fun onMarkerClick(p0: Marker?): Boolean {
        if (bean != null && (bean?.latitude ?: 0.0) > 0.0 && (bean?.longitude ?: 0.0) > 0.0) {
            if (MapNaviUtils.isGdMapInstalled()) {
                MapNaviUtils.openGaoDeNavi(
                    BaseApplication.context, 0.0, 0.0, null,
                    bean?.latitude ?: 0.0, bean?.longitude ?: 0.0,
                    bean?.name
                )
            } else {
                mModel.toast.postValue("非常抱歉，系统未安装地图软件")
            }
        } else {
            mModel.toast.postValue("非常抱歉，暂无位置信息")
        }
        return true
    }
}