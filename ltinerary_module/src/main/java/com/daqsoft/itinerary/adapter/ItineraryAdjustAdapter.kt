package com.daqsoft.itinerary.adapter

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.baselib.adapter.BindingViewHolder
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.bean.ItineraryDetailBean.AgendaBean
import com.daqsoft.itinerary.bean.ItineraryDetailBean.AgendaBean.PlansBean
import com.daqsoft.itinerary.databinding.ItineraryItemAdjustDayBinding
import com.daqsoft.itinerary.databinding.ItineraryItemAdjustPlanBinding
import com.daqsoft.itinerary.interfa.ItemMoveListener
import com.daqsoft.itinerary.util.ItemTouchHelperCallback
import com.daqsoft.baselib.widgets.DividerItemDecoration
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import java.lang.Exception
import java.util.*

/**
 * @Author：      邓益千
 * @Create by：   2020/5/19 14:15
 * @Description：
 */
class ItineraryAdjustAdapter : RecyclerViewAdapter<ItineraryItemAdjustDayBinding, PlansBean>(R.layout.itinerary_item_adjust_day), ItemMoveListener {

    var onItemLongClickListener: OnItemLongClickListener? = null

    override fun setVariable(mBinding: ItineraryItemAdjustDayBinding, position: Int, item: PlansBean) {
        mBinding.dayView.visibility = View.GONE


        mBinding.nameView.text = item.name
        mBinding.timeView.text = ""
        var leftDrawable: Drawable? = null
        when (item.resourceType) {
            "CONTENT_TYPE_SCENERY" -> {    //景区
                mBinding.timeView.text = "游玩时间：${item.adviceTime}小时"
                leftDrawable = ResourceUtils.getDrawable(mBinding.root, R.drawable.itinerary_vector_icon_scenic)
            }
            "CONTENT_TYPE_VENUE" -> {      //场馆
                mBinding.timeView.text = "游玩时间：${item.adviceTime}小时"
                leftDrawable = ResourceUtils.getDrawable(mBinding.root, R.drawable.itinerary_vector_icon_venue)
            }
            "CONTENT_TYPE_RESTAURANT" -> { //餐馆
                leftDrawable = ResourceUtils.getDrawable(mBinding.root, R.drawable.itinerary_vector_icon_eat)
            }
            "CONTENT_TYPE_HOTEL" -> {      //酒店
                leftDrawable = ResourceUtils.getDrawable(mBinding.root, R.drawable.itinerary_vector_icon_hoel)
            }
        }
        leftDrawable?.setBounds(
            0,
            0,
            ResourceUtils.getDimension(mBinding.root, R.dimen.dp_16),
            ResourceUtils.getDimension(mBinding.root, R.dimen.dp_16)
        )
        mBinding.nameView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
    }

    override fun setVariable(mBinding: ItineraryItemAdjustDayBinding, position: Int, item: PlansBean, holder: RecyclerView.ViewHolder) {
        if (item.type == 2) {
            mBinding.dayView.text = "D${item.day}\t\t${item.time}"
            mBinding.dayView.visibility = View.VISIBLE
            mBinding.vTitle.visibility = View.GONE
            mBinding.vTitle.onLongClick {
            }
            mBinding.root.background = null
            if (!isHavedItineray(item.dayId)) {
                mBinding.tvEmptyItinerary.visibility = View.VISIBLE
            } else {
                mBinding.tvEmptyItinerary.visibility = View.GONE
            }
        } else {
            mBinding.dayView.visibility = View.GONE
            mBinding.vTitle.visibility = View.VISIBLE
            mBinding.vTitle.onLongClick {
                onItemLongClickListener?.onClickListener(holder)
            }
            mBinding.root.background = ContextCompat.getDrawable(mBinding.root.context, R.drawable.itinerary_shape_stroke_dotted_grey)
        }
    }

    private fun isHavedItineray(dayId: Int): Boolean {
        var isHaved = false
        for (item in getData()) {
            if (item.dayId == dayId && item.type != 2) {
                isHaved = true
                break
            }
        }
        return isHaved
    }

    inner class AdjustPlanAdapter(private val datas: MutableList<PlansBean>) : RecyclerViewAdapter<ItineraryItemAdjustPlanBinding, PlansBean>(
        R.layout.itinerary_item_adjust_plan
    ), ItemMoveListener {

        init {
            add(datas)
        }

        override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
            try {
                if (getData()[fromPosition].type != 2) {
                    Collections.swap(datas, fromPosition, toPosition)
                    notifyItemMoved(fromPosition, toPosition)
                } else {
                    return false
                }

            } catch (ex: Exception) {
                return true
            }
            return true
        }

        override fun setVariable(mBinding: ItineraryItemAdjustPlanBinding, position: Int, item: PlansBean) {
            mBinding.nameView.text = item.name
            mBinding.timeView.text = ""
            var leftDrawable: Drawable? = null
            when (item.resourceType) {
                "CONTENT_TYPE_SCENERY" -> {    //景区
                    mBinding.timeView.text = "游玩时间：${item.adviceTime}小时"
                    leftDrawable = ResourceUtils.getDrawable(mBinding.root, R.drawable.itinerary_vector_icon_scenic)
                }
                "CONTENT_TYPE_VENUE" -> {      //场馆
                    mBinding.timeView.text = "游玩时间：${item.adviceTime}小时"
                    leftDrawable = ResourceUtils.getDrawable(mBinding.root, R.drawable.itinerary_vector_icon_venue)
                }
                "CONTENT_TYPE_RESTAURANT" -> { //餐馆
                    leftDrawable = ResourceUtils.getDrawable(mBinding.root, R.drawable.itinerary_vector_icon_eat)
                }
                "CONTENT_TYPE_HOTEL" -> {      //酒店
                    leftDrawable = ResourceUtils.getDrawable(mBinding.root, R.drawable.itinerary_vector_icon_hoel)
                }
            }
            leftDrawable?.setBounds(
                0,
                0,
                ResourceUtils.getDimension(mBinding.root, R.dimen.dp_16),
                ResourceUtils.getDimension(mBinding.root, R.dimen.dp_16)
            )
            mBinding.nameView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
        }
    }


    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        try {
            if (toPosition != 0) {
                Collections.swap(getData(), fromPosition, toPosition)
                if (toPosition > fromPosition) {
                    // 向下拖拽
                    var toItem = getData()[toPosition]
                    var tempDayId = toItem.dayId
                    if (toItem.type != 2) {
                        getData()[toPosition].dayId = getData()[fromPosition].dayId
                    }
                    getData()[fromPosition].dayId = tempDayId
                } else if (toPosition < fromPosition) {
                    // 向上拖拽
                    var toItem = getData()[fromPosition]
                    var tempDayId = toItem.dayId
                    if (toItem.type != 2) {
                        getData()[fromPosition].dayId = getData()[toPosition].dayId
                    } else {
                        if (fromPosition > 0) {
                            var prePos = toPosition - 1
                            tempDayId = getData()[prePos].dayId
                        }
                    }
                    getData()[toPosition].dayId = tempDayId
                }
            }
            notifyItemChanged(fromPosition)
            notifyItemChanged(toPosition)
            notifyItemMoved(fromPosition, toPosition)
        } catch (ex: Exception) {
            return true
        }
        return true
    }

    interface OnItemLongClickListener {
        fun onClickListener(holder: RecyclerView.ViewHolder)
    }
}