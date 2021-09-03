package com.daqsoft.travelCultureModule.hotActivity.adapter

import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemActivityOverviewTypesBinding
import com.daqsoft.mainmodule.databinding.ItemActivityOverviewTypesV1Binding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ActivityOverViewTypes

/**
 * @Description
 * @ClassName   ActivityOverViewTypesAdapter
 * @Author      luoyi
 * @Time        2020/6/9 16:36
 */
class ActivityOverViewTypesV1Adapter : RecyclerViewAdapter<ItemActivityOverviewTypesV1Binding, ActivityOverViewTypes>
    (R.layout.item_activity_overview_types_v1) {
    var times: String = ""
    override fun setVariable(mBinding: ItemActivityOverviewTypesV1Binding, position: Int, item: ActivityOverViewTypes) {
        item?.let {
            mBinding.tvActivityOverviewNums.text = "${it.num}场"
            mBinding.tvActivityOverviewTypeName.text = "" + it.name
            mBinding.root.onNoDoubleClick {
                ARouter.getInstance().build(MainARouterPath.MAIN_ACTIVITY_GL_LS)
                    .withString("classifyId", item.id)
                    .withString("times",times)
                    .navigation()
            }
        }
    }

}