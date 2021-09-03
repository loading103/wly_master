package com.daqsoft.provider.businessview.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.*
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import com.daqsoft.baselib.widgets.dialog.QrCodeDialog
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ResourceDateBean
import com.daqsoft.provider.bean.TimeAppointBean
import com.daqsoft.provider.bean.VenueDateInfo
import com.daqsoft.provider.businessview.adapter.ProviderAppointAdapter
import com.daqsoft.provider.businessview.adapter.ProviderTImeAppointAdapter
import com.daqsoft.provider.businessview.viewmodel.AppointmentViewModel
import com.daqsoft.provider.databinding.FragResourceAppointmentBinding
import com.daqsoft.provider.utils.dialog.ProviderAppointDialog
import java.util.*

/**
 * @Description 预约相关信息
 * @ClassName   AppointmentFragment
 * @Author      luoyi
 * @Time        2020/5/26 13:47
 */
class AppointmentFragment : BaseFragment<FragResourceAppointmentBinding, AppointmentViewModel>() {
    /**
     * 第三方预约适配器
     */
    private var adapater: ProviderAppointAdapter? = null
    /**
     * 分时预约适配器
     */
    private var customTimeAadpter: ProviderTImeAppointAdapter? = null

    /**
     * 资源类型id
     */
    private var resourceId: String? = ""
    /**
     * 资源类型
     */
    private var resourceType: String? = ""
    /**
     * 站点id
     * ..
     */
    private var siteId: String? = ""
    /**
     * 预约弹窗
     */
    private var providerAppointDialog: ProviderAppointDialog? = null
    /**
     * 当前选择预约实体
     */
    private var selectAppointMentBean: ResourceDateBean? = null
    /**
     * 二维码展示dialog
     */
    private var mQrCodeDialog: QrCodeDialog? = null
    /**
     * 下单类型
     */
    private var orderAddressType: String? = null


    /**
     * 从哪个页面跳转过来
     */
    private var fromWhichPage: String? = null

    var onAppointmentListener: OnAppointmentListener? = null

    companion object {
        private const val RESOURCE_ID = "resourceId"
        private const val RESOURCE_TYPE = "resourceType"
        private const val SITE_ID = "siteId"
        private const val ORDER_ADDRESS_TYPE = "orderAddressType"

        private const val FROM_WHICH_PAGE = "fromWhichPage"

        fun newInstance(
            resourceId: String,
            resourceType: String,
            siteId: String,
            orderAddressType: String,
            fromWhichPage: String? = ""
        ): AppointmentFragment {
            var frag = AppointmentFragment()
            var bundle = Bundle()
            bundle.putString(RESOURCE_ID, resourceId)
            bundle.putString(RESOURCE_TYPE, resourceType)
            bundle.putString(SITE_ID, siteId)
            bundle.putString(ORDER_ADDRESS_TYPE, orderAddressType)
            bundle.putString(FROM_WHICH_PAGE, fromWhichPage)
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayout(): Int {
        return R.layout.frag_resource_appointment
    }

    override fun injectVm(): Class<AppointmentViewModel> {
        return AppointmentViewModel::class.java
    }

    override fun initView() {
        getParam()
        initViewModel()
    }

    private fun initViewModel() {
        mModel.resourcesOrderLiveData.observe(this, Observer {
            if (!it.isNullOrEmpty()) {

                fromWhichPage?.let {
                    if (it == ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY) {
                        onAppointmentListener?.onGetAppointDate(true)
                    }
                }
                mBinding.tvVenuesDetailsAround.visibility = View.VISIBLE
                mBinding.recyAppointmentes.visibility = View.VISIBLE
                adapater?.clear()
                adapater?.add(it)
            } else {

                fromWhichPage?.let {
                    if (it == ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY) {
                        onAppointmentListener?.onGetAppointDate(true)
                    }
                }


                mBinding.tvVenuesDetailsAround.visibility = View.GONE
                mBinding.recyAppointmentes.visibility = View.GONE
            }
        })
        mModel.resourceIdLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            if (!it.isNullOrEmpty()) {
                // 返回 加密信息不为空
                var siteId = SPUtils.getInstance().getString(SPKey.SITE_ID)
                if(selectAppointMentBean?.link.isNullOrBlank()){
                    return@Observer
                }
                ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString(
                        "html",
                        StringUtil.getJingxinUrl(selectAppointMentBean!!.link, it, siteId)
                    )
                    .navigation()
            } else {
                ToastUtils.showMessage("未找到授权信息~")
            }
        })
        mModel.timeAppointLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            if (!it.isNullOrEmpty()) {
                onAppointmentListener?.onGetAppointDate(false)
                customTimeAadpter?.clear()
                customTimeAadpter?.add(it)
            } else {
                mBinding.tvVenuesDetailsAround.visibility = View.GONE
                onAppointmentListener?.onGetAppointDate(true)
            }
        })
    }

    private fun getParam() {
        resourceId = arguments?.getString(RESOURCE_ID)
        resourceType = arguments?.getString(RESOURCE_TYPE)
        siteId = arguments?.getString(SITE_ID)
        orderAddressType = arguments?.getString(ORDER_ADDRESS_TYPE)

        fromWhichPage = arguments?.getString(FROM_WHICH_PAGE)
        if (!resourceType.isNullOrEmpty()) {
            if (resourceType == ResourceType.CONTENT_TYPE_SCENERY) {
                mBinding.tvVenuesDetailsAround.text = "景区预约"
            } else if (resourceType == ResourceType.CONTENT_TYPE_VENUE) {
                mBinding.tvVenuesDetailsAround.text = "场馆预约"
            }
        }
    }

    override fun initData() {
        if (!orderAddressType.isNullOrEmpty()) {
            when (orderAddressType) {
                "custom" -> {
                    // 分时预约
                    initCustomView()
                }
                "pt" -> {
                    // 第三方跳转
                    initPtView()
                }
                else -> {
                    //默认第三方
                    initPtView()
                }
            }
        } else {
            initPtView()
        }


    }

    private fun initCustomView() {
        customTimeAadpter = ProviderTImeAppointAdapter(context!!)
        mBinding.recyAppointmentes.layoutManager = GridLayoutManager(
            context!!, 3,
            LinearLayoutManager.VERTICAL, false
        )
        mBinding.recyAppointmentes.adapter = customTimeAadpter
        customTimeAadpter?.onSelectDateListener =
            object : ProviderTImeAppointAdapter.OnSelectDateListener {
                override fun onErrorTip(code: Int) {
                }

                override fun onChangedDate(item: VenueDateInfo) {
                }

                override fun onToSelectDate(item: TimeAppointBean) {
                    item.let {
                        if (it.openStatus == 1 && item.maxNum > 0) {
                            if (AppUtils.isLogin()) {
                                if (!resourceType.isNullOrEmpty()) {
                                    if (resourceType == ResourceType.CONTENT_TYPE_VENUE) {
                                        ARouter.getInstance()
                                            .build(ARouterPath.VenuesModule.VENUES_RESERVATION_V1_ACTIVITY)
                                            .withString("venueId", resourceId)
                                            .withString("selectDate", item.date)
                                            .navigation()
                                    } else if (resourceType == ResourceType.CONTENT_TYPE_SCENERY) {
                                        ARouter.getInstance()
                                            .build(ARouterPath.VenuesModule.SCENIC_RESERVATION_ACTIVITY)
                                            .withString("scenicId", resourceId)
                                            .withString("selectDate", item.date)
                                            .navigation()
                                    }
                                }
                            } else {
                                ToastUtils.showUnLoginMsg()
                                ARouter.getInstance()
                                    .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                    .navigation()
                            }
                        }
                    }
                }

            }
        if (!resourceId.isNullOrEmpty() && !resourceType.isNullOrEmpty()) {
            var currentDate = DateUtil.getDateDayString("yyyy-MM-dd", Date())
            mModel.getResourceTimeList(resourceType!!, currentDate, "7", resourceId!!)
        }
    }

    private fun initPtView() {
        adapater = ProviderAppointAdapter(context!!)
        adapater?.onProviderAppointAdapter =
            object : ProviderAppointAdapter.OnProviderAppointListener {
                override fun toAppointment(item: ResourceDateBean, name: String) {
                    selectAppointMentBean = item
                    // 提示即将离开此平台
                    if (AppUtils.isLogin()) {
                        if(selectAppointMentBean?.link.isNullOrBlank()){
                            return
                        }
                        if (providerAppointDialog == null) {
                            providerAppointDialog = ProviderAppointDialog(context!!)
                        }
                        providerAppointDialog?.onProviderAppointClickListener = object
                            : ProviderAppointDialog.OnProviderAppointClickListener {
                            override fun onProviderAppointClick() {
                                showLoadingDialog()
                                var type = selectAppointMentBean?.jumpType
                                if (!type.isNullOrEmpty()) {
                                    if (type == "CODE") {
                                        // 二维码 展示 二维码
                                        dissMissLoadingDialog()
                                        dealShowQrCode(name)
                                    } else {
                                        mModel.getResourceIdentity()
                                    }
                                } else {
                                    dissMissLoadingDialog()
                                    ToastUtils.showMessage("返回数据异常~")
                                }

                            }
                        }
                        providerAppointDialog?.updateData(name)
                        providerAppointDialog?.show()
                    } else {
                        ToastUtils.showUnLoginMsg()
                        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                            .navigation()
                    }
                }
            }
        adapater?.emptyViewShow = false
        mBinding.recyAppointmentes.layoutManager =
            LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        mBinding.recyAppointmentes.adapter = adapater
        if (!resourceId.isNullOrEmpty() && !resourceType.isNullOrEmpty()) {
            mModel.getScenicAppointmentList(resourceId!!, resourceType!!)
        }
    }

    /**
     * 处理二维码显示
     */
    private fun dealShowQrCode(name: String) {
        if (selectAppointMentBean != null && selectAppointMentBean!!.link.isNullOrEmpty()) {

            if (mQrCodeDialog == null) {
                mQrCodeDialog =
                    QrCodeDialog.Builder().qrCodeImageUrl(selectAppointMentBean!!.link).title(name)
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
                        .build(context!!)
            } else {
                mQrCodeDialog?.updateData(selectAppointMentBean!!.link, name)
            }
            mQrCodeDialog!!.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        providerAppointDialog = null
        mQrCodeDialog = null
    }

    interface OnAppointmentListener {
        fun onGetAppointDate(isHide: Boolean)
    }
}