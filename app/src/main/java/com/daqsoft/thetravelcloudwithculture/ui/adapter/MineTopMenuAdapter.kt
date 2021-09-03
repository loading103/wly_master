package com.daqsoft.thetravelcloudwithculture.ui.adapter

import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.vlayout.LayoutHelper
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.uiTemplate.BaseDelegateAdapter
import com.daqsoft.provider.uiTemplate.TemplateType
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.ItemMineTopMenuStyleBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemMineTopMenuTemplateBinding

/**
 * 个人中心顶部菜单适配器
 */
class MineTopMenuAdapter(helper: LayoutHelper) : BaseDelegateAdapter<ItemMineTopMenuStyleBinding>(helper, R.layout.item_mine_top_menu_style) {
    var menus: MutableList<CommonTemlate> = mutableListOf()

    fun addAll(datas: MutableList<CommonTemlate>) {
        menus.clear()
        menus.addAll(datas)
    }

    override fun bindDataToView(mBinding: ItemMineTopMenuStyleBinding, position: Int) {
        if (!menus.isNullOrEmpty()) {
            mBinding?.recycleView?.run {
                layoutManager = GridLayoutManager(context, 4)
                adapter = mineTopMenuAdapter
                mineTopMenuAdapter.clear()
                mineTopMenuAdapter.add(menus)
                mineTopMenuAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        return TemplateType.MINE_TOP_MENU
    }

    /**
     * 个人中心 top menu 适配器
     */
    private var mineTopMenuAdapter =
        object : RecyclerViewAdapter<ItemMineTopMenuTemplateBinding, CommonTemlate>(R.layout.item_mine_top_menu_template) {
            override fun setVariable(mBinding: ItemMineTopMenuTemplateBinding, position: Int, item: CommonTemlate) {
                Glide.with(mBinding.root.context)
                    .load(item.selectIcon)
                    .into(mBinding.imgScTab)
                mBinding.tvScTab.text = item.name
                mBinding.root.onNoDoubleClick {
                    mBinding.root.onNoDoubleClick {
                        MenuJumpUtils.menuPageJumpUtils(item)
                    }
                }
            }
        }
}