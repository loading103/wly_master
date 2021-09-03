package com.daqsoft.provider.uiTemplate.menu.topMenu

import android.annotation.SuppressLint
import androidx.fragment.app.FragmentManager
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.databinding.ItemGridHomeMenuBinding
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.view.extend.onModuleNoDoubleClick
import java.lang.ref.SoftReference

/**
 * @Description
 * @ClassName   TopMenuItemGridAdapter
 * @Author      luoyi
 * @Time        2020/11/5 11:40
 */
class TopMenuItemGridAdapter : RecyclerViewAdapter<ItemGridHomeMenuBinding, CommonTemlate>(
    R.layout.item_grid_home_menu
) {
    var fragmentManger: SoftReference<FragmentManager>? = null
    @SuppressLint("CheckResult")
    override fun setVariable(
        mBinding: ItemGridHomeMenuBinding,
        position: Int,
        item: CommonTemlate
    ) {
        mBinding.url = item.unSelectIcon
        mBinding.name = item.name
        var path: String? = null
        mBinding.root.onModuleNoDoubleClick(item.name ?: "", ProviderApi.REGION_SHORTCUTS) {
            MenuJumpUtils.menuPageJumpUtils(item, "", "", fragmentManger?.get())
        }
    }
}