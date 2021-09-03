package com.daqsoft.provider.uiTemplate.titleBar.column

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.SubChanelChildBean
import com.daqsoft.provider.bean.SubChannelBean
import com.daqsoft.provider.databinding.ScInformationStyleTwoBinding
import com.daqsoft.provider.databinding.ScInformationStyleTwoRecycleViewItemBinding
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.view.extend.onModuleNoDoubleClick
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean

/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.information
 * @date 12/10/2020 上午 10:06
 * @author zp
 * @describe 资讯样式 2
 */

class SCColumnStyleTwo(context: Context) : SCColumnStyleBase(context) {


    private lateinit var recyclerViewAdapter : StyleTwoRecyclerViewAdapter

    override fun initView() {
        val mBinding = DataBindingUtil.inflate<ScInformationStyleTwoBinding>(
            LayoutInflater.from(context),
            R.layout.sc_information_style_two,
            this,
            false
        )
        bindDataToView(mBinding)
        this.addView(mBinding.root)
    }

    override fun columnDataChanged(data: MutableList<SubChanelChildBean>) {
        recyclerViewAdapter.clear()
        if (data.size > 5){
            recyclerViewAdapter.add(data.subList(0,5))
        }else{
            recyclerViewAdapter.add(data)
        }
        recyclerViewAdapter.notifyDataSetChanged()
    }


    fun bindDataToView(viewBinding: ScInformationStyleTwoBinding) {

        recyclerViewAdapter = StyleTwoRecyclerViewAdapter()
        with(viewBinding.recycleView) {
            adapter = recyclerViewAdapter
            val manager = GridLayoutManager(context,3)
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (position==3){
                        return 2
                    }
                    return 1
                }
            }
            layoutManager = manager
            addItemDecoration(object : RecyclerView.ItemDecoration(){
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view) // item position
                    val spanCount = if (position < 3) 3 else 2
                    val spacing = 3.dp
                    val column = position % spanCount // item c

                    if (position < 3){
                        // 去掉两边的spacing
                        outRect.left = if (column == 0) 0 else spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                        outRect.right = if (column == spanCount - 1) 0 else (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)
                    }else{
                        if(column == 1){
                            outRect.left = 0
                            outRect.right = spacing - column * spacing / 3
                        }else{
                            outRect.right = 0
                            outRect.left = (column + 1) * spacing / 3
                        }
                    }
                    if (position < spanCount) { // top edge
                        outRect.top = spacing
                    }
                    outRect.bottom = spacing // item bottom

                }
            })

        }
    }


    inner class StyleTwoRecyclerViewAdapter :
        RecyclerViewAdapter<ScInformationStyleTwoRecycleViewItemBinding, SubChanelChildBean>(
            R.layout.sc_information_style_two_recycle_view_item
        ) {

        override fun setVariable(
            mBinding: ScInformationStyleTwoRecycleViewItemBinding,
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
            mBinding.title.text = item.name

        }
    }
}

