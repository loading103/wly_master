package com.daqsoft.travelCultureModule.citycard

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.FragmentCityMenuBinding
import com.daqsoft.mainmodule.databinding.ItemCityMenuBinding
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.bean.HomeMenu
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * @des 首页第一部分菜单
 * @author PuHua
 * @Date 2019/12/5 17:52
 * @update luoyi
 * @description 修改 fragment创建方式
 *
 * */
class HomeMenuFragment : BaseFragment<FragmentCityMenuBinding, HomeMenuFragmentViewModel>() {

    var menus = mutableListOf<HomeMenu>()

    var siteId: String? = ""

    var region: String? = ""

    companion object {
        const val MENUS: String = "menu"
        const val SITE_ID: String = "site_id"
        const val REGION: String = "region"
        fun newInstance(menus: MutableList<HomeMenu>, siteId: String? = "", region: String? = ""): HomeMenuFragment {
            var frag = HomeMenuFragment()
            var bundle: Bundle = Bundle()
            bundle.putParcelableArrayList(MENUS, ArrayList(menus))
            bundle.putString(SITE_ID, siteId)
            bundle.putString(REGION, region)
            frag.arguments = bundle
            return frag

        }
    }

    override fun getLayout(): Int = R.layout.fragment_city_menu

    override fun initData() {
        getParams()
        adapter.add(this.menus)
    }

    private fun getParams() {
        try {
            var datas = arguments?.getParcelableArrayList<HomeMenu>(MENUS)
             region = arguments?.getString(REGION)
             siteId = arguments?.getString(SITE_ID)

            if (!datas.isNullOrEmpty()) {
                menus.clear()
                menus.addAll(datas)
            }
        } catch (e: Exception) {

        }
    }

    override fun initView() {

        val gridLayoutManager = GridLayoutManager(context!!, 5)
        mBinding.recyclerViewCity.layoutManager = gridLayoutManager
        mBinding.recyclerViewCity.adapter = adapter
        mBinding.recyclerViewCity.isNestedScrollingEnabled
    }


    private val adapter = object :
        RecyclerViewAdapter<ItemCityMenuBinding, HomeMenu>(
            R.layout.item_city_menu
        ) {
        @SuppressLint("CheckResult")
        override fun setVariable(mBinding: ItemCityMenuBinding, position: Int, item: HomeMenu) {
            mBinding.url = item.unSelectIcon
            mBinding.name = item.name

            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    MenuJumpUtils.menuPageJumpUtils(item,siteId,region)
                }
        }

    }

    override fun injectVm(): Class<HomeMenuFragmentViewModel> =
        HomeMenuFragmentViewModel::class.java

}


/**
 * @des 首页第一部分的viewMode
 * @author PuHua
 * @Date 2019/12/5 17:54
 */
class HomeMenuFragmentViewModel : BaseViewModel() {

}