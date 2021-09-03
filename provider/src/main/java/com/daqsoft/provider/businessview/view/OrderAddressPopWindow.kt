package com.daqsoft.provider.businessview.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.OderAddressInfoBean
import com.daqsoft.provider.databinding.ItemProviderOrderAddressBinding
import com.daqsoft.provider.databinding.PopOrderAddressBinding
import com.daqsoft.provider.view.dialog.ProviderTipDialog
import com.daqsoft.provider.view.web.WebActivity

/**
 * @Description
 * @ClassName   OrderAddressPopWindow
 * @Author      luoyi
 * @Time        2020/4/22 13:57
 */
class OrderAddressPopWindow : PopupWindow {
    private var mViewAppointment: View? = null

    private var binding: PopOrderAddressBinding? = null
    private var context: Context? = null
    private var datas: MutableList<OderAddressInfoBean> = mutableListOf()

    constructor(context: Context) : super(context) {
        this.context = context
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.pop_order_address,
            null,
            false
        )
        mViewAppointment = binding!!.root
        initView()
        initPopWindow()
    }

    private fun initView() {
        binding?.recySelectOderTypes?.layoutManager = GridLayoutManager(context!!, 3, GridLayoutManager.VERTICAL, false)
        binding?.recySelectOderTypes?.adapter = adapter
        binding?.vPopOrderAdd?.onNoDoubleClick {
            dismiss()
        }
    }

    public fun updateData(data: MutableList<OderAddressInfoBean>) {
        datas.clear()
        datas.addAll(data)
        adapter?.clear()
        adapter?.add(datas)
        binding?.recySelectOderTypes?.visibility = View.VISIBLE
    }

    fun updatePopWindowHeight(height: Int) {
        this.height = height
    }

    private fun initPopWindow() {
        contentView = mViewAppointment
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置弹出窗体的高
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        // 设置弹出窗体可点击()
        this.isFocusable = true;
        this.isOutsideTouchable = true;
        // 实例化一个ColorDrawable颜色为半透明
        val dw = ColorDrawable(0x00FFFFFF);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            isClippingEnabled = false
        }
        //设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    val adapter = object : RecyclerViewAdapter<ItemProviderOrderAddressBinding, OderAddressInfoBean>(R.layout.item_provider_order_address) {
        override fun setVariable(mBinding: ItemProviderOrderAddressBinding, position: Int, item: OderAddressInfoBean) {
            Glide.with(context!!)
                .load(item.logo)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.imgProviderOrderAddress)
            mBinding.root.onNoDoubleClick {
                if (item != null) {
                    if (item.linkTips.isNullOrEmpty()) {
                        ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                            .withString("mTitle", "详情")
                            .withString("html", item.url)
                            .navigation()
                    } else {
                        ProviderTipDialog.Builder().setContent(item.linkTips)
                            .setContent(item.linkTips)
                            .setOnTipConfirmListener(object : ProviderTipDialog.OnTipConfirmListener {
                                override fun onConfirm() {
                                    ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                                        .withString("mTitle", "详情")
                                        .withString("html", item.url)
                                        .navigation()
                                }
                            })
                            .create(context!!).show()
                    }
                }
            }
        }

    }
}