package com.daqsoft.thetravelcloudwithculture.home.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.SingleLayoutHelper
import com.daqsoft.provider.ARouterPath
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.LayoutHomeTitleBinding
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * @Description 热门活动的适配器
 * @ClassName   HeaderAdapter
 * @Author      PuHua
 * @Time        2019/12/4 15:15
 */
class TitleAdapter() : DelegateAdapter.Adapter<TitleViewHolder>() {

    private var mContext: Context? = null
    /**
     * 展示标题
     */
    public var title:String? = null
    /**
     * 跳转的页面的路径
     */
    public var path:String? = null


    constructor(context: Context) : this() {
        this.mContext = context
    }

    constructor(context: Context,title:String,path:String):this(){
        this.mContext = context
        this.title = title
        this.path = path
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleViewHolder {
        val binding: LayoutHomeTitleBinding =
            DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_home_title, parent, false)
        return TitleViewHolder(binding)
    }

    override fun getItemCount(): Int = 1

    override fun onCreateLayoutHelper(): LayoutHelper {
        return SingleLayoutHelper()
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: TitleViewHolder, position: Int) {
        holder.binding.label.text = title
        RxView.clicks(holder.binding.root)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe { o ->
                run {
                    ARouter.getInstance()
                        .build(path)
                        .navigation()
                }
            }
    }
}

class TitleViewHolder(itemView: LayoutHomeTitleBinding) : RecyclerView.ViewHolder(itemView.root) {
    val binding = itemView
}