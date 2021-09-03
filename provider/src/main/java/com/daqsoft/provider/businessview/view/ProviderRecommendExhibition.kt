package com.daqsoft.provider.businessview.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.extend.dp
import com.daqsoft.provider.R
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ExhibitionTagBean
import com.daqsoft.provider.bean.MapResBean
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.bean.VenueCollectBean
import com.daqsoft.provider.businessview.adapter.ProviderRecRecAdapter
import com.daqsoft.provider.businessview.adapter.ProviderRecTabAdapter
import com.daqsoft.provider.businessview.adapter.ProviderShowAdapter
import com.daqsoft.provider.businessview.adapter.ProviderXgtjAdapter
import com.daqsoft.provider.databinding.LayoutProviderDetailsCultureBinding
import com.daqsoft.provider.databinding.LayoutProviderDetailsRecommendBinding
import com.daqsoft.provider.model.RecommendModel

/**
 * @Description 展品详情的相关推荐
 * @ClassName   ProviderRecommendView
 */
class ProviderRecommendExhibition : FrameLayout {

    var mContext: Context? = null

    var binding: LayoutProviderDetailsCultureBinding? = null
    /**
     * 适配器
     */
    private val  mAdapter  by lazy {
        ProviderXgtjAdapter(mContext!!)
    }
    /**
     * 数据
     */
    var datas: MutableList<VenueCollectBean> = mutableListOf()

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
            val gridLayoutManager = GridLayoutManager(mContext, 2)
            layoutManager = gridLayoutManager
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
    fun setData(lists: MutableList<VenueCollectBean>, ismore: Boolean) {
        if (datas!=null) {
            if (!ismore) {
                datas.clear()
                if(lists.size>4){
                    datas.addAll(lists.subList(0,4))
                }else{
                    datas.addAll(lists)
                }
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