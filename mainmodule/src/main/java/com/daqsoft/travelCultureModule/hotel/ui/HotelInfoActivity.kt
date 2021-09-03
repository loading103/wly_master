package com.daqsoft.travelCultureModule.hotel.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityHotelMoreInfoBinding
import com.daqsoft.provider.ZARouterPath.HOTEL_INFO_MORE
import com.daqsoft.provider.bean.HotelDetailBean
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.provider.mapview.bean.MapLocation
import com.daqsoft.provider.mapview.impl.GaoDeMapManager
import com.daqsoft.provider.utils.HtmlUtils
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.travelCultureModule.hotel.viewmodel.HotelInfoViewModel
import kotlinx.android.synthetic.main.activity_hotel_more_info.*

/**
 * @Description 酒店介绍
 * @ClassName   HotelInfoActivity
 * @Author      luoyi
 * @Time        2020/4/7 19:51
 */
@Route(path = HOTEL_INFO_MORE)
class  HotelInfoActivity : TitleBarActivity<ActivityHotelMoreInfoBinding, HotelInfoViewModel>() {


    @JvmField
    @Autowired
    var id: Int = 0
    private var aMap: AMap? = null
    var mapManager: GaoDeMapManager? = null
    var hotelBean: HotelDetailBean? = null
    override fun getLayout(): Int {
        return R.layout.activity_hotel_more_info
    }

    override fun setTitle(): String {
        return getString(R.string.hotel_info_more)
    }

    override fun injectVm(): Class<HotelInfoViewModel> {
        return HotelInfoViewModel::class.java
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
            if (hotelBean != null && !hotelBean!!.latitude.isNullOrEmpty() &&
                !hotelBean!!.longitude.isNullOrEmpty()
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
        mModel.hotelInfoLiveData.observe(this, Observer {
            if (it != null) {
                mBinding.bean = it
                hotelBean = it
                if (!it.latitude.isNullOrEmpty() && !it.longitude.isNullOrEmpty()) {
                    mapManager!!.setLocation(it.latitude.toDouble(), it.longitude.toDouble())
                    val marketView = LayoutInflater.from(this).inflate(
                        R.layout.main_item_map_market,
                        null
                    )
                    val imgView = marketView.findViewById<ImageView>(R.id.iv_market)
                    imgView.setImageResource(R.mipmap.map_hotel_normal)

                    // 将活动对象传入，点击marker时可以依次判定
                    val location = MapLocation<ScenicBean>(it.latitude.toDouble(), it.longitude.toDouble())
                    location.title = it.name
                    mapManager!!.addMarket(location, marketView)
                }
                if (!it.phone.isNullOrEmpty()) {
                    mBinding.ivPhone.visibility = View.GONE
                } else {
                    mBinding.ivPhone.visibility = View.GONE
                }

                if (!it.checkInTime.isNullOrEmpty()) {
                    mBinding.ivCheckInTime.visibility = View.VISIBLE
                    mBinding.ivCheckInTime.setContent(it.checkInTime+"以后","")
                } else {
                    mBinding.ivCheckInTime.visibility = View.GONE
                }
                if (!it.checkOutTime.isNullOrEmpty()) {
                    mBinding.ivCheckOutTime.visibility = View.VISIBLE
                    mBinding.ivCheckOutTime.setContent(it.checkOutTime+"之前","")
                } else {
                    mBinding.ivCheckOutTime.visibility = View.GONE
                }
                if (!it.openYear.isNullOrEmpty()) {
                    mBinding.ivOpenTime.visibility = View.VISIBLE
                } else {
                    mBinding.ivOpenTime.visibility = View.GONE
                }
                if (!it.decorationTime.isNullOrEmpty()) {
                    mBinding.ivDecortionTime.visibility = View.VISIBLE
                } else {
                    mBinding.ivDecortionTime.visibility = View.GONE
                }
                if (!it.roomNum.isNullOrEmpty()) {
                    mBinding.ivHotelRoomNum.visibility = View.VISIBLE
                    mBinding.ivHotelRoomNum.content = "${it.roomNum}间"
                } else {
                    mBinding.ivHotelRoomNum.visibility = View.GONE
                }
                // 酒店类型
                if (!it.type.isNullOrEmpty()) {
                    il_hotel_type.visibility = View.VISIBLE
                    mBinding.llvHotelTypes.setLabels(it.type.toMutableList())
                    mBinding.llvHotelTypes.visibility = View.VISIBLE
                } else {
                    il_hotel_type.visibility = View.GONE
                    mBinding.llvHotelTypes.visibility = View.GONE
                }
                // 服务设施
                if (!it.eqt.isNullOrEmpty()) {
                    il_hotel_service_tools.visibility = View.VISIBLE
                    mBinding.llvHotelServiceTools.setLabels(it.eqt.toMutableList())
                    mBinding.llvHotelServiceTools.visibility = View.VISIBLE
                } else {
                    il_hotel_service_tools.visibility = View.GONE
                    mBinding.llvHotelServiceTools.visibility = View.GONE
                }

                // 特色服务
                if (!it.feature.isNullOrEmpty()) {
                    il_hotel_special_service.visibility = View.VISIBLE
                    mBinding.llvHotelSpecialService.visibility = View.VISIBLE
                    mBinding.llvHotelSpecialService.setLabels(it.feature.toMutableList())
                } else {
                    il_hotel_special_service.visibility = View.GONE
                    mBinding.llvHotelSpecialService.visibility = View.GONE
                }
                // 酒店介绍
                if (!it.introduce.isNullOrEmpty()) {
                    il_hotel_introuduce.visibility = View.VISIBLE
                    mBinding.txtHotelIntroduce.settings.defaultTextEncodingName = "utf-8"
                    mBinding.txtHotelIntroduce.settings.setJavaScriptEnabled(true)
                    mBinding.txtHotelIntroduce.loadDataWithBaseURL(null, StringUtil.getHtml(it.introduce), "text/html", "utf-8", null)
                    mBinding.txtHotelIntroduce.visibility = View.VISIBLE
                } else {
                    il_hotel_introuduce.visibility = View.GONE
                    mBinding.txtHotelIntroduce.visibility = View.GONE
                }
                // 交通信息
                if (!it.trafficInfo.isNullOrEmpty()) {
                    il_hotel_traffic_info.visibility = View.VISIBLE
                    mBinding.txtHotelTrafficInfo.settings.defaultTextEncodingName = "utf-8"
                    mBinding.txtHotelTrafficInfo.settings.setJavaScriptEnabled(true)
                    mBinding.txtHotelTrafficInfo.loadDataWithBaseURL(null, StringUtil.getHtml(it.trafficInfo), "text/html", "utf-8", null)
                    mBinding.txtHotelTrafficInfo.visibility = View.VISIBLE
                } else {
                    il_hotel_traffic_info.visibility = View.GONE
                    mBinding.txtHotelTrafficInfo.visibility = View.GONE
                }
            }
        })
    }

    override fun initData() {
        mModel.getHotelInfo(id.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapManager!!.iMapLifeCycleManager.oncreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
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
}