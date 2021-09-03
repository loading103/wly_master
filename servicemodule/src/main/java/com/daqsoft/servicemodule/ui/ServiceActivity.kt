package com.daqsoft.servicemodule.ui

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityServiceBinding
import com.daqsoft.android.scenic.servicemodule.databinding.ItemServiceMenuBinding
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.dialog.QrCodeDialog
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.servicemodule.*
import com.daqsoft.servicemodule.model.ServiceViewModel
import com.daqsoft.servicemodule.view.dialog.IdentifyCodeDialog
import com.daqsoft.provider.bean.ServiceSubType
import com.daqsoft.travelCultureModule.sidetour.SideTourMapActivity
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

@Route(path = ARouterPath.ServiceModule.SERVICE_ACTIVITY)
class ServiceActivity : TitleBarActivity<ActivityServiceBinding, ServiceViewModel>() {


    // 类型分类
    var serviceTravelList = ArrayList<ServiceSubType>()
    var servicePlayList = ArrayList<ServiceSubType>()
    var serviceTrafficList = ArrayList<ServiceSubType>()
    var servicePublicList = ArrayList<ServiceSubType>()
    /**
     * 二维码dialog
     */
    var mQrCodeDialog: QrCodeDialog? = null

    override fun getLayout(): Int {
        return R.layout.activity_service
    }

    override fun setTitle(): String {
        return "享服务"
    }

    override fun injectVm(): Class<ServiceViewModel> = ServiceViewModel::class.java

    override fun initView() {
        mModel.homeAd.observe(this, Observer {
            if (!it.adInfo.isNullOrEmpty()) {
//                mBinding.ivNoData.visibility = View.VISIBLE
                mBinding.imgUrl = it.adInfo[0].imgUrl
                mBinding.ivAd.visibility = View.VISIBLE
//                RxView.clicks(mBinding.ivNoData)
//                    // 1秒内不可重复点击或仅响应一次事件
//                    .throttleFirst(1, TimeUnit.SECONDS).subscribe { o ->
//                        run {
//                            ARouter.getInstance()
//                                .build(ARouterPath.ServiceModule.SERVICE_WEATHER_QUERY_ACTIVITY)
//                                .withString("jumpType", it.adInfo[0].jumpUrl)
//                                .withString("jumpTitle", "")
//                                .navigation()
//                        }
//                    }

            }
        })

    }

    override fun initData() {
        initTypeData()
        mModel.getServiceAd()
        StatisticsRepository.instance.statisticsPage(title, 1)
    }

    /**
     * 数据初始化
     * 由配置而得
     */
    fun initTypeData() {
        // var serviceTitles = resources.getStringArray(R.array.service_type)
        val serviceTravelIcons = resources.obtainTypedArray(R.array.service_travel_icon)
        val serviceTravelTypes = resources.getStringArray(R.array.service_travel_type)
        val serviceTrafficIcons = resources.obtainTypedArray(R.array.service_traffic_icon)
        val serviceTrafficTypes = resources.getStringArray(R.array.service_traffic_type)
        val servicePlayIcons = resources.obtainTypedArray(R.array.service_play_icon)
        val servicePlayTypes = resources.getStringArray(R.array.service_play_type)
        val servicePublicIcons = resources.obtainTypedArray(R.array.service_public_icon)
        val servicePublicTypes = resources.getStringArray(R.array.service_public_type)

        for ((index, title) in serviceTravelTypes.withIndex()) {
            var service = ServiceSubType(
                index, title, serviceTravelIcons.getResourceId(index, 0)
            ).apply {
                if (BaseApplication.appArea == "sc")
                    typeName = "投诉举报"
            }
            serviceTravelList.add(service)
            // 四川站点目前合并为一个 投诉和求助
            break
        }
        for ((index, title) in serviceTrafficTypes.withIndex()) {
            var service = ServiceSubType(
                index, title, serviceTrafficIcons.getResourceId(index, 0)
            )
            serviceTrafficList.add(service)
        }
        for ((index, title) in servicePlayTypes.withIndex()) {
            var service = ServiceSubType(
                index, title, servicePlayIcons.getResourceId(index, 0)
            )
            servicePlayList.add(service)
        }
        for ((index, title) in servicePublicTypes.withIndex()) {
            var service = ServiceSubType(
                index, title, servicePublicIcons.getResourceId(index, 0)
            )
            servicePublicList.add(service)
        }

        mBinding.serviceTravelRv.apply {
            layoutManager = GridLayoutManager(this@ServiceActivity, 4)
            adapter = serviceTravelAdapter;
        }
        serviceTravelAdapter.add(serviceTravelList)
        serviceTravelAdapter.notifyDataSetChanged()

        mBinding.serviceTrafficRv.apply {
            layoutManager = GridLayoutManager(this@ServiceActivity, 4)
            adapter = serviceTrafficAdapter;
        }
        serviceTrafficAdapter.add(serviceTrafficList)
        serviceTrafficAdapter.notifyDataSetChanged()

        mBinding.servicePlayRv.apply {
            layoutManager = GridLayoutManager(this@ServiceActivity, 4)
            adapter = servicePlayAdapter;
        }
        servicePlayAdapter.add(servicePlayList)
        servicePlayAdapter.notifyDataSetChanged()

        mBinding.servicePublicRv.apply {
            layoutManager = GridLayoutManager(this@ServiceActivity, 4)
            adapter = servicePublicAdapter;
        }
        servicePublicAdapter.add(servicePublicList)
        servicePublicAdapter.notifyDataSetChanged()

    }

    var serviceTravelAdapter = object :
        RecyclerViewAdapter<ItemServiceMenuBinding, ServiceSubType>(R.layout.item_service_menu) {
        override fun setVariable(
            mBinding: ItemServiceMenuBinding,
            position: Int,
            item: ServiceSubType
        ) {
            mBinding.item = item
            //mBinding.tvTravel = item.typeName
            mBinding.imgTravel.setBackgroundResource(item.imageId)
            mBinding.root.setOnClickListener {
                when (item.type) {
                    0 -> {
                        if (BaseApplication.appArea == "sc") {
                            ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                                .withString(
                                    "html",
                                    BaseApplication.complaintUrl + "?source=app&appToken=" + SPUtils.getInstance().getString(
                                        SPUtils.Config.TOKEN
                                    )
                                )
                                .withString("mTitle", title)
                                .navigation()
                        } else {
                            StatisticsRepository.instance.statistcsModuleClick(
                                "投诉求助",
                                ProviderApi.REGION_SHORTCUTS
                            );
                            ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                                .withString("html", BaseApplication.complaintUrl)
                                .withString("mTitle", "投诉求助")
                                .navigation()
                        }
                    }
                    1 -> {
                        ARouter.getInstance().build(ARouterPath.ServiceModule.SERVICE_SOS_ACTIVITY)
                            .navigation()
                    }
                }
            }
        }
    }
    var servicePlayAdapter = object :
        RecyclerViewAdapter<ItemServiceMenuBinding, ServiceSubType>(R.layout.item_service_menu) {
        override fun setVariable(
            mBinding: ItemServiceMenuBinding,
            position: Int,
            item: ServiceSubType
        ) {
            mBinding.item = item
            //mBinding.tvTravel = item.typeName
            mBinding.imgTravel.setBackgroundResource(item.imageId)

            mBinding.root.onNoDoubleClick {
                when (item.type) {
                    0 -> {
                        // 厕所
                        ARouter.getInstance()
                            .build(MainARouterPath.MAIN_SIDE_TOUR)
                            .withInt(SideTourMapActivity.TAB_POS, SideTourMapActivity.TAB_TOILET)
                            .withBoolean("isService", true)
                            .navigation()
                    }
                    1 -> {
                        // "导游"
                        ARouter.getInstance()
                            .build(ARouterPath.ServiceModule.SERVICE_TOUR_GUIDE_ACTIVITY)
                            .navigation()

                    }
                    2 -> {
                        // "旅行社"
                        ARouter.getInstance()
                            .build(ARouterPath.ServiceModule.SERVICE_TRAVEL_AGENCY_LIST_ACTIVITY)
                            .navigation()
                    }
                    3 -> {
                        // "天气查询"
                        ARouter.getInstance()
                            .build(ARouterPath.ServiceModule.SERVICE_WEATHER_QUERY_ACTIVITY)
                            .withString("jumpType", WEATHER_URL)
                            .withString("jumpTitle", "天气查询")
                            .navigation()
                    }
                    4 -> {
                        // "拍照识花"
                        IdentifyCodeDialog().show(supportFragmentManager, "")
                    }
                }
            }
        }
    }
    var serviceTrafficAdapter = object :
        RecyclerViewAdapter<ItemServiceMenuBinding, ServiceSubType>(R.layout.item_service_menu) {
        override fun setVariable(
            mBinding: ItemServiceMenuBinding,
            position: Int,
            item: ServiceSubType
        ) {
            mBinding.item = item
            //mBinding.tvTravel = item.typeName
            mBinding.imgTravel.setBackgroundResource(item.imageId)
            mBinding.root.onNoDoubleClick {
                when (item.type) {
                    0 -> {
                        //  "停车场"
                        ARouter.getInstance()
                            .build(MainARouterPath.MAIN_SIDE_TOUR)
                            .withInt(SideTourMapActivity.TAB_POS, SideTourMapActivity.TAB_PARK)
                            .withBoolean("isService", true)
                            .navigation()
                    }
                    1 -> {
                        //   "公交查询"
                        ARouter.getInstance()
                            .build(ARouterPath.ServiceModule.SERVICE_QUERY_BUS_ACTIVITY)
                            .navigation()
                    }
                    2 -> {
                        //  "火车票"
                        ARouter.getInstance()
                            .build(ARouterPath.ServiceModule.SERVICE_QUERY_TRAIN_ACTIVITY)
                            .withString("queryTraffic", SERVICE_TRAIN)
                            .navigation()
                    }
                    3 -> {
                        //  "汽车票"
                        ARouter.getInstance()
                            .build(ARouterPath.ServiceModule.SERVICE_QUERY_TRAIN_ACTIVITY)
                            .withString("queryTraffic", SERVICE_SUBWAY)
                            .navigation()
                    }
                    4 -> {
                        // "机票"
                        ARouter.getInstance()
                            .build(ARouterPath.ServiceModule.SERVICE_QUERY_TRAIN_ACTIVITY)
                            .withString("queryTraffic", SERVICE_PLANE)
                            .navigation()
                    }
                }
            }
        }
    }
    var servicePublicAdapter = object :
        RecyclerViewAdapter<ItemServiceMenuBinding, ServiceSubType>(R.layout.item_service_menu) {
        override fun setVariable(
            mBinding: ItemServiceMenuBinding,
            position: Int,
            item: ServiceSubType
        ) {
            mBinding.item = item
            //mBinding.tvTravel = item.typeName
            mBinding.imgTravel.setBackgroundResource(item.imageId)
            mBinding.tvTravel.setPadding(0, 0, 0, resources.getDimensionPixelOffset(R.dimen.dp_22))
            mBinding.root.onNoDoubleClick {
                when (item.type) {
                    0 -> {
                        // 健康码
                        showHealthyQrCode()
                    }
//                    1 -> {
////                        // "图书馆查询"
////                        ARouter.getInstance().build(ARouterPath.VenuesModule.VENUES_LIST_ACTIVITY).navigation()
//                        // "查艺考"
//                        ARouter.getInstance()
//                            .build(ARouterPath.ServiceModule.SERVICE_WEATHER_QUERY_ACTIVITY)
//                            .withString("jumpType", QUERY_EXAM)
//                            .withString("jumpTitle", "查艺考")
//                            .navigation()
//                    }
                   1-> {
                        // "艺术基金"
                        ARouter.getInstance()
                            .build(ARouterPath.ServiceModule.SERVICE_ART_FOUND_ACTIVITY)
                            .navigation()
                    }

                }
            }
        }
    }

    private fun showHealthyQrCode() {
        if (mQrCodeDialog == null) {
            mQrCodeDialog = QrCodeDialog.Builder()
                .qrCodeImageUrl("http://file.geeker.com.cn/uploadfile/server/wl-cloud/LargeLineTubePicture/mmexport1593671769169.png")
                .title("健康码")
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
                .build(this@ServiceActivity)
        } else {
            mQrCodeDialog?.updateData("", "健康码")
        }
        mQrCodeDialog!!.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mQrCodeDialog = null
        StatisticsRepository.instance.statisticsPage(title, 2)
    }
}
