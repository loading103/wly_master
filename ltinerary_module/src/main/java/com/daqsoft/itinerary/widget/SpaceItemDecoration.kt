package com.daqsoft.itinerary.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @Author：      邓益千
 * @Create by：   2020/5/11 14:06
 * @Description：
 */
class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration(){

    private var spanCount = 0

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        outRect.left = if (position%2==0) 0 else space / 2
        outRect.right = if(position%2 == 1) 0 else space / 2
        outRect.bottom = space
//        if (parent.getChildLayoutPosition(view) %spanCount == 0) {
//            outRect.left = 0
//        }

    }

}