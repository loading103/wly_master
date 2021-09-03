package com.daqsoft.provider.businessview.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.adapter.ReadStoryAdapter
import com.daqsoft.provider.bean.StoreBean
import com.daqsoft.provider.businessview.adapter.ProviderStoriesAdapter
import com.daqsoft.provider.databinding.LayoutProviderStoriesBinding

/**
 * @Description 读故事
 * @ClassName   ProviderStoriesView
 * @Author      luoyi
 * @Time        2020/4/2 13:55
 */
class ProviderStoriesView : FrameLayout {

    var mContext: Context? = null

    var binding: LayoutProviderStoriesBinding? = null
    /**
     * 活动数据集
     */
    var datas: MutableList<StoreBean> = mutableListOf()

    var adapter: ProviderStoriesAdapter? = null

    var adapterNew: ReadStoryAdapter? = null

    var resourceType: String? = ""
    var resourceId: String? = ""

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.layout_provider_stories,
            this,
            false
        )
        addView(binding!!.root)
    }

    /**
     * 设置数据
     */
    fun setData(data: MutableList<StoreBean>) {
        updateUi()
        datas.clear()
        adapter?.add(data)
    }

    /**
     * 设置数据(显示更多)
     */
    fun setDataNumber(id: String?, type1: String, data: MutableList<StoreBean>, number: String?) {
        resourceType=type1
        resourceId=id
        updateUi()
        datas.clear()
        adapter?.add(data)

        if (!data.isNullOrEmpty() && number?.toInt()!! > 4 && !resourceType.isNullOrEmpty() && !resourceId.isNullOrEmpty()) {
            binding?.tvProviderDetailsStoryCount?.text = "查看更多"
            binding?.tvProviderDetailsStoryCount?.visibility = View.VISIBLE
            binding?.tvProviderDetailsStoryCount?.onNoDoubleClick {
                if (!resourceType.isNullOrEmpty() && !resourceId.isNullOrEmpty()) {
                    ARouter.getInstance().build(MainARouterPath.MAIN_COMMON_STOREY_LIST)
                        .withString("resourceType", resourceType)
                        .withString("resourceId", resourceId)
                        .navigation()
                }
            }
        } else {
            binding?.tvProviderDetailsStoryCount?.visibility = View.GONE
        }
    }


    fun updateUi() {
        adapter = ProviderStoriesAdapter(mContext!!)
        binding?.recyclerProviderDetailsStory?.layoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
        binding?.recyclerProviderDetailsStory?.adapter = adapter

    }

    fun setDataNew(data: MutableList<StoreBean>) {
        updateUiNew()
        datas.clear()
        adapterNew?.add(data)
        if (!data.isNullOrEmpty() && data.size == 4 && !resourceType.isNullOrEmpty() && !resourceId.isNullOrEmpty()) {
            binding?.tvProviderDetailsStoryCount?.text = "查看更多"
            binding?.tvProviderDetailsStoryCount?.visibility = View.VISIBLE
            binding?.tvProviderDetailsStoryCount?.onNoDoubleClick {
                if (!resourceType.isNullOrEmpty() && !resourceId.isNullOrEmpty()) {
                    ARouter.getInstance().build(MainARouterPath.MAIN_COMMON_STOREY_LIST)
                        .withString("resourceType", resourceType)
                        .withString("resourceId", resourceId)
                        .navigation()
                }
            }
        } else {
            binding?.tvProviderDetailsStoryCount?.visibility = View.GONE
        }

    }

    fun updateUiNew() {
        adapterNew = ReadStoryAdapter(mContext!!)
        binding?.recyclerProviderDetailsStory?.layoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
        binding?.recyclerProviderDetailsStory?.adapter = adapterNew
    }

    fun setVisibe(visibility: Boolean) {
        if (visibility) {
            this.visibility = View.VISIBLE
        } else {
            this.visibility = View.GONE
        }
    }
}