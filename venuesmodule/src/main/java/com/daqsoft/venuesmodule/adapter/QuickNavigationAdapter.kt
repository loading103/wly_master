package com.daqsoft.venuesmodule.adapter

import android.content.Context
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.view.WindowManager
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemQuickNavigationBinding
import com.daqsoft.venuesmodule.model.QuickNavigationItem
import com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext


/**
 * @package name：com.daqsoft.venuesmodule.adapter
 * @date 2020/9/17 14:20
 * @author zp
 * @describe
 */
class QuickNavigationAdapter :
    RecyclerViewAdapter<ItemQuickNavigationBinding, QuickNavigationItem>(R.layout.item_quick_navigation) {

    var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int, item: QuickNavigationItem)
    }

    override fun setVariable(
        mBinding: ItemQuickNavigationBinding,
        position: Int,
        item: QuickNavigationItem
    ) {

        if (getData().size <= 4) {
            val screenWidth: Int = getScreenWidth(mBinding.root.context)
            val params: ViewGroup.LayoutParams = mBinding.root.layoutParams
            params.width = (screenWidth - 40.dp - (getData().size - 1) * 8.dp) / getData().size
            mBinding.root.layoutParams = params
        }

        mBinding.icon.setImageResource(item.icon)
        mBinding.title.text = item.text
        mBinding.root.onNoDoubleClick {
            onItemClickListener?.onItemClick(position, item)
        }
    }


    fun getScreenWidth(context: Context): Int {
        val wm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        return dm.widthPixels
    }

    override fun add(items: MutableList<QuickNavigationItem>) {
        super.add(items)
        checkIsEmpty()
    }

    override fun setNewData(items: MutableList<QuickNavigationItem>) {
        super.setNewData(items)
        checkIsEmpty()
    }


    private fun checkIsEmpty() {
        checkIsEmpty?.isEmpty(getData().isEmpty())
    }

    public fun isNeedShow(): Boolean {
        return  getData().size > 1
    }

    var checkIsEmpty: CheckIsEmpty? = null

    interface CheckIsEmpty {
        fun isEmpty(empty: Boolean)
    }
}