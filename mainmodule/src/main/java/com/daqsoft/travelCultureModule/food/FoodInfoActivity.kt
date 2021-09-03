package com.daqsoft.travelCultureModule.food

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
import com.daqsoft.mainmodule.databinding.ActivityFoodInfoBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.FoodDetailBean
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.provider.mapview.bean.MapLocation
import com.daqsoft.provider.mapview.impl.GaoDeMapManager
import com.daqsoft.provider.utils.HtmlUtils
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.travelCultureModule.food.viewmodel.FoodInfoViewModel
import kotlinx.android.synthetic.main.activity_food_info.*

/**
 * @Description 餐厅介绍
 * @ClassName   FoodInfoActivity
 * @Author      luoyi
 * @Time        2020/4/11 9:16
 */
@Route(path = MainARouterPath.MAIN_FOOD_INFO)
class FoodInfoActivity : TitleBarActivity<ActivityFoodInfoBinding, FoodInfoViewModel>() {

    @JvmField
    @Autowired
    var id: Int = 0
    private var aMap: AMap? = null
    var mapManager: GaoDeMapManager? = null
    var hotelBean: FoodDetailBean? = null

    override fun getLayout(): Int {
        return R.layout.activity_food_info
    }

    override fun setTitle(): String {
        return "餐厅介绍"
    }

    override fun injectVm(): Class<FoodInfoViewModel> {
        return FoodInfoViewModel::class.java
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
        mModel.foodDetailLiveData.observe(this, Observer {
            if (it != null) {
                mBinding.bean = it
                hotelBean = it
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

                if (!it.openTime.isNullOrEmpty()) {
                    mBinding.ivOpenTime.visibility = View.VISIBLE
                } else {
                    mBinding.ivOpenTime.visibility = View.GONE
                }
                // 酒店类型
                if (!it.type.isNullOrEmpty()) {
                    il_food_type.visibility = View.VISIBLE
                    mBinding.llvFoodTypes.setLabels(it.type.toMutableList())
                    mBinding.llvFoodTypes.visibility = View.VISIBLE
                } else {
                    il_food_type.visibility = View.GONE
                    mBinding.llvFoodTypes.visibility = View.GONE
                }
                // 服务设施
                if (!it.eqtTag.isNullOrEmpty()) {
                    il_food_service_tools.visibility = View.VISIBLE
                    mBinding.llvFoodServiceTools.setLabels(it.eqtTag.toMutableList())
                    mBinding.llvFoodServiceTools.visibility = View.VISIBLE
                } else {
                    il_food_service_tools.visibility = View.GONE
                    mBinding.llvFoodServiceTools.visibility = View.GONE
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