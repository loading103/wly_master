package com.daqsoft.itinerary.adapter

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.bean.CityBean
import com.daqsoft.itinerary.databinding.ItineraryItemCustomCityBinding
import com.daqsoft.itinerary.databinding.ItineraryItemCustomScenicBinding
import com.daqsoft.itinerary.interfa.OnItemSelectedListener
import com.daqsoft.provider.bean.ScenicBean

/**
 * @Author：      邓益千
 * @Create by：   2020/5/19 20:58
 * @Description：
 */
class CustomCityAdapter : RecyclerViewAdapter<ItineraryItemCustomCityBinding, CityBean> {

    /**item高度*/
    private var height = 0

    private var tag = 0

    private var listener: OnItemSelectedListener<CityBean>? = null

    constructor(): super(R.layout.itinerary_item_custom_city)

    constructor(datas: MutableList<CityBean>): super(R.layout.itinerary_item_custom_city){
        add(datas)
    }

    /**显示数据类型*/
    fun setShowDataType(showTag: Int){
        tag = showTag
    }

    fun setItemHeight(itemHeight: Int){
        height = itemHeight
    }

    fun setOnItemSelectedListener(listener: OnItemSelectedListener<CityBean>?){
        this.listener = listener
    }

    override fun setVariable(mBinding: ItineraryItemCustomCityBinding, position: Int, item: CityBean) {
        mBinding.nameView.text = item.regionName
        val params = mBinding.viewPoster.layoutParams as ConstraintLayout.LayoutParams
        params.height = height
        mBinding.viewPoster.layoutParams = params
        val urls = item.images.split(",")
        if (urls.isNotEmpty()){
            mBinding.url = urls[0]
        }
        if (tag == 0){
            mBinding.root.setOnClickListener {
                item.isSelected = !item.isSelected
                updateState(item,mBinding)
                listener?.onSelected(position,item)
            }
        } else {
            mBinding.dayView.text = "${item.suggestedDay}"
            mBinding.recommDay.text = "推荐游玩${item.suggestedDay}天"
            mBinding.choiceView.visibility = View.GONE
            mBinding.addLayout.visibility = View.VISIBLE
            mBinding.minusView.setOnClickListener {
                if (item.suggestedDay > 1){
                    item.suggestedDay -= 1
                    mBinding.dayView.text = "${item.suggestedDay}"
                    listener?.onSelected(position,item)
                }
            }
            mBinding.addView.setOnClickListener {
                if (item.suggestedDay < 15) {
                    item.suggestedDay += 1
                    mBinding.dayView.text = "${item.suggestedDay}"
                    listener?.onSelected(position,item)
                }
            }
        }
    }

    private fun updateState(item: CityBean, mBinding: ItineraryItemCustomCityBinding){
        if (item.isSelected){
            mBinding.choiceView.setImageResource(R.drawable.itinerary_icon_tick_selected)
        } else {
            mBinding.choiceView.setImageResource(R.drawable.itinerary_icon_tick_normal)
        }
    }

}