package com.daqsoft.travelCultureModule.playground.adapter

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemPlayDetailTopStickBinding
import com.daqsoft.provider.bean.QuickTopNavigationItem

/**
 * @Description 顶部悬浮定位导航
 * @ClassName   VenueDetailTopStickAdapter
 * @Author      luoyi
 * @Time        2020/3/28 16:49
 */
class PlayGroupDetailTopStickAdapter : RecyclerViewAdapter<ItemPlayDetailTopStickBinding, QuickTopNavigationItem>(R.layout.item_play_detail_top_stick) {

    var selectPos: Int = 0
    var onItemClickListener: OnItemClickListener? = null
    override fun setVariable(mBinding: ItemPlayDetailTopStickBinding, position: Int, item: QuickTopNavigationItem) {

        if(getData().size <= 4){
            val screenWidth: Int = getScreenWidth(mBinding.root.context)
            val params: ViewGroup.LayoutParams = mBinding.root.layoutParams
            params.width = (screenWidth) / getData().size
            mBinding.root.layoutParams = params
        }


        if (selectPos == position) {
            mBinding.tvVenueTab.isSelected = true
            mBinding.isShow = View.VISIBLE
        } else {
            mBinding.isShow = View.GONE
            mBinding.tvVenueTab.isSelected = false
        }

        mBinding.name=item.text
        mBinding.root.onNoDoubleClick {
            if(selectPos!=position){
                selectPos = position
                notifyDataSetChanged()
            }
            if (onItemClickListener != null) {
                onItemClickListener?.onItemClick(item.id)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(id: Int)
    }

    fun updateSelectPos(id: Int, recyTopScrollStick: RecyclerView) {
        for (i in getData().indices) {
            val item = getData()[i]
            if (item.id == id) {
                if(selectPos!=i) {
                    selectPos = i
                    recyTopScrollStick?.scrollToPosition(i)
                    notifyItemRangeChanged(0, getData().size)
                }
                break
            }
        }

    }


    fun getScreenWidth(context: Context): Int {
        val wm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        return dm.widthPixels
    }
}