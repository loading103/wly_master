package com.daqsoft.provider.uiTemplate.titleBar.story

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.adapter.ReadStoryAdapter
import com.daqsoft.provider.bean.HomeStoryTagBean
import com.daqsoft.provider.bean.StoreBean
import com.daqsoft.provider.databinding.ScStoryStyleTwoBinding
import com.daqsoft.provider.databinding.ScStoryStyleTwoTypeRecycleViewItemBinding
import com.daqsoft.provider.view.GridDecoration

/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.story
 * @date 12/10/2020 上午 10:06
 * @author zp
 * @describe 故事样式1
 */

class SCStoryStyleTwo(context: Context) : SCStoryStyleBase(context){

    private lateinit var storyAdapter : StoryAdapter

    private lateinit var typeAdapter: StyleTwoTypeRecyclerViewAdapter

    lateinit var mBinding : ScStoryStyleTwoBinding

    override fun initView() {
        mBinding = DataBindingUtil.inflate<ScStoryStyleTwoBinding>(
            LayoutInflater.from(context),
            R.layout.sc_story_style_two,
            this,
            false
        )
        mBinding.recycleView.visibility = View.GONE
        bindDataToView(mBinding)
        this.setBackgroundColor(resources.getColor(R.color.white))
        this.addView(mBinding.root)
    }

    override fun storyDataChanged(data: MutableList<StoreBean>) {
        mBinding.recycleView.visibility = View.VISIBLE
        storyAdapter.clear()
        storyAdapter.add(data)
        storyAdapter.notifyDataSetChanged()
    }

    override fun storyTypeDataChanged(data: MutableList<HomeStoryTagBean>) {
        typeAdapter.clear()
        typeAdapter.add(data)
        typeAdapter.notifyDataSetChanged()
    }

    /**
     * 绑定数据到视图
     * @param mBinding ScInformationStyleOneBinding
     */
    fun bindDataToView(viewBinding: ScStoryStyleTwoBinding) {
        storyAdapter = StoryAdapter(context)
        typeAdapter = StyleTwoTypeRecyclerViewAdapter()

        // 类型
        with(viewBinding.recycleViewType){
            adapter = typeAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    val position = getChildAdapterPosition(view)
                    val count = state.itemCount - 1
                    if (position != count){
                        outRect.right = 8.dp
                    }
                }
            })
        }

        // 故事
        with(viewBinding.recycleView){
            adapter = storyAdapter
            layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            addItemDecoration(GridDecoration(2,8.dp, includeEdge = true, isRemoveBoth = true))
        }
    }


    inner class StyleTwoTypeRecyclerViewAdapter : RecyclerViewAdapter<ScStoryStyleTwoTypeRecycleViewItemBinding, HomeStoryTagBean>(
        R.layout.sc_story_style_two_type_recycle_view_item) {
        @SuppressLint("CheckResult", "SetTextI18n")
        override fun setVariable(
            mBinding: ScStoryStyleTwoTypeRecycleViewItemBinding,
            position: Int,
            item: HomeStoryTagBean
        ) {
            // 标题
            mBinding.content.text = "#${item.name}"
            // item 点击
            mBinding.root.onNoDoubleClick {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_STORY_TAG_DETAIL)
                    .withString("id", item.id)
                    .navigation()
            }
        }

    }
}

