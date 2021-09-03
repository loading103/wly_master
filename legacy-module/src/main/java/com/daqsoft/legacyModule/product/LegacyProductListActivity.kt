package com.daqsoft.legacyModule.product

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.ActivityLegacyProductListBinding
import com.daqsoft.legacyModule.databinding.LegacyModuleItemProductBinding
import com.daqsoft.legacyModule.smriti.adapter.CommodityAdapter
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.ElectronicLogin
import com.daqsoft.provider.bean.LegacyProductBean
import com.daqsoft.provider.bean.LegacyProducts
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.excut
import org.jetbrains.anko.toast

/**
 *@package:com.daqsoft.legacyModule.food
 *@date:2020/5/11 15:00
 *@author: caihj
 *@des:商品列表界面
 **/
@Route(path = ARouterPath.LegacyModule.LEGACY_PRODUCT_LIST_ACTIVITY)
internal class LegacyProductListActivity : TitleBarActivity<ActivityLegacyProductListBinding, LegacyProjectViewModel>() {

    @Autowired
    @JvmField
    var resourceCode : String = ""


    override fun getLayout(): Int = R.layout.activity_legacy_product_list

    override fun setTitle(): String =getString(R.string.legacy_module_product)

    override fun injectVm(): Class<LegacyProjectViewModel> = LegacyProjectViewModel::class.java

    var page:Int = 1
    val pageSize:Int = 20

    override fun initView() {

        mModel.resourceCode = resourceCode

        productAdapter.setOnLoadMoreListener {
            page++
            mModel.getProductList(page)
        }
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
//            mBinding.mSwipeRefreshLayout.isRefreshing = false
            page = 1
            mModel.getProductList(page)
        }
        mBinding.rvProducts.layoutManager = LinearLayoutManager(this@LegacyProductListActivity)
        mBinding.rvProducts.addItemDecoration(object : RecyclerView.ItemDecoration(){
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildAdapterPosition(view)
                val count = state.itemCount - 1
                outRect.top  = 20.dp
                if(position == count){
                    outRect.bottom = 20.dp
                }
            }
        })
        mBinding.rvProducts.adapter = productAdapter
        mModel.productBean.observe(this, Observer {
            mBinding.mSwipeRefreshLayout.finishRefresh()
            dissMissLoadingDialog()
            if (it.list.isNotEmpty()){
                pageDeal(page, it.isLastPage,productAdapter)
                productAdapter.clear()
                productAdapter.add(it.list as MutableList<LegacyProductBean>)
                productAdapter.notifyDataSetChanged()
            }
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.mSwipeRefreshLayout.finishRefresh()
        })
    }

    private val productAdapter by lazy { CommodityAdapter() }

//    private val productAdapter = object : RecyclerViewAdapter<LegacyModuleItemProductBinding, LegacyProductBean>(R.layout.legacy_module_item_product){
//        @SuppressLint("SetTextI18n")
//        override fun setVariable(mBinding: LegacyModuleItemProductBinding, position: Int, item: LegacyProductBean) {
//            mBinding.name = item.productName
//            if(item.image.isNotEmpty())
//                mBinding.imageUrl = item.image
//            mBinding.tvPrice.text = "¥ ${item.marketPrice}"
//            if(item.saleType == "2"){
//                mBinding.tvTag.visibility = View.VISIBLE
//            }
//        }
//    }
    override fun initData() {
        showLoadingDialog()
        if(AppUtils.isLogin()){
            mModel.loginElectronic()
        }else{
            ToastUtils.showMessage("您还没有登录哦!")
            ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY).navigation()
        }
    }

    private fun pageDeal(
        page: Int?, lastPageFlag: Boolean, adapter:
        RecyclerViewAdapter<*, *>
    ) {
        if (page == null) {
            return
        }
        if (page == 1) {
            adapter.clear()
        }
        if (!lastPageFlag) {
            adapter.loadComplete()
        } else {
            adapter.loadEnd()
        }
    }


}


 class LegacyProjectViewModel: BaseViewModel() {
     var resourceCode:String = ""
    val productBean = MutableLiveData<LegacyProducts>()
     var sysCode =  SPUtils.getInstance().getString(SPKey.SHOP_CODE, "")
    fun getProductList(page: Int,pageSize:Int = 20){
        ElectronicRepository.electronicService.getLegacyProductLs(resourceCode = resourceCode,sysCode = sysCode,pageNum = page,pageSize = pageSize)
            .excut(object : ElectronicObserver<LegacyProducts>() {
                override fun onSuccess(response: BaseResponse<LegacyProducts>) {
                    productBean.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<LegacyProducts>) {
                    mError.postValue(response)
                }

            })
    }
     /**
      * 小电商登录
      */
     fun loginElectronic() {
         val uid = SPUtils.getInstance().getString(SPKey.UID)
         val token = SPUtils.getInstance().getString(SPKey.USER_CENTER_TOKEN)
         var encry = SPUtils.getInstance().getString(SPKey.ENCRYPTION)
         ElectronicRepository.electronicService.login(uid, token,encry)
             .excut(object : ElectronicObserver<ElectronicLogin>(mPresenter) {
                 override fun onSuccess(response: BaseResponse<ElectronicLogin>) {
                     var userLogin = response.data
                     sysCode = userLogin!!.userInfo.sysCode
                     SPUtils.getInstance().put(SPKey.SESSIONID, userLogin?.sessionId)
                    getProductList(1)
                 }
             })
     }
}