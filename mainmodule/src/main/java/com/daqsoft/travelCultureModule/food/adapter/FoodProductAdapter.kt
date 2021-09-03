package com.daqsoft.travelCultureModule.food.adapter

import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemFoodProductBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.FoodProductBean

/**
 * @Description 美食产品
 * @ClassName   FoodProductAdapter
 * @Author      luoyi
 * @Time        2020/4/11 15:05
 */
class FoodProductAdapter : RecyclerViewAdapter<ItemFoodProductBinding, FoodProductBean> {

    private var mContext: Context? = null
    var isShoreMore: Boolean = false
    var shopUrl: String = ""
    var shopName: String = ""

    constructor(context: Context) : super(R.layout.item_food_product) {
        this.mContext = context
    }

    override fun setVariable(mBinding: ItemFoodProductBinding, position: Int, item: FoodProductBean) {
        Glide.with(mContext!!).load(item.image)
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(mBinding.imgItemFoodProduct)
        mBinding.txtFoodProductName.text = "${item.productName}"
        mBinding.txtFoodProductPrice.text = "${StringUtil.companreBigDecimal(item.salePrice)}"
        // 处理标签
        var tags: MutableList<String> = mutableListOf()
        if (item.allowRefund) {
            tags.add("可退")
        } else {
            tags.add("不可退")
        }
        mBinding.lbvFoodProduct.setLabels(tags)

        mBinding.root.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                var uuid = SPUtils.getInstance().getString(SPKey.UUID)
                var token = SPUtils.getInstance().getString(SPKey.USER_CENTER_TOKEN)
                var encry = SPUtils.getInstance().getString(SPKey.ENCRYPTION)
                // 拼接跳转酒店下单页面地址
                var url = "$shopUrl/goods/detail?&id=${item.productId}" +
                        "&unid=${uuid}&token=${token}&encryption=${encry}"
                ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", shopName)
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

    override fun getItemCount(): Int {
        return if (isShoreMore) {
            super.getItemCount()
        } else {
            if (getData().size > 3) {
                3
            } else {
                getData().size
            }
        }

    }
}