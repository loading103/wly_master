package com.daqsoft.provider.uiTemplate.titleBar.story

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.adapter.ReadStoryAdapter
import com.daqsoft.provider.bean.HomeStoryTagBean
import com.daqsoft.provider.bean.StoreBean
import com.daqsoft.provider.databinding.ScStoryStyleOneBinding
import com.daqsoft.provider.databinding.ScStoryStyleOneTypeRecycleViewItemBinding
import com.daqsoft.provider.view.GridDecoration

/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.story
 * @date 12/10/2020 上午 10:06
 * @author zp
 * @describe 故事样式1
 */

class SCStoryStyleOne(context: Context) : SCStoryStyleBase(context){

    private lateinit var storyAdapter : StoryAdapter

    private lateinit var typeAdapter: StyleOneTypeRecyclerViewAdapter

    lateinit var mBinding : ScStoryStyleOneBinding


    override fun initView() {
        mBinding = DataBindingUtil.inflate<ScStoryStyleOneBinding>(
            LayoutInflater.from(context),
            R.layout.sc_story_style_one,
            this,
            false
        )
        mBinding.recycleView.visibility = View.GONE
        bindDataToView(mBinding)
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
    fun bindDataToView(viewBinding: ScStoryStyleOneBinding) {
        storyAdapter = StoryAdapter(context)
        typeAdapter = StyleOneTypeRecyclerViewAdapter()

        // 类型
        with(viewBinding.recycleViewType){
            adapter = typeAdapter
            layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
            addItemDecoration(object : RecyclerView.ItemDecoration(){
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = getChildAdapterPosition(view)
                    val count = state.itemCount - 1
                    outRect.right = -(12.dp)
                    if (position == 0){
                        outRect.left = -(10.dp)
                    }
                    if (position == count){
                        outRect.right = -(10.dp)
                    }
                }
            })
        }

        // 故事
        with(viewBinding.recycleView){
            adapter = storyAdapter
            layoutManager = GridLayoutManager(context,2)
            addItemDecoration(GridDecoration(2,12.dp, includeEdge = true, isRemoveBoth = true))
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        Glide.with(context).resumeRequests()
                    } else {
                        Glide.with(context).pauseRequests()
                    }
                }
            })
        }
    }


    inner class StyleOneTypeRecyclerViewAdapter : RecyclerViewAdapter<ScStoryStyleOneTypeRecycleViewItemBinding, HomeStoryTagBean>(
        R.layout.sc_story_style_one_type_recycle_view_item
    ) {

        val images = arrayListOf(
            R.mipmap.mode_story_recommend_no1,
            R.mipmap.mode_story_recommend_no2,
            R.mipmap.mode_story_recommend_no3,
            R.mipmap.mode_story_recommend_no4
        )

        @SuppressLint("SetTextI18n")
        override fun setVariable(
            mBinding: ScStoryStyleOneTypeRecycleViewItemBinding,
            position: Int,
            item: HomeStoryTagBean
        ) {
            val remainder = position % images.size
            // 图
            Glide.with(mBinding.root.context)
                .load(images[images.size-remainder-1])
//                .load(item.cover)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.image)
            // 标题
            mBinding.title.text = item.name
            // 数量
            mBinding.amount.text = "${item.storyNum}${resources.getString(R.string.good_story)}"
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

