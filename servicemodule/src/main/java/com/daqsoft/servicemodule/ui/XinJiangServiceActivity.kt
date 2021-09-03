package com.daqsoft.servicemodule.ui

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityXinJiangServiceBinding
import com.daqsoft.android.scenic.servicemodule.databinding.ItemXinjiangServiceMenuBinding
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.WebSiteAouter
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.servicemodule.*
import com.daqsoft.servicemodule.model.ServiceViewModel
import com.daqsoft.servicemodule.view.dialog.IdentifyCodeDialog
import com.daqsoft.provider.bean.ServiceSubTypeXJ
import com.daqsoft.provider.utils.dialog.ProviderAppointDialog
import com.daqsoft.travelCultureModule.sidetour.SideTourMapActivity
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

@Route(path = ARouterPath.ServiceModule.SERVICE_XJ_ACTIVITY)
class XinJiangServiceActivity : TitleBarActivity<ActivityXinJiangServiceBinding, ServiceViewModel>() {


    // 类型分类
    var serviceTravelList = ArrayList<ServiceSubTypeXJ>()
    var servicePlayList = ArrayList<ServiceSubTypeXJ>()
    var serviceTrafficList = ArrayList<ServiceSubTypeXJ>()
    var servicePublicList = ArrayList<ServiceSubTypeXJ>()

    override fun getLayout(): Int {
        return R.layout.activity_xin_jiang_service
    }

    override fun setTitle(): String {
        return "享服务"
    }

    override fun injectVm(): Class<ServiceViewModel> = ServiceViewModel::class.java

    override fun initView() {
        mModel.homeAd.observe(this, Observer {
            if (it.adInfo.isNotEmpty()) {
                mBinding.ivAd.visibility = View.VISIBLE
                mBinding.imgUrl = it.adInfo[0].imgUrl
                RxView.clicks(mBinding.ivAd)
                    // 1秒内不可重复点击或仅响应一次事件
                    .throttleFirst(1, TimeUnit.SECONDS).subscribe { o ->
                        run {
                            ARouter.getInstance()
                                .build(ARouterPath.ServiceModule.SERVICE_WEATHER_QUERY_ACTIVITY)
                                .withString("jumpType", it.adInfo[0].jumpUrl)
                                .withString("jumpTitle", "")
                                .navigation()
                        }
                    }

            }
        })

    }

    override fun initData() {
        initTypeData()
        mModel.getServiceAd()
    }

    /**
     * 数据初始化
     * 由配置而得
     */
    fun initTypeData() {
        setAdapter(TRAVEL_TITLE, TRAVEL_COLOR_TYPE, TRAVEL_INTRODUCE, TRAVEL_ICON, serviceTravelList, mBinding.serviceTravelRv, serviceTravelAdapter)
        setAdapter(
            TRANSPORT_TITLE,
            TRANSPORT_COLOR_TYPE,
            TRANSPORT_INTRODUCE,
            TRANSPORT_ICON,
            serviceTrafficList,
            mBinding.serviceTrafficRv,
            serviceTrafficAdapter
        )
        setAdapter(TOUR_TITLE, TOUR_COLOR_TYPE, TOUR_INTRODUCE, TOUR_ICON, servicePlayList, mBinding.servicePlayRv, servicePlayAdapter)
        setAdapter(PUBLIC_TITLE, PUBLIC_COLOR_TYPE, PUBLIC_INTRODUCE, PUBLIC_ICON, servicePublicList, mBinding.servicePublicRv, servicePublicAdapter)
    }

    private fun setAdapter(
        travelTitle: Array<String>,
        travelColorType: Array<String>,
        travelIntroduce: Array<String>,
        travelIcon: Array<Int>,
        serviceTravelList: ArrayList<ServiceSubTypeXJ>,
        serviceTravelRv: RecyclerView,
        serviceTravelAdapter: RecyclerViewAdapter<ItemXinjiangServiceMenuBinding, ServiceSubTypeXJ>
    ) {
        for ((index, title) in travelTitle.withIndex()) {
            var service = ServiceSubTypeXJ(
                index, travelColorType[index], title, travelIntroduce[index], travelIcon[index]
            )
            serviceTravelList.add(service)
        }
        serviceTravelRv.apply {
            layoutManager = GridLayoutManager(this@XinJiangServiceActivity, 2)
            adapter = serviceTravelAdapter
        }
        serviceTravelAdapter.add(serviceTravelList)
        serviceTravelAdapter.notifyDataSetChanged()
    }

    var serviceTravelAdapter = object : RecyclerViewAdapter<ItemXinjiangServiceMenuBinding, ServiceSubTypeXJ>(R.layout.item_xinjiang_service_menu) {
        override fun setVariable(mBinding: ItemXinjiangServiceMenuBinding, position: Int, item: ServiceSubTypeXJ) {
            mBinding.item = item
            mBinding.rvRoot.setBackgroundResource(getServiceBg(item.colorType))
            mBinding.imgTravel.setBackgroundResource(item.imageId)
            mBinding.root.setOnClickListener {
                when (item.type) {
                    0 -> {//机票
//                        ARouter.getInstance()
//                            .build(ARouterPath.ServiceModule.SERVICE_QUERY_TRAIN_ACTIVITY)
//                            .withString("queryTraffic", SERVICE_PLANE)
//                            .navigation()
                        showTipsDialog(4)
                    }
                    1 -> {//火车票
//                        ARouter.getInstance()
//                            .build(ARouterPath.ServiceModule.SERVICE_QUERY_TRAIN_ACTIVITY)
//                            .withString("queryTraffic", SERVICE_TRAIN)
//                            .navigation()
                        showTipsDialog(2)
                    }
                    2 -> {//导游
                        ARouter.getInstance()
                            .build(ARouterPath.ServiceModule.SERVICE_TOUR_GUIDE_ACTIVITY)
                            .navigation()
                    }
                    3 -> {//旅行社
                        ARouter.getInstance()
                            .build(ARouterPath.ServiceModule.SERVICE_TRAVEL_AGENCY_LIST_ACTIVITY)
                            .navigation()
                    }
                    4 -> {//线路
                        ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT)
                            .withString("channelCode", "lineChannel")
                            .navigation()
                    }
                    5 -> {//攻略
                        ARouter.getInstance()
                            .build(MainARouterPath.MAIN_STRATEGY_FIND)
                            .navigation()
                    }
                    6 -> {// 空气质量
                        var webUrl = SPUtils.getInstance().getString(SPKey.H5_DOMAIN)
                        ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                            .withString("mTitle", "空气质量")
                            .withString("html", "$webUrl/#/air-quality")
                            .navigation()

                    }
                }
            }
        }
    }

    private fun getServiceBg(colorType: String): Int {
        when (colorType) {
            "yellow" -> {
                return R.drawable.service_home_fff9e9_radius
            }
            "red" -> {
                return R.drawable.service_home_fff2f1_radius
            }
            "blue" -> {
                return R.drawable.service_home_e4f9fe_radius
            }
            "purple" -> {
                return R.drawable.service_home_f2f2ff_radius
            }
            "green" -> {
                return R.drawable.service_home_66f9e4_radius
            }

        }
        return R.drawable.service_home_fff9e9_radius
    }

    var servicePlayAdapter = object : RecyclerViewAdapter<ItemXinjiangServiceMenuBinding, ServiceSubTypeXJ>(R.layout.item_xinjiang_service_menu) {
        override fun setVariable(mBinding: ItemXinjiangServiceMenuBinding, position: Int, item: ServiceSubTypeXJ) {
            mBinding.item = item
            mBinding.rvRoot.setBackgroundResource(getServiceBg(item.colorType))
            mBinding.imgTravel.setBackgroundResource(item.imageId)

            mBinding.root.onNoDoubleClick {
                when (item.type) {
                    0 -> {
                        // 景区导览
                        ARouter.getInstance()
                            .build(ARouterPath.GuideModule.GUIDE_SCENIC_LIST_ACTIVITY)
                            .navigation()
                    }
                    1 -> {
                        // 厕所
                        ARouter.getInstance()
                            .build(MainARouterPath.MAIN_SIDE_TOUR)
                            .withInt(SideTourMapActivity.TAB_POS, SideTourMapActivity.TAB_TOILET)
                            .withBoolean("isService", true)
                            .navigation()
                    }
                    2 -> {
                        // "拍照识花"
                        IdentifyCodeDialog().show(supportFragmentManager, "")
                    }
                    3 -> {
                        // "AR"
                        ToastUtils.showMessage("待开发，敬请期待！~")
                    }
                    4 -> {
                        // "美食"
                        ARouter.getInstance().build(MainARouterPath.MAIN_FOOD_LS)
                            .navigation()
                    }
                }
            }
        }
    }
    var serviceTrafficAdapter = object : RecyclerViewAdapter<ItemXinjiangServiceMenuBinding, ServiceSubTypeXJ>(R.layout.item_xinjiang_service_menu) {
        override fun setVariable(mBinding: ItemXinjiangServiceMenuBinding, position: Int, item: ServiceSubTypeXJ) {
            mBinding.item = item
            mBinding.rvRoot.setBackgroundResource(getServiceBg(item.colorType))
            mBinding.imgTravel.setBackgroundResource(item.imageId)
            mBinding.root.onNoDoubleClick {
                when (item.type) {
                    0 -> {
                        //  "租车"
                        ARouter.getInstance()
                            .build(ARouterPath.Provider.WEB_ACTIVITY)
                            .withString("mTitle", "租车")
                            .withString("html", BaseApplication.webSiteUrl + WebSiteAouter.USE_CAR)
                            .navigation()
                    }
//                    1 -> {
//                        //   "景区直通车"
//                        ToastUtils.showMessage("待开发，敬请期待！~")
//                    }
                   1 -> {
                        //  "停车场"
                        ARouter.getInstance()
                            .build(MainARouterPath.MAIN_SIDE_TOUR)
                            .withBoolean("isService", true)
                            .withInt(SideTourMapActivity.TAB_POS, SideTourMapActivity.TAB_PARK)
                            .navigation()
                    }

                }
            }
        }
    }
    var servicePublicAdapter = object : RecyclerViewAdapter<ItemXinjiangServiceMenuBinding, ServiceSubTypeXJ>(R.layout.item_xinjiang_service_menu) {
        override fun setVariable(mBinding: ItemXinjiangServiceMenuBinding, position: Int, item: ServiceSubTypeXJ) {
            mBinding.item = item
            mBinding.rvRoot.setBackgroundResource(getServiceBg(item.colorType))
            //mBinding.tvTravel = item.typeName
            mBinding.imgTravel.setBackgroundResource(item.imageId)
//            mBinding.rvRoot.setPadding(0, 0, 0, resources.getDimensionPixelOffset(R.dimen.dp_22))
            mBinding.root.onNoDoubleClick {
                when (item.type) {
                    0 -> {
                        // "投诉"
                        MenuJumpUtils.gotoComplaint()
                    }
                    1 -> {
                        // "一键求助"
                        ARouter.getInstance().build(ARouterPath.ServiceModule.SERVICE_SOS_ACTIVITY).navigation()
                    }

                }
            }
        }
    }

    /**
     * @time 2020/9/18 新增
     * 显示温馨提示 dialog
     */
    fun showTipsDialog(type: Int) {
        // 提示即将离开此平台
        ProviderAppointDialog(this@XinJiangServiceActivity).apply {
            onProviderAppointClickListener = object : ProviderAppointDialog.OnProviderAppointClickListener {
                override fun onProviderAppointClick() {
                    var url = ""
                    var title = ""
                    when (type) {
                        1 -> {
                            // 公交
                            url = "https://m.8684.cn/chengdu_bus"
                            title = "公交"
                        }
                        2 -> {
                            // 火车
                            url = "https://m.ctrip.com/webapp/train"
                            title = "火车票"
                        }
                        3 -> {
                            // 汽车
                            url = "https://m.ctrip.com/webapp/bus"
                            title = "汽车票"
                        }
                        4 -> {
                            // 飞机
                            url = "https://m.ctrip.com/html5/flight/swift/index"
                            title = "机票"
                        }
                    }

                    ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString("mTitle", title)
                        .withString("html", url)
                        .navigation()
                }
            }
            setTips(getString(R.string.tip_out_platform))
            show()
        }
    }
}
