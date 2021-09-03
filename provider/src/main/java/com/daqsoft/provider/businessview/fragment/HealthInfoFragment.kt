package com.daqsoft.provider.businessview.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.dialog.QrCodeDialog
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.HelathInfoBean
import com.daqsoft.provider.bean.HelathRegionBean
import com.daqsoft.provider.bean.HelathSetingBean
import com.daqsoft.provider.businessview.view.UnRegsiterHealthCodeDialog
import com.daqsoft.provider.businessview.view.ZyTfCodeTipDialog
import com.daqsoft.provider.businessview.viewmodel.HelathInfoViewModel
import com.daqsoft.provider.databinding.FragHelathInfoBinding
import com.daqsoft.provider.view.LabelsView

/**
 * @Description 健康信息
 * @ClassName   HelathInfoFragment
 * @Author      luoyi
 * @Time        2020/7/9 10:47
 */
class HealthInfoFragment : BaseFragment<FragHelathInfoBinding, HelathInfoViewModel>() {

    var phone: String? = ""
    var idCard: String? = ""
    var region: String? = ""
    var name: String? = ""

    var healthSetingInfo: HelathSetingBean? = null

    var zyTfCodeTipDialog: ZyTfCodeTipDialog? = null

    var unregisterHealthCodeDialog: UnRegsiterHealthCodeDialog? = null

    var isInitHealth: Boolean = true
    /**
     * 健康码地区数据集
     */
    var mDatasHealthRegions: MutableList<HelathRegionBean> = mutableListOf()

    var healthInfo: HelathInfoBean? = null

    var currentRegion: HelathRegionBean? = null
    /**
     * 回调事件
     */
    var onHealthInfoListener: OnHealthInfoListener? = null
    /**
     * 是否可以预约
     */
    var isCanReservation: Int = 0

    companion object {
        const val PHONE = "phone"
        const val IDCARD = "idcard"
        const val REGION = "region"
        const val NAME = "name"

        fun newInstance(
            phone: String?,
            idCard: String?,
            region: String?,
            name: String?
        ): HealthInfoFragment {
            var bundle = Bundle()
            bundle.putString(PHONE, phone)
            bundle.putString(IDCARD, idCard)
            bundle.putString(REGION, region)
            bundle.putString(NAME, name)
            var frag: HealthInfoFragment = HealthInfoFragment()
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayout(): Int {
        return R.layout.frag_helath_info
    }

    override fun injectVm(): Class<HelathInfoViewModel> {
        return HelathInfoViewModel::class.java
    }

    override fun initView() {
        getParams()
        showUnInputView()
        initViewModel()
        initViewEvent()
    }

    private fun initViewEvent() {
        mBinding.llvHealthTypies.setOnLabelSelectChangeListener(object :
            LabelsView.OnLabelSelectChangeListener {
            override fun onLabelSelectChange(
                label: TextView?,
                data: Any?,
                isSelect: Boolean,
                position: Int
            ) {
                if (isSelect) {
                    label?.setTextColor(resources.getColor(R.color.c_36cd64))
                    label?.setBackgroundResource(R.drawable.shape_venue_selected_r3)
                    if (position < mDatasHealthRegions.size) {
                        var region = mDatasHealthRegions[position]
                        if (region != null) {
                            currentRegion = region
                            // 取消第一次初始化判断
                            if (!isInitHealth) {
                                if (name.isNullOrEmpty()) {
                                    ToastUtils.showMessage("请输入您的真实姓名")
                                } else if (phone.isNullOrEmpty()) {
                                    ToastUtils.showMessage("请输入您的手机号")
                                } else if (idCard.isNullOrEmpty()) {
                                    ToastUtils.showMessage("请输入您的身份证信息")
                                } else {
                                    if (healthSetingInfo!!.enableHealthyCode && healthSetingInfo!!.enableTravelCode) {
                                        showLoadingDialog()
                                        mModel.getHelathInfoAndReister(
                                            name!!,
                                            idCard!!,
                                            phone!!,
                                            currentRegion!!.getCurrentRegion()
                                        )
                                    } else {
                                        showLoadingDialog()
                                        mModel.getHealthInfo(
                                            phone!!,
                                            currentRegion!!.getCurrentRegion(),
                                            name!!,
                                            idCard!!
                                        )
                                    }
                                }
                            } else {
                                setIdCardAndPhone(phone, idCard, name)
                            }
                        }
                    }
                } else {
                    label?.setTextColor(resources.getColor(R.color.color_333))
                    label?.setBackgroundResource(R.drawable.shape_venue_default_r3)
                }
                isInitHealth = false
            }
        })
        mBinding.vZytfCodeInfo.onNoDoubleClick {
            if (healthSetingInfo != null) {
                var name = healthSetingInfo?.smartTravelName
                ARouter.getInstance().build(ARouterPath.VenuesModule.VENUES_ZYTF_CODE_INFO_ACTIVITY)
                    .withString("introduce", healthSetingInfo!!.healthIntroduce)
                    .withString("name", name)
                    .navigation()
            }
        }
        mBinding.tvTipToRegisterHealthCode.onNoDoubleClick {
            showUnregisterHealthDialog()
        }
        mBinding.llvHealthTypies.setmOnLabelShowMoreListener(object :
            LabelsView.OnLabelShowMoreListener {
            override fun onLabeShowLoadMore() {
                mBinding.tvMoreHealthTyoe.visibility = View.VISIBLE
            }
        })
        mBinding.tvMoreHealthTyoe.onNoDoubleClick {
            if (mBinding.llvHealthTypies.maxLines == -1) {
                mBinding.llvHealthTypies.maxLines = 2
                val topDrawable = resources.getDrawable(R.mipmap.provider_arrow_down)
                topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
                mBinding.tvMoreHealthTyoe.setCompoundDrawables(null, null, topDrawable, null)
                mBinding.tvMoreHealthTyoe.text = "查看更多"
            } else {
                mBinding.tvMoreHealthTyoe.text = "收起更多"
                mBinding.llvHealthTypies.maxLines = -1
                val topDrawable = resources.getDrawable(R.mipmap.provider_arrow_up)
                topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
                mBinding.tvMoreHealthTyoe.setCompoundDrawables(null, null, topDrawable, null)
            }
        }
    }

    private fun initViewModel() {
        mModel.healthSetingLd.observe(this, Observer {
            if (it != null) {
                healthSetingInfo = it
                if (!it.enableHealthyCode && !it.enableTravelCode) {
                    mBinding.root.visibility = View.GONE
                    isCanReservation = 1
                } else {
                    mBinding.root.visibility = View.GONE
                    if (it.enableHealthyCode) {
                        mModel.getHealthRegion()
                        if (!it.reserveOption.isNullOrEmpty() && it.reserveOption == "LowRisk") {
                            onHealthInfoListener?.onShowHealthTip()
                        }
                    } else {
                        if (it.enableTravelCode) {
                            mBinding.tvUnInputInfo.visibility = View.VISIBLE
                            setIdCardAndPhone(phone, idCard, name)
                        }
                    }
                }
                if (!healthSetingInfo!!.smartTravelName.isNullOrEmpty()) {
                    mBinding.tvTravelCodeName.text = getString(
                        R.string.travel_code_name,
                        healthSetingInfo!!.smartTravelName
                    )
                }
            } else {
                mBinding.root.visibility = View.GONE
            }
        })
        mModel.healthInfoLd.observe(this, Observer {
            dissMissLoadingDialog()
            healthInfo = it
            if (it != null) {
                showHealthCodeInfo(it)
            } else {
                showUnregisterHealthDialog()
                showUnRegiseterHealthCodeInfo()
            }
        })
        mModel.healthInfoAndRegisterLd.observe(this, Observer {
            dissMissLoadingDialog()
            healthInfo = it
            if (it != null) {
                showHealthCodeInfo(it)
            }
        })
        mModel.healthRegionsLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (!it.isNullOrEmpty()) {
                mDatasHealthRegions.clear()
                mDatasHealthRegions.addAll(it)
                var temps: MutableList<String> = mutableListOf()
                for (item in mDatasHealthRegions) {
                    if (item != null && !item.name.isNullOrEmpty())
                        temps.add(item.name)
                }
                mBinding.llvHealthTypies.setLabels(temps)
                mBinding.llvHealthTypies.maxLines = 2
                mBinding.tvHealthAddress.visibility = View.VISIBLE

            }
        })
        mModel.travelCodeInfoLd.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.llvZyCodeInfo.visibility = View.VISIBLE
            mBinding.tvUnInputInfo.visibility = View.GONE
            // 旅游码
            if (it) {
                // 注册
                mBinding.imgHealthCodeStatus.setImageResource(R.mipmap.venue_book_condition_icon_low)
                mBinding.tvHealthCodeStatus.text = "已注册"
                mBinding.tvHealthCodeStatus.setTextColor(context!!.resources.getColor(R.color.c_36cd64))
            } else {
                // 未注册
                mBinding.imgHealthCodeStatus.setImageResource(R.mipmap.venue_book_condition_icon_unknown)
                mBinding.tvHealthCodeStatus.text = "未注册"
                mBinding.tvHealthCodeStatus.setTextColor(context!!.resources.getColor(R.color.color_999))
            }
            if (!healthSetingInfo!!.enableHealthyCode) {
                isCanReservation = 1
            }
        })
    }

    private fun getParams() {
        phone = arguments?.getString(PHONE)
        name = arguments?.getString(NAME)
        idCard = arguments?.getString(IDCARD)
        region = arguments?.getString(REGION)

    }

    override fun initData() {
        mModel.getHealthSetingInfo()
    }

    /**
     * 显示未输入身份证号和手机信息
     */
    private fun showUnInputView() {
        mBinding.rvUnRegisterHealthCode.visibility = View.GONE
        mBinding.tvUnInputInfo.visibility = View.VISIBLE
        mBinding.llvHealthCodeInfo.visibility = View.GONE
        mBinding.llvZyCodeInfo.visibility = View.GONE
    }

    /**
     * 显示健康码信息
     */
    private fun showHealthCodeInfo(data: HelathInfoBean) {
        mBinding.rvUnRegisterHealthCode.visibility = View.GONE
        mBinding.tvUnInputInfo.visibility = View.GONE
        if (healthSetingInfo != null && (healthSetingInfo!!.enableHealthyCode || healthSetingInfo!!.enableTravelCode)) {
            mBinding.root.visibility = View.VISIBLE
            // 旅游码
            dealTravelCodeInfo(data)
            // 健康码
            dealHealthCodeInfo(data)
        } else {
            mBinding.root.visibility = View.GONE
        }
    }

    /**
     * 处理旅游码
     */
    private fun dealTravelCodeInfo(data: HelathInfoBean) {
        if (healthSetingInfo!!.enableTravelCode) {
            mBinding.llvZyCodeInfo.visibility = View.VISIBLE
            // 智游码
            mBinding.llvZyCodeInfo.visibility = View.VISIBLE
            if (!data.smartTravelCodeRegisterStatus) {
                // 未注册
                mBinding.imgHealthCodeStatus.setImageResource(R.mipmap.venue_book_condition_icon_unknown)
                mBinding.tvHealthCodeStatus.text = "未注册"
                mBinding.tvHealthCodeStatus.setTextColor(context!!.resources.getColor(R.color.color_999))
                if (!healthSetingInfo!!.enableHealthyCode) {
                    isCanReservation = 1
                }
                showZyTfCodeTipDialog()
            } else {
                // 注册
                mBinding.imgHealthCodeStatus.setImageResource(R.mipmap.venue_book_condition_icon_low)
                mBinding.tvHealthCodeStatus.text = "已注册"
                mBinding.tvHealthCodeStatus.setTextColor(context!!.resources.getColor(R.color.c_36cd64))
                if (!healthSetingInfo!!.enableHealthyCode) {
                    isCanReservation = 1
                }
            }
        } else {
            mBinding.llvZyCodeInfo.visibility = View.GONE
        }
    }

    /**
     * 处理健康码
     */
    private fun dealHealthCodeInfo(data: HelathInfoBean) {
        if (healthSetingInfo!!.enableHealthyCode) {
            mBinding.tvHealthAddress.visibility = View.VISIBLE
            mBinding.llvHealthTypies.visibility = View.VISIBLE
            mBinding.llvHealthCodeInfo.visibility = View.VISIBLE
            // 健康状态 01 正常 11 黄码 31红码
            if (!data.healthCode.isNullOrEmpty()) {
                when (data.healthCode) {
                    "01" -> {
                        // 绿码
                        mBinding.tvHealthStatus.text = getString(R.string.health_normal)
                        mBinding.tvHealthStatus.setTextColor(resources.getColor(R.color.c_36cd64))
                        mBinding.imgHealthStatus.setImageResource(R.mipmap.venue_book_condition_icon_low)
                        mBinding.tvHealthCanReservation.text =
                            getString(R.string.health_can_reseravtion)
                        mBinding.tvHealthCanReservation.setTextColor(resources.getColor(R.color.c_36cd64))
                        isCanReservation = 1
                    }
                    "11" -> {
                        // 黄码
                        mBinding.tvHealthStatus.text = getString(R.string.health_middle)
                        mBinding.tvHealthStatus.setTextColor(resources.getColor(R.color.color_ff9e05))
                        mBinding.tvHealthCanReservation.setTextColor(resources.getColor(R.color.color_ff9e05))
                        mBinding.imgHealthStatus.setImageResource(R.mipmap.venue_book_condition_icon_middle)
                        isCanReservation()
                    }
                    "31" -> {
                        // 红码
                        mBinding.tvHealthStatus.text = getString(R.string.health_bad)
                        mBinding.tvHealthStatus.setTextColor(resources.getColor(R.color.ff4e4e))
                        mBinding.tvHealthCanReservation.setTextColor(resources.getColor(R.color.ff4e4e))
                        mBinding.imgHealthStatus.setImageResource(R.mipmap.venue_book_condition_icon_high)
                        isCanReservation()
                    }
                }
            } else {
                // 未注册健康码
                showUnregisterHealthDialog()
                showUnRegiseterHealthCodeInfo()
            }
        } else {
            mBinding.tvHealthAddress.visibility = View.GONE
            mBinding.llvHealthTypies.visibility = View.GONE
            mBinding.llvHealthCodeInfo.visibility = View.GONE
        }
    }

    /**
     * 是否可以预订处理
     */
    private fun isCanReservation() {
        if (healthSetingInfo != null) {
            if (!healthSetingInfo!!.reserveOption.isNullOrEmpty()) {
                if (healthSetingInfo!!.reserveOption == "All") {
                    mBinding.tvHealthCanReservation.text = ""
                    isCanReservation = 1
                } else {
                    mBinding.tvHealthCanReservation.text =
                        getString(R.string.health_canot_reseravtion)
                    isCanReservation = 2
                }
            }
        }
    }

    /**
     * 显示未注册健康信息
     */
    private fun showUnRegiseterHealthCodeInfo() {
        mBinding.rvUnRegisterHealthCode.visibility = View.VISIBLE
        mBinding.tvUnInputInfo.visibility = View.GONE
        mBinding.llvHealthCodeInfo.visibility = View.GONE
    }

    /**
     * 设置用户信息
     */
    @Synchronized
    public fun setIdCardAndPhone(phone: String?, idCard: String?, name: String?) {
        if (!phone.isNullOrEmpty() && !idCard.isNullOrEmpty() && !name.isNullOrEmpty()) {
            this.phone = phone
            this.idCard = idCard
            this.name = name
            mBinding.root.visibility = View.VISIBLE
        } else {
            return
        }
        if (healthSetingInfo != null && !(!healthSetingInfo!!.enableHealthyCode && !healthSetingInfo!!.enableTravelCode)) {
            if (healthSetingInfo!!.enableHealthyCode && healthSetingInfo!!.enableTravelCode) {
                if (!mDatasHealthRegions.isNullOrEmpty() && currentRegion != null) {
                    showLoadingDialog()
                    mModel.getHelathInfoAndReister(
                        name,
                        idCard,
                        phone,
                        currentRegion!!.getCurrentRegion()
                    )
                }
            } else {
                if (healthSetingInfo!!.enableHealthyCode) {
                    if (!mDatasHealthRegions.isNullOrEmpty() && currentRegion != null) {
                        showLoadingDialog()
                        mModel.getHealthInfo(phone, currentRegion!!.region, name, idCard)
                    }
                } else {
                    showLoadingDialog()
                    if (healthSetingInfo!!.enableTravelCode) {
                        mModel.getTravelInfo(phone, name, idCard)
                    }
                }

            }
        }
    }

    /**
     * 显示智游天府码
     */
    private fun showZyTfCodeTipDialog() {
        if (zyTfCodeTipDialog == null) {
            var name = healthSetingInfo!!.smartTravelName
            zyTfCodeTipDialog = ZyTfCodeTipDialog.Builder().setName(name)
                .setOnTipConfirmListener(object : ZyTfCodeTipDialog.OnTipConfirmListener {
                    override fun onConfirm() {
                        ARouter.getInstance()
                            .build(ARouterPath.VenuesModule.VENUES_ZYTF_CODE_INFO_ACTIVITY)
                            .withString("introduce", healthSetingInfo!!.healthIntroduce)
                            .withString("name", name)
                            .navigation()
                    }

                }).create(context!!)
        }
        zyTfCodeTipDialog?.show()
    }


    /**
     * 显示未注册健康码信息
     */
    private fun showUnregisterHealthDialog() {
        if (unregisterHealthCodeDialog == null) {
            unregisterHealthCodeDialog = UnRegsiterHealthCodeDialog.Builder()
                .onDownLoadListener(object : UnRegsiterHealthCodeDialog.OnDownLoadListener {
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
        }
        if (currentRegion != null) {
            if (!currentRegion!!.healthRegisterUrl.isNullOrEmpty()) {
                unregisterHealthCodeDialog?.updateData(currentRegion!!.healthRegisterUrl, "")
                unregisterHealthCodeDialog?.show()
            } else {
                ToastUtils.showMessage("未获取的健康码注册地址，稍后再试")
            }
        } else {
            ToastUtils.showMessage("未获取的健康码注册地址，稍后再试")
        }


    }

    /**
     * 判断是否可以预约
     */
    fun getHealthStatus(): Int {
        return isCanReservation
    }

    /**
     * 隐藏健康信息
     */
    fun hideHealthInfoView() {
        mBinding.root.visibility = View.GONE
    }

    interface OnHealthInfoListener {
        fun onShowHealthTip()
    }

}
