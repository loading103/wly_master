package com.daqsoft.travelCultureModule.country.ui

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
import com.daqsoft.mainmodule.databinding.ActivityCountryDetailMoreBinding
import com.daqsoft.mainmodule.databinding.MainSecnicDetailActivityMoreBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.CountryDetailBean
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.provider.bean.ScenicSpotInfoBean
import com.daqsoft.provider.bean.Spots
import com.daqsoft.provider.mapview.bean.MapLocation
import com.daqsoft.provider.mapview.impl.GaoDeMapManager
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.HtmlUtils
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.travelCultureModule.country.model.CountryDetailMoreViewModel

/**
 * 乡村介绍页面
 */
@Route(path = ARouterPath.CountryModule.COUNTRY_DETAIL_MORE_ACTIVITY)
class CountryDetailMoreActivity :
    TitleBarActivity<ActivityCountryDetailMoreBinding, CountryDetailMoreViewModel>() {


    @JvmField
    @Autowired
    var id: String = ""

    /**
     * 二维码dialog
     */
    var mQrCodeDialog: QrCodeDialog? = null

    override fun getLayout(): Int = R.layout.activity_country_detail_more

    override fun setTitle(): String = getString(R.string.country_detail_more)
    private var aMap: AMap? = null
    var mapManager: GaoDeMapManager? = null
    override fun injectVm(): Class<CountryDetailMoreViewModel> = CountryDetailMoreViewModel::class.java

    private var mScenicBean: CountryDetailBean? = null
    override fun initView() {
        mBinding.vm = mModel
        aMap = mBinding.mapView.map
        mapManager = GaoDeMapManager(mBinding.mapView)
        // 设置地图放d大到几倍
        mBinding.mapView.map?.moveCamera(CameraUpdateFactory.zoomTo(12f))
        initViewEvent()
        initViewModel()
    }

    private fun initViewModel() {
        mModel.scenicDetail.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.bean = it
            mScenicBean = it
            mBinding.scMainScenicDetail.visibility = View.VISIBLE
            if (it.latitude > 0 && it.longitude > 0) {
                mapManager!!.setLocation(it.latitude.toDouble(), it.longitude.toDouble())
                val marketView = LayoutInflater.from(this).inflate(
                    R.layout.main_item_map_market,
                    null
                )
                val imgView = marketView.findViewById<ImageView>(R.id.iv_market)
                imgView.setImageResource(R.mipmap.map_scenic_normal)

                // 将活动对象传入，点击marker时可以依次判定
                val location = MapLocation<ScenicBean>(it.latitude.toDouble(), it.longitude.toDouble())
                location.title = it.name
                mapManager!!.addMarket(location, marketView)
            }
            // 开放时间显示隐藏
            if (it.openTimeEnd.isNullOrEmpty() || it.openTimeStart.isNullOrEmpty()) {
                mBinding.ivOpenTime.visibility = View.GONE
            } else {
                mBinding.ivOpenTime.visibility = View.VISIBLE
            }
            // 联系乡村显示隐藏
            if (mScenicBean != null && !mScenicBean!!.phone.isNullOrEmpty()) {
                mBinding.tvPhone.visibility = View.VISIBLE
            } else {
                mBinding.tvPhone.visibility = View.GONE
            }
            // 实用信息显示隐藏
            if (it.ticketPolicy.isNullOrEmpty() && (it.openTimeEnd.isNullOrEmpty() || it.openTimeStart.isNullOrEmpty()) &&
                it.suggestedTime.isNullOrEmpty() && it.suggestedTime.isNullOrEmpty() && it.bestTravel.isNullOrEmpty()
            ) {
                mBinding.ilVoice.clLabel.visibility = View.GONE
            } else {
                mBinding.ilVoice.clLabel.visibility = View.VISIBLE
            }
            // 主题
//            if (!it.theme.isNullOrEmpty()) {
//                mBinding.ivScenicTheme.setContent(DividerTextUtils.convertDotString(it.theme), "")
//                mBinding.ivScenicTheme.visibility = View.VISIBLE
//            } else {
//                mBinding.ivScenicTheme.visibility = View.GONE
//            }
            // 适合人群
//            if (!it.crowd.isNullOrEmpty()) {
//                mBinding.ivCrowd.setContent(DividerTextUtils.convertDotString(it.crowd!!), "")
//                mBinding.ivCrowd.visibility = View.VISIBLE
//            } else {
//                mBinding.ivCrowd.visibility = View.GONE
//            }
            //海拔
//            if (!it.elevation.isNullOrEmpty()) {
//                mBinding.ivMore.setContent(
//                    DividerTextUtils.convertString(
//                        StringBuilder(), "最大承载量" + it
//                            .maxNum + "人", "海拔高度" + it.elevation + "米"
//                    )
//                    , ""
//                )
//                mBinding.ivMore.visibility = View.VISIBLE
//            } else {
//                mBinding.ivMore.visibility = View.GONE
//            }
            // 交通信息
            if (!it.trafficInfo.isNullOrEmpty()) {
                mBinding.tvTrafficinfo.settings.defaultTextEncodingName = "utf-8"
                mBinding.tvTrafficinfo.settings.javaScriptEnabled = true
                mBinding.tvTrafficinfo.loadDataWithBaseURL(
                    null, StringUtil.getHtml(it.trafficInfo),
                    "text/html", "utf-8", null
                )
                mBinding.tvTrafficinfo.visibility = View.VISIBLE
            } else {
                mBinding.tvTrafficinfo.visibility = View.GONE
            }
            // 乡村介绍
            if (!it.introduce.isNullOrEmpty()) {
                mBinding.tvIntroduce.settings.defaultTextEncodingName = "utf-8"
                mBinding.tvIntroduce.settings.javaScriptEnabled = true
                mBinding.tvIntroduce.loadDataWithBaseURL(
                    null, StringUtil.getHtml(it.introduce ?: ""),
                    "text/html", "utf-8", null
                )
            }
            // 路线
//            if (!it.route.isNullOrEmpty()) {
//                mBinding.tvRouterInfo.text = "" + HtmlUtils.html2Str(it.route)
//            }
            // 联系电话
//            if (!it.phone.isNullOrEmpty()) {
//                mBinding.ivPhone.visibility = View.VISIBLE
//            } else {
//                mBinding.ivPhone.visibility = View.GONE
//            }
            // 游客中心
//            mBinding.ilRouteCenter.hide = it.address.isNullOrEmpty() && it.phone.isNullOrEmpty() && it.images.isNullOrEmpty()
//            if (!it.address.isNullOrEmpty()) {
//                mBinding.tvCenterAddress.visibility = View.VISIBLE
//                mBinding.tvCenterAddressLabel.visibility = View.VISIBLE
//            }
//            if (!it.phone.isNullOrEmpty()) {
//                mBinding.tvCenterTourstPhone.visibility = View.VISIBLE
//                mBinding.tvCenterPhoneLabel.visibility = View.VISIBLE
//            }
//            if (!it.images.isNullOrEmpty()) {
//                mBinding.imgRouteCenter.visibility = View.VISIBLE
//                Glide.with(this@CountryDetailMoreActivity).load(it.images)
//                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
//                    .into(mBinding.imgRouteCenter)
//            }
            // 地理文化
//            if (!it.geogCulture.isNullOrEmpty()) {
//                mBinding.txtGeoCulture.text = HtmlUtils.html2Str(it.geogCulture)
//            }
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
            if (it.suggestedTime.isNullOrEmpty() && it.suggestedTime.isNullOrEmpty()) {
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
                SystemHelper.callPhone(this, mScenicBean?.phone ?: "")
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
//        mBinding.tvCenterAddress.onNoDoubleClick {
//            if (mScenicBean != null && mScenicBean!!.latitude>0 &&
//                mScenicBean!!.longitude>0
//            ) {
//                if (MapNaviUtils.isGdMapInstalled()) {
//                    MapNaviUtils.openGaoDeNavi(
//                        this, 0.0, 0.0, null,
//                        mScenicBean!!.latitude, mScenicBean!!.longitude,
//                        mScenicBean!!.address
//                    )
//                } else {
//                    ToastUtils.showMessage("非常抱歉，您未安装导航APP~")
//                }
//            } else {
//                ToastUtils.showMessage("非常抱歉，暂时没有定位信息~")
//            }
//        }
//        mBinding.tvCenterTourstPhone.onNoDoubleClick {
//            if (mScenicBean != null && !mScenicBean!!.phone.isNullOrEmpty()) {
//                SystemHelper.callPhone(mScenicBean!!.phone)
//            } else {
//                ToastUtils.showMessage("非常抱歉，暂时没有联系方式~")
//            }
//        }
//        mBinding.ivWebsite.onNoDoubleClick {
//            if (mScenicBean != null && !mScenicBean!!.websiteUrl.isNullOrEmpty()) {
//                ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
//                    .withString("html", mScenicBean!!.websiteUrl)
//                    .navigation()
//            }
//        }
    }

    fun goToNavigation(v: View) {
        if (MapNaviUtils.isGdMapInstalled()) {
            if (mModel.scenicDetail.value!!.latitude > 0 && mModel.scenicDetail.value!!.longitude > 0) {
                MapNaviUtils.openGaoDeNavi(
                    this, 0.0, 0.0, null,
                    mModel.scenicDetail.value!!.latitude.toDouble(), mModel.scenicDetail.value!!.longitude.toDouble(),
                    mModel.scenicDetail.value!!.cutRegionName
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
}