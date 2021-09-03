package com.daqsoft.travelCultureModule.resource

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.AMap
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
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
import com.daqsoft.travelCultureModule.resource.adapter.ScenicExmpleImgAdapter
import com.daqsoft.travelCultureModule.resource.viewmodel.ScenicSpotViewModel

/**
 * @Description 景点详情页面
 * @ClassName  ScenicSpotDetailActivity
 * @Author     luoyi
 * @Time        2020/2/25 16:20
 */
@Route(path = MainARouterPath.MAIN_SCENIC_SPOT_DETAI)
class ScenicSpotDetailActivity :
    TitleBarActivity<MainSecnicSpotDetailActivityBinding, ScenicSpotViewModel>() {

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


    override fun getLayout(): Int = R.layout.main_secnic_spot_detail_activity

    override fun setTitle(): String = getString(R.string.main_spot_detail)

    private var aMap: AMap? = null

    var mapManager: GaoDeMapManager? = null

    override fun injectVm(): Class<ScenicSpotViewModel> = ScenicSpotViewModel::class.java

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
                mBinding?.vSccImgTop?.setData(
                    id,
                    it.name,
                    it.panoramaUrl,
                    it.panoramaCover,
                    images,
                    it.liveUrl!!,
                    supportFragmentManager
                )
                mBinding?.vSccImgTop?.visibility = View.VISIBLE
            } else {
                mBinding?.vSccImgTop?.visibility = View.GONE
            }
            // 处理地图
            mapManager!!.setLocation(it.latitude, it.longitude)
            val marketView = LayoutInflater.from(this).inflate(
                R.layout.main_item_map_market,
                null
            )
            val imageView = marketView.findViewById<ImageView>(R.id.iv_market)
            imageView.setImageResource(R.mipmap.map_scenic_normal)
            // 将活动对象传入，点击marker时可以依次判定
            val location = MapLocation<Spots>(it.latitude.toDouble(), it.longitude.toDouble())
            location.title = it.name
            mapManager!!.addMarket(location, marketView)
            if (!it.cutRegionName.isNullOrEmpty()) {
                mBinding.tvTags.text = DividerTextUtils.convertDotString(
                    StringBuilder(), it
                        .cutRegionName!!, "景点玩乐"
                )
            }

            if (!it.openTimeEnd.isNullOrEmpty() && !it.openTimeStart.isNullOrEmpty()) {
                val status = DividerTextUtils.convertString(
                    StringBuilder(),
                    "开放时间：" + it
                        .openTimeStart + "~" + it.openTimeEnd,
                    if (it.suggestedTime.isNullOrEmpty()) {
                        ""
                    } else {
                        "游玩时间：" + it.suggestedTime
                    }
                )
                mBinding.tvStatus.text = status
                mBinding.tvStatus.visibility = View.VISIBLE
                mBinding.ivOpenTime.visibility = View.VISIBLE
            } else {
                mBinding.tvStatus.visibility = View.GONE
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
                mBinding.tvIntroduce.loadDataWithBaseURL(
                    null,
                    StringUtil.getHtml(it.introduce!!),
                    "text/html",
                    "utf-8",
                    null
                )
            }
            // 拍摄图片
            if (!it.shootImgExample.isNullOrEmpty()) {
                mBinding.rvBestPhotoExample.visibility = View.VISIBLE
                val adapter = ScenicExmpleImgAdapter(this@ScenicSpotDetailActivity)
                mBinding.rvBestPhotoExample.layoutManager = GridLayoutManager(
                    this@ScenicSpotDetailActivity,
                    2,
                    GridLayoutManager.VERTICAL,
                    false
                )
                mBinding.rvBestPhotoExample.adapter = adapter
                var imags = it.shootImgExample?.split(",")
                if (!imags.isNullOrEmpty()) {
                    adapter.clear()
                    adapter.add(imags as MutableList<String>)
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
            } else {
                mBinding.vScenicSpotDetailAudios.visibility = View.GONE
            }
            if (!it.shootPointIntroduce.isNullOrEmpty()) {
                mBinding.ivBestPhotoIntroduce.content = HtmlUtils.html2Str(it.shootPointIntroduce)
            }
            if (!it.scenicId.isNullOrEmpty()) {
                scenicId = it.scenicId!!.toInt()
                mModel.getScenicBrandList(scenicId.toString())
                if (tags == null) {
                    mModel.getScenicDetail(it.scenicId!!)
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
                mBinding.psvScenicSpotStories.visibility = View.VISIBLE
                mBinding.psvScenicSpotStories.setDataNumber(id,ResourceType.CONTENT_TYPE_SCENIC_SPOTS,it,mModel.storyNumber)
            } else {
                mBinding.psvScenicSpotStories.visibility = View.GONE
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
                if (!it.level.isNullOrEmpty()) {
                    var countA = it.level!!.count {
                        it == 'A'
                    }
                    var spotName: String = ""
                    if (!it.scenicSpots.isNullOrEmpty()) {
                        spotName = it.scenicSpots + "个景点"
                    }
                    var theme = ""
                    if (!it.theme.isNullOrEmpty()) {
                        theme = DividerTextUtils.convertDotString(it.theme)
                    }
                    if (countA > 0) {
                        mBinding.txtScenicLevel.text = "${countA}A"
                    } else {
                        mBinding.txtScenicDot.visibility = View.GONE
                    }
                    mBinding.txtScenicSpotNum.text = "${spotName}"
                    if (!theme.isNullOrEmpty()) {
                        mBinding.txtScenicSpotTags.text = "·${theme}"
                    }
                }
                mBinding.vScenicSpotDetailBottom.visibility = View.VISIBLE
            } else {
                mBinding.vScenicSpotDetailBottom.visibility = View.GONE
            }

        })
    }

    private fun initViewEvent() {
        mBinding.ivPhone.onNoDoubleClick {
            if (bean != null) {
                SystemHelper.callPhone(this,bean!!.phone!!)
            }
        }
        mBinding.vScenicSpotDetailBottom.onNoDoubleClick {
            if (tags != null) {
                finish()
            } else {
                if (bean != null) {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_SCENIC_DETAIL)
                        .withString("id", scenicId.toString())
                        .navigation()
                }
            }
        }
    }


    override fun initData() {
        showLoadingDialog()
        mModel.getScenicSpotsDetail(id)
        mModel.getStoryList(id, ResourceType.CONTENT_TYPE_SCENIC_SPOTS)

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
}