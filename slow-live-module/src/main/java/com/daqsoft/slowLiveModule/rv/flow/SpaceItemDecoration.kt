package com.daqsoft.slowLiveModule.rv.flow

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

internal class SpecSpaceItemDecoration : ItemDecoration {
    /**
     * @param ctx
     * @param row 竖直间距 dp
     * @param clo 水平间距 dp
     */
    constructor(ctx: Context, row: Int, clo: Int) {
        r = dp2px(ctx, row.toFloat())
        c = dp2px(ctx, clo.toFloat())
    }

    private var c: Int
    private var r: Int

    /**
     * @param row 竖直间距  px
     * @param clo 水平间距  px
     */
    constructor(row: Int, clo: Int) {
        r = row
        c = clo
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.top = r
        outRect.left = c
        outRect.right = c
        outRect.bottom = r
    }

    private fun dp2px(ctx: Context, value: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, ctx.resources.displayMetrics).toInt()
    }
}