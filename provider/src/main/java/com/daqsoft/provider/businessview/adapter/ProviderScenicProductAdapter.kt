package com.daqsoft.provider.businessview.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.ScenicTiketProductBean
import com.daqsoft.provider.databinding.ItemProviderScenicTiketProductBinding
import com.daqsoft.provider.databinding.ItemProviderScenicTiketTagBinding
import org.jetbrains.anko.textColor

/**
 * @Description
 * @ClassName   ProviderScenicProductAdapter
 * @Author      luoyi
 * @Time        2020/11/5 15:24
 */
class ProviderScenicProductAdapter : RecyclerViewAdapter<ItemProviderScenicTiketProductBinding, ScenicTiketProductBean> {
    var mContext: Context? = null
    var shopName: String = ""
    var shopUrl: String = ""
    var onclickProductListener: OnTiketProductListener? = null

    constructor(context: Context, shopName: String, shopUrl: String) : super(R.layout.item_provider_scenic_tiket_product) {
        mContext = context
        this.shopName = shopName
        this.shopUrl = shopUrl
    }

    override fun setVariable(mBinding: ItemProviderScenicTiketProductBinding, position: Int, item: ScenicTiketProductBean) {
        item?.let {
            if (it.isActivity) {
                mBinding.tvItemTiketProductAct.visibility = View.VISIBLE
            } else {
                mBinding.tvItemTiketProductAct.visibility = View.GONE
            }
            mBinding.tvItemTiketProductName.text = "${item.productName}"
            // 处理价格
            val sellerPrice = StringUtil.companreBigDecimal(item.sellPrice)
            if (!sellerPrice.isNullOrEmpty()) {
                mBinding.vScenicProductPrice.visibility = View.VISIBLE
                mBinding.tvScenicProductPrice.text = sellerPrice
            } else {
                mBinding.vScenicProductPrice.visibility = View.GONE
            }
            // 处理倒计时

            // 处理标签
            var tags: MutableList<String> = mutableListOf()
            if (it.needFaceRecognition) {
                tags.add("人脸识别入园")
            }
            if (!it.purchaseLimit.isNullOrEmpty()) {
                tags.add(it.purchaseLimit)
            }
            if (!it.allowRefund.isNullOrEmpty()) {
                tags.add(it.allowRefund)
            }
            if (!it.needTicket.isNullOrEmpty()) {
                tags.add(it.needTicket)
            }
            if (!tags.isNullOrEmpty()) {
                val tagLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
                val tagAdapter =
                    object : RecyclerViewAdapter<ItemProviderScenicTiketTagBinding, String>(R.layout.item_provider_scenic_tiket_tag) {
                        @SuppressLint("CheckResult")
                        override fun setVariable(mBinding: ItemProviderScenicTiketTagBinding, position: Int, item: String) {

                            if (item == "人脸识别入园") {
                                mBinding.tvVolunteer.background =
                                    mContext!!.getDrawable(R.drawable.shape_scenic_tiket_16b2fa_2_0_5)
                                mBinding.tvVolunteer.textColor = mContext!!.resources.getColor(R.color.c_16b2fa)
                            } else {
                                // tag
                                mBinding.name = item
                                mBinding.tvVolunteer.background =
                                    mContext!!.getDrawable(R.drawable.shape_scenic_tiket_999999_2_0_5)
                                mBinding.tvVolunteer.textColor = mContext!!.resources.getColor(R.color.color_666)
                            }
                        }
                    }
                tagAdapter.add(tags)
                mBinding.lbvScenicProductTags.layoutManager = tagLayoutManager
                mBinding.lbvScenicProductTags.adapter = tagAdapter
                mBinding.lbvScenicProductTags.visibility = View.VISIBLE
                mBinding.lbvScenicProductTags.visibility = View.VISIBLE
            } else {
                mBinding.lbvScenicProductTags.visibility = View.GONE
            }
            // 处理秒杀
            if (it.isActivity && it.lowerTime != null && it.lowerTime > 0) {
                mBinding.dvTimeScenicProduct.start(it.lowerTime)
                mBinding.dvTimeScenicProduct.setOnCountdownEndListener {
                    // 倒计时
                    mBinding.dvTimeScenicProduct.visibility = View.GONE
                    mBinding.tvScenicProductConfirm.visibility = View.VISIBLE
                }
                mBinding.dvTimeScenicProduct.visibility = View.VISIBLE
            } else {
                mBinding.dvTimeScenicProduct.visibility = View.GONE
            }
            mBinding.tvItemScenicTiketTip.onNoDoubleClick {
                if (onclickProductListener != null) {
                    onclickProductListener?.onTiketProductClick(item.goodsSn)
                }
            }
            mBinding.tvScenicProductConfirm.onNoDoubleClick {
                // 去预订
                if (!shopUrl.isNullOrEmpty()) {
                    if (AppUtils.isLogin()) {
                        var uuid = SPUtils.getInstance().getString(SPKey.UUID)
                        var token = SPUtils.getInstance().getString(SPKey.USER_CENTER_TOKEN)
                        var encry = SPUtils.getInstance().getString(SPKey.ENCRYPTION)
                        var url = "$shopUrl/tickets/confirm?goodsSn=${item.goodsSn}&unid=${uuid}&token=${token}&encryption=${encry}"
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

            //todo 处理下架倒计时 暂时未做

            //todo 提醒功能 暂时未做 提醒功能需要考虑登录状态切换 ，最好能够重新请求接口
        }
    }

    override fun payloadUpdateUi(mBinding: ItemProviderScenicTiketProductBinding, position: Int, payloads: MutableList<Any>) {
//            if(payloads[0]=="pauseCountTime"){
//           用于更新倒计时
//            }else if(payloads=="startCountTime"){
//
//            }
    }

    interface OnTiketProductListener {
        fun onTiketProductClick(productId: String?)
    }


}