package com.daqsoft.venuesmodule.adapter

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ContentBean
import com.daqsoft.provider.view.GridDecoration
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemOtherColumnsBinding

/**
 * @package name：com.daqsoft.venuesmodule.adapter
 * @date 2020/9/16 11:27
 * @author zp
 * @describe 其他栏目 adapter
 */
class OtherColumnsAdapter(val context: Context) : RecyclerViewAdapter<ItemOtherColumnsBinding, HashMap<String,Any>>(R.layout.item_other_columns) {

    override fun setVariable(mBinding: ItemOtherColumnsBinding, position: Int, item: HashMap<String,Any>) {
        // 标题
        mBinding.title.text = item["title"].toString()

        // 数据
        val adapter = OtherColumnsRecycleViewAdapter()
        var data = item["data"] as MutableList<ContentBean>
        mBinding.tvProviderMore.visibility = if (data.size > 4) View.VISIBLE else View.GONE
        adapter.add(data)
        mBinding.recycleView.layoutManager = GridLayoutManager(context,2)
        mBinding.recycleView.addItemDecoration(GridDecoration(2,12,
            includeEdge = true,
            isRemoveBoth = true
        ))
        mBinding.recycleView.adapter = adapter

        mBinding.tvProviderMore.onNoDoubleClick {
            // 资讯列表
            ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT)
                .withString("channelCode", "jcsj")
                .navigation()
        }
    }
}