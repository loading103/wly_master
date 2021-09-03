package com.daqsoft.travelCultureModule.country.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.LayoutFoodProductBinding
import com.daqsoft.mainmodule.databinding.LayoutMerchantsGoodsBinding
import com.daqsoft.travelCultureModule.country.adapter.MerchantsGoodsAdapter
import com.daqsoft.travelCultureModule.country.bean.FoodProductBean

/**
 * desc :商家商品
 * @author 江云仙
 * @date 2020/4/16 14:07
 */
class MerchantsGoodsView : FrameLayout {


    var mContext: Context? = null
    var binding: LayoutMerchantsGoodsBinding? = null
    var adapter: MerchantsGoodsAdapter? = null
    var onProducatItemClickListener: OnProducatItemClickListener? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.layout_merchants_goods,
            this,
            false
        )
        addView(binding!!.root)
        adapter = MerchantsGoodsAdapter(mContext!!)
        adapter!!.onProductItemClick = object : MerchantsGoodsAdapter.OnGoodsItemClickListener {
            override fun onNotify(productId: String, status: Boolean,position: Int) {
                onProducatItemClickListener?.onNotify(productId, status,position)
            }

        }
        binding?.recyclerFoodProducts?.layoutManager = LinearLayoutManager(mContext!!, LinearLayoutManager.VERTICAL, false)
        binding?.recyclerFoodProducts?.adapter = adapter
    }

    fun setData(datas: MutableList<FoodProductBean>) {
        adapter?.clear()
        if (datas != null) {
            if (datas.size > 3) {
                binding?.vFoodsProductShowMore?.visibility = View.VISIBLE
                adapter?.isShoreMore = false
                binding?.vFoodsProductShowMore?.onNoDoubleClick {
                    try {
                        if (adapter!!.isShoreMore) {
                            adapter!!.isShoreMore = false
                            val drawable2 = mContext!!.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_down)
                            drawable2.setBounds(0, 0, drawable2.minimumWidth, drawable2.minimumHeight)
                            binding?.txtLooksAllProduct?.setCompoundDrawables(null, null, drawable2, null)
                        } else {
                            adapter!!.isShoreMore = true
                            val drawable2 = mContext!!.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_up)
                            drawable2.setBounds(0, 0, drawable2.minimumWidth, drawable2.minimumHeight)
                            binding?.txtLooksAllProduct?.setCompoundDrawables(null, null, drawable2, null)
                        }
                        adapter?.notifyDataSetChanged()
                    } catch (e: Exception) {
                    }

                }
            } else {
                binding?.vFoodsProductShowMore?.visibility = View.GONE
                adapter?.isShoreMore = true
            }
        }
        adapter?.add(datas)
    }

    public fun updateNotifyStatus(status: Boolean, position: Int) {
        try {
            adapter?.getData()?.get(position)!!.remindStatus = status
            adapter?.notifyItemChanged(position,"updateRemainStatus")
        } catch (e: Exception) {

        }

    }

    interface OnProducatItemClickListener {
        fun onNotify(producatId: String, reaminStatus: Boolean,position: Int)
    }
}