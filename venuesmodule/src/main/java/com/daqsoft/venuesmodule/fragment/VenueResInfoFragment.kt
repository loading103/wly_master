package com.daqsoft.venuesmodule.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.CertTypes
import com.daqsoft.provider.bean.Venue
import com.daqsoft.provider.bean.VenueOrder
import com.daqsoft.provider.bean.VenueOrderViewInfo
import com.daqsoft.provider.network.venues.VenuesRepository
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.FragVenueResInfoBinding
import com.daqsoft.venuesmodule.fragment.VenueSelectResevationFragment.Companion.VENUE_ID
import java.lang.Exception

/**
 * @Description 订单列表->讲解员预约 显示预约信息
 * @ClassName   VenueResInfoFragment
 * @Author      luoyi
 * @Time        2020/7/6 19:33
 */
class VenueResInfoFragment : BaseFragment<FragVenueResInfoBinding, VenueResInfoViewModel>() {

    var venueOrder: VenueOrder? = null

    companion object {
        const val VENUE_ORDER = "venue_order"
        fun newIntance(venueOrder: VenueOrder): VenueResInfoFragment {
            var frag: VenueResInfoFragment = VenueResInfoFragment()
            var bundle: Bundle = Bundle()
            bundle.putParcelable(VENUE_ORDER, venueOrder)
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayout(): Int {
        return R.layout.frag_venue_res_info
    }

    override fun injectVm(): Class<VenueResInfoViewModel> {
        return VenueResInfoViewModel::class.java
    }

    override fun initView() {
        initViewEvent()
        initViewModel()
    }

    private fun initViewModel() {
        // 场馆预约信息
        mModel.venueOrderViewInfoLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null) {
                bindVenueInfo(it)
            }
        })
    }

    private fun bindVenueInfo(data: VenueOrderViewInfo?) {
        if (data != null) {
            loadVenueImage(data)
            mBinding.txtVenueName.text = "" + data.venueName
            mBinding.txtVenueType.text = "" + data.type
            mBinding.txtVenueMaxPerson.text = "最大接待人数：" + data.maxNum + "人"
        }
    }

    /**
     * 加载场馆图片
     */
    private fun loadVenueImage(data: VenueOrderViewInfo) {
        var imageUrl = ""
        if (!data.images.isNullOrEmpty()) {
            var images = data.images.split(",")
            if (!images.isNullOrEmpty()) {
                imageUrl = images[0]
            }
        }
        Glide.with(context!!)
            .load(imageUrl)
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(mBinding.imgVenue)
    }

    private fun initViewEvent() {
        mBinding.tvMoreVenueResInfo?.onNoDoubleClick {
            mBinding.clMoreResInfo?.visibility = View.VISIBLE
            mBinding.tvMoreVenueResInfo?.visibility = View.GONE
        }
    }

    override fun initData() {
        try {
            venueOrder = arguments?.getParcelable(VENUE_ORDER)
            if (venueOrder != null) {
                bindVenueOrderInfo()
                mModel.getVenueOrderView(venueOrder!!.venueId!!, "")
            }

        } catch (e: Exception) {

        }
    }

    private fun bindVenueOrderInfo() {
        venueOrder?.let {
            if (!it.reservationType.isNullOrEmpty()) {
                mBinding.tvVenueResTypeValue.text = if (it.reservationType == "PERSON") {
                    mBinding.tvVenueResCompanyLable.visibility = View.GONE
                    mBinding.tvVenueResCompanyValue.visibility = View.GONE
                    mBinding.lineVenueResCompany.visibility = View.GONE
                    "个人预约"
                } else {
                    mBinding.tvVenueResCompanyValue.text = "${it.companyName}"
                    mBinding.tvVenueResCompanyLable.visibility = View.VISIBLE
                    mBinding.tvVenueResCompanyValue.visibility = View.VISIBLE
                    mBinding.lineVenueResCompany.visibility = View.VISIBLE
                    "团队预约"
                }
            }
            mBinding.tvVenueResDateValue.text = "${it.orderDate} ${DateUtil.getDayOfWeekV2(
                DateUtil.formatDate(
                    "yyyy-MM-dd",
                    it.orderDate
                )
            )}"
            // 时间段
            if (it.orderTime != null) {
                mBinding.tvVenueResTimeValue.text =
                    "${it.orderTime!!.startTime}-${it.orderTime!!.endTime}"
            }

            mBinding.tvVenueResPnumValue.text = "${it.useNum}"

            mBinding.tvVenueResContactValue.text = "${it.userPhone}"
            mBinding.tvVenueResPnameValue.text = "${it.userName}"

            var cardTypeStr: String? = if (!it.cardType.isNullOrEmpty()) {
                CertTypes.getCertTypeName(it.cardType)
            } else {
                ""
            }
            if (cardTypeStr.isNullOrEmpty()) {
                mBinding.tvVenueResIdcardValue.text = "${it.idCard}"
            } else {
                mBinding.tvVenueResIdcardValue.text = "(${cardTypeStr})${it.idCard}"
            }

        }
    }
}

class VenueResInfoViewModel : BaseViewModel() {
    /**
     * 下单详情数据
     */
    var venueOrderViewInfoLiveData: MutableLiveData<VenueOrderViewInfo> = MutableLiveData()

    fun getVenueOrderView(venueId: String, date: String) {
        mPresenter.value?.loading = false
        VenuesRepository.venuesService.getVenueOrderView(venueId)
            .excute(object : BaseObserver<VenueOrderViewInfo>(mPresenter) {
                override fun onSuccess(response: BaseResponse<VenueOrderViewInfo>) {
                    venueOrderViewInfoLiveData.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<VenueOrderViewInfo>) {
                    mError.postValue(response)
                }

            })
    }

}