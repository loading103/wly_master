package com.daqsoft.itinerary.util

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.baselib.adapter.BindingViewHolder
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.databinding.ItineraryItemAdjustDayBinding
import com.daqsoft.itinerary.interfa.ItemMoveListener
import kotlinx.android.synthetic.main.itinerary_item_adjust_day.view.*

/**
 * @Author：      邓益千
 * @Create by：   2020/5/19 15:37
 * @Description：
 */
class ItemTouchHelperCallback(private val moveListener: ItemMoveListener) : ItemCustomTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return moveListener.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
    }

    override fun isCanDrag(holder: RecyclerView.ViewHolder?): Boolean {
        if(holder!=null){
            if (holder is BindingViewHolder<*>){
               if((holder.mBinding as ItineraryItemAdjustDayBinding).dayView.visibility==View.VISIBLE){
                   return false
               }
            }
        }
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView!!.apply {
                if (day_view.visibility != View.VISIBLE) {
                    setBackgroundResource(R.drawable.itinerary_shape_stroke_dotted_green)
                    time_view.setTextColor(ResourceUtils.getColor(this, R.color.itinerary_theme))
                    name_view.apply {
                        setTextColor(ResourceUtils.getColor(this, R.color.itinerary_theme))
//                    compoundDrawables[0].setTint(ResourceUtils.getColor(this,R.color.itinerary_theme))
                    }
                }
            }
        }

    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.apply {
            if (day_view.visibility != View.VISIBLE) {
                setBackgroundResource(R.drawable.itinerary_shape_stroke_dotted_grey)
                time_view.setTextColor(ResourceUtils.getColor(this, R.color.color_999999))
                name_view.setTextColor(ResourceUtils.getColor(this, R.color.color_333333))
            }
//            name_view.compoundDrawables[0].setTint(ResourceUtils.getColor(this,R.color.color_D4D4D4))
        }
    }

}