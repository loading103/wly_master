package com.daqsoft.legacyModule.food

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.ActivityLegacyFoodListBinding
import com.daqsoft.legacyModule.databinding.LegacyModuleItemFoodBinding
import com.daqsoft.legacyModule.home.bean.LegacyFoodBean
import com.daqsoft.legacyModule.net.LegacyRepository
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.ElectronicLogin
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.excut

/**
 *@package:com.daqsoft.legacyModule.food
 *@date:2020/5/11 15:00
 *@author: caihj
 *@des:美食列表界面
 **/
@Route(path = ARouterPath.LegacyModule.LEGACY_FOOD_LIST_ACTIVITY)
internal class LegacyFoodListActivity :
    TitleBarActivity<ActivityLegacyFoodListBinding, LegacyFoodViewModel>() {

    override fun getLayout(): Int = R.layout.activity_legacy_food_list

    override fun setTitle(): String = getString(R.string.legacy_module_food)

    override fun injectVm(): Class<LegacyFoodViewModel> = LegacyFoodViewModel::class.java

    var page: Int = 1
    val pageSize: Int = 20

    override fun initView() {
        mBinding.llSearch.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }
        foodAdapter.setOnLoadMoreListener {
            page++
            mModel.getHomeFoodList(page)
        }
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
            //            mBinding.mSwipeRefreshLayout.isRefreshing = false
            page = 1
            mModel.getHomeFoodList(page)
        }
        mBinding.rvFood.layoutManager = GridLayoutManager(this@LegacyFoodListActivity, 2)
        mBinding.rvFood.adapter = foodAdapter
        mModel.foodList.observe(this, Observer {
            mBinding.mSwipeRefreshLayout.finishRefresh()
            if (!it.isNullOrEmpty()) {
                pageDeal(page, it as MutableList<LegacyFoodBean>, foodAdapter)
                foodAdapter.add(it)
            }
        })
    }

    private val foodAdapter = object :
        RecyclerViewAdapter<LegacyModuleItemFoodBinding, LegacyFoodBean>(R.layout.legacy_module_item_food) {
        override fun setVariable(
            mBinding: LegacyModuleItemFoodBinding,
            position: Int,
            item: LegacyFoodBean
        ) {
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
        mModel.getHomeFoodList(page)
    }

    private fun pageDeal(
        page: Int?, response: MutableList<LegacyFoodBean>, adapter:
        RecyclerViewAdapter<*, *>
    ) {
        if (page == null) {
            return
        }
        if (page == 1) {
            adapter.clear()
        }
        if (response == null) {
            adapter.loadEnd()
            return
        }
        if (response.size >= pageSize) {
            adapter.loadComplete()
        } else {
            adapter.loadEnd()
        }
    }


}


class LegacyFoodViewModel : BaseViewModel() {

    val foodList = MutableLiveData<List<LegacyFoodBean>>()

    fun getHomeFoodList(page: Int, pageSize: Int = 20) {
        LegacyRepository.service.getFoodList(page, pageSize, "sjsdms")
            .excute(object : BaseObserver<LegacyFoodBean>() {
                override fun onSuccess(response: BaseResponse<LegacyFoodBean>) {
                    foodList.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<LegacyFoodBean>) {
                    super.onFailed(response)
                }
            })
    }

}