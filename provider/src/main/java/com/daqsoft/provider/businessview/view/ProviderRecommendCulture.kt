package com.daqsoft.provider.businessview.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.baselib.extend.dp
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CultureListBean
import com.daqsoft.provider.businessview.adapter.ProviderRecommCultureAdapter
import com.daqsoft.provider.databinding.LayoutProviderDetailsCultureBinding

/**
 *  文物详情（相关推荐）
 */
class ProviderRecommendCulture : FrameLayout {

    var mContext: Context? = null

    var binding: LayoutProviderDetailsCultureBinding? = null
    /**
     * 适配器
     */
    private val  mAdapter  by lazy {
        ProviderRecommCultureAdapter()
    }
    /**
     * 数据
     */
    var datas: MutableList<CultureListBean> = mutableListOf()

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_provider_details_culture, this, false)
        binding?.tvVenuesDetailsAround?.text="相关推荐"
        addView(binding!!.root)
        binding?.recyclerProviderDetailsAround?.apply {
            val linearLayoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)
            layoutManager = linearLayoutManager
            adapter = mAdapter
            isNestedScrollingEnabled = false
            addItemDecoration(object : RecyclerView.ItemDecoration(){
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    val position = parent.getChildAdapterPosition(view)
                    val count = state.itemCount - 1
                    outRect.right=12.dp
                    outRect.bottom = 20.dp
                }
            })
        }
    }

    /**
     * 设置展品数据
     * ismore是不是加载更多
     */
    fun setData(lists: MutableList<CultureListBean>, ismore: Boolean) {
        if (datas!=null) {
            if (!ismore) {
                datas.clear()
                datas.addAll(lists)
                mAdapter.setNewData(datas)
                mAdapter?.notifyDataSetChanged()
            } else {
                datas.addAll(datas)
                mAdapter?.notifyDataSetChanged()
            }
        }
    }

    fun clear() {
        datas.clear()
    }
}