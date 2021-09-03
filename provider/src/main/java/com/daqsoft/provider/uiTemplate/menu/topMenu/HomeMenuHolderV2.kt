package com.daqsoft.provider.uiTemplate.menu.topMenu

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.layoutmanager.FullyGridLayoutManager
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.extend.onModuleNoDoubleClick
import com.daqsoft.provider.databinding.ItemGridHomeMenuBinding
import com.daqsoft.provider.utils.MenuJumpUtils
import java.lang.ref.SoftReference

/**
 * @Description
 * @ClassName   HomeMenuHolder
 * @Author      luoyi
 * @Time        2020/5/20 10:12
 */
class HomeMenuHolderV2 : Holder<MutableList<CommonTemlate>> {


    var mContext: Context? = null
    var recyleMenus: RecyclerView? = null
    var mFragmentManger: SoftReference<FragmentManager>? = null
    var adapter: TopMenuItemGridAdapter? = null

    constructor(itemView: View, context: Context) : super(itemView) {
        this.mContext = context
        itemView.tag = this
    }

    override fun initView(itemView: View?) {
        recyleMenus = itemView?.findViewById(R.id.recycler_view)
    }

    override fun updateUI(data: MutableList<CommonTemlate>) {
        if (mContext != null && recyleMenus != null) {
            adapter = TopMenuItemGridAdapter()
                .apply {
                    fragmentManger = mFragmentManger
                }
            adapter?.clear()
            adapter?.emptyViewShow = false
            adapter?.add(data)
            val gridLayoutManager = StaggeredGridLayoutManager(5, GridLayoutManager.VERTICAL)
            recyleMenus?.layoutManager = gridLayoutManager
            recyleMenus?.adapter = adapter
        }
    }

}