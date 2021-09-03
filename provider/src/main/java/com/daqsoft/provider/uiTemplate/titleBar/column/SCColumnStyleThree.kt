package com.daqsoft.provider.uiTemplate.titleBar.column

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.SubChanelChildBean
import com.daqsoft.provider.databinding.ScInformationStyleThreeBinding
import com.daqsoft.provider.databinding.ScInformationStyleThreeRecycleViewItemBinding
import com.daqsoft.provider.view.GridDecoration
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean

/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.information
 * @date 12/10/2020 上午 10:06
 * @author zp
 * @describe 资讯样式 3
 */

class SCColumnStyleThree(context: Context) : SCColumnStyleBase(context) {

    private lateinit var recyclerViewAdapter : StyleThreeRecyclerViewAdapter

    override fun initView() {
        val mBinding = DataBindingUtil.inflate<ScInformationStyleThreeBinding>(
            LayoutInflater.from(context),
            R.layout.sc_information_style_three,
            this,
            false
        )
        bindDataToView(mBinding)
        this.addView(mBinding.root)
    }

    override fun columnDataChanged(data: MutableList<SubChanelChildBean>) {
        recyclerViewAdapter.clear()
        if (data.size > 6){
            recyclerViewAdapter.add(data.subList(0,6))
        }else{
            recyclerViewAdapter.add(data)
        }
        recyclerViewAdapter.notifyDataSetChanged()
    }


    fun bindDataToView(viewBinding: ScInformationStyleThreeBinding) {

        recyclerViewAdapter = StyleThreeRecyclerViewAdapter()
        with(viewBinding.recycleView) {
            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration(GridDecoration(3, 12.dp, includeEdge = true, isRemoveBoth = true))
            adapter = recyclerViewAdapter
        }
    }


    inner class StyleThreeRecyclerViewAdapter :
        RecyclerViewAdapter<ScInformationStyleThreeRecycleViewItemBinding, SubChanelChildBean>(
            R.layout.sc_information_style_three_recycle_view_item
        ) {

        @SuppressLint("SetTextI18n")
        override fun setVariable(
            mBinding: ScInformationStyleThreeRecycleViewItemBinding,
            position: Int,
            item: SubChanelChildBean
        ) {
            // item点击
            mBinding.root.onNoDoubleClick {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_CONTENT)
                    .withString("channelCode", item.channelCode)
                    .withString("titleStr", item.name)
                    .navigation()
            }
            // 图
            Glide.with(mBinding.root.context)
                .load(item.backgroundImg)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.image)
            // 标题
            mBinding.title.text = "#${item.name}"
        }
    }
}

