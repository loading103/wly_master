package com.daqsoft.travelCultureModule.hotActivity.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemActivityOverviewTypesBinding
import com.daqsoft.provider.bean.ActivityOverViewTypes
import retrofit2.http.POST

/**
 * @Description
 * @ClassName   ActivityOverViewTypesAdapter
 * @Author      luoyi
 * @Time        2020/6/9 16:36
 */
class ActivityOverViewTypesAdapter : RecyclerViewAdapter<ItemActivityOverviewTypesBinding, ActivityOverViewTypes>
    (R.layout.item_activity_overview_types) {
    var isShowAll: Boolean = true
    var onActivityOverViewItemListener: OnActivityOverViewItemListener? = null
    override fun setVariable(mBinding: ItemActivityOverviewTypesBinding, position: Int, item: ActivityOverViewTypes) {
        item?.let {
            mBinding.tvActivityOverviewNums.text = "${it.num}场"
            mBinding.tvActivityOverviewTypeName.text = "" + it.name
            mBinding.root?.onNoDoubleClick {
                onActivityOverViewItemListener?.onItemClick(item)
            }
            if (position == 0) {
                mBinding.rlActivityOverview.setPadding(mBinding.root.context.resources.getDimensionPixelSize(R.dimen.dp_20), 0, mBinding.root.context.resources.getDimensionPixelSize(R.dimen.dp_12), 0)
            }else if (position == itemCount-1){
                mBinding.rlActivityOverview.setPadding(0, 0, mBinding.root.context.resources.getDimensionPixelSize(R.dimen.dp_20), 0)
            }else{
                mBinding.rlActivityOverview.setPadding(0, 0, mBinding.root.context.resources.getDimensionPixelSize(R.dimen.dp_12), 0)
            }
            when(position % 4){
                0-> mBinding.rlActivityOverviewBg.setBackgroundResource(R.mipmap.activity_overview_bg_green)
                1-> mBinding.rlActivityOverviewBg.setBackgroundResource(R.mipmap.activity_overview_bg_cyan)
                2-> mBinding.rlActivityOverviewBg.setBackgroundResource(R.mipmap.activity_overview_bg_blue)
                3-> mBinding.rlActivityOverviewBg.setBackgroundResource(R.mipmap.activity_overview_bg_orange)
            }
        }
    }

    interface OnActivityOverViewItemListener {
        fun onItemClick(item: ActivityOverViewTypes)
    }

    override fun getItemCount(): Int {
        if (!isShowAll) {
            if (getData().size > 9)
                return 9
        }
        return super.getItemCount()
    }
}