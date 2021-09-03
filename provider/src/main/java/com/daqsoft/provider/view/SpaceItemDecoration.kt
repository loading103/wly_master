package com.daqsoft.provider.view

import android.R.attr.bottom
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView



/**
 * @Description 給gridLayout设置行间距
 * @ClassName   Item
 * @Author      PuHua
 * @Time        2020/1/2 14:04
 */
class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        // 不是第一个的格子都设一个左边和底部的间距
        outRect.left = space
        outRect.bottom = space
        // 由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
        if (parent.getChildLayoutPosition(view) % 2 == 0) {
            outRect.left = 0
        }
    }

}