package com.daqsoft.provider.businessview.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.SptTicketBean
import com.daqsoft.provider.businessview.adapter.ProviderScenicTiketAdapter
import com.daqsoft.provider.businessview.view.ProviderAppointentPopWindow
import com.daqsoft.provider.businessview.viewmodel.ProviderTicketBookingViewModel
import com.daqsoft.provider.databinding.ItemProviderTicketBookingBinding

/**
 * @Description
 * @ClassName   ProviderTicketBookingFragment
 * @Author      luoyi
 * @Time        2020/11/5 15:07
 */
class ProviderTicketBookingFragment :
    DialogFragment() {

    public var resourceId: String? = ""

    public var resouceType: String? = ""

    private lateinit var mBinding: ItemProviderTicketBookingBinding

    private lateinit var mModel: ProviderTicketBookingViewModel

    private var shopName: String? = ""
    private var shopUrl: String? = ""
    var mScenicTiketBean: SptTicketBean? = null

    public var onScenicTiketViewListener: OnScenicTiketViewListener? = null

    /**
     * 景区预览须知window
     */
    var appointMentPopWindow: ProviderAppointentPopWindow? = null

    var scenicTiketAdapter: ProviderScenicTiketAdapter? = null

    companion object {
        const val RESOURCE_ID = "resource_id"
        const val RESOURCE_TYPE = "resource_type"
        const val SHOP_NAME = "shop_name"
        const val SHOP_URL = "shop_url"
        fun newInstance(shopName: String, shopUrl: String): ProviderTicketBookingFragment {
            var frag = ProviderTicketBookingFragment()
            var bundle: Bundle = Bundle()
            bundle.putString(SHOP_NAME, shopName)
            bundle.putString(SHOP_URL, shopUrl)
            frag.arguments = bundle
            return frag
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_provider_ticket_booking, container, false)
        mModel = ProviderTicketBookingViewModel()
        initView()
        initUiData()
        return mBinding.root
    }

    private fun initUiData() {
    }

    private fun initView() {
        scenicTiketAdapter = ProviderScenicTiketAdapter(context!!, shopName, shopUrl)
        mBinding?.rvAllTicketBooks?.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        mBinding?.rvAllTicketBooks?.adapter = scenicTiketAdapter
        scenicTiketAdapter!!.onScenicTiketListener = object : ProviderScenicTiketAdapter.OnScenicTiketProductListener {
            override fun onTiketProductClick(productId: String?) {
                productId?.let {
                    mModel.getReservationInfo(it)
                }

            }

        }
        scenicTiketAdapter?.clear()
        mScenicTiketBean?.let {
            scenicTiketAdapter?.add(it.sptTitleList)
        }
        mBinding.tvTicketBookingClose.onNoDoubleClick {
            dismiss()
        }
        mBinding.vOutsideContent.onNoDoubleClick {
            dismiss()
        }
        mModel.scenicReservationLiveData.observe(this, Observer {
            if (it != null) {
                if (appointMentPopWindow == null) {
                    appointMentPopWindow = ProviderAppointentPopWindow(context!!)
                }
                appointMentPopWindow!!.updateData(it)
                appointMentPopWindow!!.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        resourceId = arguments?.getString(RESOURCE_ID, "")
        resouceType = arguments?.getString(RESOURCE_TYPE, "")
        shopName = arguments?.getString(SHOP_NAME, "")
        shopUrl = arguments?.getString(SHOP_URL, "")
        scenicTiketAdapter?.shopUrl = shopUrl ?: ""
        scenicTiketAdapter?.shopName = shopName ?: ""
    }

    private fun initData() {
    }

    override fun onStart() {
        super.onStart()
        initDialogWindow()
    }

    private fun initDialogWindow() {
        val win = dialog!!.window
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.transparent)))
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        val params = win.attributes
        params.gravity = Gravity.BOTTOM
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        win.attributes = params
        win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    interface OnScenicTiketViewListener {
        fun onScenicTiketGetRegistinfo(productId: String?)
    }
}