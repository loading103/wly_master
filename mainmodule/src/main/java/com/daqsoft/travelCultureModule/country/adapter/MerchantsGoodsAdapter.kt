package com.daqsoft.travelCultureModule.country.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemFoodProductBinding
import com.daqsoft.mainmodule.databinding.ItemMerchantGoodsBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.travelCultureModule.country.ELECTRIC_URL
import com.daqsoft.travelCultureModule.country.bean.FoodProductBean
import com.daqsoft.travelCultureModule.country.view.CountDownTimeTextView
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * desc :商家商品适配器
 * @author 江云仙
 * @date 2020/4/16 14:08
 */
class MerchantsGoodsAdapter : RecyclerViewAdapter<ItemMerchantGoodsBinding, FoodProductBean> {

    private var mContext: Context? = null
    var isShoreMore: Boolean = false
    var shopUrl: String = ""
    var shopName: String = ""
    var onProductItemClick: OnGoodsItemClickListener? = null

    constructor(context: Context) : super(R.layout.item_merchant_goods) {
        this.mContext = context
    }

    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemMerchantGoodsBinding, position: Int, item: FoodProductBean) {
        Glide.with(mContext!!).load(item.image)
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(mBinding.imgItemFoodProduct)
        mBinding.txtFoodProductName.text = "${item.productName}"
        mBinding.txtFoodProductPrice.text = "${StringUtil.companreBigDecimal(item.salePrice)}"
        // 处理标签
        var tags: MutableList<String> = mutableListOf()
        if (item.allowRefund) {
            tags.add("可退")
        }
        if (!item.needPrecontract) {
            tags.add("免预约")
        }
        mBinding.lbvFoodProduct.setLabels(tags)
        mBinding.txtFoodProductConfirm.isClickable = false
        mBinding.root.isClickable = true
        delProducatStatus(item, mBinding, position)

        RxView.clicks(mBinding.root)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                //点击跳转网页
                if (AppUtils.isLogin()) {
                    var uuid = SPUtils.getInstance().getString(SPKey.UUID)
                    var token = SPUtils.getInstance().getString(SPKey.USER_CENTER_TOKEN)
                    var encry = SPUtils.getInstance().getString(SPKey.ENCRYPTION)
                    var shopUrl = SPUtils.getInstance().getString(SPKey.SHOP_URL)
                    // 拼接跳转酒店下单页面地址
                    var url = "$shopUrl/goods/detail?&id=${item.productId}" +
                            "&unid=${uuid}&token=${token}&encryption=${encry}"
                    ARouter.getInstance()
                        .build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString("mTitle", item.productName)
                        .withString("html", url)
                        .navigation()
                } else {
                    //跳转到登录界面
                    ToastUtils.showMessage("非常抱歉，你还未登录~")
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }

            }
    }

    override fun payloadUpdateUi(mBinding: ItemMerchantGoodsBinding, position: Int, payloads: MutableList<Any>) {
        if (payloads[0] == "updateRemainStatus") {
            mBinding.txtFoodProductConfirm.text = if (!getData()[position].remindStatus) {
                "提醒"
            } else {
                "取消提醒"
            }
        }
    }

    private fun delProducatStatus(item: FoodProductBean, mBinding: ItemMerchantGoodsBinding, position: Int) {
        when (item.saleType) {
            1 -> {
                // 普通商品
                mBinding.vCoutdownFoodProduct.visibility = View.GONE
                mBinding.txtFoodProductConfirm.text = "预订"
                mBinding.txtFoodProductConfirm.setTextColor(BaseApplication.context.resources.getColor(R.color.white))
                mBinding.txtFoodProductConfirm.background = BaseApplication.context.resources.getDrawable(R.drawable.shape_ff9e05_13)
            }
            2 -> {
                mBinding.vCoutdownFoodProduct.visibility = View.VISIBLE
                // 活动商品
                if (item.upperCountTime > 0) {
                    // 上架倒计时
                    mBinding.txtFoodProductConfirm.isClickable = true
                    mBinding.txtFoodProductConfirm.setTextColor(BaseApplication.context.resources.getColor(R.color.ff9e05))
                    mBinding.txtFoodProductConfirm.background = BaseApplication.context.resources.getDrawable(R.drawable.country_ff9e05_stroke_1_round_360)
                    mBinding.txtFoodProductConfirm.onNoDoubleClick {
                        onProductItemClick?.onNotify(item.productId.toString(), item.remindStatus, position)
                    }
                    mBinding.txtFoodProductConfirm.text = if (!item.remindStatus) {
                        "提醒"
                    } else {
                        "取消提醒"
                    }
                    mBinding.dvTimeFoodProduct.setTime("距开始还剩", item.upperCountTime * 1000) {
                        // 倒计时
                        mBinding.vCoutdownFoodProduct.visibility = View.GONE
                        mBinding.txtFoodProductConfirm.text = "预订"
                        mBinding.txtFoodProductConfirm.setTextColor(BaseApplication.context.resources.getColor(R.color.white))
                        mBinding.txtFoodProductConfirm.background = BaseApplication.context.resources.getDrawable(R.drawable.shape_ff9e05_13)
                    }
                    mBinding.dvTimeFoodProduct.start()
                } else {
                    // 下架倒计时
                    mBinding.txtFoodProductConfirm.text = "预订"
                    mBinding.txtFoodProductConfirm.setTextColor(BaseApplication.context.resources.getColor(R.color.white))
                    mBinding.txtFoodProductConfirm.background = BaseApplication.context.resources.getDrawable(R.drawable.shape_ff9e05_13)
                    mBinding.dvTimeFoodProduct.setTime("距结束还剩", item.lowerCountTime * 1000) {
                        // 倒计时
                        mBinding.vCoutdownFoodProduct.visibility = View.GONE
                        mBinding.txtFoodProductConfirm.text = "已下架"
                        mBinding.txtFoodProductConfirm.setTextColor(BaseApplication.context.resources.getColor(R.color.white))
                        mBinding.txtFoodProductConfirm.background = BaseApplication.context.resources.getDrawable(R.drawable.shape_grey_13)
                        mBinding.root.isClickable = false
                    }
                    mBinding.dvTimeFoodProduct.start()
                }
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

    public interface OnGoodsItemClickListener {
        fun onNotify(productId: String, status: Boolean, position: Int)
    }

}