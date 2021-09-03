package com.daqsoft.thetravelcloudwithculture.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.SingleLayoutHelper
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.LayoutHomeHeaderBinding

/**
 * @Description 首页头部适配器
 * @ClassName   HeaderAdapter
 * @Author      PuHua
 * @Time        2019/12/4 15:15
 */
class HeaderAdapter() : DelegateAdapter.Adapter<HeaderViewHolder>() {

    private var mContext: Context? = null

    constructor(context: Context) : this() {
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding: LayoutHomeHeaderBinding =
            DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_home_header, parent, false)
        return HeaderViewHolder(binding)
    }

    override fun getItemCount(): Int = 1

    override fun onCreateLayoutHelper(): LayoutHelper {
        val helper = SingleLayoutHelper()
        return helper
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {

    }
}

class HeaderViewHolder(itemView: LayoutHomeHeaderBinding) : RecyclerView.ViewHolder(itemView.root) {

}