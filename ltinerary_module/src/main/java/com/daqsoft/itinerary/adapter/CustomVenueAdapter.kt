package com.daqsoft.itinerary.adapter

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.bean.PlayItems
import com.daqsoft.itinerary.databinding.ItineraryItemCustomScenicBinding
import com.daqsoft.itinerary.databinding.ItineraryItemFilterLabelBinding
import com.daqsoft.itinerary.interfa.OnItemSelectedListener
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.venuesmodule.repository.bean.VenuesListBean

/**
 * @Author：      邓益千
 * @Create by：   2020/5/12 10:49
 * @Description：场馆
 */
class CustomVenueAdapter :
    BaseQuickAdapter<VenuesListBean, BaseViewHolder>(R.layout.itinerary_item_custom_scenic) {

    private var isCancel = true

    private var listener: OnItemSelectedListener<PlayItems>? = null

    fun setOnItemSelectedListener(listener: OnItemSelectedListener<PlayItems>) {
        this.listener = listener
    }

    fun isCancel(isCancel: Boolean) {
        this.isCancel = isCancel
    }

    override fun convert(holder: BaseViewHolder, item: VenuesListBean) {
        holder.setText(R.id.view_title, item.name)
        holder.setText(R.id.label_name, item.type)

//        if (!item.suggestedTime.isNullOrEmpty()){
//            holder.setText(R.id.play_time,"游玩${item.suggestedTime}${item.suggestedHour}小时")
//        } else {
//            holder.setText(R.id.play_time,"游玩${item.suggestedHour}小时")
//        }
        holder.setText(R.id.play_time, "游玩${item.suggestedHour}小时")

        holder.getView<AppCompatImageView>(R.id.view_choice).apply {
            updateState(item, this)
            setOnClickListener {
                if (isCancel) {
                    item.isChecked = !item.isChecked
                    updateState(item, this)
                }
                listener?.onSelected(
                    holder.layoutPosition,
                    PlayItems(
                        item.id.toInt(),
                        item.resType,
                        item.dataId,
                        item.isChecked,
                        item.suggestedHour,
                        false
                    )
                )
            }
        }
        holder.itemView.setOnClickListener {
            listener?.onSelected(
                holder.layoutPosition,
                PlayItems(item.id.toInt(), item.resType, item.dataId, item.isChecked, true)
            )
        }

        val url = if (item.images.contains(",")) {
            item.images.split(",")[0]
        } else {
            item.images
        }
        Glide.with(holder.itemView.context).load(url).placeholder(R.drawable.grid_placeholde)
            .into(holder.getView(R.id.view_poster))

    }

    private fun updateState(item: VenuesListBean, mBinding: AppCompatImageView) {
        if (item.isChecked) {
            mBinding.setImageResource(R.drawable.itinerary_icon_tick)
        } else {
            mBinding.setImageResource(R.drawable.itinerary_icon_add)
        }
    }


}