package com.daqsoft.travelCultureModule.food.view

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
import com.daqsoft.provider.bean.FoodProductBean
import com.daqsoft.travelCultureModule.food.adapter.FoodProductAdapter

/**
 * @Description 美食商品
 * @ClassName   FoodProductView
 * @Author      luoyi
 * @Time        2020/4/11 14:00
 */
class FoodProductView : FrameLayout {


    var mContext: Context? = null
    var binding: LayoutFoodProductBinding? = null
    var adapter: FoodProductAdapter? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.layout_food_product,
            this,
            false
        )
        addView(binding!!.root)
        adapter = FoodProductAdapter(mContext!!)
        binding?.recyclerFoodProducts?.layoutManager = LinearLayoutManager(mContext!!, LinearLayoutManager.VERTICAL, false)
        binding?.recyclerFoodProducts?.adapter = adapter
    }

    fun setData(datas: MutableList<FoodProductBean>) {
        adapter?.clear()
        if(datas!=null){
            if(datas.size>3){
                binding?.vFoodsProductShowMore?.visibility= View.VISIBLE
                adapter?.isShoreMore=false
                binding?.vFoodsProductShowMore?.onNoDoubleClick {
                    try {
                        if(adapter!!.isShoreMore){
                            adapter!!.isShoreMore=false
                            val drawable2 = mContext!!.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_down)
                            drawable2.setBounds(0, 0, drawable2.minimumWidth, drawable2.minimumHeight)
                            binding?.txtLooksAllProduct?.setCompoundDrawables(null,null,drawable2,null)
                        }else{
                            adapter!!.isShoreMore=true
                            val drawable2 = mContext!!.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_up)
                            drawable2.setBounds(0, 0, drawable2.minimumWidth, drawable2.minimumHeight)
                            binding?.txtLooksAllProduct?.setCompoundDrawables(null,null,drawable2,null)
                        }
                        adapter?.notifyDataSetChanged()
                    }catch (e:Exception){}

                }
            }else{
                binding?.vFoodsProductShowMore?.visibility= View.GONE
                adapter?.isShoreMore=true
            }
        }
        adapter?.add(datas)
    }

    /**
     * 设置店铺信息
     */
    fun setShopInfo(shopName:String,shopUrl:String){
        adapter?.shopUrl = shopUrl
        adapter?.shopName = shopName
    }
}