package com.daqsoft.travelCultureModule.researchbase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.dialog.QrCodeDialog
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainResearchDetailActivityMoreBinding
import com.daqsoft.mainmodule.databinding.MainSecnicDetailActivityMoreBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.provider.bean.ResearchDetailBean
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.mapview.bean.MapLocation
import com.daqsoft.provider.mapview.impl.GaoDeMapManager
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.HtmlUtils
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.AdvImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.BaseBannerImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.travelCultureModule.researchbase.viewmodel.ResearchDetailViewModel
import com.daqsoft.travelCultureModule.resource.viewmodel.ScenicDetailViewModel

/**
 * @Description 景区更多详情页面
 * @ClassName   ScenicDetailActivity
 * @Author      PuHua
 * @Time        2020/2/25 16:20
 */
@Route(path = MainARouterPath.MAIN_RESEARCH_DETAIL_MORE)
class ResearchDetailMoreActivity :
    TitleBarActivity<MainResearchDetailActivityMoreBinding, ResearchDetailViewModel>() {


    @JvmField
    @Autowired
    var id: String = ""

    /**
     * 二维码dialog
     */
    var mQrCodeDialog: QrCodeDialog? = null

    override fun getLayout(): Int = R.layout.main_research_detail_activity_more

    override fun setTitle(): String = "研学基地介绍"
    private var aMap: AMap? = null
    var mapManager: GaoDeMapManager? = null
    override fun injectVm(): Class<ResearchDetailViewModel> = ResearchDetailViewModel::class.java

    private var mScenicBean: ResearchDetailBean? = null
    override fun initView() {
        mBinding.vm = mModel
        aMap = mBinding.mapView.map
        mBinding.mapView.isEnabled=false
        mapManager = GaoDeMapManager(mBinding.mapView)
        // 设置地图放d大到几倍
        mBinding.mapView.map?.moveCamera(CameraUpdateFactory.zoomTo(16f))
        mBinding.mapView.setOnTouchListener { v, event ->
            return@setOnTouchListener true
        }
        initViewEvent()
        initViewModel()
    }

    private fun initViewModel() {
        mModel.scenicDetail.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.bean = it
            mScenicBean = it
            mBinding.scMainScenicDetail.visibility = View.VISIBLE

            if (it != null && !it!!.phone.isNullOrEmpty()) {
                mBinding.tvPhone.visibility=View.VISIBLE
                mBinding.tvLine.visibility=View.VISIBLE
            } else {
                mBinding.tvPhone.visibility=View.GONE
                mBinding.tvLine.visibility=View.GONE
            }

            if (!it.latitude.isNullOrEmpty() && !it.longitude.isNullOrEmpty()) {
                mapManager!!.setLocation(it.latitude!!.toDouble(), it.longitude!!.toDouble())
                val marketView = LayoutInflater.from(this).inflate(
                    R.layout.main_item_map_market,
                    null
                )
                val imgView = marketView.findViewById<ImageView>(R.id.iv_market)
                imgView.setImageResource(R.mipmap.map_scenic_normal)

                // 将活动对象传入，点击marker时可以依次判定
                val location =
                    MapLocation<ScenicBean>(it.latitude!!.toDouble(), it.longitude!!.toDouble())
                location.title = it.name
                mapManager!!.addMarket(location, marketView)
            }
//            if (!it.tag.isNullOrEmpty()) {
//                mBinding.ivScenicTheme.setContent(DividerTextUtils.convertDotString(it?.tag!!), "")
//                mBinding.ivScenicTheme.visibility = View.VISIBLE
//            } else {
//                mBinding.ivScenicTheme.visibility = View.GONE
//            }
            if (!it.crowd.isNullOrEmpty()) {
                mBinding.ivCrowd.setContent(DividerTextUtils.convertDotString(it.crowd!!), "")
                mBinding.ivCrowd.visibility = View.VISIBLE
            } else {
                mBinding.ivCrowd.visibility = View.GONE
            }
            var str = DividerTextUtils.convertString(
                StringBuilder(), if (!it.maxNum.isNullOrEmpty()) {
                    "最大承载量" + it
                        .maxNum + "人"
                } else {
                    ""
                }, if (!it.elevation.isNullOrEmpty()) {
                    "海拔高度" + it.elevation + "米"
                } else {
                    ""
                }
            )
            if (str.isNullOrEmpty()) {
                mBinding.ivMore.visibility = View.GONE
            } else {
                mBinding.ivMore.setContent(
                    str
                )
                mBinding.ivMore.visibility = View.VISIBLE
            }
            if (!it.trafficInfo.isNullOrEmpty()) {
                mBinding.tvTrafficinfo.settings.defaultTextEncodingName = "utf-8"
                mBinding.tvTrafficinfo.settings.javaScriptEnabled = true
                mBinding.tvTrafficinfo.loadDataWithBaseURL(
                    null, StringUtil.getHtml(it.trafficInfo!!),
                    "text/html", "utf-8", null
                )
                mBinding.tvTrafficinfo.visibility = View.VISIBLE
            } else {
                mBinding.tvTrafficinfo.visibility = View.GONE
            }
            if (!it.introduce.isNullOrEmpty()) {
                mBinding.tvIntroduce.settings.defaultTextEncodingName = "utf-8"
                mBinding.tvIntroduce.settings.javaScriptEnabled = true
                mBinding.tvIntroduce.loadDataWithBaseURL(
                    null, StringUtil.getHtml(it.introduce!!),
                    "text/html", "utf-8", null
                )
            }
            if (!it.route.isNullOrEmpty()) {
//                mBinding.tvRouterInfo.text = "" + HtmlUtils.html2Str(it.route)
                mBinding.tvRouterInfo.settings.defaultTextEncodingName = "utf-8"
                mBinding.tvRouterInfo.settings.javaScriptEnabled = true
                mBinding.tvRouterInfo.loadDataWithBaseURL(
                    null, StringUtil.getHtml(it.route!!),
                    "text/html", "utf-8", null
                )
            }
            if (!it.websiteUrl.isNullOrEmpty()) {
                mBinding.ivWebsite.visibility = View.VISIBLE
            }else {
                mBinding.ivWebsite.visibility = View.GONE
            }

            if (!it.websiteUrl.isNullOrEmpty()) {
                mBinding.ivWxAccount.visibility = View.VISIBLE
            }else {
                mBinding.ivWxAccount.visibility = View.GONE
            }
            if (!it.officialUrl.isNullOrEmpty()) {
                mBinding.ivWxAccount.visibility = View.VISIBLE
            }else {
                mBinding.ivWxAccount.visibility = View.GONE
            }
            // 联系电话
//            if (!it.phone.isNullOrEmpty()) {
//                mBinding.ivPhone.visibility = View.VISIBLE
//            } else {
//                mBinding.ivPhone.visibility = View.GONE
//            }
            // 游客中心
            mBinding.ilRouteCenter.hide =
                it.centerAddress.isNullOrEmpty() && it.centerPhone.isNullOrEmpty() && it.centerImage.isNullOrEmpty()
            if (!it.centerAddress.isNullOrEmpty()) {
                mBinding.tvCenterAddress.visibility = View.VISIBLE
                mBinding.tvCenterAddressLabel.visibility = View.VISIBLE
            }
            if (!it.centerPhone.isNullOrEmpty()) {
                mBinding.tvCenterTourstPhone.visibility = View.VISIBLE
                mBinding.tvCenterPhoneLabel.visibility = View.VISIBLE
            }
            if (!it.centerImage.isNullOrEmpty()) {
                var images: List<String>? = it.centerImage!!.split(",")
                if (!images.isNullOrEmpty()) {
                    mBinding.imgRouteCenter.visibility = View.VISIBLE
                    mBinding.imgRouteCenter.setPages(object : CBViewHolderCreator {
                        override fun createHolder(itemView: View?): Holder<*> {
                            return BaseBannerImageHolder(itemView!!)
                        }

                        override fun getLayoutId(): Int {
                            return com.daqsoft.provider.R.layout.holder_img_base
                        }
                    }, images)
                        .setCanLoop(images.size > 1)
                        .setPointViewVisible(images.size > 1)
                        .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                        .setPageIndicator(null)
                        .startTurning(3000)
                } else {
                    mBinding.imgRouteCenter.visibility = View.GONE
                }
            }
            if (it.centerLatitude.isNullOrEmpty() || it.centerLongitude.isNullOrEmpty()) {
                mBinding.tvCenterAddress.setCompoundDrawables(null, null, null, null)
            }
            // 地理文化
            if (!it.geogCulture.isNullOrEmpty()) {
                mBinding.txtGeoCulture.text = HtmlUtils.html2Str(it.geogCulture)
            }
            // 游玩时间
            var suggestTime = ""
            if (!it.suggestedTime.isNullOrEmpty()) {
                suggestTime =
                    "${it.suggestedTime}"
            }
//            if (!it.suggestedHour.isNullOrEmpty()) {
//                suggestTime = if (it.suggestedHour.contains("小时")) {
//                    "${suggestTime}${it.suggestedHour}"
//                } else {
//                    "${suggestTime}${it.suggestedHour}小时"
//                }
//            }
            if (it.suggestedTime.isNullOrEmpty() && it.suggestedHour.isNullOrEmpty()) {
                mBinding.ivSuggest.visibility = View.GONE
            }
            mBinding.suggestTime = suggestTime
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
    }

    private fun initViewEvent() {
        mBinding.tvPhone.onNoDoubleClick {
            if (mScenicBean != null && !mScenicBean!!.phone.isNullOrEmpty()) {
                SystemHelper.callPhone(this, mScenicBean?.phone!!)
            } else {
                ToastUtils.showMessage("非常抱歉，暂时没有联系方式~")
            }
        }
//        mBinding.ivPhone.onNoDoubleClick {
//            if (mScenicBean != null && !mScenicBean!!.phone.isNullOrEmpty()) {
//                SystemHelper.callPhone(mScenicBean!!.phone)
//            } else {
//                ToastUtils.showMessage("非常抱歉，暂时没有联系方式~")
//            }
//        }
        mBinding.tvCenterAddress.onNoDoubleClick {
            if (mScenicBean != null && !mScenicBean!!.centerLatitude.isNullOrEmpty() &&
                !mScenicBean!!.centerLongitude.isNullOrEmpty()
            ) {
                if (MapNaviUtils.isGdMapInstalled()) {
                    MapNaviUtils.openGaoDeNavi(
                        this,
                        0.0,
                        0.0,
                        null,
                        mScenicBean?.centerLatitude!!.toDouble(),
                        mScenicBean?.centerLongitude!!.toDouble(),
                        mScenicBean!!.centerAddress
                    )
                } else {
                    ToastUtils.showMessage("非常抱歉，您未安装导航APP~")
                }
            } else {
                ToastUtils.showMessage("非常抱歉，暂时没有定位信息~")
            }
        }
        mBinding.tvCenterTourstPhone.onNoDoubleClick {
            if (mScenicBean != null && !mScenicBean!!.centerPhone.isNullOrEmpty()) {
                SystemHelper.callPhone(this, mScenicBean?.centerPhone!!)
            } else {
                ToastUtils.showMessage("非常抱歉，暂时没有联系方式~")
            }
        }
        mBinding.ivWebsite.onNoDoubleClick {
            if (mScenicBean != null && !mScenicBean!!.websiteUrl.isNullOrEmpty()) {
                ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("html", mScenicBean!!.websiteUrl)
                    .navigation()
            }
        }
        mBinding.ivWxAccount.onNoDoubleClick {
            dealShowQrCode()
        }
    }

    fun goToNavigation(v: View) {
        if (MapNaviUtils.isGdMapInstalled()) {
            if (!mModel.scenicDetail.value!!.latitude.isNullOrEmpty() && !mModel.scenicDetail.value!!.longitude.isNullOrEmpty()) {
                MapNaviUtils.openGaoDeNavi(
                    this, 0.0, 0.0, null,
                    mModel.scenicDetail.value!!.latitude!!.toDouble(), mModel.scenicDetail.value!!.longitude!!.toDouble(),
                    mModel.scenicDetail.value!!.regionName
                )
            }
        }

    }


    override fun initData() {
        mModel.id = id
        showLoadingDialog()
        mModel.getScenicDetail(id, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapManager!!.iMapLifeCycleManager.oncreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapManager!!.iMapLifeCycleManager.onDestroy()
        mQrCodeDialog = null
    }

    override fun onResume() {
        super.onResume()
        // 在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapManager!!.iMapLifeCycleManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        // 在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapManager!!.iMapLifeCycleManager.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapManager!!.iMapLifeCycleManager.onSaveInstanceState(outState)
    }

    /**
     * 处理二维码显示
     */
    private fun dealShowQrCode() {
        if (mScenicBean != null && !mScenicBean!!.officialUrl.isNullOrEmpty()) {
            if (mQrCodeDialog == null) {
                mQrCodeDialog = QrCodeDialog.Builder().qrCodeImageUrl(mScenicBean!!.officialUrl!!)
                    .title(mScenicBean!!.officialName!!)
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
                    .build(this@ResearchDetailMoreActivity)
            } else {
                mQrCodeDialog?.updateData(mScenicBean!!.officialUrl!!, mScenicBean!!.officialName!!)
            }
            mQrCodeDialog!!.show()
        }
    }
}