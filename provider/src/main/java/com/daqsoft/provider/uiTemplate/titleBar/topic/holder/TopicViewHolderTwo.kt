package com.daqsoft.provider.uiTemplate.titleBar.topic.holder

import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.HomeTopicBean
import com.daqsoft.provider.databinding.ItemTopicStyleBinding
import com.daqsoft.provider.uiTemplate.titleBar.topic.adapter.TopicTwoAdapter
import com.daqsoft.provider.uiTemplate.titleBar.view.TitleBarFactoryView

/**
 * 话题view holder2
 */
class TopicViewHolderTwo(binding: ItemTopicStyleBinding) : BaseTopicViewHolder<ItemTopicStyleBinding>(binding) {

    private val topicTwoAdapter by lazy { TopicTwoAdapter() }

    override fun bindDataToUI(commonTemplate: CommonTemlate, dataLineType: MutableList<HomeTopicBean>) {
        val titleBarView = TitleBarFactoryView(binding.root.context).apply {
            setData(commonTemplate)
            onTitileBarClickListener =
                object : TitleBarFactoryView.OnTitleBarClickListener {
                    override fun toMoreInfo(commonTemlate: CommonTemlate) {
                        ARouter.getInstance().build(MainARouterPath.MAIN_TOPIC_LIST).navigation()
                    }
                }
        }

        val views = binding.rlRoot.children
        for (v in views) {
            if (v is TitleBarFactoryView) {
                binding.rlRoot.removeView(v)
            }
        }

        binding.rlRoot.addView(
            titleBarView,
            0
        )

        binding.recycleLineType.run {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = topicTwoAdapter
            topicTwoAdapter.clear()
            topicTwoAdapter.add(dataLineType)
            topicTwoAdapter.notifyDataSetChanged()
        }
    }
}