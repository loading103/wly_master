package com.daqsoft.legacyModule.smriti.adapter

import android.view.LayoutInflater
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.LegacyCommodityRecycleviewItemBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.LegacyProductBean

/**
 * @package name：com.daqsoft.legacyModule.smriti.adapter
 * @date 11/11/2020 上午 10:13
 * @author zp
 * @describe
 */
class CommodityAdapter : RecyclerViewAdapter<LegacyCommodityRecycleviewItemBinding, LegacyProductBean>(R.layout.legacy_commodity_recycleview_item) {


    override fun setVariable(mBinding: LegacyCommodityRecycleviewItemBinding, position: Int, item: LegacyProductBean) {

        mBinding.tvTitle.text = item.productName

        Glide
            .with(mBinding.root.context)
            .load(item.image)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5)))
            .placeholder(R.drawable.placeholder_img_fail_240_180)
            .into(mBinding.ivProduct)

        mBinding.tvPrice.text = item.salePrice

        val tags = arrayListOf<String>()

        if (item.allowRefund){
            tags.add("可退")
        }else{
            tags.add("不可退")
        }

        if (!item.needPrecontract){
            tags.add("免预约")
        }

        mBinding.tag.removeAllViews()
        tags.forEach {
            val view = LayoutInflater.from(mBinding.root.context).inflate(
                R.layout.legacy_commodity_flowlayout_item,
                mBinding.tag,
                false
            )
            view.findViewById<TextView>(R.id.tag).text = it
            mBinding.tag.addView(view)
        }

        mBinding.root.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                val shopUrl = SPUtils.getInstance().getString(SPKey.SHOP_URL, "")
                var uuid = SPUtils.getInstance().getString(SPKey.UUID)
                var token = SPUtils.getInstance().getString(SPKey.USER_CENTER_TOKEN)
                var encry = SPUtils.getInstance().getString(SPKey.ENCRYPTION)
                // 拼接跳转下单页面地址
                var url = "$shopUrl/goods/detail?&id=${item.productId}" +
                        "&unid=${uuid}&token=${token}&encryption=${encry}"
                ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", item.productName)
                    .withString("html", url)
                    .navigation()
            } else {
                ToastUtils.showMessage("非常抱歉，你还未登录~")
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
    }

}