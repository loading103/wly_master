package com.daqsoft.travelCultureModule.playground
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
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.dialog.QrCodeDialog
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityPlaygroundInfoBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.PlayGroundDetailBean
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.provider.bean.ScenicDetailBean
import com.daqsoft.provider.mapview.bean.MapLocation
import com.daqsoft.provider.mapview.impl.GaoDeMapManager
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.travelCultureModule.playground.viewmodel.PlayGroundInfoViewModel
import kotlinx.android.synthetic.main.activity_food_info.*
import kotlinx.android.synthetic.main.activity_food_info.il_food_introuduce
import kotlinx.android.synthetic.main.activity_food_info.il_food_service_tools
import kotlinx.android.synthetic.main.activity_food_info.il_food_traffic_info
import kotlinx.android.synthetic.main.activity_food_info.il_food_type
import kotlinx.android.synthetic.main.activity_playground_info.*

/**
 *  娱乐场所介绍
 */
@Route(path = MainARouterPath.MAIN_PLAYGROUND_INFO)
class PlayGroundInfoActivity : TitleBarActivity<ActivityPlaygroundInfoBinding, PlayGroundInfoViewModel>() {

    @JvmField
    @Autowired
    var id: Int = 0

    private var aMap: AMap? = null

    var mapManager: GaoDeMapManager? = null

    var hotelBean: PlayGroundDetailBean? = null

    override fun getLayout(): Int {
        return R.layout.activity_playground_info
    }

    override fun setTitle(): String {
        return "娱乐场所介绍"
    }

    override fun injectVm(): Class<PlayGroundInfoViewModel> {
        return PlayGroundInfoViewModel::class.java
    }

    override fun initView() {
        aMap = mBinding.mapView.map
        mapManager = GaoDeMapManager(mBinding.mapView)
        // 设置地图放d大到几倍
        mBinding.mapView.map?.moveCamera(CameraUpdateFactory.zoomTo(17f))
        initViewModel()
        initViewEvent()
    }

    private fun initViewEvent() {
        mBinding.tvPhone.onNoDoubleClick {
            if (hotelBean != null && !hotelBean!!.phone.isNullOrEmpty()) {
                SystemHelper.callPhone(this,hotelBean!!.phone)
            } else {
                ToastUtils.showMessage("非常抱歉，暂时没有联系方式~")
            }
        }
        mBinding.ivPhone.onNoDoubleClick {
            if (hotelBean != null && !hotelBean!!.phone.isNullOrEmpty()) {
                SystemHelper.callPhone(this,hotelBean!!.phone)
            } else {
                ToastUtils.showMessage("非常抱歉，暂时没有联系方式~")
            }
        }
        mBinding.tvNavigation.onNoDoubleClick {
            if (hotelBean != null && hotelBean!!.latitude != 0.0 &&
                hotelBean!!.longitude != 0.0
            ) {
                if (MapNaviUtils.isGdMapInstalled()) {
                    MapNaviUtils.openGaoDeNavi(
                        this, 0.0, 0.0, null,
                        hotelBean!!.latitude.toDouble(), hotelBean!!.longitude.toDouble(),
                        hotelBean!!.regionName
                    )
                } else {
                    ToastUtils.showMessage("非常抱歉，您未安装导航APP~")
                }
            } else {
                ToastUtils.showMessage("非常抱歉，暂时没有定位信息~")
            }
        }
    }

    private fun initViewModel() {
        mModel.playDetailLiveData.observe(this, Observer {
            if (it != null) {
                mBinding.bean = it
                mScenicBean= it
                hotelBean = it
                if(!it.openYear.isNullOrEmpty()){
                    mBinding.ivKyTime.content=it.openYear.replace("-","年")+"月"
                }else{
                    mBinding.ivKyTime.visibility=View.GONE
                }
                if(!it.decorationTime.isNullOrEmpty()){
                    mBinding.ivZxTime.content=it.decorationTime.replace("-","年")+"月"
                }else{
                    mBinding.ivZxTime.visibility=View.GONE
                }
                if (it.latitude != 0.0 && it.longitude != 0.0) {
                    mapManager!!.setLocation(it.latitude, it.longitude)
                    val marketView = LayoutInflater.from(this).inflate(
                        R.layout.main_item_map_market,
                        null
                    )
                    val imgView = marketView.findViewById<ImageView>(R.id.iv_market)
                    imgView.setImageResource(R.mipmap.map_food_normal)

                    // 将活动对象传入，点击marker时可以依次判定
                    val location = MapLocation<ScenicBean>(it.latitude, it.longitude)
                    location.title = it.name
                    mapManager!!.addMarket(location, marketView)
                }
                if (!it.phone.isNullOrEmpty()) {
                    mBinding.ivPhone.visibility = View.GONE
                } else {
                    mBinding.ivPhone.visibility = View.GONE
                }

                if (!it.openStartTime.isNullOrEmpty()) {
                    mBinding.ivOpenTime.visibility = View.VISIBLE
                } else {
                    mBinding.ivOpenTime.visibility = View.GONE
                }
                // 酒店类型
                if (!it.applyTag.isNullOrEmpty()) {
                    il_food_type.visibility = View.VISIBLE
                    mBinding.llvFoodTypes.setLabels(it.applyTag)
                    mBinding.llvFoodTypes.visibility = View.VISIBLE
                    mBinding.ilFoodType.hide=false
                } else {
                    il_food_type.visibility = View.GONE
                    mBinding.llvFoodTypes.visibility = View.GONE
                    mBinding.ilFoodType.hide=true
                }
                // 服务设施
                if (!it.entEqtTag.isNullOrEmpty()) {
                    il_food_service_tools.visibility = View.VISIBLE
                    mBinding.llvFoodServiceTools.setLabels(it.entEqtTag.toMutableList())
                    mBinding.llvFoodServiceTools.visibility = View.VISIBLE
                } else {
                    il_food_service_tools.visibility = View.GONE
                    mBinding.llvFoodServiceTools.visibility = View.GONE
                }
                // 特色服务
                if (!it.feature.isNullOrEmpty()) {
                    il_food_service_ts.visibility = View.VISIBLE
                    mBinding.llvFoodServiceTs.setLabels(it.feature.toMutableList())
                    mBinding.llvFoodServiceTs.visibility = View.VISIBLE
                } else {
                    il_food_service_ts.visibility = View.GONE
                    mBinding.llvFoodServiceTs.visibility = View.GONE
                }
                // 介绍
                if (!it.introduce.isNullOrEmpty()) {
                    il_food_introuduce.visibility = View.VISIBLE
                    mBinding.txtFoodIntroduce.settings.defaultTextEncodingName = "utf-8"
                    mBinding.txtFoodIntroduce.settings.setJavaScriptEnabled(true)
                    mBinding.txtFoodIntroduce.loadDataWithBaseURL(null, StringUtil.getHtml(it.introduce), "text/html", "utf-8", null)
                    mBinding.txtFoodIntroduce.visibility = View.VISIBLE
                } else {
                    il_food_introuduce.visibility = View.GONE
                    mBinding.txtFoodIntroduce.visibility = View.GONE
                }
                // 交通信息
                if (!it.trafficInfo.isNullOrEmpty()) {
                    il_food_traffic_info.visibility = View.VISIBLE
                    mBinding.txtFoodTrafficInfo.settings.defaultTextEncodingName = "utf-8"
                    mBinding.txtFoodTrafficInfo.settings.setJavaScriptEnabled(true)
                    mBinding.txtFoodTrafficInfo.loadDataWithBaseURL(null, StringUtil.getHtml(it.trafficInfo), "text/html", "utf-8", null)
                    mBinding.txtFoodTrafficInfo.visibility = View.VISIBLE
                } else {
                    il_food_traffic_info.visibility = View.GONE
                    mBinding.txtFoodTrafficInfo.visibility = View.GONE
                }

                mBinding.ilVoice.hide = mBinding.ivPhone.visibility==View.GONE &&
                        mBinding.ivOpenTime.visibility==View.GONE &&
                        mBinding.ivArea.visibility==View.GONE &&
                        mBinding.ivCapacity.visibility==View.GONE &&
                        mBinding.ivKyTime.visibility==View.GONE &&
                        mBinding.ivZxTime.visibility==View.GONE &&
                        mBinding.ivWebsite.visibility==View.GONE &&
                        mBinding.ivWxAccount.visibility==View.GONE


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
        })
    }

    override fun initData() {
        mModel.getFoodDetail(id.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapManager!!.iMapLifeCycleManager.oncreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mQrCodeDialog = null
        // 在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapManager!!.iMapLifeCycleManager.onDestroy()
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
    private  var mQrCodeDialog: QrCodeDialog? = null
    private var mScenicBean: PlayGroundDetailBean? = null
    private fun dealShowQrCode() {
        if (mScenicBean != null && !mScenicBean!!.officialUrl.isNullOrEmpty()) {
            if (mQrCodeDialog == null) {
                mQrCodeDialog = QrCodeDialog.Builder().qrCodeImageUrl(mScenicBean!!.officialUrl)
                    .title(mScenicBean!!.officialName)
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
                    .build(this)
            } else {
                mQrCodeDialog?.updateData(mScenicBean!!.officialUrl, mScenicBean!!.officialName)
            }
            mQrCodeDialog!!.show()
        }
    }
}