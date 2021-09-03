package com.daqsoft.thetravelcloudwithculture.sc.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.layoutmanager.FullyGridLayoutManager
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.extend.onModuleNoDoubleClick
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeMenuBinding
import com.daqsoft.provider.bean.HomeMenu
import com.daqsoft.thetravelcloudwithculture.ui.utils.JumpUtils

/**
 * @Description
 * @ClassName   HomeMenuHolder
 * @Author      luoyi
 * @Time        2020/5/20 10:12
 */
class HomeMenuHolder : Holder<MutableList<HomeMenu>> {


    var mContext: Context? = null
    var recyleMenus: RecyclerView? = null

    constructor(itemView: View, context: Context) : super(itemView) {
        this.mContext = context
        itemView.tag = this
    }

    override fun initView(itemView: View?) {
        recyleMenus = itemView?.findViewById(R.id.recycler_view)
    }

    override fun updateUI(data: MutableList<HomeMenu>) {
        if (mContext != null && recyleMenus != null && adapter != null) {
            adapter.clear()
            adapter.emptyViewShow = false
            val gridLayoutManager = FullyGridLayoutManager(mContext, 5)
            recyleMenus?.layoutManager = gridLayoutManager
            recyleMenus?.adapter = adapter
            adapter.add(data)
        }
    }

    private val adapter = object :
        RecyclerViewAdapter<ItemHomeMenuBinding, HomeMenu>(
            R.layout.item_home_menu
        ) {
        @SuppressLint("CheckResult")
        override fun setVariable(mBinding: ItemHomeMenuBinding, position: Int, item: HomeMenu) {
            mBinding.url = item.unSelectIcon
            mBinding.name = item.name
            var path: String? = null
            mBinding.root.onModuleNoDoubleClick(item.name, ProviderApi.REGION_SHORTCUTS) {
                JumpUtils.menuPageJumpUtils(item)
            }
        }
    }
}