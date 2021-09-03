package com.daqsoft.baselib.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.daqsoft.baselib.base.BaseApplication
import daqsoft.com.baselib.R
import daqsoft.com.baselib.databinding.LayoutAdapterEmptyBinding
import daqsoft.com.baselib.databinding.LayoutAdapterFooterBinding

/**
 * @Description 多类型的recyclerView适配器
 * @ClassName   MultipleRecyclerViewAdapter
 * @Author      PuHua
 * @Time        2019/11/19 15:48
 */
abstract class MultipleRecyclerViewAdapter<T : ViewDataBinding, D : Any>(
    private val data: MutableList<D>,
    val type: MutableList<Int>
) : RecyclerView.Adapter<BindingViewHolder<*>>() {
    constructor() : this(data = mutableListOf(), type = mutableListOf())
    constructor(layoutEmptyView: T?) : this(data = mutableListOf(), type = mutableListOf()) {
        this.layoutEmptyView = layoutEmptyView
    }


    private val footer = mutableListOf<Any>()
    /**
     * 空布局
     */
    private val ITEM_EMPTY_TYPE = 10000
    /**
     * 正常布局
     */
    private val ITEM_COTENT_TYPE = 10001
    /**
     * 加载更多布局
     */
    private val ITEM_LOAD_MORE_TYPE = 10002
    /**
     * 加载结束布局
     */
    private val ITEM_LOAD_END_TYPE = 10003
    /**
     * Footer布局
     */
    private val ITEM_FOOTER_TYPE = 10004
    /**
     * 当前布局未适配
     */
    private val ITEM_NOT_ADAPT = 10005

    /**
     * 空布局视图
     */
    private var layoutEmpty: Int? = R.layout.layout_adapter_empty
    private var layoutEmptyView: T? = null
    /**
     * 加载更多布局视图
     */
    private var layoutLoadMore: Int? = R.layout.layout_adapter_footer
    /**
     * 加载结束布局视图
     */
    private var layoutLoadEnd: Int? = null
    /**
     * 是否第一次初始化RecyclerViewAdapter
     *
     * true: 是
     * false:否
     */
    private var initFirst = true
    /**
     * 加载状态
     * -1:初始化状态(default)
     *  0:加载中
     *  1:加载完毕
     */
    private var loadingStatus = -1
    /**
     * 能否刷新的标识位
     * true ：能
     * false ：不能(default)
     */
    private var loadingEnable = false
    /**
     * 是否显示空布局
     * true : 显示(default)
     * false: 不显示
     */
    @JvmField
    var emptyViewShow = true

    var emptyContent: String = ""
    var bottomMagin: Boolean=false
    override fun getItemViewType(position: Int): Int {
        if (data.size == 0 && emptyViewShow) {
            // 若数据集合为空，显示空布局视图
            return ITEM_EMPTY_TYPE
        } else if (footer.size != 0 && position == data.size + footer.size - 1) {
            // 若滑动到底部显示加载更多或加载完成视图
            return ITEM_FOOTER_TYPE
        } else {
            // 显示正常视图
            try {
                // type和data的长度不一致表示这个布局没有适配，需要自己适配
                return type[position]
            } catch (e: ArrayIndexOutOfBoundsException) {
                e.printStackTrace()
            }
            return ITEM_NOT_ADAPT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<*> {
        when (viewType) {
            ITEM_NOT_ADAPT -> {
                return NOAdaptViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.layout_adapter_empty,
                        parent,
                        false
                    )
                )
            }
            ITEM_EMPTY_TYPE -> {
                if (layoutEmptyView != null) {
                    return EmptyViewHolder(layoutEmptyView!!)
                }
                return EmptyViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.layout_adapter_empty,
                        parent,
                        false
                    )
                )
            }
            ITEM_FOOTER_TYPE -> return FooterViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_adapter_footer,
                    parent,
                    false
                )
            )
            else -> {
                // 默认返回对应的布局
                return BindingViewHolder<T>(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        viewType,
                        parent,
                        false
                    )
                )
            }
        }

    }


    override fun getItemCount(): Int = data.size + footer.size

    override fun onBindViewHolder(holder: BindingViewHolder<*>, position: Int) {
        // 当滑动到最后一个的时候调用加载更多回调接口
        if (position == itemCount - 1 && loadingStatus == -1 && loadingEnable && !initFirst) {
            loadingStatus = 0
            loadMoreListener?.invoke()
        }

        if (holder is EmptyViewHolder) {

            holder.emptyViewHolderBinding as LayoutAdapterEmptyBinding
            if (!emptyContent.isNullOrEmpty()){
                holder.emptyViewHolderBinding.emptyContent.text = emptyContent
            }
            if (BaseApplication.appArea == "sc"){
                holder.emptyViewHolderBinding.emptyImage.setImageResource(R.mipmap.common_empty)
            }

        } else if (holder is FooterViewHolder) {
            // 底部视图数据处理

            // 若为初始状态，隐藏底部footer视图
            if (loadingStatus == -1) {
                holder.footerViewHolderBinding.mLoadingLl.visibility = View.GONE
                holder.footerViewHolderBinding.mLoadingEnd.visibility = View.GONE
                return
            }
            // 显示加载中状态
            holder.footerViewHolderBinding.mLoadingLl.visibility = if (loadingStatus == 0) View.VISIBLE else View.GONE
            holder.footerViewHolderBinding.mLoadingTv.text = BaseApplication.refreshTips
            // 显示加载完成状态
            holder.footerViewHolderBinding.mLoadingEnd.visibility =
                if (loadingStatus == 1) View.VISIBLE else View.GONE
            if (!emptyContent.isNullOrEmpty() && loadingStatus == 1) {
                if(emptyContent.contains("推荐故事到头啦")){
                    holder.footerViewHolderBinding.mLoadingEnd1.visibility =  View.VISIBLE
                    holder.footerViewHolderBinding.mLoadingEnd.visibility =  View.GONE
                }else{
                    holder.footerViewHolderBinding.mLoadingEnd.visibility =  View.VISIBLE
                    holder.footerViewHolderBinding.mLoadingEnd1.visibility =  View.GONE
                }
                holder.footerViewHolderBinding.mLoadingEnd.text = emptyContent
                holder.footerViewHolderBinding.mLoadingEnd1.text = emptyContent
            }
        } else {
            setVariable((holder as BindingViewHolder<T>).mBinding, position, data.get(position))
            holder.mBinding.executePendingBindings()
        }

        initFirst = false

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        // 设置网格布局的Header和Footer独占一行
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            layoutManager.spanSizeLookup = (object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (getItemViewType(position) == ITEM_FOOTER_TYPE || getItemViewType(position) == ITEM_EMPTY_TYPE) {
                        layoutManager.spanCount
                    } else {
                        1
                    }
                }
            })
        }
    }

    override fun onViewAttachedToWindow(holder: BindingViewHolder<*>) {
        super.onViewAttachedToWindow(holder)
        // 设置瀑布流布局的Header和Footer独占一行
        val lp = holder.itemView.layoutParams
        if (lp != null
            && lp is StaggeredGridLayoutManager.LayoutParams
            && (getItemViewType(holder.layoutPosition) == ITEM_EMPTY_TYPE
                    || getItemViewType(holder.layoutPosition) == ITEM_FOOTER_TYPE)
        ) {
            lp.isFullSpan = true
        }
    }

    /**
     * 添加单个item
     * @param item 数据
     * @author 周俊蒙
     * @date 2019/6/10
     */
    fun addItem(item: D) {
        data.add(item)
        notifyItemInserted(data.size - 1)
    }

    /**
     * 添加数据集合
     * @param items 数据集合
     * @author 周俊蒙
     * @date 2019/6/10
     */
    fun add(items: MutableList<D>) {
        data.addAll(items)
        notifyDataSetChanged()
    }

    fun addViewTypes(item: MutableList<Int>) {
        type.addAll(item)
    }

    fun addViewType(item: Int) {
        type.add(item)
    }

    /**
     * 移除某个位置的数据
     * @param position 数据位置
     * @author 周俊蒙
     * @date 2019/6/10
     */
    fun removeItem(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * 移除具体某个数据
     * @param item 数据
     * @author 周俊蒙
     * @date 2019/6/10
     */
    fun removeItem(item: D) {
        data.remove(item)
        notifyDataSetChanged()
    }

    /**
     * 清空数据
     * @author 周俊蒙
     * @date 2019/6/10
     */
    fun clear() {
        data.clear()
        type.clear()
        initFirst = true
        notifyDataSetChanged()
    }

    /**
     * 获取数据源
     */
    fun getData(): MutableList<D> {
        return data
    }

    /**
     * 加载更多回调
     * @author 周俊蒙
     * @date 2019/6/10
     */
    private var loadMoreListener: (() -> Unit)? = null

    /**
     * 设置加载更多数据回调接口
     * @param loadMoreListener 加载更多回调
     * @author 周俊蒙
     * @date 2019/6/10
     */
    fun setOnLoadMoreListener(loadMoreListener: () -> Unit) {
        this.loadMoreListener = loadMoreListener
        footer.clear()
        footer.add(0)
        loadingEnable = true
    }

    /**
     * 加载完成
     * @author 周俊蒙
     * @date 2019/6/10
     */
    fun loadComplete() {
        // 将加载状态置为初始状态
        loadingStatus = -1
    }

    /**
     * 加载完成（所有数据加载完成时调用）
     * @author 周俊蒙
     * @date 2019/6/10
     */
    fun loadEnd() {
        // 将加载状态置为加载完成
        loadingStatus = 1
    }

    /**
     * 给布局设置Binding数据
     * @param mBinding item 布局的 ViewDataBinding对象
     * @param position 当前 item 的位置
     * @param item item的数据类型
     */
    abstract fun setVariable(mBinding: T, position: Int, item: D)

    class NOAdaptViewHolder(binding: LayoutAdapterFooterBinding) : BindingViewHolder<LayoutAdapterFooterBinding>
        (binding) {
        val footerViewHolderBinding = binding
    }
}