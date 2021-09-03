package com.daqsoft.thetravelcloudwithculture.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.SingleLayoutHelper
import com.daqsoft.mainmodule.databinding.MainItemHotActivityClassifyBinding
import com.daqsoft.thetravelcloudwithculture.R

/**
 * @Description 热门活动分类适配器
 * @ClassName   HeaderAdapter
 * @Author      PuHua
 * @Time        2019/12/4 15:15
 */
class ActivityClassifyAdapter() : DelegateAdapter.Adapter<ActivityClassifyViewHolder>() {

    private var mContext: Context? = null

    constructor(context: Context) : this() {
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityClassifyViewHolder {
        val binding: MainItemHotActivityClassifyBinding =
            DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.main_item_hot_activity_classify, parent, false)
        return ActivityClassifyViewHolder(binding)
    }

    override fun getItemCount(): Int = 3

    override fun onCreateLayoutHelper(): LayoutHelper {
        val helper = SingleLayoutHelper()
        return helper
    }

    override fun onBindViewHolder(holder: ActivityClassifyViewHolder, position: Int) {

    }
}

class ActivityClassifyViewHolder(itemView: MainItemHotActivityClassifyBinding) : RecyclerView.ViewHolder(itemView.root) {

}