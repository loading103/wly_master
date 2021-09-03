package com.daqsoft.legacyModule.home


import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.LegacyModuleFragmentHomeTopMenuBinding
import com.daqsoft.legacyModule.databinding.LegacyModuleItemHomeMeunBinding
import com.daqsoft.legacyModule.home.bean.TopMenuBean
import com.daqsoft.legacyModule.rv.dsl.grid
import com.daqsoft.legacyModule.rv.dsl.linear
import com.daqsoft.provider.view.convenientbanner.utils.ScreenUtil

/**
 * @des 首页第一部分菜单
 * @author Wongxd
 * @Date 2020/4/22 15:38
 */
internal class LegacyHomeMenuFragment(val menus: List<TopMenuBean>) :
    BaseFragment<LegacyModuleFragmentHomeTopMenuBinding, LegacyHomeMenuViewModel>() {


    override fun getLayout(): Int = R.layout.legacy_module_fragment_home_top_menu


    override fun injectVm(): Class<LegacyHomeMenuViewModel> =
        LegacyHomeMenuViewModel::class.java


    override fun initView() {
        renderMenu()
    }


    private fun renderMenu() {

        mBinding.rv.grid {

            //            orientation = LinearLayoutManager.HORIZONTAL
//
//            val oneWidth =
//                (ScreenUtil.getScreenWidth(activity) - ScreenUtil.dip2px(activity, 40f)) / 5

            spanCount = 4

            menus.forEach { menuItem ->

                itemDsl {
                    xml(R.layout.legacy_module_item_home_meun)
                    render {
                        val mbinding = DataBindingUtil.bind<LegacyModuleItemHomeMeunBinding>(it)
                        mbinding?.name = menuItem.name

//                        mbinding?.ll?.layoutParams = ViewGroup.LayoutParams(oneWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

                        mbinding?.iv?.setImageResource(menuItem.imgRes)

                        it.onNoDoubleClick {
                            if(menuItem.name == "非遗资讯"){
                                ARouter.getInstance()
                                    .build(menuItem.path)
                                    .withString("channelCode","fyzx")
                                    .navigation()
                            }else{
                                ARouter.getInstance()
                                    .build(menuItem.path)
                                    .navigation()
                            }

                        }

                    }
                }

            }
        }
    }

    override fun initData() {

    }


}


/**
 * @des 首页第一部分菜单
 * @author Wongxd
 * @Date 2020/4/22 15:38
 */
internal class LegacyHomeMenuViewModel : BaseViewModel() {

}


