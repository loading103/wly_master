package com.daqsoft.thetravelcloudwithculture.ui

import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.android.CommonURL
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityWsServiceBinding
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityWsServiceBindingImpl
import com.daqsoft.android.scenic.servicemodule.databinding.ItemServiceMenuBinding
import com.daqsoft.android.scenic.servicemodule.databinding.ItemXinjiangServiceMenuBinding
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.dialog.QrCodeDialog
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.servicemodule.*
import com.daqsoft.servicemodule.model.ServiceViewModel
import com.daqsoft.servicemodule.view.dialog.IdentifyCodeDialog
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentServiceBinding
import com.daqsoft.provider.bean.ServiceSubType
import com.daqsoft.provider.bean.ServiceSubTypeXJ
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.utils.dialog.ProviderAppointDialog
import com.daqsoft.provider.view.BaseDialog
import com.daqsoft.travelCultureModule.sidetour.SideTourMapActivity
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * 公共服务
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-10-22
 * @since JDK 1.8.0_191
 * @update 20200320 am by hoklam
 */
class WsServiceFragment() : BaseFragment<ActivityWsServiceBinding ,ServiceViewModel>() {

    // 类型分类
    var serviceTravelList = ArrayList<ServiceSubTypeXJ>()
    var servicePlayList = ArrayList<ServiceSubTypeXJ>()
    var serviceTrafficList = ArrayList<ServiceSubTypeXJ>()
    var servicePublicList = ArrayList<ServiceSubTypeXJ>()

    var phoneDialog: BaseDialog? = null

    override fun getLayout(): Int {
        return com.daqsoft.android.scenic.servicemodule.R.layout.activity_ws_service
    }
    /**
     * 二维码dialog
     */
    var mQrCodeDialog: QrCodeDialog? = null


    override fun initData() {
        initTypeData()
//        initServiceData()
        mModel.getServiceAd()

    }


    override fun initView() {

        if(BaseApplication.appArea=="ws")
            mBinding.tvTitle.visibility=View.VISIBLE
        else
            mBinding.tvTitle.visibility=View.GONE

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

    override fun injectVm(): Class<ServiceViewModel> = ServiceViewModel::class.java

    /**
     * 数据初始化
     * 由配置而得
     */
    fun initTypeData() {
        setAdapter(TRAVEL_TITLE_WS, TRAVEL_COLOR_TYPE_WS, TRAVEL_INTRODUCE_WS, TRAVEL_ICON_WS, serviceTravelList, mBinding.serviceTravelRv, serviceTravelAdapter)
        setAdapter(
            TRANSPORT_TITLE_WS,
            TRANSPORT_COLOR_TYPE_WS,
            TRANSPORT_INTRODUCE_WS,
            TRANSPORT_ICON_WS,
            serviceTrafficList,
            mBinding.serviceTrafficRv,
            serviceTrafficAdapter
        )
        setAdapter(TOUR_TITLE_WS, TOUR_COLOR_TYPE_WS, TOUR_INTRODUCE_WS, TOUR_ICON_WS, servicePlayList, mBinding.servicePlayRv, servicePlayAdapter)
        setAdapter(PUBLIC_TITLE_WS, PUBLIC_COLOR_TYPE_WS, PUBLIC_INTRODUCE_WS, PUBLIC_ICON_WS, servicePublicList, mBinding.servicePublicRv, servicePublicAdapter)
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
            layoutManager = GridLayoutManager(activity, 2)
            adapter = serviceTravelAdapter
        }
        serviceTravelAdapter.add(serviceTravelList)
        serviceTravelAdapter.notifyDataSetChanged()
    }

    var serviceTravelAdapter = object : RecyclerViewAdapter<ItemXinjiangServiceMenuBinding, ServiceSubTypeXJ>(com.daqsoft.android.scenic.servicemodule.R.layout.item_xinjiang_service_menu) {
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
                }
            }
        }
    }

    private fun getServiceBg(colorType: String): Int {
        when (colorType) {
            "yellow" -> {
                return com.daqsoft.android.scenic.servicemodule.R.drawable.service_home_fff9e9_radius
            }
            "red" -> {
                return com.daqsoft.android.scenic.servicemodule.R.drawable.service_home_fff2f1_radius
            }
            "blue" -> {
                return com.daqsoft.android.scenic.servicemodule.R.drawable.service_home_e4f9fe_radius
            }
            "purple" -> {
                return com.daqsoft.android.scenic.servicemodule.R.drawable.service_home_f2f2ff_radius
            }
            "green" -> {
                return com.daqsoft.android.scenic.servicemodule.R.drawable.service_home_66f9e4_radius
            }

        }
        return com.daqsoft.android.scenic.servicemodule.R.drawable.service_home_fff9e9_radius
    }

    var servicePlayAdapter = object : RecyclerViewAdapter<ItemXinjiangServiceMenuBinding, ServiceSubTypeXJ>(com.daqsoft.android.scenic.servicemodule.R.layout.item_xinjiang_service_menu) {
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
                        IdentifyCodeDialog().show(childFragmentManager, "")
                    }
                    3 -> {
                        // "美食"
                        ARouter.getInstance().build(MainARouterPath.MAIN_FOOD_LS)
                            .navigation()
                    }
                }
            }
        }
    }
    var serviceTrafficAdapter = object : RecyclerViewAdapter<ItemXinjiangServiceMenuBinding, ServiceSubTypeXJ>(com.daqsoft.android.scenic.servicemodule.R.layout.item_xinjiang_service_menu) {
        override fun setVariable(mBinding: ItemXinjiangServiceMenuBinding, position: Int, item: ServiceSubTypeXJ) {
            mBinding.item = item
            mBinding.rvRoot.setBackgroundResource(getServiceBg(item.colorType))
            mBinding.imgTravel.setBackgroundResource(item.imageId)
            mBinding.root.onNoDoubleClick {
                when (item.type) {
                    0 -> {
                        //  "租车"
                        val url = BaseApplication.webSiteUrl+"use-car"
                        ARouter.getInstance()
                            .build(ARouterPath.Provider.WEB_ACTIVITY)
                            .withString("mTitle", item.typeName)
                            .withString("html", url)
                            .navigation()
                    }
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
    var servicePublicAdapter = object : RecyclerViewAdapter<ItemXinjiangServiceMenuBinding, ServiceSubTypeXJ>(com.daqsoft.android.scenic.servicemodule.R.layout.item_xinjiang_service_menu) {
        override fun setVariable(mBinding: ItemXinjiangServiceMenuBinding, position: Int, item: ServiceSubTypeXJ) {
            mBinding.item = item
            mBinding.rvRoot.setBackgroundResource(getServiceBg(item.colorType))
            //mBinding.tvTravel = item.typeName
            mBinding.imgTravel.setBackgroundResource(item.imageId)
//            mBinding.rvRoot.setPadding(0, 0, 0, resources.getDimensionPixelOffset(R.dimen.dp_22))
            mBinding.root.onNoDoubleClick {
                when (item.type) {
                    0 -> {
                        // "一键求助"
                        ARouter.getInstance().build(ARouterPath.ServiceModule.SERVICE_SOS_ACTIVITY).navigation()
                    }
                   1 -> {
                        // "涉未成年人举报热线"
                       phone="0991-4562500"
                       showCallPhoneTip(true)
                    }
                    2 -> {
                        // "文化市场举报平台"
                        phone="12318"
                        showCallPhoneTip(false)
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
        ProviderAppointDialog(activity).apply {
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
            setTips(getString(com.daqsoft.android.scenic.servicemodule.R.string.tip_out_platform))
            show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(phoneDialog!=null){
            phoneDialog = null
        }
    }

    private  var  phone: String ?= null
    fun showCallPhoneTip(is18: Boolean) {
        if (phoneDialog == null) {
            phoneDialog = BaseDialog(activity)
            phoneDialog!!.contentView(com.daqsoft.android.scenic.servicemodule.R.layout.call_phone_base_dialog)
                .layoutParams(
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
                .gravity(Gravity.CENTER)
                .animType(BaseDialog.AnimInType.BOTTOM)
                .canceledOnTouchOutside(false)
            phoneDialog!!.findViewById<TextView>(com.daqsoft.android.scenic.servicemodule.R.id.tv_title).text = "拨打电话"

            phoneDialog!!.findViewById<TextView>(com.daqsoft.android.scenic.servicemodule.R.id.tv_cancel).setOnClickListener {
                phoneDialog!!.dismiss()
            }
            phoneDialog!!.findViewById<TextView>(com.daqsoft.android.scenic.servicemodule.R.id.tv_query).setOnClickListener {
                phoneDialog!!.dismiss()
                SystemHelper.callPhone(BaseApplication.context!!, phone!!)
            }
        }
        if(is18){
            phoneDialog?.findViewById<TextView>(com.daqsoft.android.scenic.servicemodule.R.id.tv_content)?.text = "是否立即拨打电话${phone}？"
        }else{
            phoneDialog?.findViewById<TextView>(com.daqsoft.android.scenic.servicemodule.R.id.tv_content)?.text = "是否立即拨打电话${phone}？"
        }
        phoneDialog?.show()
    }
}