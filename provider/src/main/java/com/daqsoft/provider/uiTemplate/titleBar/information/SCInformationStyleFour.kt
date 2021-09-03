package com.daqsoft.provider.uiTemplate.titleBar.information

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.MultipleRecyclerViewAdapter
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.databinding.*
import com.daqsoft.provider.view.GridDecoration
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean
import java.lang.Exception

/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.information
 * @date 12/10/2020 上午 10:06
 * @author zp
 * @describe 资讯样式 4
 */

class SCInformationStyleFour(context: Context) : SCInformationStyleBase(context) {


    private lateinit var recyclerViewAdapter : StyleFourRecyclerViewAdapter

    override fun initView() {
        val mBinding = DataBindingUtil.inflate<ScInformationStyleFourBinding>(
            LayoutInflater.from(context),
            R.layout.sc_information_style_four,
            this,
            false
        )
        bindDataToView(mBinding)
        this.addView(mBinding.root)
    }

    override fun informationDataChanged(data: MutableList<HomeContentBean>) {
        recyclerViewAdapter.clear()

        val finalData = mutableListOf<HomeContentBean>()
        if (data.size > 7) {
            finalData.addAll(data.subList(0,7))
        }else{
            finalData.addAll(data)
        }

        finalData.forEachIndexed { index, homeContentBean ->
            if (index <= 2){
                recyclerViewAdapter.addViewType(R.layout.sc_information_style_four_recycle_view_top_item)
            }else{
                if(index%2==0){
                    recyclerViewAdapter.addViewType(R.layout.sc_information_style_four_recycle_view_bottom_item_image)
                }else{
                    recyclerViewAdapter.addViewType(R.layout.sc_information_style_four_recycle_view_bottom_item_txt)
                }
            }
            recyclerViewAdapter.addItem(homeContentBean)
        }
        recyclerViewAdapter.notifyDataSetChanged()
    }


    fun bindDataToView(viewBinding: ScInformationStyleFourBinding) {

        recyclerViewAdapter = StyleFourRecyclerViewAdapter()
        with(viewBinding.recycleView) {
            adapter = recyclerViewAdapter
            layoutManager = GridLayoutManager(context, 3).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        if (position > 2 && position%2==0){
                            return 2
                        }
                        return 1
                    }
                }
            }
            addItemDecoration(object : RecyclerView.ItemDecoration(){
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    val spanCount = if (position < 3) 3 else 2
                    val spacing = 8.dp
                    val column = position % spanCount

                    if (position < 3){
                        // 去掉两边的spacing
                        outRect.left = if (column == 0) 0 else spacing - column * spacing / spanCount
                        outRect.right = if (column == spanCount - 1) 0 else (column + 1) * spacing / spanCount
                    }else{
                        outRect.left = if (column == 1) 0 else spacing / spanCount
                        outRect.right = if (column == 0) 0 else spacing / spanCount
                    }
                    if (position < spanCount) {
                        outRect.top = spacing
                    }
                    outRect.bottom = spacing
                }
            })
        }
    }

    inner class StyleFourRecyclerViewAdapter : MultipleRecyclerViewAdapter<ViewDataBinding, HomeContentBean>() {

        override fun setVariable(mBinding: ViewDataBinding, position: Int, item: HomeContentBean) {


            // item点击
            mBinding.root.onNoDoubleClick {
                if (item.contentType == "IMAGE") {
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_IMG)
                        .withString("id", item.id.toString())
                        .navigation()
                } else {
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                        .withString("id", item.id.toString())
                        .withString("contentTitle", "资讯详情")
                        .navigation()
                }
            }

            when(mBinding){
                is ScInformationStyleFourRecycleViewTopItemBinding ->{
                    // 图
                    Glide.with(mBinding.root.context)
                        .load(item.getContentCoverImageUrl())
                        .placeholder(R.mipmap.placeholder_img_fail_240_180)
                        .into(mBinding.image)
                    // 标题
                    mBinding.title.text = item.title
                    // 标签
                    mBinding.tag.text = if (position == 0) resources.getText(R.string.hot) else resources.getText(R.string.recommend)
                }
                is ScInformationStyleFourRecycleViewBottomItemTxtBinding -> {
                    // 标题
                    mBinding.title.text = item.title
                    // 时间
                    mBinding.time.text = DateUtil.formatDateByString("yyyy-MM-dd", "yyyy-MM-dd HH:mm", item.publishTime)
                }
                is ScInformationStyleFourRecycleViewBottomItemImageBinding ->{
                    // 标题
                    mBinding.title.text = item.title
                    // 时间
                    mBinding.time.text = DateUtil.formatDateByString("yyyy-MM-dd", "yyyy-MM-dd HH:mm", item.publishTime)
                    // 图
                    Glide.with(mBinding.root.context)
                        .load(item.getContentCoverImageUrl())
                        .placeholder(R.mipmap.placeholder_img_fail_240_180)
                        .into(mBinding.image)
                }
            }
        }
    }
}

