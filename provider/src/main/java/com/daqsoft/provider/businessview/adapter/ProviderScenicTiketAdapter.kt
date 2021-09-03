package com.daqsoft.provider.businessview.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.ScenicTiketBean
import com.daqsoft.provider.databinding.ItemProviderScenicTiketBinding

/**
 * @Description
 * @ClassName   ProviderScenicTiketAdapter
 * @Author      luoyi
 * @Time        2020/11/5 15:23
 */
class ProviderScenicTiketAdapter : RecyclerViewAdapter<ItemProviderScenicTiketBinding, ScenicTiketBean> {
    var mContext: Context? = null
    var shopName: String = ""
    var shopUrl: String = ""
    var onScenicTiketListener: OnScenicTiketProductListener? = null

    constructor(context: Context, shopName: String?, shopUrl: String?) : super(R.layout.item_provider_scenic_tiket) {
        mContext = context
        this.shopName = shopName ?: ""
        this.shopUrl = shopUrl ?: ""
    }

    override fun setVariable(mBinding: ItemProviderScenicTiketBinding, position: Int, item: ScenicTiketBean) {
        item?.let {
            if (it.isActivity) {
                mBinding.tvItemTiketScenicAct.visibility = View.VISIBLE
            } else {
                mBinding.tvItemTiketScenicAct.visibility = View.GONE
            }
            mBinding.tvItemTiketScenicName.text = "${item.sptTitle}"
            val minPrice = StringUtil.companreBigDecimal(item.minSellPrice)
            if (!minPrice.isNullOrEmpty()) {
                mBinding.vItemTicketScenicPrice.visibility = View.VISIBLE
                mBinding.tvItemTiketPrice.text = minPrice
            } else {
                mBinding.vItemTicketScenicPrice.visibility = View.GONE
            }

            //景区门票商品适配器
            var scenicTiketProductAdapter: ProviderScenicProductAdapter = ProviderScenicProductAdapter(mContext!!, shopName, shopUrl)
            mBinding.recyTiketProducts.layoutManager = LinearLayoutManager(mContext!!, LinearLayoutManager.VERTICAL, false)
            mBinding.recyTiketProducts.adapter = scenicTiketProductAdapter
            scenicTiketProductAdapter?.onclickProductListener = object : ProviderScenicProductAdapter.OnTiketProductListener {
                override fun onTiketProductClick(productId: String?) {
                    if (onScenicTiketListener != null) {
                        onScenicTiketListener!!.onTiketProductClick(productId)
                    }
                }

            }
            scenicTiketProductAdapter.clear()
            if (!it.productList.isNullOrEmpty()) {
                scenicTiketProductAdapter.add(it.productList)
            }
            mBinding.vItemScenicTiketTitle.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    if (!it.productList.isNullOrEmpty()) {
                        if (it.isShowProduct) {
                            it.isShowProduct = false
                            mBinding.vItemTiketProducts.visibility = View.GONE
                            mBinding.imgItemTicketArrow.background = mContext!!.resources.getDrawable(R.mipmap.provider_arrow_down)
                        } else {
                            it.isShowProduct = true
                            mBinding.vItemTiketProducts.visibility = View.VISIBLE
                            mBinding.imgItemTicketArrow.background = mContext!!.resources.getDrawable(R.mipmap.provider_arrow_up)
                        }
                    }
                }

            })
        }
    }

    interface OnScenicTiketProductListener {
        fun onTiketProductClick(productId: String?)
    }

}