package com.daqsoft.venuesmodule.adapter

import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.OderAddressInfoBean
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.view.dialog.ProviderTipDialog
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemPerformanceReservationBinding

/**
 * @package name：com.daqsoft.venuesmodule.adapter
 * @date 2020/9/15 16:52
 * @author zp
 * @describe 演出预定 adapter
 */
class PerformanceReservationAdapter: RecyclerViewAdapter<ItemPerformanceReservationBinding, OderAddressInfoBean>(R.layout.item_performance_reservation) {


    override fun getItemCount(): Int {
        if (getData().size > 3){
            return 3
        }
        return super.getItemCount()
    }

    override fun setVariable(
        mBinding: ItemPerformanceReservationBinding,
        position: Int,
        item: OderAddressInfoBean
    ) {
        // 宣传图
        Glide
            .with(mBinding.root.context)
            .load(if (item.logo.isNullOrEmpty()) "" else item.logo)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5.dp)))
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(mBinding.image)

        //名称
        mBinding.title.text = item.platfromName

        //预订
        mBinding.booking.onNoDoubleClick {
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
                    .create(mBinding.root.context).show()
            }

        }
    }
}