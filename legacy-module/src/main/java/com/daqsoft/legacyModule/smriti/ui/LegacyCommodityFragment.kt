package com.daqsoft.legacyModule.smriti.ui

import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.FragmentLegacyCommodityBinding
import com.daqsoft.legacyModule.smriti.adapter.CommodityAdapter
import com.daqsoft.legacyModule.smriti.vm.LegacyCommodityViewModel
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.LegacyProductBean

/**
 * @package name：com.daqsoft.legacyModule.smriti.ui
 * @date 11/11/2020 上午 9:58
 * @author zp
 * @describe 非遗商品
  */
class LegacyCommodityFragment : BaseFragment<FragmentLegacyCommodityBinding, LegacyCommodityViewModel>() {

    var resourceCode:String? = ""

    companion object{
        fun newInstance(resourceCode:String): LegacyCommodityFragment = LegacyCommodityFragment().apply {
            arguments = Bundle().apply {
               putString("resourceCode",resourceCode)
           }
        }
    }

    private val commodityAdapter by lazy { CommodityAdapter() }

    override fun getLayout(): Int {
        return R.layout.fragment_legacy_commodity
    }

    override fun injectVm(): Class<LegacyCommodityViewModel> {
        return LegacyCommodityViewModel::class.java
    }

    override fun initView() {

        resourceCode = arguments?.getString("resourceCode","")

        mBinding.commodity.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    val position = parent.getChildAdapterPosition(view)
                    val count = state.itemCount - 1

                    if (position != 0 && position != count){
                        outRect.bottom = 20.dp
                    }
                }
            })
            adapter = commodityAdapter
        }

        mBinding.more.onNoDoubleClick {
            ARouter
                .getInstance()
                .build(ARouterPath.LegacyModule.LEGACY_PRODUCT_LIST_ACTIVITY)
                .withString("resourceCode",resourceCode)
                .navigation()
        }
    }

    override fun initData() {
    }

    private var data : MutableList<LegacyProductBean> ? = null

    fun setData(data:MutableList<LegacyProductBean>){
        this.data = data

        commodityAdapter.clear()
        if(data.size>3){
            commodityAdapter.add(data.subList(0,3))
        }else{
            commodityAdapter.add(data)
        }
        commodityAdapter.notifyDataSetChanged()
    }


    override fun onResume() {
        super.onResume()
        if(!data.isNullOrEmpty()){
            if (data!!.size > 3){
                mBinding.more.visibility = View.VISIBLE
            }else{
                mBinding.more.visibility = View.VISIBLE
            }
        }

    }
}