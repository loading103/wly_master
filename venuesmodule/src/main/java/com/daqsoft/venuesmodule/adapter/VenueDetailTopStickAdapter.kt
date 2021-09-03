package com.daqsoft.venuesmodule.adapter

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.ValueIdKeyBean
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemVenueDetailTopStickBinding
import com.daqsoft.venuesmodule.model.QuickNavigationItem

/**
 * @Description 顶部悬浮定位导航
 * @ClassName   VenueDetailTopStickAdapter
 * @Author      luoyi
 * @Time        2020/3/28 16:49
 */
class VenueDetailTopStickAdapter : RecyclerViewAdapter<ItemVenueDetailTopStickBinding, QuickNavigationItem>(R.layout.item_venue_detail_top_stick) {

    var selectPos: Int = 0
    var onItemClickListener: OnItemClickListener? = null
    override fun setVariable(mBinding: ItemVenueDetailTopStickBinding, position: Int, item: QuickNavigationItem) {

//        if(getData().size <= 4){
//            val screenWidth: Int = getScreenWidth(mBinding.root.context)
//            val params: ViewGroup.LayoutParams = mBinding.root.layoutParams
//            params.width = (screenWidth) / getData().size
//            mBinding.root.layoutParams = params
//        }


        if (selectPos == position) {
            mBinding.tvVenueTab.isSelected = true
            mBinding.isShow = View.VISIBLE
        } else {
            mBinding.isShow = View.GONE
            mBinding.tvVenueTab.isSelected = false
        }
        mBinding.name=item.text
        mBinding.root.onNoDoubleClick {
            selectPos = position
//            notifyItemRangeChanged(0, getData().size)
            if (onItemClickListener != null) {
                onItemClickListener?.onItemClick(item.id)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(id: Int)
    }

    fun updateSelectPos(id: Int) {
        for (i in getData().indices) {
            val item = getData()[i]
            if (item.id == id) {
                if(selectPos!=i) {
                    selectPos = i
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