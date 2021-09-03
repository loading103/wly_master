package com.daqsoft.provider.uiTemplate.titleBar.topic

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.HomeTopicBean
import com.daqsoft.provider.uiTemplate.titleBar.topic.holder.TopicViewHolderOne
import com.daqsoft.provider.uiTemplate.titleBar.topic.holder.TopicViewHolderTwo

/**
 * 话题
 */
class TopicAdapter(val helper: LayoutHelper) : DelegateAdapter.Adapter<RecyclerView.ViewHolder>() {

    val factory: TopicFactory by lazy { TopicFactory() }
    var commonTemplate: CommonTemlate? = null

    // 话题
    var topicBean: MutableList<HomeTopicBean> = mutableListOf()

    override fun onCreateLayoutHelper(): LayoutHelper {
        return helper
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return factory.create(parent, viewType)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (commonTemplate != null && !topicBean.isNullOrEmpty()) {
            when (holder) {
                is TopicViewHolderOne -> {
                    holder.bindDataToUI(commonTemplate!!, topicBean)
                }
                is TopicViewHolderTwo -> {
                    holder.bindDataToUI(commonTemplate!!, topicBean)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        commonTemplate?.let {
            return factory.getItemViewType(it.moduleCode ?: "1")
        }
        return super.getItemViewType(position)
    }
}