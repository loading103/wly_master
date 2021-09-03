package com.daqsoft.thetravelcloudwithculture.home.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentHomeMenuBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeMenuBinding
import com.daqsoft.provider.bean.HomeMenu
import com.daqsoft.thetravelcloudwithculture.ui.utils.JumpUtils
import com.jakewharton.rxbinding2.view.RxView
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * @des 首页第一部分菜单
 * @author PuHua
 * @Date 2019/12/5 17:52
 */
class HomeMenuFragment : BaseFragment<FragmentHomeMenuBinding, HomeMenuFragmentViewModel>() {
    var menus = mutableListOf<HomeMenu>()

//    constructor() {
//
//    }
//
//    constructor(menus: MutableList<HomeMenu>) : super() {
//        this.menus = menus
//    }

    companion object {
        const val MENUS = "menus"
        fun newInstance(datas: MutableList<HomeMenu>): HomeMenuFragment {
            var frag = HomeMenuFragment()
            var bundle = Bundle()
            bundle.putParcelableArrayList(MENUS, ArrayList(datas))
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayout(): Int = R.layout.fragment_home_menu

    override fun initData() {

        adapter.add(this.menus)
    }

    override fun initView() {
        getParams()
        val gridLayoutManager = GridLayoutManager(context, 5)
        mBinding.recyclerView.layoutManager = gridLayoutManager
        mBinding.recyclerView.adapter = adapter
    }

    private fun getParams() {
        try {
            var tempLists = arguments?.getParcelableArrayList<HomeMenu>(MENUS)
            if (!tempLists.isNullOrEmpty()) {
                menus.clear()
                menus.addAll(tempLists)
            }
        } catch (e: Exception) {
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
            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    activity?.let { it1 -> JumpUtils.menuPageJumpUtils(item,childFragmentManager) }
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