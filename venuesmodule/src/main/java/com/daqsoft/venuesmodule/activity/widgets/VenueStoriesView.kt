package com.daqsoft.venuesmodule.activity.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.bean.StoreBean
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.adapter.ReadStoryAdapter
import com.daqsoft.venuesmodule.adapter.VenueStroyAdapater
import com.daqsoft.venuesmodule.adapter.VenuesCommentAdapter
import com.daqsoft.venuesmodule.databinding.IncludeVenueDetailsCommentBinding
import com.daqsoft.venuesmodule.databinding.IncludeVenueDetailsStoryBinding

/**
 * @Description
 * @ClassName   VenueStoriesView
 * @Author     luoyi
 * @Time        2020/3/28 11:36
 */
class VenueStoriesView : FrameLayout {

    var mContext: Context? = null

    var binding: IncludeVenueDetailsStoryBinding? = null
    /**
     * 活动数据集
     */
    var datas: MutableList<StoreBean> = mutableListOf()

    var adapter: VenueStroyAdapater? = null



    val readStoryAdapter by lazy { ReadStoryAdapter(context) }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.include_venue_details_story,
            this,
            false
        )
        addView(binding!!.root)
    }

    /**
     * 设置数据
     */
    fun setData(data: MutableList<StoreBean>) {
        datas.clear()
        datas.addAll(data)
        updateUi()

    }

    fun updateUi() {
        adapter = VenueStroyAdapater(mContext!!)
        adapter?.add(datas)
        binding?.recyclerVenuesDetailsStory?.layoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
        binding?.recyclerVenuesDetailsStory?.adapter = adapter
    }



    fun setStoryList(storyList:MutableList<HomeStoryBean>){
        binding?.run {
            with(recyclerVenuesDetailsStory){
                layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
                readStoryAdapter.add(storyList)
                adapter = readStoryAdapter
            }
        }
    }


    @SuppressLint("SetTextI18n")
    fun setTotalStory(total:Int){
        binding?.tvVenuesDetailsStoryCount?.text = "共${total}个故事"
    }

}