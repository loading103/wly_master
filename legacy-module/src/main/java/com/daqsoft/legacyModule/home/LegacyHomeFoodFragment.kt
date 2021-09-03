package com.daqsoft.legacyModule.home


import android.os.Bundle
import android.os.Parcelable
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.LegacyModuleFragmentHomeFoodBinding
import com.daqsoft.legacyModule.databinding.LegacyModuleFragmentHomeTopMenuBinding
import com.daqsoft.legacyModule.databinding.LegacyModuleItemFoodBinding
import com.daqsoft.legacyModule.databinding.LegacyModuleItemHomeMeunBinding
import com.daqsoft.legacyModule.rv.dsl.grid
import com.daqsoft.legacyModule.rv.dsl.linear
import com.daqsoft.provider.view.convenientbanner.utils.ScreenUtil
import kotlinx.android.parcel.Parcelize
import java.lang.Exception
import com.daqsoft.legacyModule.getRealImages
import com.daqsoft.legacyModule.home.bean.LegacyFoodBean
import com.daqsoft.provider.MainARouterPath

/**
 * @des 首页非遗美食
 * @author caihj
 * @Date 2020/5/11 15:38
 */
internal class LegacyHomeFoodFragment :
    BaseFragment<LegacyModuleFragmentHomeFoodBinding, LegacyFoodViewModel>() {

    var foods: List<LegacyFoodBean>? = null

    companion object {
        const val DATAS = "food_data"

        fun newInstance(datas: ArrayList<LegacyFoodBean>): LegacyHomeFoodFragment {
            val bundle = Bundle()
            bundle.putParcelableArrayList(DATAS, datas)
            val legacyHomeFoodFragment = LegacyHomeFoodFragment()
            legacyHomeFoodFragment.arguments = bundle
            return legacyHomeFoodFragment
        }
    }

    override fun getLayout(): Int = R.layout.legacy_module_fragment_home_food


    override fun injectVm(): Class<LegacyFoodViewModel> =
        LegacyFoodViewModel::class.java


    override fun initView() {
        getParam()
        renderFood()
    }


    private fun getParam() {
        try {
            foods = arguments?.getParcelableArrayList(DATAS)
        } catch (e: Exception) {
        }

    }


    private fun renderFood() {
        mBinding.rvFood.layoutManager = GridLayoutManager(context, 2)
        foodAdapter.add(foods as MutableList<LegacyFoodBean>)
        mBinding.rvFood.adapter = foodAdapter
        foodAdapter.notifyDataSetChanged()
    }

    private var foodAdapter = object :RecyclerViewAdapter<LegacyModuleItemFoodBinding,LegacyFoodBean>(R.layout.legacy_module_item_food){
        override fun setVariable(mBinding: LegacyModuleItemFoodBinding, position: Int, item: LegacyFoodBean) {
            mBinding.name = item.title
            if (!item.imageUrls.isNullOrEmpty())
            mBinding.imageUrl = item.imageUrls[0].url
            mBinding.root.onNoDoubleClick {
                if(item.contentType=="IMAGE"){
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_IMG)
                        .withString("id", item.id.toString())
                        .navigation()
                }else{
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                        .withString("id", item.id.toString())
                        .withString("contentTitle", "美食详情")
                        .navigation()
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
internal class LegacyFoodViewModel : BaseViewModel() {

}

