package com.daqsoft.itinerary.adapter

import android.widget.CheckBox
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.bean.CityBean
import com.daqsoft.itinerary.databinding.ItineraryItemFilterLabelBinding
import com.daqsoft.itinerary.interfa.OnItemSelectedListener

/**
 * @Author：      邓益千
 * @Create by：   2020/5/11 15:58
 * @Description： 目的地城市
 */
class DestinationCityAdapter : RecyclerViewAdapter<ItineraryItemFilterLabelBinding,CityBean>(R.layout.itinerary_item_filter_label) {

    private var tempView: CheckBox? = null

    private var listener: OnItemSelectedListener<CityBean>? = null

    fun setOnItemSelectedListener(listener: OnItemSelectedListener<CityBean>?){
        this.listener = listener
    }

    override fun setVariable(mBinding: ItineraryItemFilterLabelBinding, position: Int, item: CityBean) {

        mBinding.labelName.apply {
            text = item.regionName
            isChecked = item.isSelected
            onNoDoubleClick {
                tempView?.let {
                    if (it.text.toString() == item.regionName){
                        return@onNoDoubleClick
                    } else {
                        it.isChecked = false
                    }
                }

                item.isSelected = !item.isSelected
                tempView = mBinding.labelName
                listener?.onSelected(position,item)
            }

        }

        if (position == 0){
            tempView = mBinding.labelName
        }

    }
}