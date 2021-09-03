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
import com.daqsoft.provider.bean.CultureListBean
import com.daqsoft.provider.bean.ExhibitionTagBean
import com.daqsoft.provider.bean.MapResBean
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.businessview.adapter.ProviderRecRecAdapter
import com.daqsoft.provider.businessview.adapter.ProviderRecTabAdapter
import com.daqsoft.provider.businessview.adapter.ProviderShowAdapter
import com.daqsoft.provider.businessview.adapter.ProviderShowInforAdapter
import com.daqsoft.provider.databinding.LayoutProviderDetailsCultureBinding
import com.daqsoft.provider.databinding.LayoutProviderDetailsRecommendBinding
import com.daqsoft.provider.databinding.LayoutProviderDetailsShowBinding
import com.daqsoft.provider.model.RecommendModel

/**
 * @Description 演出信息
 * @ClassName   ProviderRecommendView
 */
class ShowInforView : FrameLayout {

    var mContext: Context? = null

    var binding: LayoutProviderDetailsShowBinding? = null
    /**
     * 适配器
     */
    private val  mAdapter  by lazy {
        ProviderShowInforAdapter(mContext!!)
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
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_provider_details_show, this, false)
        addView(binding!!.root)
        binding?.recyclerProviderDetailsAround?.apply {
            val staggeredGridLayoutManager =LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            layoutManager = staggeredGridLayoutManager
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
    var  isshowMore=false
    fun setData(list: MutableList<CultureListBean>, ismore: Boolean) {
        if (datas!=null) {
            if (!ismore) {
                datas.clear()
                if(list.size>6){
                    datas.addAll(list.subList(0, 6))
                    binding?.ivContentMore?.visibility=View.VISIBLE
                    binding?.ivContentMore?.setOnClickListener {
                        if(!isshowMore){
                            val subList = list.subList(6, list.size)
                            mAdapter.add(subList)
                            mAdapter?.notifyDataSetChanged()
                            isshowMore=true
                            binding?.ivContentMore?.setImageResource(R.mipmap.scenic_details_arrow_up)
                        }else{
                            datas.clear()
                            datas.addAll(list.subList(0, 6))
                            mAdapter.clearNotify()
                            mAdapter.setNewData(datas)
                            binding?.ivContentMore?.setImageResource(R.mipmap.scenic_details_arrow_down)
                            isshowMore=false
                        }
                    }
                }else{
                    datas.addAll(list)
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