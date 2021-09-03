package com.daqsoft.provider.businessview.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.businessview.adapter.MoreListViewAdapter
import com.daqsoft.provider.databinding.LayoutMoreListViewBinding

/**
 *@package:com.daqsoft.provider.businessview.view
 *@date:2020/5/14 14:00
 *@author: caihj
 *@des:显示更多列表组件
 **/
class MoreListView:FrameLayout {

    private var mContext: Context? = null
    private var binding: LayoutMoreListViewBinding? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }


    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.layout_more_list_view,
            this,
            false
        )
        addView(binding!!.root)
    }

    /**
     * 设置标题
     */
    fun setTitle(title:String){
        binding?.providerDetailsListener?.text = title
    }

    /**
     * 设置标题icon
     */
    fun setTitleIcon(icon:Int){
        binding?.providerDetailsListener?.setCompoundDrawablesWithIntrinsicBounds(mContext?.resources?.getDrawable(icon),null,null,null)
    }

    /**
     * 设置副标题
     */
    fun setTitleInfo(text:String){
        binding?.tvCount?.text = text
    }

    fun <T: ViewDataBinding,D:Any> setAdapter(adapter:MoreListViewAdapter<T,D>,datas:MutableList<D>){
            binding?.rvList?.layoutManager = LinearLayoutManager(
                mContext, LinearLayoutManager.VERTICAL,
                false
            )

            binding?.rvList?.adapter = adapter
            adapter?.clear()
            adapter?.isNeedMore = datas.size > adapter.maxCount
            if (adapter!!.isNeedMore) {
                binding?.vProviderShowMore?.visibility = View.VISIBLE
            } else {
                binding?.vProviderShowMore?.visibility = View.GONE
            }
            adapter.add(datas)
        binding?.vProviderShowMore?.onNoDoubleClick {
            if (adapter != null) {
                if (adapter!!.isNeedMore) {
                    adapter!!.isNeedMore = false
                    adapter!!.notifyDataSetChanged()
                    val drawable2 = mContext!!.getDrawable(R.mipmap.provider_arrow_up)
                    drawable2.setBounds(0, 0, drawable2.minimumWidth, drawable2.minimumHeight)
                    binding?.txtProviderCommentaryMore?.setCompoundDrawables(null, null, drawable2, null)
                    binding?.txtProviderCommentaryMore?.text = "收起"
                } else {
                    adapter!!.isNeedMore = true
                    adapter!!.notifyDataSetChanged()
                    val drawable2 = mContext!!.getDrawable(R.mipmap.provider_arrow_down)
                    drawable2.setBounds(0, 0, drawable2.minimumWidth, drawable2.minimumHeight)
                    binding?.txtProviderCommentaryMore?.setCompoundDrawables(null, null, drawable2, null)
                    binding?.txtProviderCommentaryMore?.text = "查看更多"
                }
            }
    }
    }
}