package com.daqsoft.travelCultureModule.hotActivity.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemActivityOverviewTypesBinding
import com.daqsoft.mainmodule.databinding.ItemActivityOverviewTypesOldBinding
import com.daqsoft.provider.bean.ActivityOverViewTypes
import retrofit2.http.POST

/**
 * @Description
 * @ClassName   ActivityOverViewTypesAdapter
 * @Author      luoyi
 * @Time        2020/6/9 16:36
 */
class ActivityOverViewOldTypesAdapter : RecyclerViewAdapter<ItemActivityOverviewTypesOldBinding, ActivityOverViewTypes>
    (R.layout.item_activity_overview_types_old) {
    var isShowAll: Boolean = true
    var onActivityOverViewItemListener: OnActivityOverViewItemListener? = null
    override fun setVariable(mBinding: ItemActivityOverviewTypesOldBinding, position: Int, item: ActivityOverViewTypes) {
        item?.let {
            mBinding.tvActivityOverviewNums.text = "${it.num}场"
            mBinding.tvActivityOverviewTypeName.text = "" + it.name
            mBinding.root?.onNoDoubleClick {
                onActivityOverViewItemListener?.onItemClick(item)
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