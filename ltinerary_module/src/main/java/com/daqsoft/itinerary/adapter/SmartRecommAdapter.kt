package com.daqsoft.itinerary.adapter

import android.view.View
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.baselib.widgets.DividerItemDecoration
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.bean.CunsomScenicBean
import com.daqsoft.itinerary.bean.PlayItems
import com.daqsoft.itinerary.bean.RecommScenicItem
import com.daqsoft.itinerary.databinding.ItineraryItemCustomScenicBinding
import com.daqsoft.itinerary.databinding.ItineraryItemSmartRecommBinding
import com.daqsoft.itinerary.interfa.OnItemSelectedListener
import com.daqsoft.provider.Constants

/**
 * @Author：      邓益千
 * @Create by：   2020/5/20 17:08
 * @Description：
 */
class SmartRecommAdapter : RecyclerViewAdapter<ItineraryItemSmartRecommBinding, CunsomScenicBean> {

    private var listener: OnItemSelectedListener<PlayItems>? = null

    private var rootOnClickListener:RootOnClickListener<PlayItems,RecommScenicItem>? = null

    val scenicAdapter by lazy { ScenicAdapter(arrayListOf()) }

    constructor() : super(R.layout.itinerary_item_smart_recomm)

    fun setOnItemSelectedListener(listener: OnItemSelectedListener<PlayItems>?) {
        this.listener = listener
    }

    fun setRootOnClickListener(rootOnClickListener: RootOnClickListener<PlayItems,RecommScenicItem>){
        this.rootOnClickListener = rootOnClickListener
    }

    override fun setVariable(mBinding: ItineraryItemSmartRecommBinding, position: Int, item: CunsomScenicBean) {
        mBinding.cityName.text = "${item.regionName}, ${item.list.size}个景区, 预计游玩${item.playedDay}天"

        mBinding.recyclerView.apply {
            scenicAdapter.add(item.list)
            adapter = scenicAdapter
            addItemDecoration(DividerItemDecoration(0, ResourceUtils.getDimension(this, R.dimen.dp_12)))
        }
    }

    inner class ScenicAdapter : RecyclerViewAdapter<ItineraryItemCustomScenicBinding, RecommScenicItem> {

        constructor(dataList: MutableList<RecommScenicItem>) : super(R.layout.itinerary_item_custom_scenic) {
            add(dataList)
        }

        override fun setVariable(mBinding: ItineraryItemCustomScenicBinding, position: Int, item: RecommScenicItem) {

            val urls = item.images.split(",")
            if (!urls.isNullOrEmpty()) {
                mBinding.url = urls[0]
            }
            if (item.theme.isNullOrEmpty()) {
                mBinding.labelName.visibility = View.GONE
                mBinding.line.visibility = View.GONE
            } else {
                mBinding.labelName.visibility = View.VISIBLE
                mBinding.labelName.text = item.theme[0]
                mBinding.line.visibility = View.VISIBLE
            }

            mBinding.viewTitle.text = item.name
            if (item.suggestedHour == 0) {
                mBinding.playTime.visibility = View.INVISIBLE
            } else {
                mBinding.playTime.visibility = View.VISIBLE
                mBinding.playTime.text = "建议游玩${item.suggestedHour}小时"
            }

            mBinding.viewChoice.setOnClickListener {
                item.isChecked = !item.isChecked
                updateState(item, mBinding)
                //dataId 是共用属性，这里不需要
                listener?.onSelected(position,PlayItems(item.id,Constants.TYPE_SCENERY,0,item.isChecked,false))
            }

            mBinding.root.onNoDoubleClick {
                rootOnClickListener?.onClick(
                    position,
                    PlayItems(item.id, Constants.TYPE_SCENERY, 0, item.isChecked, true),
                    item,
                    mBinding
                )
            }

        }

        fun updateState(item: RecommScenicItem, mBinding: ItineraryItemCustomScenicBinding) {
            if (item.isChecked) {
                mBinding.viewChoice.setImageResource(R.drawable.itinerary_icon_tick)
            } else {
                mBinding.viewChoice.setImageResource(R.drawable.itinerary_icon_add)
            }
        }

    }


    interface RootOnClickListener<T,R>{
        fun onClick(position: Int,item: T,r:R,mBinding: ItineraryItemCustomScenicBinding)
    }
}