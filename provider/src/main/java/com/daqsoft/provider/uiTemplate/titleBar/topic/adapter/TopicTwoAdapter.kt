package com.daqsoft.provider.uiTemplate.titleBar.topic.adapter

import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.HomeTopicBean
import com.daqsoft.provider.databinding.ItemTopicStyleTwoBinding
import com.daqsoft.provider.uiTemplate.titleBar.topic.TopicResourceType
import com.daqsoft.provider.utils.ViewUtils

/**
 * 话题适配器样式二
 */
class TopicTwoAdapter : RecyclerViewAdapter<ItemTopicStyleTwoBinding, HomeTopicBean>(R.layout.item_topic_style_two) {
    override fun setVariable(
        mBinding: ItemTopicStyleTwoBinding,
        position: Int,
        item: HomeTopicBean
    ) {
        mBinding.url = item.image
        mBinding.name = item.name
        mBinding.data = item
        mBinding.memberNumber = item.participateNum
        mBinding.watchNumber = item.showNum
        if (!item.contentNum.isNullOrEmpty() && item.contentNum.toInt() > 0) {
            mBinding.tvContentNumber.text = mBinding.root.context.getString(R.string.home_topic_content_number, item.contentNum)
            mBinding.tvContentNumber.visibility = View.VISIBLE
        } else {
            mBinding.tvContentNumber.visibility = View.GONE
        }
        if (!item.topicTypeName.isNullOrEmpty()) {
            mBinding.tvTypeName.setBackgroundResource(TopicResourceType.getTypeBgIcon(item.topicTypeName))
        }
        if (item.hot) {
            val drawable = mBinding.root.context.getDrawable(R.mipmap.home_ht_hot)
            mBinding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        } else {
            mBinding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
        mBinding.root.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_TOPIC_DETAIL)
                .withString("id", item.id)
                .navigation()
        }
    }

}